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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import org.bonsaimind.jluascript.lua.LuaUtil;
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
		return new PathEntryIteratingFunction(getIterator(args), coercer);
	}
	
	@Override
	protected Iterator<?> getIterator(Varargs args) {
		return walk(getPath(args).normalize(), 1);
	}
	
	protected Path getPath(Varargs args) {
		return LuaUtil.varargsToPath(args);
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
