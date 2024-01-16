package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	//method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("USERNAME"+userName);
		
		User user = userRepository.getUserByUserName(userName);
		System.out.println("USER"+user);
		model.addAttribute("user",user);
	}
	
	//dashboard home
	@RequestMapping("/index")
	public String Dashbord(Model model, Principal principal) 
	{
		model.addAttribute("title","User Dashboard");
		model.addAttribute("contact",new Contact());
		
		
		//get the user using username
		return "normal/user_dashbord";
	}
	
	
	
	@GetMapping("/add-contact")
	//open add form handler
	public String openAddContactForm(Model model) {
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
		
		return "normal/add_contact_form";
	} 
	
	//process add-contact form
	@PostMapping("/user/process-contact")
	public String processContact(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {
		
		try {
		String name=principal.getName();
		User user = this.userRepository.getUserByUserName(name);
		
	
		
		contact.setUser(user);
		
		//process of adding exception
//		if(3>2) {
//		throw new Exception();
//		}
		//processing and uploading file
		
		if(file.isEmpty()) {
			//if the file is empty try your message
			
			
		} else {
			//upload the file in folder and update the name in 'contact'
			contact.setImage(file.getOriginalFilename());
			File saveFile = new ClassPathResource("static/img").getFile();
			
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			
			
			System.out.println("Image is uploaded");
		}
		
		
		user.getContacts().add(contact);
		
		this.userRepository.save(user);
		
		System.out.println("DATA"+contact);
		
		System.out.println("Added to data base");
		//message success....
		
		session.setAttribute("message", new Message("Your contact is added!! add more.....","success"));
		
		} catch (Exception e ) {
			System.out.println("ERROR"+ e.getMessage());
			e.printStackTrace();
			
			//error message........
			session.setAttribute("message", new Message("Something went wrong!! Try again","danger"));
			
			
		}
		return "normal/add_contact_form";
	}
	
	// show contact handler
	@GetMapping("/show_contacts")
	public String showContacts(Model m, Principal principal)
	{
		m.addAttribute("title", "Show User Contact");
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		List<Contact> contacts = this.contactRepository.findContactsByUser(user.getId());
		m.addAttribute("contacts",contacts);
		
		
		return "normal/show_contacts";
	}
	
	

}
