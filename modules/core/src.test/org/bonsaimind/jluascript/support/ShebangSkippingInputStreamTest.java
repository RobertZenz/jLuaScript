/*
 * Licensed under Creative Commons Zero or as Public Domain.
 */

package org.bonsaimind.jluascript.support;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;

public class ShebangSkippingInputStreamTest extends AbstractShebangSkippingTest {
	@Override
	protected void assertSkipBehavior(String input, String expected) throws Exception {
		try (InputStream stream = createFromString(input)) {
			String readValue = readAll(stream);
			
			Assertions.assertEquals(expected, readValue);
		}
	}
	
	protected InputStream createFromString(String value) {
		return new ShebangSkippingInputStream(
				new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8)),
				StandardCharsets.UTF_8);
	}
	
	protected String readAll(InputStream stream) throws Exception {
		byte[] buffer = new byte[256];
		int read = stream.read(buffer);
		
		if (read >= 0) {
			return new String(buffer, 0, read, StandardCharsets.UTF_8);
		} else {
			return "";
		}
	}
}
