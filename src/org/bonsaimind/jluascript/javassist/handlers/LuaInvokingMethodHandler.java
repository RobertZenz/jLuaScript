
package org.bonsaimind.jluascript.javassist.handlers;

import java.lang.reflect.Method;

import org.bonsaimind.jluascript.lua.LuaUtil;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;

import javassist.util.proxy.MethodHandler;

public class LuaInvokingMethodHandler implements MethodHandler {
	protected LuaValue luaFunctions = null;
	
	public LuaInvokingMethodHandler(LuaValue luaFunctions) {
		super();
		
		this.luaFunctions = luaFunctions;
	}
	
	@Override
	public Object invoke(Object instance, Method superMethod, Method thisMethod, Object[] arguments) throws Throwable {
		if (luaFunctions.isfunction()) {
			return CoerceLuaToJava.coerce(luaFunctions.invoke(LuaUtil.coerce(arguments)).arg1(), Object.class);
		} else if (luaFunctions.istable()) {
			LuaValue luaFunction = luaFunctions.get(superMethod.getName());
			
			if (luaFunction.isfunction()) {
				return CoerceLuaToJava.coerce(luaFunction.invoke(LuaUtil.coerce(arguments)).arg1(), Object.class);
			} else if (thisMethod != null) {
				return thisMethod.invoke(instance, arguments);
			}
		}
		
		throw new LuaError("Method <" + superMethod.getName() + "> has not been provided but is required.");
	}
}
