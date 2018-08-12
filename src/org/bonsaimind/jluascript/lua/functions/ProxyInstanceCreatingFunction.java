
package org.bonsaimind.jluascript.lua.functions;

import org.bonsaimind.jluascript.javassist.filters.NonFinalFilter;
import org.bonsaimind.jluascript.javassist.handlers.LuaInvokingMethodHandler;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import javassist.util.proxy.ProxyFactory;

public class ProxyInstanceCreatingFunction extends OneArgFunction {
	protected Class<?> clazz = null;
	protected ProxyFactory proxyFactory = null;
	
	public ProxyInstanceCreatingFunction(Class<?> clazz) {
		super();
		
		this.clazz = clazz;
		
		proxyFactory = new ProxyFactory();
		proxyFactory.setFilter(NonFinalFilter.INSTANCE);
		
		if (clazz.isInterface()) {
			proxyFactory.setSuperclass(Object.class);
			proxyFactory.setInterfaces(new Class<?>[] { clazz });
		} else {
			proxyFactory.setSuperclass(clazz);
		}
	}
	
	@Override
	public LuaValue call(LuaValue luaFunctions) {
		if (luaFunctions == null || luaFunctions.isnil()) {
			throw new LuaError("Table with functions has been expected.");
		}
		
		try {
			return CoerceJavaToLua.coerce(proxyFactory.create(
					new Class<?>[0],
					new Object[0],
					new LuaInvokingMethodHandler(luaFunctions)));
		} catch (Exception e) {
			throw new LuaError(e);
		}
	}
}
