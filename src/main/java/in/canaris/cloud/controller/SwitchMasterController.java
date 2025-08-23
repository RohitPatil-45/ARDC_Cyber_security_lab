package in.canaris.cloud.controller;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellResponse;

import in.canaris.cloud.entity.Group;
import in.canaris.cloud.entity.Switch;

import in.canaris.cloud.repository.SwitchRepository;

@Controller
@RequestMapping("/switchmaster")
public class SwitchMasterController {

	@Autowired
	private SwitchRepository repository;

	final String var_function_name = "switchmaster"; // small letter
	final String disp_function_name = "Switchmaster"; // capital letter

	@GetMapping("/view")
	public ModelAndView getAll() {
		ModelAndView mav = new ModelAndView(var_function_name + "_Show");
		mav.addObject("action_name", var_function_name);
		try {
			mav.addObject("listObj", repository.findAll());
		} catch (Exception e) {
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
		}
		return mav;
	}

	@GetMapping("/edit/{id}")
	public ModelAndView edit(@PathVariable("id") Integer id) {

		try {
			ModelAndView mav = new ModelAndView(var_function_name + "_add");
			mav.addObject("action_name", var_function_name);
			mav.addObject("objEnt", repository.findById(id).get());
			mav.addObject("pageTitle", "Edit " + "Switch" + " (ID: " + id + ")");
			return mav;
		} catch (Exception e) {
			ModelAndView mav = new ModelAndView("redirect:/" + var_function_name + "/view");
			mav.addObject("action_name", var_function_name);
			mav.addObject("message", e.getMessage());
			return mav;
		}
	}

	@PostMapping("/save")
	public String save(Switch obj, RedirectAttributes redirectAttributes) {
		try {
			repository.save(obj);
			redirectAttributes.addFlashAttribute("message", "The " + "Switch" + " has been saved successfully!");
		} catch (Exception e) {
			redirectAttributes.addAttribute("message", e.getMessage());
		}
		return "redirect:/" + var_function_name + "/view";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			repository.deleteById(id);
			redirectAttributes.addFlashAttribute("message",
					"The " + "Switch" + " with id=" + id + " has been deleted successfully!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/" + var_function_name + "/view";
	}

	@GetMapping("/new")
	public ModelAndView add() {
		ModelAndView mav = new ModelAndView(var_function_name + "_add");
		mav.addObject("pageTitle", "Add New " + "Switch");
		mav.addObject("action_name", var_function_name);
		Switch objEnt = new Switch();
		mav.addObject("objEnt", objEnt);
		return mav;
	}

	@GetMapping("/Discover")
	public String Discover(RedirectAttributes redirectAttributes) {

		System.out.println("start");
		PowerShell powerShell = null;
		StringBuilder buildOP = new StringBuilder();
		String responce_data = "";
		try {

			String vmcreate_cmd = "Get-VMSwitch | select name | ConvertTo-Json";
			powerShell = PowerShell.openSession("C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe");
			PowerShellResponse response = null;
			response = powerShell.executeCommand(vmcreate_cmd);
			Thread.sleep(5000);
			responce_data = response.getCommandOutput();
			// System.out.println("Status Monitoring op:"+responce_data);
			JSONArray array = new JSONArray(responce_data);
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				String switch_name = object.getString("Name");
				System.out.println("Switch Name:" + switch_name);

				List<Switch> Switchlist = repository.findBySwitchName(switch_name);

				if (Switchlist.isEmpty()) {

					Switch Switchobj = new Switch();
					Switchobj.setConnection_type("-");
					Switchobj.setInterface_description("-");
					Switchobj.setNotes("-");
					Switchobj.setSwitch_name(switch_name);

					repository.save(Switchobj);

				}

			}

		} catch (Exception ex) {
			System.out.println("Exception PS Command :" + ex);
			buildOP.append("Error:");
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

		redirectAttributes.addFlashAttribute("message", "Discovery Completed");
		return "redirect:/" + var_function_name + "/view";
	}

}
