package com.papao.books.controller;

import com.mongodb.gridfs.GridFSDBFile;
import com.papao.books.exception.UnsupportedSearchTypeException;
import com.papao.books.model.Autor;
import com.papao.books.model.Carte;
import com.papao.books.model.DocumentData;
import com.papao.books.model.TipCoperta;
import com.papao.books.repository.CacheableAutorRepository;
import com.papao.books.repository.CarteRepository;
import com.papao.books.ui.auth.EncodeLive;
import com.papao.books.ui.providers.tree.NodeType;
import com.papao.books.ui.providers.tree.SimpleTextNode;
import com.papao.books.ui.searcheable.BookSearchType;
import com.papao.books.ui.searcheable.CategoriePret;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Optional;

@Controller
public class BookController extends Observable {

    private final CarteRepository repository;
    private final CacheableAutorRepository cacheableAutorRepository;

    private Page<Carte> carti = new PageImpl<>(new ArrayList<Carte>());
    private BookSearchType searchType;
    private SimpleTextNode node;
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    public BookController(CarteRepository repository,
                          CacheableAutorRepository cacheableAutorRepository) {
        this.repository = repository;
        this.cacheableAutorRepository = cacheableAutorRepository;
    }

    public Iterable<Autor> getBookAuthorsOrderByNumeComplet(Carte carte) {
        return cacheableAutorRepository.getByIdsOrderByNumeComplet(carte.getIdAutori());
    }

    public String getBookAuthorNamesOrderByNumeComplet(Carte carte) {
        Iterable<Autor> autori = getBookAuthorsOrderByNumeComplet(carte);
        StringBuilder autoriNames = new StringBuilder();
        for (Autor autor : autori) {
            if (autoriNames.length() > 0) {
                autoriNames.append(", ");
            }
            autoriNames.append(autor.getNumeSiTitlu());
        }
        return autoriNames.toString();
    }

    public Carte save(Carte carte) {
        return repository.save(carte);
    }

    public void requestSearch(Pageable pageable) {
        this.requestSearch(this.searchType, this.node, pageable);
    }

    public Carte findOne(ObjectId id) {
        if (id == null) {
            return null;
        }
        Optional<Carte> optional = repository.findById(id.toString());
        return optional.orElse(null);
    }

    public void delete(Carte carte) {
        this.repository.delete(carte);
    }

    public CarteRepository getRepository() {
        return this.repository;
    }

