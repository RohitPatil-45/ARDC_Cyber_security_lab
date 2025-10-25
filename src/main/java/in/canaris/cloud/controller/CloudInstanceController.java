package in.canaris.cloud.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.util.StringBuilders;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import in.canaris.cloud.entity.AdditionalStorage;
import in.canaris.cloud.entity.AlertDash;
import in.canaris.cloud.entity.AppRole;
import in.canaris.cloud.entity.AppUser;
import in.canaris.cloud.entity.CloudInstance;
import in.canaris.cloud.entity.CloudInstanceCpuThresholdHistory;
import in.canaris.cloud.entity.CloudInstanceLog;
import in.canaris.cloud.entity.CloudInstanceMemoryThresholdHistory;
import in.canaris.cloud.entity.CloudInstanceNodeHealthMonitoring;
import in.canaris.cloud.entity.CloudInstanceUsage;
import in.canaris.cloud.entity.CloudInstanceUsageDaily;
import in.canaris.cloud.entity.Customer;
import in.canaris.cloud.entity.Discount;
import in.canaris.cloud.entity.DiscoverDockerContainers;
import in.canaris.cloud.entity.Group;
import in.canaris.cloud.entity.KVMDriveDetails;
import in.canaris.cloud.entity.Location;
import in.canaris.cloud.entity.PortDetails;
import in.canaris.cloud.entity.Price;
import in.canaris.cloud.entity.ProxmoxAssignedIpAddress;
import in.canaris.cloud.entity.RequestApproval;
import in.canaris.cloud.entity.SecurityGroup;
import in.canaris.cloud.entity.SubProduct;
import in.canaris.cloud.entity.Switch;
import in.canaris.cloud.entity.VMCreationBean;
import in.canaris.cloud.entity.VPC;
import in.canaris.cloud.entity.billing;
import in.canaris.cloud.openstack.entity.AssessmentUserLab;
import in.canaris.cloud.openstack.entity.AssessmentUserWiseChatBoatInstructionTemplate;
import in.canaris.cloud.openstack.entity.ChartBoatInstructionTemplate;
import in.canaris.cloud.openstack.entity.CloudInstanceForm;
import in.canaris.cloud.openstack.entity.InstructionDto;
import in.canaris.cloud.openstack.entity.ScenarioLabTemplate;
import in.canaris.cloud.openstack.entity.UserLab;
import in.canaris.cloud.openstack.entity.UserScenario;
import in.canaris.cloud.openstack.entity.UserWiseChatBoatInstructionTemplate;
import in.canaris.cloud.openstack.repository.ScenarioLabTemplateRepository;
import in.canaris.cloud.repository.AddPhysicalServerRepository;
import in.canaris.cloud.repository.AdditionalStorageRepository;
import in.canaris.cloud.repository.AppUserRepository;
import in.canaris.cloud.repository.CloudInstanceCpuThresholdHistoryRepository;
import in.canaris.cloud.repository.CloudInstanceLogRepository;
import in.canaris.cloud.repository.CloudInstanceMemoryThresholdHistoryRepository;
import in.canaris.cloud.repository.CloudInstanceNodeHealthHistoryRepository;
import in.canaris.cloud.repository.CloudInstanceNodeHealthMonitoringRepository;
import in.canaris.cloud.repository.CloudInstanceRepository;
import in.canaris.cloud.repository.CloudInstanceUsageDailyRepository;
import in.canaris.cloud.repository.CloudInstanceUsageRepository;
import in.canaris.cloud.repository.CustomUserRoleRepository;
import in.canaris.cloud.repository.CustomerRepository;
import in.canaris.cloud.repository.DiscountRepository;
import in.canaris.cloud.repository.DiscoverDockerContainersRepository;
import in.canaris.cloud.repository.FirewallRepository;
import in.canaris.cloud.repository.GroupRepository;
import in.canaris.cloud.repository.KVMDriveDetailsRepository;
import in.canaris.cloud.repository.LocationRepository;
import in.canaris.cloud.repository.NodeUtilizationRepository;
import in.canaris.cloud.repository.PortDetailsRepository;
import in.canaris.cloud.repository.PriceRepository;
import in.canaris.cloud.repository.ProxmoxAssignedIpAddressRepository;
import in.canaris.cloud.repository.RequestApprovalRepository;
import in.canaris.cloud.repository.SubProductRepository;
import in.canaris.cloud.repository.SwitchRepository;
import in.canaris.cloud.repository.UserLabRepository;
import in.canaris.cloud.repository.UserRepository;
import in.canaris.cloud.repository.VMLocationPathRepository;
import in.canaris.cloud.repository.VPCRepository;
import in.canaris.cloud.repository.UserWiseChatBoatInstructionTemplateRepository;
import in.canaris.cloud.repository.ChartBoatInstructionTemplateRepository;
import in.canaris.cloud.repository.UserScenerioRepository;
import in.canaris.cloud.repository.AssessmentUserLabRepository;
import in.canaris.cloud.repository.AssessmentUserWiseChatBoatInstructionTemplateRepository;

import in.canaris.cloud.server.entity.HardwareInventory;
import in.canaris.cloud.server.entity.HardwareInventoryLinux;
import in.canaris.cloud.server.entity.NodeAvailability;
import in.canaris.cloud.server.entity.NodeHealthMonitoring;
import in.canaris.cloud.server.entity.NodeMonitoring;
import in.canaris.cloud.server.repository.DriveRepository;
import in.canaris.cloud.server.repository.HardwareInventoryLinuxRepository;
import in.canaris.cloud.server.repository.HardwareInventoryRepository;
import in.canaris.cloud.server.repository.InterfaceMonitoringRepository;
import in.canaris.cloud.server.repository.NodeAvailabilityRepository;
import in.canaris.cloud.server.repository.NodeMonitoringRepository;
import in.canaris.cloud.server.repository.NodeStatusHistoryRepository;
import in.canaris.cloud.server.repository.VMHealthRepository;
import in.canaris.cloud.service.DockerService;
import in.canaris.cloud.service.GuacamoleService;
import in.canaris.cloud.service.ProxmoxService;
import in.canaris.cloud.utils.CMPUtil;
import in.canaris.cloud.utils.CommandResult;
import in.canaris.cloud.utils.ExecutePSCommand;
import in.canaris.cloud.utils.ExecuteSSHCommand;
import in.canaris.cloud.utils.KVMResize;
import in.canaris.cloud.utils.KillExe;

import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

@Controller
@RequestMapping("/cloud_instance")
public class CloudInstanceController {

	private RestTemplate restTemplate = new RestTemplate();

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

	@Value("${api_host}")
	private String physical_server_agent_api_host;

	@Autowired
	private CloudInstanceUsageDailyRepository repositoryInstanceDailyUsage;

	@Autowired
	private CloudInstanceRepository repository;

	@Autowired
	private AppUserRepository appRepository;

	@Autowired
	private CloudInstanceLogRepository cloudInstanceLogRepository;

	@Autowired
	private VMHealthRepository healthRepository;

	@Autowired
	private AddPhysicalServerRepository addPhysicalServerRepository;

	@Autowired
	private HardwareInventoryRepository hardwareRepository;

	@Autowired
	private HardwareInventoryLinuxRepository hardwareLinuxRepository;

	@Autowired
	private InterfaceMonitoringRepository interfaceMonitoringRepository;

	@Autowired
	private NodeStatusHistoryRepository nodeStatusHistoryRepository;

	@Autowired
	private CloudInstanceNodeHealthHistoryRepository vmHealthRepository;

	@Autowired
	private NodeAvailabilityRepository nodeAvailabilityRepository;

	@Autowired
	private NodeMonitoringRepository nodeMonitoringRepository;

	@Autowired
	private LocationRepository repositoryLocation;

	@Autowired
	private VMLocationPathRepository repositoryVMLocationPath;

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
	private CloudInstanceUsageRepository repositoryInstanceUsage;

	@Autowired
	private CloudInstanceUsageDailyRepository usageRepository;

	@Autowired
	private AdditionalStorageRepository externalStorageRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private DriveRepository driveRepository;

	@Autowired
	private CustomUserRoleRepository ToGetUserRole;

	@Autowired
	private AddPhysicalServerRepository PhysicalServerRepository;

	@Autowired
	private NodeUtilizationRepository nodeUtilizationRepository;

	@Autowired
	private CloudInstanceMemoryThresholdHistoryRepository memoryThresholdHistoryRepository;

	@Autowired
	private CloudInstanceCpuThresholdHistoryRepository cpuThresholdHistoryRepository;

	@Autowired
	private CloudInstanceNodeHealthMonitoringRepository CloudInstanceNodeHealthMonitoringRep;

	@Autowired
	private KVMDriveDetailsRepository kvmDriveDetailsRepository;

	@Autowired
	private ChartBoatInstructionTemplateRepository ChartBoatInstructionTemplateRepository;

	@Autowired
	private UserWiseChatBoatInstructionTemplateRepository instructionTemplateRepository;

	@Autowired
	private UserLabRepository userLabRepository;

	@Autowired
	private PortDetailsRepository portDetailsRepository;

	@Autowired
	private DockerService dockerService;

	@Autowired
	private UserScenerioRepository UserScenerioRepository;

	@Autowired
	private ScenarioLabTemplateRepository scenarioLabTemplateRepository;

	@Autowired
	private ProxmoxService proxmoxService;

	@Autowired
	private ProxmoxAssignedIpAddressRepository proxmoxAssignedIpAddressRepository;

	@Autowired
	private GuacamoleService guacService;

	@Autowired
	private AssessmentUserLabRepository assessmentUserLabRepository;

	@Autowired
	private DiscoverDockerContainersRepository discoverDockerContainersRepository;

	@Autowired
	private AssessmentUserWiseChatBoatInstructionTemplateRepository assessmentInstructionTemplateRepository;

	final String var_function_name = "cloud_instance"; // small letter
	final String disp_function_name = "Cloud Instance"; // capital letter

	@GetMapping("/checkUsernameExist")
	public @ResponseBody String checkUsernameExist(@RequestParam String username) {
		String duplicate = "";
		try {

			List<AppUser> list = userRepository.findByuserName(username);
			if (!list.isEmpty()) {
				duplicate = "duplicate";
			}

		} catch (Exception e) {
			System.out.println("Exception occured while checking for duplicate username = " + e);
		}
		return duplicate;
	}

	@GetMapping("/checkMobileNumber")
	public @ResponseBody String checkMobileNumber(@RequestParam String mobileNo) {
		String duplicate = "";
		try {

			List<AppUser> list = userRepository.findBymobileNo(mobileNo);
			if (!list.isEmpty()) {
				duplicate = "duplicate";
			}

		} catch (Exception e) {
			System.out.println("Exception occured while checking for duplicate Mobile number = " + e);
		}
		return duplicate;
	}

	@GetMapping("/checkDuplicateEmail")
	public @ResponseBody String checkDuplicateEmail(@RequestParam String email) {
		String duplicate = "";
		try {

			List<AppUser> list = userRepository.findByemail(email);
			if (!list.isEmpty()) {
				duplicate = "duplicate";
			}

		} catch (Exception e) {
			System.out.println("Exception occured while checking for duplicate Email = " + e);
		}
		return duplicate;
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") String id_name, Model model, RedirectAttributes redirectAttributes) {

		// String responce_data = null;

		String id = id_name.substring(0, id_name.indexOf("~"));

		String name = id_name.substring(id_name.indexOf("~") + 1);
		System.out.println("Instance name:" + name);
		System.out.println("Insatnce id" + id);
		String responce_data = null;
		try {
			ExecutePSCommand execute = new ExecutePSCommand();
			responce_data = execute.deleteVM(name);
			System.out.println("responce_data:" + responce_data);
			/// if (responce_data.equals("VL Created Successfully")) {
			try {
				repository.deleteById(Integer.parseInt(id));
				redirectAttributes.addFlashAttribute("message",
						"The " + disp_function_name + " with id=" + id + " has been deleted successfully!");
			} catch (Exception e) {
				redirectAttributes.addFlashAttribute("message", e.getMessage());
			}
			// }
		} catch (Exception e) {
			System.out.println("Exceptin" + e);
			responce_data = "" + e;
		}

		return "redirect:/" + var_function_name + "/view";
	}

	// Physical Servers
	@GetMapping("/servers")
	public ModelAndView servers() {

		ModelAndView mav1 = new ModelAndView("physicalServers");
		try {
			mav1.addObject("servers", PhysicalServerRepository.findAll());
		} catch (Exception e) {
			System.out.println("exception occured while fetching physical servers");
		}
		return mav1;
	}

	@GetMapping("/view")
	public ModelAndView getAll(Principal principal) {
		ModelAndView mav = new ModelAndView(var_function_name + "_view");

		if (principal == null) {
			ModelAndView mav1 = new ModelAndView("welcomePage");
			return mav1;
		}
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();
		AppUser obj = appRepository.findOneByUserName(username);
		List<CloudInstance> instances = null;
		mav.addObject("action_name", var_function_name);
		mav.addObject("groupList", groupRepository.getAllGroups());
		mav.addObject("customerList", customerRepository.getCustomerName());
		try {

			boolean isSuperAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));
			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
			boolean isUser = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"));

			List<String> groupName = new ArrayList<>();
			StringTokenizer token = new StringTokenizer(obj.getGroupName(), ",");
			while (token.hasMoreTokens()) {
				groupName.add(token.nextToken());
			}

			if (isSuperAdmin) {

//				instances = repository.findByIsMonitoringOrderByIdDesc(true);
				instances = repository.findAllByOrderByIdDesc();

			} else if (isAdmin) {

				System.out.println("Inoperator groupName = " + groupName);

//				instances = repository.findByIsMonitoringAndGroupNameOrderByIdDesc(true, groupName);
				instances = repository.findByGroupNameOrderByIdDesc(groupName);

			} else {
				// List<Integer> li = approvalRepository.findByRequesterNameCustom(username);

				System.out.println("InUser groupName = " + groupName);

				// System.out.println(li.toString());
				// instances = repository.findByidInAndIsMonitoring(li, true);

//				instances = repository.findByIsMonitoringAndGroupNameOrderByIdDesc(true, groupName);
				instances = repository.findByGroupNameOrderByIdDesc(groupName);
			}

			for (CloudInstance data : instances) {

				data.setDiskAssigned(data.getDiskAssigned() == null ? "-"
						: data.getDiskAssigned().equals("") ? "-"
								: data.getDiskAssigned().length() == 0 ? "-"

										: String.format("%.2f",
												Double.parseDouble(data.getDiskAssigned()) / (1024 * 1024 * 1024)));

//				data.setMemoryAssigned(data.getMemoryAssigned() == null ? "-"
//						: data.getMemoryAssigned().equals("") ? "-"
//								: data.getMemoryAssigned().length() == 0 ? "-"
//
//										: String.format("%.2f",
//												Double.parseDouble(data.getMemoryAssigned()) / (1024 * 1024 * 1024)));
				data.setMemoryAssigned(data.getMemoryAssigned());
			}
			mav.addObject("listObj", instances);

		} catch (Exception e) {
			System.out.println("Exception occured while fetching VM Data = " + e);
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
		}
		return mav;
	}

