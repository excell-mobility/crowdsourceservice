package crowdsourceservice.model;

import io.swagger.annotations.ApiModelProperty;

public class CrowdsourceServiceResponse {
	
	private double lat;
	private double lon;
	private String traffic_event;
	private long timestamp;

	public CrowdsourceServiceResponse(String traffic_event, long time,
			double lon, double lat) {
		
		this.lat = lat;
		this.lon = lon;
		this.traffic_event = traffic_event;
		this.timestamp = time;
		
	}

	@ApiModelProperty(dataType = "double")
	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	@ApiModelProperty(dataType = "double")
	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	@ApiModelProperty(dataType = "string")
	public String getTraffic_event() {
		return traffic_event;
	}

	public void setTraffic_event(String traffic_event) {
		this.traffic_event = traffic_event;
	}

	@ApiModelProperty(dataType = "long")
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}