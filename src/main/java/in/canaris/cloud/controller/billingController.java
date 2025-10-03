package in.canaris.cloud.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import in.canaris.cloud.entity.AddPhysicalServer;
import in.canaris.cloud.entity.AppUser;
import in.canaris.cloud.entity.CloudInstance;
import in.canaris.cloud.entity.billing;
import in.canaris.cloud.repository.AppUserRepository;
import in.canaris.cloud.repository.CloudInstanceRepository;
import in.canaris.cloud.repository.CloudInstanceUsageDailyRepository;

@Controller
@RequestMapping("/billing")

public class billingController {

	@Autowired
	private CloudInstanceRepository repository;

	@Autowired
	private CloudInstanceUsageDailyRepository usageRepository;

	@Autowired
	private AppUserRepository appRepository;

	final String var_function_name = "billing"; // small letter
	final String disp_function_name = "Billing"; // capital letter

	@GetMapping("/view")
	public ModelAndView add(Principal principal) {
		ModelAndView mav = new ModelAndView(var_function_name + "_view");
		if (principal == null) {
			ModelAndView mav1 = new ModelAndView("welcomePage");
			return mav1;
		}
		mav.addObject("pageTitle", "" + disp_function_name);
		mav.addObject("action_name", var_function_name);
		List<billing> billingList = new ArrayList<>();
//		mav.addObject("physicalServerIPList", addPhysicalServerRepository.getPhysicalServerIPs());

//		AddPhysicalServer objEnt = new AddPhysicalServer();
		Authentication authentication = (Authentication) principal;
		User loginedUser = (User) ((Authentication) principal).getPrincipal();
		String username = loginedUser.getUsername();
		AppUser obj22 = appRepository.findOneByUserName(username);
		try {
			boolean isSuperAdmin = authentication.getAuthorities().stream()
					.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));

