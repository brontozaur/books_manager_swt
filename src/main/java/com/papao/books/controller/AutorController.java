package com.papao.books.controller;

import com.papao.books.model.Autor;
import com.papao.books.repository.CacheableAutorRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class AutorController {

    private static CacheableAutorRepository cacheableAutorRepository;

    @Autowired
    public AutorController(CacheableAutorRepository cacheableAutorRepository) {
        AutorController.cacheableAutorRepository = cacheableAutorRepository;
    }

    public static Autor save(Autor autor) {
        return cacheableAutorRepository.getAutorRepository().save(autor);
    }

    public static Autor findOne(ObjectId id) {
        if (id == null) {
            return null;
        }
        Optional<Autor> optionalAutor = cacheableAutorRepository.getAutorRepository().findById(id.toString());
        return optionalAutor.orElse(null);
    }

    public static void delete(Autor autor) {
        AutorController.cacheableAutorRepository.getAutorRepository().delete(autor);
    }

    public static List<Autor> findAll() {
        return cacheableAutorRepository.getAutorRepository().findAll();
    }

    public static List<Autor> findByIdsOrderByNumeComplet(List<ObjectId> ids) {
        Iterable<Autor> iterable = cacheableAutorRepository.getByIdsOrderByNumeComplet(ids);
        List<Autor> autori = new ArrayList<>();
        for (Autor autor : iterable) {
            autori.add(autor);
        }
        return autori;
    }


    public static Autor getByNumeComplet(String numeComplet) {
        return cacheableAutorRepository.getByNumeComplet(numeComplet);
    }

    public static List<ObjectId> getByNumeCompletLikeIgnoreCaseOrTitluLikeIgnoreCase(String numeComplet) {
        return cacheableAutorRepository.getByNumeCompletLikeIgnoreCaseOrTitluLikeIgnoreCase(numeComplet);
    }
}
