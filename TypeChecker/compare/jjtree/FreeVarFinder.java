/***************************************************************************
 * JJTree Example: FreeVarFinder
 *
 * Class FreeVarFinder is a visitor which locates and reports free
 * (undeclared) variables in this simple subset grammar of Scheme.
 *
 * Updated for JJTree 0.3pre5 (included with JavaCC 0.7).
 *   - return types changed from void to Object.
 *   - added extra parameter "data" to visit() methods and accept() calls.
 *   - had to had "return null" at end of every method.
 *
 * Author: Kevin Tao (taokr@cs.purdue.edu)
 ***************************************************************************/

/* RCS Information
 * $Id: FreeVarFinder.java,v 1.4 1998/02/12 21:09:10 ktao Exp $
 *
 * $Log: FreeVarFinder.java,v $
 * Revision 1.4  1998/02/12 21:09:10  ktao
 * -Updated for JJTree 0.3pre3 and JavaCC 0.7.
 *
 * Revision 1.3  1997/11/16 20:54:21  ktao
 * -Added RCS info, removed automatically-generated files from repository.
 *
 */

import java.util.Hashtable;

public class FreeVarFinder implements SubSchemeVisitor {
   Hashtable symtab = new Hashtable();
   boolean withinDecl = false;
   
   public Object visit(SimpleNode node, Object data) {
      // Don't need to visit children
      return null;
   }

   public Object visit(ASTUnit node, Object data) {
      System.out.println("Now checking for free variables...");
      System.out.println();
      node.childrenAccept(this, null);
      return null;
   }

   public Object visit(ASTDeclaration node, Object data) {
      int numChildren = node.jjtGetNumChildren();

      // First visit the formal parameter list
      withinDecl = true;

      for ( int i = 0; i < numChildren - 1; ++i )
         node.jjtGetChild(i).jjtAccept(this, null);

      withinDecl = false;

      // Now visit the body of the function
      node.jjtGetChild(numChildren - 1).jjtAccept(this, null);
      return null;
   }

   public Object visit(ASTFunctionCall node, Object data) {
      node.childrenAccept(this, null);
      return null;
   }

   public Object visit(ASTZeroConstant node, Object data) {
      // Don't need to visit children
      return null;
   }

   public Object visit(ASTAddExp node, Object data) {
      node.childrenAccept(this, null);
      return null;
   }

   //
   // If we're under a Declaration node in the tree, we should put the
   // identifier into the symbol table.  Otherwise, if it is not in the
   // symbol table, report the free (undeclared) variable.
   //
   public Object visit(ASTIdentifier node, Object data) {
      String name = node.getName();

      if ( withinDecl )
         symtab.put(name, name);
      else if ( !symtab.containsKey(name) )
         System.out.println("\"" + name + "\" is a free variable.");

      return null;
   }
}
