package it.uniroma3.siw.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import it.uniroma3.siw.controller.validator.ReviewValidator;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.service.UserService;

import static it.uniroma3.siw.controller.ControllerUtils.*;

@Controller
public class ReviewController {

	@Autowired
	private ReviewValidator reviewValidator;

	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	private MovieService movieService;

	@Autowired
	private UserService userService;
	
	@GetMapping("/reviews/{review_id}")
	public String getReview(@PathVariable("review_id") Long reviewId, Model model) {
		Review review = this.reviewService.getReview(reviewId);
		model.addAttribute("review", review);
		return redirection(review, "review.html", "reviewError.html");
	}

	@GetMapping("/user/deleteReview/{review_id}")
	public String deleteUserReview(@PathVariable("review_id") Long reviewId, Model model) {
		Review review = this.reviewService.userDeleteReview(reviewId);
		return redirection(review, "redirect:/users/"+review.getAuthor().getId(), "reviewError.html");
	}

	@PostMapping("/user/newReview/{movie_id}/{user_id}")
	public String newReview(@Valid @ModelAttribute("review") Review review,
			BindingResult bindingResult,
			@PathVariable("movie_id") Long movieId,
			@PathVariable("user_id") Long userId,
			Model model) {
		//Devo recuperare lo user
		User autore = this.userService.getUser(userId);
		Movie movie = this.movieService.findById(movieId);
		if(autore!=null && movie != null) {
			review.setAuthor(autore);
			review.setReviewedMovie(movie);
			this.reviewValidator.validate(review, bindingResult);
			if(!bindingResult.hasErrors()) {
				return redirection(this.reviewService.userSaveNewReview(review, movieId, userId),"redirect:/movies/"+movieId,"reviewError.html");
			}else {
				model.addAttribute("movie", movie);
				return redirection(movie,"movie.html","movieError.html");
			}
		}else if(autore == null) {
			return "userError.html";
		}else if(movie == null) {
			return "movieError.html";
		}
		return "reviewError.html";
	}

	@GetMapping("/admin/manageReview/{review_id}")
	public String manageReview(@PathVariable("review_id") Long reviewId, Model model) {
		Review review = this.reviewService.getReview(reviewId);
		model.addAttribute("review", review);
		return redirection(review, "/admin/manageReview.html", "reviewError.html");
	}

	@GetMapping("/admin/deleteReview/{review_id}")
	public String deleteReview(@PathVariable("review_id") Long reviewId, Model model) {
		Review review = this.reviewService.adminDeleteReview(reviewId);
		return redirection(review, "redirect:/users/"+review.getAuthor().getId(), "reviewError.html");
	}
}
