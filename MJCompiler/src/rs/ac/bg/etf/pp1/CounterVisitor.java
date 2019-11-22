package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;


public class CounterVisitor extends VisitorAdaptor {
	
	protected int count;
	
	public int getCount() {
		return count;
	}
	
	public static class FormParamCounter extends CounterVisitor {

		@Override
		public void visit(FormalParam formalParam) {
			count++;
		}	
		
		@Override
		public void visit(FormalParamSquares formalParam) {
			count++;
		}	
	}
	
	public static class VarCounter extends CounterVisitor {		
		@Override
		public void visit(VardClass VarDecl) {
			count++;
		}
		
		@Override
		public void visit(VardClassSquares VarDecl) {
			count++;
		}
	}
}
