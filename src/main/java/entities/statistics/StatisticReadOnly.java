package entities.statistics;

/**
 * Interface which provides only the getter of the statistic
 * @param <T> Type of data this statistic returns
 */
public interface StatisticReadOnly<T> {
    /**
     * @return the data this statistic is tracking
     */
    T getStatData ();
}
