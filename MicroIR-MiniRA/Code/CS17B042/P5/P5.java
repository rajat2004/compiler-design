import java.util.ArrayList;

import syntaxtree.*;
import visitor.*;

public class P5 {
   public static void main(String [] args) {
      try {
         Node root = new microIRParser(System.in).Goal();
         Liveliness l = new Liveliness();
//         System.out.println("Program parsed successfully");
         root.accept(l, null); // Your assignment part is invoked here.
         
         generateRA g = new generateRA();
//         g.allProcedures = new Arr;
         root.accept(g, null);
      }
      catch (ParseException e) {
         System.out.println(e.toString());
      }
   }
} 



