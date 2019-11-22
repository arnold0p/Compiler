package rs.ac.bg.etf.pp1;

import java.io.StreamCorruptedException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import rs.etf.pp1.symboltable.concepts.*;
import rs.etf.pp1.symboltable.*;
import rs.ac.bg.etf.pp1.ast.*;

public class SemanticAnalyzer extends VisitorAdaptor {
	int printCallCount = 0;
	int varDeclCount = 0;
	Obj currentMethod = null;
	boolean returnFound = false;
	boolean errorDetected = false;
	int nVars;
	
	
	int currentFor = 0;
	boolean mainVoid = false;
	
	
	int enumNum = 0;
	
	Struct currentVarType = null;
	Obj currentClass = null;
	
	ArrayList<Obj> currentMethodCall = new ArrayList<Obj>();
	ArrayList<Integer> currentActualParams = new ArrayList<Integer>();
	
	ArrayList<Obj> currentDesignator = new ArrayList<Obj>();
	
	ArrayList<Integer> isMethod = new ArrayList<Integer>();
	
	static Struct boolType = new Struct(Struct.Bool);
	
	static Struct enumType = new Struct(Struct.Enum);
	
	static Struct classType = new Struct(Struct.Class);
	static Struct interfaceType = new Struct(Struct.Interface);
	static Struct arrayType = new Struct(Struct.Array);
	
