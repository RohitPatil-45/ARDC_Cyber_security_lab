package in.canaris.cloud.service;

import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class GuacamoleService {

	private static final String GUAC_URL = "http://localhost:8080/guacamole";
	private static final String USERNAME = "guacadmin";
	private static final String PASSWORD = "guacadmin";

	private final RestTemplate rest = new RestTemplate();

	/**
	 * Authenticate and return authToken
	 */
	public String getAuthToken() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("username", USERNAME);
		body.add("password", PASSWORD);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

		ResponseEntity<String> response = rest.postForEntity(GUAC_URL + "/api/tokens", request, String.class);

		JSONObject json = new JSONObject(response.getBody());
		return json.getString("authToken");
	}

	/**
	 * List available connections (parsed into List<Map>)
	 */
	public List<Map<String, String>> listConnections() {
		String token = getAuthToken();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", "GUAC_AUTH=" + token);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		HttpEntity<Void> request = new HttpEntity<>(headers);

		ResponseEntity<String> response = rest.exchange(GUAC_URL + "/api/session/data/mysql/connections?token=" + token,
				HttpMethod.GET, request, String.class);

		JSONObject json = new JSONObject(response.getBody());

		System.out.println("COnnection JSOn = " + json);

		List<Map<String, String>> connections = new ArrayList<>();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
				.withZone(ZoneId.systemDefault());

		for (String key : json.keySet()) {
			JSONObject conn = json.getJSONObject(key);

			Map<String, String> map = new HashMap<>();
			map.put("id", conn.getString("identifier"));
			map.put("name", conn.getString("name"));
			map.put("protocol", conn.getString("protocol"));
			map.put("activeconnection", String.valueOf(conn.getInt("activeConnections")));

			long lastActiveEpoch = conn.optLong("lastActive", -1);
			String lastActiveFormatted;

			if (lastActiveEpoch != -1) {
				lastActiveFormatted = formatter.format(Instant.ofEpochMilli(lastActiveEpoch));
			} else {
				lastActiveFormatted = "-";
			}

			map.put("lastactive", lastActiveFormatted);

			JSONObject attributes = conn.getJSONObject("attributes");
			map.put("maxconnectionperuser", attributes.optString("max-connections-per-user", ""));
			map.put("maxconnection", attributes.optString("max-connections", ""));
			map.put("guacd-hostname", attributes.optString("guacd-hostname", ""));
			map.put("guacd-port", attributes.optString("guacd-port", ""));

			connections.add(map);
		}

		return connections;
	}

	/**
	 * Create a new RDP connection
	 */
	public String createConnection(String name, String protocol, String hostname, int port, String username,
			String password, String domain, String ignoreCert, String width, String height, String privateKey,
			String passphrase, String command, String namespace, String container, String kubeCommand,
			String kubeToken) {
		String token = getAuthToken();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Cookie", "GUAC_AUTH=" + token);
		headers.setContentType(MediaType.APPLICATION_JSON);

		// Build protocol-specific params
		JSONObject params = new JSONObject();
		params.put("hostname", hostname);
		params.put("port", String.valueOf(port));

		if (username != null)
			params.put("username", username);
		if (password != null)
			params.put("password", password);

		switch (protocol) {
		case "rdp":
			if (domain != null)
				params.put("domain", domain);
			params.put("security", "nla");
			if (ignoreCert != null && ignoreCert.equals("true")) {
				params.put("ignore-cert", "true");
			}
			params.put("width", width != null ? width : "1366");
			params.put("height", height != null ? height : "768");
			break;

		case "ssh":
			if (privateKey != null)
				params.put("private-key", privateKey);
			if (passphrase != null)
				params.put("passphrase", passphrase);
			if (command != null)
				params.put("command", command);
			break;

		case "vnc":
			params.put("encodings", "tight");
			params.put("color-depth", "24");
			break;

		case "telnet":
			params.put("terminal-type", "xterm-256color");
			params.put("backspace", "delete");
			break;

		case "kubernetes":
			params.put("kubernetes-namespace", namespace);
			params.put("kubernetes-container", container);
			params.put("kubernetes-command", kubeCommand);
			params.put("kubernetes-token", kubeToken);
			if (ignoreCert != null && ignoreCert.equals("true")) {
				params.put("ignore-cert", "true");
			}
			break;
		}

		// Attributes (mandatory in Guacamole API)
		JSONObject attributes = new JSONObject();
		attributes.put("max-connections", "1");
		attributes.put("max-connections-per-user", "1");

		// Main request body
		JSONObject body = new JSONObject();
		body.put("parentIdentifier", "ROOT");
		body.put("name", name);
		body.put("protocol", protocol);
		body.put("parameters", params);
		body.put("attributes", attributes);

		HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);

		ResponseEntity<String> response = rest
				.postForEntity(GUAC_URL + "/api/session/data/mysql/connections?token=" + token, request, String.class);

		return response.getBody();
	}

	/**
	 * Generate iframe URL for embedding
	 */
	public String getEmbedUrl(String connectionId) {
		String token = getAuthToken();
		return GUAC_URL + "/#/client/" + connectionId + "?token=" + token;
	}

	public String getConnectionIdByName(String jsonResponse) throws Exception {

		JSONObject jsonObject = new JSONObject(jsonResponse);
		String identifier = jsonObject.getString("identifier");

		return identifier; // Not found
	}

	public String loginAndGetToken() {
		String loginUrl = GUAC_URL + "/api/tokens";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		String body = "username=" + USERNAME + "&password=" + PASSWORD;
		HttpEntity<String> request = new HttpEntity<>(body, headers);

		ResponseEntity<Map> response = rest.postForEntity(loginUrl, request, Map.class);

		if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
			return (String) response.getBody().get("authToken");
		}
		throw new RuntimeException("Failed to login to Guacamole and fetch token");
	}

	// Step 2: Fetch all connections and map name → identifier
	public String getConnectionId(String connectionName) {
		String token = loginAndGetToken();

		String url = GUAC_URL + "/api/session/data/mysql/connections?token=" + token;
		ResponseEntity<Map> response = rest.getForEntity(url, Map.class);

		if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
			Map<String, Map<String, Object>> connections = response.getBody();

			for (Map.Entry<String, Map<String, Object>> entry : connections.entrySet()) {
				String id = entry.getKey();
				Map<String, Object> details = entry.getValue();
				if (connectionName.equals(details.get("name"))) {
					return id; // return identifier (like "18")
				}
			}
		}
		throw new RuntimeException("Connection name '" + connectionName + "' not found in Guacamole");
	}

	// Step 3: Build embed URL from name
	public String getVncAccessUrlByName(String connectionName) {
		String id = getConnectionId(connectionName); // fetch identifier
		String token = loginAndGetToken();
		return GUAC_URL + "/#/client/mysql/" + id + "?token=" + token;
	}

}
