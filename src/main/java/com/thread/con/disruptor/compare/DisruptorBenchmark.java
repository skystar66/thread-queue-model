package com.thread.con.disruptor.compare;

import com.lmax.disruptor.*;

import java.util.Queue;
import java.util.concurrent.*;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class DisruptorBenchmark {
    private static final int RING_BUFFER_SIZE = 1024;
    private static final int EVENT_COUNT = 1000000;

    private RingBuffer<Event> ringBuffer;
    private BatchEventProcessor<Event> eventProcessor;
    private Disruptor<Event> disruptor;
    private EventProducer eventProducer;

    @Setup
    public void setup() {
        disruptor = createDisruptor();
        ringBuffer = disruptor.getRingBuffer();
        eventProcessor = createEventProcessor(ringBuffer);
        eventProducer = new EventProducer(ringBuffer);
        disruptor.start();
    }

    @TearDown
    public void teardown() {
        disruptor.shutdown();
    }

    @Benchmark
    public void testDisruptor(Blackhole blackhole) {
        for (int i = 0; i < EVENT_COUNT; i++) {
            eventProducer.produce(i);
        }
        long sequence = ringBuffer.getCursor();
        while (sequence < EVENT_COUNT - 1) {
            sequence = ringBuffer.getCursor();
        }
        blackhole.consume(sequence + 1);
    }

    private Disruptor<Event> createDisruptor() {
        EventFactory<Event> eventFactory = Event::new;
        WaitStrategy waitStrategy = new YieldingWaitStrategy();

        Disruptor<Event> disruptor = new Disruptor<>(eventFactory, RING_BUFFER_SIZE, Executors.defaultThreadFactory(), ProducerType.SINGLE, waitStrategy);
        disruptor.setDefaultExceptionHandler(new FatalExceptionHandler());

        return disruptor;
    }

    private BatchEventProcessor<Event> createEventProcessor(RingBuffer<Event> ringBuffer) {
        SequenceBarrier barrier = ringBuffer.newBarrier();
        EventHandler<Event> eventHandler = (event, sequence, endOfBatch) -> {
            // 处理事件的逻辑
        };

        return new BatchEventProcessor<>(ringBuffer, barrier, eventHandler);
    }


    private static class Event {
        // 定义事件数据结构
        private int eventData;

        public int getEventData() {
            return eventData;
        }

        public void setEventData(int eventData) {
            this.eventData = eventData;
        }
    }

    private static class EventProducer {
        private final RingBuffer<Event> ringBuffer;

        public EventProducer(RingBuffer<Event> ringBuffer) {
            this.ringBuffer = ringBuffer;
        }

        public void produce(int eventData) {
            long sequence = ringBuffer.next();
            Event event = ringBuffer.get(sequence);
            // 设置事件数据
            event.setEventData(eventData);
            ringBuffer.publish(sequence);
        }
    }
}