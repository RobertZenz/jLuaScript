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

package org.bonsaimind.jluascript.lua.libs.functions.extensions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import org.bonsaimind.jluascript.lua.LuaUtil;
import org.bonsaimind.jluascript.lua.system.Coercer;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * The {@link DirFunction} is an {@link AbstractIteratorFunction} extension
 * which allows to iterate over the contents of a single directory on the
 * filesystem.
 */
public class DirFunction extends AbstractIteratorFunction {
	/** Constant to not recurse into sub-directories. */
	protected static final int WALK_DEPTH_NO_RECURSING = 1;
	
	/**
	 * Creates a new instance of {@link DirFunction}.
	 *
	 * @param coercer The {@link Coercer} to use.
	 * @throws IllegalArgumentException If the given {@link Coercer} is
	 *         {@code null}.
	 */
	public DirFunction(Coercer coercer) {
		super(null, coercer);
	}
	
	/**
	 * Converts the given {@link Varargs} into a {@link Path} containing all the
	 * elements.
	 *
	 * @param args The {@link Varargs} to convert..
	 * @return The {@link Path} created.
	 */
	protected Path asPath(Varargs args) {
		return LuaUtil.varargsToPath(args);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IteratingFunction createIteratingFunction(Varargs args) {
		return new PathEntryIteratingFunction(getIterator(args), coercer);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Iterator<?> getIterator(Varargs args) {
		return walk(asPath(args).normalize(), WALK_DEPTH_NO_RECURSING);
	}
	
	/**
	 * Walk over the content of the given {@link Path} up to the given depth.
	 *
	 * @param path The {@link Path} to walk over.
	 * @param depth The depth of the walk.
	 * @return The {@link Iterator} to walk over the contents of the given
	 *         {@link Path}.
	 * @throws LuaError If there was a problem walking over the {@link Path}.
	 */
	protected Iterator<?> walk(Path path, int depth) {
		try {
			return Files.walk(path, depth)
					.filter(entry -> !entry.equals(path))
					.iterator();
		} catch (IOException e) {
			throw new LuaError(e);
		}
	}
	
	/**
	 * The {@link PathEntryIteratingFunction} is an
	 * {@link org.bonsaimind.jluascript.lua.libs.functions.extensions.AbstractIteratorFunction.IteratingFunction}
	 * extension which iterates over the given {@link Iterator} and returns its
	 * values.
	 */
	protected static class PathEntryIteratingFunction extends IteratingFunction {
		/**
		 * Creates a new instance of {@link PathEntryIteratingFunction}.
		 *
		 * @param iterator The {@link Iterator}.
		 * @param coercer The {@link Coercer}.
		 */
		public PathEntryIteratingFunction(Iterator<?> iterator, Coercer coercer) {
			super(iterator, coercer);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected LuaValue processKey(Object key, Object value) {
			return coercer.coerceJavaToLua(((Path)value).getFileName().toString());
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		protected LuaValue processValue(Object value, Object key) {
			return coercer.coerceJavaToLua(value);
		}
	}
}
