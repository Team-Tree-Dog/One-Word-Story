package adapters.display_data.story_data;

import java.time.LocalDateTime;

public class DateFormatterBasic implements DateFormatter {

    private String addZeroIfNecessary(int value) {
        String result = String.valueOf(value);
        if (value < 10) {
            result = '0' + result;
        }
        return result;
    }

    @Override
    public String formatDate(LocalDateTime dateTime) {
        return addZeroIfNecessary(dateTime.getMonthValue()) + "/" +
                addZeroIfNecessary(dateTime.getDayOfMonth()) + '/' +
                dateTime.getYear() + ' ' +
                addZeroIfNecessary(dateTime.getHour()) + ':' +
                addZeroIfNecessary(dateTime.getMinute()) +
                ':' + addZeroIfNecessary(dateTime.getSecond());
    }
}
