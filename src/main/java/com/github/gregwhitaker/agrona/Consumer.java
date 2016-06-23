/*
 * Copyright 2016 Greg Whitaker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.gregwhitaker.agrona;

import org.agrona.concurrent.ringbuffer.RingBuffer;

import java.util.concurrent.CountDownLatch;

/**
 * Consumes messages from the ring buffer.
 */
public class Consumer implements Runnable {
    private final RingBuffer buffer;

    /**
     * Initializes this instance of {@link Consumer} that consumes messages from the ring buffer.
     * @param buffer ring buffer
     */
    public Consumer(RingBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        CountDownLatch latch = new CountDownLatch(Integer.MAX_VALUE);

        while (latch.getCount() > 0) {
            buffer.read((msgTypeId, srcBuffer, index, length) -> {
                byte[] message = new byte[length];
                srcBuffer.getBytes(index, message);
                System.out.println("Consumer <- " + new String(message));
                latch.countDown();
            });
        }
    }
}
