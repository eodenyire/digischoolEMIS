package com.digischool.emis.dao;

import java.util.List;
import java.util.Optional;

/**
 * Generic Data Access Object interface for CRUD operations.
 *
 * @param <T>  Entity type
 * @param <ID> Primary key type
 */
public interface GenericDao<T, ID> {

    /**
     * Saves a new entity to the database.
     *
     * @param entity Entity to save
     * @return The saved entity with generated ID
     */
    T save(T entity);

    /**
     * Updates an existing entity.
     *
     * @param entity Entity to update
     * @return Updated entity
     */
    T update(T entity);

    /**
     * Finds an entity by its primary key.
     *
     * @param id Primary key
     * @return Optional containing the entity if found
     */
    Optional<T> findById(ID id);

    /**
     * Returns all entities.
     *
     * @return List of all entities
     */
    List<T> findAll();

    /**
     * Deletes an entity by primary key.
     *
     * @param id Primary key of entity to delete
     */
    void deleteById(ID id);

    /**
     * Checks if an entity exists by primary key.
     *
     * @param id Primary key
     * @return true if entity exists
     */
    boolean existsById(ID id);

    /**
     * Returns the total count of entities.
     *
     * @return Count of entities
     */
    long count();
}
