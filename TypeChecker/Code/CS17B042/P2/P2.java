import syntaxtree.*;
import visitor.*;

public class P2 {
   public static void main(String [] args) {
      try {
         Node root = new MiniJavaParser(System.in).Goal();
         TypeChecker s = new TypeChecker();
         // First pass
         root.accept(s, null);
         
         // Second pass
         root.accept(s, null);
         System.out.println("Program type checked successfully");
//         root.accept(new TypeChecker(), null); // Your assignment part is invoked here.
      }
      catch (ParseException e) {
         System.out.println(e.toString());
      }
   }
} 



