package com.cengirl.beautysalon.infrastructure.repository;

import com.cengirl.beautysalon.domain.model.BeautyService;
import com.cengirl.beautysalon.domain.repository.BeautyServiceRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryBeautyServiceRepository implements BeautyServiceRepository {

    private final Map<String, BeautyService> store = new HashMap<>();

    @Override
    public void save(BeautyService service) {
        store.put(service.getId(), service);
    }

    @Override
    public Optional<BeautyService> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<BeautyService> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(String id) {
        store.remove(id);
    }
}
