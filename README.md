# thread-queue-model
* 该工程是一个线程队列设计模型，主要解决提供并发编程中 大批量消息（例如上千万/亿级别的消息量），并且控制在100ms内级别处理速度，而设计的并发线程模型，充分利用 线程与 CPU间减少 切换 ，防止线程CPU上下文切换带来的消耗！
* 该线程设计模型可借鉴被应用于 大型直播间 消息分发/批推的方式，降低消息延迟率，提升消息到达率的并发设计方案