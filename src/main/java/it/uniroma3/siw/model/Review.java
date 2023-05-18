package it.uniroma3.siw.model;

import java.util.Objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
// Uno User può scrivere una sola recensione per film
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = {"reviewed_movie_id", "author_id"}) })
public class Review {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotBlank
	private String title;
	
	@Max(5)
	@Min(1)
	private Integer vote;
	
	@NotBlank
	private String text;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Movie reviewedMovie;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private User author;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getVote() {
		return vote;
	}

	public void setVote(Integer vote) {
		this.vote = vote;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Movie getReviewedMovie() {
		return reviewedMovie;
	}

	public void setReviewedMovie(Movie reviewedMovie) {
		this.reviewedMovie = reviewedMovie;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public boolean isAuthor(User user) {
		return author.equals(user);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(author, reviewedMovie);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Review))
			return false;
		Review other = (Review) obj;
		return Objects.equals(author, other.author) && Objects.equals(reviewedMovie, other.reviewedMovie);
	}
}
