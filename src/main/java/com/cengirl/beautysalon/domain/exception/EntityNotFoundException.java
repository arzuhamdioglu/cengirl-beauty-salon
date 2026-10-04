package com.cengirl.beautysalon.domain.exception;

public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String entityName, String id) {
        super(entityName + " not found: " + id);
    }
}
