package it.uniroma3.siw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.ArtistValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;
import javax.validation.Valid;

@Controller
public class ArtistController {

	@Autowired 
	private ArtistRepository artistRepository;
	
	@Autowired
	private ArtistValidator artistValidator;
	
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
	public String newArtist(@Valid @ModelAttribute("artist") Artist artist, @RequestAttribute("file") MultipartFile[] files , BindingResult bindingResult, Model model) {
		this.artistValidator.validate(artist, bindingResult);
		if(!bindingResult.hasErrors()) {
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
	
}
