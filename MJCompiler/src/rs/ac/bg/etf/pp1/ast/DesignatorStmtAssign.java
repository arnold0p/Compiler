// generated with ast extension for cup
// version 0.8
// 6/1/2019 20:8:14


package rs.ac.bg.etf.pp1.ast;

public class DesignatorStmtAssign extends DesignatorStatement {

    private DesignatorOrError DesignatorOrError;

    public DesignatorStmtAssign (DesignatorOrError DesignatorOrError) {
        this.DesignatorOrError=DesignatorOrError;
        if(DesignatorOrError!=null) DesignatorOrError.setParent(this);
    }

    public DesignatorOrError getDesignatorOrError() {
        return DesignatorOrError;
    }

    public void setDesignatorOrError(DesignatorOrError DesignatorOrError) {
        this.DesignatorOrError=DesignatorOrError;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(DesignatorOrError!=null) DesignatorOrError.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(DesignatorOrError!=null) DesignatorOrError.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(DesignatorOrError!=null) DesignatorOrError.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DesignatorStmtAssign(\n");

        if(DesignatorOrError!=null)
            buffer.append(DesignatorOrError.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [DesignatorStmtAssign]");
        return buffer.toString();
    }
}
