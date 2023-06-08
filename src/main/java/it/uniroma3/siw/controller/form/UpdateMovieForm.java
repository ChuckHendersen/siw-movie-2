package it.uniroma3.siw.controller.form;

import java.time.Year;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;

public class UpdateMovieForm {
	
	@NotBlank
	private String title;
	@PastOrPresent
	private Year year;
	
	
	public UpdateMovieForm(String title, Year year) {
		this.year=year;
		this.title=title;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Year getYear() {
		return year;
	}
	public void setYear(Year year) {
		this.year = year;
	}
	
	
}
