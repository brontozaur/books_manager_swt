package com.papao.books.controller;

import com.papao.books.exception.UnsupportedSearchTypeException;
import com.papao.books.model.Autor;
import com.papao.books.model.Carte;
import com.papao.books.repository.CacheableAutorRepository;
import com.papao.books.repository.CarteRepository;
import com.papao.books.view.searcheable.BookSearchType;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class BookController extends AbstractController {

    private final CarteRepository repository;
    private final CacheableAutorRepository cacheableAutorRepository;

    private Page<Carte> carti;
    private BookSearchType searchType;
    private String value;
    private boolean all;

    @Autowired
    public BookController(CarteRepository repository,
                          CacheableAutorRepository cacheableAutorRepository,
                          MongoTemplate mongoTemplate) {
        super(mongoTemplate);
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
            autoriNames.append(autor.getNumeComplet());
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
                        carti = repository.getByEdituraContainsIgnoreCase(value, pageable);
                    } else {
                        carti = repository.getByEdituraIsNullOrEdituraIs("", pageable);
                    }
                    break;
                }
                case AUTOR: {
                    if (StringUtils.isNotEmpty(value)) {
                        carti = repository.getByIdAutoriLike(new ObjectId(value), pageable);
                    } else {
                        carti = repository.getByIdAutoriIsNullOrIdAutoriIsLessThanEqual(new String[]{""}, pageable);
                    }
                    break;
                }
                case TRADUCATOR: {
                    if (StringUtils.isNotEmpty(value)) {
                        carti = repository.getByTraducatoriContainsIgnoreCase(value, pageable);
                    } else {
                        carti = repository.getByTraducatoriIsNullOrTraducatoriIsLessThanEqual(new String[]{""}, pageable);
                    }
                    break;
                }
                case AN_APARITIE: {
                    if (StringUtils.isNotEmpty(value)) {
                        carti = repository.getByAnAparitieContainsIgnoreCase(value, pageable);
                    } else {
                        carti = repository.getByAnAparitieIsNullOrAnAparitieIs("", pageable);
                    }
                    break;
                }
                case LIMBA: {
                    if (StringUtils.isNotEmpty(value)) {
                        carti = repository.getByLimbaContainsIgnoreCase(value, pageable);
                    } else {
                        carti = repository.getByLimbaIsNullOrLimbaIs("", pageable);
                    }
                    break;
                }
                case LIMBA_ORIGINALA: {
                    if (StringUtils.isNotEmpty(value)) {
                        carti = repository.getByEditiaOriginala_LimbaContainsIgnoreCase(value, pageable);
                    } else {
                        carti = repository.getByEditiaOriginala_LimbaIsNullOrEditiaOriginala_LimbaIs("", pageable);
                    }
                    break;
                }
                case TIP_COPERTA: {
                    if (StringUtils.isNotEmpty(value)) {
                        carti = repository.getByTipCopertaContainsIgnoreCase(value, pageable);
                    } else {
                        carti = repository.getByTipCopertaIsNullOrTipCopertaIs("", pageable);
                    }
                    break;
                }
                case TITLU: {
                    if (StringUtils.isNotEmpty(value)) {
                        carti = repository.getByTitluStartingWithIgnoreCase(value, pageable);
                    } else {
                        carti = repository.getByTitluIsNullOrTitluIs("", pageable);
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
}
