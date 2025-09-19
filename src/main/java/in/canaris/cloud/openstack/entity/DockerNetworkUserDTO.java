package in.canaris.cloud.openstack.entity;

public class DockerNetworkUserDTO {

    private String networkName;
    private String networkId;
    private String driver;
    private String scope;
    private String gateway;
    private String startIp;
    private String endIp;
    private String physicalServer;

    private String username;
    private Integer scenarioId;
    private String lastActiveConnection;

    public DockerNetworkUserDTO(String networkName, String networkId, String driver, String scope,
                                String gateway, String startIp, String endIp, String physicalServer,
                                String username, Integer scenarioId, String lastActiveConnection) {
        this.networkName = networkName;
        this.networkId = networkId;
        this.driver = driver;
        this.scope = scope;
        this.gateway = gateway;
        this.startIp = startIp;
        this.endIp = endIp;
        this.physicalServer = physicalServer;
        this.username = username;
        this.scenarioId = scenarioId;
        this.lastActiveConnection = lastActiveConnection;
    }

    // getters only (DTOs are read-only)
    public String getNetworkName() { return networkName; }
    public String getNetworkId() { return networkId; }
    public String getDriver() { return driver; }
    public String getScope() { return scope; }
    public String getGateway() { return gateway; }
    public String getStartIp() { return startIp; }
    public String getEndIp() { return endIp; }
    public String getPhysicalServer() { return physicalServer; }
    public String getUsername() { return username; }
    public Integer getScenarioId() { return scenarioId; }
    public String getLastActiveConnection() { return lastActiveConnection; }
}
