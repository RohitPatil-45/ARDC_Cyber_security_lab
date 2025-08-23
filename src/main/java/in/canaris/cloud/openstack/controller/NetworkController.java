package in.canaris.cloud.openstack.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import in.canaris.cloud.openstack.repository.NetworkRepository;
import in.canaris.cloud.openstack.repository.flavorRepository;
import in.canaris.cloud.openstack.repository.keyPairRepository;
import in.canaris.cloud.openstack.entity.Flavor;
import in.canaris.cloud.openstack.entity.KeyPair;
import in.canaris.cloud.openstack.entity.Network;

@Controller
@RequestMapping("/network")
public class NetworkController {
	
	@Autowired
	private NetworkRepository networkRepository;
	
	final String var_function_name = "network"; // small letter
	final String disp_function_name = "Network"; // capital letter
	
	@GetMapping("/view")
	public ModelAndView getAll() {
		ModelAndView mav = new ModelAndView(var_function_name + "_view");
		mav.addObject("action_name", var_function_name);
		try {
			mav.addObject("listObj", networkRepository.findAll());
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
		Network objEnt = new Network();
		mav.addObject("objEnt", objEnt);
		return mav;
	}

	@PostMapping("/save")
	public String save(Network obj, RedirectAttributes redirectAttributes) {
		try {
			networkRepository.save(obj);
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
