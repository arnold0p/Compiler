// generated with ast extension for cup
// version 0.8
// 6/1/2019 20:8:14


package rs.ac.bg.etf.pp1.ast;

public class SingleVarClass extends VarList {

    private Vard Vard;

    public SingleVarClass (Vard Vard) {
        this.Vard=Vard;
        if(Vard!=null) Vard.setParent(this);
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
        if(Vard!=null) Vard.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Vard!=null) Vard.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Vard!=null) Vard.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("SingleVarClass(\n");

        if(Vard!=null)
            buffer.append(Vard.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [SingleVarClass]");
        return buffer.toString();
    }
}
