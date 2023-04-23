package it.uniroma3.siw.controller;

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

import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import jakarta.validation.Valid;

@Controller
public class MovieController {

	@Autowired MovieRepository movieRepository;
	@Autowired ArtistRepository artistRepository;
	@Autowired MovieValidator movieValidator;

	@GetMapping("/index")
	public String index(Model model) {
		return "index.html";
	}

	@GetMapping("/indexMovie")
	public String indexMovie() {
		return "indexMovie.html";
	}

	@GetMapping("/formNewMovie")
	public String formNewMovie(Model model) {
		model.addAttribute("movie", new Movie());
		return "formNewMovie.html";
	}

	@PostMapping("/movies")
	public String newMovie(@Valid @ModelAttribute("movie") Movie movie, BindingResult bindingResult, Model model) {
		this.movieValidator.validate(movie, bindingResult);
		if(!bindingResult.hasErrors()) {
			this.movieRepository.save(movie);
			model.addAttribute("movie", movie);
			return "movie.html";
		} else {
			//model.addAttribute("messaggioErrore", "Questo film esiste già");
			return "formNewMovie.html";
		}
	}

	@GetMapping("/movies")
	public String showMovies(Model model) {
		model.addAttribute("movies",this.movieRepository.findAll());
		return "movies.html";
	}

	@GetMapping("/movies/{id}")
	public String getMovie(@PathVariable("id") Long id, Model model) {
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

	@GetMapping("/manageMovies")
	public String manageMovies(Model model) {
		model.addAttribute("movies", movieRepository.findAll());
		return "manageMovies.html";
	}

	@GetMapping("formUpdateMovie/{id}")
	public String formUpdateMovie(@PathVariable Long id,Model model) {
		Movie movie;
		try {
			movie = this.movieRepository.findById(id).get();
		}catch(Exception e) {
			movie = null;
		}
		//movie = this.movieRepository.findById(id).get();
		model.addAttribute("movie", movie);
		return "formUpdateMovie.html";
	}

	@GetMapping("addDirectorToMovie/{movie_id}")
	public String addDirector(@PathVariable("movie_id") Long id, Model model) {
		List<Artist> listaArtisti = (List<Artist>) this.artistRepository.findAll();
		Movie movie = (Movie) this.movieRepository.findById(id).get();
		if(listaArtisti.isEmpty()) {
			model.addAttribute("messaggioErrore", "Non esistono artisti");
		}else {
			model.addAttribute("artists", listaArtisti);
			model.addAttribute("movie", movie);
		}
		return "directorsToAdd.html";
	}

	@GetMapping("setDirectorToMovie/{director_id}/{movie_id}")
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
		return "formUpdateMovie.html";
	}

	@GetMapping("/updateActorsOfMovie/{movie_id}")
	public String addActors(Model model, @PathVariable("movie_id") Long movie_id) {
		Movie movie = movieRepository.findById(movie_id).get();
		Set<Artist> setAttoriCheNonHannoRecitato = (Set<Artist>) artistRepository.findAllByListaFilmRecitatiIsNotContaining(movie);
		model.addAttribute("movie", movie);
		model.addAttribute("artists", setAttoriCheNonHannoRecitato);
		return "actorsToAdd.html";
	}

	@GetMapping("/setActorToMovie/{actor_id}/{movie_id}")
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
		return "actorsToAdd.html";
	}

	@GetMapping("/deleteActorFromMovie/{actor_id}/{movie_id}")
	public String deleteActorFromMovie(Model model, @PathVariable("actor_id") Long actor_id, @PathVariable("movie_id") Long movie_id) {
		Movie movie = movieRepository.findById(movie_id).get();
		Artist actor2beRemoved = artistRepository.findById(actor_id).get();
		movie.getActors().remove(actor2beRemoved);
		movieRepository.save(movie);
		Set<Artist> setAttoriCheNonHannoRecitato = (Set<Artist>) artistRepository.findAllByListaFilmRecitatiIsNotContaining(movie);
		model.addAttribute("movie", movie);
		model.addAttribute("artists", setAttoriCheNonHannoRecitato);
		return "actorsToAdd.html";
	}
}
