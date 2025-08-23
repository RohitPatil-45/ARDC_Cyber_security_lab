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

import com.fasterxml.jackson.databind.ObjectMapper;

import in.canaris.cloud.entity.Product;
import in.canaris.cloud.entity.SubProduct;

import in.canaris.cloud.repository.ProductRepository;
import in.canaris.cloud.repository.SubProductRepository;

@Controller
@RequestMapping("/sub_product")
public class SubProductController {

	@Autowired
	private SubProductRepository repository;
	
	@Autowired
	private ProductRepository repositoryProduct;

	final String var_function_name = "sub_product"; // small letter
	final String disp_function_name = "SubProduct"; // capital letter

	
	@GetMapping("/getSubProductName")
	public @ResponseBody String getSubProductName(@RequestParam String productName)
	{
		System.out.println("getSubProductName  controller calledd:"+productName);
		String json = null;
		
		try {
			int product_id=repositoryProduct.getProductIDByName(productName);
			Product  p = new Product();
			p.setId(product_id);
			List<Object[]> list = repository.getAllSubProductByProduct(p);
			System.out.println("list size:"+list.size());
			json = new ObjectMapper().writeValueAsString(list);
		} catch (Exception e) {
			System.out.println("exception :"+e);
		}
		return json;
	}
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
		mav.addObject("productList", repositoryProduct.getAllProducts());
		SubProduct objEnt = new SubProduct();
		mav.addObject("objEnt", objEnt);
		return mav;
	}

	@PostMapping("/save")
	public String save(SubProduct obj, RedirectAttributes redirectAttributes) {
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
			mav.addObject("productList", repositoryProduct.getAllProducts());
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