//	@GetMapping("/new")
//	public ModelAndView add(RedirectAttributes redirectAttributes) {
//		System.out.println("  New COntroller called: ");
//		ModelAndView mav = new ModelAndView(var_function_name + "_add");
//		mav.addObject("pageTitle", "Add New " + disp_function_name);
//		mav.addObject("action_name", var_function_name);
//		mav.addObject("dcLocationList", repositoryLocation.getAllDClocations());
//		mav.addObject("vmlocationPathList", repositoryVMLocationPath.getAllVMlocationsPahts());
//		// mav.addObject("vpcList", repositoryVPC.getAllVPC());
//		mav.addObject("securityGroupList", firewallRepository.getFirewall());
//		mav.addObject("sharedCpuPlan", priceRepository.getSharedCpuPlan());
//		mav.addObject("dedicatedCpuPlan", priceRepository.getDedicatedCpuPlan());
//		mav.addObject("highMemoryPlan", priceRepository.getHighMemoryPlan());
//		mav.addObject("switchList", switchRepository.getAllSwitch());
//		mav.addObject("physicalServerIPList", PhysicalServerRepository.getPhysicalServerIPs());
//		CloudInstance objEnt = new CloudInstance();
//		mav.addObject("objEnt", objEnt);
//		// mav.addObject("result", "success");
//
//		// redirectAttributes.addFlashAttribute("result", "success");
//		return mav;
//	}

	@GetMapping("/new")
	public ModelAndView add(@RequestParam(required = false) Integer id, RedirectAttributes redirectAttributes,
			HttpSession session) {

		ModelAndView mav = new ModelAndView();

		String access = (String) session.getAttribute("access");

		if (access == null || access.equalsIgnoreCase("READ")) {
			mav.setViewName("redirect:/403");
			return mav;
		}

		mav.setViewName(var_function_name + "_add");

		mav.addObject("pageTitle", id == null ? "Add New " + disp_function_name : "Edit " + disp_function_name);
		mav.addObject("action_name", var_function_name);
		mav.addObject("dcLocationList", repositoryLocation.getAllDClocations());
		mav.addObject("vmlocationPathList", repositoryVMLocationPath.getAllVMlocationsPahts());
		mav.addObject("securityGroupList", firewallRepository.getFirewall());
		mav.addObject("sharedCpuPlan", priceRepository.getSharedCpuPlan());
		mav.addObject("dedicatedCpuPlan", priceRepository.getDedicatedCpuPlan());
		mav.addObject("highMemoryPlan", priceRepository.getHighMemoryPlan());
		mav.addObject("switchList", switchRepository.getAllSwitch());
		mav.addObject("physicalServerIPList", PhysicalServerRepository.getPhysicalServerIPs());

		CloudInstance objEnt;
		if (id != null) {
			// Edit mode - load existing data
			objEnt = repository.findById(id).orElse(new CloudInstance());

			// Load existing instructions
			int templateId = id; // Safe unboxing since id is not null
			List<ChartBoatInstructionTemplate> existingInstructions = ChartBoatInstructionTemplateRepository
					.findBytemplateId(templateId);
			mav.addObject("existingInstructions", existingInstructions);
		} else {
			// Create mode - new object
			objEnt = new CloudInstance();
		}

		mav.addObject("objEnt", objEnt);
		mav.addObject("isEdit", id != null);
		mav.addObject("templateId", id);

		return mav;
	}

	@PostMapping("/update")
	public String update(@ModelAttribute CloudInstanceForm form, @ModelAttribute CloudInstance obj,
			@RequestParam(required = false) MultipartFile uploadedImage,
			@RequestParam(required = false) List<Integer> deletedInstructions, RedirectAttributes redirectAttributes,
			Principal principal) {

		try {
			// Load existing instance
			CloudInstance existingInstance = repository.findById(obj.getId())
					.orElseThrow(() -> new RuntimeException("Instance not found"));

			// Update fields
			existingInstance.setInstance_name(obj.getInstance_name());
			existingInstance.setLab_tag(obj.getLab_tag());
			existingInstance.setConsoleUsername(obj.getConsoleUsername());
			existingInstance.setConsolePassword(obj.getConsolePassword());
			existingInstance.setConsoleProtocol(obj.getConsoleProtocol());
			existingInstance.setSecurityMode(obj.getSecurityMode());
			existingInstance.setServerCertificate(obj.getServerCertificate());
			existingInstance.setDescription(obj.getDescription());
			existingInstance.setVirtualization_type(obj.getVirtualization_type());
			existingInstance.setPhysicalServerIP(obj.getPhysicalServerIP());
			existingInstance.setSwitch_id(obj.getSwitch_id());
			existingInstance.setLocation_id(obj.getLocation_id());
			existingInstance.setSubproduct_id(obj.getSubproduct_id());
			existingInstance.setPrice_id(obj.getPrice_id());
			existingInstance.setSecurity_group_id(obj.getSecurity_group_id());

			// Handle image update
			if (uploadedImage != null && !uploadedImage.isEmpty()) {
				existingInstance.setLab_image(uploadedImage.getBytes());
			}

			// Handle docker network
			if (obj.getDocker_network_id() != null && obj.getDocker_network_id().contains("~")) {
				String[] parts = obj.getDocker_network_id().split("~", 2);
				existingInstance.setDocker_network_id(parts[0]);
				existingInstance.setDocker_network_name(parts[1]);
			}

			repository.save(existingInstance);

			// Handle instructions
			List<InstructionDto> instructions = form.getInstructions();

			// Delete removed instructions
			if (deletedInstructions != null) {
				for (Integer instructionId : deletedInstructions) {
					ChartBoatInstructionTemplateRepository.deleteById(instructionId);
				}
			}

			// Update or create instructions
			for (InstructionDto dto : instructions) {
				ChartBoatInstructionTemplate instruction;

				if (dto.getId() != null && dto.getId() > 0) {
					// Update existing instruction
					instruction = ChartBoatInstructionTemplateRepository.findById(dto.getId())
							.orElse(new ChartBoatInstructionTemplate());
				} else {
					// Create new instruction
					instruction = new ChartBoatInstructionTemplate();
				}

				instruction.setTemplateId(existingInstance.getId());
				instruction.setTemaplateName(existingInstance.getInstance_name());
				instruction.setInstructionCommand(dto.getCommandText());
				instruction.setInstructionDetails(
						dto.getInstructionText() != null ? dto.getInstructionText().getBytes() : "".getBytes());

				ChartBoatInstructionTemplateRepository.save(instruction);
			}

			redirectAttributes.addFlashAttribute("result", "success");
			redirectAttributes.addFlashAttribute("message", "Template updated successfully!");

		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("result", "Error: " + e.getMessage());
			redirectAttributes.addFlashAttribute("error", "Failed to update template: " + e.getMessage());
		}

		return "redirect:/" + var_function_name + "/view";
	}

	@GetMapping("/vm_database")
	public ModelAndView vm_database() {
		ModelAndView mav = new ModelAndView("cloud_instance_with_database_add");
		mav.addObject("pageTitle", "Add New " + disp_function_name);
		mav.addObject("action_name", var_function_name);
		mav.addObject("dcLocationList", repositoryLocation.getAllDClocations());
		mav.addObject("vpcList", repositoryVPC.getAllVPC());
		mav.addObject("securityGroupList", firewallRepository.getFirewall());
		mav.addObject("sharedCpuPlan", priceRepository.getSharedCpuPlan());
		mav.addObject("dedicatedCpuPlan", priceRepository.getDedicatedCpuPlan());
		mav.addObject("highMemoryPlan", priceRepository.getHighMemoryPlan());
		mav.addObject("switchList", switchRepository.getAllSwitch());
		CloudInstance objEnt = new CloudInstance();
		mav.addObject("objEnt", objEnt);
		return mav;
	}

	@GetMapping("/vm_application")
	public ModelAndView vm_application() {
		ModelAndView mav = new ModelAndView("cloud_instance_with_application_add");
		mav.addObject("pageTitle", "Add New " + disp_function_name);
		mav.addObject("action_name", var_function_name);
		mav.addObject("dcLocationList", repositoryLocation.getAllDClocations());
		mav.addObject("vpcList", repositoryVPC.getAllVPC());
		mav.addObject("securityGroupList", firewallRepository.getFirewall());
		mav.addObject("sharedCpuPlan", priceRepository.getSharedCpuPlan());
		mav.addObject("dedicatedCpuPlan", priceRepository.getDedicatedCpuPlan());
		mav.addObject("highMemoryPlan", priceRepository.getHighMemoryPlan());
		mav.addObject("switchList", switchRepository.getAllSwitch());
		CloudInstance objEnt = new CloudInstance();
		mav.addObject("objEnt", objEnt);
		return mav;
	}

	public static String executeRemoteCommand2(String command, String USER, String PASSWORD, String HOST) {
		Session session = null;
		ChannelExec channel = null;
		StringBuilder output = new StringBuilder();

		try {
			JSch jsch = new JSch();
			session = jsch.getSession(USER, HOST, 22);
			session.setPassword(PASSWORD);

			// Configuring SSH to ignore key checking
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

			session.connect();

			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);
			channel.setErrStream(System.err);

			InputStream in = channel.getInputStream();
			channel.connect();

			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
				// System.out.println("VMline:"+line);
				output.append(line).append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (channel != null)
				channel.disconnect();
			if (session != null)
				session.disconnect();
		}

		return output.toString().trim(); // Return the output as a string
	}

	public static String getkvmperticularVMDetails(String vmName, String USER, String PASSWORD, String HOST) {

		// drive insert

		// Get detailed XML info
		String dumpXmlCommand = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh dumpxml " + vmName;
		String xmlInfo = executeRemoteCommand2(dumpXmlCommand, USER, PASSWORD, HOST);
		System.out.println("xmlInfo:" + xmlInfo);
		String returneddata = parseKVMVMXMLInfo(vmName, xmlInfo);
		System.out.println("returneddata:" + returneddata);
		return returneddata;
	}

	public static CloudInstanceNodeHealthMonitoring getHealthDetails(String HOST, String USER, String PASSWORD,
			String vm_name) {

		CloudInstanceNodeHealthMonitoring health = new CloudInstanceNodeHealthMonitoring();
		String command = "export LIBVIRT_DEFAULT_URI=qemu:///system && vm=" + vm_name + "; "
				+ "if [ -n \"$vm\" ]; then "
				+ "total_mem=$(virsh dominfo \"$vm\" | awk '/Max memory/ {print $3 / 1024}'); "
				+ "used_mem=$(virsh dommemstat \"$vm\" | awk '/rss/ {print $2 / 1024}'); "
				+ "cpu_time=$(virsh domstats \"$vm\" | awk '/cpu_time/ {print $2}'); "
				+ "cpu_count=$(virsh vcpuinfo \"$vm\" | grep -c \"CPU\"); "
				+ "if [ -n \"$cpu_time\" ] && [ \"$cpu_count\" -gt 0 ]; then "
				+ "cpu_utilization=$(echo \"scale=2; $cpu_time / 1000000 / $cpu_count\" | bc); "
				+ "echo \"$vm,$total_mem MB,$used_mem MB,$cpu_utilization %\"; " + "else "
				+ "echo \"$vm,$total_mem MB,$used_mem MB,CPU data unavailable\"; " + "fi " + "fi ";

		Session session = null;
		ChannelExec channel = null;
		StringBuilder outputbu = new StringBuilder();

		try {
			JSch jsch = new JSch();
			session = jsch.getSession(USER, HOST, 22);
			session.setPassword(PASSWORD);

			// Configuring SSH to ignore key checking
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

			session.connect();

			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);
			channel.setErrStream(System.err);

			InputStream errStream = channel.getErrStream();
			InputStream in = channel.getInputStream();
			channel.connect();

			byte[] buffer = new byte[1024];
			int readCount;
			while ((readCount = in.read(buffer)) != -1) {
				System.out.print("CMD health op:" + new String(buffer, 0, readCount));

				outputbu.append(new String(buffer, 0, readCount));
			}
			byte[] errorBuffer = new byte[1024];
			while ((readCount = errStream.read(errorBuffer)) != -1) {
				String errorOutput = new String(errorBuffer, 0, readCount);
				System.out.println("health errorOutput:" + errorOutput + ":@");
			}

		} catch (Exception e) {
			System.out.print("Exception command:" + e.getMessage());
		} finally {
			if (channel != null) {
				channel.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}

		try {
			// Step 1: Execute the KVM SSH command

			String output = outputbu.toString();

			// Step 2: Parse the output
			String[] lines = output.split("\n");
			for (String line : lines) {
				if (line.isEmpty()) {
					continue;
				}
				String[] parts = line.split(",");
				String vm_namee = parts[0].trim();
				System.out.println("##############################Health Mon:" + vm_namee);

				double totalMem = Double.parseDouble(parts[1].replace("MB", "").trim());
				System.out.println("totalMem:" + vm_namee + ":" + totalMem);
				double usedMem = Double.parseDouble(parts[2].replace("MB", "").trim());
				System.out.println("usedMem:" + vm_namee + ":" + usedMem);
				// double cpuUtilization = 0;

				double freeMem = totalMem - usedMem;
				double memPercentage = 0;

				try {
					if (totalMem == 0 && usedMem == 0) {
						memPercentage = 0;
					} else {
						memPercentage = (usedMem / totalMem) * 100;
					}

				} catch (Exception e) {
					System.out.println("Exception memory:" + e);
				}
				System.out.println("Memory Utilization:" + memPercentage);

				health.setCpuUtilization(0);
				health.setTotalMemory(totalMem);
				health.setFreeMemory(freeMem);
				health.setUsedMemory(usedMem);
				health.setNodeName(vm_name);
				health.setVmName(vm_name);
				health.setNodeIp(HOST);
				int intValue2 = (int) Math.round(memPercentage);
				health.setMemoryUtilization(intValue2);

			}

		} catch (Exception e) {
			System.out.println("Exception Memory:" + e);
			// e.printStackTrace();
		}
		return health;
	}

	private static ArrayList<KVMDriveDetails> insertDriveDetails(String serverIP, String vmName, String USER,
			String PASSWORD) {

		// System.out.println("VM name disk:" + vmName);
		String returneddata = "";
		Session session = null;
		ChannelExec channelExec = null;
		ArrayList<KVMDriveDetails> kvmdrivelis = new ArrayList<>();
		try {

			JSch jsch = new JSch();
			session = jsch.getSession(USER, serverIP, 22);
			session.setPassword(PASSWORD);

			// Configuring SSH to ignore key checking
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

			session.connect();
//
//			channel = (ChannelExec) session.openChannel("exec");
//			channel.setCommand(command);
//			channel.setErrStream(System.err);

			// Get the list of block devices for the VM
			// export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh dumpxml
			String command = String.format("export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh domblklist %s",
					vmName);
			channelExec = (ChannelExec) session.openChannel("exec");
			channelExec.setCommand(command);
			channelExec.setErrStream(System.err);

			InputStream in = channelExec.getInputStream();
			channelExec.connect();

			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			// System.out.println("Disk Details:");
//            System.out.printf("%-10s %-10s %-10s %-10s %-10s %-10s\n", "Device", "Used", "Capacity", "Bus", "Access",
//                    "Source");

			while ((line = reader.readLine()) != null) {
				if (line.contains("/")) { // Skipping header and blank lines
					String[] parts = line.trim().split("\\s+");
					String device = parts[0];
					String source = parts[1];

					// Fetch additional disk info
					KVMDriveDetails kvmdrive = getDiskUsage(session, vmName, device, source, serverIP);
					kvmdrivelis.add(kvmdrive);
				}
			}

			channelExec.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			if (channelExec != null) {
				channelExec.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}
		return kvmdrivelis;
	}

	private static KVMDriveDetails getDiskUsage(Session session, String vmName, String device, String source,
			String serverIP) {
		String formattedString = "";
		KVMDriveDetails kvmdrive = new KVMDriveDetails();
		try {
			String command = String.format(
					"export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh domblkinfo %s %s", vmName, device);
			ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
			channelExec.setCommand(command);
			channelExec.setErrStream(System.err);

			InputStream in = channelExec.getInputStream();
			channelExec.connect();

			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			String used = "-", capacity = "-", bus = "-", access = "-";

			while ((line = reader.readLine()) != null) {
				// System.out.println("Strt drive details #################### " + line);
				if (line.contains("Capacity:")) {
					capacity = line.split(":")[1].trim();
				}
				if (line.contains("Allocation:")) {
					used = line.split(":")[1].trim();
				}
				if (line.contains("Physical:")) {
					bus = "virtio"; // Example bus type, change as needed
				}
				// Assume 'access' and other details as defaults or parse additional as needed
			}

			System.out.println(
					"Disk Details:" + device + ":" + used + ":" + capacity + ":" + bus + ":" + access + ":" + source);

			// Create new KVMDriveDetails entity

			kvmdrive = new KVMDriveDetails();
			kvmdrive.setDevice(device);
			kvmdrive.setUsed(used);
			kvmdrive.setCapacity(capacity);
			kvmdrive.setBus(bus);
			kvmdrive.setAccess(access);
			kvmdrive.setSource(source);
			kvmdrive.setPhysicalserverip(serverIP); // Set additional fields
			kvmdrive.setVmname(vmName); // Set additional fields

			// Save the new entity using the repository

			System.out.println("save successfulyy drive details");
			channelExec.disconnect();
		} catch (Exception e) {
			System.out.println(e);

		}

		return kvmdrive;
	}

	public static String parseKVMVMXMLInfo(String vmName, String xml) {
		if (xml == null || xml.isEmpty()) {
			return "No Data";
		}

		// Extracting the required details from the XML
		String hostname = "";
		String macAddress = "";
		String diskPath = "";
		String cdromIsoPath = "";
		String osType = "";
		String osVariant = "";
		String ipAddress = "";
		String cpu = "";
		String memory = "";
		String uuid = "";

		String[] lines = xml.split("\n");

		for (String line : lines) {
			if (line.contains("<name>")) {
				hostname = line.replaceAll("<[^>]+>", "").trim();
			}
			if (line.contains("<uuid>")) {
				uuid = line.replaceAll("<[^>]+>", "").trim();
			}
			if (line.contains("<memory>")) {
				memory = line.replaceAll("<[^>]+>", "").trim();
			}
			if (line.contains("<vcpu")) {
				cpu = line.replaceAll("<[^>]+>", "").trim();
			}
			if (line.contains("<disk type='file'")) {
				int startIndex = line.indexOf("source file='") + 13;
				int endIndex = line.indexOf("'", startIndex);
				diskPath = line.substring(startIndex, endIndex);
			}
			if (line.contains("<disk type='cdrom'")) {
				int startIndex = line.indexOf("source file='") + 13;
				int endIndex = line.indexOf("'", startIndex);
				cdromIsoPath = line.substring(startIndex, endIndex);
			}
			if (line.contains("<interface")) {
				int index = line.indexOf("mac address='");
				if (index != -1) {
					macAddress = line.substring(index + 14, line.indexOf("'", index + 14));
				}
			}
			if (line.contains("<os>")) {
				for (String osLine : lines) {
					if (osLine.contains("<type>")) {
						osType = osLine.replaceAll("<[^>]+>", "").trim();
					}
					if (osLine.contains("<variant>")) {
						osVariant = osLine.replaceAll("<[^>]+>", "").trim();
					}
				}
			}
		}

		System.out.println("VM Details:");
		System.out.println("  Name: " + vmName);
		System.out.println("  Hostname: " + hostname);
		System.out.println("  CPU: " + cpu);
		System.out.println("  Memory: " + memory + " MB"); // Convert from KiB to MB
		System.out.println("  Disk Path: " + diskPath);
		System.out.println("  CD-ROM ISO Path: " + cdromIsoPath);
		System.out.println("  MAC Address: " + macAddress);
		System.out.println("  OS Type: " + osType);
		System.out.println("  OS Variant: " + osVariant);
		System.out.println("  IP Address: " + ipAddress);
		System.out.println();

		return "VM Details:\n" + "  Name: " + vmName + "\n" + "  Hostname: " + hostname + "\n" + "  CPU: " + cpu + "\n"
				+ "  Memory: " + memory + " MB\n" + "  Disk Path: " + diskPath + "\n" + "  CD-ROM ISO Path: "
				+ cdromIsoPath + "\n" + "  MAC Address: " + macAddress + "\n" + "  OS Type: " + osType + "\n"
				+ "  OS Variant: " + osVariant + "\n" + "  IP Address: " + ipAddress + "\n" + "  uuid: " + uuid + "\n";

	}

//	
//	public String kvmVMCreate(String serverIP,Principal principal)
//	{
//
//
//		String responce_data = null;
//		boolean isApproved = false;
//		User loginedUser = (User) ((Authentication) principal).getPrincipal();
//		String username = loginedUser.getUsername();
//		AppUser user = userRepository.findByUsername(username);
//		//RequestApproval approvalObj = approvalRepository.findById(Long.valueOf(request_id)).get();
//		//String requestBy = approvalObj.getRequesterName();
//		//String requesterGroup = userRepository.findByUsername(requestBy).getGroupName();
//		//String fGroup[] = requesterGroup.split(",");
//		//System.out.println("(fGroup[0]  :: " + fGroup[0]);
//		//int instance_id = (int) approvalObj.getRequestId();
//		//System.out.println("Instance id =" + instance_id);
//		CloudInstance obj = repository.findById(instance_id).get();
//		String sshusername = addPhysicalServerRepository.findByserverIP(serverIP.trim()).getSsh_host();
//		String sshPassword = addPhysicalServerRepository.findByserverIP(serverIP.trim()).getSsh_password();
//		try {
//			//System.out.println("Instance id = " + request_id);
//			String response = "";
//			String requestType = approvalObj.getRequest_type();
//			System.out.println("requet_type = " + requestType);
//			if (requestType.equalsIgnoreCase("instance create")) {
//				String vm_name = obj.getInstance_name();
//				String vm_location_path = obj.getVm_location_path();
//				String vhd_path = obj.getVm_location_path() + "\\" + vm_name + "\\" + vm_name + ".vhdx";
//				String iso_file_path = subProductRepository.getISOFilePath(obj.getSubproduct_id().getId());
//				String vcpus = obj.getPrice_id().getvCpu();
//				String rams = obj.getPrice_id().getRam();
//				String disksize = obj.getPrice_id().getSsd_disk();
//
////				vcpus = parseNumberToString(vcpus);
////				rams = parseNumberToString(rams);
////				disksize = parseNumberToString(disksize);
////
////				int ramInGb = Integer.parseInt(rams.split(" ")[0]);
////				int ramInMb = ramInGb * 1024;
////				int numVcpus = Integer.parseInt(vcpus.split(" ")[0]);
//
//				// Changed to match Hyper-V
//				int numVcpus = Integer.parseInt(vcpus);
//
//				// RAM
//				int ramInMb = 0;
//				int diskInGb = 0;
//
//				if (rams.contains("GB")) {
//					rams = rams.replace("GB", "");
//					ramInMb = Integer.parseInt(rams);
//					ramInMb = ramInMb * 1024;
//
//				} else if (rams.contains("MB")) {
//					rams = rams.replace("MB", "");
//					ramInMb = Integer.parseInt(rams);
//				} else {
//					ramInMb = Integer.parseInt(rams);
//				}
//
//				// DISK
//				if (disksize.contains("GB")) {
//					disksize = disksize.replace("GB", "");
//					diskInGb = Integer.parseInt(disksize);
//
//				} else if (disksize.contains("MB")) {
//					disksize = disksize.replace("MB", "");
//					diskInGb = Integer.parseInt(disksize);
//					diskInGb = diskInGb / 1024;
//
//				} else {
//					diskInGb = Integer.parseInt(disksize);
//				}
//				// Required Parameter in MB
//				System.out.println("vcpus: " + vcpus);
//				System.out.println("rams: " + rams);
//				System.out.println("RAM in MB: " + ramInMb);
//
//				System.out.println("disks: " + disksize);
//				System.out.println("DISK in GB: " + diskInGb);
//
//				System.out.println("VM Name: " + vm_name);
//				System.out.println("ISO File Path: " + iso_file_path);
//
//				System.out.println("Number of vCPUs: " + numVcpus);
//
//				System.out.println("sshusername: " + sshusername);
//				System.out.println("sshPassword: " + sshPassword);
//
//				System.out.println("isVMCreated:" + isApproved);
//
//				String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
//				String createVMCommand = "";
//
////				createVMCommand = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virt-install --name " + vm_name
////						+ " --ram " + ramInMb + " --disk path=/var/lib/libvirt/images/" + vm_name + timestamp
////						+ ".qcow2,size=" + disksize + " --vcpus " + numVcpus
////						+ " --os-type linux --os-variant ubuntu20.04 --network network=default --graphics none --console pty,target_type=serial --cdrom "
////						+ iso_file_path + "";
//				if (serverIP.equalsIgnoreCase("172.16.5.44")) {
//
////					createVMCommand = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virt-install --name="
////							+ vm_name + " --os-variant=ubuntu20.04 --vcpu=" + numVcpus + " --ram=" + ramInMb
////							+ " --disk size=" + disksize + " --cdrom " + iso_file_path
////							+ " --network bridge=virbr0 --graphics vnc,listen=0.0.0.0";
//
//					createVMCommand = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virt-install --name "
//							+ vm_name + " --ram " + ramInMb + " --disk path=/var/lib/libvirt/images/" + vm_name
//							+ timestamp + ".qcow2,size=" + diskInGb + " --vcpus " + numVcpus
//							+ " --os-type linux --os-variant ubuntu20.04 --network network=default --graphics none --console pty,target_type=serial --cdrom "
//							+ iso_file_path + "";
//
//				} else {
//					createVMCommand = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virt-install --name "
//							+ vm_name + " --ram " + ramInMb + " --disk path=/var/lib/libvirt/images/" + vm_name
//							+ timestamp + ".qcow2,size=" + diskInGb + " --vcpus " + numVcpus
//							+ " --os-type linux --os-variant ubuntu20.04 --network network=default --graphics none --console pty,target_type=serial --cdrom ~"
//							+ iso_file_path + "";
////					
//////					createVMCommand = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virt-install --name="
//////							+ vm_name + " --os-variant=ubuntu20.04 --vcpu=" + numVcpus + " --ram=" + ramInMb
//////							+ " --disk size=" + disksize + " --cdrom ~" + iso_file_path
//////							+ " --network bridge=virbr0 --graphics vnc,listen=0.0.0.0";
////
//////					createVMCommand = "virt-install --name=" + vm_name + " --os-variant=ubuntu20.04 --vcpu=" + numVcpus
//////							+ " --ram=" + ramInMb + " --disk size=" + disksize
//////							+ " --cdrom /root/tcciso/ubuntu-22.04.5-live-server-amd64.iso --network bridge=virbr0 --graphics vnc,listen=0.0.0.0";
////
//////					 createVMCommand = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virt-install --name "
//////								+ vm_name + " --ram " + ramInMb + " --disk path=/var/lib/libvirt/images/" + vm_name + timestamp
//////								+ ".qcow2,size=" + disksize + " --vcpus " + numVcpus
//////								+ " --os-type linux --os-variant ubuntu20.04 --network network=default --graphics none --console pty,target_type=serial --cdrom "
//////								+ iso_file_path + "";
//				}
//				System.out.println(serverIP + "Command print APPROVED VM Create : " + createVMCommand);
//
//				executeRemoteCommand(createVMCommand, sshusername, sshPassword, serverIP);
////				getKVMVmdetails(sshusername, sshPassword, serverIP);
//				String myretureddata = getkvmperticularVMDetails(vm_name, sshusername, sshPassword, serverIP);
//
//				if (myretureddata.equalsIgnoreCase("No Data")) {
//
//				} else {
//					String[] lines = myretureddata.split("\n");
//					String KVMVmname = "";
//					String KVMHostname = "";
//					String KVMcpu = "";
//					String KVMmemory = "";
//					String KVMdiskPath = "";
//					String KVMcdromIsoPath = "";
//					String KVMmacAddress = "";
//					String KVMosType = "";
//					String KVMosVariant = "";
//					String KVMipAddress = "";
//
//					for (String line : lines) {
//						if (line.startsWith("  Name: ")) {
//							KVMVmname = line.replace("  Name: ", "").trim();
//						} else if (line.startsWith("  Hostname: ")) {
//							KVMHostname = line.replace("  Hostname: ", "").trim();
//						} else if (line.startsWith("  CPU: ")) {
//							KVMcpu = line.replace("  CPU: ", "").trim();
//						} else if (line.startsWith("  Memory: ")) {
//							KVMmemory = line.replace("  Memory: ", "").replace(" MB", "").trim();
//						} else if (line.startsWith("  Disk Path: ")) {
//							KVMdiskPath = line.replace("  Disk Path: ", "").trim();
//						} else if (line.startsWith("  CD-ROM ISO Path: ")) {
//							KVMcdromIsoPath = line.replace("  CD-ROM ISO Path: ", "").trim();
//						} else if (line.startsWith("  MAC Address: ")) {
//							KVMmacAddress = line.replace("  MAC Address: ", "").trim();
//						} else if (line.startsWith("  OS Type: ")) {
//							KVMosType = line.replace("  OS Type: ", "").trim();
//						} else if (line.startsWith("  OS Variant: ")) {
//							KVMosVariant = line.replace("  OS Variant: ", "").trim();
//						} else if (line.startsWith("  IP Address: ")) {
//							KVMipAddress = line.replace("  IP Address: ", "").trim();
//						}
//					}
//
//					obj.setInstance_name(KVMVmname);
//					obj.setComputer_name(KVMHostname);
//					obj.setCpuAssigned(KVMcpu);
//					// obj.setMemoryAssigned(KVMmemory); // Assuming memory is in MB; adjust if
//					// needed
//					obj.setDisk_path(KVMdiskPath);
//					obj.setIso_file_path(KVMcdromIsoPath);
//					obj.setMac_address(KVMmacAddress);
//					obj.setInstance_ip(KVMipAddress);
//					obj.setVirtualization_type(KVMosType); // Set OS type if relevant to virtualization
//					obj.setVirtualization_type("KVM"); // Set OS type if relevant to virtualization
//					obj.setGeneration_type(KVMosVariant); // Use `generation_type` to store OS variant if applicable
//					obj.setPhysicalServerIP(serverIP);
//
//					// Save the updated object
//					repository.save(obj);
//					isApproved = true;
//
//				}
//
//				try {
//					if (isApproved) {
//						VPC vpc_id = new VPC();
//						Customer customer = new Customer();
//						customer.setId(1);
//						vpc_id.setId(1);
//						obj.setVpc_id(vpc_id);
//						obj.setRequest_status("Approved");
////						obj.setGroupName("GroupA");
////						obj.setCustomer_id(customer);
//
//						obj.setMonitoring(true);
//						obj.setGroupName(fGroup[0]);
//						repository.save(obj);
//
//						approvalObj.setApproved_on(new Timestamp(System.currentTimeMillis()));
//						approvalObj.setRequest_status("Approved");
//						approvalObj.setApprover_name("Cloud Admin");
//						approvalRepository.save(approvalObj);
//						responce_data = "success";
//					}
//				} catch (Exception e) {
//					System.out.println("Exception save:" + e);
//				}
//
//			}
//
//		} catch (Exception e) {
//			System.out.println("Exception occured while approving request for VM creation = " + e);
//			// responce_data = "fail";
//		}
//		System.out.println("Is vm created = " + isApproved);
//		//return String.valueOf(isApproved);
//	
//		
//		return "success";
//	}
//

//	@PostMapping("/save")
//	public String save(CloudInstance obj, RedirectAttributes redirectAttributes, Principal principal) {
//		System.out.println("SuperAdmin CreateVM 16Jan25");
//
//		String virtType = obj.getVirtualization_type();
//		System.out.println("SuperAdmin CreateVM virtType:" + virtType);
//
//		if (virtType.trim().equalsIgnoreCase("kvm")) {
//			boolean cmd_status = false;
//			;
//			StringBuilder cmd_msg = new StringBuilder();
//			cmd_msg.append(".");
//
//			String serverIP = obj.getPhysicalServerIP();
//			// System.out.println("SuperAdmin-16Jan2025 CreateVM KVM serverIP:" + serverIP);
//			String sshusername = addPhysicalServerRepository.findByserverIP(serverIP.trim()).getSsh_host();
//			String sshPassword = addPhysicalServerRepository.findByserverIP(serverIP.trim()).getSsh_password();
//			try {
//
//				String response = "";
//				String vm_name = obj.getInstance_name();
//				String vm_location_path = obj.getVm_location_path();
//				System.out.println("KVM vm_location_path:"+vm_location_path);
//				String vhd_path = obj.getVm_location_path() + "\\" + vm_name + "\\" + vm_name + ".vhdx";
//				String iso_file_path = subProductRepository.getISOFilePath(obj.getSubproduct_id().getId());
//				String vcpus = obj.getPrice_id().getvCpu();
//				String rams = obj.getPrice_id().getRam();
//				String disksize = obj.getPrice_id().getSsd_disk();
//				int numVcpus = Integer.parseInt(vcpus);
//
//				// RAM
//				int ramInMb = 0;
//				int diskInGb = 0;
//
//				if (rams.contains("GB")) {
//					rams = rams.replace("GB", "");
//					ramInMb = Integer.parseInt(rams);
//					ramInMb = ramInMb * 1024;
//
//				} else if (rams.contains("MB")) {
//					rams = rams.replace("MB", "");
//					ramInMb = Integer.parseInt(rams);
//				} else {
//					ramInMb = Integer.parseInt(rams);
//				}
//
//				// DISK
//				if (disksize.contains("GB")) {
//					disksize = disksize.replace("GB", "");
//					diskInGb = Integer.parseInt(disksize);
//
//				} else if (disksize.contains("MB")) {
//					disksize = disksize.replace("MB", "");
//					diskInGb = Integer.parseInt(disksize);
//					diskInGb = diskInGb / 1024;
//
//				} else {
//					diskInGb = Integer.parseInt(disksize);
//				}
//				// Required Parameter in MB
//				System.out.println("vcpus: " + vcpus);
//				System.out.println("rams: " + rams);
//				System.out.println("RAM in MB: " + ramInMb);
//
//				System.out.println("disks: " + disksize);
//				System.out.println("DISK in GB: " + diskInGb);
//
//				System.out.println("VM Name: " + vm_name);
//				System.out.println("ISO File Path: " + iso_file_path);
//
//				System.out.println("Number of vCPUs: " + numVcpus);
//
//				System.out.println("sshusername: " + sshusername);
//				System.out.println("sshPassword: " + sshPassword);
//
//				// System.out.println("isVMCreated:" + isVMCreated);
//
//				String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
//				String createVMCommand = "";
//
//				String variant_name = subProductRepository.getVARIANT(obj.getSubproduct_id().getId());
//				System.out.println("@variant_name: " + variant_name);
////				createVMCommand = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virt-install --name " + vm_name
////						+ " --ram " + ramInMb + " --disk path=/var/lib/libvirt/images/" + vm_name + timestamp
////						+ ".qcow2,size=" + diskInGb + " --vcpus " + numVcpus + " --os-type linux --os-variant="
////						+ variant_name
////						+ " --network bridge=virbr0 --graphics vnc,listen=0.0.0.0 --console pty,target_type=serial --cdrom "
////						+ iso_file_path + " --wait 1";
//				
//				
//				
//				createVMCommand = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virt-install --name " + vm_name
//						+ " --ram " + ramInMb + " --disk path="+vm_location_path+"" + vm_name + timestamp
//						+ ".qcow2,size=" + diskInGb + " --vcpus " + numVcpus + " --os-type linux --os-variant="
//						+ variant_name
//						+ " --network bridge=virbr0 --graphics vnc,listen=0.0.0.0 --console pty,target_type=serial --cdrom "
//						+ iso_file_path + " --wait 1";
//
//				System.out.println(serverIP + "Command print SuperAdmin VM Create : " + createVMCommand);
//
//				ExecuteSSHCommand ssh = new ExecuteSSHCommand();
//				CommandResult cmdResult = ssh.executeCommand(createVMCommand, sshusername, sshPassword, serverIP);
//				cmd_status = cmdResult.isStatus();
//				cmd_msg = cmdResult.getMessage();
//				System.out.println("VM Create SuperAdmin CMD OP:" + cmd_status + ":" + cmd_msg);
//				Thread.sleep(7000);
//				if (cmd_status == false) {
//					System.out.println("VM Create SuperAdmin Failed:" + cmd_msg + ":" + createVMCommand);
//				} else {
//
//					try {
//						String startvmcommand = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh start "
//								+ vm_name + "";
//						executeRemoteCommand(startvmcommand, sshusername, sshPassword, serverIP);
//						System.out.println("start VM:" + startvmcommand);
//					} catch (Exception e) {
//						System.out.println("Exception start VM:" + e);
//					}
//
//					// drive details
//					System.out.println("Start Drive:" + serverIP + ":" + vm_name);
//					try {
//						ArrayList<KVMDriveDetails> kvmdrivelis = insertDriveDetails(serverIP, vm_name, sshusername,
//								sshPassword);
//						Iterator<KVMDriveDetails> itr = kvmdrivelis.iterator();
//						while (itr.hasNext()) {
//							KVMDriveDetails kvmdrive = itr.next();
//							kvmDriveDetailsRepository.deleteByVmnameAndPhysicalserverip(vm_name, serverIP);
//							kvmDriveDetailsRepository.save(kvmdrive);
//							System.out.println("Insert Drive Success:" + serverIP + ":" + vm_name);
//						}
//					} catch (Exception e) {
//						System.out.println("Exception drive: " + e);
//					}
//
//					// Memory Details
//					try {
//						CloudInstanceNodeHealthMonitoring healthdata = getHealthDetails(serverIP, sshusername,
//								sshPassword, vm_name);
//						CloudInstanceNodeHealthMonitoringRep.save(healthdata);
//
//					} catch (Exception e) {
//						System.out.println("Exception drive: " + e);
//					}
//
//					String myretureddata = getkvmperticularVMDetails(vm_name, sshusername, sshPassword, serverIP);
//					if (myretureddata.equalsIgnoreCase("No Data")) {
//						cmd_status = false;
//						cmd_msg.append("The VM was not created properly - Something went wrong.- ");
//
//					} else {
//						String[] lines = myretureddata.split("\n");
//						String KVMVmname = "";
//						String KVMHostname = "";
//						String KVMcpu = "";
//						String KVMmemory = "";
//						String KVMdiskPath = "";
//						String KVMcdromIsoPath = "";
//						String KVMmacAddress = "";
//						String KVMosType = "";
//						String KVMosVariant = "";
//						String KVMipAddress = "";
//						String uuid = "";
//
//						for (String line : lines) {
//							if (line.startsWith("  Name: ")) {
//								KVMVmname = line.replace("  Name: ", "").trim();
//							} else if (line.startsWith("  Hostname: ")) {
//								KVMHostname = line.replace("  Hostname: ", "").trim();
//							} else if (line.startsWith("  CPU: ")) {
//								KVMcpu = line.replace("  CPU: ", "").trim();
//							} else if (line.startsWith("  Memory: ")) {
//								KVMmemory = line.replace("  Memory: ", "").replace(" MB", "").trim();
//							} else if (line.startsWith("  Disk Path: ")) {
//								KVMdiskPath = line.replace("  Disk Path: ", "").trim();
//							} else if (line.startsWith("  CD-ROM ISO Path: ")) {
//								KVMcdromIsoPath = line.replace("  CD-ROM ISO Path: ", "").trim();
//							} else if (line.startsWith("  MAC Address: ")) {
//								KVMmacAddress = line.replace("  MAC Address: ", "").trim();
//							} else if (line.startsWith("  OS Type: ")) {
//								KVMosType = line.replace("  OS Type: ", "").trim();
//							} else if (line.startsWith("  OS Variant: ")) {
//								KVMosVariant = line.replace("  OS Variant: ", "").trim();
//							} else if (line.startsWith("  IP Address: ")) {
//								KVMipAddress = line.replace("  IP Address: ", "").trim();
//							} else if (line.startsWith("  uuid: ")) {
//								uuid = line.replace("  uuid: ", "").trim();
//							}
//						}
//						obj.setInstance_name(KVMVmname);
//						obj.setComputer_name(KVMHostname);
//						obj.setCpuAssigned(KVMcpu);
//						obj.setDisk_path(KVMdiskPath);
//						obj.setIso_file_path(KVMcdromIsoPath);
//						obj.setMac_address(KVMmacAddress);
//						obj.setInstance_ip(KVMipAddress);
//						obj.setVirtualization_type(KVMosType); // Set OS type if relevant to virtualization
//						obj.setVirtualization_type("KVM"); // Set OS type if relevant to virtualization
//						obj.setGeneration_type(KVMosVariant); // Use `generation_type` to store OS variant if applicable
//						obj.setPhysicalServerIP(serverIP);
//						obj.setVm_state("running");
//						obj.setVm_status("running");
//						obj.setUuid(uuid);
//						obj.setVm_id(uuid);
//						obj.setKvmvmid(uuid);
//						obj.setMac_address("-");
//						obj.setKvmostype(variant_name);
//						obj.setMemoryAssigned(rams);
//						obj.setMemory_assignedkvm(rams);
//
//						repository.save(obj);
//
//						cmd_status = true;
//						cmd_msg.append("VM Create Successfully.");
//
//						VPC vpc_id = new VPC();
//						Customer customer = new Customer();
//						customer.setId(1);
//						vpc_id.setId(1);
//						obj.setVpc_id(vpc_id);
//						obj.setRequest_status("Approved");
//						obj.setMonitoring(true);
//						obj.setGroupName("KVM_Group");
//						repository.save(obj);
//
//					}
//
//				}
//
//			} catch (Exception e) {
//				System.out.println("Exception occured while approving request for VM creation = " + e);
//				cmd_status = false;
//				cmd_msg.append("Error: " + e.getMessage());
//			}
//
//			System.out.println("Final VM Status&Message:" + cmd_status + ":" + cmd_msg.toString());
//
//			if (cmd_status) {
//
//				// restart service
//				System.out.println("Kill KVM exe");
//
//				try {
//					KillExe exe = new KillExe();
//					exe.killKVMExe();
//				} catch (Exception e) {
//					System.out.println("Exception:" + e);
//				}
//
//				redirectAttributes.addFlashAttribute("result", "success");
//			} else {
//				redirectAttributes.addFlashAttribute("result", cmd_msg.toString());
//			}
//
//			// end
//
//		} else {
//
//			System.out.println("hyper-v Superadmin VM create:");
//
//			String responce_data = null;
//			boolean isVMCreated = false;
//			User loginedUser = (User) ((Authentication) principal).getPrincipal();
//			String username = loginedUser.getUsername();
//			AppUser user = userRepository.findByUsername(username);
//
//			// kvmVMCreate( serverIP, principal);
//
//			// System.out.println("vm location path = "+obj.getVm_location_path());
//			try {
//				String vm_name = obj.getInstance_name();
//				String vm_location_path = obj.getVm_location_path();
//				
//				String vhd_path = obj.getVm_location_path() + "\\" + vm_name + "\\" + vm_name + ".vhdx";
//				String iso_file_path = subProductRepository.getISOFilePath(obj.getSubproduct_id().getId());
//				String switchName = obj.getSwitch_id().getSwitch_name();
//				String serverIP = obj.getPhysicalServerIP();
//				System.out.println("Hyperv  vm_location_path:"+vm_location_path);
//				System.out.println("Hyperv  vhd_path:"+vhd_path);
//				System.out.println("iso file path = " + iso_file_path);
//				String response = "";
////					ExecutePSCommand execute = new ExecutePSCommand();
////					String response = execute.createVM(vm_name, obj.getPrice_id().getRam(), obj.getGeneration_type(),
////							vm_location_path, obj.getSwitch_id().getSwitch_name(), vhd_path, obj.getPrice_id().getSsd_disk(),
////							obj.getPrice_id().getvCpu(), iso_file_path);
////					System.out.println("responce_data:" + response);
//
//				Socket socket = null;
//				try {
//
//					socket = new Socket(serverIP, 9005);
//					System.out.println("Physical Server IP = " + serverIP);
//					VMCreationBean bean = new VMCreationBean();
//					bean.setActivity("vm_create");
//					bean.setInstanceName(obj.getInstance_name());
//					bean.setMemoryStartupBytes(obj.getPrice_id().getRam());
//					bean.setVhdPath(vhd_path);
//					bean.setVmLocationPath(obj.getVm_location_path());
//					bean.setGeneration(obj.getGeneration_type());
//					bean.setSwitchName(switchName);
//					bean.setNewVHDSizeBytes(obj.getPrice_id().getSsd_disk());
//					bean.setvCpu(obj.getPrice_id().getvCpu());
//					bean.setIsoFilePath(iso_file_path);
//					ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
//					outputStream.writeObject(bean);
//					outputStream.flush();
//					// outputStream.close();
//					ObjectInputStream serverResponse = new ObjectInputStream(socket.getInputStream());
//					response = (String) serverResponse.readObject();
//					System.out.println("Hyper-V SUperadmin VM creation Server response: " + response);
//					isVMCreated = true;
//
//				} catch (Exception e) {
//					isVMCreated = false;
//					System.out.println("Exception occured while sending VM object to server : "
//							+ obj.getLocation_id().getPhysical_server_iP() + " = " + e);
//				} finally {
//					try {
//						if (socket != null) {
//							socket.close();
//						}
//
//					} catch (Exception e) {
//					}
//
//				}
//
//				// Create below columns in clound_insatnce_table
//				if (isVMCreated) {
//					String switch_name = "-";
//					String VlanSetting = "-";
//					String MacAddress = "-";
//					String IPAddresses = "-";
//					String ComputerName = "-";
//					String VMId = "-";
//					String state = "off";
//					String Status = "-";
//					int SizeOfSystemFiles = 0;
//
//					try {
//
//						JSONObject object = new JSONObject(response);
//						ComputerName = object.getString("ComputerName");
//						VMId = object.getString("VMId");
//						// state = object.G("state");
//						Status = object.getString("Status");
//						SizeOfSystemFiles = object.getInt("SizeOfSystemFiles");
//						JSONArray array = object.getJSONArray("NetworkAdapters");
//
//						System.out.println("Start Memory");
//						long MemoryStartup = 0;
//						double MemoryStartupD = 0;
//						// int VirtualProcessors = 0;
//						try {
//							MemoryStartup = object.getLong("MemoryStartup");
//							MemoryStartupD = MemoryStartup / 1024 / 1024 / 1024;
//						} catch (Exception e) {
//							System.out.println("Exception Memory startup:" + e);
//						}
//						System.out.println("MemoryStartup:" + ":" + obj.getInstance_name() + ":" + MemoryStartup + ":"
//								+ MemoryStartupD);
//
////						try {
////							VirtualProcessors = object.getInt("VirtualProcessors");
////						} catch (Exception e) {
////							VirtualProcessors = 0;
////						}
//						System.out.println("CPU count:" + obj.getPrice_id().getvCpu());
//
//						for (int i = 0; i < array.length(); i++) {
//							JSONObject object2 = array.getJSONObject(i);
//							switch_name = object2.getString("SwitchName");
//							VlanSetting = object2.getString("VlanSetting");
//							MacAddress = object2.getString("MacAddress");
//							IPAddresses = object2.getString("IPAddresses");
//						}
//						System.out.println("switch_name:" + switch_name);
//						System.out.println("VlanSetting:" + VlanSetting);
//						System.out.println("MacAddress:" + MacAddress);
//						System.out.println("IPAddresses:" + IPAddresses);
//						System.out.println("ComputerName:" + ComputerName);
//						System.out.println("VMId:" + VMId);
//						System.out.println("state:" + state);
//						System.out.println("Status:" + Status);
//						System.out.println("SizeOfSystemFiles:" + SizeOfSystemFiles);
//
//						obj.setVlan_setting(VlanSetting);
//						obj.setMac_address(MacAddress);
//						obj.setComputer_name(ComputerName);
//						obj.setVm_id(VMId);
//						obj.setVm_state(state);
//						obj.setVm_status(Status);
//						obj.setSize_of_system_files(SizeOfSystemFiles);
//						obj.setDisk_path(vhd_path);
//						obj.setIso_file_path(iso_file_path);
//						obj.setCpuAssigned(obj.getPrice_id().getvCpu());
//						obj.setMemoryAssigned(String.valueOf(MemoryStartupD));
//						isVMCreated = true;
//
//					} catch (Exception e) {
//						System.out.println("Exception:" + e);
//						isVMCreated = false;
//					}
//				}
//
//				System.out.println("isVMCreated:" + isVMCreated);
//
//				try {
//					if (isVMCreated) {
//						VPC vpc_id = new VPC();
//						Customer customer = new Customer();
//						customer.setId(1);
//						vpc_id.setId(1);
//						obj.setVpc_id(vpc_id);
//						obj.setRequest_status("Approved");
//						obj.setGroupName("Hyper-V_Group");
////							obj.setCustomer_id(customer);
//
//						obj.setMonitoring(true);
//						obj.setGroupName(user.getGroupName());
//						repository.save(obj);
//					}
//				} catch (Exception e) {
//					System.out.println("Exception save:" + e);
//				}
//				//// }
//			} catch (Exception e) {
//				System.out.println("Exceptin" + e);
//				responce_data = "" + e;
//			}
//			if (isVMCreated) {
//				redirectAttributes.addFlashAttribute("result", "success");
//			} else {
//				redirectAttributes.addFlashAttribute("result", "fail");
//			}
//
//		}
//
//		return "redirect:/" + var_function_name + "/new";
//	}

//	@PostMapping("/save")
//	public String save(CloudInstance obj, RedirectAttributes redirectAttributes, Principal principal) {
//		System.out.println("SuperAdmin CreateVM 23Aug25");
//
//		try {
//
//			CloudInstance list = new CloudInstance();
//
//			String LabName = obj.getInstance_name();
//			String LabId = obj.getLab_id();
////			byte[] cover_image = obj.getLab_image();
//			
//			
//			MultipartFile base64Image = list.getLab_image();
//		       
//		   
//			String LabTag = obj.getLab_tag();
//			String VncPort = obj.getVnc_port();
//			String WebId = obj.getWeb_id();
//			String Description = obj.getDescription();
//			String Vm_instructions = obj.getVm_instructions();
//			String VirtualizationType = obj.getVirtualization_type();
//			String PhysicalServerIP = obj.getPhysicalServerIP();
//
//			Switch SwitchId = obj.getSwitch_id();
//			String GenerationType = obj.getGeneration_type();
//			Location LocationId = obj.getLocation_id();
//
//			String VmLocationPath = obj.getVm_location_path();
//			SubProduct OperatingSystem = obj.getSubproduct_id();
//			Discount DiscountId = obj.getDiscount_id();
//			Price priceId = obj.getPrice_id();
//			VPC vpcId = obj.getVpc_id();
//			SecurityGroup security_group_id = obj.getSecurity_group_id();
//
//			list.setInstance_name(LabName);
//			list.setLab_id(LabId);
//			list.setLab_image(cover_image);
//			list.setLab_tag(LabTag);
//			list.setVnc_port(VncPort);
//			list.setWeb_id(WebId);
//			list.setDescription(Description);
//
//			list.setVm_instructions(Vm_instructions);
//			list.setVirtualization_type(VirtualizationType);
//			list.setPhysicalServerIP(PhysicalServerIP);
//			list.setSwitch_id(SwitchId);
//
//			list.setGeneration_type(GenerationType);
//			list.setLocation_id(LocationId);
//			list.setVm_location_path(VmLocationPath);
//			list.setSubproduct_id(OperatingSystem);
//
//			list.setDiscount_id(DiscountId);
//			list.setPrice_id(priceId);
//			list.setVpc_id(vpcId);
//			list.setSecurity_group_id(security_group_id);
//
//			repository.save(list);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return "redirect:/" + var_function_name + "/new";
//	}

//	@PostMapping("/save")
//	public String save(@ModelAttribute CloudInstance obj, @RequestParam("lab_image") MultipartFile labImage,
//			RedirectAttributes redirectAttributes, Principal principal) {
//		try {
//			CloudInstance list = new CloudInstance();
//			System.out.println("Inside_Save_cloud_instance ::");
//			System.out.println("labImage ::" + labImage);
//
//			// Copy all simple fields
//			list.setInstance_name(obj.getInstance_name());
//			list.setLab_id(obj.getLab_id());
//			list.setLab_tag(obj.getLab_tag());
//			list.setVnc_port(obj.getVnc_port());
//			list.setWeb_id(obj.getWeb_id());
//			list.setDescription(obj.getDescription());
//			list.setVm_instructions(obj.getVm_instructions());
//			list.setVirtualization_type(obj.getVirtualization_type());
//			list.setPhysicalServerIP(obj.getPhysicalServerIP());
//			list.setSwitch_id(obj.getSwitch_id());
//			list.setGeneration_type(obj.getGeneration_type());
//			list.setLocation_id(obj.getLocation_id());
//			list.setVm_location_path(obj.getVm_location_path());
//			list.setSubproduct_id(obj.getSubproduct_id());
//			list.setDiscount_id(obj.getDiscount_id());
//			list.setPrice_id(obj.getPrice_id());
//			list.setVpc_id(obj.getVpc_id());
//			list.setSecurity_group_id(obj.getSecurity_group_id());
//
//			if (labImage != null && !labImage.isEmpty()) {
//				list.setLab_image(labImage.getBytes());
//			}
//
//			repository.save(list);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return "redirect:/" + var_function_name + "/new";
//	}

//	@PostMapping("/save")
//	public String save(@ModelAttribute CloudInstance obj, @RequestParam(required = false) MultipartFile uploadedImage,
//			RedirectAttributes redirectAttributes, Principal principal) {
//		try {
//
//			// Handle the uploaded image
//
//			System.out.println("Inside_Save_cloud_instance ::");
//
//			if (uploadedImage != null && !uploadedImage.isEmpty()) {
//				obj.setLab_image(uploadedImage.getBytes()); // store file as byte[]
//				System.out.println("Uploaded labImage size: " + uploadedImage.getSize());
//			}
//
//			// Copy simple fields
////			list.setInstance_name(obj.getInstance_name());
////			list.setLab_id(obj.getLab_id());
////			list.setLab_tag(obj.getLab_tag());
////			list.setVnc_port(obj.getVnc_port());
////			list.setWeb_id(obj.getWeb_id());
////			list.setDescription(obj.getDescription());
////			list.setVm_instructions(obj.getVm_instructions());
////			list.setVirtualization_type(obj.getVirtualization_type());
////			list.setPhysicalServerIP(obj.getPhysicalServerIP());
////			list.setSwitch_id(obj.getSwitch_id());
////			list.setGeneration_type(obj.getGeneration_type());
////			list.setLocation_id(obj.getLocation_id());
////			list.setVm_location_path(obj.getVm_location_path());
////			list.setSubproduct_id(obj.getSubproduct_id());
////			list.setDiscount_id(obj.getDiscount_id());
////			list.setPrice_id(obj.getPrice_id());
////			list.setVpc_id(obj.getVpc_id());
////			list.setSecurity_group_id(obj.getSecurity_group_id());
////			list.s
//
//			ObjectMapper mapper = new ObjectMapper();
//			String jsonInstructions = mapper.writeValueAsString(obj.getVm_instructions());
//			
//			System.out.println("jsonInstructions ::"+jsonInstructions);
//			obj.setVm_instructions(jsonInstructions); // Save it as a string in DB
//
//			repository.save(obj);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return "redirect:/" + var_function_name + "/new";
//	}

//	@PostMapping("/save")
//	public String save(@ModelAttribute CloudInstanceForm form, @ModelAttribute CloudInstance obj,
//			@RequestParam(required = false) MultipartFile uploadedImage, RedirectAttributes redirectAttributes,
//			Principal principal) {
//
//		try {
//
//			if (obj == null) {
//				throw new RuntimeException("CloudInstance object is null. Please ensure form is binding correctly.");
//			}
//
//			if (uploadedImage != null && !uploadedImage.isEmpty()) {
//				obj.setLab_image(uploadedImage.getBytes());
//				System.out.println("Uploaded labImage size: " + uploadedImage.getSize());
//			}
//
//			// Save CloudInstance
//			repository.save(obj);
//
//			// Save instructions into ChartBoatInstructionTemplate
//			List<InstructionDto> instructions = form.getInstructions();
//			for (InstructionDto dto : instructions) {
//				ChartBoatInstructionTemplate instruction = new ChartBoatInstructionTemplate();
//
//				instruction.setTemplateId(obj.getId());
//				instruction.setTemaplateName(obj.getInstance_name());
//				instruction.setInstructionCommand(dto.getCommandText());
//
//				if (dto.getInstructionText() != null) {
//					instruction.setInstructionDetails(dto.getInstructionText().getBytes());
//				} else {
//					instruction.setInstructionDetails("".getBytes());
//				}
//
//				// Save instruction
//				ChartBoatInstructionTemplateRepository.save(instruction);
//			}
//
//			redirectAttributes.addFlashAttribute("result", "success");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			redirectAttributes.addFlashAttribute("result", "Error: " + e.getMessage());
//		}
//
//		return "redirect:/" + var_function_name + "/new";
//	}

	@PostMapping("/save")
	public String save(@ModelAttribute CloudInstanceForm form, @ModelAttribute CloudInstance obj,
			@RequestParam(required = false) MultipartFile uploadedImage, RedirectAttributes redirectAttributes,
			Principal principal) {

		try {
			if (uploadedImage != null && !uploadedImage.isEmpty()) {
				obj.setLab_image(uploadedImage.getBytes());
			}

			// Split docker_network_id into ID and Name
			if (obj.getDocker_network_id() != null && obj.getDocker_network_id().contains("~")) {
				String[] parts = obj.getDocker_network_id().split("~", 2);
				obj.setDocker_network_id(parts[0]);
				obj.setDocker_network_name(parts[1]);
			}
			obj.setMonitoring(true);

			repository.save(obj);

			// Save ChartBoat instructions if any
			List<InstructionDto> instructions = form.getInstructions();
			for (InstructionDto dto : instructions) {
				ChartBoatInstructionTemplate instruction = new ChartBoatInstructionTemplate();
				instruction.setTemplateId(obj.getId());
				instruction.setTemaplateName(obj.getInstance_name());
				instruction.setInstructionCommand(dto.getCommandText());
				instruction.setInstructionDetails(
						dto.getInstructionText() != null ? dto.getInstructionText().getBytes() : "".getBytes());

				ChartBoatInstructionTemplateRepository.save(instruction);
			}

			redirectAttributes.addFlashAttribute("result", "success");

		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("result", "Error: " + e.getMessage());
		}

		return "redirect:/" + var_function_name + "/new";
	}

	@GetMapping("/getCloundInstanceEdit")
	public ModelAndView getCloundInstanceEdit(@RequestParam("Id") Integer Id, Principal principal) {
		System.out.println("Edit Controller called for Id = " + Id);

		ModelAndView mav = new ModelAndView("cloud_instance_edit");
//	    mav.addObject("pageTitle", "Edit " + disp_function_name);
//	    mav.addObject("action_name", var_function_name);

		try {
			Optional<CloudInstance> optionalInstance = repository.findById(Id);
			if (!optionalInstance.isPresent()) {
				mav.addObject("error", "CloudInstance not found for ID: " + Id);
				return mav;
			}

			CloudInstance instance = optionalInstance.get();

			// Add dropdowns (same as in /new)
//	        mav.addObject("dcLocationList", repositoryLocation.getAllDClocations());
//	        mav.addObject("vmlocationPathList", repositoryVMLocationPath.getAllVMlocationsPahts());
//	        mav.addObject("securityGroupList", firewallRepository.getFirewall());
//	        mav.addObject("sharedCpuPlan", priceRepository.getSharedCpuPlan());
//	        mav.addObject("dedicatedCpuPlan", priceRepository.getDedicatedCpuPlan());
//	        mav.addObject("highMemoryPlan", priceRepository.getHighMemoryPlan());
//	        mav.addObject("switchList", switchRepository.getAllSwitch());
//	        mav.addObject("physicalServerIPList", PhysicalServerRepository.getPhysicalServerIPs());

			// Send the selected CloudInstance for editing

			mav.addObject("objEnt", instance);

		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("error", "Error fetching CloudInstance: " + e.getMessage());
		}

		return mav;
	}

	private byte[] createPlaceholderImage() {
		try {
			// Create a simple 1x1 pixel transparent image
			BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
			return baos.toByteArray();
		} catch (IOException e) {
			// Return empty array as last resort
			return new byte[0];
		}
	}

	@GetMapping("/edit/{id}")
	public ModelAndView edit(@PathVariable("id") Integer id) {

		try {
			ModelAndView mav = new ModelAndView(var_function_name + "_add");
			mav.addObject("action_name", var_function_name);
			mav.addObject("objEnt", repository.findById(id).get());
			mav.addObject("pageTitle", "Edit " + disp_function_name + " (ID: " + id + ")");
			mav.addObject("dcLocationList", repositoryLocation.getAllDClocations());
			return mav;
		} catch (Exception e) {
			ModelAndView mav = new ModelAndView("redirect:/" + var_function_name + "/view");
			mav.addObject("action_name", var_function_name);
			mav.addObject("message", e.getMessage());
			return mav;
		}
	}

	public String convertToLargestUnit(String bitsString) {
		System.out.println("########## " + bitsString);
		long bits;
		try {
			bits = Long.parseLong(bitsString);
		} catch (NumberFormatException e) {
			return "Invalid data"; // Handle invalid number format
		}

		if (bits == 0)
			return "0 bits";

		double kb = bits / 1024.0;
		double mb = kb / 1024.0;
		double gb = mb / 1024.0;

		if (gb >= 1) {
			return String.format("%.2f GB", gb);
		} else if (mb >= 1) {
			return String.format("%.2f MB", mb);
		} else if (kb >= 1) {
			return String.format("%.2f KB", kb);
		} else {
			return bits + " bits";
		}
	}

	@GetMapping("/VM/{instanceID}")
	public ModelAndView VM(@PathVariable("instanceID") int instanceID) {
		System.out.println("Cloud Instance Id = " + instanceID);
		ModelAndView mav = new ModelAndView("VMDetails");

		CloudInstance vm_instance = repository.findById(instanceID).get();

		long time = (long) ((usageRepository.getTime(vm_instance) == null ? 0
				: usageRepository.getTime(vm_instance).equals("") ? 0 : usageRepository.getTime(vm_instance)));
		long hours = TimeUnit.SECONDS.toHours(time);
		double hourlyPrice = vm_instance.getPrice_id().getHourly_price();
		mav.addObject("action_name", var_function_name);

		// String ram = String.format("%.2f",
		// Double.parseDouble(vm_instance.getMemoryAssigned()) / (1024*1024*1024));
		// String diskSize = String.format("%.2f",
		// Double.parseDouble(vm_instance.getDiskAssigned()) / (1024*1024*1024));

		mav.addObject("vmDetails", vm_instance);
//		mav.addObject("ram",
//				vm_instance.getMemoryAssigned() == null ? "-"
//						: vm_instance.getMemoryAssigned().equals("") ? "-"
//								: String.format("%.2f",
//										Double.parseDouble(vm_instance.getMemoryAssigned()) / (1024 * 1024 * 1024)));
//		mav.addObject("vmstatuscurrent", vm_instance.getVm_status());

		System.out.println("Cloud Instance to add vm status_dynamcaly = " + vm_instance.getVm_status());

		mav.addObject("ram", vm_instance.getMemoryAssigned());
		mav.addObject("diskSize",
				vm_instance.getDiskAssigned() == null ? "-"
						: vm_instance.getDiskAssigned().equals("") ? "-"
								: String.format("%.2f",
										Double.parseDouble(vm_instance.getDiskAssigned()) / (1024 * 1024 * 1024)));
		mav.addObject("vpcList", repositoryVPC.getAllVPC());
		mav.addObject("securityGroupList", firewallRepository.getFirewall());
		mav.addObject("sharedCpuPlan", priceRepository.getSharedCpuPlan());
		mav.addObject("dedicatedCpuPlan", priceRepository.getDedicatedCpuPlan());
		mav.addObject("highMemoryPlan", priceRepository.getHighMemoryPlan());
		mav.addObject("switchList", switchRepository.getAllSwitch());
		mav.addObject("externalStorageDetails", externalStorageRepository.findByinstanceId(vm_instance));
		mav.addObject("distinctRAM", priceRepository.getDistinctRAM());
		mav.addObject("distinctCPU", priceRepository.getDistinctCPU());

//	NodeHealthMonitoring li = healthRepository.findByNodeIP(vm_instance.getInstance_ip());
		CloudInstanceNodeHealthMonitoring li = CloudInstanceNodeHealthMonitoringRep
				.findBynodeIp(vm_instance.getPhysicalServerIP(), vm_instance.getInstance_name());

		if (li == null) {
			li = new CloudInstanceNodeHealthMonitoring();
			li.setCpuUtilization(0);
			li.setMemoryUtilization(0);
			li.setFreeMemory(0.0);
			li.setTotalMemory(0.0);
			li.setUsedMemory(0.0);

		}

		if (li.getTotalMemory() == null) {
			li.setTotalMemory(0.0);
		}
		if (li.getFreeMemory() == null) {
			li.setFreeMemory(0.0);
		}
		if (li.getUsedMemory() == null) {
			li.setUsedMemory(0.0);
		}
		if (li.getMemoryUtilization() == null) {
			li.setMemoryUtilization(0);
		}
		if (li.getCpuUtilization() == null) {
			li.setCpuUtilization(0);
		}

		mav.addObject("healthDetails", li);
		if (vm_instance.getSubproduct_id().getSub_product_name().contains("Windows")) {
			HardwareInventory inventory = hardwareRepository.findByDeviceIP(vm_instance.getInstance_ip());
//			HardwareInventory inventory = CloudInstanceNodeHealthMonitoringRep.findBynodeIp(vm_instance.getInstance_ip());

			if (inventory == null) {
				inventory = new HardwareInventory();
				inventory.setManufacturer("-");
				inventory.setProcessorCount("-");
				inventory.setProcessorName("-");
				inventory.setProcessorManufacturer("-");

				inventory.setBiosName("-");
				inventory.setBiosVersion("-");
				inventory.setBiosManufacturer("-");
				inventory.setMacAddress("-");
				inventory.setOriginalSerialNo("-");
				inventory.setDomain("-");
				inventory.setBuildNumber("-");
				inventory.setSystemModel("-");
				inventory.setServicePack("-");
				inventory.setProductId("-");
				inventory.setDiskSpace(0.0);
				inventory.setOsName("-");
				inventory.setOsType("-");
				inventory.setOsVersion("-");

			}

			mav.addObject("hardware", inventory);

		} else {
			HardwareInventoryLinux linuxInventory = hardwareLinuxRepository
					.findByIpAddress(vm_instance.getInstance_ip());
			if (linuxInventory == null) {
				linuxInventory = new HardwareInventoryLinux();
				linuxInventory.setArchitecture("-");
				linuxInventory.setBiosInfo("-");
				linuxInventory.setBranchName("-");
				linuxInventory.setcTime("-");
				linuxInventory.setDiscoverTime("-");
				linuxInventory.setGraphicCard("-");
				linuxInventory.setHddDrive("-");
				linuxInventory.setHostname("-");
				linuxInventory.setIpAddress("-");
				linuxInventory.setMacAddress("-");
				linuxInventory.setMotherboardName("-");
				linuxInventory.setOpName("-");
				linuxInventory.setOsName("-");
				linuxInventory.setPcName("-");
				linuxInventory.setProcessorName("-");
				linuxInventory.setRamDetails("-");
				linuxInventory.setSerialNo("-");
				linuxInventory.setVersion("-");
			}
			mav.addObject("hardware", linuxInventory);
		}

		mav.addObject("driveDetails", driveRepository.findByDeviceIP(vm_instance.getInstance_ip()));

		List<KVMDriveDetails> driveDetails22 = kvmDriveDetailsRepository
				.findByVmnameAndPhysicalServerIp22(vm_instance.getInstance_name(), vm_instance.getPhysicalServerIP());

		for (KVMDriveDetails driveDetail : driveDetails22) {
			String numericRegex = "^\\d+$";
			String usedBitsString = driveDetail.getUsed(); // Assuming getUsed() returns a String
			String capacityBitsString = driveDetail.getCapacity();
			// Check if usedBitsString and capacityBitsString contain only numbers
			if (usedBitsString != null && capacityBitsString != null && usedBitsString.matches(numericRegex)
					&& capacityBitsString.matches(numericRegex)) {
				String formattedUsed = convertToLargestUnit(usedBitsString);
				driveDetail.setUsed(formattedUsed); // Assuming setUsedFormatted() stores the formatted string

				String formattedcapacity = convertToLargestUnit(capacityBitsString);
				driveDetail.setCapacity(formattedcapacity);
			}

		}

		mav.addObject("kvmdriveDetails", driveDetails22);
//		  s

		mav.addObject("ReportData",
				nodeUtilizationRepository.vmAvailabilityReportDatadetailpage(vm_instance.getInstance_name()));
		mav.addObject("vmActivity", nodeAvailabilityRepository.vmAvailabilityReport(vm_instance.getInstance_ip()));

		List<List<Object>> result = new ArrayList<>();
		int sr = 0;
		List<Object[]> obj = vmHealthRepository.vmHealthHistoryReport(vm_instance.getInstance_name());
		for (Object[] data : obj) {
			sr++;
			List<Object> arr = new ArrayList<>();
			arr.add(sr);
			arr.add(data[0]);
			arr.add(data[1]);
			arr.add(data[2]);
			arr.add(data[3]);
			arr.add(data[4]);
			arr.add(data[5]);
			arr.add(data[6]);
			arr.add(data[7]);
			arr.add(data[8]);
			arr.add(data[9]);

			result.add(arr);
		}
		mav.addObject("vmHealthData", result);
		mav.addObject("vmCpuThreshold",
				cpuThresholdHistoryRepository.vmCpuThresholdReport(vm_instance.getInstance_name()));
		mav.addObject("vmMemoryThreshold",
				memoryThresholdHistoryRepository.vmMemoryThresholdReport(vm_instance.getInstance_name()));

		mav.addObject("price", hours * hourlyPrice);
		mav.addObject("hours", hours);
		CloudInstance objEnt = new CloudInstance();
		mav.addObject("objEnt", objEnt);

//		alertdata

		List<String> instances22 = new ArrayList<>();
		instances22.add(vm_instance.getInstance_name());
		List<AlertDash> AlertList = new ArrayList<>();
		Pageable pageable1 = PageRequest.of(0, 5);
		List<CloudInstanceMemoryThresholdHistory> ThreshHist = memoryThresholdHistoryRepository
				.findLastFiveData(instances22, pageable1);
		for (CloudInstanceMemoryThresholdHistory ThreshObj : ThreshHist) {
			AlertDash alertnewobj = new AlertDash();
			alertnewobj.setAlert_Name("MEMORY THRESHOLD");
			alertnewobj.setAlert_STATUS(ThreshObj.getMemoryStatus());
			alertnewobj.setDifference(ThreshObj.getMemoryUtilization() - ThreshObj.getMemoryThreshold());
			alertnewobj.setEVENT_TIMESTAMP(ThreshObj.getEventTimestamp());
			alertnewobj.setMEMORY_THRESHOLD(ThreshObj.getMemoryThreshold());
			alertnewobj.setMEMORY_UTILIZATION(ThreshObj.getMemoryUtilization());
			alertnewobj.setPhysicalServer_ip(ThreshObj.getNodeIp());
			alertnewobj.setVM_Name(ThreshObj.getVmName());

			AlertList.add(alertnewobj);

		}

//				List<CloudInstanceCpuThresholdHistory> ThreshCPUHist = cpuThresholdHistoryRepository.findAll();

//		List<CloudInstanceCpuThresholdHistory> ThreshCPUHist = cpuThresholdHistoryRepository
//				.findCurrentMonthData(instances22);

		Pageable pageable = PageRequest.of(0, 5);
		List<CloudInstanceCpuThresholdHistory> ThreshCPUHist = cpuThresholdHistoryRepository
				.findLastFiveData(instances22, pageable);

		for (CloudInstanceCpuThresholdHistory ThreshCPUObj : ThreshCPUHist) {
			AlertDash alertnewobj = new AlertDash();
			alertnewobj.setAlert_Name("CPU THRESHOLD");
			alertnewobj.setAlert_STATUS(ThreshCPUObj.getCpuStatus());
			alertnewobj.setDifference(ThreshCPUObj.getCpuUtilization() - ThreshCPUObj.getCpuUtilization());
			alertnewobj.setEVENT_TIMESTAMP(ThreshCPUObj.getEventTimestamp());
			alertnewobj.setMEMORY_THRESHOLD(ThreshCPUObj.getCpuThreshold());
			alertnewobj.setMEMORY_UTILIZATION(ThreshCPUObj.getCpuUtilization());
			alertnewobj.setPhysicalServer_ip(ThreshCPUObj.getNodeIp());
			alertnewobj.setVM_Name(ThreshCPUObj.getVmName());

			AlertList.add(alertnewobj);

		}

		List<AlertDash> sortedAlertList = AlertList.stream()
				.sorted(Comparator.comparing(AlertDash::getEVENT_TIMESTAMP).reversed()).collect(Collectors.toList());

		mav.addObject("AlertListObjdata", sortedAlertList);

		return mav;
	}

	@GetMapping("/getVMLocationPath")
	public @ResponseBody String getVMLocationPath(@RequestParam String location_id) {
		String json = null;
		int locationID = Integer.valueOf(location_id);

		try {
			List<Object[]> list = repositoryLocation.getVMLocationPath(locationID);
			System.out.println("list size:" + list.size());
			json = new ObjectMapper().writeValueAsString(list);
		} catch (Exception e) {
			System.out.println("exception :" + e);
		}
		return json;
	}

	@PostMapping("/resizeTab")
	public String resizeTab(@ModelAttribute("vmDetails") CloudInstance obj, RedirectAttributes redirectAttributes,
			Principal principal) {
		CloudInstance entity = repository.findById(obj.getId()).get();
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();
		try {
			System.out.println("Resize Hyper-V tab controller called");
			System.out.println("CPU: " + obj.getPrice_id().getvCpu() + "\nRAM: " + obj.getPrice_id().getRam()
					+ "\nSSD: " + obj.getPrice_id().getSsd_disk() + "\nBandwidth: " + obj.getPrice_id().getBandwidth());

			RequestApproval req = new RequestApproval();
			req.setRequestId(obj.getId());
			req.setRequesterName(username);
			req.setRequest_status("Pending");
			req.setAdminApproval("Pending");
			req.setRequest_type("instance update");
			req.setDescription("Request for Resizing VM");
			approvalRepository.save(req);

			CloudInstanceLog log = new CloudInstanceLog();
			log.setInstance_ip(entity.getInstance_ip());
			log.setInstance_name(entity.getInstance_name());
			log.setInstance_password(entity.getInstance_password());
			log.setDisk_path(entity.getDisk_path());
			log.setGeneration_type(entity.getGeneration_type());
			log.setIso_file_path(entity.getIso_file_path());
			log.setVm_location_path(entity.getVm_location_path());
			log.setLocation_id(entity.getLocation_id());
			log.setPrice_id(obj.getPrice_id());
			log.setSecurity_group_id(entity.getSecurity_group_id());
			log.setSubproduct_id(entity.getSubproduct_id());
			log.setSwitch_id(entity.getSwitch_id());
			log.setVpc_id(entity.getVpc_id());
			log.setRequest_type("instance update");
			cloudInstanceLogRepository.save(log);

			redirectAttributes.addFlashAttribute("message",
					"Dear " + username + ", your request has been raised for Resizing VM");
//			entity.setPrice_id(obj.getPrice_id());
//			repository.save(entity);
//			redirectAttributes.addFlashAttribute("message",
//					"The " + disp_function_name + " has been saved successfully!");
		} catch (Exception e) {
			redirectAttributes.addAttribute("message", e.getMessage());
		}

//		try {
//			ExecutePSCommand execute = new ExecutePSCommand();
//			String vm_name = entity.getInstance_name();
//			String memory_size = entity.getPrice_id().getRam();
//			String cpu = entity.getPrice_id().getvCpu();
//			String disk_size = entity.getPrice_id().getSsd_disk();
//			String disk_path = entity.getDisk_path();
//
//			String responsedata = execute.updateMemoryCPUDiskVM(vm_name, memory_size, disk_path, disk_size, cpu);
//
//		} catch (Exception e) {
//			System.out.println("Exception occured while memory = " + e);
//		}

		return "redirect:/" + var_function_name + "/view";
	}

	@PostMapping("/switchUpdate")
	public String switchUpdate(@ModelAttribute("vmDetails") CloudInstance obj, RedirectAttributes redirectAttributes) {
		CloudInstance entity = repository.findById(obj.getId()).get();
		try {
			System.out.println("network tab controller called");
			entity.setSwitch_id(obj.getSwitch_id());
			entity.setVpc_id(obj.getVpc_id());
			entity.setSecurity_group_id(obj.getSecurity_group_id());
			repository.save(entity);
			redirectAttributes.addFlashAttribute("message",
					"The " + disp_function_name + " has been saved successfully!");
		} catch (Exception e) {
			redirectAttributes.addAttribute("message", e.getMessage());
		}

		try {
			String vm_name = entity.getInstance_name();
			System.out.println("VM Name = " + vm_name);
			String switch_name = obj.getSwitch_id().getSwitch_name();
			ExecutePSCommand execute = new ExecutePSCommand();
			String responsedata = execute.updateSwitchNameVM(vm_name, switch_name);

		} catch (Exception e) {
			System.out.println("Exception occured while updating switch = " + e);
		}

		return "redirect:/" + var_function_name + "/view";
	}

	@PostMapping("/updateOS")
	public String updateOS(@ModelAttribute("vmDetails") CloudInstance obj, RedirectAttributes redirectAttributes,
			Principal principal) {
		CloudInstance entity = repository.findById(obj.getId()).get();
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();
		try {
			System.out.println("updateOS controller called");
			entity.setSubproduct_id(obj.getSubproduct_id());

			RequestApproval req = new RequestApproval();
			req.setRequestId(obj.getId());
			req.setRequesterName(username);
			req.setRequest_status("Pending");
			req.setRequest_type("instance update");
			req.setDescription("Request for OS update");
			approvalRepository.save(req);

			CloudInstanceLog log = new CloudInstanceLog();
			log.setInstance_ip(entity.getInstance_ip());
			log.setInstance_name(entity.getInstance_name());
			log.setInstance_password(entity.getInstance_password());
			log.setDisk_path(entity.getDisk_path());
			log.setGeneration_type(entity.getGeneration_type());
			log.setIso_file_path(entity.getIso_file_path());
			log.setVm_location_path(entity.getVm_location_path());
			log.setLocation_id(entity.getLocation_id());
			log.setPrice_id(obj.getPrice_id());
			log.setSecurity_group_id(entity.getSecurity_group_id());
			log.setSubproduct_id(entity.getSubproduct_id());
			log.setSwitch_id(entity.getSwitch_id());
			log.setVpc_id(entity.getVpc_id());
			log.setRequest_type("instance update");
			cloudInstanceLogRepository.save(log);

			redirectAttributes.addFlashAttribute("message",
					"Dear " + username + ", your request has been raised for OS update");

//			repository.save(entity);
//			redirectAttributes.addFlashAttribute("message",
//					"The " + disp_function_name + " has been saved successfully!");
		} catch (Exception e) {
			redirectAttributes.addAttribute("message", e.getMessage());
		}
		return "redirect:/" + var_function_name + "/view";
	}

	@GetMapping("/getDiscount")
	public @ResponseBody String getDiscount(@RequestParam String discountType) {
		System.out.println("Get Discount controller called :" + discountType);
		String discount = null;
		try {
			discount = discountRepository.getDiscount(discountType);
			System.out.println("discount : " + discount);
		} catch (Exception e) {
			System.out.println("exception :" + e);
		}
		return discount;
	}

	// VM creation for User

//	// Add External Storage
//	@GetMapping("/externalStorage")
//	public @ResponseBody String addStorage(@RequestParam String instance_id, String disk_path, String disk_size) {
//
//		System.out.println("data for external storage = " + instance_id + " - " + disk_path + " - " + disk_size);
//		boolean isStorageAdded = false;
//		CloudInstance obj = repository.findById(Integer.valueOf(instance_id)).get();
//		try {
//			ExecutePSCommand execute = new ExecutePSCommand();
////			String response = execute.createNewHardiskVM(obj.getInstance_name(), disk_path, disk_size);
////			if(!response.isEmpty()) {
//			isStorageAdded = true;
////			}
//			try {
////				if(isStorageAdded) {
//
//				AdditionalStorage storage = new AdditionalStorage();
//				obj.setId(Integer.valueOf(instance_id));
//				storage.setInstance_id(obj);
//				storage.setPrice(Integer.valueOf(disk_size) * 9);
//				storage.setStorage_size(disk_size);
//				externalStorageRepository.save(storage);
////				}
//			} catch (Exception e) {
//				System.out.println("Exception occured while adding external storage = " + e);
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		return String.valueOf(isStorageAdded);
//
//	}

	public void executeRemoteCommand(String command, String USER, String PASSWORD, String HOST) {

		Session session = null;
		ChannelExec channel = null;
		String status_msg = "";

		try {
			JSch jsch = new JSch();
			session = jsch.getSession(USER, HOST, 22);
			session.setPassword(PASSWORD);

			// Configuring SSH to ignore key checking
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

			session.connect();

			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);
			channel.setErrStream(System.err);

			InputStream in = channel.getInputStream();
			channel.connect();

			byte[] buffer = new byte[1024];
			int readCount;
			while ((readCount = in.read(buffer)) != -1) {
				System.out.print("CMD op:" + new String(buffer, 0, readCount));
			}
			status_msg = "true";
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.print("Exceotion command:" + e.getMessage());
			status_msg = "fail:" + e.getMessage();
		} finally {
			if (channel != null) {
				channel.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}

		// return status_msg;
	}

	@GetMapping("/startVM/{id}")
	public String startVM(@PathVariable("id") int id) {
		System.out.println("start VM ID :" + id);
		CloudInstance vm_instance = repository.findById(id).get();
		String virtulizationtype = vm_instance.getVirtualization_type();
		System.out.println("virtulizationtype " + virtulizationtype);
		if (virtulizationtype.trim().equalsIgnoreCase("kvm")) {
			String vm_name = vm_instance.getInstance_name();
			String Physicalserverip = vm_instance.getPhysicalServerIP();
			String sshusername = PhysicalServerRepository.findByserverIP(Physicalserverip.trim()).getSsh_host();
			String sshPassword = PhysicalServerRepository.findByserverIP(Physicalserverip.trim()).getSsh_password();

			String createVMCommand = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh start " + vm_name
					+ "";

			executeRemoteCommand(createVMCommand, sshusername, sshPassword, Physicalserverip);
			CloudInstanceUsage vm_usage = new CloudInstanceUsage();
			vm_usage.setInstance_id(vm_instance);
			vm_usage.setEvent_type("start");
			repositoryInstanceUsage.save(vm_usage);

			vm_instance.setVm_start_stop_status("start");
			vm_instance.setVm_start_stop_time(new Timestamp(System.currentTimeMillis()));
			repository.save(vm_instance);
		} else {

			String responce_data = null;
			Socket socket = null;
			try {
				socket = new Socket(vm_instance.getPhysicalServerIP(), 9005);
				System.out.println("Physical Server IP = " + vm_instance.getPhysicalServerIP());
				VMCreationBean bean = new VMCreationBean();
				bean.setActivity("vm_start");
				bean.setInstanceName(vm_instance.getInstance_name());
				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
				outputStream.writeObject(bean);
				outputStream.flush();
				ObjectInputStream serverResponse = new ObjectInputStream(socket.getInputStream());
				responce_data = (String) serverResponse.readObject();
				System.out.println("Server response : " + responce_data);
				if (responce_data.equalsIgnoreCase("success")) {

					CloudInstanceUsage vm_usage = new CloudInstanceUsage();
					vm_usage.setInstance_id(vm_instance);
					vm_usage.setEvent_type("start");
					repositoryInstanceUsage.save(vm_usage);

					vm_instance.setVm_start_stop_status("start");
					vm_instance.setVm_start_stop_time(new Timestamp(System.currentTimeMillis()));
					repository.save(vm_instance);

				}

			} catch (Exception e) {
				System.out.println("Exception occured while deleting VM = " + e);
				responce_data = "fail";
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			/*
			 * try { String vm_name = vm_instance.getInstance_name();
			 * System.out.println("VM Name = " + vm_name); ExecutePSCommand execute = new
			 * ExecutePSCommand(); String responsedata = execute.startVM(vm_name);
			 * System.out.println("START VM OP = " + responsedata);
			 * 
			 * } catch (Exception e) {
			 * System.out.println("Exception occured while starting VM = " + e); }
			 */

		}

		return "redirect:/" + var_function_name + "/view";
	}

	@GetMapping("/stopVM/{id}")
	public String stopVM(@PathVariable("id") int id) {
		long milliseconds;
		System.out.println("stop VM ID:" + id);
		CloudInstance vm_instance = repository.findById(id).get();

		String virtulizationtype = vm_instance.getVirtualization_type();
		System.out.println("virtulizationtype " + virtulizationtype);
		if (virtulizationtype.trim().equalsIgnoreCase("kvm")) {

			String vm_name = vm_instance.getInstance_name();
			String Physicalserverip = vm_instance.getPhysicalServerIP();
			String sshusername = PhysicalServerRepository.findByserverIP(Physicalserverip.trim()).getSsh_host();
			String sshPassword = PhysicalServerRepository.findByserverIP(Physicalserverip.trim()).getSsh_password();

			String createVMCommand = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh destroy " + vm_name
					+ "";

			executeRemoteCommand(createVMCommand, sshusername, sshPassword, Physicalserverip);

			Timestamp last_start_time = vm_instance.getVm_start_stop_time();
			Timestamp current_stop_time = new Timestamp(System.currentTimeMillis());
			if (last_start_time == null || last_start_time.equals("")) {
				milliseconds = current_stop_time.getTime();
			} else {
				milliseconds = current_stop_time.getTime() - last_start_time.getTime();
			}

			long seconds = milliseconds / 1000;
			long millis = System.currentTimeMillis();
			Date cdate = new Date(millis);
			Long old_time_diff = repositoryInstanceDailyUsage.getTimeDiff(vm_instance, cdate);
			System.out.println("Not Present:" + old_time_diff + ":" + vm_instance + ":" + cdate);

			if (old_time_diff == null) {

				CloudInstanceUsageDaily usageDaily = new CloudInstanceUsageDaily();
				usageDaily.setDate(cdate);
				usageDaily.setInstance_id(vm_instance);
				usageDaily.setTime_difference_sec(seconds);
				repositoryInstanceDailyUsage.save(usageDaily);
			} else {

				old_time_diff = old_time_diff + seconds;
				System.out.println(" Present Update add time:" + old_time_diff);
				repositoryInstanceDailyUsage.updateTimeDiff(old_time_diff, vm_instance, cdate);
			}

			CloudInstanceUsage vm_usage = new CloudInstanceUsage();
			vm_usage.setInstance_id(vm_instance);
			vm_usage.setEvent_type("stop");
			repositoryInstanceUsage.save(vm_usage);

			vm_instance.setVm_start_stop_status("stop");
			vm_instance.setVm_start_stop_time(current_stop_time);
			repository.save(vm_instance);

		} else {
			System.out.println("Hyper VM  STOP");

			String responce_data = null;
			Socket socket = null;
			try {
				socket = new Socket(vm_instance.getPhysicalServerIP(), 9005);
				System.out.println("Physical Server IP = " + vm_instance.getPhysicalServerIP());
				VMCreationBean bean = new VMCreationBean();
				bean.setActivity("vm_stop");
				bean.setInstanceName(vm_instance.getInstance_name());
				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
				outputStream.writeObject(bean);
				outputStream.flush();
				ObjectInputStream serverResponse = new ObjectInputStream(socket.getInputStream());
				responce_data = (String) serverResponse.readObject();
				System.out.println("Server response : " + responce_data);
				if (responce_data.equalsIgnoreCase("success")) {

					Timestamp last_start_time = vm_instance.getVm_start_stop_time();
					Timestamp current_stop_time = new Timestamp(System.currentTimeMillis());
					if (last_start_time == null || last_start_time.equals("")) {
						milliseconds = current_stop_time.getTime();
					} else {
						milliseconds = current_stop_time.getTime() - last_start_time.getTime();
					}

					long seconds = milliseconds / 1000;
					long millis = System.currentTimeMillis();
					Date cdate = new Date(millis);
					Long old_time_diff = repositoryInstanceDailyUsage.getTimeDiff(vm_instance, cdate);
					System.out.println("Not Present:" + old_time_diff + ":" + vm_instance + ":" + cdate);

					if (old_time_diff == null) {

						CloudInstanceUsageDaily usageDaily = new CloudInstanceUsageDaily();
						usageDaily.setDate(cdate);
						usageDaily.setInstance_id(vm_instance);
						usageDaily.setTime_difference_sec(seconds);
						repositoryInstanceDailyUsage.save(usageDaily);
					} else {

						old_time_diff = old_time_diff + seconds;
						System.out.println(" Present Update add time:" + old_time_diff);
						repositoryInstanceDailyUsage.updateTimeDiff(old_time_diff, vm_instance, cdate);
					}

					CloudInstanceUsage vm_usage = new CloudInstanceUsage();
					vm_usage.setInstance_id(vm_instance);
					vm_usage.setEvent_type("stop");
					repositoryInstanceUsage.save(vm_usage);

					vm_instance.setVm_start_stop_status("stop");
					vm_instance.setVm_start_stop_time(current_stop_time);
					repository.save(vm_instance);

				}

			} catch (Exception e) {
				System.out.println("Exception occured while deleting VM = " + e);
				responce_data = "fail";
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			/*
			 * try {
			 * 
			 * String vm_name = vm_instance.getInstance_name();
			 * 
			 * System.out.println("VM Name = " + vm_name); ExecutePSCommand execute = new
			 * ExecutePSCommand(); String responsedata = execute.stopVM(vm_name);
			 * System.out.println("STOP VM OP = " + responsedata);
			 * 
			 * } catch (Exception e) {
			 * System.out.println("Exception occured while stoping VM = " + e); }
			 */
		}

		return "redirect:/" + var_function_name + "/view";
	}

	@GetMapping("/changeVMState")
	public @ResponseBody String changeVMState(@RequestParam String state, @RequestParam String instance_id) {

		String vmState = "";
		System.out.println(state + " - " + instance_id);
		try {

			CloudInstance instance = repository.findById(Integer.parseInt(instance_id)).get();

			if (state.equalsIgnoreCase("active")) {
				instance.setVm_start_stop_status("stop");
				repository.save(instance);
				vmState = "stop";
			} else if (state.equalsIgnoreCase("suspend")) {
				instance.setVm_start_stop_status("suspended");
				repository.save(instance);
				vmState = "suspended";
			}

		} catch (Exception e) {
			System.out.println("Exception while changing VM state = " + e);
		}

		return vmState;
	}

	@GetMapping("/getCpuUtilization")
	public @ResponseBody int getCpuUtilization(@RequestParam String ip_address,
			@RequestParam String physicalServerIPtemp) {

		int data = 0;
		try {
			System.out.println("$$$getCpuUtilization :::: " + physicalServerIPtemp + ":" + ip_address);
//			NodeHealthMonitoring health = healthRepository.findByNodeIP(ip_address);
			CloudInstanceNodeHealthMonitoring health = CloudInstanceNodeHealthMonitoringRep
					.findBynodeIp(physicalServerIPtemp, ip_address);
			data = health.getCpuUtilization();

		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("cpu utilization = " + data);
		return data;
	}

	@PostMapping("/GetUserRole")
	public @ResponseBody String GetUserRole(@RequestParam("username") String userName) {

//		String data = 0;
		String UserRole = "";
		System.out.println("usernaem ::::: " + userName);
		try {

			List<Object[]> results = ToGetUserRole.findUserNameAndRoleNameByUserName(userName);
//			List<String[]> userRoles = new ArrayList<>();

			for (Object[] result : results) {
//				String[] userRole = new String[2];
//				userRole[0] = (String) result[0]; // User_Name
				UserRole = (String) result[1]; // Role_Name
//				System.out.println("Userr Role ::: " + UserRole);
//				System.out.println("Userr Role ::: " + userRole[1]);
//				userRoles.add(userRole);
			}
//			System.out.println("Userr Role ::: " + results);
//			System.out.println("Userr Role ::: " + userRoles);

		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("Current USerr Role = " + UserRole);
		return UserRole;
	}

	@GetMapping("/physicalVMDashboard")
	public ModelAndView physicalVMDashboard() {
		ModelAndView mav = new ModelAndView("physicalVmDashboard");
		mav.addObject("ip", "");
		try {

//			List<DiscoverDrives> drives = driveRepository.findByDeviceIP("192.168.56.1");
//			for (DiscoverDrives obj : drives) {
//				obj.setTotalSpace(String.format("%.2f",(Double.valueOf(obj.getTotalSpace())/1024)));
//				obj.setUsedSpace(String.format("%.2f",(Double.valueOf(obj.getUsedSpace())/1024)));
//				obj.setFreeSpace(String.format("%.2f",(Double.valueOf(obj.getFreeSpace())/1024)));
//			}
			mav.addObject("pageTitle", "Report");
			mav.addObject("PhysicalIPList", PhysicalServerRepository.getPhysicalServerIPs());

		} catch (Exception e) {
			System.out.println("Exception occured while fetching VM Data = " + e);

		}
		return mav;
	}

	@GetMapping("/physicalVM/{PhyIP}")
	public ModelAndView physicalVM(@PathVariable("PhyIP") String PhyIPAddress) {

		ModelAndView mav = new ModelAndView("physical_vm");
		mav.addObject("ip", PhyIPAddress.trim());
		try {

//			List<DiscoverDrives> drives = driveRepository.findByDeviceIP("192.168.56.1");
//			for (DiscoverDrives obj : drives) {
//				obj.setTotalSpace(String.format("%.2f",(Double.valueOf(obj.getTotalSpace())/1024)));
//				obj.setUsedSpace(String.format("%.2f",(Double.valueOf(obj.getUsedSpace())/1024)));
//				obj.setFreeSpace(String.format("%.2f",(Double.valueOf(obj.getFreeSpace())/1024)));
//			}
			mav.addObject("driveDetails", driveRepository.findByDeviceIP(PhyIPAddress.trim()));
			mav.addObject("interfaceSummary", interfaceMonitoringRepository.getInterfaceSummary(PhyIPAddress.trim()));
			mav.addObject("nodeStatus", nodeStatusHistoryRepository.findByNodeIP(PhyIPAddress.trim()));
			mav.addObject("nodeStatus", nodeStatusHistoryRepository.findByNodeIP(PhyIPAddress.trim()));
			mav.addObject("NodeHealthMonitoring", healthRepository.findByNodeIP(PhyIPAddress.trim()));

//			CloudInstance vm_instance = repository.findById(instanceID).get();CloudInstance vm_instance = repository.findById(instanceID).get();
//
//			mav.addObject("vmDetails", vm_instance);

		} catch (Exception e) {
			System.out.println("Exception occured while fetching VM Data = " + e);

		}
		return mav;
	}

	@GetMapping("/nodeUpTimeStatus")
	public @ResponseBody String nodeUpTimeStatus(@RequestParam String ip_address) {

		JSONArray jsonData = null;

		JSONArray upArray = null;
		JSONArray downArray = null;
		JSONArray catArray = null;

		try {

			List<NodeAvailability> list = nodeAvailabilityRepository.findByNodeIP(ip_address);
			System.out.println(list.size());
			String category = "";
			List<String> catList = new ArrayList();
			List<Double> upList = new ArrayList();
			List<Double> downList = new ArrayList();
			double uptime = 0;
			double downtime = 0;
			for (NodeAvailability data : list) {

				downArray = new JSONArray();
				catArray = new JSONArray();
				upArray = new JSONArray();
				category = data.getEventTime().toString();
				catList.add(category);

				uptime = data.getUptimePercent();
				upList.add(uptime);

				downtime = data.getDowntimePercent();
				downList.add(downtime);

			}
			upArray = new JSONArray(upList);
			downArray = new JSONArray(downList);
			catArray = new JSONArray(catList);

			JSONObject jsonMain = new JSONObject();
			jsonMain.put("upArray", upArray);
			jsonMain.put("downArray", downArray);
			jsonMain.put("catArray", catArray);
			jsonData = new JSONArray();
			jsonData.put(jsonMain);
		} catch (Exception e) {
			System.out.println("Exception:" + e);
		}
		System.out.println("Node Availability Data = " + jsonData);
		return jsonData.toString();
	}

	@GetMapping("/nodeLatencyStatus")
	public @ResponseBody String nodeLatencyStatus(@RequestParam String ip_address) {

		JSONArray array1 = null;
		JSONObject jsonObject = null;

		try {

			List<NodeMonitoring> dataList = nodeMonitoringRepository.findByNodeIP(ip_address);
			array1 = new JSONArray();
			for (NodeMonitoring object : dataList) {
				jsonObject = new JSONObject();
				jsonObject.put("latency", object.getLatency());
				jsonObject.put("packetLoss", object.getPacketLoss());
				array1.put(jsonObject);
			}

			System.out.println("Latency Packet drop Array:" + array1.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return array1.toString();
	}

	@PostMapping("/addVmToGroup")
	public @ResponseBody String addVmToGroup(@RequestParam("instanceID") String instanceID,
			@RequestParam String groupName) {

		String result = "";
		String ids[] = instanceID.split(",");
		try {

			System.out.println("VM's = " + instanceID + "\nGroup = " + groupName);
			for (String string : ids) {
				repository.addVmToGroup(Integer.valueOf(string), groupName);
			}
			result = "success";
		} catch (Exception e) {
			System.out.println("Exception occured while adding VM to Group = " + e);
		}

		return result;
	}

	@PostMapping("/addCustomerNameToVM")
	public @ResponseBody String addCustomerNameToVM(@RequestParam("instanceID") String instanceID,
			@RequestParam String customerName) {

		String result = "";
		String ids[] = instanceID.split(",");
		try {

			System.out.println("VM's = " + instanceID + "\nCustomer = " + customerName);
			for (String string : ids) {
				repository.addCustomerToVM(Integer.valueOf(string), customerName);
			}
			result = "success";
		} catch (Exception e) {
			System.out.println("Exception occured while adding VM to Customer = " + e);
		}

		return result;
	}

	@GetMapping("/vmDeletionRequest")
	public @ResponseBody String vmDeletionRequest(@RequestParam("instance_id") String instance_id,
			Principal principal) {
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();
		System.out.println("Instance id for Deletion = " + instance_id);
		String responce_data = null;
		try {

			CloudInstance obj = repository.findById(Integer.parseInt(instance_id)).get();
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
			requestObj.setAdminApproval("Pending");
			requestObj.setRequest_status("Pending");
			requestObj.setRequest_type("instance delete");
			requestObj.setLog_id(savedLog);
			approvalRepository.save(requestObj);

			responce_data = "success~" + username;

		} catch (Exception e) {
			System.out.println("Exception occurred while raising request for VM Deletion:" + e);
			responce_data = "fail~" + username;
		}
		return responce_data;
	}

	@GetMapping("/vmDeletionSuper")
	public @ResponseBody String vmDeletionSuper(@RequestParam String instance_id, Principal principal) {
		String responce_data = null;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();
		AppUser user = userRepository.findByUsername(username);
		System.out.println("Instance id =" + instance_id);
		CloudInstance obj = repository.findById(Integer.parseInt(instance_id)).get();

		String virtulizationtype = obj.getVirtualization_type();
		System.out.println("virtulizationtype " + virtulizationtype);
		String vm_name = obj.getInstance_name();
		if (virtulizationtype.trim().equalsIgnoreCase("kvm")) {

			String Physicalserverip = obj.getPhysicalServerIP();
			String sshusername = PhysicalServerRepository.findByserverIP(Physicalserverip.trim()).getSsh_host();
			String sshPassword = PhysicalServerRepository.findByserverIP(Physicalserverip.trim()).getSsh_password();

			String createVMCommand = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh destroy " + vm_name
					+ "";

			executeRemoteCommand(createVMCommand, sshusername, sshPassword, Physicalserverip);

			createVMCommand = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh undefine " + vm_name + "";

			executeRemoteCommand(createVMCommand, sshusername, sshPassword, Physicalserverip);

			responce_data = "success";
			if (responce_data.equalsIgnoreCase("success")) {
				obj.setMonitoring(false);
				obj.setInstance_name(vm_name + "_");
				repository.save(obj);
			}

		} else {

			System.out.println("Physical server IP = " + obj.getPhysicalServerIP());
			try {
				Socket socket = new Socket(obj.getPhysicalServerIP(), 9005);
				System.out.println("Physical Server IP = " + obj.getPhysicalServerIP());
				VMCreationBean bean = new VMCreationBean();
				bean.setActivity("vm_delete");
				bean.setInstanceName(obj.getInstance_name());
				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
				outputStream.writeObject(bean);
				outputStream.flush();
				ObjectInputStream serverResponse = new ObjectInputStream(socket.getInputStream());
				responce_data = (String) serverResponse.readObject();
				System.out.println("Server response: " + responce_data);
				if (responce_data.equalsIgnoreCase("success")) {
					obj.setMonitoring(false);
					obj.setInstance_name(vm_name + "_");
					repository.save(obj);
				}

				socket.close();

			} catch (Exception e) {
				System.out.println("Exception occured while deleting VM = " + e);
				responce_data = "fail";
			}
		}
		return responce_data;
	}

	// cloud_instance ->cpu_assigned,memory_assigned

//	@PostMapping("/resizeTabSuperadmin")
//	public String resizeTabSuperadmin(@ModelAttribute("vmDetails") CloudInstance obj,
//			RedirectAttributes redirectAttributes, Principal principal) {
//		
//		
	@PostMapping("/resizeTabSuperadmin")
	public String resizeTabSuperadmin(@ModelAttribute("vmDetails") CloudInstance obj2,
			RedirectAttributes redirectAttributes, Principal principal, @RequestParam("newRAM") String ram_val,
			@RequestParam("newCpu") String cpu_val,
			@RequestParam(name = "cpuCheck", defaultValue = "off") String cpuCheck,
			@RequestParam(name = "ramCheck", defaultValue = "off") String ramCheck) {

		boolean status = false;
		StringBuilder message = new StringBuilder();
		boolean isCPUIncrease = false;
		boolean isRAMIncrease = false;
		// boolean isDiskIncrease = false;

		int id = obj2.getId();
		CloudInstance entity = repository.findById(obj2.getId()).get();
		CloudInstanceLog log = new CloudInstanceLog();
		String vm_name = entity.getInstance_name();
		try {
			System.out.println(id + ":RAM =" + ram_val + "\nCPU =" + cpu_val);
			System.out.println("cpuCheck=" + cpuCheck + "\nramCheck=" + ramCheck);

			if (cpuCheck != null && cpuCheck.equals("on")) {
				isCPUIncrease = true;
			}
			if (ramCheck != null && ramCheck.equals("on")) {
				isRAMIncrease = true;
			}
//			if (diskCheck != null && diskCheck.equals("on")) {
//				isDiskIncrease = true;
//			}
			String virtType = entity.getVirtualization_type();
			System.out.println("SuperAdmin-virtType:" + virtType);
			String serverIP = entity.getPhysicalServerIP();
			System.out.println("SuperAdmin- serverIP:" + serverIP);
			if (virtType != null && virtType.equalsIgnoreCase("kvm")) {
				System.out.println("SuperAdmin-KVM Resize");
				String sshusername = addPhysicalServerRepository.findByserverIP(serverIP.trim()).getSsh_host();
				String sshPassword = addPhysicalServerRepository.findByserverIP(serverIP.trim()).getSsh_password();

				KVMResize kvmObj = new KVMResize(serverIP, vm_name, sshusername, sshPassword);
				// CPU Increase
				boolean isCPUFail = false;

				if (isCPUIncrease) {
					System.out.println("In SuperAdmin-KVM CPU Resize");
					int increaseCPU = Integer.parseInt(cpu_val);
					System.out.println("SuperAdmin-KVM CPU:" + increaseCPU);
					CommandResult result = kvmObj.changeKVMCPU(increaseCPU);
					status = result.isStatus();
					message.append(result.getMessage());
					if (status) {
						try {
							repository.updateVMCPU(id, cpu_val);
						} catch (Exception e) {
							System.out.print("Exception DB update CPU:" + e.getMessage());
						}
						log.setCpuAssigned(cpu_val);
					} else {
						isCPUFail = true;
					}
				}
				// RAM Increase

				if (isRAMIncrease && isCPUFail == false) {
					System.out.println("In SuperAdmin-KVM RAM Resize");
					int increaseRAM = Integer.parseInt(CMPUtil.parseNumberToString(ram_val));
					System.out.println("SuperAdmin-KVM RAM:" + increaseRAM);
					CommandResult result = kvmObj.changeKVMRAM(increaseRAM);
					status = result.isStatus();
					if (status) {

						try {
							repository.updateVMRam(id, ram_val);
						} catch (Exception e) {
							System.out.print("Exception DB update RAM:" + e.getMessage());
						}

						log.setMemoryAssigned(ram_val);
					}
					message.append(result.getMessage());
				}

//			// DISK Increase
//			if (isDiskIncrease) {
//				System.out.println("In SuperAdmin-KVM DISK Resize");
//				int increaseDisk =Integer.parseInt(disk_val);
//				System.out.println("SuperAdmin-KVM DISK:"+increaseDisk);
//
//			}
//			

			} else {
				System.out.println("SuperAdmin-Hyper-v Resize");

				String responce_data = "";

				Socket socket = null;

				try {
					socket = new Socket(entity.getPhysicalServerIP(), 9005);
					VMCreationBean bean = new VMCreationBean();
					bean.setActivity("vm_update");
					bean.setInstanceName(vm_name);
					bean.setvCpu(cpu_val);
					bean.setMemoryStartupBytes(ram_val);
					bean.setCPUIncrease(isCPUIncrease);
					bean.setRAMIncrease(isRAMIncrease);
					// bean.setVhdPath(disk_path);
					// bean.setNewVHDSizeBytes(ssd_size);

					ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
					outputStream.writeObject(bean);
					outputStream.flush();
					ObjectInputStream serverResponse = new ObjectInputStream(socket.getInputStream());
					responce_data = (String) serverResponse.readObject();
					System.out.println("Server response: " + responce_data);

					if (!responce_data.contains("Error")) {
						System.out.println("Hyper Resize sucesss:");
						if (isRAMIncrease) {
							try {
								repository.updateVMRam(id, ram_val);
							} catch (Exception e) {
								System.out.print("Exception DB update RAM:" + e.getMessage());
							}
							log.setMemoryAssigned(ram_val);
						}
						if (isCPUIncrease) {

							try {
								repository.updateVMCPU(id, cpu_val);
							} catch (Exception e) {
								System.out.print("Exception DB update CPU:" + e.getMessage());
							}
							log.setCpuAssigned(cpu_val);
						}

						status = true;
						// redirectAttributes.addFlashAttribute("updateVMCommandOP", "success");
					} else {
						status = false;
						// redirectAttributes.addFlashAttribute("updateVMCommandOP", "fail");
					}
				} catch (Exception e) {
					System.out.print("Exception Resize hyperv superadmin:" + e);
					status = false;
					message = message.append("Error: " + e.getMessage());

				} finally {
					try {
						if (socket != null) {
							socket.close();
						}
					} catch (Exception e) {
					}
				}

			}
		} catch (Exception e) {
			System.out.print("Exception command:" + e.getMessage());
			status = false;
			message = message.append("Error: " + e.getMessage());
		}

		if (status) {

			try {
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
				cloudInstanceLogRepository.save(log);
			} catch (Exception e) {
				System.out.print("Exception DB save log:" + e.getMessage());
			}

			System.out.print("Resize VM successfully");
			redirectAttributes.addFlashAttribute("updateVMCommandOP", "success");
		} else {
			redirectAttributes.addFlashAttribute("updateVMCommandOP", message.toString());
		}
		return "redirect:/cloud_instance/VM/" + id;

	}

//	@PostMapping("/resizeTabSuperadmin")
//	public String resizeTabSuperadmin(@ModelAttribute("vmDetails") CloudInstance obj,
//			RedirectAttributes redirectAttributes, Principal principal) {
//		
//		//handle discover VM resize
//		
//		System.out.println("SuperAdmin- resize cpu memory");
//
//		CloudInstance entity = repository.findById(obj.getId()).get();
//		String responce_data = null;
//		User loginedUser = (User) ((Authentication) principal).getPrincipal();
//		String username = loginedUser.getUsername();
//		
//		if (!(Integer.valueOf(obj.getPrice_id().getSsd_disk().replaceAll("\\D+", "")) > Integer
//				.valueOf(entity.getPrice_id().getSsd_disk().replaceAll("\\D+", "")))) {
//			redirectAttributes.addFlashAttribute("resizeMessage",
//					"Dear " + username + ", please select plan which has SSD size greater than your previous SSD size");
//			return "redirect:/cloud_instance/VM/" + obj.getId();
//		} else if (!(Integer.valueOf(obj.getPrice_id().getRam().replaceAll("\\D+", "")) >= Integer
//				.valueOf(entity.getPrice_id().getRam().replaceAll("\\D+", "")))) {
//			redirectAttributes.addFlashAttribute("resizeMessage",
//					"Dear " + username + ", please select plan which has RAM size greater than your previous RAM size");
//			return "redirect:/cloud_instance/VM/" + obj.getId();
//		} else if (!(Integer.valueOf(obj.getPrice_id().getvCpu().replaceAll("\\D+", "")) >= Integer
//				.valueOf(entity.getPrice_id().getvCpu().replaceAll("\\D+", "")))) {
//			redirectAttributes.addFlashAttribute("resizeMessage", "Dear " + username
//					+ ", please select plan which has CPU core greater than your previous number of CPU core");
//			return "redirect:/cloud_instance/VM/" + obj.getId();
//		}
//
//		else {
//			System.out.println("SuperAdmin- resize else condtiomn");
//
//	
//			String virtType = obj.getVirtualization_type();
//			String serverIP = obj.getPhysicalServerIP();
//
//
//			try {
//				CloudInstanceLog log = new CloudInstanceLog();
//				log.setInstance_ip(entity.getInstance_ip());
//				log.setInstance_name(entity.getInstance_name());
//				log.setInstance_password(entity.getInstance_password());
//				log.setDisk_path(entity.getDisk_path());
//				log.setGeneration_type(entity.getGeneration_type());
//				log.setIso_file_path(entity.getIso_file_path());
//				log.setVm_location_path(entity.getVm_location_path());
//				log.setLocation_id(entity.getLocation_id());
//				log.setPrice_id(obj.getPrice_id());
//				log.setSecurity_group_id(entity.getSecurity_group_id());
//				log.setSubproduct_id(entity.getSubproduct_id());
//				log.setSwitch_id(entity.getSwitch_id());
//				log.setVpc_id(entity.getVpc_id());
//				log.setRequest_type("instance update");
//				CloudInstanceLog savedLog = cloudInstanceLogRepository.save(log);
//
//				String vm_name = savedLog.getInstance_name();
//				String ram = savedLog.getPrice_id().getRam();
//				String cpu = savedLog.getPrice_id().getvCpu();
//				String ssd_size = savedLog.getPrice_id().getSsd_disk();
//				String disk_path = savedLog.getDisk_path();
//
//				System.out.println("VM = " + vm_name + "\nRAM = " + ram + "\nCPU = " + cpu + "\nSSD = " + ssd_size);
//
//				System.out.println("Physical Server IP = " + entity.getPhysicalServerIP());
//
//				Socket socket = new Socket(entity.getPhysicalServerIP(), 9005);
//
//				VMCreationBean bean = new VMCreationBean();
//				bean.setActivity("vm_update");
//				bean.setInstanceName(vm_name);
//				bean.setvCpu(cpu);
//				bean.setMemoryStartupBytes(ram);
//				bean.setVhdPath(disk_path);
//				bean.setNewVHDSizeBytes(ssd_size);
//
//				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
//				outputStream.writeObject(bean);
//				outputStream.flush();
//				ObjectInputStream serverResponse = new ObjectInputStream(socket.getInputStream());
//				responce_data = (String) serverResponse.readObject();
//				System.out.println("Server response: " + responce_data);
//
//				if (!responce_data.contains("Error")) {
//					System.out.println("in save");
//					entity.setPrice_id(savedLog.getPrice_id());
//					repository.save(entity);
//					redirectAttributes.addFlashAttribute("updateVMCommandOP", "success");
//				} else {
//					redirectAttributes.addFlashAttribute("updateVMCommandOP", "fail");
//				}
//
//			} catch (Exception e) {
//				System.out.println("Exception occured while updating vm = " + e);
//				redirectAttributes.addFlashAttribute("updateVMCommandOP", "fail");
//			}
//
//			return "redirect:/cloud_instance/view";
//
//		}
//	}

	// Additional Storage for Admin and Super Admin
	@GetMapping("/additionalStorageRequestSuper")
	public @ResponseBody String additionalStorageRequestSuper(@RequestParam String instance_id,
			@RequestParam String disk_path, @RequestParam String disk_size, Principal principal) {

		String responce_data = null;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();
		AppUser user = userRepository.findByUsername(username);
		System.out.println("data for external storage = " + instance_id + " - " + disk_path + " - " + disk_size);

		try {

			CloudInstance entity = repository.findById(Integer.valueOf(instance_id)).get();

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
			System.out.println("Physical Server IP = " + entity.getPhysicalServerIP());

			Socket socket = new Socket(entity.getPhysicalServerIP(), 9005);

			VMCreationBean bean = new VMCreationBean();
			bean.setActivity("vm_additionalStorage");
			bean.setInstanceName(entity.getInstance_name());
			bean.setVhdPath(disk_path);
			bean.setNewVHDSizeBytes(disk_size);

			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
			outputStream.writeObject(bean);
			outputStream.flush();
			ObjectInputStream serverResponse = new ObjectInputStream(socket.getInputStream());
			responce_data = (String) serverResponse.readObject();
			System.out.println("Server response: " + responce_data);

			if (responce_data.equalsIgnoreCase("success")) {

				AdditionalStorage storage = new AdditionalStorage();
				storage.setInstance_id(entity);
				storage.setPrice(Integer.valueOf(disk_size) * 9);
				storage.setStorage_size(disk_size);
				storage.setStoragePath(disk_path);
				storage.setStatus("Approved");
				externalStorageRepository.save(storage);
			}

			socket.close();

		} catch (Exception e) {
			System.out.println("Exception occured while updating additional storage of VM = " + e);
			responce_data = "fail";
		}

		return responce_data;
	}

	@GetMapping("/AlertDashboard")
	public ModelAndView AlertDashboard(Principal principal) {
		ModelAndView mav = new ModelAndView("alertDashboard");
		mav.addObject("pageTitle", "Add New " + "Alert Details");
		mav.addObject("action_name", "Alert");
		mav.addObject("fdate", "blank");
		mav.addObject("tdate", "blank");
//		mav.addObject("dcLocationList", repositoryLocation.getAllDClocations());
//		// mav.addObject("vpcList", repositoryVPC.getAllVPC());
//		mav.addObject("securityGroupList", firewallRepository.getFirewall());
//		mav.addObject("sharedCpuPlan", priceRepository.getSharedCpuPlan());
//		mav.addObject("dedicatedCpuPlan", priceRepository.getDedicatedCpuPlan());
//		mav.addObject("highMemoryPlan", priceRepository.getHighMemoryPlan());
//		mav.addObject("switchList", switchRepository.getAllSwitch());
//		mav.addObject("physicalServerIPList", PhysicalServerRepository.getPhysicalServerIPs());
//		CloudInstance objEnt = new CloudInstance();
//		mav.addObject("objEnt", objEnt);

		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();
		List<String> instances = null;

		AppUser obj = appRepository.findOneByUserName(username);
		List<String> groupName = new ArrayList<>();
		StringTokenizer token = new StringTokenizer(obj.getGroupName(), ",");
		while (token.hasMoreTokens()) {
			groupName.add(token.nextToken());
		}

		boolean isSuperAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));
		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
		boolean isUser = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"));

		if (isSuperAdmin) {

			instances = repository.findByIsMonitoringOrderByIdDescOnlyVM(true);

		} else if (isAdmin) {

			System.out.println("Inoperator groupName = " + groupName);

			instances = repository.findByIsMonitoringAndGroupNameOrderByIdDescOnlyVM(true, groupName);

		} else {
			List<Integer> li = approvalRepository.findByRequesterNameCustom(username);

			System.out.println(li.toString());
			instances = repository.findByidInAndIsMonitoringOnlyVm(li, true);
		}
//		System.out.println(instances);
		List<AlertDash> AlertList = new ArrayList<>();

//		List<CloudInstanceMemoryThresholdHistory> ThreshHist = memoryThresholdHistoryRepository.findAll();
//		Pageable pageable1 = PageRequest.of(0, 5);
		List<CloudInstanceMemoryThresholdHistory> ThreshHist = memoryThresholdHistoryRepository
				.findCurrentMonthData(instances);
		for (CloudInstanceMemoryThresholdHistory ThreshObj : ThreshHist) {
			AlertDash alertnewobj = new AlertDash();
			alertnewobj.setAlert_Name("MEMORY THRESHOLD");
			alertnewobj.setAlert_STATUS(ThreshObj.getMemoryStatus());
			alertnewobj.setDifference(ThreshObj.getMemoryUtilization() - ThreshObj.getMemoryThreshold());
			alertnewobj.setEVENT_TIMESTAMP(ThreshObj.getEventTimestamp());
			alertnewobj.setMEMORY_THRESHOLD(ThreshObj.getMemoryThreshold());
			alertnewobj.setMEMORY_UTILIZATION(ThreshObj.getMemoryUtilization());
			alertnewobj.setPhysicalServer_ip(ThreshObj.getNodeIp());
			alertnewobj.setVM_Name(ThreshObj.getVmName());

			AlertList.add(alertnewobj);

		}

//		List<CloudInstanceCpuThresholdHistory> ThreshCPUHist = cpuThresholdHistoryRepository.findAll();

		List<CloudInstanceCpuThresholdHistory> ThreshCPUHist = cpuThresholdHistoryRepository
				.findCurrentMonthData(instances);

		for (CloudInstanceCpuThresholdHistory ThreshCPUObj : ThreshCPUHist) {
			AlertDash alertnewobj = new AlertDash();
			alertnewobj.setAlert_Name("CPU THRESHOLD");
			alertnewobj.setAlert_STATUS(ThreshCPUObj.getCpuStatus());
			alertnewobj.setDifference(ThreshCPUObj.getCpuUtilization() - ThreshCPUObj.getCpuThreshold());
			alertnewobj.setEVENT_TIMESTAMP(ThreshCPUObj.getEventTimestamp());
			alertnewobj.setMEMORY_THRESHOLD(ThreshCPUObj.getCpuThreshold());
			alertnewobj.setMEMORY_UTILIZATION(ThreshCPUObj.getCpuUtilization());
			alertnewobj.setPhysicalServer_ip(ThreshCPUObj.getNodeIp());
			alertnewobj.setVM_Name(ThreshCPUObj.getVmName());

			AlertList.add(alertnewobj);

		}

		List<AlertDash> sortedAlertList = AlertList.stream()
				.sorted(Comparator.comparing(AlertDash::getEVENT_TIMESTAMP).reversed()).collect(Collectors.toList());

		mav.addObject("AlertListObj", sortedAlertList);

		return mav;
	}

	// Docker Container
	@PostMapping("/docker")
	public @ResponseBody String docker(@RequestParam("scenarioId") String scenarioId, Principal principal) {
		System.out.println("Inside_Docker_Method :::: ");
		System.out.println("scenarioId :::: " + scenarioId);
		String result = null;
		Map<Integer, Integer> portMappings = null;

		String network = null;
		Integer newVncPort;
		Integer newNoVncPort;
		Integer newRdpPort;

		String containerIP = null;

		String jsonResponse = null;

		String username = ((User) ((Authentication) principal).getPrincipal()).getUsername();

		String scenarioName = null;

		try {

			List<ScenarioLabTemplate> templates = scenarioLabTemplateRepository
					.findByScenarioId(Integer.valueOf(scenarioId));

			System.out.println("rDokcer_templates : " + templates);
			for (ScenarioLabTemplate temp : templates) {

				System.out.println("Inside_Dokcer_templates : " + templates);

				Optional<CloudInstance> obj = repository.findById(temp.getTemplateId());
				CloudInstance instance = obj.get();

				String templateName = instance.getInstance_name();
				String os = instance.getSubproduct_id().getProduct_id().getProduct_name();
				String filePath = instance.getSubproduct_id().getIso_file_path();
				File file = new File(filePath);
				String imageName = instance.getSubproduct_id().getVariant();
				int templateId = instance.getProxmoxTemplateId();
				scenarioName = temp.getScenarioName();

				System.out.println("imageNameimageName ::::" + imageName);

				Integer maxLabId1 = userLabRepository.findMaxLabId();

				int maxLabId = (maxLabId1 != null) ? maxLabId1 : 0;

				maxLabId++;

				String newInstanceName = instance.getInstance_name() + maxLabId;
				System.out.println("Inside__newInstanceName : " + newInstanceName);

				String newIp = proxmoxAssignedIpAddressRepository.findMaxIp();

				if (instance.getVirtualization_type().equalsIgnoreCase("Proxmox")) {

					Map<String, Object> resp = proxmoxService.cloneVm(templateId, newInstanceName, filePath,
							instance.getPhysicalServerIP(), proxmoxService.getNextIp(newIp));
					System.out.println("Proxmox vm creation output : " + resp.toString());
					if ("fail".equalsIgnoreCase((String) resp.get("status"))) {
						result = "fail";
					} else {

						jsonResponse = guacService.createConnection(newInstanceName, "rdp",
								proxmoxService.getNextIp(newIp), 3389, "administrator", instance.getInstance_password(),
								"", "true", "", "", "", "", "", "", "", "", "");
						if (guacService.getConnectionIdByName(jsonResponse) != null) {
							// insertIntoPortDetailsForWindows(newInstanceName, newRdpPort, newNoVncPort);
							saveInUserLab(newInstanceName, username, instance.getConsoleProtocol(),
									instance.getInstance_name(), guacService.getConnectionIdByName(jsonResponse), 0, 0,
									instance.getInstance_password(), resp.get("ip").toString(), scenarioId);

							insertUserWiseChatBoatInstruction(temp.getTemplateId(), instance.getInstance_name(),
									newInstanceName, username, scenarioId);

							saveIpAddress(newInstanceName, resp.get("ip").toString());

						}

						result = "success";
					}

				}

				else {

					portMappings = new HashMap<>();
					newVncPort = portDetailsRepository.findMaxVncPorts() + 1;
					newNoVncPort = portDetailsRepository.findMaxnoVncPort() + 1;
					newRdpPort = portDetailsRepository.findMaxRdpPort() + 1;

					if (os.equalsIgnoreCase("Windows")) {
						portMappings.put(newRdpPort, 3389);
					}

					else {
						portMappings.put(newVncPort, 5901);
					}

					portMappings.put(newNoVncPort, 8080);

					network = instance.getDocker_network_name();

					if (os.equalsIgnoreCase("windows")) {
						result = dockerService.runWindowsContainer(imageName, newInstanceName, portMappings, network,
								filePath);
						if (result.equalsIgnoreCase("success")) {
							System.out.println("inside windows docker : " + result);
							jsonResponse = guacService.createConnection(newInstanceName, "rdp",
									instance.getPhysicalServerIP(), newRdpPort, "admin", "Admin@123!", "", "true", "",
									"", "", "", "", "", "", "", "");
							if (guacService.getConnectionIdByName(jsonResponse) != null) {
								containerIP = dockerService.getContainerIpViaCli(newInstanceName);
								insertIntoPortDetailsForWindows(newInstanceName, newRdpPort, newNoVncPort);
								insertIntoUserLabForWindows(newInstanceName, username, "rdp", templateName,
										guacService.getConnectionIdByName(jsonResponse), newRdpPort, newNoVncPort,
										"Admin@123!", "admin", containerIP, scenarioId);

								insertUserWiseChatBoatInstruction(temp.getTemplateId(), templateName, newInstanceName,
										username, scenarioId);
							}

						}

					} else {

						result = dockerService.runContainer(imageName, newInstanceName, portMappings, network);

						if (result.equalsIgnoreCase("success")) {
							System.out.println("inside linux docker : " + result);
							// Create connection in Guacamole
							jsonResponse = guacService.createConnection(newInstanceName, "vnc",
									instance.getPhysicalServerIP(), newVncPort, "kali", "kalilinux", "", "", "", "", "",
									"", "", "", "", "", "");
							if (guacService.getConnectionIdByName(jsonResponse) != null) {
								containerIP = dockerService.getContainerIpViaCli(newInstanceName);
								insertIntoPortDetails(newInstanceName, newVncPort, newNoVncPort);
								insertIntoUserLab(newInstanceName, username, "vnc", templateName,
										guacService.getConnectionIdByName(jsonResponse), newVncPort, newNoVncPort,
										"kalilinux", containerIP, scenarioId);

								insertUserWiseChatBoatInstruction(temp.getTemplateId(), templateName, newInstanceName,
										username, scenarioId);
							}

						}
					}

				}

			}
			if (result.equalsIgnoreCase("success")) {
				insertUserScenerio(scenarioId, scenarioName, username);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception_Dokcer_method : " + e);
			result = "fail";
		}

		return result;

	}

	public void insertIntoUserLab(String newInstanceName, String username, String remoteType, String templateName,
			String guacamoleId, Integer newVncPort, Integer newNoVncPort, String password, String containerIP,
			String scenarioId) {
		UserLab lab = new UserLab();
		lab.setInstanceName(newInstanceName);
		lab.setGuacamoleId(Integer.valueOf(guacamoleId));
		lab.setUsername(username);
		lab.setPassword(password);
		lab.setInstanceUser("kali");
		lab.setRemoteType(remoteType);
		lab.setNoVncPort(newNoVncPort);
		lab.setVncPort(newVncPort);
		lab.setIpAddress(containerIP);
		lab.setVmState("running");
		lab.setStatus("InProgress");
		lab.setTemplateName(templateName);
		lab.setScenarioId(Integer.valueOf(scenarioId));
		lab.setLastActiveConnection(new Timestamp(System.currentTimeMillis()));

		userLabRepository.save(lab);

	}

	public void saveInUserLab(String newInstanceName, String username, String remoteType, String templateName,
			String guacamoleId, Integer newVncPort, Integer newNoVncPort, String password, String containerIP,
			String scenarioId) {
		UserLab lab = new UserLab();
		lab.setInstanceName(newInstanceName);
		lab.setGuacamoleId(Integer.valueOf(guacamoleId));
		lab.setUsername(username);
		lab.setPassword(password);
		lab.setInstanceUser("administrator");
		lab.setRemoteType(remoteType);
		lab.setNoVncPort(newNoVncPort);
		lab.setVncPort(newVncPort);
		lab.setIpAddress(containerIP);
		lab.setStatus("InProgress");
		lab.setVmState("running");
		lab.setTemplateName(templateName);
		lab.setScenarioId(Integer.valueOf(scenarioId));
		lab.setLastActiveConnection(new Timestamp(System.currentTimeMillis()));

		userLabRepository.save(lab);

	}

	public void insertIntoUserLabForWindows(String newInstanceName, String username, String remoteType,
			String templateName, String guacamoleId, Integer newRdpPort, Integer newNoVncPort, String password,
			String instanceuser, String containerIP, String scenarioId) {
		UserLab lab = new UserLab();
		lab.setInstanceName(newInstanceName);
		lab.setGuacamoleId(Integer.valueOf(guacamoleId));
		lab.setUsername(username);
		lab.setPassword(password);
		lab.setInstanceUser(instanceuser);
		lab.setRemoteType(remoteType);
		lab.setNoVncPort(newNoVncPort);
		lab.setVncPort(newRdpPort);
		lab.setIpAddress(containerIP);
		lab.setVmState("running");
		lab.setStatus("InProgress");
		lab.setTemplateName(templateName);
		lab.setScenarioId(Integer.valueOf(scenarioId));
		lab.setLastActiveConnection(new Timestamp(System.currentTimeMillis()));

		userLabRepository.save(lab);

	}

	public void insertIntoPortDetails(String newInstanceName, Integer newVncPort, Integer newNoVncPort) {
		PortDetails port = new PortDetails();
		port.setVmName(newInstanceName);
		port.setNoVncPort(newNoVncPort);
		port.setVncPort(newVncPort);
		portDetailsRepository.save(port);

	}

	public void saveIpAddress(String newInstanceName, String ip) {
		ProxmoxAssignedIpAddress obj = new ProxmoxAssignedIpAddress();
		obj.setIpAddress(ip);
		obj.setVm(newInstanceName);
		proxmoxAssignedIpAddressRepository.save(obj);
	}

	public void insertIntoPortDetailsForWindows(String newInstanceName, Integer newRdpPort, Integer newNoVncPort) {
		PortDetails port = new PortDetails();
		port.setVmName(newInstanceName);
		port.setNoVncPort(newNoVncPort);
		port.setRdpPort(newRdpPort);
		portDetailsRepository.save(port);

	}

	public void insertUserScenerio(String scenarioId, String scenarioName, String username) {
		UserScenario us = new UserScenario();
		us.setScenarioId(scenarioId);
		us.setScenarioName(scenarioName);
		us.setStatus("Start");
		us.setUsername(username);
		UserScenerioRepository.save(us);

	}

	public void insertUserWiseChatBoatInstruction(int templateId, String templateName, String LabName, String username,
			String scenarioId) {

		try {

			List<ChartBoatInstructionTemplate> templates = ChartBoatInstructionTemplateRepository
					.findBytemplateId(templateId);
			Integer labid = userLabRepository.findMaxLabId();

			if (!templates.isEmpty()) {

				for (ChartBoatInstructionTemplate template : templates) {

					UserWiseChatBoatInstructionTemplate userWiseTemplate = new UserWiseChatBoatInstructionTemplate();

					userWiseTemplate.setTemplateId(template.getTemplateId());
					userWiseTemplate.setTemaplateName(template.getTemaplateName());
					userWiseTemplate.setInstructionCommand(template.getInstructionCommand());
					userWiseTemplate.setInstructionDetails(template.getInstructionDetails());

					userWiseTemplate.setLabId(labid);
					userWiseTemplate.setLabName(LabName);

					userWiseTemplate.setUsername(username);
					userWiseTemplate.setIsCommandExecuted("false");
					userWiseTemplate.setCommandExecutedCheckTime(new Timestamp(System.currentTimeMillis()));
					userWiseTemplate.setScenarioId(Integer.parseInt(scenarioId));

					instructionTemplateRepository.save(userWiseTemplate);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Expltion_insert_chartnbordOnstruion" + e);
			// TODO: handle exception
		}

	}

	// Assesstemnt

	@PostMapping("/assessment")
	public @ResponseBody String assessmentDocker(@RequestParam("scenarioId") String scenarioId, Principal principal) {
		System.out.println("Inside_Assessment_Docker_Method :::: ");
		System.out.println("scenarioId :::: " + scenarioId);
		String result = null;
		Map<Integer, Integer> portMappings = null;

		String network = null;
		Integer newVncPort;
		Integer newNoVncPort;
		Integer newRdpPort;

		String containerIP = null;

		String jsonResponse = null;

		String username = ((User) ((Authentication) principal).getPrincipal()).getUsername();

		String scenarioName = null;

		try {

			List<ScenarioLabTemplate> templates = scenarioLabTemplateRepository
					.findByScenarioId(Integer.valueOf(scenarioId));

			System.out.println("rDokcer_templates : " + templates);
			for (ScenarioLabTemplate temp : templates) {

				System.out.println("Inside_Dokcer_templates : " + templates);

				Optional<CloudInstance> obj = repository.findById(temp.getTemplateId());
				CloudInstance instance = obj.get();

				String templateName = instance.getInstance_name();
				String os = instance.getSubproduct_id().getProduct_id().getProduct_name();
				String filePath = instance.getSubproduct_id().getIso_file_path();
				File file = new File(filePath);
				String imageName = instance.getSubproduct_id().getVariant();
				int templateId = instance.getProxmoxTemplateId();
				scenarioName = temp.getScenarioName();

				System.out.println("imageNameimageName ::::" + imageName);

				Integer maxLabId1 = assessmentUserLabRepository.findMaxLabId();

				int maxLabId = (maxLabId1 != null) ? maxLabId1 : 0;

				maxLabId++;

				String newInstanceName = instance.getInstance_name() + "Assessment" + maxLabId;
				System.out.println("Inside__newInstanceName : " + newInstanceName);

				String newIp = proxmoxAssignedIpAddressRepository.findMaxIp();

				if (instance.getVirtualization_type().equalsIgnoreCase("Proxmox")) {

					Map<String, Object> resp = proxmoxService.cloneVm(templateId, newInstanceName, filePath,
							instance.getPhysicalServerIP(), proxmoxService.getNextIp(newIp));
					System.out.println("Proxmox vm creation output : " + resp.toString());
					if ("fail".equalsIgnoreCase((String) resp.get("status"))) {
						result = "fail";
					} else {

						jsonResponse = guacService.createConnection(newInstanceName, "rdp",
								proxmoxService.getNextIp(newIp), 3389, "administrator", instance.getInstance_password(),
								"", "true", "", "", "", "", "", "", "", "", "");
						if (guacService.getConnectionIdByName(jsonResponse) != null) {
							saveInAssessmentUserLab(newInstanceName, username, instance.getConsoleProtocol(),
									instance.getInstance_name(), guacService.getConnectionIdByName(jsonResponse), 0, 0,
									instance.getInstance_password(), resp.get("ip").toString(), scenarioId);

							insertAssessmentUserWiseChatBoatInstruction(temp.getTemplateId(),
									instance.getInstance_name(), newInstanceName, username, scenarioId);

							saveIpAddress(newInstanceName, resp.get("ip").toString());

						}

						result = "success";
					}

				}

				else {

					portMappings = new HashMap<>();
					newVncPort = portDetailsRepository.findMaxVncPorts() + 1;
					newNoVncPort = portDetailsRepository.findMaxnoVncPort() + 1;
					newRdpPort = portDetailsRepository.findMaxRdpPort() + 1;

					if (os.equalsIgnoreCase("Windows")) {
						portMappings.put(newRdpPort, 3389);
					}

					else {
						portMappings.put(newVncPort, 5901);
					}

					portMappings.put(newNoVncPort, 8080);

					network = instance.getDocker_network_name();

					if (os.equalsIgnoreCase("windows")) {
						result = dockerService.runWindowsContainer(imageName, newInstanceName, portMappings, network,
								filePath);
						if (result.equalsIgnoreCase("success")) {
							System.out.println("inside windows docker : " + result);
							jsonResponse = guacService.createConnection(newInstanceName, "rdp",
									instance.getPhysicalServerIP(), newRdpPort, "admin", "Admin@123!", "", "true", "",
									"", "", "", "", "", "", "", "");
							if (guacService.getConnectionIdByName(jsonResponse) != null) {
								containerIP = dockerService.getContainerIpViaCli(newInstanceName);
								insertIntoPortDetailsForWindows(newInstanceName, newRdpPort, newNoVncPort);
								insertIntoAssessmentUserLabForWindows(newInstanceName, username, "rdp", templateName,
										guacService.getConnectionIdByName(jsonResponse), newRdpPort, newNoVncPort,
										"Admin@123!", "admin", containerIP, scenarioId);

								insertAssessmentUserWiseChatBoatInstruction(temp.getTemplateId(), templateName,
										newInstanceName, username, scenarioId);
							}

						}

					} else {

						result = dockerService.runContainer(imageName, newInstanceName, portMappings, network);

						if (result.equalsIgnoreCase("success")) {
							System.out.println("inside linux docker : " + result);
							// Create connection in Guacamole
							jsonResponse = guacService.createConnection(newInstanceName, "vnc",
									instance.getPhysicalServerIP(), newVncPort, "kali", "kalilinux", "", "", "", "", "",
									"", "", "", "", "", "");
							if (guacService.getConnectionIdByName(jsonResponse) != null) {
								containerIP = dockerService.getContainerIpViaCli(newInstanceName);
								insertIntoPortDetails(newInstanceName, newVncPort, newNoVncPort);
								insertIntoAssessmentUserLab(newInstanceName, username, "vnc", templateName,
										guacService.getConnectionIdByName(jsonResponse), newVncPort, newNoVncPort,
										"kalilinux", containerIP, scenarioId);

								insertAssessmentUserWiseChatBoatInstruction(temp.getTemplateId(), templateName,
										newInstanceName, username, scenarioId);
							}

						}
					}

				}

			}
			if (result.equalsIgnoreCase("success")) {
				insertUserScenerio(scenarioId, scenarioName, username);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception_Assessment_Dokcer_method : " + e);
			result = "fail";
		}

		return result;
	}

	public void insertIntoAssessmentUserLab(String newInstanceName, String username, String remoteType,
			String templateName, String guacamoleId, Integer newVncPort, Integer newNoVncPort, String password,
			String containerIP, String scenarioId) {
		AssessmentUserLab lab = new AssessmentUserLab();
		lab.setInstanceName(newInstanceName);
		lab.setGuacamoleId(Integer.valueOf(guacamoleId));
		lab.setUsername(username);
		lab.setPassword(password);
		lab.setInstanceUser("kali");
		lab.setRemoteType(remoteType);
		lab.setNoVncPort(newNoVncPort);
		lab.setVncPort(newVncPort);
		lab.setIpAddress(containerIP);
		lab.setVmState("running");
		lab.setStatus("InProgress");
		lab.setTemplateName(templateName);
		lab.setScenarioId(Integer.valueOf(scenarioId));
		lab.setLastActiveConnection(new Timestamp(System.currentTimeMillis()));

		assessmentUserLabRepository.save(lab);
	}

	public void saveInAssessmentUserLab(String newInstanceName, String username, String remoteType, String templateName,
			String guacamoleId, Integer newVncPort, Integer newNoVncPort, String password, String containerIP,
			String scenarioId) {
		AssessmentUserLab lab = new AssessmentUserLab();
		lab.setInstanceName(newInstanceName);
		lab.setGuacamoleId(Integer.valueOf(guacamoleId));
		lab.setUsername(username);
		lab.setPassword(password);
		lab.setInstanceUser("administrator");
		lab.setRemoteType(remoteType);
		lab.setNoVncPort(newNoVncPort);
		lab.setVncPort(newVncPort);
		lab.setIpAddress(containerIP);
		lab.setStatus("InProgress");
		lab.setVmState("running");
		lab.setTemplateName(templateName);
		lab.setScenarioId(Integer.valueOf(scenarioId));
		lab.setLastActiveConnection(new Timestamp(System.currentTimeMillis()));

		assessmentUserLabRepository.save(lab);
	}

	public void insertIntoAssessmentUserLabForWindows(String newInstanceName, String username, String remoteType,
			String templateName, String guacamoleId, Integer newRdpPort, Integer newNoVncPort, String password,
			String instanceuser, String containerIP, String scenarioId) {
		AssessmentUserLab lab = new AssessmentUserLab();
		lab.setInstanceName(newInstanceName);
		lab.setGuacamoleId(Integer.valueOf(guacamoleId));
		lab.setUsername(username);
		lab.setPassword(password);
		lab.setInstanceUser(instanceuser);
		lab.setRemoteType(remoteType);
		lab.setNoVncPort(newNoVncPort);
		lab.setVncPort(newRdpPort);
		lab.setIpAddress(containerIP);
		lab.setVmState("running");
		lab.setStatus("InProgress");
		lab.setTemplateName(templateName);
		lab.setScenarioId(Integer.valueOf(scenarioId));
		lab.setLastActiveConnection(new Timestamp(System.currentTimeMillis()));

		assessmentUserLabRepository.save(lab);
	}

	public void insertAssessmentUserWiseChatBoatInstruction(int templateId, String templateName, String LabName,
			String username, String scenarioId) {

		try {

			List<ChartBoatInstructionTemplate> templates = ChartBoatInstructionTemplateRepository
					.findBytemplateId(templateId);
			Integer labid = assessmentUserLabRepository.findMaxLabId();

			if (!templates.isEmpty()) {

				for (ChartBoatInstructionTemplate template : templates) {

					AssessmentUserWiseChatBoatInstructionTemplate userWiseTemplate = new AssessmentUserWiseChatBoatInstructionTemplate();

					userWiseTemplate.setTemplateId(template.getTemplateId());
					userWiseTemplate.setTemaplateName(template.getTemaplateName());
					userWiseTemplate.setInstructionCommand(template.getInstructionCommand());
					userWiseTemplate.setInstructionDetails(template.getInstructionDetails());

					userWiseTemplate.setLabId(labid);
					userWiseTemplate.setLabName(LabName);

					userWiseTemplate.setUsername(username);
					userWiseTemplate.setIsCommandExecuted("false");
					userWiseTemplate.setCommandExecutedCheckTime(new Timestamp(System.currentTimeMillis()));
					userWiseTemplate.setScenarioId(Integer.parseInt(scenarioId));

					assessmentInstructionTemplateRepository.save(userWiseTemplate);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception_insert_assessment_chartboardInstruction" + e);
		}
	}

	@PostMapping("/sourceImage")
	public @ResponseBody String sourceImage(@RequestParam("templateId") int templateId) {
		try {

			CloudInstance obj = repository.findById(templateId).get();
			String filePath = obj.getSubproduct_id().getIso_file_path();
			File file = new File(filePath);
			String imageName = file.getName().replaceFirst("[.][^.]+$", "");
			if (dockerService.loadImageFromTar(filePath, imageName).equalsIgnoreCase("success")) {
				try {
					subProductRepository.updateSourceImage(obj.getSubproduct_id().getId());
				} catch (Exception e) {
					e.printStackTrace();
					System.out.print("Exception DB update source image :" + e.getMessage());
				}
				return "success";
			} else {
				return "fail";
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}

	}

	@GetMapping("/template_edit/{id}")
	public String showEditForm(@PathVariable("id") int id, Model model) {
		try {
			Optional<CloudInstance> cloudInstanceOptional = repository.findById(id);

			if (cloudInstanceOptional.isPresent()) {
				CloudInstance cloudInstance = cloudInstanceOptional.get();
				model.addAttribute("cloudInstance", cloudInstance);
				model.addAttribute("pageTitle", "Edit Template");
				return "edit_template";
			} else {
				return "redirect:/cloud_instance/view?error=Template+not+found";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/cloud_instance/view?error=Error+loading+template";
		}
	}

	@PostMapping("/updateTemplate")
	public String updateTemplate(@ModelAttribute CloudInstance cloudInstance,
			@RequestParam(value = "uploadedImage", required = false) MultipartFile uploadedImage,
			@RequestParam(value = "removeImage", defaultValue = "false") boolean removeImage,
			RedirectAttributes redirectAttributes) {

		try {
			// Get existing template to preserve all data
			Optional<CloudInstance> existingTemplate = repository.findById(cloudInstance.getId());

			if (existingTemplate.isPresent()) {
				CloudInstance existing = existingTemplate.get();

				// Only update fields that are provided/not null
				if (cloudInstance.getInstance_name() != null && !cloudInstance.getInstance_name().trim().isEmpty()) {
					existing.setInstance_name(cloudInstance.getInstance_name());
				}

				if (cloudInstance.getLab_tag() != null && !cloudInstance.getLab_tag().trim().isEmpty()) {
					existing.setLab_tag(cloudInstance.getLab_tag());
				}

				if (cloudInstance.getConsoleUsername() != null
						&& !cloudInstance.getConsoleUsername().trim().isEmpty()) {
					existing.setConsoleUsername(cloudInstance.getConsoleUsername());
				}

				// Only update password if provided (not blank)
				if (cloudInstance.getConsolePassword() != null
						&& !cloudInstance.getConsolePassword().trim().isEmpty()) {
					existing.setConsolePassword(cloudInstance.getConsolePassword());
				}

				if (cloudInstance.getConsoleProtocol() != null
						&& !cloudInstance.getConsoleProtocol().trim().isEmpty()) {
					existing.setConsoleProtocol(cloudInstance.getConsoleProtocol());
				}

				if (cloudInstance.getSecurityMode() != null && !cloudInstance.getSecurityMode().trim().isEmpty()) {
					existing.setSecurityMode(cloudInstance.getSecurityMode());
				}

				if (cloudInstance.getServerCertificate() != null) {
					existing.setServerCertificate(cloudInstance.getServerCertificate());
				}

				if (cloudInstance.getDescription() != null && !cloudInstance.getDescription().trim().isEmpty()) {
					existing.setDescription(cloudInstance.getDescription());
				}

				if (cloudInstance.getVirtualization_type() != null
						&& !cloudInstance.getVirtualization_type().trim().isEmpty()) {
					existing.setVirtualization_type(cloudInstance.getVirtualization_type());
				}

				if (cloudInstance.getPhysicalServerIP() != null
						&& !cloudInstance.getPhysicalServerIP().trim().isEmpty()) {
					existing.setPhysicalServerIP(cloudInstance.getPhysicalServerIP());
				}

				// Handle image upload/removal
				if (removeImage) {
					// Remove current image
					existing.setLab_image(null);
				} else if (uploadedImage != null && !uploadedImage.isEmpty()) {
					// Handle new image upload
					if (!uploadedImage.getContentType().startsWith("image/")) {
						redirectAttributes.addFlashAttribute("errorMessage", "Please upload a valid image file");
						return "redirect:/cloud_instance/edit/" + cloudInstance.getId();
					}

					// Check file size (max 5MB)
					if (uploadedImage.getSize() > 5 * 1024 * 1024) {
						redirectAttributes.addFlashAttribute("errorMessage", "Image size should be less than 5MB");
						return "redirect:/cloud_instance/edit/" + cloudInstance.getId();
					}

					existing.setLab_image(uploadedImage.getBytes());
				}
				// If no image change, keep the existing image

				// Update the template
				repository.save(existing);

				redirectAttributes.addFlashAttribute("successMessage", "Template updated successfully");
				return "redirect:/cloud_instance/view";
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "Template not found");
				return "redirect:/cloud_instance/view";
			}

		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage", "Error updating template: " + e.getMessage());
			return "redirect:/cloud_instance/edit/" + cloudInstance.getId();
		}
	}

	@GetMapping("/syncContainers")
	public @ResponseBody String fetchAndSyncContainers(@RequestParam String serverIp) {
		try {

			System.out.println("Sync Docker containers ...!");

			@SuppressWarnings("unchecked")
			Map<String, Object> response = restTemplate
					.getForObject(physical_server_agent_api_host.replace("<physicalServerIp>", serverIp), Map.class);

			if (response == null || !"success".equals(response.get("status")))
				return "fail";

			@SuppressWarnings("unchecked")
			List<Map<String, Object>> containers = (List<Map<String, Object>>) response.get("containers");

			if (containers == null || containers.isEmpty())
				return "fail";

			for (Map<String, Object> c : containers) {

				if (c.containsKey("error"))
					continue;

				String containerId = (String) c.get("id");
				DiscoverDockerContainers container = discoverDockerContainersRepository.findByContainerId(containerId);
				if (container == null) {
					container = new DiscoverDockerContainers();
					container.setContainerId(containerId);
				}

				container.setContainerName((String) c.get("name"));
				container.setImageName((String) c.get("image"));
				container.setCommand((String) c.get("command"));
				container.setStatus((String) c.get("status"));
				container.setState((String) c.get("state"));
				container.setPorts((String) c.get("ports"));
				container.setServices((String) c.get("services"));
				container.setPhysicalServerIp(serverIp);

				if (c.get("created") != null) {
					Object createdObj = c.get("created");
					Timestamp ts = null;

					if (createdObj instanceof String) {
						try {
							ts = new Timestamp(dateFormat.parse((String) createdObj).getTime());
						} catch (Exception e) {
							ts = new Timestamp(System.currentTimeMillis()); // fallback
						}
					} else if (createdObj instanceof Date) {
						ts = new Timestamp(((Date) createdObj).getTime());
					}

					container.setCreated(ts);
				}

				discoverDockerContainersRepository.save(container);
			}

			return "success";

		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}

}
