package in.canaris.cloud.controller;

import java.nio.file.Paths;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import in.canaris.cloud.entity.AlertDash;
import in.canaris.cloud.entity.AppUser;
import in.canaris.cloud.entity.CloudInstance;
import in.canaris.cloud.entity.CloudInstanceCpuThresholdHistory;
import in.canaris.cloud.entity.CloudInstanceDailyUsageByAgent;
import in.canaris.cloud.entity.CloudInstanceLog;
import in.canaris.cloud.entity.CloudInstanceMemoryThresholdHistory;
import in.canaris.cloud.entity.CloudInstanceNodeHealthMonitoring;
import in.canaris.cloud.entity.CloudInstanceUsage;
import in.canaris.cloud.entity.Discount;
import in.canaris.cloud.entity.KVMDriveDetails;
import in.canaris.cloud.openstack.entity.Add_Scenario;
import in.canaris.cloud.repository.AppUserRepository;
import in.canaris.cloud.repository.CloudInstanceCpuThresholdHistoryRepository;
import in.canaris.cloud.repository.CloudInstanceLogRepository;
import in.canaris.cloud.repository.CloudInstanceMemoryThresholdHistoryRepository;
import in.canaris.cloud.repository.CloudInstanceNodeHealthHistoryRepository;
import in.canaris.cloud.repository.CloudInstanceNodeHealthMonitoringRepository;
import in.canaris.cloud.repository.CloudInstanceRepository;
import in.canaris.cloud.repository.CloudInstanceUsageDailyRepository;
import in.canaris.cloud.repository.CustomerRepository;
import in.canaris.cloud.repository.DiscountRepository;
import in.canaris.cloud.repository.GroupRepository;
import in.canaris.cloud.repository.KVMDriveDetailsRepository;
import in.canaris.cloud.repository.NodeUtilizationRepository;
import in.canaris.cloud.repository.RequestApprovalRepository;
import in.canaris.cloud.repository.ScenarioRepository;
import in.canaris.cloud.repository.UserRepository;
import in.canaris.cloud.repository.VMStatusHistoryRepository;
import in.canaris.cloud.repository.VmLiveStatusHistoryRepository;
import in.canaris.cloud.server.entity.NodeAvailability;
import in.canaris.cloud.server.repository.NodeAvailabilityRepository;
import in.canaris.cloud.server.repository.NodeStatusHistoryRepository;
import in.canaris.cloud.server.repository.VMHealthRepository;
import in.canaris.cloud.service.GuacamoleService;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
@RequestMapping("/report")
public class ReportController {

	final String var_function_name = "report"; // small letter
	final String disp_function_name = "Report"; // capital letter

	@Autowired
	private CloudInstanceRepository repository;

	@Autowired
	private CloudInstanceLogRepository logRepository;

	@Autowired
	private VMStatusHistoryRepository statusHistoryRepository;

	@Autowired
	private CloudInstanceUsageDailyRepository dailyUsageRepository;

	@Autowired
	private NodeAvailabilityRepository nodeAvailabilityRepository;

	@Autowired
	private NodeUtilizationRepository nodeUtilizationRepository;

	@Autowired
	private NodeStatusHistoryRepository nodeStatusHistoryRepository;

	@Autowired
	private CloudInstanceNodeHealthHistoryRepository vmHealthRepository;

	@Autowired
	private CloudInstanceMemoryThresholdHistoryRepository memoryThresholdHistoryRepository;

	@Autowired
	private CloudInstanceCpuThresholdHistoryRepository cpuThresholdHistoryRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private AppUserRepository appRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RequestApprovalRepository approvalRepository;

	@Autowired
	private VmLiveStatusHistoryRepository vmLiveStatusHistoryRepository;

	@Autowired
	private KVMDriveDetailsRepository kVMDriveDetailsRepository;

	@Autowired
	private ScenarioRepository ScenarioRepository;

	@Autowired
	private CloudInstanceNodeHealthMonitoringRepository cloudInstanceNodeHealthMonitoringRepository;

	@GetMapping("/resourceUtilization")
	public ModelAndView resourceUtilizationReport() {
		ModelAndView mav = new ModelAndView("resourceUtilizationReport");
		mav.addObject("pageTitle", "Report");
		mav.addObject("instanceNameList", repository.getInstanceName(true));
		CloudInstance objEnt = new CloudInstance();
		mav.addObject("objEnt", objEnt);
		return mav;
	}

	@GetMapping("/vmActivityReport")
	public ModelAndView vmActivityReport(Principal principal) {
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		List<CloudInstance> instances = null;
		String userName = loginedUser.getUsername();
		String groupName = "";
		List<AppUser> user1 = userRepository.findByuserName(loginedUser.getUsername());
		boolean isSuperAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

		ModelAndView mav = new ModelAndView("vmActivityReport");
		mav.addObject("pageTitle", "Report");

		if (isSuperAdmin) {
			mav.addObject("instanceNameList", repository.getInstanceName(true));
		} else {

			for (AppUser appUser : user1) {
				groupName = appUser.getGroupName();
			}
			List<String> groups = new ArrayList<>();
			StringTokenizer token = new StringTokenizer(groupName, ",");
			while (token.hasMoreTokens()) {
				groups.add(token.nextToken());
			}

			mav.addObject("instanceNameList", repository.getInstanceNameByGroup(groups, true));
		}
		return mav;
	}

	@GetMapping("/vmStatusHistoryReport")
	public ModelAndView vmStatusHistoryReport(Principal principal) {
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		List<CloudInstance> instances = null;
		String userName = loginedUser.getUsername();
		String groupName = "";
		List<AppUser> user1 = userRepository.findByuserName(loginedUser.getUsername());
		boolean isSuperAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));

