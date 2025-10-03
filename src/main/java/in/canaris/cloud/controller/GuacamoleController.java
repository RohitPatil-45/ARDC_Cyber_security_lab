package in.canaris.cloud.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.glassfish.jersey.internal.inject.ParamConverters.TypeValueOf;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.canaris.cloud.entity.AddPhysicalServer;
import in.canaris.cloud.entity.AppUser;
import in.canaris.cloud.entity.CloudInstance;
import in.canaris.cloud.entity.Discount;
import in.canaris.cloud.entity.Group;
import in.canaris.cloud.entity.PlaylistScenario;
import in.canaris.cloud.entity.PlaylistScenarioId;
import in.canaris.cloud.entity.SubProduct;
import in.canaris.cloud.openstack.entity.UserPlaylistMapping;
import in.canaris.cloud.openstack.entity.Add_Scenario;
import in.canaris.cloud.openstack.entity.CategoryMaster;
import in.canaris.cloud.openstack.entity.ChartBoatInstructionTemplate;
import in.canaris.cloud.openstack.entity.CommandHistory;
import in.canaris.cloud.openstack.entity.ContainerUserLabDTO;
import in.canaris.cloud.openstack.entity.CourseMaster;
import in.canaris.cloud.openstack.entity.DepartmentMaster;
import in.canaris.cloud.openstack.entity.Discover_Docker_Network;
import in.canaris.cloud.openstack.entity.InstructionCommand;
import in.canaris.cloud.openstack.entity.Playlist;
import in.canaris.cloud.openstack.entity.PlaylistItem;
import in.canaris.cloud.openstack.entity.ScenarioComments;
import in.canaris.cloud.openstack.entity.ScenarioLabTemplate;
import in.canaris.cloud.openstack.entity.SemesterMaster;
import in.canaris.cloud.openstack.entity.SubPlaylist;
import in.canaris.cloud.openstack.entity.SubPlaylistScenario;
import in.canaris.cloud.openstack.entity.SubjectMaster;
import in.canaris.cloud.openstack.entity.SubjectPlaylistMapping;
import in.canaris.cloud.openstack.entity.SubjectScenarioMapping;
import in.canaris.cloud.openstack.entity.SubjectSubplaylistMapping;
import in.canaris.cloud.openstack.entity.SubjectWisePlaylistView;
import in.canaris.cloud.openstack.entity.UserLab;
import in.canaris.cloud.openstack.entity.UserMappingsResponse;
import in.canaris.cloud.openstack.entity.UserPerformanceDTO;
import in.canaris.cloud.openstack.entity.UserScenario;
import in.canaris.cloud.openstack.entity.UserScenarioMapping;
import in.canaris.cloud.openstack.entity.UserSubplaylistMapping;
import in.canaris.cloud.openstack.entity.UserWiseChatBoatInstructionTemplate;
import in.canaris.cloud.openstack.entity.UserWisePlaylistForm;
import in.canaris.cloud.repository.ScenarioRepository;
import in.canaris.cloud.repository.UserRepository;
import in.canaris.cloud.repository.UserScenerioRepository;
import in.canaris.cloud.repository.UserWiseChatBoatInstructionTemplateRepository;
import in.canaris.cloud.repository.ChartBoatInstructionTemplateRepository;
import in.canaris.cloud.repository.CloudInstanceRepository;
import in.canaris.cloud.repository.PlaylistRepository;
import in.canaris.cloud.repository.PlaylistsSenarioRepository;
import in.canaris.cloud.repository.DiscoverDockerNetworkRepository;
import in.canaris.cloud.repository.GroupRepository;
import in.canaris.cloud.repository.CommandHistoryRepository;
import in.canaris.cloud.repository.ScenarioCommentsRepository;
import in.canaris.cloud.openstack.repository.ScenarioLabTemplateRepository;
import in.canaris.cloud.repository.SubPlaylistScenarioRepository;
import in.canaris.cloud.repository.PlaylistItemRepository;
import in.canaris.cloud.repository.AppUserRepository;
import in.canaris.cloud.repository.UserPlaylistMappingRepository;
import in.canaris.cloud.repository.UserSubplaylistMappingRepository;
import in.canaris.cloud.repository.UserScenarioMappingRepository;
import in.canaris.cloud.repository.DiscoverContainerRepository;
import in.canaris.cloud.repository.DepartmentMasterRepository;
import in.canaris.cloud.repository.CourseMasterRepository;
import in.canaris.cloud.repository.SemesterMasterRepository;
import in.canaris.cloud.repository.SubjectMasterRepository;
import in.canaris.cloud.repository.AddPhysicalServerRepository;
import in.canaris.cloud.repository.CategoryMasterRepository;
import in.canaris.cloud.repository.SubProductRepository;
import in.canaris.cloud.repository.ProductRepository;
import in.canaris.cloud.repository.PhysicalServerHealthMonitoringRepository;
import in.canaris.cloud.repository.SubjectPlaylistMappingRepository;
import in.canaris.cloud.repository.SubjectSubplaylistMappingRepository;
import in.canaris.cloud.repository.SubjectScenarioMappingRepository;

import in.canaris.cloud.repository.SubPlaylistRepository;
import in.canaris.cloud.repository.UserLabRepository;
import in.canaris.cloud.service.DockerService;
import in.canaris.cloud.service.GuacamoleService;
import in.canaris.cloud.service.ProxmoxService;
import in.canaris.cloud.utils.GuacIdentifierUtil;

@Controller
@RequestMapping("/guac")
public class GuacamoleController {

	private final GuacamoleService guacService;

	public GuacamoleController(GuacamoleService guacService) {
		this.guacService = guacService;
	}

	@Autowired
	private ScenarioRepository ScenarioRepository;

	@Autowired
	private PlaylistRepository PlaylistRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CloudInstanceRepository repository;

	@Autowired
	private PlaylistsSenarioRepository PlaylistsSenarioRepository;

//	@Autowired
//	private InstructionCommandRepository InstructionCommandRepository;

	@Autowired
	private CommandHistoryRepository CommandHistoryRepository;

	@Autowired
	private ScenarioCommentsRepository ScenarioCommentsRepository;

	@Autowired
	private UserLabRepository UserLabRepository;

	@Autowired
	private ChartBoatInstructionTemplateRepository ChartBoatInstructionTemplateRepository;

	@Autowired
	private UserWiseChatBoatInstructionTemplateRepository instructionTemplateRepository;

	@Autowired
	private UserScenerioRepository UserScenerioRepository;

	@Autowired
	private DiscoverDockerNetworkRepository DiscoverDockerNetworkRepository;

	@Autowired
	private DockerService dockerService;

	@Autowired
	private ScenarioLabTemplateRepository ScenarioLabTemplateRepository;

	@Autowired
	private SubPlaylistRepository SubPlaylistRepository;

	@Autowired
	SubPlaylistScenarioRepository SubPlaylistScenarioRepository;

	@Autowired
	PlaylistItemRepository PlaylistItemRepository;

	@Autowired
	AppUserRepository AppUserRepository;

	@Autowired
	UserPlaylistMappingRepository UserPlaylistMappingRepository;

	@Autowired
	GroupRepository GroupRepository;

	@Autowired
	private ProxmoxService proxmoxService;

	@Autowired
	UserSubplaylistMappingRepository UserSubplaylistMappingRepository;

	@Autowired
	UserScenarioMappingRepository UserScenarioMappingRepository;

	@Autowired
	DiscoverContainerRepository DiscoverContainerRepository;

	@Autowired
	DepartmentMasterRepository DepartmentMasterRepository;

	@Autowired
	CourseMasterRepository CourseMasterRepository;

	@Autowired
	SemesterMasterRepository SemesterMasterRepository;

	@Autowired
	SubjectMasterRepository SubjectMasterRepository;

	@Autowired
	AddPhysicalServerRepository AddPhysicalServerRepository;

	@Autowired
	CategoryMasterRepository CategoryMasterRepository;

	@Autowired
	SubProductRepository SubProductRepository;

	@Autowired
	ProductRepository ProductRepository;

	@Autowired
	PhysicalServerHealthMonitoringRepository PhysicalServerHealthMonitoringRepository;
	
	@Autowired
	SubjectPlaylistMappingRepository SubjectPlaylistMappingRepository;
	
	@Autowired
	SubjectSubplaylistMappingRepository SubjectSubplaylistMappingRepository;
	
	@Autowired
	SubjectScenarioMappingRepository SubjectScenarioMappingRepository;

	@GetMapping("/")
	public String home() {
		return "indec"; // fixed typo: "indec" → "index"
	}

	@GetMapping("/create")
	public String showCreateForm() {
		return "create";
	}

	@PostMapping("/create")
	public String createConnection(@RequestParam String name, @RequestParam String protocol, @RequestParam String host,
			@RequestParam(defaultValue = "3389") int port, @RequestParam(required = false) String user,
			@RequestParam(required = false) String pass, @RequestParam(required = false) String domain,
			@RequestParam(required = false) String ignoreCert, @RequestParam(required = false) String width,
			@RequestParam(required = false) String height, @RequestParam(required = false) String privateKey,
			@RequestParam(required = false) String passphrase, @RequestParam(required = false) String command,
			@RequestParam(required = false) String namespace, @RequestParam(required = false) String container,
			@RequestParam(required = false) String kubeCommand, @RequestParam(required = false) String kubeToken,
			Model model) {
		String msg = guacService.createConnection(name, protocol, host, port, user, pass, domain, ignoreCert, width,
				height, privateKey, passphrase, command, namespace, container, kubeCommand, kubeToken);

		model.addAttribute("message", msg);
		return "create";
	}

	@GetMapping("/list")
	public String listConnections(Model model) {
		model.addAttribute("connections", guacService.listConnections());
		return "list";
	}

//	

	@GetMapping("/Add_Docker")
	public String addDockerConnections(Model model) {
		List<String> ipAddresses = UserLabRepository.getPhysicalServerIPs();
		model.addAttribute("ipaddress", ipAddresses);
		return "Add_Docker";
	}

//	@GetMapping("/Create_docker_network")
//	public String Create_docker_network(Model model) {
//		List<String> ipAddresses = UserLabRepository.getPhysicalServerIPs();
//		model.addAttribute("ipaddress", ipAddresses);
//		return "Create_docker_network";
//	}

	@GetMapping("/Create_docker_network")
	public ModelAndView Create_docker_network(RedirectAttributes redirectAttributes) {
		ModelAndView mav = new ModelAndView("Create_docker_network");
		List<Discover_Docker_Network> instances = null;
		instances = DiscoverDockerNetworkRepository.findByDriver();
		mav.addObject("pageTitle", "Create New Docker Network");
		mav.addObject("instanceNameList", instances);
		mav.addObject("dockerNetwork", new Discover_Docker_Network());

		return mav;
	}

//	@PostMapping("/saveDockerNetwork")
//	public String saveDockerNetwork(@ModelAttribute("dockerNetwork") Discover_Docker_Network dockerNetwork,
//			RedirectAttributes redirectAttributes) {
//
//		DiscoverDockerNetworkRepository.save(dockerNetwork);
//
//		// Optionally, add a flash message
//		redirectAttributes.addFlashAttribute("successMessage", "Docker Network created successfully!");
//
//		// Redirect to a confirmation page or back to the form
//		return "redirect:/Create_docker_network";
//	}

	@PostMapping("/saveDockerNetwork")
	public String saveDockerNetwork(@ModelAttribute("dockerNetwork") Discover_Docker_Network dockerNetwork,
			RedirectAttributes redirectAttributes) {

		try {
			// STEP 1: Build Docker command
			String networkName = dockerNetwork.getName();
			String driver = dockerNetwork.getDriver();
			String subnet = "192.168.100.0/24"; // Example static subnet — you can also make it a form field
			String gateway = dockerNetwork.getGateway();
			String ipRange = dockerNetwork.getStartIp() + "/28"; // Or use a separate field if needed
			String auxAddress = "excluded1=" + dockerNetwork.getEndIp();

			List<String> command = Arrays.asList("docker", "network", "create", "--driver", driver, "--subnet", subnet,
					"--gateway", gateway, "--ip-range", ipRange, "--aux-address=" + auxAddress, networkName);

			// STEP 2: Execute the create command
			ProcessBuilder pbCreate = new ProcessBuilder(command);
			Process processCreate = pbCreate.start();
			int exitCodeCreate = processCreate.waitFor();

			if (exitCodeCreate != 0) {
				redirectAttributes.addFlashAttribute("errorMessage", "Docker network creation failed.");
				return "redirect:/guac/Create_docker_network";
			}

			// STEP 3: Get the network ID from stdout
			String networkId;
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(processCreate.getInputStream()))) {
				networkId = reader.readLine();
			}

			// STEP 4: Inspect the network
			ProcessBuilder pbInspect = new ProcessBuilder("docker", "network", "inspect", networkId);
			Process processInspect = pbInspect.start();
			int exitCodeInspect = processInspect.waitFor();

			if (exitCodeInspect != 0) {
				redirectAttributes.addFlashAttribute("errorMessage", "Docker network inspect failed.");
				return "redirect:/guac/Create_docker_network";
			}

			// STEP 5: Parse inspect output
			StringBuilder inspectOutput = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(processInspect.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					inspectOutput.append(line);
				}
			}

			// Parse JSON
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode root = objectMapper.readTree(inspectOutput.toString());
			JsonNode networkInfo = root.get(0);

			String scope = networkInfo.get("Scope").asText();
			String id = networkInfo.get("Id").asText();

			// STEP 6: Set fields in entity and save
			dockerNetwork.setScope(scope);
			dockerNetwork.setNetworkId(id); // Assuming this field is `networkId` in entity

			DiscoverDockerNetworkRepository.save(dockerNetwork);

			redirectAttributes.addFlashAttribute("successMessage", "Docker Network created successfully!");

		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error occurred: " + e.getMessage());
		}

//	    return "redirect:/guac/Create_docker_network";

		return "redirect:/guac/Create_docker_network";
	}

	@GetMapping("/View_DockerListing")
	public String viewDockerListing(Model model) {
		List<Discover_Docker_Network> dockerNetworks = DiscoverDockerNetworkRepository.findAll();
		model.addAttribute("listObj", dockerNetworks);
		return "View_DockerListing";
	}

//	@GetMapping("/SuperAdmin_Dashboard")
//	public String dashboard(Model model, Principal principal) {
//
//		Authentication auth = (Authentication) principal;
//		String username = auth.getName();
//		try {
//
//			List<Object[]> results = AddPhysicalServerRepository.findVirtualizationTypeCounts();
//
//			Map<String, Map<String, Long>> typeStats = new HashMap<>();
//			Map<String, Long> totalCountByType = new HashMap<>();
//
//			long overallUp = 0;
//			long overallDown = 0;
//
//			for (Object[] row : results) {
//				String type = (String) row[0];
//				String rawStatus = (String) row[1];
//				Long count = ((Number) row[2]).longValue();
//
//				String status = (rawStatus != null) ? rawStatus.toLowerCase() : "unknown";
//
//				typeStats.putIfAbsent(type, new HashMap<>());
//				totalCountByType.putIfAbsent(type, 0L);
//
//				typeStats.get(type).put(status, count);
//				totalCountByType.put(type, totalCountByType.get(type) + count);
//
//				if ("up".equals(status)) {
//					overallUp += count;
//				} else if ("down".equals(status)) {
//					overallDown += count;
//				}
//			}
//
//			
//			for (Map<String, Long> map : typeStats.values()) {
//				map.putIfAbsent("up", 0L);
//				map.putIfAbsent("down", 0L);
//				map.putIfAbsent("unknown", 0L);
//			}
//
//
//			List<Object[]> userStatusCounts = AppUserRepository.countByStatus();
//			long userOnline = 0;
//			long userOffline = 0;
//			for (Object[] ur : userStatusCounts) {
//				String st = (String) ur[0];
//				Long c = ((Number) ur[1]).longValue();
//				String lower = (st != null) ? st.toLowerCase() : "unknown";
//				if ("online".equals(lower) || "up".equals(lower)) {
//					userOnline += c;
//				} else if ("offline".equals(lower) || "down".equals(lower)) {
//					userOffline += c;
//				}
//			}
//
//			// 3. Other counts
//			long playlistCount = PlaylistRepository.count();
//			long subPlaylistCount = SubPlaylistRepository.count();
//			long scenarioCount = ScenarioRepository.count();
//			long templateCount = repository.count();
//
//			// 4. Add to model
//			model.addAttribute("typeStats", typeStats);
//			model.addAttribute("totalCountByType", totalCountByType);
//
//			model.addAttribute("overallUp", overallUp);
//			model.addAttribute("overallDown", overallDown);
//
//			model.addAttribute("userOnline", userOnline);
//			model.addAttribute("userOffline", userOffline);
//
//			model.addAttribute("playlistCount", playlistCount);
//			model.addAttribute("subPlaylistCount", subPlaylistCount);
//			model.addAttribute("scenarioCount", scenarioCount);
//			model.addAttribute("templateCount", templateCount);
//
//			model.addAttribute("pageTitle", "Super Admin Dashboard");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.err.println("Exption_UserWise_Dshboard");
//			// TODO: handle exception
//		}
//
//		return "SuperAdmin_Dashboard";
//	}
//	

//	@GetMapping("/SuperAdmin_Dashboard")
//	public String dashboard(Model model, Principal principal) {
//
//		Authentication auth = (Authentication) principal;
//		String username = auth.getName();
//		try {
//
//			List<Object[]> results = AddPhysicalServerRepository.findVirtualizationTypeCounts();
//
//			Map<String, Map<String, Long>> typeStats = new HashMap<>();
//			Map<String, Long> totalCountByType = new HashMap<>();
//
//			long overallUp = 0;
//			long overallDown = 0;
//
//			for (Object[] row : results) {
//				String type = (String) row[0];
//				String rawStatus = (String) row[1];
//				Long count = ((Number) row[2]).longValue();
//
//				String status = (rawStatus != null) ? rawStatus.toLowerCase() : "unknown";
//
//				typeStats.putIfAbsent(type, new HashMap<>());
//				totalCountByType.putIfAbsent(type, 0L);
//
//				typeStats.get(type).put(status, count);
//				totalCountByType.put(type, totalCountByType.get(type) + count);
//
//				if ("up".equals(status)) {
//					overallUp += count;
//				} else if ("down".equals(status)) {
//					overallDown += count;
//				}
//			}
//
//			for (Map<String, Long> map : typeStats.values()) {
//				map.putIfAbsent("up", 0L);
//				map.putIfAbsent("down", 0L);
//				map.putIfAbsent("unknown", 0L);
//			}
//
//			List<Object[]> userStatusCounts = AppUserRepository.countByStatus();
//			long userOnline = 0;
//			long userOffline = 0;
//			for (Object[] ur : userStatusCounts) {
//				String st = (String) ur[0];
//				Long c = ((Number) ur[1]).longValue();
//				String lower = (st != null) ? st.toLowerCase() : "unknown";
//				if ("online".equals(lower) || "up".equals(lower)) {
//					userOnline += c;
//				} else if ("offline".equals(lower) || "down".equals(lower)) {
//					userOffline += c;
//				}
//			}
//
//			// NEW: Add health monitoring data
//			List<Object[]> healthResults = AddPhysicalServerRepository.findHealthDataByVirtualizationType();
//
//			Map<String, Map<String, Double>> healthStats = new HashMap<>();
//
//			for (Object[] row : healthResults) {
//				String type = (String) row[0];
//				Double usedCpu = ((Number) row[1]).doubleValue();
//				Double totalCpu = ((Number) row[2]).doubleValue();
//				Double usedRam = ((Number) row[3]).doubleValue();
//				Double totalRam = ((Number) row[4]).doubleValue();
//				Double usedDisk = ((Number) row[5]).doubleValue();
//				Double totalDisk = ((Number) row[6]).doubleValue();
//
//				healthStats.putIfAbsent(type, new HashMap<>());
//
//				// Calculate usage percentages
//				double cpuUsage = totalCpu > 0 ? (usedCpu / totalCpu) * 100 : 0;
//				double ramUsage = totalRam > 0 ? (usedRam / totalRam) * 100 : 0;
//				double diskUsage = totalDisk > 0 ? (usedDisk / totalDisk) * 100 : 0;
//
//				healthStats.get(type).put("cpuUsage", cpuUsage);
//				healthStats.get(type).put("ramUsage", ramUsage);
//				healthStats.get(type).put("diskUsage", diskUsage);
//				healthStats.get(type).put("cpuFree", 100 - cpuUsage);
//				healthStats.get(type).put("ramFree", 100 - ramUsage);
//				healthStats.get(type).put("diskFree", 100 - diskUsage);
//			}
//
//			List<Object[]> subProductDetails = repository.getSubProductDetails();
//
//			List<Map<String, Object>> subProductList = new ArrayList<>();
//			for (Object[] row : subProductDetails) {
//				Map<String, Object> map = new HashMap<>();
//				map.put("productName", row[0]);
//				map.put("subProductName", row[1]);
//				map.put("instanceCount", row[2]);
//				subProductList.add(map);
//			}
//			model.addAttribute("subProductList", subProductList);
//			// Add to model
//
//			// 3. Other counts
//			long playlistCount = PlaylistRepository.count();
//			long subPlaylistCount = SubPlaylistRepository.count();
//			long scenarioCount = ScenarioRepository.count();
//			long templateCount = repository.count();
//
//			// 4. Add to model
//
//			model.addAttribute("healthStats", healthStats);
//			model.addAttribute("typeStats", typeStats);
//			model.addAttribute("totalCountByType", totalCountByType);
//
//			model.addAttribute("overallUp", overallUp);
//			model.addAttribute("overallDown", overallDown);
//
//			model.addAttribute("userOnline", userOnline);
//			model.addAttribute("userOffline", userOffline);
//
//			model.addAttribute("playlistCount", playlistCount);
//			model.addAttribute("subPlaylistCount", subPlaylistCount);
//			model.addAttribute("scenarioCount", scenarioCount);
//			model.addAttribute("templateCount", templateCount);
//
//			model.addAttribute("pageTitle", "Super Admin Dashboard");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.err.println("Exption_UserWise_Dshboard");
//			// TODO: handle exception
//		}
//
//		return "SuperAdmin_Dashboard";
//	}
	
	@GetMapping("/SuperAdmin_Dashboard")
	public String dashboard(Model model, Principal principal) {
	    Authentication auth = (Authentication) principal;
	    String username = auth.getName();
	    try {
	        // Your existing virtualization and health monitoring code
	        List<Object[]> results = AddPhysicalServerRepository.findVirtualizationTypeCounts();
	        Map<String, Map<String, Long>> typeStats = new HashMap<>();
	        Map<String, Long> totalCountByType = new HashMap<>();

	        long overallUp = 0;
	        long overallDown = 0;

	        for (Object[] row : results) {
	            String type = (String) row[0];
	            String rawStatus = (String) row[1];
	            Long count = ((Number) row[2]).longValue();

	            String status = (rawStatus != null) ? rawStatus.toLowerCase() : "unknown";

	            typeStats.putIfAbsent(type, new HashMap<>());
	            totalCountByType.putIfAbsent(type, 0L);

	            typeStats.get(type).put(status, count);
	            totalCountByType.put(type, totalCountByType.get(type) + count);

	            if ("up".equals(status)) {
	                overallUp += count;
	            } else if ("down".equals(status)) {
	                overallDown += count;
	            }
	        }

	        for (Map<String, Long> map : typeStats.values()) {
	            map.putIfAbsent("up", 0L);
	            map.putIfAbsent("down", 0L);
	            map.putIfAbsent("unknown", 0L);
	        }

	        List<Object[]> userStatusCounts = AppUserRepository.countByStatus();
	        long userOnline = 0;
	        long userOffline = 0;
	        for (Object[] ur : userStatusCounts) {
	            String st = (String) ur[0];
	            Long c = ((Number) ur[1]).longValue();
	            String lower = (st != null) ? st.toLowerCase() : "unknown";
	            if ("online".equals(lower) || "up".equals(lower)) {
	                userOnline += c;
	            } else if ("offline".equals(lower) || "down".equals(lower)) {
	                userOffline += c;
	            }
	        }

	        // NEW: Add health monitoring data
	        List<Object[]> healthResults = AddPhysicalServerRepository.findHealthDataByVirtualizationType();
	        Map<String, Map<String, Double>> healthStats = new HashMap<>();

	        for (Object[] row : healthResults) {
	            String type = (String) row[0];
	            Double usedCpu = ((Number) row[1]).doubleValue();
	            Double totalCpu = ((Number) row[2]).doubleValue();
	            Double usedRam = ((Number) row[3]).doubleValue();
	            Double totalRam = ((Number) row[4]).doubleValue();
	            Double usedDisk = ((Number) row[5]).doubleValue();
	            Double totalDisk = ((Number) row[6]).doubleValue();

	            healthStats.putIfAbsent(type, new HashMap<>());

	            // Calculate usage percentages
	            double cpuUsage = totalCpu > 0 ? (usedCpu / totalCpu) * 100 : 0;
	            double ramUsage = totalRam > 0 ? (usedRam / totalRam) * 100 : 0;
	            double diskUsage = totalDisk > 0 ? (usedDisk / totalDisk) * 100 : 0;

	            healthStats.get(type).put("cpuUsage", cpuUsage);
	            healthStats.get(type).put("ramUsage", ramUsage);
	            healthStats.get(type).put("diskUsage", diskUsage);
	            healthStats.get(type).put("cpuFree", 100 - cpuUsage);
	            healthStats.get(type).put("ramFree", 100 - ramUsage);
	            healthStats.get(type).put("diskFree", 100 - diskUsage);
	        }

	        List<Object[]> subProductDetails = repository.getSubProductDetails();
	        List<Map<String, Object>> subProductList = new ArrayList<>();
	        for (Object[] row : subProductDetails) {
	            Map<String, Object> map = new HashMap<>();
	            map.put("productName", row[0]);
	            map.put("subProductName", row[1]);
	            map.put("instanceCount", row[2]);
	            subProductList.add(map);
	        }
	        model.addAttribute("subProductList", subProductList);

	        // NEW: Get all users for SuperAdmin with their subjects
	        List<AppUser> allUsers = AppUserRepository.findAll();
	        
	        // Create a map to store user subjects (key: userId, value: list of subjects)
	        Map<Long, List<SubjectMaster>> userSubjectsMap = new HashMap<>();
	        
	        // For each user, find their subjects based on semester ID
	        for (AppUser user : allUsers) {
	            if (user.getSemesterName() != null) {
	                Integer semesterId = user.getSemesterName().getSemesterId();
	                List<SubjectMaster> userSubjects = SubjectMasterRepository.findBysemester_SemesterId(semesterId);
	                userSubjectsMap.put(user.getUserId(), userSubjects);
	                
	                System.out.println("User: " + user.getUserName() + 
	                                 ", Semester ID: " + semesterId + 
	                                 ", Subjects: " + userSubjects.size());
	            }
	        }

	        // Other counts
	        long playlistCount = PlaylistRepository.count();
	        long subPlaylistCount = SubPlaylistRepository.count();
	        long scenarioCount = ScenarioRepository.count();
	        long templateCount = repository.count();

	        // Add all attributes to model
	        model.addAttribute("healthStats", healthStats);
	        model.addAttribute("typeStats", typeStats);
	        model.addAttribute("totalCountByType", totalCountByType);

	        model.addAttribute("overallUp", overallUp);
	        model.addAttribute("overallDown", overallDown);

	        model.addAttribute("userOnline", userOnline);
	        model.addAttribute("userOffline", userOffline);

	        model.addAttribute("playlistCount", playlistCount);
	        model.addAttribute("subPlaylistCount", subPlaylistCount);
	        model.addAttribute("scenarioCount", scenarioCount);
	        model.addAttribute("templateCount", templateCount);

	        // NEW: Add users and userSubjectsMap for the table
	        model.addAttribute("users", allUsers);
	        model.addAttribute("userSubjectsMap", userSubjectsMap);

	        model.addAttribute("pageTitle", "Super Admin Dashboard");

	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println("Exception_SuperAdmin_Dashboard");
	    }

	    return "SuperAdmin_Dashboard";
	}

	private Double getDoubleValue(Object obj) {
		return (obj != null) ? ((Number) obj).doubleValue() : 0.0;
	}

//	@GetMapping("/SuperAdmin_Dashboard")
//	public String dashboard(Model model, Principal principal) {
//
//	    Authentication auth = (Authentication) principal;
//	    String username = auth.getName();
//
//	    try {
//	        // ===============================
//	        // 1. Physical Server Type Stats
//	        // ===============================
//	        List<Object[]> results = AddPhysicalServerRepository.findVirtualizationTypeCounts();
//
//	        Map<String, Map<String, Long>> typeStats = new HashMap<>();
//	        Map<String, Long> totalCountByType = new HashMap<>();
//
//	        long overallUp = 0;
//	        long overallDown = 0;
//
//	        for (Object[] row : results) {
//	            String type = (String) row[0];
//	            String rawStatus = (String) row[1];
//	            Long count = ((Number) row[2]).longValue();
//
//	            String status = (rawStatus != null) ? rawStatus.toLowerCase() : "unknown";
//
//	            typeStats.putIfAbsent(type, new HashMap<>());
//	            totalCountByType.putIfAbsent(type, 0L);
//
//	            typeStats.get(type).put(status, count);
//	            totalCountByType.put(type, totalCountByType.get(type) + count);
//
//	            if ("up".equals(status)) {
//	                overallUp += count;
//	            } else if ("down".equals(status)) {
//	                overallDown += count;
//	            }
//	        }
//
//	        for (Map<String, Long> map : typeStats.values()) {
//	            map.putIfAbsent("up", 0L);
//	            map.putIfAbsent("down", 0L);
//	            map.putIfAbsent("unknown", 0L);
//	        }
//
//	        // ===============================
//	        // 2. User Online/Offline Status
//	        // ===============================
//	        List<Object[]> userStatusCounts = AppUserRepository.countByStatus();
//	        long userOnline = 0;
//	        long userOffline = 0;
//
//	        for (Object[] ur : userStatusCounts) {
//	            String st = (String) ur[0];
//	            Long c = ((Number) ur[1]).longValue();
//	            String lower = (st != null) ? st.toLowerCase() : "unknown";
//
//	            if ("online".equals(lower) || "up".equals(lower)) {
//	                userOnline += c;
//	            } else if ("offline".equals(lower) || "down".equals(lower)) {
//	                userOffline += c;
//	            }
//	        }
//
//	        // ===============================
//	        // 3. Other Counts
//	        // ===============================
//	        long playlistCount = PlaylistRepository.count();
//	        long subPlaylistCount = SubPlaylistRepository.count();
//	        long scenarioCount = ScenarioRepository.count();
//	        long templateCount = repository.count();
//
//	        // ===============================
//	        // 4. SubProduct & Product Logic
//	        // ===============================
//	        // Assuming this queries distinct subProductIds (Integer or int)
//	        List<Integer> subProductIds = repository.findDistinctSubProductIds(); 
//
//	        Map<String, Long> subProductCountMap = new HashMap<>();
//
//	        for (Integer subProductId : subProductIds) {
//	            if (subProductId == null) continue;
//
//	            // Count how many subproducts are linked to this product
//	            Long subProductCount = SubProductRepository.countByProductId(subProductId.longValue());
//
//	            // Get product name
//	            String productName = ProductRepository.findProductNameById(subProductId.longValue());
//
//	            if (productName != null) {
//	                subProductCountMap.put(productName, subProductCount);
//	            }
//	        }
//
//	        // ===============================
//	        // 5. Add to Model
//	        // ===============================
//	        model.addAttribute("typeStats", typeStats);
//	        model.addAttribute("totalCountByType", totalCountByType);
//	        model.addAttribute("overallUp", overallUp);
//	        model.addAttribute("overallDown", overallDown);
//
//	        model.addAttribute("userOnline", userOnline);
//	        model.addAttribute("userOffline", userOffline);
//
//	        model.addAttribute("playlistCount", playlistCount);
//	        model.addAttribute("subPlaylistCount", subPlaylistCount);
//	        model.addAttribute("scenarioCount", scenarioCount);
//	        model.addAttribute("templateCount", templateCount);
//
//	        model.addAttribute("subProductIds", subProductIds);
//	        model.addAttribute("subProductCountMap", subProductCountMap);
//
//	        model.addAttribute("pageTitle", "Super Admin Dashboard");
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        System.err.println("Exception_SuperAdmin_Dashboard");
//	    }
//
//	    return "SuperAdmin_Dashboard";
//	}

