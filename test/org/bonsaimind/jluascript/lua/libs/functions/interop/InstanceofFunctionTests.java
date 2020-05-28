
package org.bonsaimind.jluascript.lua.libs.functions.interop;

import java.math.BigDecimal;

import org.bonsaimind.jluascript.lua.LuaEnvironment;
import org.bonsaimind.jluascript.lua.ScriptExecutionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InstanceofFunctionTests {
	protected LuaEnvironment environment = null;
	
	@Before
	public void setUp() throws Exception {
		environment = new LuaEnvironment();
	}
	
	@Test
	public void testFunction() throws Exception {
		environment.importClass(BigDecimal.class);
		
		Assert.assertEquals(Boolean.TRUE, run("return instanceof(Object.new(), Object.class)"));
		Assert.assertEquals(Boolean.TRUE, run("return instanceof(BigDecimal.new(0), Object.class)"));
		
		Assert.assertEquals(Boolean.FALSE, run("return instanceof(Object.new(), BigDecimal.class)"));
	}
	
	protected Object run(String script) throws ScriptExecutionException {
		return environment.execute(script, null);
	}
}
