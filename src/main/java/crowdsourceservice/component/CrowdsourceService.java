package crowdsourceservice.component;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.graphhopper.GraphHopper;
import com.graphhopper.matching.EdgeMatch;
import com.graphhopper.matching.LocationIndexMatch;
import com.graphhopper.matching.MapMatching;
import com.graphhopper.matching.MatchResult;
import com.graphhopper.routing.util.CarFlagEncoder;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.storage.index.LocationIndexTree;
import com.graphhopper.util.GPXEntry;
import com.graphhopper.util.PointList;
import com.vividsolutions.jts.geom.Coordinate;

import crowdsourceservice.model.CrowdsourceServiceResponse;

@Component
public class CrowdsourceService {
	
	private final Logger log;
	private GraphHopper hopper;
	private CarFlagEncoder encoder;
	
	@Autowired
	public CrowdsourceService(
			@Value("${crowdsource.osmfile}") String osmFile,
			@Value("${crowdsource.mapmatching}") String ghLocation) {
		
		log = LoggerFactory.getLogger(this.getClass());
		hopper = new GraphHopper().forServer();
		hopper.setOSMFile(osmFile);
		hopper.setGraphHopperLocation(ghLocation);
		encoder = new CarFlagEncoder();
		hopper.setEncodingManager(new EncodingManager(encoder));
		hopper.setCHEnable(false);
		hopper.importOrLoad();
		
	}
	
	public CrowdsourceServiceResponse mapPositionofTrafficEvent (JSONObject jsonObject) {
				
		JSONArray jsonArray = jsonObject.getJSONArray("coordinates");
		String traffic_event = jsonObject.getString("traffic-event");
		List<GPXEntry> inputGPXEntries = new LinkedList<GPXEntry>();
		
		for(int index = 0; index < jsonArray.length(); index++) {
			
			JSONObject coordinate = (JSONObject) jsonArray.get(index);
			double lat = coordinate.getDouble("lat");
			double lon = coordinate.getDouble("lon");
			long timestamp = coordinate.getLong("timestamp");
			inputGPXEntries.add(new GPXEntry(lat, lon, timestamp));
			
		}
		
		GPXEntry pointOfTrafficAlert = inputGPXEntries.get(inputGPXEntries.size() - 1);
		
		GraphHopperStorage graph = hopper.getGraphHopperStorage();
		LocationIndexMatch locationIndex = new LocationIndexMatch(graph,
				(LocationIndexTree) hopper.getLocationIndex());
		MapMatching mapMatching = new MapMatching(graph, locationIndex, encoder);
		mapMatching.setMaxNodesToVisit(50000);
		MatchResult mr = mapMatching.doWork(inputGPXEntries);

		List<EdgeMatch> matches = mr.getEdgeMatches();
	
		PointList pointList = new PointList();
		
		for(int index = 0; index < matches.size(); index++) {
			
			pointList.add(matches.get(index).getEdgeState().fetchWayGeometry(3));
			
		}
		
		TreeMap<Double, GPXEntry> sortedMap = new TreeMap<Double, GPXEntry>();
		for(int index = 0; index < pointList.size(); index++) {
				
			double distance = Double.MAX_VALUE;
			try {
				distance = getDistance(pointList.getLat(index), pointList.getLon(index), 
						pointOfTrafficAlert.getLat(), pointOfTrafficAlert.getLon());
			} catch (NoSuchAuthorityCodeException e) {
				log.error("Can not calculate the distance");
			} catch (TransformException e) {
				log.error("Can not calculate the distance");
			} catch (FactoryException e) {
				log.error("Can not calculate the distance");
			}
			sortedMap.put(distance, 
					new GPXEntry(pointList.getLat(index), pointList.getLon(index), 0l));
			
		}
		GPXEntry gpxEntry = sortedMap.get(sortedMap.firstKey());
		
		return new CrowdsourceServiceResponse(traffic_event, 
				pointOfTrafficAlert.getTime(),
				gpxEntry.getLon(),
				gpxEntry.getLat());
	}
	
	private double getDistance(double first_lat, double first_lon, 
			double second_lat, double second_lon) throws TransformException, NoSuchAuthorityCodeException, FactoryException {
		
		CoordinateReferenceSystem crs = CRS.decode("EPSG:4326");
	    GeodeticCalculator gc = new GeodeticCalculator(crs);
	    gc.setStartingPosition( JTS.toDirectPosition( new Coordinate(first_lon, first_lat), crs ) );
	    gc.setDestinationPosition( JTS.toDirectPosition( new Coordinate(second_lon, second_lat), crs ) );
	    
	    double distance = gc.getOrthodromicDistance();
	    
	    return distance;
	    
	}

}