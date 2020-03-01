
package org.bonsaimind.jluascript;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Configuration {
	protected boolean printHelp = false;
	protected boolean printJavaStackTrace = false;
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
	
	public boolean isPrintHelp() {
		return printHelp;
	}
	
	public boolean isPrintJavaStackTrace() {
		return printJavaStackTrace;
	}
	
	protected void init(String... arguments) {
		if (arguments != null) {
			for (String argument : arguments) {
				if (argument != null && !argument.isEmpty()) {
					if (argument.equals("-h") || argument.equals("--help")) {
						printHelp = true;
					} else if (argument.equals("--print-java-stacktrace")) {
						printJavaStackTrace = true;
					} else if (script == null) {
						script = argument;
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