//		boolean isAdmin = authentication.getAuthorities().stream()
//				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

		ModelAndView mav = new ModelAndView("vmStatusHistoryReport");
		mav.addObject("pageTitle", "VM Status History Report");

		if (isSuperAdmin) {
			mav.addObject("instanceNameList", repository.getInstanceName(true));
		} else {

			for (AppUser appUser : user1) {
				groupName = appUser.getGroupName();
			}
			List<String> groups = new ArrayList<>();
			StringTokenizer token = new StringTokenizer(groupName, ",");
			while (token.hasMoreTokens()) {
				groups.add(token.nextToken());
			}
			System.out.println("group name = " + groups);
			mav.addObject("instanceNameList", repository.getInstanceNameByGroup(groups, true));
		}

		return mav;
	}

	@GetMapping("/vmAvailabilityReport")
	public ModelAndView vmAvailabilityReport(Principal principal) {
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		List<CloudInstance> instances = null;
		String userName = loginedUser.getUsername();
		String groupName = "";
		List<AppUser> user1 = userRepository.findByuserName(loginedUser.getUsername());
		boolean isSuperAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
		ModelAndView mav = new ModelAndView("vmAvailability");
		mav.addObject("pageTitle", "Report");
		if (isSuperAdmin) {
			mav.addObject("instanceNameList", repository.getInstanceNameAndInstanceIP());
		} else {

			for (AppUser appUser : user1) {
				groupName = appUser.getGroupName();
			}
			List<String> groups = new ArrayList<>();
			StringTokenizer token = new StringTokenizer(groupName, ",");
			while (token.hasMoreTokens()) {
				groups.add(token.nextToken());
			}

			mav.addObject("instanceNameList", repository.getInstanceNameByGroupvalue(groups));
		}

//		else if (isAdmin) {
//
//			for (AppUser appUser : user1) {
//				groupName = appUser.getGroupName();
//			}
//			List<String> groups = new ArrayList<>();
//			StringTokenizer token = new StringTokenizer(groupName, ",");
//			while (token.hasMoreTokens()) {
//				groups.add(token.nextToken());
//			}
//
//			mav.addObject("instanceNameList", repository.getInstanceNameByGroupvalue(groups));
//		} else {
//			List<Integer> li = approvalRepository.findByRequesterNameCustom(userName);
//
//			System.out.println(li.toString());
//			mav.addObject("instanceNameList", repository.getInstanceNameByRequestervalue(li, true));
//		}

		return mav;
	}

	@GetMapping("/vmAvailability")
	public ModelAndView vmAvailability(Principal principal) {
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		List<CloudInstance> instances = null;
		String userName = loginedUser.getUsername();
		String groupName = "";
		List<AppUser> user1 = userRepository.findByuserName(loginedUser.getUsername());
		boolean isSuperAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
		ModelAndView mav = new ModelAndView("vmAvailability2");
		mav.addObject("pageTitle", "Report");

		return mav;
	}

	@GetMapping("/vmUptime")
	public ModelAndView vmUptime(Principal principal) {
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		List<CloudInstance> instances = null;
		String userName = loginedUser.getUsername();
		String groupName = "";
		List<AppUser> user1 = userRepository.findByuserName(loginedUser.getUsername());
		boolean isSuperAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
		ModelAndView mav = new ModelAndView("vmUptime");
		mav.addObject("pageTitle", "Report");

		return mav;
	}

	@GetMapping("/vmUttilizattionreport")
	public ModelAndView vmUttilizattionreport(Principal principal) {
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		List<CloudInstance> instances = null;
		String userName = loginedUser.getUsername();
		String groupName = "";
		List<AppUser> user1 = userRepository.findByuserName(loginedUser.getUsername());
		boolean isSuperAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
		ModelAndView mav = new ModelAndView("vmUttilizattionreport");
		mav.addObject("pageTitle", "Report");
		if (isSuperAdmin) {
			mav.addObject("instanceNameList", repository.getInstanceName(true));
		} else {

			for (AppUser appUser : user1) {
				groupName = appUser.getGroupName();
			}
			List<String> groups = new ArrayList<>();
			StringTokenizer token = new StringTokenizer(groupName, ",");
			while (token.hasMoreTokens()) {
				groups.add(token.nextToken());
			}

			mav.addObject("instanceNameList", repository.getInstanceNameByGroup(groups, true));
		}

//		else if (isAdmin) {
//
//			for (AppUser appUser : user1) {
//				groupName = appUser.getGroupName();
//			}
//			List<String> groups = new ArrayList<>();
//			StringTokenizer token = new StringTokenizer(groupName, ",");
//			while (token.hasMoreTokens()) {
//				groups.add(token.nextToken());
//			}
//
//			mav.addObject("instanceNameList", repository.getInstanceNameByGroup(groups));
//		} else {
//			List<Integer> li = approvalRepository.findByRequesterNameCustom(userName);
//
//			System.out.println(li.toString());
//			mav.addObject("instanceNameList", repository.getInstanceNameByRequester(li, true));
//		}

		return mav;
	}

	@GetMapping("/vmStartStopReport")
	public ModelAndView vmStartStopReport(Principal principal) {
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		List<CloudInstance> instances = null;
		String userName = loginedUser.getUsername();
		String groupName = "";
		List<AppUser> user1 = userRepository.findByuserName(loginedUser.getUsername());
		boolean isSuperAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
		ModelAndView mav = new ModelAndView("vmStartStopLog");
		mav.addObject("pageTitle", "Report");
		// mav.addObject("instanceNameList", repository.getInstanceName());
		if (isSuperAdmin) {
			mav.addObject("instanceNameList", repository.getInstanceName(true));
		} else {

			for (AppUser appUser : user1) {
				groupName = appUser.getGroupName();
			}
			List<String> groups = new ArrayList<>();
			StringTokenizer token = new StringTokenizer(groupName, ",");
			while (token.hasMoreTokens()) {
				groups.add(token.nextToken());
			}

			mav.addObject("instanceNameList", repository.getInstanceNameByGroup(groups, true));
		}

//		else if (isAdmin) {
//
//			for (AppUser appUser : user1) {
//				groupName = appUser.getGroupName();
//			}
//			List<String> groups = new ArrayList<>();
//			StringTokenizer token = new StringTokenizer(groupName, ",");
//			while (token.hasMoreTokens()) {
//				groups.add(token.nextToken());
//			}
//
//			mav.addObject("instanceNameList", repository.getInstanceNameByGroup(groups));
//		} else {
//			List<Integer> li = approvalRepository.findByRequesterNameCustom(userName);
//
//			System.out.println(li.toString());
//			mav.addObject("instanceNameList", repository.getInstanceNameByRequester(li, true));
//		}

		// CloudInstance objEnt = new CloudInstance();
		// mav.addObject("objEnt", objEnt);
		return mav;
	}

	@GetMapping("/vmDailyUsage")
	public ModelAndView vmDailyUsage() {
		ModelAndView mav = new ModelAndView("vmDailyUsage");
		mav.addObject("pageTitle", "Report");
		mav.addObject("instanceNameList", repository.getInstanceName(true));
		// CloudInstance objEnt = new CloudInstance();
		// mav.addObject("objEnt", objEnt);
		return mav;
	}

	@PostMapping("/showVMStatusHistoryReport")
	public ModelAndView showVMStatusHistoryReport(@RequestParam String from_date, @RequestParam String to_date,
			@RequestParam String instanceIP) {
		System.out.println("In showVMStatusHistoryReport");

		ModelAndView mav = new ModelAndView("vmStatusHistoryReportData");
		try {

			List<String> instances = new ArrayList<>();
			StringTokenizer token = new StringTokenizer(instanceIP, ",");
			while (token.hasMoreTokens()) {
				instances.add(token.nextToken());
			}

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date fromDate = dateFormat.parse(from_date + " 00:00:00"); // Adjust time to include the whole day
			Date toDate = dateFormat.parse(to_date + " 23:59:59");
			mav.addObject("fdate", from_date);
			mav.addObject("tdate", to_date);
			mav.addObject("activityLog", statusHistoryRepository.vmStatusHistoryData(fromDate, toDate, instances));

		} catch (Exception e) {
			System.out.println("Exception = " + e);
		}

		return mav;
	}

	@PostMapping("/showVMActivityReport")
	public ModelAndView showVMActivityReport(@RequestParam String from_date, @RequestParam String to_date,
			@RequestParam String instanceIP) {

		ModelAndView mav = new ModelAndView("vmActivityReportData");
		try {

			List<String> instances = new ArrayList<>();
			StringTokenizer token = new StringTokenizer(instanceIP, ",");
			while (token.hasMoreTokens()) {
				String str = token.nextToken();

				if (!str.isEmpty() && str.charAt(str.length() - 1) == '_') {
					str = str.substring(0, str.length() - 1); // Remove last character
				}

				instances.add(str);
			}

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date fromDate = dateFormat.parse(from_date + " 00:00:00"); // Adjust time to include the whole day
			Date toDate = dateFormat.parse(to_date + " 23:59:59");
			mav.addObject("fdate", from_date);
			mav.addObject("tdate", to_date);
			mav.addObject("activityLog", logRepository.getVmActivityLogOrderByIdDesc(fromDate, toDate, instances));

		} catch (Exception e) {
			System.out.println("Exception = " + e);
		}

		return mav;
	}

	@PostMapping("/vmAvailabilityReportData")
	public ModelAndView vmAvailabilityReportData(@RequestParam String from_date, @RequestParam String to_date,
			@RequestParam String instanceIP) {

		ModelAndView mav = new ModelAndView("vmAvailabilityDataReport");
		try {

			System.out.println("Instance ips = " + instanceIP);
			List<String> ips = new ArrayList<>();
			StringTokenizer token = new StringTokenizer(instanceIP, ",");
			while (token.hasMoreTokens()) {
				ips.add(token.nextToken());
			}
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date fromDate = dateFormat.parse(from_date + " 00:00:00"); // Adjust time to include the whole day
			Date toDate = dateFormat.parse(to_date + " 23:59:59");
			mav.addObject("fdate", from_date);
			mav.addObject("tdate", to_date);
			mav.addObject("data", nodeAvailabilityRepository.vmAvailabilityReportData(fromDate, toDate, ips));

//			highchart graph here

			List<NodeAvailability> data = nodeAvailabilityRepository.vmAvailabilityReportData(fromDate, toDate, ips);

			// Prepare data for uptimePercent
			Map<String, List<Map<String, Object>>> uptimeData = new HashMap<>();
			// Prepare data for downtimePercent
			Map<String, List<Map<String, Object>>> downtimeData = new HashMap<>();

			for (NodeAvailability record : data) {
				String nodeIp = record.getNodeIP();
				Double uptimePercent = record.getUptimePercent();
				Double downtimePercent = (double) record.getDowntimePercent();
				Timestamp eventTimestamp = new Timestamp(record.getEventTime().getTime());

				Map<String, Object> uptimeDataPoint = new HashMap<>();
				uptimeDataPoint.put("x", eventTimestamp.getTime()); // Convert Timestamp to milliseconds
				uptimeDataPoint.put("y", uptimePercent);

				Map<String, Object> downtimeDataPoint = new HashMap<>();
				downtimeDataPoint.put("x", eventTimestamp.getTime()); // Convert Timestamp to milliseconds
				downtimeDataPoint.put("y", downtimePercent);

				uptimeData.computeIfAbsent(nodeIp, k -> new ArrayList<>()).add(uptimeDataPoint);
				downtimeData.computeIfAbsent(nodeIp, k -> new ArrayList<>()).add(downtimeDataPoint);
			}

			// Convert grouped data into series format for uptime chart
			List<Map<String, Object>> uptimeSeriesData = new ArrayList<>();
			for (Map.Entry<String, List<Map<String, Object>>> entry : uptimeData.entrySet()) {
				Map<String, Object> series = new HashMap<>();
				series.put("name", entry.getKey() + " Uptime");
				series.put("data", entry.getValue());
				uptimeSeriesData.add(series);
			}

			// Convert grouped data into series format for downtime chart
			List<Map<String, Object>> downtimeSeriesData = new ArrayList<>();
			for (Map.Entry<String, List<Map<String, Object>>> entry : downtimeData.entrySet()) {
				Map<String, Object> series = new HashMap<>();
				series.put("name", entry.getKey() + " Downtime");
				series.put("data", entry.getValue());
				downtimeSeriesData.add(series);
			}

			mav.addObject("uptimeSeriesData", uptimeSeriesData);
			mav.addObject("downtimeSeriesData", downtimeSeriesData);

		} catch (Exception e) {
			System.out.println("Exception = " + e);
		}

		return mav;
	}

	// Vm Availability as per working hours
	@PostMapping("/vmAvailabilityData")
	public ModelAndView vmAvailabilityData(@RequestParam String from_date, @RequestParam String to_date,
			Principal principal) {

		ModelAndView mav = new ModelAndView("vmAvailabilityDataReport2");
		JSONArray array = null;
		JSONArray arrayList = new JSONArray();
		int sr = 0;
		List<Object[]> dataList;

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date fromDate = dateFormat.parse(from_date + " 00:00:00"); // Adjust time to include the whole day
			Date toDate = dateFormat.parse(to_date + " 23:59:59");
			mav.addObject("fdate", from_date);
			mav.addObject("tdate", to_date);
			System.out.println("fdate = " + from_date + "\ntdate = " + to_date);

			// start datt

			// start

//			Authentication authentication = (Authentication) principal;
//			User loginedUser = (User) ((Authentication) principal).getPrincipal();
//			List<CloudInstance> instances = null;
//			String userName = loginedUser.getUsername();
//			String groupName = "";
//			String VmName = "";
//			List<AppUser> user1 = userRepository.findByuserName(loginedUser.getUsername());
//			boolean isSuperAdmin = authentication.getAuthorities().stream()
//					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));
//
//			boolean isAdmin = authentication.getAuthorities().stream()
//					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
////		ModelAndView mav = new ModelAndView("Disk_Health_Utilization");
////		mav.addObject("pageTitle", "Report");
//			if (isSuperAdmin) {
////			mav.addObject("instanceNameList", repository.getInstanceName());
//
////				dataList = kVMDriveDetailsRepository.findBykVMDetails();
//
//				dataObject = vmLiveStatusHistoryRepository.showVmAvailabilityReport(fromDate, toDate);
//			} else {
//
//				for (AppUser appUser : user1) {
//					groupName = appUser.getGroupName();
//				}
//				List<String> groups = new ArrayList<>();
//				StringTokenizer token = new StringTokenizer(groupName, ",");
//				while (token.hasMoreTokens()) {
//					groups.add(token.nextToken());
//				}
//
//				List<Object[]> vm = repository.getInstanceNameByGroup(groups, true);
//
//				for (Object[] vmlist : vm) {
////					System.out.println("vmlist 0 :" + vmlist[0].toString());
////					System.out.println("vmlist 1 :" + vmlist[1].toString());
//
//					VmName = vmlist[1].toString();
//				}
//
//				List<String> vmgroups = new ArrayList<>();
//				StringTokenizer vmtoken = new StringTokenizer(VmName, ",");
//				while (vmtoken.hasMoreTokens()) {
//					vmgroups.add(vmtoken.nextToken());
//				}
//
////				dataList = kVMDriveDetailsRepository.findBykVMDetails(vmgroups);
//				dataObject = vmLiveStatusHistoryRepository.showVmAvailabilityReport(fromDate, toDate, vmgroups);
//			}

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
				dataList = vmLiveStatusHistoryRepository.showVmAvailabilityReport(fromDate, toDate);
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

				dataList = vmLiveStatusHistoryRepository.showVmAvailabilityReport(fromDate, toDate, vmGroups);
			}

			// end dataa
