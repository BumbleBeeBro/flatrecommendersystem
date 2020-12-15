package recommender.web;

import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

public class IndexController {


		private static final org.slf4j.Logger log = LoggerFactory.getLogger(IndexController.class);

		/**
		 * Controller for initial get request. First contact point for customer.
		 * @param model, Model. Page model.
		 * @return index view.
		 */
		
	    
	    @GetMapping("/")
	    public String index(Model model){
	    	
	    	log.info("Someone entered the site");
	    	
	    	return "index";
	    }
}
