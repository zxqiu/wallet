package com.wallet.utils.misc;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;

public class DataInputByteBuffer extends DataInputStream 
{
	public static class ByteBufferInputStream extends InputStream
	{
		protected ByteBuffer buf_;
		protected final int size_ = 4096;		
		
		public ByteBufferInputStream()
		{
		}
		
		public ByteBufferInputStream(ByteBuffer buffer)
		{
			buf_ = buffer;
		}
		
		public int read() throws IOException 
		{			
			return buf_.get() & 0xFF;	
		}

		public int read(byte[] b) throws IOException
		{				
			int initialPosition = buf_.position();
			buf_.get(b);	
			return buf_.position() - initialPosition;
		}

		public int read(byte b[], int off, int len) throws IOException
		{			
			int initialPosition = buf_.position();
			buf_.get(b, off, len);
			return buf_.position() - initialPosition;
		}

		/* Reads data from input stream into the direct ByteBuffer buffer */
		public int read(ByteBuffer buffer) throws IOException
		{
			if (buffer == null)
				throw new NullPointerException();
				
			buffer = buf_.duplicate();
			return buffer.position();
		}

		/* Reads upto 'len' bytes of data from input stream into the direct ByteBuffer buffer starting at offset off */
		public int read(ByteBuffer buffer, int off, int len) throws IOException
		{
			if (buffer == null)
				throw new NullPointerException();
			else if (off < 0 || len <= 0 || len > buffer.remaining() - off)
				throw new IndexOutOfBoundsException();

			if (buf_.remaining() < len)
				throw new BufferUnderflowException();

			if (off > buf_.remaining())
				throw new IllegalArgumentException("off > buffer size");

			 
			byte[] bytes = new byte[len];
			buf_.get(bytes, off, len);
			buffer = ByteBuffer.wrap(bytes);
			return buffer.remaining();
		}

		public int skipBytes(int n) throws IOException 
		{
			int bytesSkipped = 0;
			if (n < 0)
				return 0;
			
			if (n > 0)
			{
				if (n > buf_.remaining())
					throw new IllegalArgumentException("Cannot skip beyong available buffer bytes");
				else	
				{		
					buf_.position(n);
					bytesSkipped = buf_.position();
				}
			}
			return bytesSkipped;
		}
		
		public int available()
		{
			return buf_.remaining();
		}
	}

	public static class Buffer extends ByteBufferInputStream
	{        			
		public Buffer()
		{		
		}   
		
		public Buffer(ByteBuffer buffer)
		{
			super(buffer);
		}

 		public int capacity()
		{
			return buf_.capacity();
		}
		
		public int position()
		{
			return buf_.position();
		}
		
		public byte peekAt(int position)
        {
        	if (position < 0 || position > buf_.capacity())
        		throw new IllegalArgumentException("Trying to access a byte outside the boundaires");
        	return buf_.get(position);
        }
		
		public void reset(ByteBuffer buffer)
		{
			buf_ = buffer;
		} 
		
		protected ByteBuffer getInternalBuffer()
		{
			return buf_;
		}

		public void seek(int position)
		{
			if (position < 0)
				throw new IllegalArgumentException("Cannot seek to a negative position");

			try
			{
				buf_.position(position);
			}
			catch (Throwable th)
			{
				logger_.debug(buf_.position() + "/" + buf_.limit() + "/" + position);
				logger_.warn(LogUtils.throwableToString(th));				
			}
		}
		
		public void close()
		{
			buf_ = null;
		}	
	}	
	
	private static final Logger logger_ = LoggerFactory.getLogger(DataInputByteBuffer.class);
	private Buffer buffer_;
	
	public DataInputByteBuffer()
	{
		this(new Buffer());
	}

	public DataInputByteBuffer(Buffer buffer) 
	{
		super(buffer);
		this.buffer_ = buffer;
	} 
	
	public DataInputByteBuffer(ByteBuffer buffer)
	{
		this(new Buffer(buffer));
	}
	
	public int position()
    {
    	return buffer_.position();
    }
	
	public byte peekAt(int position)
    {
    	return buffer_.peekAt(position);
    }
    
    public void reset(ByteBuffer buffer)
    {
    	buffer_.reset(buffer);
    }
    
    public void seek(int length)
    {
    	if (length > buffer_.capacity())
    		throw new IllegalArgumentException("length: " + length + " > buffer capacity: " + buffer_.capacity());
		
    	buffer_.seek(length);
    }
    
    public int capacity()
    {
    	return buffer_.capacity();
    }
    
    public ByteBuffer toByteBuffer()
    {
    	return buffer_.getInternalBuffer();
    }
        
    public void close() throws IOException
    {
    	buffer_.close();
    }
}

