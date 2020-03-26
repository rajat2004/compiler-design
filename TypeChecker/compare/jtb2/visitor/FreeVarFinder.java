/***************************************************************************
 * JTB 1.2 Example: FreeVarFinder
 *
 * Class FreeVarFinder is a visitor which locates and reports free
 * (undeclared) variables in this simple subset grammar of Scheme.
 *
 * This visitor operates on the slightly modified grammar of the second JTB
 * example.
 *
 * Author: Kevin Tao
 * Revised by: Wanjun Wang
 ***************************************************************************/

/* RCS Information
 * $Id: FreeVarFinder.java,v 1.3 1997/11/19 09:42:04 ktao Exp $
 *
 * $Log: FreeVarFinder.java,v $
 * Revision 1.4  1999/7/5 wanjun
 * -Upgraded for JTB 1.2
 *
 * Revision 1.3  1997/11/19 09:42:04  ktao
 * -Fixed some stupid bugs in FreeVarFinder
 *
 * Revision 1.2  1997/11/16 21:02:06  ktao
 * -Added comments and RCS info
 *
 */

package visitor;
import syntaxtree.*;
import java.util.*;

public class FreeVarFinder extends DepthFirstVisitor {
   Hashtable symtab = new Hashtable();

   //
   // f0 -> <IDENT>
   //       | LambdaDecl()
   //       | "(" Exp() ( Exp() )* ")"
   //       | "0"
   //       | "(" "+" Exp() "1" ")"
   //
   public void visit(Exp n) {
      String name;

      if ( n.f0.which == 0 ) {
         name = n.f0.choice.toString();
         if ( !symtab.containsKey(name) )
            System.out.println("\"" + name + "\" is a free variable.");
      }
      else
         n.f0.accept(this);
   }

   //
   // f0 -> "("
   // f1 -> "lambda"
   // f2 -> "("
   // f3 -> ( <IDENT> )*
   // f4 -> ")"
   // f5 -> Exp()
   // f6 -> ")"
   //
   public void visit(LambdaDecl n) {
      String name;

      if ( n.f3.present() ) {
         for ( Enumeration e = n.f3.elements(); e.hasMoreElements(); ) {
            name = e.nextElement().toString();
            symtab.put(name, name);
         }
      }

      n.f5.accept(this);
   }
}
