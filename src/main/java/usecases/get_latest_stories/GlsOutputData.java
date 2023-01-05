package usecases.get_latest_stories;

import usecases.FullStoryDTO;
import usecases.Response;

import java.util.List;

/**
 * Output data class of Get Latest Stories use-case
 */
public record GlsOutputData(List<FullStoryDTO> stories, Response res) {}
