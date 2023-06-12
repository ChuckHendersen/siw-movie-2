package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import it.uniroma3.siw.controller.form.UpdateArtistForm;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Picture;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;

@Service
public class ArtistService {

	@Autowired
	private ArtistRepository artistRepository;

	@Autowired
	private PictureService pictureService;

	@Autowired
	private MovieRepository movieRepository;

	public Artist createArtist() {
		return new Artist();
	}

	@Transactional
	public Artist saveNewArtist(Artist artist, MultipartFile f) throws IOException {
		Picture picture = this.pictureService.savePicture(f);
		artist.setPicture(picture);
		Artist savedArtist =  this.artistRepository.save(artist);
		return savedArtist;
	}

	@Transactional
	public Artist updateArtistPicture(Long artistId, MultipartFile file) throws IOException {
		Artist artist = this.findById(artistId);
		if(artist != null) {
			Picture oldPicture = artist.getPicture();
			Picture picture = this.pictureService.savePicture(file);
			artist.setPicture(picture);
			this.artistRepository.save(artist);
			if(oldPicture != null) {
				this.pictureService.delete(oldPicture);
			}
		}
		return artist;
	}

	@Transactional
	public Iterable<Artist> findAll(){
		return this.artistRepository.findAll();
	}

	@Transactional
	public Artist deleteArtist(Long artistId) {
		Artist artist = this.findById(artistId);
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
			//nevvero, ora si cancella la foto
			if(artistPicture != null) {
				pictureService.delete(artistPicture);
			}
		}
		return artist;
	}

	/**
	 * Returns an artist if id is valid, null otherwise
	 * @param artistId identifier for an artist
	 * @return the artist of the associated id or null
	 */
	@Transactional
	public Artist findById(Long artistId) {
		return this.artistRepository.findById(artistId).orElse(null);
	}


	@Transactional
	public Artist updateArtistDetails(Long artistId, UpdateArtistForm updateArtistForm) {
		Artist originalArtist = this.findById(artistId);
		if(originalArtist!=null && updateArtistForm != null) {
			originalArtist.setName(updateArtistForm.getName());
			originalArtist.setSurname(updateArtistForm.getSurname());
			originalArtist.setBirthDate(updateArtistForm.getBirthDate());
			originalArtist.setDeceasedDate(updateArtistForm.getDeceasedDate());
			return this.artistRepository.save(originalArtist);
		}
		return originalArtist;
	}

	@Transactional
	public Set<Artist> findAllByListaFilmRecitatiIsNotContaining(Movie movie) {
		return this.artistRepository.findAllByListaFilmRecitatiIsNotContaining(movie);
	}

	@Transactional
    public boolean alreadyExists(String name, String surname) {
		return this.artistRepository.existsByNameAndSurname(name, surname);
    }

	@Transactional
	public UpdateArtistForm generateUpdateArtistForm(Long artistId) {
		Artist artist = this.findById(artistId);
		UpdateArtistForm updateArtistForm=null;
		if(artist != null) {
			updateArtistForm = new UpdateArtistForm(artist.getName(),artist.getSurname(),artist.getBirthDate(),artist.getDeceasedDate());
		}
		return updateArtistForm;
	}
}
