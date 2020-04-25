
package org.bonsaimind.jluascript.lua.system.coercers;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaValue;

public class DefaultCoercerTests {
	protected DefaultCoercer coercer = null;
	
	@Before
	public void setUp() {
		coercer = new DefaultCoercer();
		
		// Workaround for a dependency problem between LuaValue and its deriving
		// classes. If the static constructor of, say LuaInteger is being called
		// before LuaValue has been loaded, it will fail.
		LuaValue.valueOf(1);
	}
	
	@Test
	public void testCoerceJavaToLuaBoolean() {
		Assert.assertTrue(coercer.coerceJavaToLua(Boolean.TRUE).isboolean());
		Assert.assertTrue(coercer.coerceJavaToLua(Boolean.TRUE).toboolean());
		Assert.assertTrue(coercer.coerceJavaToLua(Boolean.FALSE).isboolean());
		Assert.assertFalse(coercer.coerceJavaToLua(Boolean.FALSE).toboolean());
	}
	
	@Test
	public void testCoerceJavaToLuaInteger() {
		Assert.assertTrue(coercer.coerceJavaToLua(Integer.valueOf(5)).isint());
		Assert.assertTrue(coercer.coerceJavaToLua(Integer.valueOf(5)).islong());
		Assert.assertTrue(coercer.coerceJavaToLua(Integer.valueOf(5)).isnumber());
		Assert.assertEquals(5, coercer.coerceJavaToLua(Integer.valueOf(5)).toint());
	}
	
	@Test
	public void testCoerceJavaToLuaNull() {
		Assert.assertTrue(coercer.coerceJavaToLua(null).isnil());
	}
	
	@Test
	public void testCoerceJavaToLuaObject() {
		Assert.assertTrue(coercer.coerceJavaToLua(new Object()).isuserdata());
		Assert.assertTrue(coercer.coerceJavaToLua(new BigDecimal("5")).isuserdata());
		Assert.assertTrue(coercer.coerceJavaToLua(new BigDecimal("5")).isuserdata(BigDecimal.class));
		Assert.assertEquals(new BigDecimal("5"), coercer.coerceJavaToLua(new BigDecimal("5")).touserdata());
	}
	
	@Test
	public void testCoerceJavaToLuaString() {
		Assert.assertTrue(coercer.coerceJavaToLua("AAA").isstring());
		Assert.assertEquals("AAA", coercer.coerceJavaToLua("AAA").tojstring());
	}
	
	@Test
	public void testCoerceLuaToJavaBoolean() {
		Assert.assertEquals(Boolean.TRUE, coercer.coerceLuaToJava(LuaValue.valueOf(true)));
		Assert.assertEquals(Boolean.FALSE, coercer.coerceLuaToJava(LuaValue.valueOf(false)));
	}
	
	@Test
	public void testCoerceLuaToJavaDouble() {
		Assert.assertEquals(Double.valueOf(5.55), coercer.coerceLuaToJava(LuaValue.valueOf(5.55)));
	}
	
	@Test
	public void testCoerceLuaToJavaInteger() {
		Assert.assertEquals(Integer.valueOf(5), coercer.coerceLuaToJava(LuaInteger.valueOf(5)));
		Assert.assertEquals(Integer.valueOf(5), coercer.coerceLuaToJava(LuaValue.valueOf(5)));
	}
	
	@Test
	public void testCoerceLuaToJavaNil() {
		Assert.assertNull(coercer.coerceLuaToJava(LuaValue.NIL));
	}
	
	@Test
	public void testCoerceLuaToJavaNull() {
		Assert.assertNull(coercer.coerceLuaToJava(null));
	}
	
	@Test
	public void testCoerceLuaToJavaObject() {
		Assert.assertEquals(new BigDecimal("5"), coercer.coerceLuaToJava(LuaValue.userdataOf(new BigDecimal("5"))));
	}
	
	@Test
	public void testCoerceLuaToJavaString() {
		Assert.assertEquals("AAA", coercer.coerceLuaToJava(LuaInteger.valueOf("AAA")));
	}
}
