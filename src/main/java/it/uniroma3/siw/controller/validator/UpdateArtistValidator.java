package it.uniroma3.siw.controller.validator;

import java.time.LocalDate;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.uniroma3.siw.model.Artist;

@Component
public class UpdateArtistValidator implements org.springframework.validation.Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return Artist.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Artist updatedArtist = (Artist) target;
		if(updatedArtist.getBirthDate().isAfter(LocalDate.now())) {
			errors.reject("artist.dataNascita.invalid");
		}
		if(updatedArtist.getName().equals("") || updatedArtist.getName().equals(" ")) {
			errors.reject("artist.nome");
		}
		if(updatedArtist.getSurname().equals("") || updatedArtist.getSurname().equals(" ")) {
			errors.reject("artist.cognome");
		}
	}

}
