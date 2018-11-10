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
import java.util.Map;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public abstract class AbstractIteratorFunction extends VarArgFunction {
	protected LuaValue originalIterationFunction = null;
	
	protected AbstractIteratorFunction(LuaValue originalIPairsFunction) {
		super();
		
		this.originalIterationFunction = originalIPairsFunction;
	}
	
	@Override
	public Varargs invoke(Varargs args) {
		Iterator<?> iterator = getIterator(args.arg(1));
		
		if (iterator != null) {
			return varargsOf(new LuaValue[] {
					createIteratingFunction(iterator),
					args.arg(1),
					LuaValue.ZERO
			});
		} else {
			return originalIterationFunction.invoke(args);
		}
	}
	
	protected IteratingFunction createIteratingFunction(Iterator<?> iterator) {
		return new IteratingFunction(iterator);
	}
	
	protected Iterator<?> getIterator(LuaValue luaValue) {
		if (luaValue.isuserdata(Iterable.class)) {
			return ((Iterable<?>)luaValue.touserdata()).iterator();
		} else if (luaValue.isuserdata(Iterator.class)) {
			return (Iterator<?>)luaValue.touserdata();
		} else if (luaValue.isuserdata(Map.class)) {
			return ((Map<?, ?>)luaValue.touserdata()).entrySet().iterator();
		} else {
			return null;
		}
	}
	
	protected static class IteratingFunction extends VarArgFunction {
		protected int index = 0;
		protected Iterator<?> iterator = null;
		
		public IteratingFunction(Iterator<?> iterator) {
			super();
			
			this.iterator = iterator;
		}
		
		@Override
		public Varargs invoke(Varargs args) {
			if (iterator.hasNext()) {
				Object key = Integer.valueOf(++index);
				Object value = iterator.next();
				
				if (value instanceof Map.Entry<?, ?>) {
					Map.Entry<?, ?> entry = (Map.Entry<?, ?>)value;
					
					key = entry.getKey();
					value = entry.getValue();
				}
				
				return varargsOf(new LuaValue[] {
						processKey(key),
						processValue(value) });
			} else {
				return LuaValue.NIL;
			}
		}
		
		protected LuaValue processKey(Object key) {
			return CoerceJavaToLua.coerce(key);
		}
		
		protected LuaValue processValue(Object value) {
			return CoerceJavaToLua.coerce(value);
		}
	}
}
