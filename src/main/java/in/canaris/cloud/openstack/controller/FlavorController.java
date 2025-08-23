package in.canaris.cloud.openstack.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import in.canaris.cloud.openstack.repository.flavorRepository;
import in.canaris.cloud.entity.VMCreationBean;
import in.canaris.cloud.openstack.entity.Flavor;

import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/flavor")
public class FlavorController {

	@Autowired
	private flavorRepository flavorRepository;

	final String var_function_name = "flavor"; // small letter
	final String disp_function_name = "Flavor"; // capital letter

	private static String token = OpenstackCloudInstanceController.token;
	private static Instant tokenExpiry = OpenstackCloudInstanceController.tokenExpiry;

	@GetMapping("/view")
	public ModelAndView getAll() {
		ModelAndView mav = new ModelAndView(var_function_name + "_view");
		mav.addObject("action_name", var_function_name);
		try {
			mav.addObject("listObj", flavorRepository.findAll());
		} catch (Exception e) {
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
		}
		return mav;
	}

	@GetMapping("/new")
	public ModelAndView add() {
		ModelAndView mav = new ModelAndView(var_function_name + "_add");
		mav.addObject("pageTitle", "Add New " + disp_function_name);
		mav.addObject("action_name", var_function_name);
		Flavor objEnt = new Flavor();
		mav.addObject("objEnt", objEnt);
		return mav;
	}

	@PostMapping("/save")
	public String save(Flavor obj, RedirectAttributes redirectAttributes) {
		String responsetosend = "";
		try {

			System.out.println(" " + obj.getName() + " " + obj.getVcpus() + " " + obj.getRootDisk() + " " + obj.getRam()
					+ " " + obj.getIsPublic());

//			flavorRepository.save(obj);
			int intPublic = obj.getIsPublic();
			boolean publicVal;

			if (intPublic == 1) {
				publicVal = true;
			} else if (intPublic == 0) {
				publicVal = false;
			} else {
				// Handle the case where isPublic is neither 1 nor 0
				throw new IllegalArgumentException("Invalid value for isPublic: " + intPublic);
			}
			try {

				String KEYSTONE_URL = "http://172.16.5.33:5000/v3/auth/tokens";
				String NOVA_URL = "http://172.16.5.33:8774/v2.1/284df609814f4a5e8072146ddaf06335/flavors";
				String USERNAME = "admin";
				String PASSWORD = "admin123";
				String PROJECT_NAME = "admin";
				String DOMAIN_ID = "default";

				String Flavor_name = obj.getName();// "Small";
				int ram = obj.getRam();// 1024; // mb
				int vcpus = obj.getVcpus();// 1;
				int disk = Integer.parseInt(obj.getRootDisk().trim());// 20; // GB
				boolean isPublic = publicVal;// true;

				if (token == null || Instant.now().isAfter(tokenExpiry)) {
					authenticate(KEYSTONE_URL, USERNAME, PASSWORD, PROJECT_NAME, DOMAIN_ID);
				}
				String FlovorID = createFlavor(Flavor_name, ram, vcpus, disk, isPublic, NOVA_URL);

				try {
					System.out.println("In Create flavor  get TCP openstack ");

					String discoverType = "OpenStack_All_Discover";
					System.out.println("Flovor Id for tcp sending = " + FlovorID);
					ObjectOutputStream outputStream = null;
					Socket socket = null;
					try {
						String ipAddress = "172.16.5.24";
						socket = new Socket(ipAddress, 9006);
						VMCreationBean bean = new VMCreationBean();
						bean.setActivity(discoverType);
						bean.setInstanceName(FlovorID);

						outputStream = new ObjectOutputStream(socket.getOutputStream());
						outputStream.writeObject(bean);

						ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
						String response = (String) inputStream.readObject();

						System.out.println("response return: " + response);
//						redirectAttributes.addFlashAttribute("message", "success");
						responsetosend = "success";
					} catch (Exception e) {
						System.out.println(e);
						e.printStackTrace();
						responsetosend = "fail";
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
//					redirectAttributes.addFlashAttribute("message", "fail");
					responsetosend = "fail";
				}

			} catch (Exception e) {
				System.out.println("[Error] while creating flavor :: " + e);

				e.printStackTrace();
//				redirectAttributes.addAttribute("message", "fail");
				responsetosend = "fail";
			}

		} catch (Exception e) {
//			redirectAttributes.addAttribute("message", "fail");
			responsetosend = "fail";
		}
		redirectAttributes.addFlashAttribute("message", responsetosend);
		return "redirect:/" + var_function_name + "/new";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") String id, Model model, RedirectAttributes redirectAttributes) {
		String messagetosend = "";
		System.out.println("[inside Flavor delete] " + id);

		flavorRepository.deleteByid(id);

		try {

			String KEYSTONE_URL = "http://172.16.5.33:5000/v3/auth/tokens";
			String GLANCE_URL = "http://172.16.5.33:8774/v2.1/284df609814f4a5e8072146ddaf06335/flavors/";
			String USERNAME = "admin";
			String PASSWORD = "admin123";
			String PROJECT_NAME = "admin";
			String DOMAIN_ID = "default";

			if (token == null || Instant.now().isAfter(tokenExpiry)) {
				authenticate(KEYSTONE_URL, USERNAME, PASSWORD, PROJECT_NAME, DOMAIN_ID);
			}

			deleteFlavor(token, id, GLANCE_URL);

			messagetosend = "The " + disp_function_name + " with id=" + id + " has been deleted successfully!";
		} catch (Exception e) {
			System.out.println("[error] while deleteing flavor " + e);
			e.printStackTrace();
			messagetosend = e.getMessage();
//			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}

		redirectAttributes.addFlashAttribute("message", messagetosend);
		return "redirect:/" + var_function_name + "/view";
	}

	public static void deleteFlavor(String token, String flavorId, String NOVA_URL) throws IOException {
		String url = NOVA_URL + flavorId;
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			HttpDelete httpDelete = new HttpDelete(url);
			httpDelete.setHeader("X-Auth-Token", token);

			try (CloseableHttpResponse response = client.execute(httpDelete)) {
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 204) {
					System.out.println("Flavor deleted successfully.");
				} else {
					System.out.println("Failed to delete flavor. Status code: " + statusCode);
				}
			}
		}
	}

