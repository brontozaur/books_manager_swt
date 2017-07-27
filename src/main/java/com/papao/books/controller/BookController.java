package com.papao.books.controller;

import com.mongodb.gridfs.GridFSDBFile;
import com.papao.books.exception.UnsupportedSearchTypeException;
import com.papao.books.model.Autor;
import com.papao.books.model.Carte;
import com.papao.books.model.DocumentData;
import com.papao.books.repository.CacheableAutorRepository;
import com.papao.books.repository.CarteRepository;
import com.papao.books.ui.providers.tree.NodeType;
import com.papao.books.ui.providers.tree.SimpleTextNode;
import com.papao.books.ui.searcheable.BookSearchType;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

@Controller
public class BookController extends Observable {

    private final CarteRepository repository;
    private final CacheableAutorRepository cacheableAutorRepository;

    private Page<Carte> carti = new PageImpl<>(new ArrayList<Carte>());
    private BookSearchType searchType;
    private SimpleTextNode node;

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
        return repository.findOne(id.toString());
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
                    carti = repository.getByEdituraContainsIgnoreCaseOrderByTitluAsc((String) value, pageable);
                } else {
                    carti = repository.getByEdituraIsNullOrEdituraIsOrderByTitluAsc("", pageable);
                }
                break;
            }
            case Locatie: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByLocatieContainsIgnoreCaseOrderByTitluAsc((String) value, pageable);
                } else {
                    carti = repository.getByLocatieIsNullOrLocatieIsOrderByTitluAsc("", pageable);
                }
                break;
            }
            case Taguri: {
                if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByTagsContainsIgnoreCaseOrderByTitluAsc((String) value, pageable);
                } else {
                    carti = repository.getByTagsIsNullOrTagsIsLessThanEqualOrderByTitluAsc(new String[]{""}, pageable);
                }
                break;
            }
            case Gen_literar: {
                if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByGenLiterarContainsIgnoreCaseOrderByTitluAsc((String) value, pageable);
                } else {
                    carti = repository.getByGenLiterarIsNullOrGenLiterarIsLessThanEqualOrderByTitluAsc(new String[]{""}, pageable);
                }
                break;
            }
            case Autor: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByIdAutoriLikeOrderByTitluAsc(new ObjectId((String) value), pageable);
                } else {
                    carti = repository.getByIdAutoriIsNullOrIdAutoriIsLessThanEqualOrderByTitluAsc(new String[]{""}, pageable);
                }
                break;
            }
            case Traducator: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByTraducatoriContainsIgnoreCaseOrderByTitluAsc((String) value, pageable);
                } else {
                    carti = repository.getByTraducatoriIsNullOrTraducatoriIsLessThanEqualOrderByTitluAsc(new String[]{""}, pageable);
                }
                break;
            }
            case An_aparitie: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByAnAparitieContainsIgnoreCaseOrderByTitluAsc((String) value, pageable);
                } else {
                    carti = repository.getByAnAparitieIsNullOrAnAparitieIsOrderByTitluAsc("", pageable);
                }
                break;
            }
            case Limba: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByLimbaContainsIgnoreCaseOrderByTitluAsc((String) value, pageable);
                } else {
                    carti = repository.getByLimbaIsNullOrLimbaIsOrderByTitluAsc("", pageable);
                }
                break;
            }
            case Limba_originala: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByEditiaOriginala_LimbaContainsIgnoreCaseOrderByTitluAsc((String) value, pageable);
                } else {
                    carti = repository.getByEditiaOriginala_LimbaIsNullOrEditiaOriginala_LimbaIsOrderByTitluAsc("", pageable);
                }
                break;
            }
            case Tip_coperta: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByTipCopertaContainsIgnoreCaseOrderByTitluAsc((String) value, pageable);
                } else {
                    carti = repository.getByTipCopertaIsNullOrTipCopertaIsOrderByTitluAsc("", pageable);
                }
                break;
            }
            case Titlu: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByTitluStartingWithIgnoreCaseOrderByTitluAsc((String) value, pageable);
                } else {
                    carti = repository.getByTitluIsNullOrTitluIsOrderByTitluAsc("", pageable);
                }
                break;
            }
            case Serie: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getBySerieStartingWithIgnoreCaseOrderBySerieAsc((String) value, pageable);
                } else {
                    carti = repository.getBySerieIsNullOrSerieIsOrderBySerieAsc("", pageable);
                }
                break;
            }
            case Creata: {
                if (value != null) {
                    carti = repository.getByCreatedAtBetweenOrderByTitluAsc(node.getMinDate(), node.getMaxDate(), pageable);
                } else {
                    carti = repository.getByCreatedAtIsNullOrderByTitluAsc(pageable);
                }
                break;
            }
            case Actualizata: {
                if (value != null) {
                    carti = repository.getByUpdatedAtBetweenOrderByTitluAsc(node.getMinDate(), node.getMaxDate(), pageable);
                } else {
                    carti = repository.getByUpdatedAtIsNullOrderByTitluAsc(pageable);
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
                carti = repository.getByIdInOrderByTitluAsc(bookIds, pageable);
                break;
            }
            case Nota_traducere: {
                List<ObjectId> bookIds;
                if (value != null) {
                    bookIds = UserController.getBookIdsWithSpecifiedTranslationRatingForCurrentUser((Integer) value);
                } else {
                    bookIds = UserController.getBookIdsWithSpecifiedTranslationRatingForCurrentUser(-1);
                }
                carti = repository.getByIdInOrderByTitluAsc(bookIds, pageable);
                break;
            }
            case Cititori: {
                List<ObjectId> bookIds = UserController.getReadedBookIdsForUser((ObjectId) value);
                carti = repository.getByIdInOrderByTitluAsc(bookIds, pageable);
                break;
            }
            case Utilizatori: {
                List<ObjectId> bookIds = UserController.getBookIdsForUser((ObjectId) value);
                carti = repository.getByIdInOrderByTitluAsc(bookIds, pageable);
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

    public void getByIdIsOrTitluLikeOrIdAutoriContains(String searchTerm, List<ObjectId> idAutori, Pageable pageable) {
        carti = repository.getByIdIsOrTitluLikeIgnoreCaseOrIdAutoriContains(searchTerm, searchTerm, idAutori, pageable);
        setChanged();
        notifyObservers();
    }

    public List<Carte> getByIdAutoriContains(ObjectId idAutor) {
        return repository.getByIdAutoriContains(idAutor);
    }

    public Page<Carte> getBooksWithNoImage(Pageable pageable) {
        return repository.getByCopertaFataIsNull(pageable);
    }

    public void getByIdAutoriIn(List<ObjectId> idAutori, Pageable pageable) {
        carti = repository.getByIdAutoriIn(idAutori, pageable);
        setChanged();
        notifyObservers();
    }
}
