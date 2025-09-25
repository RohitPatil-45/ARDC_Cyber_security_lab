package in.canaris.cloud.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProxmoxService {

	@Value("${proxmox_host}")
	private String proxmox_host;

	@Value("${proxmox_node}")
	private String proxmox_node;

	@Value("${proxmox_tokenId}")
	private String proxmox_tokenId;

	@Value("${proxmox_tokenSecret}")
	private String proxmox_tokenSecret;

	private final RestTemplate restTemplate = new RestTemplate();

	public String getProxmoxConsoleUrl(int vmId, String vmName) {
		Map<String, Object> vncData = getVncWebSocketUrl(vmId); // your API call to /vncproxy
		String ticket = (String) vncData.get("ticket");

		return String.format("https://%s:8006/?console=kvm&novnc=1&vmid=%d&vmname=%s&node=%s&resize=off&vncticket=%s",
				proxmox_host, vmId, vmName, proxmox_node, ticket);
	}

	public String getProxmoxNoVncConsoleUrl(int vmId, String vmName) {
		return String.format(
				"https://%s:8006/?console=kvm&novnc=1&vmid=%d&vmname=%s&node=%s&resize=off&resize=off&cmd=",
				proxmox_host, vmId, vmName, proxmox_node);
	}

	public Map<String, Object> getVncWebSocketUrl(int vmId) {
		Map<String, Object> result = new HashMap<>();
		try {
			String url = String.format("https://%s:8006/api2/json/nodes/%s/qemu/%d/vncproxy", proxmox_host,
					proxmox_node, vmId);

			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "PVEAPIToken=" + proxmox_tokenId + "=" + proxmox_tokenSecret);
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			String body = "websocket=1";
			HttpEntity<String> entity = new HttpEntity<>(body, headers);

			ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
				result.put("status", "success");
				result.put("host", data.get("host"));
				result.put("port", data.get("port"));
				result.put("ticket", data.get("ticket"));
				result.put("vmId", vmId);
			} else {
				result.put("status", "fail");
				result.put("error", "Unable to get VNC URL");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("status", "fail");
			result.put("error", e.getMessage());
		}
		return result;
	}

	// Get the next available VM ID
	public int getNextVmId() {
		String url = String.format("https://%s:8006/api2/json/cluster/nextid", proxmox_host);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "PVEAPIToken=" + proxmox_tokenId + "=" + proxmox_tokenSecret);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

		if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
			String body = response.getBody();
			String id = body.replaceAll("[^0-9]", ""); // extract number
			return Integer.parseInt(id);
		} else {
			throw new RuntimeException("Failed to fetch next VM ID: " + response.getStatusCode());
		}
	}

	public Map<String, Object> cloneVm(int templateId, String newVmName, String filepath, String physicalServerIp, String ip) {
		Map<String, Object> result = new HashMap<>();
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("bash", filepath, String.valueOf(templateId), newVmName,
					physicalServerIp, ip);
			processBuilder.redirectErrorStream(true);

			Process process = processBuilder.start();

			StringBuilder output = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					output.append(line);
				}
			}

			int exitCode = process.waitFor();

			ObjectMapper mapper = new ObjectMapper();
			result = mapper.readValue(output.toString(), Map.class);

			// Normalize status
			if (exitCode != 0 || !"success".equals(result.get("status"))) {
				result.put("status", "fail");
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.clear();
			result.put("status", "fail");
			result.put("error", e.getMessage());
		}
		return result;
	}

	// Start VM
	public boolean startVm(int vmId) {
		String url = String.format("https://%s:8006/api2/json/nodes/%s/qemu/%d/status/start", proxmox_host,
				proxmox_node, vmId);

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "PVEAPIToken=" + proxmox_tokenId + "=" + proxmox_tokenSecret);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

		return response.getStatusCode().is2xxSuccessful();
	}

	public String getNextIp(String ip) throws Exception {
		byte[] bytes = InetAddress.getByName(ip).getAddress();
		int ipInt = ByteBuffer.wrap(bytes).getInt();
		int nextIpInt = ipInt + 1;
		return InetAddress.getByAddress(ByteBuffer.allocate(4).putInt(nextIpInt).array()).getHostAddress();
	}

}
