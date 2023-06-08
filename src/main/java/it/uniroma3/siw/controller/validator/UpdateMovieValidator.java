package it.uniroma3.siw.controller.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.uniroma3.siw.controller.form.UpdateMovieForm;

@Component
public class UpdateMovieValidator implements org.springframework.validation.Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return UpdateMovieForm.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		//UpdateMovieForm updateMovieForm = (UpdateMovieForm)target;
		
	}

}
