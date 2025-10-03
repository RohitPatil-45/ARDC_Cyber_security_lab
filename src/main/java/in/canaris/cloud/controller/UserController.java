package in.canaris.cloud.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import in.canaris.cloud.entity.AppRole;
import in.canaris.cloud.entity.AppUser;
import in.canaris.cloud.entity.Switch;
import in.canaris.cloud.entity.UserMasterRole;
import in.canaris.cloud.entity.UserRole;
import in.canaris.cloud.openstack.entity.DepartmentMaster;
import in.canaris.cloud.repository.AppRoleRepository;
import in.canaris.cloud.repository.DepartmentMasterRepository;
import in.canaris.cloud.repository.GroupRepository;
import in.canaris.cloud.repository.SwitchRepository;
import in.canaris.cloud.repository.UserRepository;

import in.canaris.cloud.repository.UserRoleRepository;
import in.canaris.cloud.service.UserDetailsServiceImpl;

@Controller
@RequestMapping("/users")
public class UserController {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private AppRoleRepository appRoleRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private SwitchRepository switchRepository;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;
	
	
	@Autowired
	DepartmentMasterRepository DepartmentMasterRepository;

//	@Autowired
//	private SwitchRepository switchRepository;

	@GetMapping("/view")
	public String getAll(Model model, Principal principal) {
		// System.out.println("user controller :");
		if (principal == null) {
			// Redirect to login page if the principal is null
			return "redirect:/";
		}
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String groupName = "";
		try {
			List<AppUser> users = new ArrayList<AppUser>();
			boolean isSuperAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));

			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

			List<AppUser> user1 = userRepository.findByuserName(loginedUser.getUsername());
			for (AppUser appUser : user1) {
				groupName = appUser.getGroupName();
			}

			if (isSuperAdmin) {
				userRepository.findAll().forEach(users::add);
				model.addAttribute("listObj", users);
			} else if (isAdmin) {
				System.out.println("group = " + groupName);
				List<String> groups = new ArrayList<>();
				StringTokenizer token = new StringTokenizer(groupName, ",");
				while (token.hasMoreTokens()) {
					groups.add(token.nextToken());
				}
				userRepository.findBygroups(groups).forEach(users::add);
				model.addAttribute("listObj", users);
			}

		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
		}
		return "user_view";
	}

//	@GetMapping("/new")
//	public String addUser(Model model) {
//		UserMasterRole user = new UserMasterRole();
//		model.addAttribute("objEnt", user);
//		model.addAttribute("pageTitle", "Create new User");
//		model.addAttribute("groupList", groupRepository.getAllGroups());
//		model.addAttribute("switchList", switchRepository.getAllSwitch());
//		return "user_add";
//	}
	
	
	@GetMapping("/new")
	public String addUser(Model model) {
	    UserMasterRole user = new UserMasterRole();
	    model.addAttribute("objEnt", user);
	    model.addAttribute("pageTitle", "Create new User");

	    model.addAttribute("groupList", groupRepository.getAllGroups());
	    model.addAttribute("switchList", switchRepository.getAllSwitch());

	    // Also send departments for the first dropdown
	    List<DepartmentMaster> depts = DepartmentMasterRepository.findAll();
	    model.addAttribute("departments", depts);

	    return "user_add";
	}


	@PostMapping("/save")
	public String saveUser(UserMasterRole userRole, RedirectAttributes redirectAttributes, BindingResult result,
			Model model) {
		System.out.println("App User save: controler ");

		if (result.hasErrors()) {
			System.out.println("App User save: error ");

		}
		AppUser user = userRole.getAppUser();
		System.out.println("hhhhhhhhhh " + user.getGroupName());
		System.out.println("user id = " + user.getUserId());

		if (user.getUserId() == null) {
			try {
				BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
				user.setEncrytedPassword(encoder.encode(user.getEncrytedPassword()));
				user.setEnabled(true);
				System.out.println("hhhhhhhhhh " + user.getGroupName());
				userRepository.save(user);
				redirectAttributes.addFlashAttribute("message", "The User has been saved successfully!");
			} catch (Exception e) {
				// redirectAttributes.addAttribute("message", e.getMessage());
				System.out.println("Exception e1:" + e);
				UserMasterRole user2 = new UserMasterRole();
				user2.setAppUser(user);
				model.addAttribute("objEnt", user2);
				model.addAttribute("pageTitle", "Create new User");
				model.addAttribute("groupList", groupRepository.getAllGroups());
				return "user_add";
			}

			try {
				Long role_id = userRole.getAppRole().getRoleId();
				AppRole aprole = new AppRole();
				aprole.setRoleId(role_id);

				UserRole userRole2 = new UserRole();
				System.out.println("hhhhhhhhhh " + user.getGroupName());
				userRole2.setAppUser(user);

				userRole2.setAppRole(aprole);
				userRoleRepository.save(userRole2);
				
				userDetailsServiceImpl.sendSimpleMail(user.getUserName(), user.getConfirmPassword(), user.getEmail());
				
				redirectAttributes.addFlashAttribute("message", "The User has been saved successfully!");
			} catch (Exception e) {
				// redirectAttributes.addAttribute("message", e.getMessage());
				System.out.println("Exception e2:" + e);
			}
		} else {
			try {

				userRepository.updateUser(user.getName(), user.getEmail(), user.getMobileNo(), user.getGroupName(),
						user.getSwitch_id(), user.getGenerationType(), user.getUserId());
				userRoleRepository.updateUserRole(user.getUserId(), userRole.getAppRole().getRoleId());
				redirectAttributes.addFlashAttribute("message", "The User has been updated successfully!");
			} catch (Exception e) {
				System.out.println("Exception occured while updating user:" + e);
			}
		}

		return "redirect:/users/view";
	}

