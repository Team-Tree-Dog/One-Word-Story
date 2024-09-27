package net.onewordstory.spring.controllers.enums;

public enum StoriesRequestOptions {

    LIKED, LATEST;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}
