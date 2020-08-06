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

package org.bonsaimind.jluascript.lua.libs;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.bonsaimind.jluascript.lua.system.Coercer;
import org.bonsaimind.jluascript.lua.system.types.functions.InstanceMethodInvokingFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

/**
 * The {@link StringExtendingLib} provides additional functions on the
 * {@code string} class of Lua. It adds most of the {@link String} functions
 * which you would expect from a Java class.
 */
public class StringExtendingLib extends TwoArgFunction {
	protected Coercer coercer = null;
	
	public StringExtendingLib(Coercer coercer) {
		super();
		
		this.coercer = coercer;
	}
	
	@Override
	public LuaValue call(LuaValue modname, LuaValue environment) {
		LuaTable stringTable = (LuaTable)environment.get("string");
		
		extendWith(stringTable, "getBytes");
		extendWith(stringTable, "charAt");
		extendWith(stringTable, "concat");
		extendWith(stringTable, "contains");
		extendWith(stringTable, "contentEquals");
		extendWith(stringTable, "endsWith");
		extendWith(stringTable, "equalsIgnoreCase");
		extendWith(stringTable, "indexOf");
		extendWith(stringTable, "isEmpty");
		extendWith(stringTable, "lastIndexOf");
		extendWith(stringTable, "length");
		extendWith(stringTable, "matches");
		extendWith(stringTable, "replace");
		extendWith(stringTable, "replaceAll");
		extendWith(stringTable, "replaceFirst");
		extendWith(stringTable, "split");
		extendWith(stringTable, "startsWith");
		extendWith(stringTable, "substring");
		extendWith(stringTable, "toLowerCase");
		extendWith(stringTable, "toUpperCase");
		extendWith(stringTable, "trim");
		
		return environment;
	}
	
	protected void extendWith(LuaTable stringTable, String methodName) {
		stringTable.set(methodName, new InstanceMethodInvokingFunction(String.class, getMethods(methodName), coercer));
	}
	
	protected List<Method> getMethods(String name) {
		List<Method> methods = new ArrayList<>();
		
		for (Method method : String.class.getMethods()) {
			if (!Modifier.isStatic(method.getModifiers())
					&& Modifier.isPublic(method.getModifiers())
					&& method.getName().equals(name)) {
				methods.add(method);
			}
		}
		
		return methods;
	}
}
