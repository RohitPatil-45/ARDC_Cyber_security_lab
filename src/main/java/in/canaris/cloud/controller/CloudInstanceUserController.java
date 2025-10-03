package in.canaris.cloud.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import in.canaris.cloud.entity.AdditionalStorage;
import in.canaris.cloud.entity.AppUser;
import in.canaris.cloud.entity.CloudInstance;
import in.canaris.cloud.entity.CloudInstanceLog;
import in.canaris.cloud.entity.Customer;
import in.canaris.cloud.entity.RequestApproval;
import in.canaris.cloud.entity.Switch;
import in.canaris.cloud.entity.VPC;
import in.canaris.cloud.repository.AdditionalStorageRepository;
import in.canaris.cloud.repository.AppUserRepository;
import in.canaris.cloud.repository.CloudInstanceLogRepository;
import in.canaris.cloud.repository.CloudInstanceRepository;
import in.canaris.cloud.repository.DiscountRepository;
import in.canaris.cloud.repository.FirewallRepository;
import in.canaris.cloud.repository.LocationRepository;
import in.canaris.cloud.repository.PriceRepository;
import in.canaris.cloud.repository.RequestApprovalRepository;
import in.canaris.cloud.repository.SubProductRepository;
import in.canaris.cloud.repository.SwitchRepository;
import in.canaris.cloud.repository.UserRepository;
import in.canaris.cloud.repository.VPCRepository;

@Controller
@RequestMapping("/cloud_instance_user")
public class CloudInstanceUserController {

	@Autowired
	private CloudInstanceRepository repository;

	@Autowired
	private CloudInstanceLogRepository cloudInstanceLogRepository;

	@Autowired
	private LocationRepository repositoryLocation;

	@Autowired
	private VPCRepository repositoryVPC;

	@Autowired
	private PriceRepository priceRepository;

	@Autowired
	private FirewallRepository firewallRepository;

	@Autowired
	private SwitchRepository switchRepository;

	@Autowired
	private SubProductRepository subProductRepository;

	@Autowired
	private DiscountRepository discountRepository;

	@Autowired
	private RequestApprovalRepository approvalRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AppUserRepository appRepository;

	@Autowired
	private AdditionalStorageRepository externalStorageRepository;

	final String var_function_name = "cloud_instance"; // small letter
	final String disp_function_name = "Cloud Instance"; // capital letter

	@GetMapping("/vm_user")
	public ModelAndView vm_user() {
		ModelAndView mav = new ModelAndView("cloud_instance_user_add");
		mav.addObject("pageTitle", "Add New " + disp_function_name);
		mav.addObject("action_name", var_function_name);
		mav.addObject("dcLocationList", repositoryLocation.getAllDClocations());
		// mav.addObject("vpcList", repositoryVPC.getAllVPC());
		mav.addObject("securityGroupList", firewallRepository.getFirewall());
		mav.addObject("sharedCpuPlan", priceRepository.getSharedCpuPlan());
		mav.addObject("dedicatedCpuPlan", priceRepository.getDedicatedCpuPlan());
		mav.addObject("highMemoryPlan", priceRepository.getHighMemoryPlan());
		// mav.addObject("switchList", switchRepository.getAllSwitch());
		CloudInstance objEnt = new CloudInstance();
		mav.addObject("objEnt", objEnt);
		return mav;
	}

	@GetMapping("/view")
	public ModelAndView getAll(Principal principal) {
		ModelAndView mav = new ModelAndView("cloud_instance_view");

		if (principal == null) {
			ModelAndView mav1 = new ModelAndView("welcomePage");
			return mav1;
		}
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();
		AppUser obj = appRepository.findOneByUserName(username);
		mav.addObject("action_name", var_function_name);
		try {
			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
			if (isAdmin) {
				mav.addObject("listObj", repository.findByIsMonitoring(true));
			} else {
				// String groupName = obj.getGroupName();
				List<String> groupName = new ArrayList<>();
				StringTokenizer token = new StringTokenizer(obj.getGroupName(), ",");
				while (token.hasMoreTokens()) {
					groupName.add(token.nextToken());
				}

				System.out.println("Inoperator groupName = " + groupName);
				mav.addObject("listObj", repository.findByIsMonitoringAndGroupName(true, groupName));
			}
		} catch (Exception e) {
			System.out.println("Exception occured while fetching VM Data = " + e);
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
		}
		return mav;
	}

