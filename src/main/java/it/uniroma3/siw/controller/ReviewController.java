package it.uniroma3.siw.controller;

import javax.validation.Valid;
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
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.repository.UserRepository;

@Controller
public class ReviewController {

	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private MovieRepository movieRepository;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ReviewValidator reviewValidator;

	@GetMapping("/user/formNewReview")
	public String formNewReview(Model model) {
		model.addAttribute("review", new Review());
		return "/user/formNewReview.html";
	}

	@PostMapping("/user/newReview/{movie_id}/{user_id}")
	public String newReview(@Valid @ModelAttribute("review") Review review,
			BindingResult bindingResult,
			@PathVariable("movie_id") Long movieId,
			@PathVariable("user_id") Long userId,
			Model model) {
		//Devo recuperare lo user
		this.reviewValidator.validate(review, bindingResult);
		System.out.println("movie_id: "+movieId);
		if(!bindingResult.hasErrors()) {
			Movie movie = this.movieRepository.findById(movieId).get();
			User author = this.userRepository.findById(userId).get();
			movie.getReviews().add(review);
			author.getReviews().add(review);
			review.setReviewedMovie(movie);
			review.setAuthor(author);
			this.reviewRepository.save(review);
			this.movieRepository.save(movie);
			this.userRepository.save(author);
		}
		return "redirect:/movies/"+movieId;
	}

	@GetMapping("/admin/manageReview/{review_id}")
	public String manageReview(@PathVariable("review_id") Long reviewId, Model model) {
		Review review = this.reviewRepository.findById(reviewId).get();
		if(review != null) {
			model.addAttribute("review", review);
		}
		return "/admin/manageReview.html";
	}

	@GetMapping("/admin/deleteReview/{review_id}")
	public String deleteReview(@PathVariable("review_id") Long reviewId, Model model) {
		Review review = this.reviewRepository.findById(reviewId).get();
		if(review != null) {
			review.getAuthor().getReviews().remove(review);
			review.getReviewedMovie().getReviews().remove(review);
			this.reviewRepository.deleteById(reviewId);
			this.userRepository.save(review.getAuthor());
			this.movieRepository.save(review.getReviewedMovie());
			return "redirect:/movies/"+review.getReviewedMovie().getId();
		}
		return "redirect:/";
	}
}
