package usecases.comment_as_guest;

import org.jetbrains.annotations.NotNull;
import usecases.Response;

/**
 * Output data for Comment As Guest use case
 */
public class CagOutputData {

    private final Response res;

    /**
     * Constructor for CagOutputData
     * @param res the response, describing what the response was when commentAsGuest was called
     */
    public CagOutputData(@NotNull Response res) {

        this.res = res;
    }

    @NotNull
    public Response getRes() { return res; }
}
