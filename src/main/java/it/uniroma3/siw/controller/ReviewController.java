package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;

@Controller
public class ReviewController {
	
	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private MovieRepository movieRepository;
	
	@GetMapping("/user/formNewReview")
	public String formNewReview(Model model) {
		model.addAttribute("review", new Review());
		return "/user/formNewReview.html";
	}
	
	@PostMapping("/user/newReview")
	public String newReview(@ModelAttribute("review") Review review, @ModelAttribute("movie") Movie movie, Model model) {
		return "redirect:/movies/"+movie.getId();
	}
}
