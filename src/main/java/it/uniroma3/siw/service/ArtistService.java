package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

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
			artist.setPicture(null);
			artistRepository.save(artist);
			if(artistPicture != null) {
				pictureService.delete(artistPicture);
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
	public Artist updateArtistdetails(Long artistId, Artist artistUpdatedDetails) {
		Artist originalArtist = this.findById(artistId);
		if(originalArtist!=null && artistUpdatedDetails != null) {
			originalArtist.setName(artistUpdatedDetails.getName());
			originalArtist.setSurname(artistUpdatedDetails.getSurname());
			originalArtist.setBirthDate(artistUpdatedDetails.getBirthDate());
			originalArtist.setDeceasedDate(artistUpdatedDetails.getDeceasedDate());
			this.artistRepository.save(originalArtist);
		}
		return originalArtist;
	}

	public Set<Artist> findAllByListaFilmRecitatiIsNotContaining(Movie movie) {
		return this.artistRepository.findAllByListaFilmRecitatiIsNotContaining(movie);
	}

}
