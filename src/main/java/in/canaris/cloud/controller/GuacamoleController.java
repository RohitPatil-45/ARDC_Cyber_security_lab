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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.canaris.cloud.entity.AppUser;
import in.canaris.cloud.entity.CloudInstance;
import in.canaris.cloud.entity.Discount;
import in.canaris.cloud.entity.Group;
import in.canaris.cloud.entity.PlaylistScenario;
import in.canaris.cloud.entity.PlaylistScenarioId;
import in.canaris.cloud.openstack.entity.UserPlaylistMapping;
import in.canaris.cloud.openstack.entity.Add_Scenario;
import in.canaris.cloud.openstack.entity.ChartBoatInstructionTemplate;
import in.canaris.cloud.openstack.entity.CommandHistory;
import in.canaris.cloud.openstack.entity.Discover_Docker_Network;
import in.canaris.cloud.openstack.entity.InstructionCommand;
import in.canaris.cloud.openstack.entity.Playlist;
import in.canaris.cloud.openstack.entity.PlaylistItem;
import in.canaris.cloud.openstack.entity.ScenarioComments;
import in.canaris.cloud.openstack.entity.ScenarioLabTemplate;
import in.canaris.cloud.openstack.entity.SubPlaylist;
import in.canaris.cloud.openstack.entity.SubPlaylistScenario;
import in.canaris.cloud.openstack.entity.UserLab;
import in.canaris.cloud.openstack.entity.UserMappingsResponse;
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

import in.canaris.cloud.repository.SubPlaylistRepository;
import in.canaris.cloud.repository.UserLabRepository;
import in.canaris.cloud.service.DockerService;
import in.canaris.cloud.service.GuacamoleService;
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
	UserSubplaylistMappingRepository UserSubplaylistMappingRepository;

	@Autowired
	UserScenarioMappingRepository UserScenarioMappingRepository;

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
		List<Discover_Docker_Network> dockerNetworks = DiscoverDockerNetworkRepository.findAll(); // service method
		model.addAttribute("listObj", dockerNetworks);
		return "View_DockerListing"; // Thymeleaf page name
	}

//	@GetMapping("/View_Vm_Listing")
//	public ModelAndView viewVmListing(@RequestParam("Id") int scenarioId, Model model, Principal principal) {
//		System.out.println("Requested scenario Id = " + scenarioId);
//
//		Authentication auth = (Authentication) principal;
//		String username = auth.getName();
//
//		ModelAndView mav = new ModelAndView("View_Vm_Listing");
//
//		List<UserLab> labs = UserLabRepository.findByScenarioIdAndUsername(scenarioId, username);
//
//		// Add percentage for each lab
//		List<Map<String, Object>> labData = new ArrayList<>();
//		for (UserLab lab : labs) {
//			Map<String, Object> map = new HashMap<>();
//			map.put("lab", lab);
//
//			// Fetch true and false counts from repository
//			String insatnceName = lab.getTemplateName();
//			
//			
//			
//			List<CloudInstance> cloudobj = repository.findByInstanceName(insatnceName);
//			CloudInstance instance = cloudobj.get();
//
//			String templateName = instance.getInstance_name();
//			String password = instance.getInstance_password();
//			String os = instance.getSubproduct_id().getProduct_id().getProduct_name();
//			
//			Long labIdLong = lab.getLabId();
//			int labId = labIdLong.intValue();
//
//			Integer falseCountObj = instructionTemplateRepository.getfalseCompletionCountsByTemplateName(labId);
//			Integer trueCountObj = instructionTemplateRepository.gettrueCompletionCountsByTemplateName(labId);
//
//			// Handle null values
//			int falseCount = (falseCountObj != null) ? falseCountObj : 0;
//			int trueCount = (trueCountObj != null) ? trueCountObj : 0;
//
//			int total = trueCount + falseCount;
//
//			// Calculate percentage (avoid division by zero)
//			int percentage = (total == 0) ? 0 : (trueCount * 100 / total);
//			map.put("percentage", percentage);
//
//			labData.add(map);
//
//			System.out.println("labData ::" + labData);
//		}
//
//		model.addAttribute("labData", labData);
//		return mav;
//	}

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

	@GetMapping("/viewPlaylistConnection/{id}")
	public String viewPlaylistConnection(@PathVariable String id, Model model) {
		String identifier = GuacIdentifierUtil.encode(id, "mysql");
		String url = guacService.getEmbedUrl(identifier);
		model.addAttribute("embedUrl", url);

		List<CloudInstance> instances = null;
		instances = repository.findByGuacamoleId(id);

//		List<UserLab> labDetails = UserLabRepository.findByLabId(UserLabId);
//
//		if (!labDetails.isEmpty()) {
//			// Get the first item from the list.
//			UserLab userLab = labDetails.get(0);
//			;
//			// Get the guacamoleId from the retrieved object.
//			Integer guacamoleId = userLab.getGuacamoleId();
//			Integer scenarioId = userLab.getScenarioId();
//			String templateIdString = userLab.getTemplateName();
//			String LabName = userLab.getInstanceName();

//		List<UserWiseChatBoatInstructionTemplate> instances = null;
//		instances = instructionTemplateRepository.findByGuacamoleId(id);

		model.addAttribute("instructionsdata", instances);

//		List<InstructionCommand> instrucion = null;
//		instrucion = InstructionCommandRepository.findByLabId(id);
//		
//		model.addAttribute("instructionscommanddata", instrucion);
		return "viewConnection";
	}

