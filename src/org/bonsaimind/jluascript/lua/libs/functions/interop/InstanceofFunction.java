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

package org.bonsaimind.jluascript.lua.libs.functions.interop;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

public class InstanceofFunction extends TwoArgFunction {
	public InstanceofFunction() {
		super();
	}
	
	@Override
	public LuaValue call(LuaValue arg1, LuaValue arg2) {
		if (!arg1.isuserdata() || !arg2.isuserdata(Class.class)) {
			throw new LuaError("Expected parameters of type Object and Class.");
		}
		
		Object object = arg1.touserdata();
		Class<?> clazz = (Class<?>)arg2.touserdata(Class.class);
		
		return LuaValue.valueOf(clazz.isAssignableFrom(object.getClass()));
	}
}
