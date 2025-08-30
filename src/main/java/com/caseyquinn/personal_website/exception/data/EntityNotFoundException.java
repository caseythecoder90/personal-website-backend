package com.caseyquinn.personal_website.exception.data;

public class EntityNotFoundException extends DataAccessException {
    public EntityNotFoundException(String entityType, Object id) {
        super("ENTITY_NOT_FOUND", 
              String.format("%s not found with id: %s", entityType, id), 
              null, entityType, id);
    }
}