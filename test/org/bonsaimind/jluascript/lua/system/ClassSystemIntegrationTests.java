
package org.bonsaimind.jluascript.lua.system;

import java.util.function.Supplier;

import org.bonsaimind.jluascript.lua.LuaEnvironment;
import org.bonsaimind.jluascript.lua.ScriptExecutionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ClassSystemIntegrationTests {
	protected LuaEnvironment environment = null;
	
	@Before
	public void setUp() throws Exception {
		environment = new LuaEnvironment();
	}
	
	@Test
	public void testFunctionalInterfaceBridge() throws Exception {
		environment.addToEnvironment("testObject", new StringSupplyingTestObject());
		
		Assert.assertEquals("ABCDE", run("return testObject:getStringValue(function() return \"ABCDE\" end)"));
	}
	
	@Test
	public void testInstanceClassMethod() throws Exception {
		Assert.assertEquals(Object.class, run("return Object.new():getClass()"));
		Assert.assertEquals(Object.class.getName(), run("return Object.new():getClass():getName()"));
	}
	
	@Test
	public void testInstanceMethod() throws Exception {
		Assert.assertEquals("234", run("return String.new(\"12345\"):substring(1, 4)"));
	}
	
	@Test
	public void testInstanceMethodOverloads() throws Exception {
		Assert.assertEquals("2345", run("return String.new(\"12345\"):substring(1)"));
		Assert.assertEquals("234", run("return String.new(\"12345\"):substring(1, 4)"));
	}
	
	@Test
	public void testStaticClassField() throws Exception {
		Assert.assertEquals(Object.class, run("return Object.class"));
		Assert.assertEquals(Object.class.getName(), run("return Object.class:getName()"));
	}
	
	@Test
	public void testStaticMethod() throws Exception {
		Assert.assertEquals("12345", run("return String.valueOf(12345)"));
	}
	
	protected Object run(String script) throws ScriptExecutionException {
		return environment.execute(script, null);
	}
	
	public static class StringSupplyingTestObject {
		public StringSupplyingTestObject() {
			super();
		}
		
		public String getStringValue(Supplier<String> supplier) {
			return supplier.get();
		}
	}
}
