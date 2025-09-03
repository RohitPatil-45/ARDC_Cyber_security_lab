package in.canaris.cloud.openstack.entity;

import java.util.List;

import in.canaris.cloud.entity.CloudInstance;

public class CloudInstanceForm {
	private CloudInstance cloudInstance;
	private List<InstructionDto> instructions;

	// Getters and Setters
	public CloudInstance getCloudInstance() {
		return cloudInstance;
	}

	public void setCloudInstance(CloudInstance cloudInstance) {
		this.cloudInstance = cloudInstance;
	}

	public List<InstructionDto> getInstructions() {
		return instructions;
	}

	public void setInstructions(List<InstructionDto> instructions) {
		this.instructions = instructions;
	}
}
