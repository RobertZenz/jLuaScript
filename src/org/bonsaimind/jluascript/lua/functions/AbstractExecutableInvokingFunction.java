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

import java.lang.reflect.Array;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import org.bonsaimind.jluascript.lua.LuaUtil;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public abstract class AbstractExecutableInvokingFunction<EXECUTABLE extends Executable> extends VarArgFunction {
	protected Class<?> clazz = null;
	protected List<EXECUTABLE> executables = null;
	
	public AbstractExecutableInvokingFunction(Class<?> clazz) {
		super();
		
		this.clazz = clazz;
	}
	
	@Override
	public Varargs invoke(Varargs args) {
		List<Object> parameters = coerceToJavaParameters(args);
		EXECUTABLE executable = findMatchingExecutable(parameters);
		
		if (executable == null) {
			throw new LuaError("No matching method found.");
		}
		
		if (hasVargArgsParameter(executable)) {
			Parameter varArgParameter = executable.getParameters()[executable.getParameterCount() - 1];
			
			if (parameters.size() >= executable.getParameterCount()) {
				
				Object[] varargs = (Object[])Array.newInstance(
						varArgParameter.getType().getComponentType(),
						parameters.size() - executable.getParameterCount() + 1);
				
				for (int index = 0; index < varargs.length; index++) {
					varargs[index] = parameters.remove(executable.getParameterCount() - 1);
				}
				
				parameters.add(varargs);
			} else {
				parameters.add(Array.newInstance(
						varArgParameter.getType().getComponentType(),
						0));
			}
		}
		
		try {
			return CoerceJavaToLua.coerce(execute(executable, parameters));
		} catch (Exception e) {
			throw new LuaError(e);
		}
	}
	
	protected List<Object> coerceToJavaParameters(Varargs args) {
		List<Object> javaParameters = new ArrayList<>();
		
		for (int index = 1; index <= args.narg(); index++) {
			javaParameters.add(LuaUtil.coerceAsJavaObject(args.arg(index)));
		}
		
		return javaParameters;
	}
	
	protected abstract Object execute(EXECUTABLE executable, List<Object> parameters) throws Exception;
	
	protected EXECUTABLE findMatchingExecutable(List<Object> parameters) {
		for (EXECUTABLE executable : getExecutables()) {
			if (isMatching(executable, parameters)) {
				return executable;
			}
		}
		
		return null;
	}
	
	protected List<EXECUTABLE> getExecutables() {
		if (executables == null) {
			executables = initializeExecutableList();
		}
		
		return executables;
	}
	
	protected boolean hasVargArgsParameter(Executable executable) {
		return executable.getParameterCount() > 0
				&& executable.getParameters()[executable.getParameterCount() - 1].isVarArgs();
	}
	
	protected abstract List<EXECUTABLE> initializeExecutableList();
	
	protected boolean isMatching(Class<?> expectedClass, Class<?> clazz) {
		return expectedClass.isAssignableFrom(clazz)
				|| (expectedClass == byte.class && clazz == Byte.class)
				|| (expectedClass == short.class && (clazz == Byte.class
						|| clazz == Short.class))
				|| (expectedClass == int.class && (clazz == Byte.class
						|| clazz == Short.class
						|| clazz == Integer.class))
				|| (expectedClass == long.class && (clazz == Byte.class
						|| clazz == Short.class
						|| clazz == Integer.class
						|| clazz == Long.class))
				|| (expectedClass == float.class && clazz == Float.class)
				|| (expectedClass == double.class && (clazz == Float.class || clazz == Double.class));
	}
	
	protected boolean isMatching(Executable executable, List<Object> parameters) {
		Parameter[] methodParameters = executable.getParameters();
		
		for (int parameterIndex = 0; parameterIndex < methodParameters.length; parameterIndex++) {
			Parameter methodParameter = methodParameters[parameterIndex];
			
			if (methodParameter.isVarArgs()) {
				// Test if the remaining parameters are matching.
				Class<?> methodParameterClass = methodParameter.getType().getComponentType();
				
				for (; parameterIndex < parameters.size(); parameterIndex++) {
					Object parameter = parameters.get(parameterIndex);
					
					if (!methodParameterClass.isAssignableFrom(parameter.getClass())) {
						return false;
					}
				}
			} else {
				if (parameterIndex >= parameters.size()) {
					return false;
				}
				
				Object parameter = parameters.get(parameterIndex);
				
				if (!isMatching(methodParameter.getType(), parameter.getClass())) {
					return false;
				}
			}
		}
		
		return true;
	}
}
