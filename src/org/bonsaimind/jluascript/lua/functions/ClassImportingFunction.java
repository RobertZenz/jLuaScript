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

import org.bonsaimind.jluascript.lua.LuaUtil;
import org.bonsaimind.jluascript.lua.system.Coercer;
import org.luaj.vm2.LuaValue;

public class ClassImportingFunction extends ClassLoadingFunction {
	protected LuaValue environment = null;
	
	public ClassImportingFunction(LuaValue environment, ClassLoader classLoader, Coercer coercer) {
		super(classLoader, coercer);
		
		this.environment = environment;
	}
	
	@Override
	public LuaValue call(LuaValue arg) {
		LuaValue coercedStaticInstance = super.call(arg);
		Class<?> clazz = (Class<?>)coercedStaticInstance.get("class").touserdata(Class.class);
		
		LuaUtil.addStaticInstanceDirect(environment, clazz, coercedStaticInstance);
		LuaUtil.addStaticInstancePackage(environment, clazz, coercedStaticInstance);
		
		return coercedStaticInstance;
	}
}