//			List<Object[]> dataObject = vmLiveStatusHistoryRepository.showVmAvailabilityReport(fromDate, toDate);

			for (Object[] obj : dataList) {
				sr++;
				array = new JSONArray();

				double uptimeDouble = Double.parseDouble(obj[1].toString().trim());
				// Convert to long
				long uptime = (long) uptimeDouble;
//				Long uptime = Long.parseLong(data[1].toString().trim());
				double DowntimeDouble = Double.parseDouble(obj[2].toString().trim());
				// Convert to long
				long downtime = (long) DowntimeDouble;
//				Long downtime = Long.parseLong(data[2].toString().trim());

				Long totalTime = uptime + downtime;

				// Calculate Uptime (%) and Downtime (%)
				double uptimePercentage = (uptime * 100.0) / totalTime;
				double downtimePercentage = (downtime * 100.0) / totalTime;

//				struig cal

				int day = (int) TimeUnit.SECONDS.toDays(uptime);
				long hours = TimeUnit.SECONDS.toHours(uptime) - (day * 24);
				long minute = TimeUnit.SECONDS.toMinutes(uptime) - (TimeUnit.SECONDS.toHours(uptime) * 60);
				long second = TimeUnit.SECONDS.toSeconds(uptime) - (TimeUnit.SECONDS.toMinutes(uptime) * 60);
				String var_uptime = day + " Days, " + hours + " Hours, " + minute + " Minutes, " + second + " Seconds";

				day = (int) TimeUnit.SECONDS.toDays(downtime);
				hours = TimeUnit.SECONDS.toHours(downtime) - (day * 24);
				minute = TimeUnit.SECONDS.toMinutes(downtime) - (TimeUnit.SECONDS.toHours(downtime) * 60);
				second = TimeUnit.SECONDS.toSeconds(downtime) - (TimeUnit.SECONDS.toMinutes(downtime) * 60);
				String var_Downtime = day + " Days, " + hours + " Hours, " + minute + " Minutes, " + second
						+ " Seconds";

				DecimalFormat df = new DecimalFormat("0.00");

				array.put(sr);
				array.put(obj[0]);
				array.put(var_uptime);
				array.put(var_Downtime);
				array.put(df.format(uptimePercentage));
				array.put(df.format(downtimePercentage));
				arrayList.put(array);
			}

			mav.addObject("data", arrayList);
			System.out.println("VM Availability report ::: " + arrayList);

		} catch (Exception e) {
			System.out.println("Exception = " + e);
		}

		return mav;
	}
	// End VM Availability as per working hours

	@PostMapping("/vmUptimeData")
	public ModelAndView vmUptimeData(@RequestParam String from_date, @RequestParam String to_date) {

		ModelAndView mav = new ModelAndView("vmUptimeDataReport");
		JSONArray array = null;
		JSONArray arrayList = new JSONArray();
		int sr = 0;

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date fromDate = dateFormat.parse(from_date + " 00:00:00"); // Adjust time to include the whole day
			Date toDate = dateFormat.parse(to_date + " 23:59:59");
			mav.addObject("fdate", from_date);
			mav.addObject("tdate", to_date);
			System.out.println("fdate = " + from_date + "\ntdate = " + to_date);
			List<Object[]> dataObject = vmLiveStatusHistoryRepository.vmUptimeData(fromDate, toDate);

			for (Object[] obj : dataObject) {
				sr++;
				array = new JSONArray();
				array.put(sr);
				array.put(obj[0].toString());
				array.put(obj[1].toString());
				array.put(obj[2].toString());
				array.put(obj[3].toString());
				array.put(obj[4].toString());
				array.put(obj[5].toString());

				arrayList.put(array);
			}

			mav.addObject("data", arrayList);
			System.out.println("VM Uptime report ::: " + arrayList);

		} catch (Exception e) {
			System.out.println("Exception = " + e);
		}

		return mav;
	}

	@GetMapping("/vmUtilizationReportData")
	public ModelAndView vmUtilizationReportData(@RequestParam String from_date, @RequestParam String to_date,
			@RequestParam String instanceIP) {

		ModelAndView mav = new ModelAndView("vmUttilizattionreport_view");
		try {

			System.out.println("Instance ips s = " + instanceIP);
			List<String> ips = new ArrayList<>();
			StringTokenizer token = new StringTokenizer(instanceIP, ",");
			while (token.hasMoreTokens()) {
				ips.add(token.nextToken());
			}
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date fromDate = dateFormat.parse(from_date + " 00:00:00"); // Adjust time to include the whole day
			Date toDate = dateFormat.parse(to_date + " 23:59:59");
			mav.addObject("fdate", from_date);
			mav.addObject("tdate", to_date);
			mav.addObject("data", nodeUtilizationRepository.vmAvailabilityReportData(fromDate, toDate, ips));

//			highchart

			List<CloudInstanceDailyUsageByAgent> data = nodeUtilizationRepository.vmAvailabilityReportData(fromDate,
					toDate, ips);

			// Prepare data for the chart
			Map<String, List<Map<String, Object>>> usageData = new HashMap<>();

			for (CloudInstanceDailyUsageByAgent record : data) {
				String vmName = record.getVmname();
				Long totalDuration = record.getTotalDuration();
				Date eventDate = record.getEventDate();
				String timeInHMS = record.getTimeInHMS();

				Map<String, Object> dataPoint = new HashMap<>();
				dataPoint.put("x", eventDate.getTime()); // Convert Date to milliseconds
				dataPoint.put("y", totalDuration);
				dataPoint.put("timeInHMS", timeInHMS);

				usageData.computeIfAbsent(vmName, k -> new ArrayList<>()).add(dataPoint);
			}

			// Convert grouped data into series format for the chart
			List<Map<String, Object>> seriesData = new ArrayList<>();
			for (Map.Entry<String, List<Map<String, Object>>> entry : usageData.entrySet()) {
				Map<String, Object> series = new HashMap<>();
				series.put("name", entry.getKey());
				series.put("data", entry.getValue());
				seriesData.add(series);
			}

			mav.addObject("seriesData", seriesData);

		} catch (Exception e) {
			System.out.println("Exception = " + e);
		}

		return mav;
	}

	@GetMapping("/vmUtilizationReport")
	public @ResponseBody String vmUtilizationReport(@RequestParam String fromDate, @RequestParam String toDate,
			@RequestParam String vm) {
		JSONArray arr2 = new JSONArray();
		JSONArray obj = null;

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date fDate = null;
		Date tDate = null;
		try {
			fDate = dateFormat.parse(fromDate + " 00:00:00");
			tDate = dateFormat.parse(toDate + " 23:59:59");
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // Adjust time to include the whole day

//		utilization
		try {

//			System.out.println("F date = " + fDate + "\nToDate = " + tDate + "\nvm = " + vm);
			List<CloudInstanceDailyUsageByAgent> li = nodeUtilizationRepository.vmAvailabilityReport(fDate, tDate, vm);
			System.out.println("list size = " + li.size());
			JSONArray arr = new JSONArray();
			int sr = 0;
			for (CloudInstanceDailyUsageByAgent data : li) {
				obj = new JSONArray();
				sr++;
				obj.put(sr);
				obj.put(data.getVmname());
				obj.put(data.getTimeInHMS());
				obj.put(data.getEventDate());
				obj.put(data.getInstance_id().getPhysicalServerIP());

				arr.put(obj);
			}
			arr2.put(arr);
		} catch (Exception e) {
			System.out.println("Exception occured while fetching vm utilization report = " + e);
		}

//		health history

		try {

//			System.out.println("F date = " + fDate + "\nToDate = " + tDate + "\nvm = " + vm);
			List<Object[]> vmHealthHistoryReportTable = vmHealthRepository.vmHealthHistoryReportData2(fDate, tDate, vm);
			JSONArray arr = new JSONArray();
			int sr = 0;
			for (Object[] data : vmHealthHistoryReportTable) {
				obj = new JSONArray();
				sr++;
				obj.put(sr);
				obj.put(data[0]);
				obj.put(data[1]);
				obj.put(data[2]);
				obj.put(data[3]);
				obj.put(data[4]);
				obj.put(data[5]);
				obj.put(data[6]);
				obj.put(data[7]);
				obj.put(data[8]);
				obj.put(data[9]);

				arr.put(obj);
			}
			arr2.put(arr);
		} catch (Exception e) {
			System.out.println("Exception occured while fetching vm health history report = " + e);
		}

//		VM Availability

		try {

//			System.out.println("F date = " + fDate + "\nToDate = " + tDate + "\nvm = " + vm);
			List<NodeAvailability> getVmActivityLogTable = nodeAvailabilityRepository.vmAvailabilityReportData2(fDate,
					tDate, vm);

			JSONArray arr = new JSONArray();
			int sr = 0;

			for (NodeAvailability cloudInstanceLog : getVmActivityLogTable) {
				obj = new JSONArray();

				obj.put(cloudInstanceLog.getID());
				obj.put(cloudInstanceLog.getNodeIP());
				obj.put(cloudInstanceLog.getUptimePercent());
				obj.put(cloudInstanceLog.getUptimeStr());
				obj.put(cloudInstanceLog.getDowntimePercent());
				obj.put(cloudInstanceLog.getDowntimeStr());
				obj.put(cloudInstanceLog.getEventTime());

				arr.put(obj);
			}

			arr2.put(arr);
		} catch (Exception e) {
			System.out.println("Exception occured while fetching vm health history report = " + e);
		}

//		cpu threshhold

		try {

//			System.out.println("F date = " + fDate + "\nToDate = " + tDate + "\nvm = " + vm);
			List<CloudInstanceCpuThresholdHistory> vmCpuThresholdReportDataTable = cpuThresholdHistoryRepository
					.vmCpuThresholdReportData2(fDate, tDate, vm);
			JSONArray arr = new JSONArray();
			int sr = 0;

			for (CloudInstanceCpuThresholdHistory cloudInstanceCpuThresholdHistory : vmCpuThresholdReportDataTable) {
				obj = new JSONArray();
				sr++;
				obj.put(sr);
				obj.put(cloudInstanceCpuThresholdHistory.getVmName());
				obj.put(cloudInstanceCpuThresholdHistory.getNodeIp());
				obj.put(cloudInstanceCpuThresholdHistory.getCpuUtilization());
				obj.put(cloudInstanceCpuThresholdHistory.getCpuThreshold());
				obj.put(cloudInstanceCpuThresholdHistory.getCpuStatus());
				obj.put(cloudInstanceCpuThresholdHistory.getEventTimestamp());

				arr.put(obj);
			}

			arr2.put(arr);
		} catch (Exception e) {
			System.out.println("Exception occured while fetching vm health history report = " + e);
		}

//		memory threshhold

		try {

//			System.out.println("F date = " + fDate + "\nToDate = " + tDate + "\nvm = " + vm);
			List<CloudInstanceMemoryThresholdHistory> vmMemoryThresholdReportDataTable = memoryThresholdHistoryRepository
					.vmMemoryThresholdReportData2(fDate, tDate, vm);
			JSONArray arr = new JSONArray();
			int sr = 0;

			for (CloudInstanceMemoryThresholdHistory cloudInstancememoryThresholdHistory : vmMemoryThresholdReportDataTable) {
				obj = new JSONArray();
				sr++;
				obj.put(sr);
				obj.put(cloudInstancememoryThresholdHistory.getVmName());
				obj.put(cloudInstancememoryThresholdHistory.getNodeIp());
				obj.put(cloudInstancememoryThresholdHistory.getMemoryUtilization());
				obj.put(cloudInstancememoryThresholdHistory.getMemoryThreshold());
				obj.put(cloudInstancememoryThresholdHistory.getMemoryStatus());
				obj.put(cloudInstancememoryThresholdHistory.getEventTimestamp());

				arr.put(obj);
			}

			arr2.put(arr);
		} catch (Exception e) {
			System.out.println("Exception occured while fetching vm health history report = " + e);
		}

		System.out.println("Final json for vm utilization = " + arr2);
		String jsonString = arr2.toString();
		return jsonString;
	}

	@GetMapping("/vmStatusReport")
	public ModelAndView vmStatusHistoryReport() {
		ModelAndView mav = new ModelAndView("vmStatusReport");
		mav.addObject("pageTitle", "Report");
		mav.addObject("instanceNameList", repository.getInstanceNameAndInstanceIP());
		return mav;
	}

	@PostMapping("/vmStatusReportData")
	public ModelAndView vmStatusReportData(@RequestParam String from_date, @RequestParam String to_date,
			@RequestParam String instanceIP) {

		ModelAndView mav = new ModelAndView("vmStatusReportData");
		List<List<Object>> result = new ArrayList<>();
		int sr = 0;

		try {
			System.out.println("Instance ips = " + instanceIP);
			List<String> ips = new ArrayList<>();
			StringTokenizer token = new StringTokenizer(instanceIP, ",");
			while (token.hasMoreTokens()) {
				ips.add(token.nextToken());
			}

			String[] ip_address = instanceIP.split(",");
			List<String> list = Arrays.asList(ip_address);
			String ip_data = list.toString().replace("[", "").replace("]", "").replace(",", "','").replace(" ", "");
			System.out.println("ip list = " + ip_data);

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date fromDate = dateFormat.parse(from_date + " 00:00:00");
			Date toDate = dateFormat.parse(to_date + " 23:59:59");

			List<Object[]> obj = nodeStatusHistoryRepository.vmStatusReportData(fromDate, toDate, ip_data);
			for (Object[] data : obj) {
				sr++;
				List<Object> arr = new ArrayList<>();
				arr.add(sr);
				arr.add(data[0]);
				arr.add(data[1]);
				arr.add(data[2]);
				arr.add(data[3]);

				result.add(arr);
			}

			mav.addObject("fdate", from_date);
			mav.addObject("tdate", to_date);
			System.out.println("List result for vm status = " + result);
			mav.addObject("data", result);

		} catch (Exception e) {
			System.out.println("Exception = " + e);
		}

		return mav;
	}

	@GetMapping("/vmHealthHistoryReport")
	public ModelAndView vmHealthHistoryReport(Principal principal) {
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		List<CloudInstance> instances = null;
		String userName = loginedUser.getUsername();
		String groupName = "";
		List<AppUser> user1 = userRepository.findByuserName(loginedUser.getUsername());
		boolean isSuperAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
		ModelAndView mav = new ModelAndView("vmHealthHistoryReport");
		mav.addObject("pageTitle", "Report");
		if (isSuperAdmin) {
			mav.addObject("instanceNameList", repository.getInstanceName(true));
		} else {

			for (AppUser appUser : user1) {
				groupName = appUser.getGroupName();
			}
			List<String> groups = new ArrayList<>();
			StringTokenizer token = new StringTokenizer(groupName, ",");
			while (token.hasMoreTokens()) {
				groups.add(token.nextToken());
			}

			mav.addObject("instanceNameList", repository.getInstanceNameByGroup(groups, true));
		}

//		else if (isAdmin) {
//
//			for (AppUser appUser : user1) {
//				groupName = appUser.getGroupName();
//			}
//			List<String> groups = new ArrayList<>();
//			StringTokenizer token = new StringTokenizer(groupName, ",");
//			while (token.hasMoreTokens()) {
//				groups.add(token.nextToken());
//			}
//
//			mav.addObject("instanceNameList", repository.getInstanceNameByGroup(groups));
//		} else {
//			List<Integer> li = approvalRepository.findByRequesterNameCustom(userName);
//
//			System.out.println(li.toString());
//			mav.addObject("instanceNameList", repository.getInstanceNameByRequester(li, true));
//		}
		return mav;
	}

	@PostMapping("/vmHealthHistoryReportData")
	public ModelAndView vmHealthHistoryReportData(@RequestParam String from_date, @RequestParam String to_date,
			@RequestParam String instanceIP) {

		ModelAndView mav = new ModelAndView("vmHealthHistoryReportData");
		List<List<Object>> result = new ArrayList<>();
		int sr = 0;

		try {
			System.out.println("Instance name = " + instanceIP);
			List<String> ips = new ArrayList<>();
			StringTokenizer token = new StringTokenizer(instanceIP, ",");
			while (token.hasMoreTokens()) {
				ips.add(token.nextToken());
			}

			String[] ip_address = instanceIP.split(",");
			List<String> list = Arrays.asList(ip_address);

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date fromDate = dateFormat.parse(from_date + " 00:00:00");
			Date toDate = dateFormat.parse(to_date + " 23:59:59");

			DecimalFormat df = new DecimalFormat("#.##");
			List<Object[]> obj = vmHealthRepository.vmHealthHistoryReportData(fromDate, toDate, list);
			for (Object[] data : obj) {
				sr++;
				List<Object> arr = new ArrayList<>();

				String memUtilization = df.format(data[2]);

				arr.add(sr);
				arr.add(data[0]);
				arr.add(data[1]);
				arr.add(memUtilization);
				arr.add(data[3]);
				arr.add(data[4]);
				arr.add(data[5].toString().contains("-") ? data[5].toString().replace("-", "Exceeded Memory ")
						: data[5]);

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String formattedTimestamp = sdf.format(data[6]);
				arr.add(formattedTimestamp);
//				arr.add(data[7]);
//				arr.add(data[8]);
//				arr.add(data[9]);

				result.add(arr);
			}

			mav.addObject("fdate", from_date);
			mav.addObject("tdate", to_date);
			System.out.println("List result for vm status = " + result);
			mav.addObject("data", result);

			// Group data by NODE_IP
			Map<String, List<Map<String, Object>>> groupedcpuData = new HashMap<>();
			Map<String, List<Map<String, Object>>> groupedMemoryData = new HashMap<>();
			for (Object[] record : obj) {
				String VMNAME = (String) record[0];
				Double cpuUtilization = (Double) record[1];
				Double MemoryyUtilization = (Double) record[2];
				Timestamp eventTimestamp = (Timestamp) record[6];

				Map<String, Object> dataCPUPoint = new HashMap<>();
				dataCPUPoint.put("x", eventTimestamp.getTime()); // Convert Timestamp to milliseconds
				dataCPUPoint.put("y", cpuUtilization);

				Map<String, Object> datamemoryPoint = new HashMap<>();
				datamemoryPoint.put("x", eventTimestamp.getTime()); // Convert Timestamp to milliseconds
				datamemoryPoint.put("y", MemoryyUtilization);

				groupedcpuData.computeIfAbsent(VMNAME, k -> new ArrayList<>()).add(dataCPUPoint);
				groupedMemoryData.computeIfAbsent(VMNAME, k -> new ArrayList<>()).add(datamemoryPoint);
			}

			// Convert grouped data into series format
			List<Map<String, Object>> seriescpuData = new ArrayList<>();
			for (Map.Entry<String, List<Map<String, Object>>> entry : groupedcpuData.entrySet()) {
				Map<String, Object> series = new HashMap<>();
				series.put("name", entry.getKey());
				series.put("data", entry.getValue());
				seriescpuData.add(series);
			}

			List<Map<String, Object>> memorycpuData = new ArrayList<>();
			for (Map.Entry<String, List<Map<String, Object>>> entry : groupedMemoryData.entrySet()) {
				Map<String, Object> series = new HashMap<>();
				series.put("name", entry.getKey());
				series.put("data", entry.getValue());
				memorycpuData.add(series);
			}

			mav.addObject("CPUUtilizationSeriesData", seriescpuData);
			mav.addObject("MemorytilizationSeriesData", memorycpuData);

		} catch (Exception e) {
			System.out.println("Exception = " + e);
		}

		return mav;
	}

	@GetMapping("/vmMemoryThresholdReport")
	public ModelAndView vmMemoryThresholdReport(Principal principal) {
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		List<CloudInstance> instances = null;
		String userName = loginedUser.getUsername();
		String groupName = "";
		List<AppUser> user1 = userRepository.findByuserName(loginedUser.getUsername());
		boolean isSuperAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
		ModelAndView mav = new ModelAndView("vmMemoryThresholdHistory");
		mav.addObject("pageTitle", "Report");
		if (isSuperAdmin) {
			mav.addObject("instanceNameList", repository.getInstanceName(true));
		} else {

			for (AppUser appUser : user1) {
				groupName = appUser.getGroupName();
			}
			List<String> groups = new ArrayList<>();
			StringTokenizer token = new StringTokenizer(groupName, ",");
			while (token.hasMoreTokens()) {
				groups.add(token.nextToken());
			}

			mav.addObject("instanceNameList", repository.getInstanceNameByGroup(groups, true));
		}

//		else if (isAdmin) {
//
//			for (AppUser appUser : user1) {
//				groupName = appUser.getGroupName();
//			}
//			List<String> groups = new ArrayList<>();
//			StringTokenizer token = new StringTokenizer(groupName, ",");
//			while (token.hasMoreTokens()) {
//				groups.add(token.nextToken());
//			}
//
//			mav.addObject("instanceNameList", repository.getInstanceNameByGroup(groups));
//		} else {
//			List<Integer> li = approvalRepository.findByRequesterNameCustom(userName);
//
//			System.out.println(li.toString());
//			mav.addObject("instanceNameList", repository.getInstanceNameByRequester(li, true));
//		}

		return mav;
	}

	@PostMapping("/vmMemoryThresholdReportData")
	public ModelAndView vmMemoryThresholdReportData(@RequestParam String from_date, @RequestParam String to_date,
			@RequestParam String instanceIP) {

		ModelAndView mav = new ModelAndView("vmMemoryThresholdReportData");
		try {

			System.out.println("Instance name = " + instanceIP);
			List<String> ips = new ArrayList<>();
			StringTokenizer token = new StringTokenizer(instanceIP, ",");
			while (token.hasMoreTokens()) {
				ips.add(token.nextToken());
			}
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date fromDate = dateFormat.parse(from_date + " 00:00:00"); // Adjust time to include the whole day
			Date toDate = dateFormat.parse(to_date + " 23:59:59");
			mav.addObject("fdate", from_date);
			mav.addObject("tdate", to_date);
			mav.addObject("data", memoryThresholdHistoryRepository.vmMemoryThresholdReportData(fromDate, toDate, ips));

//			highchart

			List<CloudInstanceMemoryThresholdHistory> data = memoryThresholdHistoryRepository
					.vmMemoryThresholdReportData(fromDate, toDate, ips);

			// Prepare data for the chart
			Map<String, List<Map<String, Object>>> memoryData = new HashMap<>();

			for (CloudInstanceMemoryThresholdHistory record : data) {
				String vmName = record.getVmName();
				Double memoryUtilization = record.getMemoryUtilization();
				Timestamp eventTimestamp = record.getEventTimestamp();
				String memoryStatus = record.getMemoryStatus();

				Map<String, Object> dataPoint = new HashMap<>();
				dataPoint.put("x", eventTimestamp.getTime()); // Convert Timestamp to milliseconds
				dataPoint.put("y", memoryUtilization);
				dataPoint.put("memoryStatus", memoryStatus);

				memoryData.computeIfAbsent(vmName, k -> new ArrayList<>()).add(dataPoint);
			}

			// Convert grouped data into series format for the chart
			List<Map<String, Object>> seriesData = new ArrayList<>();
			for (Map.Entry<String, List<Map<String, Object>>> entry : memoryData.entrySet()) {
				Map<String, Object> series = new HashMap<>();
				series.put("name", entry.getKey());
				series.put("data", entry.getValue());
				seriesData.add(series);
			}

			mav.addObject("seriesData", seriesData);

		} catch (Exception e) {
			System.out.println("Exception = " + e);
		}

		return mav;
	}

	@GetMapping("/vmCpuThresholdReport")
	public ModelAndView vmCpuThresholdReport(Principal principal) {
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		List<CloudInstance> instances = null;
		String userName = loginedUser.getUsername();
		String groupName = "";
		List<AppUser> user1 = userRepository.findByuserName(loginedUser.getUsername());
		boolean isSuperAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
		ModelAndView mav = new ModelAndView("vmCpuThresholdHistory");

		mav.addObject("pageTitle", "Report");
		if (isSuperAdmin) {
			mav.addObject("instanceNameList", repository.getInstanceName(true));
		} else {

			for (AppUser appUser : user1) {
				groupName = appUser.getGroupName();
			}
			List<String> groups = new ArrayList<>();
			StringTokenizer token = new StringTokenizer(groupName, ",");
			while (token.hasMoreTokens()) {
				groups.add(token.nextToken());
			}

			mav.addObject("instanceNameList", repository.getInstanceNameByGroup(groups, true));
		}

//		else if (isAdmin) {
//
//			for (AppUser appUser : user1) {
//				groupName = appUser.getGroupName();
//			}
//			List<String> groups = new ArrayList<>();
//			StringTokenizer token = new StringTokenizer(groupName, ",");
//			while (token.hasMoreTokens()) {
//				groups.add(token.nextToken());
//			}
//
//			mav.addObject("instanceNameList", repository.getInstanceNameByGroup(groups));
//		} else {
//			List<Integer> li = approvalRepository.findByRequesterNameCustom(userName);
//
//			System.out.println(li.toString());
//			mav.addObject("instanceNameList", repository.getInstanceNameByRequester(li, true));
//		}

		return mav;
	}

	@PostMapping("/vmCpuThresholdReportData")
	public ModelAndView vmCpuThresholdReportData(@RequestParam String from_date, @RequestParam String to_date,
			@RequestParam String instanceIP) {

		ModelAndView mav = new ModelAndView("vmCpuThresholdReportData");
		try {

			System.out.println("Instance name = " + instanceIP);
			List<String> ips = new ArrayList<>();
			StringTokenizer token = new StringTokenizer(instanceIP, ",");
			while (token.hasMoreTokens()) {
				ips.add(token.nextToken());
			}
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date fromDate = dateFormat.parse(from_date + " 00:00:00"); // Adjust time to include the whole day
			Date toDate = dateFormat.parse(to_date + " 23:59:59");
			mav.addObject("fdate", from_date);
			mav.addObject("tdate", to_date);
			mav.addObject("data", cpuThresholdHistoryRepository.vmCpuThresholdReportData(fromDate, toDate, ips));

//			highchart

			List<CloudInstanceCpuThresholdHistory> data = cpuThresholdHistoryRepository
					.vmCpuThresholdReportData(fromDate, toDate, ips);

			// Group data by VM name
			// Prepare data for the chart
			Map<String, List<Map<String, Object>>> cpuData = new HashMap<>();

			for (CloudInstanceCpuThresholdHistory record : data) {
				String vmName = record.getVmName();
				Double cpuUtilization = record.getCpuUtilization();
				Timestamp eventTimestamp = record.getEventTimestamp();
				String cpuStatus = record.getCpuStatus();

				Map<String, Object> dataPoint = new HashMap<>();
				dataPoint.put("x", eventTimestamp.getTime()); // Convert Timestamp to milliseconds
				dataPoint.put("y", cpuUtilization);
				dataPoint.put("cpuStatus", cpuStatus);

				cpuData.computeIfAbsent(vmName, k -> new ArrayList<>()).add(dataPoint);
			}

			// Convert grouped data into series format for the chart
			List<Map<String, Object>> seriesData = new ArrayList<>();
			for (Map.Entry<String, List<Map<String, Object>>> entry : cpuData.entrySet()) {
				Map<String, Object> series = new HashMap<>();
				series.put("name", entry.getKey());
				series.put("data", entry.getValue());
				seriesData.add(series);
			}

			mav.addObject("seriesData", seriesData);

		} catch (Exception e) {
			System.out.println("Exception = " + e);
		}

		return mav;
	}

	@PostMapping("/showVMStartStopReport")
	public ModelAndView showVMStartStopReport(@RequestParam String from_date, @RequestParam String to_date,
			@RequestParam List<Integer> instanceName) {

		System.out.println("in start stop controller report");
		ModelAndView mav = new ModelAndView("vmStartStopReportData");
		try {
			List<CloudInstance> instances = repository.findAllById(instanceName);

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date fromDate = dateFormat.parse(from_date + " 00:00:00"); // Adjust time to include the whole day
			Date toDate = dateFormat.parse(to_date + " 23:59:59");
			mav.addObject("fdate", from_date);
			mav.addObject("tdate", to_date);
			mav.addObject("activityLog", logRepository.showVMStartStopReport(fromDate, toDate, instances));

//			highchart

			List<CloudInstanceUsage> data = logRepository.showVMStartStopReport(fromDate, toDate, instances);

			// Process data into chartData JSON format
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode chartData = mapper.createArrayNode();

			for (CloudInstanceUsage usage : data) {
				ObjectNode point = mapper.createObjectNode();
				point.put("name", usage.getInstance_id().getInstance_name());
				point.put("x", usage.getEvent_time().getTime());
				point.put("y", "start".equals(usage.getEvent_type()) ? 100 : 5);
				point.put("event_type", usage.getEvent_type());
//				point.put("color", "start".equals(usage.getEvent_type()) ? "green" : "red");

				chartData.add(point);
			}
			mav.addObject("StartstopChartdata", chartData);
//			List<CloudInstanceUsage> data = logRepository.showVMStartStopReport(fromDate, toDate, instance_id);
//
//			// Prepare data for the chart
//			List<Map<String, Object>> chartData = new ArrayList<>();
//
//			// Group data by event_time and set value and color based on event_type
//			data.forEach(record -> {
//				String eventType = record.getEvent_type();
//				long eventTime = record.getEvent_time().getTime(); // Convert Timestamp to milliseconds
//				int value = "start".equalsIgnoreCase(eventType) ? 100 : 50;
//				String color = "start".equalsIgnoreCase(eventType) ? "green" : "red";
//
//				// Add to chart data
//				Map<String, Object> chartPoint = new HashMap<>();
//				chartPoint.put("name", new java.util.Date(eventTime).toString()); // Format the timestamp as a string
//				chartPoint.put("x", eventTime);
//				chartPoint.put("y", value);
//				chartPoint.put("color", color);
//				chartData.add(chartPoint);
//			});
//
//			mav.addObject("chartData", chartData);

		} catch (Exception e) {
			System.out.println("Exception = " + e);
		}

		return mav;
	}

	@GetMapping("/showVMDailyUsageReport")
	public ModelAndView showVMDailyUsageReport(@RequestParam String from_date, @RequestParam String to_date,
			@RequestParam String instance_id) {

		System.out.println("in showVMDailyUsageReport controller report = " + instance_id);
		ModelAndView mav = new ModelAndView("vmDailyUsageReportData");
		try {

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date fromDate = dateFormat.parse(from_date); // Adjust time to include the whole day
			Date toDate = dateFormat.parse(to_date);
			mav.addObject("fdate", from_date);
			mav.addObject("tdate", to_date);

			if (instance_id.equalsIgnoreCase("all")) {
				System.out.println("in all");
				mav.addObject("activityLog", logRepository.showVMDailyUsageReportForAllVM(fromDate, toDate));
			} else {
				CloudInstance instance = repository.findById(Integer.parseInt(instance_id)).get();
				mav.addObject("activityLog", logRepository.showVMDailyUsageReport(fromDate, toDate, instance));
			}

		} catch (Exception e) {
			System.out.println("Exception = " + e);
		}

		return mav;
	}

	@GetMapping("/customerWise")
	public ModelAndView customerWise() {
		ModelAndView mav = new ModelAndView("customerWise");
		mav.addObject("pageTitle", "Report");
		mav.addObject("customerList", customerRepository.getCustomerName());
		CloudInstance objEnt = new CloudInstance();
		mav.addObject("objEnt", objEnt);
		return mav;
	}

	@GetMapping("/groupWise")
	public ModelAndView groupWise(Principal principal) {
		ModelAndView mav = new ModelAndView("groupWise");
		mav.addObject("pageTitle", "Report");

		if (principal == null) {
			ModelAndView mav1 = new ModelAndView("welcomePage");
			return mav1;
		}

		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();

		try {
			boolean isSuperAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));
			if (isSuperAdmin) {
				mav.addObject("groupList", groupRepository.getAllGroups());
				CloudInstance objEnt = new CloudInstance();
				mav.addObject("objEnt", objEnt);

			} else {
				AppUser obj = appRepository.findByuserName(username);
				String groupName = obj.getGroupName();

				mav.addObject("groupList", groupRepository.getByGroups(groupName));
				CloudInstance objEnt = new CloudInstance();
				mav.addObject("objEnt", objEnt);

			}
		} catch (Exception e) {
			System.out.println("Exception occured while fetching VM Data = " + e);
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
		}

		return mav;
	}

	@PostMapping("/groupWiseReport")
	public ModelAndView groupWiseReport(CloudInstance obj) {
		System.out.println("group id = " + obj.getGroupName());
		ModelAndView mav = new ModelAndView("report");
		List<String> groupName = new ArrayList<>();
		StringTokenizer token = new StringTokenizer(obj.getGroupName(), ",");
		while (token.hasMoreTokens()) {
			groupName.add(token.nextToken());
		}
		try {
			mav.addObject("pageTitle", "Group Wise Report");
			mav.addObject("listObj", repository.findByGroupIN(groupName));
		} catch (Exception e) {
			System.out.println("Exception occured while fetching Group wise report = " + e);
		}

		return mav;
	}

	@GetMapping("/customerWiseReport")
	public ModelAndView customerWiseReport(CloudInstance obj, Principal principal) {
		System.out.println("customer id = " + obj.getCustomerName());
		ModelAndView mav = new ModelAndView("report");

		if (principal == null) {
			ModelAndView mav1 = new ModelAndView("welcomePage");
			return mav1;
		}

		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();

		try {
			boolean isSuperAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));
			if (isSuperAdmin) {
				try {
					mav.addObject("pageTitle", "Customer Wise Report");
					mav.addObject("listObj", repository.findByCustomer(obj.getCustomerName()));
				} catch (Exception e) {
					System.out.println("Exception occured while fetching Customer wise report = " + e);
				}

			} else {
				AppUser obj2 = appRepository.findByuserName(username);
				String groupName = obj2.getGroupName();
				try {
					mav.addObject("pageTitle", "Customer Wise Report");
					mav.addObject("listObj", repository.findByCustomerandgroupbysort(obj.getCustomerName(), groupName));
				} catch (Exception e) {
					System.out.println("Exception occured while fetching Customer wise report = " + e);
				}

			}
		} catch (Exception e) {
			System.out.println("Exception occured while fetching VM Data = " + e);
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
		}

		return mav;
	}

	public static ModelAndView mavstatus = null;

	@GetMapping("/statusWise")
	public ModelAndView statusWise() {
		mavstatus = new ModelAndView("statusWise");
//		mavstatus.addObject("vmstatusSelectList", "no");
		mavstatus.addObject("pageTitle", "Report");
		CloudInstance objEnt = new CloudInstance();
		mavstatus.addObject("objEnt", objEnt);
		return mavstatus;
	}

	@PostMapping("/statusWiseReport")
	public ModelAndView statusWiseReport(CloudInstance obj, Principal principal) {
		System.out.println("vm status =" + obj.getVm_status());

//		ModelAndView mavstatus = new ModelAndView("report");

		List<String> statuslist = new ArrayList<>();
		StringTokenizer token = new StringTokenizer(obj.getVm_status(), ",");
		while (token.hasMoreTokens()) {
			statuslist.add(token.nextToken());
		}

		mavstatus.addObject("vmstatusSelectList", obj.getVm_state());

		if (principal == null) {
			ModelAndView mav1 = new ModelAndView("welcomePage");
			return mav1;
		}

		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();

		mavstatus.addObject("pageTitle", "Status Wise Report");
		try {
			boolean isSuperAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));
			if (isSuperAdmin) {
				try {

//					mav.addObject("listObj", repository.findByVMStatus(obj.getVm_status()));
					mavstatus.addObject("listObj", repository.findByVMStatusIN(statuslist, true));

				} catch (Exception e) {
					System.out.println("Exception occured while fetching Status wise report = " + e);
				}
			} else {
				AppUser obj2 = appRepository.findByuserName(username);
				String groupName = obj2.getGroupName();
				System.out.println("group name :: " + groupName);
				try {
//					mav.addObject("listObj", repository.findByVMStatusbygroupsort(obj.getVm_status(), groupName));
					mavstatus.addObject("listObj", repository.findByVMStatusbygroupsortIn(statuslist, groupName, true));
				} catch (Exception e) {
					System.out.println("Exception occured while fetching Status wise report = " + e);
				}

			}
		} catch (Exception e) {
			System.out.println("Exception occured while fetching VM Data = " + e);
			mavstatus.addObject("listObj", null);
			mavstatus.addObject("error", e.getMessage());
		}

		return mavstatus;
	}

	@PostMapping("/AlertDashboardFilter")
	public ModelAndView AlertDashboardFilter(@RequestParam String from_date, @RequestParam String to_date,
			@RequestParam String sevrity, Principal principal) {
		ModelAndView mav = new ModelAndView("alertDashboard");
		mav.addObject("pageTitle", "Add New " + "Alert Details");
		mav.addObject("action_name", "Alert Filter");
		mav.addObject("fdate", from_date);
		mav.addObject("tdate", to_date);

		List<String> ListSevirity = new ArrayList<>();
		StringTokenizer token = new StringTokenizer(sevrity, ",");
		while (token.hasMoreTokens()) {
			ListSevirity.add(token.nextToken().toLowerCase().trim());
		}

		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();
		List<String> instancesalert = null;
		AppUser obj = appRepository.findByuserName(username);
		List<String> groupName = new ArrayList<>();
		StringTokenizer token2 = new StringTokenizer(obj.getGroupName(), ",");
		while (token2.hasMoreTokens()) {
			groupName.add(token2.nextToken());
		}

		boolean isSuperAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));
		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
		boolean isUser = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"));

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

