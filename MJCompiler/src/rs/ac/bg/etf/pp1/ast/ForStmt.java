// generated with ast extension for cup
// version 0.8
// 6/1/2019 20:8:14


package rs.ac.bg.etf.pp1.ast;

public class ForStmt extends Statement {

    private ForStatement ForStatement;

    public ForStmt (ForStatement ForStatement) {
        this.ForStatement=ForStatement;
        if(ForStatement!=null) ForStatement.setParent(this);
    }

    public ForStatement getForStatement() {
        return ForStatement;
    }

    public void setForStatement(ForStatement ForStatement) {
        this.ForStatement=ForStatement;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ForStatement!=null) ForStatement.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ForStatement!=null) ForStatement.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ForStatement!=null) ForStatement.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ForStmt(\n");

        if(ForStatement!=null)
            buffer.append(ForStatement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ForStmt]");
        return buffer.toString();
    }
}
