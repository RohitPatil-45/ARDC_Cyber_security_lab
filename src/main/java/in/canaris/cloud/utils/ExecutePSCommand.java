
package in.canaris.cloud.utils;

import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellResponse;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author testsys
 */
public class ExecutePSCommand {

	public static void main(String[] args) {
//        String vm_name = "TestVM4";
//        String vm_location_path = "C:\\VMData\\";
//        String vhd_path = "C:\\VMData\\" + vm_name + "\\" + vm_name + ".vhdx";
//        ExecutePSCommand execute = new ExecutePSCommand();
//        String op = execute.createVM(vm_name, "512MB", "2", vm_location_path, "SCVMMSW01", vhd_path, "900MB");
//        System.out.println("create VM OP:" + op);
//        

		ExecutePSCommand executedel = new ExecutePSCommand();
		String op = executedel.deleteVM("TestVM4");
		System.out.println("delete VM OP:" + op);

		// update memeory
//		ExecutePSCommand execute = new ExecutePSCommand();
//		String vm_name = "TestVM4";
//		String memory_size = "1GB";
//		String responsedata = execute.updateMemoryVM(vm_name, memory_size);

		// update switch
//		String vm_name = "TestVM4";
//		String switch_name = "TestVirtualSwitch";
//		ExecutePSCommand execute = new ExecutePSCommand();
//		String responsedata = execute.updateSwitchNameVM(vm_name, switch_name);

		// Start
//		String vm_name = "TestVM4";
//		ExecutePSCommand execute = new ExecutePSCommand();
//		String responsedata = execute.stopVM(vm_name);

	}

	public String updateSwitchNameVM(String vm_name, String switch_name) {
		// Get-VM "MyVMTest2" | Get-VMNetworkAdapter | Connect-VMNetworkAdapter
		// -SwitchName "SCVMMSW01"
		PowerShell powerShell = null;
		StringBuilder buildOP = new StringBuilder();
		try {
			String command = "Get-VM " + vm_name + " | Get-VMNetworkAdapter | Connect-VMNetworkAdapter -SwitchName "
					+ switch_name + "";
			System.out.println("command:" + command);
			powerShell = PowerShell.openSession("C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe");
			PowerShellResponse response = null;
			response = powerShell.executeCommand(command);
			String responce_data = response.getCommandOutput();
			buildOP.append(responce_data);
		} catch (Exception ex) {
			System.out.println("Exception PS Command :" + ex);
			buildOP.append("Error:");
			buildOP.append(ex);
		} finally {
			try {
				if (powerShell != null) {
					powerShell.close();
				}
			} catch (Exception e) {
				System.out.println("Exception:" + e);
			}
		}
		return buildOP.toString();
	}

	public String updateMemoryCPUDiskVM(String vm_name, String memory_size, String disk_path, String disk_size,
			String cpu_core) {
		// set-vm -name "+vm_name+" -MemoryStartupBytes "+memory_size+"
		PowerShell powerShell = null;
		StringBuilder buildOP = new StringBuilder();
		try {

			powerShell = PowerShell.openSession("C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe");
			PowerShellResponse response = null;

			try {
				String command="Stop-VM -VMName " + vm_name + " -Force";
				System.out.println("command 1:" + command);
				response = powerShell.executeCommand(command);
			} catch (Exception ex) {
				System.out.println("Exception 1:" + ex);
			}

			Thread.sleep(5000);

			try {
				// RAM
				String command = "set-vm -name " + vm_name + "  -MemoryStartupBytes " + memory_size + "";
				System.out.println("command 2:" + command);
				response = powerShell
						.executeCommand(command);
			} catch (Exception ex) {
				System.out.println("Exception 2:" + ex);
			}

			// disk
			// Resize-VHD -Path C:\\VMs\\MyVM2.vhdx -SizeBytes 70GB
			try {
				String command = "Resize-VHD -Path " + disk_path + " -SizeBytes " + disk_size;
				System.out.println("command 3:" + command);
				response = powerShell.executeCommand("Resize-VHD -Path " + disk_path + " -SizeBytes " + disk_size);
			} catch (Exception ex) {
				System.out.println("Exception 3:" + ex);
			}

			// CPU Process
			try {
				String command = "Set-VMProcessor " + vm_name + "  -Count " + cpu_core + " -Reserve 10 -Maximum 75 -RelativeWeight 200";
				System.out.println("command 4:" + command);
				response = powerShell.executeCommand("Set-VMProcessor " + vm_name + "  -Count " + cpu_core
						+ " -Reserve 10 -Maximum 75 -RelativeWeight 200");
			} catch (Exception ex) {
				System.out.println("Exception 4:" + ex);
			}
			
			String command0="Set-VMProcessor -VMName  "+vm_name+" -Count "+cpu_core;
			System.out.println("Command1 :" + command0);
			response = powerShell.executeCommand(command0);
			String responce_data0 = response.getCommandOutput();
			System.out.println("Responce  Data0 :" + responce_data0);

			String responce_data = response.getCommandOutput();
			buildOP.append(responce_data);
		} catch (Exception ex) {
			System.out.println("Exception PS Command :" + ex);
			buildOP.append("Error:");
			buildOP.append(ex);
		} finally {
			try {
				if (powerShell != null) {
					powerShell.close();
				}
			} catch (Exception e) {
				System.out.println("Exception:" + e);
			}
		}
		return buildOP.toString();
	}

