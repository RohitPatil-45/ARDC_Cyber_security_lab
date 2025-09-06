package in.canaris.cloud.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

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

import in.canaris.cloud.entity.AppUser;
import in.canaris.cloud.entity.CloudInstance;
import in.canaris.cloud.entity.Discount;
import in.canaris.cloud.entity.PlaylistScenario;
import in.canaris.cloud.entity.PlaylistScenarioId;
import in.canaris.cloud.openstack.entity.Add_Scenario;
import in.canaris.cloud.openstack.entity.ChartBoatInstructionTemplate;
import in.canaris.cloud.openstack.entity.CommandHistory;
import in.canaris.cloud.openstack.entity.Discover_Docker_Network;
import in.canaris.cloud.openstack.entity.InstructionCommand;
import in.canaris.cloud.openstack.entity.Playlist;
import in.canaris.cloud.openstack.entity.ScenarioComments;
import in.canaris.cloud.openstack.entity.UserLab;
import in.canaris.cloud.openstack.entity.UserScenario;
import in.canaris.cloud.openstack.entity.UserWiseChatBoatInstructionTemplate;
import in.canaris.cloud.repository.ScenarioRepository;
import in.canaris.cloud.repository.UserRepository;
import in.canaris.cloud.repository.UserScenerioRepository;
import in.canaris.cloud.repository.UserWiseChatBoatInstructionTemplateRepository;
import in.canaris.cloud.repository.ChartBoatInstructionTemplateRepository;
import in.canaris.cloud.repository.CloudInstanceRepository;
import in.canaris.cloud.repository.PlaylistRepository;
import in.canaris.cloud.repository.PlaylistsSenarioRepository;
import in.canaris.cloud.repository.DiscoverDockerNetworkRepository;
import in.canaris.cloud.repository.CommandHistoryRepository;
import in.canaris.cloud.repository.ScenarioCommentsRepository;
import in.canaris.cloud.repository.UserLabRepository;
import in.canaris.cloud.service.DockerService;
import in.canaris.cloud.service.GuacamoleService;

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

	@GetMapping("/View_DockerListing")
	public String viewDockerListing(Model model) {
		List<Discover_Docker_Network> dockerNetworks = DiscoverDockerNetworkRepository.findAll(); // service method
		model.addAttribute("listObj", dockerNetworks);
		return "View_DockerListing"; // Thymeleaf page name
	}

	@GetMapping("/View_Vm_Listing")
	public ModelAndView viewVmListing(@RequestParam("Id") int scenarioId, Model model) {
		System.out.println("Requested scenario Id = " + scenarioId);
		ModelAndView mav = new ModelAndView("View_Vm_Listing");

		List<UserLab> labs = UserLabRepository.findByscenarioId(scenarioId);

		// Add percentage for each lab
		List<Map<String, Object>> labData = new ArrayList<>();
		for (UserLab lab : labs) {
			Map<String, Object> map = new HashMap<>();
			map.put("lab", lab);

			// Fetch true and false counts from repository

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
		String url = guacService.getEmbedUrl(id);
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

	@GetMapping("/View_Particular_Playlist")
	public ModelAndView getView_Particular_Playlist(@RequestParam String Id) {

		ModelAndView mav = new ModelAndView("View_Particular_Playlist");
		JSONArray Finalarray = new JSONArray();
		List<Playlist> dataList;

		Optional<Add_Scenario> ScenariodataList;

		try {

			int SRNO = Integer.parseInt(Id);

			dataList = PlaylistRepository.getView_Particular_Scenerio(SRNO);

			List<Add_Scenario> scenarioDataList = PlaylistsSenarioRepository.getScenariosByPlaylist(SRNO);

			for (Add_Scenario temp : scenarioDataList) {
				System.out.println("Scenario ID: " + temp.getId() + ", Name: " + temp.getScenarioName());

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
				String NumberofInstance = temp.getNumberofInstance() != null ? temp.getNumberofInstance() : "";
//				String Cover_Image = temp.getCover_Image() != null ? temp.getCover_Image() : "";
				String Cover_Image = "";
				int SrNo = temp.getId();

//				srno++;

				obj.put("Scenario_Name", Scenario_Name);
				obj.put("Scenario_Title", Scenario_Title);
//				obj.put("Description", Description);
				obj.put("Category", Category);
				obj.put("Scenario_Type", Scenario_Type);
				obj.put("Mode", Mode);
				obj.put("Difficulty_Level", Difficulty_Level);
				obj.put("Duration", Duration);
				obj.put("NumberofInstance", NumberofInstance);
				obj.put("Cover_Image", Cover_Image);
				obj.put("Id", SrNo);

				Finalarray.put(obj);
			}

			System.out.println("Fetched Datawqwqw: " + dataList.toString()); // Print to console
			int srno = 0;
			for (Playlist temp : dataList) {
				JSONObject obj = new JSONObject();

				String PlaylistTitle = temp.getPlaylistTitle() != null ? temp.getPlaylistTitle() : "";
				String PlaylistName = temp.getPlaylistName() != null ? temp.getPlaylistName() : "";
				String Description = temp.getDescription() != null ? temp.getDescription() : "";
				String Tag = temp.getTag() != null ? temp.getTag() : "";

//				String Cover_Image = temp.getCover_Image() != null ? temp.getCover_Image() : "";
				String Cover_Image = "";
				int SrNo = temp.getId();

//				srno++;

				obj.put("PlaylistTitle", PlaylistTitle);
				obj.put("PlaylistName", PlaylistName);
				obj.put("Description", Description);
				obj.put("Tag", Tag);

				obj.put("Cover_Image", Cover_Image);
				obj.put("Id", SrNo);

				Finalarray.put(obj);
			}

//			System.out.println("Finalarray_getView_Scenario ::" + Finalarray);

			mav.addObject("listObj", Finalarray.toString());
			mav.addObject("scenarioList", ScenarioRepository.findAll());

		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
			System.out.println("Error fetching data: " + e.getMessage());
		}
		return mav;
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

	@GetMapping("/View_Playlist")
	public ModelAndView getView_Playlist(Principal principal) {
		ModelAndView mav = new ModelAndView("View_Playlist");
		JSONArray Finalarray = new JSONArray();
		List<Playlist> dataList;
		try {
			// start

			Authentication authentication = (Authentication) principal;
			User loginedUser = (User) authentication.getPrincipal();

			String userName = loginedUser.getUsername();
			String groupName = "";
			StringBuilder vmNamesBuilder = new StringBuilder();

			List<AppUser> userList = userRepository.findByuserName(userName);

			boolean isSuperAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));

			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

			if (isSuperAdmin) {
				dataList = PlaylistRepository.findAll();
			} else {
				// Get group names
				for (AppUser appUser : userList) {
					groupName = appUser.getGroupName(); // Assuming only one AppUser per username
				}

				List<String> groups = new ArrayList<>();
				StringTokenizer groupTokenizer = new StringTokenizer(groupName, ",");
				while (groupTokenizer.hasMoreTokens()) {
					groups.add(groupTokenizer.nextToken());
				}

				// Get VM names by group
				List<Object[]> vmList = repository.getInstanceNameByGroup(groups, true);
				for (Object[] vmEntry : vmList) {
					vmNamesBuilder.append(vmEntry[1].toString()).append(",");
				}

				// Split VM names string into a list
				List<String> vmGroups = new ArrayList<>();
				String vmNames = vmNamesBuilder.toString();
				if (!vmNames.isEmpty()) {
					StringTokenizer vmTokenizer = new StringTokenizer(vmNames, ",");
					while (vmTokenizer.hasMoreTokens()) {
						vmGroups.add(vmTokenizer.nextToken());
					}
				}
//				dataList = ScenarioRepository.getView_Scenario(vmGroups);
				dataList = PlaylistRepository.findAll();
			}

//			 dataList = kVMDriveDetailsRepository.findBykVMDetails();
			System.out.println("Fetched Data: " + dataList.toString()); // Print to console
			int srno = 0;
			for (Playlist temp : dataList) {
				JSONObject obj = new JSONObject();

				String PlaylistTitle = temp.getPlaylistTitle() != null ? temp.getPlaylistTitle() : "";
				String PlaylistName = temp.getPlaylistName() != null ? temp.getPlaylistName() : "";
				String Description = temp.getDescription() != null ? temp.getDescription() : "";
				String Tag = temp.getTag() != null ? temp.getTag() : "";

//				String Cover_Image = temp.getCover_Image() != null ? temp.getCover_Image() : "";
				String Cover_Image = "";
				int SrNo = temp.getId();

//				srno++;

				obj.put("PlaylistTitle", PlaylistTitle);
				obj.put("PlaylistName", PlaylistName);
				obj.put("Description", Description);
				obj.put("Tag", Tag);

				obj.put("Cover_Image", Cover_Image);
				obj.put("Id", SrNo);

				Finalarray.put(obj);
			}

			System.out.println("Finalarray_getView_Playlist ::" + Finalarray);

			mav.addObject("listObj", Finalarray.toString());

		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
			System.out.println("Error fetching data: " + e.getMessage());
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

		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
			System.out.println("Error fetching data: " + e.getMessage());
		}
		return mav;
	}

//	@GetMapping("/Scenario_Details")
//	public ModelAndView Scenario_Details(Principal principal) {
//		Authentication authentication = (Authentication) principal;
//		User loginedUser = (User) ((Authentication) principal).getPrincipal();
//		List<CloudInstance> instances = null;
//		String userName = loginedUser.getUsername();
//		String groupName = "";
//		List<AppUser> user1 = userRepository.findByuserName(loginedUser.getUsername());
//		boolean isSuperAdmin = authentication.getAuthorities().stream()
//				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));
//
//		boolean isAdmin = authentication.getAuthorities().stream()
//				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
//		ModelAndView mav = new ModelAndView("Add_Scenario");
//		mav.addObject("pageTitle", "Report");
//		mav.addObject("scenario", new Add_Scenario()); // Add this line
//
//		if (isSuperAdmin) {
//			instances = repository.getInstanceNameNotAssigned();
//			mav.addObject("instanceNameList", instances);
//		} else {
//			for (AppUser appUser : user1) {
//				groupName = appUser.getGroupName();
//			}
//			List<String> groups = new ArrayList<>();
//			StringTokenizer token = new StringTokenizer(groupName, ",");
//			while (token.hasMoreTokens()) {
//				groups.add(token.nextToken());
//			}
//
//			instances = repository.getInstanceNameNotAssigned();
//			mav.addObject("instanceNameList", instances);
//		}
//
//		return mav;
//	}

//	@PostMapping("/saveScenarioData")
//	public String saveScenarioData(@ModelAttribute("scenario") Add_Scenario scenarioObj,
//			RedirectAttributes redirectAttributes, @RequestParam(required = false) MultipartFile cover_image,
//			@RequestParam("Labs") String[] labsArray, // Changed parameter name
//			Principal principal) {
//
//		ModelAndView mav = new ModelAndView("redirect:/Scenario_Details");
//
//		try {
//			// Set created by if needed
//			if (principal != null) {
//				// If you have a createdBy field in your entity
//				// scenarioObj.setCreatedBy(principal.getName());
//			}
//
//			// Process multiple selected labs
//			List<String> labIds = new ArrayList<>();
//			List<String> labNames = new ArrayList<>();
//
//			for (String lab : labsArray) {
//				String[] parts = lab.split("~");
//				if (parts.length == 2) {
//					labIds.add(parts[0]);
//					labNames.add(parts[1]);
//					System.out.println("Selected lab: ID=" + parts[0] + ", Name=" + parts[1]);
//
//					// Update instance assigned count for each lab
//					try {
//						repository.updateInstanceNameAssigned(Integer.valueOf(parts[0]));
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//
//			// Save all labs as comma-separated values
//			scenarioObj.setLabs(String.join(",", labNames));
//			scenarioObj.setLabId(String.join(",", labIds));
//			scenarioObj.setComments("");
//
//			// Handle image
//			if (cover_image != null && !cover_image.isEmpty()) {
//				String contentType = cover_image.getContentType();
//				if (contentType != null && contentType.startsWith("image/")) {
//					scenarioObj.setCoverImage(cover_image.getBytes());
//				} else {
//					redirectAttributes.addFlashAttribute("message", "Invalid file type. Please upload an image file.");
//					redirectAttributes.addFlashAttribute("status", "error");
////					return mav;
//				}
//			} else {
//				// Load default image
//				String defaultImagePath = "C:\\Users\\vijay\\Desktop\\18825\\New folder\\default.jpg";
//				try {
//					byte[] defaultImageBytes = Files.readAllBytes(Paths.get(defaultImagePath));
//					scenarioObj.setCoverImage(defaultImageBytes);
//				} catch (IOException e) {
//					scenarioObj.setCoverImage(createPlaceholderImage());
//				}
//			}
//
//			// Save scenario
//			ScenarioRepository.save(scenarioObj);
//
//			redirectAttributes.addFlashAttribute("message", "Scenario saved successfully!");
//			redirectAttributes.addFlashAttribute("status", "success");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			redirectAttributes.addFlashAttribute("message", "Error while saving scenario: " + e.getMessage());
//			redirectAttributes.addFlashAttribute("status", "error");
//		}
//
//		return "redirect:/guac/Scenario_Details";
//	}

	@GetMapping("/Scenario_Details")
	public ModelAndView showScenario_Details() {
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

			if (principal != null) {
				// scenarioObj.setCreatedBy(principal.getName());
			}

			String labsArray1 = scenarioObj.getLabs(); // e.g., "101~Lab A,102~Lab B"

			// Step 1: Split by comma to get each lab entry
			String[] labsArray = labsArray1.split(",");

			// Step 2: Process Labs
			List<String> labIds = new ArrayList<>();
			List<String> labNames = new ArrayList<>();

			for (String lab : labsArray) {
				String[] parts = lab.split("~");
				if (parts.length == 2) {
					labIds.add(parts[0].trim());
					labNames.add(parts[1].trim());
					try {
						repository.updateInstanceNameAssigned(Integer.parseInt(parts[0].trim()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

//			scenarioObj.setLabId(String.join(",", labIds));
			scenarioObj.setLabs(String.join(",", labNames));

			scenarioObj.setComments("");

			if (isNew) {
				// Handle image for new scenario
				if (cover_image != null && !cover_image.isEmpty()) {
					String contentType = cover_image.getContentType();
					if (contentType != null && contentType.startsWith("image/")) {
						scenarioObj.setCoverImage(cover_image.getBytes());
					} else {
						redirectAttributes.addFlashAttribute("message",
								"Invalid file type. Please upload an image file.");
						redirectAttributes.addFlashAttribute("status", "error");
					}
				} else {
					// Load default image
					String defaultImagePath = "C:\\Users\\vijay\\Desktop\\18825\\New folder\\default.jpg";
					try {
						byte[] defaultImageBytes = Files.readAllBytes(Paths.get(defaultImagePath));
						scenarioObj.setCoverImage(defaultImageBytes);
					} catch (IOException e) {
						scenarioObj.setCoverImage(createPlaceholderImage());
					}
				}

			} else {
				// Editing existing scenario
				Optional<Add_Scenario> existing = ScenarioRepository.findById(scenarioObj.getId());
				if (existing.isPresent()) {
					Add_Scenario existingScenario = existing.get();

					if (cover_image == null || cover_image.isEmpty()) {
						scenarioObj.setCoverImage(existingScenario.getCoverImage());
					} else {
						String contentType = cover_image.getContentType();
						if (contentType != null && contentType.startsWith("image/")) {
							scenarioObj.setCoverImage(cover_image.getBytes());
						} else {
							redirectAttributes.addFlashAttribute("message",
									"Invalid file type. Please upload an image file.");
							redirectAttributes.addFlashAttribute("status", "error");
						}
					}
				}
			}

			// Save scenario
			Add_Scenario savedScenario = ScenarioRepository.save(scenarioObj);
			redirectAttributes.addFlashAttribute("message", "Scenario saved successfully!");
			redirectAttributes.addFlashAttribute("status", "success");

			// Redirect after save
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

	@GetMapping("/View_Scenario")
	public ModelAndView getView_Scenario(Principal principal) {
		ModelAndView mav = new ModelAndView("View_Scenario");
		JSONArray Finalarray = new JSONArray();
		List<Add_Scenario> dataList;
		try {
			// start

			Authentication authentication = (Authentication) principal;
			User loginedUser = (User) authentication.getPrincipal();

			String userName = loginedUser.getUsername();
			String groupName = "";
			StringBuilder vmNamesBuilder = new StringBuilder();

			List<AppUser> userList = userRepository.findByuserName(userName);

			boolean isSuperAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));

			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

			if (isSuperAdmin) {
				dataList = ScenarioRepository.findAll();
			} else {
				// Get group names
				for (AppUser appUser : userList) {
					groupName = appUser.getGroupName(); // Assuming only one AppUser per username
				}

				List<String> groups = new ArrayList<>();
				StringTokenizer groupTokenizer = new StringTokenizer(groupName, ",");
				while (groupTokenizer.hasMoreTokens()) {
					groups.add(groupTokenizer.nextToken());
				}

				// Get VM names by group
				List<Object[]> vmList = repository.getInstanceNameByGroup(groups, true);
				for (Object[] vmEntry : vmList) {
					vmNamesBuilder.append(vmEntry[1].toString()).append(",");
				}

				// Split VM names string into a list
				List<String> vmGroups = new ArrayList<>();
				String vmNames = vmNamesBuilder.toString();
				if (!vmNames.isEmpty()) {
					StringTokenizer vmTokenizer = new StringTokenizer(vmNames, ",");
					while (vmTokenizer.hasMoreTokens()) {
						vmGroups.add(vmTokenizer.nextToken());
					}
				}
//				dataList = ScenarioRepository.getView_Scenario(vmGroups);
				dataList = ScenarioRepository.findAll();
			}

//			 dataList = kVMDriveDetailsRepository.findBykVMDetails();
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
//				obj.put("Description", Description);
				obj.put("Category", Category);
				obj.put("Scenario_Type", Scenario_Type);
				obj.put("Mode", Mode);
				obj.put("Difficulty_Level", Difficulty_Level);
				obj.put("Duration", Duration);
//				obj.put("Labs", Labs);
				obj.put("Cover_Image", Cover_Image);
				obj.put("Id", SrNo);

				Finalarray.put(obj);
			}

//			System.out.println("Finalarray_getView_Scenario ::" + Finalarray);

			mav.addObject("listObj", Finalarray.toString());

		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
			System.out.println("Error fetching data: " + e.getMessage());
		}
		return mav;
	}

	@GetMapping("/My_Scenario")
	public ModelAndView getMy_View_Scenario(Principal principal) {
		ModelAndView mav = new ModelAndView("My_View_Scenario");
		JSONArray Finalarray = new JSONArray();
		List<Add_Scenario> dataList;
		try {
			// start

			Authentication authentication = (Authentication) principal;
			User loginedUser = (User) authentication.getPrincipal();

			String userName = loginedUser.getUsername();
			String groupName = "";
			StringBuilder vmNamesBuilder = new StringBuilder();

			List<AppUser> userList = userRepository.findByuserName(userName);

			boolean isSuperAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));

			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

			if (isSuperAdmin) {
				dataList = ScenarioRepository.findByUserScenario();
			} else {

				for (AppUser appUser : userList) {
					groupName = appUser.getGroupName(); // Assuming only one AppUser per username
				}

				List<String> groups = new ArrayList<>();
				StringTokenizer groupTokenizer = new StringTokenizer(groupName, ",");
				while (groupTokenizer.hasMoreTokens()) {
					groups.add(groupTokenizer.nextToken());
				}

				// Get VM names by group
				List<Object[]> vmList = repository.getInstanceNameByGroup(groups, true);
				for (Object[] vmEntry : vmList) {
					vmNamesBuilder.append(vmEntry[1].toString()).append(",");
				}

				// Split VM names string into a list
				List<String> vmGroups = new ArrayList<>();
				String vmNames = vmNamesBuilder.toString();
				if (!vmNames.isEmpty()) {
					StringTokenizer vmTokenizer = new StringTokenizer(vmNames, ",");
					while (vmTokenizer.hasMoreTokens()) {
						vmGroups.add(vmTokenizer.nextToken());
					}
				}
//				dataList = ScenarioRepository.getView_Scenario(vmGroups);
				dataList = ScenarioRepository.findByUserScenario();
			}

//			 dataList = kVMDriveDetailsRepository.findBykVMDetails();
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
//				obj.put("Description", Description);
				obj.put("Category", Category);
				obj.put("Scenario_Type", Scenario_Type);
				obj.put("Mode", Mode);
				obj.put("Difficulty_Level", Difficulty_Level);
				obj.put("Duration", Duration);
//				obj.put("Labs", Labs);
				obj.put("Cover_Image", Cover_Image);
				obj.put("Id", SrNo);

				Finalarray.put(obj);
			}

//			System.out.println("Finalarray_getView_Scenario ::" + Finalarray);

			mav.addObject("listObj", Finalarray.toString());

		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
			System.out.println("Error fetching data: " + e.getMessage());
		}
		return mav;
	}

	@PostMapping("/addplaylist_Scenario")
	@ResponseBody
	public String addScenarioToPlaylist(@RequestParam("playlistId") int playlistId,
			@RequestParam("scenarioId") int scenarioId) {
		try {
			// fetch Playlist and Scenario
			Playlist playlist = PlaylistRepository.findById(playlistId)
					.orElseThrow(() -> new RuntimeException("Playlist not found"));

			Add_Scenario scenario = ScenarioRepository.findById(scenarioId)
					.orElseThrow(() -> new RuntimeException("Scenario not found"));

			// create composite key
			PlaylistScenarioId id = new PlaylistScenarioId(playlistId, scenarioId);

			// create entity
			PlaylistScenario ps = new PlaylistScenario();
			ps.setId(id);
			ps.setPlaylist(playlist);
			ps.setScenario(scenario);

			// save
			PlaylistsSenarioRepository.save(ps);

			return "success";

		} catch (Exception e) {
			e.printStackTrace();
			return "error: " + e.getMessage();
		}
	}

	@GetMapping("/Remove_Particular_ScenerioPlaylist")
	@ResponseBody
	public String removeParticularScenarioPlaylist(@RequestParam("playlistId") int playlistId,
			@RequestParam("scenarioId") int scenarioId) {
		String result = "fail";
		try {
			// Create composite key
			PlaylistScenarioId id = new PlaylistScenarioId(playlistId, scenarioId);

			// Check if record exists
			if (PlaylistsSenarioRepository.existsById(id)) {
				PlaylistsSenarioRepository.deleteById(id);
				result = "success";
			} else {
				result = "not_found";
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error deleting data: " + e.getMessage());
		}
		return result;
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

			Optional<UserScenario> scenarioOpt = UserScenerioRepository.findByScenarioId(scenarioId);

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

	@PostMapping("/remove_lab")
	@ResponseBody
	public String remove_lab(@RequestParam("containerName") String containerName, Principal principal) {
		try {
			System.out.println("Inside_remove_lab containerName: " + containerName);
			dockerService.removeContainerByName(containerName);
			System.out.println("Container removed successfully: " + containerName);

			CommandHistoryRepository.deleteByContainerName(containerName);
			instructionTemplateRepository.deleteByLabName(containerName);
			UserLabRepository.deleteByInstanceName(containerName);

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

		System.out.println("Selected IP: " + selectedIp);

		// Discover and save Docker networks to DB
		dockerService.discoverAndSaveDockerNetworks(selectedIp);

		// Optional: fetch again to display in UI
		List<String> networks = dockerService.listDockerNetworks();
		redirectAttributes.addFlashAttribute("networks", networks);

		return "redirect:/guac/Add_Docker";
	}

}
