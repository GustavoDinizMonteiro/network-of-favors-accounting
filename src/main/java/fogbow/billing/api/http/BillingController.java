package fogbow.billing.api.http;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fogbow.billing.services.BillingService;

@Controller
@CrossOrigin
@RestController
@RequestMapping(value = "billing")
public class BillingController {
	
	private final Logger LOGGER = Logger.getLogger(BillingController.class);
	
	@RequestMapping(value = "/{federationUserId}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Double>> getCompute(
            @PathVariable String federationUserId)
            throws Exception {
		
        LOGGER.info("Get billing for federation user <" + federationUserId + "> received.");
        Map<String, Double> userReport = BillingService.getInstance().getUserBilling(federationUserId);
        return new ResponseEntity<Map<String, Double>>(userReport, HttpStatus.OK);
    }

}
