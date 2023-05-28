package it.uniroma3.siw.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Picture;
import it.uniroma3.siw.repository.PictureRepository;

@Service
public class PictureService {

	@Autowired 
	private PictureRepository pictureRepository;
	
	@Transactional
	public Picture[] savePictures(MultipartFile[] files) throws IOException {
		Picture[] pictures = new Picture[files.length];
		int i=0;
		for(MultipartFile f:files) {
			Picture picture = new Picture();
			picture.setName(f.getResource().getFilename());
			picture.setData(f.getBytes());
			this.pictureRepository.save(picture);
			pictures[i] = picture;
			i++;	
		}
		return pictures;
	}
	
}
