package com.papao.books.controller;

import com.mongodb.gridfs.GridFSDBFile;
import com.papao.books.exception.UnsupportedSearchTypeException;
import com.papao.books.model.Autor;
import com.papao.books.model.Carte;
import com.papao.books.model.DocumentData;
import com.papao.books.repository.CacheableAutorRepository;
import com.papao.books.repository.CarteRepository;
import com.papao.books.view.searcheable.BookSearchType;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Observable;

@Controller
public class BookController extends Observable {

    private final CarteRepository repository;
    private final CacheableAutorRepository cacheableAutorRepository;

    private Page<Carte> carti;
    private BookSearchType searchType;
    private String value;
    private boolean all;

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
        this.requestSearch(this.searchType, this.value, pageable, this.all);
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

    public void requestSearch(BookSearchType searchType, String value, Pageable pageable, boolean all) {
        this.searchType = searchType;
        this.value = value;
        this.all = all;
        if (all) {
            carti = repository.findAll(pageable);
        } else {
            switch (searchType) {
                case EDITURA: {
                    if (StringUtils.isNotEmpty(value)) {
                        carti = repository.getByEdituraContainsIgnoreCaseOrderByTitluAsc(value, pageable);
                    } else {
                        carti = repository.getByEdituraIsNullOrEdituraIsOrderByTitluAsc("", pageable);
                    }
                    break;
                }
                case AUTOR: {
                    if (StringUtils.isNotEmpty(value)) {
                        carti = repository.getByIdAutoriLikeOrderByTitluAsc(new ObjectId(value), pageable);
                    } else {
                        carti = repository.getByIdAutoriIsNullOrIdAutoriIsLessThanEqualOrderByTitluAsc(new String[]{""}, pageable);
                    }
                    break;
                }
                case TRADUCATOR: {
                    if (StringUtils.isNotEmpty(value)) {
                        carti = repository.getByTraducatoriContainsIgnoreCaseOrderByTitluAsc(value, pageable);
                    } else {
                        carti = repository.getByTraducatoriIsNullOrTraducatoriIsLessThanEqualOrderByTitluAsc(new String[]{""}, pageable);
                    }
                    break;
                }
                case AN_APARITIE: {
                    if (StringUtils.isNotEmpty(value)) {
                        carti = repository.getByAnAparitieContainsIgnoreCaseOrderByTitluAsc(value, pageable);
                    } else {
                        carti = repository.getByAnAparitieIsNullOrAnAparitieIsOrderByTitluAsc("", pageable);
                    }
                    break;
                }
                case LIMBA: {
                    if (StringUtils.isNotEmpty(value)) {
                        carti = repository.getByLimbaContainsIgnoreCaseOrderByTitluAsc(value, pageable);
                    } else {
                        carti = repository.getByLimbaIsNullOrLimbaIsOrderByTitluAsc("", pageable);
                    }
                    break;
                }
                case LIMBA_ORIGINALA: {
                    if (StringUtils.isNotEmpty(value)) {
                        carti = repository.getByEditiaOriginala_LimbaContainsIgnoreCaseOrderByTitluAsc(value, pageable);
                    } else {
                        carti = repository.getByEditiaOriginala_LimbaIsNullOrEditiaOriginala_LimbaIsOrderByTitluAsc("", pageable);
                    }
                    break;
                }
                case TIP_COPERTA: {
                    if (StringUtils.isNotEmpty(value)) {
                        carti = repository.getByTipCopertaContainsIgnoreCaseOrderByTitluAsc(value, pageable);
                    } else {
                        carti = repository.getByTipCopertaIsNullOrTipCopertaIsOrderByTitluAsc("", pageable);
                    }
                    break;
                }
                case TITLU: {
                    if (StringUtils.isNotEmpty(value)) {
                        carti = repository.getByTitluStartingWithIgnoreCaseOrderByTitluAsc(value, pageable);
                    } else {
                        carti = repository.getByTitluIsNullOrTitluIsOrderByTitluAsc("", pageable);
                    }
                    break;
                }
                default:
                    throw new UnsupportedSearchTypeException("The search type " + searchType + " is not (yet) implemented!");
            }
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

    public void getByTitluLikeOrIdAutoriContains(String titlu, List<ObjectId> idAutori, Pageable pageable) {
        carti = repository.getByTitluLikeIgnoreCaseOrIdAutoriContains(titlu, idAutori, pageable);
        setChanged();
        notifyObservers();
    }
}
