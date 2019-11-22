// generated with ast extension for cup
// version 0.8
// 6/1/2019 20:8:14


package rs.ac.bg.etf.pp1.ast;

public class VarDeclListCClass extends VarDeclListC {

    private VarDeclListC VarDeclListC;
    private VarDeclsC VarDeclsC;

    public VarDeclListCClass (VarDeclListC VarDeclListC, VarDeclsC VarDeclsC) {
        this.VarDeclListC=VarDeclListC;
        if(VarDeclListC!=null) VarDeclListC.setParent(this);
        this.VarDeclsC=VarDeclsC;
        if(VarDeclsC!=null) VarDeclsC.setParent(this);
    }

    public VarDeclListC getVarDeclListC() {
        return VarDeclListC;
    }

    public void setVarDeclListC(VarDeclListC VarDeclListC) {
        this.VarDeclListC=VarDeclListC;
    }

    public VarDeclsC getVarDeclsC() {
        return VarDeclsC;
    }

    public void setVarDeclsC(VarDeclsC VarDeclsC) {
        this.VarDeclsC=VarDeclsC;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(VarDeclListC!=null) VarDeclListC.accept(visitor);
        if(VarDeclsC!=null) VarDeclsC.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarDeclListC!=null) VarDeclListC.traverseTopDown(visitor);
        if(VarDeclsC!=null) VarDeclsC.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarDeclListC!=null) VarDeclListC.traverseBottomUp(visitor);
        if(VarDeclsC!=null) VarDeclsC.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarDeclListCClass(\n");

        if(VarDeclListC!=null)
            buffer.append(VarDeclListC.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarDeclsC!=null)
            buffer.append(VarDeclsC.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarDeclListCClass]");
        return buffer.toString();
    }
}
