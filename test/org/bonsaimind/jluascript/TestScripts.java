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
	private LuaEnvironment environment = null;
	
	@Before
	public void setUp() {
		environment = new LuaEnvironment();
	}
	
	@Test
	public void testArguments() {
		testScript("arguments", "aaa", "bbb", "ccc");
	}
	
	@Test
	public void testExtend() {
		testScript("extend");
	}
	
	@Test
	public void testGlobalVariables() {
		testScript("global-variables");
	}
	
	@Test
	public void testImplement() {
		testScript("implement");
	}
	
	@Test
	public void testImport() {
		testScript("import");
	}
	
	@Test
	public void testLoadClass() {
		testScript("load-class");
	}
	
	private void testScript(String scriptName, String... arguments) {
		List<String> args = null;
		
		if (arguments != null) {
			args = Arrays.asList(arguments);
		}
		
		environment.execute(Paths.get("./test/org/bonsaimind/jluascript/scripts", scriptName + ".jluascript"), args);
	}
}
