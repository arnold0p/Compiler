// generated with ast extension for cup
// version 0.8
// 6/1/2019 20:8:14


package rs.ac.bg.etf.pp1.ast;

public class ConstDeclsClass extends DeclList {

    private ConstDecls ConstDecls;

    public ConstDeclsClass (ConstDecls ConstDecls) {
        this.ConstDecls=ConstDecls;
        if(ConstDecls!=null) ConstDecls.setParent(this);
    }

    public ConstDecls getConstDecls() {
        return ConstDecls;
    }

    public void setConstDecls(ConstDecls ConstDecls) {
        this.ConstDecls=ConstDecls;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ConstDecls!=null) ConstDecls.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstDecls!=null) ConstDecls.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstDecls!=null) ConstDecls.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstDeclsClass(\n");

        if(ConstDecls!=null)
            buffer.append(ConstDecls.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstDeclsClass]");
        return buffer.toString();
    }
}
