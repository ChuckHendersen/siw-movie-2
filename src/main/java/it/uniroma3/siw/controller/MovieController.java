package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Picture;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.PictureRepository;
import it.uniroma3.siw.repository.UserRepository;
import it.uniroma3.siw.service.CredentialsService;

import jakarta.validation.Valid;

@Controller
public class MovieController {

	@Autowired MovieRepository movieRepository;
	@Autowired ArtistRepository artistRepository;
	@Autowired MovieValidator movieValidator;
	@Autowired PictureRepository pictureRepository;
	@Autowired UserRepository userRepository;
	@Autowired CredentialsService credentialsService;

	@GetMapping("/")
	public String index(Model model) {
		return indexGeneral(model);
	}

	@GetMapping("/index")
	public String index1(Model model) {
		return indexGeneral(model);
	}
	
	private String indexGeneral(Model model) {
		//model.addAttribute("credentials", this.getCredentials());
		return "index.html";
	}
	
	@GetMapping("/admin/indexMovie")
	public String indexMovie() {
		return "/admin/indexMovie.html";
	}

	@GetMapping("/admin/formNewMovie")
	public String formNewMovie(Model model) {
		model.addAttribute("movie", new Movie());
		//model.addAttribute("picture", new Picture());
		return "/admin/formNewMovie.html";
	}
	
	//RICORDARSI DI CAMBIARE I COSTRAINST DI JPA nella tabella di join fra movie e picture (non so come dirlo da java)
	@PostMapping("/admin/movies")
	public String newMovie(@Valid @ModelAttribute("movie") Movie movie, @RequestParam("file") MultipartFile[] files, BindingResult bindingResult, Model model) throws IOException {
		this.movieValidator.validate(movie, bindingResult);
		if(!bindingResult.hasErrors()) {
			movie.setPictures(new HashSet<Picture>());
			Picture[] pictures = this.savePictureIfNotExistsOrRetrieve(files);
			for(Picture p:pictures) {
				movie.getPictures().add(p);
			}
			movie = this.movieRepository.save(movie);
			return "redirect:/movies/"+movie.getId();
		} else {
			//model.addAttribute("messaggioErrore", "Questo film esiste già");
			return "/admin/formNewMovie.html";
		}
	}

	@PostMapping("/admin/uploadPhoto/{movie_id}")
	public String uploadPhoto(@PathVariable("movie_id") Long movieId, @RequestParam("file") MultipartFile[] files, Model model) throws IOException {
		// Prendere il film, caricare le nuove foto nell'array e poi mettere il movie nel model addattribute
		Movie movie = movieRepository.findById(movieId).get();
		Picture[] pictures = this.savePictureIfNotExistsOrRetrieve(files);
		for(Picture p:pictures) {
			movie.getPictures().add(p);	
		}
		movieRepository.save(movie);
		model.addAttribute("movie", movie);
		return "/admin/formUpdateMovie.html";
	}

	@GetMapping("/movies")
	public String showMovies(Model model) {
		model.addAttribute("movies",this.movieRepository.findAll());
		return "movies.html";
	}

	@GetMapping("/movies/{id}")
	public String getMovie(@PathVariable("id") Long id, Model model) {
		Credentials credentials = this.getCredentials();
		model.addAttribute("credentials", credentials);
		if(credentials!=null) {
			//System.out.println("Utente loggato");
			model.addAttribute("review", new Review());
		}
		Movie movie;
		try {
			movie = this.movieRepository.findById(id).get();
		}catch(Exception e) {
			movie = null;
		}
		//movie = this.movieRepository.findById(id).get();
		model.addAttribute("movie", movie);
		return "movie.html";
	}

	@GetMapping("/formSearchMovies")
	public String formSearchMovies() {
		return "formSearchMovies.html";
	}

	@PostMapping("/searchMovies")
	public String searchMovies(Model model, @RequestParam Integer year) {
		model.addAttribute("movies",this.movieRepository.findByYear(year));
		return "foundMovies.html";
	}

	@GetMapping("/admin/manageMovies")
	public String manageMovies(Model model) {
		model.addAttribute("movies", movieRepository.findAll());
		return "/admin/manageMovies.html";
	}

	@GetMapping("/admin/formUpdateMovie/{id}")
	public String formUpdateMovie(@PathVariable Long id,Model model) {
		Movie movie;
		try {
			movie = this.movieRepository.findById(id).get();
		}catch(Exception e) {
			movie = null;
		}
		//movie = this.movieRepository.findById(id).get();
		model.addAttribute("movie", movie);
		return "/admin/formUpdateMovie.html";
	}

