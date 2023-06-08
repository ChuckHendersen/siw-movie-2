package it.uniroma3.siw.controller;

import java.io.IOException;
import java.time.Year;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.form.UpdateMovieForm;
import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.controller.validator.MultipartFileArrayValidator;
import it.uniroma3.siw.controller.validator.MyMultipartFileValidator;
//import it.uniroma3.siw.controller.validator.UpdateMovieValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.MovieService;
import jakarta.validation.Valid;
import static it.uniroma3.siw.controller.ControllerUtils.*;

@Controller
public class MovieController {
	@Autowired private MovieService movieService;
	@Autowired private ArtistService artistService;
	@Autowired private MultipartFileArrayValidator mpfaValidator;
	@Autowired private MyMultipartFileValidator mmpfValidator;
	@Autowired private MovieValidator movieValidator;
	//@Autowired private UpdateMovieValidator updateMovieValidator;

	@GetMapping("/")
	public String index(Model model) {
		return indexGeneral(model);
	}

	@GetMapping("/index")
	public String index1(Model model) {
		return indexGeneral(model);
	}

	private String indexGeneral(Model model) {
		return "index.html";
	}

	@GetMapping("/admin/indexMovie")
	public String indexMovie() {
		return "/admin/indexMovie.html";
	}

	@GetMapping("/admin/formNewMovie")
	public String formNewMovie(Model model) {
		model.addAttribute("movie", this.movieService.createMovie());
		//model.addAttribute("picture", new Picture());
		return "/admin/formNewMovie.html";
	}

	//RICORDARSI DI CAMBIARE I COSTRAINST DI JPA nella tabella di join fra movie e picture (non so come dirlo da java)
	@PostMapping("/admin/movies")
	public String newMovie(@Valid @ModelAttribute("movie") Movie movie,BindingResult movieBr ,@RequestParam("file") MultipartFile[] files, BindingResult filesBr, Model model) throws IOException {
		this.mpfaValidator.validate(files, filesBr);
		this.movieValidator.validate(movie, movieBr);
		if(movieBr.hasErrors() || filesBr.hasErrors()) {
			System.out.println("Dio cane");
			return "/admin/formNewMovie.html";
		}else {
			this.movieService.saveNewMovie(movie, files);
			return "redirect:/admin/formUpdateMovie/"+movie.getId();
		}
	}

	@PostMapping("/admin/updateMovieDetails/{movie_id}")
	public String updateMovieDetails(@PathVariable("movie_id") Long movieId, 
			@Valid @ModelAttribute("updateMovieForm") UpdateMovieForm updateMovieForm, BindingResult bindingResult,
			Model model) {
		//Non va validato altrimenti trova sempre lo stesso film e fallirà sempre
		//this.updateMovieValidator.validate(updateMovieForm, bindingResult); capire come validare quell'aspetto
		if(!bindingResult.hasErrors()) {
			this.movieService.updateMovieDetails(movieId, updateMovieForm);
			return "redirect:/admin/formUpdateMovie/"+movieId;
		}else {
			Movie movie2 = this.movieService.findById(movieId);
			model.addAttribute("movie", movie2);
			System.out.println("test");
			return "/admin/formUpdateMovie.html";
		}
	}

	@PostMapping("/admin/uploadPhoto/{movie_id}")
	public String uploadPhoto(@PathVariable("movie_id") Long movieId, @RequestParam("file") MultipartFile[] files, Model model) throws IOException {
		// Prendere il film, caricare le nuove foto nell'array e poi mettere il movie nel model addattributo
		if(this.mmpfValidator.validate(files)) {
			return redirection(this.movieService.uploadNewPhoto(movieId, files), "redirect:/admin/formUpdateMovie/"+movieId, "movieError.html");
		}else {
			model.addAttribute("messaggioErroreFoto", "Nessun file è stato caricato");
			Movie movie = this.movieService.findById(movieId);
			model.addAttribute("movie", movie);
			model.addAttribute("updateMovieForm", this.movieService.generateUpdateMovieForm(movieId));
			return redirection(movie, "/admin/formUpdateMovie.html", "movieError.html");
		}
	}

	@GetMapping("/admin/deletePhoto/{movie_id}/{photo_id}")
	public String deletePhoto(@PathVariable("movie_id") Long movieId, @PathVariable("photo_id") Long photoId) {
		Movie movie = this.movieService.deletePhoto(movieId, photoId);
		return redirection(movie, "redirect:/admin/formUpdateMovie/"+movieId, "movieError.html");
	}

	@GetMapping("/movies")
	public String showMovies(Model model) {
		model.addAttribute("movies",this.movieService.findAll());
		return "movies.html";
	}

	@GetMapping("/movies/{id}")
	public String getMovie(@PathVariable("id") Long id, @ModelAttribute("credentials") Credentials credentials, Model model) {
		System.out.println(credentials.getUsername());
		System.out.println(credentials.getUser());
		Movie movie = this.movieService.findById(id);
		model.addAttribute("credentials", credentials);
		if(credentials!=null) {
			//System.out.println("Utente loggato");
			model.addAttribute("review", new Review());
		}
		model.addAttribute("movie", movie);
		return redirection(movie, "movie.html", "movieError.html");
	}

