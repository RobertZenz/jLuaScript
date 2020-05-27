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

package org.bonsaimind.jluascript.lua.libs.functions.extensions;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import org.bonsaimind.jluascript.lua.libs.functions.AbstractIteratorFunction;
import org.bonsaimind.jluascript.lua.system.Coercer;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class DirFunction extends AbstractIteratorFunction {
	public DirFunction(Coercer coercer) {
		super(null, coercer);
	}
	
	@Override
	protected IteratingFunction createIteratingFunction(Varargs args) {
		return new PathEntryIteratingFunction(getIterator(args.arg1()), coercer);
	}
	
	@Override
	protected Iterator<?> getIterator(LuaValue luaValue) {
		return walk(getPath(luaValue).normalize(), 1);
	}
	
	protected Path getPath(LuaValue luaValue) {
		Object javaValue = coercer.coerceLuaToJava(luaValue);
		
		if (javaValue instanceof Path) {
			return (Path)javaValue;
		} else if (javaValue instanceof File) {
			return ((File)javaValue).toPath();
		} else if (javaValue instanceof String) {
			return Paths.get((String)javaValue);
		} else if (javaValue instanceof URI) {
			return Paths.get((URI)javaValue);
		} else {
			throw new LuaError("Cannot iterator over given value.");
		}
	}
	
	protected Iterator<?> walk(Path path, int depth) {
		try {
			return Files.walk(path, depth)
					.filter(entry -> !entry.equals(path))
					.iterator();
		} catch (IOException e) {
			throw new LuaError(e);
		}
	}
	
	protected static class PathEntryIteratingFunction extends IteratingFunction {
		public PathEntryIteratingFunction(Iterator<?> iterator, Coercer coercer) {
			super(iterator, coercer);
		}
		
		@Override
		protected LuaValue processKey(Object key, Object value) {
			return coercer.coerceJavaToLua(((Path)value).getFileName().toString());
		}
		
		@Override
		protected LuaValue processValue(Object value, Object key) {
			return coercer.coerceJavaToLua(value);
		}
	}
}
