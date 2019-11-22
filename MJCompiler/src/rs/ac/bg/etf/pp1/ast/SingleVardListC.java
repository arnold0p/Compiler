// generated with ast extension for cup
// version 0.8
// 6/1/2019 20:8:14


package rs.ac.bg.etf.pp1.ast;

public class SingleVardListC extends VarListC {

    private VardC VardC;

    public SingleVardListC (VardC VardC) {
        this.VardC=VardC;
        if(VardC!=null) VardC.setParent(this);
    }

    public VardC getVardC() {
        return VardC;
    }

    public void setVardC(VardC VardC) {
        this.VardC=VardC;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(VardC!=null) VardC.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VardC!=null) VardC.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VardC!=null) VardC.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("SingleVardListC(\n");

        if(VardC!=null)
            buffer.append(VardC.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [SingleVardListC]");
        return buffer.toString();
    }
}
