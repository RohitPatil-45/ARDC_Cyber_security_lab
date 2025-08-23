package in.canaris.cloud.controller;

import java.util.List;

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

import in.canaris.cloud.entity.AppUser;
import in.canaris.cloud.entity.Customer;
import in.canaris.cloud.repository.CustomerRepository;

@Controller
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	private CustomerRepository repository;

	final String var_function_name = "customer"; // small letter
	final String disp_function_name = "Customer"; // capital letter

	@GetMapping("/view")
	public ModelAndView getAll() {
		ModelAndView mav = new ModelAndView(var_function_name + "_view");
		mav.addObject("action_name", var_function_name);
		try {
			mav.addObject("listObj", repository.findAll());
		} catch (Exception e) {
			mav.addObject("listObj", null);
			mav.addObject("error", e.getMessage());
		}
		return mav;
	}

	@GetMapping("/new")
	public ModelAndView add() {
		ModelAndView mav = new ModelAndView(var_function_name + "_add");
		mav.addObject("pageTitle", "Add New " + disp_function_name);
		mav.addObject("action_name", var_function_name);
		Customer objEnt = new Customer();
		mav.addObject("objEnt", objEnt);
		return mav;
	}
	
	@GetMapping("/checkDuplicateCustomer")
	public @ResponseBody String checkDuplicateCustomer(@RequestParam String customer)
	{
		String duplicate = "";
		try {
			
			List<Customer> list = repository.findByCustomerName(customer);
			if(!list.isEmpty()) {
				duplicate = "duplicate";
			}
			
		} catch (Exception e) {
			System.out.println("Exception occured while checking for duplicate customer = "+e);
		}
		return duplicate;
	}
	
	@GetMapping("/checkEmailExist")
	public @ResponseBody String checkEmailExist(@RequestParam String email)
	{
		String duplicate = "";
		try {
			
			List<Customer> list = repository.findByEmail(email);
			if(!list.isEmpty()) {
				duplicate = "duplicate";
			}
			
		} catch (Exception e) {
			System.out.println("Exception occured while checking for duplicate Email = "+e);
		}
		return duplicate;
	}

	@PostMapping("/save")
	public String save(Customer obj, RedirectAttributes redirectAttributes) {
		try {
			repository.save(obj);
			redirectAttributes.addFlashAttribute("message",
					"The " + disp_function_name + " has been saved successfully!");
		} catch (Exception e) {
			redirectAttributes.addAttribute("message", e.getMessage());
		}
		return "redirect:/" + var_function_name + "/view";
	}

	@GetMapping("/edit/{id}")
	public ModelAndView edit(@PathVariable("id") Integer id) {
		
		try {
			ModelAndView mav = new ModelAndView(var_function_name + "_add");
			mav.addObject("action_name", var_function_name);
			mav.addObject("objEnt", repository.findById(id).get());
			mav.addObject("pageTitle",  "Edit " + disp_function_name + " (ID: " + id + ")");
			return mav;
		} catch (Exception e) {
			ModelAndView mav = new ModelAndView("redirect:/" + var_function_name + "/view");
			mav.addObject("action_name", var_function_name);
			mav.addObject("message", e.getMessage());
			return mav;
		}
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			repository.deleteById(id);
			redirectAttributes.addFlashAttribute("message",
					"The " + disp_function_name + " with id=" + id + " has been deleted successfully!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/" + var_function_name + "/view";
	}
}