//		System.out.println(sevrity);
//		mav.addObject("dcLocationList", repositoryLocation.getAllDClocations());
//		// mav.addObject("vpcList", repositoryVPC.getAllVPC());
//		mav.addObject("securityGroupList", firewallRepository.getFirewall());
//		mav.addObject("sharedCpuPlan", priceRepository.getSharedCpuPlan());
//		mav.addObject("dedicatedCpuPlan", priceRepository.getDedicatedCpuPlan());
//		mav.addObject("highMemoryPlan", priceRepository.getHighMemoryPlan());
//		mav.addObject("switchList", switchRepository.getAllSwitch());
//		mav.addObject("physicalServerIPList", PhysicalServerRepository.getPhysicalServerIPs());
//		CloudInstance objEnt = new CloudInstance();
//		mav.addObject("objEnt", objEnt);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date fromDate = null;
		Date toDate = null;
		try {
			fromDate = dateFormat.parse(from_date + " 00:00:00");

			toDate = dateFormat.parse(to_date + " 23:59:59");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Adjust time to include the whole day
		List<AlertDash> AlertList = new ArrayList<>();

//		List<CloudInstanceMemoryThresholdHistory> ThreshHist = memoryThresholdHistoryRepository.findAll();
		List<CloudInstanceMemoryThresholdHistory> ThreshHist = memoryThresholdHistoryRepository
				.findfilteredData(fromDate, toDate, ListSevirity, instancesalert);
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

		List<CloudInstanceCpuThresholdHistory> ThreshCPUHist = cpuThresholdHistoryRepository.findfilteredData(fromDate,
				toDate, ListSevirity, instancesalert);

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

		return mav;
	}

