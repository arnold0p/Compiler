// generated with ast extension for cup
// version 0.8
// 6/1/2019 20:8:14


package rs.ac.bg.etf.pp1.ast;

public class DesignatorSufixListClass extends DesignatorSufixList {

    private DesignatorSufix DesignatorSufix;
    private DesignatorSufixList DesignatorSufixList;

    public DesignatorSufixListClass (DesignatorSufix DesignatorSufix, DesignatorSufixList DesignatorSufixList) {
        this.DesignatorSufix=DesignatorSufix;
        if(DesignatorSufix!=null) DesignatorSufix.setParent(this);
        this.DesignatorSufixList=DesignatorSufixList;
        if(DesignatorSufixList!=null) DesignatorSufixList.setParent(this);
    }

    public DesignatorSufix getDesignatorSufix() {
        return DesignatorSufix;
    }

    public void setDesignatorSufix(DesignatorSufix DesignatorSufix) {
        this.DesignatorSufix=DesignatorSufix;
    }

    public DesignatorSufixList getDesignatorSufixList() {
        return DesignatorSufixList;
    }

    public void setDesignatorSufixList(DesignatorSufixList DesignatorSufixList) {
        this.DesignatorSufixList=DesignatorSufixList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(DesignatorSufix!=null) DesignatorSufix.accept(visitor);
        if(DesignatorSufixList!=null) DesignatorSufixList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(DesignatorSufix!=null) DesignatorSufix.traverseTopDown(visitor);
        if(DesignatorSufixList!=null) DesignatorSufixList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(DesignatorSufix!=null) DesignatorSufix.traverseBottomUp(visitor);
        if(DesignatorSufixList!=null) DesignatorSufixList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DesignatorSufixListClass(\n");

        if(DesignatorSufix!=null)
            buffer.append(DesignatorSufix.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(DesignatorSufixList!=null)
            buffer.append(DesignatorSufixList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [DesignatorSufixListClass]");
        return buffer.toString();
    }
}
