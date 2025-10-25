package in.canaris.cloud.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.canaris.cloud.entity.ProxmoxVMInfo;
import in.canaris.cloud.repository.ProxmoxVMInfoRepository;
import in.canaris.cloud.service.ProxmoxService;

@RestController
@RequestMapping("/api/vm")
public class ProxmoxController {

	private final ProxmoxService proxmoxService;

	@Autowired
    private ProxmoxVMInfoRepository proxmoxVMInfoRepository;
	

	public ProxmoxController(ProxmoxService proxmoxService) {
		this.proxmoxService = proxmoxService;
	}

	
	@GetMapping("/discover-vms")
    public String showVms(Model model) {
        try {
            // Fetch VM list from Proxmox
            List<Map<String, Object>> vms = proxmoxService.discoverVms();

            for (Map<String, Object> vm : vms) {
                int vmid = (Integer) vm.get("vmid");

                // Either update existing or create new
                ProxmoxVMInfo vmInfo = proxmoxVMInfoRepository.findById(vmid).orElse(new ProxmoxVMInfo());
                vmInfo.setVmid(vmid);
                vmInfo.setNode((String) vm.get("node"));
                vmInfo.setName((String) vm.get("name"));
                vmInfo.setStatus((String) vm.get("status"));
                vmInfo.setCpuUsage(vm.get("cpu") != null ? ((Number) vm.get("cpu")).doubleValue() : 0.0);
                vmInfo.setCpuCores(vm.get("cpus") != null ? ((Number) vm.get("cpus")).intValue() : 0);
                vmInfo.setMemUsed(vm.get("mem") != null ? ((Number) vm.get("mem")).longValue() : 0);
                vmInfo.setMemTotal(vm.get("maxmem") != null ? ((Number) vm.get("maxmem")).longValue() : 0);
                vmInfo.setDiskUsed(vm.get("disk") != null ? ((Number) vm.get("disk")).longValue() : 0);
                vmInfo.setDiskTotal(vm.get("maxdisk") != null ? ((Number) vm.get("maxdisk")).longValue() : 0);
                vmInfo.setOsType((String) vm.get("ostype"));
                vmInfo.setTags((String) vm.get("tags"));
                vmInfo.setUptime(vm.get("uptime") != null ? ((Number) vm.get("uptime")).longValue() : 0);

                proxmoxVMInfoRepository.save(vmInfo);
            }

            model.addAttribute("vms", proxmoxVMInfoRepository.findAll());
            model.addAttribute("message", "VMs discovered and updated successfully.");

        } catch (Exception e) {
            model.addAttribute("error", "Error discovering VMs: " + e.getMessage());
        }

        return "vm-list"; 
    }

}
