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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.bonsaimind.jluascript.lua.system.Coercer;

public class InstanceMethodInvokingFunction extends AbstractExecutableInvokingFunction<Method> {
	public InstanceMethodInvokingFunction(Class<?> clazz, String methodName, Coercer coercer) {
		super(clazz, methodName, coercer);
	}
	
	@Override
	protected Object execute(Method executable, List<Object> parameters) throws Exception {
		List<Object> methodParameters = new ArrayList<>(parameters);
		Object instance = methodParameters.remove(0);
		
		return executable.invoke(instance, methodParameters.toArray());
	}
	
	@Override
	protected Method findMatchingExecutable(List<Object> parameters) {
		List<Object> methodParameters = new ArrayList<>(parameters);
		methodParameters.remove(0);
		
		return super.findMatchingExecutable(methodParameters);
	}
	
	@Override
	protected List<Method> initializeExecutableList() {
		List<Method> methods = new ArrayList<>();
		
		for (Method method : clazz.getMethods()) {
			if (!Modifier.isStatic(method.getModifiers())
					&& Modifier.isPublic(method.getModifiers())
					&& method.getName().equals(executableName)) {
				methods.add(method);
			}
		}
		
		return methods;
	}
	
}
