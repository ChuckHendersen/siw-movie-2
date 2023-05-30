package it.uniroma3.siw.service;

import java.io.IOException;
import java.time.Year;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Picture;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.PictureRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.repository.UserRepository;

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

	@Transactional
	public Movie deletePhoto(Long movieId, Long pictureId) {
		Movie movie = this.movieRepository.findById(movieId).orElse(null);
		Picture picture = this.pictureRepository.findById(pictureId).orElse(null);
		if(movie != null && picture != null) {
			Set<Picture> pictures = movie.getPictures();
			if(pictures.size()>1 && pictures.contains(picture)) {
				pictures.remove(picture);
				this.movieRepository.save(movie);
			}
			this.pictureRepository.delete(picture);
		}
		return movie;
	}

	@Transactional
	public List<Movie> findByYear(Year year) {
		return this.movieRepository.findByYear(year);
	}

	public Iterable<Movie> findAll() {
		return movieRepository.findAll();
	}

	@Transactional
	public Movie setDirectorToMovie(Long directorId, Long movieId) {
		Movie movie = this.movieRepository.findById(movieId).orElse(null);
		Artist director= this.artistRepository.findById(directorId).orElse(null);
		if(movie != null) {
			movie.setDirector(director);
			director.getListaFilmDiretti().add(movie);
			movieRepository.save(movie);
			artistRepository.save(director);
		}
		return movie;
	}

	@Transactional
	public Movie setActorToMovie(Long actorId, Long movieId) {
		Movie movie = movieRepository.findById(movieId).orElse(null);
		Artist actor = artistRepository.findById(actorId).orElse(null);
		if(movie != null && actor != null){
			if(!movie.getActors().contains(actor)) {
				movie.getActors().add(actor);
				actor.getListaFilmRecitati().add(movie);
				movieRepository.save(movie);
				//artistRepository.save(actor); duplica le entry nel database. Fare solo 1 save?
			}
			return movie;
		}
		return null;
	}

	@Transactional
	public Movie deleteActorFromMovie(Long actorId, Long movieId) {
		Movie movie = movieRepository.findById(movieId).orElse(null);
		if(movie != null) {
			Artist actor2beRemoved = artistRepository.findById(actorId).orElse(null);
			if(actor2beRemoved != null) {
				movie.getActors().remove(actor2beRemoved);
				movieRepository.save(movie);
				return movie;
			}
		}
		return null;
	}
	
	@Transactional
	public Movie deleteMovie(Long movieId) {
		Movie movieToBeDeleted = this.movieRepository.findById(movieId).orElse(null);
		if(movieToBeDeleted != null) {
			Set<Review> reviews = movieToBeDeleted.getReviews();
			Set<Artist> actors = movieToBeDeleted.getActors();
			Artist director = movieToBeDeleted.getDirector();
			Set<Picture> pictures = movieToBeDeleted.getPictures();
			Hibernate.initialize(reviews);
			Hibernate.initialize(actors);
			Hibernate.initialize(pictures);
			movieToBeDeleted.setReviews(null);
			movieToBeDeleted.setActors(null);
			movieToBeDeleted.setDirector(null);
			movieToBeDeleted.setPictures(null);
			this.movieRepository.save(movieToBeDeleted);
			//Eliminiamo le foto
			for(Picture p : pictures) {
				pictureRepository.delete(p);
			}
			//Eliminiamo le recensioni
			for(Review r : reviews) {
				r.getAuthor().getReviews().remove(r);
				this.userRepository.save(r.getAuthor());
				this.reviewRepository.delete(r);
			}
			//eliminiamo il riferimento al film recitato dell'artista
			for(Artist actor : actors) {
				actor.getListaFilmRecitati().remove(movieToBeDeleted);
				artistRepository.save(actor);
			}
			// ripetere il processo per tutti quanti
			// sono troppo stanco per continuare
			//Elimiamo il riferimento del film dall'artista che lo ha diretto
			if(director!=null) {
				director.getListaFilmDiretti().remove(movieToBeDeleted);
			}
			this.movieRepository.delete(movieToBeDeleted);
		}
		return movieToBeDeleted;
	}
}