//	@GetMapping("/UserWise_Dashboard")
//	public String UserWise_Dashboard(Model model, Principal principal) {
//
//		Authentication auth = (Authentication) principal;
//		String username = auth.getName();
//		try {
//
//			long playlistCount = UserPlaylistMappingRepository.countByUserName(username);
//			long subPlaylistCount = UserSubplaylistMappingRepository.countByUserName(username);
//			long scenarioCount = UserScenarioMappingRepository.countByUserName(username);
//
//			Integer falseCountObj = instructionTemplateRepository.getFalseCompletionCountsByTemplateId(username);
//			Integer trueCountObj = instructionTemplateRepository.getTrueCompletionCountsByTemplateId(username);
//
//			int falseCount = (falseCountObj != null) ? falseCountObj : 0;
//			int trueCount = (trueCountObj != null) ? trueCountObj : 0;
//
//			int total = trueCount + falseCount;
//			int percentage = (total == 0) ? 0 : (trueCount * 100 / total);
//
//			model.addAttribute("playlistCount", playlistCount);
//			model.addAttribute("subPlaylistCount", subPlaylistCount);
//			model.addAttribute("scenarioCount", scenarioCount);
//			model.addAttribute("completionPercentage", percentage);
//			model.addAttribute("completedTasks", trueCount);
//			model.addAttribute("totalTasks", total);
//
//			model.addAttribute("pageTitle", "User Dashboard");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.err.println("Exption_UserWise_Dshboard");
//			// TODO: handle exception
//		}
//
//		return "UserWise_Dashboard";
//	}
	
//	@GetMapping("/UserWise_Dashboard")
//	public String UserWise_Dashboard(Model model, Principal principal) {
//	    Authentication auth = (Authentication) principal;
//	    String username = auth.getName();
//	    try {
//	        long playlistCount = UserPlaylistMappingRepository.countByUserName(username);
//	        long subPlaylistCount = UserSubplaylistMappingRepository.countByUserName(username);
//	        long scenarioCount = UserScenarioMappingRepository.countByUserName(username);
//
//	        Integer falseCountObj = instructionTemplateRepository.getFalseCompletionCountsByTemplateId(username);
//	        Integer trueCountObj = instructionTemplateRepository.getTrueCompletionCountsByTemplateId(username);
//
//	        int falseCount = (falseCountObj != null) ? falseCountObj : 0;
//	        int trueCount = (trueCountObj != null) ? trueCountObj : 0;
//
//	        int total = trueCount + falseCount;
//	        int percentage = (total == 0) ? 0 : (trueCount * 100 / total);
//
//	        // Get all users for the table
//	        List<AppUser> users = AppUserRepository.findByUserName(username);
//	        
//	        if (!users.isEmpty()) {
//	            AppUser firstUser = users.get(0);
//	            System.out.println("User properties:");
//	            System.out.println("Username: " + firstUser.getUserName());
//	            System.out.println("Department: " + firstUser.getDepartmentName());
//	            System.out.println("Course: " + firstUser.getCourseName());
//	            System.out.println("Semester: " + firstUser.getSemesterName());
//	            System.out.println("Subject: " + firstUser.getSubjectName());
//	        }
//	        
//	        model.addAttribute("users", users);
//	        
//	        model.addAttribute("playlistCount", playlistCount);
//	        model.addAttribute("subPlaylistCount", subPlaylistCount);
//	        model.addAttribute("scenarioCount", scenarioCount);
//	        model.addAttribute("completionPercentage", percentage);
//	        model.addAttribute("completedTasks", trueCount);
//	        model.addAttribute("totalTasks", total);
////	        model.addAttribute("users", users); // Add users list to model
//	        model.addAttribute("pageTitle", "User Dashboard");
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        System.err.println("Exception_UserWise_Dashboard");
//	    }
//
//	    return "UserWise_Dashboard";
//	}
	
	
//	@GetMapping("/UserWise_Dashboard")
//	public String UserWise_Dashboard(Model model, Principal principal) {
//	    Authentication auth = (Authentication) principal;
//	    String username = auth.getName();
//	    try {
//	        long playlistCount = UserPlaylistMappingRepository.countByUserName(username);
//	        long subPlaylistCount = UserSubplaylistMappingRepository.countByUserName(username);
//	        long scenarioCount = UserScenarioMappingRepository.countByUserName(username);
//
//	        Integer falseCountObj = instructionTemplateRepository.getFalseCompletionCountsByTemplateId(username);
//	        Integer trueCountObj = instructionTemplateRepository.getTrueCompletionCountsByTemplateId(username);
//
//	        int falseCount = (falseCountObj != null) ? falseCountObj : 0;
//	        int trueCount = (trueCountObj != null) ? trueCountObj : 0;
//
//	        int total = trueCount + falseCount;
//	        int percentage = (total == 0) ? 0 : (trueCount * 100 / total);
//
//	        // Get all users with related entities
//	        List<AppUser> users = AppUserRepository.findByUserName(username);
//	        
//	        // For each user, find their subjects based on semester ID
//	        for (AppUser user : users) {
//	            if (user.getSemesterName() != null) {
//	                Integer semesterId = user.getSemesterName().getSemesterId();
//	                List<SubjectMaster> userSubjects = SubjectMasterRepository.findBysemester_SemesterId(semesterId);
//	                // You can store this in a transient field or use as needed
//	                System.out.println("User: " + user.getUserName() + 
//	                                 ", Semester ID: " + semesterId + 
//	                                 ", Subjects: " + userSubjects.size());
//	            }
//	        }
//	        
//	        model.addAttribute("playlistCount", playlistCount);
//	        model.addAttribute("subPlaylistCount", subPlaylistCount);
//	        model.addAttribute("scenarioCount", scenarioCount);
//	        model.addAttribute("completionPercentage", percentage);
//	        model.addAttribute("completedTasks", trueCount);
//	        model.addAttribute("totalTasks", total);
//	        model.addAttribute("users", users);
//	        model.addAttribute("pageTitle", "User Dashboard");
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        System.err.println("Exception_UserWise_Dashboard");
//	    }
//
//	    return "UserWise_Dashboard";
//	}
	
	
//	@GetMapping("/UserWise_Dashboard")
//	public String UserWise_Dashboard(Model model, Principal principal) {
//	    Authentication auth = (Authentication) principal;
//	    String username = auth.getName();
//	    try {
//	        long playlistCount = UserPlaylistMappingRepository.countByUserName(username);
//	        long subPlaylistCount = UserSubplaylistMappingRepository.countByUserName(username);
//	        long scenarioCount = UserScenarioMappingRepository.countByUserName(username);
//
//	        Integer falseCountObj = instructionTemplateRepository.getFalseCompletionCountsByTemplateId(username);
//	        Integer trueCountObj = instructionTemplateRepository.getTrueCompletionCountsByTemplateId(username);
//
//	        int falseCount = (falseCountObj != null) ? falseCountObj : 0;
//	        int trueCount = (trueCountObj != null) ? trueCountObj : 0;
//
//	        int total = trueCount + falseCount;
//	        int percentage = (total == 0) ? 0 : (trueCount * 100 / total);
//
//	        // Get all users with related entities
//	        List<AppUser> users = AppUserRepository.findByUserName(username);
//	        
//	        // Create a map to store user subjects (key: userId, value: list of subjects)
//	        Map<Long, List<SubjectMaster>> userSubjectsMap = new HashMap<>();
//	        
//	        // For each user, find their subjects based on semester ID
//	        for (AppUser user : users) {
//	            if (user.getSemesterName() != null) {
//	                Integer semesterId = user.getSemesterName().getSemesterId();
//	                List<SubjectMaster> userSubjects = SubjectMasterRepository.findBysemester_SemesterId(semesterId);
//	                userSubjectsMap.put(user.getUserId(), userSubjects);
//	                
//	                System.out.println("User: " + user.getUserName() + 
//	                                 ", Semester ID: " + semesterId + 
//	                                 ", Subjects: " + userSubjects.size());
//	            }
//	        }
//	        
//	        model.addAttribute("playlistCount", playlistCount);
//	        model.addAttribute("subPlaylistCount", subPlaylistCount);
//	        model.addAttribute("scenarioCount", scenarioCount);
//	        model.addAttribute("completionPercentage", percentage);
//	        model.addAttribute("completedTasks", trueCount);
//	        model.addAttribute("totalTasks", total);
//	        model.addAttribute("users", users);
//	        model.addAttribute("userSubjectsMap", userSubjectsMap); // Pass the map to view
//	        model.addAttribute("pageTitle", "User Dashboard");
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        System.err.println("Exception_UserWise_Dashboard");
//	    }
//
//	    return "UserWise_Dashboard";
//	}
	
	
	@GetMapping("/UserWise_Dashboard")
	public String UserWise_Dashboard(Model model, Principal principal) {
	    Authentication auth = (Authentication) principal;
	    String username = auth.getName();
	    try {
	        // Get current user
	        List<AppUser> users = AppUserRepository.findByUserName(username);
	        if (users.isEmpty()) {
	            model.addAttribute("error", "User not found");
	            return "UserWise_Dashboard";
	        }
	        
	        AppUser currentUser = users.get(0);
	        
	        // Get user's semester and subjects
	        Integer semesterId = null;
	        List<SubjectMaster> userSubjects = new ArrayList<>();
	        
	        if (currentUser.getSemesterName() != null) {
	            semesterId = currentUser.getSemesterName().getSemesterId();
	            userSubjects = SubjectMasterRepository.findBysemester_SemesterId(semesterId);
	        }
	        
	        // Count playlists, sub-playlists, and scenarios based on user's subjects
	        long playlistCount = getPlaylistCountForUser(userSubjects);
	        long subPlaylistCount = getSubPlaylistCountForUser(userSubjects);
	        long scenarioCount = getScenarioCountForUser(userSubjects);

	        // Your existing completion count logic
	        Integer falseCountObj = instructionTemplateRepository.getFalseCompletionCountsByTemplateId(username);
	        Integer trueCountObj = instructionTemplateRepository.getTrueCompletionCountsByTemplateId(username);

	        int falseCount = (falseCountObj != null) ? falseCountObj : 0;
	        int trueCount = (trueCountObj != null) ? trueCountObj : 0;

	        int total = trueCount + falseCount;
	        int percentage = (total == 0) ? 0 : (trueCount * 100 / total);

	        // Create a map to store user subjects (key: userId, value: list of subjects)
	        Map<Long, List<SubjectMaster>> userSubjectsMap = new HashMap<>();
	        
	        // For each user, find their subjects based on semester ID
	        for (AppUser user : users) {
	            if (user.getSemesterName() != null) {
	                Integer userSemesterId = user.getSemesterName().getSemesterId();
	                List<SubjectMaster> subjectsForUser = SubjectMasterRepository.findBysemester_SemesterId(userSemesterId);
	                userSubjectsMap.put(user.getUserId(), subjectsForUser);
	                
	                System.out.println("User: " + user.getUserName() + 
	                                 ", Semester ID: " + userSemesterId + 
	                                 ", Subjects: " + subjectsForUser.size());
	            }
	        }
	        
	        model.addAttribute("playlistCount", playlistCount);
	        model.addAttribute("subPlaylistCount", subPlaylistCount);
	        model.addAttribute("scenarioCount", scenarioCount);
	        model.addAttribute("completionPercentage", percentage);
	        model.addAttribute("completedTasks", trueCount);
	        model.addAttribute("totalTasks", total);
	        model.addAttribute("users", users);
	        model.addAttribute("userSubjectsMap", userSubjectsMap);
	        model.addAttribute("pageTitle", "User Dashboard");

	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println("Exception_UserWise_Dashboard");
	    }

	    return "UserWise_Dashboard";
	}

	// Count playlists based on user's subjects
	private long getPlaylistCountForUser(List<SubjectMaster> userSubjects) {
	    if (userSubjects == null || userSubjects.isEmpty()) {
	        return 0;
	    }
	    
	    // Get subject IDs
	    List<Integer> subjectIds = userSubjects.stream()
	            .map(SubjectMaster::getSubjectId)
	            .collect(Collectors.toList());
	    
	    // Get playlist IDs from mapping table
	    List<Integer> playlistIds = SubjectPlaylistMappingRepository.findPlaylistIdsBySubjectIds(subjectIds);
	    
	    return playlistIds != null ? playlistIds.size() : 0;
	}

	// Count sub-playlists based on user's subjects
	private long getSubPlaylistCountForUser(List<SubjectMaster> userSubjects) {
	    if (userSubjects == null || userSubjects.isEmpty()) {
	        return 0;
	    }
	    
	    // Get subject IDs
	    List<Integer> subjectIds = userSubjects.stream()
	            .map(SubjectMaster::getSubjectId)
	            .collect(Collectors.toList());
	    
	    // Get sub-playlist IDs from mapping table
	    List<Integer> subPlaylistIds = SubjectSubplaylistMappingRepository.findSubPlaylistIdsBySubjectIds(subjectIds);
	    
	    return subPlaylistIds != null ? subPlaylistIds.size() : 0;
	}

	// Count scenarios based on user's subjects
	private long getScenarioCountForUser(List<SubjectMaster> userSubjects) {
	    if (userSubjects == null || userSubjects.isEmpty()) {
	        return 0;
	    }
	    
	    // Get subject IDs
	    List<Integer> subjectIds = userSubjects.stream()
	            .map(SubjectMaster::getSubjectId)
	            .collect(Collectors.toList());
	    
	    // Get scenario IDs from mapping table
	    List<Integer> scenarioIds = SubjectScenarioMappingRepository.findScenarioIdsBySubjectIds(subjectIds);
	    
	    return scenarioIds != null ? scenarioIds.size() : 0;
	}

//	@GetMapping("/UserWise_Dashboard")
//	public String UserWise_Dashboard(Model model, Principal principal) {
//	    Authentication auth = (Authentication) principal;
//	    String username = auth.getName();
//
//	    try {
//	        long playlistCount = UserPlaylistMappingRepository.countByUserName(username);
//	        long subPlaylistCount = UserSubplaylistMappingRepository.countByUserName(username);
//	        long scenarioCount = UserScenarioMappingRepository.countByUserName(username);
//
//	        Integer falseCountObj = instructionTemplateRepository.getFalseCompletionCountsByTemplateId(username);
//	        Integer trueCountObj = instructionTemplateRepository.getTrueCompletionCountsByTemplateId(username);
//
//	        int falseCount = (falseCountObj != null) ? falseCountObj : 0;
//	        int trueCount = (trueCountObj != null) ? trueCountObj : 0;
//
//	        int total = trueCount + falseCount;
//	        int percentage = (total == 0) ? 0 : (trueCount * 100 / total);
//
//	        model.addAttribute("playlistCount", playlistCount);
//	        model.addAttribute("subPlaylistCount", subPlaylistCount);
//	        model.addAttribute("scenarioCount", scenarioCount);
//
//	        model.addAttribute("trueCount", trueCount);
//	        model.addAttribute("falseCount", falseCount);
//	        model.addAttribute("percentage", percentage);
//
//	        model.addAttribute("pageTitle", "User Dashboard");
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        System.err.println("Exception in UserWise_Dashboard");
//	    }
//
//	    return "UserWise_Dashboard";
//	}

	@GetMapping("/View_DiscoverContainerListing")
	public String viewDiscoverContainerListing(Model model) {

		List<Object[]> results = DiscoverContainerRepository.fetchContainerWithUserLab();

		// Map raw Object[] results to a DTO or Map to pass to view
		List<ContainerUserLabDTO> dtoList = results.stream().map(record -> {
			ContainerUserLabDTO dto = new ContainerUserLabDTO();

			dto.setId((Integer) record[0]);
			dto.setContainerId((String) record[1]);
			dto.setImageName((String) record[2]);
			dto.setCommand((String) record[3]);
			dto.setCreated(record[4] != null ? (Timestamp) record[4] : null);
			dto.setStatus((String) record[5]);
			dto.setPorts((String) record[6]);
			dto.setContainerName((String) record[7]);
			dto.setPhysicalServerIp((String) record[8]);

			String username = (String) record[9];
			dto.setUsername(username != null ? username : "unassigned");

			String scenarioName = (String) record[10];
			dto.setScenarioName(scenarioName != null ? scenarioName : "-");

			Timestamp lastActive = record[11] != null ? (Timestamp) record[11] : null;
			dto.setLastActiveConnection(lastActive);

			dto.setGuacamoleId(record[12] != null ? String.valueOf(record[12]) : null);

//	        dto.setGuacamoleId(guacamole_id);

			return dto;
		}).collect(Collectors.toList());

		model.addAttribute("listObj", dtoList);
		return "View_DiscoverContainerListing";
	}

	@GetMapping("/View_Vm_Listing")
	public ModelAndView viewVmListing(@RequestParam("Id") int scenarioId, Model model, Principal principal) {
		System.out.println("Requested scenario Id = " + scenarioId);

		Authentication auth = (Authentication) principal;
		String username = auth.getName();

		ModelAndView mav = new ModelAndView("View_Vm_Listing");

		List<UserLab> labs = UserLabRepository.findByScenarioIdAndUsername(scenarioId, username);

		// Add percentage for each lab
		List<Map<String, Object>> labData = new ArrayList<>();
		for (UserLab lab : labs) {
			Map<String, Object> map = new HashMap<>();
			map.put("lab", lab);

			// Fetch CloudInstance by instance name
			String instanceName = lab.getTemplateName();
			List<CloudInstance> cloudInstances = repository.findByInstanceName(instanceName);

			if (!cloudInstances.isEmpty()) {
				CloudInstance instance = cloudInstances.get(0); // ✅ first match

				String templateName = instance.getInstance_name();
				String password = instance.getInstance_password();
				String os = instance.getSubproduct_id().getProduct_id().getProduct_name();

				// You can store them in the map if needed
//	            map.put("templateName", templateName);
//	            map.put("password", password);
				map.put("os", os);
			} else {
				System.out.println("No CloudInstance found for name: " + instanceName);
			}

			// Handle LabId
			Long labIdLong = lab.getLabId();
			int labId = labIdLong.intValue();

			Integer falseCountObj = instructionTemplateRepository.getfalseCompletionCountsByTemplateName(labId);
			Integer trueCountObj = instructionTemplateRepository.gettrueCompletionCountsByTemplateName(labId);

			// Handle null values
			int falseCount = (falseCountObj != null) ? falseCountObj : 0;
			int trueCount = (trueCountObj != null) ? trueCountObj : 0;

			int total = trueCount + falseCount;

			// Calculate percentage (avoid division by zero)
			int percentage = (total == 0) ? 0 : (trueCount * 100 / total);
			map.put("percentage", percentage);

			labData.add(map);

			System.out.println("labData ::" + labData);
		}

		model.addAttribute("labData", labData);
		return mav;
	}

	@GetMapping("/view/{id}")
	public String viewConnection(@PathVariable String id, Model model) {
		String url = guacService.getEmbedUrl(id);
		model.addAttribute("embedUrl", url);
		return "view";
	}

