/*
 * Licensed under Creative Commons Zero or as Public Domain.
 */

package org.bonsaimind.jluascript.lua.system.coercers;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaValue;

public class DefaultCoercerTests {
	protected DefaultCoercer coercer = null;
	
	@BeforeEach
	public void setUp() {
		coercer = new DefaultCoercer();
		
		// Workaround for a dependency problem between LuaValue and its deriving
		// classes. If the static constructor of, say LuaInteger is being called
		// before LuaValue has been loaded, it will fail.
		LuaValue.valueOf(1);
	}
	
	@Test
	public void testCoerceJavaToLuaBoolean() {
		Assertions.assertTrue(coercer.coerceJavaToLua(Boolean.TRUE).isboolean());
		Assertions.assertTrue(coercer.coerceJavaToLua(Boolean.TRUE).toboolean());
		Assertions.assertTrue(coercer.coerceJavaToLua(Boolean.FALSE).isboolean());
		Assertions.assertFalse(coercer.coerceJavaToLua(Boolean.FALSE).toboolean());
	}
	
	@Test
	public void testCoerceJavaToLuaInteger() {
		Assertions.assertTrue(coercer.coerceJavaToLua(Integer.valueOf(5)).isint());
		Assertions.assertTrue(coercer.coerceJavaToLua(Integer.valueOf(5)).islong());
		Assertions.assertTrue(coercer.coerceJavaToLua(Integer.valueOf(5)).isnumber());
		Assertions.assertEquals(5, coercer.coerceJavaToLua(Integer.valueOf(5)).toint());
	}
	
	@Test
	public void testCoerceJavaToLuaNull() {
		Assertions.assertTrue(coercer.coerceJavaToLua(null).isnil());
	}
	
	@Test
	public void testCoerceJavaToLuaObject() {
		Assertions.assertTrue(coercer.coerceJavaToLua(new Object()).isuserdata());
		Assertions.assertTrue(coercer.coerceJavaToLua(new BigDecimal("5")).isuserdata());
		Assertions.assertTrue(coercer.coerceJavaToLua(new BigDecimal("5")).isuserdata(BigDecimal.class));
		Assertions.assertEquals(new BigDecimal("5"), coercer.coerceJavaToLua(new BigDecimal("5")).touserdata());
	}
	
	@Test
	public void testCoerceJavaToLuaString() {
		Assertions.assertTrue(coercer.coerceJavaToLua("AAA").isstring());
		Assertions.assertEquals("AAA", coercer.coerceJavaToLua("AAA").tojstring());
	}
	
	@Test
	public void testCoerceLuaToJavaBoolean() {
		Assertions.assertEquals(Boolean.TRUE, coercer.coerceLuaToJava(LuaValue.valueOf(true)));
		Assertions.assertEquals(Boolean.FALSE, coercer.coerceLuaToJava(LuaValue.valueOf(false)));
	}
	
	@Test
	public void testCoerceLuaToJavaDouble() {
		Assertions.assertEquals(Double.valueOf(5.55), coercer.coerceLuaToJava(LuaValue.valueOf(5.55)));
	}
	
	@Test
	public void testCoerceLuaToJavaInteger() {
		Assertions.assertEquals(Integer.valueOf(5), coercer.coerceLuaToJava(LuaInteger.valueOf(5)));
		Assertions.assertEquals(Integer.valueOf(5), coercer.coerceLuaToJava(LuaValue.valueOf(5)));
	}
	
	@Test
	public void testCoerceLuaToJavaNil() {
		Assertions.assertNull(coercer.coerceLuaToJava(LuaValue.NIL));
	}
	
	@Test
	public void testCoerceLuaToJavaNull() {
		Assertions.assertNull(coercer.coerceLuaToJava(null));
	}
	
	@Test
	public void testCoerceLuaToJavaObject() {
		Assertions.assertEquals(new BigDecimal("5"), coercer.coerceLuaToJava(LuaValue.userdataOf(new BigDecimal("5"))));
	}
	
	@Test
	public void testCoerceLuaToJavaString() {
		Assertions.assertEquals("AAA", coercer.coerceLuaToJava(LuaInteger.valueOf("AAA")));
	}
}
