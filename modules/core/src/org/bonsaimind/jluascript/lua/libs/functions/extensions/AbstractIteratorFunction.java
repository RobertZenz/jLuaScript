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
import java.util.Map;

import org.bonsaimind.jluascript.lua.system.Coercer;
import org.bonsaimind.jluascript.lua.system.types.ArrayUserData;
import org.bonsaimind.jluascript.support.ArrayIterator;
import org.bonsaimind.jluascript.utils.Verifier;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

/**
 * The {@link AbstractIteratorFunction} is a {@link VarArgFunction} which
 * provides the base for creating iterators which can be used by a Lua for-loop.
 */
public abstract class AbstractIteratorFunction extends VarArgFunction {
	/** The {@link Coercer} to use. */
	protected Coercer coercer = null;
	/**
	 * The {@link LuaValue original iterating function} which is being extended
	 * by this class.
	 */
	protected LuaValue originalIterationFunction = null;
	
	/**
	 * Creates a new instance of {@link AbstractIteratorFunction}.
	 *
	 * @param originalIPairsFunction The {@link LuaValue original iterating
	 *        function}. Can be {@code null} if there is none.
	 * @param coercer The {@link Coercer} to use. Cannot be {@code null}.
	 * @throws IllegalArgumentException If the given {@link Coercer} is
	 *         {@code null}.
	 */
	protected AbstractIteratorFunction(LuaValue originalIPairsFunction, Coercer coercer) {
		super();
		
		Verifier.notNull("coercer", coercer);
		
		this.originalIterationFunction = originalIPairsFunction;
		this.coercer = coercer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Varargs invoke(Varargs args) {
		IteratingFunction iteratingFunction = createIteratingFunction(args);
		
		if (iteratingFunction != null) {
			return varargsOf(new LuaValue[] {
					iteratingFunction,
					LuaValue.NIL,
					LuaValue.ZERO
			});
		} else if (originalIterationFunction != null) {
			return originalIterationFunction.invoke(args);
		} else {
			throw new LuaError("Cannot iterate over given value.");
		}
	}
	
	/**
	 * Creates the {@link IteratingFunction} for the given arguments.
	 * 
	 * @param args The arguments to use for the creation.
	 * @return The {@link IteratingFunction} for the given arguments, may be
	 *         {@code null} if one cannot iterate over the given arguments.
	 */
	protected IteratingFunction createIteratingFunction(Varargs args) {
		Iterator<?> iterator = getIterator(args.arg1());
		
		if (iterator != null) {
			return new IteratingFunction(iterator, coercer);
		} else {
			return null;
		}
	}
	
	/**
	 * Creates the {@link Iterator} for the given arguments.
	 * 
	 * @param args The arguments to use for the creation.
	 * @return The {@link Iterator} for the given arguments, may be {@code null}
	 *         if the arguments cannto be iterated over.
	 */
	protected Iterator<?> getIterator(Varargs args) {
		LuaValue luaValue = args.arg1();
		
		if (luaValue == null || luaValue.isnil()) {
			return null;
		} else if (luaValue instanceof ArrayUserData) {
			ArrayUserData arrayUserData = (ArrayUserData)luaValue;
			
			return new ArrayIterator(arrayUserData.getArray());
		} else if (luaValue.isuserdata(Iterable.class)) {
			return ((Iterable<?>)luaValue.touserdata()).iterator();
		} else if (luaValue.isuserdata(Iterator.class)) {
			return (Iterator<?>)luaValue.touserdata();
		} else if (luaValue.isuserdata(Map.class)) {
			return ((Map<?, ?>)luaValue.touserdata()).entrySet().iterator();
		} else {
			return null;
		}
	}
	
	/**
	 * The {@link IteratingFunction} is a {@link VarArgFunction} extension which
	 * allows to iterate over {@link Iterator}s with the default {@code for}
	 * loop.
	 */
	protected static class IteratingFunction extends VarArgFunction {
		/** The {@link Coercer} to use. */
		protected Coercer coercer = null;
		/** The current index at which the iteration is. */
		protected int index = 0;
		/** The {@link Iterator} to use. */
		protected Iterator<?> iterator = null;
		
		/**
		 * Creates a new instance of {@link IteratingFunction}.
		 *
		 * @param iterator The {@link Iterator} to use, cannot be {@code null}.
		 * @param coercer The {@link Coercer} to use, cannot be @{code null}.
		 * @throws IllegalArgumentException If the given {@code iterator} or
		 *         {@code coercer} is {@code null}.
		 */
		public IteratingFunction(Iterator<?> iterator, Coercer coercer) {
			super();
			
			Verifier.notNull("iterator", iterator);
			Verifier.notNull("coercer", coercer);
			
			this.iterator = iterator;
			this.coercer = coercer;
		}
		
		/**
		 * {@inheritDoc}
		 */
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
						processKey(key, value),
						processValue(value, key) });
			} else {
				return LuaValue.NIL;
			}
		}
		
		/**
		 * Coerces the given key into a Lua value.
		 * 
		 * @param key The key to coerce.
		 * @param value The acompanying value.
		 * @return The coerced key.
		 */
		protected LuaValue processKey(Object key, Object value) {
			return coercer.coerceJavaToLua(key);
		}
		
		/**
		 * Coerces the given key into a Lua value.
		 * 
		 * @param value The value to coerce.
		 * @param key The acompanying key.
		 * @return The coerced value.
		 */
		protected LuaValue processValue(Object value, Object key) {
			return coercer.coerceJavaToLua(value);
		}
	}
}
