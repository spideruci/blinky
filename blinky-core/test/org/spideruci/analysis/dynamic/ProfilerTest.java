package org.spideruci.analysis.dynamic;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.spideruci.analysis.statik.instrumentation.Config;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ProfilerTest {

    @Parameters
    public static Collection<Object[]> parameters(){
        // Pair-wise testing of different configurations (cat1: 0, A, xile; cat2: calldepth, cat3:whitelist)
        return Arrays.asList(new Object[][] {
                {"0,calldepth,whitelist",  false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true},
                {"0,calldepth",         false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, false},

                {"A,calldepth,whitelist", true, true, true, true, true, true, true, true, true, true, true, false, true, true, true, true},
                {"A"                    , true, true, true, true, true, true, true, true, true, true, true, false, true, true, false, false},

                {"xile,calldepth,whitelist", true, true, true, true, false, false, false, false, false, false, false, false, false, false, true, true},
                {"xile", true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false},
        });
    }

    @Parameter
    public String inputArguments;
    @Parameter(1)
    public boolean outEnter;
    @Parameter(2)
    public boolean outExit;
    @Parameter(3)
    public boolean outInvoke;
    @Parameter(4)
    public boolean outLineNum;
    @Parameter(5)
    public boolean outVar;
    @Parameter(6)
    public boolean outJump;
    @Parameter(7)
    public boolean outZero;
    @Parameter(8)
    public boolean outConstant;
    @Parameter(9)
    public boolean outField;
    @Parameter(10)
    public boolean outType;
    @Parameter(11)
    public boolean outSwitch;
    @Parameter(12)
    public boolean outInvokeRuntimeSign;
    @Parameter(13)
    public boolean outEnterRuntimeSign;
    @Parameter(14)
    public boolean outLog;
    @Parameter(15)
    public boolean outCallDepth;
    @Parameter(16)
    public boolean outInclusionList;

    @BeforeClass
    public static void setUp() {
        // When: we have a profiler where all the flags are set to false
        Profiler.setLogFlags(false);
        Profiler.callDepth = false;
        Config.checkInclusionList = false;
    }

    @Test
    public void configureProfilerToTraceAll(){

        // Given: we give the argument to the profiler.
        Profiler.initProfiler(inputArguments);

        // Then:
        assertEquals(outEnter, Profiler.logMethodEnter);
        assertEquals(outExit, Profiler.logMethodExit);
        assertEquals(outInvoke, Profiler.logMethodInvoke);
        assertEquals(outLineNum, Profiler.logSourceLineNumber);
        assertEquals(outVar, Profiler.logVar);
        assertEquals(outJump, Profiler.logJump);
        assertEquals(outZero, Profiler.logZero);
        assertEquals(outConstant, Profiler.logConstant);
        assertEquals(outField, Profiler.logField);
        assertEquals(outType, Profiler.logType);
        assertEquals(outSwitch, Profiler.logSwitch);
        assertEquals(outInvokeRuntimeSign, Profiler.logInvokeRuntimeSign);
        assertEquals(outEnterRuntimeSign, Profiler.logEnterRuntimeSign);
        assertEquals(outLog, Profiler.log);

        assertEquals(outCallDepth, Profiler.callDepth);
        assertEquals(outInclusionList, Config.checkInclusionList);
    }

    @After
    public void tearDown(){
        // Due to all being static variables I reset them to their starting value else it will impact other tests
        Profiler.setLogFlags(false);
        Profiler.callDepth = false;
        Config.checkInclusionList = false;
    }
}
