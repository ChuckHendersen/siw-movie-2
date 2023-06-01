package it.uniroma3.siw.controller;

import jakarta.validation.Valid;

import java.lang.management.GarbageCollectorMXBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;

@Controller
public class LoginController {
	
	@Autowired
	private CredentialsService credentialsService;
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/login") 
	public String showLoginForm (Model model) {
		return "formLogin";
	}
	
	@GetMapping("/register") 
	public String showRegisterForm (Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("newCredentials", new Credentials());
		return "formRegisterUser.html";
	}
	
	@PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
    			BindingResult userBindingResult, 
    			@Valid @ModelAttribute("newCredentials") Credentials credentials,
                BindingResult credentialsBindingResult,
                Model model) {
        // se user e credential hanno entrambi contenuti validi, memorizza User e the Credentials nel DB
        if(!userBindingResult.hasErrors() && ! credentialsBindingResult.hasErrors()) {
            credentials.setUser(user);
            user.setCredentials(credentials);
            credentialsService.saveCredentials(credentials);
            userService.saveUser(user);
            model.addAttribute("user", user);
            return "registrationSuccessful.html";
        }
        System.out.println(userBindingResult.getNestedPath());
        System.out.println(credentialsBindingResult.getNestedPath());
        return "formRegisterUser.html";
    }
	
	@GetMapping("/success")
    public String defaultAfterLogin(Model model) {
    	UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
    	model.addAttribute("credentials", credentials);
        return "index.html";
    }
}
