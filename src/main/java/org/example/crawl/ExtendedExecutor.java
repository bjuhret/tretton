package org.example.crawl;

import java.util.concurrent.*;

/**
 * An extension of ThreadPoolExecutor that captures exceptions thrown by tasks during execution.
 * The purpose of this class is to allow access to errors thrown inside the threads, facilitating
 * the ability to capture and handle exceptions to control the overall execution flow.
 */
public final class ExtendedExecutor extends ThreadPoolExecutor {

    // Variable to store the exception thrown by a task
    RuntimeException exception = null;

    /**
     * Constructs a new ExtendedExecutor with the specified core pool size.
     *
     * @param corePoolSize The number of threads to keep in the pool, even if they are idle.
     */
    public ExtendedExecutor(int corePoolSize) {
        // Initialize the ThreadPoolExecutor with a LinkedBlockingQueue
        super(corePoolSize, corePoolSize,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }

    /**
     * Overrides the afterExecute method to capture exceptions thrown by tasks.
     *
     * @param r The runnable that has completed.
     * @param throwable The exception thrown by the task, or null if none.
     */
    protected void afterExecute(Runnable r, Throwable throwable) {
        super.afterExecute(r, throwable);

        // Check if the task is a Future and if it is done
        if (throwable == null && r instanceof Future<?>) {
            try {
                Future<?> future = (Future<?>) r;

                // Check if the Future is done and get the result
                if (future.isDone()) {
                    future.get();
                }
            } catch (CancellationException ce) {
                // Handle cancellation exception
                throwable = ce;
            } catch (ExecutionException ee) {
                // Handle execution exception and get the cause
                throwable = ee.getCause();
            } catch (InterruptedException ie) {
                // Reassert the interrupted status
                Thread.currentThread().interrupt();
            }
        }

        // If an exception is still present, store it in the 'exception' variable
        if (throwable != null) {
            exception = new RuntimeException(throwable);
        }
    }
}
