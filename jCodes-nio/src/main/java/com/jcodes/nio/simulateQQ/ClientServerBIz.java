package com.jcodes.nio.simulateQQ;
import java.io.IOException;  
import java.net.InetSocketAddress;  
import java.nio.ByteBuffer;  
import java.nio.channels.SocketChannel;  
 
 
public class ClientServerBIz {  
      
    private SocketChannel sc;  
    public ClientServerBIz() throws IOException  
    {  
        sc = SocketChannel.open();  
        sc.configureBlocking(false);  
        sc.connect(new InetSocketAddress("localhost",8001));  
    }  
 
    //发送信息到服务器  
    public void sendToServer(Object obj)  
    {  
        try {  
            while(!sc.finishConnect())  
            {}  
            sc.write(ByteBuffer.wrap(obj.toString().getBytes()));  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
      
    //获取服务器信息传递信息到客户端  
    public Object sendToClient()  
    {  
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);  
        buffer.clear();  
        StringBuffer sb = new StringBuffer();  
        int count = 0;  
        Object obj = null;  
        try {  
            //获取字节长度  
            Thread.sleep(100);  
            while ((count = sc.read(buffer)) > 0) {  
                sb.append(new String(buffer.array(), 0, count));  
            }  
            if( sb.length() > 0 )  
            {  
                obj = sb.toString();  
                if("close".equals(sb.toString()))  
                {  
                    obj = null;  
                    sc.close();  
                    sc.socket().close();  
                }  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
        return obj;  
    }  
} 