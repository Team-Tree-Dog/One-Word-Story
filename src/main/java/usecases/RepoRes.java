package usecases;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Output from the DB with rows of data
 * @param <T> The type of each individual row of the DB content being returned
 */
public class RepoRes<T> {
    private final List<T> rows;
    private final Response res;

    /**
     * @param res Repo's Response
     */
    public RepoRes (@NotNull Response res) {
        this.res = res;

        // rows null if code is fail, otherwise initialize empty array
        if (res.getCode() == Response.ResCode.SUCCESS) {
            this.rows = new ArrayList<>();
        } else {
            this.rows = null;
        }

    }

    /**
     * @param res Repo's Response
     * @param rows Rows of data to be returned by the repo. Should be null if res is not success
     */
    public RepoRes (@NotNull Response res, @Nullable List<T> rows) {
        // Asserts one of the two possible states
        assert (res.getCode() == Response.ResCode.SUCCESS && rows != null) ||
                (res.getCode() != Response.ResCode.SUCCESS && rows == null);

        this.res = res;
        this.rows = rows;
    }

    public Response getRes() { return res; }

    public boolean isSuccess () { return rows != null; }

    public List<T> getRows() { return rows; }

    public void addRow(T row) {rows.add(row); }
}
