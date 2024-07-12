package com.github.copilot.task.scheduler;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Represents an item with a delay before it becomes available for processing.
 * This class is used in delay queues where elements are only taken when their delay has expired.
 *
 * @param <T> The type of the item being delayed.
 */
public class DelayItem<T> implements Delayed {

    private final long delay; // The delay time in milliseconds.
    private final long expire; // The expiration time in milliseconds when the item becomes available.
    private final T t; // The item being delayed.

    private final long now; // The creation time of the item.

    /**
     * Constructs a new DelayItem.
     *
     * @param delay The delay time in milliseconds before the item becomes available.
     * @param t     The item to be delayed.
     */
    public DelayItem(long delay, T t) {
        this.delay = delay;
        this.t = t;
        // Calculate the expiration time based on the current time and the specified delay.
        expire = System.currentTimeMillis() + delay;
        now = System.currentTimeMillis();
    }

    /**
     * Returns the remaining delay associated with this item, in the given time unit.
     *
     * @param unit The time unit in which the delay is to be returned.
     * @return The remaining delay; zero or negative values indicate that the delay has already expired.
     */
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.expire - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Compares this DelayItem with another Delayed object.
     *
     * @param o The Delayed object to compare with.
     * @return A negative integer, zero, or a positive integer as this item is less than, equal to,
     *         or greater than the specified object.
     */
    @Override
    public int compareTo(Delayed o) {
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }

    /**
     * Retrieves the item being delayed.
     *
     * @return The delayed item.
     */
    public T getItem() {
        return t;
    }

    @Override
    public String toString() {
        String sb = "DelayedElement{" + "delay=" + delay +
                ", expire=" + expire +
                ", now=" + now +
                '}';
        return sb;
    }
}