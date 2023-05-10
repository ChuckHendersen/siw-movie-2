package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.ReviewRepository;

@Controller
public class ReviewController {
	@SuppressWarnings("unused")
	@Autowired
	private ReviewRepository reviewRepository;
	
	@GetMapping("/user/formNewReview")
	public String formNewReview(Model model) {
		model.addAttribute("review", new Review());
		return "formNewReview.html";
	}
}
