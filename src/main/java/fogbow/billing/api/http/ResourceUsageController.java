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
	
	@RequestMapping(value = "/{federationUserId}/{initialPeriod}/{finalPeriod}", method = RequestMethod.GET)
    public ResponseEntity<List<Usage>> getResourceUsageFromUser(
            @PathVariable String federationUserId, @PathVariable String initialPeriod, @PathVariable String finalPeriod)
            throws Exception {
		
        List<Usage> listOfUsage = ResourceUsageService.getInstance().getUserUsage(federationUserId, initialPeriod, finalPeriod);

        return new ResponseEntity<List<Usage>>(listOfUsage, HttpStatus.OK);      
    }

}
