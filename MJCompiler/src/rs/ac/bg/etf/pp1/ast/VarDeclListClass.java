// generated with ast extension for cup
// version 0.8
// 6/1/2019 20:8:14


package rs.ac.bg.etf.pp1.ast;

public class VarDeclListClass extends VarDeclList {

    private VarDeclList VarDeclList;
    private VarDecls VarDecls;

    public VarDeclListClass (VarDeclList VarDeclList, VarDecls VarDecls) {
        this.VarDeclList=VarDeclList;
        if(VarDeclList!=null) VarDeclList.setParent(this);
        this.VarDecls=VarDecls;
        if(VarDecls!=null) VarDecls.setParent(this);
    }

    public VarDeclList getVarDeclList() {
        return VarDeclList;
    }

    public void setVarDeclList(VarDeclList VarDeclList) {
        this.VarDeclList=VarDeclList;
    }

    public VarDecls getVarDecls() {
        return VarDecls;
    }

    public void setVarDecls(VarDecls VarDecls) {
        this.VarDecls=VarDecls;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(VarDeclList!=null) VarDeclList.accept(visitor);
        if(VarDecls!=null) VarDecls.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarDeclList!=null) VarDeclList.traverseTopDown(visitor);
        if(VarDecls!=null) VarDecls.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarDeclList!=null) VarDeclList.traverseBottomUp(visitor);
        if(VarDecls!=null) VarDecls.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarDeclListClass(\n");

        if(VarDeclList!=null)
            buffer.append(VarDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarDecls!=null)
            buffer.append(VarDecls.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarDeclListClass]");
        return buffer.toString();
    }
}