	private static void authenticate(String KEYSTONE_URL, String USERNAME, String PASSWORD, String PROJECT_NAME,
			String DOMAIN_ID) throws Exception {
		String jsonRequest = "{ \"auth\": { \"identity\": { \"methods\": [ \"password\" ], \"password\": { \"user\": { \"name\": \""
				+ USERNAME + "\", \"domain\": { \"id\": \"" + DOMAIN_ID + "\" }, \"password\": \"" + PASSWORD
				+ "\" } } }, \"scope\": { \"project\": { \"name\": \"" + PROJECT_NAME + "\", \"domain\": { \"id\": \""
				+ DOMAIN_ID + "\" } } } } }";
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
	}

	private static String createFlavor(String name, int ram, int vcpus, int disk, boolean isPublic, String NOVA_URL)
			throws Exception {
		String response2 = "false";
		try {

			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(NOVA_URL);

			String jsonRequest = "{\n" + "  \"flavor\": {\n" + "    \"name\": \"" + name + "\",\n" + "    \"ram\": "
					+ ram + ",\n" + "    \"vcpus\": " + vcpus + ",\n" + "    \"disk\": " + disk + ",\n"
					+ "    \"os-flavor-access:is_public\": " + isPublic + "\n" + "  }\n" + "}";
			StringEntity entity = new StringEntity(jsonRequest);
			httpPost.setEntity(entity);
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("X-Auth-Token", token);

			CloseableHttpResponse response = client.execute(httpPost);
			String jsonResponse = EntityUtils.toString(response.getEntity());

			ObjectMapper mapper = new ObjectMapper();
			JsonNode flavor = mapper.readTree(jsonResponse).path("flavor");

			System.out.println("Created Flavor:");
			System.out.println("ID: " + flavor.get("id").asText());
			System.out.println("Name: " + flavor.get("name").asText());
			response2 = flavor.get("id").asText();
			client.close();

		} catch (Exception e) {
			System.out.println("[error] while creating flavor by api" + e);
			e.printStackTrace();
		}

		return response2;
	}
}
