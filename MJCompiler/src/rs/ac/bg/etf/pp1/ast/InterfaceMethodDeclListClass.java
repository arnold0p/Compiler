// generated with ast extension for cup
// version 0.8
// 6/1/2019 20:8:14


package rs.ac.bg.etf.pp1.ast;

public class InterfaceMethodDeclListClass extends InterfaceMethodDeclList {

    private InterfaceMethodDeclList InterfaceMethodDeclList;
    private InterfaceMethodDecl InterfaceMethodDecl;

    public InterfaceMethodDeclListClass (InterfaceMethodDeclList InterfaceMethodDeclList, InterfaceMethodDecl InterfaceMethodDecl) {
        this.InterfaceMethodDeclList=InterfaceMethodDeclList;
        if(InterfaceMethodDeclList!=null) InterfaceMethodDeclList.setParent(this);
        this.InterfaceMethodDecl=InterfaceMethodDecl;
        if(InterfaceMethodDecl!=null) InterfaceMethodDecl.setParent(this);
    }

    public InterfaceMethodDeclList getInterfaceMethodDeclList() {
        return InterfaceMethodDeclList;
    }

    public void setInterfaceMethodDeclList(InterfaceMethodDeclList InterfaceMethodDeclList) {
        this.InterfaceMethodDeclList=InterfaceMethodDeclList;
    }

    public InterfaceMethodDecl getInterfaceMethodDecl() {
        return InterfaceMethodDecl;
    }

    public void setInterfaceMethodDecl(InterfaceMethodDecl InterfaceMethodDecl) {
        this.InterfaceMethodDecl=InterfaceMethodDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(InterfaceMethodDeclList!=null) InterfaceMethodDeclList.accept(visitor);
        if(InterfaceMethodDecl!=null) InterfaceMethodDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(InterfaceMethodDeclList!=null) InterfaceMethodDeclList.traverseTopDown(visitor);
        if(InterfaceMethodDecl!=null) InterfaceMethodDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(InterfaceMethodDeclList!=null) InterfaceMethodDeclList.traverseBottomUp(visitor);
        if(InterfaceMethodDecl!=null) InterfaceMethodDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("InterfaceMethodDeclListClass(\n");

        if(InterfaceMethodDeclList!=null)
            buffer.append(InterfaceMethodDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(InterfaceMethodDecl!=null)
            buffer.append(InterfaceMethodDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [InterfaceMethodDeclListClass]");
        return buffer.toString();
    }
}
