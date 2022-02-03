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

import java.util.Iterator;

import org.bonsaimind.jluascript.lua.system.Coercer;
import org.luaj.vm2.Varargs;

/**
 * The {@link TreeDirFunction} is an {@link DirFunction} extension which allows
 * to iterate over the contents of a directory on the filesystem and recursing
 * into all sub-directories up to an infinite depth.
 */
public class TreeDirFunction extends DirFunction {
	/** Constant to recurse into sub-directories. */
	protected static final int WALK_DEPTH_INFINITE = Integer.MAX_VALUE;
	
	/**
	 * Creates a new instance of {@link TreeDirFunction}.
	 *
	 * @param coercer The {@link Coercer}.
	 * @throws IllegalArgumentException If the given {@link Coercer} is
	 *         {@code null}.
	 */
	public TreeDirFunction(Coercer coercer) {
		super(coercer);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Iterator<?> getIterator(Varargs args) {
		return walk(asPath(args).normalize(), WALK_DEPTH_INFINITE);
	}
}
