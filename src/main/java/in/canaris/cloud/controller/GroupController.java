package in.canaris.cloud.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
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
import in.canaris.cloud.entity.Group;
import in.canaris.cloud.repository.AppUserRepository;
import in.canaris.cloud.repository.GroupRepository;
import in.canaris.cloud.repository.SwitchRepository;

@Controller
@RequestMapping("/group")
public class GroupController {

	@Autowired
	private GroupRepository repository;

	@Autowired
	private SwitchRepository Switchrepository;

	@Autowired
	private AppUserRepository appRepository;

	final String var_function_name = "group"; // small letter
	final String disp_function_name = "Group"; // capital letter

	@GetMapping("/view")
	public ModelAndView getAll(Principal principal) {
		ModelAndView mav = new ModelAndView(var_function_name + "_view");
		mav.addObject("action_name", var_function_name);

		if (principal == null) {
			ModelAndView mav1 = new ModelAndView("welcomePage");
			return mav1;
		}

		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();

		try {
			boolean isSuperAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));
			if (isSuperAdmin) {

				try {
					mav.addObject("listObj", repository.findAll());
				} catch (Exception e) {
					mav.addObject("listObj", null);
					mav.addObject("error", e.getMessage());
				}

			} else {
				AppUser obj = appRepository.findByuserName(username);
				String groupName = obj.getGroupName();
//				System.out.println("Group name ::" + groupName);

				try {
					mav.addObject("listObj", repository.findByGroupName(groupName));
				} catch (Exception e) {
					mav.addObject("listObj", null);
					mav.addObject("error", e.getMessage());
				}

			}
		} catch (Exception e) {
			System.out.println("Exception occured while fetching VM Data = " + e);
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
		Group objEnt = new Group();
		mav.addObject("objEnt", objEnt);
		return mav;
	}

	@PostMapping("/save")
	public String save(Group obj, RedirectAttributes redirectAttributes) {
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
			mav.addObject("pageTitle", "Edit " + disp_function_name + " (ID: " + id + ")");
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

	@GetMapping("/checkGroupExist")
	public @ResponseBody String checkUsernameExist(@RequestParam String groupName) {
		String duplicate = "";
		try {

			List<Group> list = repository.findByGroupName(groupName);
			if (!list.isEmpty()) {
				duplicate = "duplicate";
			}

		} catch (Exception e) {
			System.out.println("Exception occured while checking for duplicate group name = " + e);
		}
		return duplicate;
	}

	@GetMapping("/viewSwitch")
	public ModelAndView viewSwitch() {
		ModelAndView mav = new ModelAndView("Switch" + "_Show");
		mav.addObject("action_name", "group");
		try {
			mav.addObject("listObj", Switchrepository.findAll());
		} catch (Exception e) {
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
		}
		return mav;
	}
}