//	@PostMapping("/loadInitialInstruction")
//	@ResponseBody
//	public Map<String, Object> loadInitialInstruction(@RequestParam String labId) {
//		Map<String, Object> response = new HashMap<>();
//		try {
//			InstructionCommand instructionCommand = InstructionCommandRepository.findNextUnexecutedByLabId(labId);
//
//			if (instructionCommand != null) {
//				response.put("success", true);
//				response.put("instruction", instructionCommand.getInstruction());
//				response.put("command", instructionCommand.getCommand());
//				response.put("isLast", false); // Set this properly if needed
//			} else {
//				response.put("success", false);
//				response.put("error", "No unexecuted instructions found.");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			response.put("success", false);
//			response.put("error", "Failed to load instruction.");
//		}
//
//		return response;
//	}

//	@PostMapping("/loadInitialInstruction")
//	@ResponseBody
//	public Map<String, Object> loadInitialInstruction(@RequestParam String labId) {
//		Map<String, Object> response = new HashMap<>();
//		try {
////			InstructionCommand instructionCommand = InstructionCommandRepository.findNextUnexecutedByLabId(labId);
//			UserWiseChatBoatInstructionTemplate instructionCommand = instructionTemplateRepository.findNextUnexecutedByLabId(labId);
////			
//			if (instructionCommand != null) {
//				String newCommand = instructionCommand.getInstructionCommand();
//
//				// ✅ Compare with the last stored command
//				String lastCommand = lastCommandMap.get(labId + "~" + newCommand);
//
//				if (newCommand != null && newCommand.equals(lastCommand)) {
////					response.put("success", false);
////					response.put("error", "Duplicate command. Nothing new to return.");
////					return response;
//				} else {
//
//					response.put("success", true);
//					response.put("instruction", instructionCommand.getInstructionDetails());
//					response.put("command", newCommand);
//					response.put("isLast", false);
//					lastCommandMap.put(labId + "~" + newCommand, newCommand);
//				}
//
//			} else {
//				response.put("success", false);
//				response.put("error", "No unexecuted instructions found.");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			response.put("success", false);
//			response.put("error", "Failed to load instruction.");
//		}
//
//		return response;
//	}

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

	private final ConcurrentHashMap<String, String> lastCommandMap = new ConcurrentHashMap<>();

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
//				String templateName = userLab.getTemplateName();
//				Long laabId = userLab.getLabId();
				String LabName = userLab.getInstanceName();

				Long labIdLong = userLab.getLabId();
				int laabId = labIdLong.intValue();
				System.out.println("laabId ::" + laabId);
				// Fetch next instruction
				UserWiseChatBoatInstructionTemplate instructionCommand = instructionTemplateRepository
						.findNextUnexecutedByLabId(laabId);

				if (instructionCommand != null) {
					System.out.println("LabName_if ::" + LabName);
					String newCommand = instructionCommand.getInstructionCommand();
					System.out.println("newCommand ::" + newCommand);

//					String lastCommand = lastCommandMap.get(labId + "~last");
//					System.out.println("lastCommand ::" + lastCommand);
					System.out.println("getInstructionDetails ::" + instructionCommand.getInstructionDetails());

					byte[] htmlBytes = instructionCommand.getInstructionDetails();
					String decodedHtml = new String(htmlBytes, StandardCharsets.UTF_8);
					System.out.println("decodedHtml ::" + decodedHtml);

//					if (newCommand != null && newCommand.equals(lastCommand)) {
					// If duplicate, return but mark it as duplicate
					response.put("success", false);
					response.put("error", "Duplicate command skipped.");
//					} else {
					// Normal flow
					response.put("success", true);
					response.put("instructionText", decodedHtml); // ✅ decoded HTML
					response.put("command", newCommand);
					response.put("isLast", false); // you can adjust logic here

					// Save last executed command
					lastCommandMap.put(labId + "~last", newCommand);
//					}

				} else {
					response.put("success", false);
					response.put("error", "No unexecuted instructions found.");
				}

			} else {
				response.put("success", false);
				response.put("error", "Lab not found.");
			}

			System.out.println("chatLoadresponse :::" + response);

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
//				Long laabId = userLab.getLabId();
				String LabName = userLab.getInstanceName();

				Long labIdLong = userLab.getLabId();
				int laabId = labIdLong.intValue();
				System.out.println("LabId ::" + laabId);

				List<CommandHistory> commandHistoryList = CommandHistoryRepository.findByContainerName(LabName,
						labcommand);

				boolean commandMatched = false;
				for (CommandHistory ch : commandHistoryList) {
					if (ch.getCommand().equalsIgnoreCase(labcommand)) {
						commandMatched = true;
						break;
					}
				}

				if (commandMatched) {
					// Case 1: Command found in history
					System.out.println("Inside: command matched in history");

					int updatedRows = instructionTemplateRepository.modifyCommandByLabId(laabId, labcommand);

					if (updatedRows > 0) {

						UserWiseChatBoatInstructionTemplate instructionCommand = instructionTemplateRepository
								.findNextUnexecutedByLabId(laabId);
						System.out.println("Lab Updatedd.");
						if (instructionCommand != null) {
							byte[] htmlBytes = instructionCommand.getInstructionDetails();
							String decodedHtml = new String(htmlBytes, StandardCharsets.UTF_8);

							response.put("success", true);
							response.put("instruction", decodedHtml);
							response.put("command", instructionCommand.getInstructionCommand());
							response.put("isLast", false);
							System.out.println("Lab Next Command.");
						} else {
							// No more instructions → Lab completed
							System.out.println("Lab not completed.");
							response.put("success", true);
							response.put("instruction", "Lab is completed.");
							response.put("command", "");
							response.put("isLast", true);
						}
					} else {
						System.out.println("Execute this command first.");
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

			} else {
				System.out.println("Lab not found.");
				response.put("success", false);
				response.put("error", "Lab not found.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to load process Lab not found.");
			response.put("success", false);
			response.put("error", "Failed to process command.");
		}
		System.out.println("response Lab " + response);
		return response;
	}

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

				response.put("success", true);
				response.put("username", username);
				response.put("labName", userLab.getTemplateName());
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

	@GetMapping("/Add_Playlist")
	public ModelAndView showAddPlaylistForm() {
		ModelAndView mav = new ModelAndView("Add_Playlist");
//		pageTitle
		mav.addObject("pageTitle", "Create New Playlist");

		mav.addObject("playlist", new Playlist()); // This is crucial!
		return mav;
	}

	@GetMapping("/Add_Sub_Playlist")
	public ModelAndView showAdd_Sub_PlaylistForm() {
		ModelAndView mav = new ModelAndView("Add_Sub_Playlist");
//		pageTitle
		mav.addObject("pageTitle", "Create New Sub Playlist");

		mav.addObject("playlist", new Playlist());
		return mav;
	}

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

	@PostMapping("/playlistsave")
	public String savePlaylistData(Playlist obj, RedirectAttributes redirectAttributes,
			@RequestParam(required = false) MultipartFile cover_image, Principal principal) {

		try {
			// Set createdBy
			if (principal != null) {
				obj.setCreatedBy(principal.getName());
			}

			boolean isNew = (obj.getId() == 0);

			if (isNew) {
				// New playlist - handle image
				if (cover_image != null && !cover_image.isEmpty()) {
					String contentType = cover_image.getContentType();
					if (contentType != null && contentType.startsWith("image/")) {
						obj.setCoverImage(cover_image.getBytes());
						System.out.println("Uploaded image saved to database");
					} else {
						redirectAttributes.addFlashAttribute("message",
								"Invalid file type. Please upload an image file.");
						redirectAttributes.addFlashAttribute("status", "error");
					}
				} else {
					// No image uploaded - load default
					String defaultImagePath = "C:\\Users\\vijay\\Desktop\\18825\\New folder\\default.jpg";
					try {
						byte[] defaultImageBytes = Files.readAllBytes(Paths.get(defaultImagePath));
						obj.setCoverImage(defaultImageBytes);
						System.out.println("Default image loaded and saved to database");
					} catch (IOException e) {
						System.err.println("Error loading default image: " + e.getMessage());
						obj.setCoverImage(createPlaceholderImage());
						System.out.println("Placeholder image created and saved to database");
					}
				}
			} else {
				// Editing existing playlist
				Optional<Playlist> existing = PlaylistRepository.findById(obj.getId());
				if (existing.isPresent()) {
					Playlist existingPlaylist = existing.get();

					// Retain existing image if new one isn't uploaded
					if (cover_image == null || cover_image.isEmpty()) {
						obj.setCoverImage(existingPlaylist.getCoverImage());
					} else {
						String contentType = cover_image.getContentType();
						if (contentType != null && contentType.startsWith("image/")) {
							obj.setCoverImage(cover_image.getBytes());
							System.out.println("Updated image saved to database");
						} else {
							redirectAttributes.addFlashAttribute("message",
									"Invalid file type. Please upload an image file.");
							redirectAttributes.addFlashAttribute("status", "error");
						}
					}
				}
			}

			// Save to DB
			Playlist saved = PlaylistRepository.save(obj);
			redirectAttributes.addFlashAttribute("message", "The playlist has been saved successfully!");
			redirectAttributes.addFlashAttribute("status", "success");

			// Redirect based on new/edit
			if (isNew) {
				return "redirect:/guac/Add_Playlist";
			} else {
				return "redirect:/guac/View_Particular_Playlist?Id=" + saved.getId();
			}

		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "Error while saving playlist: " + e.getMessage());
			redirectAttributes.addFlashAttribute("status", "error");
			return "redirect:/guac/Add_Playlist";
		}
	}

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
				// SuperAdmin → show all playlists
				dataList = PlaylistRepository.findAll();
			} else {
				// Normal User → get only playlists assigned to them
				List<Integer> playlistIds = UserPlaylistMappingRepository.findPlaylistIdsByUserName(userName);

				if (playlistIds != null && !playlistIds.isEmpty()) {
					dataList = PlaylistRepository.findAllById(playlistIds);
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
				// Normal User → only subplaylists assigned to them
				List<Integer> subPlaylistIds = UserSubplaylistMappingRepository.findSubPlaylistIdsByUserName(userName);

				if (subPlaylistIds != null && !subPlaylistIds.isEmpty()) {
					dataList = SubPlaylistRepository.findAllById(subPlaylistIds);
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

	@GetMapping("/Scenario_Details")
	public ModelAndView showScenario_Details(RedirectAttributes redirectAttributes) {
		ModelAndView mav = new ModelAndView("Add_Scenario");
		List<CloudInstance> instances = null;
		instances = repository.getInstanceNameNotAssigned();
		mav.addObject("pageTitle", "Create New Scenario");
		mav.addObject("instanceNameList", instances);
		mav.addObject("scenario", new Add_Scenario());

		return mav;
	}

	@GetMapping("/editsceneriolist/{id}")
	public ModelAndView editsceneriolist(@PathVariable("id") Integer id) {

		try {
			List<CloudInstance> instances = null;
			instances = repository.getInstanceNameNotAssigned();

			ModelAndView mav = new ModelAndView("Add_Scenario");
//			mav.addObject("action_name", var_function_name);
			mav.addObject("scenario", ScenarioRepository.findById(id).get());
			mav.addObject("instanceNameList", instances);
			mav.addObject("pageTitle", "Edit Scenario (ID: " + id + ")");
//			mav.addObject("pageTitle",  "Edit " + disp_function_name + " (ID: " + id + ")");
			return mav;
		} catch (Exception e) {
			ModelAndView mav = new ModelAndView("Add_Scenario");
//			mav.addObject("action_name", var_function_name);
			mav.addObject("message", e.getMessage());
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
			String[] labsArray = labsString.split(",");
			List<String> labIds = new ArrayList<>();
			List<String> labNames = new ArrayList<>();

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
			scenarioObj.setLabs(String.join(",", labNames));
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

			// Save lab templates into ScenarioLabTemplate table
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
				// Normal User → only mapped scenarios
				List<Integer> scenarioIds = UserScenarioMappingRepository.findScenarioIdsByUserName(userName);

				if (scenarioIds != null && !scenarioIds.isEmpty()) {
					dataList = ScenarioRepository.findAllById(scenarioIds);
				}
			}

			// Convert to JSON
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

//	@GetMapping("/My_Scenario")
//	public ModelAndView getMy_View_Scenario(Principal principal) {
//		ModelAndView mav = new ModelAndView("My_View_Scenario");
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
//				dataList = ScenarioRepository.findByUserScenario();
//			} else {
//
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
//				dataList = ScenarioRepository.findByUserScenario();
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

	@GetMapping("/My_Scenario")
	public ModelAndView getMy_View_Scenario(Principal principal) {
		ModelAndView mav = new ModelAndView("My_View_Scenario");
		JSONArray Finalarray = new JSONArray();
		List<Add_Scenario> dataList = new ArrayList<>();

		try {
			Authentication authentication = (Authentication) principal;
			User loginedUser = (User) authentication.getPrincipal();

			String userName = loginedUser.getUsername();
			
			List<UserScenario> scenarioIds = UserScenerioRepository.findByUsername(userName);
			
			for (UserScenario objs : scenarioIds) {
				Add_Scenario temp = ScenarioRepository.findById(Integer.parseInt(objs.getScenarioId())).get();
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
				

			System.out.println("Finalarray_getMy_View_Scenario ::" + Finalarray);
			mav.addObject("listObj", Finalarray.toString());

		}catch(

	Exception e)
	{
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
	public String showLabButtonOn_Check(@RequestParam("scenarioId") String scenarioId,
			@RequestParam("scenarioName") String scenarioName, Principal principal) {
		try {
//			System.out.println("Checking scenarioId: " + scenarioId); usernmae

			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			String username = loginedUser.getUsername();

			Optional<UserScenario> scenarioOpt = UserScenerioRepository.findByScenarioId(scenarioId, username);

			if (scenarioOpt.isPresent()) {
//				System.out.println("Scenario found.");
				return "available";
			} else {
//				System.out.println("Scenario NOT found.");
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

				if (scenarioId != null) {
					UserScenerioRepository.deleteByScenarioIdAndUsername(getUsername, scenarioIdStr);
				}
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
			instructionTemplateRepository.UpdateresetByLabName(containerName);
			UserLabRepository.UpdateresetByLabName(containerName);

			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception Inside_reset_lab : " + e);
			return "fail";
		}
	}

	@PostMapping("/getPercenetageParticularScenerio")
	@ResponseBody
	public String getPercentageParticularScenario(Principal principal) {
		try {

			Authentication auth = (Authentication) principal;
			String username = auth.getName();

			Integer falseCountObj = instructionTemplateRepository.getFalseCompletionCountsByTemplateId(username);
			Integer trueCountObj = instructionTemplateRepository.getTrueCompletionCountsByTemplateId(username);

			int falseCount = (falseCountObj != null) ? falseCountObj : 0;
			int trueCount = (trueCountObj != null) ? trueCountObj : 0;

			System.out.println("True count: " + trueCount + ", False count: " + falseCount);

			int total = trueCount + falseCount;

			if (total == 0) {
				return "0";
			}

			int percentage = (trueCount * 100) / total;
			System.out.println("Percentage: " + percentage + "%");

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

	@PostMapping("/subplaylistsave")
	public String subplaylistsaveData(SubPlaylist obj, RedirectAttributes redirectAttributes,
			@RequestParam(required = false) MultipartFile cover_image, Principal principal) {

		try {
			// Set createdBy
			if (principal != null) {
				obj.setCreatedBy(principal.getName());
			}

			boolean isNew = (obj.getId() == 0);

			if (isNew) {
				// New playlist - handle image
				if (cover_image != null && !cover_image.isEmpty()) {
					String contentType = cover_image.getContentType();
					if (contentType != null && contentType.startsWith("image/")) {
						obj.setCoverImage(cover_image.getBytes());
						System.out.println("Uploaded image saved to database");
					} else {
						redirectAttributes.addFlashAttribute("message",
								"Invalid file type. Please upload an image file.");
						redirectAttributes.addFlashAttribute("status", "error");
					}
				} else {
					// No image uploaded - load default
					String defaultImagePath = "C:\\Users\\vijay\\Desktop\\18825\\New folder\\default.jpg";
					try {
						byte[] defaultImageBytes = Files.readAllBytes(Paths.get(defaultImagePath));
						obj.setCoverImage(defaultImageBytes);
						System.out.println("Default image loaded and saved to database");
					} catch (IOException e) {
						System.err.println("Error loading default image: " + e.getMessage());
						obj.setCoverImage(createPlaceholderImage());
						System.out.println("Placeholder image created and saved to database");
					}
				}
			} else {
				// Editing existing playlist
				Optional<SubPlaylist> existing = SubPlaylistRepository.findById(obj.getId());
				if (existing.isPresent()) {
					SubPlaylist existingPlaylist = existing.get();

					// Retain existing image if new one isn't uploaded
					if (cover_image == null || cover_image.isEmpty()) {
						obj.setCoverImage(existingPlaylist.getCoverImage());
					} else {
						String contentType = cover_image.getContentType();
						if (contentType != null && contentType.startsWith("image/")) {
							obj.setCoverImage(cover_image.getBytes());
							System.out.println("Updated image saved to database");
						} else {
							redirectAttributes.addFlashAttribute("message",
									"Invalid file type. Please upload an image file.");
							redirectAttributes.addFlashAttribute("status", "error");
						}
					}
				}
			}

			// Save to DB
			SubPlaylistRepository.save(obj);

			redirectAttributes.addFlashAttribute("message", "The playlist has been saved successfully!");
			redirectAttributes.addFlashAttribute("status", "success");

//	        return "redirect:/guac/View_Particular_Playlist?Id=" + obj.getId();
			return "redirect:/guac/Add_Sub_Playlist";

		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "Error while saving playlist: " + e.getMessage());
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

//	@PostMapping("/save_UserWisePlaylist")
//	public String saveUserWisePlaylist(@ModelAttribute UserWisePlaylistForm form) {
//
//		try {
//
//			// Get username from AppUser table
//			String userName = AppUserRepository.getUserNameById(form.getUserId());
//
//			// Save or update for each playlist
//			for (Integer playlistId : form.getPlaylistIds()) {
//				UserPlaylistMappingRepository.upsertUserPlaylist(userName, playlistId);
//			}
//			
////			user_subplaylist_mapping
//			UserSubplaylistMappingRepository.insert
//			
////			user_scenario_mapping
//			
//			UserScenarioMappingRepository.insert
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return "redirect:/guac/Add_UserWisePlaylist";
//	}

	@PostMapping("/save_UserWisePlaylist")
	public String saveUserWisePlaylist(@RequestParam(required = false) String groupId,
			@RequestParam("userIds") List<Long> userIds, @RequestParam("playlistIds") List<Integer> playlistIds,
			@RequestParam("subplaylistIds") List<Integer> subplaylistIds,
			@RequestParam("scenarioIds") List<Integer> scenarioIds) {
		try {
			// Loop over all selected users
			for (Long userId : userIds) {
				String userName = AppUserRepository.getUserNameById(userId);
				System.out.println("Processing userId: " + userId + " -> userName: " + userName);

				if (userName == null) {
					throw new RuntimeException("No username found for userId=" + userId);
				}

//
//				SubPlaylistScenarioRepository.save(joinEntity);
				// Save playlists
				for (Integer playlistId : playlistIds) {

					UserPlaylistMapping UserPlaylistEntity = new UserPlaylistMapping();
					UserPlaylistEntity.setPlaylistId(playlistId);
					UserPlaylistEntity.setUserName(userName);

					UserPlaylistMappingRepository.save(UserPlaylistEntity);
				}

				// Save subplaylists
				for (Integer subplaylistId : subplaylistIds) {

					UserSubplaylistMapping UserSubplaylistEntity = new UserSubplaylistMapping();
					UserSubplaylistEntity.setSubPlaylistId(subplaylistId);
					UserSubplaylistEntity.setUserName(userName);

					UserSubplaylistMappingRepository.save(UserSubplaylistEntity);
				}

				// Save scenarios
				for (Integer scenarioId : scenarioIds) {

					UserScenarioMapping UserScenarioEntity = new UserScenarioMapping();
					UserScenarioEntity.setScenarioId(scenarioId);
					UserScenarioEntity.setUserName(userName);

					UserScenarioMappingRepository.save(UserScenarioEntity);
				}
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

}
