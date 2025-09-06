package in.canaris.cloud.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.canaris.cloud.entity.AdditionalStorage;
import in.canaris.cloud.entity.AppUser;
import in.canaris.cloud.entity.CloudInstance;
import in.canaris.cloud.entity.CloudInstanceLog;
import in.canaris.cloud.entity.CloudInstanceNodeHealthMonitoring;
import in.canaris.cloud.entity.Customer;
import in.canaris.cloud.entity.Group;
import in.canaris.cloud.entity.KVMDriveDetails;
import in.canaris.cloud.entity.RequestApproval;
import in.canaris.cloud.entity.VMCreationBean;
import in.canaris.cloud.entity.VPC;
import in.canaris.cloud.repository.AddPhysicalServerRepository;
import in.canaris.cloud.repository.AdditionalStorageRepository;
import in.canaris.cloud.repository.CloudInstanceLogRepository;
import in.canaris.cloud.repository.CloudInstanceNodeHealthMonitoringRepository;
import in.canaris.cloud.repository.CloudInstanceRepository;
import in.canaris.cloud.repository.DiscountRepository;
import in.canaris.cloud.repository.DiscoverDockerNetworkRepository;
import in.canaris.cloud.repository.FirewallRepository;
import in.canaris.cloud.repository.KVMDriveDetailsRepository;
import in.canaris.cloud.repository.LocationRepository;
import in.canaris.cloud.repository.PriceRepository;
import in.canaris.cloud.repository.RequestApprovalRepository;
import in.canaris.cloud.repository.SubProductRepository;
import in.canaris.cloud.repository.SwitchRepository;
import in.canaris.cloud.repository.UserRepository;
import in.canaris.cloud.repository.VPCRepository;
import in.canaris.cloud.utils.CMPUtil;
import in.canaris.cloud.utils.CommandResult;
import in.canaris.cloud.utils.ExecutePSCommand;
import in.canaris.cloud.utils.ExecuteSSHCommand;
import in.canaris.cloud.utils.KVMResize;
import in.canaris.cloud.utils.KillExe;

@Controller
@RequestMapping("/approval")
public class ApprovalController {

	@Autowired
	private CloudInstanceRepository repository;

	@Autowired
	private CloudInstanceLogRepository cloudInstanceLogRepository;

	@Autowired
	private LocationRepository repositoryLocation;

	@Autowired
	private VPCRepository repositoryVPC;

	@Autowired
	private PriceRepository priceRepository;

	@Autowired
	private FirewallRepository firewallRepository;

	@Autowired
	private SwitchRepository switchRepository;

	@Autowired
	private SubProductRepository subProductRepository;

	@Autowired
	private DiscountRepository discountRepository;

	@Autowired
	private RequestApprovalRepository approvalRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AdditionalStorageRepository externalStorageRepository;

	@Autowired
	private AddPhysicalServerRepository addPhysicalServerRepository;

	@Autowired
	private KVMDriveDetailsRepository kvmDriveDetailsRepository;

	@Autowired
	private CloudInstanceNodeHealthMonitoringRepository CloudInstanceNodeHealthMonitoringRep;
	
	@Autowired
	private DiscoverDockerNetworkRepository DiscoverDockerNetworkRepository;

	@GetMapping("/allRequests")
	public ModelAndView allRequests(Principal principal) {
		ModelAndView mav = new ModelAndView("approval_requests_view");
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

//				mav.addObject("listObj", approvalRepository.findAll());
				// mav.addObject("listObj",
				// approvalRepository.findByadminApprovalOrderByRequestIdDesc("Approved"));
				mav.addObject("listObj", approvalRepository.findAllByOrderByIdDesc());

			} else if (isAdmin) {
				System.out.println("Request listing ISAdmin ");
				String loging_username = loginedUser.getUsername();
				System.out.println("Request listing loging_username: " + loging_username);
				if (loging_username.equals("TarangAdmin")) {
					System.out.println("Request listing In TarangAdmin: " + loging_username);
					mav.addObject("listObj", approvalRepository.findAll(Sort.by(Sort.Direction.DESC, "id")));

				} else {
					System.out.println("Request listing In NotTarangAdmin: " + loging_username);
					for (AppUser appUser : user1) {
						groupName = appUser.getGroupName();
					}
					List<String> groups = new ArrayList<>();
					StringTokenizer token = new StringTokenizer(groupName, ",");
					while (token.hasMoreTokens()) {
						groups.add(token.nextToken());
					}
//					List<AppUser> users = userRepository.findBygroups(groups);

					List<AppUser> users = new ArrayList<>();
					for (String Stringgroup : groups) {
						users.addAll(userRepository.findByGroupNameContaining(Stringgroup));
					}
					for (AppUser obj : users) {
						userNames.add(obj.getUserName());
					}
					System.out.println("users in " + groupName + " = " + userNames);
					mav.addObject("listObj", approvalRepository.findByRequesters(userNames));
				}

			}

		} catch (Exception e) {
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
		}
		mav.addObject("physicalServerIPList", addPhysicalServerRepository.getPhysicalServerIPs());
