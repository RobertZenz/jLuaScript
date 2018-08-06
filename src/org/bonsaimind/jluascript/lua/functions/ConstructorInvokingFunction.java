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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ConstructorInvokingFunction extends AbstractExecutableInvokingFunction<Constructor<?>> {
	public ConstructorInvokingFunction(Class<?> clazz) {
		super(clazz);
	}
	
	@Override
	protected Object execute(Constructor<?> executable, List<Object> parameters) throws Exception {
		return executable.newInstance(parameters.toArray());
	}
	
	@Override
	protected List<Constructor<?>> initializeExecutableList() {
		List<Constructor<?>> constructors = new ArrayList<>();
		
		for (Constructor<?> constructor : clazz.getConstructors()) {
			if (Modifier.isPublic(constructor.getModifiers())) {
				constructors.add(constructor);
			}
		}
		
		return constructors;
	}
	
}
