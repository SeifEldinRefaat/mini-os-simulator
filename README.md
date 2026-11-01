# Simulated Operating System

A Java simulation of an operating system for CSEN 602 (Operating Systems, Spring 2023) at the German University in Cairo. It reads programs from text files, runs them as processes, and manages mutexes and round-robin scheduling.

> **Status: runs.** `javac` compiles cleanly and `Main` executes all three sample programs to completion. The memory model and disk swapping remain unimplemented — see [Current state](#current-state). The project brief is the university's document and is not included here.

## Requirements

- JDK 14 or later (the code uses switch expressions with blocks)
- A JDK, to compile. The stray `jdk.jshell` import that previously forced a full
  JDK at runtime was unused and has been removed.

## Project layout

```
inputfiles/              Programs loaded by the simulator
src/
  Main.java              Entry point; schedules the three programs and runs them
  Interpreter.java       Loads programs, executes instructions, drives the run loop
  Memory.java            40-word memory and process creation (unimplemented)
  Mutex.java             semWait / semSignal with a per-resource blocked queue
  PCB.java               Process control block
  Process.java           A PCB, its instructions and three variable words
  ProcessState.java      NEW, READY, RUNNING, BLOCKED, FINISHED
  Scheduler.java         Round-robin ready and blocked queues
  SystemCall.java        File, screen and user input primitives
  Value.java             Mutex value (Zero, One)
  Word.java              One memory word: a name and its data
```

## Building

Run from the repository root, because programs are loaded from the relative path `inputfiles/`:

```sh
javac -d out src/*.java
java -cp out Main
```

`Main` loads the three sample programs at the arrival times from the brief
(t = 0, 1, 4) and runs them under a two-instruction round-robin quantum. Pass
program paths as arguments to run different ones, all arriving at t = 0.

The programs read from standard input, so you can drive a full run
non-interactively:

```sh
printf '1
5
data.txt
hello
data.txt
' | java -cp out Main
```

Program 1 prints `1 2 3 4 5`, Program 2 writes `hello` to `data.txt`, and
Program 3 reads it back and prints it — with the `file` mutex serialising the
two processes' access.

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

**Working**

- **Round-robin scheduling** with a configurable quantum (default: 2 instructions)
  and the brief's arrival times — processes 1, 2 and 3 are admitted at t = 0, 1 and 4.
- **Mutexes** for `userInput`, `userOutput` and `file`. A process requesting a held
  resource joins that resource's blocked queue; on `semSignal` ownership passes
  directly to the next waiter, which returns to the ready queue already holding it.
- **All seven instructions** execute, resolving operands against the running
  process's own variables: `assign` (literal, `input`, `readFile`), `print`,
  `printFromTo`, `writeFile`, `readFile`, `semWait`, `semSignal`.
- **Inter-process communication through the file system**: Program 2 writes a file
  and Program 3 reads it back, serialised by the `file` mutex.
- **Queue output after each scheduling event**, as the brief asks.

**Not implemented**

- The **40-word memory model**. `Memory.createProcess`, `Memory.swap` and
  `Memory.writeOnDisk` are stubs; processes hold their instructions and variables
  as ordinary Java fields rather than occupying memory words.
- **Swapping to disk** when a process does not fit, and the on-disk memory format.
- **Memory-boundary enforcement** — nothing stops a process reading outside its block.
- **Printing memory on every clock cycle** (depends on the memory model above).

This is the honest split: the process, synchronisation and scheduling half of the
project works and is demonstrable; the memory-management half is not built.

**Bugs fixed**

| File | Problem | Fix |
|------|---------|-----|
| `Mutex.java` | `blocked` declared `Queue<String>` but holds `Process` | Declared `Queue<Process>` |
| `Mutex.java` | `value` never initialised, so every `semWait` blocked | Initialised to `Value.One` |
| `SystemCall.java` | `writeFile` never closed its `FileWriter` | try-with-resources |
| `SystemCall.java` | `writeFile` appended instead of creating | Opens with append `false` |
| `SystemCall.java` | `readFile` returned only the first line, never closed | Reads all lines, try-with-resources |
| `SystemCall.java` | `print` printed the variable name; `printFromTo` parsed literals | Both resolve variables via `Process.getVariable` |
| `SystemCall.java` | `printFromTo` printed no separator | Space-separated |
| `SystemCall.java` | Unused `jdk.jshell` import forced a full JDK | Removed |
| — | No entry point | Added `Main.java` |
| `Interpreter.java` | `loadProcess` was empty | Reads the file and admits a process |
| `Scheduler.java` | Scheduling loop was commented out and would not compile | Round-robin implemented in `Interpreter.run` |

## Provenance

Built for CSEN 602 (Operating Systems, Spring 2023) at the Faculty of Media
Engineering and Technology, The German University in Cairo, then repaired and
documented for publication. The assignment brief is the university's document
and is not included in this repository.
