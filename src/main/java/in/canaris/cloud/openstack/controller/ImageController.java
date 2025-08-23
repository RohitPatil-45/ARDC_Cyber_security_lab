package in.canaris.cloud.openstack.controller;

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

import in.canaris.cloud.entity.VMCreationBean;
import in.canaris.cloud.openstack.entity.Image;
import in.canaris.cloud.openstack.repository.ImageRepository;

import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Instant;

@Controller
@RequestMapping("/image")
public class ImageController {

	@Autowired
	private ImageRepository imageRepository;

	final String var_function_name = "image"; // small letter
	final String disp_function_name = "Image"; // capital letter

	private static String token = OpenstackCloudInstanceController.token;
	private static Instant tokenExpiry = OpenstackCloudInstanceController.tokenExpiry;

	@GetMapping("/view")
	public ModelAndView getAll() {
		ModelAndView mav = new ModelAndView(var_function_name + "_view");
		mav.addObject("action_name", var_function_name);
		try {
			mav.addObject("listObj", imageRepository.findAll());
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
		Image objEnt = new Image();
		mav.addObject("objEnt", objEnt);
		return mav;
	}

	@PostMapping("/save")
	public String save(Image obj, RedirectAttributes redirectAttributes) {
		System.out.println("Image Name = " + obj.getImgName());
		System.out.println("Image Disk format = " + obj.getDiskFormat());
		System.out.println("Image Img File = " + obj.getImgFile());
		System.out.println("Image Min disk = " + obj.getMinDisk());
		System.out.println("Image Min Ram = " + obj.getMinRam());
		System.out.println("Image Visibility = " + obj.getVisibility());
		System.out.println("Image Prtected = " + obj.getIsProtected());
		String responsetosend = "";
		try {

			int intProtected = obj.getIsProtected();
			boolean ProtectedVal;

			if (intProtected == 1) {
				ProtectedVal = true;
			} else if (intProtected == 0) {
				ProtectedVal = false;
			} else {
				// Handle the case where isPublic is neither 1 nor 0
				throw new IllegalArgumentException("Invalid value for isPublic: " + intProtected);
			}

			String KEYSTONE_URL = "http://172.16.5.33:5000/v3/auth/tokens";
			String GLANCE_URL = "http://172.16.5.33:9292/v2/images";
			String USERNAME = "admin";
			String PASSWORD = "admin123";
			String PROJECT_NAME = "admin";
			String DOMAIN_ID = "default";

			String Iamge_name = obj.getImgName().trim(); // "Testiamgepro2";
			String disk_format = obj.getDiskFormat().trim(); // "iso";
			String container_format = "bare";
			String visibility = obj.getVisibility(); // "public";
			int min_disk = obj.getMinDisk();// 20;
			int min_ram = obj.getMinRam(); // 2048;
			Boolean protectedval = ProtectedVal;// true;
			String ISOPath = obj.getImgFile(); // "/path/to/your.iso";

			if (token == null || Instant.now().isAfter(tokenExpiry)) {
				authenticate(KEYSTONE_URL, USERNAME, PASSWORD, PROJECT_NAME, DOMAIN_ID);
			}
			String imageId = createImage(Iamge_name, disk_format, container_format, min_disk, min_ram, visibility,
					protectedval, GLANCE_URL);

			uploadISO(imageId, new File(ISOPath), GLANCE_URL);

//			redirectAttributes.addFlashAttribute("message", "success");

			responsetosend = "success";

			try {
				System.out.println("In Create iamage  get TCP openstack ");

				String discoverType = "OpenStack_All_Discover";
				System.out.println("Image Id for tcp sending = " + imageId);
				ObjectOutputStream outputStream = null;
				Socket socket = null;
				try {
					String ipAddress = "172.16.5.24";
					socket = new Socket(ipAddress, 9006);
					VMCreationBean bean = new VMCreationBean();
					bean.setActivity(discoverType);
					bean.setInstanceName(imageId);

					outputStream = new ObjectOutputStream(socket.getOutputStream());
					outputStream.writeObject(bean);

					ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
					String response = (String) inputStream.readObject();

					System.out.println("response return: " + response);
//					redirectAttributes.addFlashAttribute("message", "success");
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
//				redirectAttributes.addFlashAttribute("message", "fail");
				responsetosend = "fail";
			}

		} catch (Exception e) {
//			redirectAttributes.addAttribute("message", "fail");
			System.out.println("[error]  while creating iamge by api :: " + e);
			e.printStackTrace();
			responsetosend = "fail";
		}

		redirectAttributes.addFlashAttribute("message", responsetosend);
		return "redirect:/" + var_function_name + "/new";

	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") String id, Model model, RedirectAttributes redirectAttributes) {
		String messagetosend = "";
		System.out.println("[inside instance delete] " + id);

		try {

			String KEYSTONE_URL = "http://172.16.5.33:5000/v3/auth/tokens";
			String GLANCE_URL = "http://172.16.5.33:9292/v2/images/";
			String USERNAME = "admin";
			String PASSWORD = "admin123";
			String PROJECT_NAME = "admin";
			String DOMAIN_ID = "default";

			if (token == null || Instant.now().isAfter(tokenExpiry)) {
				authenticate(KEYSTONE_URL, USERNAME, PASSWORD, PROJECT_NAME, DOMAIN_ID);
			}

			deleteImage(token, id, GLANCE_URL);

			imageRepository.deleteByImageId(id);
			messagetosend = "The " + disp_function_name + " with id=" + id + " has been deleted successfully!";
		} catch (Exception e) {
			System.out.println("[error] while deleteing image " + e);
			e.printStackTrace();
			messagetosend = e.getMessage();
//			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}

		redirectAttributes.addFlashAttribute("message", messagetosend);
		return "redirect:/" + var_function_name + "/view";
	}

	public static void deleteImage(String token, String imageId, String GLANCE_URL) throws IOException {
		String url = GLANCE_URL + imageId;
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			HttpDelete httpDelete = new HttpDelete(url);
			httpDelete.setHeader("X-Auth-Token", token);

			try (CloseableHttpResponse response = client.execute(httpDelete)) {
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 204) {
					System.out.println("Image deleted successfully.");
				} else {
					System.out.println("Failed to delete image. Status code: " + statusCode);
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

	private static String createImage(String imagename, String diskFormat, String containerFormat, int minDisk,
			int minRam, String visibility, Boolean protectedval, String GLANCE_URL) throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(GLANCE_URL);

		String jsonRequest = "{\n" + "  \"name\": \"" + imagename + "\",\n" + "  \"disk_format\": \"" + diskFormat
				+ "\",\n" + "  \"container_format\": \"" + containerFormat + "\",\n" + "  \"visibility\": \""
				+ visibility + "\",\n" + "  \"min_disk\": " + minDisk + ",\n" + "  \"min_ram\": " + minRam + ",\n"
				+ "  \"protected\":" + protectedval + "\n" + "}";
		StringEntity entity = new StringEntity(jsonRequest);
		httpPost.setEntity(entity);
		httpPost.setHeader("Content-Type", "application/json");
		httpPost.setHeader("X-Auth-Token", token);

		CloseableHttpResponse response = client.execute(httpPost);
		String jsonResponse = EntityUtils.toString(response.getEntity());

		ObjectMapper mapper = new ObjectMapper();
		JsonNode image = mapper.readTree(jsonResponse);

		client.close();

		return image.get("id").asText();
	}

	private static void uploadISO(String imageId, File isoFile, String GLANCE_URL) throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPut httpPut = new HttpPut(GLANCE_URL + "/" + imageId + "/file");

		FileEntity fileEntity = new FileEntity(isoFile);
		httpPut.setEntity(fileEntity);
		httpPut.setHeader("Content-Type", "application/octet-stream");
		httpPut.setHeader("X-Auth-Token", token);

		CloseableHttpResponse response = client.execute(httpPut);
		EntityUtils.consume(response.getEntity());

		client.close();
	}

	@GetMapping("/checkImageExist")
	public @ResponseBody String checkImageExist(@RequestParam String image) {
		String duplicate = "";
		try {

			List<Image> list = imageRepository.findByimgName(image);
			if (!list.isEmpty()) {
				duplicate = "duplicate";
			}

		} catch (Exception e) {
			System.out.println("Exception occured while checking for duplicate Image = " + e);
		}
		return duplicate;
	}

}
