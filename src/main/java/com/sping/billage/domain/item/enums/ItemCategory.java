package com.sping.billage.domain.item.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemCategory {

    TOOL("공구"),
    SUIT("정장"),
    CAMPING("캠핑용품"),
    LIVING("생활용품"),
    ETC("기타");

    private final String displayName;
}
