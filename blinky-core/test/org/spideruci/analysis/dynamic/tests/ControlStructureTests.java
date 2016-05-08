package org.spideruci.analysis.dynamic.tests;

public class ControlStructureTests {

  public static void main(String[] args) throws Exception {
    ControlStructureTests cst = new ControlStructureTests();
    //		cst.count1to10();
    //		cst.sum(10);
    //		cst.monthOfTheYear();
    //		cst.linearSearch(5, 1, 2, 3, 4, 5, 6, 7);
    //		cst.ldc_longExperiment();
    //		cst.ldc_long_objectExperiment();
    //		
//    summary_lib lib_object = new summary_lib();
    //		
    //		lib_object.summary_test(1);
    //		lib_object.summary_test2(1, 2);
    //		lib_object.summary_test3(1, 4);
    //		lib_object.summary_test4(new mock(), new mock2());
    //		int i = lib_object.summary_test5(new mock());
    //		int j = lib_object.summary_test2(1, i);
    //		System.out.println(j);

    cst.athrowExperiment();
    try {
      cst.athrow_throwsExperiment();
    } catch(Exception e) {
      System.out.println(e + "at main.");
    }
    try{
    	cst.athrow_nested();
    } catch(Exception e) {
      System.out.println("nested " + e + "at main.");
    }
    cst.athrow_throwsExperiment();
  }

  /**
   * Test for combination of For loop and If statement.
   * @param a
   * @param who
   * @return
   */
  public int linearSearch(int who, int ... a) {
    for (int k = 0; k < a.length; k++) {
      if(a[k] == who) {
        return k;
      }
    }
    System.out.println("random print statement");
    return -1;
  }

  public void haltTest() {
    boolean a = 1 > 2;
    if(a) {
      System.exit(0);
      System.out.println(true);
    }
    System.out.println(true);
    a = true;
    return;
  }

  public void inbetweenReturnTest() {
    boolean a = 1 > 2;
    if(a) {
      return;
    }
    a = true;		
    return;
  }

  /**
   * Test for a simple for loop.
   */
  public void count1to10() {
    for(int i = 1; i <= 10; i ++) {
      System.out.println(i);
    }
  }

  /**
   * Test for if-statement; if-else statement w/ multiple predicates
   */
  public void ifElseTest() {
    int i = 0;
    int j = 1;
    if(i == 0) {
      i ++;
    }

    if(j == 1) {
      j ++;
    }

    if(i == 0 && j == 1) {
      i ++;
      j ++;
    }
    else {
      i --;
      j --;
    }


  }

  public void ifELSE() {
    int i =0;
    if(i == 0) {
      i ++;
    }
    else {
      i --;
    }
  }

  public void blah() {
    int i =0;
    if(i == 0) {
      i ++;
    }
  }

  /**
   * Test for a simple while loop
   * @param X
   * @return
   */
  public int sum(int X) {
    int sum = 0;
    while(X!=0) {
      sum += X;
      X--;
    }
    return sum;
  }

  /**
   * Test for a simple switch statment
   */
  public void monthOfTheYear() {
    int month = 8;
    switch (month) {
    case 1:  System.out.println("January"); break;
    case 2:  System.out.println("February"); break;
    case 3:  System.out.println("March"); break;
    case 4:  System.out.println("April"); break;
    case 5:  System.out.println("May"); break;
    case 6:  System.out.println("June"); break;
    case 7:  System.out.println("July"); break;
    case 8:  System.out.println("August"); break;
    case 9:  System.out.println("September"); break;
    case 10: System.out.println("October"); break;
    case 11: System.out.println("November"); break;
    case 12: System.out.println("December"); 
    default: System.out.println("Invalid month.");break;
    }
  }

  public void athrowExperiment() {
    
    System.out.println();
    
    try {
      Exception e = new Exception();
      throw e;
    }
    catch(Exception x) {
      System.out.println(x);
    }
  }

  public void athrow_throwsExperiment() throws Exception {
    Exception e = new Exception();
    throw e;
  }

  public void ldc_longExperiment() {
    long l = 110L;
    System.out.println(l);
  }

  public void ldc_long_objectExperiment() {
    Long l = 110L;
    l = new Long(112L);
    System.out.println(l);
  }

  public void athrow_nested() throws Exception {
    private_athrow();
    System.out.println("i am unreachable. oh crap! something went worng");
  }

  private void private_athrow() throws Exception {
    throw new Exception();
  }


}

class mock {
  public int mock_field = 9;
  public mock2 mock_field2 = new mock2();
  
  public int sum(int... nums) {
    int sums = 0;
    for(int i = 0; i < nums.length; i ++) {
      sums += nums[i];
    }
    mock_field2.data = sums;
    return mock_field2.data;
  }
}

class mock2 {
  public static int static_mock2_field = 100;
  public int data = 10;
}
