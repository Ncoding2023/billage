package com.travel.billage.domain.category;

public enum Category {
    TOOL("공구"),
    CLOTHES("의류"),
    CAMPING("캠핑용품"),
    LIVING("생활용품");
    
    private final String description;

    Category(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
