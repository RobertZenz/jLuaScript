jLuaScript
==========

jLuaScript allows to to write Lua scripts which have full access and control of Java classes. It utilizes LuaJ for executing Lua code. The idea is to be able to write Lua scripts which have complete access to the Java ecosystem and can use the Java classes and instances with a simple syntax.

A similar project is [jLua](https://github.com/nirhal/JLua), which is using a different syntax for accessing the Java classes and also uses a native-compiled Lua engine.


License
-------

jLuaScript is licensed under LGPLv3.


Dependencies
------------

Please see the license files or the Ivy reports for details.

### core

 * [BCEL](https://commons.apache.org/proper/commons-bcel/), Apache
 * [javassist](https://www.javassist.org/), Mozilla Public License
 * [LuaJ](https://sourceforge.net/projects/luaj/), BSD-like

### standalone

 * [JLine3](https://github.com/jline/jline3), BSD 3-clause


Usage
-----

The scripts executed by jLuaScript are Lua scripts which have all the capabilities of Lua scripts but also can access the whole Java ecosystem.

Even though an `import`-like function is used to define classes, it is not necessary to import all implicitly used classes, only those which are used explicitly.

Before a static instance of a class can be used, it must either be loaded or imported. Afterwards the static functions can be accessed through dot syntax. A new instance can be created through the static "new" function. Instance methods can be accessed with the colon syntax.

### Global Functions

#### loadClass(String)

`loadClass(String)` allows to load a single, static instance of the given class.

```lua
local Paths = loadClass("java.nio.file.Paths")
local path = Paths.get("/etc", "apt", "sources.list");
```

#### loadJar(String...)

`loadJar(String...)` allows to add a single jar to the classpath which is being used by the environment. It accepts String varargs for the single path elements,

```lua
loadJar("/opt", "someapp", "lib", "somelib.jar")

import("com.somelib.Function")

Function.execute()
```

#### import(String)

`import(String)` loads and imports a single, static instance of the given class. The static instance is set as global variables with the name of the class, and also the fully qualified name is being made available.

```lua
import("java.nio.file.Paths")

local path = Paths.get("/etc", "apt", "sources.list");
local path = java.nio.file.Paths.get("/etc", "apt", "sources.list");
```

`import(String)` does also return the static instance which just has been
imported.

```lua
local P = import("java.nio.file.Paths");

local path = P.get("/etc", "apt", "sources.list");
```

Note that jars must be loaded before the classes can be imported and that
the order of functions is important because the classpath is extended
dynamically.

#### instanceof(Object, Class)

`instanceof(Object, Class)` allows to check whether the given value is of the given class.

```lua
local value = BigDecimal.new("5")

if instanceof(value, BigDecimal.class) then
    print("This is a BigDecimal.")
end
```

### Global Helper Functions

### dir Function

There is a global `dir` function, which allows to iterate over directories:

```lua
for name, path in dir("/usr/share/") do
    -- name is the name of the entry.
    -- path is a java.nio.Path object.
    print(name)
end
```

### exec/execWait/run

There are global function to run commands directly. All three functions are aliases of each other, they do the exact same thing. They wait for the started process to finish, forward all output and return the exit code.

```lua
exec("ls", "-l")
```

### treeDir Function

There is a global `treedir` function, which allows to iterate over a whole directory tree:

```lua
for name, path in treedir("/usr/share/") do
    -- name is the name of the entry.
    -- path is a java.nio.Path object.
    print(name)
end
```

### Global Variables

#### ARGS

A table which holds all arguments for the script. Can be empty if there are none.

```lua
-- jluascript myscript.lua first second third

print(ARGS[1]) -- "first"
print(ARGS[2]) -- "second"
print(ARGS[3]) -- "third"
```

#### CWD

The current working directory, as it exists in the process environment.

```lua
print(CWD) -- "/some/path/to/dir"
```

#### DIR

The current working directory, as it exists in the process environment.

```lua
print(DIR) -- "/some/path/to/dir"
```

#### HOME

The home directory of the user, as it exists in the process environment.

```lua
print(HOME) -- "/home/user"
```

#### SCRIPT_DIR

A global String variable which contains the directory of the currently being
executed script, absolute and platform-specific. Can be nil if no file is being
executed.

```lua
print(SCRIPT_DIR) -- "/some/path/to/dir-with-script"
```

#### SCRIPT_FILE

A global String variable which contains the file of the currently being executed
script, absolute and platform-specific. Can be nil if no file is being executed.

```lua
print(SCRIPT_FILE) -- "/some/path/to/dir-with-script/script.jluascript"
```

#### WORKING_DIR

The current working directory, as it exists in the process environment.

```lua
print(WORKING_DIR) -- "/some/path/to/dir"
```

### Extended String

The global `string` table has been extended with the following Java functions:

 * `charAt`
 * `codePointAt`
 * `codePointBefore`
 * `codePointCount`
 * `compareTo`
 * `compareToIgnoreCase`
 * `concat`
 * `contains`
 * `contentEquals`
 * `endsWith`
 * `equalsIgnoreCase`
 * `getBytes`
 * `indexOf`
 * `isEmpty`
 * `lastIndexOf`
 * `length`
 * `matches`
 * `offsetByCodePoints`
 * `regionMatches`
 * `replace`
 * `replaceAll`
 * `replaceFirst`
 * `split`
 * `startsWith`
 * `substring`
 * `toCharArray`
 * `toLowerCase`
 * `toUpperCase`
 * `trim`

Please note that these accept the same parameters as in Java, so for example the `substring` function expects an 0-based index.

### Extended pairs/ipairs

The `pairs`/`ipairs` functions have been extended to be able to iterate over `Iterable` and `Iterators` directly. That means that these functions can iterate over any `List` or `Map` by default:

```lua
local list = ArrayList:new()
list:add("A")
list:add("B")
list:add("C")

for index, item in pairs(list) do
    print(item)
end

for index, item in ipairs(list) do
    print(item)
end

local map = HashMap:new()
map:put("A-Key", "A-Value")
map:put("B-Key", "B-Value")
map:put("C-Key", "C-Value")

for key, value in pairs(map) do
    print(key .. " => " .. value)
end

for index, value in ipairs(map) do
    print(value)
end
```

Note that `ipairs` will return an index even for `Map`s, the order of items is still defined by the implementation of the `Map`.

### Embedding

jLuaScript can be run stand-alone and execute given scripts, or it can be embedded into your application.

The main class is the `LuaEnvironment`, it allows you to execute files and scripts directly from a `String`.

```java
LuaEnvironment environment = new LuaEnvironment();

try {
    environment.execute("print(\"Hello World!\")");
} catch(ScriptExecutionException e) {
    e.printStackTrace();
}
```

You can also access returned values from the script:

```java
LuaEnvironment environment = new LuaEnvironment();

try {
    String value = environment.execute("return \"abc\"");
} catch(ScriptExecutionException e) {
    e.printStackTrace();
}
```

If you need to limit the available functionality, you have two options:

 1. You override `LuaEnvironment.loadDefaultLibraries` and only load
    the functionality you want to have available.
 2. You use a custom SecurityManager which limits what classes can be accessed.

Even though `LuaEnvironment` provides you with a way to set a classloader, you
might need to load it already in a confined environment.


Examples
--------

See the "examples" directory and the unit tests for further examples.

The classic "Hello World!" example.

```lua
-- Note that System is one of the classes which are imported by default.
System.out:println("Hello World!")
```

Working with BigDecimals.

```lua
import("java.math.BigDecimal")

local valueA = BigDecimal.new("1.02")
local valueB = BigDecimal.new("5.44")
local result = valueA:add(valueB)

System.out:println(result:toString())
```

One can also access additional jars, for example EvalEx.

```lua
loadJar(SCRIPT_DIR, "evalex.jar")

import("java.math.BigDecimal")
import("com.udojava.evalex.Expression")

local expression = Expression.new("1 + sqrt(17) * 5.433839 + a")
expression:with("a", BigDecimal.new("3.77722"))

local result = expression:eval()

System.out:println(result)
```

Implementing an interface:

```java
import("java.lng.Runnable")

local RunnableImpl = Runnable.implement({
    run = function()
        -- Put logic here.
    end
})

local runnableInstance = RunnableImpl.new()

runnableInstance:run()
```

### Using as a lib

The environment can also be called from other applications:

```java
LuaEnvironment environment = new LuaEnvironment();

// Add a default import.
environment.importClass(SomeImportantClass.class);

// Add a global variable, given object is a Java object.
environment.addToEnvironment("someObject", someObject);

// Execute the actual script from a String.
Object returnedValue = environment.execute(script);
```
