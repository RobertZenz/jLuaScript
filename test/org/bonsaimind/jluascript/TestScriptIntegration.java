/*
 * Licensed under Creative Commons Zero or as Public Domain.
 */

package org.bonsaimind.jluascript;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.bonsaimind.jluascript.lua.LuaEnvironment;
import org.bonsaimind.jluascript.lua.ScriptExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestScriptIntegration {
	protected LuaEnvironment environment = null;
	
	@BeforeEach
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
			
			Assertions.fail("Error should have been thrown, but was not.");
		} catch (ScriptExecutionException e) {
			StackTraceElement[] stackTrace = e.getStackTrace();
			
			Assertions.assertNotNull(stackTrace);
			Assertions.assertEquals(3, stackTrace.length);
			Assertions.assertEquals("error-include", stackTrace[0].getClassName());
			Assertions.assertEquals("inner", stackTrace[0].getMethodName());
			Assertions.assertEquals("error", stackTrace[1].getClassName());
			Assertions.assertEquals("outer", stackTrace[1].getMethodName());
			Assertions.assertEquals("error", stackTrace[2].getClassName());
			Assertions.assertEquals("onInvoke", stackTrace[2].getMethodName());
		}
		
		try {
			runString("error");
			
			Assertions.fail("Error should have been thrown, but was not.");
		} catch (ScriptExecutionException e) {
			StackTraceElement[] stackTrace = e.getStackTrace();
			
			Assertions.assertNotNull(stackTrace);
			Assertions.assertEquals(3, stackTrace.length);
			Assertions.assertEquals("error-include", stackTrace[0].getClassName());
			Assertions.assertEquals("inner", stackTrace[0].getMethodName());
			Assertions.assertEquals("script", stackTrace[1].getClassName());
			Assertions.assertEquals("outer", stackTrace[1].getMethodName());
			Assertions.assertEquals("script", stackTrace[2].getClassName());
			Assertions.assertEquals("onInvoke", stackTrace[2].getMethodName());
		}
	}
	
	@Test
	public void testExtend() throws Exception {
		runFile("extend");
	}
	
	@Test
	public void testForLoop() throws Exception {
		runFile("for-loop",
				new int[] { 1, 2, 3 },
				new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.TEN });
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
	public void testStaticInstance() throws Exception {
		runFile("static-instance");
	}
	
	@Test
	public void testString() throws Exception {
		runFile("string");
	}
	
	protected void runFile(String scriptName, Object... arguments) throws Exception {
		List<Object> args = null;
		
		if (arguments != null) {
			args = Arrays.asList(arguments);
		}
		
		environment.execute(Paths.get("./test/org/bonsaimind/jluascript/scripts", scriptName + ".jluascript"), args);
	}
	
	protected void runString(String scriptName, Object... arguments) throws Exception {
		List<Object> args = null;
		
		if (arguments != null) {
			args = Arrays.asList(arguments);
		}
		
		Path scriptPath = Paths.get("./test/org/bonsaimind/jluascript/scripts", scriptName + ".jluascript");
		String script = new String(Files.readAllBytes(scriptPath), StandardCharsets.UTF_8);
		
		environment.execute(script, args);
	}
}
