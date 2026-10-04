package com.cengirl.beautysalon.domain.repository;

import com.cengirl.beautysalon.domain.model.BeautyService;

import java.util.List;
import java.util.Optional;

public interface BeautyServiceRepository {

    void save(BeautyService service);

    Optional<BeautyService> findById(String id);

    List<BeautyService> findAll();

    void deleteById(String id);
}
