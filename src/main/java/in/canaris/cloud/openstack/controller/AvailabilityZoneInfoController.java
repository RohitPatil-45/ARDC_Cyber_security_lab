package in.canaris.cloud.openstack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import in.canaris.cloud.openstack.entity.AvailabilityZoneInfo;
import in.canaris.cloud.openstack.entity.Flavor;
import in.canaris.cloud.openstack.repository.AvailabilityZoneInfoRepository;

@Controller
@RequestMapping("/availabilityZoneInfo")
public class AvailabilityZoneInfoController {
	
	@Autowired
	private AvailabilityZoneInfoRepository availabilityZoneInfoRepository;
	
	final String var_function_name = "availabilityZoneInfo"; // small letter
	final String disp_function_name = "AvailabilityZoneInfo"; // capital letter
	
	@GetMapping("/view")
	public ModelAndView getAll() {
		ModelAndView mav = new ModelAndView(var_function_name + "_view");
		mav.addObject("action_name", var_function_name);
		try {
			mav.addObject("listObj", availabilityZoneInfoRepository.findAll());
		} catch (Exception e) {
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
		}
		return mav;
	}
	
	@GetMapping("/new")
	public ModelAndView add() {
		ModelAndView mav = new ModelAndView(var_function_name + "_add");
		mav.addObject("pageTitle", "Add New " + disp_function_name);
		mav.addObject("action_name", var_function_name);
		Flavor objEnt = new Flavor();
		mav.addObject("objEnt", objEnt);
		return mav;
	}

	@PostMapping("/save")
	public String save(AvailabilityZoneInfo obj, RedirectAttributes redirectAttributes) {
		try {
			availabilityZoneInfoRepository.save(obj);
			redirectAttributes.addFlashAttribute("message",
					"success");
		} catch (Exception e) {
			redirectAttributes.addAttribute("message", "fail");
		}
		return "redirect:/" + var_function_name + "/new";
	}
	
	
	@GetMapping("/checkFlavorExist")
	public @ResponseBody String checkFlavorExist(@RequestParam String flavorName) {
		String duplicate = "";
		try {

//			List<Flavor> list = flavorRepository.findByfalvorName(flavorName);
//			if (!list.isEmpty()) {
//				duplicate = "duplicate";
//			}

		} catch (Exception e) {
			System.out.println("Exception occured while checking for duplicate flavor = " + e);
		}
		return duplicate;
	}


}
