package crowdsourceservice.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import crowdsourceservice.component.CrowdsourceService;
import crowdsourceservice.model.CrowdsourceServiceResponse;

@RestController
@Api(value="/v1/crowdsourceevent")
public class CrowdsourceController {
	
	@Autowired
	private CrowdsourceService crowdsourceService;
	
	@RequestMapping(value = "/v1/crowdsourceevent", method = RequestMethod.POST)
    @ApiOperation(
    		value = "Get correctly mapped traffic event for a list of coordinates", 
    		response=CrowdsourceServiceResponse.class, 
    		produces = "application/json")
    @ResponseBody
    public CrowdsourceServiceResponse crowdsourceevent(
    		@ApiParam(name="jsonObjectInput", value="JSON object of time gaps with coordinates")
    		@RequestBody String jsonObjectInput) {
    		JSONObject jsonObject = new JSONObject(jsonObjectInput);
    		return crowdsourceService.mapPositionofTrafficEvent(jsonObject);
    }
	
    @ExceptionHandler(value = Exception.class)
    public String inputParameterError(Exception e) {
    	return "Your input parameters for the crowdsource event service are invalid! Reason: " + e.getMessage();
    }

}
