package com.lmax.ticketing.io;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.lmax.disruptor.EventHandler;
import com.lmax.ticketing.api.Message;

public class Journaller implements EventHandler<Message>
{
    private final File directory;
    private FileChannel file = null;
    private final ByteBuffer[] buffers = new ByteBuffer[2];

    public Journaller(File directory)
    {
        this.directory = directory;
        buffers[0] = ByteBuffer.allocate(4);
    }
    
    @Override
    public void onEvent(Message event, long sequence, boolean endOfBatch) throws Exception
    {
        int size = event.getSize();
        
        if (null == file)
        {
            file = new RandomAccessFile(new File(directory, "jnl"), "rw").getChannel();
        }
        
        buffers[0].clear();
        buffers[0].putInt(size).flip();
        buffers[1] = event.getByteBuffer();
        buffers[1].clear().limit(size);
        
        while (buffers[1].hasRemaining())
        {
            file.write(buffers);
        }

        buffers[1].clear();
        buffers[1] = null;
        
        if (endOfBatch)
        {
            file.force(true);
        }
    }
}