	@GetMapping("/formSearchMovies")
	public String formSearchMovies() {
		return "formSearchMovies.html";
	}

	@PostMapping("/searchMovies")
	public String searchMovies(Model model, @RequestParam Year year) {
		if(year!=null)
			model.addAttribute("movies",this.movieService.findByYear(year));
		else {
			model.addAttribute("movies", List.<Movie>of());
		}
		return "foundMovies.html";
	}

	@GetMapping("/admin/manageMovies")
	public String manageMovies(Model model) {
		model.addAttribute("movies", this.movieService.findAll());
		return "/admin/manageMovies.html";
	}

	@GetMapping("/admin/formUpdateMovie/{id}")
	public String formUpdateMovie(@PathVariable Long id,Model model) {
		Movie movie = this.movieService.findById(id);
		model.addAttribute("movie", movie);
		if(movie != null) {
			model.addAttribute("updateMovieForm", this.movieService.generateUpdateMovieForm(id));
		}
		return redirection(movie, "/admin/formUpdateMovie.html", "movieError.html");
	}

	@GetMapping("/admin/addDirectorToMovie/{movie_id}")
	public String addDirector(@PathVariable("movie_id") Long id, Model model) {
		List<Artist> listaArtisti = (List<Artist>) this.artistService.findAll();
		Movie movie = this.movieService.findById(id);
		if(movie != null) {
			if(listaArtisti.isEmpty()) {
				model.addAttribute("messaggioErrore", "Non esistono artisti");
			}else {
				model.addAttribute("artists", listaArtisti);
				model.addAttribute("movie", movie);
			}
			return "/admin/directorsToAdd.html";
		}else {
			return "movieError.html";
		}
	}

	@GetMapping("/admin/setDirectorToMovie/{director_id}/{movie_id}")
	public String setDirectorToMovie(@PathVariable("director_id") Long director_id,@PathVariable("movie_id") Long movie_id, Model model) {
		Movie movie = this.movieService.setDirectorToMovie(director_id, movie_id);
		model.addAttribute("movie", movie);
		return redirection(movie, "/admin/formUpdateMovie.html", "movieError.html");
	}

	@GetMapping("/admin/updateActorsOfMovie/{movie_id}")
	public String addActors(Model model, @PathVariable("movie_id") Long movie_id) {
		Movie movie = movieService.findById(movie_id);
		if(movie !=null) {
			Set<Artist> setAttoriCheNonHannoRecitato = (Set<Artist>) artistService.findAllByListaFilmRecitatiIsNotContaining(movie);
			model.addAttribute("movie", movie);
			model.addAttribute("artists", setAttoriCheNonHannoRecitato);
		}
		return redirection(movie, "/admin/actorsToAdd.html", "movieError.html");
	}

	@GetMapping("/admin/setActorToMovie/{actor_id}/{movie_id}")
	public String setActorToMovie(Model model, @PathVariable("actor_id") Long actor_id, @PathVariable("movie_id") Long movie_id) {
		Movie movie = this.movieService.setActorToMovie(actor_id, movie_id);
		Set<Artist> setAttoriCheNonHannoRecitato = (Set<Artist>) artistService.findAllByListaFilmRecitatiIsNotContaining(movie);
		model.addAttribute("movie", movie);
		model.addAttribute("artists", setAttoriCheNonHannoRecitato);
		return redirection(movie, "/admin/actorsToAdd.html","movieError.html");
	}

	@GetMapping("/admin/deleteActorFromMovie/{actor_id}/{movie_id}")
	public String deleteActorFromMovie(Model model, @PathVariable("actor_id") Long actor_id, @PathVariable("movie_id") Long movie_id) {
		Movie movie = movieService.deleteActorFromMovie(actor_id, movie_id);
		Set<Artist> setAttoriCheNonHannoRecitato = (Set<Artist>) artistService.findAllByListaFilmRecitatiIsNotContaining(movie);
		model.addAttribute("movie", movie);
		model.addAttribute("artists", setAttoriCheNonHannoRecitato);
		return redirection(movie,"/admin/actorsToAdd.html","movieError.html");
	}

	@GetMapping("/admin/moviesToDeleteIndex")
	public String moviesToDeleteIndex(Model model) {
		Iterable<Movie> movies = this.movieService.findAll();
		model.addAttribute("movies", movies);
		return "/admin/movieToDeleteIndex.html";
	}

	@GetMapping("/admin/confirmMovieDeletion/{movie_id}")
	public String confirmMovieDeletion(@PathVariable("movie_id") Long movieId, Model model) {
		Movie movie = this.movieService.findById(movieId);
		model.addAttribute("movie", movie);
		return redirection(movie, "/admin/confirmMovieDeletion.html", "movieError.html");
	}

	// DA TESTARE
	@GetMapping("/admin/deleteMovie/{movie_id}")
	public String deleteMovie(@PathVariable("movie_id") Long movieId, Model model) {
		return redirection(this.movieService.deleteMovie(movieId), "redirect:/admin/moviesToDeleteIndex", "movieError.html");
	}
}