//	@GetMapping("/viewPlaylistConnection/{id}")
//	public String viewPlaylistConnection(@PathVariable String id, Model model) {
//
//		String url = null;
//		try {
//
//			UserLab lab = UserLabRepository.getByProxmoxId(Integer.valueOf(id));
//			CloudInstance inst = repository.findByInstance(lab.getTemplateName());
//
//			String identifier = GuacIdentifierUtil.encode(id, "mysql");
//			url = guacService.getEmbedUrl(identifier);
//
//			model.addAttribute("embedUrl", url);
//
//			List<CloudInstance> instances = null;
//			instances = repository.findByGuacamoleId(id);
//
//			model.addAttribute("instructionsdata", instances);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return "viewConnection";
//	}

	@GetMapping("/viewPlaylistConnection/{id}")
	public String viewPlaylistConnection(@PathVariable String id, Model model) {
		String url = null;
		try {
			UserLab lab = UserLabRepository.getByProxmoxId(Integer.valueOf(id));
			CloudInstance inst = repository.findByInstance(lab.getTemplateName());

			String identifier = GuacIdentifierUtil.encode(id, "mysql");
			url = guacService.getEmbedUrl(identifier);

			model.addAttribute("embedUrl", url);

			List<CloudInstance> instances = repository.findByGuacamoleId(id);
			model.addAttribute("instructionsdata", instances);

			// ✅ Add OS to the model
			if (inst != null) {
				String os = inst.getSubproduct_id().getProduct_id().getProduct_name();
				model.addAttribute("os", os);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "viewConnection";
	}

	@PostMapping("/ActionloadInitialInstruction")
	@ResponseBody
	public Map<String, Object> ActionloadInitialInstruction(@RequestParam String labId) {
		Map<String, Object> response = new HashMap<>();

		try {
			// Convert labId to Integer
			int temp = Integer.parseInt(labId);

			List<UserLab> labDetails = UserLabRepository.findByguacamoleId(temp);

			if (!labDetails.isEmpty()) {
				UserLab userLab = labDetails.get(0);
//				String TemplateName = userLab.getTemplateName();
//				Long laabId = userLab.getLabId();
				Long labIdLong = userLab.getLabId();
				int laabId = labIdLong.intValue();

				String LabName = userLab.getInstanceName();
//				System.out.println("labName ::" + labName);
				// Fetch instructions for this lab
				List<UserWiseChatBoatInstructionTemplate> datalist = instructionTemplateRepository.findBylabId(laabId);

				if (datalist != null && !datalist.isEmpty()) {

					List<Map<String, String>> instructionList = new ArrayList<>();
					for (UserWiseChatBoatInstructionTemplate inst : datalist) {
						Map<String, String> map = new HashMap<>();
						map.put("command", inst.getInstructionCommand());
						map.put("instructionDetails", new String(inst.getInstructionDetails(), StandardCharsets.UTF_8));
						instructionList.add(map);
					}

					response.put("success", true);
					response.put("instructions", instructionList);
				} else {
					response.put("success", false);
					response.put("error", "No instructions found for this lab.");
				}
			} else {
				response.put("success", false);
				response.put("error", "Lab not found.");
			}
//			System.out.println("response ::" + response);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("error", "Failed to load instructions.");
		}

		return response;
	}

//	// Helper method to determine OS based on template name or lab name
	private String determineOS(String templateName, String labName) {

		String value = "";
		System.out.println("labName_value ::: " + labName);

		if (labName != null) {
			String lab = labName;

			if (lab.equalsIgnoreCase("windows")) {

				value = "windows";
			} else if (lab.equalsIgnoreCase("Kali Linux")) {
				value = "kali Linux";
			}

		}
		System.out.println("check_value ::: " + value);
		// Default to linux
		return value;
	}

	private String getMandatoryCommand(String osType, String labName) {
		if ("windows".equalsIgnoreCase(osType)) {

			System.out.println("inside_Windows ::: ");
			return "[System.Environment]::SetEnvironmentVariable(\"container\", \"" + labName + "\", \"Machine\")";
		} else {

			System.out.println("inside_Linux ::: ");
			return "source /etc/bash.bashrc";
		}
	}

	private String getMandatoryInstruction(String osType) {
		String osDisplay = "Kali Linux";
		if ("windows".equalsIgnoreCase(osType)) {
			osDisplay = "Windows";
		}
		// For any non-Windows OS, show Linux/Kali instruction
		return "<div class='system-message'>" + "<i class='fas fa-info-circle'></i> "
				+ "<strong>Mandatory Setup Command</strong><br>" + "Please execute this " + osDisplay
				+ " environment setup command before proceeding with the lab instructions." + "</div>";
	}

//	// Helper method to check if commands match (with flexible comparison)
	private boolean isCommandMatch(String historyCommand, String currentCommand) {
		if (historyCommand == null || currentCommand == null)
			return false;

		// Normalize both commands for comparison
		String normalizedHistory = historyCommand.replaceAll("\\s+", " ").replace("'", "\"").trim().toLowerCase();

		String normalizedCurrent = currentCommand.replaceAll("\\s+", " ").replace("'", "\"").trim().toLowerCase();

		return normalizedHistory.equals(normalizedCurrent);
	}

//
	private boolean isMandatoryCommand(String command) {
		if (command == null)
			return false;

		String trimmedCommand = command.trim();

		// Normalize the command
		String normalizedCommand = trimmedCommand.replaceAll("\\s+", " ").replace("'", "\"").trim().toLowerCase();

		// Linux mandatory command
		String linuxCommand = "source /etc/bash.bashrc";
		String normalizedLinux = linuxCommand.replaceAll("\\s+", " ").trim().toLowerCase();

		// Windows mandatory command pattern (generic, without lab name)
		String windowsCommandPattern = "setenvironmentvariable(\"container\",";
		String normalizedWindowsPattern = windowsCommandPattern.replaceAll("\\s+", " ").trim().toLowerCase();

		System.out.println("Checking mandatory command:");
		System.out.println("Normalized input: " + normalizedCommand);
		System.out.println("Linux pattern: " + normalizedLinux);
		System.out.println("Windows pattern: " + normalizedWindowsPattern);

		boolean isLinuxMatch = normalizedCommand.equals(normalizedLinux);
		boolean isWindowsMatch = normalizedCommand.contains(normalizedWindowsPattern);

		System.out.println("Is Linux match: " + isLinuxMatch);
		System.out.println("Is Windows match: " + isWindowsMatch);

		return isLinuxMatch || isWindowsMatch;
	}

//	@PostMapping("/chatloadInitialInstruction")
//	@ResponseBody
//	public Map<String, Object> chatloadInitialInstruction(@RequestParam String labId) {
//	    Map<String, Object> response = new HashMap<>();
//	    try {
//	        int temp = Integer.parseInt(labId);
//
//	        List<UserLab> labDetails = UserLabRepository.findByguacamoleId(temp);
//
//	        if (!labDetails.isEmpty()) {
//	            UserLab userLab = labDetails.get(0);
//	            Long labIdLong = userLab.getLabId();
//	            int laabId = labIdLong.intValue();
//	            String  containerName = userLab.getInstanceName();
//
//	            // Fetch next instruction
//	            UserWiseChatBoatInstructionTemplate instructionCommand =
//	                    instructionTemplateRepository.findNextUnexecutedByLabId(laabId);
//
//	            if (instructionCommand != null) {
//
//	                int templateId = instructionCommand.getTemplateId();
//	                CloudInstance tempid = repository.getById(templateId);
//
//	                String os = tempid.getSubproduct_id().getProduct_id().getProduct_name();
//	                String newCommand;
//
//	                // Key to check if mandatory was already executed
//	                String mandatoryKey = labId + "~mandatory";
//	                boolean mandatoryExecuted = lastCommandMap.containsKey(mandatoryKey);
//
//	                if (!mandatoryExecuted) {
//	                    // Show mandatory command only once // Kali Linux
//	                    if (os.equalsIgnoreCase("Windows")) {
//	                        newCommand = "source /etc/bash.bashrc";
//	                        response.put("instructionText", "Execute this command first to get started.");
//	                    } else if (os.equalsIgnoreCase("WindowsLabTemplate")) {
//	                        newCommand = "SetEnvironmentVariable('container', containerName, 'Machine')"; 
//	                        response.put("instructionText", "Execute this command first to get started.");
//	                    } else {
//	                        // If other OS, just skip mandatory logic
//	                        newCommand = instructionCommand.getInstructionCommand();
//	                        byte[] htmlBytes = instructionCommand.getInstructionDetails();
//	                        String decodedHtml = new String(htmlBytes, StandardCharsets.UTF_8);
//	                        response.put("instructionText", decodedHtml);
//	                    }
//
//	                    // ✅ Mark mandatory command as executed so it won't be shown again
//	                    lastCommandMap.put(mandatoryKey, "done");
//
//	                } else {
//	                    // Normal DB command flow
//	                    newCommand = instructionCommand.getInstructionCommand();
//	                    byte[] htmlBytes = instructionCommand.getInstructionDetails();
//	                    String decodedHtml = new String(htmlBytes, StandardCharsets.UTF_8);
//	                    response.put("instructionText", decodedHtml);
//	                }
//
//	                response.put("success", true);
//	                response.put("command", newCommand);
//	                response.put("isLast", false);
//
//	                // Save last executed command (for duplicate detection)
//	                lastCommandMap.put(labId + "~last", newCommand);
//
//	            } else {
//	                response.put("success", false);
//	                response.put("error", "No unexecuted instructions found.");
//	            }
//
//	        } else {
//	            response.put("success", false);
//	            response.put("error", "Lab not found.");
//	        }
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        response.put("success", false);
//	        response.put("error", "Failed to load instruction.");
//	    }
//
//	    return response;
//	}

//	@PostMapping("/continueActionInstruction")
//	@ResponseBody
//	public Map<String, Object> continueActionInstruction(@RequestParam String labId, @RequestParam String labcommand) {
//
//		Map<String, Object> response = new HashMap<>();
//
//		try {
//			int gucamleid = Integer.parseInt(labId);
//
//			List<UserLab> labDetails = UserLabRepository.findByguacamoleId(gucamleid);
//
//			if (!labDetails.isEmpty()) {
//				UserLab userLab = labDetails.get(0);
////				Long laabId = userLab.getLabId();
//				String LabName = userLab.getInstanceName();
//
//				Long labIdLong = userLab.getLabId();
//				int laabId = labIdLong.intValue();
//				System.out.println("LabId ::" + laabId);
//
//				List<CommandHistory> commandHistoryList = CommandHistoryRepository.findByContainerName(LabName,
//						labcommand);
//
//				boolean commandMatched = false;
//				for (CommandHistory ch : commandHistoryList) {
//					if (ch.getCommand().equalsIgnoreCase(labcommand)) {
//						commandMatched = true;
//						break;
//					}
//				}
//
//				if (commandMatched) {
//					// Case 1: Command found in history
//					System.out.println("Inside: command matched in history");
//
//					int updatedRows = instructionTemplateRepository.modifyCommandByLabId(laabId, labcommand);
//
//					if (updatedRows > 0) {
//
//						UserWiseChatBoatInstructionTemplate instructionCommand = instructionTemplateRepository
//								.findNextUnexecutedByLabId(laabId);
//						System.out.println("Lab Updatedd.");
//						if (instructionCommand != null) {
//							byte[] htmlBytes = instructionCommand.getInstructionDetails();
//							String decodedHtml = new String(htmlBytes, StandardCharsets.UTF_8);
//
//							response.put("success", true);
//							response.put("instruction", decodedHtml);
//							response.put("command", instructionCommand.getInstructionCommand());
//							response.put("isLast", false);
//							System.out.println("Lab Next Command.");
//						} else {
//							// No more instructions → Lab completed
//							System.out.println("Lab not completed.");
//							response.put("success", true);
//							response.put("instruction", "Lab is completed.");
//							response.put("command", "");
//							response.put("isLast", true);
//						}
//					} else {
//						System.out.println("Execute this command first.");
//						response.put("success", true);
//						response.put("instruction", "Execute this command first.");
//						response.put("command", labcommand);
//						response.put("isLast", false);
//					}
//
//				} else {
//					response.put("success", true);
//					response.put("instruction", "Execute this command first.");
//					response.put("command", labcommand);
//					response.put("isLast", false);
//				}
//
//			} else {
//				System.out.println("Lab not found.");
//				response.put("success", false);
//				response.put("error", "Lab not found.");
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("Failed to load process Lab not found.");
//			response.put("success", false);
//			response.put("error", "Failed to process command.");
//		}
//		System.out.println("response Lab " + response);
//		return response;
//	}

	// Helper method to check if a command is a mandatory command

//	
//	private boolean isMandatoryCommand(String osType, String labName) {
//	    if ("windows".equalsIgnoreCase(osType)) {
//	        // Windows environment setup command
//	        return "SetEnvironmentVariable('container','" + labName + "', 'Machine')";
//	    } else {
//	        // Linux / Kali Linux environment setup command
//	        return "source /etc/bash.bashrc";
//	    }
//	}

	@PostMapping("/InstructionCompleted")
	@ResponseBody
	public String instructionCompleted(@RequestParam("LabId") String labId, Principal principal) {
		String result = "fail";
		try {
			System.out.println("labId : : " + labId);

			int temp = Integer.parseInt(labId);

			Authentication auth = (Authentication) principal;
			String username = auth.getName();

			// Update lab status

			List<UserLab> labDetails = UserLabRepository.findByguacamoleId(temp);

			if (!labDetails.isEmpty()) {
				UserLab userLab = labDetails.get(0);
				Long Id = userLab.getLabId();

				int updatedRows = UserLabRepository.updateStatusById(Id);

				if (updatedRows > 0) {
					result = "success";
				} else {
					result = "fail";
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "Error";

		}

		return result;
	}

	@PostMapping("/getConnectionDeatils")
	@ResponseBody
	public Map<String, Object> getConnectionDeatils(@RequestParam("LabId") String LabId, Principal principal) {
		Map<String, Object> response = new HashMap<>();

		try {
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			String username = loginedUser.getUsername();

			// Fetch lab details
			int temp = Integer.parseInt(LabId);

			List<UserLab> labDetails = UserLabRepository.findByguacamoleId(temp);

			if (!labDetails.isEmpty()) {
				UserLab userLab = labDetails.get(0);

				String instanceName = userLab.getTemplateName();

				List<CloudInstance> cloudInstances = repository.findByInstanceName(instanceName);
				String password = "";
				if (!cloudInstances.isEmpty()) {
					CloudInstance instance = cloudInstances.get(0); // ✅ first match

					String templateName = instance.getInstance_name();
					password = instance.getInstance_password();
					String os = instance.getSubproduct_id().getProduct_id().getProduct_name();

					// You can store them in the map if needed
//		            map.put("templateName", templateName);
//		            map.put("password", password);

				}

				response.put("success", true);
				response.put("username", userLab.getInstanceUser());
				response.put("password", password);
				response.put("labName", userLab.getInstanceName());
				response.put("ipAddress", userLab.getIpAddress());
			} else {
				response.put("success", false);
				response.put("error", "No lab details found for UserLabId " + LabId);
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("error", "Exception occurred: " + e.getMessage());
		}

		return response;
	}

//	@PostMapping("/checkLabCompletionStatus")
//	@ResponseBody
//	public Map<String, Object> checkLabCompletionStatus(@RequestParam("labId") String labId, Principal principal) {
//	    Map<String, Object> response = new HashMap<>();
//	    
//	    try {
//	        int guacamoleId = Integer.parseInt(labId);
//	        
//	        // Get the lab details
//	        List<UserLab> labDetails = UserLabRepository.findByguacamoleId(guacamoleId);
//	        
//	        if (labDetails.isEmpty()) {
//	            response.put("completed", false);
//	            response.put("error", "Lab not found");
//	            return response;
//	        }
//	        
//	        UserLab userLab = labDetails.get(0);
//	        String labName = userLab.getInstanceName();
//	        Long labIdLong = userLab.getLabId();
//	        int internalLabId = labIdLong.intValue();
//	        
//	        // Check if all instructions are completed for this lab
//	        List<UserWiseChatBoatInstructionTemplate> allInstructions = 
//	            instructionTemplateRepository.findBylabId(internalLabId);
//	        
//	        List<UserWiseChatBoatInstructionTemplate> unexecutedInstructions = 
//	            instructionTemplateRepository.findUnexecutedByLabId(internalLabId);
//	        
//	        // If there are no unexecuted instructions, the lab is completed
//	        boolean isCompleted = unexecutedInstructions.isEmpty() && !allInstructions.isEmpty();
//	        
//	        response.put("completed", isCompleted);
//	        
//	        if (isCompleted) {
//	            // Get completion time if available
//	            // You might need to add this to your UserLab entity or create a separate table
////	            response.put("completionDate", userLab.getCompletionDate() != null ? 
////	                userLab.getCompletionDate().toString() : new Date().toString());
//	        }
//	        
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        response.put("completed", false);
//	        response.put("error", "Error checking lab status");
//	    }
//	    
//	    return response;
//	}
//	
//	@PostMapping("/getCompletedLabChatData")
//	@ResponseBody
//	public Map<String, Object> getCompletedLabChatData(@RequestParam("labId") String labId, Principal principal) {
//	    Map<String, Object> response = new HashMap<>();
//	    
//	    try {
//	        int guacamoleId = Integer.parseInt(labId);
//	        
//	        // Get the lab details
//	        List<UserLab> labDetails = UserLabRepository.findByguacamoleId(guacamoleId);
//	        
//	        if (labDetails.isEmpty()) {
//	            response.put("success", false);
//	            response.put("error", "Lab not found");
//	            return response;
//	        }
//	        
//	        UserLab userLab = labDetails.get(0);
//	        String labName = userLab.getInstanceName();
//	        Long labIdLong = userLab.getLabId();
//	        int internalLabId = labIdLong.intValue();
//	        
//	        // Get all executed commands for this lab
//	        List<CommandHistory> commandHistory = CommandHistoryRepository.findByContainerName(labName);
//	        
//	        // Format chat data
//	        List<Map<String, Object>> chatData = new ArrayList<>();
//	        for (CommandHistory command : commandHistory) {
//	            Map<String, Object> chatItem = new HashMap<>();
//	            chatItem.put("message", "Command: " + command.getCommand());
//	            chatItem.put("timestamp", command.getEventTimestamp());
//	            chatData.add(chatItem);
//	        }
//	        
//	        // Get all instructions to include in chat
//	        List<UserWiseChatBoatInstructionTemplate> allInstructions = 
//	            instructionTemplateRepository.findBylabId(internalLabId);
//	        
//	        for (UserWiseChatBoatInstructionTemplate instruction : allInstructions) {
//	            Map<String, Object> chatItem = new HashMap<>();
//	            byte[] htmlBytes = instruction.getInstructionDetails();
//	            String decodedHtml = new String(htmlBytes, StandardCharsets.UTF_8);
//	            chatItem.put("message", "Instruction: " + decodedHtml);
//	            chatItem.put("timestamp", new Date()); // Use appropriate timestamp if available
//	            chatData.add(chatItem);
//	        }
//	        
//	        // Sort by timestamp if available
//	        chatData.sort((a, b) -> {
//	            Date dateA = (Date) a.get("timestamp");
//	            Date dateB = (Date) b.get("timestamp");
//	            return dateA != null && dateB != null ? dateA.compareTo(dateB) : 0;
//	        });
//	        
//	        response.put("success", true);
//	        response.put("chatData", chatData);
////	        response.put("completionDate", userLab.getCompletionDate() != null ? 
////	            userLab.getCompletionDate() : new Date());
//	        
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        response.put("success", false);
//	        response.put("error", "Error loading completed lab data");
//	    }
//	    
//	    return response;
//	}

	@PostMapping("/chatloadInitialInstruction")
	@ResponseBody
	public Map<String, Object> chatloadInitialInstruction(@RequestParam String labId) {
		Map<String, Object> response = new HashMap<>();

		try {
			System.out.println("chatlabId ::" + labId);
			int temp = Integer.parseInt(labId);

			List<UserLab> labDetails = UserLabRepository.findByguacamoleId(temp);

			if (!labDetails.isEmpty()) {
				UserLab userLab = labDetails.get(0);
				String LabName = userLab.getInstanceName();
				String TemplateName = userLab.getTemplateName();
				Long labIdLong = userLab.getLabId();
				int laabId = labIdLong.intValue();
				System.out.println("laabId ::" + laabId);

				// Check if mandatory command has been executed for this lab FROM DATABASE
				boolean mandatoryExecuted = userLab.getMandatoryCommandExecuted();
				System.out.println("Mandatory command executed status from DB: " + mandatoryExecuted);

				String instanceName = userLab.getTemplateName();
				List<CloudInstance> cloudInstances = repository.findByInstanceName(instanceName);
				String os = "";
				if (!cloudInstances.isEmpty()) {
					CloudInstance instance = cloudInstances.get(0);
					os = instance.getSubproduct_id().getProduct_id().getProduct_name();
				}

				// Determine OS and set mandatory command
				String osType = determineOS(TemplateName, os);
				String mandatoryCommand = getMandatoryCommand(osType, LabName);
				String mandatoryInstruction = getMandatoryInstruction(osType);

				// If mandatory command not executed yet, return it
				if (!mandatoryExecuted) {
					response.put("success", true);
					response.put("instructionText", mandatoryInstruction);
					response.put("command", mandatoryCommand);
					response.put("isMandatory", true);
					response.put("isLast", false);
					return response;
				}

				// If mandatory command already executed, proceed with normal instructions
				UserWiseChatBoatInstructionTemplate instructionCommand = instructionTemplateRepository
						.findNextUnexecutedByLabId(laabId);

				if (instructionCommand != null) {
					System.out.println("LabName_if ::" + LabName);
					String newCommand = instructionCommand.getInstructionCommand();

					byte[] htmlBytes = instructionCommand.getInstructionDetails();
					String decodedHtml = new String(htmlBytes, StandardCharsets.UTF_8);

					response.put("success", true);
					response.put("instructionText", decodedHtml);
					response.put("command", newCommand);
					response.put("isMandatory", false);
					response.put("isLast", false);

				} else {
					response.put("success", false);
					response.put("error", "No unexecuted instructions found.");
				}

			} else {
				response.put("success", false);
				response.put("error", "Lab not found.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("error", "Failed to load instruction.");
		}

		return response;
	}

	@PostMapping("/continueActionInstruction")
	@ResponseBody
	public Map<String, Object> continueActionInstruction(@RequestParam String labId, @RequestParam String labcommand) {

		Map<String, Object> response = new HashMap<>();

		try {
			int gucamleid = Integer.parseInt(labId);

			List<UserLab> labDetails = UserLabRepository.findByguacamoleId(gucamleid);

			if (!labDetails.isEmpty()) {
				UserLab userLab = labDetails.get(0);
				String LabName = userLab.getInstanceName();
				Long labIdLong = userLab.getLabId();
				int laabId = labIdLong.intValue();
				System.out.println("LabId ::" + laabId);
				System.out.println("LabName ::" + LabName);
				System.out.println("Received command: " + labcommand);

				// Check if this is a mandatory command
				boolean isMandatoryCommand = isMandatoryCommand(labcommand);

				System.out.println("Is mandatory command: " + isMandatoryCommand);
				String os = "";
				if (isMandatoryCommand) {
					System.out.println("Mandatory command detected: " + labcommand);

					String instanceName = userLab.getTemplateName();
					List<CloudInstance> cloudInstances = repository.findByInstanceName(instanceName);

					if (!cloudInstances.isEmpty()) {
						CloudInstance instance = cloudInstances.get(0); // ✅ first match

						os = instance.getSubproduct_id().getProduct_id().getProduct_name();

					}
					List<CommandHistory> mandatoryCommandHistory;
					if (os.equalsIgnoreCase("Windows")) {
						System.out.println("Mandatory command Windows: " + labcommand);
						mandatoryCommandHistory = CommandHistoryRepository.findByCommand(labcommand);

					} else {
						System.out.println("Mandatory command Linux: " + labcommand);
						mandatoryCommandHistory = CommandHistoryRepository.findByContainerNameAndCommand(LabName,
								labcommand);
					}

					boolean mandatoryCommandExecuted = false;

					for (CommandHistory ch : mandatoryCommandHistory) {
						if (isCommandMatch(ch.getCommand(), labcommand)) {
							mandatoryCommandExecuted = true;
							break;
						}
					}

					if (mandatoryCommandExecuted) {
						// Mandatory command found in history - update database status
						System.out.println("Mandatory command found in command history");

						// UPDATE DATABASE STATUS
						UserLabRepository.updateMandatoryCommandStatus(gucamleid, true);

						// Reload userLab to get updated status
						labDetails = UserLabRepository.findByguacamoleId(gucamleid);
						userLab = labDetails.get(0);

						// Get the first actual instruction after mandatory command
						UserWiseChatBoatInstructionTemplate instructionCommand = instructionTemplateRepository
								.findNextUnexecutedByLabId(laabId);

						if (instructionCommand != null) {
							byte[] htmlBytes = instructionCommand.getInstructionDetails();
							String decodedHtml = new String(htmlBytes, StandardCharsets.UTF_8);

							response.put("success", true);
							response.put("instruction", decodedHtml);
							response.put("command", instructionCommand.getInstructionCommand());
							response.put("isLast", false);
							System.out.println("First instruction after mandatory command loaded.");
						} else {
							response.put("success", true);
							response.put("instruction", "Lab setup completed. No further instructions found.");
							response.put("command", "");
							response.put("isLast", true);
						}
					} else {
						System.out.println("Mandatory command not found in command history. Please execute it first.");
						response.put("success", true);
						response.put("instruction",
								"Please execute the mandatory setup command first before proceeding.");
						response.put("command", labcommand);
						response.put("isLast", false);
					}

				} else {
					// Normal command flow
					System.out.println("Processing as normal command...");

					List<CommandHistory> commandHistoryList = CommandHistoryRepository.findByContainerName(LabName,
							labcommand);

					boolean commandMatched = false;
					for (CommandHistory ch : commandHistoryList) {
						if (isCommandMatch(ch.getCommand(), labcommand)) {
							commandMatched = true;
							break;
						}
					}

					if (commandMatched) {
						System.out.println("Inside: command matched in history");

						int updatedRows = instructionTemplateRepository.modifyCommandByLabId(laabId, labcommand);

						if (updatedRows > 0) {
							UserWiseChatBoatInstructionTemplate instructionCommand = instructionTemplateRepository
									.findNextUnexecutedByLabId(laabId);

							if (instructionCommand != null) {
								byte[] htmlBytes = instructionCommand.getInstructionDetails();
								String decodedHtml = new String(htmlBytes, StandardCharsets.UTF_8);

								response.put("success", true);
								response.put("instruction", decodedHtml);
								response.put("command", instructionCommand.getInstructionCommand());
								response.put("isLast", false);
							} else {
								response.put("success", true);
								response.put("instruction", "Lab is completed.");
								response.put("command", "");
								response.put("isLast", true);
							}
						} else {
							response.put("success", true);
							response.put("instruction", "Execute this command first.");
							response.put("command", labcommand);
							response.put("isLast", false);
						}

					} else {
						response.put("success", true);
						response.put("instruction", "Execute this command first.");
						response.put("command", labcommand);
						response.put("isLast", false);
					}
				}

			} else {
				response.put("success", false);
				response.put("error", "Lab not found.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("error", "Failed to process command.");
		}

		return response;
	}

	// Remove the in-memory map since we're using database now
	// private final ConcurrentHashMap<String, String> lastCommandMap = new
	// ConcurrentHashMap<>();

	@PostMapping("/getCompletedLabInstructions")
	@ResponseBody
	public Map<String, Object> getCompletedLabInstructions(@RequestParam("labId") String labId, Principal principal) {
		Map<String, Object> response = new HashMap<>();

		try {
			int guacamoleId = Integer.parseInt(labId);

			// Get the lab details
			List<UserLab> labDetails = UserLabRepository.findByguacamoleId(guacamoleId);

			if (labDetails.isEmpty()) {
				response.put("success", false);
				response.put("error", "Lab not found");
				return response;
			}

			UserLab userLab = labDetails.get(0);
			Long labIdLong = userLab.getLabId();
			int internalLabId = labIdLong.intValue();

			// Get all instructions for this lab
			List<UserWiseChatBoatInstructionTemplate> allInstructions = instructionTemplateRepository
					.findBylabId(internalLabId);

			List<Map<String, Object>> instructions = new ArrayList<>();
			for (UserWiseChatBoatInstructionTemplate instruction : allInstructions) {
				Map<String, Object> instructionData = new HashMap<>();
				byte[] htmlBytes = instruction.getInstructionDetails();
				String decodedHtml = new String(htmlBytes, StandardCharsets.UTF_8);

				instructionData.put("instructionDetails", decodedHtml);
				instructionData.put("command", instruction.getInstructionCommand());
				instructionData.put("isExecuted", instruction.getIsCommandExecuted());

				instructions.add(instructionData);
			}

			response.put("success", true);
			response.put("instructions", instructions);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("error", "Error loading completed instructions");
		}

		return response;
	}

//	@GetMapping("/Add_Playlist")
//	public ModelAndView showAddPlaylistForm() {
//		ModelAndView mav = new ModelAndView("Add_Playlist");
////		pageTitle
//		mav.addObject("pageTitle", "Create New Playlist");
//
//		mav.addObject("playlist", new Playlist()); // This is crucial!
//		return mav;
//	}

	@GetMapping("/Add_Playlist")
	public String showCreatePlaylistForm(@RequestParam(value = "Id", required = false) Integer id, Model model) {
		Playlist playlist = (id != null) ? PlaylistRepository.findById(id).orElse(new Playlist()) : new Playlist();
		model.addAttribute("playlist", playlist);

		model.addAttribute("pageTitle", (id != null) ? "Edit Playlist" : "Create Playlist");
		return "Add_Playlist";
	}

	@GetMapping("/Add_Category")
	public String showAddCategoryForm(@RequestParam(value = "Id", required = false) Integer id, Model model) {
		CategoryMaster category = null;

		if (id != null) {
			Optional<CategoryMaster> optionalCategory = CategoryMasterRepository.findById(id);
			if (optionalCategory.isPresent()) {
				category = optionalCategory.get();
				System.out.println("Editing category: " + category.getCategory());
			} else {
				System.out.println("Category not found with id: " + id);
			}
		}

		if (category == null) {
			category = new CategoryMaster();
			System.out.println("Creating new category.");
		}

		model.addAttribute("categoryObj", category);
		model.addAttribute("pageTitle", (id != null) ? "Edit Category" : "Create Category");

		return "Add_Category";
	}

	@PostMapping("/categorysave")
	public String saveCategory(@ModelAttribute("categoryObj") CategoryMaster category,
			RedirectAttributes redirectAttributes) {
		try {
			boolean isNew = (category.getCategoryId() == 0);
			CategoryMasterRepository.save(category);
			String message = isNew ? "Category created successfully." : "Category updated successfully.";
			redirectAttributes.addFlashAttribute("successMessage", message);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Failed to save category: " + e.getMessage());
			return "redirect:/guac/Add_Category";
		}

		return "redirect:/guac/View_Category";
	}

	@GetMapping("/View_Category")
	public String listCategories(Model model) {
		List<CategoryMaster> categories = CategoryMasterRepository.findAll();
		model.addAttribute("categories", categories);
		model.addAttribute("pageTitle", "Category List");
		return "View_Category";
	}

	@GetMapping("/deleteCategory")
	public String deleteCategory(@RequestParam("Id") Integer id) {
		CategoryMasterRepository.deleteById(id);
		return "redirect:/guac/View_Category";
	}

	@GetMapping("/Add_Department")
	public String showAddDepartmentForm(@RequestParam(value = "Id", required = false) Integer id, Model model) {
		DepartmentMaster department = null;

		if (id != null) {
			Optional<DepartmentMaster> optionalDepartment = DepartmentMasterRepository.findById(id);
			if (optionalDepartment.isPresent()) {
				department = optionalDepartment.get();
			}
		}

		if (department == null) {
			department = new DepartmentMaster();
		}

		model.addAttribute("department", department);
		model.addAttribute("pageTitle", (id != null) ? "Edit Department" : "Create Department");

		return "Add_Department";
	}

	@PostMapping("/saveDepartment")
	public String saveDepartment(@ModelAttribute("department") DepartmentMaster department) {
		System.out.println("Inside_saveDepartment :: " + department);

		DepartmentMaster savedDepartment = DepartmentMasterRepository.save(department);

		if (savedDepartment.getDepartmentId() == 0) {
			// This case normally shouldn't happen after save, but just in case
			return "redirect:/guac/Add_Department";
		} else {
			// If departmentId exists (updated or newly created), redirect to view page
			return "redirect:/guac/View_Department?Id=" + savedDepartment.getDepartmentId();
		}
	}

	@GetMapping("/View_Department")
	public String listDepartments(Model model) {
		List<DepartmentMaster> departments = DepartmentMasterRepository.findAll();
		model.addAttribute("departments", departments);
		model.addAttribute("pageTitle", "Department List");
		return "View_Department"; // new Thymeleaf template for listing
	}

	@GetMapping("/deleteDepartment")
	public String deleteDepartment(@RequestParam("Id") Integer id) {
		DepartmentMasterRepository.deleteById(id);
		return "redirect:/departments"; // redirect to listing after delete
	}

	@GetMapping("/Add_Course")
	public String showAddCourseForm(@RequestParam(value = "Id", required = false) Integer id, Model model) {
		CourseMaster course = null;

		if (id != null) {
			Optional<CourseMaster> optionalCourse = CourseMasterRepository.findBycourseId(id);
			if (optionalCourse.isPresent()) {
				course = optionalCourse.get();
				if (course.getDepartment() != null) {
//	                course.setDepartmentId(course.getDepartment().getDepartmentId());

				}
			}
		}

		if (course == null) {
			course = new CourseMaster();
		}

		List<DepartmentMaster> departments = DepartmentMasterRepository.findAll();

		model.addAttribute("course", course);
		model.addAttribute("departments", departments);
		model.addAttribute("pageTitle", (id != null) ? "Edit Course" : "Create Course");

		return "Add_Course";
	}

	@PostMapping("/saveCourse")
	public String saveCourse(@ModelAttribute("course") CourseMaster course) {
		Integer deptId = (course.getDepartment() != null) ? course.getDepartment().getDepartmentId() : null;

		if (deptId != null) {
			DepartmentMaster department = DepartmentMasterRepository.findById(deptId).orElse(null);
			if (department != null) {
				course.setDepartment(department);

				CourseMasterRepository.save(course);

				return "redirect:/guac/View_Course";
			}
		}

		return "redirect:/guac/Add_Course";
	}

	@GetMapping("/View_Course")
	public String listCourses(Model model) {
		List<CourseMaster> courses = CourseMasterRepository.findAll(); // get courses
		model.addAttribute("courses", courses);
		model.addAttribute("pageTitle", "Course List");
		return "View_Course";
	}

	@GetMapping("/deleteCourse")
	public String deleteCourse(@RequestParam("Id") int courseId) {
		if (CourseMasterRepository.existsById(courseId)) {
			CourseMasterRepository.deleteById(courseId);
		}
		return "redirect:/guac/View_Course";
	}

	@GetMapping("/Add_Semester")
	public String showAddSemesterForm(@RequestParam(value = "Id", required = false) Integer id, Model model) {
		SemesterMaster semester = new SemesterMaster(); // default (for create)
		Integer selectedDepartmentId = null; // for pre-selecting department

		if (id != null) {
			Optional<SemesterMaster> optionalSemester = SemesterMasterRepository.findById(id);
			if (optionalSemester.isPresent()) {
				semester = optionalSemester.get();

				// Access the department from the course
				CourseMaster course = semester.getCourse();
				if (course != null && course.getDepartment() != null) {
					selectedDepartmentId = course.getDepartment().getDepartmentId();
				}
			}
		}

		// Load departments to populate the dropdown
		List<DepartmentMaster> departments = DepartmentMasterRepository.findAll();

		// Send data to the view
		model.addAttribute("semester", semester); // contains course
		model.addAttribute("departments", departments); // for department dropdown
		model.addAttribute("selectedDepartmentId", selectedDepartmentId); // for JS preselection
		model.addAttribute("pageTitle", (id != null) ? "Edit Semester" : "Create Semester");

		return "Add_Semester";
	}

	@PostMapping("/saveSemester")
	public String saveSemester(@ModelAttribute("semester") SemesterMaster semester) {

		boolean isNew = (semester.getSemesterId() == 0);

		// Fetch full Course entity if course is selected
		if (semester.getCourse() != null && semester.getCourse().getCourseId() != 0) {
			CourseMaster course = CourseMasterRepository.findById(semester.getCourse().getCourseId()).orElse(null);
			semester.setCourse(course);
		}

		SemesterMasterRepository.save(semester);

		if (isNew) {
			return "redirect:/guac/Add_Semester";
		} else {
			return "redirect:/guac/View_Semester";
		}
	}

	@GetMapping("/courses")
	@ResponseBody
	public List<Map<String, Object>> getCoursesByDepartment(@RequestParam("departmentId") Integer departmentId) {
		List<CourseMaster> courses = CourseMasterRepository.findByDepartment_DepartmentId(departmentId);

		return courses.stream().map(course -> {
			Map<String, Object> map = new HashMap<>();
			map.put("courseId", course.getCourseId());
			map.put("courseName", course.getCourseName());
			return map;
		}).collect(Collectors.toList());
	}

	@GetMapping("/View_Semester")
	public String listSemesters(Model model) {
		model.addAttribute("pageTitle", "Semester List");
		model.addAttribute("semesters", SemesterMasterRepository.findAll());
		return "View_Semester"; // path to your list HTML
	}

	@GetMapping("/deleteSemester")
	public String deleteSemester(@RequestParam("Id") int courseId) {
		if (SemesterMasterRepository.existsById(courseId)) {
			SemesterMasterRepository.deleteById(courseId);
		}
		return "redirect:/guac/View_Semester";
	}

	@GetMapping("/semesters")
	@ResponseBody
	public List<Map<String, Object>> getSemestersByCourse(@RequestParam("courseId") Integer courseId) {
		List<SemesterMaster> semesters = SemesterMasterRepository.findByCourse_CourseId(courseId);

		return semesters.stream().map(sem -> {
			Map<String, Object> map = new HashMap<>();
			map.put("semesterId", sem.getSemesterId());
			map.put("semesterName", sem.getSemesterName());
			return map;
		}).collect(Collectors.toList());
	}

	@GetMapping("/subjects")
	@ResponseBody
	public List<Map<String, Object>> getSubjectsBySemester(@RequestParam("semesterId") Integer semesterId) {
		List<SubjectMaster> subjects = SubjectMasterRepository.findBysemester_SemesterId(semesterId);

		return subjects.stream().map(sub -> {
			Map<String, Object> map = new HashMap<>();
			map.put("subjectId", sub.getSubjectId());
			map.put("subjectName", sub.getSubjectName());
			return map;
		}).collect(Collectors.toList());
	}

	@GetMapping("/Add_Subject")
	public String showAddSubjectForm(@RequestParam(value = "Id", required = false) Integer id, Model model) {
		SubjectMaster subject = new SubjectMaster();
		Integer selectedDepartmentId = null;
		Integer selectedCourseId = null;
		Integer selectedSemesterId = null;

		if (id != null) {
			Optional<SubjectMaster> optionalSubject = SubjectMasterRepository.findById(id);
			if (optionalSubject.isPresent()) {
				subject = optionalSubject.get();
				SemesterMaster sem = subject.getSemester();

				if (sem != null && sem.getCourse() != null && sem.getCourse().getDepartment() != null) {
					selectedDepartmentId = sem.getCourse().getDepartment().getDepartmentId();
					selectedCourseId = sem.getCourse().getCourseId();
					selectedSemesterId = sem.getSemesterId();
				}
			}
		} else {
			subject.setSemester(new SemesterMaster());
		}

		List<DepartmentMaster> departments = DepartmentMasterRepository.findAll();

		model.addAttribute("subject", subject);
		model.addAttribute("departments", departments);
		model.addAttribute("selectedDepartmentId", selectedDepartmentId);
		model.addAttribute("selectedCourseId", selectedCourseId);
		model.addAttribute("selectedSemesterId", selectedSemesterId);
		model.addAttribute("pageTitle", (id != null) ? "Edit Subject" : "Create Subject");

		return "Add_Subject";
	}

	@PostMapping("/saveSubject")
	public String saveSubject(@ModelAttribute("subject") SubjectMaster subject, BindingResult result, Model model) {

		if (result.hasErrors()) {
			List<DepartmentMaster> departments = DepartmentMasterRepository.findAll();
			model.addAttribute("departments", departments);
			model.addAttribute("pageTitle", (subject.getSubjectId() == 0) ? "Create Subject" : "Edit Subject");
			return "Add_Subject";
		}

		// 2. Validate semester
		if (subject.getSemester() == null || subject.getSemester().getSemesterId() == 0) {
			result.rejectValue("semester", "error.subject", "Semester is required");
			List<DepartmentMaster> departments = DepartmentMasterRepository.findAll();
			model.addAttribute("departments", departments);
			model.addAttribute("pageTitle", (subject.getSubjectId() == 0) ? "Create Subject" : "Edit Subject");
			return "Add_Subject";
		}

		// 3. Ensure semester exists
		Optional<SemesterMaster> semesterOpt = SemesterMasterRepository.findById(subject.getSemester().getSemesterId());
		if (!semesterOpt.isPresent()) {
			result.rejectValue("semester", "error.subject", "Selected semester does not exist");
			List<DepartmentMaster> departments = DepartmentMasterRepository.findAll();
			model.addAttribute("departments", departments);
			model.addAttribute("pageTitle", (subject.getSubjectId() == 0) ? "Create Subject" : "Edit Subject");
			return "Add_Subject";
		}

		subject.setSemester(semesterOpt.get());

		SubjectMasterRepository.save(subject);

		if (subject.getSubjectId() != 0) {

			return "redirect:/guac/View_Subject";
		} else {

			return "redirect:/guac/Add_Subject";
		}
	}

	@GetMapping("/View_Subject")
	public String showSubjects(Model model) {
		List<SubjectMaster> subjects = SubjectMasterRepository.findAll();

		model.addAttribute("subjects", subjects);
		model.addAttribute("pageTitle", "Subject List");

		return "View_Subject";
	}

	@GetMapping("/deleteSubject")
	public String deleteSubject(@RequestParam("Id") Integer subjectId, RedirectAttributes redirectAttrs) {
		try {
			SubjectMasterRepository.deleteById(subjectId);
			redirectAttrs.addFlashAttribute("message", "Subject deleted successfully.");
			redirectAttrs.addFlashAttribute("alertClass", "alert-success");
		} catch (Exception e) {
			redirectAttrs.addFlashAttribute("message", "Error deleting subject.");
			redirectAttrs.addFlashAttribute("alertClass", "alert-danger");
		}
		return "redirect:/guac/View_Subject";
	}

	@GetMapping("/Add_Sub_Playlist")
	public String showCreateForm(Model model) {
		model.addAttribute("playlist", new SubPlaylist());
		model.addAttribute("pageTitle", "Create New Sub Playlist");
		return "Add_Sub_Playlist";
	}

	@GetMapping("/Edit_Sub_Playlist/{id}")
	public String showEditForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		Optional<SubPlaylist> optional = SubPlaylistRepository.findById(id);
		if (optional.isPresent()) {
			model.addAttribute("playlist", optional.get());
			model.addAttribute("pageTitle", "Edit Sub Playlist");
		} else {
			redirectAttributes.addFlashAttribute("message", "Sub Playlist not found.");
			redirectAttributes.addFlashAttribute("status", "error");
			return "redirect:/guac/Add_Sub_Playlist";
		}
		return "Add_Sub_Playlist";
	}

	@GetMapping("/deletesubplaylist/{id}")
	public String deletesubplaylist(@PathVariable("id") Integer id) {

		try {
			ModelAndView mav = new ModelAndView("Add_Sub_Playlist");
//			mav.addObject("action_name", var_function_name);
//			mav.addObject("playlist", PlaylistRepository.deleteById(id));
			SubPlaylistRepository.deleteById(id);

//			mav.addObject("pageTitle",  "Edit " + disp_function_name + " (ID: " + id + ")");
//			return mav;
		} catch (Exception e) {
			ModelAndView mav = new ModelAndView("Add_Sub_Playlist");
//			mav.addObject("action_name", var_function_name);
			mav.addObject("message", e.getMessage());
//			return mav;
		}

		return "redirect:/guac/View_SubPalylist";
	}

//	    @GetMapping("/Edit_Sub_Playlist/{id}")
//		public ModelAndView showEditForm(@PathVariable("id") Integer id) {
//
//			try {
//				ModelAndView mav = new ModelAndView("Add_Sub_Playlist");
////				mav.addObject("action_name", var_function_name);
//				mav.addObject("playlist", SubPlaylistRepository.findById(id).get());
//				mav.addObject("pageTitle", "EEdit Sub Playlist (ID: " + id + ")");
////				mav.addObject("pageTitle",  "Edit " + disp_function_name + " (ID: " + id + ")");
//				return mav;
//			} catch (Exception e) {
//				ModelAndView mav = new ModelAndView("Add_Sub_Playlist");
////				mav.addObject("action_name", var_function_name);
//				mav.addObject("message", e.getMessage());
//				return mav;
//			}
//		}

	@GetMapping("/Add_Scenario_and_Sub_Playlist_In_Playlist")
	public ModelAndView showAddScenarioAndSubPlaylistForm() {
		ModelAndView mav = new ModelAndView("Add_Scenario_and_Sub_Playlist_In_Playlist");

		mav.addObject("pageTitle", "Add Scenario & Sub Playlist In Playlist");

		// Add the lists for dropdowns
		List<Playlist> playlists = PlaylistRepository.findAll();
		List<SubPlaylist> subPlaylists = SubPlaylistRepository.findAll();
		List<Add_Scenario> scenarios = ScenarioRepository.findAll();

		mav.addObject("playlists", playlists);
		mav.addObject("subPlaylists", subPlaylists);
		mav.addObject("scenarios", scenarios);

		return mav;
	}

	@PostMapping("/save_AddPlaylistInScenarioInSub_Playlist")
	public String saveItemInPlaylist(@RequestParam("playlistId") int playlistId,
			@RequestParam("itemType") String itemType,
			@RequestParam(value = "scenarioIds", required = false) List<Integer> scenarioIds,
			@RequestParam(value = "subPlaylistId", required = false) Integer subPlaylistId) {

		// Validate Playlist
		Optional<Playlist> playlistOpt = PlaylistRepository.findById(playlistId);
		if (!playlistOpt.isPresent()) {
			throw new IllegalArgumentException("Invalid Playlist ID: " + playlistId);
		}

		// Handle different case variations
		if ("scenario".equalsIgnoreCase(itemType) || "scenario".equals(itemType)) {
			if (scenarioIds != null && !scenarioIds.isEmpty()) {
				for (Integer scenarioId : scenarioIds) {
					PlaylistItem item = new PlaylistItem();
					item.setPlaylistId(playlistId);
					item.setItemType(PlaylistItem.ItemType.scenario);
					item.setItemId(scenarioId);
					PlaylistItemRepository.save(item);
				}
			}
		} else if ("sub_playlist".equalsIgnoreCase(itemType) || "subplaylist".equalsIgnoreCase(itemType)) {
			if (subPlaylistId != null) {
				PlaylistItem item = new PlaylistItem();
				item.setPlaylistId(playlistId);
				item.setItemType(PlaylistItem.ItemType.sub_playlist);
				item.setItemId(subPlaylistId);
				PlaylistItemRepository.save(item);
			}
		} else {
			throw new IllegalArgumentException("Invalid item type: " + itemType);
		}

		return "redirect:/guac/Add_Scenario_and_Sub_Playlist_In_Playlist";
	}

	@GetMapping("/Add_ScenarioInSub_Playlist")
	public ModelAndView showAddScenarioInSubPlaylistForm() {
		ModelAndView mav = new ModelAndView("Add_ScenarioInSub_Playlist");
		mav.addObject("pageTitle", "Add Scenario In Sub Playlist");

		// All subplaylists
		List<SubPlaylist> subPlaylists = SubPlaylistRepository.findAll();
		mav.addObject("subPlaylists", subPlaylists);

		// All scenarios
		List<Add_Scenario> scenarios = ScenarioRepository.findAll();
		mav.addObject("scenarios", scenarios);

		// New object to bind form data
		mav.addObject("subPlaylist", new SubPlaylist());

		return mav;
	}

	@PostMapping("/save_ScenarioInSub_Playlist")
	public String saveScenarioInSubPlaylist(@RequestParam("subPlaylistId") Integer subPlaylistId,
			@RequestParam("scenarioIds") List<Integer> scenarioIds) {

		for (Integer scenarioId : scenarioIds) {
			SubPlaylist subPlaylist = new SubPlaylist();
			subPlaylist.setId(subPlaylistId);

			Add_Scenario scenario = new Add_Scenario();
			scenario.setId(scenarioId);

			SubPlaylistScenario joinEntity = new SubPlaylistScenario();
			joinEntity.setSubPlaylist(subPlaylist);
			joinEntity.setScenario(scenario);

			SubPlaylistScenarioRepository.save(joinEntity);
		}

		return "redirect:/guac/Add_ScenarioInSub_Playlist";
	}

	@GetMapping("/View_Particular_Playlist")
	public ModelAndView getView_Particular_Playlist(@RequestParam String Id) {
		ModelAndView mav = new ModelAndView("View_Particular_Playlist");
		JSONArray Finalarray = new JSONArray();

		try {
			int SRNO = Integer.parseInt(Id);

			// Fetch playlist data
			List<Playlist> dataList = PlaylistRepository.getView_Particular_Scenerio(SRNO);

			// Fetch playlist items
			List<PlaylistItem> playlistItems = PlaylistItemRepository.findByPlaylistId(SRNO);

			// Separate scenario and sub-playlist items
			List<Integer> scenarioIds = playlistItems.stream()
					.filter(item -> item.getItemType() == PlaylistItem.ItemType.scenario).map(PlaylistItem::getItemId)
					.collect(Collectors.toList());

			List<Integer> subPlaylistIds = playlistItems.stream()
					.filter(item -> item.getItemType() == PlaylistItem.ItemType.sub_playlist)
					.map(PlaylistItem::getItemId).collect(Collectors.toList());

			// Fetch all scenarios by IDs
			List<Add_Scenario> scenarioDataList = new ArrayList<>();
			if (!scenarioIds.isEmpty()) {
				scenarioDataList = ScenarioRepository.findAllById(scenarioIds);
			}

			// Fetch all sub-playlists by IDs
			List<SubPlaylist> subPlaylistDataList = new ArrayList<>();
			if (!subPlaylistIds.isEmpty()) {
				subPlaylistDataList = SubPlaylistRepository.findAllById(subPlaylistIds);
			}

			// Prepare scenario JSON
			for (Add_Scenario temp : scenarioDataList) {
				JSONObject obj = new JSONObject();
				obj.put("item_type", "scenario");
				obj.put("Scenario_Name", temp.getScenarioName() != null ? temp.getScenarioName() : "");
				obj.put("Scenario_Title", temp.getScenarioTitle() != null ? temp.getScenarioTitle() : "");
				obj.put("Category", temp.getCategory() != null ? temp.getCategory() : "");
				obj.put("Scenario_Type", temp.getScenarioType() != null ? temp.getScenarioType() : "");
				obj.put("Mode", temp.getMode() != null ? temp.getMode() : "");
				obj.put("Difficulty_Level", temp.getDifficultyLevel() != null ? temp.getDifficultyLevel() : "");
				obj.put("Duration", temp.getDuration() != null ? temp.getDuration() : "");
				obj.put("NumberofInstance", temp.getNumberofInstance() != null ? temp.getNumberofInstance() : "");
				obj.put("Cover_Image", ""); // keeping blank for now
				obj.put("Id", temp.getId());
				Finalarray.put(obj);
			}

			// Prepare sub-playlist JSON
			for (SubPlaylist temp : subPlaylistDataList) {
				JSONObject obj = new JSONObject();
				obj.put("item_type", "sub_playlist");
				obj.put("Playlist_Name", temp.getPlaylistName() != null ? temp.getPlaylistName() : "");
				obj.put("Playlist_Title", temp.getPlaylistTitle() != null ? temp.getPlaylistTitle() : "");
				obj.put("Description", temp.getDescription() != null ? temp.getDescription() : "");
				obj.put("Tag", temp.getTag() != null ? temp.getTag() : "");
				obj.put("Cover_Image", ""); // keeping blank for now
				obj.put("Id", temp.getId());
				Finalarray.put(obj);
			}

			// Prepare playlist JSON
			for (Playlist temp : dataList) {
				JSONObject obj = new JSONObject();
				obj.put("PlaylistTitle", temp.getPlaylistTitle() != null ? temp.getPlaylistTitle() : "");
				obj.put("PlaylistName", temp.getPlaylistName() != null ? temp.getPlaylistName() : "");
				obj.put("Description", temp.getDescription() != null ? temp.getDescription() : "");
				obj.put("Tag", temp.getTag() != null ? temp.getTag() : "");
				obj.put("Cover_Image", ""); // keeping blank for now
				obj.put("Id", temp.getId());
				Finalarray.put(obj);
			}

			// Add to model
			mav.addObject("listObj", Finalarray.toString());

			List<SubPlaylist> subPlaylists = SubPlaylistRepository.findAll();
			mav.addObject("subPlaylists", subPlaylists);

			List<Add_Scenario> scenarios = ScenarioRepository.findAll();
			mav.addObject("scenarioList", scenarios);

		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
			System.out.println("Error fetching data: " + e.getMessage());
		}

		return mav;
	}

	@PostMapping("/addplaylist_SubPlaylist")
	@ResponseBody
	public String addSubPlaylistToPlaylist(@RequestParam("playlistId") int playlistId,
			@RequestParam("subPlaylistId") int subPlaylistId) {
		try {
			// check parent playlist exists
			Playlist playlist = PlaylistRepository.findById(playlistId)
					.orElseThrow(() -> new RuntimeException("Playlist not found"));

			// check sub playlist exists
			SubPlaylist sub = SubPlaylistRepository.findById(subPlaylistId)
					.orElseThrow(() -> new RuntimeException("Sub Playlist not found"));

			// prevent duplicates
			boolean exists = PlaylistItemRepository.existsByPlaylistIdAndItemIdAndItemType(playlistId, subPlaylistId,
					PlaylistItem.ItemType.sub_playlist);

			if (!exists) {
				PlaylistItem item = new PlaylistItem();
				item.setPlaylistId(playlistId);
				item.setItemId(subPlaylistId);
				item.setItemType(PlaylistItem.ItemType.sub_playlist);

				PlaylistItemRepository.save(item);
			}

			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "error: " + e.getMessage();
		}
	}

	@GetMapping("/editplaylist/{id}")
	public ModelAndView editplaylist(@PathVariable("id") Integer id) {

		try {
			ModelAndView mav = new ModelAndView("Add_Playlist");
//			mav.addObject("action_name", var_function_name);
			mav.addObject("playlist", PlaylistRepository.findById(id).get());
			mav.addObject("pageTitle", "Edit Playlist (ID: " + id + ")");
//			mav.addObject("pageTitle",  "Edit " + disp_function_name + " (ID: " + id + ")");
			return mav;
		} catch (Exception e) {
			ModelAndView mav = new ModelAndView("Add_Playlist");
//			mav.addObject("action_name", var_function_name);
			mav.addObject("message", e.getMessage());
			return mav;
		}
	}

	@GetMapping("/deletplaylist/{id}")
	public String deletplaylist(@PathVariable("id") Integer id) {

		try {
			ModelAndView mav = new ModelAndView("Add_Playlist");
//			mav.addObject("action_name", var_function_name);
//			mav.addObject("playlist", PlaylistRepository.deleteById(id));
			PlaylistRepository.deleteById(id);
//			mav.addObject("pageTitle",  "Edit " + disp_function_name + " (ID: " + id + ")");
//			return mav;
		} catch (Exception e) {
			ModelAndView mav = new ModelAndView("Add_Playlist");
//			mav.addObject("action_name", var_function_name);
			mav.addObject("message", e.getMessage());
//			return mav;
		}
		return "redirect:/guac/View_Playlist";
	}

//	@PostMapping("/playlistsave")
//	public String savePlaylistData(Playlist obj, RedirectAttributes redirectAttributes,
//			@RequestParam(required = false) MultipartFile cover_image, Principal principal) {
//
////		ModelAndView mav = new ModelAndView("Add_Playlist");
//
//		try {
//			// Set created by (assuming you want to set the principal name)
//			if (principal != null) {
//				obj.setCreatedBy(principal.getName());
//			}
//
//			// Handle the uploaded image file first
//			if (cover_image != null && !cover_image.isEmpty()) {
//				// Validate file type
//				String contentType = cover_image.getContentType();
//				if (contentType != null && contentType.startsWith("image/")) {
//					// Store image bytes in the Playlist object
//					obj.setCoverImage(cover_image.getBytes());
//					System.out.println("Uploaded image saved to database");
//				} else {
//					// Invalid file type
//					redirectAttributes.addFlashAttribute("message", "Invalid file type. Please upload an image file.");
//					redirectAttributes.addFlashAttribute("status", "error");
////					return mav;
//				}
//			} else {
//				// No image uploaded - try to set a default image
//				String defaultImagePath = "C:\\Users\\vijay\\Desktop\\18825\\New folder\\default.jpg";
//				try {
//					byte[] defaultImageBytes = Files.readAllBytes(Paths.get(defaultImagePath));
//					obj.setCoverImage(defaultImageBytes);
//					System.out.println("Default image loaded and saved to database");
//				} catch (IOException e) {
//					System.err.println("Error loading default image: " + e.getMessage());
//					// Create a minimal placeholder image instead of null
//					obj.setCoverImage(createPlaceholderImage());
//					System.out.println("Placeholder image created and saved to database");
//				}
//			}
//
//			// Save to database
//			try {
//				PlaylistRepository.save(obj);
//				redirectAttributes.addFlashAttribute("message", "The playlist has been saved successfully!");
//				redirectAttributes.addFlashAttribute("status", "success");
//			} catch (Exception e) {
//				redirectAttributes.addFlashAttribute("message", "Error saving playlist: " + e.getMessage());
//				redirectAttributes.addFlashAttribute("status", "error");
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			redirectAttributes.addFlashAttribute("message", "Error while saving playlist: " + e.getMessage());
//			redirectAttributes.addFlashAttribute("status", "error");
//		}
//
//		return "redirect:/guac/Add_Playlist";
//	}

//	@PostMapping("/playlistsave")
//	public String savePlaylistData(Playlist obj, RedirectAttributes redirectAttributes,
//			@RequestParam(required = false) MultipartFile cover_image, Principal principal) {
//
//		try {
//			// Set createdBy
//			if (principal != null) {
//				obj.setCreatedBy(principal.getName());
//			}
//
//			boolean isNew = (obj.getId() == 0);
//
//			if (isNew) {
//				// New playlist - handle image
//				if (cover_image != null && !cover_image.isEmpty()) {
//					String contentType = cover_image.getContentType();
//					if (contentType != null && contentType.startsWith("image/")) {
//						obj.setCoverImage(cover_image.getBytes());
//						System.out.println("Uploaded image saved to database");
//					} else {
//						redirectAttributes.addFlashAttribute("message",
//								"Invalid file type. Please upload an image file.");
//						redirectAttributes.addFlashAttribute("status", "error");
//					}
//				} else {
//					// No image uploaded - load default
//					String defaultImagePath = "C:\\Users\\vijay\\Desktop\\18825\\New folder\\default.jpg";
//					try {
//						byte[] defaultImageBytes = Files.readAllBytes(Paths.get(defaultImagePath));
//						obj.setCoverImage(defaultImageBytes);
//						System.out.println("Default image loaded and saved to database");
//					} catch (IOException e) {
//						System.err.println("Error loading default image: " + e.getMessage());
//						obj.setCoverImage(createPlaceholderImage());
//						System.out.println("Placeholder image created and saved to database");
//					}
//				}
//			} else {
//				// Editing existing playlist
//				Optional<Playlist> existing = PlaylistRepository.findById(obj.getId());
//				if (existing.isPresent()) {
//					Playlist existingPlaylist = existing.get();
//
//					// Retain existing image if new one isn't uploaded
//					if (cover_image == null || cover_image.isEmpty()) {
//						obj.setCoverImage(existingPlaylist.getCoverImage());
//					} else {
//						String contentType = cover_image.getContentType();
//						if (contentType != null && contentType.startsWith("image/")) {
//							obj.setCoverImage(cover_image.getBytes());
//							System.out.println("Updated image saved to database");
//						} else {
//							redirectAttributes.addFlashAttribute("message",
//									"Invalid file type. Please upload an image file.");
//							redirectAttributes.addFlashAttribute("status", "error");
//						}
//					}
//				}
//			}
//
//			// Save to DB
//			Playlist saved = PlaylistRepository.save(obj);
//			redirectAttributes.addFlashAttribute("message", "The playlist has been saved successfully!");
//			redirectAttributes.addFlashAttribute("status", "success");
//
//			// Redirect based on new/edit
//			if (isNew) {
//				return "redirect:/guac/Add_Playlist";
//			} else {
//				return "redirect:/guac/View_Particular_Playlist?Id=" + saved.getId();
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			redirectAttributes.addFlashAttribute("message", "Error while saving playlist: " + e.getMessage());
//			redirectAttributes.addFlashAttribute("status", "error");
//			return "redirect:/guac/Add_Playlist";
//		}
//	}

	@PostMapping("/playlistsave")
	public String savePlaylistData(@ModelAttribute("playlist") Playlist obj, RedirectAttributes redirectAttributes,
			@RequestParam(value = "cover_image", required = false) MultipartFile coverImage, Principal principal) {

		try {
			boolean isNew = (obj.getId() == 0);

			// Set createdBy if available
			if (principal != null) {
				obj.setCreatedBy(principal.getName());
			}

			// Handle cover image
			if (coverImage != null && !coverImage.isEmpty()) {
				String contentType = coverImage.getContentType();
				if (contentType != null && contentType.startsWith("image/")) {
					obj.setCoverImage(coverImage.getBytes());
				} else {
					redirectAttributes.addFlashAttribute("message", "Invalid file type. Upload an image.");
					redirectAttributes.addFlashAttribute("status", "error");
					return "redirect:/guac/Add_Playlist";
				}
			} else if (!isNew) {
				// Retain existing image
				Optional<Playlist> existing = PlaylistRepository.findById(obj.getId());
				existing.ifPresent(existingPlaylist -> obj.setCoverImage(existingPlaylist.getCoverImage()));
			} else {
				// Load default image for new playlist
				String defaultImagePath = "C:\\Users\\vijay\\Desktop\\18825\\New folder\\default.jpg";
				try {
					obj.setCoverImage(Files.readAllBytes(Paths.get(defaultImagePath)));
				} catch (IOException e) {
					obj.setCoverImage(new byte[0]); // fallback to empty image
				}
			}

			// Save playlist
			Playlist saved = PlaylistRepository.save(obj);
			redirectAttributes.addFlashAttribute("message", "Playlist saved successfully!");
			redirectAttributes.addFlashAttribute("status", "success");

			return isNew ? "redirect:/guac/Add_Playlist"
					: "redirect:/guac/View_Particular_Playlist?Id=" + saved.getId();

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "Error saving playlist: " + e.getMessage());
			redirectAttributes.addFlashAttribute("status", "error");
			return "redirect:/guac/Add_Playlist";
		}
	}

//	    @GetMapping("/playlist-image/{id}")
//	    public void getPlaylistImage(@PathVariable("id") int id, HttpServletResponse response) throws IOException {
//	        Optional<Playlist> playlistOpt = playlistRepository.findById(id);
//	        if (playlistOpt.isPresent() && playlistOpt.get().getCoverImage() != null) {
//	            byte[] imageBytes = playlistOpt.get().getCoverImage();
//	            response.setContentType("image/jpeg"); // or "image/png"
//	            response.getOutputStream().write(imageBytes);
//	            response.getOutputStream().flush();
//	        } else {
//	            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//	        }
//	    }

	// Add this method if you don't have it
	private byte[] createPlaceholderImage() {
		try {
			BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
			return baos.toByteArray();
		} catch (IOException e) {
			return new byte[0];
		}
	}

//	@GetMapping("/View_Playlist")
//	public ModelAndView getView_Playlist(Principal principal) {
//		ModelAndView mav = new ModelAndView("View_Playlist");
//		JSONArray Finalarray = new JSONArray();
//		List<Playlist> dataList;
//		try {
//			// start
//
//			Authentication authentication = (Authentication) principal;
//			User loginedUser = (User) authentication.getPrincipal();
//
//			String userName = loginedUser.getUsername();
//			String groupName = "";
//			StringBuilder vmNamesBuilder = new StringBuilder();
//
//			List<AppUser> userList = userRepository.findByuserName(userName);
//
//			boolean isSuperAdmin = authentication.getAuthorities().stream()
//					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));
//
//			boolean isAdmin = authentication.getAuthorities().stream()
//					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
//
//			if (isSuperAdmin) {
//				dataList = PlaylistRepository.findAll();
//			} else {
//				// Get group names
//				for (AppUser appUser : userList) {
//					groupName = appUser.getGroupName(); // Assuming only one AppUser per username
//				}
//
//				List<String> groups = new ArrayList<>();
//				StringTokenizer groupTokenizer = new StringTokenizer(groupName, ",");
//				while (groupTokenizer.hasMoreTokens()) {
//					groups.add(groupTokenizer.nextToken());
//				}
//
//				// Get VM names by group
//				List<Object[]> vmList = repository.getInstanceNameByGroup(groups, true);
//				for (Object[] vmEntry : vmList) {
//					vmNamesBuilder.append(vmEntry[1].toString()).append(",");
//				}
//
//				// Split VM names string into a list
//				List<String> vmGroups = new ArrayList<>();
//				String vmNames = vmNamesBuilder.toString();
//				if (!vmNames.isEmpty()) {
//					StringTokenizer vmTokenizer = new StringTokenizer(vmNames, ",");
//					while (vmTokenizer.hasMoreTokens()) {
//						vmGroups.add(vmTokenizer.nextToken());
//					}
//				}
////				dataList = ScenarioRepository.getView_Scenario(vmGroups);
//				dataList = PlaylistRepository.findAll();
//			}
//
////			 dataList = kVMDriveDetailsRepository.findBykVMDetails();
//			System.out.println("Fetched Data: " + dataList.toString()); // Print to console
//			int srno = 0;
//			for (Playlist temp : dataList) {
//				JSONObject obj = new JSONObject();
//
//				String PlaylistTitle = temp.getPlaylistTitle() != null ? temp.getPlaylistTitle() : "";
//				String PlaylistName = temp.getPlaylistName() != null ? temp.getPlaylistName() : "";
//				String Description = temp.getDescription() != null ? temp.getDescription() : "";
//				String Tag = temp.getTag() != null ? temp.getTag() : "";
//
////				String Cover_Image = temp.getCover_Image() != null ? temp.getCover_Image() : "";
//				String Cover_Image = "";
//				int SrNo = temp.getId();
//
////				srno++;
//
//				obj.put("PlaylistTitle", PlaylistTitle);
//				obj.put("PlaylistName", PlaylistName);
//				obj.put("Description", Description);
//				obj.put("Tag", Tag);
//
//				obj.put("Cover_Image", Cover_Image);
//				obj.put("Id", SrNo);
//
//				Finalarray.put(obj);
//			}
//
//			System.out.println("Finalarray_getView_Playlist ::" + Finalarray);
//
//			mav.addObject("listObj", Finalarray.toString());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			mav.addObject("listObj", null);
//			mav.addObject("error", e.getMessage());
//			System.out.println("Error fetching data: " + e.getMessage());
//		}
//		return mav;
//	}

//	@GetMapping("/View_Playlist")
//	public ModelAndView getView_Playlist(Principal principal) {
//		ModelAndView mav = new ModelAndView("View_Playlist");
//		JSONArray Finalarray = new JSONArray();
//		List<Playlist> dataList = new ArrayList<>();
//
//		try {
//			Authentication authentication = (Authentication) principal;
//			User loginedUser = (User) authentication.getPrincipal();
//
//			String userName = loginedUser.getUsername();
//
//			boolean isSuperAdmin = authentication.getAuthorities().stream()
//					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));
//
//			if (isSuperAdmin) {
//				// SuperAdmin → show all playlists
//				dataList = PlaylistRepository.findAll();
//			} else {
//				// Normal User → get only playlists assigned to them
//				List<Integer> playlistIds = UserPlaylistMappingRepository.findPlaylistIdsByUserName(userName);
//
//				if (playlistIds != null && !playlistIds.isEmpty()) {
//					dataList = PlaylistRepository.findAllById(playlistIds);
//				}
//			}
//
//			// Convert to JSON
//			for (Playlist temp : dataList) {
//				JSONObject obj = new JSONObject();
//
//				obj.put("PlaylistTitle", temp.getPlaylistTitle() != null ? temp.getPlaylistTitle() : "");
//				obj.put("PlaylistName", temp.getPlaylistName() != null ? temp.getPlaylistName() : "");
//				obj.put("Description", temp.getDescription() != null ? temp.getDescription() : "");
//				obj.put("Tag", temp.getTag() != null ? temp.getTag() : "");
//				obj.put("Cover_Image", ""); // for now blank
//				obj.put("Id", temp.getId());
//
//				Finalarray.put(obj);
//			}
//
//			System.out.println("Finalarray_getView_Playlist ::" + Finalarray);
//			mav.addObject("listObj", Finalarray.toString());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			mav.addObject("listObj", null);
//			mav.addObject("error", e.getMessage());
//		}
//
//		return mav;
//	}
	
	@GetMapping("/View_Playlist")
	public ModelAndView getView_Playlist(Principal principal) {
	    ModelAndView mav = new ModelAndView("View_Playlist");
	    JSONArray Finalarray = new JSONArray();
	    List<Playlist> dataList = new ArrayList<>();

	    try {
	        Authentication authentication = (Authentication) principal;
	        User loginedUser = (User) authentication.getPrincipal();
	        String userName = loginedUser.getUsername();

	        boolean isSuperAdmin = authentication.getAuthorities().stream()
	                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));

	        if (isSuperAdmin) {
	            // SuperAdmin → all playlists
	            dataList = PlaylistRepository.findAll();
	        } else {
	            // Normal User → playlists based on their subjects
	            List<Integer> userSubjectIds = getUserSubjectIds(userName);
	            
	            if (userSubjectIds != null && !userSubjectIds.isEmpty()) {
	                // Get playlist IDs mapped to user's subjects
	                List<Integer> playlistIds = SubjectPlaylistMappingRepository.findPlaylistIdsBySubjectIds(userSubjectIds);
	                
	                if (playlistIds != null && !playlistIds.isEmpty()) {
	                    dataList = PlaylistRepository.findAllById(playlistIds);
	                }
	            }
	        }

	        // Convert to JSON
	        for (Playlist temp : dataList) {
	            JSONObject obj = new JSONObject();
	            obj.put("PlaylistTitle", temp.getPlaylistTitle() != null ? temp.getPlaylistTitle() : "");
	            obj.put("PlaylistName", temp.getPlaylistName() != null ? temp.getPlaylistName() : "");
	            obj.put("Description", temp.getDescription() != null ? temp.getDescription() : "");
	            obj.put("Tag", temp.getTag() != null ? temp.getTag() : "");
	            obj.put("Cover_Image", ""); // for now blank
	            obj.put("Id", temp.getId());
	            Finalarray.put(obj);
	        }

	        System.out.println("Finalarray_getView_Playlist ::" + Finalarray);
	        mav.addObject("listObj", Finalarray.toString());

	    } catch (Exception e) {
	        e.printStackTrace();
	        mav.addObject("listObj", null);
	        mav.addObject("error", e.getMessage());
	    }

	    return mav;
	}

//	@GetMapping("/View_SubPalylist")
//	public ModelAndView getView_SubPalylist(Principal principal) {
//		ModelAndView mav = new ModelAndView("View_SubPalylist");
//		JSONArray Finalarray = new JSONArray();
//		List<SubPlaylist> dataList;
//		try {
//			// start
//
//			Authentication authentication = (Authentication) principal;
//			User loginedUser = (User) authentication.getPrincipal();
//
//			String userName = loginedUser.getUsername();
//			String groupName = "";
//			StringBuilder vmNamesBuilder = new StringBuilder();
//
//			List<AppUser> userList = userRepository.findByuserName(userName);
//
//			boolean isSuperAdmin = authentication.getAuthorities().stream()
//					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));
//
//			boolean isAdmin = authentication.getAuthorities().stream()
//					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
//
//			if (isSuperAdmin) {
//				dataList = SubPlaylistRepository.findAll();
//			} else {
//				// Get group names
//				for (AppUser appUser : userList) {
//					groupName = appUser.getGroupName(); // Assuming only one AppUser per username
//				}
//
//				List<String> groups = new ArrayList<>();
//				StringTokenizer groupTokenizer = new StringTokenizer(groupName, ",");
//				while (groupTokenizer.hasMoreTokens()) {
//					groups.add(groupTokenizer.nextToken());
//				}
//
//				// Get VM names by group
//				List<Object[]> vmList = repository.getInstanceNameByGroup(groups, true);
//				for (Object[] vmEntry : vmList) {
//					vmNamesBuilder.append(vmEntry[1].toString()).append(",");
//				}
//
//				// Split VM names string into a list
//				List<String> vmGroups = new ArrayList<>();
//				String vmNames = vmNamesBuilder.toString();
//				if (!vmNames.isEmpty()) {
//					StringTokenizer vmTokenizer = new StringTokenizer(vmNames, ",");
//					while (vmTokenizer.hasMoreTokens()) {
//						vmGroups.add(vmTokenizer.nextToken());
//					}
//				}
////				dataList = ScenarioRepository.getView_Scenario(vmGroups);
//				dataList = SubPlaylistRepository.findAll();
//			}
//
////			 dataList = kVMDriveDetailsRepository.findBykVMDetails();
//			System.out.println("Fetched Data: " + dataList.toString()); // Print to console
//			int srno = 0;
//			for (SubPlaylist temp : dataList) {
//				JSONObject obj = new JSONObject();
//
//				String PlaylistTitle = temp.getPlaylistTitle() != null ? temp.getPlaylistTitle() : "";
//				String PlaylistName = temp.getPlaylistName() != null ? temp.getPlaylistName() : "";
//				String Description = temp.getDescription() != null ? temp.getDescription() : "";
//				String Tag = temp.getTag() != null ? temp.getTag() : "";
//
////				String Cover_Image = temp.getCover_Image() != null ? temp.getCover_Image() : "";
//				String Cover_Image = "";
//				int SrNo = temp.getId();
//
////				srno++;
//
//				obj.put("PlaylistTitle", PlaylistTitle);
//				obj.put("PlaylistName", PlaylistName);
//				obj.put("Description", Description);
//				obj.put("Tag", Tag);
//
//				obj.put("Cover_Image", Cover_Image);
//				obj.put("Id", SrNo);
//
//				Finalarray.put(obj);
//			}
//
//			System.out.println("Finalarray_getView_SubPalylist ::" + Finalarray);
//
//			mav.addObject("listObj", Finalarray.toString());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			mav.addObject("listObj", null);
//			mav.addObject("error", e.getMessage());
//			System.out.println("Error fetching data: " + e.getMessage());
//		}
//		return mav;
//	}

//	@GetMapping("/View_SubPalylist")
//	public ModelAndView getView_SubPlaylist(Principal principal) {
//		ModelAndView mav = new ModelAndView("View_SubPalylist");
//		JSONArray Finalarray = new JSONArray();
//		List<SubPlaylist> dataList = new ArrayList<>();
//
//		try {
//			Authentication authentication = (Authentication) principal;
//			User loginedUser = (User) authentication.getPrincipal();
//
//			String userName = loginedUser.getUsername();
//
//			boolean isSuperAdmin = authentication.getAuthorities().stream()
//					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));
//
//			if (isSuperAdmin) {
//				// SuperAdmin → all subplaylists
//				dataList = SubPlaylistRepository.findAll();
//			} else {
//				// Normal User → only subplaylists assigned to them
//				List<Integer> subPlaylistIds = UserSubplaylistMappingRepository.findSubPlaylistIdsByUserName(userName);
//
//				if (subPlaylistIds != null && !subPlaylistIds.isEmpty()) {
//					dataList = SubPlaylistRepository.findAllById(subPlaylistIds);
//				}
//			}
//
//			// Convert to JSON
//			for (SubPlaylist temp : dataList) {
//				JSONObject obj = new JSONObject();
//				obj.put("PlaylistTitle", temp.getPlaylistTitle() != null ? temp.getPlaylistTitle() : "");
//				obj.put("PlaylistName", temp.getPlaylistName() != null ? temp.getPlaylistName() : "");
//				obj.put("Description", temp.getDescription() != null ? temp.getDescription() : "");
//				obj.put("Tag", temp.getTag() != null ? temp.getTag() : "");
//				obj.put("Cover_Image", "");
//				obj.put("Id", temp.getId());
//				Finalarray.put(obj);
//			}
//
//			System.out.println("Finalarray_getView_SubPlaylist ::" + Finalarray);
//			mav.addObject("listObj", Finalarray.toString());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			mav.addObject("listObj", null);
//			mav.addObject("error", e.getMessage());
//		}
//		return mav;
//	}
	
	
	@GetMapping("/View_SubPalylist")
	public ModelAndView getView_SubPlaylist(Principal principal) {
	    ModelAndView mav = new ModelAndView("View_SubPalylist");
	    JSONArray Finalarray = new JSONArray();
	    List<SubPlaylist> dataList = new ArrayList<>();

	    try {
	        Authentication authentication = (Authentication) principal;
	        User loginedUser = (User) authentication.getPrincipal();
	        String userName = loginedUser.getUsername();

	        boolean isSuperAdmin = authentication.getAuthorities().stream()
	                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));

	        if (isSuperAdmin) {
	            // SuperAdmin → all subplaylists
	            dataList = SubPlaylistRepository.findAll();
	        } else {
	            // Normal User → subplaylists based on their subjects
	            List<Integer> userSubjectIds = getUserSubjectIds(userName);
	            
	            if (userSubjectIds != null && !userSubjectIds.isEmpty()) {
	                // Get subplaylist IDs mapped to user's subjects
	                List<Integer> subPlaylistIds = SubjectSubplaylistMappingRepository.findSubPlaylistIdsBySubjectIds(userSubjectIds);
	                
	                if (subPlaylistIds != null && !subPlaylistIds.isEmpty()) {
	                    dataList = SubPlaylistRepository.findAllById(subPlaylistIds);
	                }
	            }
	        }

	        // Convert to JSON
	        for (SubPlaylist temp : dataList) {
	            JSONObject obj = new JSONObject();
	            obj.put("PlaylistTitle", temp.getPlaylistTitle() != null ? temp.getPlaylistTitle() : "");
	            obj.put("PlaylistName", temp.getPlaylistName() != null ? temp.getPlaylistName() : "");
	            obj.put("Description", temp.getDescription() != null ? temp.getDescription() : "");
	            obj.put("Tag", temp.getTag() != null ? temp.getTag() : "");
	            obj.put("Cover_Image", "");
	            obj.put("Id", temp.getId());
	            Finalarray.put(obj);
	        }

	        System.out.println("Finalarray_getView_SubPlaylist ::" + Finalarray);
	        mav.addObject("listObj", Finalarray.toString());

	    } catch (Exception e) {
	        e.printStackTrace();
	        mav.addObject("listObj", null);
	        mav.addObject("error", e.getMessage());
	    }
	    return mav;
	}

	@GetMapping("/Playlistimage/{id}")
	public void getPlaylistImage(@PathVariable int id, HttpServletResponse response) throws IOException {
//		System.out.println("inside_render_image_getPlaylistImage ::");
		Optional<Playlist> scenario = PlaylistRepository.findById(id);
		if (scenario.isPresent() && scenario.get().getCoverImage() != null) {
			byte[] imageBytes = scenario.get().getCoverImage();
			response.setContentType("image/jpeg"); // Adjust based on actual image type
			response.getOutputStream().write(imageBytes);
			response.getOutputStream().close();
		} else {
			// Return a default image if no image exists
			response.sendRedirect("/images/default-student.jpg");
		}
	}

	@GetMapping("/Scenarioimage/{id}")
	public void getScenarioImage(@PathVariable int id, HttpServletResponse response) throws IOException {
//		System.out.println("inside_render_image_getScenarioImage ::");
		Optional<Add_Scenario> scenario = ScenarioRepository.findById(id);
		if (scenario.isPresent() && scenario.get().getCoverImage() != null) {
			byte[] imageBytes = scenario.get().getCoverImage();
			response.setContentType("image/jpeg");
			response.getOutputStream().write(imageBytes);
			response.getOutputStream().close();
		} else {

			response.sendRedirect("/images/default-student.jpg");
		}
	}

	@GetMapping("/SubPlaylistimage/{id}")
	public void getSubPlaylistimage(@PathVariable int id, HttpServletResponse response) throws IOException {
//		System.out.println("inside_render_image_getScenarioImage ::");
		Optional<SubPlaylist> scenario = SubPlaylistRepository.findById(id);
		if (scenario.isPresent() && scenario.get().getCoverImage() != null) {
			byte[] imageBytes = scenario.get().getCoverImage();
			response.setContentType("image/jpeg");
			response.getOutputStream().write(imageBytes);
			response.getOutputStream().close();
		} else {

			response.sendRedirect("/images/default-student.jpg");
		}
	}

	@GetMapping("/View_Particular_Scenerio")
	public ModelAndView getView_Particular_Scenerio(@RequestParam String Id) {

		ModelAndView mav = new ModelAndView("View_Particular_Scenerio");
		JSONArray Finalarray = new JSONArray();
		List<Add_Scenario> dataList;
		try {

			int SRNO = Integer.parseInt(Id);

			dataList = ScenarioRepository.getView_Particular_Scenerio(SRNO);

			System.out.println("Fetched Data: " + dataList.toString()); // Print to console
			int srno = 0;
			for (Add_Scenario temp : dataList) {
				JSONObject obj = new JSONObject();

				String Scenario_Name = temp.getScenarioName() != null ? temp.getScenarioName() : "";
				String Scenario_Title = temp.getScenarioTitle() != null ? temp.getScenarioTitle() : "";
				String Description = temp.getDescription() != null ? temp.getDescription() : "";
				String Category = temp.getCategory() != null ? temp.getCategory() : "";
				String Scenario_Type = temp.getScenarioType() != null ? temp.getScenarioType() : "";
				String Mode = temp.getMode() != null ? temp.getMode() : "";
				String Difficulty_Level = temp.getDifficultyLevel() != null ? temp.getDifficultyLevel() : "";
				String Duration = temp.getDuration() != null ? temp.getDuration() : "";
				String Labs = temp.getLabs() != null ? temp.getLabs() : "";
//				String Cover_Image = temp.getCover_Image() != null ? temp.getCover_Image() : "";
				String Cover_Image = "";
				int SrNo = temp.getId();

//				srno++;

				obj.put("Scenario_Name", Scenario_Name);
				obj.put("Scenario_Title", Scenario_Title);
				obj.put("Description", Description);
				obj.put("Category", Category);
				obj.put("Scenario_Type", Scenario_Type);
				obj.put("Mode", Mode);
				obj.put("Difficulty_Level", Difficulty_Level);
				obj.put("Duration", Duration);
				obj.put("Labs", Labs);
				obj.put("Cover_Image", Cover_Image);
				obj.put("Id", SrNo);

				Finalarray.put(obj);
			}

			System.out.println("Finalarray_getView_Scenario ::" + Finalarray);

			mav.addObject("listObj", Finalarray.toString());
			mav.addObject("PlaylistList", PlaylistRepository.findAll());

		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
			System.out.println("Error fetching data: " + e.getMessage());
		}
		return mav;
	}

//	@GetMapping("/Scenario_Details")
//	public ModelAndView showScenario_Details(RedirectAttributes redirectAttributes) {
//		ModelAndView mav = new ModelAndView("Add_Scenario");
//		List<CloudInstance> instances = null;
//		instances = repository.getInstanceNameNotAssigned();
//		mav.addObject("pageTitle", "Create New Scenario");
//		mav.addObject("instanceNameList", instances);
//		
//		mav.addObject("CategoryList", category);
//		mav.addObject("scenario", new Add_Scenario());
//
//		return mav;
//	}

	@GetMapping("/Scenario_Details")
	public ModelAndView showScenarioDetails() {
		ModelAndView mav = new ModelAndView("Add_Scenario");

		List<CloudInstance> instances = repository.getInstanceNameNotAssigned();
		List<CategoryMaster> categoryList = CategoryMasterRepository.findAll();

		mav.addObject("pageTitle", "Create New Scenario");
		mav.addObject("instanceNameList", instances);
		mav.addObject("CategoryList", categoryList);
		mav.addObject("scenario", new Add_Scenario());

		return mav;
	}

//	@PostMapping("/saveScenarioData")
//	public String saveScenarioData(@ModelAttribute("scenario") Add_Scenario scenarioObj,
//			RedirectAttributes redirectAttributes, @RequestParam(required = false) MultipartFile cover_image,
//			Principal principal) {
//
//		try {
//			boolean isNew = (scenarioObj.getId() == 0);
//
//			// Set creator if needed
//			if (principal != null) {
//				// scenarioObj.setCreatedBy(principal.getName());
//			}
//
//			// Step 1: Parse Labs - format: "101~Lab A,102~Lab B"
//			String labsString = scenarioObj.getLabs();
//			
//			System.out.println("labsString ::"+labsString);
//			String[] labsArray = labsString.split(",");
//			List<String> labIds = new ArrayList<String>();
//			List<String> labNames = new ArrayList<String>();
//
//			for (String lab : labsArray) {
//				String[] parts = lab.split("~");
//				if (parts.length == 2) {
//					String labId = parts[0].trim();
//					String labName = parts[1].trim();
//
//					labIds.add(labId);
//					labNames.add(labName);
//
//					try {
//						repository.updateInstanceNameAssigned(Integer.parseInt(labId));
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//
//			// For saving readable lab names in scenarioObj
//			StringBuilder sb = new StringBuilder();
//			for (int i = 0; i < labNames.size(); i++) {
//				sb.append(labNames.get(i));
//				if (i < labNames.size() - 1) {
//					sb.append(",");
//				}
//			}
//			scenarioObj.setLabs(sb.toString());
//			scenarioObj.setComments("");
//
//			// Handle cover image
//			if (cover_image != null && !cover_image.isEmpty()) {
//				String contentType = cover_image.getContentType();
//				if (contentType != null && contentType.startsWith("image/")) {
//					scenarioObj.setCoverImage(cover_image.getBytes());
//				} else {
//					redirectAttributes.addFlashAttribute("message", "Invalid file type. Please upload an image file.");
//					redirectAttributes.addFlashAttribute("status", "error");
//				}
//			} else if (isNew) {
//				// Use default image for new scenario
//				String defaultImagePath = "C:\\Users\\vijay\\Desktop\\18825\\New folder\\default.jpg";
//				try {
//					byte[] defaultImageBytes = Files.readAllBytes(Paths.get(defaultImagePath));
//					scenarioObj.setCoverImage(defaultImageBytes);
//				} catch (IOException e) {
//					scenarioObj.setCoverImage(createPlaceholderImage());
//				}
//			} else {
//				// Use existing image for edit
//				Optional<Add_Scenario> existing = ScenarioRepository.findById(scenarioObj.getId());
//				existing.ifPresent(existingScenario -> {
//					scenarioObj.setCoverImage(existingScenario.getCoverImage());
//				});
//			}
//
//			// Save scenario and get saved entity with ID
//			Add_Scenario savedScenario = ScenarioRepository.save(scenarioObj);
//
//			// 🔥 Delete existing labs for this scenario before inserting new ones
//			ScenarioLabTemplateRepository.deleteByScenarioId(savedScenario.getId());
//
//			// Save lab templates into ScenarioLabTemplate table
//			for (String lab : labsArray) {
//				String[] parts = lab.split("~");
//				if (parts.length == 2) {
//					ScenarioLabTemplate labTemplate = new ScenarioLabTemplate();
//					labTemplate.setScenarioId(savedScenario.getId());
//					labTemplate.setScenarioName(savedScenario.getScenarioName());
//					labTemplate.setTemplateId(Integer.parseInt(parts[0].trim()));
//					labTemplate.setTemplateName(parts[1].trim());
//
//					ScenarioLabTemplateRepository.save(labTemplate);
//				}
//			}
//
//			redirectAttributes.addFlashAttribute("message", "Scenario saved successfully!");
//			redirectAttributes.addFlashAttribute("status", "success");
//			redirectAttributes.addFlashAttribute("result", "success");
//
//			// Redirect based on whether it's new or edit
//			if (isNew) {
//				return "redirect:/guac/Scenario_Details";
//			} else {
//				return "redirect:/guac/View_Particular_Scenerio?Id=" + savedScenario.getId();
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			redirectAttributes.addFlashAttribute("message", "Error while saving scenario: " + e.getMessage());
//			redirectAttributes.addFlashAttribute("status", "error");
//			return "redirect:/guac/Scenario_Details";
//		}
//	}

	@PostMapping("/saveScenarioData")
	public String saveScenarioData(@ModelAttribute("scenario") Add_Scenario scenarioObj,
			RedirectAttributes redirectAttributes, @RequestParam(required = false) MultipartFile cover_image,
			Principal principal) {

		try {
			boolean isNew = (scenarioObj.getId() == 0);

			// Set creator if needed
			if (principal != null) {
				// scenarioObj.setCreatedBy(principal.getName());
			}

			// Step 1: Parse Labs - format: "101~Lab A,102~Lab B"
			String labsString = scenarioObj.getLabs();

			String description = scenarioObj.getDescription();

			// 1. Remove all carriage returns and newlines
			description = description.replaceAll("\\r|\\n", "");

			// 2. Remove extra spaces between tags
			description = description.replaceAll(">\\s+<", "><");

			// 3. Optional: trim the whole string
			description = description.trim();

//	     System.out.println(description);

			scenarioObj.setDescription(description);

			System.out.println("labsString ::" + labsString);

			// Check if labs string is not empty
			if (labsString != null && !labsString.trim().isEmpty()) {
				String[] labsArray = labsString.split(",");
				List<String> labIds = new ArrayList<String>();
				List<String> labNames = new ArrayList<String>();

				for (String lab : labsArray) {
					String[] parts = lab.split("~");
					if (parts.length == 2) {
						String labId = parts[0].trim();
						String labName = parts[1].trim();

						labIds.add(labId);
						labNames.add(labName);

						try {
							repository.updateInstanceNameAssigned(Integer.parseInt(labId));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				// For saving readable lab names in scenarioObj
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < labNames.size(); i++) {
					sb.append(labNames.get(i));
					if (i < labNames.size() - 1) {
						sb.append(",");
					}
				}
				scenarioObj.setLabs(sb.toString());
			}

			scenarioObj.setComments("");

			// Handle cover image
			if (cover_image != null && !cover_image.isEmpty()) {
				String contentType = cover_image.getContentType();
				if (contentType != null && contentType.startsWith("image/")) {
					scenarioObj.setCoverImage(cover_image.getBytes());
				} else {
					redirectAttributes.addFlashAttribute("message", "Invalid file type. Please upload an image file.");
					redirectAttributes.addFlashAttribute("status", "error");
				}
			} else if (isNew) {
				// Use default image for new scenario
				String defaultImagePath = "C:\\Users\\vijay\\Desktop\\18825\\New folder\\default.jpg";
				try {
					byte[] defaultImageBytes = Files.readAllBytes(Paths.get(defaultImagePath));
					scenarioObj.setCoverImage(defaultImageBytes);
				} catch (IOException e) {
					scenarioObj.setCoverImage(createPlaceholderImage());
				}
			} else {
				// Use existing image for edit
				Optional<Add_Scenario> existing = ScenarioRepository.findById(scenarioObj.getId());
				existing.ifPresent(existingScenario -> {
					scenarioObj.setCoverImage(existingScenario.getCoverImage());
				});
			}

			// Save scenario and get saved entity with ID
			Add_Scenario savedScenario = ScenarioRepository.save(scenarioObj);

			// Only update lab templates if labs were provided
			if (labsString != null && !labsString.trim().isEmpty()) {
				// 🔥 Delete existing labs for this scenario before inserting new ones
				ScenarioLabTemplateRepository.deleteByScenarioId(savedScenario.getId());

				// Save lab templates into ScenarioLabTemplate table
				String[] labsArray = labsString.split(",");
				for (String lab : labsArray) {
					String[] parts = lab.split("~");
					if (parts.length == 2) {
						ScenarioLabTemplate labTemplate = new ScenarioLabTemplate();
						labTemplate.setScenarioId(savedScenario.getId());
						labTemplate.setScenarioName(savedScenario.getScenarioName());
						labTemplate.setTemplateId(Integer.parseInt(parts[0].trim()));
						labTemplate.setTemplateName(parts[1].trim());

						ScenarioLabTemplateRepository.save(labTemplate);
					}
				}
			}

			redirectAttributes.addFlashAttribute("message", "Scenario saved successfully!");
			redirectAttributes.addFlashAttribute("status", "success");
			redirectAttributes.addFlashAttribute("result", "success");

			// Redirect based on whether it's new or edit
			if (isNew) {
				return "redirect:/guac/Scenario_Details";
			} else {
				return "redirect:/guac/View_Particular_Scenerio?Id=" + savedScenario.getId();
			}

		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "Error while saving scenario: " + e.getMessage());
			redirectAttributes.addFlashAttribute("status", "error");
			return "redirect:/guac/Scenario_Details";
		}
	}

//	@GetMapping("/editsceneriolist/{id}")
//	public ModelAndView editsceneriolist(@PathVariable("id") Integer id) {
//		try {
//			List<CloudInstance> instances = repository.getInstanceNameNotAssigned();
//
//			ModelAndView mav = new ModelAndView("Add_Scenario");
//			Add_Scenario scenario = ScenarioRepository.findByCheckId(id);
//			if (scenario == null) {
//				throw new IllegalArgumentException("Invalid scenario ID: " + id);
//			}
//
//			// Convert lab names back into "id~name" format for dropdown pre-selection
//			List<ScenarioLabTemplate> assignedLabs = ScenarioLabTemplateRepository.findByScenarioId(id);
//			List<String> assignedLabPairs = new ArrayList<String>();
//
//			for (ScenarioLabTemplate l : assignedLabs) {
//				assignedLabPairs.add(l.getTemplateId() + "~" + l.getTemplateName());
//			}
//
//			// Manual join (since String.join is Java 8+)
//			StringBuilder sb = new StringBuilder();
//			for (int i = 0; i < assignedLabPairs.size(); i++) {
//				sb.append(assignedLabPairs.get(i));
//				if (i < assignedLabPairs.size() - 1) {
//					sb.append(",");
//				}
//			}
//			scenario.setLabs(sb.toString());
//
//			mav.addObject("scenario", scenario);
//			mav.addObject("instanceNameList", instances);
//			mav.addObject("pageTitle", "Edit Scenario (ID: " + id + ")");
//			mav.addObject("isEdit", true);
//			return mav;
//		} catch (Exception e) {
//			ModelAndView mav = new ModelAndView("Add_Scenario");
//			mav.addObject("message", e.getMessage());
//			mav.addObject("isEdit", false);
//			return mav;
//		}
//	}

	@GetMapping("/editsceneriolist/{id}")
	public ModelAndView editsceneriolist(@PathVariable("id") Integer id) {
		ModelAndView mav = new ModelAndView("Add_Scenario");
		try {
			List<CloudInstance> instances = repository.getInstanceNameNotAssigned();
			List<CategoryMaster> categories = CategoryMasterRepository.findAll(); // get categories for dropdown

			Add_Scenario scenario = ScenarioRepository.findByCheckId(id);
			if (scenario == null) {
				throw new IllegalArgumentException("Invalid scenario ID: " + id);
			}

			List<ScenarioLabTemplate> assignedLabs = ScenarioLabTemplateRepository.findByScenarioId(id);
			List<String> assignedLabPairs = new ArrayList<>();

			for (ScenarioLabTemplate l : assignedLabs) {
				assignedLabPairs.add(l.getTemplateId() + "~" + l.getTemplateName());
			}

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < assignedLabPairs.size(); i++) {
				sb.append(assignedLabPairs.get(i));
				if (i < assignedLabPairs.size() - 1) {
					sb.append(",");
				}
			}
			scenario.setLabs(sb.toString());

			mav.addObject("scenario", scenario);
			mav.addObject("instanceNameList", instances);
			mav.addObject("CategoryList", categories); // add category list here
			mav.addObject("pageTitle", "Edit Scenario (ID: " + id + ")");
			mav.addObject("isEdit", true);
			return mav;
		} catch (Exception e) {
			mav.addObject("message", e.getMessage());
			mav.addObject("isEdit", false);
			return mav;
		}
	}

	@GetMapping("/deletsceneriolist/{id}")
	public String deletsceneriolist(@PathVariable("id") Integer id) {

		try {
			ModelAndView mav = new ModelAndView("Add_Playlist");
//			mav.addObject("action_name", var_function_name);
//			mav.addObject("playlist", PlaylistRepository.deleteById(id));
			ScenarioRepository.deleteById(id);
//			mav.addObject("pageTitle",  "Edit " + disp_function_name + " (ID: " + id + ")");
//			return mav;
		} catch (Exception e) {
			ModelAndView mav = new ModelAndView("Add_Playlist");
//			mav.addObject("action_name", var_function_name);
			mav.addObject("message", e.getMessage());
//			return mav;
		}

		return "redirect:/guac/View_Scenario";
	}

//	@GetMapping("/View_Scenario")
//	public ModelAndView getView_Scenario(Principal principal) {
//		ModelAndView mav = new ModelAndView("View_Scenario");
//		JSONArray Finalarray = new JSONArray();
//		List<Add_Scenario> dataList;
//		try {
//			// start
//
//			Authentication authentication = (Authentication) principal;
//			User loginedUser = (User) authentication.getPrincipal();
//
//			String userName = loginedUser.getUsername();
//			String groupName = "";
//			StringBuilder vmNamesBuilder = new StringBuilder();
//
//			List<AppUser> userList = userRepository.findByuserName(userName);
//
//			boolean isSuperAdmin = authentication.getAuthorities().stream()
//					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));
//
//			boolean isAdmin = authentication.getAuthorities().stream()
//					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
//
//			if (isSuperAdmin) {
//				dataList = ScenarioRepository.findAll();
//			} else {
//				// Get group names
//				for (AppUser appUser : userList) {
//					groupName = appUser.getGroupName(); // Assuming only one AppUser per username
//				}
//
//				List<String> groups = new ArrayList<>();
//				StringTokenizer groupTokenizer = new StringTokenizer(groupName, ",");
//				while (groupTokenizer.hasMoreTokens()) {
//					groups.add(groupTokenizer.nextToken());
//				}
//
//				// Get VM names by group
//				List<Object[]> vmList = repository.getInstanceNameByGroup(groups, true);
//				for (Object[] vmEntry : vmList) {
//					vmNamesBuilder.append(vmEntry[1].toString()).append(",");
//				}
//
//				// Split VM names string into a list
//				List<String> vmGroups = new ArrayList<>();
//				String vmNames = vmNamesBuilder.toString();
//				if (!vmNames.isEmpty()) {
//					StringTokenizer vmTokenizer = new StringTokenizer(vmNames, ",");
//					while (vmTokenizer.hasMoreTokens()) {
//						vmGroups.add(vmTokenizer.nextToken());
//					}
//				}
////				dataList = ScenarioRepository.getView_Scenario(vmGroups);
//				dataList = ScenarioRepository.findAll();
//			}
//
////			 dataList = kVMDriveDetailsRepository.findBykVMDetails();
//			System.out.println("Fetched Data: " + dataList.toString()); // Print to console
//			int srno = 0;
//			for (Add_Scenario temp : dataList) {
//				JSONObject obj = new JSONObject();
//
//				String Scenario_Name = temp.getScenarioName() != null ? temp.getScenarioName() : "";
//				String Scenario_Title = temp.getScenarioTitle() != null ? temp.getScenarioTitle() : "";
//				String Description = temp.getDescription() != null ? temp.getDescription() : "";
//				String Category = temp.getCategory() != null ? temp.getCategory() : "";
//				String Scenario_Type = temp.getScenarioType() != null ? temp.getScenarioType() : "";
//				String Mode = temp.getMode() != null ? temp.getMode() : "";
//				String Difficulty_Level = temp.getDifficultyLevel() != null ? temp.getDifficultyLevel() : "";
//				String Duration = temp.getDuration() != null ? temp.getDuration() : "";
//				String Labs = temp.getLabs() != null ? temp.getLabs() : "";
////				String Cover_Image = temp.getCover_Image() != null ? temp.getCover_Image() : "";
//				String Cover_Image = "";
//				int SrNo = temp.getId();
//
////				srno++;
//
//				obj.put("Scenario_Name", Scenario_Name);
//				obj.put("Scenario_Title", Scenario_Title);
////				obj.put("Description", Description);
//				obj.put("Category", Category);
//				obj.put("Scenario_Type", Scenario_Type);
//				obj.put("Mode", Mode);
//				obj.put("Difficulty_Level", Difficulty_Level);
//				obj.put("Duration", Duration);
////				obj.put("Labs", Labs);
//				obj.put("Cover_Image", Cover_Image);
//				obj.put("Id", SrNo);
//
//				Finalarray.put(obj);
//			}
//
////			System.out.println("Finalarray_getView_Scenario ::" + Finalarray);
//
//			mav.addObject("listObj", Finalarray.toString());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			mav.addObject("listObj", null);
//			mav.addObject("error", e.getMessage());
//			System.out.println("Error fetching data: " + e.getMessage());
//		}
//		return mav;
//	}

//	@GetMapping("/View_Scenario")
//	public ModelAndView getView_Scenario(Principal principal) {
//		ModelAndView mav = new ModelAndView("View_Scenario");
//		JSONArray Finalarray = new JSONArray();
//		List<Add_Scenario> dataList = new ArrayList<>();
//
//		try {
//			Authentication authentication = (Authentication) principal;
//			User loginedUser = (User) authentication.getPrincipal();
//
//			String userName = loginedUser.getUsername();
//
//			boolean isSuperAdmin = authentication.getAuthorities().stream()
//					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));
//
//			if (isSuperAdmin) {
//				// SuperAdmin → all scenarios
//				dataList = ScenarioRepository.findAll();
//			} else {
//				// Normal User → only mapped scenarios
//				List<Integer> scenarioIds = UserScenarioMappingRepository.findScenarioIdsByUserName(userName);
//
//				if (scenarioIds != null && !scenarioIds.isEmpty()) {
//					dataList = ScenarioRepository.findAllById(scenarioIds);
//				}
//			}
//
//			// Convert to JSON
//			for (Add_Scenario temp : dataList) {
//				JSONObject obj = new JSONObject();
//				obj.put("Scenario_Name", temp.getScenarioName() != null ? temp.getScenarioName() : "");
//				obj.put("Scenario_Title", temp.getScenarioTitle() != null ? temp.getScenarioTitle() : "");
//				obj.put("Category", temp.getCategory() != null ? temp.getCategory() : "");
//				obj.put("Scenario_Type", temp.getScenarioType() != null ? temp.getScenarioType() : "");
//				obj.put("Mode", temp.getMode() != null ? temp.getMode() : "");
//				obj.put("Difficulty_Level", temp.getDifficultyLevel() != null ? temp.getDifficultyLevel() : "");
//				obj.put("Duration", temp.getDuration() != null ? temp.getDuration() : "");
//				obj.put("Cover_Image", "");
//				obj.put("Id", temp.getId());
//				Finalarray.put(obj);
//			}
//
//			System.out.println("Finalarray_getView_Scenario ::" + Finalarray);
//			mav.addObject("listObj", Finalarray.toString());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			mav.addObject("listObj", null);
//			mav.addObject("error", e.getMessage());
//		}
//
//		return mav;
//	}
	
	@GetMapping("/View_Scenario")
	public ModelAndView getView_Scenario(Principal principal) {
	    ModelAndView mav = new ModelAndView("View_Scenario");
	    JSONArray Finalarray = new JSONArray();
	    List<Add_Scenario> dataList = new ArrayList<>();

	    try {
	        Authentication authentication = (Authentication) principal;
	        User loginedUser = (User) authentication.getPrincipal();
	        String userName = loginedUser.getUsername();

	        boolean isSuperAdmin = authentication.getAuthorities().stream()
	                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));

	        if (isSuperAdmin) {
	            // SuperAdmin → all scenarios
	            dataList = ScenarioRepository.findAll();
	        } else {
	            // Normal User → scenarios based on their subjects
	            List<Integer> userSubjectIds = getUserSubjectIds(userName);
	            
	            if (userSubjectIds != null && !userSubjectIds.isEmpty()) {
	                // Get scenario IDs mapped to user's subjects
	                List<Integer> scenarioIds = SubjectScenarioMappingRepository.findScenarioIdsBySubjectIds(userSubjectIds);
	                
	                if (scenarioIds != null && !scenarioIds.isEmpty()) {
	                    dataList = ScenarioRepository.findAllById(scenarioIds);
	                }
	            }
	        }

	        // Convert to JSON (your existing code)
	        for (Add_Scenario temp : dataList) {
	            JSONObject obj = new JSONObject();
	            obj.put("Scenario_Name", temp.getScenarioName() != null ? temp.getScenarioName() : "");
	            obj.put("Scenario_Title", temp.getScenarioTitle() != null ? temp.getScenarioTitle() : "");
	            obj.put("Category", temp.getCategory() != null ? temp.getCategory() : "");
	            obj.put("Scenario_Type", temp.getScenarioType() != null ? temp.getScenarioType() : "");
	            obj.put("Mode", temp.getMode() != null ? temp.getMode() : "");
	            obj.put("Difficulty_Level", temp.getDifficultyLevel() != null ? temp.getDifficultyLevel() : "");
	            obj.put("Duration", temp.getDuration() != null ? temp.getDuration() : "");
	            obj.put("Cover_Image", "");
	            obj.put("Id", temp.getId());
	            Finalarray.put(obj);
	        }

	        System.out.println("Finalarray_getView_Scenario ::" + Finalarray);
	        mav.addObject("listObj", Finalarray.toString());

	    } catch (Exception e) {
	        e.printStackTrace();
	        mav.addObject("listObj", null);
	        mav.addObject("error", e.getMessage());
	    }

	    return mav;
	}

	// Helper method to get user's subject IDs
	private List<Integer> getUserSubjectIds(String userName) {
	    List<Integer> subjectIds = new ArrayList<>();
	    
	    try {
	        // Get user details
	        List<AppUser> users = AppUserRepository.findByUserName(userName);
	        
	        if (users != null && !users.isEmpty()) {
	            AppUser user = users.get(0); // Assuming one user per username
	            
	            // If user has a semester assigned, get subjects for that semester
	            if (user.getSemesterName() != null) {
	                Integer semesterId = user.getSemesterName().getSemesterId();
	                List<SubjectMaster> userSubjects = SubjectMasterRepository.findBysemester_SemesterId(semesterId);
	                
	                // Extract subject IDs
	                for (SubjectMaster subject : userSubjects) {
	                    subjectIds.add(subject.getSubjectId());
	                }
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return subjectIds;
	}

	@GetMapping("/My_Scenario_Inprogress")
	public ModelAndView getMy_View_Scenario(Principal principal, Model model) {
		ModelAndView mav = new ModelAndView("My_View_Scenario");
		JSONArray finalArray = new JSONArray();

		try {
			Authentication authentication = (Authentication) principal;
			User loginedUser = (User) authentication.getPrincipal();
			String userName = loginedUser.getUsername();

			model.addAttribute("pageTitle", "My Scenario Inprogress");
			List<UserScenario> scenarioIds = UserScenerioRepository.findByUsername(userName);

			for (UserScenario objs : scenarioIds) {
				Add_Scenario temp = ScenarioRepository.findById(Integer.parseInt(objs.getScenarioId())).orElse(null);

				if (temp == null) {
					continue; // skip if scenario not found
				}

				String username = principal.getName();
				System.out.println("scenarioId : " + objs.getId());

				Integer falseCount = instructionTemplateRepository
						.getFalseCompletionCountsByusernameandscenarioId(username, temp.getId());
				Integer trueCount = instructionTemplateRepository
						.getTrueCompletionCountsByusernameandscenarioId(username, temp.getId());

				falseCount = (falseCount != null) ? falseCount : 0;
				trueCount = (trueCount != null) ? trueCount : 0;

				int total = falseCount + trueCount;
				int percentage = 0;

				if (total > 0) {
					percentage = (trueCount * 100) / total;
				}

				System.out.println("percentage : " + percentage);
				System.out.println("percentage : " + percentage);

				// ✅ compare integers, not string
				if (percentage != 100) {
					JSONObject obj = new JSONObject();
					obj.put("Scenario_Name", temp.getScenarioName() != null ? temp.getScenarioName() : "");
					obj.put("Scenario_Title", temp.getScenarioTitle() != null ? temp.getScenarioTitle() : "");
					obj.put("Category", temp.getCategory() != null ? temp.getCategory() : "");
					obj.put("Scenario_Type", temp.getScenarioType() != null ? temp.getScenarioType() : "");
					obj.put("Mode", temp.getMode() != null ? temp.getMode() : "");
					obj.put("Difficulty_Level", temp.getDifficultyLevel() != null ? temp.getDifficultyLevel() : "");
					obj.put("Duration", temp.getDuration() != null ? temp.getDuration() : "");
					obj.put("Cover_Image", ""); // add image path if available
					obj.put("Id", temp.getId());

					finalArray.put(obj);
				}
			}

			System.out.println("Finalarray_getMy_View_Scenario ::" + finalArray);
			mav.addObject("listObj", finalArray.toString());

		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
			System.out.println("Error fetching data: " + e.getMessage());
		}

		return mav;
	}

	@GetMapping("/My_Scenario_Completed")
	public ModelAndView getMy_View_Scenario_Completed(Principal principal, Model model) {
		ModelAndView mav = new ModelAndView("My_View_Scenario");
		JSONArray finalArray = new JSONArray();

		try {
			Authentication authentication = (Authentication) principal;
			User loginedUser = (User) authentication.getPrincipal();
			String userName = loginedUser.getUsername();

			model.addAttribute("pageTitle", "My Scenario Completed");

			List<UserScenario> scenarioIds = UserScenerioRepository.findByUsername(userName);

			for (UserScenario objs : scenarioIds) {
				Add_Scenario temp = ScenarioRepository.findById(Integer.parseInt(objs.getScenarioId())).orElse(null);

				if (temp == null) {
					continue; // skip if scenario not found
				}

				String username = principal.getName();
				System.out.println("scenarioId : " + objs.getId());

				Integer falseCount = instructionTemplateRepository
						.getFalseCompletionCountsByusernameandscenarioId(username, temp.getId());
				Integer trueCount = instructionTemplateRepository
						.getTrueCompletionCountsByusernameandscenarioId(username, temp.getId());

				falseCount = (falseCount != null) ? falseCount : 0;
				trueCount = (trueCount != null) ? trueCount : 0;

				int total = falseCount + trueCount;
				int percentage = 0;

				if (total > 0) {
					percentage = (trueCount * 100) / total;
				}

				System.out.println("percentage : " + percentage);
				System.out.println("percentage : " + percentage);

				// ✅ compare integers, not string
				if (percentage == 100) {
					JSONObject obj = new JSONObject();
					obj.put("Scenario_Name", temp.getScenarioName() != null ? temp.getScenarioName() : "");
					obj.put("Scenario_Title", temp.getScenarioTitle() != null ? temp.getScenarioTitle() : "");
					obj.put("Category", temp.getCategory() != null ? temp.getCategory() : "");
					obj.put("Scenario_Type", temp.getScenarioType() != null ? temp.getScenarioType() : "");
					obj.put("Mode", temp.getMode() != null ? temp.getMode() : "");
					obj.put("Difficulty_Level", temp.getDifficultyLevel() != null ? temp.getDifficultyLevel() : "");
					obj.put("Duration", temp.getDuration() != null ? temp.getDuration() : "");
					obj.put("Cover_Image", ""); // add image path if available
					obj.put("Id", temp.getId());

					finalArray.put(obj);
				}
			}

			System.out.println("Finalarray_getMy_View_Scenario ::" + finalArray);
			mav.addObject("listObj", finalArray.toString());

		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
			System.out.println("Error fetching data: " + e.getMessage());
		}

		return mav;
	}

//	@PostMapping("/addplaylist_Scenario")
//	@ResponseBody
//	public String addScenarioToPlaylist(@RequestParam("playlistId") int playlistId,
//			@RequestParam("scenarioId") int scenarioId) {
//		try {
//			// fetch Playlist and Scenario
//			Playlist playlist = PlaylistRepository.findById(playlistId)
//					.orElseThrow(() -> new RuntimeException("Playlist not found"));
//
//			Add_Scenario scenario = ScenarioRepository.findById(scenarioId)
//					.orElseThrow(() -> new RuntimeException("Scenario not found"));
//
//			// create composite key
//			PlaylistScenarioId id = new PlaylistScenarioId(playlistId, scenarioId);
//
//			// create entity
//			PlaylistScenario ps = new PlaylistScenario();
//			ps.setId(id);
//			ps.setPlaylist(playlist);
//			ps.setScenario(scenario);
//
//			PlaylistsSenarioRepository.save(ps);
//
//			return "success";
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return "error: " + e.getMessage();
//		}
//	}

	@PostMapping("/addplaylist_Scenario")
	@ResponseBody
	public String addScenarioToPlaylist(@RequestParam("playlistId") int playlistId,
			@RequestParam("scenarioId") List<Integer> scenarioIds) {
		try {
			Playlist playlist = PlaylistRepository.findById(playlistId)
					.orElseThrow(() -> new RuntimeException("Playlist not found"));

			for (Integer scenarioId : scenarioIds) {
				Add_Scenario scenario = ScenarioRepository.findById(scenarioId)
						.orElseThrow(() -> new RuntimeException("Scenario not found: " + scenarioId));

				// Prevent duplicates
				boolean exists = PlaylistItemRepository.existsByPlaylistIdAndItemIdAndItemType(playlistId, scenarioId,
						PlaylistItem.ItemType.scenario);

				if (!exists) {
					PlaylistItem item = new PlaylistItem();
					item.setPlaylistId(playlistId);
					item.setItemId(scenarioId);
					item.setItemType(PlaylistItem.ItemType.scenario);
					PlaylistItemRepository.save(item);
				}
			}
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "error: " + e.getMessage();
		}
	}

	@GetMapping("/getSubPlaylistScenarios")
	@ResponseBody
	public List<Map<String, Object>> getSubPlaylistScenarios(@RequestParam int subPlaylistId) {
		List<Map<String, Object>> scenarios = new ArrayList<>();

		try {
			SubPlaylist subPlaylist = SubPlaylistRepository.findById(subPlaylistId).orElse(null);
			if (subPlaylist != null) {
				for (SubPlaylistScenario sps : subPlaylist.getScenarios()) {
					Add_Scenario scenario = sps.getScenario();
					Map<String, Object> scenarioMap = new HashMap<>();
					scenarioMap.put("id", scenario.getId());
					scenarioMap.put("title", scenario.getScenarioTitle());
					scenarioMap.put("type", scenario.getScenarioType());
					scenarioMap.put("difficulty", scenario.getDifficultyLevel());
					scenarios.add(scenarioMap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return scenarios;
	}

	@GetMapping("/removePlaylistItem")
	@ResponseBody
	public String removePlaylistItem(@RequestParam int playlistId, @RequestParam String itemType,
			@RequestParam int itemId) {
		try {
			PlaylistItem.ItemType type = PlaylistItem.ItemType.valueOf(itemType);
			PlaylistItemRepository.deleteByPlaylistIdAndItemTypeAndItemId(playlistId, type, itemId);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
	}
//	

	@PostMapping("/addComment")
	@ResponseBody
	public String addComment(@RequestParam("comment") String comment, @RequestParam("scenarioId") Long scenarioId,
			Principal principal) {
		String result = "fail";
		try {
			// Get the logged-in user

			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			String username = loginedUser.getUsername();

			ScenarioComments savedComment = new ScenarioComments();
			savedComment.setScenarioId(scenarioId);
			savedComment.setComment(comment);
			savedComment.setCreateBy(username);
			savedComment.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

			ScenarioCommentsRepository.save(savedComment);

			result = "success";

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error saving comment: " + e.getMessage());
		}
		return result;
	}

	@GetMapping("/getComments")
	@ResponseBody
	public List<Map<String, Object>> getComments(@RequestParam("scenarioId") Long scenarioId) {
		List<ScenarioComments> comments = ScenarioCommentsRepository.findByScenarioId(scenarioId);

		List<Map<String, Object>> response = new ArrayList<>();
		for (ScenarioComments c : comments) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", c.getId());
			map.put("content", c.getComment());
			map.put("author", c.getCreateBy());
			map.put("createdAt", c.getCreatedAt());
			response.add(map);
		}

		return response;
	}

	@PostMapping("/updateComment")
	@ResponseBody
	public String updateComment(@RequestParam("commentId") Long commentId, @RequestParam("comment") String comment,
			Principal principal) {

		String result = "fail";
		try {
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			String username = loginedUser.getUsername();

			Timestamp updatedTime = Timestamp.valueOf(LocalDateTime.now());

			int updatedRows = ScenarioCommentsRepository.updateCommentById(commentId, comment, username, updatedTime);

			if (updatedRows > 0) {
				result = "success";
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error updating comment: " + e.getMessage());
		}

		return result;
	}

	@PostMapping("/deleteComment")
	@ResponseBody
	public String deleteComment(@RequestParam("commentId") Long commentId) {
		String result = "fail";
		try {
			int deletedRows = ScenarioCommentsRepository.deleteByIdCustom(commentId);

			if (deletedRows > 0) {
				result = "success";
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error deleting comment: " + e.getMessage());
		}

		return result;
	}

	@PostMapping("/showLabButtonOn_Check")
	@ResponseBody
	public String showLabButtonOn_Check(@RequestParam("scenarioId") Integer scenarioId,
			@RequestParam("scenarioName") String scenarioName, Principal principal) {
		try {
//			System.out.println("Checking scenarioId: " + scenarioId); usernmae

			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			String username = loginedUser.getUsername();

//			Optional<UserScenario> scenarioOpt = UserScenerioRepository.findByScenarioId(scenarioId, username);

			List<UserLab> labs = UserLabRepository.findByScenarioIdAndUsername(scenarioId, username);

			if (!labs.isEmpty()) {
				return "available";
			} else {
				return "notavailable";
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}

	@PostMapping("/start_lab")
	@ResponseBody
	public String start_lab(@RequestParam("containerName") String containerName, Principal principal) {
		try {
			System.out.println("Inside_start_lab containerName: " + containerName);
			dockerService.startContainerByName(containerName);
			String Status = "running";
			UserLabRepository.updateStatusByLabName(containerName, Status);
			System.out.println("Container start successfully: " + containerName);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception Inside_start_lab : " + e);
			return "fail";
		}
	}

	@PostMapping("/stop_lab")
	@ResponseBody
	public String stop_lab(@RequestParam("containerName") String containerName, Principal principal) {
		try {
			System.out.println("Inside_stop_lab containerName: " + containerName);
			String Status = "stop";
			dockerService.stopContainerByName(containerName);
			UserLabRepository.updateStatusByLabName(containerName, Status);
			System.out.println("Container stopped successfully: " + containerName);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception Inside_stop_lab : " + e);
			return "fail";
		}
	}

//	@PostMapping("/remove_lab")
//	@ResponseBody
//	public String remove_lab(@RequestParam("containerName") String containerName, Principal principal) {
//		try {
//			
//			 Authentication auth = (Authentication) principal;
//			    String username = auth.getName();
//			    
//			System.out.println("Inside_remove_lab containerName: " + containerName);
//			dockerService.removeContainerByName(containerName);
//			System.out.println("Container removed successfully: " + containerName);
//			
//			List<UserLab> list = UserLabRepository.findByInstnaceName(containerName);
//			
//			String getusername=  list.getusername;
//			String scenarioId = list.scenarioId;
//
//			CommandHistoryRepository.deleteByContainerName(containerName);
//			instructionTemplateRepository.deleteByLabName(containerName);
//			UserLabRepository.deleteByInstanceName(containerName);
//			
//			
//			UserScenerioRepository.deleteByScenarioIdandUsername(getusername,scenarioId);
//
//			return "success";
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("Exception Inside_remove_lab : " + e);
//			return "fail";
//		}
//	}

	@PostMapping("/remove_lab")
	@ResponseBody
	public String remove_lab(@RequestParam("containerName") String containerName, Principal principal) {
		try {
			Authentication auth = (Authentication) principal;
			String username = auth.getName();

			System.out.println("Inside_remove_lab containerName: " + containerName);

			// remove container from docker
			dockerService.removeContainerByName(containerName);
			System.out.println("Container removed successfully: " + containerName);

			// find lab by instance name
			List<UserLab> labs = UserLabRepository.findByInstnaceName(containerName);

			if (!labs.isEmpty()) {
				UserLab lab = labs.get(0); // take first record
				String getUsername = lab.getUsername();
				Integer scenarioId = lab.getScenarioId();

				String scenarioIdStr = String.valueOf(scenarioId);

				// delete related records
				CommandHistoryRepository.deleteByContainerName(containerName);
				instructionTemplateRepository.deleteByLabName(containerName);
				UserLabRepository.deleteByInstanceName(containerName);

//				if (scenarioId != null) {
//					UserScenerioRepository.deleteByScenarioIdAndUsername(getUsername, scenarioIdStr);
//				}
			} else {
				System.out.println("No UserLab found for instanceName: " + containerName);
			}

			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception Inside_remove_lab : " + e);
			return "fail";
		}
	}

	@PostMapping("/reset_lab")
	@ResponseBody
	public String reset_lab(@RequestParam("containerName") String containerName, Principal principal) {
		try {
			System.out.println("Inside_reset_lab containerName: " + containerName);
//			dockerService.removeContainerByName(containerName);
			System.out.println("Container reset_lab successfully: " + containerName);

			CommandHistoryRepository.deleteByContainerName(containerName);
			int updatedRows = instructionTemplateRepository.UpdateresetByLabName(containerName);
			System.out.println("Rows updated instrion : " + updatedRows);
			UserLabRepository.UpdateresetByLabName(containerName);

			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception Inside_reset_lab : " + e);
			return "fail";
		}
	}

	@PostMapping("/getPercentageParticularScenario")
	@ResponseBody
	public String getPercentageParticularScenario(Principal principal, @RequestParam("scenarioId") int scenarioId,
			@RequestParam("scenarioName") String scenarioName) {
		try {
			String username = principal.getName();
			System.out.println("scenarioId : " + scenarioId);

			Integer falseCount = instructionTemplateRepository.getFalseCompletionCountsByusernameandscenarioId(username,
					scenarioId);
			Integer trueCount = instructionTemplateRepository.getTrueCompletionCountsByusernameandscenarioId(username,
					scenarioId);

			falseCount = (falseCount != null) ? falseCount : 0;
			trueCount = (trueCount != null) ? trueCount : 0;

			int total = falseCount + trueCount;

			if (total == 0) {
				return "0";
			}

			int percentage = (trueCount * 100) / total;
			System.out.println("percentage : " + percentage);

			return String.valueOf(percentage);

		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}

	@PostMapping("/savediscoverdokcerIpaddress")
	public String savediscoverdokcerIpaddress(@RequestParam("selectedIp") String selectedIp,
			RedirectAttributes redirectAttributes) {
		try {
			dockerService.discoverAndSaveDockerNetworks(selectedIp);
			redirectAttributes.addFlashAttribute("result", "success");

		} catch (org.springframework.dao.DataIntegrityViolationException ex) {
			redirectAttributes.addFlashAttribute("result", "Duplicate network entry. This network already exists.");
		} catch (Exception ex) {
			redirectAttributes.addFlashAttribute("result", "An unexpected error occurred: " + ex.getMessage());
		}

		return "redirect:/guac/Add_Docker";
	}

	@PostMapping("/checkLabCompletion")
	@ResponseBody
	public String checkLabCompletion(@RequestParam("labId") String labId) {
		try {
			int num = Integer.parseInt(labId);

			System.out.println("Lab ID: " + num);
			List<UserLab> userLabs = UserLabRepository.findByguacamoleId(num);

			if (userLabs != null && !userLabs.isEmpty()) {

				for (UserLab lab : userLabs) {
					System.out.println("Lab ID: " + lab.getLabId() + ", Status: " + lab.getStatus());
				}

				boolean isCompleted = userLabs.stream().anyMatch(lab -> "Completed".equalsIgnoreCase(lab.getStatus()));

				if (isCompleted) {
					return "success";
				} else {
					return "fail";
				}
			} else {
				System.out.println("No UserLab records found for labId: " + num);
				return "fail";
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid labId format: " + labId);
			return "fail";
		} catch (Exception ex) {
			System.out.println("Error checking lab completion: " + ex.getMessage());
			return "fail";
		}
	}

	@PostMapping("/getCompletedCommands")
	@ResponseBody
	public Map<String, Object> getCompletedCommands(@RequestParam String labId) {
		Map<String, Object> response = new HashMap<>();

		try {
			int guacamoleId = Integer.parseInt(labId);

			// Find UserLab by guacamoleId
			List<UserLab> labDetails = UserLabRepository.findByguacamoleId(guacamoleId);

			if (!labDetails.isEmpty()) {
				UserLab userLab = labDetails.get(0);
				String labName = userLab.getInstanceName();

				// Find all executed commands for this lab name
				List<UserWiseChatBoatInstructionTemplate> executedCommands = instructionTemplateRepository
						.findExecutedByLabName(labName);

				if (!executedCommands.isEmpty()) {
					// Map commands to a list of simplified objects for frontend
					List<Map<String, String>> commandsList = new ArrayList<>();

					for (UserWiseChatBoatInstructionTemplate cmd : executedCommands) {
						Map<String, String> cmdMap = new HashMap<>();
						byte[] instructionBytes = cmd.getInstructionDetails();
						String instruction = new String(instructionBytes, StandardCharsets.UTF_8);

						cmdMap.put("instruction", instruction);
						cmdMap.put("command", cmd.getInstructionCommand());
						commandsList.add(cmdMap);
					}

					response.put("success", true);
					response.put("completedCommands", commandsList);
				} else {
					response.put("success", true);
					response.put("completedCommands", Collections.emptyList());
				}
			} else {
				response.put("success", false);
				response.put("error", "Lab not found.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("error", "Failed to process command.");
		}

		return response;
	}

//	getPlaylists

//	@PostMapping("/subplaylistsave")
//	public String subplaylistsaveData(SubPlaylist obj, RedirectAttributes redirectAttributes, Principal principal) {
//
//		try {
//			// Set CreatedBy
//			if (principal != null) {
//				obj.setCreatedBy(principal.getName());
//			}
//
//			boolean isNew = (obj.getId() == 0);
//
//			// If editing, fetch existing and merge if necessary
//			if (!isNew) {
//				Optional<SubPlaylist> existing = SubPlaylistRepository.findById(obj.getId());
//				if (existing.isPresent()) {
//					SubPlaylist existingPlaylist = existing.get();
//					// Optionally merge data (if some fields should not be overwritten)
//				} else {
//					redirectAttributes.addFlashAttribute("message", "Playlist not found.");
//					redirectAttributes.addFlashAttribute("status", "error");
//					return "redirect:/guac/Add_Sub_Playlist";
//				}
//			}
//
//			// Save to DB
//			SubPlaylistRepository.save(obj);
//
//			redirectAttributes.addFlashAttribute("message", "The playlist has been saved successfully!");
//			redirectAttributes.addFlashAttribute("status", "success");
//
////	        return "redirect:/guac/View_Particular_Playlist?Id=" + obj.getId();
//			return "redirect:/guac/Add_Sub_Playlist";
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			redirectAttributes.addFlashAttribute("message", "Error while saving playlist: " + e.getMessage());
//			redirectAttributes.addFlashAttribute("status", "error");
//			return "redirect:/guac/Add_Sub_Playlist";
//		}
//	}

//	@PostMapping("/subplaylistsave")
//	public String subplaylistsaveData(SubPlaylist obj, RedirectAttributes redirectAttributes,
//			@RequestParam(required = false) MultipartFile cover_image, Principal principal) {
//
//		try {
//			// Set createdBy
//			if (principal != null) {
//				obj.setCreatedBy(principal.getName());
//			}
//
//			boolean isNew = (obj.getId() == 0);
//
//			if (isNew) {
//				// New playlist - handle image
//				if (cover_image != null && !cover_image.isEmpty()) {
//					String contentType = cover_image.getContentType();
//					if (contentType != null && contentType.startsWith("image/")) {
//						obj.setCoverImage(cover_image.getBytes());
//						System.out.println("Uploaded image saved to database");
//					} else {
//						redirectAttributes.addFlashAttribute("message",
//								"Invalid file type. Please upload an image file.");
//						redirectAttributes.addFlashAttribute("status", "error");
//					}
//				} else {
//					// No image uploaded - load default
//					String defaultImagePath = "C:\\Users\\vijay\\Desktop\\18825\\New folder\\default.jpg";
//					try {
//						byte[] defaultImageBytes = Files.readAllBytes(Paths.get(defaultImagePath));
//						obj.setCoverImage(defaultImageBytes);
//						System.out.println("Default image loaded and saved to database");
//					} catch (IOException e) {
//						System.err.println("Error loading default image: " + e.getMessage());
//						obj.setCoverImage(createPlaceholderImage());
//						System.out.println("Placeholder image created and saved to database");
//					}
//				}
//			} else {
//				// Editing existing playlist
//				Optional<SubPlaylist> existing = SubPlaylistRepository.findById(obj.getId());
//				if (existing.isPresent()) {
//					SubPlaylist existingPlaylist = existing.get();
//
//					// Retain existing image if new one isn't uploaded
//					if (cover_image == null || cover_image.isEmpty()) {
//						obj.setCoverImage(existingPlaylist.getCoverImage());
//					} else {
//						String contentType = cover_image.getContentType();
//						if (contentType != null && contentType.startsWith("image/")) {
//							obj.setCoverImage(cover_image.getBytes());
//							System.out.println("Updated image saved to database");
//						} else {
//							redirectAttributes.addFlashAttribute("message",
//									"Invalid file type. Please upload an image file.");
//							redirectAttributes.addFlashAttribute("status", "error");
//						}
//					}
//				}
//			}
//
//			// Save to DB
//			SubPlaylistRepository.save(obj);
//
//			redirectAttributes.addFlashAttribute("message", "The playlist has been saved successfully!");
//			redirectAttributes.addFlashAttribute("status", "success");
//
////	        return "redirect:/guac/View_Particular_Playlist?Id=" + obj.getId();
//			return "redirect:/guac/Add_Sub_Playlist";
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			redirectAttributes.addFlashAttribute("message", "Error while saving playlist: " + e.getMessage());
//			redirectAttributes.addFlashAttribute("status", "error");
//			return "redirect:/guac/Add_Sub_Playlist";
//		}
//	}

	@PostMapping("/subplaylistsave")
	public String saveSubPlaylist(@ModelAttribute("playlist") SubPlaylist obj, RedirectAttributes redirectAttributes,
			@RequestParam(required = false) MultipartFile cover_image, Principal principal) {
		try {
			if (principal != null) {
				obj.setCreatedBy(principal.getName());
			}

			boolean isNew = (obj.getId() == 0);

			if (isNew) {
				// New Playlist
				if (cover_image != null && !cover_image.isEmpty()) {
					if (cover_image.getContentType().startsWith("image/")) {
						obj.setCoverImage(cover_image.getBytes());
					} else {
						redirectAttributes.addFlashAttribute("message", "Invalid file type.");
						redirectAttributes.addFlashAttribute("status", "error");
						return "redirect:/guac/Add_Sub_Playlist";
					}
				} else {
					// Set default image
					String defaultImagePath = "C:\\Users\\vijay\\Desktop\\18825\\New folder\\default.jpg";
					try {
						byte[] defaultImageBytes = Files.readAllBytes(Paths.get(defaultImagePath));
						obj.setCoverImage(defaultImageBytes);
					} catch (IOException e) {
						obj.setCoverImage(new byte[0]); // Placeholder
					}
				}
			} else {
				// Update Playlist
				Optional<SubPlaylist> existingOpt = SubPlaylistRepository.findById(obj.getId());
				if (existingOpt.isPresent()) {
					SubPlaylist existing = existingOpt.get();
					if (cover_image == null || cover_image.isEmpty()) {
						obj.setCoverImage(existing.getCoverImage()); // retain old image
					} else {
						if (cover_image.getContentType().startsWith("image/")) {
							obj.setCoverImage(cover_image.getBytes());
						} else {
							redirectAttributes.addFlashAttribute("message", "Invalid file type.");
							redirectAttributes.addFlashAttribute("status", "error");
							return "redirect:/guac/Edit_Sub_Playlist?id=" + obj.getId();
						}
					}
				}
			}

			SubPlaylist savedObj = SubPlaylistRepository.save(obj);

			redirectAttributes.addFlashAttribute("message", "Playlist saved successfully!");
			redirectAttributes.addFlashAttribute("status", "success");

			// redirect logic
			if (isNew) {
				return "redirect:/guac/Add_Sub_Playlist";
			} else {
				return "redirect:/guac/View_Particular_SubPlaylist?Id=" + savedObj.getId();
			}

		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "Error: " + e.getMessage());
			redirectAttributes.addFlashAttribute("status", "error");
			return "redirect:/guac/Add_Sub_Playlist";
		}
	}

	@GetMapping("/View_Particular_SubPlaylist")
	public ModelAndView getView_Particular_SubPlaylist(@RequestParam String Id) {
		ModelAndView mav = new ModelAndView("View_Particular_SubPlaylist");
		JSONArray Finalarray = new JSONArray();

		try {
			int SRNO = Integer.parseInt(Id);

			// ✅ Fetch sub-playlist
			Optional<SubPlaylist> subPlaylistOpt = SubPlaylistRepository.findById(SRNO);

			if (subPlaylistOpt.isPresent()) {
				SubPlaylist subPlaylist = subPlaylistOpt.get();

				List<SubPlaylistScenario> scenarioLinks = SubPlaylistScenarioRepository.findBySubPlaylist(subPlaylist);

				for (SubPlaylistScenario link : scenarioLinks) {
					Add_Scenario temp = link.getScenario();

					JSONObject obj = new JSONObject();
					obj.put("item_type", "scenario");
					obj.put("Scenario_Name", temp.getScenarioName() != null ? temp.getScenarioName() : "");
					obj.put("Scenario_Title", temp.getScenarioTitle() != null ? temp.getScenarioTitle() : "");
					obj.put("Category", temp.getCategory() != null ? temp.getCategory() : "");
					obj.put("Scenario_Type", temp.getScenarioType() != null ? temp.getScenarioType() : "");
					obj.put("Mode", temp.getMode() != null ? temp.getMode() : "");
					obj.put("Difficulty_Level", temp.getDifficultyLevel() != null ? temp.getDifficultyLevel() : "");
					obj.put("Duration", temp.getDuration() != null ? temp.getDuration() : "");
					obj.put("NumberofInstance", temp.getNumberofInstance() != null ? temp.getNumberofInstance() : "");
					obj.put("Cover_Image", ""); // ✅ serve scenario image if needed
					obj.put("Id", temp.getId());
					obj.put("subPlaylistId", subPlaylist.getId()); // ✅ Add sub-playlist ID to each scenario
					Finalarray.put(obj);
				}

				// ✅ Pass scenario JSON
				mav.addObject("listObj", Finalarray.toString());

				// ✅ Pass SubPlaylist details separately
				JSONObject subObj = new JSONObject();
				subObj.put("Id", subPlaylist.getId());
				subObj.put("Playlist_Name", subPlaylist.getPlaylistName());
				subObj.put("Playlist_Title", subPlaylist.getPlaylistTitle());
				subObj.put("Description", subPlaylist.getDescription());
				subObj.put("Tag", subPlaylist.getTag());
				subObj.put("Cover_Image", "");
				mav.addObject("subPlaylistObj", subObj.toString());

			} else {
				mav.addObject("listObj", "[]");
				mav.addObject("subPlaylistObj", "{}");
				mav.addObject("error", "SubPlaylist not found for ID " + SRNO);
			}

			List<Add_Scenario> scenarios = ScenarioRepository.findAll();
			mav.addObject("scenarioList", scenarios);

		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("listObj", "[]");
			mav.addObject("subPlaylistObj", "{}");
			mav.addObject("error", e.getMessage());
			System.out.println("Error fetching data: " + e.getMessage());
		}

		return mav;
	}

	@PostMapping("/addSubPlaylist_Scenario")
	public String addSubPlaylist_Scenario(@RequestParam("subPlaylistId") Integer subPlaylistId,
			@RequestParam("scenarioIds") List<Integer> scenarioIds) {

		// ✅ Fetch existing SubPlaylist
		SubPlaylist subPlaylist = SubPlaylistRepository.findById(subPlaylistId)
				.orElseThrow(() -> new RuntimeException("SubPlaylist not found with ID " + subPlaylistId));

		for (Integer scenarioId : scenarioIds) {
			Add_Scenario scenario = ScenarioRepository.findById(scenarioId)
					.orElseThrow(() -> new RuntimeException("Scenario not found with ID " + scenarioId));

			SubPlaylistScenario joinEntity = new SubPlaylistScenario();
			joinEntity.setSubPlaylist(subPlaylist);
			joinEntity.setScenario(scenario);

			SubPlaylistScenarioRepository.save(joinEntity);
		}

		return "redirect:/guac/View_Particular_Playlist?Id=" + subPlaylistId;
	}

	@GetMapping("/removeSubPlaylistItem")
	@ResponseBody
	public String removeSubPlaylistItem(@RequestParam("subPlaylistId") Integer subPlaylistId,
			@RequestParam("itemId") Integer scenarioId) {
		try {
			// Call repository method to delete record by both IDs
			SubPlaylistScenarioRepository.deleteBySubPlaylistIdAndScenarioId(subPlaylistId, scenarioId);
			return "success";
		} catch (Exception e) {
			return "error";
		}
	}

//	@GetMapping("/Add_UserWisePlaylist")
//	public ModelAndView showAdd_UserWisePlaylistForm() {
//		ModelAndView mav = new ModelAndView("Add_ScenarioInSub_Playlist");
//		mav.addObject("pageTitle", "Add Scenario In Sub Playlist");
//
//		// All subplaylists
//		List<SubPlaylist> subPlaylists = SubPlaylistRepository.findAll();
//		mav.addObject("subPlaylists", subPlaylists);
//
//		// All scenarios
//		List<Add_Scenario> scenarios = ScenarioRepository.findAll();
//		mav.addObject("scenarios", scenarios);
//
//		// New object to bind form data
//		mav.addObject("subPlaylist", new SubPlaylist());
//
//		return mav;
//	}

	@GetMapping("/Add_UserWisePlaylist")
	public ModelAndView showAdd_UserWisePlaylistForm() {
		ModelAndView mav = new ModelAndView("Add_UserWisePlaylist");
		mav.addObject("pageTitle", "Add User Wise Playlist");

		List<Group> groups = GroupRepository.findAll();
		mav.addObject("groups", groups); // Changed from "group" to "groups" for clarity

		// Initially load all users, but we'll implement AJAX filtering
		List<AppUser> users = AppUserRepository.findAll();
		mav.addObject("users", users);

		// All playlists
		List<Playlist> playlists = PlaylistRepository.findAll();
		mav.addObject("playlists", playlists);

		List<SubPlaylist> subplaylists = SubPlaylistRepository.findAll();
		mav.addObject("subplaylists", subplaylists);

		List<Add_Scenario> scenarios = ScenarioRepository.findAll();
		mav.addObject("scenarios", scenarios); // Changed from "Scenario" to "scenarios"

		return mav;
	}

	// Add this endpoint for AJAX user filtering
	@GetMapping("/getUsersByGroup")
	@ResponseBody
	public List<AppUser> getUsersByGroup(@RequestParam(required = false) String groupName) {
		if (groupName == null || groupName.isEmpty() || groupName.equalsIgnoreCase("all")) {
			return AppUserRepository.findAll(); // return all users
		} else {
			return AppUserRepository.findByGroupName(groupName);
		}
	}

	@PostMapping("/save_UserWisePlaylist")
	public String saveUserWisePlaylist(@RequestParam(required = false) String groupId,
			@RequestParam(value = "userIds", required = false) List<Long> userIds,
			@RequestParam(value = "playlistIds", required = false) List<Integer> playlistIds,
			@RequestParam(value = "subplaylistIds", required = false) List<Integer> subplaylistIds,
			@RequestParam(value = "scenarioIds", required = false) List<Integer> scenarioIds) {

		try {
			// Check if userIds exist
			if (userIds != null && !userIds.isEmpty()) {
				for (Long userId : userIds) {
					String userName = AppUserRepository.getUserNameById(userId);
					System.out.println("Processing userId: " + userId + " -> userName: " + userName);

					if (userName == null) {
						System.err.println("⚠ No username found for userId=" + userId);
						continue; // Skip if no username found
					}

					// Save playlists
					if (playlistIds != null && !playlistIds.isEmpty()) {
						for (Integer playlistId : playlistIds) {
							UserPlaylistMapping userPlaylistEntity = new UserPlaylistMapping();
							userPlaylistEntity.setPlaylistId(playlistId);
							userPlaylistEntity.setUserName(userName);
							UserPlaylistMappingRepository.save(userPlaylistEntity);
						}
					}

					// Save subplaylists
					if (subplaylistIds != null && !subplaylistIds.isEmpty()) {
						for (Integer subplaylistId : subplaylistIds) {
							UserSubplaylistMapping userSubplaylistEntity = new UserSubplaylistMapping();
							userSubplaylistEntity.setSubPlaylistId(subplaylistId);
							userSubplaylistEntity.setUserName(userName);
							UserSubplaylistMappingRepository.save(userSubplaylistEntity);
						}
					}

					// Save scenarios
					if (scenarioIds != null && !scenarioIds.isEmpty()) {
						for (Integer scenarioId : scenarioIds) {
							UserScenarioMapping userScenarioEntity = new UserScenarioMapping();
							userScenarioEntity.setScenarioId(scenarioId);
							userScenarioEntity.setUserName(userName);
							UserScenarioMappingRepository.save(userScenarioEntity);
						}
					}
				}
			} else {
				System.err.println("⚠ No userIds provided, skipping save.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/guac/Add_UserWisePlaylist";
	}

	@GetMapping("/user_playlist_summary")
	public ModelAndView showUserPlaylistSummary() {
		ModelAndView mav = new ModelAndView("user_playlist_summary");

		try {
			// Call the method on the injected repository instance
			mav.addObject("pageTitle", "View User Wise Playlist");

			List<Group> groups = GroupRepository.findAll();
			mav.addObject("groups", groups); // Changed from "group" to "groups" for clarity

			// Initially load all users, but we'll implement AJAX filtering
			List<AppUser> users = AppUserRepository.findAll();
			mav.addObject("users", users);

			// All playlists
			List<Playlist> playlists = PlaylistRepository.findAll();
			mav.addObject("playlists", playlists);

			List<SubPlaylist> subplaylists = SubPlaylistRepository.findAll();
			mav.addObject("subplaylists", subplaylists);

			List<Add_Scenario> scenarios = ScenarioRepository.findAll();
			mav.addObject("scenarios", scenarios); // Changed from "Scenario" to "scenarios"

		} catch (Exception e) {
			mav.addObject("error", "Error fetching summary: " + e.getMessage());
		}

		return mav;
	}

	@PostMapping("/Update_UserWisePlaylistScenarioSubplaylist")
	public String updateUserMappings(@RequestParam(value = "userId", required = false) Long userId,
			@RequestParam(value = "playlistIds", required = false) List<Integer> playlistIds,
			@RequestParam(value = "subplaylistIds", required = false) List<Integer> subplaylistIds,
			@RequestParam(value = "scenarioIds", required = false) List<Integer> scenarioIds) {

		// Redirect to summary page
		String redirectUrl = "redirect:/guac/user_playlist_summary";

		try {
			// If userId is null or 0, skip processing
			if (userId == null || userId == 0) {
				return redirectUrl;
			}

			// 1. Find username by userId
			String userName = AppUserRepository.getUserNameById(userId);

			if (userName == null || userName.isEmpty()) {
				return redirectUrl;
			}

			// 2. Delete existing mappings for this user
			UserPlaylistMappingRepository.deleteByUserName(userName);
			UserSubplaylistMappingRepository.deleteByUserName(userName);
			UserScenarioMappingRepository.deleteByUserName(userName);

			// 3. Insert new playlist mappings if list is not empty
			if (playlistIds != null && !playlistIds.isEmpty()) {
				for (Integer playlistId : playlistIds) {
					UserPlaylistMapping entity = new UserPlaylistMapping();
					entity.setUserName(userName);
					entity.setPlaylistId(playlistId);
					UserPlaylistMappingRepository.save(entity);
				}
			}

			// 4. Insert new subplaylist mappings
			if (subplaylistIds != null && !subplaylistIds.isEmpty()) {
				for (Integer subplaylistId : subplaylistIds) {
					UserSubplaylistMapping entity = new UserSubplaylistMapping();
					entity.setUserName(userName);
					entity.setSubPlaylistId(subplaylistId);
					UserSubplaylistMappingRepository.save(entity);
				}
			}

			// 5. Insert new scenario mappings
			if (scenarioIds != null && !scenarioIds.isEmpty()) {
				for (Integer scenarioId : scenarioIds) {
					UserScenarioMapping entity = new UserScenarioMapping();
					entity.setUserName(userName);
					entity.setScenarioId(scenarioId);
					UserScenarioMappingRepository.save(entity);
				}
			}

		} catch (Exception e) {
			e.printStackTrace(); // Log exception
		}

		return redirectUrl;
	}

	@GetMapping("/getUserMappings")
	@ResponseBody
	public Map<String, Object> getUserMappings(@RequestParam long userId) {
		Map<String, Object> response = new HashMap<String, Object>();
		try {

			String userName = AppUserRepository.getUserNameById(userId);
			System.out.println("Processing userId: " + userId + " -> userName: " + userName);

			// Fetch playlist IDs
			List<UserPlaylistMapping> playlistMappings = UserPlaylistMappingRepository.findByUserName(userName);
			List<Integer> playlistIds = new ArrayList<Integer>();
			for (UserPlaylistMapping m : playlistMappings) {
				playlistIds.add(m.getPlaylistId());
			}

			// Fetch subplaylist IDs
			List<UserSubplaylistMapping> subplaylistMappings = UserSubplaylistMappingRepository
					.findByUserName(userName);
			List<Integer> subplaylistIds = new ArrayList<Integer>();
			for (UserSubplaylistMapping m : subplaylistMappings) {
				subplaylistIds.add(m.getSubPlaylistId());
			}

			// Fetch scenario IDs
			List<UserScenarioMapping> scenarioMappings = UserScenarioMappingRepository.findByUserName(userName);
			List<Integer> scenarioIds = new ArrayList<Integer>();
			for (UserScenarioMapping m : scenarioMappings) {
				scenarioIds.add(m.getScenarioId());
			}

			response.put("playlistIds", playlistIds);
			response.put("subplaylistIds", subplaylistIds);
			response.put("scenarioIds", scenarioIds);
			response.put("status", "success");
		} catch (Exception e) {
			response.put("status", "error");
			response.put("message", e.getMessage());
		}
		return response;
	}

	@GetMapping("/UserWisePerformance")
	public ModelAndView getUserWisePerformance(Principal principal) {
		ModelAndView mav = new ModelAndView("UserWisePerformance");
		JSONArray Finalarray = new JSONArray();

		try {
			List<AppUser> dataList = AppUserRepository.findAll();

			int srno = 0;
			for (AppUser temp : dataList) {
				JSONArray array = new JSONArray();

				String userName = temp.getUserName() != null ? temp.getUserName() : "";

				Integer falseCountObj = instructionTemplateRepository.getFalseCompletionCountsByTemplateId(userName);
				Integer trueCountObj = instructionTemplateRepository.getTrueCompletionCountsByTemplateId(userName);

				int falseCount = (falseCountObj != null) ? falseCountObj : 0;
				int trueCount = (trueCountObj != null) ? trueCountObj : 0;

				int total = trueCount + falseCount;
				int percentage = (total == 0) ? 0 : (trueCount * 100 / total);

				srno++;

				array.put(srno);
				array.put(userName);
				array.put(percentage + "%");

				// 4th column: action button
				String actionBtn = "<button class='btn btn-info action-btn' " + "onclick=\"getuserPerformance('"
						+ userName + "')\" " + "data-bs-toggle='modal' data-bs-target='#performanceModal' "
						+ "data-username='" + userName + "'>"
						+ "<i class='fas fa-chart-line'></i> View Scenarios</button>";
//
				array.put(actionBtn);

				Finalarray.put(array);
			}

			System.out.println("Finalarray_UserWisePerformance ::" + Finalarray);

			mav.addObject("listObj", Finalarray.toString());

		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
			System.out.println("Error fetching data: " + e.getMessage());
		}
		return mav;
	}

	@GetMapping("/UserNameTaskPerformance")
	public ModelAndView getUserTaskPerformance(Model model, Principal principal, @RequestParam String username) {
		ModelAndView mav = new ModelAndView("UserTaskPerformance");
		JSONArray finalArray = new JSONArray();

		try {
//	        Authentication auth = (Authentication) principal;
//	        String username = auth.getName();

			// Return List<Integer> now
			List<Object> scenarioIdList = instructionTemplateRepository.findByScenarioIdAndUserName(username);

			int srno = 0;
			for (Object scenarioId : scenarioIdList) {
				srno++;
				JSONArray array = new JSONArray();

				String scenarioName = "Unknown";

				Optional<Add_Scenario> scenarioOpt = ScenarioRepository
						.findById(Integer.parseInt(scenarioId.toString()));
				if (scenarioOpt.isPresent()) {
					scenarioName = scenarioOpt.get().getScenarioName();
				}

				Integer falseCount = instructionTemplateRepository.getFalseCompletionCountsByusernameandscenarioId(
						username, Integer.parseInt(scenarioId.toString()));
				Integer trueCount = instructionTemplateRepository.getTrueCompletionCountsByusernameandscenarioId(
						username, Integer.parseInt(scenarioId.toString()));

				int total = (falseCount != null ? falseCount : 0) + (trueCount != null ? trueCount : 0);
				int percentage = (total == 0) ? 0 : ((trueCount != null ? trueCount : 0) * 100 / total);

				array.put(srno);
				array.put(username);
				array.put(scenarioName);
				array.put(percentage + "%");

				finalArray.put(array);
			}

			mav.addObject("listObj", finalArray.toString());

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("performanceList", new ArrayList<>());
		}

		return mav;
	}

	@GetMapping("/UserTaskPerformance")
	public ModelAndView getUserTaskPerformance(Model model, Principal principal) {
		ModelAndView mav = new ModelAndView("UserTaskPerformance");
		JSONArray finalArray = new JSONArray();

		try {
			Authentication auth = (Authentication) principal;
			String username = auth.getName();

			// Return List<Integer> now
			List<Object> scenarioIdList = instructionTemplateRepository.findByScenarioIdAndUserName(username);

			int srno = 0;
			for (Object scenarioId : scenarioIdList) {
				srno++;
				JSONArray array = new JSONArray();

				String scenarioName = "Unknown";

				Optional<Add_Scenario> scenarioOpt = ScenarioRepository
						.findById(Integer.parseInt(scenarioId.toString()));
				if (scenarioOpt.isPresent()) {
					scenarioName = scenarioOpt.get().getScenarioName();
				}

				Integer falseCount = instructionTemplateRepository.getFalseCompletionCountsByusernameandscenarioId(
						username, Integer.parseInt(scenarioId.toString()));
				Integer trueCount = instructionTemplateRepository.getTrueCompletionCountsByusernameandscenarioId(
						username, Integer.parseInt(scenarioId.toString()));

				int total = (falseCount != null ? falseCount : 0) + (trueCount != null ? trueCount : 0);
				int percentage = (total == 0) ? 0 : ((trueCount != null ? trueCount : 0) * 100 / total);

				array.put(srno);
				array.put(username);
				array.put(scenarioName);
				array.put(percentage + "%");

				finalArray.put(array);
			}

			mav.addObject("listObj", finalArray.toString());

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("performanceList", new ArrayList<>());
		}

		return mav;
	}

	@GetMapping("/UserWiseChartBoarView")
	public ModelAndView getUserWiseChartBoarView(Principal principal) {
		ModelAndView mav = new ModelAndView("UserWiseChartBoarView");
		List<Map<String, Object>> finalList = new ArrayList<>();

		try {
			int srno = 0;
			List<Object[]> rows = instructionTemplateRepository.findDistinctLabUserTemplate();

			for (Object[] row : rows) {
				String labName = row[0] != null ? row[0].toString() : "";
				String userName = row[1] != null ? row[1].toString() : "";
				String templateName = row[2] != null ? row[2].toString() : "";

				Map<String, Object> map = new HashMap<>();
				map.put("srNo", ++srno);
				map.put("labName", labName);
				map.put("userName", userName);
				map.put("templateName", templateName);

				String eyeButton = "<button type='button' class='btn btn-sm btn-primary' "
						+ "onclick=\"getInstructionOfUser('" + templateName + "','" + userName + "')\">"
						+ "<i class='fas fa-eye'></i></button>";
				map.put("action", eyeButton);

				finalList.add(map);
			}

			mav.addObject("listObj", finalList);

		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("listObj", Collections.emptyList());
		}
		return mav;
	}

	@GetMapping("/getInstructionOfUser")
	@ResponseBody
	public String getInstructionOfUser(@RequestParam String Id, Principal principal) {
		StringBuilder html = new StringBuilder();

		try {

			Optional<CloudInstance> list = repository.findById(Integer.parseInt(Id));

			CloudInstance instance = list.get();

			Authentication auth = (Authentication) principal;
			String username = auth.getName();

			String templateName = instance.getInstance_name();

			List<Object[]> instructions = instructionTemplateRepository
					.findByTemplateNameAndUserName(instance.getInstance_name(), username);

			html.append(
					"<section class='content'><div class='container-fluid'><div class='row'><div class='col-md-12'>");
			html.append("<div class='card border-transparent'><div class='card-header'>");
			html.append("<h3 class='card-title' style='display:flex;flex-direction:column;'>")
					.append("<span style='font-weight:bold;'>Setup Instructions</span>")
					.append("<span style='font-size:1rem;color:#b5b5c3;'>Update instructions and commands for this Lab setup</span>")
					.append("</h3></div><div class='card-body'>");

			// Wrapper with scroll
			html.append("<div id='instructions-wrapper' style='max-height:400px;overflow-y:auto;padding-right:10px;'>");
			html.append("<div id='instructions-container'>");

//			int idx = 0;
			for (Object[] row : instructions) {
				int id = row[0] != null ? ((Number) row[0]).intValue() : 0; // get ID
				String command = row[2] != null ? row[2].toString() : "";
				byte[] detailsBytes = row[3] != null ? (byte[]) row[3] : null;
				String instructionDetails = detailsBytes != null ? new String(detailsBytes, StandardCharsets.UTF_8)
						: "";

				html.append("<div class='instruction-group mb-4 border p-3' data-id='").append(id).append("'>");

				html.append("<label><strong>Instruction</strong></label>");
				html.append("<div class='instruction-content mb-2 p-2 border' contenteditable='true' "
						+ "style='background:#fff; border-radius:5px; min-height:80px;'>").append(instructionDetails)
						.append("</div>");

				html.append("<label><strong>Command</strong></label>");
				html.append("<textarea class='form-control command' rows='2'>").append(command).append("</textarea>");

				html.append("</div>");
			}

			// If no instructions found
			if (instructions.isEmpty()) {
				html.append("<div class='alert alert-warning'>No instructions available for this template.</div>");
			}

			html.append("</div></div>");

			html.append("<div class='text-right mt-3'>").append("<button type='button' class='btn btn-success' ")
					.append("onclick=\"saveInstruction('").append(templateName).append("','").append(username)
					.append("')\">").append("Save</button>").append("</div>");

			html.append("</div></div></div></div></section>");

		} catch (Exception e) {
			html.setLength(0);
			html.append("<div class='alert alert-danger'>Error loading instructions: ").append(e.getMessage())
					.append("</div>");
			e.printStackTrace();
		}

		System.out.println("Final HTML: " + html);
		return html.toString();
	}

	@PostMapping("/saveInstructionOfUser")
	@ResponseBody
	public String saveInstructionOfUser(@RequestBody List<Map<String, String>> instructions) {
		try {
			for (Map<String, String> ins : instructions) {
				int id = Integer.parseInt(ins.get("id"));
				String command = ins.get("command");
				String instructionDetails = ins.get("instructionDetails");

				instructionTemplateRepository.updateInstructionById(id, command,
						instructionDetails.getBytes(StandardCharsets.UTF_8));
			}
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error: " + e.getMessage();
		}
	}

//	@GetMapping("/editplaylist/{id}")
//	public ModelAndView editplaylist(@PathVariable("id") Integer id) {
//
//		try {
//			ModelAndView mav = new ModelAndView("Add_Playlist");
////			mav.addObject("action_name", var_function_name);
//			mav.addObject("playlist", PlaylistRepository.findById(id).get());
//			mav.addObject("pageTitle", "Edit Playlist (ID: " + id + ")");
////			mav.addObject("pageTitle",  "Edit " + disp_function_name + " (ID: " + id + ")");
//			return mav;
//		} catch (Exception e) {
//			ModelAndView mav = new ModelAndView("Add_Playlist");
////			mav.addObject("action_name", var_function_name);
//			mav.addObject("message", e.getMessage());
//			return mav;
//		}
//	}

	@PostMapping("/getCloundInstanceEdit")
	public ModelAndView getCloundInstanceEdit(@RequestParam String id, Principal principal) {
		System.out.println("Edit Controller called for Id = " + id);

		ModelAndView mav = new ModelAndView("cloud_instance_edit");

		try {
			// Convert String ID to Integer
			int instanceId = Integer.parseInt(id);

			// Fetch the CloudInstance
			Optional<CloudInstance> optionalInstance = repository.findById(instanceId);
			if (!optionalInstance.isPresent()) {
				mav.addObject("error", "Instance not found");
				return mav;
			}

			CloudInstance instance = optionalInstance.get();
			mav.addObject("objEnt", instance);

			// Add any other dropdown data here if needed
			// e.g., mav.addObject("dcLocationList",
			// repositoryLocation.getAllDClocations());

		} catch (NumberFormatException e) {
			e.printStackTrace();
			mav.addObject("error", "Invalid ID format: " + id);
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("error", "Error fetching CloudInstance: " + e.getMessage());
		}

		return mav;
	}

//	@PostMapping("/getCloundInstanceEdit")
//	public String getCloundInstanceEdit(@RequestParam String id, Model model, Principal principal) {
//	    System.out.println("Edit Controller called for Id = " + id);
//
//	    try {
//	        Optional<CloudInstance> optionalInstance = repository.findById(Integer.parseInt(id));
//	        if (optionalInstance.isEmpty()) {
//	            model.addAttribute("error", "Instance not found");
//	            return "error_page"; // or return some error fragment
//	        }
//
//	        CloudInstance instance = optionalInstance.get();
//	        model.addAttribute("objEnt", instance);
//
//	        // Load dropdowns here if needed
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        model.addAttribute("error", "Error fetching CloudInstance: " + e.getMessage());
//	    }
//
//	    return "redirect:/Create_docker_network";
////	    return "cloud_instance_edit"; 
//	}

	@GetMapping("/deleteVM")
	@ResponseBody
	public ResponseEntity<String> deleteVM(@RequestParam("Id") Integer id, Principal principal) {
		try {
			Optional<CloudInstance> optionalInstance = repository.findById(id);

			if (!optionalInstance.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("VM not found with ID: " + id);
			}

			// Delete the VM
			repository.deleteById(id);

			return ResponseEntity.ok("VM deleted successfully");

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting VM: " + e.getMessage());
		}
	}

	@GetMapping("/View_All_Vm_Listing")
	public ModelAndView viewVmListing(Model model, Principal principal) {
		// Get the username of the authenticated user
		Authentication auth = (Authentication) principal;
		String username = auth.getName();

		// Create the ModelAndView
		ModelAndView mav = new ModelAndView("View_All_Vm_Listing");

		// Fetch all labs for the authenticated user
		List<UserLab> labs = UserLabRepository.findAll();

		// Add percentage for each lab
		List<Map<String, Object>> labData = new ArrayList<>();
		for (UserLab lab : labs) {
			Map<String, Object> map = new HashMap<>();
			map.put("lab", lab);

			// Fetch CloudInstance by instance name
			String instanceName = lab.getTemplateName();
			List<CloudInstance> cloudInstances = repository.findByInstanceName(instanceName);

			if (!cloudInstances.isEmpty()) {
				CloudInstance instance = cloudInstances.get(0); // ✅ first match

				String templateName = instance.getInstance_name();
				String PhysicalServerIP = instance.getPhysicalServerIP();
				String password = instance.getInstance_password();
				String os = instance.getSubproduct_id().getProduct_id().getProduct_name();

				// You can store them in the map if needed
				map.put("os", os);
				map.put("PhysicalServerIP", PhysicalServerIP);
			} else {
				System.out.println("No CloudInstance found for name: " + instanceName);
			}

			int ScenarioId = lab.getScenarioId();
			Optional<Add_Scenario> Scenariolist = ScenarioRepository.findById(ScenarioId);

			if (Scenariolist.isPresent()) {
				Add_Scenario Scenarioinstance = Scenariolist.get(); // Use get() on Optional to extract the value

				String ScenarioName = Scenarioinstance.getScenarioName();

				map.put("ScenarioName", ScenarioName);
			} else {
				System.out.println("No Add_Scenario found for id: " + ScenarioId);
			}

			// Handle LabId
			Long labIdLong = lab.getLabId();
			int labId = labIdLong.intValue();

			// Get true and false counts for completion
			Integer falseCountObj = instructionTemplateRepository.getfalseCompletionCountsByTemplateName(labId);
			Integer trueCountObj = instructionTemplateRepository.gettrueCompletionCountsByTemplateName(labId);

			// Handle null values
			int falseCount = (falseCountObj != null) ? falseCountObj : 0;
			int trueCount = (trueCountObj != null) ? trueCountObj : 0;

			// Calculate total and percentage
			int total = trueCount + falseCount;

			// Avoid division by zero
			int percentage = (total == 0) ? 0 : (trueCount * 100 / total);
			map.put("percentage", percentage);

			labData.add(map);

			System.out.println("labData ::" + labData);
		}

		// Add the lab data to the model
		model.addAttribute("labData", labData);

		// Return the view
		return mav;
	}

	@GetMapping("/View_UserWise_Vm_Listing")
	public ModelAndView View_UserWise_Vm_Listing(Model model, Principal principal) {
		// Get the username of the authenticated user
		Authentication auth = (Authentication) principal;
		String username = auth.getName();

		// Create the ModelAndView
		ModelAndView mav = new ModelAndView("View_All_Vm_Listing");

		// Fetch all labs for the authenticated user
		List<UserLab> labs = UserLabRepository.findByusername(username);

		// Add percentage for each lab
		List<Map<String, Object>> labData = new ArrayList<>();
		for (UserLab lab : labs) {
			Map<String, Object> map = new HashMap<>();
			map.put("lab", lab);

			// Fetch CloudInstance by instance name
			String instanceName = lab.getTemplateName();
			List<CloudInstance> cloudInstances = repository.findByInstanceName(instanceName);

			if (!cloudInstances.isEmpty()) {
				CloudInstance instance = cloudInstances.get(0); // ✅ first match

				String templateName = instance.getInstance_name();
				String PhysicalServerIP = instance.getPhysicalServerIP();
				String password = instance.getInstance_password();
				String os = instance.getSubproduct_id().getProduct_id().getProduct_name();

				// You can store them in the map if needed
				map.put("os", os);
				map.put("PhysicalServerIP", PhysicalServerIP);
			} else {
				System.out.println("No CloudInstance found for name: " + instanceName);
			}

			int ScenarioId = lab.getScenarioId();
			Optional<Add_Scenario> Scenariolist = ScenarioRepository.findById(ScenarioId);

			if (Scenariolist.isPresent()) {
				Add_Scenario Scenarioinstance = Scenariolist.get(); // Use get() on Optional to extract the value

				String ScenarioName = Scenarioinstance.getScenarioName();

				map.put("ScenarioName", ScenarioName);
			} else {
				System.out.println("No Add_Scenario found for id: " + ScenarioId);
			}

			// Handle LabId
			Long labIdLong = lab.getLabId();
			int labId = labIdLong.intValue();

			// Get true and false counts for completion
			Integer falseCountObj = instructionTemplateRepository.getfalseCompletionCountsByTemplateName(labId);
			Integer trueCountObj = instructionTemplateRepository.gettrueCompletionCountsByTemplateName(labId);

			// Handle null values
			int falseCount = (falseCountObj != null) ? falseCountObj : 0;
			int trueCount = (trueCountObj != null) ? trueCountObj : 0;

			// Calculate total and percentage
			int total = trueCount + falseCount;

			// Avoid division by zero
			int percentage = (total == 0) ? 0 : (trueCount * 100 / total);
			map.put("percentage", percentage);

			labData.add(map);

			System.out.println("labData ::" + labData);
		}

		// Add the lab data to the model
		model.addAttribute("labData", labData);

		// Return the view
		return mav;
	}

	@GetMapping("/getScenarioByUsername")
	@ResponseBody
	public JSONArray getScenarioByUsername(@RequestParam("username") String username) {
		JSONArray response = new JSONArray();

		try {
			List<UserLab> labs = UserLabRepository.findByusername(username);

			int srno = 0;
			for (UserLab lab : labs) {
				srno++;
				JSONArray row = new JSONArray();

				// Serial No
				row.put(srno);

				// Scenario
				String scenarioName = "";
				Integer scenarioId = lab.getScenarioId();
				if (scenarioId != null) {
					Optional<Add_Scenario> scenarioOpt = ScenarioRepository.findById(scenarioId);
					if (scenarioOpt.isPresent()) {
						scenarioName = scenarioOpt.get().getScenarioName();
					}
				}
				row.put(scenarioName);

				// CloudInstance details
				String instanceName = lab.getTemplateName();
				List<CloudInstance> cloudInstances = repository.findByInstanceName(instanceName);

				if (!cloudInstances.isEmpty()) {
					CloudInstance instance = cloudInstances.get(0);

					String os = instance.getSubproduct_id().getProduct_id().getProduct_name();
					String physicalServerIP = instance.getPhysicalServerIP();

					row.put(os);
					row.put(physicalServerIP);
					row.put(instance.getInstance_name());
				}

				// Get true/false completion counts
				Integer falseCountObj = instructionTemplateRepository
						.getFalseCompletionCountsByusernameandscenarioId(username, scenarioId);
				Integer trueCountObj = instructionTemplateRepository
						.getTrueCompletionCountsByusernameandscenarioId(username, scenarioId);

				int falseCount = (falseCountObj != null) ? falseCountObj : 0;
				int trueCount = (trueCountObj != null) ? trueCountObj : 0;

				int total = trueCount + falseCount;
				int percentage = (total == 0) ? 0 : (trueCount * 100 / total);

				row.put(percentage + "%");

				// Add row to response
				response.put(row);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Final response JSON :: " + response);
		return response;
	}

	@PostMapping("/update_last_session")
	@ResponseBody
	public ResponseEntity<String> updateLastSession(@RequestParam("id") Integer scenarioId, Principal principal) {
		try {
			String username = principal.getName();

			int updatedRows = UserLabRepository.updateLastActiveConnection(scenarioId, username);
			if (updatedRows > 0) {
				return ResponseEntity.ok("Last session updated");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lab not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error updating session: " + e.getMessage());
		}
	}

	@PostMapping("/getserverdetails")
	@ResponseBody
	public List<Map<String, Object>> getServerDetails(@RequestParam("type") String type,
			@RequestParam("status") String status, Principal principal) {
		List<Object[]> results;
		try {
			String username = principal.getName();

			if (type.equalsIgnoreCase("all")) {
				if (status.equalsIgnoreCase("operational")) {
					status = "up";
				} else {
					status = "down";
				}
				results = AddPhysicalServerRepository.findServerDetailsByAllStatus(status);
			}

			else if (status.equalsIgnoreCase("total")) {
				results = AddPhysicalServerRepository.findServerDetailsByTypeAndAllStatus(type);
			} else {
				if (status.equalsIgnoreCase("Up")) {
					status = "up";
				} else {
					status = "down";
				}

				results = AddPhysicalServerRepository.findServerDetailsByTypeAndStatus(type, status);
			}

			List<Map<String, Object>> serverList = new ArrayList<>();

			for (Object[] row : results) {
				Map<String, Object> server = new HashMap<>();
				server.put("serverName", row[0]);
				server.put("ip", row[1]);
				server.put("status", row[2]);
				serverList.add(server);
			}

			return serverList;

		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@PostMapping("/gethealthdetails")
	@ResponseBody
	public List<Map<String, Object>> getHealthDetails(@RequestParam String type, @RequestParam String resource) {
		List<Map<String, Object>> data = new ArrayList<>();
		List<Object[]> results = new ArrayList<>();

		// Call specific query based on resource
		if ("cpu".equalsIgnoreCase(resource)) {
			results = PhysicalServerHealthMonitoringRepository.findCpuHealthByVirtualizationType(type);
		} else if ("ram".equalsIgnoreCase(resource)) {
			results = PhysicalServerHealthMonitoringRepository.findRamHealthByVirtualizationType(type);
		} else if ("disk".equalsIgnoreCase(resource)) {
			results = PhysicalServerHealthMonitoringRepository.findDiskHealthByVirtualizationType(type);
		} else {
			return data; // Empty list if resource is invalid
		}

		for (Object[] row : results) {
			Map<String, Object> map = new HashMap<>();
			map.put("serverName", row[0]);
			map.put("ip", row[1]);

			double used = ((Number) row[2]).doubleValue();
			double total = ((Number) row[3]).doubleValue();
			double free = ((Number) row[4]).doubleValue();
			double usage = total > 0 ? (used / total) * 100 : 0;

			map.put("used", used);
			map.put("total", total);
			map.put("free", free);
			map.put("usagePercent", usage);

			data.add(map);
		}

		return data;
	}

	@PostMapping("/getuserdetails")
	@ResponseBody
	public List<Map<String, Object>> getUserDetails(@RequestParam("status") String status) {
		if (status.equalsIgnoreCase("online")) {
			status = "online";
		} else {
			status = "offline";
		}

		List<Object[]> users = AppUserRepository.findUserNameAndStatusByStatus(status);
		List<Map<String, Object>> result = new ArrayList<>();

		for (Object[] user : users) {
			Map<String, Object> map = new HashMap<>();
			map.put("username", user[0].toString());
			map.put("status", user[1].toString());
			result.add(map);
		}

		return result;
	}

//	SemesterWisePlaylist

	@GetMapping("/SemesterWisePlaylist")
	public String showForm(Model model) {
	    model.addAttribute("pageTitle", "Add User Wise Playlist");
	    
	    // All departments
	    List<DepartmentMaster> depts = DepartmentMasterRepository.findAll();
	    model.addAttribute("departments", depts);

	    // All playlists
	    List<Playlist> playlists = PlaylistRepository.findAll();
	    model.addAttribute("playlists", playlists);

	    List<SubPlaylist> subplaylists = SubPlaylistRepository.findAll();
	    model.addAttribute("subplaylists", subplaylists);

	    List<Add_Scenario> scenarios = ScenarioRepository.findAll();
	    model.addAttribute("scenarios", scenarios);
	    
	    return "SemesterWisePlaylist";
	}



	@PostMapping("/save_SubjectWisePlaylist")
	public String save_SubjectWisePlaylist(
	        @RequestParam(required = false) String groupId,
	        @RequestParam(value = "playlistIds", required = false) List<Integer> playlistIds,
	        @RequestParam(value = "subplaylistIds", required = false) List<Integer> subplaylistIds,
	        @RequestParam(value = "scenarioIds", required = false) List<Integer> scenarioIds,
	        @RequestParam(value = "subjectId", required = false) Integer subjectId
	) {

	    try {
	       

	      
	        if (subjectId != null) {
	            System.out.println("Saving for Subject ID: " + subjectId);

	            if (playlistIds != null) {
	                for (Integer playlistId : playlistIds) {
	                    SubjectPlaylistMapping sp = new SubjectPlaylistMapping();
	                    sp.setSubject(subjectId);
	                    sp.setPlaylistId(playlistId);
	                    SubjectPlaylistMappingRepository.save(sp);
	                }
	            }

	            if (subplaylistIds != null) {
	                for (Integer subplaylistId : subplaylistIds) {
	                    SubjectSubplaylistMapping ssp = new SubjectSubplaylistMapping();
	                    ssp.setSubject(subjectId);
	                    ssp.setSubPlaylistId(subplaylistId);
	                    SubjectSubplaylistMappingRepository.save(ssp);
	                }
	            }

	            if (scenarioIds != null) {
	                for (Integer scenarioId : scenarioIds) {
	                    SubjectScenarioMapping ssm = new SubjectScenarioMapping();
	                    ssm.setSubject(subjectId);
	                    ssm.setScenarioId(scenarioId);
	                    SubjectScenarioMappingRepository.save(ssm);
	                }
	            }
	        } else {
	            System.err.println("⚠ Subject ID is null. Skipping subject-wise saving.");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return "redirect:/guac/SemesterWisePlaylist";
	}
	
	
//	@GetMapping("/SubjectWisePlaylistView")
//	public String viewSubjectWisePlaylists(Model model) {
//	    model.addAttribute("pageTitle", "Subject Wise Playlists");
//	    
//	    List<Map<String, Object>> viewData = SubjectMasterRepository.findAllWithHierarchy().stream()
//	        .map(subject -> {
//	            Map<String, Object> row = new HashMap<>();
//	            row.put("subjectId", subject.getSubjectId());
//	            row.put("subjectName", subject.getSubjectName());
//	            
//	            // Get playlists
//	            List<Playlist> playlists = getPlaylistsForSubject(subject.getSubjectId());
//	            row.put("playlists", playlists);
//	            row.put("playlistNames", playlists.stream().map(Playlist::getPlaylistName).collect(Collectors.joining(", ")));
//	            row.put("playlistCount", playlists.size());
//	            
//	            // Get subplaylists
//	            List<SubPlaylist> subplaylists = getSubplaylistsForSubject(subject.getSubjectId());
//	            row.put("subplaylists", subplaylists);
//	            row.put("subplaylistNames", subplaylists.stream().map(SubPlaylist::getPlaylistName).collect(Collectors.joining(", ")));
//	            row.put("subplaylistCount", subplaylists.size());
//	            
//	            // Get scenarios
//	            List<Add_Scenario> scenarios = getScenariosForSubject(subject.getSubjectId());
//	            row.put("scenarios", scenarios);
//	            row.put("scenarioNames", scenarios.stream().map(Add_Scenario::getScenarioName).collect(Collectors.joining(", ")));
//	            row.put("scenarioCount", scenarios.size());
//	            
//	            row.put("totalCount", playlists.size() + subplaylists.size() + scenarios.size());
//	            
//	            return row;
//	        })
//	        .filter(row -> (Integer)row.get("totalCount") > 0)
//	        .collect(Collectors.toList());
//	    
//	    model.addAttribute("subjectWisePlaylists", viewData);
//	    return "SubjectWisePlaylistView";
//	}
//
//	
//	private List<Playlist> getPlaylistsForSubject(Integer subjectId) {
//	    return SubjectPlaylistMappingRepository.findBySubject(subjectId).stream()
//	        .map(mapping -> PlaylistRepository.findById(mapping.getPlaylistId()).orElse(null))
//	        .filter(Objects::nonNull)
//	        .collect(Collectors.toList());
//	}
//
//	private List<SubPlaylist> getSubplaylistsForSubject(Integer subjectId) {
//	    return SubjectSubplaylistMappingRepository.findBySubject(subjectId).stream()
//	        .map(mapping -> SubPlaylistRepository.findById(mapping.getSubPlaylistId()).orElse(null))
//	        .filter(Objects::nonNull)
//	        .collect(Collectors.toList());
//	}
//
//	private List<Add_Scenario> getScenariosForSubject(Integer subjectId) {
//	    return SubjectScenarioMappingRepository.findBySubject(subjectId).stream()
//	        .map(mapping -> ScenarioRepository.findById(mapping.getScenarioId()).orElse(null))
//	        .filter(Objects::nonNull)
//	        .collect(Collectors.toList());
//	}
	
	
//	@GetMapping("/viewsubjectTasks")
//	public String viewSubjectTasks(Model model, Principal principal) {
//	    Authentication auth = (Authentication) principal;
//	    String username = auth.getName();
//	    
//	    try {
//	        // Get playlists for the user
//	        List<Playlist> playlists = getPlaylistsForUser(username, auth);
//	        model.addAttribute("playlists", playlists);
//	        
//	        // Get sub-playlists for the user  
//	        List<SubPlaylist> subPlaylists = getSubPlaylistsForUser(username, auth);
//	        model.addAttribute("subPlaylists", subPlaylists);
//	        
//	        // Get scenarios for the user
//	        List<Add_Scenario> scenarios = getScenariosForUser(username, auth);
//	        model.addAttribute("scenarios", scenarios);
//	        
//	        model.addAttribute("pageTitle", "Subject Tasks");
//	        
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        model.addAttribute("error", "Error loading tasks");
//	    }
//	    
//	    return "viewsubjecttasks";
//	}
//
	// Helper methods to get data based on user role
//	private List<Playlist> getPlaylistsForUser(String username, Authentication auth) {
//	    boolean isSuperAdmin = auth.getAuthorities().stream()
//	            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));
//	    
//	    if (isSuperAdmin) {
//	        return PlaylistRepository.findAll();
//	    } else {
//	        List<Integer> userSubjectIds = getUserSubjectIds(username);
//	        if (userSubjectIds != null && !userSubjectIds.isEmpty()) {
//	            List<Integer> playlistIds = SubjectPlaylistMappingRepository.findPlaylistIdsBySubjectIds(userSubjectIds);
//	            if (playlistIds != null && !playlistIds.isEmpty()) {
//	                return PlaylistRepository.findAllById(playlistIds);
//	            }
//	        }
//	        return new ArrayList<>();
//	    }
//	}
//
//	private List<SubPlaylist> getSubPlaylistsForUser(String username, Authentication auth) {
//	    boolean isSuperAdmin = auth.getAuthorities().stream()
//	            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));
//	    
//	    if (isSuperAdmin) {
//	        return SubPlaylistRepository.findAll();
//	    } else {
//	        List<Integer> userSubjectIds = getUserSubjectIds(username);
//	        if (userSubjectIds != null && !userSubjectIds.isEmpty()) {
//	            List<Integer> subPlaylistIds = SubjectSubplaylistMappingRepository.findSubPlaylistIdsBySubjectIds(userSubjectIds);
//	            if (subPlaylistIds != null && !subPlaylistIds.isEmpty()) {
//	                return SubPlaylistRepository.findAllById(subPlaylistIds);
//	            }
//	        }
//	        return new ArrayList<>();
//	    }
//	}
//
//	private List<Add_Scenario> getScenariosForUser(String username, Authentication auth) {
//	    boolean isSuperAdmin = auth.getAuthorities().stream()
//	            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));
//	    
//	    if (isSuperAdmin) {
//	        return ScenarioRepository.findAll();
//	    } else {
//	        List<Integer> userSubjectIds = getUserSubjectIds(username);
//	        if (userSubjectIds != null && !userSubjectIds.isEmpty()) {
//	            List<Integer> scenarioIds = SubjectScenarioMappingRepository.findScenarioIdsBySubjectIds(userSubjectIds);
//	            if (scenarioIds != null && !scenarioIds.isEmpty()) {
//	                return ScenarioRepository.findAllById(scenarioIds);
//	            }
//	        }
//	        return new ArrayList<>();
//	    }
//	}
	
	
//	@GetMapping("/viewsubjectTasks/{subjectId}")
//	public String viewSubjectTasks(@PathVariable("subjectId") Integer subjectId, 
//	                              Model model, Principal principal) {
//	    Authentication auth = (Authentication) principal;
//	    String username = auth.getName();
//	    
//	    try {
//	        // Verify subject exists
//	        SubjectMaster subject = SubjectMasterRepository.findById(subjectId)
//	            .orElseThrow(() -> new RuntimeException("Subject not found with ID: " + subjectId));
//	        
//	        // Check if user has access to this subject
//	        if (!hasAccessToSubject(username, subjectId, auth)) {
//	            model.addAttribute("error", "You don't have access to this subject");
//	            return "viewsubjecttasks";
//	        }
//	        
//	        // Get playlists for this specific subject using mapping table
//	        List<Playlist> playlists = getPlaylistsForSubject(subjectId);
//	        model.addAttribute("playlists", playlists);
//	        
//	        // Get sub-playlists for this specific subject using mapping table
//	        List<SubPlaylist> subPlaylists = getSubPlaylistsForSubject(subjectId);
//	        model.addAttribute("subPlaylists", subPlaylists);
//	        
//	        // Get scenarios for this specific subject using mapping table
//	        List<Add_Scenario> scenarios = getScenariosForSubject(subjectId);
//	        model.addAttribute("scenarios", scenarios);
//	        
//	        // Add subject info to model
//	        model.addAttribute("subject", subject);
//	        model.addAttribute("subjectId", subjectId);
//	        model.addAttribute("pageTitle", subject.getSubjectName() + " - Tasks");
//	        
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        model.addAttribute("error", "Error loading tasks: " + e.getMessage());
//	    }
//	    
//	    return "viewsubjecttasks";
//	}
//
//	
//	private List<Playlist> getPlaylistsForSubject(Integer subjectId) {
//	    // Get playlist IDs from mapping table
//	    List<Integer> playlistIds = SubjectPlaylistMappingRepository.findSubject(subjectId);
//	    
//	    if (playlistIds != null && !playlistIds.isEmpty()) {
//	        return PlaylistRepository.findAllById(playlistIds);
//	    }
//	    return new ArrayList<>();
//	}
//
//	
//	private List<SubPlaylist> getSubPlaylistsForSubject(Integer subjectId) {
//	    // Get sub-playlist IDs from mapping table
//	    List<Integer> subPlaylistIds = SubjectSubplaylistMappingRepository.findSubject(subjectId);
//	   
//	    if (subPlaylistIds != null && !subPlaylistIds.isEmpty()) {
//	        return SubPlaylistRepository.findAllById(subPlaylistIds);
//	    }
//	    return new ArrayList<>();
//	}
//
//	
//	private List<Add_Scenario> getScenariosForSubject(Integer subjectId) {
//	    // Get scenario IDs from mapping table
//	    List<Integer> scenarioIds = SubjectScenarioMappingRepository.findSubject(subjectId);
//	    
//	    if (scenarioIds != null && !scenarioIds.isEmpty()) {
//	        return ScenarioRepository.findAllById(scenarioIds);
//	    }
//	    return new ArrayList<>();
//	}
//
//	// Check if user has access to the subject
//	private boolean hasAccessToSubject(String username, Integer subjectId, Authentication auth) {
//	    boolean isSuperAdmin = auth.getAuthorities().stream()
//	            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));
//	    
//	    if (isSuperAdmin) {
//	        return true;
//	    } else {
//	        List<Integer> userSubjectIds = getUserSubjectIds(username);
//	        return userSubjectIds != null && userSubjectIds.contains(subjectId);
//	    }
//	}
	
	@GetMapping("/viewsubjectTasks/{subjectId}")
	public String viewSubjectTasks(@PathVariable("subjectId") Integer subjectId, 
	                              Model model, Principal principal) {
	    Authentication auth = (Authentication) principal;
	    String username = auth.getName();
	    
	    try {
	        // Verify subject exists
	        SubjectMaster subject = SubjectMasterRepository.findById(subjectId)
	            .orElseThrow(() -> new RuntimeException("Subject not found with ID: " + subjectId));
	        
	        // Check if user has access to this subject
//	        if (!hasAccessToSubject(username, subjectId, auth)) {
//	            model.addAttribute("error", "You don't have access to this subject");
//	            return "viewsubjecttasks";
//	        }
	        
	        // Get playlists for this specific subject using mapping table
	        List<Playlist> playlists = getPlaylistsForSubject(subjectId);
	        model.addAttribute("playlists", playlists);
	        
	        // Get sub-playlists for this specific subject using mapping table
	        List<SubPlaylist> subPlaylists = getSubPlaylistsForSubject(subjectId);
	        model.addAttribute("subPlaylists", subPlaylists);
	        
	        // Get scenarios for this specific subject using mapping table
	        List<Add_Scenario> scenarios = getScenariosForSubject(subjectId);
	        model.addAttribute("scenarios", scenarios);
	        
	        // Debug: Print counts to console
	        System.out.println("Subject ID: " + subjectId);
	        System.out.println("Playlists found: " + (playlists != null ? playlists.size() : 0));
	        System.out.println("SubPlaylists found: " + (subPlaylists != null ? subPlaylists.size() : 0));
	        System.out.println("Scenarios found: " + (scenarios != null ? scenarios.size() : 0));
	        
	        // Add subject info to model
	        model.addAttribute("subject", subject);
	        model.addAttribute("subjectId", subjectId);
	        model.addAttribute("pageTitle", subject.getSubjectName() + " - Tasks");
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        model.addAttribute("error", "Error loading tasks: " + e.getMessage());
	    }
	    
	    return "viewsubjecttasks";
	}

//	private List<Playlist> getPlaylistsForSubject(Integer subjectId) {
//	    // Get playlist IDs from mapping table
//	    List<Integer> playlistIds = SubjectPlaylistMappingRepository.findSubject(subjectId);
//	    
//	    System.out.println("Playlist IDs for subject " + subjectId + ": " + playlistIds);
//	    
//	    if (playlistIds != null && !playlistIds.isEmpty()) {
//	        List<Playlist> playlists = PlaylistRepository.findAllById(playlistIds);
//	        System.out.println("Playlists retrieved: " + playlists.size());
//	        return playlists;
//	    }
//	    return new ArrayList<>();
//	}
//
//	private List<SubPlaylist> getSubPlaylistsForSubject(Integer subjectId) {
//	    // Get sub-playlist IDs from mapping table
//	    List<Integer> subPlaylistIds = SubjectSubplaylistMappingRepository.findSubject(subjectId);
//	    
//	    System.out.println("SubPlaylist IDs for subject " + subjectId + ": " + subPlaylistIds);
//	    
//	    if (subPlaylistIds != null && !subPlaylistIds.isEmpty()) {
//	        List<SubPlaylist> subPlaylists = SubPlaylistRepository.findAllById(subPlaylistIds);
//	        System.out.println("SubPlaylists retrieved: " + subPlaylists.size());
//	        return subPlaylists;
//	    }
//	    return new ArrayList<>();
//	}
//
//	private List<Add_Scenario> getScenariosForSubject(Integer subjectId) {
//	    // Get scenario IDs from mapping table
//	    List<Integer> scenarioIds = SubjectScenarioMappingRepository.findSubject(subjectId);
//	    
//	    System.out.println("Scenario IDs for subject " + subjectId + ": " + scenarioIds);
//	    
//	    if (scenarioIds != null && !scenarioIds.isEmpty()) {
//	        List<Add_Scenario> scenarios = ScenarioRepository.findAllById(scenarioIds);
//	        System.out.println("Scenarios retrieved: " + scenarios.size());
//	        return scenarios;
//	    }
//	    return new ArrayList<>();
//	}
	
	
	private List<Playlist> getPlaylistsForSubject(Integer subjectId) {
	    // Get playlist IDs from mapping table
	    List<Integer> playlistIds = SubjectPlaylistMappingRepository.findSubject(subjectId);
	    
	    System.out.println("Playlist IDs for subject " + subjectId + ": " + playlistIds);
	    
	    if (playlistIds != null && !playlistIds.isEmpty()) {
	        List<Playlist> playlists = PlaylistRepository.findAllById(playlistIds);
	        System.out.println("Playlists retrieved: " + playlists.size());
	        
	        // Debug each playlist
	        for (Playlist playlist : playlists) {
	            System.out.println("Playlist ID: " + playlist.getId() + ", Title: " + playlist.getPlaylistTitle());
	        }
	        
	        return playlists;
	    }
	    return new ArrayList<>();
	}

	private List<SubPlaylist> getSubPlaylistsForSubject(Integer subjectId) {
	    // Get sub-playlist IDs from mapping table
	    List<Integer> subPlaylistIds = SubjectSubplaylistMappingRepository.findSubject(subjectId);
	    
	    System.out.println("SubPlaylist IDs for subject " + subjectId + ": " + subPlaylistIds);
	    
	    if (subPlaylistIds != null && !subPlaylistIds.isEmpty()) {
	        List<SubPlaylist> subPlaylists = SubPlaylistRepository.findAllById(subPlaylistIds);
	        System.out.println("SubPlaylists retrieved: " + subPlaylists.size());
	        
	        // Debug each subplaylist
	        for (SubPlaylist subplaylist : subPlaylists) {
	            System.out.println("SubPlaylist ID: " + subplaylist.getId() + ", Title: " + subplaylist.getPlaylistTitle());
	        }
	        
	        return subPlaylists;
	    }
	    return new ArrayList<>();
	}

	private List<Add_Scenario> getScenariosForSubject(Integer subjectId) {
	    // Get scenario IDs from mapping table
	    List<Integer> scenarioIds = SubjectScenarioMappingRepository.findSubject(subjectId);
	    
	    System.out.println("Scenario IDs for subject " + subjectId + ": " + scenarioIds);
	    
	    if (scenarioIds != null && !scenarioIds.isEmpty()) {
	        List<Add_Scenario> scenarios = ScenarioRepository.findAllById(scenarioIds);
	        System.out.println("Scenarios retrieved: " + scenarios.size());
	        
	        // Debug each scenario
	        for (Add_Scenario scenario : scenarios) {
	            System.out.println("Scenario ID: " + scenario.getId() + ", Title: " + scenario.getScenarioTitle());
	        }
	        
	        return scenarios;
	    }
	    return new ArrayList<>();
	}
	
	
//	@GetMapping("/subjectView")
//	public String subjectView(Model model, Principal principal) {
//	    Authentication auth = (Authentication) principal;
//	    String username = auth.getName();
//	    try {
//	        // Get current user
//	        List<AppUser> users = AppUserRepository.findByUserName(username);
//	        if (users.isEmpty()) {
//	            model.addAttribute("error", "User not found");
//	            return "subjectView";
//	        }
//	        
//	        AppUser currentUser = users.get(0);
//	        
//	        // Get user's subjects based on semester (same logic as your dashboard)
//	        List<SubjectMaster> userSubjects = new ArrayList<>();
//	        if (currentUser.getSemesterName() != null) {
//	            Integer semesterId = currentUser.getSemesterName().getSemesterId();
//	            userSubjects = SubjectMasterRepository.findBysemester_SemesterId(semesterId);
//	            
//	            System.out.println("User: " + currentUser.getUserName() + 
//	                             ", Semester ID: " + semesterId + 
//	                             ", Subjects: " + userSubjects.size());
//	        }
//	        
//	        model.addAttribute("user", currentUser);
//	        model.addAttribute("userSubjects", userSubjects);
//	        model.addAttribute("pageTitle", "My Subjects");
//	        
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        model.addAttribute("error", "Error loading subjects: " + e.getMessage());
//	    }
//	    
//	    return "subjectView";
//	}
	
	
	@GetMapping("/subjectView/{userId}")
	public String subjectView(@PathVariable("userId") Long userId, Model model, Principal principal) {
	    try {
	        // Verify user exists
	        AppUser user = AppUserRepository.findByuserId(userId);
	        
	        // Get user's subjects based on semester
	        List<SubjectMaster> userSubjects = new ArrayList<>();
	        if (user.getSemesterName() != null) {
	            Integer semesterId = user.getSemesterName().getSemesterId();
	            userSubjects = SubjectMasterRepository.findBysemester_SemesterId(semesterId);
	            
	            System.out.println("User: " + user.getUserName() + 
	                             ", User ID: " + userId +
	                             ", Semester ID: " + semesterId + 
	                             ", Subjects: " + userSubjects.size());
	        }
	        
	        model.addAttribute("user", user);
	        model.addAttribute("userSubjects", userSubjects);
	        model.addAttribute("pageTitle", user.getName() + "'s Subjects");
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        model.addAttribute("error", "Error loading subjects: " + e.getMessage());
	    }
	    
	    return "subjectView";
	}
}
