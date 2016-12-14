package org.spideruci.analysis.statik.subjects;

public class CallGraphSubject {
  
  public static void jumbo(String[] args) {
    doStuff();
  }
  
  public static void doStuff() {
    new A().foo();
    A b = (A) new B();
    b.foo();
  }

}

class A {
  
  B field;
  
  public void foo() {
//    bar(new B());
  }
  
  public int bar(B arg) {
    this.field = arg;
    B temp = this.field;
    int localFoo = temp.foo;
    
//    temp.foo();
    
    temp.foo = temp.foo + this.field.foo;
    
    return localFoo;
  }
}

class B extends A {
  
  int foo = 0;
  
  public void foo() {
    bazz();
  }
  
  public int bazz() {
    
    int temp = this.foo;
    temp = -1;
    int gimp = 5;
    int a = temp + gimp;
    this.foo = -2 * a;
    return temp;
  }
}