	public String deleteVM(String vm_name) {
		PowerShell powerShell = null;
		StringBuilder buildOP = new StringBuilder();
		try {
			String delete_vm = "Remove-VM -Name " + vm_name + " -Force";
			System.out.println("delete vm:" + delete_vm);
			powerShell = PowerShell.openSession("C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe");
			PowerShellResponse response = null;
			response = powerShell.executeCommand(delete_vm);
			String responce_data = response.getCommandOutput();
//            if (responce_data.contains("CMPVM")) {
			buildOP.append("VM Delete Successfully");
//            } else {
			//buildOP.append(responce_data);
			// }

		} catch (Exception ex) {
			System.out.println("Exception PS Command :" + ex);
			buildOP.append("Error:");
			buildOP.append(ex);

		} finally {
			try {
				if (powerShell != null) {
					powerShell.close();
				}
			} catch (Exception e) {
				System.out.println("Exception:" + e);
			}
		}
		return buildOP.toString();
	}

	public String createVM(String instance_name, String MemoryStartupBytes, String Generation, String vm_location_path,
			String switch_name, String vhd_path, String NewVHDSizeBytes,String cpu_core,String isp_file_path) {

		PowerShell powerShell = null;
		StringBuilder buildOP = new StringBuilder();
		try {
			// New-VM -Name DC -MemoryStartupBytes 512MB -Path
			// C:\Users\Administrator\Desktop\vm (create a new vm)
			String vmcreate_cmd = "New-VM -Name " + instance_name + " -MemoryStartupBytes " + MemoryStartupBytes
					+ " -Generation " + Generation + " -Path " + vm_location_path + " -SwitchName " + switch_name
					+ " -NewVHDPath " + vhd_path + " -NewVHDSizeBytes " + NewVHDSizeBytes+ " | ConvertTo-Json ";
			System.out.println("vmcreate_cmd:" + vmcreate_cmd);
			powerShell = PowerShell.openSession("C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe");
			PowerShellResponse response = null;
			response = powerShell.executeCommand(vmcreate_cmd);
			String responce_data = response.getCommandOutput();
			//if (responce_data.contains("Name    State CPUUsage(%)")) {
			//	buildOP.append("VM Created Successfully:");
				buildOP.append(responce_data);
			//} else {
			//	buildOP.append(responce_data);
			//}

		} catch (Exception ex) {
			System.out.println("Exception PS Command :" + ex);
			//buildOP.append("Error:");
			//buildOP.append(ex);

		} finally {
			try {
				if (powerShell != null) {
					powerShell.close();
				}
			} catch (Exception e) {
				System.out.println("Exception:" + e);
			}

		}
		
		
		PowerShell powerShell2 = null;

		try {
			
			powerShell2 = PowerShell.openSession("C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe");
			
			String command0="Set-VMProcessor -VMName  "+instance_name+" -Count "+cpu_core;
			System.out.println("Command1 :" + command0);
			PowerShellResponse response = null;
			response = powerShell2.executeCommand(command0);
			String responce_data0 = response.getCommandOutput();
			System.out.println("Responce  Data0 :" + responce_data0);

			String command1="Set-VMFirmware -VMName "+instance_name+" -EnableSecureBoot Off";
			System.out.println("Command1 :" + command1);
			response = powerShell2.executeCommand(command1);
			String responce_data1 = response.getCommandOutput();
			System.out.println("Responce  Data1 :" + responce_data1);
			
			String command2="Set-VMFirmware -VMName "+instance_name+" -FirstBootDevice (Get-VMDvdDrive -VMName "+instance_name+")";
			System.out.println("Command2 :" + command2);
			response = powerShell2.executeCommand(command2);
			String responce_data2 = response.getCommandOutput();
			System.out.println("Responce  Data2 :" + responce_data2);
			
			
			String command3="Add-VMDvdDrive -VMName "+instance_name+" -Path "+isp_file_path;
			System.out.println("Command3 :" + command3);
			response = powerShell2.executeCommand(command3);
			String responce_data3 = response.getCommandOutput();
			System.out.println("Responce  Data3 :" + responce_data3);
		} catch (Exception ex) {
			System.out.println("Exception PS Command :" + ex);

		} finally {
			try {
				if (powerShell2 != null) {
					powerShell2.close();
				}
			} catch (Exception e) {
				System.out.println("Exception:" + e);
			}
		}
		
		
		System.out.println("Command oP:" + buildOP);

		return buildOP.toString();
	}

