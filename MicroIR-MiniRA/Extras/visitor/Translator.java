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
public class Translator extends GJNoArguDepthFirst<String> {
   //
   // Auto class visitors--probably don't need to be overridden.
   //

   public Proc proc;
   public int maxCallArgs = 0;
   // int currTempCount = 0;
   public int stackSpace;
   
   HashMap<String, String> locMap = new HashMap<String, String>();
   HashMap<String, String> stackMap = new HashMap<String, String>();
   ArrayList<LiveRange> argMap = new ArrayList<LiveRange>();
   HashSet<String> used;

   public ArrayList<String> fns; 
   public String label_prefix;
   
   String pretext = "";
   String endText = "";
   boolean isStacked = false;
   boolean lookup	 = true;
   boolean iscall = false;
   
   boolean v0_used = false;
   boolean v1_used = false;

   public String genGenSave(int beg, String c, int lim){
	   String s = "";
	   int count = beg;
	   for(int i=0; i<=lim; i++)
		   if(used.contains(c + Integer.toString(i))){
			   s += "\tASTORE SPILLEDARG " + Integer.toString(count) + " " + c + Integer.toString(i) + "\n";
			   count++;
		   }
			   
	   return s;
   }
   
   public String genGenRestore(int beg, String c, int lim){
	   String s = "";
	   int count = beg;
	   for(int i=0; i<=lim; i++)
		   if(used.contains(c + Integer.toString(i))){
			   s += "\tALOAD " + c + Integer.toString(i) + " SPILLEDARG " + Integer.toString(count) + "\n";
			   count++;
		   }
			   
	   return s;
   }
   
   public String genCalleeSave(int beg){
	   return genGenSave(beg, "s", 7);
	}
   
   public String genCalleeRestore(int beg){
	   return genGenRestore(beg, "s", 7);
   }
   
   public String genCallerSave(int beg){
	   return genGenSave(beg, "t", 9) + genGenSave(beg+10, "a", 3); 
   }
   
   public String genCallerRestore(int beg){
	   return genGenRestore(beg, "t", 9) + genGenRestore(beg+10, "a", 3);
   }
   
   public String freeStackReg(){
	   if(!v0_used){
		   v0_used = true;
		   return "v0 ";
	   }
	   else if(!v1_used){
		   v1_used = true;
		   return "v1 ";		   
	   }
	   else{
		   refreshStackReg();
		   return freeStackReg();
	   }
   }
   
   public void refreshStackReg(){
	   v0_used = false; 
	   v1_used = false;
   }
   
   public void buildHash(){
	   for(LiveRange l: proc.liveRanges)
		   if(!l.stacked)
			   locMap.put(l.temp, l.location);
		   else
			   stackMap.put(l.temp, Integer.toString(l.stackLoc));
   }

   public void addPretext(String s){
	   pretext += s + "\n";
   }
   
   public void addPretextTop(String s){
	   pretext = s + "\n" + pretext;
   }
   
   public String extractPretext(){
	   String temp = pretext;
	   pretext = "";
	   if(temp == "")
		   return temp;
	   else
		   return temp + "\t";
   }
   
