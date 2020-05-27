
package org.bonsaimind.jluascript.lua.system.types;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.bonsaimind.jluascript.lua.system.Coercer;
import org.bonsaimind.jluascript.lua.system.types.functions.InstanceMethodInvokingFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class InstanceUserData extends AbstractInterjectingUserData {
	protected Coercer coercer = null;
	
	public InstanceUserData(Object object, Coercer coercer) {
		super(object);
		
		this.coercer = coercer;
	}
	
	protected LuaValue getField(String name) {
		for (Field field : m_instance.getClass().getFields()) {
			if (!Modifier.isStatic(field.getModifiers())
					&& Modifier.isPublic(field.getModifiers())
					&& field.getName().equals(name)) {
				return CoerceJavaToLua.coerce(field);
			}
		}
		
		return null;
	}
	
	protected LuaValue getMethod(String name) {
		List<Method> methods = new ArrayList<>();
		
		for (Method method : m_instance.getClass().getMethods()) {
			if (!Modifier.isStatic(method.getModifiers())
					&& Modifier.isPublic(method.getModifiers())
					&& method.getName().equals(name)) {
				methods.add(method);
			}
		}
		
		if (!methods.isEmpty()) {
			return new InstanceMethodInvokingFunction(
					m_instance.getClass(),
					methods,
					coercer);
		}
		
		return null;
	}
	
	@Override
	protected LuaValue provide(String name) {
		LuaValue luaValue = getField(name);
		
		if (luaValue == null) {
			return luaValue = getMethod(name);
		}
		
		return luaValue;
	}
}
