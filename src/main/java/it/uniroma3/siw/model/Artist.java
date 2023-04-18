package it.uniroma3.siw.model;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class Artist {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	private String surname;
	
	@OneToMany(mappedBy="director")
	private List<Movie> listaFilmDiretti;
	
	@ManyToMany(mappedBy="actors")
	private List<Movie> listaFilmRecitati;
	
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
