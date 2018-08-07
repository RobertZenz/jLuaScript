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

package org.bonsaimind.jluascript.lua.libs;

import org.bonsaimind.jluascript.lua.LuaUtil;
import org.bonsaimind.jluascript.lua.functions.ClassImportingFunction;
import org.bonsaimind.jluascript.lua.functions.ClassLoadingFunction;
import org.bonsaimind.jluascript.lua.functions.JarLoadingFunction;
import org.bonsaimind.jluascript.support.DynamicClassLoader;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public class JLuaScriptLib extends TwoArgFunction {
	protected DynamicClassLoader classLoader = null;
	
	public JLuaScriptLib(DynamicClassLoader classLoader) {
		super();
		
		this.classLoader = classLoader;
	}
	
	@Override
	public LuaValue call(LuaValue modname, LuaValue environment) {
		addDefaultFunctions(environment);
		importDefaults(environment);
		
		return environment;
	}
	
	protected void addDefaultFunctions(LuaValue environment) {
		environment.set("loadClass", new ClassLoadingFunction(classLoader));
		environment.set("loadJar", new JarLoadingFunction(classLoader));
		environment.set("import", new ClassImportingFunction(environment, classLoader));
	}
	
	protected void importClass(LuaValue environment, Class<?> clazz) {
		LuaValue coercedStaticInstance = LuaUtil.coerceStaticIstance(clazz);
		
		LuaUtil.addStaticInstanceDirect(environment, clazz, coercedStaticInstance);
		LuaUtil.addStaticInstancePackage(environment, clazz, coercedStaticInstance);
	}
	
	protected void importDefaults(LuaValue environment) {
		importClass(environment, Byte.class);
		importClass(environment, Character.class);
		importClass(environment, Double.class);
		importClass(environment, Float.class);
		importClass(environment, Integer.class);
		importClass(environment, Long.class);
		importClass(environment, Short.class);
		importClass(environment, String.class);
		importClass(environment, System.class);
	}
}
