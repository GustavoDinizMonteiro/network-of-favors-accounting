package fogbow.billing.api.http;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fogbow.billing.services.AuthorizationDirectoryServiceImpl;

@Controller
@RestController
@RequestMapping(value = AuthorizationController.AUTH_ENDPOINT)
public class AuthorizationController {
	
	private final Logger LOGGER = Logger.getLogger(AuthorizationController.class);
	
	public static final String AUTH_ENDPOINT = "auth"; 
	
	public static final String USER_FEDERATION_ID = "federationUserId"; 
	public static final String RESOURCE_TYPE = "resourceType";
	public static final String OPERATION = "operation";
	
	@RequestMapping(value = "/{federationUserId}/{resourceType}/{operation}", method = RequestMethod.GET)
    public ResponseEntity<Boolean> isAuthorizedPrePaid(
            @PathVariable String federationUserId, String resourceType, String operation)
            throws Exception {
		
        LOGGER.info("Get authorization for federation user <" + federationUserId + "> received.");
        
        boolean result = AuthorizationDirectoryServiceImpl.getInstance().getUserAuthorization(federationUserId,
        		resourceType, operation);

        return new ResponseEntity<Boolean>(result, HttpStatus.OK);      
    }
	
	@RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Boolean> saveUser(
    		@RequestBody String federationUserId)
            throws Exception {
		
        LOGGER.info("Post federation user <" + federationUserId + "> received.");
        
        boolean result = AuthorizationDirectoryServiceImpl.getInstance().addUser(federationUserId);
        
        if (result) {
        	return new ResponseEntity<Boolean>(result, HttpStatus.OK);      
        } 
        
        return new ResponseEntity<Boolean>(result, HttpStatus.BAD_REQUEST);       
        
    }
	
	@RequestMapping(value = "/unauthorize", method = RequestMethod.POST)
    public ResponseEntity<Boolean> unauthorizeUser(
    		@RequestBody Map<String, String> jsonParameters)
            throws Exception {
		
		String federationUserId = jsonParameters.get(USER_FEDERATION_ID);
		String resourceType = jsonParameters.get(RESOURCE_TYPE);
		String operation = jsonParameters.get(OPERATION);
		
        LOGGER.info("Unauthorize federation user <" + federationUserId + "> received.");
        
        AuthorizationDirectoryServiceImpl.getInstance().unauthorizeUser(federationUserId, resourceType, operation);
      
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);      
        
    }
	
	@RequestMapping(value = "/authorize", method = RequestMethod.POST)
    public ResponseEntity<Boolean> authorizeUser(
    		@RequestBody Map<String, String> jsonParameters)
            throws Exception {
		
		String federationUserId = jsonParameters.get(USER_FEDERATION_ID);
		String resourceType = jsonParameters.get(RESOURCE_TYPE);
		String operation = jsonParameters.get(OPERATION);
		
        LOGGER.info("Authorize federation user <" + federationUserId + "> received.");
        
        AuthorizationDirectoryServiceImpl.getInstance().authorizeUser(federationUserId, resourceType, operation);
       
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);      
        
    }

}
