/*
 * Copyright 2018, Robert 'Bobby' Zenz
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

package org.bonsaimind.jluascript.lua;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.bonsaimind.jluascript.lua.functions.ConstructorInvokingFunction;
import org.bonsaimind.jluascript.lua.functions.StaticMethodInvokingFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public final class LuaUtil {
	
	private LuaUtil() {
		super();
	}
	
	public static final void addStaticInstanceDirect(LuaValue environment, Class<?> clazz, LuaValue coercedStaticInstance) {
		environment.set(clazz.getSimpleName(), coercedStaticInstance);
	}
	
	public static final void addStaticInstancePackage(LuaValue environment, Class<?> clazz, LuaValue coercedStaticInstance) {
		LuaValue previousPackageTable = environment;
		
		for (String packagePart : clazz.getPackage().getName().split("\\.")) {
			LuaValue packageTable = previousPackageTable.get(packagePart);
			
			if (packageTable.isnil()) {
				packageTable = new LuaTable();
				previousPackageTable.set(packagePart, packageTable);
			}
			
			previousPackageTable = packageTable;
		}
		
		previousPackageTable.set(clazz.getSimpleName(), coercedStaticInstance);
	}
	
	public final static Object coerceAsJavaObject(LuaValue luaValue) {
		if (luaValue == null || luaValue.isnil()) {
			return null;
		}
		
		if (luaValue.isboolean()) {
			return Boolean.valueOf(luaValue.toboolean());
		}
		
		if (luaValue.isint()) {
			return Integer.valueOf(luaValue.toint());
		}
		
		if (luaValue.islong()) {
			return Long.valueOf(luaValue.tolong());
		}
		
		if (luaValue.isnumber()) {
			return Double.valueOf(luaValue.todouble());
		}
		
		if (luaValue.isstring()) {
			return luaValue.tojstring();
		}
		
		if (luaValue.isuserdata()) {
			return luaValue.touserdata();
		}
		
		return null;
	}
	
	public final static LuaValue coerceStaticObject(Class<?> clazz) {
		LuaTable staticTable = new LuaTable();
		staticTable.set("class", CoerceJavaToLua.coerce(clazz));
		staticTable.set("new", new ConstructorInvokingFunction(clazz));
		
		for (Field field : clazz.getFields()) {
			if (Modifier.isStatic(field.getModifiers())
					&& Modifier.isPublic(field.getModifiers())) {
				try {
					staticTable.set(field.getName(), CoerceJavaToLua.coerce(field.get(null)));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		for (Method method : clazz.getMethods()) {
			if (Modifier.isStatic(method.getModifiers())
					&& Modifier.isPublic(method.getModifiers())) {
				if (staticTable.get(method.getName()).isnil()) {
					staticTable.set(method.getName(), new StaticMethodInvokingFunction(clazz, method.getName()));
				}
			}
		}
		
		return staticTable;
	}
}