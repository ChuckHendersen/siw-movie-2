package it.uniroma3.siw.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Picture;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.PictureRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.repository.UserRepository;
import jakarta.validation.Valid;

@Service
public class MovieService {

	@Autowired private MovieRepository movieRepository;
	@Autowired private ArtistRepository artistRepository;
	@Autowired private ReviewRepository reviewRepository;
	@Autowired private MovieValidator movieValidator;
	@Autowired private PictureRepository pictureRepository;
	@Autowired private UserRepository userRepository;

	@Autowired private PictureService pictureService;

	public Movie createMovie() {
		return new Movie();
	}

	@Transactional
	public Movie saveNewMovie(Movie newMovie, MultipartFile[] files, BindingResult bindingResult) throws IOException {
		this.movieValidator.validate(newMovie, bindingResult);
		if(!bindingResult.hasErrors()) {
			Picture[] pictures = this.pictureService.savePictures(files);
			for(Picture p:pictures) {
				newMovie.getPictures().add(p);
			}
			return this.movieRepository.save(newMovie);
		}
		return null;
	}

	@Transactional
	public Movie findById(Long movieId) {
		return this.movieRepository.findById(movieId).orElse(null);
	}
	
	@Transactional
	public Movie updateMovieDetails(Long movieId, Movie newMovie) {
		Movie oldMovie = this.movieRepository.findById(movieId).orElse(null);
		if(oldMovie != null) {
			oldMovie.setTitle(newMovie.getTitle());
			oldMovie.setYear(newMovie.getYear());
			movieRepository.save(oldMovie);
		}
		return oldMovie;
	}

	@Transactional
	public Movie uploadNewPhoto(Long movieId, MultipartFile[] files) throws IOException {
		Movie movie = this.movieRepository.findById(movieId).orElse(null);
		if(movie != null) {
			Picture[] pictures = this.pictureService.savePictures(files);
			for(Picture p:pictures) {
				movie.getPictures().add(p);	
			}
			movie = movieRepository.save(movie);
		}
		return movie;
	}

}
