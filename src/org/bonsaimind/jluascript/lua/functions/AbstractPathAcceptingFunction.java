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

import java.nio.file.Path;
import java.nio.file.Paths;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public abstract class AbstractPathAcceptingFunction extends VarArgFunction {
	protected AbstractPathAcceptingFunction() {
		super();
	}
	
	@Override
	public Varargs invoke(Varargs args) {
		if (args.narg() == 0) {
			throw new LuaError("Expected the path as parameters.");
		}
		
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
		
		return performAction(path);
	}
	
	protected abstract Varargs performAction(Path path);
}
