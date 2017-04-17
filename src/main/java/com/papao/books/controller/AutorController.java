package com.papao.books.controller;

import com.papao.books.model.Autor;
import com.papao.books.repository.CacheableAutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class AutorController extends AbstractController {

    private final CacheableAutorRepository cacheableAutorRepository;

    @Autowired
    public AutorController(CacheableAutorRepository cacheableAutorRepository,
                           MongoTemplate mongoTemplate) {
        super(mongoTemplate);
        this.cacheableAutorRepository = cacheableAutorRepository;
    }

    public Autor save(Autor autor) {
        return cacheableAutorRepository.getAutorRepository().save(autor);
    }

    public Autor findOne(String id) {
        return cacheableAutorRepository.getAutorRepository().findOne(id);
    }

    public void delete(Autor autor) {
        this.cacheableAutorRepository.getAutorRepository().delete(autor);
    }

    public List<Autor> findAll() {
        return cacheableAutorRepository.getAutorRepository().findAll();
    }
}
