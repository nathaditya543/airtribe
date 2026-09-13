# JVM Basics

## JDK, JRE, and JVM

- **JVM (Java Virtual Machine)** is the program that actually executes Java bytecode. It's what turns a `.class` file into running behavior on your specific operating system and CPU.
- **JRE (Java Runtime Environment)** is the JVM plus the standard library classes (`java.util`, `java.io`, etc.) needed to run compiled Java programs. If all you need to do is *run* Java software, the JRE is enough.
- **JDK (Java Development Kit)** is the JRE plus the development tools — `javac` (the compiler), `javadoc`, debuggers, and so on. You need the JDK to *write and compile* Java code like this project.

So the relationship is: **JDK ⊃ JRE ⊃ JVM** — each one is a superset of the one before it, adding the tools needed for a different job (developing vs. just running).

## What Is Bytecode?

When you compile a `.java` file with `javac`, it isn't turned directly into machine code for your CPU. Instead, it's compiled into **bytecode** — a compact, platform-neutral set of instructions stored in a `.class` file. Bytecode is not tied to Windows, Linux, or any particular processor; it's an intermediate format that only the JVM understands. When you run `java Main`, the JVM reads that bytecode and either interprets it step by step or compiles hot paths to native machine code on the fly (JIT compilation).

## "Write Once, Run Anywhere"

This phrase describes the payoff of the JDK/bytecode/JVM split above: a developer compiles their source code **once** into bytecode, and that same `.class` file can then run on **any** machine that has a JVM for its platform — Windows, macOS, Linux, etc. — without recompiling. The JVM is the piece that's platform-specific (there's a different JVM build for each OS/architecture), while the bytecode it runs stays identical everywhere. That's what let LearnTrack's `.class` files, once compiled on one machine, be handed to a grader on a completely different machine and still run correctly.
