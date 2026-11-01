# Simulated Operating System

A Java simulation of an operating system for CSEN 602 (Operating Systems, Spring 2023) at the German University in Cairo. It reads programs from text files, runs them as processes, and manages memory, mutexes and scheduling. The full brief is in [OS_Project_2023.pdf](OS_Project_2023.pdf).

> **Status: incomplete.** The code doesn't compile yet and has no entry point, so it can't be run. See [Current state](#current-state).

## Requirements

- JDK 14 or later (the code uses multi-label `switch` cases; the IntelliJ project is set to JDK 16)
- A full JDK rather than a JRE, because `SystemCall.java` imports from `jdk.jshell`

## Project layout

```
OS_Project_2023.pdf        Project brief
Program_1..3.txt           Sample programs (same as the copies in inputfiles/)
Project/untitled/          IntelliJ project
  inputfiles/              Programs loaded by the simulator
  outputfiles/             Empty; not used by the code yet
  src/
    Interpreter.java       Creates the OS components; program loading is a stub
    Memory.java            40-word memory and process creation
    Mutex.java             semWait / semSignal
    PCB.java               Process control block
    Process.java           A PCB plus three variable words
    ProcessState.java      NEW, READY, RUNNING, BLOCKED, FINISHED
    Scheduler.java         Ready and blocked queues; scheduling loop is commented out
    SystemCall.java        File, screen and user input operations
    Value.java             Mutex value (Zero, One)
    Word.java              One memory word: a name and its data
```

## Building

Run this from `Project/untitled`, because programs are loaded from the relative path `inputfiles/`:

```sh
javac -d out src/*.java
```

This currently fails with the `Mutex.java` errors listed below. Once the code compiles, it still needs a `main` method before it can run.

## What the simulator must do

**Programs**

| File | Behaviour |
|------|-----------|
| `Program_1.txt` | Reads two numbers and prints the numbers between them |
| `Program_2.txt` | Reads a filename and data, then writes the data to that file |
| `Program_3.txt` | Reads a filename, then prints that file's contents |

**Instructions**

| Instruction | Effect |
|-------------|--------|
| `print x` | Prints `x` on the screen |
| `assign x y` | Sets `x` to `y`. If `y` is `input`, prints `Please enter a value` and reads it from the user |
| `writeFile x y` | Writes data `y` to file `x` |
| `readFile x` | Reads file `x` |
| `printFromTo x y` | Prints all numbers between `x` and `y` |
| `semWait r` / `semSignal r` | Acquires or releases resource `r`: `userInput`, `userOutput` or `file` |

**Design requirements**

- **Memory:** 40 words holding each process's instructions, 3 variables and PCB. A process can't access memory outside its own block.
- **Swapping:** if a new process doesn't fit, an existing process is moved to disk. It stays in the scheduler and is swapped back in when it next runs.
- **PCB:** process ID, state, program counter and memory boundaries.
- **Mutexes:** one per resource. A process that requests a busy resource joins that resource's blocked queue and the general blocked queue.
- **Scheduler:** Round Robin with 2 instructions per time slice. Processes 1, 2 and 3 arrive at times 0, 1 and 4.
- **Configurable for the evaluation:** the time slice, scheduling order and arrival times can all be changed on request.
- **Output:**
  - The queues after each scheduling event
  - The running process and current instruction
  - The memory on every clock cycle
  - The ID of any process swapped in or out, and the format of memory on disk

## Current state

**Not started or stubbed**

- There is no `main` method.
- `Interpreter.loadProcess` is empty, and the `Scheduler` loop is commented out.
- `Memory.swap`, `Memory.writeOnDisk`, `SystemCall.readMemory` and `SystemCall.writeMemory` are empty.
- `Memory.createProcess` never stores instructions in memory. It writes the PCB under the wrong names, repeating `programCounter`.
- In `SystemCall.executeSystemCall`, the `writeFile`, `readFile`, `semWait` and `semSignal` cases do nothing, and `assign b readFile a` stores `null`.

**Known bugs**

| File | Problem | Effect |
|------|---------|--------|
| `Mutex.java` | `blocked` is declared as `Queue<String>` but holds `Process` objects | Compile error |
| `Mutex.java` | `value` is never initialised | Once compiled, every `semWait` blocks |
| `SystemCall.java` | `writeFile` never closes its `FileWriter` | Data may never reach the file |
| `SystemCall.java` | `writeFile` opens the file in append mode | Doesn't match the brief, which says the file is always created |
| `SystemCall.java` | `readFile` returns only the first line and never closes its reader | `Program_3` can't print a multi-line file |
| `SystemCall.java` | `print` prints the variable's name, and `printFromTo` parses its arguments as literal numbers | Neither looks up variables, so `print b` and `printFromTo a b` fail |
| `SystemCall.java` | `printFromTo` prints no separator between numbers | Output runs together |

## Submission

Per the brief, submit one zip containing the Java files, named after the team (for example `Team_00`).
