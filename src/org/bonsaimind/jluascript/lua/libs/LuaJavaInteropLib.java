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

import org.bonsaimind.jluascript.lua.functions.IteratorIPairsFunction;
import org.bonsaimind.jluascript.lua.functions.IteratorPairsFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

/**
 * The {@link LuaJavaInteropLib} provides interoperability methods and extends
 * existing ones to make it easier to write Lua code interacting with Java
 * objects.
 */
public class LuaJavaInteropLib extends TwoArgFunction {
	public LuaJavaInteropLib() {
		super();
	}
	
	@Override
	public LuaValue call(LuaValue modname, LuaValue environment) {
		environment.set("ipairs", new IteratorIPairsFunction(environment.get("ipairs")));
		environment.set("pairs", new IteratorPairsFunction(environment.get("pairs")));
		
		return environment;
	}
}
