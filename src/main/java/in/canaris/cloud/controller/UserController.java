package in.canaris.cloud.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import in.canaris.cloud.openstack.entity.BatchMaster;
import in.canaris.cloud.openstack.entity.CourseMaster;
import in.canaris.cloud.openstack.entity.DepartmentMaster;
import in.canaris.cloud.openstack.entity.SemesterMaster;
import in.canaris.cloud.repository.AppRoleRepository;
import in.canaris.cloud.repository.BatchMasterRepository;
import in.canaris.cloud.repository.CourseMasterRepository;
import in.canaris.cloud.repository.DepartmentMasterRepository;
import in.canaris.cloud.repository.GroupRepository;
import in.canaris.cloud.repository.SemesterMasterRepository;
import in.canaris.cloud.repository.SwitchRepository;
import in.canaris.cloud.repository.UserRepository;

import in.canaris.cloud.repository.UserRoleRepository;
import in.canaris.cloud.service.UserDetailsServiceImpl;


import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.util.*;

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
	
	@Autowired
	CourseMasterRepository CourseMasterRepository;

	@Autowired
	SemesterMasterRepository SemesterMasterRepository;
	
	@Autowired
	BatchMasterRepository BatchMasterRepository;

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
	
	
//	@GetMapping("/new")
//	public String addUser(Model model) {
//	    UserMasterRole user = new UserMasterRole();
//	    model.addAttribute("objEnt", user);
//	    model.addAttribute("pageTitle", "Create new User");
//
//	    model.addAttribute("groupList", groupRepository.getAllGroups());
//	    model.addAttribute("switchList", switchRepository.getAllSwitch());
//
//	    // Also send departments for the first dropdown
//	    List<DepartmentMaster> depts = DepartmentMasterRepository.findAll();
//	    model.addAttribute("departments", depts);
//
//	    return "user_add";
//	}
//
//
//	@PostMapping("/save")
//	public String saveUser(UserMasterRole userRole, RedirectAttributes redirectAttributes, BindingResult result,
//			Model model) {
//		System.out.println("App User save: controler ");
//
//		if (result.hasErrors()) {
//			System.out.println("App User save: error ");
//
//		}
//		AppUser user = userRole.getAppUser();
//		System.out.println("hhhhhhhhhh " + user.getGroupName());
//		System.out.println("user id = " + user.getUserId());
//
//		if (user.getUserId() == null) {
//			try {
//				BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//				user.setEncrytedPassword(encoder.encode(user.getEncrytedPassword()));
//				user.setEnabled(true);
//				System.out.println("hhhhhhhhhh " + user.getGroupName());
//				userRepository.save(user);
//				redirectAttributes.addFlashAttribute("message", "The User has been saved successfully!");
//			} catch (Exception e) {
//				// redirectAttributes.addAttribute("message", e.getMessage());
//				System.out.println("Exception e1:" + e);
//				UserMasterRole user2 = new UserMasterRole();
//				user2.setAppUser(user);
//				model.addAttribute("objEnt", user2);
//				model.addAttribute("pageTitle", "Create new User");
//				model.addAttribute("groupList", groupRepository.getAllGroups());
//				return "user_add";
//			}
//
//			try {
//				Long role_id = userRole.getAppRole().getRoleId();
//				AppRole aprole = new AppRole();
//				aprole.setRoleId(role_id);
//
//				UserRole userRole2 = new UserRole();
//				System.out.println("hhhhhhhhhh " + user.getGroupName());
//				userRole2.setAppUser(user);
//
//				userRole2.setAppRole(aprole);
//				userRoleRepository.save(userRole2);
//				
//				userDetailsServiceImpl.sendSimpleMail(user.getUserName(), user.getConfirmPassword(), user.getEmail());
//				
//				redirectAttributes.addFlashAttribute("message", "The User has been saved successfully!");
//			} catch (Exception e) {
//				// redirectAttributes.addAttribute("message", e.getMessage());
//				System.out.println("Exception e2:" + e);
//			}
//		} else {
//			try {
//
//				userRepository.updateUser(user.getName(), user.getEmail(), user.getMobileNo(), user.getGroupName(),
//						user.getSwitch_id(), user.getGenerationType(), user.getUserId());
//				userRoleRepository.updateUserRole(user.getUserId(), userRole.getAppRole().getRoleId());
//				redirectAttributes.addFlashAttribute("message", "The User has been updated successfully!");
//			} catch (Exception e) {
//				System.out.println("Exception occured while updating user:" + e);
//			}
//		}
//
//		return "redirect:/users/view";
//	}
	
