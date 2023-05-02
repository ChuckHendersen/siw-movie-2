package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Picture;

public interface PictureRepository extends CrudRepository<Picture, Long> {

}
