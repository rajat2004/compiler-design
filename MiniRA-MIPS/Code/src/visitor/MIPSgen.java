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
public class MIPSgen<R,A> extends GJDepthFirst<R,A> {
	
	boolean isSimpleExpr = false;
	int simpleExprType = -1;
	
	int argsOnStack = -1;
	int callArgsOnStack = -1;
	
	boolean debug = false;
	
	String op=null, op_reg1=null, op_reg2=null;
	String label = null;
	
	void print(String s) {
		if (debug)
			System.out.println(s);
	}
	
   //
   // Auto class visitors--probably don't need to be overridden.
   //
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
      if ( n.present() ) {
         R _ret = n.node.accept(this,argu);
         System.out.println(label + ": ");
         System.out.println("\t nop");
         return _ret;
      }
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
    * f0 -> "MAIN"
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> "["
    * f5 -> IntegerLiteral()
    * f6 -> "]"
    * f7 -> "["
    * f8 -> IntegerLiteral()
    * f9 -> "]"
    * f10 -> StmtList()
    * f11 -> "END"
    * f12 -> ( SpillInfo() )?
    * f13 -> ( Procedure() )*
    * f14 -> <EOF>
    */
   public R visit(Goal n, A argu) {
      R _ret=null;
      System.out.println("\t .text");
      System.out.println("\t .globl \t main");
      System.out.println("main:");
      
      System.out.println("\t move $fp, $sp");
      
      int noArgs = Integer.parseInt(n.f2.f0.tokenImage);
      int stackSpace = Integer.parseInt(n.f5.f0.tokenImage);
      int callSpace = Integer.parseInt(n.f8.f0.tokenImage);
      argsOnStack = noArgs > 4 ?  noArgs-4 : 0;
      callArgsOnStack = callSpace > 4 ? callSpace-4 : 0;

      System.out.println("\t subu $sp, $sp, " + Integer.toString(4*(stackSpace + callArgsOnStack + 2)) );
      
      System.out.println("\t sw $ra, -4($fp)");
//      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
      n.f7.accept(this, argu);
      n.f8.accept(this, argu);
      n.f9.accept(this, argu);
      n.f10.accept(this, argu);
//      n.f11.accept(this, argu);
//      n.f12.accept(this, argu);
      
      System.out.println("\t lw $ra, -4($fp)");
      System.out.println("\t addu $sp, $sp, " + Integer.toString(4*(stackSpace + callArgsOnStack + 2)) );
      System.out.println("\t j $ra");
      
      
      n.f13.accept(this, argu);
      n.f14.accept(this, argu);
      
      
      
      System.out.println("\n");
      // HALLOC function
      System.out.println("\t .text");
      System.out.println("\t .globl _halloc");
      System.out.println("_halloc:");
      System.out.println("\t li $v0, 9");
      System.out.println("\t syscall");
      System.out.println("\t j $ra");
      
      System.out.println("\n");
      // Print
      System.out.println("\t .text");
      System.out.println("\t .globl _print");
      System.out.println("_print:");
      System.out.println("\t li $v0, 1");
      System.out.println("\t syscall");
      System.out.println("\t la $a0, newl");
      System.out.println("\t li $v0, 4");
      System.out.println("\t syscall");
      System.out.println("\t j $ra");
      
      System.out.println("\n");
      // Newline
      System.out.println("\t .data");
      System.out.println("\t .align 0");
      System.out.println("newl: \t .asciiz \"\\n\"");
      
      System.out.println("\n");
      // String error
      System.out.println("\t .data");
      System.out.println("\t .align 0");
      System.out.println("str_er:  .asciiz \" ERROR: abnormal termination\\n\"");
      
      return _ret;
   }

