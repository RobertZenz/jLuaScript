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

package org.bonsaimind.jluascript.lua.libs;

import org.bonsaimind.jluascript.lua.functions.RunFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

/**
 * The {@link ProcessLib} provides easier to use functions for starting
 * processes.
 */
public class ProcessLib extends TwoArgFunction {
	public ProcessLib() {
		super();
	}
	
	@Override
	public LuaValue call(LuaValue modname, LuaValue environment) {
		environment.set("exec", new RunFunction());
		environment.set("execWait", new RunFunction());
		environment.set("run", new RunFunction());
		
		return environment;
	}
}
