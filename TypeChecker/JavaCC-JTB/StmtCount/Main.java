import syntaxtree.*;
import visitor.*;

public class Main {
   public static void main(String [] args) {
      try {
         Node root = new MiniJavaParser(System.in).Goal();
         System.out.println("Program parsed successfully");
         Object value = root.accept(new GJDepthFirst2(), null); // Your assignment part is invoked here.

         // System.out.println("Value returned by visitor : " + (Integer)value);
      }
      catch (ParseException e) {
         System.out.println(e.toString());
      }
   }
} 



