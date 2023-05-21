package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.ArtistValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Picture;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.PictureRepository;
import jakarta.validation.Valid;

@Controller
public class ArtistController {

	@Autowired 
	private ArtistRepository artistRepository;

	@Autowired
	private ArtistValidator artistValidator;

	@Autowired
	private PictureRepository pictureRepository;

	@GetMapping("/admin/indexArtist")
	public String indexArtist() {
		return "/admin/indexArtist.html";
	}

	@GetMapping("/admin/formNewArtist")
	public String formNewArtist(Model model) {
		model.addAttribute("artist", new Artist());
		return "/admin/formNewArtist.html";
	}

	@PostMapping("/admin/artists")
	public String newArtist(@Valid @ModelAttribute("artist") Artist artist, 
			@RequestAttribute("file") MultipartFile file , 
			BindingResult bindingResult, 
			Model model) throws IOException {
		this.artistValidator.validate(artist, bindingResult);
		if(!bindingResult.hasErrors()) {
			//dovrebbe avere una sola immagine l'array
			Picture picture = this.savePictureIfNotExistsOrRetrieve(file);
			artist.setPicture(picture);
			this.artistRepository.save(artist);
			model.addAttribute(artist);
			return "artist.html";
		}else {
			//model.addAttribute("messaggioErrore", "Questo artista è già presente");
			return "/admin/formNewArtist.html";
		}
	}

	@GetMapping("/artists")
	public String artists(Model model) {
		List<Artist> listaArtisti = (List<Artist>)artistRepository.findAll();
		model.addAttribute("artists", listaArtisti);		
		return "artists.html";
	}

	@GetMapping("/artists/{id}")
	public String artist(@PathVariable Long id, Model model) {
		Artist artist=null;
		try {
			artist=artistRepository.findById(id).get();
			model.addAttribute("artist", artist);
		}catch(Exception e) {
			if(artist==null) {
				model.addAttribute("messaggioErrore","Artista non trovato");
			}else {
				model.addAttribute("messaggioErrore","Errore generico");
			}
		}
		return "artist.html";
	}
	
	//Implementare cancellazione artista
	
	private Picture savePictureIfNotExistsOrRetrieve(MultipartFile f) throws IOException {
		Picture picture = new Picture();
		//System.out.println("la foto non esiste");
		picture = new Picture();
		picture.setName(f.getResource().getFilename());
		picture.setData(f.getBytes());
		this.pictureRepository.save(picture);	
		return picture;
	}

}
