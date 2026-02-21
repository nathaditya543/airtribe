# JVM Report

## 1. Class Loader
The Class Loader loads `.class` files into JVM memory when needed.

Main responsibilities:
- load class bytecode
- link classes (verification, preparation, resolution)
- initialize static data

Common class loader chain:
- Bootstrap Class Loader: core Java classes (`java.lang.*`, etc.)
- Platform/Extension Class Loader: platform libraries
- Application Class Loader: classes from your app classpath

This is why classes are loaded lazily (on first use), not all at startup.

## 2. Runtime Data Areas

### Heap
- Shared by all threads
- Stores objects and arrays
- Managed by Garbage Collector (GC)

### Stack
- Each thread has its own JVM stack
- Contains stack frames for method calls
- Each frame stores local variables, operand stack, and return info

### Method Area
- Shared memory for class metadata
- Stores runtime constant pool, method data, static variables

### PC Register
- Each thread has its own Program Counter register
- Tracks current bytecode instruction being executed

## 3. Execution Engine
The JVM Execution Engine runs bytecode loaded into memory.

It:
- interprets bytecode instruction-by-instruction
- uses JIT to compile frequently used bytecode into native machine code
- coordinates with GC and runtime services

## 4. JIT Compiler vs Interpreter

### Interpreter
- Executes bytecode line by line
- Fast startup
- Slower for repeated/hot code paths

### JIT Compiler
- Detects hot methods/loops
- Compiles them to native code
- Better long-run performance
- Higher compile overhead initially

In practice, JVM uses both:
- interpreter first
- JIT optimization as the program runs

## 5. "Write Once, Run Anywhere"
Java source compiles to platform-independent bytecode (`.class`).

As long as a target machine has a compatible JVM:
- same bytecode runs on Windows, Linux, macOS, etc.
- no need to recompile for each OS/CPU architecture

This portability is one of Java's core strengths.