	@PostMapping("/vmRequest")
	public String vmRequest(CloudInstance obj, RedirectAttributes redirectAttributes, Principal principal) {

		String responce_data = null;
		boolean isVMCreated = false;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();
		try {

			String vm_name = obj.getInstance_name();
			String vhd_path = obj.getVm_location_path() + "\\" + vm_name + "\\" + vm_name + ".vhdx";
			String iso_file_path = subProductRepository.getISOFilePath(obj.getSubproduct_id().getId());
			obj.setDisk_path(vhd_path);
			obj.setIso_file_path(iso_file_path);
			obj.setRequest_status("Pending");
			obj.setMonitoring(false);

			List<Object[]> userData = userRepository.getData(username);
			for (Object[] newObj : userData) {
				obj.setGeneration_type((newObj[0].toString() == null) ? "-"
						: newObj[0].toString().equals("") ? "-" : newObj[0].toString());
				obj.setSwitch_id((Switch) newObj[1]);
			}
			VPC vpc_id = new VPC();
			Customer customer = new Customer();
			customer.setId(1);
			vpc_id.setId(1);
			obj.setVpc_id(vpc_id);
			obj.setRequest_status("Approved");
//			obj.setGroupName("GroupA");
//			obj.setCustomer_id(customer);

			CloudInstance result = repository.save(obj);
			System.out.println("instance id = " + result.getId());

			CloudInstanceLog log = new CloudInstanceLog();
			log.setInstance_ip(result.getInstance_ip());
			log.setInstance_name(result.getInstance_name());
			log.setInstance_password(result.getInstance_password());
			log.setDisk_path(result.getDisk_path());
			log.setGeneration_type(result.getGeneration_type());
			log.setIso_file_path(result.getIso_file_path());
			log.setVm_location_path(result.getVm_location_path());
			log.setLocation_id(result.getLocation_id());
			log.setPrice_id(result.getPrice_id());
			log.setSecurity_group_id(result.getSecurity_group_id());
			log.setSubproduct_id(result.getSubproduct_id());
			log.setSwitch_id(result.getSwitch_id());
			log.setVpc_id(result.getVpc_id());
			log.setRequest_type("instance create");
			CloudInstanceLog savedLog = cloudInstanceLogRepository.save(log);

			RequestApproval req = new RequestApproval();
			req.setRequestId(result.getId());
			req.setRequesterName(username);
			req.setRequest_status("Pending");
			

			Authentication authentication = (Authentication) principal;
			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
			if (isAdmin) {
				req.setAdminApproval("Approved");
			}
			else
			{
				req.setAdminApproval("Pending");
				
			}
			//req.setAdminApproval("Pending");
			req.setRequest_type("instance create");
			req.setDescription("Vm creation for the user");
			req.setLog_id(savedLog);
			approvalRepository.save(req);

			isVMCreated = true;

		} catch (Exception e) {
			System.out.println("Exceptin" + e);
			responce_data = "" + e;
		}

		if (isVMCreated) {
			redirectAttributes.addFlashAttribute("message",
					"Dear " + username + ", your request has been raised for VM Creation");
		} else {
			redirectAttributes.addAttribute("message", "Looks like there is something wrong in raising your request");
		}
		return "redirect:/" + var_function_name + "/view";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") String id_name, Model model, RedirectAttributes redirectAttributes,
			Principal principal) {

		// String responce_data = null;

		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();

		String id = id_name.substring(0, id_name.indexOf("~"));

		String name = id_name.substring(id_name.indexOf("~") + 1);
		System.out.println("Instance name:" + name);
		System.out.println("Insatnce id" + id);
		String responce_data = null;
		try {
//			ExecutePSCommand execute = new ExecutePSCommand();
//			responce_data = execute.deleteVM(name);
//			System.out.println("responce_data:" + responce_data);
//			/// if (responce_data.equals("VL Created Successfully")) {
//			try {
//				repository.deleteById(Integer.parseInt(id));
//				redirectAttributes.addFlashAttribute("message",
//						"The " + disp_function_name + " with id=" + id + " has been deleted successfully!");
//			} catch (Exception e) {
//				redirectAttributes.addFlashAttribute("message", e.getMessage());
//			}

			CloudInstance obj = repository.findById(Integer.parseInt(id)).get();
			obj.setRequest_status("Delete Pending");
			repository.save(obj);

			CloudInstanceLog log = new CloudInstanceLog();
			log.setInstance_ip(obj.getInstance_ip());
			log.setInstance_name(obj.getInstance_name());
			log.setInstance_password(obj.getInstance_password());
			log.setDisk_path(obj.getDisk_path());
			log.setGeneration_type(obj.getGeneration_type());
			log.setIso_file_path(obj.getIso_file_path());
			log.setVm_location_path(obj.getVm_location_path());
			log.setLocation_id(obj.getLocation_id());
			log.setPrice_id(obj.getPrice_id());
			log.setSecurity_group_id(obj.getSecurity_group_id());
			log.setSubproduct_id(obj.getSubproduct_id());
			log.setSwitch_id(obj.getSwitch_id());
			log.setVpc_id(obj.getVpc_id());
			log.setRequest_type("instance delete");
			CloudInstanceLog savedLog = cloudInstanceLogRepository.save(log);

			RequestApproval requestObj = new RequestApproval();
			requestObj.setRequestId(obj.getId());
			requestObj.setRequesterName(username);
			requestObj.setDescription("Request for VM Deletion");
			requestObj.setRequest_status("Pending");
			requestObj.setAdminApproval("Pending");
			requestObj.setRequest_type("instance delete");
			requestObj.setLog_id(savedLog);
			approvalRepository.save(requestObj);

			redirectAttributes.addFlashAttribute("message",
					"Dear " + username + ", your request has been raised for VM Deletion");

		} catch (Exception e) {
			System.out.println("Exceptin" + e);
			responce_data = "" + e;
			redirectAttributes.addFlashAttribute("message", "Dear " + username + ", Looks Like something is wrong");
		}

		return "redirect:/" + var_function_name + "/view";
	}

