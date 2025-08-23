package in.canaris.cloud.entity;

import java.io.Serializable;

public class VMCreationBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String activity;

	private String instanceName;

	private String memoryStartupBytes;

	private String generation;

	private String vmLocationPath;

	private String switchName;

	private String vhdPath;

	private String newVHDSizeBytes;
	
	private String vCpu;
	
	private String isoFilePath;

	
	private boolean isCPUIncrease;

	private boolean isRAMIncrease;
	
	
	
	
	
	public boolean isCPUIncrease() {
		return isCPUIncrease;
	}

	public void setCPUIncrease(boolean isCPUIncrease) {
		this.isCPUIncrease = isCPUIncrease;
	}

	public boolean isRAMIncrease() {
		return isRAMIncrease;
	}

	public void setRAMIncrease(boolean isRAMIncrease) {
		this.isRAMIncrease = isRAMIncrease;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getMemoryStartupBytes() {
		return memoryStartupBytes;
	}

	public void setMemoryStartupBytes(String memoryStartupBytes) {
		this.memoryStartupBytes = memoryStartupBytes;
	}

	public String getGeneration() {
		return generation;
	}

	public void setGeneration(String generation) {
		this.generation = generation;
	}

	public String getVmLocationPath() {
		return vmLocationPath;
	}

	public void setVmLocationPath(String vmLocationPath) {
		this.vmLocationPath = vmLocationPath;
	}

	public String getSwitchName() {
		return switchName;
	}

	public void setSwitchName(String switchName) {
		this.switchName = switchName;
	}

	public String getVhdPath() {
		return vhdPath;
	}

	public void setVhdPath(String vhdPath) {
		this.vhdPath = vhdPath;
	}

	public String getNewVHDSizeBytes() {
		return newVHDSizeBytes;
	}

	public void setNewVHDSizeBytes(String newVHDSizeBytes) {
		this.newVHDSizeBytes = newVHDSizeBytes;
	}
	
	public String getvCpu() {
		return vCpu;
	}

	public void setvCpu(String vCpu) {
		this.vCpu = vCpu;
	}

	public String getIsoFilePath() {
		return isoFilePath;
	}

	public void setIsoFilePath(String isoFilePath) {
		this.isoFilePath = isoFilePath;
	}

	@Override
	public String toString() {
		return "VMCreationBean [activity=" + activity + ", instanceName=" + instanceName + ", memoryStartupBytes="
				+ memoryStartupBytes + ", generation=" + generation + ", vmLocationPath=" + vmLocationPath
				+ ", switchName=" + switchName + ", vhdPath=" + vhdPath + ", newVHDSizeBytes=" + newVHDSizeBytes
				+ ", vCpu=" + vCpu + ", isoFilePath=" + isoFilePath + "]";
	}

	
	
	

}
