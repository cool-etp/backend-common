package org.cooletp.common.util;

import org.springframework.data.domain.Sort;

public class ParamsBuilder {
    public static Sort constructSort(String sortBy, String sortOrder) {
        Sort sortInfo = null;
        if (sortBy != null) {
            sortInfo = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        }
        return sortInfo;
    }
}
