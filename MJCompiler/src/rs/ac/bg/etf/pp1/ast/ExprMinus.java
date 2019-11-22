// generated with ast extension for cup
// version 0.8
// 6/1/2019 20:8:14


package rs.ac.bg.etf.pp1.ast;

public class ExprMinus extends Expr {

    private MinusExpr MinusExpr;
    private Term Term;
    private AddopTermList AddopTermList;

    public ExprMinus (MinusExpr MinusExpr, Term Term, AddopTermList AddopTermList) {
        this.MinusExpr=MinusExpr;
        if(MinusExpr!=null) MinusExpr.setParent(this);
        this.Term=Term;
        if(Term!=null) Term.setParent(this);
        this.AddopTermList=AddopTermList;
        if(AddopTermList!=null) AddopTermList.setParent(this);
    }

    public MinusExpr getMinusExpr() {
        return MinusExpr;
    }

    public void setMinusExpr(MinusExpr MinusExpr) {
        this.MinusExpr=MinusExpr;
    }

    public Term getTerm() {
        return Term;
    }

    public void setTerm(Term Term) {
        this.Term=Term;
    }

    public AddopTermList getAddopTermList() {
        return AddopTermList;
    }

    public void setAddopTermList(AddopTermList AddopTermList) {
        this.AddopTermList=AddopTermList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(MinusExpr!=null) MinusExpr.accept(visitor);
        if(Term!=null) Term.accept(visitor);
        if(AddopTermList!=null) AddopTermList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MinusExpr!=null) MinusExpr.traverseTopDown(visitor);
        if(Term!=null) Term.traverseTopDown(visitor);
        if(AddopTermList!=null) AddopTermList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MinusExpr!=null) MinusExpr.traverseBottomUp(visitor);
        if(Term!=null) Term.traverseBottomUp(visitor);
        if(AddopTermList!=null) AddopTermList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ExprMinus(\n");

        if(MinusExpr!=null)
            buffer.append(MinusExpr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Term!=null)
            buffer.append(Term.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(AddopTermList!=null)
            buffer.append(AddopTermList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ExprMinus]");
        return buffer.toString();
    }
}
