package in.canaris.cloud.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.canaris.cloud.entity.AdditionalStorage;
import in.canaris.cloud.entity.AlertDash;
import in.canaris.cloud.entity.AppRole;
import in.canaris.cloud.entity.AppUser;
import in.canaris.cloud.entity.CloudInstance;
import in.canaris.cloud.entity.CloudInstanceCpuThresholdHistory;
import in.canaris.cloud.entity.CloudInstanceLog;
import in.canaris.cloud.entity.CloudInstanceMemoryThresholdHistory;
import in.canaris.cloud.entity.CloudInstanceNodeHealthMonitoring;
import in.canaris.cloud.entity.CloudInstanceUsage;
import in.canaris.cloud.entity.CloudInstanceUsageDaily;
import in.canaris.cloud.entity.Customer;
import in.canaris.cloud.entity.Discount;
import in.canaris.cloud.entity.Group;
import in.canaris.cloud.entity.RequestApproval;
import in.canaris.cloud.entity.VMCreationBean;
import in.canaris.cloud.entity.VPC;
import in.canaris.cloud.entity.billing;
import in.canaris.cloud.openstack.entity.Instance;
import in.canaris.cloud.openstack.entity.projects;
import in.canaris.cloud.openstack.repository.InstanceVmRepository;
import in.canaris.cloud.openstack.repository.projectRepository;
import in.canaris.cloud.repository.AddPhysicalServerRepository;
import in.canaris.cloud.repository.AdditionalStorageRepository;
import in.canaris.cloud.repository.AppUserRepository;
import in.canaris.cloud.repository.CloudInstanceCpuThresholdHistoryRepository;
import in.canaris.cloud.repository.CloudInstanceLogRepository;
import in.canaris.cloud.repository.CloudInstanceMemoryThresholdHistoryRepository;
import in.canaris.cloud.repository.CloudInstanceNodeHealthMonitoringRepository;
import in.canaris.cloud.repository.CloudInstanceRepository;
import in.canaris.cloud.repository.CloudInstanceUsageDailyRepository;
import in.canaris.cloud.repository.CloudInstanceUsageRepository;
import in.canaris.cloud.repository.CustomUserRoleRepository;
import in.canaris.cloud.repository.CustomerRepository;
import in.canaris.cloud.repository.DiscountRepository;
import in.canaris.cloud.repository.FirewallRepository;
import in.canaris.cloud.repository.GroupRepository;
import in.canaris.cloud.repository.LocationRepository;
import in.canaris.cloud.repository.NodeUtilizationRepository;
import in.canaris.cloud.repository.PriceRepository;
import in.canaris.cloud.repository.RequestApprovalRepository;
import in.canaris.cloud.repository.SubProductRepository;
import in.canaris.cloud.repository.SwitchRepository;
import in.canaris.cloud.repository.UserRepository;
import in.canaris.cloud.repository.VPCRepository;
import in.canaris.cloud.server.entity.HardwareInventory;
import in.canaris.cloud.server.entity.HardwareInventoryLinux;
import in.canaris.cloud.server.entity.NodeAvailability;
import in.canaris.cloud.server.entity.NodeHealthMonitoring;
import in.canaris.cloud.server.entity.NodeMonitoring;
import in.canaris.cloud.server.repository.DriveRepository;
import in.canaris.cloud.server.repository.HardwareInventoryLinuxRepository;
import in.canaris.cloud.server.repository.HardwareInventoryRepository;
import in.canaris.cloud.server.repository.InterfaceMonitoringRepository;
import in.canaris.cloud.server.repository.NodeAvailabilityRepository;
import in.canaris.cloud.server.repository.NodeMonitoringRepository;
import in.canaris.cloud.server.repository.NodeStatusHistoryRepository;
import in.canaris.cloud.server.repository.VMHealthRepository;
import in.canaris.cloud.utils.ExecutePSCommand;

import java.util.Calendar;
import java.util.Comparator;

@Controller
@RequestMapping("/MainDashboard")
public class MainDashboardController {

	@Autowired
	private AppUserRepository appRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CloudInstanceRepository repository;

	@Autowired
	private RequestApprovalRepository approvalRepository;

	@Autowired
	private InstanceVmRepository requestprojectRepository;

	@Autowired
	private CloudInstanceCpuThresholdHistoryRepository cpuThresholdHistoryRepository;

	@Autowired
	private CloudInstanceMemoryThresholdHistoryRepository memoryThresholdHistoryRepository;

	@GetMapping("/Main")
	public ModelAndView Mainview(Principal principal) {
		ModelAndView mav = new ModelAndView("MainDashboard");
		mav.addObject("pageTitle", "Dashboard");
		mav.addObject("action_name", "Dashboard");
		boolean isSuperAdmin = false;
		boolean isAdmin = false;
		boolean isUser = false;
		List<String> groupName = null;

//		get hyperv instance data
		if (principal == null) {
			ModelAndView mav1 = new ModelAndView("welcomePage");
			return mav1;
		}
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();
		AppUser obj = appRepository.findOneByUserName(username);
		List<CloudInstance> instances = null;
//		mav.addObject("action_name", var_function_name);
		mav.addObject("groupList", groupRepository.getAllGroups());
		mav.addObject("customerList", customerRepository.getCustomerName());
		try {

			isSuperAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));
			isAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
			isUser = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"));

