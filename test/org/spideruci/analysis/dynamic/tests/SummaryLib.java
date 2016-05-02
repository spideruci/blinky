package org.spideruci.analysis.dynamic.tests;

public class SummaryLib {
  
  public static void main(String[] args) {
    SummaryLib libobj = new SummaryLib();
    switch(Integer.parseInt(args[0])) {
    case 1:
      libobj.summary_test(1);
      return;
    case 2:
      int sum = libobj.summary_test2(1, 2);
      System.out.println(sum);
      return;
    case 3:
      libobj.summary_test3(55, 3);
      return;
    case 4:
      libobj.summary_test4(new mock());
      return;
    case 5:
      libobj.summary_test5(new mock());
      return;
    case 6:
      libobj.summary_test2(libobj.summary_test3(1, 2), 5);
      return;
    case 7:
      libobj.summary_test7(1, 2);
      return;
    }
  }
  
  public double summary_test(int a) {
    return a + Math.E;
  }

  public int summary_test2(int a, int b) {
    return a + b;
  }

  public int summary_test3(int a, int b) {
    int c = 0;
    return (a + b) * c;
  }

  public int summary_test4(mock a, mock2 b) {
    int x = a.mock_field;
    int y = b.data;
    int z = x + y;
    return z;
  }

  public int summary_test4(mock a) {
    int x = a.mock_field;
    int y = a.mock_field2.data;
    int z = x + y +  mock2.static_mock2_field;
    return z;
  }

  public int summary_test5(mock a) {
    int x = a.mock_field;
    int y = a.mock_field2.data;
    int z = new mock().sum(x, y,  mock2.static_mock2_field, new mock().mock_field);
    return z;
  }
  
  public int summary_test7(int a, int b) {
    return summary_test3(a, b);
  }
}