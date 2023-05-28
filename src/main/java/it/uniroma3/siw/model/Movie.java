package it.uniroma3.siw.model;
import java.time.Year;
import java.util.Objects;
import java.util.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
public class Movie {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@PastOrPresent
	private Year year;
	
	@NotBlank
	private String title;

	@ManyToOne
	private Artist director;

	@ManyToMany(fetch = FetchType.LAZY)
	private Set<Artist> actors;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "reviewedMovie")
	private Set<Review> reviews;

	@ManyToMany(fetch = FetchType.LAZY)
	private	Set<Picture> pictures;

	public Movie() {
		this.actors = new HashSet<Artist>();
		this.reviews = new HashSet<Review>();
		this.pictures = new HashSet<Picture>();
	}
	
	public Artist getDirector() {
		return director;
	}

	public void setDirector(Artist director) {
		this.director = director;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Year getYear() {
		return year;
	}

	public void setYear(Year year) {
		this.year = year;
	}

	@Override
	public int hashCode() {
		return Objects.hash(title, year);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Movie))
			return false;
		Movie other = (Movie) obj;
		return Objects.equals(title, other.title) && Objects.equals(year, other.year);
	}

	public Set<Artist> getActors() {
		return actors;
	}

	public void setActors(Set<Artist> actors) {
		this.actors = actors;
	}

	public Set<Review> getReviews() {
		return reviews;
	}

	public void setReviews(Set<Review> reviews) {
		this.reviews = reviews;
	}

	public Set<Picture> getPictures() {
		return pictures;
	}

	public void setPictures(Set<Picture> pictures) {
		this.pictures = pictures;
	}

	public float getAverageVote() {
		float risultato=0;
		if(this.reviews.size()>0) {
			for(Review r: this.reviews) {
				risultato = risultato + r.getVote();
			}
			risultato = risultato/this.reviews.size();
			//flooring
			int aux = (int) (risultato*10);
			risultato = (float) aux/10f;
		}
		return risultato;
		
	}
	
	public boolean areThereAnyReviews() {
		return reviews.size()>0;
	}
}
