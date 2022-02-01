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

package org.bonsaimind.jluascript.lua;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

/**
 * The {@link LuaUtil} provides various helper methods for interacting with
 * LuaJ.
 */
public final class LuaUtil {
	/**
	 * No instancing allowed.
	 */
	private LuaUtil() {
		// No instancing allowed.
	}
	
	/**
	 * Adds the coerced {@link Class} to the given {@link LuaValue environment}
	 * through its package path.
	 * 
	 * @param environment The {@link LuaValue environment} to which to add the
	 *        coerced {@link Class}.
	 * @param clazz The {@link Class} which has been coerced.
	 * @param coercedStaticInstance The coerced {@link Class} to add.
	 */
	public static final void addClassByPackage(LuaValue environment, Class<?> clazz, LuaValue coercedStaticInstance) {
		LuaValue previousPackageTable = environment;
		
		for (String packagePart : clazz.getPackage().getName().split("\\.")) {
			LuaValue packageTable = previousPackageTable.get(packagePart);
			
			if (packageTable.isnil()) {
				packageTable = new LuaTable();
				previousPackageTable.set(packagePart, packageTable);
			}
			
			previousPackageTable = packageTable;
		}
		
		previousPackageTable.set(clazz.getSimpleName(), coercedStaticInstance);
	}
	
	/**
	 * Converts the given {@link Varargs} to a combined {@link Path}.
	 * 
	 * @param args The {@link Varargs} to convert.
	 * @return The {@link Path} created, it is empty if {@code args} is empty.
	 */
	public static final Path varargsToPath(Varargs args) {
		Path path = Paths.get("");
		
		for (int index = 1; index <= args.narg(); index++) {
			LuaValue arg = args.arg(index);
			
			if (arg != null && !arg.isnil()) {
				if (arg.isuserdata(Path.class)) {
					path = path.resolve((Path)arg.touserdata());
				} else if (arg.isstring()) {
					path = path.resolve(arg.tojstring());
				} else {
					path = path.resolve(arg.toString());
				}
			}
		}
		
		return path;
	}
}
