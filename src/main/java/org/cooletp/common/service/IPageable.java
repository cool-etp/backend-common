package org.cooletp.common.service;

import org.cooletp.common.entity.IEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IPageable<E extends IEntity> {
    List<E> findAllPaginated(final int page, final int size);
    Page<E> findAllPaginatedRow(int page, int size);
}
