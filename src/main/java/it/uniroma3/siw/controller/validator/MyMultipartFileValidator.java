package it.uniroma3.siw.controller.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MyMultipartFileValidator{

	public boolean validate(MultipartFile f) {
		return f.getSize()>0;
	}
	
	public boolean validate(MultipartFile[] files) {
		return !(files.length==0) && !(files[0].getSize()==0);
	}
}
