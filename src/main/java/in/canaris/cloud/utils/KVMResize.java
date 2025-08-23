package in.canaris.cloud.utils;

import in.canaris.cloud.entity.CloudInstance;

public class KVMResize {
	
	String serverIP;
	String vm_name;
	String sshusername;
	String sshPassword;
	
	public KVMResize(String serverIP,String vm_name,String sshusername,String sshPassword)
	{
		this.serverIP=serverIP;
		this.vm_name=vm_name;
		this.sshusername=sshusername;
		this.sshPassword=sshPassword;

		
	}
	

	public CommandResult changeKVMRAM(int ramInGB) {
		
		int ramInKB=ramInGB*1024*1024;
		//<size>: The desired memory size in kibibytes (KiB).
		String commands = String
				.format("export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh setmaxmem %s %d --config; " + // Set
																													// maximum
																													// memory
						"export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh setmem %s %d --config; ", // Set
																												// active
																												// memory
						vm_name, ramInKB, vm_name, ramInKB, vm_name);
		System.out.println("KVM change memory command:"+commands);

		ExecuteSSHCommand ssh = new ExecuteSSHCommand();
		CommandResult cmdResult = ssh.executeCommand(commands, sshusername, sshPassword, serverIP);
		return cmdResult;
	}
	
	public CommandResult changeKVMCPU(int cpuCount) {
		String commands = String.format(
				"export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh setvcpus %s %d --maximum --config; " + // Set
																													// maximum
																													// vCPU
																													// count
						"export LIBVIRT_DEFAULT_URI=qemu:///system && /usr/bin/virsh setvcpus %s %d --config; ", // Set
																													// active
																													// vCPU
																													// count
						vm_name, cpuCount, vm_name, cpuCount, vm_name);
		System.out.println("KVM change CPU command:"+commands);

		ExecuteSSHCommand ssh = new ExecuteSSHCommand();
		CommandResult cmdResult = ssh.executeCommand(commands, sshusername, sshPassword, serverIP);
		return cmdResult;
	}
}
