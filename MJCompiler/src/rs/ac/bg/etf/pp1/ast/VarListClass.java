// generated with ast extension for cup
// version 0.8
// 6/1/2019 20:8:14


package rs.ac.bg.etf.pp1.ast;

public class VarListClass extends VarList {

    private VarList VarList;
    private Vard Vard;

    public VarListClass (VarList VarList, Vard Vard) {
        this.VarList=VarList;
        if(VarList!=null) VarList.setParent(this);
        this.Vard=Vard;
        if(Vard!=null) Vard.setParent(this);
    }

    public VarList getVarList() {
        return VarList;
    }

    public void setVarList(VarList VarList) {
        this.VarList=VarList;
    }

    public Vard getVard() {
        return Vard;
    }

    public void setVard(Vard Vard) {
        this.Vard=Vard;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(VarList!=null) VarList.accept(visitor);
        if(Vard!=null) Vard.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarList!=null) VarList.traverseTopDown(visitor);
        if(Vard!=null) Vard.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarList!=null) VarList.traverseBottomUp(visitor);
        if(Vard!=null) Vard.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarListClass(\n");

        if(VarList!=null)
            buffer.append(VarList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Vard!=null)
            buffer.append(Vard.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarListClass]");
        return buffer.toString();
    }
}
