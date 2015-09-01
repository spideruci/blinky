package org.spideruci.analysis.dynamic;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class JunitListener extends RunListener {

  public void testRunStarted(Description description)  {
    System.out.println("testRunStarted");
    System.out.println("\t" + description.getClassName());
    System.out.println("\t" + description.getDisplayName());
    System.out.println("\t" + description.getMethodName());
    
  }

  public void testRunFinished(Result result)  {
    
    System.out.println("\tTest count:" + result.getRunCount());
    System.out.println("\tIgnored Test count:" + result.getIgnoreCount());
    System.out.println("\tFailed Test count:" + result.getFailureCount());
    for(Failure failure : result.getFailures()) {
      System.out.println("\t\t" + failure.getMessage());
    }
    System.out.println("testRunFinished");
  }

  public void testStarted(Description description)  {
    System.out.println("\t\ttestStarted");
    System.out.println("\t\t\t" + description.getClassName());
    System.out.println("\t\t\t" + description.getDisplayName());
    System.out.println("\t\t\t" + description.getMethodName());
  }

  public void testFinished(Description description) 
      throws java.lang.Exception  {
    System.out.println("\t\t\t" + description.getClassName());
    System.out.println("\t\t\t" + description.getDisplayName());
    System.out.println("\t\t\t" + description.getMethodName());
    System.out.println("\t\ttestFinished");
  }

}
