package org.cooletp.common.service;

import org.cooletp.common.entity.IEntity;

import java.util.List;

public interface ISorted<E extends IEntity> {
    List<E> findAllSorted(final String sortBy, final String sortOrder);
}
