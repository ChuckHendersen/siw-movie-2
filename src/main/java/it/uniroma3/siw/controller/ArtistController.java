package it.uniroma3.siw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;

@Controller
public class ArtistController {

	@Autowired 
	private ArtistRepository artistRepository;
	
	@GetMapping("/indexArtist")
	public String indexArtist() {
		return "indexArtist.html";
	}
	
	@GetMapping("/formNewArtist")
	public String formNewArtist(Model model) {
		model.addAttribute("artist", new Artist());
		return "formNewArtist.html";
	}
	
	@PostMapping("/artists")
	public String newArtist(@ModelAttribute("artist") Artist artist,Model model) {
		if(!artistRepository.existsByNameAndSurname(artist.getName(),artist.getSurname())) {
			this.artistRepository.save(artist);
			model.addAttribute(artist);
			return "artist.html";
		}else {
			model.addAttribute("messaggioErrore", "Questo artista è già presente");
			return "formNewArtist.html";
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
