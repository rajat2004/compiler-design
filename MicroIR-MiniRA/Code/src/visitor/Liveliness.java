//
// Generated by JTB 1.3.2
//

package visitor;
import syntaxtree.*;
import java.util.*;

import com.sun.org.apache.xpath.internal.operations.Bool;



class BasicBlock {
	String stmtType = "";
	int lineNo;
	
	ArrayList<String> use = new ArrayList<String>();
	ArrayList<String> def = new ArrayList<String>();
	HashSet<String> in = new HashSet<String>();
	HashSet<String> out = new HashSet<String>();
	
	HashSet<BasicBlock> succ = new HashSet<BasicBlock>();

}



class TempLiveRange {
	int start = Integer.MAX_VALUE, end = Integer.MIN_VALUE;
	String tempNo = "-1";
	
	String register="";
	int stackLocation = -1;
	String argReg = "";
	boolean spilled = false;
}

class tempStartComparator implements Comparator<TempLiveRange> {
	@Override
	public int compare(TempLiveRange temp1, TempLiveRange temp2) {
		return temp1.start - temp2.start;
	}
}



class tempEndComparator implements Comparator<TempLiveRange> {
	@Override
	public int compare(TempLiveRange temp1, TempLiveRange temp2) {
		return temp1.end - temp2.end;
	}
}



// Data to be stored for each Procedure
class ProcedureClass{
	ArrayList<BasicBlock> proc_blocks = new ArrayList<BasicBlock>();

	HashMap<String, TempLiveRange> tempMap = new HashMap<String, TempLiveRange>();
	ArrayList<TempLiveRange> sortedTemp = new ArrayList<TempLiveRange>();
	ArrayList<TempLiveRange> arguments = new ArrayList<TempLiveRange>();
	HashMap<String, TempLiveRange> argsMap = new HashMap<String, TempLiveRange>();
	
	String label = "Fuck";
	int procLineNo = 0;
	
	int no_args = 0, stack_slots = 0, maxCallArgs = 0;
	
	boolean doesCalls = false;
	
	boolean spilled = false;
}


/**
 * Provides default methods which visit each node in the tree in depth-first
 * order.  Your visitors may extend this class.
 */
public class Liveliness<R,A> extends GJDepthFirst<R,A> {
	
	boolean debug = false;
	
	static ArrayList<ProcedureClass> allProcedures = new ArrayList<ProcedureClass>();
	ArrayList<BasicBlock> lines = new ArrayList<BasicBlock>();
	HashMap<String, TempLiveRange> currProcTempMap = new HashMap<String, TempLiveRange>();
	
	HashMap<String, BasicBlock> labelSeen = new HashMap<String, BasicBlock>();
	HashMap<String, ArrayList<BasicBlock>> labelsToBeFilled = new HashMap<String, ArrayList<BasicBlock>>();
	
	int lineNo = 0;
	String currlabel = "";
	
	BasicBlock curr_line = new BasicBlock();
	BasicBlock prev_line = new BasicBlock();
	ProcedureClass curr_proc = new ProcedureClass();
	
	boolean use = false;
	
	
	int stackLocation = 0;
	
	
	void print(String s) {
		if (debug)
			System.out.println(s);
	}
	
	
	
	void livelinessAnalysis() {
		// Run for each procedure
		for (ProcedureClass p : allProcedures) {
			boolean flag = true;
			
			while (flag) {
				flag = false;
				for (BasicBlock l : p.proc_blocks) {
					// Check ordering
					HashSet<String> new_out = new HashSet<String>(l.out);
					HashSet<String> new_in = new HashSet<String>(l.in);
					
					// Find new out
					for (BasicBlock s : l.succ) {
						new_out.addAll(s.in);
					}
					
					if (!l.out.containsAll(new_out)){
						flag = true;
						l.out = new_out;
					}
					
					
					// Find new in
					new_in.addAll(l.out);
					new_in.removeAll(l.def);
					new_in.addAll(l.use);
					
					if (!l.in.containsAll(new_in)){
						flag = true;
						l.in = new_in;
					}
					
				}
			}
		}
	}
	
	
	
