package in.canaris.cloud.openstack.controller;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.canaris.cloud.openstack.repository.InstanceVmRepository;
import in.canaris.cloud.openstack.repository.SecurityGroupRepository;
import in.canaris.cloud.openstack.repository.flavorRepository;
import in.canaris.cloud.openstack.repository.keyPairRepository;
import in.canaris.cloud.openstack.entity.Flavor;
import in.canaris.cloud.openstack.entity.KeyPair;

@Controller
@RequestMapping("/InstanceVm")
public class InstanceController {

	@Autowired
	private InstanceVmRepository Repository;

	final String var_function_name = "instanceVm"; // small letter
	final String disp_function_name = "InstanceVm"; // capital letter

	private static String token = OpenstackCloudInstanceController.token;
	private static Instant tokenExpiry = OpenstackCloudInstanceController.tokenExpiry;

	@GetMapping("/view")
	public ModelAndView getAll() {
		ModelAndView mav = new ModelAndView(var_function_name + "_view");
		mav.addObject("action_name", disp_function_name);
		try {
			mav.addObject("listObj", Repository.findAll());
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
	public String save(KeyPair obj, RedirectAttributes redirectAttributes) {
		try {
//			Repository.save(obj);
			redirectAttributes.addFlashAttribute("message", "success");
		} catch (Exception e) {
			redirectAttributes.addAttribute("message", "fail");
		}
		return "redirect:/" + var_function_name + "/new";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") String id, Model model, RedirectAttributes redirectAttributes) {
		String messagetosend = "";
		System.out.println("[inside instance delete] " + id);
//		Repository.deleteByInstanceid(id);

		try {

			String KEYSTONE_URL = "http://172.16.5.33:5000/v3/auth/tokens";
			String GLANCE_URL = "http://172.16.5.33:8774/v2.1/284df609814f4a5e8072146ddaf06335/servers/";
			String USERNAME = "admin";
			String PASSWORD = "admin123";
			String PROJECT_NAME = "admin";
			String DOMAIN_ID = "default";

			if (token == null || Instant.now().isAfter(tokenExpiry)) {
				authenticate(KEYSTONE_URL, USERNAME, PASSWORD, PROJECT_NAME, DOMAIN_ID);
			}

			deleteInstance(token, id, GLANCE_URL);
			Repository.deleteByinstanceID(id);

			messagetosend = "The " + disp_function_name + " with id=" + id + " has been deleted successfully!";
		} catch (Exception e) {
			System.out.println("[error] while deleteing instance " + e);
			e.printStackTrace();
			messagetosend = e.getMessage();
//			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}

		redirectAttributes.addFlashAttribute("message", messagetosend);
		return "redirect:/" + disp_function_name + "/view";
	}

	public static void deleteInstance(String token, String ImageId, String NOVA_URL) throws IOException {
		String url = NOVA_URL + ImageId;
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			HttpDelete httpDelete = new HttpDelete(url);
			httpDelete.setHeader("X-Auth-Token", token);

			try (CloseableHttpResponse response = client.execute(httpDelete)) {
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 204) {
					System.out.println("Instance deleted successfully.");
				} else {
					System.out.println("Failed to delete instance. Status code: " + statusCode);
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

//	@GetMapping("/checkFlavorExist")
//	public @ResponseBody String checkFlavorExist(@RequestParam String flavorName) {
//		String duplicate = "";
//		try {
//
////			List<Flavor> list = flavorRepository.findByfalvorName(flavorName);
////			if (!list.isEmpty()) {
////				duplicate = "duplicate";
////			}
//
//		} catch (Exception e) {
//			System.out.println("Exception occured while checking for duplicate flavor = " + e);
//		}
//		return duplicate;
//	}

}