//	@GetMapping("/edit/{id}")
//	public String editTutorial(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
//		try {
//			AppUser user = userRepository.findById(id).get();
//
//			UserMasterRole usermasterRole = new UserMasterRole();
//			usermasterRole.setAppUser(user);
//			// UserRole userRole = userRoleRepository.findRole(id);
//			long roleID = userRoleRepository.findRole(id);
//			System.out.println("role id = " + roleID);
//			AppRole role = appRoleRepository.findByRoleId(roleID);
//
//			usermasterRole.setAppRole(role);
//
//			model.addAttribute("user", user);
//			model.addAttribute("pageTitle", "Edit User (ID: " + id + ")");
//			model.addAttribute("userID", id);
//			// model.addAttribute("roleID",userRoleRepository.findRole(id));
//			model.addAttribute("objEnt", usermasterRole);
//			model.addAttribute("groupList", groupRepository.getAllGroups());
//			model.addAttribute("switchList", switchRepository.getAllSwitch());
//			
//			List<DepartmentMaster> departments = DepartmentMasterRepository.findAll();
//
//			model.addAttribute("subject", subject);
//			model.addAttribute("departments", departments);
//			model.addAttribute("selectedDepartmentId", selectedDepartmentId);
//			model.addAttribute("selectedCourseId", selectedCourseId);
//			model.addAttribute("selectedSemesterId", selectedSemesterId);
//			
//			return "user_add";
//		} catch (Exception e) {
//			redirectAttributes.addFlashAttribute("message", e.getMessage());
//			return "redirect:/users/view";
//		}
//	}
	
	@GetMapping("/edit/{id}")
	public String editTutorial(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
	    try {
	        AppUser user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
	        
	        UserMasterRole usermasterRole = new UserMasterRole();
	        usermasterRole.setAppUser(user);

	        long roleID = userRoleRepository.findRole(id);
	        AppRole role = appRoleRepository.findByRoleId(roleID);
	        usermasterRole.setAppRole(role);

	        model.addAttribute("user", user);
	        model.addAttribute("pageTitle", "Edit User (ID: " + id + ")");
	        model.addAttribute("userID", id);
	        model.addAttribute("objEnt", usermasterRole);
	        model.addAttribute("groupList", groupRepository.getAllGroups());
	        model.addAttribute("switchList", switchRepository.getAllSwitch());

	        // Add Departments
	        List<DepartmentMaster> departments = DepartmentMasterRepository.findAll();
	        model.addAttribute("departments", departments);

	        // ✅ Pass selected values for preselection in JS
	        model.addAttribute("selectedDepartmentId", user.getDepartmentName());
	        model.addAttribute("selectedCourseId", user.getCourseName());
	        model.addAttribute("selectedSemesterId", user.getSemesterName());

	        return "user_add";
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("message", e.getMessage());
	        return "redirect:/users/view";
	    }
	}


	@GetMapping("/delete/{id}")
	public String deleteTutorial(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
		try {
			userRepository.deleteById(id);
			redirectAttributes.addFlashAttribute("message",
					"The User with id=" + id + " has been deleted successfully!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/users/view";
	}

	@GetMapping("/resetPassForm")
	public String resetPasswordForm(Model model) {
		AppUser user = new AppUser();
		model.addAttribute("objEnt", user);
		return "reset_password";
	}

	@RequestMapping("/resetloginpassword")
	public String reset(Model model, HttpServletRequest request) {
		AppUser user = new AppUser();
		
		String resetPasswordMessage = (String) request.getSession().getAttribute("resetPasswordMessage");

	    if (resetPasswordMessage != null) {
	        model.addAttribute("resetPasswordMessage", resetPasswordMessage);
	        request.getSession().removeAttribute("resetPasswordMessage"); // Remove the attribute
	    }
	    
		model.addAttribute("objEnt", user);
		return "firstLoginPasswordReset";
	}

	@GetMapping("/validateOldPassword")
	public @ResponseBody String validateOldPassword(@RequestParam String password, Principal principal) {
		String result = "";
		String oldPass = "";
		if (principal == null) {
			// Redirect to login page if the principal is null
			return "redirect:/";
		}
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		try {

			List<AppUser> appUser = userRepository.findByuserName(loginedUser.getUsername());
			for (AppUser user : appUser) {
				System.out.println("old password = " + user.getEncrytedPassword());
				oldPass = user.getEncrytedPassword();
			}
			if (passwordEncoder.matches(password, oldPass)) {
				result = "match";
			}

		} catch (Exception e) {
			System.out.println("Exception occured while checking for duplicate username = " + e);
		}
		return result;
	}

	@PostMapping("/reset")
	public String resetPassword(AppUser user, RedirectAttributes redirectAttributes, Principal principal, Model model) {
		System.out.println("new password  = " + user.getEncrytedPassword());
		if (principal == null) {
			// Redirect to login page if the principal is null
			return "redirect:/";
		}
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		try {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			AppUser user1 = userRepository.findByUsername(loginedUser.getUsername());
			user1.setEncrytedPassword(encoder.encode(user.getEncrytedPassword()));
			user1.setConfirmPassword(user.getConfirmPassword());
			user1.setPasswordChangedTime(new Date());
			userRepository.save(user1);
			
			redirectAttributes.addFlashAttribute("message", "success");
		} catch (Exception e) {
			System.out.println("Exception occured while resetting password = " + e);
		}

		return "redirect:/users/resetPassForm";
	}

	@PostMapping("/resetpassword")
	public String resetpassword(AppUser user, RedirectAttributes redirectAttributes, Principal principal, Model model) {
		System.out.println("new password  = " + user.getEncrytedPassword());
		if (principal == null) {
			// Redirect to login page if the principal is null
			return "redirect:/";
		}
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		try {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			AppUser user1 = userRepository.findByUsername(loginedUser.getUsername());
			user1.setEncrytedPassword(encoder.encode(user.getEncrytedPassword()));
			user1.setConfirmPassword(user.getConfirmPassword());
			user1.setIsFirstTimeLogin(false);
			user1.setPasswordChangedTime(new Date());
			userRepository.save(user1);
			redirectAttributes.addFlashAttribute("resetPasswordMessage", "success");
		} catch (Exception e) {
			System.out.println("Exception occured while resetting password = " + e);
		}

		return "redirect:/";
	}
	
	@RequestMapping("/resetuserpassword/{id}")
	public String resetuserpassword(Model model, @PathVariable("id") Long id) {
		AppUser user = new AppUser();
		model.addAttribute("objEnt", user);
		model.addAttribute("userId", id);
		return "resetUserPasswordForm";
	}
	
	@PostMapping("/userpasswordreset")
	public String userpasswordreset(AppUser user, @RequestParam("userId") long userId, RedirectAttributes redirectAttributes, Model model) {
		
		try {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			AppUser user1 = userRepository.findById(userId).get();
			user1.setEncrytedPassword(encoder.encode(user.getEncrytedPassword()));
			user1.setConfirmPassword(user.getConfirmPassword());
			user1.setPasswordChangedTime(new Date());
			userRepository.save(user1);
			
			redirectAttributes.addFlashAttribute("userPasswordResetMsg", "success");
		} catch (Exception e) {
			System.out.println("Exception occured while resetting password = " + e);
			redirectAttributes.addFlashAttribute("userPasswordResetMsg", "fail");
		}

		return "redirect:/users/view";
	}

	@GetMapping("/signup")
	public String CreateUser(Model model) {
		UserMasterRole user = new UserMasterRole();
		model.addAttribute("objEnt", user);
		model.addAttribute("pageTitle", "Create new User");
		model.addAttribute("groupList", groupRepository.getAllGroups());
//		model.addAttribute("switchList", switchRepository.getAllSwitch());
		return "CreateUser";
	}

	@PostMapping("/account")
	public String savenewUser(UserMasterRole userRole, RedirectAttributes redirectAttributes, BindingResult result,
			Model model) {
		System.out.println("App save new User save: controler ");

		if (result.hasErrors()) {
			System.out.println("App save new User save: error ");

		}
		AppUser user = userRole.getAppUser();
		System.out.println("user id = " + user.getUserId());

		try {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			user.setEncrytedPassword(encoder.encode(user.getEncrytedPassword()));
			user.setEnabled(true);
			user.setGenerationType("1");
			user.setGroupName("No Group");

			Switch switch_id = new Switch();
			switch_id.setId(1);

			user.setSwitch_id(switch_id);

			userRepository.save(user);
			redirectAttributes.addFlashAttribute("message", "Account created successfully!");
		} catch (Exception e) {
			// redirectAttributes.addAttribute("message", e.getMessage());
			System.out.println("Exception e1:" + e);
			UserMasterRole user2 = new UserMasterRole();
			user2.setAppUser(user);
			model.addAttribute("objEnt", user2);
			model.addAttribute("pageTitle", "Create new User");
			model.addAttribute("groupList", groupRepository.getAllGroups());
			return "CreateUser";
		}

		try {
			Long role_id = (long) 2;
			AppRole aprole = new AppRole();
			aprole.setRoleId(role_id);

			UserRole userRole2 = new UserRole();
			userRole2.setAppUser(user);

			userRole2.setAppRole(aprole);
			userRoleRepository.save(userRole2);
			redirectAttributes.addFlashAttribute("message", "Account created successfully!");
		} catch (Exception e) {
			// redirectAttributes.addAttribute("message", e.getMessage());
			System.out.println("Exception e2:" + e);
		}

		return "redirect:/users/signup";
	}

//	new mappings

	@GetMapping("/ForgetPass")
	public String ForgetPass(Model model) {
		UserMasterRole user = new UserMasterRole();
		model.addAttribute("objEnt", user);
		model.addAttribute("pageTitle", "Create new User");
		model.addAttribute("groupList", groupRepository.getAllGroups());
//		model.addAttribute("switchList", switchRepository.getAllSwitch());
		return "ForgetPassword";
	}

	@GetMapping("/Customeview/{id}")
	public String Customeview(Model model, Principal principal, @PathVariable("id") String id) {
		System.out.println("user controller : " + id);
		String compareRole = "";
		if (id.equalsIgnoreCase("Authorizer")) {
			compareRole = "ROLE_ADMIN";
		} else if (id.equalsIgnoreCase("Requester")) {
			compareRole = "ROLE_USER";
		}
		if (principal == null) {
			// Redirect to login page if the principal is null
			return "redirect:/";
		}
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String groupName = "";
		try {
			List<AppUser> users = new ArrayList<AppUser>();
			List<AppUser> users2 = new ArrayList<AppUser>();
			boolean isSuperAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));

			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

			List<AppUser> user1 = userRepository.findByuserName(loginedUser.getUsername());
			for (AppUser appUser : user1) {
				groupName = appUser.getGroupName();
			}

			if (isSuperAdmin) {
				userRepository.findAll().forEach(users::add);

				for (AppUser appUser : users) {

//					System.out.println(" " + appUser.getUserId() + " " + appUser.getUserName());

					long roleID = userRoleRepository.findRole(appUser.getUserId());
					AppRole role = appRoleRepository.findByRoleId(roleID);
					System.out.println(" " + appUser.getUserId() + " " + appUser.getUserName() + " " + roleID + " "
							+ role.getRoleName());

//					if (role.getRoleName().equalsIgnoreCase("ROLE_USER")) {
//
//					} else if (role.getRoleName().equalsIgnoreCase("ROLE_ADMIN")) {
//
//					} else if (role.getRoleName().equalsIgnoreCase("ROLE_SUPERADMIN")) {
//
//					}

					if (role.getRoleName().equalsIgnoreCase(compareRole)) {

						users2.add(appUser);

					}
				}
//				model.addAttribute("listObj", users);
				model.addAttribute("listObj", users2);
			} else if (isAdmin) {
				System.out.println("group = " + groupName);
				List<String> groups = new ArrayList<>();
				StringTokenizer token = new StringTokenizer(groupName, ",");
				while (token.hasMoreTokens()) {
					groups.add(token.nextToken());
				}
				userRepository.findBygroups(groups).forEach(users::add);

				for (AppUser appUser : users) {

//					System.out.println(" " + appUser.getUserId() + " " + appUser.getUserName());

					long roleID = userRoleRepository.findRole(appUser.getUserId());
					AppRole role = appRoleRepository.findByRoleId(roleID);
					System.out.println(" " + appUser.getUserId() + " " + appUser.getUserName() + " " + roleID + " "
							+ role.getRoleName());

//					if (role.getRoleName().equalsIgnoreCase("ROLE_USER")) {
//
//					} else if (role.getRoleName().equalsIgnoreCase("ROLE_ADMIN")) {
//
//					} else if (role.getRoleName().equalsIgnoreCase("ROLE_SUPERADMIN")) {
//
//					}

					if (role.getRoleName().equalsIgnoreCase(compareRole)) {

						users2.add(appUser);

					}
				}

//				model.addAttribute("listObj", users);
				model.addAttribute("listObj", users2);
			}
			model.addAttribute("titleofpage", id);

		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
		}
		return "CustomeUser_view";
	}

}
