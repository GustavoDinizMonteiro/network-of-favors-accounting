package fogbow.billing.api.http;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fogbow.billing.model.Usage;
import fogbow.billing.services.ResourceUsageService;

@Controller
@RestController
@RequestMapping(value = ResourceUsageController.USAGE_ENDPOINT)
public class ResourceUsageController {
	
	public static final String USAGE_ENDPOINT = "usage";
	
	@RequestMapping(value = "/{userId}/{requestingMember}/{providingMember}/{resourceType}/{initialDate}/{finalDate}", method = RequestMethod.GET)
    public ResponseEntity<List<Usage>> getResourceUsageFromUser(
            @PathVariable String userId,
            @PathVariable String requestingMember,
            @PathVariable String providingMember,
            @PathVariable String resourceType,
            @PathVariable String initialDate,
            @PathVariable String finalDate)
            throws Exception {
		
		System.out.println("Entrei");
        List<Usage> listOfUsage = ResourceUsageService.getInstance().getUsage(userId, 
        		requestingMember, providingMember, resourceType, initialDate, finalDate);

        return new ResponseEntity<List<Usage>>(listOfUsage, HttpStatus.OK);        
    
    }

}
