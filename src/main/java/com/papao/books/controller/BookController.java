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

    public Iterable<Autor> getBookAuthors(Carte carte) {
        return cacheableAutorRepository.getByIds(carte.getIdAutori());
    }

    public String getBookAuthorNames(Carte carte) {
        Iterable<Autor> autori = getBookAuthors(carte);
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
            case EDITURA: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByEdituraContainsIgnoreCaseOrderByTitluAsc((String) value, pageable);
                } else {
                    carti = repository.getByEdituraIsNullOrEdituraIsOrderByTitluAsc("", pageable);
                }
                break;
            }
            case LOCATIE: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByLocatieContainsIgnoreCaseOrderByTitluAsc((String) value, pageable);
                } else {
                    carti = repository.getByLocatieIsNullOrLocatieIsOrderByTitluAsc("", pageable);
                }
                break;
            }
            case AUTOR: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByIdAutoriLikeOrderByTitluAsc(new ObjectId((String) value), pageable);
                } else {
                    carti = repository.getByIdAutoriIsNullOrIdAutoriIsLessThanEqualOrderByTitluAsc(new String[]{""}, pageable);
                }
                break;
            }
            case TRADUCATOR: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByTraducatoriContainsIgnoreCaseOrderByTitluAsc((String) value, pageable);
                } else {
                    carti = repository.getByTraducatoriIsNullOrTraducatoriIsLessThanEqualOrderByTitluAsc(new String[]{""}, pageable);
                }
                break;
            }
            case AN_APARITIE: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByAnAparitieContainsIgnoreCaseOrderByTitluAsc((String) value, pageable);
                } else {
                    carti = repository.getByAnAparitieIsNullOrAnAparitieIsOrderByTitluAsc("", pageable);
                }
                break;
            }
            case LIMBA: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByLimbaContainsIgnoreCaseOrderByTitluAsc((String) value, pageable);
                } else {
                    carti = repository.getByLimbaIsNullOrLimbaIsOrderByTitluAsc("", pageable);
                }
                break;
            }
            case LIMBA_ORIGINALA: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByEditiaOriginala_LimbaContainsIgnoreCaseOrderByTitluAsc((String) value, pageable);
                } else {
                    carti = repository.getByEditiaOriginala_LimbaIsNullOrEditiaOriginala_LimbaIsOrderByTitluAsc("", pageable);
                }
                break;
            }
            case TIP_COPERTA: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByTipCopertaContainsIgnoreCaseOrderByTitluAsc((String) value, pageable);
                } else {
                    carti = repository.getByTipCopertaIsNullOrTipCopertaIsOrderByTitluAsc("", pageable);
                }
                break;
            }
            case TITLU: {
                if (node.getNodeType() == NodeType.ALL) {
                    carti = repository.findAll(pageable);
                } else if (StringUtils.isNotEmpty((String) value)) {
                    carti = repository.getByTitluStartingWithIgnoreCaseOrderByTitluAsc((String) value, pageable);
                } else {
                    carti = repository.getByTitluIsNullOrTitluIsOrderByTitluAsc("", pageable);
                }
                break;
            }
            case CREATA: {
                if (value != null) {
                    carti = repository.getByCreatedAtBetweenOrderByTitluAsc(node.getMinDate(), node.getMaxDate(), pageable);
                } else {
                    carti = repository.getByCreatedAtIsNullOrderByTitluAsc(pageable);
                }
                break;
            }
            case ACTUALIZATA: {
                if (value != null) {
                    carti = repository.getByUpdatedAtBetweenOrderByTitluAsc(node.getMinDate(), node.getMaxDate(), pageable);
                } else {
                    carti = repository.getByUpdatedAtIsNullOrderByTitluAsc(pageable);
                }
                break;
            }
            case BOOK_RATING: {
                List<ObjectId> bookIds;
                if (value != null) {
                    bookIds = UserController.getBookIdsWithSpecifiedRatingForCurrentUser((Integer) value);
                } else {
                    bookIds = UserController.getBookIdsWithSpecifiedRatingForCurrentUser(-1);
                }
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
}