	@GetMapping("/admin/addDirectorToMovie/{movie_id}")
	public String addDirector(@PathVariable("movie_id") Long id, Model model) {
		List<Artist> listaArtisti = (List<Artist>) this.artistRepository.findAll();
		Movie movie = (Movie) this.movieRepository.findById(id).get();
		if(listaArtisti.isEmpty()) {
			model.addAttribute("messaggioErrore", "Non esistono artisti");
		}else {
			model.addAttribute("artists", listaArtisti);
			model.addAttribute("movie", movie);
		}
		return "/admin/directorsToAdd.html";
	}

	@GetMapping("/admin/setDirectorToMovie/{director_id}/{movie_id}")
	public String setDirectorToMovie(@PathVariable("director_id") Long director_id,@PathVariable("movie_id") Long movie_id, Model model) {
		Artist director=null;
		Movie movie = null;
		try {
			movie = (Movie)this.movieRepository.findById(movie_id).get();
			director=(Artist)this.artistRepository.findById(director_id).get();
			movie.setDirector(director);
			director.getListaFilmDiretti().add(movie);
			movieRepository.save(movie);
			artistRepository.save(director);
			model.addAttribute("movie", movie);
		}catch(Exception e){
			if(director==null) {
				model.addAttribute("messaggioErrore", "L'artista non esiste");
			}else if(movie==null) {
				model.addAttribute("messaggioErrore", "L'artista non esiste");
			}else {
				model.addAttribute("messaggioErrore", "Errore Generico");
			}
		}
		return "/admin/formUpdateMovie.html";
	}

	@GetMapping("/admin/updateActorsOfMovie/{movie_id}")
	public String addActors(Model model, @PathVariable("movie_id") Long movie_id) {
		Movie movie = movieRepository.findById(movie_id).get();
		Set<Artist> setAttoriCheNonHannoRecitato = (Set<Artist>) artistRepository.findAllByListaFilmRecitatiIsNotContaining(movie);
		model.addAttribute("movie", movie);
		model.addAttribute("artists", setAttoriCheNonHannoRecitato);
		return "/admin/actorsToAdd.html";
	}

	@GetMapping("/admin/setActorToMovie/{actor_id}/{movie_id}")
	public String setActorToMovie(Model model, @PathVariable("actor_id") Long actor_id, @PathVariable("movie_id") Long movie_id) {
		Movie movie = movieRepository.findById(movie_id).get();
		Artist actor = artistRepository.findById(actor_id).get();
		if(!movie.getActors().contains(actor)) {
			movie.getActors().add(actor);
			actor.getListaFilmRecitati().add(movie);
			movieRepository.save(movie);
			//artistRepository.save(actor); duplica le entry nel database. Fare solo 1 save?
		}
		Set<Artist> setAttoriCheNonHannoRecitato = (Set<Artist>) artistRepository.findAllByListaFilmRecitatiIsNotContaining(movie);
		model.addAttribute("movie", movie);
		model.addAttribute("artists", setAttoriCheNonHannoRecitato);
		return "/admin/actorsToAdd.html";
	}

	@GetMapping("/admin/deleteActorFromMovie/{actor_id}/{movie_id}")
	public String deleteActorFromMovie(Model model, @PathVariable("actor_id") Long actor_id, @PathVariable("movie_id") Long movie_id) {
		Movie movie = movieRepository.findById(movie_id).get();
		Artist actor2beRemoved = artistRepository.findById(actor_id).get();
		movie.getActors().remove(actor2beRemoved);
		movieRepository.save(movie);
		Set<Artist> setAttoriCheNonHannoRecitato = (Set<Artist>) artistRepository.findAllByListaFilmRecitatiIsNotContaining(movie);
		model.addAttribute("movie", movie);
		model.addAttribute("artists", setAttoriCheNonHannoRecitato);
		return "/admin/actorsToAdd.html";
	}
	
	private Credentials getCredentials() {
		if(!SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
			UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
			return credentials;
		}
		return null;
	}
	
	private Picture[] savePictureIfNotExistsOrRetrieve(MultipartFile[] files) throws IOException {
		Picture[] pictures = new Picture[files.length];
		int i=0;
		for(MultipartFile f:files) {
			Picture picture;
			if(!pictureRepository.existsByName(f.getResource().getFilename())) {
				//System.out.println("la foto non esiste");
				picture = new Picture();
				picture.setName(f.getResource().getFilename());
				picture.setData(f.getBytes());
				this.pictureRepository.save(picture);
			}else {
				System.out.println("La foto già esiste");
				picture = this.pictureRepository.findByName(f.getResource().getFilename());
			}
			pictures[i] = picture;
			i++;	
		}
		return pictures;
	}
}
