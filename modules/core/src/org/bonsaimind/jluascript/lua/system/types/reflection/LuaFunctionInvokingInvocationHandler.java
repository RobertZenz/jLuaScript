/*
 * Copyright 2020, Robert 'Bobby' Zenz
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

package org.bonsaimind.jluascript.lua.system.types.reflection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.bonsaimind.jluascript.lua.system.Coercer;
import org.bonsaimind.jluascript.utils.Verifier;
import org.luaj.vm2.LuaValue;

/**
 * The {@link LuaFunctionInvokingInvocationHandler} is an
 * {@link InvocationHandler} implementation which forwards the invocation to a
 * {@link LuaValue}.
 */
public class LuaFunctionInvokingInvocationHandler implements InvocationHandler {
	/** The {@link Coercer} to use. */
	protected Coercer coercer = null;
	/** The {@link LuaValue} that is being invoked. */
	protected LuaValue luaFunction = null;
	
	/**
	 * Creates a new instance of {@link LuaFunctionInvokingInvocationHandler}.
	 *
	 * @param luaFunction The {@link LuaValue} that is being invoked, cannot be
	 *        {@code null}.
	 * @param coercer The {@link Coercer} that should be used, cannot be
	 *        {@code null}.
	 * @throws IllegalArgumentException If the given {@code luaFunction} or
	 *         {@code coercer} is {@code null}.
	 */
	public LuaFunctionInvokingInvocationHandler(LuaValue luaFunction, Coercer coercer) {
		super();
		
		Verifier.notNull("luaFunction", luaFunction);
		Verifier.notNull("coercer", coercer);
		
		this.luaFunction = luaFunction;
		this.coercer = coercer;
	}
	
	/**
	 * {@inheritDoc}
	 */
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
