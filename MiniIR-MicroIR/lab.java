//
// Generated by JTB 1.3.2
//

package visitor;
import syntaxtree.*;
import java.util.*;

/**
 * Provides default methods which visit each node in the tree in depth-first
 * order.  Your visitors may extend this class.
 */
public class lab<R,A> extends GJDepthFirst<R,A> {
   //
   // Auto class visitors--probably don't need to be overridden.
   //
	int t =0;
	public String idgen(String id){
		id = id + Integer.toString(t);
		t++;
		return id;
	}
	
   public R visit(NodeList n, A argu) {
      R _ret=null;
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this,argu);
         _count++;
      }
      return _ret;
   }

   public R visit(NodeListOptional n, A argu) {
      if ( n.present() ) {
         R _ret=null;
         int _count=0;
         for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            e.nextElement().accept(this,argu);
            _count++;
         }
         return _ret;
      }
      else
         return null;
   }

   public R visit(NodeOptional n, A argu) {
      if ( n.present() )
         return n.node.accept(this,argu);
      else
         return null;
   }

   public R visit(NodeSequence n, A argu) {
      R _ret=null;
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this,argu);
         _count++;
      }
      return _ret;
   }

   public R visit(NodeToken n, A argu) { return null; }

   //
   // User-generated visitor methods below
   //

   /**
    * f0 -> ( DeclStatement() )*
    * f1 -> ( Statement() )*
    * f2 -> PrintStatment()
    * f3 -> <EOF>
    */
   public R visit(Goal n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> IntDecl()
    *       | BoolDecl()
    */
   public R visit(DeclStatement n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> <INT>
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public R visit(IntDecl n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> <BOOL>
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public R visit(BoolDecl n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> <PRINT>
    * f1 -> Expression()
    */
   public R visit(PrintStatment n, A argu) {
      R _ret=null;
      String print = n.f0.tokenImage;
      String _exp = (String) n.f1.accept(this, argu);
      String label = idgen("L");
      System.out.println(label + "=" + _exp);
      System.out.println(print + " " + label );
      return _ret;
   }

   /**
    * f0 -> AssignmentStatement()
    */
   public R visit(Statement n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   public R visit(AssignmentStatement n, A argu) {
      R _ret=null;
      String _id =(String) n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      String _exp = (String) n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      System.out.println(_id + "=" + _exp + ";");
      return _ret;
   }

   /**
    * f0 -> PlusExpression()
    *       | MinusExpression()
    *       | PrimaryExpression()
    */
   public R visit(Expression n, A argu) {
      R _ret=null;
      _ret = n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> Expression()
    */
   public R visit(PlusExpression n, A argu) {
      String _ret=null;
      _ret = (String) n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      _ret = _ret + "+" + (String) n.f2.accept(this, argu);
      return (R) _ret;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> Expression()
    */
   public R visit(MinusExpression n, A argu) {
	   String _ret=null;
	      _ret = (String) n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      _ret = _ret + "-" + (String) n.f2.accept(this, argu);
	      return (R) _ret;
   }

   /**
    * f0 -> IntegerLiteral()
    *       | Identifier()
    *       | BracketExpression()
    */
   public R visit(PrimaryExpression n, A argu) {
      R _ret=null;
      _ret = n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public R visit(IntegerLiteral n, A argu) {
      String _ret=null;
      n.f0.accept(this, argu);
      _ret = n.f0.tokenImage;
      return (R)_ret;
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public R visit(Identifier n, A argu) {
	   String _ret=null;
	      n.f0.accept(this, argu);
	      _ret = n.f0.tokenImage;
	      return (R)_ret;
   }

   /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
   public R visit(BracketExpression n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      R _exp = n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      String label = idgen("L");
      System.out.println(label + "=" +(String) _exp);
      return (R) label;
   }

}