	Logger log = Logger.getLogger(getClass());

	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.error(msg.toString());
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message); 
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.info(msg.toString());
	}
	
	private Obj getCurrDes() {
		return currentDesignator.get(currentDesignator.size()-1);
	}
	
	private void resetCurrDes(Obj obj) {
		currentDesignator.remove(currentDesignator.size()-1);
		currentDesignator.add(obj);
	}
	
	private boolean checkEnumInt(Struct s1,Struct s2) {
		return ((s1!=null) && ((s1==Tab.intType && s2.getKind()==Struct.Enum) ||(s1.getKind()==Struct.Enum && s2==Tab.intType)));
	}
	
	 //pocetak programa
	public void visit(ProgName progName){
		Tab.insert(Obj.Type, "bool" , boolType);
		
    	progName.obj = Tab.insert(Obj.Prog, progName.getPName(), Tab.noType);
    	Tab.openScope();
    }
    
	//kraj programa
    public void visit(Program program){
    	nVars = Tab.currentScope.getnVars();
    	Tab.chainLocalSymbols(program.getProgName().obj);
    	Tab.closeScope();
    }
	
	
	//kad se vidi type za deklaraciju promenljivih
	public void visit(TypeVar typeVar) {
		if (typeVar.getType().struct != Tab.noType) {
			currentVarType = typeVar.getType().struct;
		}
		else {
			report_error("Greska na liniji " + typeVar.getLine() + " " + typeVar.getType().getTypeName() + " los tip "+ typeVar.getType().getTypeName(),null);
		}
	}
	

	
	// tipovi za konstante
	public void visit(NumConstClass numConstClass) {
		
		SingleConstDecl parent = (SingleConstDecl) numConstClass.getParent();
		
		if (Tab.intType == currentVarType) {
			//report_info("Deklarisana promenljiva "+ singleConstDecl.getConstName(), singleConstDecl);
			Obj obj = Tab.insert(Obj.Con, parent.getConstName(), currentVarType);
			
			if (obj !=Tab.noObj) {
				obj.setAdr(numConstClass.getN1());
			}
			else {
				report_error("Greska na liniji "  + parent.getLine() +" vec deklarisano "+ parent.getConstName() , null);
			}
		}
		else {
			report_error ("Greska na liniji " + parent.getLine() + ": neekvivalentni tipovi", null );
		}
		
		
	}
	public void visit(CharConstClass charConstClass) {
		
		SingleConstDecl parent = (SingleConstDecl) charConstClass.getParent();
		
		if (Tab.charType == currentVarType) {
			//report_info("Deklarisana promenljiva "+ singleConstDecl.getConstName(), singleConstDecl);
			Obj obj = Tab.insert(Obj.Con, parent.getConstName(), currentVarType);
			
			if (obj !=Tab.noObj) {
				obj.setAdr(charConstClass.getC1());
			}
			else {
				report_error("Greska na liniji "  + parent.getLine() +" vec deklarisano "+ parent.getConstName() , null);
			}
		}
		else {
			report_error ("Greska na liniji " + parent.getLine() + ": neekvivalentni tipovi", null );
		}
	}
	public void visit(BoolConstClass boolConstClass) {
	
		SingleConstDecl parent = (SingleConstDecl) boolConstClass.getParent();
		
		if (boolType == currentVarType) {
			//report_info("Deklarisana promenljiva "+ singleConstDecl.getConstName(), singleConstDecl);
			Obj obj = Tab.insert(Obj.Con, parent.getConstName(), currentVarType);
			
			if (obj !=Tab.noObj) {
				if (boolConstClass.getB1().equals("false"))
					obj.setAdr(0);
				else
					obj.setAdr(1);
			}
			else {
				report_error("Greska na liniji "  + parent.getLine() +" vec deklarisano "+ parent.getConstName() , null);
			}
		}
		else {
			report_error ("Greska na liniji " + parent.getLine() + ": neekvivalentni tipovi", null );
		}
		
		
	}
	
	// ime enuma
	public void visit(EnumName enumName) {
		Struct struct = new Struct(Struct.Enum);
		
		Obj obj = enumName.obj = Tab.insert(Obj.Type, enumName.getEnumName(), struct);
		
		if (obj==Tab.noObj)
			report_error("Greska na liniji "  + enumName.getLine() +" vec deklarisano "+ enumName.getEnumName() , null);
		
    	Tab.openScope();
    	enumNum = 0;
	}
	
	//ceo enum zavrsen
	public void visit(EnumDecls enumDecls) {
		Tab.chainLocalSymbols(enumDecls.getEnumName().obj.getType());
    	Tab.closeScope();
	}
	
	// provera unikatnosti za enum
	private boolean checkEnumNum() {
		
		Scope scope = Tab.currentScope();
		
		if (scope.getLocals()==null)
			return true;
		
		Object objs[] = scope.getLocals().symbols().toArray();
		
		for (int i=0;i<objs.length;i++) {
			Obj curr = (Obj) objs[i];
			
			if (curr.getAdr()==enumNum) {
				return false;
			}
		}
		
		return true;
	}
	
	// enum bez broja
	public void visit(EnumVarIdent enumVarIdent) {
		if (!checkEnumNum())
			report_error("Greska na liniji "  + enumVarIdent.getLine() +" enum vec sadrzi broj " + enumNum , null);
		
		Obj obj = Tab.insert(Obj.Con, enumVarIdent.getName(), Tab.intType);
		
		if (obj==Tab.noObj)
			report_error("Greska na liniji "  + enumVarIdent.getLine() +" vec deklarisano "+ enumVarIdent.getName() , null);		
		
		obj.setAdr(enumNum);
		
		enumNum++;
	}
	
	//enum sa brojem
	public void visit(EnumVarIdentEqNum enumVarIdentEqNum) {
		
		enumNum = enumVarIdentEqNum.getValue();
		if (!checkEnumNum())
			report_error("Greska na liniji "  + enumVarIdentEqNum.getLine() +" enum vec sadrzi broj " + enumNum , null);
		
		Obj obj= Tab.insert(Obj.Con, enumVarIdentEqNum.getName(), Tab.intType);
		
		if (obj==Tab.noObj)
			report_error("Greska na liniji "  + enumVarIdentEqNum.getLine() +" vec deklarisano "+ enumVarIdentEqNum.getName() , null);
		
		obj.setAdr(enumNum);
		
		enumNum++;
		
	}
	
	//kad se zavrsi cela deklaracija promenljivih
	public void visit(SingleVarDecls singleVarDecls) {
		currentVarType = null;
	}
	
	// obicna promenljiva
	public void visit(VardClass vardClass){
		varDeclCount++;
		
		Obj obj = Tab.insert(Obj.Var, vardClass.getVarName(), currentVarType);
		if (obj==Tab.noObj)
			report_error("Greska na liniji "  + vardClass.getLine() +" vec deklarisano "+ vardClass.getVarName() , null);
		//report_info("Pretraga na " + vardClass.getLine() + " nadjeno "+ vardClass.getVarName() + " " , null);
		
	}
	
	//nizovna promenljiva
	public void visit(VardClassSquares vardClassSquares) {
		
		
		Struct struct = new Struct(Struct.Array);
		struct.setElementType(currentVarType);	//elementi niza
		
		Obj obj = Tab.insert(Obj.Var, vardClassSquares.getVarName(), struct);
		if (obj==Tab.noObj)
			report_error("Greska na liniji "  + vardClassSquares.getLine() +" vec deklarisano "+ vardClassSquares.getVarName() , null);
		
		//report_info("Pretraga na " + vardClassSquares.getLine() + " nadjeno "+ vardClassSquares.getVarName() + " " , null);
		
	}
	
	// ime klase pocetak
	public void visit(ClassName className) {
		Struct currentClassStruct = new Struct(Struct.Class);
		currentClassStruct.setElementType(null);
		className.obj = Tab.insert(Obj.Type, className.getClassName(), currentClassStruct);
		currentClass = className.obj;
    	Tab.openScope();
    	Tab.insert(Obj.Fld, "%virtualtablestart", Tab.intType);
    	//if (obj==Tab.noObj)
		//	report_error("Greska na liniji "  + className.getLine() +" vec deklarisano "+ className.getClassName() , null);
		
    	//report_info("Obradjuje se klasa " + className.getClassName(),className);
	}
	
	// extends ima
	public void visit(ExtendsClass extendsClass) {
		Obj temp = Tab.find(extendsClass.getType().getTypeName());
		if (temp!=Tab.noObj && temp.getType().getKind() == Struct.Class) {
			currentClass.getType().setElementType(temp.getType());	//postavi roditelja
			//System.out.println(" " + temp.getName() + " " + extendsClass.getLine() + " " + temp.getType().getElemType());
			//uvezi sve metode iz te klase i promenljive jbg
			//currentClass.getType().setElementType(temp.getType());
		}
		else {
			report_error("Greska na liniji "  + extendsClass.getLine() +" ne postoji klasa "+ extendsClass.getType().getTypeName() , null);
		}
	}
	
	// interfejs koji se implementira
	public void visit(SingleTypeList singleTypeList) {
		if (singleTypeList.getType().struct.getKind() == Struct.Interface) {
			if (currentClass.getType().getImplementedInterfaces().contains(singleTypeList.getType().struct)) {
				report_error("Greska na liniji " + singleTypeList.getLine() +" vec je implementiran interjfes " + singleTypeList.getType().getTypeName(), singleTypeList);
			}
			else {
				currentClass.getType().addImplementedInterface(singleTypeList.getType().struct);//dodaj interfejs u listu interfejsa
				// i provera ako dve metode razlicitih interfejsa imaju ista imena
				// extra uvezi metode
				//System.out.println(" interfjes " + currentClass.getName());
			}
		}
		else {
			report_error("Greska na liniji "  + singleTypeList.getLine() +" ne postoji interfejs "+ singleTypeList.getType().getTypeName() , null);
		}
	}
	//vise interfejsa pa krajnji
	public void visit(TypeListClass singleTypeList) {
		if (singleTypeList.getType().struct.getKind() == Struct.Interface) {
			if (currentClass.getType().getImplementedInterfaces().contains(singleTypeList.getType().struct)) {
				report_error("Greska na liniji " + singleTypeList.getLine() +" vec je implementiran interjfes " + singleTypeList.getType().getTypeName(), singleTypeList);
			}
			else {
				currentClass.getType().addImplementedInterface(singleTypeList.getType().struct);//dodaj interfejs u listu interfejsa
				// i provera ako dve metode razlicitih interfejsa imaju ista imena
				// extra uvezi metode
			}
		}
		else {
			report_error("Greska na liniji "  + singleTypeList.getLine() +" ne postoji interfejs "+ singleTypeList.getType().getTypeName() , null);
		}
	}
	
	
		//kad se zavrsi cela deklaracija promenljivih u klasi
		public void visit(VardDeclsC singleVarDecls) {
			currentVarType = null;
		}
		
		// obicna promenljiva u klasi
		public void visit(VardCClass vardClass){
			varDeclCount++;
			//report_info("Deklarisana promenljiva u klasi "+ vardClass.getVarName(), vardClass);
			Obj obj = Tab.insert(Obj.Fld, vardClass.getVarName(), currentVarType);
			
			if (obj==Tab.noObj)
				report_error("Greska na liniji "  + vardClass.getLine() +" vec deklarisano "+ vardClass.getVarName() , null);
			
		}
		
		//nizovna promenljiva u klasi
		public void visit(VardCSquares vardClassSquares) {
			//report_info("Deklarisana promenljiva u klasi "+ vardClassSquares.getVarName(), vardClassSquares);
			
			Struct struct = new Struct(Struct.Array);
			struct.setElementType(currentVarType);	//elementi niza
			
			Obj obj = Tab.insert(Obj.Fld, vardClassSquares.getVarName(), struct);
			
			if (obj==Tab.noObj)
				report_error("Greska na liniji "  + vardClassSquares.getLine() +" vec deklarisano "+ vardClassSquares.getVarName() , null);
			
			
		}
	
	//provera dal su iste metode
	private boolean checkMethods(Obj m1,Obj m2) {
		//report_info(" rade se " + m1.getName() + " " + m2.getName() + " " + m1.getType().getKind() + " " + m2.getType().getKind(),null);
		
		if (m1.getType()!=m2.getType()) {
			return false;
		}
		
		if (m1.getLevel()!=m2.getLevel()+1) {
			return false;
		}
		
		for (int i=0;i<m2.getLevel();i++) {
			//report_info(" method " + m1.getName() + " method2 " + m2.getName(),null);
			
			Obj arg1 =(Obj) m1.getLocalSymbols().toArray()[i+1];
			Obj arg2 =(Obj) m2.getLocalSymbols().toArray()[i];
			
			if (arg1.getType()!=arg2.getType()) {
				
				return false;
			}
		}
		
		return true;
	}
	
	// kraj klase
	public void visit(ClassDecl classDecl) {
	
		//System.out.println(" " + currentClass.getName() + currentClass.getType().getElemType());
		Tab.chainLocalSymbols(classDecl.getClassName().obj.getType());	//members
    	Tab.closeScope();
    	
    
		Struct struct = classDecl.getClassName().obj.getType();
		
		Object interfejsi[] = struct.getImplementedInterfaces().toArray();
		for (int i = 0;i < interfejsi.length;i++)
		{
			Object metodeinter[] = ((Struct) interfejsi[i]).getMembers().toArray();
			Object metodeklasa[] =  classDecl.getClassName().obj.getType().getMembers().toArray();
			for (int j =0; j < metodeinter.length;j++) {
				boolean found = false;
				for (int k=0; k < metodeklasa.length; k++) {
					if (((Obj)metodeklasa[k]).getKind()==Obj.Meth) {
						if (((Obj)metodeklasa[k]).getName().equals(((Obj)metodeinter[j]).getName())) {
							found = true;
							if (!checkMethods((Obj)metodeklasa[k],(Obj)metodeinter[j])){
								report_error("Greska na liniji " + classDecl.getLine() + " ne poklapa se potpis funkcije " + ((Obj)metodeklasa[k]).getName() +
							" sa interfjesom koji se nasledjuje u klasi " + classDecl.getClassName().getClassName(), null);
							}
							
						}
						
					}
				}
				if (!found)
					report_error("Greska na liniji " + classDecl.getLine() + " ne nije nadjena fja iz interfjesa koji se nasledjuje u klasi " + classDecl.getClassName().getClassName(), null);
					
			}
		}
		
		currentClass=null;
	}
	
	//pocetak interfejsa
	public void visit(InterfaceName interfaceName) {
		Struct struct = new Struct(Struct.Interface);
		interfaceName.obj = Tab.insert(Obj.Type, interfaceName.getIName(), struct);
    	Tab.openScope();
	}
	
	//kraj interfejsa
	public void visit(InterfaceDecl interfaceDecl) {
		Tab.chainLocalSymbols(interfaceDecl.getInterfaceName().obj.getType());
		
		Tab.closeScope();
	}
	  
    
    // ZA TIP 
    public void visit(Type type){
    	Obj typeNode = Tab.find(type.getTypeName());
    	if(typeNode == Tab.noObj){
    		report_error("Greska na liniji " + type.getLine() + " Nije pronadjen tip " + type.getTypeName() + " u tabeli simbola! ", null);
    		type.struct = Tab.noType;
    		
    	}else{
    		if(Obj.Type == typeNode.getKind()){
    			type.struct = typeNode.getType();
    		}else{
    			report_error("Greska na liniji " + type.getLine() + "  Ime " + type.getTypeName() + " ne predstavlja tip!", null);
    			type.struct = Tab.noType;
    		}
    	}
    	
    	if (type.getParent().getClass() == NewType.class) {
    		if (Tab.noObj == typeNode)
    			typeNode = new Obj(Obj.NO_VALUE,"%error",Tab.noType);
    		((NewType)type.getParent()).obj = typeNode;
    		
    	}
    }
    
    // definicija fje sa povratnim tipom
    public void visit(MethodTypeNameWithType methodTypeNameWithType){
    	currentMethod = Tab.insert(Obj.Meth, methodTypeNameWithType.getMethName(), methodTypeNameWithType.getType().struct);
    	methodTypeNameWithType.obj = currentMethod;
    	currentMethod.setLevel(0);
    	Tab.openScope();
    	
    	if (currentClass!=null) {
    		Tab.insert(Obj.Var, "this", currentClass.getType());
    		currentMethod.setLevel(1);
    	}
    	
		//report_info("Obradjuje se funkcija " + methodTypeNameWithType.getMethName(), methodTypeNameWithType);
    }
    
    // definicija void fje
    public void visit(MethodTypeNameWithVoid methodTypeNameWithVoid) {
    
    	currentMethod = Tab.insert(Obj.Meth, methodTypeNameWithVoid.getMethName(), Tab.noType);
    	methodTypeNameWithVoid.obj = currentMethod;
    	currentMethod.setLevel(0);
    	Tab.openScope();
    	
    	if (currentClass!=null) {
    		Tab.insert(Obj.Var, "this", currentClass.getType());
    		currentMethod.setLevel(1);
    	}
    	
    	
    	//report_info("Obradjuje se funkcija " + methodTypeNameWithVoid.getMethName(), methodTypeNameWithVoid);
    }
    
   
    // kraj fje
    public void visit(MethodDecl methodDecl){
    	if(!returnFound && currentMethod.getType() != Tab.noType){
			report_error("Greska na liniji " + methodDecl.getLine() + " funkcija " + currentMethod.getName() + " nema return iskaz!", null);
    	}
    	Tab.chainLocalSymbols(currentMethod);
    	Tab.closeScope();
    	
    	returnFound = false;
    	currentMethod = null;
    	
    	if (methodDecl.getMethodTypeName().obj.getName().equals("main")) {
    		if (methodDecl.getMethodTypeName().obj.getType() == Tab.noType && methodDecl.getMethodTypeName().obj.getLevel() ==0)
    			mainVoid = true;
    	}
    }
    
    //kreaj interfejs metode jbt
    public void visit(InterfaceMethodDecl node ) {
    	Tab.chainLocalSymbols(currentMethod);
    	Tab.closeScope();
    	returnFound = false;
    	currentMethod = null;
    }
    
    //formalni paramater
    public void visit(FormalParam formalParam) {   	
    	Obj obj = Tab.insert(Obj.Var, formalParam.getPName(), formalParam.getType().struct);
    	obj.setFpPos(currentMethod.getLevel());
    	
    	if (obj==Tab.noObj)
			report_error("Greska na liniji "  + formalParam.getLine() +" vec deklarisano "+ formalParam.getPName() , null);
    	
    	currentMethod.setLevel(currentMethod.getLevel()+1);
    }
    
    //formalni paramater kao niz
    public void visit(FormalParamSquares formalParamSquares) { 	
    	Struct struct = new Struct(Struct.Array);
    	struct.setElementType(formalParamSquares.getType().struct);
    	Obj obj = Tab.insert(Obj.Var, formalParamSquares.getPName(), struct);
    	obj.setFpPos(currentMethod.getLevel());
    	
    	if (obj==Tab.noObj)
			report_error("Greska na liniji "  + formalParamSquares.getLine() +" vec deklarisano "+ formalParamSquares.getPName() , null);
    	currentMethod.setLevel(currentMethod.getLevel()+1); 	
    }
    
    
    
    
    public void visit(FuncName funcName) {
    	currentMethodCall.add(funcName.getDesignator().obj);
    	
//    	if (Tab.find(funcName.getDesignator().obj.getName())==Tab.noObj) {	//ovo znaci da je metoda klase ne znaci
//    		//System.out.println(funcName.getDesignator().obj.getName());
//    		currentActualParams.add(funcName.getDesignator().obj.getLevel()-2);
//    		isMethod.add(1);
//    	}
//    	else {
//    		currentActualParams.add(funcName.getDesignator().obj.getLevel()-1);
//    		isMethod.add(0);
//    	}
    	
    	currentActualParams.add(funcName.getDesignator().obj.getLevel()-1);
    	
    }
    
    // poziv funkcije
    public void visit(DesignatorStmtFunc designatorStmtFunc){
    	Obj func = designatorStmtFunc.getFuncName().getDesignator().obj;
    	if(Obj.Meth == func.getKind()){

			report_info("Pretraga na liniji " + designatorStmtFunc.getLine() +" Pronadjen poziv funkcije " + func.getName(), null);
			designatorStmtFunc.struct = func.getType();
		
    	}else{
			report_error("Greska na liniji " + designatorStmtFunc.getLine()+ "  ime " + func.getName() + " nije funkcija!", null);
			designatorStmtFunc.struct = Tab.noType;
    	}
    	currentMethodCall.remove(currentMethodCall.size()-1);
    	currentActualParams.remove(currentActualParams.size() -1);
    
    }
    
    
    public void visit(BreakStmt breakStmt) {
    	if (currentFor>0) {
    		//ok
    	}
    	else {
    		report_error("Greska na liniji " + breakStmt.getLine() + " break nije u foru", null);
    	}
    }
    
    public void visit(ContinueStmt continueStmt) {
    	if (currentFor>0) {
    		
    	}
    	else {
    		report_error("Greska na liniji " + continueStmt.getLine() + " continue nije u foru", null);
    	}
    }
    
    // designator++
    public void visit(DesignatorStmtPlus2 designatorStmtPlus2) {
    	//provera da li postoji taj designator
    	Obj var = designatorStmtPlus2.getDesignator().obj;
    	if (var.getType() == Tab.intType) {
    		// ok
    	}
    	else {
    		report_error("Greska na liniji " + designatorStmtPlus2.getLine() + " designator nije tipa int " , null);
    	}
    }
    
    // designator--
    public void visit(DesignatorStmtMinus2 designatorStmtMinus2) {
    	//provera da li postoji taj designator
    	Obj var = designatorStmtMinus2.getDesignator().obj;
    	if (var.getType() == Tab.intType) {
    		// ok
    	}
    	else {
    		report_error("Greska na liniji " + designatorStmtMinus2.getLine() + " designator nije tipa int ", designatorStmtMinus2);
    	}
    }
    
    public void visit(ReadStmt readStmt) {
    	//provera da li je promenljiva polje klase ili element niza ***********************mora se odradi svugde gde treba
    	if (readStmt.getDesignator().obj.getType() != Tab.intType && readStmt.getDesignator().obj.getType() != Tab.charType && readStmt.getDesignator().obj.getType() !=boolType)
    		report_error ("Greska na liniji " + readStmt.getLine() + " Operand instrukcije READ mora biti char ili int ili bool tipa", null );
		
    }
    
    public void visit(PrintStmt print) {
    	if	(print.getExpr().struct != Tab.intType && print.getExpr().struct!= Tab.charType && print.getExpr().struct!=boolType && print.getExpr().struct.getKind()!= Struct.Enum) 
    		report_error ("Greska na liniji " + print.getLine() + " Operand instrukcije PRINT mora biti char ili int ili bool tipa", null );
		
    	printCallCount++;
	}
    
    // expr bez minusa
    public void visit(ExprNoMinus exprNoMinus) {
    	Struct te = exprNoMinus.getAddopTermList().struct;
    	Struct t = exprNoMinus.getTerm().struct;
    	
    	if (te == null) {
    		//ok
    		
    		exprNoMinus.struct = t;
    	}
    	else if (checkEnumInt(t, te) || (t==Tab.intType && te==Tab.intType)) {
    		
    		exprNoMinus.struct = t;
    	}else{
    		//report_info("eeee " + t.getKind() +" " + te.getKind(), null);
			report_error("Greska na liniji "+ exprNoMinus.getLine() + " nekompatibilni tipovi u izrazu za sabiranje.", null);
			exprNoMinus.struct = Tab.noType;
    	}
    }
    

    
    // expresion ako su oba tipa ista moze
    public void visit(ExprMinus exprMinus){
    	Struct te = exprMinus.getAddopTermList().struct;
    	Struct t = exprMinus.getTerm().struct;
    	
    	if (te == null) {
    		//ok
    		exprMinus.struct = t;
    	}
    	else if(checkEnumInt(t, te) || (t==Tab.intType && te==Tab.intType)){
    		exprMinus.struct = t;
    	}else{
			report_error("Greska na liniji "+ exprMinus.getLine() + "  nekompatibilni tipovi u izrazu za sabiranje. ", null);
			exprMinus.struct = Tab.noType;
    	}
    }
    
    
    // addop term list
    public void visit(AddopTermListClass node) {
    	
    	if (node.getAddopTermList().struct!=null) {
    		if (checkEnumInt(node.getAddopTermList().struct, node.getTerm().struct) || (node.getTerm().struct==Tab.intType && node.getAddopTermList().struct==Tab.intType) ) {
    			//ok
    			node.struct = node.getTerm().struct;
    		}
    		else {
    			report_error("Greska na liniji "+ node.getLine() + " nekompatibilni tipovi u izrazu za sabiranje. ", null);
    			node.struct = Tab.noType;
    		}
    	}
    	else {
    		//ok kraj liste
    		node.struct = node.getTerm().struct;
    	}
    	
    	
    	
    }
    
    
    // addop term list ends
    public void visit(NoAddopTermList node) {
    	node.struct = null;
    }
    
    
    
    //Factor = null
    public void visit(NullFact nullFact){
    	nullFact.struct = Tab.nullType;
    }

    
    //Factor = num
    public void visit(NumFact numFact){
    	numFact.struct = Tab.intType;
    }
    
    //Factor = char
    public void visit(CharFact charFact){
    	charFact.struct = Tab.charType;
    }
    
    //Factor = bool
    public void visit(BoolFact boolFact){
    	boolFact.struct = boolType;
    }
    
    // facotr = (expr)
    public void visit(ExprFact exprFact) {
    	exprFact.struct = exprFact.getExpr().struct;
    }
    
    //Factor = designator
    public void visit(DesignatorFact designatorFact){
    	designatorFact.struct = designatorFact.getDesignator().obj.getType();
    }
    
    //factor FUNC CALL
    public void visit (FactorFuncCall node ) {
    	Obj func = node.getFuncName().getDesignator().obj;
    	if(Obj.Meth == func.getKind()){

			report_info("Pretraga na liniji " + node.getLine() +" Pronadjen poziv funkcije " + func.getName(), null);
			node.struct = func.getType();
		
    	}else{
			report_error("Greska na liniji " + node.getLine()+ "  ime " + func.getName() + " nije funkcija!", null);
			node.struct = Tab.noType;
    	}
    	currentMethodCall.remove(currentMethodCall.size()-1);
    	currentActualParams.remove(currentActualParams.size() -1);
    	
    }
    
    
    public void visit(ReturnSmt returnSmt){
    	if (currentMethod==null)
    	{
    		report_error("Greska na liniji " + returnSmt.getLine() + " ne moze return van tela funkcije ", null);
	    	
    	}
    	else {
    		returnFound = true;
	    	Struct currMethType = currentMethod.getType();
	    	if (checkClassInterfaceNull(currMethType,returnSmt.getExpr().struct) || checkEnumInt(currMethType, returnSmt.getExpr().struct) || checkClassExtendsImplements(currMethType, returnSmt.getExpr().struct)) {
	    		
	    	}
	    	else
	    	if(!currMethType.compatibleWith(returnSmt.getExpr().struct)){
				report_error("Greska na liniji " + returnSmt.getLine() + " tip izraza u return naredbi ne slaze se sa tipom povratne vrednosti funkcije " + currentMethod.getName(), null);
	    	}
    	}
    }
    
    // pocetak fora
    public void visit(ForHeader forHeader) {
    	currentFor++;
    }
    // kraj fora
    public void visit(ForStatement forStatement) {
    	currentFor--;
    }
    
    // zavresno posecivanje parametara
    public void visit(ActPars actPars) {
    	
    	if (currentActualParams.get(currentActualParams.size()-1) >= 0 ) {
    		
    		Object object[] = currentMethodCall.get(currentMethodCall.size()-1).getLocalSymbols().toArray();
    		Obj obj = (Obj) object[0];
    		
    		if (obj.getName().equals("this") && currentActualParams.get(currentActualParams.size()-1) == 0) {
    			//ok
    		}
    		else {
    			//System.out.println(obj.getName() + " " + currentActualParams.get(currentActualParams.size()-1) );
    			report_error("Greska na liniji " + actPars.getLine() + " nema dovoljan broj parametara za poziv funkcije " + currentMethodCall.get(currentMethodCall.size()-1).getName()  , null);
    		}
    	}
    	
    	//currentActualParams.remove(currentActualParams.size()-1);
    	//isMethod.remove(isMethod.size()-1);
    }
    
    // krece redom posecivanje pravih parametara u pozivu fje
    public void visit(ExprListClass exprListClass) {	
    	Obj obj ;
    	Struct formParam ;
    	
    	
    	//rekurzija
    	if (currentMethod!=null && currentMethodCall.get(currentMethodCall.size()-1).getName().equals(currentMethod.getName())) {
    		Scope scope = Tab.currentScope();
    		Object object[] = scope.getLocals().symbols().toArray();
    		obj = (Obj) object[currentActualParams.get(currentActualParams.size()-1)];
    		formParam = obj.getType();
    		if (checkClassExtendsImplements(formParam,exprListClass.getExpr().struct)) {
    			// okej je
    		}
    		else if (!exprListClass.getExpr().struct.assignableTo(formParam) && !checkEnumInt(formParam, exprListClass.getExpr().struct)) {
	    		
	    		report_error("Greska na liniji " + exprListClass.getLine() + " nekompatibilni parametri " + obj.getName()  , null);
	    	}
	    	else {
		    	if (currentActualParams.get(currentActualParams.size()-1) > currentMethodCall.get(currentMethodCall.size()-1).getLevel()) {
		    		report_error("Greska na liniji " + exprListClass.getLine() + " vise parametara nego sto fja ima " + currentMethodCall.get(currentMethodCall.size()-1).getName() , null);
		    	}
	    	}
    	}
    	else
    	if (currentActualParams.get(currentActualParams.size()-1)< 0) {
    		
    		report_error("Greska na liniji " + exprListClass.getLine() + " veci broj parametara od potrebnog za poziv funkcije " + currentMethodCall.get(currentMethodCall.size()-1).getName()  , null);
    	
    	}else {
	    	obj = (Obj) currentMethodCall.get(currentMethodCall.size()-1).getLocalSymbols().toArray()[currentActualParams.get(currentActualParams.size()-1)];	
	    	formParam = obj.getType();
	    	
	    	if (checkClassExtendsImplements(formParam,exprListClass.getExpr().struct)) {
    			// okej je
    		}
    		else
	    	if (!exprListClass.getExpr().struct.assignableTo(formParam) && !checkEnumInt(formParam, exprListClass.getExpr().struct)) {
	    		
	    		report_error("Greska na liniji " + exprListClass.getLine() + " nekompatibilni parametri " + obj.getName()  , null);
	    	}
	    	else {
		    	if (currentActualParams.get(currentActualParams.size()-1) > currentMethodCall.get(currentMethodCall.size()-1).getLevel()) {
		    		report_error("Greska na liniji " + exprListClass.getLine() + " vise parametara nego sto fja ima " + currentMethodCall.get(currentMethodCall.size()-1).getName() , null);
		    	}
	    	}
    	}
    	int x = currentActualParams.remove(currentActualParams.size()-1);
    	currentActualParams.add(x-1);
    }
    
    //jedan pravi parametar u pozivu fje
    public void visit(SingleExpr singleExpr) {
    	//rekurzija
    	if (currentMethod!=null && currentMethodCall.get(currentMethodCall.size()-1).getName().equals(currentMethod.getName())) {
    		Scope scope = Tab.currentScope();
    		Object object[] = scope.getLocals().symbols().toArray();
    		Obj obj = (Obj) object[currentActualParams.get(currentActualParams.size()-1)];
    		Struct formParam = obj.getType();
    		
    		if (checkClassExtendsImplements(formParam,singleExpr.getExpr().struct)) {
    			// okej je
    		}
    		else
    		if (!singleExpr.getExpr().struct.assignableTo(formParam) && !checkEnumInt(formParam, singleExpr.getExpr().struct) ) {
	    		report_error("Greska na liniji " + singleExpr.getLine() + " nekompatibilni parametri " + obj.getName()  , null);
	    	}
	    	else {
	    		//ok
	    	}
    	}
    	else if (currentActualParams.get(currentActualParams.size()-1) < 0 ) {
    		report_error("Greska na liniji " + singleExpr.getLine() + " veci broj parametara od potrebnog za poziv funkcije " + currentMethodCall.get(currentMethodCall.size()-1).getName()  , null);
        
    	}
    	else {
    		Obj obj = (Obj) currentMethodCall.get(currentMethodCall.size()-1).getLocalSymbols().toArray()[currentActualParams.get(currentActualParams.size()-1)];	
	    	Struct formParam = obj.getType();
	    	if (checkClassExtendsImplements(formParam,singleExpr.getExpr().struct)) {
    			// okej je
    		}
    		else
	    	if (!singleExpr.getExpr().struct.assignableTo(formParam) && !checkEnumInt(formParam, singleExpr.getExpr().struct) ) {
	    	
	    		
	    		report_error("Greska na liniji " + singleExpr.getLine() + " nekompatibilni parametri " + obj.getName()  , singleExpr);
	    	}
	    	else {
	    		//ok
	    	}
    	}
    	int x = currentActualParams.remove(currentActualParams.size()-1);
    	currentActualParams.add(x-1);
    }
    
    // condition
    public void visit(Condition node) {
    	//ovde ne treba jer je dole vec ispitano
    	
    }
    // OR LISTA
    public void visit(CondTermListClass node) {
    	if (node.getCondTerm().struct!=boolType)
    		report_error("Greska na liniji " + node.getLine() + " uslov nije bool tip " , null);
    	else if (node.getCondTermList().struct!=null && node.getCondTermList().struct!=boolType)
			report_error("Greska na liniji " + node.getLine() + " uslov nije bool tip " , null);
    	node.struct= boolType;
    }
    
    public void visit(CondTerm node) {
    	if (node.getCondFact().struct!=boolType)
    		report_error("Greska na liniji " + node.getLine() + " uslov nije bool tip " , null);
    	else if (node.getCondFactList().struct!=null && node.getCondFactList().struct!=boolType)
			report_error("Greska na liniji " + node.getLine() + " uslov nije bool tip " , null);
    	
    	node.struct=boolType;
    }
    
    // AND LISTA
    public void visit(CondFactListClass node) {
    	if (node.getCondFact().struct!=boolType)
    		report_error("Greska na liniji " + node.getLine() + " uslov nije bool tip " , null);
    	else if (node.getCondFactList().struct!=null && node.getCondFactList().struct!=boolType)
			report_error("Greska na liniji " + node.getLine() + " uslov nije bool tip " , null);
    	node.struct = boolType;
    }
    
    // bez relacionog operatora expr
    public void visit(CondFactExpr condFactExpr) {
    	
    	if (condFactExpr.getExpr().struct!=boolType)
    		report_error("Greska na liniji " + condFactExpr.getLine() + " uslov nije bool tip " , null);
    	
    	condFactExpr.struct = condFactExpr.getExpr().struct;
    }
    
    private boolean checkClassInterfaceNull(Struct s1,Struct s2) {
    	if (s1.getKind()==Struct.Interface && s2==Tab.nullType)
    		return true;
    	
    	if (s1.getKind()==Struct.Class && s2==Tab.nullType)
    		return true;
    	
    	if (s1.getKind()==Struct.Array && s2==Tab.nullType)
    		return true;
    	
    	if (s2.getKind()==Struct.Interface && s1==Tab.nullType)
    		return true;
    	
    	if (s2.getKind()==Struct.Class && s1==Tab.nullType)
    		return true;
    	
    	if (s2.getKind()==Struct.Array && s1==Tab.nullType)
    		return true;
    	
    	return false;
    }
    // expr sa relacionim operatorom
    public void visit(CondFactExprRelopExpr node) {
    	
    	if ((node.getExpr().struct.getKind()==Struct.Array && node.getExpr1().struct==Tab.nullType) ||(node.getExpr1().struct.getKind()==Struct.Array && node.getExpr().struct==Tab.nullType)){
    		//ok
    	}
    	else if (checkEnumInt(node.getExpr().struct, node.getExpr1().struct)) {
    		//ok
    	}
    	else if (checkClassInterfaceNull(node.getExpr().struct, node.getExpr1().struct)){
    		
    	}
    	else if (node.getExpr().struct.equals(node.getExpr1().struct)){
    		
    	}
    	else if (!node.getExpr().struct.compatibleWith(node.getExpr1().struct)) {
    		report_error("Greska na liniji " + node.getLine() + " nekompatibilni tipovi " , null);
    	}
    	else if ((node.getExpr().struct.getKind()==Struct.Array || node.getExpr().struct.getKind()==Struct.Class)) {
    		report_error("Greska na liniji " + node.getLine() + " nekompatibilni tipovi " , null);
    	}
    	else {
    		//ok
    	}
    	
    	node.struct = boolType;
    }
    
    
    private boolean checkClassExtendsImplements(Struct left, Struct right) {
    	
    	
    	if (left.getKind()!=Struct.Class && left.getKind()!=Struct.Interface)	//ako nije klasa ili interfejs levo ne proveravaj
    		return false;
    	
    	if (right==Tab.nullType)
    		return true;
    	
    	if (right==left)
    		return true;
    	
    	Struct curr;
    	if (left.getKind()==Struct.Interface) {
    		curr = right;
    		while (curr!=null) {
    			Object inters[] =curr.getImplementedInterfaces().toArray();
    			Struct inter;
    			for (int i =0;i<inters.length;i++) {
    				inter = (Struct) inters[i];
    				if (left == inter) {
    					return true;
    				}
    			}
    			curr = curr.getElemType();
    		}
    	}
    	else { // klasa
    		curr = right;
    		
    		while (curr!=null) { 			
    			if (left==curr)
    				return true;
    			
    			curr = curr.getElemType();
    			//System.out.println("usao 1 " + curr.getKind());
    		}
    	}
    	
    	return false;
    }
    public void visit(Assign assign){
    	
    	if (checkEnumInt(assign.getExpr().struct, assign.getDesignator().obj.getType())) {
    		//ok
    	}
    	else if (assign.getDesignator().obj.getType().getKind()==Struct.Class || assign.getDesignator().obj.getType().getKind() == Struct.Interface) {	//klasa i nadklasa mora i sve natklase i sve interfejse da se provere jbg
    		if (checkClassExtendsImplements(assign.getDesignator().obj.getType(),assign.getExpr().struct)) {
    			//ok
    		}
    		else {
    			report_error("Greska na liniji " + assign.getLine() + " nekompatibilni tipovi u dodeli vrednosti!", null);
    		}
    	}
    	else if (assign.getDesignator().obj.getType().getKind()==Struct.Interface){
    		if (assign.getExpr().struct.getImplementedInterfaces().contains(assign.getDesignator().obj.getType())) {
    			//ok implementira interfejs
    		}
    		else {
    			report_error("Greska na liniji " + assign.getLine() + " nekompatibilni tipovi u dodeli vrednosti!", null);
    		}	
    	}
    	else if (assign.getDesignator().obj.getType().getKind()==Struct.Array && assign.getDesignator().obj.getType().getElemType().getKind()==Struct.Class)  {
    		Struct levo = assign.getDesignator().obj.getType().getElemType();
    		Struct desno = assign.getExpr().struct.getElemType();
    		//System.out.println(" E " + desno.getKind());
    		
    		if (levo==desno.getElemType() || levo==desno) {
    			//ok
    		}
    		else {
    			report_error("Greska na liniji " + assign.getLine() + " nekompatibilni tipovi u dodeli vrednosti!", null);
    		}
    	}
    	else if ((assign.getDesignator().obj.getType().getKind()==Struct.Array || assign.getDesignator().obj.getType().getKind()==Struct.Array) && assign.getExpr().struct==Tab.nullType){
    		//ok
    	}
    	else if(!assign.getExpr().struct.assignableTo(assign.getDesignator().obj.getType())) {
    		//System.out.println(assign.getDesignator().obj.getType().getKind() +" " + assign.getExpr().struct.getKind());
    		report_error("Greska na liniji " + assign.getLine() + " nekompatibilni tipovi u dodeli vrednosti!", null);
    	}
    	else {
    		//ok
    	}
    }
    
    //term kao prosledjivanje samo strukture da se vidi dal je kompatibilna
    public void visit(Term term){
    	
    	if (term.getMulopFactorList().struct!=null && (checkEnumInt(term.getFactor().struct, term.getMulopFactorList().struct))) {
    		term.struct = Tab.intType;	//enum i int
    	}
    	else if (term.getMulopFactorList().struct!=null && !term.getFactor().struct.compatibleWith(term.getMulopFactorList().struct)){
    		report_error("Greska na liniji " + term.getLine() + " nekompatibilni! ", null);
    		term.struct=Tab.noType;
    	}
    	else {
    		
    		term.struct = term.getFactor().struct;
    	}
    }
    
    // mulop faktor lista redom mora da se vide svi tipovi
    public void visit(MulopFactorListClass node ) {
    	if (node.getFactor().struct!=Tab.intType && node.getFactor().struct.getKind()!=Struct.Enum) {
    		report_error("Greska na liniji " + node.getLine() + " nekompatibilni tipovi ", null);
    		node.struct = Tab.noType;
    	}
    	else if (node.getMulopFactorList().struct!=null && node.getMulopFactorList().struct!=Tab.intType &&  node.getMulopFactorList().struct.getKind()!=Struct.Enum) {
    		report_error("Greska na liniji " + node.getLine() + " nekompatibilni tipovi ", null);
    		node.struct = Tab.noType;
    	}
    	else {
    		node.struct = node.getFactor().struct; 
    	}
    }
    
    //kraj mulop faktor liste
    public void visit(NoMulopFactorList node) {
    	node.struct = null;
    }
    
    // new KLASA
    public void visit(NewFact newFact) {
    	Struct tip = newFact.getNewType().obj.getType();
    	
    	
    	if (tip.getKind()!=Struct.Class) {
    		report_error("Greska na liniji " + newFact.getLine() + " tip nije klasa ", newFact);
    	}
    	else {
    		//ok
    		newFact.struct = tip;
    		
    	}
    	
    }
    
    // new int[] .....
    public void visit(NewFactSquare newFactSquare) {
    	Struct tip = newFactSquare.getType().struct;
    	
    	
    	if (newFactSquare.getExpr().struct!=Tab.intType && newFactSquare.getExpr().struct.getKind()!=Struct.Enum) {
    		report_error("Greska na liniji " + newFactSquare.getLine() + " tip nije niz ", newFactSquare);
    	}
    	else {
    		//ok
    		newFactSquare.struct = new Struct(Struct.Array);
    		newFactSquare.struct.setElementType(tip);
    	}
    }
    
    
    
    
    // designator kraj designatora
    public void visit(Designator designator){
    	designator.obj = currentDesignator.remove(currentDesignator.size()-1);
    	//currentDesignator = null;
    	
    	//report_info("KOJI KURAC", designator);
    	
    }
    
    // ime designatora tjst pocetak
    public void visit(DesignatorName designatorName) {
    	designatorName.obj = Tab.find(designatorName.getName());
    	Obj obj = designatorName.obj;
    	boolean found = false;
    	int i;
    	
    	// ako mu se pristupa u klasi bez this OPA LOMI ME
    	if (obj == Tab.noObj && currentClass!=null) {
    		//System.out.println(" eee "  + designatorName.getName() + " " +designatorName.getName() );
    		Object innerobjs[] =  currentClass.getType().getMembers().toArray();
    		for (i=0;i<innerobjs.length;i++) {
    			if (((Obj)innerobjs[i]).getName().equals(designatorName.getName())) {
    				found = true;		
    				obj = (Obj)innerobjs[i];
    				designatorName.obj = obj;
    				break;
    			}
    		}
    		
    		// roditelji
    		Struct pom = currentClass.getType();
    		while (!found && pom.getElemType()!=null) {
    			innerobjs =  pom.getElemType().getMembers().toArray();
        		for (i=0;i<innerobjs.length;i++) {
        			if (((Obj)innerobjs[i]).getName().equals(designatorName.getName())) {
        				found = true;
        				obj = (Obj)innerobjs[i];
        				designatorName.obj = obj;
        				break;
        			}
        		}
        		pom = pom.getElemType();
    		}
    		
    		if (found) {
    			//ok
    			report_info("Pretraga na liniji " +  designatorName.getLine() + " (" + designatorName.getName() +") , nadjeno ",null);
    		}
    		else {
    			designatorName.obj=Tab.noObj;
    			report_error("Greska na liniji " + designatorName.getLine()+ " ime "+ designatorName.getName() + " nije deklarisano! ", null);
    		}
    	}
    	
    	else if(obj == Tab.noObj){
    		designatorName.obj=Tab.noObj;
			report_error("Greska na liniji " + designatorName.getLine()+ " ime "+ designatorName.getName() + " nije deklarisano! ", null);
    	}
    	else {
    		//designatorName.obj=Tab.noObj;
    		designatorName.obj = obj;
    		report_info("Pretraga na liniji " +  designatorName.getLine() + " (" + designatorName.getName() +") , nadjeno ",null);
    	}
    	
    	//report_info("DES " + designatorName.getName(),designatorName);
    	currentDesignator.add(obj);
    }
    
    // designator.SLEDECI
    public void visit(DesignatorSufixDot node) {
    	Obj obj = Tab.find(node.getName());
    	boolean found = false;
    	
    	int i;
    	
    	if (getCurrDes().getType().getKind()==Struct.Class){
    		
    		Object innerobjs[] =  getCurrDes().getType().getMembers().toArray();
    		for (i=0;i<innerobjs.length;i++) {
    			//System.out.println("imena u klasi " + ((Obj)innerobjs[i]).getName());
    			if (((Obj)innerobjs[i]).getName().equals(node.getName())) {
    				found = true;
    				
    				resetCurrDes((Obj)innerobjs[i]);
    				
    				//System.out.println("osnovna " + getCurrDes().getName() + " " + getCurrDes().getType().getKind());
    				break;
    			}
    		}
    		
    		
    		
    		if (!found && currentClass!= null && currentClass.getType() == currentDesignator.get(currentDesignator.size()-1).getType()) {
    			Obj curr = Tab.find(node.getName());
    			if (curr!=Tab.noObj) {
    				found = true;
    				resetCurrDes(curr);
    			}
    		}
    		Struct pom  = null;
    		if (!found) {
    			pom = getCurrDes().getType();
    			//System.out.println("e " + found + " " +getCurrDes().getName()  + " " + pom.getElemType());
    		}
    		
    		while (!found && pom.getElemType()!=null) {
    			
    			innerobjs =  pom.getElemType().getMembers().toArray();
    			
        		for (i=0;i<innerobjs.length;i++){
        			//System.out.println("nadklasa " + getCurrDes().getName() + " " + node.getName() + " " + ((Obj)innerobjs[i]).getName());
        			if (((Obj)innerobjs[i]).getName().equals(node.getName())) {
        				found = true;
        				
        				resetCurrDes((Obj)innerobjs[i]);
        				
        				//System.out.println("nadklasa " + getCurrDes().getName() + " " + getCurrDes().getType().getKind());
        				break;
        			}
        		}
        		pom = pom.getElemType();
    		}
    		
    		
    		
    		if (found) {
    			//ok
    			//report_info("Usao u klasu  "+ node.getName() + " " + currentDesignator.get(currentDesignator.size()-1).getName(), null);
    			report_info("Pretraga na liniji " +  node.getLine() + " (" + node.getName() +") , nadjeno ",null);
    		}
    		else {
    			report_error("Greska na liniji " + node.getLine()+ " : ime "+ node.getName() + " nije deklarisano u klasi! ", null);
    			resetCurrDes(Tab.noObj);
    		}
    		
    	}
    	else if (getCurrDes().getType().getKind()==Struct.Interface){
    		Object innerobjs[] =  getCurrDes().getType().getMembers().toArray();
    		for (i=0;i<innerobjs.length;i++) {
    			if (((Obj)innerobjs[i]).getName().equals(node.getName())) {
    				found = true;
    				
    				resetCurrDes((Obj)innerobjs[i]);
    				break;
    			}
    		}
    		//report_info("DdasdasdasdsadsadsaDDD " + node.getName() + " " + found,null);
    		
    		if (found) {
    			//ok
    			//report_info("Usao u klasu  "+ node.getName() + " " + currentDesignator.get(currentDesignator.size()-1).getName(), null);
    			report_info("Pretraga na liniji " +  node.getLine() + " (" + node.getName() +") , nadjeno ",null);
    		}	
    	}
    	else if (getCurrDes().getType().getKind()==Struct.Enum) {
    		//report_info("Usao u enum "+ node.getName() , null);
    		Object innerobjs[] = getCurrDes().getType().getMembers().toArray();
    		for (i=0;i<innerobjs.length;i++) {
    			if (((Obj)innerobjs[i]).getName().equals(node.getName())) {
    				found = true;
    				break;
    			}
    				
    		}
    		if (found) {
    			//ok
    			resetCurrDes((Obj)innerobjs[i]);
    			//report_info("Tip je " + currentDesignator.getType().getKind() +" " + currentDesignator.getName(), null);
    			report_info("Pretraga na liniji " +  node.getLine() + " (" + node.getName() +") , nadjeno ",null);
    		}
    		else {
    			report_error("Greska na liniji " + node.getLine()+ " : ime "+ node.getName() + " nije deklarisano u enumu! ", null);
    			resetCurrDes(Tab.noObj);
    		}
    	}
    	else if (obj == Tab.noObj){
			report_error("Greska na liniji " + node.getLine()+ " : ime "+ node.getName() + " nije deklarisano! ", null);
			resetCurrDes(Tab.noObj);
    	}    	
    	
    	
    }
    
    public void visit(DesignatorSufixSquare node) {
    	
    	if (getCurrDes().getType()!=null && getCurrDes().getType().getKind()!=Struct.Array) {
    		report_error("Greska nije nizovni tip " + getCurrDes().getName() , node);
    		resetCurrDes(Tab.noObj);
    	}
    	else
    	if (node.getExpr().struct!=Tab.intType && node.getExpr().struct.getKind()!=Struct.Enum) {
    		report_error("Greska na liniji " + node.getLine() + " nije int tip unutar uglastih zagrada" , null);
    		resetCurrDes(Tab.noObj);
    	}
    	else {
    		//ok
    		if (getCurrDes().getType().getElemType().getKind()==Struct.Class) {
    			resetCurrDes(new Obj(Obj.Elem,"%mytemp",getCurrDes().getType().getElemType()));
    			
    			//report_info("KLASA E "+getCurrDes().getName(),null);
    			//resetCurrDes(Tab.find(getCurrDes().getName()));
    		}
    		else if (getCurrDes().getType().getElemType().getKind()==Struct.Interface) {
    			//report_info("INTERFEJS E "+getCurrDes().getName() + " " + getCurrDes().getType().getElemType().getNumberOfFields(),null);
    			
    			resetCurrDes(new Obj(Obj.Elem,"%mytemp",getCurrDes().getType().getElemType()));
    			
    		}
    		else {
    			//uradi ako je obican tip sta onda
    			//ili se ostavi pa ako je array proverava se tip al nzm
    			resetCurrDes(new Obj(Obj.Elem,"%mytemp", getCurrDes().getType().getElemType()));
    			
    			//report_info("INTERFEJS E"+getCurrDes().getName(),null);
    		}
    		
    	
    	}
    	
    }
    
    
    public boolean passed(){
    	return !errorDetected && mainVoid;
    }
}
