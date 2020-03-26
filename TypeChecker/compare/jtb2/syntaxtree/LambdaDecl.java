//
// Generated by JTB 1.2
//

package syntaxtree;

/**
 * Grammar production:
 * f0 -> "("
 * f1 -> "lambda"
 * f2 -> "("
 * f3 -> ( <IDENT> )*
 * f4 -> ")"
 * f5 -> Exp()
 * f6 -> ")"
 */
public class LambdaDecl implements Node {
   public NodeToken f0;
   public NodeToken f1;
   public NodeToken f2;
   public NodeListOptional f3;
   public NodeToken f4;
   public Exp f5;
   public NodeToken f6;

   public LambdaDecl(NodeToken n0, NodeToken n1, NodeToken n2, NodeListOptional n3, NodeToken n4, Exp n5, NodeToken n6) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
      f4 = n4;
      f5 = n5;
      f6 = n6;
   }

   public LambdaDecl(NodeListOptional n0, Exp n1) {
      f0 = new NodeToken("(");
      f1 = new NodeToken("lambda");
      f2 = new NodeToken("(");
      f3 = n0;
      f4 = new NodeToken(")");
      f5 = n1;
      f6 = new NodeToken(")");
   }

   public void accept(visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

