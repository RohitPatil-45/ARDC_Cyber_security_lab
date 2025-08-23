package in.canaris.cloud.openstack.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import in.canaris.cloud.openstack.repository.InstanceVmRepository;
import in.canaris.cloud.openstack.repository.NetworkRepository;
import in.canaris.cloud.openstack.repository.OpenstackVMRequestRepository;
import in.canaris.cloud.openstack.repository.SecurityGroupRepository;
import in.canaris.cloud.openstack.repository.flavorRepository;
import in.canaris.cloud.openstack.repository.keyPairRepository;
import in.canaris.cloud.repository.CloudInstanceUsageDailyRepository;
import in.canaris.cloud.entity.CloudInstance;
import in.canaris.cloud.entity.VMCreationBean;
import in.canaris.cloud.openstack.entity.Flavor;
import in.canaris.cloud.openstack.entity.KeyPair;
import in.canaris.cloud.openstack.entity.Network;
import in.canaris.cloud.openstack.entity.OpenstackVMRequest;

import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.Principal;
import java.time.Instant;

@Controller
@RequestMapping("/openstack_user")
public class OpenstackCloudInstanceUserController {

	@Autowired
	private ImageRepository imageRepository;

	@Autowired
	private flavorRepository flavorRepository;

	@Autowired
	private OpenstackVMRequestRepository requestRepository;
	
	@Autowired
	private InstanceVmRepository instanceRepository;
	
	@Autowired
	private AvailabilityZoneInfoRepository availabilityZoneRepository;

	@Autowired
	private NetworkRepository networkRepository;

	@Autowired
	private keyPairRepository keyPairRepository;

	@Autowired
	private SecurityGroupRepository securityGroupRepository;

	public static String token;
	public static Instant tokenExpiry;
	
	

	@GetMapping("/vm")
	public ModelAndView add() {
		ModelAndView mav = new ModelAndView("openstack_cloud_instance_user");
		mav.addObject("imageList", imageRepository.findDistinctImageName());
		mav.addObject("flavorList", flavorRepository.findDistinctFlavor());
		return mav;
	}

	@GetMapping("/view")
	public ModelAndView getAll() {
		ModelAndView mav = new ModelAndView("instanceVm_view");
		mav.addObject("action_name", "InstanceVm");
		try {
			mav.addObject("listObj", instanceRepository.findAll());
		} catch (Exception e) {
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
		}
		return mav;
	}
	
	@GetMapping("/userRequest")
	public ModelAndView userRequests(Principal principal) {
		ModelAndView mav = new ModelAndView("openstack_vm_request");
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();
		try {
			
			mav.addObject("listObj", requestRepository.findByrequestBy(username));
			mav.addObject("availabilityZoneList", availabilityZoneRepository.findDistinctAvailabilityZone());
			mav.addObject("networkList", networkRepository.findDistinctNetwork());
			mav.addObject("keyPairList", keyPairRepository.findDistinctKeyPair());
			mav.addObject("securityGroupList", securityGroupRepository.findDistinctSecurityGroup());
		} catch (Exception e) {
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
		}
		return mav;
	}
	
	@GetMapping("/request")
	public @ResponseBody String createInstance(@RequestParam String instanceName, @RequestParam String image,
			@RequestParam String flavor, Principal principal) {
		
		String result = null;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();
		try {
			OpenstackVMRequest req = new OpenstackVMRequest();
			req.setInstanceName(instanceName);
			req.setImage(image);
			req.setFlavor(flavor);
			req.setRequestType("instance create");
			req.setDescription("Vm creation for the user");
			req.setRequestBy(username);
			req.setAdminApprovalStatus("Pending");
			req.setFinalApprovalStatus("Pending");
			
			requestRepository.save(req);
			
			result = "success~"+username;
			
		} catch (Exception e) {
			System.out.println("Exception occured while raising request for vm creation = "+e);
		}
		return result;
		
	}

}
