package org.cooletp.common.service;

import org.cooletp.common.entity.IEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IPageableSorted<E extends IEntity> extends IPageable<E>, ISorted<E> {
    List<E> findAllPaginatedAndSorted(final int page, final int size, final String sortBy, final String sortOrder);
    Page<E> findAllPaginatedAndSortedRow(final int page, final int size, final String sortBy, final String sortOrder);
}
