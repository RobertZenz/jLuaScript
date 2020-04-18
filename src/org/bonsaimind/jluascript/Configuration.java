/*
 * Copyright 2019, Robert 'Bobby' Zenz
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Configuration {
	protected boolean noScriptPathResolve = false;
	protected boolean printHelp = false;
	protected boolean printJavaStackTrace = false;
	protected boolean repl = false;
	protected String script = null;
	protected List<String> scriptArguments = new ArrayList<>();
	private List<String> readonlyScriptArguments = null;
	
	public Configuration(String... arguments) {
		super();
		
		init(arguments);
	}
	
	public String getScript() {
		return script;
	}
	
	public List<String> getScriptArguments() {
		if (readonlyScriptArguments == null) {
			readonlyScriptArguments = Collections.unmodifiableList(scriptArguments);
		}
		
		return readonlyScriptArguments;
	}
	
	public boolean isNoScriptPathResolve() {
		return noScriptPathResolve;
	}
	
	public boolean isPrintHelp() {
		return printHelp;
	}
	
	public boolean isPrintJavaStackTrace() {
		return printJavaStackTrace;
	}
	
	public boolean isRepl() {
		return repl;
	}
	
	protected void init(String... arguments) {
		if (arguments != null) {
			for (String argument : arguments) {
				if (argument != null && !argument.isEmpty()) {
					if (script == null) {
						if (argument.equals("-h") || argument.equals("--help")) {
							printHelp = true;
						} else if (argument.equals("--no-script-path-resolve")) {
							noScriptPathResolve = true;
						} else if (argument.equals("--print-java-stacktrace")) {
							printJavaStackTrace = true;
						} else if (argument.equals("--repl")) {
							repl = true;
						} else if (script == null) {
							script = argument;
						}
					} else {
						scriptArguments.add(argument);
					}
				}
			}
		}
		
		if (script == null) {
			printHelp = true;
		}
	}
}
