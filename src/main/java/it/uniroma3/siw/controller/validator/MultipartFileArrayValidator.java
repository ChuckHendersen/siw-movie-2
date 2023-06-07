package it.uniroma3.siw.controller.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MultipartFileArrayValidator implements org.springframework.validation.Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return MultipartFile[].class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		MultipartFile[] mpfArray = (MultipartFile[]) target;
		if(mpfArray.length==0 || mpfArray[0].getSize()==0) {
			errors.reject("multipartfile.nofile");
		}else {
			for(MultipartFile f : mpfArray) {
				System.out.println(f.toString());
				System.out.println(f.getSize());
			}
		}
	}

}
