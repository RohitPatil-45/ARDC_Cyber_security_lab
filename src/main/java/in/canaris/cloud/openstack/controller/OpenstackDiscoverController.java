package in.canaris.cloud.openstack.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.*;
import java.security.Principal;

import in.canaris.cloud.entity.AddPhysicalServer;
import in.canaris.cloud.entity.VMCreationBean;
import in.canaris.cloud.repository.AddPhysicalServerRepository;
import in.canaris.cloud.repository.PhysicalServerRepository;

@Controller
@RequestMapping("/OpenStackDiscover")
public class OpenstackDiscoverController {

//	@Autowired
//	private PhysicalServerRepository repository;
//
//	@Autowired
//	private AddPhysicalServerRepository addPhysicalServerRepository;

	final String var_function_name = "openStackDiscover"; // small letter
	final String disp_function_name = "OpenStackDiscover"; // capital letter

	@GetMapping("/view")
	public ModelAndView add(Principal principal) {
		ModelAndView mav = new ModelAndView(var_function_name + "_view");
		if (principal == null) {
			ModelAndView mav1 = new ModelAndView("welcomePage");
			return mav1;
		}
		mav.addObject("pageTitle", "" + disp_function_name);
		mav.addObject("action_name", var_function_name);
//		mav.addObject("physicalServerIPList", addPhysicalServerRepository.getPhysicalServerIPs());
//		AddPhysicalServer objEnt = new AddPhysicalServer();
//		mav.addObject("objEnt", objEnt);
		return mav;
	}

//	@GetMapping("/getIpList")
//	public @ResponseBody String getIpList() {
//		System.out.println("getSubProductName  controller calledd:");
//		String json = null;
//
//		try {
//			List<Object[]> PhysicalServeriplist = repository.getAllPhysicalServerip();
//			json = new ObjectMapper().writeValueAsString(PhysicalServeriplist);
//		} catch (Exception e) {
//			System.out.println("exception :" + e);
//		}
//		return json;
//	}

	@GetMapping("/save")
	public @ResponseBody String discover(@RequestParam String serverIP, @RequestParam String discoverType,
			RedirectAttributes redirectAttributes) {
		System.out.println("In discover TCP openstack ");
		String result = "";
		try {

			System.out.println("Server IP = " + serverIP + "\nDiscoverType = " + discoverType);
			ObjectOutputStream outputStream = null;
			Socket socket = null;
			try {
				String ipAddress = "172.16.5.24";
				socket = new Socket(ipAddress, 9006);
				VMCreationBean bean = new VMCreationBean();
				bean.setActivity(discoverType);
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
			System.out.println("Exception occured while discovery = " + e);
		}
		return result;
	}

}
