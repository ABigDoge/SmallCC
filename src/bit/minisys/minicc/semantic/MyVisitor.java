package bit.minisys.minicc.semantic;

import bit.minisys.minicc.parser.ast.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class MyVisitor implements ASTVisitor {

	Map<String, SymbolProperty> globalst = new HashMap<>();
	Map<String, SymbolProperty> funcmap = new HashMap<>();
	Map<String, Integer> funcinfo = new HashMap<>();
	public static List<String> strlist = new LinkedList<>();
	Stack<Map<String, SymbolProperty>> stack = new Stack<>();
	public static Stack<Map<String, SymbolProperty>> ststore = new Stack<>();
	
	SymbolProperty spnow = new SymbolProperty();
	
	boolean inIteration = false;
	boolean hasReturn = false;
	String typeCheck = "no";

	ArrayList<String> gotolist = new ArrayList<>();
	
	public static Stack<Map<String, SymbolProperty>> getstack() {
		return ststore;
	}

	public static List<String> getstr() {
		return strlist;
	}

	@Override
	public void visit(ASTCompilationUnit program) throws Exception {
		// TODO Auto-generated method stub
		for(ASTNode item : program.items) {
		//	System.out.println("class: " + item.getClass().getName());
			spnow.scope = "global";
			item.accept(this);
		}
		
	}

	@Override
	public void visit(ASTDeclaration declaration) throws Exception {
		// TODO Auto-generated method stub
	//	SymbolProperty sp = new SymbolProperty();
		for(ASTToken specifier : declaration.specifiers) {
		//	System.out.println("class: " + specifier.getClass().getName());
			spnow.datatype = specifier.value;
		}

		spnow.vartype = "variable";
		for(ASTInitList initlist : declaration.initLists) {
			initlist.accept(this);
		}

	}

	@Override
	public void visit(ASTArrayDeclarator arrayDeclarator) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ASTVariableDeclarator variableDeclarator) throws Exception {
		// TODO Auto-generated method stub
		String varname = variableDeclarator.identifier.value;
	
		SymbolProperty spnew = new SymbolProperty(spnow.vartype, spnow.datatype, spnow.scope);

		if(spnow.scope.equals("global")) {
			if(!globalst.containsKey(varname)) {
				globalst.put(varname, spnew);
			}
			else {
				System.out.println("ES02: " + spnew.vartype + " " 
					+ varname + " has been defined");
			}
		}
		else {
	
			Map<String, SymbolProperty> mapnow = stack.peek();
			if(!mapnow.containsKey(varname)) {
				mapnow.put(varname, spnew);
		//		System.out.println("new var: " + spnew.datatype + " " + varname);
			}
			else {
				System.out.println("ES02: " + spnew.vartype + " " 
					+ varname + " has been defined");
			}
		}
	}
		

	@Override
	public void visit(ASTFunctionDeclarator functionDeclarator) throws Exception {
		// TODO Auto-generated method stub
		spnow.vartype = "function";
		ASTDeclarator decl = functionDeclarator.declarator;
		decl.accept(this);

	}

	@Override
	public void visit(ASTParamsDeclarator paramsDeclarator) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ASTArrayAccess arrayAccess) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ASTBinaryExpression binaryExpression) throws Exception {
		// TODO Auto-generated method stub
		ASTExpression e1 = binaryExpression.expr1;
		ASTExpression e2 = binaryExpression.expr2;
		String opt = binaryExpression.op.value;
	//	System.out.println("op: " + opt);
		
		if (opt.equals("<<") || opt.equals(">>") || opt.equals("&") ||
			opt.equals("|") || opt.equals("^")) {
				typeCheck = "int";
		//		System.out.println("typecheck: " + typeCheck);
			}
	
		e1.accept(this);
		e2.accept(this);
		
		typeCheck = "no";

	}

	@Override
	public void visit(ASTBreakStatement breakStat) throws Exception {
		// TODO Auto-generated method stub
		if (inIteration == false) {
			System.out.println("ES03: break statement should be in a loop.");
		}

	}

	@Override
	public void visit(ASTContinueStatement continueStatement) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ASTCastExpression castExpression) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ASTCharConstant charConst) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ASTCompoundStatement compoundStat) throws Exception {
		// TODO Auto-generated method stub
		Map<String, SymbolProperty> newst = new HashMap<>();
		stack.push(newst);
		for (ASTNode item : compoundStat.blockItems) {
			item.accept(this);
		}		
		funcmap = stack.firstElement();
		ststore.push(funcmap);
		stack.pop();

	}

	@Override
	public void visit(ASTConditionExpression conditionExpression) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ASTExpression expression) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ASTExpressionStatement expressionStat) throws Exception {
		// TODO Auto-generated method stub
		for (ASTExpression exp : expressionStat.exprs) {
			exp.accept(this);
		}

	}

	@Override
	public void visit(ASTFloatConstant floatConst) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ASTFunctionCall funcCall) throws Exception {
		// TODO Auto-generated method stub
	//	System.out.println("funcall");
		ASTExpression exp = funcCall.funcname;
		spnow.vartype = "function";
		exp.accept(this);

		for (ASTExpression arg : funcCall.argList) {
		//	System.out.println("arg");
			arg.accept(this);
			
		}


	}

	@Override
	public void visit(ASTGotoStatement gotoStat) throws Exception {
		// TODO Auto-generated method stub
		String varname = gotoStat.label.value;
		gotolist.add(varname);

	}

	@Override
	public void visit(ASTIdentifier identifier) throws Exception {
		// TODO Auto-generated method stub
		String varname = identifier.value;
	//	System.out.println("id name: " + varname);
		SymbolProperty sptmp = new SymbolProperty();

		if(spnow.vartype.equals("function")) {
			if(!globalst.containsKey(varname)) {
		//		System.out.println("ES01: " + spnow.vartype + " " 
		//			+ varname + " is not defined");
				return;
			}
		}
		else {
			int flag = 0;
			for (Map<String, SymbolProperty> mapnow : stack) {
				if(mapnow.containsKey(varname)) {
					flag = 1;
					sptmp = mapnow.get(varname);
			//		System.out.println("find it: " + sptmp.datatype + " " + varname);
					break;
				}
			}
			if(flag == 0 ) {
				if (globalst.containsKey(varname) == true) {
					sptmp = globalst.get(varname);
				}
				else {
			//		System.out.println("ES01: " + spnow.vartype + " " 
			//			+ varname + " is not defined");
					return;
				}
			}
		}

		//typecheck
	//	System.out.println("datatype: " + sptmp.datatype);
	//	System.out.println("typeCheck: " + typeCheck);
		if( typeCheck.equals("no") ) {
			return;
		}
		else if ( !sptmp.datatype.equals(typeCheck) ) {
			System.out.println("ES05: operand of(<< >> & | ^) should be " 
				+ typeCheck );
		}

	}

	@Override
	public void visit(ASTInitList initList) throws Exception {
		// TODO Auto-generated method stub
		ASTDeclarator decl = initList.declarator;
		decl.accept(this);
		for ( ASTExpression expr : initList.exprs ) {
			expr.accept(this);
		}

	}

	@Override
	public void visit(ASTIntegerConstant intConst) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ASTIterationDeclaredStatement iterationDeclaredStat) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ASTIterationStatement iterationStat) throws Exception {
		// TODO Auto-generated method stub
		inIteration = true;
		ASTStatement st = iterationStat.stat;
		st.accept(this);
		inIteration = false;

	}

	@Override
	public void visit(ASTLabeledStatement labeledStat) throws Exception {
		// TODO Auto-generated method stub
		SymbolProperty spnew = new SymbolProperty("label", "no", spnow.scope);
		String varname = labeledStat.label.value;
		Map<String, SymbolProperty> mapnow = stack.firstElement();
		mapnow.put(varname, spnew);

		ASTStatement st = labeledStat.stat;
		st.accept(this);

	}

	@Override
	public void visit(ASTMemberAccess memberAccess) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ASTPostfixExpression postfixExpression) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ASTReturnStatement returnStat) throws Exception {
		// TODO Auto-generated method stub
		hasReturn = true;

	}

	@Override
	public void visit(ASTSelectionStatement selectionStat) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ASTStringConstant stringConst) throws Exception {
		// TODO Auto-generated method stub
	//	System.out.println("string");
		strlist.add(stringConst.value);

	}

	@Override
	public void visit(ASTTypename typename) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ASTUnaryExpression unaryExpression) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ASTUnaryTypename unaryTypename) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ASTFunctionDefine functionDefine) throws Exception {
		// TODO Auto-generated method stub
	//	SymbolProperty sp = new SymbolProperty();
		for(ASTToken specifier : functionDefine.specifiers) {
		//	System.out.println("class: " + specifier.getClass().getName());
			spnow.vartype = "function";
			spnow.datatype = specifier.value;
		}
		ASTDeclarator fdecl = functionDefine.declarator;
		fdecl.accept(this);

		//enter function
		spnow.scope = "local";
		ASTCompoundStatement cs = functionDefine.body;
		cs.accept(this);
		spnow.scope = "global";

		//check goto label
		if (!gotolist.isEmpty()) {
			for (String gotolabel : gotolist) {
				if( !funcmap.containsKey(gotolabel) ) {
					System.out.println("ES07: label " + gotolabel + " is not defined");
				}
			}
		}

		//check return 
		if (hasReturn == false) {
			System.out.println("ES08: no return statement");
		}
		hasReturn = false;

	}

	@Override
	public void visit(ASTDeclarator declarator) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ASTStatement statement) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ASTToken token) throws Exception {
		// TODO Auto-generated method stub

	}

}
