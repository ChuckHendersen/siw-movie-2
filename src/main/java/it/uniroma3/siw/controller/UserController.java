package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.UserRepository;

@Controller
public class UserController {
	@Autowired
	UserRepository userRepository;

	@GetMapping("/users/{user_id}")
	public String getUser(@PathVariable("user_id") Long userId,Model model) {
		try {
			User user = userRepository.findById(userId).get();
			model.addAttribute("user", user);
		}catch(Exception e) {
			//utente non trovato
			model.addAttribute("errore", "utente non esistente");
		}
		return "user.html";
	}
}
