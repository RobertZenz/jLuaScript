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

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.bonsaimind.jluascript.support.DynamicClassLoader;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public class JarLoadingFunction extends VarArgFunction {
	protected DynamicClassLoader classLoader = null;
	
	public JarLoadingFunction(DynamicClassLoader classLoader) {
		super();
		
		this.classLoader = classLoader;
	}
	
	@Override
	public Varargs invoke(Varargs args) {
		if (args.narg() == 0) {
			throw new LuaError("Expected the path as string parameters.");
		}
		
		List<String> elements = new ArrayList<>();
		
		for (int index = 1; index <= args.narg(); index++) {
			if (!args.isstring(index)) {
				throw new LuaError("Only strings are accepted as parameters.");
			}
			
			elements.add(args.tojstring(index));
		}
		
		Path jarPath = Paths.get("", elements.toArray(new String[elements.size()]));
		
		try {
			classLoader.addJar(jarPath.toUri().toURL());
		} catch (MalformedURLException e) {
			throw new LuaError(e);
		}
		
		return LuaValue.NIL;
	}
}
