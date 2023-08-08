package org.example.utils;

import java.nio.ByteBuffer;
import java.util.Iterator;

import org.apache.spark.api.java.function.VoidFunction;

public class MemoryConsumer implements VoidFunction<Iterator<Integer>> {

    private long memoryToConsume;

    public MemoryConsumer(long memoryToConsume) {
        this.memoryToConsume = memoryToConsume;
    }

    @Override
    public void call(Iterator<Integer> iterator) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate((int) memoryToConsume);
        while (iterator.hasNext()) {
            // consume memory by writing to the buffer
            buffer.putInt(iterator.next());
        }
    }
}
