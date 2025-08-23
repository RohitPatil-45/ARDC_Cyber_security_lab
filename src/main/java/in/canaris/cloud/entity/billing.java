package in.canaris.cloud.entity;

public class billing {

	private int ID;
	private String VM_Name;
	private double VM_Usage;
	private double Per_Hour_PRice;
	private double UsagePrice;
	private String ProductName;
	private String CPUAssigned;
	private String MemoryAssigned;
	private String DiskAssigned;
	private String VCPU;
	private String RAM;
	private String SSD_Disk;
	private int Discount_percentage;
	private String Discount_Type;
	private double DiscountedPrice;

	public String getVM_Name() {
		return VM_Name;
	}

	public void setVM_Name(String vM_Name) {
		VM_Name = vM_Name;
	}

	public double getVM_Usage() {
		return VM_Usage;
	}

	public void setVM_Usage(double vM_Usage) {
		VM_Usage = vM_Usage;
	}

	public double getPer_Hour_PRice() {
		return Per_Hour_PRice;
	}

	public void setPer_Hour_PRice(double per_Hour_PRice) {
		Per_Hour_PRice = per_Hour_PRice;
	}

	public double getUsagePrice() {
		return UsagePrice;
	}

	public void setUsagePrice(double usagePrice) {
		UsagePrice = usagePrice;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getProductName() {
		return ProductName;
	}

	public void setProductName(String productName) {
		ProductName = productName;
	}

	public String getCPUAssigned() {
		return CPUAssigned;
	}

	public void setCPUAssigned(String cPUAssigned) {
		CPUAssigned = cPUAssigned;
	}

	public String getMemoryAssigned() {
		return MemoryAssigned;
	}

	public void setMemoryAssigned(String memoryAssigned) {
		MemoryAssigned = memoryAssigned;
	}

	public String getDiskAssigned() {
		return DiskAssigned;
	}

	public void setDiskAssigned(String diskAssigned) {
		DiskAssigned = diskAssigned;
	}

	public String getVCPU() {
		return VCPU;
	}

	public void setVCPU(String vCPU) {
		VCPU = vCPU;
	}

	public String getRAM() {
		return RAM;
	}

	public void setRAM(String rAM) {
		RAM = rAM;
	}

	public String getSSD_Disk() {
		return SSD_Disk;
	}

	public void setSSD_Disk(String sSD_Disk) {
		SSD_Disk = sSD_Disk;
	}

	public int getDiscount_percentage() {
		return Discount_percentage;
	}

	public void setDiscount_percentage(int discount_percentage) {
		Discount_percentage = discount_percentage;
	}

	public String getDiscount_Type() {
		return Discount_Type;
	}

	public void setDiscount_Type(String discount_Type) {
		Discount_Type = discount_Type;
	}

	public double getDiscountedPrice() {
		return DiscountedPrice;
	}

	public void setDiscountedPrice(double discountedPrice) {
		DiscountedPrice = discountedPrice;
	}

}
