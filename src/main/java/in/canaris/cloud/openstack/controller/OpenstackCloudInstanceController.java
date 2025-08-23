package in.canaris.cloud.openstack.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import in.canaris.cloud.openstack.repository.AvailabilityZoneInfoRepository;
import in.canaris.cloud.openstack.repository.ImageRepository;
import in.canaris.cloud.openstack.repository.NetworkRepository;
import in.canaris.cloud.openstack.repository.OpenstackVMRequestRepository;
import in.canaris.cloud.openstack.repository.SecurityGroupRepository;
import in.canaris.cloud.openstack.repository.flavorRepository;
import in.canaris.cloud.openstack.repository.keyPairRepository;
import in.canaris.cloud.openstack.repository.projectRepository;
import in.canaris.cloud.repository.CloudInstanceUsageDailyRepository;
import in.canaris.cloud.repository.UserRepository;
import in.canaris.cloud.entity.AppUser;
import in.canaris.cloud.entity.CloudInstance;
import in.canaris.cloud.entity.RequestApproval;
import in.canaris.cloud.entity.VMCreationBean;
import in.canaris.cloud.openstack.entity.Flavor;
import in.canaris.cloud.openstack.entity.KeyPair;
import in.canaris.cloud.openstack.entity.Network;
import in.canaris.cloud.openstack.entity.OpenstackVMRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.Instant;

@Controller
@RequestMapping("/openstack")
public class OpenstackCloudInstanceController {

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private flavorRepository flavorRepository;

	@Autowired
	private AvailabilityZoneInfoRepository availabilityZoneRepository;

	@Autowired
	private NetworkRepository networkRepository;

	@Autowired
	private keyPairRepository keyPairRepository;

	@Autowired
	private SecurityGroupRepository securityGroupRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OpenstackVMRequestRepository requestRepository;

	@Autowired
	private projectRepository requestprojectRepository;

	public static String token;
	public static Instant tokenExpiry;

	@GetMapping("/new")
	public ModelAndView add() {
		ModelAndView mav = new ModelAndView("openstack_cloud_instance");
		mav.addObject("imageList", imageRepository.findDistinctImageName());
		mav.addObject("flavorList", flavorRepository.findDistinctFlavor());
		mav.addObject("availabilityZoneList", availabilityZoneRepository.findDistinctAvailabilityZone());
		mav.addObject("networkList", networkRepository.findDistinctNetwork());
		mav.addObject("keyPairList", keyPairRepository.findDistinctKeyPair());
		mav.addObject("securityGroupList", securityGroupRepository.findDistinctSecurityGroup());
		mav.addObject("ProjectsList", requestprojectRepository.findByprojectName());

		return mav;
	}

	@GetMapping("/viewProjects")
	public ModelAndView viewProjects() {
		ModelAndView mav = new ModelAndView("openstack_viewProjects");
		mav.addObject("listObj", requestprojectRepository.findAll());

		return mav;
	}

	@GetMapping("/Projectview")
	public ModelAndView Projectview() {
		ModelAndView mav = new ModelAndView("openstack_Projectview");
//		mav.addObject("imageList", imageRepository.findDistinctImageName());
//		mav.addObject("flavorList", flavorRepository.findDistinctFlavor());
//		mav.addObject("availabilityZoneList", availabilityZoneRepository.findDistinctAvailabilityZone());
//		mav.addObject("networkList", networkRepository.findDistinctNetwork());
//		mav.addObject("keyPairList", keyPairRepository.findDistinctKeyPair());
//		mav.addObject("securityGroupList", securityGroupRepository.findDistinctSecurityGroup());
//		mav.addObject("ProjectsList", requestprojectRepository.findByprojectName());

		return mav;
	}

	@GetMapping("/allRequests")
	public ModelAndView allRequests(Principal principal) {
		ModelAndView mav = new ModelAndView("openstack_vm_request");
		if (principal == null) {
			// Redirect to login page if the principal is null
			ModelAndView mav1 = new ModelAndView("welcomePage");
			return mav1;
		}
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String groupName = "";
		List<String> userNames = new ArrayList<>();
		// mav.addObject("action_name", var_function_name);
		try {
			List<AppUser> user1 = userRepository.findByuserName(loginedUser.getUsername());
			boolean isSuperAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));
			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