	void generateLiveRanges() {
		for (ProcedureClass p : allProcedures) {
			
			for (BasicBlock l : p.proc_blocks) {
				HashSet<String> liveTemps = new HashSet<String>();
				liveTemps.addAll(l.in);
				liveTemps.addAll(l.def);
				
				for (String temp : liveTemps) {
					int curr_start = p.tempMap.get(temp).start;
					p.tempMap.get(temp).start = (curr_start > l.lineNo) ? l.lineNo : curr_start;

					int curr_end = p.tempMap.get(temp).end;
					p.tempMap.get(temp).end = (curr_end < l.lineNo) ? l.lineNo : curr_end;
				}
			}

			p.sortedTemp = new ArrayList<TempLiveRange>(p.tempMap.values());
			Collections.sort(p.sortedTemp, new tempStartComparator());
		}
	}
	
	
	String str(TempLiveRange t) {
		return "TEMP " + t.tempNo + ": " + "start-" + Integer.toString(t.start) + 
				 " end-" + Integer.toString(t.end) + " reg: " + t.register + " spilled: " + Boolean.toString(t.spilled) + " stack: " + Integer.toString(t.stackLocation)
				 + " argReg: " + t.argReg; 
	}
	
	
	void printProcLines() {
		if (debug) {
			for (ProcedureClass p : allProcedures) {
				System.out.println(Integer.toString(p.procLineNo) + ": " + p.label + " StackSlots-" + Integer.toString(p.stack_slots)
									+ " MaxCallArgs: " + Integer.toString(p.maxCallArgs));
				System.out.println("DoesCalls: " + Boolean.toString(p.doesCalls));
				for (BasicBlock l : p.proc_blocks) {
					System.out.println(l.lineNo + ": "  + l.stmtType);
					System.out.println("Use: " + l.use);
					System.out.println("Def: " + l.def);
					System.out.print("Succ: ");
					for (BasicBlock s : l.succ) {
						System.out.print(s.lineNo + ", ");
					}
					System.out.println();
					
					System.out.println("In: " + l.in);
					System.out.println("Out: " + l.out);
				}
				
				
				System.out.println("TEMPS: ");
				
				for (TempLiveRange t : p.sortedTemp) {
					System.out.println(str(t));
				}
				
				System.out.println();
				for (TempLiveRange t : p.arguments) {
					System.out.println(str(t));
				}
				
				System.out.println("\n");
			}
		}
	}
	
	
	
	
	void expireOldIntervals(TempLiveRange i, ArrayList<TempLiveRange> active, ArrayList<String> freeRegisters) {		
		while (!active.isEmpty()) {
//			print(Integer.toString(active.size()) );
			if (active.get(0).end >= i.start) 
				return;
			
			String freedReg = active.get(0).register;
			active.remove(0);
			freeRegisters.add(freedReg);
		}
	}
	
	
	
	void spillAtInterval(TempLiveRange i, ArrayList<TempLiveRange> active) {
		TempLiveRange spill = active.get(active.size() - 1);
		if (spill.end > i.end) {
			i.register = spill.register;
			spill.spilled = true;
			spill.stackLocation = stackLocation;
			stackLocation++;
			
			active.remove(active.size()-1);
			active.add(i);
			Collections.sort(active, new tempEndComparator());
		}
		else {
			i.stackLocation = stackLocation;
			i.spilled = true;
			stackLocation++;
		}
	}

	
	