//	@PostMapping("/KVMviewPage")
//	public ModelAndView loadKvmReportPage() {
//	    return new ModelAndView("kvmReportData");
//	}
//	

//	@GetMapping("/KVMviewPage")
//	public ModelAndView getKVMviewPage() {
//	    ModelAndView mav = new ModelAndView("kvmReportData");
//	    try {
//	        List<KVMDriveDetails> dataList = kVMDriveDetailsRepository.findBykVMDetails();
//	        System.out.println("Fetched Data: " + dataList.toString()); // Print to console
//	        mav.addObject("listObj", dataList);
//	    } catch (Exception e) {
//	        mav.addObject("data", null);
//	        mav.addObject("error", e.getMessage());
//	        System.out.println("Error fetching data: " + e.getMessage());
//	    }
//	    return mav;
//	}

	@GetMapping("/KVMviewPage")
	public ModelAndView getKVMviewPage(Principal principal) {
		ModelAndView mav = new ModelAndView("kvmReportData");
		JSONArray Finalarray = new JSONArray();
		List<KVMDriveDetails> dataList;
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
				dataList = kVMDriveDetailsRepository.findBykVMDetails();
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

				dataList = kVMDriveDetailsRepository.findBykVMDetails(vmGroups);
			}

//			 dataList = kVMDriveDetailsRepository.findBykVMDetails();
			System.out.println("Fetched Data: " + dataList.toString()); // Print to console
			int srno = 0;
			for (KVMDriveDetails temp : dataList) {
				JSONArray array = new JSONArray();

				String Device = temp.getDevice() != null ? temp.getDevice() : "";
				String Used = temp.getUsed() != null ? temp.getUsed() : "";
				String Capacity = temp.getCapacity() != null ? temp.getCapacity() : "";
				String Bus = temp.getBus() != null ? temp.getBus() : "";
				String Access = temp.getAccess() != null ? temp.getAccess() : "";
				String Source = temp.getSource() != null ? temp.getSource() : "";
				String Physicalserverip = temp.getPhysicalserverip() != null ? temp.getPhysicalserverip() : "";
				String Vmname = temp.getVmname() != null ? temp.getVmname() : "";

				// Use Long parsing instead of Integer parsing
				long capacityLong = 0L;
				long usedLong = 0L;
//				long capacityGB = 0L;
//				long usedGB = 0L;

				String Capacitystore = "";
				String Usedstore = "";
				String Freestore = "";

				if (Capacity != null && !Capacity.isEmpty() && Capacity.contains("GB")) {
					// Remove "GB" and trim any whitespace
					String capacityNumber = Capacity.replace("GB", "").trim();
					String usedNumber = Used.replace("GB", "").trim(); // Assuming Used also contains "GB"

					try {
						double capacityConvert = Double.parseDouble(capacityNumber);
						double usedConvert = Double.parseDouble(usedNumber);

						double freeGB = capacityConvert - usedConvert;

						// Round to 2 decimal places
						Capacitystore = String.format("%.2fGB", capacityConvert);
						Usedstore = String.format("%.2fGB", usedConvert);
						Freestore = String.format("%.2fGB", freeGB);
					} catch (NumberFormatException e) {
						System.err.println("Invalid number format: " + e.getMessage());
						Freestore = "0GB";
						Capacitystore = "0GB";
						Usedstore = "0GB";
					}
				} else {
					try {
						// Assuming Capacity and Used are in bytes (e.g., "8589934592")
						Long capacityconvert = Long.parseLong(Capacity);
						Long usedconvert = Long.parseLong(Used);

						long capacityGB = capacityconvert / 1024 / 1024 / 1024;
						long usedGB = usedconvert / 1024 / 1024 / 1024;
						long freeGB = capacityGB - usedGB;

						// Append "GB" to all stores
						Capacitystore = capacityGB + "GB";
						Usedstore = usedGB + "GB";
						Freestore = freeGB + "GB";
					} catch (NumberFormatException e) {
						System.err.println("Invalid number format in fallback: " + e.getMessage());
						Freestore = "0GB";
						Capacitystore = "0GB";
						Usedstore = "0GB";
					}
				}

//				if (!Capacity.isEmpty() && Capacity.matches("\\d+")) {
//					capacityLong = Long.parseLong(Capacity);
//					capacityGB = capacityLong / 1024 / 1024 / 1024;
//				}
//				if (!Used.isEmpty() && Used.matches("\\d+")) {
//					usedLong = Long.parseLong(Used);
//					usedGB = usedLong / 1024 / 1024 / 1024;
//				}
//				if (!Capacity.isEmpty() && (Capacity.contains("GB"))) {
//
//				} else {
//					capacityLong = Long.parseLong(Capacity);
//					capacityGB = capacityLong / 1024 / 1024 / 1024;
//
//					usedLong = Long.parseLong(Used);
//					usedGB = usedLong / 1024 / 1024 / 1024;
//				}
//
//				long freeGB = capacityGB - usedGB;

				srno++;

				array.put(srno);
				array.put(Device);
				array.put(Usedstore);
				array.put(Capacitystore);
				array.put(Freestore);
				array.put(Bus);
//				array.put(Access);
				array.put(Source);
				array.put(Physicalserverip);
				array.put(Vmname);

				Finalarray.put(array);
			}

			System.out.println("Finalarray_HardDriveKVM ::" + Finalarray);

			mav.addObject("listObj", Finalarray.toString());

		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
			System.out.println("Error fetching data: " + e.getMessage());
		}
		return mav;
	}

	@GetMapping("/HealthMonitoring")
	public ModelAndView getAll(Principal principal) {
		ModelAndView mav = new ModelAndView("HealthmonitoringData");
		List<CloudInstanceNodeHealthMonitoring> dataList;

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
				dataList = cloudInstanceNodeHealthMonitoringRepository.findByHealthMonitoring();
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

				dataList = cloudInstanceNodeHealthMonitoringRepository.findByHealthMonitoring(vmGroups);
			}

