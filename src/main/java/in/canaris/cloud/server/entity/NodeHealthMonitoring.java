package in.canaris.cloud.server.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="node_health_monitoring" )
public class NodeHealthMonitoring implements Serializable{

	private static final long serialVersionUID = -2264642949863409860L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false)
    private Long ID;
	@Column
	private String NODE_IP; 
	@Column
	private String NODE_NAME;
	@Column
	private String MAKE_AND_MODEL;
	@Column
	private String VERSION;
	@Column
	private String SERIAL_NO;
	@Column
	private String UPTIME;
	@Column
	private int CPU_UTILIZATION;
	@Column
	private String CPU_STATUS;
	@Column
	private int MEMORY_UTILIZATION;
	@Column
	private String MEMORY_STATUS;
	@Column
	private double TOTAL_MEMORY;
	@Column
	private double USED_MEMORY;
	@Column
	private double FREE_MEMORY;
	@Column
	private int TEMPERATURE;
	public Long getID() {
		return ID;
	}
	public void setID(Long iD) {
		ID = iD;
	}
	public String getNODE_IP() {
		return NODE_IP;
	}
	public void setNODE_IP(String nODE_IP) {
		NODE_IP = nODE_IP;
	}
	public String getNODE_NAME() {
		return NODE_NAME;
	}
	public void setNODE_NAME(String nODE_NAME) {
		NODE_NAME = nODE_NAME;
	}
	public String getMAKE_AND_MODEL() {
		return MAKE_AND_MODEL;
	}
	public void setMAKE_AND_MODEL(String mAKE_AND_MODEL) {
		MAKE_AND_MODEL = mAKE_AND_MODEL;
	}
	public String getVERSION() {
		return VERSION;
	}
	public void setVERSION(String vERSION) {
		VERSION = vERSION;
	}
	public String getSERIAL_NO() {
		return SERIAL_NO;
	}
	public void setSERIAL_NO(String sERIAL_NO) {
		SERIAL_NO = sERIAL_NO;
	}
	public String getUPTIME() {
		return UPTIME;
	}
	public void setUPTIME(String uPTIME) {
		UPTIME = uPTIME;
	}
	public int getCPU_UTILIZATION() {
		return CPU_UTILIZATION;
	}
	public void setCPU_UTILIZATION(int cPU_UTILIZATION) {
		CPU_UTILIZATION = cPU_UTILIZATION;
	}
	public String getCPU_STATUS() {
		return CPU_STATUS;
	}
	public void setCPU_STATUS(String cPU_STATUS) {
		CPU_STATUS = cPU_STATUS;
	}
	public int getMEMORY_UTILIZATION() {
		return MEMORY_UTILIZATION;
	}
	public void setMEMORY_UTILIZATION(int mEMORY_UTILIZATION) {
		MEMORY_UTILIZATION = mEMORY_UTILIZATION;
	}
	public String getMEMORY_STATUS() {
		return MEMORY_STATUS;
	}
	public void setMEMORY_STATUS(String mEMORY_STATUS) {
		MEMORY_STATUS = mEMORY_STATUS;
	}
	public double getTOTAL_MEMORY() {
		return TOTAL_MEMORY;
	}
	public void setTOTAL_MEMORY(double tOTAL_MEMORY) {
		TOTAL_MEMORY = tOTAL_MEMORY;
	}
	public double getUSED_MEMORY() {
		return USED_MEMORY;
	}
	public void setUSED_MEMORY(double uSED_MEMORY) {
		USED_MEMORY = uSED_MEMORY;
	}
	public double getFREE_MEMORY() {
		return FREE_MEMORY;
	}
	public void setFREE_MEMORY(double fREE_MEMORY) {
		FREE_MEMORY = fREE_MEMORY;
	}
	public int getTEMPERATURE() {
		return TEMPERATURE;
	}
	public void setTEMPERATURE(int tEMPERATURE) {
		TEMPERATURE = tEMPERATURE;
	}
	
	
	

}
