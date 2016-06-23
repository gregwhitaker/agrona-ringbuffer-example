agrona-ringbuffer-example
===

This example shows you how to send fire-and-forget messages between two threads using an [Agrona](https://github.com/real-logic/Agrona) RingBuffer.

The example starts a consumer that receives messages sent from a producer at a set interval.

##Running the Example
The example can be run using the following gradle command:

```
$ ./gradlew run
```