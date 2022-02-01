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
