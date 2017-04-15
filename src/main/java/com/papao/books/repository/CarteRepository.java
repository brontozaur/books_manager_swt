package com.papao.books.repository;

import com.papao.books.model.Carte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarteRepository extends MongoRepository<Carte, String> {

    //string values - contains
    Page<Carte> getByEdituraIsNullOrEdituraIs(String emptyString, Pageable pageable);

    Page<Carte> getByEdituraContainsIgnoreCase(String editura, Pageable pageable);

    Page<Carte> getByLimbaIsNullOrLimbaIs(String emptyString, Pageable pageable);

    Page<Carte> getByLimbaContainsIgnoreCase(String limba, Pageable pageable);

    Page<Carte> getByLimbaOrLimbaIsNullOrLimbaOriginalaIs(String emptyString, Pageable pageable);

    Page<Carte> getByLimbaOriginalaContainsIgnoreCase(String limbaOriginala, Pageable pageable);

    Page<Carte> getByTipCopertaIsNullOrTipCopertaIs(String emptyString, Pageable pageable);

    Page<Carte> getByTipCopertaContainsIgnoreCase(String tipCoperta, Pageable pageable);

    Page<Carte> getByAnAparitieIsNullOrAnAparitieIs(String emptyString, Pageable pageable);

    Page<Carte> getByAnAparitieContainsIgnoreCase(String editura, Pageable pageable);

    // string values - begins with, for first letter tree items
    Page<Carte> getByTitluIsNullOrTitluIs(String emptyString, Pageable pageable);

    Page<Carte> getByTitluStartingWithIgnoreCase(String titluStartsWith, Pageable pageable);

    //string arrays
    Page<Carte> getByTraducatoriIsNullOrTraducatoriIsLessThanEqual(String[] emptyString, Pageable pageable);

    Page<Carte> getByTraducatoriContainsIgnoreCase(String traducator, Pageable pageable);

    //reference collections

    Page<Carte> getByIdAutoriIsNullOrIdAutoriIsLessThanEqual(String[] emptyString, Pageable pageable);

    Page<Carte> getByIdAutoriContainsIgnoreCase(List<String> idAutori, Pageable pageable);
}
