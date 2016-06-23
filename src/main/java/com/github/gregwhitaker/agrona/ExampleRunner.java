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

import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.concurrent.ringbuffer.OneToOneRingBuffer;
import org.agrona.concurrent.ringbuffer.RingBufferDescriptor;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Runs the Agrona OneToOneRingBuffer example.
 */
public class ExampleRunner {

    private static final OneToOneRingBuffer BUFFER = new OneToOneRingBuffer(new UnsafeBuffer(
            ((ByteBuffer)(ByteBuffer.allocate(32768 + RingBufferDescriptor.TRAILER_LENGTH)))));

    /**
     * Main entry-point for this example.  This starts the producer and consumer applications.
     *
     * @param args command line arguments
     * @throws Exception
     */
    public static void main(String... args) throws Exception {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(new Producer(BUFFER));
        executor.execute(new Consumer(BUFFER));

        Thread.currentThread().join();
    }
}
