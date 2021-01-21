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
import java.util.List;

import org.bonsaimind.jluascript.lua.system.Coercer;

public class StaticMethodInvokingFunction extends AbstractExecutableInvokingFunction<Method> {
	public StaticMethodInvokingFunction(Class<?> clazz, List<Method> methods, Coercer coercer) {
		super(clazz, methods, methods.get(0).getName(), coercer);
	}
	
	@Override
	protected Object execute(Method executable, List<Object> parameters) throws Exception {
		return executable.invoke(null, parameters.toArray());
	}
}