			if (isSuperAdmin) {

				List<CloudInstance> instances = repository.findByIsMonitoringOrderByIdDesc(true);
				for (CloudInstance cloudInstances : instances) {
					int instanceID = cloudInstances.getId();

					String VMName = "";
					double Vmusage = 0;
					double VmperhourPricee = 0;
					double VMUsagePrice = 0;

					CloudInstance vm_instance = repository.findById(instanceID).get();
					long time = (long) ((usageRepository.getTime(vm_instance) == null ? 0
							: usageRepository.getTime(vm_instance).equals("") ? 0
									: usageRepository.getTime(vm_instance)));
//					long hours = TimeUnit.SECONDS.toHours(time);
					double hours = time / 3600.0; // 3600 seconds in an hour
					BigDecimal bd = new BigDecimal(hours);
					BigDecimal roundedHours = bd.setScale(2, RoundingMode.CEILING);
					hours = roundedHours.doubleValue();
					double hourlyPrice = vm_instance.getPrice_id().getHourly_price();
					VMName = cloudInstances.getInstance_name();
					Vmusage = hours;
					VmperhourPricee = hourlyPrice;
					VMUsagePrice = hours * hourlyPrice;

					billing billingobj = new billing();
					billingobj.setPer_Hour_PRice(VmperhourPricee);
					billingobj.setUsagePrice(VMUsagePrice);
					billingobj.setVM_Name(VMName);
					billingobj.setVM_Usage(Vmusage);
					billingobj.setID(cloudInstances.getId());
					billingobj.setProductName(cloudInstances.getSubproduct_id().getSub_product_name());
					billingobj.setCPUAssigned(cloudInstances.getCpuAssigned());
					billingobj.setDiskAssigned(cloudInstances.getDiskAssigned() == null ? "-"
							: cloudInstances.getDiskAssigned().equals("") ? "-"
									: String.format("%.2f", Double.parseDouble(cloudInstances.getDiskAssigned())
											/ (1024 * 1024 * 1024)));
					billingobj.setMemoryAssigned(cloudInstances.getMemoryAssigned() == null ? "-"
							: cloudInstances.getMemoryAssigned().equals("") ? "-"
									: String.format("%.2f", Double.parseDouble(cloudInstances.getMemoryAssigned())
											/ (1024 * 1024 * 1024)));
					billingobj.setVCPU(cloudInstances.getPrice_id().getvCpu());
					billingobj.setRAM(cloudInstances.getPrice_id().getRam());
					billingobj.setSSD_Disk(cloudInstances.getPrice_id().getSsd_disk());
					billingobj.setDiscount_percentage(cloudInstances.getDiscount_id().getDiscount_percentage());
					billingobj.setDiscount_Type(cloudInstances.getDiscount_id().getDiscount_type());
					int Discountpercentage = cloudInstances.getDiscount_id().getDiscount_percentage();
					double discountAmount = VMUsagePrice * Discountpercentage / 100.0;
					double discountedPrice = VMUsagePrice - discountAmount;
					billingobj.setDiscountedPrice(discountedPrice);

					billingList.add(billingobj);

				}

			} else {

				List<String> groupName = new ArrayList<>();
				StringTokenizer token = new StringTokenizer(obj22.getGroupName(), ",");
				while (token.hasMoreTokens()) {
					groupName.add(token.nextToken());
				}

				List<CloudInstance> instances = repository.findByIsMonitoringAndGroupNameOrderByIdDesc(true, groupName);
				for (CloudInstance cloudInstances : instances) {
					int instanceID = cloudInstances.getId();

					String VMName = "";
					double Vmusage = 0;
					double VmperhourPricee = 0;
					double VMUsagePrice = 0;

					CloudInstance vm_instance = repository.findById(instanceID).get();
					long time = (long) ((usageRepository.getTime(vm_instance) == null ? 0
							: usageRepository.getTime(vm_instance).equals("") ? 0
									: usageRepository.getTime(vm_instance)));
//					long hours = TimeUnit.SECONDS.toHours(time);
					double hours = time / 3600.0; // 3600 seconds in an hour
					BigDecimal bd = new BigDecimal(hours);
					BigDecimal roundedHours = bd.setScale(2, RoundingMode.CEILING);
					hours = roundedHours.doubleValue();
					double hourlyPrice = vm_instance.getPrice_id().getHourly_price();
					VMName = cloudInstances.getInstance_name();
					Vmusage = hours;
					VmperhourPricee = hourlyPrice;
					VMUsagePrice = hours * hourlyPrice;

					billing billingobj = new billing();
					billingobj.setPer_Hour_PRice(VmperhourPricee);
					billingobj.setUsagePrice(VMUsagePrice);
					billingobj.setVM_Name(VMName);
					billingobj.setVM_Usage(Vmusage);
					billingobj.setID(cloudInstances.getId());
					billingobj.setProductName(cloudInstances.getSubproduct_id().getSub_product_name());
					billingobj.setCPUAssigned(cloudInstances.getCpuAssigned());
					billingobj.setDiskAssigned(cloudInstances.getDiskAssigned() == null ? "-"
							: cloudInstances.getDiskAssigned().equals("") ? "-"
									: String.format("%.2f", Double.parseDouble(cloudInstances.getDiskAssigned())
											/ (1024 * 1024 * 1024)));
					billingobj.setMemoryAssigned(cloudInstances.getMemoryAssigned() == null ? "-"
							: cloudInstances.getMemoryAssigned().equals("") ? "-"
									: String.format("%.2f", Double.parseDouble(cloudInstances.getMemoryAssigned())
											/ (1024 * 1024 * 1024)));
					billingobj.setVCPU(cloudInstances.getPrice_id().getvCpu());
					billingobj.setRAM(cloudInstances.getPrice_id().getRam());
					billingobj.setSSD_Disk(cloudInstances.getPrice_id().getSsd_disk());
					billingobj.setDiscount_percentage(cloudInstances.getDiscount_id().getDiscount_percentage());
					billingobj.setDiscount_Type(cloudInstances.getDiscount_id().getDiscount_type());
					int Discountpercentage = cloudInstances.getDiscount_id().getDiscount_percentage();
					double discountAmount = VMUsagePrice * Discountpercentage / 100.0;
					double discountedPrice = VMUsagePrice - discountAmount;
					billingobj.setDiscountedPrice(discountedPrice);

					billingList.add(billingobj);

				}

			}

		} catch (Exception e) {
			System.out.println("Exception occured while fetching VM Data = " + e);
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
		}

		mav.addObject("listObj", billingList);
		return mav;
	}

}
