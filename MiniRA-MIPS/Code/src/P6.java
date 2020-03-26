import java.util.ArrayList;

import syntaxtree.*;
import visitor.*;

public class P6 {
   public static void main(String [] args) {
      try {
         Node root = new MiniRAParser(System.in).Goal();
         MIPSgen g = new MIPSgen();
//         System.out.println("Program parsed successfully");
         root.accept(g, null); // Your assignment part is invoked here.
      }
      catch (ParseException e) {
         System.out.println(e.toString());
      }
   }
} 



