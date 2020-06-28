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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bonsaimind.jluascript.lua.system.Coercer;
import org.luaj.vm2.LuaError;

public class InstanceMethodInvokingFunction extends AbstractExecutableInvokingFunction<Method> {
	public InstanceMethodInvokingFunction(Class<?> clazz, List<Method> methods, Coercer coercer) {
		super(clazz, methods, methods.get(0).getName(), coercer);
	}
	
	@Override
	protected Object execute(Method executable, List<Object> parameters) throws Exception {
		if (parameters.size() < 1) {
			throw new LuaError("Expected to invoke instance function, but no instance parameter provided.");
		}
		
		List<Object> methodParameters = new ArrayList<>(parameters);
		Object instance = methodParameters.remove(0);
		
		return executable.invoke(instance, methodParameters.toArray());
	}
	
	@Override
	protected Method findMatchingExecutable(List<Object> parameters) {
		if (parameters.size() < 1) {
			throw new LuaError("Expected to invoke instance function, but no instance parameter provided.");
		}
		
		List<Object> methodParameters = new ArrayList<>(parameters);
		methodParameters.remove(0);
		
		return super.findMatchingExecutable(methodParameters);
	}
	
	@Override
	protected String getRequestedMethodSignature(List<Object> parameters) {
		StringBuilder methodSignature = new StringBuilder();
		methodSignature.append(clazz.getSimpleName())
				.append(":")
				.append(executableName)
				.append("(");
		
		List<Object> methodParameters = new ArrayList<>(parameters);
		methodParameters.remove(0);
		
		for (Object parameter : methodParameters) {
			if (parameter != null) {
				methodSignature.append(parameter.getClass().getName())
						.append(", ");
			} else {
				methodSignature.append("nil, ");
			}
		}
		
		if (!parameters.isEmpty()) {
			methodSignature.delete(methodSignature.length() - 2, methodSignature.length());
		}
		
		methodSignature.append(")");
		
		return methodSignature.toString();
	}
}
