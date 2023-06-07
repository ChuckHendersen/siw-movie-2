package it.uniroma3.siw.controller.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MultipartFileValidator implements org.springframework.validation.Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return MultipartFile.class == clazz;
	}

	@Override
	public void validate(Object target, Errors errors) {
		MultipartFile file = (MultipartFile) target;
		if(file.getSize()==0) {
			System.out.println("size = 0");
			errors.reject("multipartfile.nofile");
		}
	}

}