//			dataList = cloudInstanceNodeHealthMonitoringRepository.findByHealthMonitoring();

			mav.addObject("listObj", dataList);
		} catch (Exception e) {
			mav.addObject("data", null);
			mav.addObject("error", e.getMessage());
			System.out.println("Error fetching data: " + e.getMessage());
		}
		return mav;
	}

	@GetMapping("/DiskandHealthviewPage")
	public ModelAndView getDiskandHealthPage(Principal principal) {
		ModelAndView mav = new ModelAndView("Disk_Health_Utilization");
		JSONArray finalArray = new JSONArray();
		List<Object[]> dataList;
		try {

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
				dataList = kVMDriveDetailsRepository.findDriveAndMemoryDetails();
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

				dataList = kVMDriveDetailsRepository.findDriveAndMemoryDetails(vmGroups);
			}

			int srno = 0;

			for (Object[] row : dataList) {
				srno++;
				JSONArray array = new JSONArray();

				String physicalServerIp = toStr(row[0]);
				String device = toStr(row[1]);
				String vmName = toStr(row[2]);
				String bus = toStr(row[3]);
				String source = toStr(row[4]);
				String access = toStr(row[5]);

				String capacityDisk = toStr(row[6]);
				String usedDisk = toStr(row[7]);
				String totalMemory = toStr(row[8]);
				String usedMemory = toStr(row[9]);
				String freeMemory = toStr(row[10]);
				String cpuUtil = toStr(row[11]);
				String memoryUtil = toStr(row[12]);

				String capacityStore = "0GB", usedStore = "0GB", freeStore = "0GB", utilizationDiskFormatted = "0.00";

				try {
					double capacity = parseToGB(capacityDisk);
					double used = parseToGB(usedDisk);
					double free = capacity - used;

					capacityStore = String.format("%.2fGB", capacity);
					usedStore = String.format("%.2fGB", used);
					freeStore = String.format("%.2fGB", free);

					if (capacity > 0) {
						double utilizationDisk = (used / capacity) * 100;
						utilizationDiskFormatted = String.format("%.2f", utilizationDisk);
					}
				} catch (NumberFormatException e) {
					System.err.println("Disk parsing error: " + e.getMessage());
				}

				// Construct array
				array.put(srno);
				array.put(physicalServerIp);
				array.put(device);
				array.put(vmName);
				array.put(bus);
				array.put(source);
				// array.put(access); // Uncomment if needed

				array.put(usedStore);
				array.put(capacityStore);
				array.put(freeStore);

				array.put(usedMemory + " MB");
				array.put(totalMemory + " MB");
				array.put(freeMemory + " MB");

				array.put(utilizationDiskFormatted);
				array.put(memoryUtil);
				array.put(cpuUtil);

				finalArray.put(array);
			}

			System.out.println("Finalarray_Disk_Health :: " + finalArray);
			mav.addObject("listObj", finalArray.toString());

		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
			System.err.println("Error fetching disk health data: " + e.getMessage());
		}

		return mav;
	}

	// --- Utility methods ---

	private String toStr(Object obj) {
		return obj != null ? obj.toString().trim() : "";
	}

	private double parseToGB(String input) throws NumberFormatException {
		if (input == null || input.isEmpty())
			return 0;

		if (input.toUpperCase().contains("GB")) {
			return Double.parseDouble(input.replace("GB", "").trim());
		} else {
			// Assume bytes
			long bytes = Long.parseLong(input.trim());
			return bytes / 1024.0 / 1024.0 / 1024.0;
		}
	}

	@GetMapping("/vmDiskHistorylogReport")
	public ModelAndView vmHealthHistorylogReport(Principal principal) {
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		List<CloudInstance> instances = null;
		String userName = loginedUser.getUsername();
		String groupName = "";
		List<AppUser> user1 = userRepository.findByuserName(loginedUser.getUsername());
		boolean isSuperAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
		ModelAndView mav = new ModelAndView("vmDiskHistorylogReport");
		mav.addObject("pageTitle", "Report");
		if (isSuperAdmin) {
			mav.addObject("instanceNameList", repository.getInstanceName(true));
		} else {

			for (AppUser appUser : user1) {
				groupName = appUser.getGroupName();
			}
			List<String> groups = new ArrayList<>();
			StringTokenizer token = new StringTokenizer(groupName, ",");
			while (token.hasMoreTokens()) {
				groups.add(token.nextToken());
			}

			mav.addObject("instanceNameList", repository.getInstanceNameByGroup(groups, true));
		}

//		else if (isAdmin) {
//
//			for (AppUser appUser : user1) {
//				groupName = appUser.getGroupName();
//			}
//			List<String> groups = new ArrayList<>();
//			StringTokenizer token = new StringTokenizer(groupName, ",");
//			while (token.hasMoreTokens()) {
//				groups.add(token.nextToken());
//			}
//
//			mav.addObject("instanceNameList", repository.getInstanceNameByGroup(groups));
//		} else {
//			List<Integer> li = approvalRepository.findByRequesterNameCustom(userName);
//
//			System.out.println(li.toString());
//			mav.addObject("instanceNameList", repository.getInstanceNameByRequester(li, true));
//		}
		return mav;
	}

	@PostMapping("/vmDiskHistoryReportlogData")
	public ModelAndView vmDiskHistoryReportlogData(@RequestParam String from_date, @RequestParam String to_date,
			@RequestParam String instanceIP) {

		ModelAndView mav = new ModelAndView("vmDiskHistoryReportlogData");
		List<List<Object>> result = new ArrayList<>();

		JSONArray Finalarray = new JSONArray();
		int srno = 0;

		try {
			System.out.println("Instance name = " + instanceIP);
			List<String> ips = new ArrayList<>();
			StringTokenizer token = new StringTokenizer(instanceIP, ",");
			while (token.hasMoreTokens()) {
				ips.add(token.nextToken());
			}

			String[] ip_address = instanceIP.split(",");
			List<String> list = Arrays.asList(ip_address);

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date fromDate = dateFormat.parse(from_date + " 00:00:00");
			Date toDate = dateFormat.parse(to_date + " 23:59:59");

			DecimalFormat df = new DecimalFormat("#.##");
			List<Object[]> obj = kVMDriveDetailsRepository.vmDiskHistoryReportlogData(fromDate, toDate, list);
			for (Object[] data : obj) {
				JSONArray array = new JSONArray();
//				physicalserverip,device, vmname, bus,source,access,capacity,used,EventTimestamp

				String Physicalserverip = data[0].toString() != null ? data[0].toString() : "";
				String Device = data[1].toString() != null ? data[1].toString() : "";
				String Vmname = data[2].toString() != null ? data[2].toString() : "";
				String Bus = data[3].toString() != null ? data[3].toString() : "";
				String Source = data[4].toString() != null ? data[4].toString() : "";
				String Access = data[5].toString() != null ? data[5].toString() : "";

				String Used = data[7].toString() != null ? data[7].toString() : "";
				String Capacity = data[6].toString() != null ? data[6].toString() : "";
				String EventTimestamp = data[8].toString() != null ? data[8].toString() : "";

				// Use Long parsing instead of Integer parsing
				long capacityLong = 0L;
				long usedLong = 0L;

				if (!Capacity.isEmpty() && Capacity.matches("\\d+")) {
					capacityLong = Long.parseLong(Capacity);
				}
				if (!Used.isEmpty() && Used.matches("\\d+")) {
					usedLong = Long.parseLong(Used);
				}

				long capacityGB = capacityLong / 1024 / 1024 / 1024;
				long usedGB = usedLong / 1024 / 1024 / 1024;
				long freeGB = capacityGB - usedGB;

				srno++;

				array.put(srno);
				array.put(Physicalserverip);
				array.put(Device);
				array.put(Vmname);
				array.put(Bus);
				array.put(Source);
//				array.put(Access);
				array.put(usedGB + " GB");
				array.put(capacityGB + " GB");
				array.put(freeGB + " GB");
				array.put(EventTimestamp);

				Finalarray.put(array);
			}

			mav.addObject("fdate", from_date);
			mav.addObject("tdate", to_date);
			System.out.println("List result for vm status = " + Finalarray);
			mav.addObject("data", Finalarray.toString());

			// Group data by NODE_IP
		} catch (Exception e) {
			System.out.println("Exception = " + e);
		}

		return mav;
	}

