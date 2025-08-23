package in.canaris.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellResponse;

import in.canaris.cloud.entity.PowershellEntity;


import java.util.ArrayList;
import java.util.Iterator;

@Controller
@RequestMapping("/powershell")
public class PowershellController {

	/*
	 * @Autowired private DiscountRepository repository;
	 */

	final String var_function_name = "powershell"; // small letter
	final String disp_function_name = "Powershell"; // capital letter

	@GetMapping("/view")
	public ModelAndView getAll() {
		ModelAndView mav = new ModelAndView(var_function_name + "_view");
		mav.addObject("action_name", var_function_name);
		try {
		} catch (Exception e) {
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
		}
		return mav;
	}

	@GetMapping("/new")
	public ModelAndView add() {
		ModelAndView mav = new ModelAndView(var_function_name + "_add");
		mav.addObject("pageTitle", "Test " + disp_function_name);
		mav.addObject("action_name", var_function_name);
		PowershellEntity objEnt = new PowershellEntity();
		mav.addObject("objEnt", objEnt);
		return mav;
	}

	@PostMapping("/save")
	public String save(PowershellEntity obj, RedirectAttributes redirectAttributes) {
		try {
			// repository.save(obj);
			String msg = obj.getCommand();

			ArrayList<String> commandList = new ArrayList();

			// commandList.add("Get-CimInstance -ClassName Win32_OperatingSystem |
			// Select-Object -Property Caption, Version");
			/*
			 * commandList.
			 * add("Get-CimInstance -ClassName Win32_OperatingSystem | Select-Object -Property Caption, Version"
			 * ); commandList.add("Get-WmiObject Win32_BIOS");
			 */
			commandList.add(msg);

			PowerShell powerShell = null;
			StringBuilder buildOP = new StringBuilder();
			try {
				powerShell = PowerShell.openSession("C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe");
				Iterator<String> itr = commandList.iterator();
				PowerShellResponse response = null;
				int i = 0;
				while (itr.hasNext()) {
					i=i+1;
					String command = itr.next();
					// System.out.println("command:" + command);
					response = powerShell.executeCommand(command);
					//buildOP.append("<span>###########################################################<br>");
					buildOP.append("Command");
					buildOP.append(i);
					buildOP.append(":");
					buildOP.append(command);
					//buildOP.append("<br>-----------------");
					buildOP.append(":Command OP:");
					buildOP.append(response.getCommandOutput());
					//buildOP.append("<br>");
					//buildOP.append("<br></span>");

				}
			} catch (Exception ex) {
				System.out.println("Exception PS Command :" + ex);
				buildOP.append(ex);
			} finally {
				try {
					if (powerShell != null) {
						powerShell.close();
					}
				} catch (Exception e) {
					System.out.println("Exception:" + e);
				}

			}
			
			System.out.println("PS op data:" + buildOP);
			redirectAttributes.addFlashAttribute("message", buildOP);
		} catch (Exception e) {
			System.out.println("PS op Exception:" + e);
			redirectAttributes.addAttribute("message", e.getMessage());
		}
		return "redirect:/" + var_function_name + "/view";
	}

}
