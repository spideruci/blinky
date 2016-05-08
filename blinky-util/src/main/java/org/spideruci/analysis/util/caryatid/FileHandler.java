package org.spideruci.analysis.util.caryatid;

import static com.google.common.base.Preconditions.checkState;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.Assert;


public class FileHandler {
  public static Scanner getScanner(File fileToBeScanned) {
    Scanner scanner;
    try {
      scanner = new Scanner(fileToBeScanned);
    } catch(FileNotFoundException fileNotFoundEx) {
      throw new RuntimeException(fileNotFoundEx.toString());
    }
    return scanner;
  }

  public static File reverseFile(File file) {
    String fileName = file.getAbsolutePath();
    String shell_cmd = "tac " + fileName + " > " + fileName + ".rev";
    try {
      Process p = Runtime.getRuntime().exec(new String[]{"bash","-c", shell_cmd});
      System.out.println(p.waitFor());
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    File reversedFile = new File(fileName + ".rev");
    if(!reversedFile.exists()) {
      throw new RuntimeException("Reversed file not found.");
    }
    return reversedFile;
  }

  public static void deleteFile(File file) {
    boolean result = file.delete();
    Assert.assertEquals(true, result);
  }

  public static void bufferWrite(String content, File file) throws FileNameTooLong {
    try {
      createFileAndEverythingInbetween(file);
      FileWriter filewriter = new FileWriter(file);
      BufferedWriter out = new BufferedWriter(filewriter);
      out.write(content);
      out.flush();
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Calm the fuck down! check the exception.");
    }
  }

  public static boolean createFileAndEverythingInbetween(File file) throws FileNameTooLong {
    ArrayList<File> parents = new ArrayList<>();

    for(File parent = file.getParentFile(); 
        !parent.exists(); 
        parent = parent.getParentFile()) {
      if(parent.isFile()) {
        throw new RuntimeException("I know that Oracle is bad, but you've "
            + "to give me a folder to create a folder inside it. This is a"
            + "file.");
      }
      parents.add(0, parent);
    }

    for(File parent : parents) {
      parent.mkdir();
    }

    try {
      if(!file.exists() && !file.createNewFile()) {
        throw new RuntimeException("You know who to blame -> Oracle."
            + " " + file.getAbsolutePath());
      }
    } catch (IOException e) {
      if(e.getMessage().startsWith("File name too long")) {
        throw new FileNameTooLong();
      }
      e.printStackTrace();
      throw new RuntimeException("look up ... not at the cieling though. " + file.getName());
    }

    return true;
  }

  public static boolean createFileParent(File file) {
    File fileParent = file.getParentFile();
    if(!fileParent.exists() && !fileParent.mkdirs()) {
      throw new RuntimeException("unable to create file: " 
          + fileParent.getAbsolutePath());
    }
    return true;
  }

  public static String bufferReadIntoString(File serializedSummary) {        
    StringBuffer buffer = null;
    try {
      FileReader fileStream = new FileReader(serializedSummary);
      BufferedReader in = new BufferedReader(fileStream);
      buffer = new StringBuffer();
      for(String line = in.readLine(); line != null; line = in.readLine()) {
        buffer.append(line).append("\n");
      }
      in.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw new RuntimeException("Calm the fuck down! Check the exception.");
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Calm the fuck down! Check the exception.");
    }
    return buffer.toString();

  }

  public static void replaceAndCreateFile(File file) {
    checkState(file != null);
    if(file.exists()) file.delete();
    try {
      if(!file.createNewFile()) {
        throw new RuntimeException("depfile creation failed:" + file.getAbsolutePath());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void createDirectory(File dir) {
    if(!dir.exists()) {
      if(!dir.mkdirs()) {
        String msg = "Failed to create directory: ";
        throw new RuntimeException(msg + " " + dir.getAbsolutePath());
      }
    } else {
      checkState(dir.isDirectory());
    }
  }



  public static void purgeDirectory(File rootdir) {
    checkState(rootdir.isDirectory());
    ArrayList<File> dirs = new ArrayList<>();
    for(File file : rootdir.listFiles()) {
      if(file.isDirectory()) {
        dirs.add(file);
      } else {
        file.delete();
      }
    }

    for(File dir : dirs) {
      purgeDirectory(dir);
      dir.delete();
    }
  }

  public static class Navigator {
    File root;
    ArrayList<File> tbd;

    public static FileHandler.Navigator init(File root) {
      FileHandler.Navigator x = new Navigator(root).initializeTbd();
      return x;
    }

    private Navigator(File root) {
      this.root = root;
      this.tbd = new ArrayList<>();
    }

    /**
     * 
     * @return null if there are no files left.
     */
    public File next() {
      if(tbd.isEmpty())
        return null;
      File file = tbd.remove(0);

      while(file.isDirectory()) {
        File[] files = file.listFiles();
        for(File f : files) {
          if(f == null) continue;
          this.tbd.add(f);
        }

        if(tbd.isEmpty())
          return null;
        file = tbd.remove(0);
      }
      return file;
    }

    private FileHandler.Navigator initializeTbd() {
      File[] files = this.root.listFiles();

      for(File f : files) {
        if(f == null) continue;
        this.tbd.add(f);
      }

      return this;
    }
  }

  public static class FileNameTooLong extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public FileNameTooLong() {
      super();
    }

  }
}