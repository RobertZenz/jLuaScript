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
import java.io.Reader;

import org.bonsaimind.jluascript.utils.Verifier;

/**
 * The {@link ShebangSkippingReader} is an {@link Reader} extension and wrapper
 * which skips over a Shebang line at the start of the reader, if one is
 * present.
 */
public class ShebangSkippingReader extends Reader {
	/** The CR as char. */
	protected static final char CARRIAGE_RETURN = 0x0d;
	/** The LF as char. */
	protected static final char LINE_FEED = 0x0a;
	/** The line marker for a Shebang line. */
	protected static final String SHEBANG_START = "#!";
	
	/** The buffer for reading the Shebang marker. */
	protected char[] buffer = new char[2];
	/** How many bytes have been read into the buffer. */
	protected int bufferLength = 0;
	/** Whether this is the first read operation on the reader. */
	protected boolean firstRead = true;
	/** The wrapped source {@link Reader}. */
	protected Reader sourceReader = null;
	
	/**
	 * Creates a new instance of {@link ShebangSkippingInputStream}.
	 *
	 * @param sourceReader The {@link Reader} that is going to be wrapped,
	 *        cannot be {@code null}.
	 * @throws IllegalArgumentException If the given {@code reader} is
	 *         {@code null}.
	 */
	public ShebangSkippingReader(Reader sourceReader) {
		super();
		
		Verifier.notNull("sourceReader", sourceReader);
		
		this.sourceReader = sourceReader;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws IOException {
		sourceReader.close();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		if (firstRead) {
			skipShebang();
			firstRead = false;
		}
		
		if (bufferLength > 0) {
			for (int index = 0; (index + off) < len && index < bufferLength; index++) {
				cbuf[index + off] = buffer[index];
			}
			
			int bytesWrittenFromBuffer = Math.min(len, bufferLength);
			
			discardBuffer(bytesWrittenFromBuffer);
			
			int read = sourceReader.read(cbuf, bytesWrittenFromBuffer, len - bytesWrittenFromBuffer);
			
			if (read < 0) {
				read = 0;
			}
			
			return read + bytesWrittenFromBuffer;
		}
		
		return sourceReader.read(cbuf, off, len);
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
	 * Skips the Shebang line in the underlying {@link #sourceReader} if there
	 * is any.
	 * 
	 * @throws IOException If reading from the {@link #sourceReader} failed.
	 */
	protected void skipShebang() throws IOException {
		bufferLength = sourceReader.read(buffer);
		
		if (bufferLength < 2) {
			return;
		}
		
		String firstTwoCharacters = new String(new char[] { buffer[0], buffer[1] });
		
		if (firstTwoCharacters.equals(SHEBANG_START)) {
			int readByte = 0;
			
			while ((readByte = sourceReader.read()) >= 0 && readByte != CARRIAGE_RETURN && readByte != LINE_FEED) {
				// Continue to read from the stream and discard anything until
				// we find a newline of some sort.
			}
			
			if (readByte >= 0) {
				buffer[0] = (char)readByte;
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
