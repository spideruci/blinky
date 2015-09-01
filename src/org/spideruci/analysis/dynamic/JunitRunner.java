package org.spideruci.analysis.dynamic;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import org.junit.runner.JUnitCore;

public class JunitRunner {

  public static void main(String[] args) {

    JUnitCore core = new JUnitCore();
    JunitListener listener = new JunitListener();
    core.addListener(listener);

    for(String testClass : getClasses(args[0])){
      try {
        System.out.println(testClass);
        core.run(Class.forName(testClass));
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  private static ArrayList<String> getClasses(final String p) {
    System.out.println("p:" + p);
    final ArrayList<String> ret = new ArrayList<String>();

    try {
      Files.walkFileTree(Paths.get(p), new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
            throws IOException {
          String str = file.toString();
          if(str.endsWith(".class") && !str.matches("(.*)\\$(.*)")) {
            String x = str.replaceAll(p.endsWith("/")?p:p+"/","")
                .replace('/','.')
                .replaceAll("\\.class","");
            System.out.println(x);
            ret.add(x);
          }
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
    return ret;
  }

}
