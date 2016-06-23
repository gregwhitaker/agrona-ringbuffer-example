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
import org.agrona.concurrent.ringbuffer.RingBuffer;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import rx.Observable;
import rx.RxReactiveStreams;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Producer implements Runnable {
    private final RingBuffer buffer;

    public Producer(RingBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        // Create an observable that emits messages at a specific interval.
        Publisher<String> requestStream = RxReactiveStreams
                .toPublisher(Observable
                        .interval(1_000, TimeUnit.MILLISECONDS)
                        .onBackpressureDrop()
                        .map(i -> "Yo " + i)
                );

        CountDownLatch latch = new CountDownLatch(Integer.MAX_VALUE);

        requestStream.subscribe(new Subscriber<String>() {

            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(String s) {
                System.out.println("Producer -> " + s);
                UnsafeBuffer unsafeBuffer = new UnsafeBuffer(s.getBytes());
                unsafeBuffer.wrap(s.getBytes());
                buffer.write(1, unsafeBuffer, 0, s.length());
                latch.countDown();
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                latch.countDown();
            }

            @Override
            public void onComplete() {
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.exit(0);
    }
}
