package in.canaris.cloud.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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

import in.canaris.cloud.entity.AppUser;
import in.canaris.cloud.entity.Location;
import in.canaris.cloud.repository.AddPhysicalServerRepository;
import in.canaris.cloud.repository.LocationRepository;


@Controller
@RequestMapping("/location")
public class LocationController {

	@Autowired
	private LocationRepository repository;
	
	@Autowired
	private AddPhysicalServerRepository addPhysicalServerRepository;

	final String var_function_name="location";
	final String disp_function_name="Location";

	@GetMapping("/view")
	public String getAll(Model model) {
		try {
			List<Location> listObj = new ArrayList<Location>();
			repository.findAll().forEach(listObj::add);
			model.addAttribute("listObj", listObj);
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
		}
		return var_function_name+"_view";
	}

	@GetMapping("/new")
	public String add(Model model, Principal principal) {
		if (principal == null) {
			return "welcomePage";
	    }
		model.addAttribute("pageTitle", "Add New "+disp_function_name);
		Location objEnt = new Location();
		model.addAttribute("objEnt", objEnt);
		model.addAttribute("physicalServerIPList", addPhysicalServerRepository.getPhysicalServerIPs());
		return var_function_name+"_add";
	}
	
	@GetMapping("/checkDuplicateLocation")
	public @ResponseBody String checkDuplicateLocation(@RequestParam String location)
	{
		String duplicate = "";
		try {
			
			List<Location> list = repository.findByLocationName(location);
			if(!list.isEmpty()) {
				duplicate = "duplicate";
			}
			
		} catch (Exception e) {
			System.out.println("Exception occured while checking for duplicate Location = "+e);
		}
		return duplicate;
	}

	@PostMapping("/save")
	public String save(Location obj, RedirectAttributes redirectAttributes) {
		try {
			repository.save(obj);
			redirectAttributes.addFlashAttribute("message", "The "+disp_function_name+" has been saved successfully!");
		} catch (Exception e) {
			redirectAttributes.addAttribute("message", e.getMessage());
		}
		return "redirect:/"+var_function_name+"/view";
	}

	@GetMapping("/edit/{id}")
	public String edit(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Location objEnt = repository.findById(id).get();
			model.addAttribute("objEnt", objEnt);
			model.addAttribute("pageTitle", "Edit "+disp_function_name+" (ID: " + id + ")");
			model.addAttribute("physicalServerIPList", addPhysicalServerRepository.getPhysicalServerIPs());
			return var_function_name+"_add";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/"+var_function_name+"/view";
		}
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			repository.deleteById(id);
			redirectAttributes.addFlashAttribute("message",
					"The "+disp_function_name+" with id=" + id + " has been deleted successfully!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/"+var_function_name+"/view";
	}
}