//	@GetMapping("/edit/{id}")
//	public String editTutorial(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
//	    try {
//	        AppUser user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
//	        
//	        UserMasterRole usermasterRole = new UserMasterRole();
//	        usermasterRole.setAppUser(user);
//
//	        long roleID = userRoleRepository.findRole(id);
//	        AppRole role = appRoleRepository.findByRoleId(roleID);
//	        usermasterRole.setAppRole(role);
//
//	        model.addAttribute("user", user);
//	        model.addAttribute("pageTitle", "Edit User (ID: " + id + ")");
//	        model.addAttribute("userID", id);
//	        model.addAttribute("objEnt", usermasterRole);
//	        model.addAttribute("groupList", groupRepository.getAllGroups());
//	        model.addAttribute("switchList", switchRepository.getAllSwitch());
//
//	        // Add Departments
//	        List<DepartmentMaster> departments = DepartmentMasterRepository.findAll();
//	        model.addAttribute("departments", departments);
//
//	        // ✅ Pass selected values for preselection in JS
//	        model.addAttribute("selectedDepartmentId", user.getDepartmentName());
//	        model.addAttribute("selectedCourseId", user.getCourseName());
//	        model.addAttribute("selectedSemesterId", user.getSemesterName());
//
//	        return "user_add";
//	    } catch (Exception e) {
//	        redirectAttributes.addFlashAttribute("message", e.getMessage());
//	        return "redirect:/users/view";
//	    }
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
	    System.out.println("App User save: controller ");

	    if (result.hasErrors()) {
	        System.out.println("App User save: error ");
	    }
	    
	    AppUser user = userRole.getAppUser();
	    System.out.println("Group: " + user.getGroupName());
	    System.out.println("user id = " + user.getUserId());

	    // For Admin, Super Admin, Teacher - clear academic fields
	    Long roleId = userRole.getAppRole().getRoleId();
	    if (roleId == 1L || roleId == 3L || roleId == 4L) { // Admin, Super Admin, Teacher
	        user.setDepartmentName(null);
	        user.setCourseName(null);
	        user.setSemesterName(null);
	        user.setBatchName(null);
	    }

	    if (user.getUserId() == null) {
	        try {
	            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	            user.setEncrytedPassword(encoder.encode(user.getEncrytedPassword()));
	            user.setEnabled(true);
	            System.out.println("Group: " + user.getGroupName());
	            userRepository.save(user);
	            redirectAttributes.addFlashAttribute("message", "The User has been saved successfully!");
	        } catch (Exception e) {
	            System.out.println("Exception e1:" + e);
	            UserMasterRole user2 = new UserMasterRole();
	            user2.setAppUser(user);
	            model.addAttribute("objEnt", user2);
	            model.addAttribute("pageTitle", "Create new User");
	            model.addAttribute("groupList", groupRepository.getAllGroups());
	            model.addAttribute("departments", DepartmentMasterRepository.findAll());
	            return "user_add";
	        }

	        try {
	            Long role_id = userRole.getAppRole().getRoleId();
	            AppRole aprole = new AppRole();
	            aprole.setRoleId(role_id);

	            UserRole userRole2 = new UserRole();
	            userRole2.setAppUser(user);
	            userRole2.setAppRole(aprole);
	            userRoleRepository.save(userRole2);
	            
	            userDetailsServiceImpl.sendSimpleMail(user.getUserName(), user.getConfirmPassword(), user.getEmail());
	            
	            redirectAttributes.addFlashAttribute("message", "The User has been saved successfully!");
	        } catch (Exception e) {
	            System.out.println("Exception e2:" + e);
	        }
	    } else {
	        try {
	            // Update user with academic fields
	            userRepository.updateUserWithAcademic(user.getName(), user.getEmail(), user.getMobileNo(), 
	                    user.getGroupName(), user.getSwitch_id(), user.getGenerationType(), 
	                    user.getDepartmentName(), user.getCourseName(), user.getSemesterName(), 
	                    user.getBatchName(), user.getUserId());
	                    
	            userRoleRepository.updateUserRole(user.getUserId(), userRole.getAppRole().getRoleId());
	            redirectAttributes.addFlashAttribute("message", "The User has been updated successfully!");
	        } catch (Exception e) {
	            System.out.println("Exception occurred while updating user:" + e);
	            // Reload necessary data for the form
	            model.addAttribute("groupList", groupRepository.getAllGroups());
	            model.addAttribute("switchList", switchRepository.getAllSwitch());
	            model.addAttribute("departments", DepartmentMasterRepository.findAll());
	            return "user_add";
	        }
	    }

	    return "redirect:/users/view";
	}

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

	        // Get selected values for preselection
	        if (user.getDepartmentName() != null) {
	            model.addAttribute("selectedDepartmentId", user.getDepartmentName().getDepartmentId());
	        }
	        if (user.getCourseName() != null) {
	            model.addAttribute("selectedCourseId", user.getCourseName().getCourseId());
	        }
	        if (user.getSemesterName() != null) {
	            model.addAttribute("selectedSemesterId", user.getSemesterName().getSemesterId());
	        }
	        if (user.getBatchName() != null) {
	            model.addAttribute("selectedBatchId", user.getBatchName().getBatchId());
	        }

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
	
	//upload csv code start
	
	


    @GetMapping("/upload")
    public String showUploadPage(Model model) {
        model.addAttribute("pageTitle", "Bulk User Upload");
        return "user_upload";
    }

    @GetMapping("/download-csv-template")
    public void downloadCsvTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=user_upload.csv");
        
        String csvContent = "name,userName,email,mobileNo,roleId,groupName,switchId,generationType,departmentName,courseName,semesterName,batchName\n" +
                           "John Doe,john.doe,john@example.com,9876543210,2,Students,1,1,Computer Science,B.Tech CS,Semester 1,Batch A\n" +
                           "Jane Smith,jane.smith,jane@example.com,9876543211,4,Faculty,1,1,Computer Science,B.Tech CS,Semester 1,Batch A\n" +
                           "Admin User,admin,admin@example.com,9876543212,1,Administrators,1,1,,,,\n" +
                           "Super Admin,superadmin,super@example.com,9876543213,3,SuperAdmins,1,1,,,,\n\n" +
                           "Note:\n" +
                           "- Required fields: name, userName, email, mobileNo, roleId, groupName, switchId, generationType\n" +
                           "- Role IDs: 1=Admin, 2=User, 3=Super Admin, 4=Teacher\n" +
                           "- Generation Type: 1=Generation 1, 2=Generation 2\n" +
                           "- Switch ID: Must be a valid switch ID from the system\n" +
                           "- For Admin, Super Admin, Teacher roles: leave academic fields empty\n" +
                           "- For User role: fill academic fields (departmentName, courseName, semesterName, batchName)";
        
        response.getWriter().write(csvContent);
    }

    @GetMapping("/download-excel-template")
    public void downloadExcelTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=user_upload.xlsx");
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("User Template");
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"name", "userName", "email", "mobileNo", "roleId", "groupName", "switchId", "generationType", "departmentName", "courseName", "semesterName", "batchName"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        
        // Create sample data rows
        Object[][] sampleData = {
            {"John Doe", "john.doe", "john@example.com", "9876543210", "2", "Students", "1", "1", "Computer Science", "B.Tech CS", "Semester 1", "Batch A"},
            {"Jane Smith", "jane.smith", "jane@example.com", "9876543211", "4", "Faculty", "1", "1", "Computer Science", "B.Tech CS", "Semester 1", "Batch A"},
            {"Admin User", "admin", "admin@example.com", "9876543212", "1", "Administrators", "1", "1", "", "", "", ""},
            {"Super Admin", "superadmin", "super@example.com", "9876543213", "3", "SuperAdmins", "1", "1", "", "", "", ""}
        };
        
        for (int i = 0; i < sampleData.length; i++) {
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < sampleData[i].length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(sampleData[i][j].toString());
            }
        }
        
        // Add note row
        Row noteRow = sheet.createRow(sampleData.length + 2);
        Cell noteCell = noteRow.createCell(0);
        noteCell.setCellValue("Note: Required fields - name, userName, email, mobileNo, roleId, groupName, switchId, generationType. Role IDs: 1=Admin, 2=User, 3=Super Admin, 4=Teacher. Generation Type: 1=Generation 1, 2=Generation 2");
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @PostMapping("/bulk-upload")
    @ResponseBody
    public Map<String, Object> bulkUpload(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, String>> successUsers = new ArrayList<>();
        List<Map<String, String>> errorUsers = new ArrayList<>();
        
        try {
            String fileName = file.getOriginalFilename();
            
            if (fileName == null || fileName.isEmpty()) {
                throw new RuntimeException("File name is empty");
            }
            
            if (fileName.toLowerCase().endsWith(".csv")) {
                processCsvFile(file, successUsers, errorUsers);
            } else if (fileName.toLowerCase().endsWith(".xlsx") || fileName.toLowerCase().endsWith(".xls")) {
                processExcelFile(file, successUsers, errorUsers);
            } else {
                throw new RuntimeException("Unsupported file format. Please upload CSV or Excel file.");
            }
            
            response.put("success", true);
            response.put("message", "File processed successfully");
            response.put("successUsers", successUsers);
            response.put("errorUsers", errorUsers);
            response.put("totalProcessed", successUsers.size() + errorUsers.size());
            response.put("successCount", successUsers.size());
            response.put("errorCount", errorUsers.size());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error processing file: " + e.getMessage());
        }
        
        return response;
    }

    private void processCsvFile(MultipartFile file, List<Map<String, String>> successUsers, List<Map<String, String>> errorUsers) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
            
            for (CSVRecord record : csvParser) {
                processUserRecord(record.toMap(), successUsers, errorUsers);
            }
        }
    }

    private void processExcelFile(MultipartFile file, List<Map<String, String>> successUsers, List<Map<String, String>> errorUsers) throws IOException {
        Workbook workbook;
        String fileName = file.getOriginalFilename();
        
        if (fileName.toLowerCase().endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(file.getInputStream());
        } else {
            workbook = new HSSFWorkbook(file.getInputStream());
        }
        
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        
        // Skip header row
        if (rowIterator.hasNext()) {
            rowIterator.next();
        }
        
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Map<String, String> userData = new HashMap<>();
            
            String[] headers = {"name", "userName", "email", "mobileNo", "roleId", "groupName", "switchId", "generationType", "departmentName", "courseName", "semesterName", "batchName"};
            
            for (int i = 0; i < headers.length && i < row.getLastCellNum(); i++) {
                Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String value = getCellValueAsString(cell);
                userData.put(headers[i], value);
            }
            
            processUserRecord(userData, successUsers, errorUsers);
        }
        
        workbook.close();
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Check if it's an integer value
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception ex) {
                        return cell.getCellFormula();
                    }
                }
            default:
                return "";
        }
    }

    private void processUserRecord(Map<String, String> userData, List<Map<String, String>> successUsers, List<Map<String, String>> errorUsers) {
        try {
            // Debug: Print all received data
            System.out.println("Processing record for user: " + userData.get("userName"));
            userData.forEach((key, value) -> System.out.println(key + ": '" + value + "'"));
            
            // Validate required fields
            if (!isValidUserData(userData)) {
                Map<String, String> error = new HashMap<>();
                error.put("userName", userData.get("userName"));
                error.put("error", "Missing required fields");
                errorUsers.add(error);
                return;
            }
            
            // Check if username already exists
            if (userRepository.existsByUserName(userData.get("userName"))) {
                Map<String, String> error = new HashMap<>();
                error.put("userName", userData.get("userName"));
                error.put("error", "Username already exists");
                errorUsers.add(error);
                return;
            }
            
            // Validate switch exists with proper error handling
            String switchIdStr = userData.get("switchId");
            Integer switchId = null;

            try {
                if (switchIdStr != null && !switchIdStr.trim().isEmpty()) {
                    switchId = Integer.parseInt(switchIdStr.trim());
                } else {
                    Map<String, String> error = new HashMap<>();
                    error.put("userName", userData.get("userName"));
                    error.put("error", "Switch ID is required");
                    errorUsers.add(error);
                    return;
                }
            } catch (NumberFormatException e) {
                Map<String, String> error = new HashMap<>();
                error.put("userName", userData.get("userName"));
                error.put("error", "Invalid switch ID format: '" + switchIdStr + "'. Must be a number.");
                errorUsers.add(error);
                return;
            }

            Switch userSwitch = switchRepository.findById(switchId).orElse(null);
            if (userSwitch == null) {
                Map<String, String> error = new HashMap<>();
                error.put("userName", userData.get("userName"));
                error.put("error", "Invalid switch ID: " + switchId);
                errorUsers.add(error);
                return;
            }
            
            // Create and save user
            AppUser user = new AppUser();
            user.setName(userData.get("name"));
            user.setUserName(userData.get("userName"));
            user.setEmail(userData.get("email"));
            user.setMobileNo(userData.get("mobileNo"));
            user.setGroupName(userData.get("groupName"));
            user.setSwitch_id(userSwitch);
            
            // Set generation type (default to "1" if not provided)
            String generationType = userData.get("generationType");
            if (generationType != null && !generationType.trim().isEmpty()) {
                user.setGenerationType(generationType);
            } else {
                user.setGenerationType("1");
            }
            
            // Set default password and confirm password
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String defaultPassword = "defaultPassword123";
            user.setEncrytedPassword(encoder.encode(defaultPassword));
            user.setConfirmPassword(defaultPassword);
            
            // Set default values
            user.setEnabled(true);
            user.setIsFirstTimeLogin(true);
            user.setStatus("Active");
            
            // Handle academic fields based on role
            Long roleId = Long.parseLong(userData.get("roleId"));
            if (roleId == 2L) {
                setAcademicFields(user, userData);
            } else {
                user.setDepartmentName(null);
                user.setCourseName(null);
                user.setSemesterName(null);
                user.setBatchName(null);
            }
            
            // Save user
            AppUser savedUser = userRepository.save(user);
            
            // Create user role
            AppRole appRole = new AppRole();
            appRole.setRoleId(roleId);
            
            UserRole userRole = new UserRole();
            userRole.setAppUser(savedUser);
            userRole.setAppRole(appRole);
            userRoleRepository.save(userRole);
            
            // Add to success list
            Map<String, String> success = new HashMap<>();
            success.put("name", savedUser.getName());
            success.put("userName", savedUser.getUserName());
            success.put("email", savedUser.getEmail());
            success.put("generationType", savedUser.getGenerationType());
            successUsers.add(success);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("userName", userData.get("userName"));
            error.put("error", "Error creating user: " + e.getMessage());
            errorUsers.add(error);
            e.printStackTrace();
        }
    }

    private boolean isValidUserData(Map<String, String> userData) {
        return userData.get("name") != null && !userData.get("name").trim().isEmpty() &&
               userData.get("userName") != null && !userData.get("userName").trim().isEmpty() &&
               userData.get("email") != null && !userData.get("email").trim().isEmpty() &&
               userData.get("mobileNo") != null && !userData.get("mobileNo").trim().isEmpty() &&
               userData.get("roleId") != null && !userData.get("roleId").trim().isEmpty() &&
               userData.get("groupName") != null && !userData.get("groupName").trim().isEmpty() &&
               userData.get("switchId") != null && !userData.get("switchId").trim().isEmpty() &&
               userData.get("generationType") != null && !userData.get("generationType").trim().isEmpty();
    }

    private void setAcademicFields(AppUser user, Map<String, String> userData) {
        try {
            // Set department if provided
            if (userData.get("departmentName") != null && !userData.get("departmentName").trim().isEmpty()) {
                DepartmentMaster dept = DepartmentMasterRepository.findByDepartmentName(userData.get("departmentName"));
                if (dept != null) {
                    user.setDepartmentName(dept);
                } else {
                    System.out.println("Warning: Department not found: " + userData.get("departmentName"));
                    // You might want to add this to error list or handle differently
                }
            }
            
            // Set course if provided
            if (userData.get("courseName") != null && !userData.get("courseName").trim().isEmpty()) {
                CourseMaster course = CourseMasterRepository.findByCourseName(userData.get("courseName"));
                if (course != null) {
                    user.setCourseName(course);
                } else {
                    System.out.println("Warning: Course not found: " + userData.get("courseName"));
                }
            }
            
            // Set semester if provided
            if (userData.get("semesterName") != null && !userData.get("semesterName").trim().isEmpty()) {
                SemesterMaster semester = SemesterMasterRepository.findBySemesterName(userData.get("semesterName"));
                if (semester != null) {
                    user.setSemesterName(semester);
                } else {
                    System.out.println("Warning: Semester not found: " + userData.get("semesterName"));
                }
            }
            
            // Set batch if provided
            if (userData.get("batchName") != null && !userData.get("batchName").trim().isEmpty()) {
                BatchMaster batch = BatchMasterRepository.findByBatchName(userData.get("batchName"));
                if (batch != null) {
                    user.setBatchName(batch);
                } else {
                    System.out.println("Warning: Batch not found: " + userData.get("batchName"));
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error setting academic fields: " + e.getMessage());
            e.printStackTrace();
            // Continue without academic fields if there's an error
        }
    }

}
