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
import org.bonsaimind.jluascript.utils.Verifier;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import javassist.util.proxy.ProxyFactory;

/**
 * The {@link ProxyInstanceCreatingFunction} is an {@link OneArgFunction}
 * extension which allows to createa a proxy instance of a {@link Class}.
 */
public class ProxyInstanceCreatingFunction extends OneArgFunction {
	/** The {@link Class} to create proxy instances for. */
	protected Class<?> clazz = null;
	/** The {@link Coercer} to use. */
	protected Coercer coercer = null;
	/** The default methods to use. */
	protected LuaInvokingMethodHandler defaultMethodHandler = null;
	/** The {@link ProxyFactory} that is being used. */
	protected ProxyFactory proxyFactory = null;
	
	/**
	 * Creates a new instance of {@link ProxyInstanceCreatingFunction}.
	 *
	 * @param clazz The {@link Class} from which to create a proxy instance,
	 *        cannot be {@code null}.
	 * @param coercer The {@link Coercer} to use, cannot be {@code null}.
	 * @throws IllegalArgumentException If the given {@code clazz} or
	 *         {@code coercer} is {@code null}.
	 */
	public ProxyInstanceCreatingFunction(Class<?> clazz, Coercer coercer) {
		this(clazz, null, coercer);
	}
	
	/**
	 * Creates a new instance of {@link ProxyInstanceCreatingFunction}.
	 *
	 * @param clazz The {@link Class} from which to create a proxy instance,
	 *        cannot be {@code null}.
	 * @param defaultLuaFunctions The {@link LuaValue Lua table} containing the
	 *        default functions to invoke.
	 * @param coercer The {@link Coercer} to use, cannot be {@code null}.
	 * @throws IllegalArgumentException If the given {@code clazz} or
	 *         {@code coercer} is {@code null}.
	 */
	public ProxyInstanceCreatingFunction(Class<?> clazz, LuaValue defaultLuaFunctions, Coercer coercer) {
		super();
		
		Verifier.notNull("clazz", clazz);
		Verifier.notNull("coercer", coercer);
		
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LuaValue call(LuaValue luaFunctions) {
		if (luaFunctions == null
				|| (luaFunctions.isnil() && defaultMethodHandler == null)) {
			throw new LuaError("Argument must be a table with functions.");
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
