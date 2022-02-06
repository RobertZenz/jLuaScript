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

package org.bonsaimind.jluascript.lua.libs.functions;

import java.nio.file.Path;

import org.bonsaimind.jluascript.lua.LuaUtil;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

/**
 * The {@link AbstractPathAcceptingFunction} is a {@link VarArgFunction}
 * extension which accepts Lua parameters and converts them into a {@link Path}
 * before forwarding to the actual logic.
 */
public abstract class AbstractPathAcceptingFunction extends VarArgFunction {
	/**
	 * Creates a new instance of {@link AbstractPathAcceptingFunction}.
	 */
	protected AbstractPathAcceptingFunction() {
		super();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Varargs invoke(Varargs args) {
		if (args.narg() == 0) {
			throw new LuaError("Expected the path as parameters, but no parameters given.");
		}
		
		return performAction(LuaUtil.varargsToPath(args));
	}
	
	/**
	 * Run the action.
	 * 
	 * @param path The {@link Path} from the parameters, never {@code null}.
	 * @return The value to be returned by the action, should not be
	 *         {@code null} but {@link LuaValue#NIL} instead.
	 */
	protected abstract Varargs performAction(Path path);
}
