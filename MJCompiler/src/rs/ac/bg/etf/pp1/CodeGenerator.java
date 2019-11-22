package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.HashMap;

import com.sun.org.apache.bcel.internal.generic.NEWARRAY;

import rs.ac.bg.etf.pp1.CounterVisitor.FormParamCounter;
import rs.ac.bg.etf.pp1.CounterVisitor.VarCounter;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class CodeGenerator extends VisitorAdaptor {
	
	public int varCount;
	
	private int paramCnt;
	
	private boolean returnFound = false;
	
	private int mainPc;
	
	private ArrayList<Obj> currentDesignator = new ArrayList<>();
	
	private ArrayList<Integer> adr =new ArrayList<>();
	
	private ArrayList<Integer> forAdrUpCond =new ArrayList<>();
	private ArrayList<Integer> forAdrUpInkr =new ArrayList<>();
	private ArrayList<Integer> forAdrFixupTrue =new ArrayList<>();
	private ArrayList<Integer> forAdrFixupFalse =new ArrayList<>();
	
	private ArrayList<ArrayList<Integer>> breakList =new ArrayList<>();
	
	
	private class MethodNode {
		public String ime;
		public int adr;
	};
	
	public int virtualTableStart;
	public Obj currentClass = null;
	
	private ArrayList<Obj> currentClassMethods = new ArrayList<>();
	
	private HashMap<Struct, Integer> hashVirtualTable = new HashMap<>();
	
	private ArrayList<MethodNode> listNameAdr = new ArrayList<>();
	
	private boolean methodCall = false;
	private boolean methodInside = false;
	public Obj methodCallObj = null;
	
	int forDesOpt = 0;
	boolean interfaceIn = false;
	
	public int getMainPc() {
		return mainPc;
	}
	
	private Obj getCurrDes() {
		return currentDesignator.get(currentDesignator.size()-1);
	}
	
	private void resetCurrDes(Obj obj) {
		currentDesignator.remove(currentDesignator.size()-1);
		currentDesignator.add(obj);
	}
	
	//pocetak interfejsa
	@Override
	public void visit(InterfaceName node) {
		interfaceIn = true;
	}
	
	// kraj interfejsa
	@Override
	public void visit(InterfaceDecl node) {
		interfaceIn = false;
	}
	@Override
	public void visit(MethodTypeNameWithType MethodTypeName) {
		if (interfaceIn)
			return;
		
		int c=0;
		MethodTypeName.obj.setAdr(Code.pc);
		
		if (currentClass!=null) {
			c=1;
			currentClassMethods.add(MethodTypeName.obj);
			virtualTableStart += MethodTypeName.obj.getName().length() + 2;
		}
	

		// Collect arguments and local variables.
		SyntaxNode methodNode = MethodTypeName.getParent();
		VarCounter varCnt = new VarCounter();
		methodNode.traverseTopDown(varCnt);
		FormParamCounter fpCnt = new FormParamCounter();
		methodNode.traverseTopDown(fpCnt);
		
		// Generate the entry.
		Code.put(Code.enter);
		Code.put(fpCnt.getCount() + c);
		Code.put(varCnt.getCount() + fpCnt.getCount() + c);
		
	}
	
	//generisanje funkcija na pocetku mejna za virtuelne tabele
	private void generateVirtualTable() {
		int datapc = varCount;
		
		for (int i=0;i<currentClassMethods.size();i++) {
			
			if (currentClassMethods.get(i)==null) {
				Code.loadConst(-2);
				Code.put(Code.putstatic);
				Code.put2(datapc++);
				continue;
			}
			String name = currentClassMethods.get(i).getName();
			int adr = currentClassMethods.get(i).getAdr();
			for (int j=0;j<name.length();j++) {
				
				Code.loadConst(name.charAt(j));
				Code.put(Code.putstatic);
				Code.put2(datapc++); 
			}
			Code.loadConst(-1);
			Code.put(Code.putstatic);
			Code.put2(datapc++);
			Code.loadConst(adr);
			Code.put(Code.putstatic);
			Code.put2(datapc++);
		}
	}
	
	@Override
	public void visit(MethodTypeNameWithVoid methodTypeNameWithVoid) {
		if (interfaceIn)
			return;
		int c=0;
		methodTypeNameWithVoid.obj.setAdr(Code.pc);
		
		if (currentClass!=null) {
			c=1;
			currentClassMethods.add(methodTypeNameWithVoid.obj);
			virtualTableStart += methodTypeNameWithVoid.obj.getName().length() + 2;
		}
		
		
		
		if ("main".equalsIgnoreCase(methodTypeNameWithVoid.getMethName())) {
			mainPc = Code.pc;
			//System.out.println(virtualTableStart);
			
			
		}
		
		// Collect arguments and local variables.
		SyntaxNode methodNode = methodTypeNameWithVoid.getParent();
		VarCounter varCnt = new VarCounter();
		methodNode.traverseTopDown(varCnt);
		FormParamCounter fpCnt = new FormParamCounter();
		methodNode.traverseTopDown(fpCnt);
		
		
		
		// Generate the entry.
		Code.put(Code.enter);
		Code.put(fpCnt.getCount() + c);
		Code.put(varCnt.getCount() + fpCnt.getCount() + c);
	
		if ("main".equalsIgnoreCase(methodTypeNameWithVoid.getMethName())) {
			generateVirtualTable();
		}
		
	}
	
	@Override
	public void visit(SingleVarClass singleVarClass) {
		//varCount++;
	}

	@Override
	public void visit(FormalParamDecl FormalParam) {
		//paramCnt++;
	}	
	
	
	@Override
	public void visit(MethodDecl MethodDecl) {
		if (!returnFound) {
			Code.put(Code.exit);
			Code.put(Code.return_);

		}
		returnFound = false;
		
	}
	
	@Override
	public void visit(ReturnSmt ReturnExpr) {
		returnFound=true;
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	@Override
	public void visit(ReturnVoid ReturnNoExpr) {
		returnFound=true;
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	@Override
	public void visit(Assign assign) {
		
		if (assign.getDesignator().obj.getKind()==Obj.Var && assign.getDesignator().obj.getType().getKind()==Struct.Enum) {
			Code.store(new Obj(Obj.Var,"%",Tab.intType,assign.getDesignator().obj.getAdr(),assign.getDesignator().obj.getLevel()));
		} 
		else {
			//System.out.println(" storeobj "  + assign.getDesignator().obj.getName() +" " + assign.getDesignator().obj.getAdr());
			
			//System.out.println(" storedesign "  + getCurrDes().getName() +" " + getCurrDes().getAdr());
			
//			if (assign.getDesignator().obj.getType().getKind()==Struct.Class) {
//				Code.store(getCurrDes());
//				currentDesignator.remove(currentDesignator.size()-1);
//			}
//			else {
//				Code.store(assign.getDesignator().obj);
//			}
	//		if (getCurrDes().getName().equals("k")) {
		//		System.out.println(assign.getLine() + " " + getCurrDes().getAdr());
			//}
			Code.store(getCurrDes());
			currentDesignator.remove(getCurrDes());
			
		}
		
		
	}
	
	private boolean Minus(SyntaxNode obj) {
		while (obj.getParent()!=null) {
			if (obj.getParent() instanceof ExprMinus)
				return true;	
			if (obj.getParent() instanceof ExprNoMinus)
				break;
			obj=obj.getParent();
		}	
		return false;
	}
	
	
	//ako je expresion minus mora da se negira
	@Override
	public void visit(ExprMinus node) {
		//Code.put(Code.neg);
		//Code.load(new Obj(Obj.Con, "$", Tab.intType, -1, 0));
		//Code.put(Code.mul);
	}
	
	//ovde mora da se negira(odmah a ne na krajuaddoplist)
	@Override
	public void visit(Term term) {
		if (term.getParent().getClass() == ExprMinus.class)
			Code.put(Code.neg);
	}
	
	//konstanta null
	@Override
	public void visit(NullFact nullFact) {	
		Code.load(new Obj(Obj.Con, "$", nullFact.struct, 0, 0));
	}
	
	//konstanta number
	@Override
	public void visit(NumFact numFact) {	
		Code.load(new Obj(Obj.Con, "$", numFact.struct, numFact.getN1(), 0));
	}
	
	//char const
	@Override
	public void visit(CharFact charFact) {
		Code.load(new Obj(Obj.Con, "$", charFact.struct, charFact.getC1(), 0));
	}
	
	//boolconstmora dodam
	@Override
	public void visit(BoolFact boolFact) {
		if (boolFact.getB1().equalsIgnoreCase("true"))
			Code.load(new Obj(Obj.Con, "$", boolFact.struct, 1, 0));
		else
			Code.load(new Obj(Obj.Con, "$", boolFact.struct, 0, 0));
	}
	
	private Obj getFld(Obj fld,SyntaxNode node) {
		int ukupnopolja = 0;
		Struct pom = currentClass.getType().getElemType();
		
		//racunanje polja nadklasa
		while (pom!=null) {
			ukupnopolja+=pom.getNumberOfFields()-1;	//-polje za virtuelne fje
			pom = pom.getElemType();
		}
		
		boolean found = false;
		Object objs[] = currentClass.getType().getMembers().toArray();
		
		for (int i=0;i<objs.length;i++) {
			Obj obj = (Obj) objs[i];
			if (obj.getName().equals(fld.getName())) {
				//System.out.println(node.getName() + " onda je ovde nasao " +node.getLine());
					
				obj = new Obj(Obj.Fld,"%fldsapomerajem",obj.getType(),obj.getAdr()+ukupnopolja,obj.getLevel());	//mora novo zbog pomeraja pri izvodjenju i poljima...
				//System.out.println(" polje svoje bez this " + node.getLine() +  " " + obj.getAdr() +" " + ((Obj) objs[i]).getName() + " " + ukupnopolja);
				//Code.load(obj);
			
				return obj;
				//found = true;
				//break;
			}
			
		}
		Struct parent = currentClass.getType();
		
		while (!found && parent.getElemType()!=null) {
			
			
			ukupnopolja-=parent.getElemType().getNumberOfFields();	//smanji za njegov broj polja + 1 jer ce on da oduzme i polje za virtuelne tabele
			ukupnopolja++;
			objs = parent.getElemType().getMembers().toArray();
			
			for (int i=0;i<objs.length;i++) {
				Obj obj = (Obj) objs[i];
				if (obj.getName().equals(fld.getName())) {
					Obj moranovi = new Obj(Obj.Fld,"%",obj.getType(),obj.getAdr()+ukupnopolja,obj.getLevel());	//ovde treba -1 zbog field 0 koji sluzi za tabelu virt fja
//					System.out.println(" polje tudje bez this " + node.getLine() + " " + moranovi.getAdr() + " " + ((Obj) objs[i]).getName());
//					System.out.println(" ukupno polja  " +ukupnopolja + "  linija " +node.getLine() + " " + parent.getElemType().getNumberOfFields());

					return moranovi;
					//found = true;
					
					//break;
				}
			}
			parent=parent.getElemType();
		}
		
		return fld;
	}
	
	private void loadFld(Obj fld,SyntaxNode node) {
		int ukupnopolja = 0;
		Struct pom = currentClass.getType().getElemType();
		
		//racunanje polja nadklasa
		while (pom!=null) {
			ukupnopolja+=pom.getNumberOfFields()-1;	//-polje za virtuelne fje
			pom = pom.getElemType();
		}
		
		boolean found = false;
		Object objs[] = currentClass.getType().getMembers().toArray();
		
		for (int i=0;i<objs.length;i++) {
			Obj obj = (Obj) objs[i];
			if (obj.getName().equals(fld.getName())) {
				//System.out.println(node.getName() + " onda je ovde nasao " +node.getLine());
					
				obj = new Obj(Obj.Fld,"%fldsapomerajem",obj.getType(),obj.getAdr()+ukupnopolja,obj.getLevel());	//mora novo zbog pomeraja pri izvodjenju i poljima...
				//System.out.println(" polje svoje bez this " + node.getLine() +  " " + obj.getAdr() +" " + ((Obj) objs[i]).getName() + " " + ukupnopolja);
				Code.load(obj);
			
				found = true;
				break;
			}
			
		}
		Struct parent = currentClass.getType();
		
		while (!found && parent.getElemType()!=null) {
			
			
			ukupnopolja-=parent.getElemType().getNumberOfFields();	//smanji za njegov broj polja + 1 jer ce on da oduzme i polje za virtuelne tabele
			ukupnopolja++;
			objs = parent.getElemType().getMembers().toArray();
			
			for (int i=0;i<objs.length;i++) {
				Obj obj = (Obj) objs[i];
				if (obj.getName().equals(fld.getName())) {
					Obj moranovi = new Obj(Obj.Fld,"%",obj.getType(),obj.getAdr()+ukupnopolja,obj.getLevel());	//ovde treba -1 zbog field 0 koji sluzi za tabelu virt fja
//					System.out.println(" polje tudje bez this " + node.getLine() + " " + moranovi.getAdr() + " " + ((Obj) objs[i]).getName());
//					System.out.println(" ukupno polja  " +ukupnopolja + "  linija " +node.getLine() + " " + parent.getElemType().getNumberOfFields());

					Code.load(moranovi);
					found = true;
					
					break;
				}
			}
			parent=parent.getElemType();
		}
	}
	
	@Override
	public void visit(Designator designator) {
		SyntaxNode parent = designator.getParent();
		
		boolean assignleft = false;
		assignleft = ParentAssign(designator);
		
		if (designator.obj.getKind()==Obj.Meth) {
			currentDesignator.remove(currentDesignator.size()-1);
			return;
		}
		
		//System.out.println(" designator " + designator.obj.getName() + " " + designator.getLine());
		
		// ako je polje klase i stoji samo bez this mora da mu se sracuna adresa 
		if  (Assign.class != parent.getClass() && designator.obj.getKind()==Obj.Fld && designator.getDesignatorSufixList().getClass()==NoDesignatorSufixList.class) {
			if (Plus2Minus2(designator)) {
				Code.put(Code.dup);
			}
			loadFld(designator.obj,designator);
			//System.out.println(" usao " + designator.obj.getName() + " " + designator.getLine() + " " + designator.obj.getType().getKind());
			//moze da mu se pristupa i bez this iako nije sam pazi
		}
		// ako je polje . nesto loaduj ga
		else if (designator.obj.getKind()==Obj.Fld && designator.getDesignatorSufixList().getClass()==DesignatorSufixList.class) {
			//ne mora da znaci da je polje . nesto a i mora da mu se sracuna adresa sve jedno
			loadFld(designator.obj,designator);
			//System.out.println("evo ga " + designator.obj.getName() + " " + designator.getLine());
			//Code.load(designator.obj);
		}
		// magija za niz[1]++;
		else if (designator.getParent().getClass() == DesignatorStmtPlus2.class && designator.obj.getKind()==Obj.Elem) {
			Code.put(Code.dup2);
			Code.load(designator.obj);
		}
		else if (designator.getParent().getClass() == DesignatorStmtMinus2.class && designator.obj.getKind()==Obj.Elem) {
			Code.put(Code.dup2);
			Code.load(designator.obj);
		}
		//sto ovo ?
		else if (FactorFuncCall.class != parent.getClass() && Assign.class != parent.getClass() && designator.getDesignatorName().obj.getType().getKind()!=Struct.Enum && designator.getDesignatorName().obj.getType().getKind()!=Struct.Array && designator.getDesignatorName().obj.getType().getKind()!=Struct.Class && !ParentRead(designator)) {
			Code.load(designator.obj);
			//System.out.println(" Load 1 " + designator.obj.getName() + " " + designator.getLine());
		}
		//ako ima sufix a nije enum load
		else if (Assign.class != parent.getClass() && designator.obj.getType().getKind()==Struct.Enum && designator.getDesignatorSufixList().getClass()==NoDesignatorSufixList.class) {
			Code.load(new Obj(Obj.Var,"%",Tab.intType, designator.obj.getAdr(),designator.obj.getLevel()));	
			//System.out.println(" Load 2 " + designator.obj.getName());
		}
		
		
		if (!(Assign.class == parent.getClass() && ((Assign)parent).getDesignator()==designator)) {
			currentDesignator.remove(currentDesignator.size()-1);
		}
		else {
			//System.out.println("WTF " + designator.obj.getName() + " " + designator.getLine());
		}
		
//		if (designator.obj.getType().getKind() != Struct.Class)
//			currentDesignator.remove(currentDesignator.size()-1);
		
	}
	
	//za niz fora al, ne moze ako je sa leve strane jednako samo niz
	@Override
	public void visit(DesignatorName node) {
		
		if (node.obj.getKind()==Obj.Fld && currentClass!=null) {
			 Code.put(Code.load_n + 0);			//loaduj this za polje
			 // i ovde svakako treba da mu se sracuna adresa
			 
			 //currentDesignator.add(new Obj(Obj.Var,"this", currentClass.getType(),0,1));
			
			 //ako je niz i ako je levo od asign a ima nastavak treba da se loaduje
			 if (node.obj.getType().getKind()==Struct.Array && ((Designator)node.getParent()).getDesignatorSufixList().getClass()!=NoDesignatorSufixList.class ) {//&& node.getParent().getParent().getClass()==Assign.class)
				 loadFld(node.obj, node);
				// System.out.println(" niz levo load za niz" + node.getLine() + " " + node.obj.getName() );
			 }
			 
			 else
			 // ili ako je klasa a ima dalje ovo sam dodao i proradio je do sorta
			 if (node.obj.getType().getKind()==Struct.Class && ((Designator)node.getParent()).getDesignatorSufixList().getClass()!=NoDesignatorSufixList.class ) {//&& node.getParent().getParent().getClass()==Assign.class)
				 loadFld(node.obj, node);
				 // System.out.println(" niz levo load za klasu" + node.getLine() + " " + node.obj.getName() );
			 }	
			 // onda je store i treba svakako da mu se sracune adresa za dalje
			 else {
				 node.obj = getFld(node.obj, node);
			 }
				 
				 //System.out.println(" load this " + node.getLine() + " " + node.obj.getName());
		}
		else if (node.obj.getType().getKind()==Struct.Array && node.getParent().getParent().getClass()!=Assign.class) {
			Code.load(node.obj);
		}
		//ako ima sufixa i ako nije enum
		else if (((Designator)node.getParent()).getDesignatorSufixList().getClass() == DesignatorSufixListClass.class && node.obj.getType().getKind()!=Struct.Enum) {
			//System.out.println(" Load 2 " + node.obj.getName());
			Code.load(node.obj);
		}
		//za klase u pozivu metode
		else if (node.getParent().getParent().getClass()!=Assign.class && node.obj.getType().getKind()==Struct.Class && node.obj.getKind()!=Obj.Meth){
			Code.load(node.obj);
		}
		currentDesignator.add(node.obj);
	}
	
	
	private boolean ParentRead(SyntaxNode obj) {
		while (obj.getParent()!=null) {
			if (obj.getParent().getClass()==ReadStmt.class)
				return true;
			if (obj.getParent().getClass()==DesignatorSufixSquare.class)
				return false;
			
			obj=obj.getParent();
		}
		return false;

	}
	
	@Override
	public void visit(DesignatorSufixDot node) {
		
		if (getCurrDes().getType().getKind()==Struct.Enum) {
			
			Object objs[] = getCurrDes().getType().getMembers().toArray();
			Obj obj;
			for (int i=0;i<objs.length;i++) {
				obj = (Obj) objs[i];
				if (obj.getName().equals(node.getName())) {
					Code.loadConst(obj.getAdr());
					resetCurrDes(obj);
					break;
				}
			}
			
			
		}
		else if (getCurrDes().getType().getKind()==Struct.Class) {
			
			int ukupnopolja = 0;
			Struct pom = getCurrDes().getType().getElemType();
			
			//racunanje polja nadklasa
			while (pom!=null) {
				ukupnopolja+=pom.getNumberOfFields()-1;	//-polje za virtuelne fje
				pom = pom.getElemType();
				
			}
			
			
			boolean found = false;
			Object objs[] = getCurrDes().getType().getMembers().toArray();
			
			for (int i=0;i<objs.length;i++) {
				Obj obj = (Obj) objs[i];
				if (obj.getName().equals(node.getName())) {
					//System.out.println(node.getName() + " onda je ovde nasao " +node.getLine());
					if ((!ParentAssign(node) && (!ParentRead(node))) || ((DesignatorSufixListClass)node.getParent()).getDesignatorSufixList().getClass() != NoDesignatorSufixList.class) {
						
						if (obj.getKind()!=Obj.Meth) {
							obj = new Obj(Obj.Fld,"%fldsapomerajem",obj.getType(),obj.getAdr()+ukupnopolja,obj.getLevel());	//mora novo zbog pomeraja pri izvodjenju i poljima...
							//System.out.println(" polje svoje " + node.getLine() +  " " + obj.getAdr() +" " + ((Obj) objs[i]).getName());
							
							if (Plus2Minus2(node) && ((DesignatorSufixListClass)node.getParent()).getDesignatorSufixList().getClass() == NoDesignatorSufixList.class) {
								Code.put(Code.dup);
							}
							
							Code.load(obj);
						}	
						else {
							methodCall = true;
							methodCallObj = getCurrDes();
						}
						//System.out.println(" polje svoje " + node.getLine() +  " " + obj.getAdr() +" " + ((Obj) objs[i]).getName());
						
		
					}
					else {
						obj = new Obj(Obj.Fld,"%fldsapomerajem",obj.getType(),obj.getAdr()+ukupnopolja,obj.getLevel());	//mora novo zbog pomeraja pri izvodjenju i poljima...
						//System.out.println(" polje svoje store " + node.getLine() +  " " + obj.getAdr() +" " + ((Obj) objs[i]).getName());
						
					}
					resetCurrDes(obj);
					found = true;
					break;
				}
				
			}
			Struct parent = getCurrDes().getType();
			
			while (!found && parent.getElemType()!=null) {
				//System.out.println(" broj polja natklasa " + ukupnopolja + " " + node.getName());
				//System.out.println(" petlja " + parent.getElemType());
				ukupnopolja-=parent.getElemType().getNumberOfFields();	//smanji za njegov broj polja + 1 jer ce on da oduzme i polje za virtuelne tabele
				ukupnopolja++;
				objs = parent.getElemType().getMembers().toArray();
				
				for (int i=0;i<objs.length;i++) {
					Obj obj = (Obj) objs[i];
					if (obj.getName().equals(node.getName())) {
						//Obj moranovi = new Obj(Obj.Fld,"%",obj.getType(),obj.getAdr()+ukupnopolja,obj.getLevel());	//ovde treba -1 zbog field 0 koji sluzi za tabelu virt fja
						//System.out.println(" polje tudje " + node.getLine() + " " + obj.getAdr() + " " + ((Obj) objs[i]).getName());
						//System.out.println(node.getName() + " linija " +node.getLine());
						if ((!ParentAssign(node) && (!ParentRead(node))) || ((DesignatorSufixListClass)node.getParent()).getDesignatorSufixList().getClass() != NoDesignatorSufixList.class) {
							
							if (obj.getKind()!=Obj.Meth) {
								obj = new Obj(Obj.Fld,"%fldsapomerajem",obj.getType(),obj.getAdr()+ukupnopolja,obj.getLevel());	//mora novo zbog pomeraja pri izvodjenju i poljima...
								//System.out.println(" polje tudje " + node.getLine() +  " " + obj.getAdr() +" " + ((Obj) objs[i]).getName());
								
								if (Plus2Minus2(node) && ((DesignatorSufixListClass)node.getParent()).getDesignatorSufixList().getClass() == NoDesignatorSufixList.class) {
									Code.put(Code.dup);
								}
								
								Code.load(obj);
							}	
							else {
								methodCall = true;
								methodCallObj = getCurrDes();
							}
							
							//Code.load(moranovi);
							
							
						}
						else {
							obj = new Obj(Obj.Fld,"%fldsapomerajem",obj.getType(),obj.getAdr()+ukupnopolja,obj.getLevel());	//mora novo zbog pomeraja pri izvodjenju i poljima...
							//System.out.println(" polja br " + ukupnopolja+ " polje tudje store " + node.getLine() +  " " + obj.getAdr() +" " +node.getName());
					
						}
						found = true;
						resetCurrDes(obj);
						break;
					}
				}
				parent=parent.getElemType();
			}
			
			//Code.load(node);
			
		}
		else if (getCurrDes().getType().getKind()==Struct.Interface) {
			
			boolean found = false;
			Object objs[] = getCurrDes().getType().getMembers().toArray();
			
			for (int i=0;i<objs.length;i++) {
				Obj obj = (Obj) objs[i];
				if (obj.getName().equals(node.getName())) {
					//System.out.println(node.getName() + " onda je ovde nasao " +node.getLine());
					if ((!ParentAssign(node) && (!ParentRead(node))) || ((DesignatorSufixListClass)node.getParent()).getDesignatorSufixList().getClass() != NoDesignatorSufixList.class) {
						
						if (obj.getKind()!=Obj.Meth) {
							Code.load(obj);
						}	
						else {
							methodCall = true;
							methodCallObj = getCurrDes();
						}
		
					}
					resetCurrDes(obj);
					found = true;
					break;
				}
				
			}
		}
		
	}
	
	private boolean ParentAssign(SyntaxNode obj) {
		while (obj.getParent()!=null) {
			if (obj.getParent().getClass()==DesignatorSufixSquare.class)	// on sluzi za indeks samo
				return false;
			if (obj.getParent().getClass()==Assign.class && ((Assign)obj.getParent()).getDesignator()==obj)
				return true;
			obj=obj.getParent();
		}
		return false;
	}
	
	private boolean Plus2Minus2(SyntaxNode node) {
		while (node.getParent()!=null) {
			if (node.getParent().getClass()==DesignatorSufixSquare.class)	//ne treba njega plus plus on sluzi za indeks samo
				return false;
			if (node.getParent().getClass()==DesignatorStmtMinus2.class || node.getParent().getClass()==DesignatorStmtPlus2.class)
				return true;
			node=node.getParent();
		}
		return false;
	}
	
	//proveri dal se u klasi nalazi ova fja
	private boolean checkFunInClass(Struct klasa,String fja) {
		boolean found = false;
		Object objs[] = klasa.getMembers().toArray();
		Obj objinclass;
	
		for (int i=0;i<objs.length;i++) {
			objinclass = (Obj) objs[i];
			if (objinclass.getKind()==Obj.Meth && objinclass.getName().equals(fja)) {
				found=true;
				break;
			}
		}
		
		//ako nema metode u toj klasi pogledaj u roditelju
		if (!found && klasa.getElemType()!=null) {
			objs = klasa.getElemType().getMembers().toArray();
			for (int i=0;i<objs.length;i++) {
				objinclass = (Obj) objs[i];
				if (objinclass.getKind()==Obj.Meth && objinclass.getName().equals(fja)) {
					found=true;
					break;
				}
			}
			
		}
		
		return found;
	}
	@Override
	public void visit(DesignatorSufixSquare node) {
		
		Obj obj = getCurrDes();
		
		boolean assignleft = false;
		assignleft = ParentAssign(node);	
		
	
		
		resetCurrDes(new Obj(Obj.Elem,"%", getCurrDes().getType().getElemType(),getCurrDes().getAdr(),getCurrDes().getLevel()));
		
		
		//ako je klasa u nizu pa treba da se uradi aload za virtuelne fje opet treba da se poturi indeks i adresa niza
		if (getCurrDes().getKind()==Obj.Elem && getCurrDes().getType().getKind()==Struct.Class) {
			if (node.getParent().getClass()==DesignatorSufixListClass.class) {
				DesignatorSufixListClass sufix = (DesignatorSufixListClass) node.getParent();
				//String fja = ((DesignatorSufixDot) sufix.getDesignatorSufixList()).getName();
				//if (checkFunInClass(getCurrDes().getType(),fja)) {
				
				//Code.put(Code.dup2);
					//System.out.println(" evo ga "  + getCurrDes().getName());
				//}	
				
				
				//reseno na drugi nacin obilaskom opet
				
			}
			
		}
				
		if (!assignleft && !Plus2Minus2(node) && node.getParent().getParent().getParent().getClass() != ReadStmt.class) {	//ako nije niz[i]++ ili niz[i]-- ili ako nije read.... ili kao ima sufix onda treba load
			
			
			Code.load(getCurrDes());
		
		}
		else if (((DesignatorSufixListClass)node.getParent()).getDesignatorSufixList().getClass() != NoDesignatorSufixList.class) {//mora read provera)
			Code.load(getCurrDes());
		}
		
		
		//Code.load(new Obj(Obj.Var,"%",obj.getType(),obj.getAdr(),obj.getLevel()));
	}
	
	
	//poziv metoda klase 
	private void classMethodCall(Obj obj) {
		//int adr = hashVirtualTable.get(methodCallObj.getType());
		//System.out.println(adr);
		//System.out.println("invoke ovde " + methodCallObj.getName());
		
		String name = obj.getName();
		
		
	
		//Code.load(methodCallObj);	//loadujuj field 0
		
		if (methodInside)
			Code.put(Code.load_n + 0);
		
		Code.put(Code.getfield);
		Code.put2(0);
		
		Code.put(Code.invokevirtual);
		for (int j=0;j<name.length();j++) {
			Code.put4(name.charAt(j));
		}
		Code.put4(-1);
		
		methodCall = false;
		methodCallObj = null;
		methodInside = false;
	}
	
	
	//poziv fje iz metoda bez 	this ne mora da znaci da je bez this
	@Override
	public void visit(FuncName node) {
		//ne mora da znaci da je bez this ako nije opet generisi taj designator
		if (node.getDesignator().getDesignatorSufixList().getClass()==DesignatorSufixListClass.class) {
			//System.out.println(" ovde eeee " + node.getDesignator().obj.getName() + " " + node.getLine());
			//CodeGenerator get = new CodeGenerator();
			//get.currentClass = currentClass;
			//get.methodCall = methodCall;
			
			//node.getDesignator().traverseBottomUp(get);
			return;
		}
		
		Obj obj = node.getDesignator().obj;
		if (currentClass!=null) {
			Object objs[] = currentClass.getType().getMembers().toArray();
			boolean found = false;
			Obj objinclass = null;
		
			for (int i=0;i<objs.length;i++) {
				objinclass = (Obj) objs[i];
				if (objinclass.getKind()==Obj.Meth && objinclass.getName().equals(obj.getName())) {
					found=true;
					break;
				}
			}
			
			//ako nema metode u toj klasi pogledaj u roditelju
			if (!found && currentClass.getType().getElemType()!=null) {
				objs = currentClass.getType().getElemType().getMembers().toArray();
				for (int i=0;i<objs.length;i++) {
					objinclass = (Obj) objs[i];
					if (objinclass.getKind()==Obj.Meth && objinclass.getName().equals(obj.getName())) {
						found=true;
						break;
					}
				}
				
			}
			
			if (found) {
				Code.put(Code.load_n + 0);			//loaduj this za metodu	i more invoke ovde jbt
				
				//System.out.println(" metoda load this " + node.getLine() + " " + obj.getName() );
				methodCall = true;
				methodCallObj = objinclass;
				methodInside = true;
			}
		}
		
	}
	// poziv fje
	@Override
	public void visit(FactorFuncCall FuncCall) {
		if (methodCall) {
			CodeGenerator get = new CodeGenerator();
			get.currentClass = currentClass;
			get.methodCall = methodCall;
			
			if (FuncCall.getFuncName().getDesignator().getDesignatorSufixList().getClass()==DesignatorSufixListClass.class)	//ako ima sufixa ako nema onda je samo fja
				FuncCall.getFuncName().traverseBottomUp(get);	//generisi opet taj designator da bi se loadalo taj obj zbog niza...
			
			classMethodCall(FuncCall.getFuncName().getDesignator().obj);
		
			return;
		}
		
		if ("ord".equalsIgnoreCase(FuncCall.getFuncName().getDesignator().obj.getName()) || "chr".equalsIgnoreCase(FuncCall.getFuncName().getDesignator().obj.getName())) {
			return;
		}
		if ("len".equalsIgnoreCase(FuncCall.getFuncName().getDesignator().obj.getName())) {
			Code.put(Code.arraylength);
			return;
		}
		
		Obj functionObj = FuncCall.getFuncName().getDesignator().obj;
		int offset = functionObj.getAdr() - Code.pc; 
		Code.put(Code.call);
		Code.put2(offset);
	}
	
	//poziv fje ovako iz bleje bez izraza
	public void visit(DesignatorStmtFunc node) {
		if (methodCall) {
			CodeGenerator get = new CodeGenerator();
			get.currentClass=currentClass;
			get.methodCall = methodCall;
			if (node.getFuncName().getDesignator().getDesignatorSufixList().getClass()==DesignatorSufixListClass.class)	//ako ima sufixa ako nema onda je samo fja	
				node.getFuncName().traverseBottomUp(get);	//generisi opet taj designator da bi se loadalo taj obj
			
			classMethodCall(node.getFuncName().getDesignator().obj);
			return;
		}
		if ("ord".equalsIgnoreCase(node.getFuncName().getDesignator().obj.getName()) || "chr".equalsIgnoreCase(node.getFuncName().getDesignator().obj.getName())) {
			return;
		}
		
		if ("len".equalsIgnoreCase(node.getFuncName().getDesignator().obj.getName())) {
			Code.put(Code.arraylength);
			return;
		}
		
		Obj functionObj = node.getFuncName().getDesignator().obj;
		int offset = functionObj.getAdr() - Code.pc; 
		Code.put(Code.call);
		Code.put2(offset);
		
		// ako nije void treba pop
		if (node.getFuncName().getDesignator().obj.getType()!=Tab.noType)
			Code.put(Code.pop);
	}
	
	//print
	@Override
	public void visit(PrintStmt PrintStmt) {
		if (PrintStmt.getNumConstOpt().getClass()==NoNumConstOpt.class) {
			Code.put(Code.const_5);
			
		}
		else {
			Code.loadConst(((NumConstOptClass)PrintStmt.getNumConstOpt()).getN1());
			//Code.put(((NumConstOptClass)PrintStmt.getNumConstOpt()).getN1());
			
		}
			
		if (PrintStmt.getExpr().struct==Tab.charType) {
			
			Code.put(Code.bprint);
		}
		else 
		{
			Code.put(Code.print);
		}
		
	}
	
	// read
	@Override
	public void visit(ReadStmt readStmt) {
		if (readStmt.getDesignator().obj.getType()==Tab.intType || readStmt.getDesignator().obj.getType()==SemanticAnalyzer.boolType) {
			Code.put(Code.read);
			Code.store(readStmt.getDesignator().obj);
		}
		else if (readStmt.getDesignator().obj.getType()==Tab.charType) {
			Code.put(Code.bread);
			Code.store(readStmt.getDesignator().obj);
		}
	}
	
	// odavde ka dole operatori + - ...
	@Override
	public void visit(AddopTermListClass node) {
		
		if (node.getAddop() instanceof Plus)
			Code.put(Code.add);
		
		if (node.getAddop() instanceof Minus)
			Code.put(Code.sub);
	}
	@Override
	public void visit(MulopFactorListClass node) {

		if (node.getMulop() instanceof Mul)
			Code.put(Code.mul);
		
		if (node.getMulop() instanceof Div)
			Code.put(Code.div);
		
		if (node.getMulop() instanceof Mod)
			Code.put(Code.rem);
		
	}
	
	
	@Override
	public void visit(NewFactSquare node) {
		if (node.getType().struct==Tab.charType) {
			Code.put(Code.newarray);
			Code.put(0);
		}
		else {
			Code.put(Code.newarray);
			Code.put(1);
		}
	}
	
	// new klasa
	@Override
	public void visit(NewFact node) {
		
		Obj obj = node.getNewType().obj;
	
		int numfields = obj.getType().getNumberOfFields();
		
		Struct pom = obj.getType().getElemType();
		while (pom!=null) {
			numfields += pom.getNumberOfFields()-1;
			pom=pom.getElemType();
		}
		
		numfields*=4;
		
		int adr = hashVirtualTable.get(obj.getType());
		
		Code.put(Code.new_);
		Code.put2(numfields);
	
		//System.out.println("asnewign " + obj.getName() + " " + adr + " " + numfields);
		Code.put(Code.dup);
		
		Code.loadConst(adr);
		Code.put(Code.putfield); 
		Code.put2(0); 
		
		//if (obj!=Tab.noObj)
		//	System.out.println(obj.getName() + " " + numfields);
		
	}
	
	// ++
	@Override
	public void visit(DesignatorStmtPlus2 node) {
		
		Code.load(new Obj(Obj.Con, "%", Tab.intType, 1, 0));
		Code.put(Code.add);
		
		Code.store(node.getDesignator().obj);
	}
	
	// --
	@Override
	public void visit(DesignatorStmtMinus2 node) {
		Code.load(new Obj(Obj.Con, "%", Tab.intType, 1, 0));
		Code.put(Code.sub);
		
//		CodeGenerator get = new CodeGenerator();
//		get.currentClass=currentClass;
//		get.methodCall = methodCall;
//		node.getDesignator().traverseBottomUp(get);
		
		Code.store(node.getDesignator().obj);
	}

	
	
	//ifovi lomi me krecu
	// if 
	@Override
	public void visit(IfStmtJmp node) {
		Code.put(Code.jmp); 
		Code.put2(3); 
		
//		Code.fixup(adr);
//		adr = Code.pc-2;
		
		Code.fixup(adr.remove(adr.size()-1));
		adr.add(Code.pc-2);
		
	}
	// else
	@Override
	public void visit(ElseStmt node) {
		Code.fixup(adr.get(adr.size()-1));
	}
	
	//kada se zavrsi if treba da se skine iz adr steka mog
	@Override
	public void visit(If node) {
		adr.remove(adr.size()-1);
	}
	
	//kada se zavrsi if treba da se skine iz adr steka mog
	@Override
	public void visit(IfElse node) {
		adr.remove(adr.size()-1);
	}
	
	//uslov ostavi mesto za skok
	@Override
	public void visit(ConditionIfClass node) {
		Code.load(new Obj(Obj.Con, "%", Tab.intType, 0, 0));
		Code.put(Code.jcc + Code.eq); 
		Code.put2(0); 
		//adr = Code.pc - 2;
		adr.add(Code.pc-2);
	}
	
	@Override
	public void visit(Condition node) {
		
	}
	
	// OR
	@Override
	public void visit(CondTermListClass node) {
		Code.put(Code.add);
		Code.load(new Obj(Obj.Con, "%", SemanticAnalyzer.boolType, 1, 0));
		Code.put(Code.jcc + Code.ge);
		Code.put2(7);
		
		Code.load(new Obj(Obj.Con, "%", SemanticAnalyzer.boolType, 0, 0));
		Code.put(Code.jmp);
		Code.put2(4);
		
		Code.load(new Obj(Obj.Con, "%", SemanticAnalyzer.boolType, 1, 0));
	}
	
	// AND
	@Override
	public void visit(CondFactListClass node) {
		Code.put(Code.add);
		Code.load(new Obj(Obj.Con, "%", SemanticAnalyzer.boolType, 2, 0));
		Code.put(Code.jcc + Code.eq);
		Code.put2(7);
		
		Code.load(new Obj(Obj.Con, "%", SemanticAnalyzer.boolType, 0, 0));
		Code.put(Code.jmp);
		Code.put2(4);
		
		Code.load(new Obj(Obj.Con, "%", SemanticAnalyzer.boolType, 1, 0));
	}
	
	// OVO MORA DA JE BOOL
	@Override
	public void visit(CondFactExpr node) {
		// nista bool je vec stavljen
	}
	
	// RELOP OPERATOR
	@Override
	public void visit(CondFactExprRelopExpr node) {
		int uslov = 0;
		
		if (node.getRelop() instanceof Eq) 
			uslov = Code.eq;
		
		if (node.getRelop() instanceof Neq) 
			uslov = Code.ne;
		
		if (node.getRelop() instanceof Gt) 
			uslov = Code.gt;
		
		if (node.getRelop() instanceof Gte) 
			uslov = Code.ge;
		
		if (node.getRelop() instanceof Lt) 
			uslov = Code.lt;
		
		if (node.getRelop() instanceof Lte) 
			uslov = Code.le;
		
		
		Code.put(Code.jcc + uslov);
		Code.put2(7);
		
		Code.load(new Obj(Obj.Con, "%", SemanticAnalyzer.boolType, 0, 0));
		Code.put(Code.jmp);
		Code.put2(4);
		Code.load(new Obj(Obj.Con, "%", SemanticAnalyzer.boolType, 1, 0));
		
	}
	
	//for odavde
	
	//treba uslov i skok
	@Override
	public void visit(ConditionOptClass node) {
		Code.load(new Obj(Obj.Con, "%", Tab.intType, 0, 0));
		Code.put(Code.jcc + Code.ne); 
		Code.put2(3); 
		forAdrFixupTrue.add(Code.pc-2);
		
		Code.put(Code.jmp);
		Code.put2(3);
		forAdrFixupFalse.add(Code.pc-2);
		
		forAdrUpInkr.add(Code.pc);
		
		//za break
		breakList.add(new ArrayList<>());
		
	}
	
	
	// nema uslova beskonacna petlja
	@Override
	public void visit(NoConditionOpt node) {
		Code.put(Code.jmp);
		Code.put2(3); 
		forAdrFixupTrue.add(Code.pc-2);
		forAdrUpInkr.add(Code.pc);
		forAdrFixupFalse.add(-1);
		
		//za break
		breakList.add(new ArrayList<>());
		
	}
	
	
	private void designatorIf(DesignatorStatementOpt node) {
		if (forDesOpt == 0) {	//prvi ifdesopt
			forAdrUpCond.add(Code.pc);
		}
		else {	//drugi if desopt
			Code.putJump(forAdrUpCond.remove(forAdrUpCond.size()-1));
			Code.fixup(forAdrFixupTrue.remove(forAdrFixupTrue.size()-1));
		}
		
		forDesOpt++;
		
		forDesOpt %= 2;
	}
	//designator ima
	@Override
	public void visit(DesignatorStmtOpt node) {
		designatorIf(node);
	}
	
	//designator nema
	@Override
	public void visit(NoDesignatorStmtOpt node) {
		designatorIf(node);
	}
	
	//kad se zavrsi for stavi skok na inkrement i za breakove i fiksuj i skini inkr adresu koja je trebala za continue
	@Override
	public void visit(ForStatement node) {
		//System.out.println(" for " +node.getLine());
		Code.putJump(forAdrUpInkr.get(forAdrUpInkr.size()-1));
		if (forAdrFixupFalse.get(forAdrFixupFalse.size()-1)!=-1)
			Code.fixup(forAdrFixupFalse.remove(forAdrFixupFalse.size()-1));
		else
			forAdrFixupFalse.remove(forAdrFixupFalse.size()-1);
		
		for (int i=0;i<breakList.get(breakList.size()-1).size();i++) {
			Code.fixup(breakList.get(breakList.size()-1).remove(0));
		}
		
		forAdrUpInkr.remove(forAdrUpInkr.size()-1);

		
		breakList.remove(breakList.size()-1);
	}
	
	// break
	@Override
	public void visit(BreakStmt node) {
		Code.put(Code.jmp);
		Code.put2(3); 
		breakList.get(breakList.size()-1).add(Code.pc-2);
		
	}
	
	// continue
	@Override
	public void visit(ContinueStmt node) {
		Code.putJump(forAdrUpInkr.get(forAdrUpInkr.size()-1));
		//Code.put(Code.jmp);
		//Code.put2(forAdrUpInkr.get(forAdrUpInkr.size()-1)); 
		
	}
	
	
	//klase
	// pocetak klase
	@Override
	public void visit(ClassName node) {
		hashVirtualTable.put(node.obj.getType(), virtualTableStart);
		currentClass = node.obj;
		
		//System.out.println(currentClass.getName());
	}
	
	//kraj klase
	@Override
	public void visit(ClassDecl node) {
		Struct parent = currentClass.getType().getElemType();
		// ka gore u izvodjenju mora while
		while (parent!=null) {
			Object objs[] = parent.getMembers().toArray();
			Obj obj;
			for (int i=0;i<objs.length;i++) {
				obj = (Obj) objs[i];
				if (obj.getKind()==Obj.Meth) {
					currentClassMethods.add(obj);
					virtualTableStart+=obj.getName().length()+2;
				}
			}
			parent=parent.getElemType();
		}
		
		if (node.getClassMethods().getClass()!=NoClassMethods.class) {
			virtualTableStart += 1;
			currentClassMethods.add(null);
		}
		currentClass = null;
		
		
	}
	

	
}