   public String visit(NodeList n) {
      String _ret="";
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         _ret += e.nextElement().accept(this);
         _count++;
      }
      return _ret;
   }

   public String visit(NodeListOptional n) {
      if ( n.present() ) {
         String _ret="";
         int _count=0;
         String tab = "";
         for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            if(iscall){
            	if(_count <= 3)
                	addPretext(tab + "MOVE a" + Integer.toString(_count) + " " + e.nextElement().accept(this));
                else{
                	addPretext("\tPASSARG " + Integer.toString(_count-3) + " " + e.nextElement().accept(this));
                }
                _count++;
                if(maxCallArgs < _count)
                	maxCallArgs = _count;
            }
            else
            	_ret += e.nextElement().accept(this);
            tab = "\t";
         }
         return _ret;
      }
      else
         return "";
   }

   public String visit(NodeOptional n) {
      if ( n.present() )
         return n.node.accept(this);
      else
         return "";
   }

   public String visit(NodeSequence n) {
      String _ret="";
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         _ret += e.nextElement().accept(this);
         _count++;
      }
      return _ret;
   }

   public String visit(NodeToken n) { return ""; }

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
   public String visit(Goal n) {
      String tmp = n.f1.accept(this);
	  String _ret="MAIN[0][" + Integer.toString(stackSpace+used.size()) + "][" + Integer.toString(maxCallArgs) + "]\n";
      _ret += tmp + "END\n";
      return _ret;
   }

   /**
    * f0 -> ( ( Label() )? Stmt() )*
    */
   public String visit(StmtList n) {
	  return n.f0.accept(this);
   }

   /**
    * f0 -> Label()
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> StmtExp()
    */
   public String visit(Procedure n) {
	  String tmp = n.f4.accept(this);
	   
	  String _ret = n.f0.accept(this) + "[";

	  String  arg = n.f2.accept(this);
	  int    argc = Integer.parseInt(arg.substring(0, arg.length()-1));
      _ret += Integer.toString(argc) + "][" + Integer.toString(stackSpace+used.size()) + "][" + Integer.toString(maxCallArgs)+"]\n";
      
      // Do callee save stuff here
      _ret += genCalleeSave(stackSpace);
      
      for(LiveRange l: argMap){
    	  if(l.stacked){
    		  _ret += "\tASTORE " + " SPILLEDARG " + l.stackLoc + " " + l.argLoc + "\n";
    	  }
    	  else{
    		  _ret += "\tMOVE " + l.location + " " + l.argLoc + "\n";
    	  }
      }
      
      _ret += tmp;
      // Do callee restore stuff here
      _ret += genCalleeRestore(stackSpace) + "END\n";
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
   public String visit(Stmt n) {
      refreshStackReg();
      return "\t" + n.f0.accept(this);
   }

   /**
    * f0 -> "NOOP"
    */
   public String visit(NoOpStmt n) {
      return "NOOP\n";
   }

   /**
    * f0 -> "ERROR"
    */
   public String visit(ErrorStmt n) {
      return "ERROR\n";
   }

   /**
    * f0 -> "CJUMP"
    * f1 -> Temp()
    * f2 -> Label()
    */
   public String visit(CJumpStmt n) {
      String _ret="CJUMP ";
      lookup = true;
      _ret += n.f1.accept(this) + " ";
      _ret += n.f2.accept(this) + "\n";
      lookup = false;
      _ret = extractPretext() + _ret;
      return _ret;
   }

   /**
    * f0 -> "JUMP"
    * f1 -> Label()
    */
   public String visit(JumpStmt n) {
      String _ret="JUMP ";
      _ret += n.f1.accept(this) + "\n";
      return _ret;
   }

   /**
    * f0 -> "HSTORE"
    * f1 -> Temp()
    * f2 -> IntegerLiteral()
    * f3 -> Temp()
    */
   public String visit(HStoreStmt n) {
      String _ret="HSTORE ";
      lookup = true;
      _ret += n.f1.accept(this) + " ";
      _ret += n.f2.accept(this) + " ";
      _ret += n.f3.accept(this) + "\n";
      lookup = false;
      _ret = extractPretext()   + _ret;
      return _ret;
   }

   /**
    * f0 -> "HLOAD"
    * f1 -> Temp()
    * f2 -> Temp()
    * f3 -> IntegerLiteral()
    */
   public String visit(HLoadStmt n) {
      String _ret="";
      
      lookup = false;
      String s = n.f1.accept(this);
      lookup = true;
      if(isStacked){
    	  v0_used = true;
          _ret += "HLOAD v0 " + n.f2.accept(this) + " " + n.f3.accept(this) + "\n";
          _ret += "ASTORE " + s + " v0";
          _ret = extractPretext() + _ret;  
      }
      else{
    	  _ret += "HLOAD " + s + " " + n.f2.accept(this) + " " + n.f3.accept(this) + "\n";
          _ret = extractPretext() + _ret;
      }
      lookup = false;
      
      return _ret;
   }

   /**
    * f0 -> "MOVE"
    * f1 -> Temp()
    * f2 -> Exp()
    */
   public String visit(MoveStmt n) {
      String _ret="";

      lookup = false;
      String s = n.f1.accept(this) + " ";
      lookup= true;
      if(isStacked){
    	  _ret = "MOVE a0 " + n.f2.accept(this) + "\n";
    	  _ret += "\tASTORE " + s + " a0\n";
    	  _ret = extractPretext() + _ret;
      }
      else{
    	  _ret = "MOVE " + s + " " + n.f2.accept(this) + "\n";
    	  _ret = extractPretext() + _ret;
      }
      lookup=false;
      
      _ret += endText;
      endText = "";
      return _ret;
   }

   /**
    * f0 -> "PRINT"
    * f1 -> SimpleExp()
    */
   public String visit(PrintStmt n) {
      String _ret="PRINT ";
      lookup = true;
      _ret += n.f1.accept(this);
      lookup = false;
      String extract = extractPretext();
	  _ret = extract + _ret + "\n";

	  return _ret;
   }

   /**
    * f0 -> Call()
    *       | HAllocate()
    *       | BinOp()
    *       | SimpleExp()
    */
   public String visit(Exp n) {
      return n.f0.accept(this);
   }

   /**
    * f0 -> "BEGIN"
    * f1 -> StmtList()
    * f2 -> "RETURN"
    * f3 -> SimpleExp()
    * f4 -> "END"
    */
   public String visit(StmtExp n) {
      String _ret="";
      _ret += n.f1.accept(this);
      if(pretext != ""){
    	  System.out.println("Sanity Check Error 2");
    	  System.exit(0);
      }
      lookup = true;
      String s = n.f3.accept(this);
      lookup = false;
      v0_used = true;
      _ret = _ret + extractPretext() + "\tMOVE v0 " + s + "\n";
      return _ret;
   }

   /**
    * f0 -> "CALL"
    * f1 -> SimpleExp()
    * f2 -> "("
    * f3 -> ( Temp() )*
    * f4 -> ")"
    */
   public String visit(Call n) {
	  String _ret="\tCALL ";
      lookup = true;
      _ret += n.f1.accept(this);
      iscall = true;
      n.f3.accept(this);
      iscall = false;
      // Do caller save stuff here
      int scount = 0;
      for(String s : used)
    	  if(s.startsWith("s"))
    		  scount++;
      _ret = extractPretext() + genCallerSave(stackSpace+scount) + _ret;
      lookup = false;
      // Do caller restore stuff here
      _ret += "\n" + genCallerRestore(stackSpace+scount);
      addPretext(_ret);
      return "v0 ";
   }

   /**
    * f0 -> "HALLOCATE"
    * f1 -> SimpleExp()
    */
   public String visit(HAllocate n) {
      String _ret="HALLOCATE ";
      lookup = true;
      _ret += n.f1.accept(this);
      lookup = false;
      // _ret = extractPretext() + _ret;
      return _ret;
   }

   /**
    * f0 -> Operator()
    * f1 -> Temp()
    * f2 -> SimpleExp()
    */
   public String visit(BinOp n) {
      String _ret = n.f0.accept(this);
      
      if(_ret != "LT " && _ret != "BITXOR "){
    	  lookup = true;
          _ret += n.f1.accept(this) + " ";
//          _ret = extractPretext() + _ret;
          _ret += n.f2.accept(this);
//          _ret = extractPretext() + _ret;
          lookup = false;
      }
      else if(_ret == "BITXOR "){
    	  lookup = true;
    	  String t1 = n.f1.accept(this);
    	  String t2 = n.f2.accept(this);
    	  lookup = false;
    	  
    	  addPretext("\tMOVE a2 " + t2);
    	  addPretext("\tMOVE a0 LT a2" + " " + t1);
    	  addPretext("\tMOVE a1 LT " + t1 + " " + t2);
    	  addPretext("\tMOVE a0 PLUS a0 a1\n");
    	  _ret = "a0\n";
      }
      else{
    	  lookup = true;
    	  String t1 = n.f1.accept(this);
    	  String t2 = n.f2.accept(this);
    	  _ret += t1 + " " + t2;
    	  lookup = false;
    	  addPretext("MOVE " + t1 + " MINUS " + t1 + " 1");
    	  endText = "\tMOVE " + t1 + " PLUS " + t1 + " 1\n";
      }      
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
   public String visit(Operator n) {
	  String op = ((NodeToken)n.f0.choice).tokenImage;
	  if(op != "LE" && op != "NE")
		  return ((NodeToken)n.f0.choice).tokenImage + " ";
	  // Check LE and NE 
	  else if(op == "NE"){
		  return "BITXOR ";
	  }
	  else{
		  return "LT ";
	  }
   }

   /**
    * f0 -> Temp()
    *       | IntegerLiteral()
    *       | Label()
    */
   public String visit(SimpleExp n) {
	   return n.f0.accept(this);
   }
      
   /**
    * f0 -> "TEMP"
    * f1 -> IntegerLiteral()
    */
   public String visit(Temp n) {
	   if(locMap.containsKey(n.f1.f0.tokenImage)){
		   isStacked = false;
		   return locMap.get(n.f1.f0.tokenImage);
	   }
	   else{
		   isStacked = true;
		   if(lookup){
			   String stackReg = freeStackReg() + " ";
			   addPretext("ALOAD " + stackReg + " SPILLEDARG " + stackMap.get(n.f1.f0.tokenImage));
			   return stackReg;
		   }
		   else{
			   return "SPILLEDARG " + stackMap.get(n.f1.f0.tokenImage) + " ";
		   }
	   }	   
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public String visit(IntegerLiteral n) {
	   return n.f0.tokenImage + " ";
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public String visit(Label n) {
	   if(fns.contains(n.f0.tokenImage))
		   return n.f0.tokenImage + " ";
	   else
		   return label_prefix + n.f0.tokenImage + " ";
   }
}
