// generated with ast extension for cup
// version 0.8
// 6/1/2019 20:8:14


package rs.ac.bg.etf.pp1.ast;

public class EnumVarListClass extends EnumVarList {

    private EnumVarList EnumVarList;
    private EnumVar EnumVar;

    public EnumVarListClass (EnumVarList EnumVarList, EnumVar EnumVar) {
        this.EnumVarList=EnumVarList;
        if(EnumVarList!=null) EnumVarList.setParent(this);
        this.EnumVar=EnumVar;
        if(EnumVar!=null) EnumVar.setParent(this);
    }

    public EnumVarList getEnumVarList() {
        return EnumVarList;
    }

    public void setEnumVarList(EnumVarList EnumVarList) {
        this.EnumVarList=EnumVarList;
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
        if(EnumVarList!=null) EnumVarList.accept(visitor);
        if(EnumVar!=null) EnumVar.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(EnumVarList!=null) EnumVarList.traverseTopDown(visitor);
        if(EnumVar!=null) EnumVar.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(EnumVarList!=null) EnumVarList.traverseBottomUp(visitor);
        if(EnumVar!=null) EnumVar.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("EnumVarListClass(\n");

        if(EnumVarList!=null)
            buffer.append(EnumVarList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(EnumVar!=null)
            buffer.append(EnumVar.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [EnumVarListClass]");
        return buffer.toString();
    }
}
