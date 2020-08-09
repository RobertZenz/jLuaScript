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

package org.bonsaimind.jluascript.lua.system.types;

import java.lang.reflect.Array;

import org.bonsaimind.jluascript.lua.system.Coercer;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;

public class ArrayUserData extends LuaUserdata {
	protected Coercer coercer = null;
	
	public ArrayUserData(Object array, Coercer coercer) {
		super(array);
		
		this.coercer = coercer;
	}
	
	@Override
	public LuaValue get(LuaValue key) {
		if (key.equals("length")) {
			return LuaValue.valueOf(Array.getLength(m_instance));
		} else if (key.isint()) {
			int index = key.toint() - 1;
			int length = Array.getLength(m_instance);
			
			if (index >= 0 && index < length) {
				return coercer.coerceJavaToLua(Array.get(m_instance, index));
			}
		}
		
		return LuaValue.NIL;
	}
}
