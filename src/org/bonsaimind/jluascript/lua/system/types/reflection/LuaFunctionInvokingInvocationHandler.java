
package org.bonsaimind.jluascript.lua.system.types.reflection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.bonsaimind.jluascript.lua.system.Coercer;
import org.luaj.vm2.LuaValue;

public class LuaFunctionInvokingInvocationHandler implements InvocationHandler {
	protected Coercer coercer = null;
	protected LuaValue luaFunction = null;
	
	public LuaFunctionInvokingInvocationHandler(LuaValue luaFunction, Coercer coercer) {
		super();
		
		this.luaFunction = luaFunction;
		this.coercer = coercer;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (args != null && args.length > 0) {
			LuaValue[] parameters = new LuaValue[args.length];
			
			for (int index = 0; index < args.length; index++) {
				parameters[index] = coercer.coerceJavaToLua(args[index]);
			}
			
			return coercer.coerceLuaToJava(luaFunction.invoke(LuaValue.varargsOf(parameters)).arg1());
		} else {
			return coercer.coerceLuaToJava(luaFunction.invoke().arg1());
		}
	}
}
