package in.canaris.cloud.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import in.canaris.cloud.entity.ADConfig;
import in.canaris.cloud.entity.Customer;
import in.canaris.cloud.server.entity.AactiveDConfig;
import in.canaris.cloud.server.repository.AactivedConfigRepository;

@Controller
@RequestMapping("/adConfig")
public class AactiveDConfigController {

	@Autowired
	private AactivedConfigRepository repository;

	final String var_function_name = "adConfig"; // small letter
	final String disp_function_name = "ADConfig"; // capital letter

	@GetMapping("/view")
	public ModelAndView getAll() {
		ModelAndView mav = new ModelAndView(var_function_name + "_view");
		mav.addObject("action_name", var_function_name);
		try {
			mav.addObject("listObj", repository.findAll());
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
		AactiveDConfig objEnt = new AactiveDConfig();
		mav.addObject("objEnt", objEnt);
		return mav;
	}
	
	@GetMapping("/checkUsernameExist")
	public @ResponseBody String checkUsernameExist(@RequestParam String username)
	{
		String duplicate = "";
		try {
			
			List<AactiveDConfig> list = repository.findByusername(username);
			if(!list.isEmpty()) {
				duplicate = "duplicate";
			}
			
		} catch (Exception e) {
			System.out.println("Exception occured while checking for duplicate username = "+e);
		}
		return duplicate;
	}

	@PostMapping("/save")
	public String save(AactiveDConfig obj, RedirectAttributes redirectAttributes) {
		try {
			repository.save(obj);
			redirectAttributes.addFlashAttribute("message",
					"The " + disp_function_name + " has been saved successfully!");
		} catch (Exception e) {
			redirectAttributes.addAttribute("message", e.getMessage());
		}
		return "redirect:/" + var_function_name + "/view";
	}

	@GetMapping("/edit/{id}")
	public ModelAndView edit(@PathVariable("id") Integer id) {
		
		try {
			ModelAndView mav = new ModelAndView(var_function_name + "_add");
			mav.addObject("action_name", var_function_name);
			mav.addObject("objEnt", repository.findById(id).get());
			mav.addObject("pageTitle",  "Edit " + disp_function_name + " (ID: " + id + ")");
			return mav;
		} catch (Exception e) {
			ModelAndView mav = new ModelAndView("redirect:/" + var_function_name + "/view");
			mav.addObject("action_name", var_function_name);
			mav.addObject("message", e.getMessage());
			return mav;
		}
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			repository.deleteById(id);
			redirectAttributes.addFlashAttribute("message",
					"The " + disp_function_name + " with id=" + id + " has been deleted successfully!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/" + var_function_name + "/view";
	}
}
