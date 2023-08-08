package org.example.utils;

import java.nio.ByteBuffer;
import org.apache.spark.sql.api.java.UDF1;

public class LongToByteBuffer implements UDF1<Integer, ByteBuffer> {
    @Override
    public ByteBuffer call(Integer value) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(value);
        buffer.putLong(value);
        buffer.flip();
        return buffer;
    }
}
