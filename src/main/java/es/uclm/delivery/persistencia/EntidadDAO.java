package es.uclm.delivery.persistencia;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.*;

@Transactional
public abstract class EntidadDAO<E> {

    @PersistenceContext
    protected EntityManager entityManager;

    private Class<E> entityClass;

    public EntidadDAO(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    public int insert(E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        try {
            entityManager.persist(entity);
            return 1; // Success
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // Rethrow the exception to be handled by the caller
        }
    }
    

    public int update(E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        try {
            entityManager.merge(entity);
            return 1; // Success
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Failure
        }
    }

    public int delete(E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        try {
            entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
            return 1; // Success
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Failure
        }
    }

    public Optional<E> select(String id) {
        try {
            return Optional.ofNullable(entityManager.find(entityClass, id));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty(); // Failure
        }
    }

    public Optional<E> findById(Long id) {
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    public List<E> findAll() {
        return entityManager.createQuery("from " + entityClass.getName(), entityClass).getResultList();
    }

    public List<E> findAllById(List<Long> ids) {
        return entityManager.createQuery("from " + entityClass.getName() + " where id in :ids", entityClass)
                .setParameter("ids", ids)
                .getResultList();
    }
    
}