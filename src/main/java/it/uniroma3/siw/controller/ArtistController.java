package it.uniroma3.siw.controller;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import it.uniroma3.siw.controller.validator.ArtistValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.service.ArtistService;
import jakarta.validation.Valid;
import static it.uniroma3.siw.controller.ControllerUtils.*;
@Controller
public class ArtistController {

	@Autowired
	private ArtistValidator artistValidator;

	@Autowired
	private ArtistService artistService;


	@GetMapping("/admin/indexArtist")
	public String indexArtist() {
		return "/admin/indexArtist.html";
	}

	@GetMapping("/admin/formNewArtist")
	public String formNewArtist(Model model) {
		model.addAttribute("artist", this.artistService.createArtist());
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
			artist = this.artistService.saveNewArtist(artist, file);
			model.addAttribute(artist);
			return "artist.html";
		}else {
			//model.addAttribute("messaggioErrore", "Questo artista è già presente");
			return "/admin/formNewArtist.html";
		}
	}

	@GetMapping("/admin/artistToDeleteIndex")
	public String artistToDeleteIndex(Model model) {
		model.addAttribute("artists",this.artistService.findAll());
		return "/admin/artistToDeleteIndex.html";
	}

	@GetMapping("/admin/confirmArtistDeletion/{artist_id}")
	public String confirmArtistDeletion(@PathVariable("artist_id") Long artistId, Model model) {
		Artist artist = this.artistService.findById(artistId);
		model.addAttribute("artist", artist);
		return redirection(artist, "/admin/confirmArtistDeletion.html", "artistError.html");
	}

	@GetMapping("/admin/deleteArtist/{artist_id}")
	public String deleteArtist(@PathVariable("artist_id") Long artistId, Model model) {
		Artist artist = this.artistService.deleteArtist(artistId);
		return redirection(artist, "redirect:/admin/artistToDeleteIndex", "artistError.html");
	}

	@GetMapping("/artists")
	public String artists(Model model) {
		Iterable<Artist> listaArtisti = this.artistService.findAll();
		model.addAttribute("artists", listaArtisti);		
		return "artists.html";
	}

	@GetMapping("/artists/{id}")
	public String artist(@PathVariable Long id, Model model) {
		Artist artist = this.artistService.findById(id);
		model.addAttribute("artist", artist);
		return redirection(artist, "artist.html","artistError.html");
	}

	@GetMapping("/admin/manageArtists")
	public String manageArtist(Model model) {
		Iterable<Artist> artists = this.artistService.findAll();
		model.addAttribute("artists", artists);
		return "/admin/manageArtists.html";
	}

	@GetMapping("/admin/formUpdateArtist/{artist_id}")
	public String formUpdateArtist(@PathVariable("artist_id") Long artistId, Model model) {
		Artist artist = this.artistService.findById(artistId);
		model.addAttribute("artist", artist);
		return redirection(artist, "/admin/formUpdateArtist.html", "artistError.html");
	}

	@PostMapping("/admin/updateArtistDetails/{artist_id}")
	public String updateArtistDetails(@PathVariable("artist_id") Long artistId,
			@Valid @ModelAttribute("artist") Artist artist,
			BindingResult bindingResult, 
			Model model) {
		if(!bindingResult.hasErrors()) {
			Artist originalArtist = this.artistService.updateArtistdetails(artistId, artist);
			return redirection(originalArtist, "redirect:/admin/formUpdateArtist/"+artistId, "artistError.html");
		}else {
			//gestione degli input errati che ne so
			return null;
		}
	}

	@PostMapping("/admin/updateArtistPicture/{artist_id}")
	public String updateArtistPicture(@PathVariable("artist_id") Long artistId, 
			@RequestAttribute("file") MultipartFile file, 
			Model model) throws IOException {
		Artist artist = this.artistService.updateArtistPicture(artistId, file);
		return redirection(artist, "redirect:/admin/formUpdateArtist/"+artistId, "artistError.html");
	}
}
