package org.spideruci.analysis.diagnostics.subjects;

public class Namaste {

  public static void main(String[] args) {
    final String hello = "hello world"; 
    System.out.println(hello);
    
    for(int i = 0; i < 10; i += 1) {
      new Namaste().sayHi();
      new Namaste().sayHiTo("interation" + i);
    }

  }
  
  public void sayHi() {
    System.out.println("Hi there stranger! :D");
  }
  
  public void sayHiTo(String name) {
    System.out.printf("Hi %s!\n", name);
  }

}
