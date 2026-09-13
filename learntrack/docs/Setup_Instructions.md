# Setup Instructions

## JDK Version Used

This project was developed and tested against **JDK 21** (any JDK 11+ will compile and run it, since no version-specific features beyond `switch` expressions are used).

Check your installed version with:

```bash
java -version
javac -version
```

## Running "Hello World" to Verify the Install

1. Create a file `Hello.java`:

   ```java
   public class Hello {
       public static void main(String[] args) {
           System.out.println("Hello, World!");
       }
   }
   ```

2. Compile it:

   ```bash
   javac Hello.java
   ```

   This produces a `Hello.class` bytecode file in the same folder.

3. Run it:

   ```bash
   java Hello
   ```

   Expected output:

   ```
   Hello, World!
   ```

If you see the greeting printed, the JDK is installed and configured correctly (`javac` on the `PATH`, `JAVA_HOME` set if your tools need it).

## Compiling and Running LearnTrack

See the root `README.md` for the exact commands to compile and run this project.
