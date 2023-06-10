package it.uniroma3.siw.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;

@Component
public class ArtistValidator implements org.springframework.validation.Validator{
	
	@Autowired
	ArtistRepository artistRepository;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Artist.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Artist artist = (Artist) target;
		if(artist.getName()!=null && artist.getSurname()!=null && artistRepository.existsByNameAndSurname(artist.getName(), artist.getSurname())) {
			System.out.println("duplicate");
			errors.reject("artist.duplicate");
		}
	}

}
