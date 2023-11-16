package org.example.crawl;

/**
 * Represents the progress of the crawling operation, including the number of items persisted,
 * the number of scheduled tasks, the elapsed time in seconds, and any runtime exception encountered.
 */
public class Progress {

    // Member variables
    private final Integer persisted;
    private final Integer scheduled;
    private final Long elapsedTimeInSeconds;
    private final RuntimeException exception;

    /**
     * Constructs a Progress instance.
     *
     * @param persisted           The number of items persisted during crawling.
     * @param scheduled           The number of scheduled tasks.
     * @param elapsedTimeInSeconds The elapsed time in seconds.
     * @param exception           Any runtime exception encountered during crawling.
     */
    public Progress(Integer persisted, Integer scheduled, Long elapsedTimeInSeconds, RuntimeException exception) {
        this.persisted = persisted;
        this.scheduled = scheduled;
        this.elapsedTimeInSeconds = elapsedTimeInSeconds;
        this.exception = exception;
    }

    /**
     * Gets the runtime exception encountered during crawling, if any.
     *
     * @return The runtime exception, or null if no exception occurred.
     */
    public RuntimeException getException() {
        return exception;
    }

    /**
     * Gets the number of items persisted during crawling.
     *
     * @return The number of items persisted.
     */
    public Integer getPersisted() {
        return persisted;
    }

    /**
     * Gets the number of scheduled tasks.
     *
     * @return The number of scheduled tasks.
     */
    public Integer getScheduled() {
        return scheduled;
    }

    /**
     * Gets the elapsed time in seconds.
     *
     * @return The elapsed time in seconds.
     */
    public Long getElapsedTimeInSeconds() {
        return elapsedTimeInSeconds;
    }
}
