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

package org.bonsaimind.jluascript.lua.libs.functions.extensions;

import java.util.Iterator;

import org.bonsaimind.jluascript.lua.libs.functions.AbstractIteratorFunction;
import org.bonsaimind.jluascript.lua.system.Coercer;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class IteratorIPairsFunction extends AbstractIteratorFunction {
	public IteratorIPairsFunction(LuaValue originalIPairsFunction, Coercer coercer) {
		super(originalIPairsFunction, coercer);
	}
	
	@Override
	protected IteratingFunction createIteratingFunction(Varargs args) {
		Iterator<?> iterator = getIterator(args.arg1());
		
		if (iterator != null) {
			return new IndexIteratingFunction(iterator, coercer);
		} else {
			return null;
		}
	}
	
	protected static class IndexIteratingFunction extends IteratingFunction {
		public IndexIteratingFunction(Iterator<?> iterator, Coercer coercer) {
			super(iterator, coercer);
		}
		
		@Override
		protected LuaValue processKey(Object key, Object value) {
			return LuaValue.valueOf(index);
		}
	}
}
