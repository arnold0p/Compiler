// generated with ast extension for cup
// version 0.8
// 6/1/2019 20:8:14


package rs.ac.bg.etf.pp1.ast;

public class SingleEnumVar extends EnumVarList {

    private EnumVar EnumVar;

    public SingleEnumVar (EnumVar EnumVar) {
        this.EnumVar=EnumVar;
        if(EnumVar!=null) EnumVar.setParent(this);
    }

    public EnumVar getEnumVar() {
        return EnumVar;
    }

    public void setEnumVar(EnumVar EnumVar) {
        this.EnumVar=EnumVar;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(EnumVar!=null) EnumVar.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(EnumVar!=null) EnumVar.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(EnumVar!=null) EnumVar.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("SingleEnumVar(\n");

        if(EnumVar!=null)
            buffer.append(EnumVar.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [SingleEnumVar]");
        return buffer.toString();
    }
}
