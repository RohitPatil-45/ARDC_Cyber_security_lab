package in.canaris.cloud.openstack.entity;

public class UserPerformanceDTO {
    private int srNo;
    private String username;
    private String scenarioName;
    private String percentage;

    // Constructors
    public UserPerformanceDTO() {}
    public UserPerformanceDTO(int srNo, String username, String scenarioName, String percentage) {
        this.srNo = srNo;
        this.username = username;
        this.scenarioName = scenarioName;
        this.percentage = percentage;
    }

    // Getters and setters
    public int getSrNo() { return srNo; }
    public void setSrNo(int srNo) { this.srNo = srNo; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getScenarioName() { return scenarioName; }
    public void setScenarioName(String scenarioName) { this.scenarioName = scenarioName; }

    public String getPercentage() { return percentage; }
    public void setPercentage(String percentage) { this.percentage = percentage; }
}

