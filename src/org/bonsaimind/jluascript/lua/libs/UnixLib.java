/*
 * Copyright 2019, Robert 'Bobby' Zenz
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

import org.bonsaimind.jluascript.lua.functions.unix.CatFunction;
import org.bonsaimind.jluascript.lua.functions.unix.EchoFunction;
import org.bonsaimind.jluascript.lua.functions.unix.GrepFunction;
import org.bonsaimind.jluascript.lua.functions.unix.LsFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public class UnixLib extends TwoArgFunction {
	public UnixLib() {
		super();
	}
	
	@Override
	public LuaValue call(LuaValue modname, LuaValue environment) {
		environment.set("cat", new CatFunction());
		environment.set("echo", new EchoFunction());
		environment.set("grep", new GrepFunction());
		environment.set("ls", new LsFunction());
		
		return environment;
	}
}