			if (isSuperAdmin) {

				mav.addObject("listObj", requestRepository.findByadminApprovalStatus("Approved"));
				mav.addObject("availabilityZoneList", availabilityZoneRepository.findDistinctAvailabilityZone());
				mav.addObject("networkList", networkRepository.findDistinctNetwork());
				mav.addObject("keyPairList", keyPairRepository.findDistinctKeyPair());
				mav.addObject("securityGroupList", securityGroupRepository.findDistinctSecurityGroup());
				mav.addObject("ProjectsList", requestprojectRepository.findByprojectName());

			} else if (isAdmin) {
				for (AppUser appUser : user1) {
					groupName = appUser.getGroupName();
				}
				List<String> groups = new ArrayList<>();
				StringTokenizer token = new StringTokenizer(groupName, ",");
				while (token.hasMoreTokens()) {
					groups.add(token.nextToken());
				}
				List<AppUser> users = userRepository.findBygroups(groups);
				for (AppUser obj : users) {
					userNames.add(obj.getUserName());
				}
				System.out.println("users in " + groupName + " = " + userNames);
				mav.addObject("listObj", requestRepository.findByRequesters(userNames));
			}

		} catch (Exception e) {
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
		}
		return mav;
	}

	// Admin
	@GetMapping("/acceptAdminApproval")
	public @ResponseBody String acceptAdminApproval(@RequestParam String reqId, Principal principal) {
		String response = "";
		try {
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			String username = loginedUser.getUsername();
			AppUser user = userRepository.findByUsername(username);
			OpenstackVMRequest approvalObj = requestRepository.findById(Integer.valueOf(reqId)).get();

			approvalObj.setApprovedOn(new Timestamp(System.currentTimeMillis()));
			approvalObj.setAdminApprovalStatus("Approved");
			approvalObj.setApproverName(username);
			requestRepository.save(approvalObj);

			response = "success";

		} catch (Exception e) {
			System.out.println("Exception occured during admin approval : " + e);
			response = "fail";
		}

		return response;

	}

	@GetMapping("/rejectAdminApproval")
	public @ResponseBody String rejectAdminApproval(@RequestParam String reqId, @RequestParam String remark,
			Principal principal) {
		String response = "";
		try {
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			String username = loginedUser.getUsername();
			AppUser user = userRepository.findByUsername(username);
			OpenstackVMRequest approvalObj = requestRepository.findById(Integer.valueOf(reqId)).get();

			approvalObj.setAdminApprovalStatus("Rejected");
			approvalObj.setFinalApprovalStatus("Rejected");
			approvalObj.setDescription(remark);
			approvalObj.setApproverName(username);
			approvalObj.setApprovedOn(new Timestamp(System.currentTimeMillis()));
			requestRepository.save(approvalObj);
			response = "reject";

		} catch (Exception e) {
			System.out.println("Exception occured during admin rejection : " + e);
			response = "fail";
		}

		return response;

	}
	// End Admin

	@GetMapping("/createInstance")
	public @ResponseBody String createInstance(@RequestParam String instanceName, @RequestParam String image,
			@RequestParam String flavor, @RequestParam String availabilityZone, @RequestParam String network,
			@RequestParam String keyPair, @RequestParam String securityGroupName, @RequestParam String projectid)
			throws Exception {

		String result = "fail";

		try {

			System.out.println("Instance Name: " + instanceName + "\nImage: " + image + "\nFlavor: " + flavor
					+ "\nAvailability Zone: " + availabilityZone + "\nNetwork: " + network + "\nKey Pair: " + keyPair
					+ "\nSecurity Group Name: " + securityGroupName + "\nProject id: " + projectid);

			String KEYSTONE_URL = "http://172.16.5.33:5000/v3/auth/tokens";
			String NOVA_URL = "http://172.16.5.33:8774/v2.1/284df609814f4a5e8072146ddaf06335/servers";
			String USERNAME = "admin";
			String PASSWORD = "admin123";
			String PROJECT_NAME = "admin";
			String DOMAIN_ID = "default";

			String instanceNamemain = instanceName;// "MyInstance";
			String imageId = image;// "39a21a17-ef79-4594-85b0-a00c5881758f";
			String flavorId = flavor;// "0";
			String networkId = network;// "92a0b49e-e3c5-4ab9-a26c-6ac112d4151d";
			String availabilityZonemain = availabilityZone; // "nova";
			String keypairName = keyPair; // "key1";
			String securityGroupNamemain = securityGroupName;// "default";

			if (token == null || Instant.now().isAfter(tokenExpiry)) {
				authenticate(KEYSTONE_URL, USERNAME, PASSWORD, PROJECT_NAME, DOMAIN_ID);
			}
			Map<String, String> detailsfromapi = createInstancehere(instanceNamemain, imageId, flavorId, networkId,
					availabilityZonemain, keypairName, securityGroupNamemain, NOVA_URL, projectid);
			System.out.println("Created Instance ID: " + detailsfromapi.toString());
			result = "success";

			try {
				System.out.println("In Create vm details get TCP openstack ");

				String discoverType = "GetOpenstackVmDetails_ByID";
				System.out.println("instanceId for tcp sending = " + detailsfromapi.get("id"));
				ObjectOutputStream outputStream = null;
				Socket socket = null;
				try {
					String ipAddress = "172.16.5.24";
					socket = new Socket(ipAddress, 9006);
					VMCreationBean bean = new VMCreationBean();
					bean.setActivity(discoverType);
					bean.setInstanceName(detailsfromapi.get("id"));
					bean.setGeneration(detailsfromapi.get("securityGroupName"));

					outputStream = new ObjectOutputStream(socket.getOutputStream());
					outputStream.writeObject(bean);

					ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
					String response = (String) inputStream.readObject();

					System.out.println("response return: " + response);
					result = "success";
				} catch (Exception e) {
					System.out.println(e);
					result = e.toString();
				} finally {

					try {
						if (outputStream != null) {
							outputStream.flush();
							outputStream.close();
						}
						if (socket != null) {
							socket.close();
						}
					} catch (Exception e) {
						System.out.println("Exception:" + e);
					}

				}

			} catch (Exception e) {
				System.out.println("[error] while creating vm openstack TCP" + e);
				e.printStackTrace();
				result = "fail";
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("[error] while creating vm openstack " + e);
			e.printStackTrace();
			result = "fail";
		}

		return result;
	}

	private Map<String, String> createInstancehere(String instanceName, String imageId, String flavorId,
			String networkId, String availabilityZone, String keypairName, String securityGroupName, String NOVA_URL,
			String Projectid) throws Exception {
		JsonNode instance = null;
		Map<String, String> result = new HashMap<>();
		try {

			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(NOVA_URL);

			String jsonRequest = "{ \"server\": { \"name\": \"" + instanceName + "\", \"imageRef\": \"" + imageId
					+ "\", \"flavorRef\": \"" + flavorId + "\", \"networks\": [{ \"uuid\": \"" + networkId
					+ "\" }], \"availability_zone\": \"" + availabilityZone + "\", \"key_name\": \"" + keypairName
					+ "\", \"security_groups\": [{ \"name\": \"" + securityGroupName + "\" }], \"tenant_id\": \""
					+ Projectid + "\" } }";
			StringEntity entity = new StringEntity(jsonRequest);
			httpPost.setEntity(entity);
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("X-Auth-Token", token);

			CloseableHttpResponse response = client.execute(httpPost);
			String jsonResponse = EntityUtils.toString(response.getEntity());

			ObjectMapper mapper = new ObjectMapper();
			instance = mapper.readTree(jsonResponse).path("server");
			String id = instance.get("id").asText();
			String secGroupName = instance.path("security_groups").get(0).path("name").asText();

			result.put("id", id);
			result.put("securityGroupName", secGroupName);
			client.close();

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("[error in creating vm] : " + e);
			e.printStackTrace();
		}
		return result;

	}

	private void authenticate(String KEYSTONE_URL, String USERNAME, String PASSWORD, String PROJECT_NAME,
			String DOMAIN_ID) throws Exception {

		try {

			String jsonRequest = "{ \"auth\": { \"identity\": { \"methods\": [ \"password\" ], \"password\": { \"user\": { \"name\": \""
					+ USERNAME + "\", \"domain\": { \"id\": \"" + DOMAIN_ID + "\" }, \"password\": \"" + PASSWORD
					+ "\" } } }, \"scope\": { \"project\": { \"name\": \"" + PROJECT_NAME
					+ "\", \"domain\": { \"id\": \"" + DOMAIN_ID + "\" } } } } }";
			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(KEYSTONE_URL);

			StringEntity entity = new StringEntity(jsonRequest);
			httpPost.setEntity(entity);
			httpPost.setHeader("Content-Type", "application/json");

			CloseableHttpResponse response = client.execute(httpPost);
			token = response.getFirstHeader("X-Subject-Token").getValue();

			String jsonResponse = EntityUtils.toString(response.getEntity());
			ObjectMapper mapper = new ObjectMapper();
			JsonNode tokenNode = mapper.readTree(jsonResponse).path("token");

			String expiry = tokenNode.path("expires_at").asText();
			tokenExpiry = Instant.parse(expiry);

			client.close();
		} catch (Exception e) {
			System.out.println("[error in Authentication on openstack] : " + e);
			e.printStackTrace();
		}
	}

	@GetMapping("/AceeptedCreatingVmBySuper")
	public @ResponseBody String AceeptedCreatingVmBySuper(@RequestParam String availabilityZone,
			@RequestParam String network, @RequestParam String keyPair, @RequestParam String securityGroupName,
			@RequestParam int requestId, @RequestParam String Projectid) throws Exception {

		String result = "fail";

		OpenstackVMRequest approvalObj = requestRepository.findById(requestId).get();
		String instanceName = approvalObj.getInstanceName();
		String image = approvalObj.getImage();
		String flavor = approvalObj.getFlavor();

		try {

			System.out.println("Instance Name: " + instanceName + "\nImage: " + image + "\nFlavor: " + flavor
					+ "\nAvailability Zone: " + availabilityZone + "\nNetwork: " + network + "\nKey Pair: " + keyPair
					+ "\nSecurity Group Name: " + securityGroupName + "\nProject id Name: " + Projectid);

			String KEYSTONE_URL = "http://172.16.5.33:5000/v3/auth/tokens";
			String NOVA_URL = "http://172.16.5.33:8774/v2.1/284df609814f4a5e8072146ddaf06335/servers";
			String USERNAME = "admin";
			String PASSWORD = "admin123";
			String PROJECT_NAME = "admin";
			String DOMAIN_ID = "default";

			String instanceNamemain = instanceName;// "MyInstance";
			String imageId = image;// "39a21a17-ef79-4594-85b0-a00c5881758f";
			String flavorId = flavor;// "0";
			String networkId = network;// "92a0b49e-e3c5-4ab9-a26c-6ac112d4151d";
			String availabilityZonemain = availabilityZone; // "nova";
			String keypairName = keyPair; // "key1";
			String securityGroupNamemain = securityGroupName;// "default";

			if (token == null || Instant.now().isAfter(tokenExpiry)) {
				authenticate(KEYSTONE_URL, USERNAME, PASSWORD, PROJECT_NAME, DOMAIN_ID);
			}
			Map<String, String> detailsfromapi = createInstancehere(instanceNamemain, imageId, flavorId, networkId,
					availabilityZonemain, keypairName, securityGroupNamemain, NOVA_URL, Projectid);
			System.out.println("Created Instance ID: " + detailsfromapi.toString());
			result = "success";

			try {
				System.out.println("In Create vm details get TCP openstack ");

				String discoverType = "GetOpenstackVmDetails_ByID";
				System.out.println("instanceId for tcp sending = " + detailsfromapi.get("id"));
				ObjectOutputStream outputStream = null;
				Socket socket = null;
				try {
					String ipAddress = "172.16.5.24";
					socket = new Socket(ipAddress, 9006);
					VMCreationBean bean = new VMCreationBean();
					bean.setActivity(discoverType);
					bean.setInstanceName(detailsfromapi.get("id"));
					bean.setGeneration(detailsfromapi.get("securityGroupName"));

					outputStream = new ObjectOutputStream(socket.getOutputStream());
					outputStream.writeObject(bean);

					ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
					String response = (String) inputStream.readObject();

					System.out.println("response return: " + response);
					result = "success";
				} catch (Exception e) {
					System.out.println(e);
					result = e.toString();
				} finally {

					try {
						if (outputStream != null) {
							outputStream.flush();
							outputStream.close();
						}
						if (socket != null) {
							socket.close();
						}
					} catch (Exception e) {
						System.out.println("Exception:" + e);
					}

				}

			} catch (Exception e) {
				System.out.println("[error] while creating vm openstack TCP" + e);
				e.printStackTrace();
				result = "fail";
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("[error] while creating vm openstack " + e);
			e.printStackTrace();
			result = "fail";
		}

		return result;
	}

	@GetMapping("/rejectSuperAdminApproval")
	public @ResponseBody String rejectSuperAdminApproval(@RequestParam String reqId, @RequestParam String remark,
			Principal principal) {
		String response = "";
		try {
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			String username = loginedUser.getUsername();
			AppUser user = userRepository.findByUsername(username);
			OpenstackVMRequest approvalObj = requestRepository.findById(Integer.valueOf(reqId)).get();

			approvalObj.setFinalApprovalStatus("Rejected");
			approvalObj.setDescription(remark);
			approvalObj.setApproverName(username);
			approvalObj.setApprovedOn(new Timestamp(System.currentTimeMillis()));
			requestRepository.save(approvalObj);
			response = "reject";

		} catch (Exception e) {
			System.out.println("Exception occured during super admin rejection : " + e);
			response = "fail";
		}

		return response;

	}

	@GetMapping("/createProject")
	public @ResponseBody String createProject(@RequestParam String createProjectname,
			@RequestParam String createProjectDescription, @RequestParam String CreateProjectEnabled) throws Exception {

		String result = "fail";
		System.out.println("createProjectname: " + createProjectname);
		System.out.println("createProjectDescription: " + createProjectDescription);
		System.out.println("CreateProjectEnabled: " + CreateProjectEnabled);

		try {
			String KEYSTONE_URL = "http://172.16.5.33:5000/v3/auth/tokens";
			String NOVA_URL = "http://172.16.5.33:8774/v2.1/284df609814f4a5e8072146ddaf06335/servers";
			String USERNAME = "admin";
			String PASSWORD = "admin123";
			String PROJECT_NAME = "admin";
			String DOMAIN_ID = "default";

			if (token == null || Instant.now().isAfter(tokenExpiry)) {
				authenticate(KEYSTONE_URL, USERNAME, PASSWORD, PROJECT_NAME, DOMAIN_ID);
			}
			createOpenStackProject(createProjectname, createProjectDescription, CreateProjectEnabled);
			System.out.println("[Success] project created");
			result = "success";

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("[error] while creating Project openstack " + e);
			e.printStackTrace();
			result = "fail";
		}

		return result;
	}

	private void createOpenStackProject(String projectName, String projectDescription, String enabledString) {
		// OpenStack Keystone API endpoint for project creation
		String keystoneEndpoint = "http://identity.example.com/v3/projects";

		// Authentication token obtained from Keystone
		String domainId = "default";
		boolean enabled = Boolean.parseBoolean(enabledString.trim());
		;
		String jsonPayload = "{ \"project\": { \"name\": \"" + projectName + "\", " + "\"description\": \""
				+ projectDescription + "\", " + "\"domain_id\": \"" + domainId + "\", " + "\"enabled\": " + enabled
				+ " } }";

		// Create HttpClient
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			// Create HTTP POST request
			HttpPost httpPost = new HttpPost(keystoneEndpoint);

			// Set headers
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("X-Auth-Token", token);

			// Set request body
			StringEntity entity = new StringEntity(jsonPayload);
			httpPost.setEntity(entity);

			// Execute the request
			HttpResponse response = httpClient.execute(httpPost);

			// Handle response
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				String responseString = EntityUtils.toString(responseEntity);
				System.out.println("Response from OpenStack: " + responseString);
			} else {
				System.out.println("Empty response from OpenStack");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}