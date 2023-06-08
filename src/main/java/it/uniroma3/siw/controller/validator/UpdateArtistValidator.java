package it.uniroma3.siw.controller.validator;

import it.uniroma3.siw.controller.form.UpdateArtistForm;
import it.uniroma3.siw.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.uniroma3.siw.model.Artist;

@Component
public class UpdateArtistValidator{

	@Autowired
	private ArtistService artistService;

	public void validate(UpdateArtistForm updateArtistForm, Artist artist, Errors errors) {
		if(!(updateArtistForm.getName().equals(artist.getName()) && updateArtistForm.getSurname().equals(artist.getSurname()))) {
			if(this.artistService.alreadyExists(updateArtistForm.getName(), updateArtistForm.getSurname())) {
				errors.reject("artist.duplicate");
			}
		}
	}

}
