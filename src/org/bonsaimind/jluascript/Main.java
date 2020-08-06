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

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.bonsaimind.jluascript.lua.LuaEnvironment;
import org.bonsaimind.jluascript.lua.ScriptExecutionException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;

public final class Main {
	private Main() {
	}
	
	public static void main(String[] args) {
		Configuration configuration = new Configuration(args);
		
		if (configuration.isPrintHelp() || (configuration.getScript() == null && !configuration.isRepl())) {
			System.out.println("jLuaScript [OPTIONS] SCRIPT [ARGUMENTS...]");
			System.exit(1);
		}
		
		LuaEnvironment environment = new LuaEnvironment();
		
		if (configuration.getScript() != null) {
			runFile(environment, configuration);
		}
		
		if (configuration.isRepl()) {
			runRepl(environment, configuration);
		}
	}
	
	private static final Throwable getFirstCause(Throwable throwable) {
		Throwable firstThrowable = throwable;
		
		while (firstThrowable != null && firstThrowable.getCause() != null) {
			firstThrowable = firstThrowable.getCause();
		}
		
		return firstThrowable;
	}
	
	private static final void printException(ScriptExecutionException e, PrintStream targetPrintStream, Configuration configuration) {
		Throwable cause = getFirstCause(e);
		
		targetPrintStream.println("Error while executing script: " + cause.getClass().getSimpleName() + " - " + cause.getMessage());
		
		for (StackTraceElement stackTraceElement : e.getStackTrace()) {
			if (stackTraceElement.getMethodName().equals("onInvoke")) {
				targetPrintStream.println("    (" + stackTraceElement.getFileName() + ") <script>:" + stackTraceElement.getLineNumber());
			} else {
				targetPrintStream.println("    (" + stackTraceElement.getFileName() + ") " + stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber());
			}
		}
		
		if (configuration.isPrintJavaStackTrace()) {
			targetPrintStream.println();
			e.printStackTrace(targetPrintStream);
		}
	}
	
	private static final void runFile(LuaEnvironment environment, Configuration configuration) {
		try {
			Path scriptPath = Paths.get(configuration.getScript());
			List<String> scriptArguments = configuration.getScriptArguments();
			
			if (!configuration.isNoScriptPathResolve()) {
				scriptPath = scriptPath.toRealPath();
			}
			
			Object returnedObject = environment.execute(scriptPath, scriptArguments);
			
			if (returnedObject != null) {
				System.out.println(returnedObject.toString());
			}
		} catch (ScriptExecutionException e) {
			printException(e, System.err, configuration);
			
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			
			System.exit(1);
		}
	}
	
	private static final void runRepl(LuaEnvironment environment, Configuration configuration) {
		LineReader reader = LineReaderBuilder.builder()
				.build();
		
		boolean running = true;
		
		while (running) {
			try {
				String input = reader.readLine("> ");
				
				if (input != null && !input.trim().isEmpty()) {
					Object returnedObject = environment.execute(input, Collections.emptyList());
					
					if (returnedObject != null) {
						System.out.println(returnedObject.toString());
					}
				}
			} catch (ScriptExecutionException e) {
				System.out.println("ERROR: " + e.getCause().getMessage());
				
				for (StackTraceElement stackTraceElement : e.getStackTrace()) {
					System.out.println("    " + stackTraceElement.toString());
				}
				
				if (configuration.isPrintJavaStackTrace()) {
					System.out.println();
					e.printStackTrace(System.out);
				}
			} catch (UserInterruptException e) {
				// Nothing to do except exiting.
				System.exit(0);
			} catch (Exception e) {
				e.printStackTrace();
				
				System.exit(1);
			}
		}
	}
}
