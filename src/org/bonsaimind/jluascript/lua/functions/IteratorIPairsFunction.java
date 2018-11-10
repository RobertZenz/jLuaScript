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

import java.util.Iterator;

import org.luaj.vm2.LuaValue;

public class IteratorIPairsFunction extends AbstractIteratorFunction {
	public IteratorIPairsFunction(LuaValue originalIPairsFunction) {
		super(originalIPairsFunction);
	}
	
	@Override
	protected IteratingFunction createIteratingFunction(Iterator<?> iterator) {
		return new IndexIteratingFunction(iterator);
	}
	
	protected static class IndexIteratingFunction extends IteratingFunction {
		public IndexIteratingFunction(Iterator<?> iterator) {
			super(iterator);
		}
		
		@Override
		protected LuaValue processKey(Object key) {
			return LuaValue.valueOf(index);
		}
	}
}
