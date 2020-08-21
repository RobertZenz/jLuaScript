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

public class ArrayIterator implements Iterator<Object> {
	protected Object array = null;
	protected int index = 0;
	
	public ArrayIterator(Object array) {
		super();
		
		this.array = array;
	}
	
	@Override
	public boolean hasNext() {
		return index < Array.getLength(array);
	}
	
	@Override
	public Object next() {
		return Array.get(array, index++);
	}
}