    public void requestSearch(BookSearchType searchType, SimpleTextNode node, Pageable pageable) {
        this.searchType = searchType;
        this.node = node;
        //TODO need to think about consistent handling for node type ALL
        Object value = node.getQueryValue();
        switch (searchType) {
            case Editura: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByEdituraContainsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc((String) value, pageable);
                } else {
                    carti = repository.getByEdituraIsNullOrEdituraIsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc("", pageable);
                }
                break;
            }
            case Pret: {
                if (node.getQueryValue() == null) {
                    carti = repository.getByPretIsNullOrPret_PretOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(0, pageable);
                } else {
                    CategoriePret categoriePret = (CategoriePret) node.getQueryValue();
                    carti = repository.getByPret_PretBetweenOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(categoriePret.getMin(), categoriePret.getMax(), pageable);
                }
                break;
            }
            case Lipsa_informatii: {
                switch (node.getNodeType()) {
                    case FARA_IMAGINE:
                        carti = repository.getByCopertaFataIsNullOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(pageable);
                        break;
                    case FARA_PRET:
                        carti = repository.getByPretIsNullOrPret_PretIntregOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(0, pageable);
                        break;
                    case FARA_DATA_CUMPARARII:
                        carti = repository.getByPretIsNullOrPret_DataCumparariiIsNullOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(pageable);
                        break;
                    case FARA_EDITURA:
                        carti = repository.getByEdituraIsNullOrEdituraIsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc("", pageable);
                        break;
                    case FARA_TRADUCATOR:
                        carti = repository.getByTraducatoriIsNullOrTraducatoriIsLessThanEqualOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(new String[]{}, pageable);
                        break;
                    case FARA_TIP_COPERTA:
                        carti = repository.getByTipCopertaIsNullOrTipCopertaIsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(TipCoperta.Nespecificat.name(), pageable);
                        break;
                    case FARA_LOCATIE:
                        carti = repository.getByLocatieIsNullOrLocatieIsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc("", pageable);
                        break;
                    case NECITITA: {
                        List<ObjectId> idsCartiCitite = UserController.getReadedBookIdsForUser(EncodeLive.getIdUser());
                        carti = repository.getByIdNotInOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(idsCartiCitite, pageable);
                        break;
                    }
                    case CITITA: {
                        List<ObjectId> idsCartiCitite = UserController.getReadedBookIdsForUser(EncodeLive.getIdUser());
                        carti = repository.getByIdInOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(idsCartiCitite, pageable);
                        break;
                    }
                    case FARA_GEN_LITERAR:
                        carti = repository.getByGenLiterarIsNullOrGenLiterarIsLessThanEqualOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(new String[]{}, pageable);
                        break;
                    case FARA_TAGURI:
                        carti = repository.getByTagsIsNullOrTagsIsLessThanEqualOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(new String[]{}, pageable);
                        break;
                    case FARA_ISBN:
                        carti = repository.getByIsbnIsNullOrIsbnIsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc("", pageable);
                        break;
                    case FARA_AN_APARITIE:
                        carti = repository.getByAnAparitieIsNullOrAnAparitieIsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc("", pageable);
                        break;
                    case CU_REVIEW: {
                        List<ObjectId> idsCartiCuReview = UserController.getBookIdsWithReview(EncodeLive.getIdUser());
                        carti = repository.getByIdInOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(idsCartiCuReview, pageable);
                        break;
                    }
                    case FARA_REVIEW: {
                        List<ObjectId> idsCartiCuReview = UserController.getBookIdsWithReview(EncodeLive.getIdUser());
                        carti = repository.getByIdNotInOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(idsCartiCuReview, pageable);
                        break;
                    }
                    default:
                        carti = repository.findAll(pageable);
                        logger.error("Node type unsupported " + node.getNodeType());
                }
                break;
            }
            case Colectie: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByColectieContainsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc((String) value, pageable);
                } else {
                    carti = repository.getByColectieIsNullOrEdituraIsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc("", pageable);
                }
                break;
            }
            case Locatie: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByLocatieContainsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc((String) value, pageable);
                } else {
                    carti = repository.getByLocatieIsNullOrLocatieIsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc("", pageable);
                }
                break;
            }
            case Taguri: {
                if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByTagsContainsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc((String) value, pageable);
                } else {
                    carti = repository.getByTagsIsNullOrTagsIsLessThanEqualOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(new String[]{""}, pageable);
                }
                break;
            }
            case Gen_literar: {
                if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByGenLiterarContainsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc((String) value, pageable);
                } else {
                    carti = repository.getByGenLiterarIsNullOrGenLiterarIsLessThanEqualOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(new String[]{""}, pageable);
                }
                break;
            }
            case Autor: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByIdAutoriLikeOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(new ObjectId((String) value), pageable);
                } else {
                    carti = repository.getByIdAutoriIsNullOrIdAutoriIsLessThanEqualOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(new String[]{""}, pageable);
                }
                break;
            }
            case Traducator: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByTraducatoriContainsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc((String) value, pageable);
                } else {
                    carti = repository.getByTraducatoriIsNullOrTraducatoriIsLessThanEqualOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(new String[]{""}, pageable);
                }
                break;
            }
            case An_aparitie: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByAnAparitieContainsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc((String) value, pageable);
                } else {
                    carti = repository.getByAnAparitieIsNullOrAnAparitieIsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc("", pageable);
                }
                break;
            }
            case Limba: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByLimbaContainsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc((String) value, pageable);
                } else {
                    carti = repository.getByLimbaIsNullOrLimbaIsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc("", pageable);
                }
                break;
            }
            case Limba_originala: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByEditiaOriginala_LimbaContainsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc((String) value, pageable);
                } else {
                    carti = repository.getByEditiaOriginala_LimbaIsNullOrEditiaOriginala_LimbaIsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc("", pageable);
                }
                break;
            }
            case Tip_coperta: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByTipCopertaContainsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc((String) value, pageable);
                } else {
                    carti = repository.getByTipCopertaIsNullOrTipCopertaIsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(TipCoperta.Nespecificat.name(), pageable);
                }
                break;
            }
            case Titlu: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByTitluStartingWithIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc((String) value, pageable);
                } else {
                    carti = repository.getByTitluIsNullOrTitluIsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc("", pageable);
                }
                break;
            }
            case Serie: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.getBySerieExistsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(true, pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getBySerie_NumeIsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc((String) value, pageable);
                } else {
                    throw new IllegalArgumentException("Nume serie invalid!");
                }
                break;
            }
            case Creata: {
                if (value != null) {
                    carti = repository.getByCreatedAtBetweenOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(node.getMinDate(), node.getMaxDate(), pageable);
                } else {
                    carti = repository.getByCreatedAtIsNullOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(pageable);
                }
                break;
            }
            case Actualizata: {
                if (value != null) {
                    carti = repository.getByUpdatedAtBetweenOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(node.getMinDate(), node.getMaxDate(), pageable);
                } else {
                    carti = repository.getByUpdatedAtIsNullOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(pageable);
                }
                break;
            }
            case Data_cumpararii: {
                if (value != null) {
                    carti = repository.getByPret_DataCumparariiIsBetweenOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(node.getMinDate(), node.getMaxDate(), pageable);
                } else {
                    carti = repository.getByPretIsNullOrPret_DataCumparariiIsNullOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(pageable);
                }
                break;
            }
            case Nota_carte: {
                List<ObjectId> bookIds;
                if (value != null) {
                    bookIds = UserController.getBookIdsWithSpecifiedRatingForCurrentUser((Integer) value);
                } else {
                    bookIds = UserController.getBookIdsWithSpecifiedRatingForCurrentUser(-1);
                }
                carti = repository.getByIdInOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(bookIds, pageable);
                break;
            }
            case Nota_traducere: {
                List<ObjectId> bookIds;
                if (value != null) {
                    bookIds = UserController.getBookIdsWithSpecifiedTranslationRatingForCurrentUser((Integer) value);
                } else {
                    bookIds = UserController.getBookIdsWithSpecifiedTranslationRatingForCurrentUser(-1);
                }
                carti = repository.getByIdInOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(bookIds, pageable);
                break;
            }
            case Cititori: {
                List<ObjectId> bookIds = UserController.getReadedBookIdsForUser((ObjectId) value);
                carti = repository.getByIdInOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(bookIds, pageable);
                break;
            }
            case Utilizatori: {
                List<ObjectId> bookIds = UserController.getBookIdsForUser((ObjectId) value);
                carti = repository.getByIdInOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(bookIds, pageable);
                break;
            }
            default:
                throw new UnsupportedSearchTypeException("The search type " + searchType + " is not (yet) implemented!");
        }
        setChanged();
        notifyObservers();
    }

    public Page<Carte> getSearchResult() {
        return carti;
    }

    public Image getImage(DocumentData data) {
        GridFSDBFile frontCover = ApplicationController.getDocumentData(data.getId());
        Image image = null;
        if (frontCover != null) {
            image = new Image(Display.getDefault(), frontCover.getInputStream());
            data.setFileName(frontCover.getFilename());
        }
        return image;
    }

    public Carte getByTitluAndIdAutori(String titlu, List<ObjectId> idAutori) {
        return repository.getByTitluAndIdAutori(titlu, idAutori);
    }

    public void getByIdIsOrTitluLikeIgnoreCaseOrColectieLikeIgnoreCaseOrSubtitluLikeIgnoreCaseOrIdAutoriContainsOrSerie_NumeLikeIgnoreCaseOrVolumLikeIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String searchTerm, List<ObjectId> idAutori, Pageable pageable) {
        carti = repository.getByIdIsOrTitluLikeIgnoreCaseOrColectieLikeIgnoreCaseOrSubtitluLikeIgnoreCaseOrIdAutoriContainsOrSerie_NumeLikeIgnoreCaseOrVolumLikeIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(searchTerm, searchTerm, searchTerm, searchTerm, idAutori, searchTerm, searchTerm, pageable);
        setChanged();
        notifyObservers();
    }

    public List<Carte> getByIdAutoriContains(ObjectId idAutor) {
        return repository.getByIdAutoriContainsOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(idAutor);
    }

    public Page<Carte> getBooksWithNoImage(Pageable pageable) {
        return repository.getByCopertaFataIsNullOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(pageable);
    }

    public void getByIdAutoriInOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(List<ObjectId> idAutori, Pageable pageable) {
        carti = repository.getByIdAutoriInOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(idAutori, pageable);
        setChanged();
        notifyObservers();
    }

    public void getByColectieContainsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String colectie, Pageable pageable) {
        carti = repository.getByColectieContainsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(colectie, pageable);
        setChanged();
        notifyObservers();
    }

    public void getByColectieIsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(String colectie, Pageable pageable) {
        carti = repository.getByColectieIsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(colectie, pageable);
        setChanged();
        notifyObservers();
    }
}
