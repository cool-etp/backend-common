package org.cooletp.common.service;

import org.cooletp.common.entity.IEntity;

import java.util.List;
import java.util.Optional;

public interface ICrud<E extends IEntity> {
    Optional<E> findOneById(final Long id);
    List<E> findAll();
    void create(final E entity);
    Optional<E> update(final E entity);
    void delete(final Long id);
    long count();
}
