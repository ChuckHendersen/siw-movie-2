package it.uniroma3.siw.controller.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class UpdateArtistForm {
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


    public UpdateArtistForm(String name, String surname, LocalDate birthDate, LocalDate deceasedDate) {
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.deceasedDate = deceasedDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public LocalDate getDeceasedDate() {
        return deceasedDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setDeceasedDate(LocalDate deceasedDate) {
        this.deceasedDate = deceasedDate;
    }
}