			groupName = new ArrayList<>();
			StringTokenizer token = new StringTokenizer(obj.getGroupName(), ",");
			while (token.hasMoreTokens()) {
				groupName.add(token.nextToken());
			}

			if (isSuperAdmin) {

				instances = repository.findByIsMonitoringOrderByIdDesc(true);

			} else if (isAdmin) {

				System.out.println("Inoperator groupName = " + groupName);

				instances = repository.findByIsMonitoringAndGroupNameOrderByIdDesc(true, groupName);

			} else {
				List<Integer> li = approvalRepository.findByRequesterNameCustom(username);

				System.out.println(li.toString());
				instances = repository.findByidInAndIsMonitoring(li, true);
			}

//			for (CloudInstance data : instances) {
//
//				data.setDiskAssigned(data.getDiskAssigned() == null ? "-"
//						: data.getDiskAssigned().equals("") ? "-"
//								
//										: String.format("%.2f",
//												Double.parseDouble(data.getDiskAssigned()) / (1024 * 1024 * 1024)));
//
//				data.setMemoryAssigned(data.getMemoryAssigned() == null ? "-"
//						: data.getMemoryAssigned().equals("") ? "-"
//								
//										: String.format("%.2f",
//												Double.parseDouble(data.getMemoryAssigned()) / (1024 * 1024 * 1024)));
//			}
			mav.addObject("listObj", instances);

		} catch (Exception e) {
			System.out.println("Exception occured while fetching VM Data = " + e);
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
		}

//		get openstack data
		List<Instance> openstackinstances = requestprojectRepository.findAll();
		mav.addObject("listObj", requestprojectRepository.findAll());

//		get data for piechart
		mav.addObject("instanceCount", instances != null ? instances.size() : 0);
		mav.addObject("openstackinstancesCount", openstackinstances != null ? openstackinstances.size() : 0);

//		alerts
		List<String> instancesalert = null;

		if (isSuperAdmin) {

			instancesalert = repository.findByIsMonitoringOrderByIdDescOnlyVM(true);

		} else if (isAdmin) {

			System.out.println("Inoperator groupName = " + groupName);

			instancesalert = repository.findByIsMonitoringAndGroupNameOrderByIdDescOnlyVM(true, groupName);

		} else {
			List<Integer> li = approvalRepository.findByRequesterNameCustom(username);

			System.out.println(li.toString());
			instancesalert = repository.findByidInAndIsMonitoringOnlyVm(li, true);
		}

		List<AlertDash> AlertList = new ArrayList<>();

//		List<CloudInstanceMemoryThresholdHistory> ThreshHist = memoryThresholdHistoryRepository.findAll();
		List<CloudInstanceMemoryThresholdHistory> ThreshHist = memoryThresholdHistoryRepository
				.findCurrentMonthData(instancesalert);

		for (CloudInstanceMemoryThresholdHistory ThreshObj : ThreshHist) {
			AlertDash alertnewobj = new AlertDash();
			alertnewobj.setAlert_Name("MEMORY THRESHOLD");
			alertnewobj.setAlert_STATUS(ThreshObj.getMemoryStatus());
			alertnewobj.setDifference(ThreshObj.getMemoryUtilization() - ThreshObj.getMemoryThreshold());
			alertnewobj.setEVENT_TIMESTAMP(ThreshObj.getEventTimestamp());
			alertnewobj.setMEMORY_THRESHOLD(ThreshObj.getMemoryThreshold());
			alertnewobj.setMEMORY_UTILIZATION(ThreshObj.getMemoryUtilization());
			alertnewobj.setPhysicalServer_ip(ThreshObj.getNodeIp());
			alertnewobj.setVM_Name(ThreshObj.getVmName());

			AlertList.add(alertnewobj);

		}

//		List<CloudInstanceCpuThresholdHistory> ThreshCPUHist = cpuThresholdHistoryRepository.findAll();
		List<CloudInstanceCpuThresholdHistory> ThreshCPUHist = cpuThresholdHistoryRepository
				.findCurrentMonthData(instancesalert);

		for (CloudInstanceCpuThresholdHistory ThreshCPUObj : ThreshCPUHist) {
			AlertDash alertnewobj = new AlertDash();
			alertnewobj.setAlert_Name("CPU THRESHOLD");
			alertnewobj.setAlert_STATUS(ThreshCPUObj.getCpuStatus());
			alertnewobj.setDifference(ThreshCPUObj.getCpuUtilization() - ThreshCPUObj.getCpuUtilization());
			alertnewobj.setEVENT_TIMESTAMP(ThreshCPUObj.getEventTimestamp());
			alertnewobj.setMEMORY_THRESHOLD(ThreshCPUObj.getCpuThreshold());
			alertnewobj.setMEMORY_UTILIZATION(ThreshCPUObj.getCpuUtilization());
			alertnewobj.setPhysicalServer_ip(ThreshCPUObj.getNodeIp());
			alertnewobj.setVM_Name(ThreshCPUObj.getVmName());

			AlertList.add(alertnewobj);

		}

		List<AlertDash> sortedAlertList = AlertList.stream()
				.sorted(Comparator.comparing(AlertDash::getEVENT_TIMESTAMP).reversed()).collect(Collectors.toList());

		mav.addObject("AlertListObj", sortedAlertList);

