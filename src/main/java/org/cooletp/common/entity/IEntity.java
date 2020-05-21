package org.cooletp.common.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public interface IEntity extends Serializable {
    Long getId();
    void setId(Long id);

    LocalDateTime getDateCreated();
    void setDateCreated(LocalDateTime dateCreated);

    LocalDateTime getDateModified();
    void setDateModified(LocalDateTime dateModified);
}
