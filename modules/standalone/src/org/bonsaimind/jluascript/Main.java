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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.bonsaimind.jluascript.lua.LuaEnvironment;
import org.bonsaimind.jluascript.lua.ScriptExecutionException;
import org.bonsaimind.jluascript.utils.Verifier;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.utils.InputStreamReader;

/**
 * The main entry point.
 */
public final class Main {
	/**
	 * No instantiation allowed.
	 */
	private Main() {
	}
	
	/**
	 * The main entry point.
	 * 
	 * @param args The arguments passed from the command line, should not be
	 *        {@code null}.
	 */
	public static void main(String[] args) {
		Configuration configuration = new Configuration(args);
		
		if (configuration.isPrintHelp() || (configuration.getScript() == null && !configuration.isRepl())) {
			printHelp();
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
	
	/**
	 * Gets the first cause in the exception chain.
	 * 
	 * @param throwable The {@link Throwable} from which to get the cause,
	 *        cannot be {@code null}.
	 * @return The cause of the exception, never {@code null}.
	 * @throws IllegalArgumentException If the given {@code throwable} is
	 *         {@code null}.
	 */
	private static final Throwable getFirstCause(Throwable throwable) {
		Verifier.notNull("throwable", throwable);
		
		Throwable firstThrowable = throwable;
		
		while (firstThrowable != null && firstThrowable.getCause() != null) {
			firstThrowable = firstThrowable.getCause();
		}
		
		return firstThrowable;
	}
	
	/**
	 * Prints the given exception.
	 * 
	 * @param exception The exception that should be printed, cannot be
	 *        {@code null}.
	 * @param targetPrintStream The target, cannot be {@code null}.
	 * @param configuration The current {@link Configuration}, cannot be
	 *        {@code null}.
	 * @throws IllegalArgumentException If the {@code exception} or
	 *         {@code targetPrintStream} or {@code configuration} is
	 *         {@code null}.
	 */
	private static final void printException(ScriptExecutionException exception, PrintStream targetPrintStream, Configuration configuration) {
		Verifier.notNull("exception", exception);
		Verifier.notNull("targetPrintStream", targetPrintStream);
		Verifier.notNull("configuration", configuration);
		
		Throwable cause = getFirstCause(exception);
		
		targetPrintStream.println("Error while executing script: " + cause.getClass().getSimpleName() + " - " + cause.getMessage());
		
		for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
			if (stackTraceElement.getMethodName().equals("onInvoke")) {
				targetPrintStream.println("    (" + stackTraceElement.getFileName() + ") <script>:" + stackTraceElement.getLineNumber());
			} else {
				targetPrintStream.println("    (" + stackTraceElement.getFileName() + ") " + stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber());
			}
		}
		
		if (configuration.isPrintJavaStackTrace()) {
			targetPrintStream.println();
			exception.printStackTrace(targetPrintStream);
		}
	}
	
	/**
	 * Prints the helpt text to {@link System#err}.
	 */
	private static final void printHelp() {
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/org/bonsaimind/jluascript/help.text")))) {
			bufferedReader.lines().forEach(System.out::println);
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}
	
	/**
	 * Runs the file from the {@link Configuration} in the given
	 * {@link LuaEnvironment}.
	 * 
	 * @param environment The {@link LuaEnvironment} for execution, cannot be
	 *        {@code null}.
	 * @param configuration The {@link Configuration} that holds the file to
	 *        execute, cannot be {@code null}.
	 * @throws IllegalArgumentException If the {@code environment} or
	 *         {@code configuration} is {@code null}.
	 */
	private static final void runFile(LuaEnvironment environment, Configuration configuration) {
		Verifier.notNull("environment", environment);
		Verifier.notNull("configuration", configuration);
		
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
	
	/**
	 * Runs the REPL with the given {@link Configuration} in the given
	 * {@link LuaEnvironment}.
	 * 
	 * @param environment The {@link LuaEnvironment} for execution, cannot be
	 *        {@code null}.
	 * @param configuration The {@link Configuration} that holds the file to
	 *        execute, cannot be {@code null}.
	 * @throws IllegalArgumentException If the {@code environment} or
	 *         {@code configuration} is {@code null}.
	 */
	private static final void runRepl(LuaEnvironment environment, Configuration configuration) {
		Verifier.notNull("environment", environment);
		Verifier.notNull("configuration", configuration);
		
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
