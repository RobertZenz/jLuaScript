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

package org.bonsaimind.jluascript.lua.functions.unix;

import org.bonsaimind.jluascript.lua.LuaUtil;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public class EchoFunction extends VarArgFunction {
	public EchoFunction() {
		super();
	}
	
	@Override
	public Varargs invoke(Varargs args) {
		Object object = LuaUtil.coerceAsJavaObject(args.arg1());
		
		if (object.getClass().isArray()) {
			for (Object item : (Object[])object) {
				System.out.println(item);
			}
		} else {
			System.out.println(object);
		}
		
		return LuaValue.NIL;
	}
}
