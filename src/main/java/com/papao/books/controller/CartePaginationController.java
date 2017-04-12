package com.papao.books.controller;

import com.papao.books.model.Carte;
import com.papao.books.repository.CarteRepository;
import com.papao.books.view.searcheable.BookSearchType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import java.util.Observable;

@Controller
public class CartePaginationController extends Observable {

    private Page<Carte> carti;
    private final CarteRepository repository;

    @Autowired
    public CartePaginationController(CarteRepository repository) {
        this.repository = repository;
    }

    public void requestSearch(BookSearchType searchType, String value, Pageable pageable) {
        if (searchType == BookSearchType.EDITURA) {
            if (StringUtils.isNotEmpty(value)) {
                carti = repository.getByEdituraContainsOrderByTitluAsc(value, pageable);
            } else {
                carti = repository.getByEdituraIsNullOrEdituraIs("", pageable);
            }
        }
        setChanged();
        notifyObservers();
    }

    public Page<Carte> getSearchResult() {
        return carti;
    }
}
