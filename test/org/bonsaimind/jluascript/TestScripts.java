/*
 * Licensed under Creative Commons Zero or as Public Domain.
 */

package org.bonsaimind.jluascript;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.bonsaimind.jluascript.lua.LuaEnvironment;
import org.bonsaimind.jluascript.lua.ScriptExecutionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestScripts {
	protected LuaEnvironment environment = null;
	
	@Before
	public void setUp() throws Exception {
		environment = new LuaEnvironment();
	}
	
	@Test
	public void testArguments() throws Exception {
		runFile("arguments", "aaa", "bbb", "ccc");
	}
	
	@Test
	public void testError() throws Exception {
		try {
			runFile("error");
			
			Assert.fail();
		} catch (ScriptExecutionException e) {
			StackTraceElement[] stackTrace = e.getStackTrace();
			
			Assert.assertNotNull(stackTrace);
			Assert.assertEquals(3, stackTrace.length);
			Assert.assertEquals("error-include", stackTrace[0].getClassName());
			Assert.assertEquals("inner", stackTrace[0].getMethodName());
			Assert.assertEquals("error", stackTrace[1].getClassName());
			Assert.assertEquals("outer", stackTrace[1].getMethodName());
			Assert.assertEquals("error", stackTrace[2].getClassName());
			Assert.assertEquals("onInvoke", stackTrace[2].getMethodName());
		}
		
		try {
			runString("error");
			
			Assert.fail();
		} catch (ScriptExecutionException e) {
			StackTraceElement[] stackTrace = e.getStackTrace();
			
			Assert.assertNotNull(stackTrace);
			Assert.assertEquals(3, stackTrace.length);
			Assert.assertEquals("error-include", stackTrace[0].getClassName());
			Assert.assertEquals("inner", stackTrace[0].getMethodName());
			Assert.assertEquals("script", stackTrace[1].getClassName());
			Assert.assertEquals("outer", stackTrace[1].getMethodName());
			Assert.assertEquals("script", stackTrace[2].getClassName());
			Assert.assertEquals("onInvoke", stackTrace[2].getMethodName());
		}
	}
	
	@Test
	public void testExtend() throws Exception {
		runFile("extend");
	}
	
	@Test
	public void testForLoop() throws Exception {
		runFile("for-loop");
	}
	
	@Test
	public void testGlobalVariables() throws Exception {
		runFile("global-variables");
	}
	
	@Test
	public void testImplement() throws Exception {
		runFile("implement");
	}
	
	@Test
	public void testImport() throws Exception {
		runFile("import");
	}
	
	@Test
	public void testLoadClass() throws Exception {
		runFile("load-class");
	}
	
	@Test
	public void testString() throws Exception {
		runFile("string");
	}
	
	protected void runFile(String scriptName, String... arguments) throws Exception {
		List<String> args = null;
		
		if (arguments != null) {
			args = Arrays.asList(arguments);
		}
		
		environment.execute(Paths.get("./test/org/bonsaimind/jluascript/scripts", scriptName + ".jluascript"), args);
	}
	
	protected void runString(String scriptName, String... arguments) throws Exception {
		List<String> args = null;
		
		if (arguments != null) {
			args = Arrays.asList(arguments);
		}
		
		Path scriptPath = Paths.get("./test/org/bonsaimind/jluascript/scripts", scriptName + ".jluascript");
		String script = new String(Files.readAllBytes(scriptPath), StandardCharsets.UTF_8);
		
		environment.execute(script, args);
	}
}
