
package org.bonsaimind.jluascript.lua.functions;

import org.bonsaimind.jluascript.javassist.filters.HandleOnlyNonFinalFilter;
import org.bonsaimind.jluascript.javassist.handlers.LuaInvokingMethodHandler;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import javassist.util.proxy.ProxyFactory;

public class ProxyInstanceCreatingFunction extends OneArgFunction {
	protected Class<?> clazz = null;
	protected LuaInvokingMethodHandler defaultMethodHandler = null;
	protected ProxyFactory proxyFactory = null;
	
	public ProxyInstanceCreatingFunction(Class<?> clazz) {
		this(clazz, null);
	}
	
	public ProxyInstanceCreatingFunction(Class<?> clazz, LuaValue defaultLuaFunctions) {
		super();
		
		this.clazz = clazz;
		
		if (defaultLuaFunctions != null) {
			this.defaultMethodHandler = new LuaInvokingMethodHandler(defaultLuaFunctions);
		}
		
		proxyFactory = new ProxyFactory();
		proxyFactory.setFilter(HandleOnlyNonFinalFilter.INSTANCE);
		
		if (clazz.isInterface()) {
			proxyFactory.setSuperclass(Object.class);
			proxyFactory.setInterfaces(new Class<?>[] { clazz });
		} else {
			proxyFactory.setSuperclass(clazz);
		}
	}
	
	@Override
	public LuaValue call(LuaValue luaFunctions) {
		if (luaFunctions.isnil() && defaultMethodHandler == null) {
			throw new LuaError("Table with functions has been expected.");
		}
		
		LuaInvokingMethodHandler methodHandler = defaultMethodHandler;
		
		if (!luaFunctions.isnil()) {
			methodHandler = new LuaInvokingMethodHandler(luaFunctions);
		}
		
		try {
			return CoerceJavaToLua.coerce(proxyFactory.create(
					new Class<?>[0],
					new Object[0],
					methodHandler));
		} catch (Exception e) {
			throw new LuaError(e);
		}
	}
}
