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

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.bonsaimind.jluascript.lua.LuaUtil;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class LsFunction extends VarArgFunction {
	public LsFunction() {
		super();
	}
	
	@Override
	public Varargs invoke(Varargs args) {
		Path path = LuaUtil.coerceAsPath(args.arg(1));
		
		try {
			List<String> entries = new ArrayList<>();
			
			try (DirectoryStream<Path> contents = Files.newDirectoryStream(path)) {
				for (Path entry : contents) {
					entries.add(entry.getFileName().toString());
				}
			}
			
			return CoerceJavaToLua.coerce(entries.toArray(new String[entries.size()]));
		} catch (IOException e) {
			throw new LuaError(e);
		}
	}
}
