Java NIO如何处理慢速的连接
本文地址：http://developer.51cto.com/art/201112/307733.htm

对企业级的服务器软件，高性能和可扩展性是基本的要求。除此之外，还应该有应对各种不同环境的能力。例如，一个好的服务器软件不应该假设所有的客户端都有很快的处理能力和很好的网络环境。如果一个客户端的运行速度很慢，或者网络速度很慢，这就意味着整个请求的时间变长。而对于服务器来说，这就意味着这个客户端的请求将占用更长的时间。这个时间的延迟不是由服务器造成的，因此CPU的占用不会增加什么，但是网络连接的时间会增加，处理线程的占用时间也会增加。这就造成了当前处理线程和其他资源得不到很快的释放，无法被其他客户端的请求来重用。例如Tomcat，当存在大量慢速连接的客户端时，线程资源被这些慢速的连接消耗掉，使得服务器不能响应其他的请求了。

前面介绍过，NIO的异步非阻塞的形式，使得很少的线程就能服务于大量的请求。通过Selector的注册功能，可以有选择性地返回已经准备好的频道，这样就不需要为每一个请求分配单独的线程来服务。

在一些流行的NIO的框架中，都能看到对OP_ACCEPT和OP_READ的处理。很少有对OP_WRITE的处理。我们经常看到的代码就是在请求处理完成后，直接通过下面的代码将结果返回给客户端：

不对OP_WRITE进行处理的样例：

while (bb.hasRemaining()) {  
    int len = socketChannel.write(bb);  
    if (len < 0) {  
        throw new EOFException();  
    }  
} 
这样写在大多数的情况下都没有什么问题。但是在客户端的网络环境很糟糕的情况下，服务器会遭到很沉重的打击。

因为如果客户端的网络或者是中间交换机的问题，使得网络传输的效率很低，这时候会出现服务器已经准备好的返回结果无法通过TCP/IP层传输到客户端。这时候在执行上面这段程序的时候就会出现以下情况。

(1) bb.hasRemaining()一直为“true”，因为服务器的返回结果已经准备好了。

(2) socketChannel.write(bb)的结果一直为0，因为由于网络原因数据一直传不过去。

(3) 因为是异步非阻塞的方式，socketChannel.write(bb)不会被阻塞，立刻被返回。

(4) 在一段时间内，这段代码会被无休止地快速执行着，消耗着大量的CPU的资源。事实上什么具体的任务也没有做，一直到网络允许当前的数据传送出去为止。

这样的结果显然不是我们想要的。因此，我们对OP_WRITE也应该加以处理。在NIO中最常用的方法如下。

一般NIO框架中对OP_WRITE的处理：

while (bb.hasRemaining()) {  
    int len = socketChannel.write(bb);  
    if (len < 0){  
        throw new EOFException();  
    }  
    if (len == 0) {  
        selectionKey.interestOps(  
                        selectionKey.interestOps() | SelectionKey.OP_WRITE);  
        mainSelector.wakeup();  
        break;  
    }  
} 
上面的程序在网络不好的时候，将此频道的OP_WRITE操作注册到Selector上，这样，当网络恢复，频道可以继续将结果数据返回客户端的时候，Selector会通过SelectionKey来通知应用程序，再去执行写的操作。这样就能节约大量的CPU资源，使得服务器能适应各种恶劣的网络环境。

可是，Grizzly中对OP_WRITE的处理并不是这样的。我们先看看Grizzly的源码吧。在Grizzly中，对请求结果的返回是在ProcessTask中处理的，经过SocketChannelOutputBuffer的类，最终通过OutputWriter类来完成返回结果的动作。在OutputWriter中处理OP_WRITE的代码如下：

Grizzly中对OP_WRITE的处理：

public static long flushChannel(SocketChannel socketChannel,  
        ByteBuffer bb, long writeTimeout) throws IOException  
{  
    SelectionKey key = null;  
    Selector writeSelector = null;  
    int attempts = 0;  
    int bytesProduced = 0;  
    try {  
        while (bb.hasRemaining()) {  
            int len = socketChannel.write(bb);  
            attempts++;  
            if (len < 0){  
                throw new EOFException();  
            }  
            bytesProduced += len;  
            if (len == 0) {  
                if (writeSelector == null){  
                    writeSelector = SelectorFactory.getSelector();  
                    if (writeSelector == null){  
                        // Continue using the main one  
                        continue;  
                    }  
                }  
                key = socketChannel.register(writeSelector, key.OP_WRITE);  
                if (writeSelector.select(writeTimeout) == 0) {  
                    if (attempts > 2)  
                        throw new IOException("Client disconnected");  
                } else {  
                    attempts--;  
                }  
            } else {  
                attempts = 0;  
            }  
        }  
    } finally {  
        if (key != null) {  
            key.cancel();  
            key = null;  
        }  
        if (writeSelector != null) {  
            // Cancel the key.  
            writeSelector.selectNow();  
            SelectorFactory.returnSelector(writeSelector);  
        }  
    }  
    return bytesProduced;  
} 
上面的程序例17.9与例17.8的区别之处在于：当发现由于网络情况而导致的发送数据受阻(len==0)时，例17.8的处理是将当前的频道注册到当前的Selector中；而在例17.9中，程序从SelectorFactory中获得了一个临时的Selector。在获得这个临时的Selector之后，程序做了一个阻塞的操作：writeSelector.select(writeTimeout)。这个阻塞操作会在一定时间内(writeTimeout)等待这个频道的发送状态。如果等待时间过长，便认为当前的客户端的连接异常中断了。

这种实现方式颇受争议。有很多开发者置疑Grizzly的作者为什么不使用例17.8的模式。另外在实际处理中，Grizzly的处理方式事实上放弃了NIO中的非阻塞的优势，使用writeSelector.select(writeTimeout)做了个阻塞操作。虽然CPU的资源没有浪费，可是线程资源在阻塞的时间内，被这个请求所占有，不能释放给其他请求来使用。

Grizzly的作者对此的回应如下。

(1) 使用临时的Selector的目的是减少线程间的切换。当前的Selector一般用来处理OP_ACCEPT，和OP_READ的操作。使用临时的Selector可减轻主Selector的负担；而在注册的时候则需要进行线程切换，会引起不必要的系统调用。这种方式避免了线程之间的频繁切换，有利于系统的性能提高。

(2) 虽然writeSelector.select(writeTimeout)做了阻塞操作，但是这种情况只是少数极端的环境下才会发生。大多数的客户端是不会频繁出现这种现象的，因此在同一时刻被阻塞的线程不会很多。

(3) 利用这个阻塞操作来判断异常中断的客户连接。

(4) 经过压力实验证明这种实现的性能是非常好的。

原文链接：http://qingfengjushi1.iteye.com/blog/1185070

