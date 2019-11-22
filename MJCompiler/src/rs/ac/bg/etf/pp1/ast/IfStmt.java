// generated with ast extension for cup
// version 0.8
// 6/1/2019 20:8:14


package rs.ac.bg.etf.pp1.ast;

public class IfStmt extends Statement {

    private IfStatement IfStatement;

    public IfStmt (IfStatement IfStatement) {
        this.IfStatement=IfStatement;
        if(IfStatement!=null) IfStatement.setParent(this);
    }

    public IfStatement getIfStatement() {
        return IfStatement;
    }

    public void setIfStatement(IfStatement IfStatement) {
        this.IfStatement=IfStatement;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(IfStatement!=null) IfStatement.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(IfStatement!=null) IfStatement.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(IfStatement!=null) IfStatement.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("IfStmt(\n");

        if(IfStatement!=null)
            buffer.append(IfStatement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [IfStmt]");
        return buffer.toString();
    }
}
