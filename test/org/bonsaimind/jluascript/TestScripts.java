/*
 * Licensed under Creative Commons Zero or as Public Domain.
 */

package org.bonsaimind.jluascript;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.bonsaimind.jluascript.lua.LuaEnvironment;
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
		testScript("arguments", "aaa", "bbb", "ccc");
	}
	
	@Test
	public void testExtend() throws Exception {
		testScript("extend");
	}
	
	@Test
	public void testForLoop() throws Exception {
		testScript("for-loop");
	}
	
	@Test
	public void testGlobalVariables() throws Exception {
		testScript("global-variables");
	}
	
	@Test
	public void testImplement() throws Exception {
		testScript("implement");
	}
	
	@Test
	public void testImport() throws Exception {
		testScript("import");
	}
	
	@Test
	public void testLoadClass() throws Exception {
		testScript("load-class");
	}
	
	@Test
	public void testString() throws Exception {
		testScript("string");
	}
	
	@Test
	public void testUnix() throws Exception {
		testScript("unix");
	}
	
	private void testScript(String scriptName, String... arguments) throws Exception {
		List<String> args = null;
		
		if (arguments != null) {
			args = Arrays.asList(arguments);
		}
		
		environment.execute(Paths.get("./test/org/bonsaimind/jluascript/scripts", scriptName + ".jluascript"), args);
	}
}
