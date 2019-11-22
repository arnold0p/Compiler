// generated with ast extension for cup
// version 0.8
// 6/1/2019 20:8:14


package rs.ac.bg.etf.pp1.ast;

public class EnumDecls implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private EnumName EnumName;
    private EnumVarList EnumVarList;

    public EnumDecls (EnumName EnumName, EnumVarList EnumVarList) {
        this.EnumName=EnumName;
        if(EnumName!=null) EnumName.setParent(this);
        this.EnumVarList=EnumVarList;
        if(EnumVarList!=null) EnumVarList.setParent(this);
    }

    public EnumName getEnumName() {
        return EnumName;
    }

    public void setEnumName(EnumName EnumName) {
        this.EnumName=EnumName;
    }

    public EnumVarList getEnumVarList() {
        return EnumVarList;
    }

    public void setEnumVarList(EnumVarList EnumVarList) {
        this.EnumVarList=EnumVarList;
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
        if(EnumName!=null) EnumName.accept(visitor);
        if(EnumVarList!=null) EnumVarList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(EnumName!=null) EnumName.traverseTopDown(visitor);
        if(EnumVarList!=null) EnumVarList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(EnumName!=null) EnumName.traverseBottomUp(visitor);
        if(EnumVarList!=null) EnumVarList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("EnumDecls(\n");

        if(EnumName!=null)
            buffer.append(EnumName.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(EnumVarList!=null)
            buffer.append(EnumVarList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [EnumDecls]");
        return buffer.toString();
    }
}
