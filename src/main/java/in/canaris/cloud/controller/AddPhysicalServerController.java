package in.canaris.cloud.controller;

import java.sql.Timestamp;
import java.util.Date;
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

import in.canaris.cloud.entity.AddPhysicalServer;
import in.canaris.cloud.entity.CloudInstance;
import in.canaris.cloud.entity.Discount;
import in.canaris.cloud.entity.PhysicalServer;
import in.canaris.cloud.repository.AddPhysicalServerRepository;

@Controller
@RequestMapping("/physicalServer")
public class AddPhysicalServerController {

	@Autowired
	private AddPhysicalServerRepository repository;

	final String var_function_name = "physicalServer"; // small letter
	final String disp_function_name = "PhysicalServer"; // capital letter

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
		AddPhysicalServer objEnt = new AddPhysicalServer();
		mav.addObject("objEnt", objEnt);
		return mav;
	}

	@PostMapping("/save")
	public String save(AddPhysicalServer obj, RedirectAttributes redirectAttributes) {
		try {
			//obj.setLastSyncTime((Timestamp) new Date());
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
			mav.addObject("objEnt", repository.findById((long)id).get());
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
			repository.deleteById((long)id);
			redirectAttributes.addFlashAttribute("message",
					"The " + disp_function_name + " with id=" + id + " has been deleted successfully!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/" + var_function_name + "/view";
	}
	
	@GetMapping("/checkforDuplicatePhysicalServerIP")
	public @ResponseBody String checkforDuplicatePhysicalServerIP(@RequestParam String ip) {
		String duplicate = "";
		try {

			AddPhysicalServer list = repository.findByserverIP(ip);
			if (list.getServerIP() != null) {
				duplicate = "duplicate";
			}

		} catch (Exception e) {
			System.out.println("Exception occured while checking for duplicate physical server ip = " + e);
		}
		return duplicate;
	}
	
	
	
}
