package usecases;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Output from the repo with rows of data
 * @param <T> The type of each individual row of the repo content being returned
 */
public class RepoRes<T> {
    private final List<T> rows;
    private Response res;

    /**
     * This constructor initializes an empty arraylist for rows of data
     * and sets the response to be successful. Use the response setter
     * to later change this.
     */
    public RepoRes () {
        this(Response.getSuccessful("ReposRes Default success response"));
    }

    /**
     * @param res Repo's Response
     */
    public RepoRes (@NotNull Response res) {
        this.res = res;
        this.rows = new ArrayList<>();
    }

    /**
     * Note that if the response code is a failure, even if rows contains
     * content, the rows getter will return null!!
     * @param res Repo's Response
     * @param rows Rows of data to be returned by the repo, not null
     */
    public RepoRes (@NotNull Response res, @NotNull List<T> rows) {
        this.res = res;
        this.rows = rows;
    }

    public Response getRes() { return res; }

    /**
     * @return if the stored response has a SUCCESS code
     */
    public boolean isSuccess () { return res.getCode() == Response.ResCode.SUCCESS; }

    /**
     * @return null if response is fail, or a list of rows otherwise
     */
    @Nullable
    public List<T> getRows() {
        if (isSuccess()) {
            return rows;
        }
        return null;
    }

    /**
     * New rows can still be added even in light of a fail code, just that
     * getRows will return null unless the response code is a success
     * @param row Row to add
     */
    public void addRow(T row) {rows.add(row); }

    /**
     * Change the response. Note that changing the response to failure
     * will not delete already added rows of data. Instead, getRows
     * will return null until the response code is successful again.
     * @param res new response
     */
    public void setResponse (@NotNull Response res) {
        this.res = res;
    }
}
