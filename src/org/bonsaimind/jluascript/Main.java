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

package org.bonsaimind.jluascript;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bonsaimind.jluascript.lua.LuaEnvironment;
import org.bonsaimind.jluascript.lua.ScriptExecutionException;

public final class Main {
	private Main() {
	}
	
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("jLuaScript SCRIPT [ARGUMENTS...]");
			System.exit(1);
		}
		
		List<String> arguments = new ArrayList<>(Arrays.asList(args));
		String script = arguments.remove(0);
		
		LuaEnvironment environment = new LuaEnvironment();
		
		try {
			Object returnedObject = environment.execute(Paths.get(script), arguments);
			
			if (returnedObject != null) {
				System.out.println(returnedObject.toString());
			}
		} catch (ScriptExecutionException e) {
			e.printStackTrace();
			
			System.exit(1);
		}
	}
}
