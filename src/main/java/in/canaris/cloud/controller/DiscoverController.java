package in.canaris.cloud.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import in.canaris.cloud.entity.AppUser;
import in.canaris.cloud.entity.CloudInstance;
import in.canaris.cloud.entity.CloudInstanceLog;
import in.canaris.cloud.entity.Discount;
import in.canaris.cloud.entity.KVMDriveDetails;
import in.canaris.cloud.entity.Location;
import in.canaris.cloud.entity.Price;
import in.canaris.cloud.entity.SecurityGroup;
import in.canaris.cloud.entity.SubProduct;
import in.canaris.cloud.entity.Switch;
import in.canaris.cloud.entity.VMCreationBean;
import in.canaris.cloud.entity.VPC;
import in.canaris.cloud.openstack.repository.SecurityGroupRepository;
import in.canaris.cloud.repository.AddPhysicalServerRepository;
import in.canaris.cloud.repository.CloudInstanceLogRepository;
import in.canaris.cloud.repository.CloudInstanceRepository;
import in.canaris.cloud.repository.DiscountRepository;
import in.canaris.cloud.repository.FirewallRepository;
import in.canaris.cloud.repository.KVMDriveDetailsRepository;
import in.canaris.cloud.repository.LocationRepository;
import in.canaris.cloud.repository.PhysicalServerRepository;
import in.canaris.cloud.repository.PriceRepository;
import in.canaris.cloud.repository.SubProductRepository;
import in.canaris.cloud.repository.SwitchRepository;
import in.canaris.cloud.repository.VPCRepository;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import javax.sound.midi.Soundbank;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

@Controller
@RequestMapping("/discover")
public class DiscoverController {

	@Autowired
	private PhysicalServerRepository repository;

	@Autowired
	private AddPhysicalServerRepository addPhysicalServerRepository;

	@Autowired
	private CloudInstanceRepository cloudInstanceRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private VPCRepository vpcRepository;

	@Autowired
	private FirewallRepository securityGroupRepository;

	@Autowired
	private DiscountRepository discountRepository;

	@Autowired
	private PriceRepository priceRepository;

	@Autowired
	private SwitchRepository switchRepository;

	@Autowired
	private SubProductRepository subProductRepository;

	@Autowired
	private KVMDriveDetailsRepository kvmDriveDetailsRepository;
	
	@Autowired
	private CloudInstanceLogRepository cloudInstanceLogRepository;

	final String var_function_name = "discover"; // small letter
	final String disp_function_name = "Discover"; // capital letter

	@GetMapping("/view")
	public ModelAndView add(Principal principal) {
		ModelAndView mav = new ModelAndView(var_function_name + "_view");
		if (principal == null) {
			ModelAndView mav1 = new ModelAndView("welcomePage");
			return mav1;
		}
		mav.addObject("pageTitle", "" + disp_function_name);
		mav.addObject("action_name", var_function_name);
		mav.addObject("physicalServerIPList", addPhysicalServerRepository.getPhysicalServerIPs());
		AddPhysicalServer objEnt = new AddPhysicalServer();
		mav.addObject("objEnt", objEnt);
		return mav;
	}

	@GetMapping("/getIpList")
	public @ResponseBody String getIpList() {
		System.out.println("getSubProductName  controller calledd:");
		String json = null;

		try {
			List<Object[]> PhysicalServeriplist = repository.getAllPhysicalServerip();
			json = new ObjectMapper().writeValueAsString(PhysicalServeriplist);
		} catch (Exception e) {
			System.out.println("exception :" + e);
		}
		return json;
	}

