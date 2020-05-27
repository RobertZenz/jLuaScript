/*
 * Copyright 2019, Robert 'Bobby' Zenz
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.bonsaimind.jluascript.lua.system.types.functions;

import org.bonsaimind.jluascript.javassist.filters.HandleOnlyNonFinalFilter;
import org.bonsaimind.jluascript.javassist.handlers.LuaInvokingMethodHandler;
import org.bonsaimind.jluascript.lua.system.Coercer;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import javassist.util.proxy.ProxyFactory;

public class ProxyInstanceCreatingFunction extends OneArgFunction {
	protected Class<?> clazz = null;
	protected Coercer coercer = null;
	protected LuaInvokingMethodHandler defaultMethodHandler = null;
	protected ProxyFactory proxyFactory = null;
	
	public ProxyInstanceCreatingFunction(Class<?> clazz, Coercer coercer) {
		this(clazz, null, coercer);
	}
	
	public ProxyInstanceCreatingFunction(Class<?> clazz, LuaValue defaultLuaFunctions, Coercer coercer) {
		super();
		
		this.clazz = clazz;
		this.coercer = coercer;
		
		if (defaultLuaFunctions != null) {
			this.defaultMethodHandler = new LuaInvokingMethodHandler(defaultLuaFunctions, coercer);
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
			methodHandler = new LuaInvokingMethodHandler(luaFunctions, coercer);
		}
		
		try {
			return coercer.coerceJavaToLua(proxyFactory.create(
					new Class<?>[0],
					new Object[0],
					methodHandler));
		} catch (Exception e) {
			throw new LuaError(e);
		}
	}
}
