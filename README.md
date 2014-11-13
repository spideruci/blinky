# BLINKY: Java Source-code-line Instrumenter And Execution Tracer

Blinky is a Source-code level instruction instrumenter and execution tracer for software systems that compile to Java Bytecode and target the Java Virtual Machine for execution.

**Source-code-line**: For the purposes of this project, a "source code line" or a "source code level instruction" is a single executable line of code, delimited by a newline character, within a Java source code file (*.java).

This instrumenter and execution tracer has the capability to log the execution of any source-code-instruction being executed during a software run. In addition, it also logs events that indicate the execution flow's entry and exit from a method. Event logs that indicate method exits take the form of return or throw instructions.

## ARCHITECTURE
*More to come.*

## EXTERNAL DEPENDENCIES

- ASM: Java Bytecode Manipulation And Ananlysis Framework (http://asm.ow2.org/index.html), v4.0
 

## INSTALLATION & USAGE INSTRUCTIONS
*More to come.*

### EXECUTION TRACES and .trc file format
Execution Traces, for software program runs, are stored in trace files or *.trc files. These traces have been tailored for Java programs.
Each line in a trace file (.trc) represents the execution of a Java source code instruction and it has the following format:

    *<thread-id>*<log-id>,<object-instance-code>,<source-code-line-number>,<owner-class-name>,<owner-method-name>,<log-family>,<opcode>

`<thread-id>` is obtained from the following code snippet: `Thread.currentThread().getId()`. It identifies the thread of execution along which the source code instruction was executed. NanoXML is a single threaded applicaiton; thus you should see only one thread id.

`<log-id>` is a unique id assigned to the log of an event execution.

`<owner-class>` is the name of the class that contains the source code instruction.

`<owner-method>` is the name and the type-descriptor of the method that contains the source code instruction.

`<source-code-line-number>` is the actual line number (within a Java source file) of the executed source code instruction.

`<opcode>` is meaningfully stored in the event of recording execution logs for `return` or `athrow` statements. The opcode in the case of `return` is useful to decipher the kind of value being returned (e.g. int, float, double, long, object/array reference, etc.).

`<object-instance-code>` is the dynamically observed system-hashcode for the `this` object *iff* the `owner-method` is an instance member of the `owner-class`. This value is computed with the `System.identityHashcode(Object object);` method available in the Java SDK, and may not be the same as the `hashcode()` method available to all objects in Java. In the event of a static `owner-method` the name of the `owner-class` is recorded instead of a numeric hashcode value.

`<log-family>` indicates the type of event logged in the execution trace and can either be an `$enter$`, `$return$`, `$athrow$` or `$sourcelinenumber$`.

### Authors:
- Vijay Krishna Palepu, vpalepu [at] uci [dot] edu  
- James A. Jones, jajones [at] uci [dot] edu 

### Acknowledgements:
This work is supported by the National Science Foundation under awards `CAREER CCF-1350837` and `CCF-1116943`.

# LICENSE.txt

Also avilable with this distribution at Blinky/LICENSE.txt

```
:::tex
-----------------------------------------------------------------------------
LICENSE FOR `BLINKY: JAVA SOURCE-CODE-LINE INSTRUMENTER AND EXECUTION TRACER`
-----------------------------------------------------------------------------

Copyright (c) 2014, Vijay Krishna Palepu and James A. Jones, Spider Lab, 
http://spideruci.org/.
All rights reserved.

Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this 
list of conditions, the following list of research publications along with 
their citations and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, 
this list of conditions, citations for the following research publications and 
the following disclaimer in the documentation and/or other materials provided 
with the distribution.

3. Research data, works or publications that make use of this distribution, or 
its derivative, in source code or in binday form must cite the following 
research publications.

4. Neither the name of the copyright holder nor the names of its contributors 
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, 
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.


----------------------
RESEARCH PUBLICATIONS 
----------------------

[RESEARCH-PUBLICATION-1] Palepu, Vijay Krishna and Jones, James, "Visualizing 
Constituent Behaviors within Executions," , 2013 1st IEEE International 
Working Conference on Software Visualization (VISSOFT), pp.1-4, 27-28 
September 2013.

[CITATIONS]
[Format: BibTex]
@INPROCEEDINGS{6650537,
author={Palepu, V.K. and Jones, J.A.},
booktitle={Software Visualization (VISSOFT), 2013 First IEEE Working Conference on},
title={Visualizing constituent behaviors within executions},
year={2013},
month={Sept},
pages={1-4},
keywords={computer animation;data flow analysis;data visualisation;program 
visualisation;source coding;THE BRAIN;behavioral feature 
production;constituent behavior visualization;dynamic control flow;modular 
source-code structures;neural imaging;program activity;software 
features;source code clustered visualization;user-controlled 
animations;Animation;Data 
visualization;Force;Layout;Software;Visualization;XML},
doi={10.1109/VISSOFT.2013.6650537},}

[Format: Plain Text]
Palepu, V.K.; Jones, J.A., "Visualizing constituent behaviors within executions," Software Visualization (VISSOFT), 2013 First IEEE Working Conference on , vol., no., pp.1,4, 27-28 Sept. 2013
doi: 10.1109/VISSOFT.2013.6650537
keywords: {computer animation;data flow analysis;data visualisation;program 
visualisation;source coding;THE BRAIN;behavioral feature 
production;constituent behavior visualization;dynamic control flow;modular 
source-code structures;neural imaging;program activity;software 
features;source code clustered visualization;user-controlled 
animations;Animation;Data 
visualization;Force;Layout;Software;Visualization;XML},
URL: http://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=6650537&
isnumber=6650514



----------------------------------------------------------------------------
LICENSE FOR `ASM: JAVA BYTECODE MANIPULATION AND ANANLYSIS FRAMEWORK`
----------------------------------------------------------------------------

Copyright (c) 2012 France Télécom
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:
1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. Neither the name of the copyright holders nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
THE POSSIBILITY OF SUCH DAMAGE.
```