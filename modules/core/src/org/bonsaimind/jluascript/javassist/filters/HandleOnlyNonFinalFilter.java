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

package org.bonsaimind.jluascript.javassist.filters;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javassist.util.proxy.MethodFilter;

/**
 * The {@link HandleOnlyNonFinalFilter} is a {@link MethodFilter} extension
 * which filters only non-final methods, removing all final ones.
 */
public class HandleOnlyNonFinalFilter implements MethodFilter {
	/** A cached instance for reuse. */
	public static final HandleOnlyNonFinalFilter INSTANCE = new HandleOnlyNonFinalFilter();
	
	/**
	 * Creates a new instance of {@link HandleOnlyNonFinalFilter}.
	 */
	public HandleOnlyNonFinalFilter() {
		super();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHandled(Method method) {
		return !Modifier.isFinal(method.getModifiers());
	}
}
