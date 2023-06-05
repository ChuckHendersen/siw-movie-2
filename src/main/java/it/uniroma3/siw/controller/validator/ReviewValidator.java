package it.uniroma3.siw.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.ReviewRepository;

@Component
public class ReviewValidator implements org.springframework.validation.Validator{

	@Autowired
	private ReviewRepository reviewRepository;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Review.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Review review = (Review) target;
		if(this.reviewRepository.existsByReviewedMovieAndAuthor(review.getReviewedMovie(), review.getAuthor())) {
			errors.reject("review.duplicate");
		}else if(review.getVote()<1 ||  review.getVote()>5) {
			errors.reject("review.invalidVote");
		}
	}

}
