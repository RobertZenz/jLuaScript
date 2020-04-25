/*
 * Licensed under Creative Commons Zero or as Public Domain.
 */

package org.bonsaimind.jluascript;

import org.junit.Assert;
import org.junit.Test;

public class ConfigurationTests {
	@Test
	public void testEmptyArguments() {
		Configuration configuration = new Configuration();
		
		Assert.assertTrue(configuration.isPrintHelp());
		Assert.assertNull(configuration.getScript());
	}
	
	@Test
	public void testNull() {
		Configuration configuration = new Configuration((String[])null);
		
		Assert.assertTrue(configuration.isPrintHelp());
		Assert.assertNull(configuration.getScript());
	}
	
	@Test
	public void testScriptParameters() {
		Configuration configuration = new Configuration("--print-java-stacktrace", "script", "arg1", "arg2");
		
		Assert.assertFalse(configuration.isPrintHelp());
		Assert.assertTrue(configuration.isPrintJavaStackTrace());
		Assert.assertEquals("script", configuration.getScript());
		Assert.assertEquals(2, configuration.getScriptArguments().size());
		Assert.assertEquals("arg1", configuration.getScriptArguments().get(0));
		Assert.assertEquals("arg2", configuration.getScriptArguments().get(1));
	}
	
	@Test
	public void testWellFormed() {
		Configuration configuration = new Configuration("script", "--help", "--print-java-stacktrace");
		
		Assert.assertFalse(configuration.isPrintHelp());
		Assert.assertFalse(configuration.isPrintJavaStackTrace());
		Assert.assertEquals("script", configuration.getScript());
		Assert.assertEquals(2, configuration.getScriptArguments().size());
		Assert.assertEquals("--help", configuration.getScriptArguments().get(0));
		Assert.assertEquals("--print-java-stacktrace", configuration.getScriptArguments().get(1));
	}
	
	@Test
	public void testWellFormedWithHelp() {
		Configuration configuration = new Configuration("--print-java-stacktrace", "--help", "script", "arg1", "arg2");
		
		Assert.assertTrue(configuration.isPrintHelp());
		Assert.assertTrue(configuration.isPrintJavaStackTrace());
		Assert.assertEquals("script", configuration.getScript());
		Assert.assertEquals(2, configuration.getScriptArguments().size());
		Assert.assertEquals("arg1", configuration.getScriptArguments().get(0));
		Assert.assertEquals("arg2", configuration.getScriptArguments().get(1));
	}
}
