package in.canaris.cloud.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
import in.canaris.cloud.openstack.entity.Playlist;
import in.canaris.cloud.repository.ScenarioRepository;
import in.canaris.cloud.repository.UserRepository;
import in.canaris.cloud.repository.CloudInstanceRepository;
import in.canaris.cloud.repository.PlaylistRepository;
import in.canaris.cloud.repository.PlaylistsSenarioRepository;
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

//	@GetMapping("/View_Vm_Listing")
//	public String View_Vm_ListingConnections(@RequestParam("Id") int id, Model model) {
//		System.out.println("Requested Id = " + id);
//
//		CloudInstance instance = repository.findById(id).orElse(null);
//		System.out.println("vvvvFound instance: " + instance);
//		if (instance != null) {
//			System.out.println("getVm_instructions: " + instance.getVm_instructions());
//			System.out.println("IP: " + instance.getInstance_ip());
//		} else {
//			System.out.println("No instance found for ID: " + id);
//		}
//
//		List<CloudInstance> connections = new ArrayList<>();
//		if (instance != null) {
//			connections.add(instance);
//		}
//
//		System.out.println("connectionsFound instance: " + connections);
//		model.addAttribute("connections", connections);
//		model.addAttribute("instructions", instance.getVm_instructions());
//		return "View_Vm_Listing";
//	}

	@GetMapping("/View_Vm_Listing")
	public String viewVmListing(@RequestParam("Id") int scenarioId, Model model) {
		System.out.println("Requested scenario Id = " + scenarioId);

		// Fetch scenario
		Add_Scenario scenario = ScenarioRepository.findById(scenarioId).orElse(null);
		if (scenario == null) {
			System.out.println("No scenario found for ID: " + scenarioId);
			model.addAttribute("connections", Collections.emptyList());
			model.addAttribute("instructions", "");
			return "View_Vm_Listing";
		}

		System.out.println("Found scenario: " + scenario.getScenarioTitle());

		// Extract Lab IDs
		String labIdsStr = scenario.getLabId(); // e.g., "287,289,290,295"
		List<Integer> labIds = new ArrayList<>();
		if (labIdsStr != null && !labIdsStr.isEmpty()) {
			for (String idStr : labIdsStr.split(",")) {
				try {
					labIds.add(Integer.valueOf(idStr.trim()));
				} catch (NumberFormatException e) {
					System.err.println("Invalid LabId: " + idStr);
				}
			}
		}

		// Fetch all CloudInstance objects for these lab IDs
		List<CloudInstance> connections = new ArrayList<>();
		if (!labIds.isEmpty()) {
			connections = repository.findAllById(labIds); // Spring Data JPA method
		}

		// Debug logs
		connections.forEach(ci -> {
			System.out.println("CloudInstance: ID=" + ci.getId() + ", IP=" + ci.getInstance_ip());
		});

		// Pass to view
		model.addAttribute("connections", connections);
//		model.addAttribute("instructions", connections.getVm_instructions()); // If scenario has instructions
		return "View_Vm_Listing";
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
		model.addAttribute("instructionsdata", instances);
		return "viewConnection";
	}

//	@PostMapping("/playlistsave")
//	public ModelAndView savePlaylistData(@RequestParam String playlist_title, @RequestParam String playlist_name,
//			@RequestParam String description, @RequestParam(required = false) MultipartFile cover_image,
//			@RequestParam String tag_input, Principal principal) {
//
//		ModelAndView mav = new ModelAndView("Add_Playlist");
//
//		try {
//
//			User loginedUser = (User) ((Authentication) principal).getPrincipal();
//			String username = loginedUser.getUsername();
//			// Log all parameters
//			System.out.println("playlist_title: " + playlist_title);
//			System.out.println("playlist_name: " + playlist_name);
//			System.out.println("description: " + description);
//
//			System.out.println("tag_suggestions: " + tag_input);
//
//			System.out.println(
//					"cover_image: " + (cover_image != null ? cover_image.getOriginalFilename() : "No file uploaded"));
//
////	        // Create new scenario object
//			Playlist list = new Playlist();
//			list.setPlaylistName(playlist_name);
//			list.setPlaylistTitle(playlist_title);
//			list.setDescription(description);
//			list.setTag(tag_input);
//			list.setCreatedBy(username);
//
//			// Handle the uploaded image file
//			if (cover_image != null && !cover_image.isEmpty()) {
//				// Validate file type
//				String contentType = cover_image.getContentType();
//				if (contentType != null && contentType.startsWith("image/")) {
//					// Store image bytes in database
//					list.setCoverImage(cover_image.getBytes());
//					System.out.println("Uploaded image saved to database");
//				} else {
//					// Invalid file type
//					mav.addObject("message", "Invalid file type. Please upload an image file.");
//					mav.addObject("status", "error");
//					return mav;
//				}
//			} else {
//				// No image uploaded - try to set a default image
//				String defaultImagePath = "C:\\Users\\vijay\\Desktop\\18825\\New folder\\default.jpg";
//				try {
//					byte[] defaultImageBytes = Files.readAllBytes(Paths.get(defaultImagePath));
//					list.setCoverImage(defaultImageBytes);
//					System.out.println("Default image loaded and saved to database");
//				} catch (IOException e) {
//					System.err.println("Error loading default image: " + e.getMessage());
//					// Create a minimal placeholder image instead of null
//					list.setCoverImage(createPlaceholderImage());
//					System.out.println("Placeholder image created and saved to database");
//				}
//			}
//
//			// Save to database
//			PlaylistRepository.save(list);
//
//			mav.addObject("message", "Scenario saved successfully!");
//			mav.addObject("status", "success");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			mav.addObject("message", "Error while saving scenario: " + e.getMessage());
//			mav.addObject("status", "error");
//		}
//
//		return mav;
//	}

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
		System.out.println("inside_render_image_getPlaylistImage ::");
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
		System.out.println("inside_render_image_getScenarioImage ::");
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
				String LabId = temp.getLabId() != null ? temp.getLabId() : "";
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
				obj.put("LabId", LabId);

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

			scenarioObj.setLabId(String.join(",", labIds));
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

}
