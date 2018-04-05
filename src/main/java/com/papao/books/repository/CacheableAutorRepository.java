package com.papao.books.repository;

import com.papao.books.model.Autor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Optional<Autor> optional = autorRepository.findById(id);
        return optional.orElse(null);
    }

    @Cacheable("autoriList")
    public Iterable<Autor> getByIdsOrderByNumeComplet(List<ObjectId> ids) {
        return autorRepository.getByIdInOrderByNumeComplet(ids);
    }

    @Cacheable("byNumeComplet")
    public Autor getByNumeComplet(String numeComplet) {
        return autorRepository.getByNumeComplet(numeComplet);
    }

    public List<ObjectId> getByNumeCompletLikeIgnoreCaseOrTitluLikeIgnoreCase(String numeComplet) {
        List<Autor> autori = autorRepository.getByNumeCompletLikeIgnoreCaseOrTitluLikeIgnoreCase(numeComplet, numeComplet);
        List<ObjectId> idAutori = new ArrayList<>();
        for (Autor autor : autori) {
            idAutori.add(autor.getId());
        }
        return idAutori;
    }
}
