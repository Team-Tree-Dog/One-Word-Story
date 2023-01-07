package adapters.display_data.story_data;

import java.time.LocalDateTime;

public interface DateFormatter {

    /**
     * @param dateTime date
     * @return String version of this date
     */
    String formatDate(LocalDateTime dateTime);
}
