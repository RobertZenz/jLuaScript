/*
 * Licensed under Creative Commons Zero or as Public Domain.
 */

package org.bonsaimind.jluascript.support;

import org.junit.jupiter.api.Test;

public abstract class AbstractShebangSkippingTest {
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
	
	protected abstract void assertSkipBehavior(String input, String expected) throws Exception;
}
