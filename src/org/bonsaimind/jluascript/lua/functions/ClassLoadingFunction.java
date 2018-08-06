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

package org.bonsaimind.jluascript.lua.functions;

import org.bonsaimind.jluascript.lua.LuaUtil;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

public class ClassLoadingFunction extends OneArgFunction {
	protected ClassLoader classLoader = null;
	
	public ClassLoadingFunction(ClassLoader classLoader) {
		super();
		
		this.classLoader = classLoader;
	}
	
	@Override
	public LuaValue call(LuaValue arg) {
		if (!arg.isstring()) {
			throw new LuaError("Expected the class name as parameter.");
		}
		
		String className = arg.tojstring();
		
		if (className == null || className.isEmpty()) {
			throw new LuaError("Expected the class name as parameter.");
		}
		
		try {
			Class<?> clazz = classLoader.loadClass(className);
			LuaValue coercedStaticClass = LuaUtil.coerceStaticObject(clazz);
			
			return coercedStaticClass;
		} catch (ClassNotFoundException e) {
			throw new LuaError("Class <" + arg.tojstring() + "> could not be loaded.");
		}
	}
}
