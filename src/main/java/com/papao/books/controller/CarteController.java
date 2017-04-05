package com.papao.books.controller;

import com.papao.books.model.Carte;
import com.papao.books.repository.CarteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class CarteController {

    @Autowired
    private CarteRepository carteRepository;

    public Carte createNewCarte(){
        return carteRepository.save(new Carte());
    }
}
