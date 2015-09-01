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
        Class<?> testClazz = Class.forName(testClass);
        core.run(testClazz);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  private static ArrayList<String> getClasses(final String p) {
    System.out.println("p:" + p);
    final ArrayList<String> ret = new ArrayList<String>();
    final String dotClass = ".class";
    try {
      Files.walkFileTree(Paths.get(p), new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
            throws IOException {
          String filePath = file.toString();
          if(filePath.endsWith(dotClass) && !filePath.matches("(.*)\\$(.*)")) {
            String trimmedPath = filePath.substring(p.length())
                .replace('/',' ').trim().replace(' ', '.');
            String qualifiedClassName = 
                trimmedPath.substring(0, trimmedPath.length() - dotClass.length());
            ret.add(qualifiedClassName);
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
