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

package org.bonsaimind.jluascript.lua.system.types.functions;

import java.util.UUID;

import org.bonsaimind.jluascript.lua.system.Coercer;
import org.bonsaimind.jluascript.lua.system.types.StaticUserData;
import org.bonsaimind.jluascript.utils.Verifier;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

/**
 * The {@link ClassCreatingFunction} is an {@link OneArgFunction} extension
 * which creates a new {@link Class} on invocation.
 */
public class ClassCreatingFunction extends OneArgFunction {
	/** The {@link Class} to create a new {@link Class} from. */
	protected Class<?> clazz = null;
	/** The {@link Coercer} to use. */
	protected Coercer coercer = null;
	
	/**
	 * Creates a new instance of {@link ClassCreatingFunction}.
	 *
	 * @param clazz The {@link Class} to use as base for the new
	 *        {@link Class}es, cannot be {@code null}.
	 * @param coercer The {@link Coercer} to use, cannot be {@code null}.
	 * @throws IllegalArgumentException If the given {@code clazz} or
	 *         {@code coercer} is {@code null}.
	 */
	public ClassCreatingFunction(Class<?> clazz, Coercer coercer) {
		super();
		
		Verifier.notNull("clazz", clazz);
		Verifier.notNull("coercer", coercer);
		
		this.clazz = clazz;
		this.coercer = coercer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LuaValue call(LuaValue luaFunctions) {
		if (luaFunctions == null
				|| luaFunctions.isnil()
				|| (!luaFunctions.istable() && !luaFunctions.isfunction())) {
			throw new LuaError("Argument must be a table with functions (or a single function).");
		}
		
		ClassPool classPool = ClassPool.getDefault();
		
		try {
			CtClass classPrototype = classPool.makeClass(createClassName(clazz));
			
			if (clazz.isInterface()) {
				classPrototype.setSuperclass(classPool.get(Object.class.getName()));
				classPrototype.addInterface(classPool.get(clazz.getName()));
			} else {
				classPrototype.setSuperclass(classPool.get(clazz.getName()));
			}
			
			Class<?> createdClass = classPrototype.toClass(clazz.getClassLoader(), clazz.getProtectionDomain());
			
			StaticUserData luaClass = new StaticUserData(createdClass, coercer);
			
			luaClass.putIntoCache(StaticUserData.CONSTRUCTOR_NAME, new ProxyInstanceCreatingFunction(clazz, luaFunctions, coercer));
			
			return luaClass;
		} catch (CannotCompileException | NotFoundException e) {
			throw new LuaError(e);
		}
	}
	
	/**
	 * Creates a new random class anme based on the given {@link Class}.
	 * 
	 * @param clazz The {@link Class} to create the name from.
	 * @return A new random class name for the given {@link Class}.
	 * @throws IllegalArgumentException If the given {@code clazz} is
	 *         {@code null}.
	 */
	protected String createClassName(Class<?> clazz) {
		Verifier.notNull("clazz", clazz);
		
		return clazz.getSimpleName() + "$" + UUID.randomUUID().toString().replace("-", "");
	}
}
