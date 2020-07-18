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

public class ShebangSkippingInputStream extends InputStream {
	protected static final int CARRIAGE_RETURN = 0x0d;
	protected static final int LINE_FEED = 0x0a;
	protected static final String SHEBANG_START = "#!";
	
	protected int[] buffer = new int[2];
	protected int bufferLength = 0;
	protected Charset charset = null;
	protected boolean firstRead = true;
	protected InputStream sourceStream = null;
	
	public ShebangSkippingInputStream(InputStream sourceStream, Charset charset) {
		super();
		
		this.sourceStream = sourceStream;
		this.charset = charset;
	}
	
	@Override
	public int available() throws IOException {
		return sourceStream.available();
	}
	
	@Override
	public void close() throws IOException {
		sourceStream.close();
	}
	
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
	
	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (firstRead) {
			skipShebang();
			firstRead = false;
		}
		
		if (bufferLength > 0) {
			for (int index = 0; (index + off) < len && index < bufferLength; index++) {
				b[index + off] = (byte)buffer[index];
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
	
	@Override
	public long skip(long n) throws IOException {
		if (firstRead) {
			skipShebang();
			firstRead = false;
		}
		
		discardBuffer((int)n);
		
		return sourceStream.skip(n - bufferLength);
	}
	
	protected void discardBuffer(int countToDiscard) {
		for (int index = 0; index < bufferLength - countToDiscard; index++) {
			buffer[index] = buffer[index + countToDiscard];
		}
		
		bufferLength = Math.max(0, bufferLength - countToDiscard);
	}
	
	protected void skipShebang() throws IOException {
		buffer[0] = sourceStream.read();
		buffer[1] = sourceStream.read();
		
		if (buffer[0] <= -1) {
			bufferLength = 0;
			
			return;
		}
		
		if (buffer[1] <= -1) {
			bufferLength = 1;
			
			return;
		}
		
		String firstTwoCharacters = new String(
				new byte[] { (byte)buffer[0], (byte)buffer[1] },
				charset);
		
		if (firstTwoCharacters.equals(SHEBANG_START)) {
			int read = 0;
			
			while ((read = sourceStream.read()) >= 0 && read != CARRIAGE_RETURN && read != LINE_FEED) {
				// Continue to read from the stream and discard anything until
				// we find a newline of some sort.
			}
			
			if (read >= 0) {
				buffer[0] = read;
				bufferLength = 1;
			} else {
				// We've exhausted the stream trying to find a newline...tough
				// luck I'm afraid.
			}
		} else {
			bufferLength = 2;
		}
	}
}