//		mav.addObject("physicalServerIPListtype", addPhysicalServerRepository.getVirtualization_type());
		return mav;
	}

	@GetMapping("/userRequests")
	public ModelAndView userRequests(Principal principal) {
		ModelAndView mav = new ModelAndView("user_requests_view");
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();
		try {
//			mav.addObject("listObj", approvalRepository.findByRequesterName(username));
			mav.addObject("listObj", approvalRepository.findByRequesterTest(username));

		} catch (Exception e) {
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
		}
		return mav;
	}

	// Admin Approval
	@GetMapping("/acceptAdminApproval")
	public @ResponseBody String acceptAdminApproval(@RequestParam String reqId, Principal principal) {
		String response = "";
		try {
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			String username = loginedUser.getUsername();
			AppUser user = userRepository.findByUsername(username);
			RequestApproval approvalObj = approvalRepository.findById(Long.valueOf(reqId)).get();

			approvalObj.setApproved_on(new Timestamp(System.currentTimeMillis()));
			approvalObj.setAdminApproval("Approved");
			// approvalObj.setApprover_name(username);
			approvalRepository.save(approvalObj);

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
			RequestApproval approvalObj = approvalRepository.findById(Long.valueOf(reqId)).get();

			approvalObj.setAdminApproval("Rejected");
			approvalObj.setDescription(remark);
			approvalObj.setApprover_name(username);
			approvalObj.setApproved_on(new Timestamp(System.currentTimeMillis()));
			approvalRepository.save(approvalObj);
			response = "reject";

		} catch (Exception e) {
			System.out.println("Exception occured during admin rejection : " + e);
			response = "fail";
		}

		return response;

	}
	// End Admin Approval

	private static String parseNumberToString(String input) {
		String numericPart = input.replaceAll("[^\\d]", ""); // Remove all non-digit characters
		return numericPart;
	}

	@GetMapping("/requestApprovedforkvm")
	public @ResponseBody String requestApprovedforkvm(@RequestParam String request_id, @RequestParam String serverIP,
			Principal principal) {

		// boolean isVMCreated = false;
		System.out.println(" requestApprovedforkvm 19feb25approval id = " + request_id);
		System.out.println("Server ip = " + serverIP);

		// String responce_data = null;
		boolean cmd_status = false;
		;
		StringBuilder cmd_msg = new StringBuilder();
		cmd_msg.append(".");
		// boolean isApproved = false;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();
		AppUser user = userRepository.findByUsername(username);
		RequestApproval approvalObj = approvalRepository.findById(Long.valueOf(request_id)).get();
		String requestBy = approvalObj.getRequesterName();
		String requesterGroup = userRepository.findByUsername(requestBy).getGroupName();
		String fGroup[] = requesterGroup.split(",");
		System.out.println("(fGroup[0]  :: " + fGroup[0]);
		int instance_id = (int) approvalObj.getRequestId();
		System.out.println("Instance id =" + instance_id);
		CloudInstance obj = repository.findById(instance_id).get();
		String sshusername = addPhysicalServerRepository.findByserverIP(serverIP.trim()).getSsh_host();
		String sshPassword = addPhysicalServerRepository.findByserverIP(serverIP.trim()).getSsh_password();
		try {
			System.out.println("Instance id = " + request_id);
			String response = "";
			String requestType = approvalObj.getRequest_type();
			System.out.println("requet_type = " + requestType);
			if (requestType.equalsIgnoreCase("instance create")) {
				String vm_name = obj.getInstance_name();
				String vm_location_path = obj.getVm_location_path();
				String vhd_path = obj.getVm_location_path() + "\\" + vm_name + "\\" + vm_name + ".vhdx";
				String iso_file_path = subProductRepository.getISOFilePath(obj.getSubproduct_id().getId());
				String variant_name = subProductRepository.getVARIANT(obj.getSubproduct_id().getId());
				System.out.println("Approval variant_name: " + variant_name);
				String vcpus = obj.getPrice_id().getvCpu();
				String rams = obj.getPrice_id().getRam();
				String disksize = obj.getPrice_id().getSsd_disk();

				// Changed to match Hyper-V
				int numVcpus = Integer.parseInt(vcpus);

				// RAM
				int ramInMb = 0;
				int diskInGb = 0;

				if (rams.contains("GB")) {
					rams = rams.replace("GB", "");
					ramInMb = Integer.parseInt(rams);
					ramInMb = ramInMb * 1024;

				} else if (rams.contains("MB")) {
					rams = rams.replace("MB", "");
					ramInMb = Integer.parseInt(rams);
				} else {
					ramInMb = Integer.parseInt(rams);
				}

				// DISK
				if (disksize.contains("GB")) {
					disksize = disksize.replace("GB", "");
					diskInGb = Integer.parseInt(disksize);

				} else if (disksize.contains("MB")) {
					disksize = disksize.replace("MB", "");
					diskInGb = Integer.parseInt(disksize);
					diskInGb = diskInGb / 1024;

				} else {
					diskInGb = Integer.parseInt(disksize);
				}
				// Required Parameter in MB
				System.out.println("vcpus: " + vcpus);
				System.out.println("rams: " + rams);
				System.out.println("RAM in MB: " + ramInMb);

				System.out.println("disks: " + disksize);
				System.out.println("DISK in GB: " + diskInGb);

				System.out.println("VM Name: " + vm_name);
				System.out.println("ISO File Path: " + iso_file_path);

				System.out.println("Number of vCPUs: " + numVcpus);

				System.out.println("sshusername: " + sshusername);
				System.out.println("sshPassword: " + sshPassword);

				String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
				String createVMCommand = "";

				createVMCommand = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virt-install --name " + vm_name
						+ " --ram " + ramInMb + " --disk path=/var/lib/libvirt/images/" + vm_name + timestamp
						+ ".qcow2,size=" + diskInGb + " --vcpus " + numVcpus + " --os-type linux --os-variant="
						+ variant_name
						+ " --network bridge=virbr0 --graphics vnc,listen=0.0.0.0 --console pty,target_type=serial --cdrom "
						+ iso_file_path + " --wait 1";

				// System.out.println("Create VM Command:" + createVMCommand);

				System.out.println(serverIP + ":Command print APPROVED VM Create : " + createVMCommand);

				ExecuteSSHCommand ssh = new ExecuteSSHCommand();
				CommandResult cmdResult = ssh.executeCommand(createVMCommand, sshusername, sshPassword, serverIP);
				cmd_status = cmdResult.isStatus();
				cmd_msg = cmdResult.getMessage();
				System.out.println("VM Create Approval CMD OP:" + cmd_status + ":" + cmd_msg);
				Thread.sleep(7000);
				if (cmd_status == false) {
					System.out.println("VM Create Approval Failed:" + cmd_msg + ":" + createVMCommand);
				} else {
					
					try {
						String startvmcommand = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh start "
								+ vm_name + "";
						executeRemoteCommand(startvmcommand, sshusername, sshPassword, serverIP);
						System.out.println("start VM Aprove:" + startvmcommand);
					} catch (Exception e) {
						System.out.println("Exception start VM:" + e);
					}

					System.out.println("Start Drive:" + serverIP + ":" + vm_name);
					try {
						ArrayList<KVMDriveDetails> kvmdrivelis = insertDriveDetails(serverIP, vm_name, sshusername,
								sshPassword);
						Iterator<KVMDriveDetails> itr = kvmdrivelis.iterator();
						while (itr.hasNext()) {
							KVMDriveDetails kvmdrive = itr.next();
							kvmDriveDetailsRepository.deleteByVmnameAndPhysicalserverip(vm_name, serverIP);
							kvmDriveDetailsRepository.save(kvmdrive);
							System.out.println("Insert Drive Success:" + serverIP + ":" + vm_name);
						}
					} catch (Exception e) {
						System.out.println("Exception drive: " + e);
					}

					// Memory Details
					try {
						CloudInstanceNodeHealthMonitoring healthdata = getHealthDetails(serverIP, sshusername,
								sshPassword, vm_name);
						CloudInstanceNodeHealthMonitoringRep.save(healthdata);

					} catch (Exception e) {
						System.out.println("Exception drive: " + e);
					}

					String myretureddata = getkvmperticularVMDetails(vm_name, sshusername, sshPassword, serverIP);
					if (myretureddata.equalsIgnoreCase("No Data")) {
						cmd_status = false;
						cmd_msg.append("The VM was not created properly - Something went wrong.- ");

					} else {
						String[] lines = myretureddata.split("\n");
						String KVMVmname = "";
						String KVMHostname = "";
						String KVMcpu = "";
						String KVMmemory = "";
						String KVMdiskPath = "";
						String KVMcdromIsoPath = "";
						String KVMmacAddress = "";
						String KVMosType = "";
						String KVMosVariant = "";
						String KVMipAddress = "";
						String uuid = "";

						for (String line : lines) {
							if (line.startsWith("  Name: ")) {
								KVMVmname = line.replace("  Name: ", "").trim();
							} else if (line.startsWith("  Hostname: ")) {
								KVMHostname = line.replace("  Hostname: ", "").trim();
							} else if (line.startsWith("  CPU: ")) {
								KVMcpu = line.replace("  CPU: ", "").trim();
							} else if (line.startsWith("  Memory: ")) {
								KVMmemory = line.replace("  Memory: ", "").replace(" MB", "").trim();
							} else if (line.startsWith("  Disk Path: ")) {
								KVMdiskPath = line.replace("  Disk Path: ", "").trim();
							} else if (line.startsWith("  CD-ROM ISO Path: ")) {
								KVMcdromIsoPath = line.replace("  CD-ROM ISO Path: ", "").trim();
							} else if (line.startsWith("  MAC Address: ")) {
								KVMmacAddress = line.replace("  MAC Address: ", "").trim();
							} else if (line.startsWith("  OS Type: ")) {
								KVMosType = line.replace("  OS Type: ", "").trim();
							} else if (line.startsWith("  OS Variant: ")) {
								KVMosVariant = line.replace("  OS Variant: ", "").trim();
							} else if (line.startsWith("  IP Address: ")) {
								KVMipAddress = line.replace("  IP Address: ", "").trim();
							} else if (line.startsWith("  uuid: ")) {
								uuid = line.replace("  uuid: ", "").trim();
							}
						}

						obj.setInstance_name(KVMVmname);
						obj.setComputer_name(KVMHostname);
						obj.setCpuAssigned(KVMcpu);
						// obj.setMemoryAssigned(KVMmemory); // Assuming memory is in MB; adjust if
						// needed
						obj.setDisk_path(KVMdiskPath);
						obj.setIso_file_path(KVMcdromIsoPath);
						obj.setMac_address(KVMmacAddress);
						obj.setInstance_ip(KVMipAddress);
						obj.setVirtualization_type(KVMosType); // Set OS type if relevant to virtualization
						obj.setVirtualization_type("KVM"); // Set OS type if relevant to virtualization
						obj.setGeneration_type(KVMosVariant); // Use `generation_type` to store OS variant if applicable
						obj.setPhysicalServerIP(serverIP);
						obj.setVm_state("running");
						obj.setVm_status("running");
						obj.setUuid(uuid);
						obj.setVm_id(uuid);
						obj.setKvmvmid(uuid);
						obj.setMac_address("-");
						obj.setKvmostype(variant_name);
						obj.setMemoryAssigned(rams);
						obj.setMemory_assignedkvm(rams);

						repository.save(obj);
						cmd_status = true;
						cmd_msg.append("VM Create Successfully.");

						VPC vpc_id = new VPC();
						Customer customer = new Customer();
						customer.setId(1);
						vpc_id.setId(1);
						obj.setVpc_id(vpc_id);
						obj.setRequest_status("Approved");
						obj.setMonitoring(true);
						obj.setGroupName(fGroup[0]);
						repository.save(obj);

						approvalObj.setApproved_on(new Timestamp(System.currentTimeMillis()));
						approvalObj.setRequest_status("Approved");
						approvalObj.setApprover_name("Cloud Admin");
						approvalRepository.save(approvalObj);
						
						
						try {
							getKVMIpAdressesMain(serverIP, sshusername, serverIP, sshPassword);
						} catch (Exception e) {
							System.out.println(e);
							e.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Exception occured while approving request for VM creation = " + e);
		}
		System.out.println("Final VM Status&Message:" + cmd_status + ":" + cmd_msg.toString());

		if (cmd_status) {
			System.out.println("Kill KVM exe User");

			  try {
                KillExe exe = new KillExe();
                exe.killKVMExe();
            } catch (Exception e) {
                System.out.println("Exception:" + e);
            }
			  
			  
			  
			
			return "success";
		} else {
			return cmd_msg.toString();
		}

	}

	private void getKVMIpAdressesMain(String serverIP, String user, String host, String password) {

//      String host = "172.16.5.22";
//      String user = "ptsadmin";
//      String password = "K$n77&Xe@B";
		JSch jsch = new JSch();
		Session session = null;
		ChannelExec channel = null;
		InputStream in = null;
		BufferedReader reader = null;
		System.out.println("inside getvmdetails " + serverIP + " " + user + " " + host + " " + password);
		try {

			session = jsch.getSession(user, host, 22);
			session.setPassword(password);
			// System.out.println("inside getvmdetails2");
			// Set session properties
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

			// Connect to the server
			session.connect();
			// System.out.println("inside getvmdetails3");
			// Command to list all VMs and get IP addresses and MAC addresses
			// String command = "for dom in $(virsh list --name); do echo \"VM: $dom\";
			// virsh domifaddr \"$dom\" --source agent; done";
			String command = "for dom in $(export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh list --name); do\n"
					+ "  echo \"VM: $dom\"\n"
					+ "  export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh domifaddr \"$dom\" --source agent\n"
					+ "done";
			// String command="export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh
			// domifaddr ADRADVBDB1LR --source agent";

			// Execute the command
			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);
			channel.setErrStream(System.err);

			reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
			channel.connect();
			// Define patterns to match IP and MAC addresses
			Pattern ipPattern = Pattern.compile("\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b");
			Pattern macPattern = Pattern.compile("([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}");

			String line;
			String currentVm = null;

			String vm_ip = "";
			String vm_mac = "";
			HashMap<String, Boolean> ipmap = new HashMap();

			// Process each line of the output
			while ((line = reader.readLine()) != null) {
				// System.out.println("line:"+line);
				// Check if the line contains the VM name
				System.out.println("inside getvmdetails5 " + line);
				if (line.startsWith("VM: ")) {
					currentVm = line.substring(4);
					ipmap.put(currentVm, Boolean.FALSE);
					// System.out.println("VM Name: " + currentVm);
				}

				// Find and print the IP address
				Matcher ipMatcher = ipPattern.matcher(line);
				String ipAddress = "";
				if (ipMatcher.find()) {
					ipAddress = ipMatcher.group();

					// System.out.println("IP Address: " + ipAddress);
				}

				// Find and print the MAC address
				Matcher macMatcher = macPattern.matcher(line);
				if (macMatcher.find()) {
					String macAddress = macMatcher.group();
					boolean ischeck = ipmap.get(currentVm);
					// System.out.println("MAC Address: " + macAddress);
					if (!macAddress.equals("00:00:00:00:00:00") && ischeck == false) {
						ipmap.put(currentVm, Boolean.TRUE);
						vm_mac = macAddress;
						vm_ip = ipAddress;
						System.out.println("VM Name: " + currentVm);
						System.out.println("VM IP: " + vm_ip);
						System.out.println("VM Mac: " + vm_mac);

						try {
							int rowsUpdated = repository.updateInstanceIpIfExists(serverIP, currentVm,
									vm_ip,vm_mac);
							System.out.println("ip insert instance ip success " + rowsUpdated);
						} catch (Exception e) {
							e.printStackTrace();
						}
						// add update and insert ip and mac code
					}
				}

			}

			// Disconnect
			// channelExec.disconnect();
			// session.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		} finally {
			// Close resources in reverse order of their creation
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (channel != null) {
				channel.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}

	}


	public static CloudInstanceNodeHealthMonitoring getHealthDetails(String HOST, String USER, String PASSWORD,
			String vm_name) {

		CloudInstanceNodeHealthMonitoring health = new CloudInstanceNodeHealthMonitoring();
		String command = "export LIBVIRT_DEFAULT_URI=qemu:///system && vm=" + vm_name + "; "
				+ "if [ -n \"$vm\" ]; then "
				+ "total_mem=$(virsh dominfo \"$vm\" | awk '/Max memory/ {print $3 / 1024}'); "
				+ "used_mem=$(virsh dommemstat \"$vm\" | awk '/rss/ {print $2 / 1024}'); "
				+ "cpu_time=$(virsh domstats \"$vm\" | awk '/cpu_time/ {print $2}'); "
				+ "cpu_count=$(virsh vcpuinfo \"$vm\" | grep -c \"CPU\"); "
				+ "if [ -n \"$cpu_time\" ] && [ \"$cpu_count\" -gt 0 ]; then "
				+ "cpu_utilization=$(echo \"scale=2; $cpu_time / 1000000 / $cpu_count\" | bc); "
				+ "echo \"$vm,$total_mem MB,$used_mem MB,$cpu_utilization %\"; " + "else "
				+ "echo \"$vm,$total_mem MB,$used_mem MB,CPU data unavailable\"; " + "fi " + "fi ";

		Session session = null;
		ChannelExec channel = null;
		StringBuilder outputbu = new StringBuilder();

		try {
			JSch jsch = new JSch();
			session = jsch.getSession(USER, HOST, 22);
			session.setPassword(PASSWORD);

			// Configuring SSH to ignore key checking
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

			session.connect();

			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);
			channel.setErrStream(System.err);

			InputStream errStream = channel.getErrStream();
			InputStream in = channel.getInputStream();
			channel.connect();

			byte[] buffer = new byte[1024];
			int readCount;
			while ((readCount = in.read(buffer)) != -1) {
				System.out.print("CMD health op:" + new String(buffer, 0, readCount));

				outputbu.append(new String(buffer, 0, readCount));
			}
			byte[] errorBuffer = new byte[1024];
			while ((readCount = errStream.read(errorBuffer)) != -1) {
				String errorOutput = new String(errorBuffer, 0, readCount);
				System.out.println("health errorOutput:" + errorOutput + ":@");
			}

		} catch (Exception e) {
			System.out.print("Exception command:" + e.getMessage());
		} finally {
			if (channel != null) {
				channel.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}

		try {
			// Step 1: Execute the KVM SSH command

			String output = outputbu.toString();

			// Step 2: Parse the output
			String[] lines = output.split("\n");
			for (String line : lines) {
				if (line.isEmpty()) {
					continue;
				}
				String[] parts = line.split(",");
				String vm_namee = parts[0].trim();
				System.out.println("##############################Health Mon:" + vm_namee);

				double totalMem = Double.parseDouble(parts[1].replace("MB", "").trim());
				System.out.println("totalMem:" + vm_namee + ":" + totalMem);
				double usedMem = Double.parseDouble(parts[2].replace("MB", "").trim());
				System.out.println("usedMem:" + vm_namee + ":" + usedMem);
				// double cpuUtilization = 0;

				double freeMem = totalMem - usedMem;
				double memPercentage = 0;

				try {
					if (totalMem == 0 && usedMem == 0) {
						memPercentage = 0;
					} else {
						memPercentage = (usedMem / totalMem) * 100;
					}

				} catch (Exception e) {
					System.out.println("Exception memory:" + e);
				}
				System.out.println("Memory Utilization:" + memPercentage);

				health.setCpuUtilization(0);
				health.setTotalMemory(totalMem);
				health.setFreeMemory(freeMem);
				health.setUsedMemory(usedMem);
				health.setNodeName(vm_name);
				health.setVmName(vm_name);
				health.setNodeIp(HOST);
				int intValue2 = (int) Math.round(memPercentage);
				health.setMemoryUtilization(intValue2);

			}

		} catch (Exception e) {
			System.out.println("Exception Memory:" + e);
			// e.printStackTrace();
		}
		return health;
	}

	private static ArrayList<KVMDriveDetails> insertDriveDetails(String serverIP, String vmName, String USER,
			String PASSWORD) {

		// System.out.println("VM name disk:" + vmName);
		String returneddata = "";
		Session session = null;
		ChannelExec channelExec = null;
		ArrayList<KVMDriveDetails> kvmdrivelis = new ArrayList<>();
		try {

			JSch jsch = new JSch();
			session = jsch.getSession(USER, serverIP, 22);
			session.setPassword(PASSWORD);

			// Configuring SSH to ignore key checking
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

			session.connect();
//
//			channel = (ChannelExec) session.openChannel("exec");
//			channel.setCommand(command);
//			channel.setErrStream(System.err);

			// Get the list of block devices for the VM
			String command = String.format("export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh domblklist %s",
					vmName);
			channelExec = (ChannelExec) session.openChannel("exec");
			channelExec.setCommand(command);
			channelExec.setErrStream(System.err);

			InputStream in = channelExec.getInputStream();
			channelExec.connect();

			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			// System.out.println("Disk Details:");
//            System.out.printf("%-10s %-10s %-10s %-10s %-10s %-10s\n", "Device", "Used", "Capacity", "Bus", "Access",
//                    "Source");

			while ((line = reader.readLine()) != null) {
				if (line.contains("/")) { // Skipping header and blank lines
					String[] parts = line.trim().split("\\s+");
					String device = parts[0];
					String source = parts[1];

					// Fetch additional disk info
					KVMDriveDetails kvmdrive = getDiskUsage(session, vmName, device, source, serverIP);
					kvmdrivelis.add(kvmdrive);
				}
			}

			channelExec.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			if (channelExec != null) {
				channelExec.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}
		return kvmdrivelis;
	}

	private static KVMDriveDetails getDiskUsage(Session session, String vmName, String device, String source,
			String serverIP) {
		String formattedString = "";
		KVMDriveDetails kvmdrive = new KVMDriveDetails();
		try {
			String command = String.format(
					"export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh domblkinfo %s %s", vmName, device);
			ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
			channelExec.setCommand(command);
			channelExec.setErrStream(System.err);

			InputStream in = channelExec.getInputStream();
			channelExec.connect();

			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			String used = "-", capacity = "-", bus = "-", access = "-";

			while ((line = reader.readLine()) != null) {
				// System.out.println("Strt drive details #################### " + line);
				if (line.contains("Capacity:")) {
					capacity = line.split(":")[1].trim();
				}
				if (line.contains("Allocation:")) {
					used = line.split(":")[1].trim();
				}
				if (line.contains("Physical:")) {
					bus = "virtio"; // Example bus type, change as needed
				}
				// Assume 'access' and other details as defaults or parse additional as needed
			}

			System.out.println(
					"Disk Details:" + device + ":" + used + ":" + capacity + ":" + bus + ":" + access + ":" + source);

			// Create new KVMDriveDetails entity

			kvmdrive = new KVMDriveDetails();
			kvmdrive.setDevice(device);
			kvmdrive.setUsed(used);
			kvmdrive.setCapacity(capacity);
			kvmdrive.setBus(bus);
			kvmdrive.setAccess(access);
			kvmdrive.setSource(source);
			kvmdrive.setPhysicalserverip(serverIP); // Set additional fields
			kvmdrive.setVmname(vmName); // Set additional fields

			// Save the new entity using the repository

			System.out.println("save successfulyy drive details");
			channelExec.disconnect();
		} catch (Exception e) {
			System.out.println(e);

		}

		return kvmdrive;
	}

	public static void getKVMVmdetails(String USER, String PASSWORD, String HOST) {

		// Step 1: Get list of all VMs
		String listVMsCommand = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh list --all";
		String vmNames = executeRemoteCommand2(listVMsCommand, USER, PASSWORD, HOST);
		if (vmNames == null || vmNames.isEmpty()) {
			System.out.println("No VMs found.");
			return;
		}

		// Split the output to get individual VM names
		String[] vmArray = vmNames.split("\n");

		// Step 2: Get detailed info for each VM
		for (String vm : vmArray) {
			String[] parts = vm.trim().split("\\s+");
			if (parts.length > 1) {
				String vmName = parts[1]; // Assuming the VM name is in the second column
				System.out.println("Details for VM: " + vmName);
				String myretureddata = getkvmperticularVMDetails(vmName, USER, PASSWORD, HOST);
			}
		}

	}

	public static String getkvmperticularVMDetails(String vmName, String USER, String PASSWORD, String HOST) {
		System.out.println("start command dumpxml");
		// Get basic VM info
//		String domInfoCommand = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh dominfo " + vmName;
//		String domInfo = executeRemoteCommand2(domInfoCommand, USER, PASSWORD, HOST);
//		System.out.println(domInfo);

		// Get detailed XML info
		String dumpXmlCommand = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh dumpxml " + vmName;
		String xmlInfo = executeRemoteCommand2(dumpXmlCommand, USER, PASSWORD, HOST);
		String returneddata = parseKVMVMXMLInfo(vmName, xmlInfo);
		return returneddata;
	}

	public static String parseKVMVMXMLInfo(String vmName, String xml) {
		if (xml == null || xml.isEmpty()) {
			return "No Data";
		}

		// Extracting the required details from the XML
		String hostname = "";
		String macAddress = "";
		String diskPath = "";
		String cdromIsoPath = "";
		String osType = "";
		String osVariant = "";
		String ipAddress = "";
		String cpu = "";
		String memory = "";
		String uuid = "";

		String[] lines = xml.split("\n");

		for (String line : lines) {
			if (line.contains("<name>")) {
				hostname = line.replaceAll("<[^>]+>", "").trim();
			}
			if (line.contains("<uuid>")) {
				uuid = line.replaceAll("<[^>]+>", "").trim();
			}
			if (line.contains("<memory>")) {
				memory = line.replaceAll("<[^>]+>", "").trim();
			}
			if (line.contains("<vcpu")) {
				cpu = line.replaceAll("<[^>]+>", "").trim();
			}
			if (line.contains("<disk type='file'")) {
				int startIndex = line.indexOf("source file='") + 13;
				int endIndex = line.indexOf("'", startIndex);
				diskPath = line.substring(startIndex, endIndex);
			}
			if (line.contains("<disk type='cdrom'")) {
				int startIndex = line.indexOf("source file='") + 13;
				int endIndex = line.indexOf("'", startIndex);
				cdromIsoPath = line.substring(startIndex, endIndex);
			}
			if (line.contains("<interface")) {
				int index = line.indexOf("mac address='");
				if (index != -1) {
					macAddress = line.substring(index + 14, line.indexOf("'", index + 14));
				}
			}
			if (line.contains("<os>")) {
				for (String osLine : lines) {
					if (osLine.contains("<type>")) {
						osType = osLine.replaceAll("<[^>]+>", "").trim();
					}
					if (osLine.contains("<variant>")) {
						osVariant = osLine.replaceAll("<[^>]+>", "").trim();
					}
				}
			}
		}

		System.out.println("VM Details:");
		System.out.println("  Name: " + vmName);
		System.out.println("  Hostname: " + hostname);
		System.out.println("  CPU: " + cpu);
		System.out.println("  Memory: " + memory + " MB"); // Convert from KiB to MB
		System.out.println("  Disk Path: " + diskPath);
		System.out.println("  CD-ROM ISO Path: " + cdromIsoPath);
		System.out.println("  MAC Address: " + macAddress);
		System.out.println("  OS Type: " + osType);
		System.out.println("  OS Variant: " + osVariant);
		System.out.println("  IP Address: " + ipAddress);
		System.out.println();

		return "VM Details:\n" + "  Name: " + vmName + "\n" + "  Hostname: " + hostname + "\n" + "  CPU: " + cpu + "\n"
				+ "  Memory: " + memory + " MB\n" + "  Disk Path: " + diskPath + "\n" + "  CD-ROM ISO Path: "
				+ cdromIsoPath + "\n" + "  MAC Address: " + macAddress + "\n" + "  OS Type: " + osType + "\n"
				+ "  OS Variant: " + osVariant + "\n" + "  IP Address: " + ipAddress + "\n" + "  uuid: " + uuid + "\n";

	}

	public static String executeRemoteCommand2(String command, String USER, String PASSWORD, String HOST) {
		Session session = null;
		ChannelExec channel = null;
		StringBuilder output = new StringBuilder();

		try {
			JSch jsch = new JSch();
			session = jsch.getSession(USER, HOST, 22);
			session.setPassword(PASSWORD);

			// Configuring SSH to ignore key checking
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

			session.connect();

			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);
			channel.setErrStream(System.err);

			InputStream in = channel.getInputStream();
			channel.connect();

			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
				// System.out.println("VMline:"+line);
				output.append(line).append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (channel != null)
				channel.disconnect();
			if (session != null)
				session.disconnect();
		}

		return output.toString().trim(); // Return the output as a string
	}

	public static void executeRemoteCommand(String command, String USER, String PASSWORD, String HOST) {

		Session session = null;
		ChannelExec channel = null;
		System.out.println("Start command ");
		StringBuilder status_msg = new StringBuilder();
		status_msg.append("start ");

		try {
			JSch jsch = new JSch();
			session = jsch.getSession(USER, HOST, 22);
			session.setPassword(PASSWORD);

			// Configuring SSH to ignore key checking
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

			session.connect();

			channel = (ChannelExec) session.openChannel("exec");
//			channel.setCommand("sudo -i");
//			channel.setCommand(PASSWORD);
			channel.setCommand(command);
			channel.setErrStream(System.err);

			InputStream er = channel.getErrStream();
			InputStream in = channel.getInputStream();
			channel.connect();

			byte[] buffer = new byte[1024];
			int readCount;
			while ((readCount = in.read(buffer)) != -1) {
				System.out.print(new String(buffer, 0, readCount));
			}

			byte[] buffer2 = new byte[1024];
			int readCount2;
			while ((readCount2 = er.read(buffer2)) != -1) {
				System.out.print("Error: " + new String(buffer2, 0, readCount2));
				status_msg.append("fail:" + new String(buffer2, 0, readCount2));

			}
		} catch (Exception e) {
			System.out.println("creating vm exeption :: " + e);
			status_msg.append("fail:" + e.getMessage());
//			status_msg = "fail:" + e.getMessage();
			e.printStackTrace();
		} finally {
			if (channel != null) {
				channel.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}
		System.out.println("complete command ");

		// return status_msg.toString();
	}

	@GetMapping("/requestApproved")
	public @ResponseBody String approveVMCreation(@RequestParam String request_id, @RequestParam String serverIP,
			@RequestParam String switchName, Principal principal) {
		System.out.println("Hyper V Approve req");
		System.out.println("approval id = " + request_id);
		System.out.println("Server ip = " + serverIP);
		System.out.println("switch = " + switchName);
		String responce_data = null;
		boolean isApproved = false;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();
		AppUser user = userRepository.findByUsername(username);
		RequestApproval approvalObj = approvalRepository.findById(Long.valueOf(request_id)).get();
		String requestBy = approvalObj.getRequesterName();
		String requesterGroup = userRepository.findByUsername(requestBy).getGroupName();
		String fGroup[] = requesterGroup.split(",");
		System.out.println("(fGroup[0]  :: " + fGroup[0]);
		int instance_id = (int) approvalObj.getRequestId();
		System.out.println("Instance id =" + instance_id);
		CloudInstance obj = repository.findById(instance_id).get();
		try {
			System.out.println("Instance id = " + request_id);
			String response = "";
			String requestType = approvalObj.getRequest_type();
			System.out.println("requet_type = " + requestType);
			if (requestType.equalsIgnoreCase("instance create")) {
				String vm_name = obj.getInstance_name();
				String vm_location_path = obj.getVm_location_path();
				String vhd_path = obj.getVm_location_path() + "\\" + vm_name + "\\" + vm_name + ".vhdx";
				String iso_file_path = subProductRepository.getISOFilePath(obj.getSubproduct_id().getId());

				try {

					Socket socket = new Socket(serverIP, 9005);
					System.out.println("Physical Server IP = " + serverIP);
					VMCreationBean bean = new VMCreationBean();
					bean.setActivity("vm_create");
					bean.setInstanceName(obj.getInstance_name());
					bean.setMemoryStartupBytes(obj.getPrice_id().getRam());
					bean.setVhdPath(vhd_path);
					bean.setVmLocationPath(obj.getVm_location_path());
					bean.setGeneration(obj.getGeneration_type());
					bean.setSwitchName(switchName);
					bean.setNewVHDSizeBytes(obj.getPrice_id().getSsd_disk());
					bean.setvCpu(obj.getPrice_id().getvCpu());
					bean.setIsoFilePath(iso_file_path);
					ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
					outputStream.writeObject(bean);
					outputStream.flush();
					// outputStream.close();
					ObjectInputStream serverResponse = new ObjectInputStream(socket.getInputStream());
					response = (String) serverResponse.readObject();
					System.out.println("Server response: " + response);
					socket.close();
				} catch (Exception e) {
					System.out.println("Exception occured while sending VM object to server : "
							+ obj.getLocation_id().getPhysical_server_iP() + " = " + e);
				}
				// }

				System.out.println("responce_data:" + response);
				String switch_name = "-";
				String VlanSetting = "-";
				String MacAddress = "-";
				String IPAddresses = "-";
				String ComputerName = "-";
				String VMId = "-";
				String state = "off";
				String Status = "-";
				int SizeOfSystemFiles = 0;

				try {

					JSONObject object = new JSONObject(response);
					ComputerName = object.getString("ComputerName");
					VMId = object.getString("VMId");
					// state = object.G("state");
					Status = object.getString("Status");
					SizeOfSystemFiles = object.getInt("SizeOfSystemFiles");
					JSONArray array = object.getJSONArray("NetworkAdapters");
					for (int i = 0; i < array.length(); i++) {
						JSONObject object2 = array.getJSONObject(i);
						switch_name = object2.getString("SwitchName");
						VlanSetting = object2.getString("VlanSetting");
						MacAddress = object2.getString("MacAddress");
						IPAddresses = object2.getString("IPAddresses");
					}
					System.out.println("switch_name:" + switch_name);
					System.out.println("VlanSetting:" + VlanSetting);
					System.out.println("MacAddress:" + MacAddress);
					System.out.println("IPAddresses:" + IPAddresses);
					System.out.println("ComputerName:" + ComputerName);
					System.out.println("VMId:" + VMId);
					System.out.println("state:" + state);
					System.out.println("Status:" + Status);
					System.out.println("SizeOfSystemFiles:" + SizeOfSystemFiles);

					obj.setVlan_setting(VlanSetting);
					obj.setMac_address(MacAddress);
					obj.setComputer_name(ComputerName);
					obj.setVm_id(VMId);
					obj.setVm_state(state);
					obj.setVm_status(Status);
					obj.setSize_of_system_files(SizeOfSystemFiles);
					obj.setVirtualization_type("hyperv");

					obj.setDisk_path(vhd_path);
					obj.setIso_file_path(iso_file_path);
					obj.setPhysicalServerIP(serverIP);
					obj.setCpuAssigned(obj.getPrice_id().getvCpu());
					obj.setMemoryAssigned(obj.getPrice_id().getRam());

					isApproved = true;
				} catch (Exception e) {
					System.out.println("Exception:" + e);
				}

				System.out.println("isVMCreated: " + isApproved);

				try {
					if (isApproved) {
						VPC vpc_id = new VPC();
						Customer customer = new Customer();
						customer.setId(1);
						vpc_id.setId(1);
						obj.setVpc_id(vpc_id);
						obj.setRequest_status("Approved");
//						obj.setGroupName("GroupA");
//						obj.setCustomer_id(customer);

						obj.setMonitoring(true);
						obj.setGroupName(fGroup[0]);
						repository.save(obj);

						approvalObj.setApproved_on(new Timestamp(System.currentTimeMillis()));
						approvalObj.setRequest_status("Approved");
						approvalObj.setApprover_name("Cloud Admin");
						approvalRepository.save(approvalObj);
						// responce_data = "success";

					}
				} catch (Exception e) {
					System.out.println("Exception save:" + e);
				}

			} else if (requestType.equalsIgnoreCase("instance delete")) {

				// ExecutePSCommand execute = new ExecutePSCommand();

				String vm_name = obj.getInstance_name();
				ExecutePSCommand execute = new ExecutePSCommand();
				responce_data = execute.deleteVM(vm_name);
				System.out.println("responce_data:" + responce_data);

				// if (responce_data.equals("VM Delete Successfully")) {

				// repository.deleteById(obj.getId());
				obj.setMonitoring(false);
				repository.save(obj);
				approvalObj.setRequest_status("Approved");
				approvalObj.setApprover_name(username);
				approvalObj.setApproved_on(new Timestamp(System.currentTimeMillis()));
				approvalRepository.save(approvalObj);

				// }

				isApproved = true;

			} else if (requestType.equalsIgnoreCase("instance update")) {
				String subRequestType = approvalObj.getSub_request_type();
				// ExecutePSCommand execute = new ExecutePSCommand();

				if (subRequestType.equalsIgnoreCase("resize")) {

					obj.setPrice_id(approvalObj.getLog_id().getPrice_id());
					repository.save(obj);

					approvalObj.setRequest_status("Approved");
					approvalObj.setApprover_name(username);
					approvalObj.setApproved_on(new Timestamp(System.currentTimeMillis()));
					approvalRepository.save(approvalObj);

					try {
						ExecutePSCommand execute = new ExecutePSCommand();
						String vm_name = obj.getInstance_name();
						String memory_size = obj.getPrice_id().getRam();
						String cpu = obj.getPrice_id().getvCpu();
						String disk_size = obj.getPrice_id().getSsd_disk();
						String disk_path = obj.getDisk_path();

						String responsedata = execute.updateMemoryCPUDiskVM(vm_name, memory_size, disk_path, disk_size,
								cpu);
						isApproved = true;
					} catch (Exception e) {
						System.out.println("Exception occured while memory = " + e);
					}

				}

			}

		} catch (Exception e) {
			System.out.println("Exception occured while approving request for VM creation = " + e);
			// responce_data = "fail";
		}
		System.out.println("Is vm created = " + isApproved);
		return String.valueOf(isApproved);
	}

	@GetMapping("/requestRejected")
	public @ResponseBody String rejectVMCreation(@RequestParam String request_id, @RequestParam String remark,
			Principal principal) {

		String result = "";
		System.out.println(remark);
		System.out.println("aprroval id = " + request_id);
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();
		try {
//			CloudInstance obj = repository.findById(Integer.valueOf(request_id)).get();
//			RequestApproval approvalObj = approvalRepository.findById(Long.valueOf(request_id)).get();

			RequestApproval approvalObj = approvalRepository.findById(Long.valueOf(request_id)).get();
			int instance_id = (int) approvalObj.getRequestId();
			System.out.println("Instance id =" + instance_id);
			CloudInstance obj = repository.findById(instance_id).get();

			obj.setRequest_status("Rejected");
			repository.save(obj);

			approvalObj.setRequest_status("Rejected");
			approvalObj.setDescription(remark);
			approvalObj.setApprover_name(username);
			approvalObj.setApproved_on(new Timestamp(System.currentTimeMillis()));
			approvalRepository.save(approvalObj);
			result = "reject";
		} catch (Exception e) {
			System.out.println("Exception occured while rejecting VM request = " + e);
			result = "exception";
		}
		return result;
	}

	@GetMapping("/rejectAdditionalStorageRequest")
	public @ResponseBody String rejectAdditionalStorageRequest(@RequestParam String request_id,
			@RequestParam String remark, Principal principal) {

		String result = "";
		System.out.println(remark);
		System.out.println("aprroval id = " + request_id);
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();
		try {

			RequestApproval approvalObj = approvalRepository.findById(Long.valueOf(request_id)).get();
			int instance_id = (int) approvalObj.getRequestId();
			System.out.println("Instance id =" + instance_id);
			CloudInstance obj = repository.findById(instance_id).get();

			obj.setRequest_status("Rejected");
			repository.save(obj);

			approvalObj.setRequest_status("Rejected");
			approvalObj.setDescription(remark);
			approvalObj.setApprover_name(username);
			approvalObj.setApproved_on(new Timestamp(System.currentTimeMillis()));
			approvalRepository.save(approvalObj);

			AdditionalStorage st = externalStorageRepository.findByinstId(obj);
			st.setStatus("Reject");
			externalStorageRepository.save(st);

			result = "reject";
		} catch (Exception e) {
			System.out.println("Exception occured while rejecting VM request = " + e);
			result = "exception";
		}
		return result;
	}

	@GetMapping("/currenVM/{log_id}")
	public @ResponseBody String currenVM(@PathVariable int log_id, Principal principal) {

		String result = "";
		try {

			CloudInstanceLog logData = cloudInstanceLogRepository.findById(log_id).get();
			result = logData.toString();

		} catch (Exception e) {
			System.out.println("Exception occured while fetching log data = " + e);
		}
		return result;
	}

	@GetMapping("/getSwitchByIP")
	public @ResponseBody String getSwitchByIP(@RequestParam String serverIP) {
		System.out.println("getSwitchByIP  controller calledd:" + serverIP);
		String json = null;

		try {

			List<Object[]> list = switchRepository.findByphysicalServerIP(serverIP);
			System.out.println("list size:" + list.size());
			json = new ObjectMapper().writeValueAsString(list);
		} catch (Exception e) {
			System.out.println("exception :" + e);
		}
		return json;
	}

	// get server ip by virtualization type
	@GetMapping("/getIpByVirtualizationType")
	public @ResponseBody String getIpByVirtualizationType(@RequestParam String serverIP) {
		System.out.println("getIpByVirtualizationType  controller calledd:" + serverIP);
		String json = null;
		try {
			List<Object[]> list = addPhysicalServerRepository.getPhysicalServerIpByVirtualizationType(serverIP);
			System.out.println("list size:" + list.size());
			json = new ObjectMapper().writeValueAsString(list);
		} catch (Exception e) {
			System.out.println("exception :" + e);
		}
		return json;
	}
	
	
	// get server ip by virtualization type
	@GetMapping("/getDockerNetworks")
	public @ResponseBody String getDockerNetworks() {
		System.out.println("getIpByVirtualizationType  controller calledd:");
		String json = null;
		try {
			List<Object[]> list = DiscoverDockerNetworkRepository.getDockerNetworks();
			System.out.println("list size:" + list.size());
			json = new ObjectMapper().writeValueAsString(list);
		} catch (Exception e) {
			System.out.println("exception :" + e);
		}
		return json;
	}
	
	

	// VM Deletion request
	@GetMapping("/approveVMDeletion")
	public @ResponseBody String approveVMDeletion(@RequestParam String requestId, Principal principal) {
		String responce_data = null;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();
		AppUser user = userRepository.findByUsername(username);
		RequestApproval approvalObj = approvalRepository.findById(Long.valueOf(requestId)).get();
		int instance_id = (int) approvalObj.getRequestId();
		System.out.println("approveVMDeletion Instance id =" + instance_id);
		CloudInstance obj = repository.findById(instance_id).get();

		String virtType = obj.getVirtualization_type();
		System.out.println("approveVMDeletion virtType=" + virtType);
		String vm_name = obj.getInstance_name();

		if (virtType.trim().equalsIgnoreCase("kvm")) {

			
			String Physicalserverip = obj.getPhysicalServerIP();
			System.out.println("approveVMDeletion Physicalserverip=" + Physicalserverip);
			String sshusername = addPhysicalServerRepository.findByserverIP(Physicalserverip.trim()).getSsh_host();
			System.out.println("approveVMDeletion sshusername=" + sshusername);
			String sshPassword = addPhysicalServerRepository.findByserverIP(Physicalserverip.trim()).getSsh_password();
			System.out.println("approveVMDeletion sshPassword=" + sshPassword);

			String createVMCommand = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh destroy " + vm_name
					+ "";

			executeRemoteCommand(createVMCommand, sshusername, sshPassword, Physicalserverip);

			createVMCommand = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh undefine " + vm_name + "";

			executeRemoteCommand(createVMCommand, sshusername, sshPassword, Physicalserverip);

			responce_data = "success";

			if (responce_data.equalsIgnoreCase("success")) {
				obj.setMonitoring(false);
				obj.setInstance_name(vm_name+"_");
				repository.save(obj);

				approvalObj.setRequest_status("Approved");
				approvalObj.setApprover_name(username);
				approvalObj.setApproved_on(new Timestamp(System.currentTimeMillis()));
				approvalRepository.save(approvalObj);
			}

		} else {

			try {
				Socket socket = new Socket(obj.getPhysicalServerIP(), 9005);
				System.out.println("Physical Server IP = " + obj.getPhysicalServerIP());
				VMCreationBean bean = new VMCreationBean();
				bean.setActivity("vm_delete");
				bean.setInstanceName(obj.getInstance_name());
				ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
				outputStream.writeObject(bean);
				outputStream.flush();
				ObjectInputStream serverResponse = new ObjectInputStream(socket.getInputStream());
				responce_data = (String) serverResponse.readObject();
				System.out.println("Server response: " + responce_data);
				if (responce_data.equalsIgnoreCase("success")) {
					obj.setMonitoring(false);
					obj.setInstance_name(vm_name+"_");
					repository.save(obj);
					approvalObj.setRequest_status("Approved");
					approvalObj.setApprover_name(username);
					approvalObj.setApproved_on(new Timestamp(System.currentTimeMillis()));
					approvalRepository.save(approvalObj);
				}

				socket.close();

			} catch (Exception e) {
				System.out.println("Exception occured while deleting VM = " + e);
				responce_data = "fail";
			}
		}

		return responce_data;
	}

	@GetMapping("/approveVMUpdate")
	public @ResponseBody String approveVMUpdate(@RequestParam String requestId, Principal principal) {

		boolean status = false;
		StringBuilder message = new StringBuilder();
		String responce_data = null;
		boolean isCPUIncrease = false;
		boolean isRAMIncrease = false;
		CloudInstanceLog log = new CloudInstanceLog();
		RequestApproval approvalObj = null;
		int instance_id = 0;
		System.out.println("Instance id =" + instance_id);
		CloudInstance obj = null;
		String username = null;

		try {
			approvalObj = approvalRepository.findById(Long.valueOf(requestId)).get();
			instance_id = (int) approvalObj.getRequestId();
			obj = repository.findById(instance_id).get();

			System.out.println("Resize VM CPU,RAM " + requestId);
			User loginedUser = (User) ((Authentication) principal).getPrincipal();
			username = loginedUser.getUsername();
			// AppUser user = userRepository.findByUsername(username);
			// String requestBy = approvalObj.getRequesterName();

			int id = obj.getId();

			String virtType = obj.getVirtualization_type();
			String serverIP = obj.getPhysicalServerIP();
			String vm_name = obj.getInstance_name();


			String subRequestType = approvalObj.getSub_request_type();
			// ExecutePSCommand execute = new ExecutePSCommand();

			String input = approvalObj.getNewData();
			input = input.replace("{", "{\"").replace("}", "\"}").replace(", ", "\", \"").replace("=", "\": \"");

			input = input.replace("\": \"\"", "\": \"\"");
			JSONObject jsonObject = new JSONObject(input);
			String ram_val = jsonObject.getString("ram");
			String cpu_val = jsonObject.getString("cpu");

			if (cpu_val != null && cpu_val.length() > 0) {
				isCPUIncrease = true;
				System.out.println("isCPUIncrease :" + isCPUIncrease);
			}
			if (ram_val != null && ram_val.length() > 0) {
				isRAMIncrease = true;
				System.out.println("isRAMIncrease :" + isRAMIncrease);
			}

			if (subRequestType.equalsIgnoreCase("resize")) {
				System.out.println("User Aprove Resize");

				if (virtType.trim().equalsIgnoreCase("kvm")) {
					// System.out.println("Resize KVM VM CPU,RAM kvm" + requestId);
					System.out.println("User Aprove KVM Resize");

					// String ssd_size = jsonObject.getString("storage");
					// String disk_path = obj.getDisk_path();

					System.out.println("Physical Server IP = " + obj.getPhysicalServerIP());

					System.out.println("New RAM = " + ram_val);
					System.out.println("New CPU = " + cpu_val);

					// start
					System.out.println("SuperAdmin-KVM Resize");
					String sshusername = addPhysicalServerRepository.findByserverIP(serverIP.trim()).getSsh_host();
					String sshPassword = addPhysicalServerRepository.findByserverIP(serverIP.trim()).getSsh_password();

					KVMResize kvmObj = new KVMResize(serverIP, vm_name, sshusername, sshPassword);
					// CPU Increase
					boolean isCPUFail = false;

					if (isCPUIncrease) {
						System.out.println("In SuperAdmin-KVM CPU Resize");
						int increaseCPU = Integer.parseInt(cpu_val);
						System.out.println("SuperAdmin-KVM CPU:" + increaseCPU);
						CommandResult result = kvmObj.changeKVMCPU(increaseCPU);
						status = result.isStatus();
						message.append(result.getMessage());
						if (status) {
							try {
								repository.updateVMCPU(id, cpu_val);
							} catch (Exception e) {
								System.out.print("Exception DB update CPU:" + e.getMessage());
							}
							log.setCpuAssigned(cpu_val);
						} else {
							isCPUFail = true;
						}
					}
					// RAM Increase

					if (isRAMIncrease && isCPUFail == false) {
						System.out.println("In SuperAdmin-KVM RAM Resize");
						int increaseRAM = Integer.parseInt(CMPUtil.parseNumberToString(ram_val));
						System.out.println("SuperAdmin-KVM RAM:" + increaseRAM);
						CommandResult result = kvmObj.changeKVMRAM(increaseRAM);
						status = result.isStatus();
						if (status) {

							try {
								repository.updateVMRam(id, ram_val);
							} catch (Exception e) {
								System.out.print("Exception DB update RAM:" + e.getMessage());
							}

							log.setMemoryAssigned(ram_val);
						}
						message.append(result.getMessage());
					}

					// End

				} else {

					System.out.println("User Aprove-Hyper-v Resize");
					Socket socket = null;
					try {
						 socket = new Socket(obj.getPhysicalServerIP(), 9005);
						VMCreationBean bean = new VMCreationBean();
						bean.setActivity("vm_update");
						bean.setInstanceName(vm_name);
						bean.setvCpu(cpu_val);
						bean.setMemoryStartupBytes(ram_val);
						bean.setCPUIncrease(isCPUIncrease);
						bean.setRAMIncrease(isRAMIncrease);
						ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
						outputStream.writeObject(bean);
						outputStream.flush();
						ObjectInputStream serverResponse = new ObjectInputStream(socket.getInputStream());
						responce_data = (String) serverResponse.readObject();
						System.out.println("Server response: " + responce_data);

						if (!responce_data.contains("Error")) {
							System.out.println("Hyper Resize sucesss:");
							if (isRAMIncrease) {
								try {
									repository.updateVMRam(id, ram_val);
								} catch (Exception e) {
									System.out.print("Exception DB update RAM:" + e.getMessage());
								}
								log.setMemoryAssigned(ram_val);
							}
							if (isCPUIncrease) {

								try {
									repository.updateVMCPU(id, cpu_val);
								} catch (Exception e) {
									System.out.print("Exception DB update CPU:" + e.getMessage());
								}
								log.setCpuAssigned(cpu_val);
							}

							status = true;
						} else {
							status = false;
						}
					} catch (Exception e) {
						System.out.println("Exception occured while updating VM = " + e);
						responce_data = "fail";
						status = false;
						message = message.append("Error: " + e.getMessage());

					}
					finally {
						try {
							if (socket != null) {
								socket.close();
							}
						} catch (Exception e) {
						}
					}

				}
			}

		} catch (Exception e) {
			System.out.print("Exception command:" + e.getMessage());
			status = false;
			message = message.append("Error: " + e.getMessage());
		}

		if (status) {
			try {
				log.setInstance_ip(obj.getInstance_ip());
				log.setInstance_name(obj.getInstance_name());
				log.setInstance_password(obj.getInstance_password());
				log.setDisk_path(obj.getDisk_path());
				log.setGeneration_type(obj.getGeneration_type());
				log.setIso_file_path(obj.getIso_file_path());
				log.setVm_location_path(obj.getVm_location_path());
				log.setLocation_id(obj.getLocation_id());
				log.setPrice_id(obj.getPrice_id());
				log.setSecurity_group_id(obj.getSecurity_group_id());
				log.setSubproduct_id(obj.getSubproduct_id());
				log.setSwitch_id(obj.getSwitch_id());
				log.setVpc_id(obj.getVpc_id());
				log.setRequest_type("instance update");
				cloudInstanceLogRepository.save(log);
			} catch (Exception e) {
				System.out.print("Exception DB save log:" + e.getMessage());
			}

			try {
				approvalObj.setRequest_status("Approved");
				approvalObj.setApprover_name(username);
				approvalObj.setApproved_on(new Timestamp(System.currentTimeMillis()));
				approvalRepository.save(approvalObj);
			} catch (Exception e) {
				System.out.print("Exception DB approvalObj save log:" + e.getMessage());
			}
			responce_data = "success";

			System.out.print("Resize VM USER successfully");
			// redirectAttributes.addFlashAttribute("updateVMCommandOP", "success");
		} else {
			responce_data = message.toString();
			System.out.print("Failed VM update:" + responce_data);
			// redirectAttributes.addFlashAttribute("updateVMCommandOP",
			// message.toString());
		}

		return responce_data;

	}

	// End VM Deletion request

	// VM update
//	@GetMapping("/approveVMUpdate")
//	public @ResponseBody String approveVMUpdate(@RequestParam String requestId, Principal principal) {
//
//		String responce_data = null;
//		System.out.println("Resize VM CPU,RAM " + requestId);
//		User loginedUser = (User) ((Authentication) principal).getPrincipal();
//		String username = loginedUser.getUsername();
//		AppUser user = userRepository.findByUsername(username);
//		RequestApproval approvalObj = approvalRepository.findById(Long.valueOf(requestId)).get();
//		int instance_id = (int) approvalObj.getRequestId();
//		System.out.println("Instance id =" + instance_id);
//
//		String requestBy = approvalObj.getRequesterName();
//
//		CloudInstance obj = repository.findById(instance_id).get();
//
//		String virtType = obj.getVirtualization_type();
//		String serverIP = obj.getPhysicalServerIP();
//
//		String subRequestType = approvalObj.getSub_request_type();
//		// ExecutePSCommand execute = new ExecutePSCommand();
//
//		if (subRequestType.equalsIgnoreCase("resize")) {
//			System.out.println("Resize VM CPU,RAM resize");
//
//			if (virtType.trim().equalsIgnoreCase("kvm")) {
//				System.out.println("Resize VM CPU,RAM  kvm" + requestId);
//				String input = approvalObj.getNewData();
//				 input = input.replace("{", "{\"")
//	                     .replace("}", "\"}")
//	                     .replace(", ", "\", \"")
//	                     .replace("=", "\": \"");
//	        
//				 input = input.replace("\": \"\"", "\": \"\"");
//	                     
//				 
//				JSONObject jsonObject = new JSONObject(input);
//				 
//				String vm_name = obj.getInstance_name();
//				String ram = jsonObject.getString("ram");
//				String cpu = jsonObject.getString("cpu");
//				//String ssd_size = jsonObject.getString("storage");
//				String disk_path = obj.getDisk_path();
//
//				System.out.println("Physical Server IP = " + obj.getPhysicalServerIP());
//				
//				System.out.println("New RAM = "+ram);
//				System.out.println("New CPU = "+cpu);
//				//System.out.println("New Storage = "+ssd_size);
//
////				String sshusername = addPhysicalServerRepository.findByserverIP(serverIP.trim()).getSsh_host();
////				String sshPassword = addPhysicalServerRepository.findByserverIP(serverIP.trim()).getSsh_password();
////				obj.setPrice_id(approvalObj.getLog_id().getPrice_id());
////				repository.save(obj);
////
////				try {
//////					ExecutePSCommand execute = new ExecutePSCommand();
////					String vm_name = obj.getInstance_name();
////					String ram = obj.getPrice_id().getRam();
////					String cpu = obj.getPrice_id().getvCpu();
////					String ssd_size = obj.getPrice_id().getSsd_disk();
////					String disk_path = obj.getDisk_path();
////
////					String vcpus = parseNumberToString(cpu);
////					String rams = parseNumberToString(ram);
////					String disksize = parseNumberToString(ssd_size);
////
////					int ramInGb = Integer.parseInt(rams.split(" ")[0]);
////					int ramInMb = ramInGb * 1024;
////					int numVcpus = Integer.parseInt(vcpus.split(" ")[0]);
////
////					System.out.println("VM Name: " + vm_name);
//////					System.out.println("ISO File Path: " + iso_file_path);
////					System.out.println("vcpus: " + vcpus);
////					System.out.println("Number of vCPUs: " + numVcpus);
////					System.out.println("rams: " + rams);
////					System.out.println("RAM in MB: " + ramInMb);
////
////					System.out.println("Physical Server IP = " + obj.getPhysicalServerIP());
////
////					changeKVMVMCPU(vm_name, numVcpus, ramInMb, sshusername, sshPassword, serverIP);
////					changeKVMVMMemory(vm_name, numVcpus, ramInMb, sshusername, sshPassword, serverIP);
////
////					responce_data = "success";
////					if (responce_data.equalsIgnoreCase("success")) {
//////						obj.setPrice_id(approvalObj.getLog_id().getPrice_id());
//////						repository.save(obj);
////
////						approvalObj.setRequest_status("Approved");
////						approvalObj.setApprover_name(username);
////						approvalObj.setApproved_on(new Timestamp(System.currentTimeMillis()));
////						approvalRepository.save(approvalObj);
////					}
////
////				} catch (Exception e) {
////					System.out.println("Exception occured while updating VM = " + e);
////					responce_data = "fail";
////				}
//
//			} else {
//
////
////			approvalObj.setRequest_status("Approved");
////			approvalObj.setApprover_name(username);
////			approvalObj.setApproved_on(new Timestamp(System.currentTimeMillis()));
////			approvalRepository.save(approvalObj);
//
//				try {
////				ExecutePSCommand execute = new ExecutePSCommand();
//					
//					//Added By rohit to get values of cpu, ram, disk 
//					String input = approvalObj.getNewData();
//					 input = input.replace("{", "{\"")
//		                     .replace("}", "\"}")
//		                     .replace(", ", "\", \"")
//		                     .replace("=", "\": \"");
//		        
//					 input = input.replace("\": \"\"", "\": \"\"");
//		                     
//					 
//					JSONObject jsonObject = new JSONObject(input);
//					 
//					String vm_name = obj.getInstance_name();
//					String ram = jsonObject.getString("ram");
//					String cpu = jsonObject.getString("cpu");
//					String ssd_size = jsonObject.getString("storage");
//					String disk_path = obj.getDisk_path();
//
//					System.out.println("Physical Server IP = " + obj.getPhysicalServerIP());
//					
//					System.out.println("New RAM = "+ram);
//					System.out.println("New CPU = "+cpu);
//					System.out.println("New Storage = "+ssd_size);
//
////					Socket socket = new Socket(obj.getPhysicalServerIP(), 9005);
////
////					VMCreationBean bean = new VMCreationBean();
////					bean.setActivity("vm_update");
////					bean.setInstanceName(vm_name);
////					bean.setvCpu(cpu);
////					bean.setMemoryStartupBytes(ram);
////					bean.setVhdPath(disk_path);
////					bean.setNewVHDSizeBytes(ssd_size);
////
////					ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
////					outputStream.writeObject(bean);
////					outputStream.flush();
////					ObjectInputStream serverResponse = new ObjectInputStream(socket.getInputStream());
////					responce_data = (String) serverResponse.readObject();
////					System.out.println("Server response: " + responce_data);
////
////					if (responce_data.equalsIgnoreCase("success")) {
//////					obj.setPrice_id(approvalObj.getLog_id().getPrice_id());
//////					repository.save(obj);
////						obj.setPrice_id(approvalObj.getLog_id().getPrice_id());
////						repository.save(obj);
////
////						approvalObj.setRequest_status("Approved");
////						approvalObj.setApprover_name(username);
////						approvalObj.setApproved_on(new Timestamp(System.currentTimeMillis()));
////						approvalRepository.save(approvalObj);
////					}
////
////					socket.close();
//
//				} catch (Exception e) {
//					System.out.println("Exception occured while updating VM = " + e);
//					responce_data = "fail";
//				}
//			}
//		}
//		return responce_data;
//
//	}

	private void changeKVMVMMemory(String vm_name, int numVcpus, int ramInMb, String sshusername, String sshPassword,
			String serverIP) {

		String vmName = vm_name; // Replace with your VM name
		int maxMemory = ramInMb; // Set maximum memory in MiB (e.g., 8192 MiB = 8 GiB)
		int activeMemory = ramInMb; // Set desired active memory in MiB (e.g., 4096 MiB = 4 GiB)

		// Construct the multi-command string
		String commands = String.format(
				// "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh shutdown %s; " +
				// "sleep 20; " + // Give time for the VM to shut down
				"export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh setmaxmem %s %d --config; " + // Set
																											// maximum
																											// memory
						"export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh setmem %s %d --config; ", // Set
																												// active
																												// memory
				// "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh start %s",
				vmName, maxMemory * 1024, vmName, activeMemory * 1024, vmName);

		try {
			executeSSHCommandsformemorychange(commands, sshusername, sshPassword, serverIP);
			System.out.println("Memory increase commands executed successfully.");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void executeSSHCommandsformemorychange(String commands, String sshusername, String sshPassword,
			String serverIP) throws JSchException, IOException {

		JSch jsch = new JSch();
		Session session = jsch.getSession(sshusername, serverIP, 22);
		session.setPassword(sshPassword);

		// Configure SSH session settings
		session.setConfig("StrictHostKeyChecking", "no");
		session.connect();

		// Open an exec channel
		ChannelExec channel = (ChannelExec) session.openChannel("exec");
		channel.setCommand(commands);

		// Capture output
		channel.setOutputStream(System.out);
		channel.setErrStream(System.err);
		channel.connect();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		}

		channel.disconnect();
		session.disconnect();

	}

	private void changeKVMVMCPU(String vm_name, int numVcpus, int ramInMb, String sshusername, String sshPassword,
			String serverIP) throws IOException {

		String vmName = vm_name; // Replace with your VM name
		int maxCpuCount = numVcpus; // New maximum allowable vCPUs
		int activeCpuCount = numVcpus; // Desired active vCPUs

		// Construct the multi-command string
//        String commands = String.format(
//            "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh shutdown %s; " +
//            "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh setvcpus %s %d --maximum --config; " +  // Set maximum vCPU count
//            "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh setvcpus %s %d --config; " +           // Set active vCPU count
//            "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh start %s", 
//            vmName, vmName, maxCpuCount, vmName, activeCpuCount, vmName
//        );

		String commands = String.format(
				// "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh shutdown %s; " +
				// "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/sleep 20; " + // Give
				// time for the VM to shut down
				"export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh setvcpus %s %d --maximum --config; " + // Set
																													// maximum
																													// vCPU
																													// count
						"export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh setvcpus %s %d --config; ", // Set
																													// active
																													// vCPU
																													// count
				// "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh start %s",
				vmName, maxCpuCount, vmName, activeCpuCount, vmName);

		try {
			executeSSHCommandsforcpu(commands, sshusername, sshPassword, serverIP);
			System.out.println("update Commands executed successfully.");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// VM update

	private void executeSSHCommandsforcpu(String commands, String sshusername, String sshPassword, String serverIP)
			throws JSchException, IOException {

		JSch jsch = new JSch();
		Session session = jsch.getSession(sshusername, serverIP, 22);
		session.setPassword(sshPassword);

		// Configure SSH session settings
		session.setConfig("StrictHostKeyChecking", "no");
		session.connect();

		// Open an exec channel
		ChannelExec channel = (ChannelExec) session.openChannel("exec");
		channel.setCommand(commands);

		// Capture output
		channel.setOutputStream(System.out);
		channel.setErrStream(System.err);
		channel.connect();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		}

		channel.disconnect();
		session.disconnect();

	}

	// Additional storage update
	@GetMapping("/approveAdditionalStorageRequest")
	public @ResponseBody String approveAdditionalStorageRequest(@RequestParam String requestId, Principal principal) {

		String responce_data = null;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();
		AppUser user = userRepository.findByUsername(username);

		try {

			RequestApproval approvalObj = approvalRepository.findById(Long.valueOf(requestId)).get();
			int instance_id = (int) approvalObj.getRequestId();
			System.out.println("Instance id =" + instance_id);
			CloudInstance obj = repository.findById(instance_id).get();

			AdditionalStorage storage = externalStorageRepository.findByinstId(obj);

			System.out.println("Physical Server IP = " + obj.getPhysicalServerIP());

			Socket socket = new Socket(obj.getPhysicalServerIP(), 9005);

			VMCreationBean bean = new VMCreationBean();
			bean.setActivity("vm_additionalStorage");
			bean.setInstanceName(storage.getInstance_id().getInstance_name());
			bean.setVhdPath(storage.getStoragePath());
			bean.setNewVHDSizeBytes(storage.getStorage_size());

			ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
			outputStream.writeObject(bean);
			outputStream.flush();
			ObjectInputStream serverResponse = new ObjectInputStream(socket.getInputStream());
			responce_data = (String) serverResponse.readObject();
			System.out.println("Server response: " + responce_data);

			if (responce_data.equalsIgnoreCase("success")) {

				approvalObj.setRequest_status("Approved");
				approvalObj.setApprover_name(username);
				approvalObj.setApproved_on(new Timestamp(System.currentTimeMillis()));
				approvalRepository.save(approvalObj);

				storage.setStatus("Approved");
				externalStorageRepository.save(storage);
			}

			socket.close();

		} catch (Exception e) {
			System.out.println("Exception occured while updating additional storage of VM = " + e);
			responce_data = "fail";
		}

		return responce_data;
	}

}
