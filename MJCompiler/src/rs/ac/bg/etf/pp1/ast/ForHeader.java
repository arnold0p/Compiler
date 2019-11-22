// generated with ast extension for cup
// version 0.8
// 6/1/2019 20:8:14


package rs.ac.bg.etf.pp1.ast;

public class ForHeader implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private DesignatorStatementOpt DesignatorStatementOpt;
    private ConditionOpt ConditionOpt;
    private DesignatorStatementOpt DesignatorStatementOpt1;

    public ForHeader (DesignatorStatementOpt DesignatorStatementOpt, ConditionOpt ConditionOpt, DesignatorStatementOpt DesignatorStatementOpt1) {
        this.DesignatorStatementOpt=DesignatorStatementOpt;
        if(DesignatorStatementOpt!=null) DesignatorStatementOpt.setParent(this);
        this.ConditionOpt=ConditionOpt;
        if(ConditionOpt!=null) ConditionOpt.setParent(this);
        this.DesignatorStatementOpt1=DesignatorStatementOpt1;
        if(DesignatorStatementOpt1!=null) DesignatorStatementOpt1.setParent(this);
    }

    public DesignatorStatementOpt getDesignatorStatementOpt() {
        return DesignatorStatementOpt;
    }

    public void setDesignatorStatementOpt(DesignatorStatementOpt DesignatorStatementOpt) {
        this.DesignatorStatementOpt=DesignatorStatementOpt;
    }

    public ConditionOpt getConditionOpt() {
        return ConditionOpt;
    }

    public void setConditionOpt(ConditionOpt ConditionOpt) {
        this.ConditionOpt=ConditionOpt;
    }

    public DesignatorStatementOpt getDesignatorStatementOpt1() {
        return DesignatorStatementOpt1;
    }

    public void setDesignatorStatementOpt1(DesignatorStatementOpt DesignatorStatementOpt1) {
        this.DesignatorStatementOpt1=DesignatorStatementOpt1;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(DesignatorStatementOpt!=null) DesignatorStatementOpt.accept(visitor);
        if(ConditionOpt!=null) ConditionOpt.accept(visitor);
        if(DesignatorStatementOpt1!=null) DesignatorStatementOpt1.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(DesignatorStatementOpt!=null) DesignatorStatementOpt.traverseTopDown(visitor);
        if(ConditionOpt!=null) ConditionOpt.traverseTopDown(visitor);
        if(DesignatorStatementOpt1!=null) DesignatorStatementOpt1.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(DesignatorStatementOpt!=null) DesignatorStatementOpt.traverseBottomUp(visitor);
        if(ConditionOpt!=null) ConditionOpt.traverseBottomUp(visitor);
        if(DesignatorStatementOpt1!=null) DesignatorStatementOpt1.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ForHeader(\n");

        if(DesignatorStatementOpt!=null)
            buffer.append(DesignatorStatementOpt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ConditionOpt!=null)
            buffer.append(ConditionOpt.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(DesignatorStatementOpt1!=null)
            buffer.append(DesignatorStatementOpt1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ForHeader]");
        return buffer.toString();
    }
}
