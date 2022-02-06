/*
 * Copyright 2020, Robert 'Bobby' Zenz
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.bonsaimind.jluascript.support;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.bonsaimind.jluascript.utils.Verifier;

/**
 * The {@link ShebangSkippingInputStream} is an {@link InputStream} extension
 * and wrapper which skips over a Shebang line at the start of the stream, if
 * one is present.
 */
public class ShebangSkippingInputStream extends InputStream {
	/** The CR as int. */
	protected static final int CARRIAGE_RETURN = 0x0d;
	/** The LF as int. */
	protected static final int LINE_FEED = 0x0a;
	/** The line marker for a Shebang line. */
	protected static final String SHEBANG_START = "#!";
	
	/** The buffer for reading the Shebang marker. */
	protected byte[] buffer = new byte[2];
	/** How many bytes have been read into the buffer. */
	protected int bufferLength = 0;
	/** The {@link Charset} of the wrapped stream. */
	protected Charset charset = null;
	/** Whether this is the first read operation on the stream. */
	protected boolean firstRead = true;
	/** The wrapped source {@link InputStream}. */
	protected InputStream sourceStream = null;
	
	/**
	 * Creates a new instance of {@link ShebangSkippingInputStream}.
	 *
	 * @param sourceStream The {@link InputStream} that is going to be wrapped,
	 *        cannot be {@code null}.
	 * @param charset The {@link Charset} to use, cannot be {@code null}.
	 * @throws IllegalArgumentException If the given {@code inputStream} or
	 *         {@code charset} is {@code null}.
	 */
	public ShebangSkippingInputStream(InputStream sourceStream, Charset charset) {
		super();
		
		Verifier.notNull("sourceStream", sourceStream);
		Verifier.notNull("charset", charset);
		
		this.sourceStream = sourceStream;
		this.charset = charset;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int available() throws IOException {
		return sourceStream.available();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws IOException {
		sourceStream.close();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int read() throws IOException {
		if (firstRead) {
			skipShebang();
			firstRead = false;
		}
		
		if (bufferLength > 0) {
			int value = buffer[0];
			
			discardBuffer(1);
			
			return value;
		}
		
		return sourceStream.read();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (firstRead) {
			skipShebang();
			firstRead = false;
		}
		
		if (bufferLength > 0) {
			for (int index = 0; (index + off) < len && index < bufferLength; index++) {
				b[index + off] = buffer[index];
			}
			
			int bytesWrittenFromBuffer = Math.min(len, bufferLength);
			
			discardBuffer(bytesWrittenFromBuffer);
			
			int read = sourceStream.read(b, bytesWrittenFromBuffer, len - bytesWrittenFromBuffer);
			
			if (read < 0) {
				read = 0;
			}
			
			return read + bytesWrittenFromBuffer;
		}
		
		return sourceStream.read(b, off, len);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long skip(long n) throws IOException {
		if (firstRead) {
			skipShebang();
			firstRead = false;
		}
		
		discardBuffer((int)n);
		
		return sourceStream.skip(n - bufferLength);
	}
	
	/**
	 * Discards the given amount from the {@link #buffer}.
	 * 
	 * @param countToDiscard The amount to discard.
	 */
	protected void discardBuffer(int countToDiscard) {
		for (int index = 0; index < bufferLength - countToDiscard; index++) {
			buffer[index] = buffer[index + countToDiscard];
		}
		
		bufferLength = Math.max(0, bufferLength - countToDiscard);
	}
	
	/**
	 * Skips the Shebang line in the underlying {@link #sourceStream} if there
	 * is any.
	 * 
	 * @throws IOException If reading from the {@link #sourceStream} failed.
	 */
	protected void skipShebang() throws IOException {
		bufferLength = sourceStream.read(buffer);
		
		if (bufferLength < 2) {
			return;
		}
		
		String firstTwoCharacters = new String(new byte[] { buffer[0], buffer[1] }, charset);
		
		if (firstTwoCharacters.equals(SHEBANG_START)) {
			int readByte = 0;
			
			while ((readByte = sourceStream.read()) >= 0 && readByte != CARRIAGE_RETURN && readByte != LINE_FEED) {
				// Continue to read from the stream and discard anything until
				// we find a newline of some sort.
			}
			
			if (readByte >= 0) {
				buffer[0] = (byte)readByte;
				bufferLength = 1;
			} else {
				// We've exhausted the stream trying to find a newline...tough
				// luck I'm afraid.
				bufferLength = 0;
			}
		} else {
			bufferLength = 2;
		}
	}
}