	void LinearScanRegAllocation(ProcedureClass p) {
		int numGenRegs = 18;
		ArrayList<String> freeRegisters = new ArrayList<String>();
		
		for (int i=0; i<=7; i++)
			freeRegisters.add("s" + Integer.toString(i));
		for (int i=0; i<=9; i++)
			freeRegisters.add("t" + Integer.toString(i));
		
		ArrayList<TempLiveRange> activeTemps = new ArrayList<TempLiveRange>();
		
		for (TempLiveRange i : p.sortedTemp) {
			expireOldIntervals(i, activeTemps, freeRegisters);
			if (activeTemps.size() == numGenRegs) {
				
				print("Spilling: " + p.label + ": ");
				for (TempLiveRange t : activeTemps)
					print(str(t));
				
				spillAtInterval(i, activeTemps);
				p.spilled = true;
			}
			else {
				i.register = freeRegisters.get(0);
				freeRegisters.remove(0);
				activeTemps.add(i);
				Collections.sort(activeTemps, new tempEndComparator());
			}
		}
		
	}
	
	
	
	
	void RegisterAllocation() {
		for (ProcedureClass p: allProcedures) {
			stackLocation = 0;
			ArrayList<TempLiveRange> arguments = new ArrayList<TempLiveRange>();
			ArrayList<TempLiveRange> stackedArgs = new ArrayList<TempLiveRange>();
			
			for (TempLiveRange t: p.sortedTemp) {
				if (Integer.parseInt(t.tempNo) < p.no_args) {
					if (Integer.parseInt(t.tempNo) <= 3) {
						t.argReg = "a" + t.tempNo;
					}
					else {
						t.stackLocation = Integer.parseInt(t.tempNo) - 4;
						stackLocation++;
						stackedArgs.add(t);
					// Is this really needed?
//					p.sortedTemp.remove(t);
					}	
				
					arguments.add(t);
				}
			}
			
			p.sortedTemp.removeAll(stackedArgs);
						
			
			LinearScanRegAllocation(p);
			p.sortedTemp.addAll(stackedArgs);
			Collections.sort(p.sortedTemp, new tempStartComparator());
			
			p.arguments = arguments;
			p.stack_slots += stackLocation;
			
			if (p.label != "MAIN") {
				if (p.doesCalls) 
					p.stack_slots += 18;
				else
					p.stack_slots += 8;
			}
			
			
			for (TempLiveRange t: p.arguments) {
				p.argsMap.put(t.tempNo, t);
			}
		}
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
    	  print("In NodeOptional");
    	  R _ret =  n.node.accept(this,argu);
    	  lineNo++;
    	  curr_line = new BasicBlock();
    	  curr_line.lineNo = lineNo;
    	  lines.add(curr_line);
    	  
    	  if (prev_line != null) {
    		  prev_line.succ.add(curr_line);
    	  }
    	  
    	  // New label start, so add succ in all the lines which point to this
    	  String label = (String) _ret;
    	  
    	  if (labelsToBeFilled.containsKey(label)) {
    		  for (BasicBlock l : labelsToBeFilled.get(label)) {
    			  l.succ.add(curr_line);
    		  }
    		  
    		  labelsToBeFilled.remove(label);
    	  }
    	  
    	  labelSeen.put(label, curr_line);
    	  prev_line = curr_line;
    	  
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
    * f1 -> StmtList()
    * f2 -> "END"
    * f3 -> ( Procedure() )*
    * f4 -> <EOF>
    */
   public R visit(Goal n, A argu) {
      R _ret=null;
      
      curr_proc = new ProcedureClass();
      curr_proc.label = "MAIN";
      curr_proc.no_args = 0;
      
      lineNo++;
      n.f0.accept(this, argu);
      
      curr_proc.procLineNo = lineNo;
      
      n.f1.accept(this, argu);
      
      lineNo++;
      n.f2.accept(this, argu);
      
      curr_proc.proc_blocks = lines;
      curr_proc.tempMap = currProcTempMap;
      
      currProcTempMap = new HashMap<String, TempLiveRange>();
      lines = new ArrayList<BasicBlock>();
      
      
      if (curr_proc.doesCalls)
    	  curr_proc.stack_slots += 10;
      
      allProcedures.add(curr_proc);
      curr_proc = new ProcedureClass();

      
      n.f3.accept(this, argu);
      
      n.f4.accept(this, argu);
      
      livelinessAnalysis();
	  generateLiveRanges();
		
	  RegisterAllocation();
		
	  printProcLines();
	  
	  
	  
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
    * f4 -> StmtExp()
    */
   public R visit(Procedure n, A argu) {
      R _ret=null;
      
      curr_proc = new ProcedureClass();
      curr_proc.label = n.f0.f0.tokenImage;
      curr_proc.no_args = Integer.parseInt(n.f2.f0.tokenImage);
      print("Now in procedure: " + curr_proc.label + " Args: " + Integer.toString(curr_proc.no_args));

      lineNo++;
      curr_proc.procLineNo = lineNo;
//      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
//      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      
      curr_proc.proc_blocks = lines;
      curr_proc.tempMap = currProcTempMap;
      
      // Extra
//      for (int i=0; i<curr_proc.no_args; i++) {
//    	  if (! curr_proc.tempMap.containsKey(Integer.toString(i))) 
//    		  continue;
//    	  else {
//    		  curr_proc.proc_blocks.get(0).def.add(Integer.toString(i));
//    		  
//    		  // // Even more extra!!
////    		  curr_proc.proc_blocks.get(curr_proc.proc_blocks.size() -1).use.add(Integer.toString(i));
//    	  
////    	  if (! curr_proc.tempMap.containsKey(Integer.toString(i))) {
////        	  TempLiveRange newTemp = new TempLiveRange();
////        	  newTemp.tempNo = Integer.toString(i);
////        	  curr_proc.tempMap.put(Integer.toString(i), newTemp);
////          }
//    	  }
//      }
      
      currProcTempMap = new HashMap<String, TempLiveRange>();
      lines = new ArrayList<BasicBlock>();
      allProcedures.add(curr_proc);
      
      curr_proc = new ProcedureClass();
      prev_line = null;
      
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
    */
   public R visit(Stmt n, A argu) {	   
	   R _ret = null;
	   lineNo++;   
	   curr_line = new BasicBlock();
	   curr_line.lineNo = lineNo;
	   n.f0.accept(this, argu);
      
      return _ret;
   }

   /**
    * f0 -> "NOOP"
    */
   public R visit(NoOpStmt n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);

      curr_line.stmtType = "NOOP";
      
      if (prev_line != null) {
    	  prev_line.succ.add(curr_line);
      }
      
      prev_line = curr_line;
      lines.add(curr_line);
      
      return _ret;
   }

   /**
    * f0 -> "ERROR"
    */
   public R visit(ErrorStmt n, A argu) {
     R _ret=null;
     n.f0.accept(this, argu);

     curr_line.stmtType = "ERROR";
     
     if (prev_line != null) {
    	 prev_line.succ.add(curr_line);
     }
     
     prev_line = curr_line;
     lines.add(curr_line);
     return _ret;
   }

   /**
    * f0 -> "CJUMP"
    * f1 -> Temp()
    * f2 -> Label()
    */
   public R visit(CJumpStmt n, A argu) {
	   R _ret=null;
	   n.f0.accept(this, argu);
	   

	   curr_line.stmtType = "CJUMP";
	   
	   if (prev_line != null)
		   prev_line.succ.add(curr_line);
	   
	   String label = n.f2.f0.tokenImage;
		  
	   if (labelSeen.containsKey(label)) {
		   curr_line.succ.add(labelSeen.get(label));
	   }
	   else {
		   if (labelsToBeFilled.containsKey(label)) 
			   labelsToBeFilled.get(label).add(curr_line);	
		   else {
			   labelsToBeFilled.put(label, new ArrayList<BasicBlock>());
			   labelsToBeFilled.get(label).add(curr_line);
		   }
	   }
	   lines.add(curr_line);
	   
	   use = true;
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      prev_line = curr_line;
      return _ret;
   }

   /**
    * f0 -> "JUMP"
    * f1 -> Label()
    */
   public R visit(JumpStmt n, A argu) {
      R _ret=null;

	  curr_line.stmtType = "JUMP";
	  
	  if (prev_line != null)
		  prev_line.succ.add(curr_line);
	  
	  String label = n.f1.f0.tokenImage;
	  
	  if (labelSeen.containsKey(label)) {
		  curr_line.succ.add(labelSeen.get(label));
	  }
	  else {
		  if (labelsToBeFilled.containsKey(label)) 
			  labelsToBeFilled.get(label).add(curr_line);
		  else {
			  labelsToBeFilled.put(label, new ArrayList<BasicBlock>());
			  labelsToBeFilled.get(label).add(curr_line);
		  }
	  }
	  
	  prev_line = null;
	  lines.add(curr_line);
	  
	   
      n.f0.accept(this, argu);
//      n.f1.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "HSTORE"
    * f1 -> Temp()
    * f2 -> IntegerLiteral()
    * f3 -> Temp()
    */
   public R visit(HStoreStmt n, A argu) {
      R _ret=null;

      curr_line.stmtType = "HSTORE";
      
      if (prev_line != null)
    	  prev_line.succ.add(curr_line);
      
      use = true;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      
      lines.add(curr_line);
      prev_line = curr_line;
      
      return _ret;
   }

   /**
    * f0 -> "HLOAD"
    * f1 -> Temp()
    * f2 -> Temp()
    * f3 -> IntegerLiteral()
    */
   public R visit(HLoadStmt n, A argu) {
      R _ret=null;

      curr_line.stmtType = "HLOAD";
      
      if (prev_line != null)
    	  prev_line.succ.add(curr_line);
      
      n.f0.accept(this, argu);
      
      use = false;
      n.f1.accept(this, argu);
      
      use = true;
      n.f2.accept(this, argu);
      n.f3.accept(this, argu);
      
      lines.add(curr_line);
      prev_line = curr_line;
      
      return _ret;
   }

   /**
    * f0 -> "MOVE"
    * f1 -> Temp()
    * f2 -> Exp()
    */
   public R visit(MoveStmt n, A argu) {
      R _ret=null;

      curr_line.stmtType = "MOVE";
      
      if (prev_line != null)
    	  prev_line.succ.add(curr_line);
      
      n.f0.accept(this, argu);
      use = false;
      n.f1.accept(this, argu);
      use = true;
      n.f2.accept(this, argu);
      
      lines.add(curr_line);
      prev_line = curr_line;
      return _ret;
   }

   /**
    * f0 -> "PRINT"
    * f1 -> SimpleExp()
    */
   public R visit(PrintStmt n, A argu) {
      R _ret=null;

      curr_line.stmtType = "PRINT";
      
      if (prev_line != null)
    	  prev_line.succ.add(curr_line);
      
      n.f0.accept(this, argu);
      use = true;
      n.f1.accept(this, argu);
      
      lines.add(curr_line);
      prev_line = curr_line;
      return _ret;
   }

   /**
    * f0 -> Call()
    *       | HAllocate()
    *       | BinOp()
    *       | SimpleExp()
    */
   public R visit(Exp n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "BEGIN"
    * f1 -> StmtList()
    * f2 -> "RETURN"
    * f3 -> SimpleExp()
    * f4 -> "END"
    */
   public R visit(StmtExp n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      lineNo++;
      
      curr_line = new BasicBlock();
      curr_line.lineNo = lineNo;
      curr_line.stmtType = "BEGIN";
      
      prev_line = curr_line;
      
      n.f1.accept(this, argu);
      
      n.f2.accept(this, argu);
      lineNo++;
      
      use = true;
      n.f3.accept(this, argu);
      
      n.f4.accept(this, argu);
      lineNo++;
      
      prev_line = null;
      return _ret;
   }

   /**
    * f0 -> "CALL"
    * f1 -> SimpleExp()
    * f2 -> "("
    * f3 -> ( Temp() )*
    * f4 -> ")"
    */
   public R visit(Call n, A argu) {
      R _ret=null;
      curr_proc.doesCalls = true;
      print(curr_proc.label + ": " + "calls=true");
      
      n.f0.accept(this, argu);
      use = true;
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      use = true;
      n.f3.accept(this, argu);
      n.f4.accept(this, argu);
      
      if (curr_proc.maxCallArgs < n.f3.nodes.size())
    	  curr_proc.maxCallArgs = n.f3.nodes.size();
      
      return _ret;
   }

   /**
    * f0 -> "HALLOCATE"
    * f1 -> SimpleExp()
    */
   public R visit(HAllocate n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      use = true;
      n.f1.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> Operator()
    * f1 -> Temp()
    * f2 -> SimpleExp()
    */
   public R visit(BinOp n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      use = true;
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
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
      return _ret;
   }

   /**
    * f0 -> Temp()
    *       | IntegerLiteral()
    *       | Label()
    */
   public R visit(SimpleExp n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "TEMP"
    * f1 -> IntegerLiteral()
    */
   public R visit(Temp n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      n.f1.accept(this, argu);
      
      if (! currProcTempMap.containsKey(n.f1.f0.tokenImage)) {
    	  TempLiveRange newTemp = new TempLiveRange();
    	  newTemp.tempNo = n.f1.f0.tokenImage;
    	  currProcTempMap.put(n.f1.f0.tokenImage, newTemp);
      }
      
      if (use)
    	  curr_line.use.add(n.f1.f0.tokenImage);
      else
    	  curr_line.def.add(n.f1.f0.tokenImage);
      
      return _ret;
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public R visit(IntegerLiteral n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public R visit(Label n, A argu) {
      R _ret=null;
      n.f0.accept(this, argu);
      return (R)n.f0.tokenImage;
   }

}