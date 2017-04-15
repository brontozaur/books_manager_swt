package com.papao.books.repository;

import com.papao.books.model.Autor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CacheableAutorRepository {

    private final AutorRepository autorRepository;

    @Autowired
    public CacheableAutorRepository(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    public AutorRepository getAutorRepository() {
        return autorRepository;
    }

    @Cacheable("autori")
    public Autor getById(String id) {
        return autorRepository.findOne(id);
    }

    @Cacheable("autoriList")
    public Iterable<Autor> getByIds(List<String> ids) {
        return autorRepository.findAll(ids);
    }
}
