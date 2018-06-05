# netty-study
netty学习中的一些代码实例，多数代码是学习Norman Maurer的[《Netty In Action》](https://www.amazon.cn/dp/1617291471/ref=sr_1_2?s=books&ie=UTF8&qid=1528177249&sr=1-2&keywords=netty+in+action)自己动手写的一些章节实例，仅供参考。
代码中使用的netty版本为4.1.24.Final，日志使用[slf4j](https://www.slf4j.org/)+[lokback](https://logback.qos.ch/)处理。

## nio-service
nio-service模块是非Netty版本的Java阻塞/非阻塞版本的服务端示例。

## discard-service
discard-service模块是参照Netty官网上的丢弃服务写的Netty入门使用的示例，主要在于熟悉Netty客户端/服务端的体系架构。

## echo-service
echo-service模块是用Netty实现的echo服务，主要功能是客户端连接服务端，发送数据到服务端，服务端收到数据后，再将数据回送给客户端。

## bytebuf-api
bytebuf-api模块是Netty底层核心组件主要API的测试示例，包括slice(),copy(),getXXX(),setXXX(),readerXXX(),writerXXX()等。

## handler-test
handler-test模块是使用EmbeddedChannel对ChannelHandler进行单元测试的示例，主要涉及使用EmbeddedChannel测试ChannelHandler如何处理入站消息，出站消息，
异常处理等方面。单元测试工作是结合[Junit](https://junit.org/junit4/)完成。

## codec-service
codec-service模块是Netty编解码器API示例，包括ByteToMessageDecoder，MessageToByteEncoder，MessageToMessageCodec，CombinedIntegerStringCodec编解码器使用方法。模块中使用MessageToMessageCodec实现了一个简单二进制私有协议栈编解码功能。
