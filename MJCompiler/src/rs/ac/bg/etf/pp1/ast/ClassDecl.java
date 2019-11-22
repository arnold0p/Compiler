// generated with ast extension for cup
// version 0.8
// 6/1/2019 20:8:14


package rs.ac.bg.etf.pp1.ast;

public class ClassDecl implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private ClassName ClassName;
    private Extends Extends;
    private Implements Implements;
    private VarDeclListC VarDeclListC;
    private ClassMethods ClassMethods;

    public ClassDecl (ClassName ClassName, Extends Extends, Implements Implements, VarDeclListC VarDeclListC, ClassMethods ClassMethods) {
        this.ClassName=ClassName;
        if(ClassName!=null) ClassName.setParent(this);
        this.Extends=Extends;
        if(Extends!=null) Extends.setParent(this);
        this.Implements=Implements;
        if(Implements!=null) Implements.setParent(this);
        this.VarDeclListC=VarDeclListC;
        if(VarDeclListC!=null) VarDeclListC.setParent(this);
        this.ClassMethods=ClassMethods;
        if(ClassMethods!=null) ClassMethods.setParent(this);
    }

    public ClassName getClassName() {
        return ClassName;
    }

    public void setClassName(ClassName ClassName) {
        this.ClassName=ClassName;
    }

    public Extends getExtends() {
        return Extends;
    }

    public void setExtends(Extends Extends) {
        this.Extends=Extends;
    }

    public Implements getImplements() {
        return Implements;
    }

    public void setImplements(Implements Implements) {
        this.Implements=Implements;
    }

    public VarDeclListC getVarDeclListC() {
        return VarDeclListC;
    }

    public void setVarDeclListC(VarDeclListC VarDeclListC) {
        this.VarDeclListC=VarDeclListC;
    }

    public ClassMethods getClassMethods() {
        return ClassMethods;
    }

    public void setClassMethods(ClassMethods ClassMethods) {
        this.ClassMethods=ClassMethods;
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
        if(ClassName!=null) ClassName.accept(visitor);
        if(Extends!=null) Extends.accept(visitor);
        if(Implements!=null) Implements.accept(visitor);
        if(VarDeclListC!=null) VarDeclListC.accept(visitor);
        if(ClassMethods!=null) ClassMethods.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ClassName!=null) ClassName.traverseTopDown(visitor);
        if(Extends!=null) Extends.traverseTopDown(visitor);
        if(Implements!=null) Implements.traverseTopDown(visitor);
        if(VarDeclListC!=null) VarDeclListC.traverseTopDown(visitor);
        if(ClassMethods!=null) ClassMethods.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ClassName!=null) ClassName.traverseBottomUp(visitor);
        if(Extends!=null) Extends.traverseBottomUp(visitor);
        if(Implements!=null) Implements.traverseBottomUp(visitor);
        if(VarDeclListC!=null) VarDeclListC.traverseBottomUp(visitor);
        if(ClassMethods!=null) ClassMethods.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ClassDecl(\n");

        if(ClassName!=null)
            buffer.append(ClassName.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Extends!=null)
            buffer.append(Extends.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Implements!=null)
            buffer.append(Implements.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarDeclListC!=null)
            buffer.append(VarDeclListC.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ClassMethods!=null)
            buffer.append(ClassMethods.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ClassDecl]");
        return buffer.toString();
    }
}
