/*
 * Licensed under Creative Commons Zero or as Public Domain.
 */

package org.bonsaimind.jluascript;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigurationTests {
	@Test
	public void testEmptyArguments() {
		Configuration configuration = new Configuration();
		
		Assertions.assertTrue(configuration.isPrintHelp());
		Assertions.assertNull(configuration.getScript());
	}
	
	@Test
	public void testNull() {
		Configuration configuration = new Configuration((String[])null);
		
		Assertions.assertTrue(configuration.isPrintHelp());
		Assertions.assertNull(configuration.getScript());
	}
	
	@Test
	public void testScriptParameters() {
		Configuration configuration = new Configuration("--print-java-stacktrace", "script", "arg1", "arg2");
		
		Assertions.assertFalse(configuration.isPrintHelp());
		Assertions.assertTrue(configuration.isPrintJavaStackTrace());
		Assertions.assertEquals("script", configuration.getScript());
		Assertions.assertEquals(2, configuration.getScriptArguments().size());
		Assertions.assertEquals("arg1", configuration.getScriptArguments().get(0));
		Assertions.assertEquals("arg2", configuration.getScriptArguments().get(1));
	}
	
	@Test
	public void testWellFormed() {
		Configuration configuration = new Configuration("script", "--help", "--print-java-stacktrace");
		
		Assertions.assertFalse(configuration.isPrintHelp());
		Assertions.assertFalse(configuration.isPrintJavaStackTrace());
		Assertions.assertEquals("script", configuration.getScript());
		Assertions.assertEquals(2, configuration.getScriptArguments().size());
		Assertions.assertEquals("--help", configuration.getScriptArguments().get(0));
		Assertions.assertEquals("--print-java-stacktrace", configuration.getScriptArguments().get(1));
	}
	
	@Test
	public void testWellFormedWithHelp() {
		Configuration configuration = new Configuration("--print-java-stacktrace", "--help", "script", "arg1", "arg2");
		
		Assertions.assertTrue(configuration.isPrintHelp());
		Assertions.assertTrue(configuration.isPrintJavaStackTrace());
		Assertions.assertEquals("script", configuration.getScript());
		Assertions.assertEquals(2, configuration.getScriptArguments().size());
		Assertions.assertEquals("arg1", configuration.getScriptArguments().get(0));
		Assertions.assertEquals("arg2", configuration.getScriptArguments().get(1));
	}
}
