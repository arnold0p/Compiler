// generated with ast extension for cup
// version 0.8
// 6/1/2019 20:8:14


package rs.ac.bg.etf.pp1.ast;

public class SingleConstDecl extends ConstDecl {

    private String constName;
    private ConstVar ConstVar;

    public SingleConstDecl (String constName, ConstVar ConstVar) {
        this.constName=constName;
        this.ConstVar=ConstVar;
        if(ConstVar!=null) ConstVar.setParent(this);
    }

    public String getConstName() {
        return constName;
    }

    public void setConstName(String constName) {
        this.constName=constName;
    }

    public ConstVar getConstVar() {
        return ConstVar;
    }

    public void setConstVar(ConstVar ConstVar) {
        this.ConstVar=ConstVar;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ConstVar!=null) ConstVar.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstVar!=null) ConstVar.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstVar!=null) ConstVar.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("SingleConstDecl(\n");

        buffer.append(" "+tab+constName);
        buffer.append("\n");

        if(ConstVar!=null)
            buffer.append(ConstVar.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [SingleConstDecl]");
        return buffer.toString();
    }
}
