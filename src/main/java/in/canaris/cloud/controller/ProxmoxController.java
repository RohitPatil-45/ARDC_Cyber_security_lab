package in.canaris.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.canaris.cloud.openstack.repository.ScenarioLabTemplateRepository;
import in.canaris.cloud.repository.CloudInstanceRepository;
import in.canaris.cloud.repository.UserLabRepository;
import in.canaris.cloud.service.ProxmoxService;

@RestController
@RequestMapping("/api/vm")
public class ProxmoxController {

	private final ProxmoxService proxmoxService;

	@Autowired
	private CloudInstanceRepository repository;

	@Autowired
	private ScenarioLabTemplateRepository scenarioLabTemplateRepository;

	@Autowired
	private UserLabRepository userLabRepository;
	

	public ProxmoxController(ProxmoxService proxmoxService) {
		this.proxmoxService = proxmoxService;
	}


}
