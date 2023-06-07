package it.uniroma3.siw.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.model.*;

@Component
public class MovieValidator implements org.springframework.validation.Validator {
	
	@Autowired
	private MovieRepository movieRepository;

	@Override
	public void validate(Object o, Errors errors) {
		Movie movie = (Movie)o;
		if (movie.getTitle()!=null && movie.getYear()!=null && movieRepository.existsByTitleAndYear(movie.getTitle(), movie.getYear())) {
			errors.reject("movie.duplicate");
		}
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return Movie.class.equals(aClass);
	}

}