//	 public static String convertBytes(long bytes) {
//	        int length = String.valueOf(bytes).length();
//
//	        if (length <= 5) { // Less than or equal to 5 digits → KB
//	            double kb = bytes / 1024.0;
//	            return String.format("%.2f KB", kb);
//	        } else if (length >= 6 && length <= 8) { // 6 to 8 digits → MB
//	            double mb = bytes / (1024.0 * 1024);
//	            return String.format("%.2f MB", mb);
//	        } else { // 9 or more digits → GB
//	            double gb = bytes / (1024.0 * 1024 * 1024);
//	            return String.format("%.2f GB", gb);
//	        }
//	    }

	@GetMapping("/Scenario_Details")
	public ModelAndView Scenario_Details(Principal principal) {
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		List<CloudInstance> instances = null;
		String userName = loginedUser.getUsername();
		String groupName = "";
		List<AppUser> user1 = userRepository.findByuserName(loginedUser.getUsername());
		boolean isSuperAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
		ModelAndView mav = new ModelAndView("Add_Scenario");
		mav.addObject("pageTitle", "Report");

		if (isSuperAdmin) {
//			mav.addObject("instanceNameList", repository.getInstanceNameNotAssigned());

			instances = repository.getInstanceNameNotAssigned();
			mav.addObject("instanceNameList", instances);
		} else {

			for (AppUser appUser : user1) {
				groupName = appUser.getGroupName();
			}
			List<String> groups = new ArrayList<>();
			StringTokenizer token = new StringTokenizer(groupName, ",");
			while (token.hasMoreTokens()) {
				groups.add(token.nextToken());
			}

			instances = repository.getInstanceNameNotAssigned();
			mav.addObject("instanceNameList", instances);
//			mav.addObject("instanceNameList", repository.getInstanceNameByGroup(groups, true));
		}

		return mav;
	}

	@GetMapping("/Add_Playlist")
	public ModelAndView Add_Playlist(Principal principal) {
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		List<CloudInstance> instances = null;
		String userName = loginedUser.getUsername();
		String groupName = "";
		List<AppUser> user1 = userRepository.findByuserName(loginedUser.getUsername());
		boolean isSuperAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));

		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
		ModelAndView mav = new ModelAndView("Add_Playlist");
		mav.addObject("pageTitle", "Report");
		if (isSuperAdmin) {
			mav.addObject("instanceNameList", repository.getInstanceName(true));
		} else {

			for (AppUser appUser : user1) {
				groupName = appUser.getGroupName();
			}
			List<String> groups = new ArrayList<>();
			StringTokenizer token = new StringTokenizer(groupName, ",");
			while (token.hasMoreTokens()) {
				groups.add(token.nextToken());
			}

			mav.addObject("instanceNameList", repository.getInstanceNameByGroup(groups, true));
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

	@GetMapping("/image/{id}")
	public void getImage(@PathVariable int id, HttpServletResponse response) throws IOException {
		System.out.println("inside_render_image ::");
		Optional<Add_Scenario> scenario = ScenarioRepository.findById(id);
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

	@GetMapping("/View_Vm_Listing")
	public ModelAndView getView_Vm_Listing(@RequestParam String Id) {

		ModelAndView mav = new ModelAndView("View_Vm_Listing");
		JSONArray Finalarray = new JSONArray();
		List<Add_Scenario> dataList;
//		try {
//			
//			int SRNO = Integer.parseInt(Id);
//
//			dataList = ScenarioRepository.getView_Particular_Scenerio(SRNO);
//
//			System.out.println("Fetched Data: " + dataList.toString()); // Print to console
//			int srno = 0;
//			for (Add_Scenario temp : dataList) {
//				JSONObject obj = new JSONObject();
//
//				String Scenario_Name = temp.getScenario_Name() != null ? temp.getScenario_Name() : "";
//				String Scenario_Title = temp.getScenario_Title() != null ? temp.getScenario_Title() : "";
//				String Description = temp.getDescription() != null ? temp.getDescription() : "";
//				String Category = temp.getCategory() != null ? temp.getCategory() : "";
//				String Scenario_Type = temp.getScenario_Type() != null ? temp.getScenario_Type() : "";
//				String Mode = temp.getMode() != null ? temp.getMode() : "";
//				String Difficulty_Level = temp.getDifficulty_Level() != null ? temp.getDifficulty_Level() : "";
//				String Duration = temp.getDuration() != null ? temp.getDuration() : "";
//				String Labs = temp.getLabs() != null ? temp.getLabs() : "";
//				String Cover_Image = temp.getCover_Image() != null ? temp.getCover_Image() : "";
//				int SrNo = temp.getSrNo();
//
////				srno++;
//
//				obj.put("Scenario_Name", Scenario_Name);
//				obj.put("Scenario_Title", Scenario_Title);
//				obj.put("Description", Description);
//				obj.put("Category", Category);
//				obj.put("Scenario_Type", Scenario_Type);
//				obj.put("Mode", Mode);
//				obj.put("Difficulty_Level", Difficulty_Level);
//				obj.put("Duration", Duration);
//				obj.put("Labs", Labs);
//				obj.put("Cover_Image", Cover_Image);
//				obj.put("Id", SrNo);
//
//				Finalarray.put(obj);
//			}
//
////			System.out.println("Finalarray_getView_Scenario ::" + Finalarray);
//
//			mav.addObject("listObj", Finalarray.toString());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			mav.addObject("listObj", null);
//			mav.addObject("error", e.getMessage());
//			System.out.println("Error fetching data: " + e.getMessage());
//		}
		return mav;
	}

	@PostMapping("/saveScenarioData")
	public ModelAndView saveScenarioData(@RequestParam String Scenario_title, @RequestParam String Scenario_name,
			@RequestParam String description, @RequestParam String Category, @RequestParam String type,
			@RequestParam String mode, @RequestParam String Difficulty, @RequestParam String Duration,
			@RequestParam(required = false) String max_players,
			@RequestParam(required = false) MultipartFile cover_image, @RequestParam String labsn) {

		ModelAndView mav = new ModelAndView("Add_Scenario");

		try {
			// Log all parameters
			System.out.println("Scenario_title: " + Scenario_title);
			System.out.println("Scenario_name: " + Scenario_name);
			System.out.println("description: " + description);
			System.out.println("Category: " + Category);
			System.out.println("type: " + type);
			System.out.println("mode: " + mode);
			System.out.println("Difficulty: " + Difficulty);
			System.out.println("Duration: " + Duration);
			System.out.println("max_players: " + max_players);
			System.out.println(
					"cover_image: " + (cover_image != null ? cover_image.getOriginalFilename() : "No file uploaded"));

//			System.out.println("comments: " + comments);

			System.out.println("labs: " + labsn);
			String[] parts = labsn.split("~");
			String labId = parts[0];
			String labName = parts[1];

			// Create new scenario object
			Add_Scenario scenario = new Add_Scenario();
			scenario.setScenarioTitle(Scenario_title);
			scenario.setScenarioName(Scenario_name);
			scenario.setDescription(description);
			scenario.setCategory(Category);
			scenario.setScenarioType(type);
			scenario.setMode(mode);
			scenario.setDifficultyLevel(Difficulty);
			scenario.setDuration(Duration);
			scenario.setMaxPlayers(max_players);
			scenario.setLabs(labName);
			scenario.setLabId(labId);
			scenario.setComments("");

			int instances = 0;
			try {
				instances = repository.updateInstanceNameAssigned(Integer.valueOf(labId));
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Exption_update_AssignedLab" + e);
			}

			// Handle the uploaded image file
			if (cover_image != null && !cover_image.isEmpty()) {
				// Validate file type
				String contentType = cover_image.getContentType();
				if (contentType != null && contentType.startsWith("image/")) {
					// Store image bytes in database
					scenario.setCoverImage(cover_image.getBytes());
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
					scenario.setCoverImage(defaultImageBytes);
					System.out.println("Default image loaded and saved to database");
				} catch (IOException e) {
					System.err.println("Error loading default image: " + e.getMessage());
					// Create a minimal placeholder image instead of null
					scenario.setCoverImage(createPlaceholderImage());
					System.out.println("Placeholder image created and saved to database");
				}
			}

			// Save to database
			ScenarioRepository.save(scenario);

			mav.addObject("message", "Scenario saved successfully!");
			mav.addObject("status", "success");

		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("message", "Error while saving scenario: " + e.getMessage());
			mav.addObject("status", "error");
		}

		return mav;
	}

	// Method to create a minimal placeholder image
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

}
