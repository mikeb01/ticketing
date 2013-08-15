package com.lmax.ticketing.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import com.lmax.disruptor.EventHandler;
import com.lmax.ticketing.api.Message;

public class UdpEventHandler implements EventHandler<Message>
{
    private final int port;
    private final ByteBuffer buffer = ByteBuffer.allocate(1400);
    private DatagramChannel channel;
    private final String host;

    public UdpEventHandler(String host, int port)
    {
        this.host = host;
        this.port = port;
    }
    
    public void bind() throws IOException
    {
        channel = DatagramChannel.open();
        channel.connect(new InetSocketAddress(host, port));
    }
    
    @Override
    public void onEvent(Message message, long sequence, boolean endOfBatch) throws Exception
    {
        int size = message.getSize();
        
        if (buffer.remaining() < size + 4)
        {
            flush();
        }
        
        buffer.putInt(size);
        ByteBuffer messageBuffer = message.getByteBuffer();
        int messagePosition = message.getByteBufferPosition();

        messageBuffer.position(messagePosition);
        messageBuffer.limit(messagePosition + size);
        buffer.put(messageBuffer);
        messageBuffer.clear();
        
        if (endOfBatch)
        {
            flush();
        }
    }

    private void flush() throws IOException
    {
        buffer.flip();
        
        DatagramChannel channel = bindAndGetChannel();
        
        while (buffer.remaining() > 0)
        {
            channel.write(buffer);
        }
        
        buffer.clear();
    }
    
    private DatagramChannel bindAndGetChannel() throws IOException
    {
        if (null == channel)
        {
            bind();
        }
        
        return channel;
    }
}
