package net.onewordstory.core.usecases.comment_as_guest;

/**
 * Input boundary for Comment As Guest use case
 */
public interface CagInputBoundary {

    /**
     * Adds a guest comment to a story
     * @param data contains the comment, chosen display name, id of the story the comment was
     *             made on, and the id of this request
     * @param pres output boundary for this use case
     */
    void commentAsGuest(CagInputData data, CagOutputBoundary pres);
}