	// Stop VM
	public String stopVM(String vm_name) {
		// Stop-VM -VMName "Kali Linux"
		PowerShell powerShell = null;
		StringBuilder buildOP = new StringBuilder();
		try {
			String command = "Stop-VM -VMName " + vm_name + " -Force";
			System.out.println("command:" + command);
			powerShell = PowerShell.openSession("C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe");
			PowerShellResponse response = null;
			response = powerShell.executeCommand(command);
			String responce_data = response.getCommandOutput();
			buildOP.append(responce_data);
		} catch (Exception ex) {
			System.out.println("Exception PS Command :" + ex);
			buildOP.append("Error:");
			buildOP.append(ex);
		} finally {
			try {
				if (powerShell != null) {
					powerShell.close();
				}
			} catch (Exception e) {
				System.out.println("Exception:" + e);
			}
		}
		return buildOP.toString();
	}
	// End stop vm

	// Start VM
	public String startVM(String vm_name) {
		// Stop-VM -VMName "Kali Linux"
		PowerShell powerShell = null;
		StringBuilder buildOP = new StringBuilder();
		try {
			String command = "Start-VM -VMName " + vm_name + " -Passthru | Get-VM";
			System.out.println("command:" + command);
			powerShell = PowerShell.openSession("C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe");
			PowerShellResponse response = null;
			response = powerShell.executeCommand(command);
			String responce_data = response.getCommandOutput();
			buildOP.append(responce_data);
		} catch (Exception ex) {
			System.out.println("Exception PS Command :" + ex);
			buildOP.append("Error:");
			buildOP.append(ex);
		} finally {
			try {
				if (powerShell != null) {
					powerShell.close();
				}
			} catch (Exception e) {
				System.out.println("Exception:" + e);
			}
		}
		return buildOP.toString();
	}
	// End Start VM
	
	
	//Additional Storage
	public String createNewHardiskVM(String vm_name,String disk_path, String disk_size) {
		// set-vm -name "+vm_name+" -MemoryStartupBytes "+memory_size+"
		PowerShell powerShell = null;
		StringBuilder buildOP = new StringBuilder();
		try {

			powerShell = PowerShell.openSession("C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe");
			PowerShellResponse response = null;

			try {
				String command="Stop-VM -VMName " + vm_name + " -Force";
				System.out.println("command 1:" + command);
				response = powerShell.executeCommand(command);
			} catch (Exception ex) {
				System.out.println("Exception 1:" + ex);
			}

			Thread.sleep(5000);

			try {
				// RAM
                            
                            
				String command = "New-VHD -Path "+disk_path+" -Dynamic -SizeBytes "+disk_size;
				response = powerShell
						.executeCommand(command);
			} catch (Exception ex) {
				System.out.println("Exception 2:" + ex);
			}


			// CPU Process
			try {
				String command = " ADD-VMHardDiskDrive -VMName "+vm_name+" -Path "+disk_path;
				System.out.println("command 4:" + command);
				response = powerShell.executeCommand(command);
			} catch (Exception ex) {
				System.out.println("Exception 4:" + ex);
			}

			String responce_data = response.getCommandOutput();
			buildOP.append(responce_data);
		} catch (Exception ex) {
			System.out.println("Exception PS Command :" + ex);
			buildOP.append("Error:");
			buildOP.append(ex);
		} finally {
			try {
				if (powerShell != null) {
					powerShell.close();
				}
			} catch (Exception e) {
				System.out.println("Exception:" + e);
			}
		}
		return buildOP.toString();
	}
	//End Additional Storage

}