//		sort alert list

		List<AlertDash> highAlerts = new ArrayList<>();
		List<AlertDash> lowAlerts = new ArrayList<>();
		List<AlertDash> mediumAlerts = new ArrayList<>();

		for (AlertDash alert : sortedAlertList) {
			switch (alert.getAlert_STATUS().toLowerCase()) {
			case "high":
				highAlerts.add(alert);
				break;
			case "low":
				lowAlerts.add(alert);
				break;
			case "medium":
				mediumAlerts.add(alert);
				break;
			}
		}
		mav.addObject("AlertListHighObj", highAlerts);
		mav.addObject("AlertListMediumObj", mediumAlerts);
		mav.addObject("AlertListLowObj", lowAlerts);

//		its count
		mav.addObject("AlertListHighCount", highAlerts != null ? highAlerts.size() : 0);
		mav.addObject("AlertListMediumCount", mediumAlerts != null ? mediumAlerts.size() : 0);
		mav.addObject("AlertListLowCount", lowAlerts != null ? lowAlerts.size() : 0);

		return mav;
	}

	@GetMapping("/RoleMain")
	public ModelAndView RoleMainview(Principal principal) {
		ModelAndView mav = new ModelAndView("RoleMainDashboard");
		mav.addObject("pageTitle", "Dashboard");
		mav.addObject("action_name", "Dashboard");

//		get hyperv instance data
		if (principal == null) {
			ModelAndView mav1 = new ModelAndView("welcomePage");
			return mav1;
		}
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();
		AppUser obj = appRepository.findOneByUserName(username);
		List<CloudInstance> instances = null;
//		mav.addObject("action_name", var_function_name);
		mav.addObject("groupList", groupRepository.getAllGroups());
		mav.addObject("customerList", customerRepository.getCustomerName());
		try {

			boolean isSuperAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));
			boolean isAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
			boolean isUser = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"));

			List<String> groupName = new ArrayList<>();
			StringTokenizer token = new StringTokenizer(obj.getGroupName(), ",");
			while (token.hasMoreTokens()) {
				groupName.add(token.nextToken());
			}

			if (isSuperAdmin) {

				instances = repository.findByIsMonitoringOrderByIdDesc(true);

			} else  {

				System.out.println("Inoperator groupName = " + groupName);

				instances = repository.findByIsMonitoringAndGroupNameOrderByIdDesc(true, groupName);

			} 
			
//			if (isAdmin) {
//
//				System.out.println("Inoperator groupName = " + groupName);
//
//				instances = repository.findByIsMonitoringAndGroupNameOrderByIdDesc(true, groupName);
//
//			} else {
//				List<Integer> li = approvalRepository.findByRequesterNameCustom(username);
//
//				System.out.println(li.toString());
//				instances = repository.findByidInAndIsMonitoring(li, true);
//			}

//			for (CloudInstance data : instances) {
//
//				data.setDiskAssigned(data.getDiskAssigned() == null ? "-"
//						: data.getDiskAssigned().equals("") ? "-"
//								
//										: String.format("%.2f",
//												Double.parseDouble(data.getDiskAssigned()) / (1024 * 1024 * 1024)));
//
//				data.setMemoryAssigned(data.getMemoryAssigned() == null ? "-"
//						: data.getMemoryAssigned().equals("") ? "-"
//								
//										: String.format("%.2f",
//												Double.parseDouble(data.getMemoryAssigned()) / (1024 * 1024 * 1024)));
//			}
			
			for (CloudInstance data : instances) {

//				data.setDiskAssigned(data.getDiskAssigned() == null ? "-"
//						:  String.format("%.2f",
//										Double.parseDouble(data.getDiskAssigned()) / (1024 * 1024 * 1024)));

				data.setMemoryAssigned(data.getMemoryAssigned() == null ? "-"
						: data.getMemoryAssigned().equals("") ? "-": data.getMemoryAssigned().length()==0 ? "-"

								: String.format("%.2f",
										Double.parseDouble(data.getMemoryAssigned()) / (1024 * 1024 * 1024)));
			}
			mav.addObject("listObj", instances);

		} catch (Exception e) {
			System.out.println("Exception occured while fetching VM Data = " + e);
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
		}

//		get openstack data

		List<Instance> openstackinstances = requestprojectRepository.findAll();
		mav.addObject("listObjopenstack", requestprojectRepository.findAll());

//		get data for count
		mav.addObject("instanceCount", instances != null ? instances.size() : 0);
		mav.addObject("openstackinstancesCount", openstackinstances != null ? openstackinstances.size() : 0);

		return mav;
	}

}
