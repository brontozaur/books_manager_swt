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

    Page<Carte> getByIdIsOrTitluLikeIgnoreCaseOrSubtitluLikeIgnoreCaseOrIdAutoriContainsOrSerie_NumeLikeIgnoreCaseOrVolumLikeIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String id, String titlu, String subtitlu, List<ObjectId> idAutori, String serie, String volum, Pageable pageable);

    List<Carte> getByIdAutoriContainsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(ObjectId idAutor);

    Page<Carte> getByCopertaFataIsNullOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(Pageable pageable);

    Page<Carte> getByIdAutoriInOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(List<ObjectId> idAutori, Pageable pageable);

    Page<Carte> getByIsbnIsNullOrIsbnIsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String emptyString, Pageable pageable);

    Page<Carte> getByIdNotInOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(List<ObjectId> bookIds, Pageable pageable);

    //string values - contains
    Page<Carte> getByEdituraIsNullOrEdituraIsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String emptyString, Pageable pageable);

    Page<Carte> getByEdituraContainsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String editura, Pageable pageable);

    Page<Carte> getByColectieIsNullOrEdituraIsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String emptyString, Pageable pageable);

    Page<Carte> getByColectieContainsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String editura, Pageable pageable);

    Page<Carte> getByLocatieIsNullOrLocatieIsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String emptyString, Pageable pageable);

    Page<Carte> getByLocatieContainsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String locatie, Pageable pageable);

    Page<Carte> getByLimbaIsNullOrLimbaIsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String emptyString, Pageable pageable);

    Page<Carte> getByLimbaContainsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String limba, Pageable pageable);

    Page<Carte> getByEditiaOriginala_LimbaIsNullOrEditiaOriginala_LimbaIsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String emptyString, Pageable pageable);

    Page<Carte> getByEditiaOriginala_LimbaContainsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String limbaOriginala, Pageable pageable);

    Page<Carte> getByTipCopertaIsNullOrTipCopertaIsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String emptyString, Pageable pageable);

    Page<Carte> getByTipCopertaContainsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String tipCoperta, Pageable pageable);

    Page<Carte> getByAnAparitieIsNullOrAnAparitieIsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String emptyString, Pageable pageable);

    Page<Carte> getByAnAparitieContainsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String editura, Pageable pageable);

    // string values - begins with, for first letter tree items
    Page<Carte> getByTitluIsNullOrTitluIsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String emptyString, Pageable pageable);

    Page<Carte> getByTitluStartingWithIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String titluStartsWith, Pageable pageable);

    Page<Carte> getBySerieExistsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(boolean exists, Pageable pageable);

    Page<Carte> getBySerie_NumeIsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String serieStartsWith, Pageable pageable);

    Page<Carte> getByPretIsNullOrPret_DataCumparariiIsNullOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(Pageable pageable);

    Page<Carte> getByPret_DataCumparariiIsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(Date dataCumpararii, Pageable pageable);

    Page<Carte> getByCreatedAtIsNullOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(Pageable pageable);

    Page<Carte> getByCreatedAtBetweenOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(Date createdDateStart, Date createdDateEnd, Pageable pageable);

    Page<Carte> getByUpdatedAtIsNullOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(Pageable pageable);

    Page<Carte> getByUpdatedAtBetweenOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(Date updatedDateStart, Date updatedDateEnd, Pageable pageable);

    //string arrays
    Page<Carte> getByTraducatoriIsNullOrTraducatoriIsLessThanEqualOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String[] emptyString, Pageable pageable);

    Page<Carte> getByTraducatoriContainsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String traducator, Pageable pageable);

    Page<Carte> getByTagsIsNullOrTagsIsLessThanEqualOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String[] emptyString, Pageable pageable);

    Page<Carte> getByTagsContainsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String tag, Pageable pageable);

    Page<Carte> getByGenLiterarIsNullOrGenLiterarIsLessThanEqualOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String[] emptyString, Pageable pageable);

    Page<Carte> getByGenLiterarContainsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String genLiterar, Pageable pageable);

    //reference collections - using ref ObjectId!

    Page<Carte> getByIdAutoriIsNullOrIdAutoriIsLessThanEqualOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String[] emptyString, Pageable pageable);

    Page<Carte> getByIdAutoriLikeOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(ObjectId idAutor, Pageable pageable);

    Page<Carte> getByIdInOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(List<ObjectId> bookIds, Pageable pageable);

    //pret

    Page<Carte> getByPretIsNullOrPret_PretIntregOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(double zero, Pageable pageable);

    Page<Carte> getByPretIsNullOrPret_PretOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(double zero, Pageable pageable);

    Page<Carte> getByPret_PretBetweenOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(double min, double max, Pageable pageable);
}
