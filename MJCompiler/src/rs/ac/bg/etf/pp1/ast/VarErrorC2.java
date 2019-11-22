// generated with ast extension for cup
// version 0.8
// 6/1/2019 20:8:14


package rs.ac.bg.etf.pp1.ast;

public class VarErrorC2 extends VarDeclsC {

    public VarErrorC2 () {
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarErrorC2(\n");

        buffer.append(tab);
        buffer.append(") [VarErrorC2]");
        return buffer.toString();
    }
}
