
package org.bonsaimind.jluascript.javassist.handlers;

import java.lang.reflect.Method;

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
		LuaValue luaFunction = luaFunctions.get(superMethod.getName());
		
		if (luaFunction.isfunction()) {
			return CoerceLuaToJava.coerce(luaFunction.invoke().arg1(), Object.class);
		} else if (thisMethod != null) {
			return thisMethod.invoke(instance, arguments);
		} else {
			throw new LuaError("Method <" + superMethod.getName() + "> has not been provided but is required.");
		}
	}
}
