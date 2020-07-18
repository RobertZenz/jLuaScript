
package org.bonsaimind.jluascript.lua.system;

import java.util.ArrayList;
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
	public void testEnumMethods() throws Exception {
		environment.addToEnvironment("TestEnum", TestEnum.class);
		
		Assert.assertEquals(TestEnum.class, run("return TestEnum.class"));
		Assert.assertEquals("ALPHA", run("return TestEnum.ALPHA:toString()"));
		Assert.assertEquals("ALPHA", run("return TestEnum.ALPHA:name()"));
	}
	
	@Test
	public void testFieldAccess() throws Exception {
		FieldHoldingObject fieldHoldingObject = new FieldHoldingObject();
		
		environment.addToEnvironment("FieldHoldingObject", FieldHoldingObject.class);
		environment.addToEnvironment("fieldHoldingObject", fieldHoldingObject);
		
		Assert.assertNull(run("return FieldHoldingObject.staticField"));
		Assert.assertNull(run("return fieldHoldingObject.instanceField"));
		
		FieldHoldingObject.staticField = "newValue";
		fieldHoldingObject.instanceField = "newValue";
		Assert.assertEquals("newValue", run("return FieldHoldingObject.staticField"));
		Assert.assertEquals("newValue", run("return fieldHoldingObject.instanceField"));
		
		FieldHoldingObject.staticField = "newValue2";
		fieldHoldingObject.instanceField = "newValue2";
		Assert.assertEquals("newValue2", run("return FieldHoldingObject.staticField"));
		Assert.assertEquals("newValue2", run("return fieldHoldingObject.instanceField"));
	}
	
	@Test
	public void testFunctionalInterfaceBridge() throws Exception {
		environment.addToEnvironment("testObject", new StringSupplyingTestObject());
		
		Assert.assertEquals("ABCDE", run("return testObject:getStringValue(function() return \"ABCDE\" end)"));
	}
	
	@Test
	public void testFunctionalInterfaceBridge2() throws Exception {
		environment.addToEnvironment("list", new ArrayList<>());
		
		run("list:forEach(function(item) print(\"No action required.\") end)");
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
	
	public static class FieldHoldingObject {
		public static String staticField = null;
		public String instanceField = null;
		
		public FieldHoldingObject() {
			super();
		}
	}
	
	public static class StringSupplyingTestObject {
		public StringSupplyingTestObject() {
			super();
		}
		
		public String getStringValue(Supplier<String> supplier) {
			return supplier.get();
		}
	}
	
	public enum TestEnum {
		ALPHA, BRAVO, CHARLY;
	}
}
