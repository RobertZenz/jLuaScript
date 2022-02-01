
package org.bonsaimind.jluascript.lua.libs;

import org.bonsaimind.jluascript.lua.LuaEnvironment;
import org.bonsaimind.jluascript.lua.ScriptExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StringExtendingLibTests {
	protected LuaEnvironment environment = null;
	
	@BeforeEach
	public void setUp() throws Exception {
		environment = new LuaEnvironment();
	}
	
	@Test
	public void testOverloads() throws Exception {
		Assertions.assertEquals("2345", run("return string.substring(\"12345\", 1)"));
		Assertions.assertEquals("234", run("return string.substring(\"12345\", 1, 4)"));
	}
	
	protected Object run(String script) throws ScriptExecutionException {
		return environment.execute(script, null);
	}
}
