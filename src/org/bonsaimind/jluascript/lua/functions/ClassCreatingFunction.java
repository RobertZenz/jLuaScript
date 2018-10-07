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

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class ClassCreatingFunction extends OneArgFunction {
	protected Class<?> clazz = null;
	
	public ClassCreatingFunction(Class<?> clazz) {
		super();
		
		this.clazz = clazz;
	}
	
	@Override
	public LuaValue call(LuaValue luaFunctions) {
		if (luaFunctions == null
				|| luaFunctions.isnil()
				|| (!luaFunctions.istable() && !luaFunctions.isfunction())) {
			throw new LuaError("Table with functions (or a single function) has been expected.");
		}
		
		ClassPool classPool = ClassPool.getDefault();
		
		try {
			CtClass classPrototype = classPool.makeClass(clazz.getSimpleName());
			
			if (clazz.isInterface()) {
				classPrototype.setSuperclass(classPool.get(Object.class.getName()));
				classPrototype.addInterface(classPool.get(clazz.getName()));
			} else {
				classPrototype.setSuperclass(classPool.get(clazz.getName()));
			}
			
			Class<?> prototypeClass = classPrototype.toClass(clazz.getClassLoader(), clazz.getProtectionDomain());
			
			LuaValue luaClass = LuaUtil.coerceStaticIstance(prototypeClass);
			
			luaClass.set("new", new ProxyInstanceCreatingFunction(prototypeClass, luaFunctions));
			luaClass.set("__functionsTable", luaFunctions);
			
			return luaClass;
		} catch (NotFoundException | CannotCompileException e) {
			throw new LuaError(e);
		}
	}
}
