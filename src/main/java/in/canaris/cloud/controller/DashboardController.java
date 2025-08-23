package in.canaris.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import in.canaris.cloud.entity.Discount;
import in.canaris.cloud.repository.DiscountRepository;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

	@GetMapping("/view")
	public ModelAndView add() {
		ModelAndView mav = new ModelAndView("dashboard");
		return mav;
	}
	
	
	@GetMapping("/approval")
	public String approval() {
		return "dashboard_approval";
	}

}
