package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.repository.UserRepository;

@Service
public class ReviewService {
	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private MovieService movieService;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CredentialsService credentialsService;

	/**
	 * Retrieves a review based on its Id
	 * @param reviewId ID of the review to be retrieved
	 * @return the review if exists on DB, null otherwise
	 */
	public Review getReview(Long reviewId) {
		Review review = this.reviewRepository.findById(reviewId).orElse(null);
		return review;
	}

	/**
	 * Creates a new review
	 * @return new review
	 */
	public Review createReview() {
		return new Review();
	}

	public Review userSaveNewReview(Review review, Long movieId, Long userId) {
		Movie movie = this.movieService.findById(movieId);
		User author = this.userRepository.findById(userId).get();
		if(movie != null && author != null && !this.reviewRepository.existsByReviewedMovieAndAuthor(movie, author)) {
			movie.getReviews().add(review);
			author.getReviews().add(review);
			review.setReviewedMovie(movie);
			review.setAuthor(author);
			review = this.reviewRepository.save(review);
			this.movieService.save(movie);
			this.userRepository.save(author);
			return review;
		}
		return null;
	}

	public boolean hasUserWrittenReviewForMovie(Movie movie, User user) {
		return reviewRepository.existsByReviewedMovieAndAuthor(movie, user);
	}
	
	public Review userDeleteReview(Long reviewId) {
		Review review = this.getReview(reviewId);
		if(review != null) {
			//devo capire se la review è scritta dallo stesso utente che in questo momento sta provando
			//a invocare questo metodo
			User author = review.getAuthor();
			Movie movie = review.getReviewedMovie();
			Credentials authorCredentials = author.getCredentials();
			UserDetails currentUserDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Credentials currentUserCredentials = this.credentialsService.getCredentials(currentUserDetails.getUsername());
			if(authorCredentials.equals(currentUserCredentials)) {
				author.getReviews().remove(review);
				movie.getReviews().remove(review);
				this.movieService.save(movie);
				this.userRepository.save(author);
				this.reviewRepository.delete(review);
			}
		}
		return review;
	}
	
	public Review adminDeleteReview(Long reviewId) {
		Review review = this.getReview(reviewId);
		if(review != null) {
			review.getAuthor().getReviews().remove(review);
			review.getReviewedMovie().getReviews().remove(review);
			this.reviewRepository.deleteById(reviewId);
			this.userRepository.save(review.getAuthor());
			this.movieService.save(review.getReviewedMovie());
		}
		return review;
	}

}
