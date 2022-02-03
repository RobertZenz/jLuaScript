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
import org.bonsaimind.jluascript.utils.Verifier;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

/**
 * The {@link StringExtendingLib} is a {@link TwoArgFunction} extension which
 * provides additional functions on the {@code string} class of Lua. It adds
 * most of the {@link String} functions which you would expect from a Java
 * class.
 */
public class StringExtendingLib extends TwoArgFunction {
	/** The {@link Coercer} to use. */
	protected Coercer coercer = null;
	
	/**
	 * Creates a new instance of {@link StringExtendingLib}.
	 *
	 * @param coercer The {@link Coercer} to use, cannot be {@code null}.
	 * @throws IllegalArgumentException If the given {@code coercer} is
	 *         {@code null}.
	 */
	public StringExtendingLib(Coercer coercer) {
		super();
		
		Verifier.notNull("coercer", coercer);
		
		this.coercer = coercer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LuaValue call(LuaValue modname, LuaValue environment) {
		LuaTable stringTable = (LuaTable)environment.get("string");
		
		extendWith(stringTable, "charAt");
		extendWith(stringTable, "codePointAt");
		extendWith(stringTable, "codePointBefore");
		extendWith(stringTable, "codePointCount");
		extendWith(stringTable, "compareTo");
		extendWith(stringTable, "compareToIgnoreCase");
		extendWith(stringTable, "concat");
		extendWith(stringTable, "contains");
		extendWith(stringTable, "contentEquals");
		extendWith(stringTable, "endsWith");
		extendWith(stringTable, "equalsIgnoreCase");
		extendWith(stringTable, "getBytes");
		extendWith(stringTable, "indexOf");
		extendWith(stringTable, "isEmpty");
		extendWith(stringTable, "lastIndexOf");
		extendWith(stringTable, "length");
		extendWith(stringTable, "matches");
		extendWith(stringTable, "offsetByCodePoints");
		extendWith(stringTable, "regionMatches");
		extendWith(stringTable, "replace");
		extendWith(stringTable, "replaceAll");
		extendWith(stringTable, "replaceFirst");
		extendWith(stringTable, "split");
		extendWith(stringTable, "startsWith");
		extendWith(stringTable, "substring");
		extendWith(stringTable, "toCharArray");
		extendWith(stringTable, "toLowerCase");
		extendWith(stringTable, "toUpperCase");
		extendWith(stringTable, "trim");
		
		return environment;
	}
	
	/**
	 * Extends the given {@link LuaTable} with the String function(s) with the
	 * given name.
	 * 
	 * @param stringTable The {@link LuaTable} to extend, cannot be
	 *        {@code null}.
	 * @param methodName The name of the function(s), cannot be {@code null} or
	 *        empty.
	 * @throws IllegalArgumentException If the given {@code stringTable} is
	 *         {@code null} or {@code methodName} is {@code null} or empty.
	 */
	protected void extendWith(LuaTable stringTable, String methodName) {
		Verifier.notNull("stringTable", stringTable);
		Verifier.notNullOrEmpty("methodName", methodName);
		
		stringTable.set(methodName, new InstanceMethodInvokingFunction(getMethods(methodName), coercer));
	}
	
	/**
	 * Gets the {@link Method}s with the given name.
	 * 
	 * @param name The name of the method(s), cannot be {@code null} or empty.
	 * @return The {@link List} of {@link Method}s.
	 * @throws IllegalArgumentException If the given {@code name} is
	 *         {@code null} or empty.
	 */
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
