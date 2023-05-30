package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.UserService;

import static it.uniroma3.siw.controller.ControllerUtils.*;

@Controller
public class UserController {
	@Autowired
	private UserService userService;

	@GetMapping("/users/{user_id}")
	public String getUser(@PathVariable("user_id") Long userId,Model model) {
		User user = this.userService.getUser(userId);
		model.addAttribute("user", user);
		return redirection(user, "user.html", "userError.html");
	}
}
