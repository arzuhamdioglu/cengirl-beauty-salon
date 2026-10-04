package com.cengirl.beautysalon.infrastructure.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates unique string identifiers for in-memory entities.
 */
public final class IdGenerator {

    private static final AtomicLong SEQUENCE = new AtomicLong(1000);

    private IdGenerator() {
    }

    public static String next(String prefix) {
        return prefix + "-" + SEQUENCE.incrementAndGet();
    }
}
