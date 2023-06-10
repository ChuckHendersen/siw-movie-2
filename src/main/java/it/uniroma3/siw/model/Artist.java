package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.util.*;
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

	public Artist() {
		this.listaFilmDiretti = new ArrayList<>();
		this.listaFilmRecitati = new ArrayList<>();
	}

	public Artist(String name, String surname, LocalDate birthDate, LocalDate deceasedDate, Picture picture,
			List<Movie> listaFilmRecitati, List<Movie> listaFilmDiretti) {
		this.name = name;
		this.surname = surname;
		this.birthDate = birthDate;
		this.deceasedDate = deceasedDate;
		this.picture = picture;
		this.listaFilmRecitati = listaFilmRecitati;
		this.listaFilmDiretti = listaFilmDiretti;
	}

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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Artista\n")
		.append("Nome: ")
		.append(this.name)
		.append("\nCognome: ")
		.append(this.surname)
		.append("\nData nascita: ")
		.append(this.birthDate.toString())
		.append("\nData morte: ");
		if(this.deceasedDate!=null) {
			sb.append(this.deceasedDate.toString());
		}else {
			sb.append("non deceduto");
		}
		return sb.toString();
	}

	public String birthDateToString() {
		return this.birthDate.getDayOfMonth()+"-"+this.birthDate.getMonthValue()+"-"+this.birthDate.getYear();
	}

	public String deceasedDateToString() {
		if(this.deceasedDate!=null) {
			return this.deceasedDate.getDayOfMonth()+"-"+this.deceasedDate.getMonthValue()+"-"+this.deceasedDate.getYear();
		}else {
			return "non deceduto";
		}
	}
}
