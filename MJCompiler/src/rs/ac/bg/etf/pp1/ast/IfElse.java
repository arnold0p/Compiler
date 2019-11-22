// generated with ast extension for cup
// version 0.8
// 6/1/2019 20:8:14


package rs.ac.bg.etf.pp1.ast;

public class IfElse extends IfStatement {

    private ConditionIf ConditionIf;
    private IfStmtJmp IfStmtJmp;
    private ElseStmt ElseStmt;

    public IfElse (ConditionIf ConditionIf, IfStmtJmp IfStmtJmp, ElseStmt ElseStmt) {
        this.ConditionIf=ConditionIf;
        if(ConditionIf!=null) ConditionIf.setParent(this);
        this.IfStmtJmp=IfStmtJmp;
        if(IfStmtJmp!=null) IfStmtJmp.setParent(this);
        this.ElseStmt=ElseStmt;
        if(ElseStmt!=null) ElseStmt.setParent(this);
    }

    public ConditionIf getConditionIf() {
        return ConditionIf;
    }

    public void setConditionIf(ConditionIf ConditionIf) {
        this.ConditionIf=ConditionIf;
    }

    public IfStmtJmp getIfStmtJmp() {
        return IfStmtJmp;
    }

    public void setIfStmtJmp(IfStmtJmp IfStmtJmp) {
        this.IfStmtJmp=IfStmtJmp;
    }

    public ElseStmt getElseStmt() {
        return ElseStmt;
    }

    public void setElseStmt(ElseStmt ElseStmt) {
        this.ElseStmt=ElseStmt;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ConditionIf!=null) ConditionIf.accept(visitor);
        if(IfStmtJmp!=null) IfStmtJmp.accept(visitor);
        if(ElseStmt!=null) ElseStmt.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConditionIf!=null) ConditionIf.traverseTopDown(visitor);
        if(IfStmtJmp!=null) IfStmtJmp.traverseTopDown(visitor);
        if(ElseStmt!=null) ElseStmt.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConditionIf!=null) ConditionIf.traverseBottomUp(visitor);
        if(IfStmtJmp!=null) IfStmtJmp.traverseBottomUp(visitor);
        if(ElseStmt!=null) ElseStmt.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("IfElse(\n");

        if(ConditionIf!=null)
            buffer.append(ConditionIf.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(IfStmtJmp!=null)
            buffer.append(IfStmtJmp.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ElseStmt!=null)
            buffer.append(ElseStmt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [IfElse]");
        return buffer.toString();
    }
}
