package it.uniroma3.siw.model;

import java.util.Objects;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Entity
public class Movie {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotNull
	@Min(1900)
	@Max(2023)
	private Integer year;
	
	private String urlImage;
	
	@NotBlank
	private String title;
	
	
	@ManyToOne
	private Artist director;
	
	@ManyToMany
	private Set<Artist> actors;
	
	@OneToMany
	@JoinColumn(name="movie_id")
	private Set<News> news;
	
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
	
	public Integer getYear() {
		return year;
	}
	
	public void setYear(Integer year) {
		this.year = year;
	}
	
	public String getUrlImage() {
		return urlImage;
	}
	
	public void setUrlImage(String urlImage) {
		this.urlImage = urlImage;
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
	
}
