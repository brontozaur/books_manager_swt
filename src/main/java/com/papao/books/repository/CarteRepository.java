package com.papao.books.repository;

import com.papao.books.model.Carte;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CarteRepository extends MongoRepository<Carte, String> {

    Carte getByTitluAndIdAutori(String titlu, List<ObjectId> idAutori);

    Page<Carte> getByIdIsOrTitluLikeIgnoreCaseOrIdAutoriContains(String id, String titlu, List<ObjectId> idAutori, Pageable pageable);

    List<Carte> getByIdAutoriContains(ObjectId idAutor);

    //string values - contains
    Page<Carte> getByEdituraIsNullOrEdituraIsOrderByTitluAsc(String emptyString, Pageable pageable);

    Page<Carte> getByEdituraContainsIgnoreCaseOrderByTitluAsc(String editura, Pageable pageable);

    Page<Carte> getByLimbaIsNullOrLimbaIsOrderByTitluAsc(String emptyString, Pageable pageable);

    Page<Carte> getByLimbaContainsIgnoreCaseOrderByTitluAsc(String limba, Pageable pageable);

    Page<Carte> getByEditiaOriginala_LimbaIsNullOrEditiaOriginala_LimbaIsOrderByTitluAsc(String emptyString, Pageable pageable);

    Page<Carte> getByEditiaOriginala_LimbaContainsIgnoreCaseOrderByTitluAsc(String limbaOriginala, Pageable pageable);

    Page<Carte> getByTipCopertaIsNullOrTipCopertaIsOrderByTitluAsc(String emptyString, Pageable pageable);

    Page<Carte> getByTipCopertaContainsIgnoreCaseOrderByTitluAsc(String tipCoperta, Pageable pageable);

    Page<Carte> getByAnAparitieIsNullOrAnAparitieIsOrderByTitluAsc(String emptyString, Pageable pageable);

    Page<Carte> getByAnAparitieContainsIgnoreCaseOrderByTitluAsc(String editura, Pageable pageable);

    // string values - begins with, for first letter tree items
    Page<Carte> getByTitluIsNullOrTitluIsOrderByTitluAsc(String emptyString, Pageable pageable);

    Page<Carte> getByTitluStartingWithIgnoreCaseOrderByTitluAsc(String titluStartsWith, Pageable pageable);

    Page<Carte> getByCreatedAtIsNullOrderByTitluAsc(Pageable pageable);

    Page<Carte> getByCreatedAtBetweenOrderByTitluAsc(Date createdDateStart, Date createdDateEnd,  Pageable pageable);

    Page<Carte> getByUpdatedAtIsNullOrderByTitluAsc(Pageable pageable);

    Page<Carte> getByUpdatedAtBetweenOrderByTitluAsc(Date updatedDateStart, Date updatedDateEnd,  Pageable pageable);

    //string arrays
    Page<Carte> getByTraducatoriIsNullOrTraducatoriIsLessThanEqualOrderByTitluAsc(String[] emptyString, Pageable pageable);

    Page<Carte> getByTraducatoriContainsIgnoreCaseOrderByTitluAsc(String traducator, Pageable pageable);

    //reference collections - using ref ObjectId!

    Page<Carte> getByIdAutoriIsNullOrIdAutoriIsLessThanEqualOrderByTitluAsc(String[] emptyString, Pageable pageable);

    Page<Carte> getByIdAutoriLikeOrderByTitluAsc(ObjectId idAutor, Pageable pageable);

    Page<Carte> getByIdInOrderByTitluAsc(List<ObjectId> bookIds, Pageable pageable);
}
