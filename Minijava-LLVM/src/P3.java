import syntaxtree.*;
import visitor.*;

public class P3 {
   public static void main(String [] args) {
      try {
         Node root = new MiniJavaParser(System.in).Goal();
         // First Pass
         Pass1 p = new Pass1();
         root.accept(p,null);
//         System.out.println("Program parsed successfully");
//         IR_generator ir = new IR_generator();
//         ir.classhm = p.classhm;
         LLVM_generator g = new LLVM_generator();
         g.classhm = p.classhm;
         g.main_class = p.main_class;
         root.accept(g, null); // Your assignment part is invoked here.
      }
      catch (ParseException e) {
         System.out.println(e.toString());
      }
   }
} 



