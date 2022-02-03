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

/**
 * The command line parameters as configuration.
 */
public class Configuration {
	/** Whether the path resolving should be disabled. */
	protected boolean noScriptPathResolve = false;
	/** Wether the help text should be printed. */
	protected boolean printHelp = false;
	/** Whether the Java stacktraces should be printed on error. */
	protected boolean printJavaStackTrace = false;
	/** Whether the REPL should be started. */
	protected boolean repl = false;
	/** The script which should be run. */
	protected String script = null;
	/** The arguments to pass to the run script. */
	protected List<String> scriptArguments = new ArrayList<>();
	/** The readonly wrapper of {@link #scriptArguments}. */
	private List<String> readonlyScriptArguments = null;
	
	/**
	 * Creates a new instance of {@link Configuration}.
	 *
	 * @param arguments The {@link String} array with the arguments, can be
	 *        {@code null} or empty.
	 */
	public Configuration(String... arguments) {
		super();
		
		init(arguments);
	}
	
	/**
	 * The script which should be run.
	 * 
	 * @return The path to the script which should be run.
	 */
	public String getScript() {
		return script;
	}
	
	/**
	 * The arguments to pass to the run script.
	 * 
	 * @return The arguments to pass to the run script.
	 */
	public List<String> getScriptArguments() {
		if (readonlyScriptArguments == null) {
			readonlyScriptArguments = Collections.unmodifiableList(scriptArguments);
		}
		
		return readonlyScriptArguments;
	}
	
	/**
	 * Whether the path resolving should be disabled.
	 * 
	 * @return {@code true} if the path resolving should be disabled.
	 */
	public boolean isNoScriptPathResolve() {
		return noScriptPathResolve;
	}
	
	/**
	 * Wether the help text should be printed.
	 * 
	 * @return {@code true} if the help text should be printed.
	 */
	public boolean isPrintHelp() {
		return printHelp;
	}
	
	/**
	 * Whether the Java stacktraces should be printed on error.
	 * 
	 * @return @{code true} if the Java stacktraces should be printed on error.
	 */
	public boolean isPrintJavaStackTrace() {
		return printJavaStackTrace;
	}
	
	/**
	 * Whether the REPL should be started.
	 * 
	 * @return {@code true} if the REPL should be started.
	 */
	public boolean isRepl() {
		return repl;
	}
	
	/**
	 * Parses the given arguments.
	 * 
	 * @param arguments The {@link String} array with the arguments, can be
	 *        {@code null} or empty.
	 */
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
						} else if (argument.startsWith("--")) {
							printHelp = true;
						}
					} else {
						scriptArguments.add(argument);
					}
				}
			}
		}
		
		if (script == null && !repl) {
			printHelp = true;
		}
	}
}
