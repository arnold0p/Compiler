// generated with ast extension for cup
// version 0.8
// 6/1/2019 20:8:14


package rs.ac.bg.etf.pp1.ast;

public class VardDeclsC extends VarDeclsC {

    private TypeVar TypeVar;
    private VarListC VarListC;

    public VardDeclsC (TypeVar TypeVar, VarListC VarListC) {
        this.TypeVar=TypeVar;
        if(TypeVar!=null) TypeVar.setParent(this);
        this.VarListC=VarListC;
        if(VarListC!=null) VarListC.setParent(this);
    }

    public TypeVar getTypeVar() {
        return TypeVar;
    }

    public void setTypeVar(TypeVar TypeVar) {
        this.TypeVar=TypeVar;
    }

    public VarListC getVarListC() {
        return VarListC;
    }

    public void setVarListC(VarListC VarListC) {
        this.VarListC=VarListC;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(TypeVar!=null) TypeVar.accept(visitor);
        if(VarListC!=null) VarListC.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(TypeVar!=null) TypeVar.traverseTopDown(visitor);
        if(VarListC!=null) VarListC.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(TypeVar!=null) TypeVar.traverseBottomUp(visitor);
        if(VarListC!=null) VarListC.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VardDeclsC(\n");

        if(TypeVar!=null)
            buffer.append(TypeVar.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarListC!=null)
            buffer.append(VarListC.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VardDeclsC]");
        return buffer.toString();
    }
}
