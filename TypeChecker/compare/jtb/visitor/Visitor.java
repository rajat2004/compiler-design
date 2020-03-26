//
// Generated by JTB 1.2
//

package visitor;
import syntaxtree.*;
import java.util.*;

/**
 * All void visitors must implement this interface.
 */
public interface Visitor {
   //
   // void Auto class visitors
   //
   public void visit(NodeList n);

   public void visit(NodeListOptional n);

   public void visit(NodeOptional n);

   public void visit(NodeSequence n);

   public void visit(NodeToken n);


   //
   // User-generated visitor methods below
   //

   /**
    * f0 -> ( Exp() )+
    * f1 -> <EOF>
    */
   public void visit(Unit n);
   /**
    * f0 -> <IDENT>
    *       | "(" "lambda" "(" ( <IDENT> )* ")" Exp() ")"
    *       | "(" Exp() ( Exp() )* ")"
    *       | "0"
    *       | "(" "+" Exp() "1" ")"
    */
   public void visit(Exp n);
}

