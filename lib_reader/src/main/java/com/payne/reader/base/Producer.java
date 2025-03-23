package com.payne.reader.base;

/**
 * A functional interface (callback) that transmits a single value.
 *
 * @param <T> the value type
 * @author naz
 * Date 2020/7/16
 */
public interface Producer<T> {
    /**
     * Production given value.
     *
     * @return T the value
     * @throws Exception on error
     */
    T dispatch() throws Exception;
}
