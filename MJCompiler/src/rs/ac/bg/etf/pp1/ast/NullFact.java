// generated with ast extension for cup
// version 0.8
// 6/1/2019 20:8:14


package rs.ac.bg.etf.pp1.ast;

public class NullFact extends Factor {

    private String N1;

    public NullFact (String N1) {
        this.N1=N1;
    }

    public String getN1() {
        return N1;
    }

    public void setN1(String N1) {
        this.N1=N1;
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
        buffer.append("NullFact(\n");

        buffer.append(" "+tab+N1);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [NullFact]");
        return buffer.toString();
    }
}
