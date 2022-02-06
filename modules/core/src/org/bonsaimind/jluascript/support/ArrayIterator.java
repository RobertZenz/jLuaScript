/*
 * Copyright 2020, Robert 'Bobby' Zenz
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

package org.bonsaimind.jluascript.support;

import java.lang.reflect.Array;
import java.util.Iterator;

import org.bonsaimind.jluascript.utils.Verifier;

/**
 * THe {@link ArrayIterator} is an {@link Iterator} implementation which allows
 * to iterate over a given {@link Object} array.
 */
public class ArrayIterator implements Iterator<Object> {
	/** The array to iterate over. */
	protected Object array = null;
	/** The current index at which the iterator is. */
	protected int index = 0;
	
	/**
	 * Creates a new instance of {@link ArrayIterator}.
	 *
	 * @param array The {@link Object array} to iterate over. Cannot be
	 *        {@code null}, but can be empty.
	 * @throws IllegalArgumentException If the given {@link Object array} is
	 *         {@code null} or not an array.
	 */
	public ArrayIterator(Object array) {
		super();
		
		Verifier.notNull("array", array);
		
		this.array = array;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		return index < Array.getLength(array);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object next() {
		return Array.get(array, index++);
	}
}