	@GetMapping("/save")
	public @ResponseBody String discover(@RequestParam String serverIP, @RequestParam String discoverType,
			RedirectAttributes redirectAttributes) {
		System.out.println("In discover TCP ");
		String result = "";

		String servertype = addPhysicalServerRepository.findByserverIP(serverIP.trim()).getVirtualization_type();
		System.out.println(servertype);

		if (servertype.trim().equalsIgnoreCase("kvm") && discoverType.trim().equalsIgnoreCase("vm_discover")) {

			try {

				String Host = serverIP.trim();
				String sshusername = addPhysicalServerRepository.findByserverIP(serverIP.trim()).getSsh_host();
				String sshPassword = addPhysicalServerRepository.findByserverIP(serverIP.trim()).getSsh_password();

				discoverKVMVMS(Host, sshusername, sshPassword, serverIP);

				result = "success";
			} catch (Exception e) {
				System.out.println("Excpetion:" + e);
				result = e.toString();
			}

		} else {

			try {

				System.out.println("Server IP = " + serverIP + "\nDiscoverType = " + discoverType);
				ObjectOutputStream outputStream = null;
				Socket socket = null;
				try {
					socket = new Socket(serverIP, 9005);
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
		}
		return result;
	}

	private void discoverKVMVMS(String host2, String sshusername, String sshPassword, String serverIP) {

		System.out.println("Discover KVM VMs");
		String user = sshusername;
		String host = host2;
		String password = sshPassword;
		System.out.println(user + " " + host + " " + password);

		String command = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh list --all"; // List VM names

		JSch jsch = new JSch();
		Session session = null;
		ChannelExec channel = null;
		InputStream in = null;
		BufferedReader reader = null;
		List<String> vmNamess = new ArrayList<>();
		try {
			session = jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);
			channel.setErrStream(System.err);
			in = channel.getInputStream();
			channel.connect();
			reader = new BufferedReader(new InputStreamReader(in));
			// System.out.println(reader);
			String readline;
			Pattern pattern = Pattern.compile("^\\s*[-\\d]+\\s+([\\w.-]+)"); // Regex pattern to match VM name lines

			while ((readline = reader.readLine()) != null) {
				System.out.println("" + readline);
				Matcher matcher = pattern.matcher(readline);
				if (matcher.find()) {
					String extractedVmName = matcher.group(1); // Capture the VM name
					System.out.println("VM Name: " + extractedVmName);
					vmNamess.add(extractedVmName); // Add VM name to the list
				}
			}
		} catch (Exception e) {
			System.out.println("Exception1: " + e);
		}

		finally {
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

//Delete VM which is not present in console

		try {
			System.out.println("Actual VM  List: " + vmNamess + ":size:" + vmNamess.size());
			List<String> db_vmlist = cloudInstanceRepository.getInstanceNameByServerIP(serverIP,true);
			System.out.println("DB VM list:" + db_vmlist + ":size:" + db_vmlist.size());
			db_vmlist.removeAll(vmNamess); // Remove all elements of B from A
			System.out.println("Extra elements in DB: " + db_vmlist + ":" + db_vmlist.size());
			Iterator<String> itr = db_vmlist.iterator();
			while (itr.hasNext()) {
				String delete_vmname = itr.next();
				System.out.println("Delete VM:: " + delete_vmname);
				
				
				try {
					cloudInstanceRepository.updateInstanceFalse( serverIP, delete_vmname,delete_vmname+"_", false);
				} catch (Exception e) {
					System.out.print("Exception DB update RAM:" + e.getMessage());
				}
				
				try
				{
			    CloudInstance entity = cloudInstanceRepository.findByInstanceNamee(delete_vmname);
				CloudInstanceLog log = new CloudInstanceLog();
				log.setInstance_ip(entity.getInstance_ip());
				log.setInstance_name(entity.getInstance_name());
				log.setInstance_password(entity.getInstance_password());
				log.setDisk_path(entity.getDisk_path());
				log.setGeneration_type(entity.getGeneration_type());
				log.setIso_file_path(entity.getIso_file_path());
				log.setVm_location_path(entity.getVm_location_path());
				log.setLocation_id(entity.getLocation_id());
				log.setPrice_id(entity.getPrice_id());
				log.setSecurity_group_id(entity.getSecurity_group_id());
				log.setSubproduct_id(entity.getSubproduct_id());
				log.setSwitch_id(entity.getSwitch_id());
				log.setVpc_id(entity.getVpc_id());
				log.setRequest_type("instance Deleted");
				
				cloudInstanceLogRepository.save(log);
				} catch (Exception e) {
					System.out.print("Exception log delete vm:" + e.getMessage());
				}
				

			}

		} catch (Exception e) {
			System.out.println("Exception occured while checking for delete db vm = " + e);
		}

		try {
			Iterator<String> itr = vmNamess.iterator();
			while (itr.hasNext()) {
				String vmName = itr.next();
				System.out
						.println("##########################################################################" + vmName);
				if (vmName.trim().equalsIgnoreCase("")) {
				} else {

					String vmdetailsresult = fetchVmDetails(host2, sshusername, sshPassword, vmName);
					String[] lines = vmdetailsresult.split("\\n");
					String KVMvmName = "";
					String cpuCount = "";
					String memoryMB = "";
					String osType = "";
					String osVariant = "";
					String cdRomPath = "";
					String diskPath = "";
					String macAddress = "";
					String ipAddress = "";
					String memoryGB = "";
					// Iterate over lines and assign values based on prefix
					for (String line : lines) {
						if (line.startsWith("Details for VM: ")) {
							KVMvmName = line.substring("Details for VM: ".length());
						} else if (line.startsWith("CPU Count: ")) {
							cpuCount = line.substring("CPU Count: ".length());
						} else if (line.startsWith("Memory (MB): ")) {
							memoryMB = line.substring("Memory (MB): ".length());
							int memoryMBValue = Integer.parseInt(memoryMB);

							// Convert from MB to GB
							double memoryGBValue = memoryMBValue / 1024.0; // Use 1024.0 to get a double result

							// If you want to store it back as a String, you can do
							memoryGB = String.format("%.2f", memoryGBValue);
						} else if (line.startsWith("OS Type: ")) {
							osType = line.substring("OS Type: ".length());
						} else if (line.startsWith("OS Variant: ")) {
							osVariant = line.substring("OS Variant: ".length());
						} else if (line.startsWith("CD-ROM Path: ")) {
							cdRomPath = line.substring("CD-ROM Path: ".length());
						} else if (line.startsWith("Disk Path: ")) {
							diskPath = line.substring("Disk Path: ".length());
						} else if (line.startsWith("MAC Address: ")) {
							macAddress = line.substring("MAC Address: ".length());
						} else if (line.startsWith("IP Address: ")) {
							ipAddress = line.substring("IP Address: ".length());
						}
					}

					try {
						fetchdrivedetails(host2, sshusername, sshPassword, vmName);
					} catch (Exception e) {
						System.out.println("Exception: " + e);

					}

					String detailsoutpuresult = basicVmDetailscall(host2, sshusername, sshPassword, vmName);

					String[] detailLines = detailsoutpuresult.split("\n");
					String vmState2 = "";
					String osType2 = "";
					String vmId2 = "";
					for (String detailLine : detailLines) {
						if (detailLine.startsWith("VM State:")) {
							vmState2 = detailLine.split(":")[1].trim(); // Assign to variable
						}
						if (detailLine.startsWith("OS Type:")) {
							osType2 = detailLine.split(":")[1].trim(); // Assign to variable
						}
						if (detailLine.startsWith("VM Id:")) {
							vmId2 = detailLine.split(":")[1].trim(); // Assign to variable
						}
					}

					// Now you can use the variables as needed
					System.out.println("VM State: " + vmState2);
					System.out.println("OS Type: " + osType2);
					System.out.println("VM Id: " + vmId2);
					String instancekvmipAddressmain = "-";

					CloudInstance cloudInstance = cloudInstanceRepository
							.findByInstanceNameAndVirtualizationType(KVMvmName, "KVM", serverIP);

					if (cloudInstance == null) {
						System.out.println("New VM: " + KVMvmName);
						// If not found, create a new instance
						cloudInstance = new CloudInstance();
						cloudInstance.setInstance_name(KVMvmName);
					} else {
						System.out.println("Existing VM: " + KVMvmName);

					}

					Location location = locationRepository.findById(5).orElse(null);
					VPC vpc = vpcRepository.findById(1).orElse(null);
					SecurityGroup securityGroup = securityGroupRepository.findById(1).orElse(null);
					Discount discount = discountRepository.findById(1).orElse(null);
					Price price = priceRepository.findById(1).orElse(null);
//						Price price = null;
					Switch switchEntity = switchRepository.findById(1).orElse(null);
					SubProduct SubProductentity = subProductRepository.findById(1).orElse(null);

					// Set or update properties
					cloudInstance.setCpuAssigned(cpuCount);
					cloudInstance.setMemory_assignedkvm(memoryGB);
					cloudInstance.setMemoryAssigned(memoryGB);
					cloudInstance.setVirtualization_type(osType);
					cloudInstance.setVm_status(osVariant);
//					cloudInstance.setIso_file_path(cdRomPath);
					cloudInstance.setVm_location_path(diskPath);
					cloudInstance.setGroupName("KVM_Group");
//					cloudInstance.setsw
					cloudInstance.setDisk_path(cdRomPath);
					cloudInstance.setRequest_status("Approved");
					cloudInstance.setDisk_path(diskPath);
					cloudInstance.setMac_address(macAddress);
					cloudInstance.setVm_status(vmState2);
					cloudInstance.setKvmostype(osType2);
					cloudInstance.setKvmvmid(vmId2);
					cloudInstance.setInstance_ip(instancekvmipAddressmain);
					cloudInstance.setPhysicalServerIP(serverIP);
					cloudInstance.setVirtualization_type("KVM");
					cloudInstance.setMonitoring(true);

					// Update default values for IDs if needed
					cloudInstance.setLocation_id(location);
					cloudInstance.setVpc_id(vpc);
					cloudInstance.setSecurity_group_id(securityGroup);
					cloudInstance.setDiscount_id(discount);
					cloudInstance.setPrice_id(price);
					cloudInstance.setSwitch_id(switchEntity);
					cloudInstance.setSubproduct_id(SubProductentity);
//					obj.setUuid(uuid);
//					obj.setVm_id(uuid);
//					obj.setKvmvmid(uuid);

					// Save to the database (insert if new, update if existing)
					cloudInstanceRepository.save(cloudInstance);
					System.out.println("VM details saved or updated successfully.");

				}

			}

		} catch (Exception e) {
			System.out.println("Exception1: " + e);
		}

		try {
			getKVMIpAdressesMain(serverIP, user, host, password);
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}

	}

	private void getKVMIpAdressesMain(String serverIP, String user, String host, String password) {

//        String host = "172.16.5.22";
//        String user = "ptsadmin";
//        String password = "K$n77&Xe@B";
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
							int rowsUpdated = cloudInstanceRepository.updateInstanceIpIfExists(serverIP, currentVm,
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

	private String getKVMVMIPAddress(String USER, String HOST, String PASSWORD, String VM_NAME, String serverIP)
			throws JSchException, IOException {

		JSch jsch = new JSch();
		Session session2 = jsch.getSession(USER, HOST, 22);
		session2.setPassword(PASSWORD);
		session2.setConfig("StrictHostKeyChecking", "no");
		session2.connect();

		// Command to retrieve the IP address of the VM
		String command = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh domifaddr " + VM_NAME;

		// Open an exec channel for command execution
		ChannelExec channel = (ChannelExec) session2.openChannel("exec");
		channel.setCommand(command);

		// Capture output
		BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
		channel.connect();

		String line;
		String ipAddress = null;

		// Read the output from the command
		while ((line = reader.readLine()) != null) {
			// The output format of virsh domifaddr is typically:
			// " vnet0 192.168.122.10/24"
			// We will check for lines that contain the IP address.
			if (!line.trim().isEmpty() && line.contains("/")) {
				// Extract the IP address
				ipAddress = line.split(" ")[1].split("/")[0];
				break; // Get the first available IP address
			}
		}

		// Clean up
		reader.close();
		channel.disconnect();
		session2.disconnect();

		return ipAddress;
	}

	private String basicVmDetailscall(String host2, String sshusername, String sshPassword, String vmName) {

		StringBuilder output = new StringBuilder();
		JSch jsch = new JSch();
		Session session = null;
		ChannelExec channel = null;
		InputStream in = null;
		BufferedReader reader = null;
		try {
			// Execute command to get VM details in XML format
			String command = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh dominfo " + vmName;
			session = jsch.getSession(sshusername, host2, 22);
			session.setPassword(sshPassword);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);
			channel.setErrStream(System.err);
			in = channel.getInputStream();
			channel.connect();

			// Read VM details
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder xmlOutput = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				// System.out.println("line Data:" + line);
				if (line.contains("State:")) {
					System.out.println("VM State: " + line.split(":")[1].trim());
					output.append("VM State: ").append(line.split(":")[1].trim()).append("\n");
				}
				if (line.contains("OS Type:")) {
					System.out.println("OS Type: " + line.split(":")[1].trim());
					output.append("OS Type: ").append(line.split(":")[1].trim()).append("\n");
				}
				if (line.contains("Id:")) {
					System.out.println("VM Id: " + line.split(":")[1].trim());
					output.append("VM Id: ").append(line.split(":")[1].trim()).append("\n");
				}
				if (line.contains("CPU(s):")) {
					System.out.println("CPU(s): " + line.split(":")[1].trim());
				}
				if (line.contains("CPU time:")) {
					System.out.println("CPU time: " + line.split(":")[1].trim());
				}
				if (line.contains("Max memory:")) {
					System.out.println("Max memory:" + line.split(":")[1].trim());
				}
				if (line.contains("Used memory:")) {
					System.out.println("Used memory:" + line.split(":")[1].trim());
				}
			}

			// Parse XML to extract details
			parseVmDetails(xmlOutput.toString());

		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("Exception:" + e);
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
		return output.toString();
	}

	private String fetchdrivedetails(String host, String user, String password, String vmName) {

		System.out.println("VM name disk:" + vmName);
		JSch jsch = new JSch();
		Session session = null;
		ChannelExec channel = null;
		InputStream in = null;
		BufferedReader reader = null;
		String returneddata = "";
		try {
			// Get the list of block devices for the VM
			String command = String.format("export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh domblklist %s",
					vmName);
			session = jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
			channelExec.setCommand(command);
			channelExec.setErrStream(System.err);

			in = channelExec.getInputStream();
			channelExec.connect();

			reader = new BufferedReader(new InputStreamReader(in));
			String line;
			System.out.println("Disk Details:");
			System.out.printf("%-10s %-10s %-10s %-10s %-10s %-10s\n", "Device", "Used", "Capacity", "Bus", "Access",
					"Source");

			while ((line = reader.readLine()) != null) {
				if (line.contains("/")) { // Skipping header and blank lines
					String[] parts = line.trim().split("\\s+");
					String device = parts[0];
					String source = parts[1];

					// Fetch additional disk info
					returneddata = getDiskUsage(vmName, device, source, host, user, password);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
		return returneddata;
	}

	private String getDiskUsage(String vmName, String device, String source, String serverIP, String user,
			String password) {
		String formattedString = "";
		JSch jsch = new JSch();
		Session session = null;
		ChannelExec channel = null;
		InputStream in = null;
		BufferedReader reader = null;
		try {
			String command = String.format(
					"export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh domblkinfo %s %s", vmName, device);
			session = jsch.getSession(user, serverIP, 22);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
			channelExec.setCommand(command);
			channelExec.setErrStream(System.err);

			in = channelExec.getInputStream();
			channelExec.connect();

			reader = new BufferedReader(new InputStreamReader(in));
			String line;
			String used = "-", capacity = "-", bus = "-", access = "-";

			while ((line = reader.readLine()) != null) {
				System.out.println("Strt drive details #################### " + line);
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
			System.out.println();
			System.out.println("Strt drive details ####################");
			System.out.println();
			System.out.println(device);
			System.out.println(used);
			System.out.println(capacity);
			System.out.printf("%-10s %-10s %-10s %-10s %-10s %-10s\n", device, used, capacity, bus, access, source);
//			formattedString = String.format("%-10s %-10s %-10s %-10s %-10s %-10s", device, used, capacity, bus, access,
//					source);

			// Create new KVMDriveDetails entity
			kvmDriveDetailsRepository.deleteByVmnameAndPhysicalserverip(vmName, serverIP);

			KVMDriveDetails kvmdrive = new KVMDriveDetails();
			kvmdrive.setDevice(device);
			kvmdrive.setUsed(used);
			kvmdrive.setCapacity(capacity);
			kvmdrive.setBus(bus);
			kvmdrive.setAccess(access);
			kvmdrive.setSource(source);
			kvmdrive.setPhysicalserverip(serverIP); // Set additional fields
			kvmdrive.setVmname(vmName); // Set additional fields

			// Save the new entity using the repository
			kvmDriveDetailsRepository.save(kvmdrive);
			System.out.println("save successfulyy drive details");
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();

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

		return formattedString;
	}

	private void basicVmDetails(Session session, String vmName) {

		try {
			// Execute command to get VM details in XML format
			String command = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh dominfo " + vmName;
			ChannelExec channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);
			channel.setErrStream(System.err);
			InputStream in = channel.getInputStream();
			channel.connect();

			// Read VM details
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder xmlOutput = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println("line Data:" + line);
			}

			// Parse XML to extract details
			parseVmDetails(xmlOutput.toString());

			// Clean up
			channel.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String fetchVmDetails(String host, String user, String password, String vmName) {

		JSch jsch = new JSch();
		Session session = null;
		ChannelExec channel = null;
		InputStream in = null;
		BufferedReader reader = null;
		String result = "";
		try {
			// Execute command to get VM details in XML format
			String command = "export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh dumpxml " + vmName;
			session = jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);
			channel.setErrStream(System.err);
			in = channel.getInputStream();
			channel.connect();
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder xmlOutput = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				xmlOutput.append(line).append("\n");
			}
			String vmdetailsresult = parseVmDetails(xmlOutput.toString());
			result = vmdetailsresult;

		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("Exceptione VM: " + e);
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

		return result;
	}

	private static String parseVmDetails(String xml) {
		// System.out.println("XML:" + xml);

		StringBuilder result = new StringBuilder();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new java.io.ByteArrayInputStream(xml.getBytes()));

			// Extract details from XML
			String vmName = doc.getElementsByTagName("name").item(0).getTextContent();
			String memory = doc.getElementsByTagName("memory").item(0).getTextContent();
			String vcpu = doc.getElementsByTagName("vcpu").item(0).getTextContent();
			String osType = doc.getElementsByTagName("os").item(0).getChildNodes().item(0).getTextContent();
			String osVariant = doc.getElementsByTagName("os").item(0).getChildNodes().item(1).getTextContent();
			NodeList disks = doc.getElementsByTagName("disk");
			NodeList nics = doc.getElementsByTagName("interface");

			System.out.println("Details for VM: " + vmName);
			System.out.println("CPU Count: " + vcpu);
			System.out.println("Memory (MB): " + (Integer.parseInt(memory) / 1024)); // Convert KiB to MB

			NodeList diskList = doc.getElementsByTagName("disk");
			String filePath2 = "";
			String filePath = "";
			for (int i = 0; i < diskList.getLength(); i++) {
				String diskType = disks.item(i).getAttributes().getNamedItem("device").getNodeValue();
				// System.out.println("diskType:" + diskType);

				if ("disk".equals(diskType)) {
					Element diskElement = (Element) diskList.item(i);
					NodeList sourceList = diskElement.getElementsByTagName("source");
					if (sourceList.getLength() > 0) {
						Element sourceElement = (Element) sourceList.item(0);
						filePath2 = sourceElement.getAttribute("file");

					}
				}

				if ("cdrom".equals(diskType)) {
					Element diskElement = (Element) diskList.item(i);
					NodeList sourceList = diskElement.getElementsByTagName("source");
					if (sourceList.getLength() > 0) {
						Element sourceElement = (Element) sourceList.item(0);
						filePath = sourceElement.getAttribute("file");

					}
				}

			}

//             Disk Path
//            for (int i = 0; i < disks.getLength(); i++) {
//                System.out.println("In disk@@ Path");
//                String diskSource = disks.item(i).getChildNodes().item(1).getAttributes().getNamedItem("file").getNodeValue();
//                System.out.println("Disk Path: " + diskSource);
//            }
//             MAC Address
//             for (int i = 0; i < nics.getLength(); i++) {
//            String macAddress = nics.item(i).getElementsByTagName("mac").item(0).getAttributes().getNamedItem("address").getNodeValue();
//            System.out.println("MAC Address: " + macAddress);
//        }
			String macAddress = "";
			for (int i = 0; i < nics.getLength(); i++) {
				NodeList macList = ((Element) nics.item(i)).getElementsByTagName("mac");
				if (macList.getLength() > 0) {
					macAddress = macList.item(0).getAttributes().getNamedItem("address").getNodeValue();

				}
			}
//            String diskSource = "";
//            for (int i = 0; i < disks.getLength(); i++) {
//                System.out.println("In disk@@ Path");
//                diskSource = disks.item(i).getChildNodes().item(1).getAttributes().getNamedItem("file").getNodeValue();
//
//            }
			// CD-ROM ISO Path
//            String cdromSource = "";
//            for (int i = 0; i < disks.getLength(); i++) {
//                System.out.println("In disk@@ device");
//                String diskType = disks.item(i).getAttributes().getNamedItem("device").getNodeValue();
//                if ("cdrom".equals(diskType)) {
//                    cdromSource = disks.item(i).getChildNodes().item(1).getAttributes().getNamedItem("file").getNodeValue();
//
//                }
//            }
			// IP Address (if implemented)
			String ipAddress = "Not Implemented"; // You would need additional commands to fetch IP

//            System.out.println("IP Address: " + ipAddress);
			System.out.println("OS Type: " + osType);
			System.out.println("OS Variant: " + osVariant);
//            System.out.println("CD-ROM ISO Path: " + cdromSource);
//            System.out.println("Disk Path: " + diskSource);
			System.out.println("MAC Address: " + macAddress);
			System.out.println("cdrom path:" + filePath);
			System.out.println("disk path :" + filePath2);
			System.out.println("---------------------------------------------------");

			result.append("Details for VM: ").append(vmName).append("\n");
			result.append("CPU Count: ").append(vcpu).append("\n");
			result.append("Memory (MB): ").append(Integer.parseInt(memory) / 1024).append("\n"); // Convert KiB to MB
			result.append("OS Type: ").append(osType).append("\n");
			result.append("OS Variant: ").append(osVariant).append("\n");
			result.append("CD-ROM Path: ").append(filePath).append("\n");
			result.append("Disk Path: ").append(filePath2).append("\n");
			result.append("MAC Address: ").append(macAddress).append("\n");
			result.append("IP Address: ").append(ipAddress).append("\n");
			result.append("---------------------------------------------------\n");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result.toString();
	}

}