   /**
    * f0 -> ( ( Label() )? Stmt() )*
    */
   public R visit(StmtList n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> Label()
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> "["
    * f5 -> IntegerLiteral()
    * f6 -> "]"
    * f7 -> "["
    * f8 -> IntegerLiteral()
    * f9 -> "]"
    * f10 -> StmtList()
    * f11 -> "END"
    * f12 -> ( SpillInfo() )?
    */
   public R visit(Procedure n, A argu) {
      R _ret=null;
      System.out.println("\n");
      System.out.println("\t .text");
      System.out.println("\t .globl \t " + n.f0.f0.tokenImage);
      System.out.println(n.f0.f0.tokenImage + ":");
      
      System.out.println("\t sw $fp, -8($sp)");
      System.out.println("\t move $fp, $sp");
      
      int noArgs = Integer.parseInt(n.f2.f0.tokenImage);
      int stackSpace = Integer.parseInt(n.f5.f0.tokenImage);
      int callSpace = Integer.parseInt(n.f8.f0.tokenImage);
      argsOnStack = noArgs > 4 ?  noArgs-4 : 0;
      callArgsOnStack = callSpace > 4 ? callSpace-4 : 0;
      
      System.out.println("\t subu $sp, $sp, " + Integer.toString(4*(stackSpace + callArgsOnStack + 2)) );
      
      System.out.println("\t sw $ra, -4($fp)");
//      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      n.f5.accept(this, argu);
      n.f6.accept(this, argu);
      n.f7.accept(this, argu);
      n.f8.accept(this, argu);
      n.f9.accept(this, argu);
      n.f10.accept(this, argu);
//      n.f11.accept(this, argu);
//      n.f12.accept(this, argu);
      System.out.println("\t lw $ra, -4($fp)");
      System.out.println("\t lw $fp, " + Integer.toString(4*(stackSpace + callArgsOnStack)) + "($sp)" );
      System.out.println("\t addu $sp, $sp, " + Integer.toString(4*(stackSpace + callArgsOnStack + 2)) );
      System.out.println("\t j $ra");
      
      
      return _ret;
   }

   /**
    * f0 -> NoOpStmt()
    *       | ErrorStmt()
    *       | CJumpStmt()
    *       | JumpStmt()
    *       | HStoreStmt()
    *       | HLoadStmt()
    *       | MoveStmt()
    *       | PrintStmt()
    *       | ALoadStmt()
    *       | AStoreStmt()
    *       | PassArgStmt()
    *       | CallStmt()
    */
   public R visit(Stmt n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "NOOP"
    */
   public R visit(NoOpStmt n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      System.out.println("\t nop");
      return _ret;
   }

   /**
    * f0 -> "ERROR"
    */
   public R visit(ErrorStmt n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "CJUMP"
    * f1 -> Reg()
    * f2 -> Label()
    */
   public R visit(CJumpStmt n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      String reg = (String) n.f1.accept(this, argu);
      String lbl = (String) n.f2.accept(this, argu);
      System.out.println("\t beqz " + reg + ", " + lbl);
      return _ret;
   }

   /**
    * f0 -> "JUMP"
    * f1 -> Label()
    */
   public R visit(JumpStmt n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      String lbl = (String) n.f1.accept(this, argu);
      System.out.println("\t b " + lbl);
      return _ret;
   }

   /**
    * f0 -> "HSTORE"
    * f1 -> Reg()
    * f2 -> IntegerLiteral()
    * f3 -> Reg()
    */
   public R visit(HStoreStmt n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      String reg1 = (String) n.f1.accept(this, argu);
      String num = (String) n.f2.accept(this, argu);
      String reg2 = (String) n.f3.accept(this, argu);
      System.out.println("\t sw " + reg2 + ", " + num + "(" + reg1 + ")");
      return _ret;
   }

   /**
    * f0 -> "HLOAD"
    * f1 -> Reg()
    * f2 -> Reg()
    * f3 -> IntegerLiteral()
    */
   public R visit(HLoadStmt n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      String reg1 = (String) n.f1.accept(this, argu);
      String reg2 = (String) n.f2.accept(this, argu);
      String num = (String) n.f3.accept(this, argu);
      System.out.println("\t lw " + reg1 + ", " + num + "(" + reg2 + ")");
      return _ret;
   }

   /**
    * f0 -> "MOVE"
    * f1 -> Reg()
    * f2 -> Exp()
    */
   public R visit(MoveStmt n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      String reg = (String) n.f1.accept(this, argu);
      String expr = (String) n.f2.accept(this, argu);
      
      if (n.f2.f0.which == 2) {
    	  // Register
    	  if (simpleExprType == 0) 
    		  System.out.println("\t move " + reg + " " + expr);
    	  // Integer
    	  else if (simpleExprType == 1)
    		  System.out.println("\t li " + reg + " " + expr);
    	  // Label
    	  else if (simpleExprType == 2)
    		  System.out.println("\t la " + reg + " " + expr);
    	  else
    		  print("Fuck up");
    	  
      }
      // Hallocate
      else if (n.f2.f0.which == 0) {
    	  System.out.println("\t move " + reg + " $v0");
      }
      // BinOp
      else {
    	  System.out.println("\t " + op + " " + reg + ", " + op_reg1 + ", " + op_reg2);
      }
      return _ret;
   }

   /**
    * f0 -> "PRINT"
    * f1 -> SimpleExp()
    */
   public R visit(PrintStmt n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      String simpleExp = (String) n.f1.accept(this, argu);
      System.out.println("\t move $a0 " + simpleExp);
      System.out.println("\t jal _print");
      return _ret;
   }

   /**
    * f0 -> "ALOAD"
    * f1 -> Reg()
    * f2 -> SpilledArg()
    */
   public R visit(ALoadStmt n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      String reg = (String) n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      int num = Integer.parseInt(n.f2.f1.f0.tokenImage);
      print("ALOAD " + reg + num);
      if (num < argsOnStack)
    	  System.out.println("\t lw " + reg + ", " + Integer.toString(num*4) + "($fp)");
      else
    	  System.out.println("\t lw " + reg + ", " + Integer.toString((num + callArgsOnStack)*4) + "($sp)" );
      return _ret;
   }

   /**
    * f0 -> "ASTORE"
    * f1 -> SpilledArg()
    * f2 -> Reg()
    */
   public R visit(AStoreStmt n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      String reg = (String) n.f2.accept(this, argu);
      int num = Integer.parseInt(n.f1.f1.f0.tokenImage);
      if (num < argsOnStack)
    	  System.out.println("\t sw " + reg + ", " + Integer.toString(num*4) + "($fp)");
      else
    	  System.out.println("\t sw " + reg + ", " + Integer.toString((num + callArgsOnStack)*4) + "($sp)" );
      return _ret;
   }

   /**
    * f0 -> "PASSARG"
    * f1 -> IntegerLiteral()
    * f2 -> Reg()
    */
   public R visit(PassArgStmt n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      String reg = (String) n.f2.accept(this, argu);
      int num = Integer.parseInt(n.f1.f0.tokenImage);
      System.out.println("\t sw " + reg + ", " + Integer.toString((num-1)*4) +  "($sp)");
      return _ret;
   }

   /**
    * f0 -> "CALL"
    * f1 -> SimpleExp()
    */
   public R visit(CallStmt n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      String reg = (String) n.f1.accept(this, argu);
      System.out.println("\t jalr " + reg);
      return _ret;
   }

   /**
    * f0 -> HAllocate()
    *       | BinOp()
    *       | SimpleExp()
    */
   public R visit(Exp n, A argu) {
      R _ret=null;
      _ret = n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "HALLOCATE"
    * f1 -> SimpleExp()
    */
   public R visit(HAllocate n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      String simpleExpr = (String) n.f1.accept(this, argu);
      print("In halloc: " + simpleExpr + "  " + Integer.toString(n.f1.f0.which));
      
      // Register
	  if (simpleExprType == 0) 
		  System.out.println("\t move $a0 " + simpleExpr);
	  // Integer
	  else if (simpleExprType == 1)
		  System.out.println("\t li $a0 " +  simpleExpr);
	  // Label
	  else if (simpleExprType == 2)
		  System.out.println("\t la $a0 " + simpleExpr);
	  else
		  print("Fuck up: Hallocate");
    	  
      System.out.println("\t jal _halloc");
      return _ret;
   }

   /**
    * f0 -> Operator()
    * f1 -> Reg()
    * f2 -> SimpleExp()
    */
   public R visit(BinOp n, A argu) {
      R _ret=null;
      op = (String) n.f0.accept(this, argu);
      op_reg1 = (String) n.f1.accept(this, argu);
      op_reg2 = (String) n.f2.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "LE"
    *       | "NE"
    *       | "PLUS"
    *       | "MINUS"
    *       | "TIMES"
    *       | "DIV"
    */
   public R visit(Operator n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      switch (n.f0.which) {
      case 0: return (R) "sle";
      case 1: return (R) "sne";
      case 2: return (R) "add";
      case 3: return (R) "sub";
      case 4: return (R) "mul";
      case 5: return (R) "div";
      }
      return _ret;
   }

   /**
    * f0 -> "SPILLEDARG"
    * f1 -> IntegerLiteral()
    */
   public R visit(SpilledArg n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> Reg()
    *       | IntegerLiteral()
    *       | Label()
    */
   public R visit(SimpleExp n, A argu) {
      R _ret=null;
      isSimpleExpr = true;
      _ret = n.f0.accept(this, argu);
      simpleExprType = n.f0.which;
      return _ret;
   }

   /**
    * f0 -> "a0"
    *       | "a1"
    *       | "a2"
    *       | "a3"
    *       | "t0"
    *       | "t1"
    *       | "t2"
    *       | "t3"
    *       | "t4"
    *       | "t5"
    *       | "t6"
    *       | "t7"
    *       | "s0"
    *       | "s1"
    *       | "s2"
    *       | "s3"
    *       | "s4"
    *       | "s5"
    *       | "s6"
    *       | "s7"
    *       | "t8"
    *       | "t9"
    *       | "v0"
    *       | "v1"
    */
   public R visit(Reg n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      return (R) ("$" + ((NodeToken)n.f0.choice).tokenImage);
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public R visit(IntegerLiteral n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      return (R) n.f0.tokenImage;
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public R visit(Label n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      label = n.f0.tokenImage;
      return (R) n.f0.tokenImage;
   }

   /**
    * f0 -> "//"
    * f1 -> SpillStatus()
    */
   public R visit(SpillInfo n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> <SPILLED>
    *       | <NOTSPILLED>
    */
   public R visit(SpillStatus n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

}
