
package org.bonsaimind.jluascript.lua.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

import org.bonsaimind.jluascript.lua.LuaEnvironment;
import org.bonsaimind.jluascript.lua.ScriptExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClassSystemIntegrationTests {
	protected LuaEnvironment environment = null;
	
	@BeforeEach
	public void setUp() throws Exception {
		environment = new LuaEnvironment();
	}
	
	@Test
	public void testEnumMethods() throws Exception {
		environment.addToEnvironment("TestEnum", TestEnum.class);
		
		Assertions.assertEquals(TestEnum.class, run("return TestEnum.class"));
		Assertions.assertEquals("ALPHA", run("return TestEnum.ALPHA:toString()"));
		Assertions.assertEquals("ALPHA", run("return TestEnum.ALPHA:name()"));
	}
	
	@Test
	public void testFieldAccess() throws Exception {
		FieldHoldingObject fieldHoldingObject = new FieldHoldingObject();
		
		environment.addToEnvironment("FieldHoldingObject", FieldHoldingObject.class);
		environment.addToEnvironment("fieldHoldingObject", fieldHoldingObject);
		
		Assertions.assertNull(run("return FieldHoldingObject.staticField"));
		Assertions.assertNull(run("return fieldHoldingObject.instanceField"));
		
		FieldHoldingObject.staticField = "newValue";
		fieldHoldingObject.instanceField = "newValue";
		Assertions.assertEquals("newValue", run("return FieldHoldingObject.staticField"));
		Assertions.assertEquals("newValue", run("return fieldHoldingObject.instanceField"));
		
		FieldHoldingObject.staticField = "newValue2";
		fieldHoldingObject.instanceField = "newValue2";
		Assertions.assertEquals("newValue2", run("return FieldHoldingObject.staticField"));
		Assertions.assertEquals("newValue2", run("return fieldHoldingObject.instanceField"));
	}
	
	@Test
	public void testFunctionalInterfaceBridge() throws Exception {
		environment.addToEnvironment("testObject", new StringSupplyingTestObject());
		
		Assertions.assertEquals("ABCDE", run("return testObject:getStringValue(function() return \"ABCDE\" end)"));
	}
	
	@Test
	public void testFunctionalInterfaceBridge2() throws Exception {
		environment.addToEnvironment("list", new ArrayList<>());
		
		run("list:forEach(function(item) print(\"No action required.\") end)");
	}
	
	@Test
	public void testInstanceClassMethod() throws Exception {
		Assertions.assertEquals(Object.class, run("return Object.new():getClass()"));
		Assertions.assertEquals(Object.class.getName(), run("return Object.new():getClass():getName()"));
	}
	
	@Test
	public void testInstanceMethod() throws Exception {
		Assertions.assertEquals("234", run("return String.new(\"12345\"):substring(1, 4)"));
	}
	
	@Test
	public void testInstanceMethodOverloads() throws Exception {
		Assertions.assertEquals("2345", run("return String.new(\"12345\"):substring(1)"));
		Assertions.assertEquals("234", run("return String.new(\"12345\"):substring(1, 4)"));
	}
	
	@Test
	public void testStaticClassField() throws Exception {
		Assertions.assertEquals(Object.class, run("return Object.class"));
		Assertions.assertEquals(Object.class.getName(), run("return Object.class:getName()"));
	}
	
	@Test
	public void testStaticMethod() throws Exception {
		Assertions.assertEquals("12345", run("return String.valueOf(12345)"));
	}
	
	@Test
	public void testVarargsOnlyParameterArrayArgument() throws Exception {
		environment.addToEnvironment("Arrays", Arrays.class);
		
		Assertions.assertArrayEquals(
				new Object[] { "A", "B", "C" },
				(Object[])run("return Arrays.asList(string.split(\"ABC\", \"\")):toArray()"));
	}
	
	@Test
	public void testVarargsOnlyParameterMultipleArguments() throws Exception {
		environment.addToEnvironment("Arrays", Arrays.class);
		
		Assertions.assertArrayEquals(
				new Object[] { "A", "B", "C" },
				(Object[])run("return Arrays.asList(\"A\", \"B\", \"C\"):toArray()"));
	}
	
	@Test
	public void testVarargsOnlyParameterNoArguments() throws Exception {
		environment.addToEnvironment("Arrays", Arrays.class);
		
		Assertions.assertArrayEquals(
				new Object[] {},
				(Object[])run("return Arrays.asList():toArray()"));
	}
	
	@Test
	public void testVarargsOnlyParameterOneArguments() throws Exception {
		environment.addToEnvironment("Arrays", Arrays.class);
		
		Assertions.assertArrayEquals(
				new Object[] { "A" },
				(Object[])run("return Arrays.asList(\"A\"):toArray()"));
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
