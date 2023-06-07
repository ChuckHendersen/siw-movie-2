package it.uniroma3.siw.controller.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MyMultipartFileValidator{

	public boolean validate(MultipartFile f) {
		return f.getSize()>0;
	}
}
