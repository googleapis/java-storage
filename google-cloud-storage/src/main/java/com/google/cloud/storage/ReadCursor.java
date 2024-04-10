package com.google.cloud.storage;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Shrink wraps a beginning, offset and limit for tracking state of an individual invocation of
 * {@link #read}
 */
final class ReadCursor {
    private final long beginning;
    private long offset;
    private final long limit;

    ReadCursor(long beginning, long limit) {
        this.limit = limit;
        this.beginning = beginning;
        this.offset = beginning;
    }

    public boolean hasRemaining() {
        return limit - offset > 0;
    }

    public void advance(long incr) {
        checkArgument(incr >= 0);
        offset += incr;
    }

    public long read() {
        return offset - beginning;
    }

    @Override
    public String toString() {
        return String.format("ReadCursor{begin=%d, offset=%d, limit=%d}", beginning, offset, limit);
    }
}
