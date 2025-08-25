package in.canaris.cloud.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
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

import in.canaris.cloud.entity.AppUser;
import in.canaris.cloud.entity.CloudInstance;
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

	@GetMapping("/View_Vm_Listing")
	public String View_Vm_ListingConnections(@RequestParam("Id") int id, Model model) {
		System.out.println("Requested Id = " + id);

		CloudInstance instance = repository.findById(id).orElse(null);
		System.out.println("vvvvFound instance: " + instance);
		if (instance != null) {
			System.out.println("getVm_instructions: " + instance.getVm_instructions());
			System.out.println("IP: " + instance.getInstance_ip());
		} else {
			System.out.println("No instance found for ID: " + id);
		}

		List<CloudInstance> connections = new ArrayList<>();
		if (instance != null) {
			connections.add(instance);
		}

		System.out.println("connectionsFound instance: " + connections);
		model.addAttribute("connections", connections);
		model.addAttribute("instructions", instance.getVm_instructions());
		return "View_Vm_Listing";
	}

	@GetMapping("/view/{id}")
	public String viewConnection(@PathVariable String id, Model model) {
		String url = guacService.getEmbedUrl(id);
		model.addAttribute("embedUrl", url);
		return "view";
	}

	@PostMapping("/playlistsave")
	public ModelAndView savePlaylistData(@RequestParam String playlist_title, @RequestParam String playlist_name,
			@RequestParam String description, @RequestParam(required = false) MultipartFile cover_image,
			@RequestParam String tag_input, Principal principal) {

		ModelAndView mav = new ModelAndView("Add_Playlist");

		try {

			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			String username = loginedUser.getUsername();
			// Log all parameters
			System.out.println("playlist_title: " + playlist_title);
			System.out.println("playlist_name: " + playlist_name);
			System.out.println("description: " + description);

			System.out.println("tag_suggestions: " + tag_input);

			System.out.println(
					"cover_image: " + (cover_image != null ? cover_image.getOriginalFilename() : "No file uploaded"));

//	        // Create new scenario object
			Playlist list = new Playlist();
			list.setPlaylistName(playlist_name);
			list.setPlaylistTitle(playlist_title);
			list.setDescription(description);
			list.setTag(tag_input);
			list.setCreatedBy(username);

			// Handle the uploaded image file
			if (cover_image != null && !cover_image.isEmpty()) {
				// Validate file type
				String contentType = cover_image.getContentType();
				if (contentType != null && contentType.startsWith("image/")) {
					// Store image bytes in database
					list.setCoverImage(cover_image.getBytes());
					System.out.println("Uploaded image saved to database");
				} else {
					// Invalid file type
					mav.addObject("message", "Invalid file type. Please upload an image file.");
					mav.addObject("status", "error");
					return mav;
				}
			} else {
				// No image uploaded - try to set a default image
				String defaultImagePath = "C:\\Users\\vijay\\Desktop\\18825\\New folder\\default.jpg";
				try {
					byte[] defaultImageBytes = Files.readAllBytes(Paths.get(defaultImagePath));
					list.setCoverImage(defaultImageBytes);
					System.out.println("Default image loaded and saved to database");
				} catch (IOException e) {
					System.err.println("Error loading default image: " + e.getMessage());
					// Create a minimal placeholder image instead of null
					list.setCoverImage(createPlaceholderImage());
					System.out.println("Placeholder image created and saved to database");
				}
			}

			// Save to database
			PlaylistRepository.save(list);

			mav.addObject("message", "Scenario saved successfully!");
			mav.addObject("status", "success");

		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("message", "Error while saving scenario: " + e.getMessage());
			mav.addObject("status", "error");
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

	@GetMapping("/View_Particular_Playlist")
	public ModelAndView getView_Particular_Scenerio(@RequestParam String Id) {

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