	@PostMapping("/resizeTabUser")
	public String resizeTab(@ModelAttribute("vmDetails") CloudInstance obj, RedirectAttributes redirectAttributes,
			Principal principal, @RequestParam("newRAM") String ram,
			@RequestParam("newCpu") String cpu, @RequestParam(name = "cpuCheck", defaultValue = "off") String cpuCheck) 
	
	{
	
		//, @RequestParam("storage_size") String storage_size
		CloudInstance entity = repository.findById(obj.getId()).get();
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();

		HashMap<String, String> oldData = new HashMap<>();
		HashMap<String, String> newData = new HashMap<>();
		
//		if(!(Integer.valueOf(obj.getPrice_id().getSsd_disk().replaceAll("\\D+", "")) > Integer.valueOf(entity.getPrice_id().getSsd_disk().replaceAll("\\D+", ""))))
//		{
//			redirectAttributes.addFlashAttribute("resizeMessage",
//					"Dear " + username + ", please select plan which has SSD size greater than your previous SSD size");
//			return "redirect:/cloud_instance/VM/"+obj.getId();
//		}
//		else if(!(Integer.valueOf(obj.getPrice_id().getRam().replaceAll("\\D+", "")) >= Integer.valueOf(entity.getPrice_id().getRam().replaceAll("\\D+", ""))))
//		{
//			redirectAttributes.addFlashAttribute("resizeMessage",
//					"Dear " + username + ", please select plan which has RAM size greater than your previous RAM size");
//			return "redirect:/cloud_instance/VM/"+obj.getId();
//		}
//		else if(!(Integer.valueOf(obj.getPrice_id().getvCpu().replaceAll("\\D+", "")) >= Integer.valueOf(entity.getPrice_id().getvCpu().replaceAll("\\D+", ""))))
//		{
//			redirectAttributes.addFlashAttribute("resizeMessage",
//					"Dear " + username + ", please select plan which has CPU core greater than your previous number of CPU core");
//			return "redirect:/cloud_instance/VM/"+obj.getId();
//		}
//		else
//		{
			try {
				System.out.println("Resize tab controller called");
				System.out.println("CPU: " + entity.getPrice_id().getvCpu() + "\nRAM: " + entity.getPrice_id().getRam()
						+ "\nSSD: " + entity.getPrice_id().getSsd_disk() + "\nBandwidth: " + entity.getPrice_id().getBandwidth());
				try {
					CloudInstanceLog log = new CloudInstanceLog();
					log.setInstance_ip(entity.getInstance_ip());
					log.setInstance_name(entity.getInstance_name());
					log.setInstance_password(entity.getInstance_password());
					log.setDisk_path(entity.getDisk_path());
					log.setGeneration_type(entity.getGeneration_type());
					log.setIso_file_path(entity.getIso_file_path());
					log.setVm_location_path(entity.getVm_location_path());
					log.setLocation_id(entity.getLocation_id());
					log.setPrice_id(entity.getPrice_id());
					log.setSecurity_group_id(entity.getSecurity_group_id());
					log.setSubproduct_id(entity.getSubproduct_id());
					log.setSwitch_id(entity.getSwitch_id());
					log.setVpc_id(entity.getVpc_id());
					log.setRequest_type("instance update");
					CloudInstanceLog savedLog = cloudInstanceLogRepository.save(log);

					RequestApproval req = new RequestApproval();
					req.setRequestId(obj.getId());
					req.setRequesterName(username);
					req.setRequest_status("Pending");
					req.setAdminApproval("Pending");
					req.setSub_request_type("resize");
					req.setRequest_type("instance update");
					req.setDescription("Request for Resizing VM");

					oldData.put("price_id", String.valueOf(entity.getPrice_id().getId()));
					newData.put("ram", ram);
					newData.put("cpu", cpu);
					//newData.put("storage", storage_size);

					req.setOldData(oldData.toString());
					req.setNewData(newData.toString());

					req.setLog_id(savedLog);
					approvalRepository.save(req);

				} catch (Exception ee) {
					System.out.println("exception while adding in log = " + ee);
				}

				redirectAttributes.addFlashAttribute("message",
						"Dear " + username + ", your request has been raised for Resizing VM");
			} catch (Exception e) {
				redirectAttributes.addAttribute("message", e.getMessage());
			}
			return "redirect:/" + var_function_name + "/view";
		//}
		
		
	}

