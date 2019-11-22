// generated with ast extension for cup
// version 0.8
// 6/1/2019 20:8:14


package rs.ac.bg.etf.pp1.ast;

public class EnumDeclsClass extends DeclList {

    private EnumDecls EnumDecls;

    public EnumDeclsClass (EnumDecls EnumDecls) {
        this.EnumDecls=EnumDecls;
        if(EnumDecls!=null) EnumDecls.setParent(this);
    }

    public EnumDecls getEnumDecls() {
        return EnumDecls;
    }

    public void setEnumDecls(EnumDecls EnumDecls) {
        this.EnumDecls=EnumDecls;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(EnumDecls!=null) EnumDecls.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(EnumDecls!=null) EnumDecls.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(EnumDecls!=null) EnumDecls.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("EnumDeclsClass(\n");

        if(EnumDecls!=null)
            buffer.append(EnumDecls.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [EnumDeclsClass]");
        return buffer.toString();
    }
}
