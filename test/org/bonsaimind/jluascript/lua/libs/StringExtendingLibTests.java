
package org.bonsaimind.jluascript.lua.libs;

import org.bonsaimind.jluascript.lua.LuaEnvironment;
import org.bonsaimind.jluascript.lua.ScriptExecutionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StringExtendingLibTests {
	protected LuaEnvironment environment = null;
	
	@Before
	public void setUp() throws Exception {
		environment = new LuaEnvironment();
	}
	
	@Test
	public void testOverloads() throws Exception {
		Assert.assertEquals("2345", run("return string.substring(\"12345\", 1)"));
		Assert.assertEquals("234", run("return string.substring(\"12345\", 1, 4)"));
	}
	
	protected Object run(String script) throws ScriptExecutionException {
		return environment.execute(script, null);
	}
}
