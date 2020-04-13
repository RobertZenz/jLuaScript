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

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

/**
 * The {@link LuaUtil} provides various helper methods for interacting with
 * LuaJ.
 */
@Deprecated
public final class LuaUtil {
	/**
	 * No instancing allowed.
	 */
	private LuaUtil() {
		// No instancing allowed.
	}
	
	/**
	 * Only adds the coerced {@link Class} to the given {@link LuaValue
	 * environment}.
	 * 
	 * @param environment The {@link LuaValue environment} to which to add the
	 *        coerced {@link Class}.
	 * @param clazz The {@link Class} which has been coerced.
	 * @param coercedStaticInstance The coerced {@link Class} to add.
	 */
	public static final void addStaticInstanceDirect(LuaValue environment, Class<?> clazz, LuaValue coercedStaticInstance) {
		environment.set(clazz.getSimpleName(), coercedStaticInstance);
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
	public static final void addStaticInstancePackage(LuaValue environment, Class<?> clazz, LuaValue coercedStaticInstance) {
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
}