	@GetMapping("/checkforDuplicateVM")
	public @ResponseBody String checkforDuplicateVM(@RequestParam String instanceName) {
		String duplicate = "";
		try {

			List<CloudInstance> list = repository.findByInstanceName(instanceName);
			if (!list.isEmpty()) {
				duplicate = "duplicate";
			}

		} catch (Exception e) {
			System.out.println("Exception occured while checking for duplicate VM = " + e);
		}
		return duplicate;
	}

	// Add External Storage
	@GetMapping("/additionalStorageRequest")
	public @ResponseBody String addStorage(@RequestParam String instance_id, @RequestParam String disk_path, @RequestParam String disk_size,
			Principal principal) {

		System.out.println("data for external storage = " + instance_id + " - " + disk_path + " - " + disk_size);
		boolean isStorageAdded = false;
		CloudInstance obj = repository.findById(Integer.valueOf(instance_id)).get();
		CloudInstance entity = repository.findById(obj.getId()).get();
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();
		try {
			CloudInstanceLog log = new CloudInstanceLog();
			log.setInstance_ip(entity.getInstance_ip());
			log.setInstance_name(entity.getInstance_name());
			log.setInstance_password(entity.getInstance_password());
			log.setDisk_path(entity.getDisk_path());
			log.setGeneration_type(entity.getGeneration_type());
			log.setIso_file_path(entity.getIso_file_path());
			log.setVm_location_path(entity.getVm_location_path());
			log.setLocation_id(entity.getLocation_id());
			log.setPrice_id(entity.getPrice_id());
			log.setSecurity_group_id(entity.getSecurity_group_id());
			log.setSubproduct_id(entity.getSubproduct_id());
			log.setSwitch_id(entity.getSwitch_id());
			log.setVpc_id(entity.getVpc_id());
			log.setRequest_type("additional_storage");
			CloudInstanceLog savedLog = cloudInstanceLogRepository.save(log);

			RequestApproval req = new RequestApproval();
			req.setRequestId(entity.getId());
			req.setRequesterName(username);
			req.setRequest_status("Pending");
			req.setAdminApproval("Pending");
			req.setSub_request_type("add_storage");
			req.setRequest_type("additional_storage");
			req.setDescription("Request for Additional storage");
			req.setLog_id(savedLog);
			approvalRepository.save(req);
			isStorageAdded = true;

			AdditionalStorage storage = new AdditionalStorage();
			obj.setId(Integer.valueOf(instance_id));
			storage.setInstance_id(obj);
			storage.setPrice(Integer.valueOf(disk_size) * 9);
			storage.setStorage_size(disk_size);
			storage.setStoragePath(disk_path);
			storage.setStatus("Pending");
			externalStorageRepository.save(storage);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return String.valueOf(isStorageAdded);

	}

}
