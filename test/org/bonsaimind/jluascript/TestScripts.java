/*
 * Licensed under Creative Commons Zero or as Public Domain.
 */

package org.bonsaimind.jluascript;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.bonsaimind.jluascript.lua.LuaEnvironment;
import org.bonsaimind.jluascript.lua.ScriptExecutionException;
import org.junit.Before;
import org.junit.Test;

public class TestScripts {
	private LuaEnvironment environment = null;
	
	@Before
	public void setUp() {
		environment = new LuaEnvironment();
	}
	
	@Test
	public void testArguments() throws ScriptExecutionException {
		testScript("arguments", "aaa", "bbb", "ccc");
	}
	
	@Test
	public void testExtend() throws ScriptExecutionException {
		testScript("extend");
	}
	
	@Test
	public void testForLoop() throws ScriptExecutionException {
		testScript("for-loop");
	}
	
	@Test
	public void testGlobalVariables() throws ScriptExecutionException {
		testScript("global-variables");
	}
	
	@Test
	public void testImplement() throws ScriptExecutionException {
		testScript("implement");
	}
	
	@Test
	public void testImport() throws ScriptExecutionException {
		testScript("import");
	}
	
	@Test
	public void testLoadClass() throws ScriptExecutionException {
		testScript("load-class");
	}
	
	private void testScript(String scriptName, String... arguments) throws ScriptExecutionException {
		List<String> args = null;
		
		if (arguments != null) {
			args = Arrays.asList(arguments);
		}
		
		environment.execute(Paths.get("./test/org/bonsaimind/jluascript/scripts", scriptName + ".jluascript"), args);
	}
}
