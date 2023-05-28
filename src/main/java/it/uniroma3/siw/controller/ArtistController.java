package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import it.uniroma3.siw.controller.validator.ArtistValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Picture;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
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

	@Autowired
	private MovieRepository movieRepository;

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
			Picture picture = this.savePicture(file);
			artist.setPicture(picture);
			this.artistRepository.save(artist);
			model.addAttribute(artist);
			return "artist.html";
		}else {
			//model.addAttribute("messaggioErrore", "Questo artista è già presente");
			return "/admin/formNewArtist.html";
		}
	}

	@GetMapping("/admin/artistToDeleteIndex")
	public String artistToDeleteIndex(Model model) {
		model.addAttribute("artists",this.artistRepository.findAll());
		return "/admin/artistToDeleteIndex.html";
	}

	@GetMapping("/admin/confirmArtistDeletion/{artist_id}")
	public String confirmArtistDeletion(@PathVariable("artist_id") Long artistId, Model model) {
		Artist artist = this.artistRepository.findById(artistId).orElse(null);
		if(artist==null) {
			return "artistError.html";
		}else {
			model.addAttribute("artist", artist);
			return "/admin/confirmArtistDeletion.html";
		}
	}

	@GetMapping("/admin/deleteArtist/{artist_id}")
	public String deleteArtist(@PathVariable("artist_id") Long artistId, Model model) {
		Artist artist = this.artistRepository.findById(artistId).orElse(null);
		if(artist != null) {
			List<Movie> filmRecitati;
			List<Movie> filmDiretti;
			Picture artistPicture;
			filmDiretti = artist.getListaFilmDiretti();
			filmRecitati = artist.getListaFilmRecitati();
			artistPicture = artist.getPicture();
			Hibernate.initialize(filmRecitati);
			Hibernate.initialize(filmDiretti);
			Hibernate.initialize(artistPicture);
			artist.setListaFilmDiretti(null);
			artist.setListaFilmRecitati(null);
			artist.setPicture(null);
			artistRepository.save(artist);
			if(artistPicture != null) {
				pictureRepository.delete(artistPicture);
			}

			for(Movie m:filmRecitati) {
				Set<Artist> actors = m.getActors();
				Hibernate.initialize(actors);
				actors.remove(artist);
				this.movieRepository.save(m);
			}

			for(Movie m:filmDiretti) {
				m.setDirector(null);
				this.movieRepository.save(m);
			}
			//finalmente si cancella l'artista
			this.artistRepository.delete(artist);
			return "redirect:/admin/artistToDeleteIndex";
		}
		return "artistError.html";
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

	@GetMapping("/admin/manageArtists")
	public String manageArtist(Model model) {
		Iterable<Artist> artists = this.artistRepository.findAll();
		model.addAttribute("artists", artists);
		return "/admin/manageArtists.html";
	}

	@GetMapping("/admin/formUpdateArtist/{artist_id}")
	public String formUpdateArtist(@PathVariable("artist_id") Long artistId, Model model) {
		Artist artist = this.artistRepository.findById(artistId).orElse(null);
		if(artist != null) {
			model.addAttribute("artist", artist);
			return "/admin/formUpdateArtist.html";
		}
		return "artistError.html";
	}

	@PostMapping("/admin/updateArtistDetails/{artist_id}")
	public String updateArtistDetails(@PathVariable("artist_id") Long artistId,
			@Valid @ModelAttribute("artist") Artist artist,
			BindingResult bindingResult, 
			Model model) {
		Artist originalArtist = this.artistRepository.findById(artistId).orElse(null);
		if(originalArtist!=null && artist != null && !bindingResult.hasErrors()) {
			originalArtist.setName(artist.getName());
			originalArtist.setSurname(artist.getSurname());
			originalArtist.setBirthDate(artist.getBirthDate());
			originalArtist.setDeceasedDate(artist.getDeceasedDate());
			this.artistRepository.save(originalArtist);
			return "redirect:/admin/formUpdateArtist/"+artistId;
		}
		return "artistError.html";
	}

	@PostMapping("/admin/updateArtistPicture/{artist_id}")
	public String updateArtistPicture(@PathVariable("artist_id") Long artistId, 
			@RequestAttribute("file") MultipartFile file, 
			Model model) throws IOException {
		Artist artist = this.artistRepository.findById(artistId).orElse(null);
		if(artist != null) {
			Picture newPicture = this.savePicture(file);
			if(newPicture != null) {
				Picture oldPicture = artist.getPicture();
				artist.setPicture(newPicture);
				this.artistRepository.save(artist);
				if(oldPicture != null) {
					this.pictureRepository.delete(oldPicture);
				}
			}
			return "redirect:/admin/formUpdateArtist/"+artistId;
		}
		return "artistError.html";
	}

	private Picture savePicture(MultipartFile f) throws IOException {
		Picture picture;
		if(f.getSize()!=0) {
			//System.out.println("la foto non esiste");
			picture = new Picture();
			picture.setName(f.getResource().getFilename());
			picture.setData(f.getBytes());
			return this.pictureRepository.save(picture);
		}else {
			return null;
		}
	}
}
