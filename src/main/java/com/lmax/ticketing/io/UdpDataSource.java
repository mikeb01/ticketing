package com.lmax.ticketing.io;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import com.lmax.disruptor.RingBuffer;
import com.lmax.ticketing.api.Message;

public class UdpDataSource implements Runnable
{
    private final RingBuffer<Message> ringBuffer;
    private final SocketAddress address;
    private DatagramChannel channel;
    private DatagramSocket socket;

    public UdpDataSource(RingBuffer<Message> ringBuffer, int port)
    {
        this.ringBuffer = ringBuffer;
        this.address = new InetSocketAddress(port);
    }
    
    public void bind() throws IOException
    {
        System.out.println("Binding to address: " + address);
        channel = DatagramChannel.open();
        socket = channel.socket();
        socket.bind(address);
    }

    @Override
    public void run()
    {
        ByteBuffer buffer = ByteBuffer.allocate(1400);
        ByteBuffer slice = buffer.slice();
        
        Thread t = Thread.currentThread();
        try
        {
            while (!t.isInterrupted())
            {
                buffer.clear();
                slice.clear();
                
                channel.receive(buffer);
                buffer.flip();
//                System.out.println(buffer);
                
                do
                {
                    int length = buffer.getInt(slice.position());
                    slice.position(slice.position() + 4);
                    slice.limit(slice.position() + length);
                    
                    long sequence = ringBuffer.next();
                    Message message = ringBuffer.get(sequence);
                    try
                    {
                        if (message.getByteBuffer().remaining() < slice.remaining())
                        {
                            System.out.println(message.getByteBuffer());
                        }
                        message.getByteBuffer().clear();
                        message.getByteBuffer().put(slice);
                    }
                    catch (RuntimeException e)
                    {
                        System.out.println(buffer);
                        System.out.println(slice);
                        System.out.println(message.getByteBuffer());
                        
                        throw e;
                    }
                    ringBuffer.publish(sequence);
                    
                    slice.limit(buffer.limit());
                } 
                while (slice.position() < buffer.limit());
            }
        }
        catch (IOException e)
        {
            System.err.println("Buffer receive failed, exiting...");
            e.printStackTrace();
        }
    }    
}
