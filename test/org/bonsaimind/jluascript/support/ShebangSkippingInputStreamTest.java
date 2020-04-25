/*
 * Licensed under Creative Commons Zero or as Public Domain.
 */

package org.bonsaimind.jluascript.support;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

public class ShebangSkippingInputStreamTest {
	@Test
	public void testEmptyStream() throws Exception {
		assertSkipBehavior("", "");
	}
	
	@Test
	public void testNoShebangLookALike() throws Exception {
		assertSkipBehavior(
				"This is some\ntest one multiple\nlines.",
				"This is some\ntest one multiple\nlines.");
	}
	
	@Test
	public void testNoShebangString() throws Exception {
		assertSkipBehavior(
				"# This is some\n# test one multiple\nlines.",
				"# This is some\n# test one multiple\nlines.");
	}
	
	@Test
	public void testShebang() throws Exception {
		assertSkipBehavior(
				"#!/usr/bin/env sh\nThis is some text.",
				"\nThis is some text.");
	}
	
	@Test
	public void testShebangOnlyNoNewline() throws Exception {
		assertSkipBehavior(
				"#!/usr/bin/env sh",
				"");
	}
	
	@Test
	public void testShebangOnlyWithNewline() throws Exception {
		assertSkipBehavior(
				"#!/usr/bin/env sh\n",
				"\n");
		assertSkipBehavior(
				"#!/usr/bin/env sh\r",
				"\r");
	}
	
	protected void assertSkipBehavior(String input, String expected) throws Exception {
		try (InputStream stream = createFromString(input)) {
			String readValue = readAll(stream);
			
			Assert.assertEquals(expected, readValue);
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
