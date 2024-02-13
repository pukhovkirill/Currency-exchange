package org.cnyex.repository;

import java.util.List;
import java.util.Optional;

public interface CrudLayer<T> {
    Optional<T> findById(Long id);
    List<T> findAll();
    boolean save(T entity);
    boolean update(T entity);
    boolean delete(Long id);
}
