package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Artist {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotBlank
	private String name;
	
	@NotBlank
	private String surname;
	
	@PastOrPresent
	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate birthDate;
	
	@PastOrPresent
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate deceasedDate;
	
	@OneToMany(mappedBy="director")
	private List<Movie> listaFilmDiretti;
	
	@ManyToMany(mappedBy="actors")
	private List<Movie> listaFilmRecitati;
	
	@OneToOne(fetch = FetchType.LAZY)
	private Picture picture;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String nome) {
		this.name = nome;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public void setSurname(String cognome) {
		this.surname = cognome;
	}
	
	public List<Movie> getListaFilmDiretti() {
		return listaFilmDiretti;
	}
	
	public void setListaFilmDiretti(List<Movie> listaFilmDiretti) {
		this.listaFilmDiretti = listaFilmDiretti;
	}
	
	public List<Movie> getListaFilmRecitati() {
		return listaFilmRecitati;
	}
	
	public void setListaFilmRecitati(List<Movie> listaFilmRecitati) {
		this.listaFilmRecitati = listaFilmRecitati;
	}
	
	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public LocalDate getDeceasedDate() {
		return deceasedDate;
	}

	public void setDeceasedDate(LocalDate deceasedDate) {
		this.deceasedDate = deceasedDate;
	}

	public Picture getPicture() {
		return picture;
	}

	public void setPicture(Picture picture) {
		this.picture = picture;
	}

	@Override
	public int hashCode() {
		return Objects.hash(surname, name);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Artist))
			return false;
		Artist other = (Artist) obj;
		return Objects.equals(surname, other.surname) && Objects.equals(name, other.name);
	}
}
