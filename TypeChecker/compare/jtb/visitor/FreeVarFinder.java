/***************************************************************************
 * JTB 1.2 Example: FreeVarFinder
 *
 * Class FreeVarFinder is a visitor which locates and reports free
 * (undeclared) variables in this simple subset grammar of Scheme.
 *
 * Original Author: Kevin Tao
 * Revised by:      Wanjun Wang
 ***************************************************************************/

/* RCS Information
 * $Id: FreeVarFinder.java,v 1.2 1997/11/16 21:02:01 ktao Exp $
 *
 * $Log: FreeVarFinder.java,v $
 * Revision 1.3  1999/7/5 wanjun
 * -Upgraded for JTB 1.2
 *
 * Revision 1.2  1997/11/16 21:02:01  ktao
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
   //       | "(" "lambda" "(" ( <IDENT> )* ")" Exp() ")"
   //       | "(" Exp() ( Exp() )* ")"
   //       | "0"
   //       | "(" "+" Exp() "1" ")"
   //
   public void visit(Exp n) {
      String name;

      switch ( n.f0.which ) {
         case 0 :
            name = n.f0.choice.toString();
            if ( !symtab.containsKey(name) )
               System.out.println("\"" + name + "\" is a free variable.");
            break;
         case 1 :
            NodeSequence seq = (NodeSequence)n.f0.choice;
            NodeListOptional paramList = (NodeListOptional)seq.elementAt(3);

            if ( paramList.present() ) {
               for ( Enumeration e = paramList.elements();
                     e.hasMoreElements(); ) {
                  name = e.nextElement().toString();
                  symtab.put(name, name);
               }
            }

            seq.elementAt(5).accept(this);
            break;
         default :
            n.f0.accept(this);
            break;
      }
   }
}
