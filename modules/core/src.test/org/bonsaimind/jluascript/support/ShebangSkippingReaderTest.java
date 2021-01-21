/*
 * Licensed under Creative Commons Zero or as Public Domain.
 */

package org.bonsaimind.jluascript.support;

import java.io.Reader;
import java.io.StringReader;

import org.junit.jupiter.api.Assertions;

public class ShebangSkippingReaderTest extends AbstractShebangSkippingTest {
	@Override
	protected void assertSkipBehavior(String input, String expected) throws Exception {
		try (Reader reader = createFromString(input)) {
			String readValue = readAll(reader);
			
			Assertions.assertEquals(expected, readValue);
		}
	}
	
	protected Reader createFromString(String value) {
		return new ShebangSkippingReader(new StringReader(value));
	}
	
	protected String readAll(Reader reader) throws Exception {
		char[] buffer = new char[256];
		int read = reader.read(buffer);
		
		if (read >= 0) {
			return new String(buffer, 0, read);
		} else {
			return "";
		}
	}
}
