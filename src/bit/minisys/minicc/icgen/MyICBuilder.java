package bit.minisys.minicc.icgen;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.python.antlr.PythonParser.else_clause_return;

import bit.minisys.minicc.parser.ast.*;

public class MyICBuilder implements ASTVisitor{

	private Map<ASTNode, ASTNode> map;				// 存储节点-返回值
	public static Map<String, TemporaryLabel> labellist;  // label列表
	public static List<Quat> quats;						// 四元式
	public static Map<String, Integer> funcinfo;
//	public static 
	private Integer tmpId;							// 临时变量编号
    private Integer tmpLb;
	int hasbreak = 0;
	int initeration = 0;
	public MyICBuilder() {
		map = new HashMap<ASTNode, ASTNode>();
		labellist = new HashMap<String, TemporaryLabel>();
		quats = new LinkedList<Quat>();
		funcinfo = new HashMap<>();
		tmpId = 0;
        tmpLb = 0;
	}
	public static List<Quat> getQuats() {
		return quats;
	}
	public static Map<String, TemporaryLabel> getlblist() {
		return labellist;
	}
	public static Map<String, Integer> getfuncinfo() {
		return funcinfo;
	}
	public Map<ASTNode, ASTNode> getmap(){
		return map;
	}

	@Override
	public void visit(ASTCompilationUnit program) throws Exception {
		for (ASTNode node : program.items) {
			if(node instanceof ASTFunctionDefine)
				visit((ASTFunctionDefine)node);
		}
	}

	@Override
	public void visit(ASTDeclaration declaration) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ASTArrayDeclarator arrayDeclarator) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ASTVariableDeclarator variableDeclarator) throws Exception {
		// TODO Auto-generated method stub
		map.put(variableDeclarator, variableDeclarator.identifier);
		
	}

	@Override
	public void visit(ASTFunctionDeclarator functionDeclarator) throws Exception {
		// TODO Auto-generated method stub
		ASTDeclarator funcd = functionDeclarator.declarator;
		funcd.accept(this);
		ASTNode func = map.get(funcd);
		int argnum=0;
		for (ASTParamsDeclarator para : functionDeclarator.params) {
			argnum++;
		}
		funcinfo.put(((ASTIdentifier)func).value, argnum);

		Quat quat = new Quat("enter", func, null, null);
		quats.add(quat);
		map.put(functionDeclarator, func);
		
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
		String op = binaryExpression.op.value;
		ASTNode res = null;
		ASTNode opnd1 = null;
		ASTNode opnd2 = null;
		
		if (op.equals("=")) {
			
			visit(binaryExpression.expr1);
			res = map.get(binaryExpression.expr1);
			
			if (binaryExpression.expr2 instanceof ASTIdentifier) {
				opnd1 = binaryExpression.expr2;
			}else if(binaryExpression.expr2 instanceof ASTIntegerConstant) {
				opnd1 = binaryExpression.expr2;
			}else if(binaryExpression.expr2 instanceof ASTBinaryExpression) {
				ASTBinaryExpression value = (ASTBinaryExpression)binaryExpression.expr2;
				op = value.op.value;
				visit(value.expr1);
				opnd1 = map.get(value.expr1);
				visit(value.expr2);
				opnd2 = map.get(value.expr2);
			}else if(binaryExpression.expr2 instanceof ASTUnaryExpression){
				visit(binaryExpression.expr2);
				opnd1 = map.get(binaryExpression.expr2);
			}else if(binaryExpression.expr2 instanceof ASTFunctionCall){
				visit(binaryExpression.expr2);
				opnd1 = map.get(binaryExpression.expr2);
			}
			
		}else if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/")
                 || op.equals(">") || op.equals("<")) {
	
			res = new TemporaryValue(++tmpId);
			visit(binaryExpression.expr1);
			opnd1 = map.get(binaryExpression.expr1);
			visit(binaryExpression.expr2);
			opnd2 = map.get(binaryExpression.expr2);
		} 
		else if (op.equals("+=")) {
			
			op = "+";
			visit(binaryExpression.expr1);
			res = map.get(binaryExpression.expr1);
			opnd1 = map.get(binaryExpression.expr1);
			visit(binaryExpression.expr2);
			opnd2 = map.get(binaryExpression.expr2); 

		}
		else if (op.equals("-=")) {
			
			op = "-";
			visit(binaryExpression.expr1);
			res = map.get(binaryExpression.expr1);
			opnd1 = map.get(binaryExpression.expr1);
			visit(binaryExpression.expr2);
			opnd2 = map.get(binaryExpression.expr2); 

		}
    		
		// build quat
		Quat quat = new Quat(op, res, opnd1, opnd2);
		quats.add(quat);
		map.put(binaryExpression, res);
	}

	@Override
	public void visit(ASTBreakStatement breakStat) throws Exception {
		// TODO Auto-generated method stub
		hasbreak = 1;
		
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
		ASTNode ret =  map.get(compoundStat);
		for (ASTNode node : compoundStat.blockItems) {
			map.put(node, ret);
			node.accept(this);
		}
		
	}

	@Override
	public void visit(ASTConditionExpression conditionExpression) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ASTExpression expression) throws Exception {
		if(expression instanceof ASTArrayAccess) {
			visit((ASTArrayAccess)expression);
		}else if(expression instanceof ASTBinaryExpression) {
			visit((ASTBinaryExpression)expression);
		}else if(expression instanceof ASTCastExpression) {
			visit((ASTCastExpression)expression);
		}else if(expression instanceof ASTCharConstant) {
			visit((ASTCharConstant)expression);
		}else if(expression instanceof ASTConditionExpression) {
			visit((ASTConditionExpression)expression);
		}else if(expression instanceof ASTFloatConstant) {
			visit((ASTFloatConstant)expression);
		}else if(expression instanceof ASTFunctionCall) {
			visit((ASTFunctionCall)expression);
		}else if(expression instanceof ASTIdentifier) {
			visit((ASTIdentifier)expression);
		}else if(expression instanceof ASTIntegerConstant) {
			visit((ASTIntegerConstant)expression);
		}else if(expression instanceof ASTMemberAccess) {
			visit((ASTMemberAccess)expression);
		}else if(expression instanceof ASTPostfixExpression) {
			visit((ASTPostfixExpression)expression);
		}else if(expression instanceof ASTStringConstant) {
			visit((ASTStringConstant)expression);
		}else if(expression instanceof ASTUnaryExpression) {
			visit((ASTUnaryExpression)expression);
		}else if(expression instanceof ASTUnaryTypename){
			visit((ASTUnaryTypename)expression);
		}
	}

	@Override
	public void visit(ASTExpressionStatement expressionStat) throws Exception {
		for (ASTExpression node : expressionStat.exprs) {
			visit((ASTExpression)node);
		}
	}

	@Override
	public void visit(ASTFloatConstant floatConst) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ASTFunctionCall funcCall) throws Exception {
		// TODO Auto-generated method stub
		ASTExpression func = funcCall.funcname;
		for (ASTExpression arg : funcCall.argList) {
			Quat quat = new Quat("param", arg, null, null);
			quats.add(quat);
			
		}

		ASTNode res = new TemporaryValue(++tmpId);
		Quat quat1 = new Quat("call", res, func, null);
		quats.add(quat1);
		map.put(funcCall, res);
	}

	@Override
	public void visit(ASTGotoStatement gotoStat) throws Exception {
		// TODO Auto-generated method stub
		String lbname = gotoStat.label.value;
		TemporaryLabel lb = null;
		if(labellist.containsKey(lbname) == true) {
			lb = labellist.get(lbname);
		}
		else {
			lb = new TemporaryLabel(++tmpLb); 
			labellist.put(lbname, lb);
		}
		Quat quat1 = new Quat("J", lb, null, null);
		quats.add(quat1);
		map.put(gotoStat, lb);
		
	}

	@Override
	public void visit(ASTIdentifier identifier) throws Exception {
		map.put(identifier, identifier);
	}

	@Override
	public void visit(ASTInitList initList) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ASTIntegerConstant intConst) throws Exception {
		map.put(intConst, intConst);
	}

	@Override
	public void visit(ASTIterationDeclaredStatement iterationDeclaredStat) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ASTIterationStatement iterationStat) throws Exception {
		// TODO Auto-generated method stub
		//initial
		initeration = 1;
		ASTNode tmpv = null;
        for (ASTExpression exp1 : iterationStat.init ) {
            exp1.accept(this);
        }
		//condition
		TemporaryLabel jidx = new TemporaryLabel(++tmpLb);
		labellist.put(jidx.simplename(), jidx);
		
		jidx.idx = quats.size()+1;
		for (ASTExpression exp2 : iterationStat.cond ) {
            exp2.accept(this);
			tmpv = map.get(exp2);
        }

		TemporaryLabel jfidx = new TemporaryLabel(++tmpLb);
		labellist.put(jfidx.simplename(), jfidx);
        Quat quat1 = new Quat("JF", jfidx, tmpv, null);
	//	System.out.println(tmpLb);
		quats.add(quat1);
		map.put(iterationStat, jfidx);

		//stat
		ASTStatement itstat = iterationStat.stat;
		map.put(itstat, jfidx);
		itstat.accept(this);

		//step
		for (ASTExpression exp3 : iterationStat.step ) {
            exp3.accept(this);
        }
		Quat quat2 = new Quat("J", jidx, null, null);
		quats.add(quat2);

		jfidx.idx = quats.size()+1;
		initeration = 0;
		
	}

	@Override
	public void visit(ASTLabeledStatement labeledStat) throws Exception {
		// TODO Auto-generated method stub
		TemporaryLabel lb = null;
		String lbname = labeledStat.label.value;

		if (labellist.containsKey(lbname) == true) {
			lb = labellist.get(lbname);
			lb.idx = quats.size()+1;
		}
		else {
			lb = new TemporaryLabel(++tmpLb);
			lb.idx = quats.size()+1;
			labellist.put(lbname, lb);
		}
		
		ASTStatement lbstat = labeledStat.stat;
		lbstat.accept(this);
		
	}

	@Override
	public void visit(ASTMemberAccess memberAccess) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ASTPostfixExpression postfixExpression) throws Exception {
		// TODO Auto-generated method stub
		String op = postfixExpression.op.value;
		String qop = null;
		ASTNode res = null;
		ASTNode opnd1 = null;
		ASTNode opnd2 = null;
		ASTIntegerConstant cst = new ASTIntegerConstant();

		if(op.equals("++")) {
			qop = "+";
			visit(postfixExpression.expr);
			res = map.get(postfixExpression.expr);
			
			if (postfixExpression.expr instanceof ASTIdentifier) {
				opnd1 = postfixExpression.expr;
			}			
			cst.value = 1;
			opnd2 = cst;
		}
		else if(op.equals("--")) {
			qop = "-";
			visit(postfixExpression.expr);
			res = map.get(postfixExpression.expr);
			
			if (postfixExpression.expr instanceof ASTIdentifier) {
				opnd1 = postfixExpression.expr;
			}			
			cst.value = 1;
			opnd2 = cst;
		}

		// build quat
		Quat quat = new Quat(qop, res, opnd1, opnd2);
		quats.add(quat);
		map.put(postfixExpression, res);
		
	}

	@Override
	public void visit(ASTReturnStatement returnStat) throws Exception {
		// TODO Auto-generated method stub
		for (ASTExpression exp : returnStat.expr) {
			Quat quat = new Quat("ret", exp, null, null);
			quats.add(quat);
			map.put(returnStat, exp);
		}
		
	}

	@Override
	public void visit(ASTSelectionStatement selectionStat) throws Exception {
		// TODO Auto-generated method stub
        //condition
		ASTNode tmpv = null;
        for (ASTExpression exp : selectionStat.cond ) {
            exp.accept(this);
            tmpv = map.get(exp);
        }

        ASTNode opnd2 = null;
        TemporaryLabel jfidx = new TemporaryLabel(++tmpLb);
	//	System.out.println(tmpLb);
	//	System.out.println(jfidx.simplename());
	//	System.out.println(labellist.size());
		labellist.put(jfidx.simplename(), jfidx);
		
        Quat quat1 = new Quat("JF", jfidx, tmpv, opnd2);
		quats.add(quat1);
	//	System.out.println(tmpLb);
		
        //condition==true
        ASTStatement statthen = selectionStat.then;
        statthen.accept(this);
        
   /*     TemporaryLabel retlb = new TemporaryLabel(0);
		ASTNode tmplb = map.get(selectionStat);
		if (tmplb != null) {
			retlb.idx = ((TemporaryLabel)tmplb).idx;
			retlb.id = ((TemporaryLabel)tmplb).id;
		} */
        
		ASTNode retlb = map.get(selectionStat);
		
	//	Quat quat5 = new Quat("sele", retlb, null, null);
	//	quats.add(quat5);
		
		TemporaryLabel jidx = new TemporaryLabel(++tmpLb);
		labellist.put(jidx.simplename(), jidx);
	//	System.out.println(tmpLb-1);
		if (hasbreak != 0) 
		{
			Quat quat2 = new Quat("J", retlb, null, null);
			quats.add(quat2);
			hasbreak = 0;
		//	labellist.put(retlb.simplename(), retlb);
		}
		else
		{
			Quat quat2 = new Quat("J", jidx, null, null);
			quats.add(quat2);
		}	

        //else
        jfidx.idx = quats.size()+1;       
        ASTStatement statelse = selectionStat.otherwise;
		if (statelse != null){
			statelse.accept(this);
		}

        //end
        jidx.idx = quats.size()+1;
		
	}

	@Override
	public void visit(ASTStringConstant stringConst) throws Exception {
		// TODO Auto-generated method stub
		map.put(stringConst, stringConst);
		
	}

	@Override
	public void visit(ASTTypename typename) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ASTUnaryExpression unaryExpression) throws Exception {
		// TODO Auto-generated method stub
		String op = unaryExpression.op.value;
		ASTNode res = null;
		ASTNode opnd1 = null;
		visit(unaryExpression.expr);
		res = new TemporaryValue(++tmpId);
		opnd1 = map.get(unaryExpression.expr);

		Quat quat = new Quat(op, res, opnd1, null);
		quats.add(quat);
		map.put(unaryExpression, res);
		
	}

	@Override
	public void visit(ASTUnaryTypename unaryTypename) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ASTFunctionDefine functionDefine) throws Exception {
		ASTDeclarator funcdec = functionDefine.declarator;
		funcdec.accept(this);
		visit(functionDefine.body);

		ASTNode func = map.get(funcdec);
		Quat quat = new Quat("leave", func, null, null);
		quats.add(quat);
		map.put(functionDefine, func);
	}

	@Override
	public void visit(ASTDeclarator declarator) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ASTStatement statement) throws Exception {
		if(statement instanceof ASTIterationDeclaredStatement) {
			visit((ASTIterationDeclaredStatement)statement);
		}else if(statement instanceof ASTIterationStatement) {
			visit((ASTIterationStatement)statement);
		}else if(statement instanceof ASTCompoundStatement) {
			visit((ASTCompoundStatement)statement);
		}else if(statement instanceof ASTSelectionStatement) {
			visit((ASTSelectionStatement)statement);
		}else if(statement instanceof ASTExpressionStatement) {
			visit((ASTExpressionStatement)statement);
		}else if(statement instanceof ASTBreakStatement) {
			visit((ASTBreakStatement)statement);
		}else if(statement instanceof ASTContinueStatement) {
			visit((ASTContinueStatement)statement);
		}else if(statement instanceof ASTReturnStatement) {
			visit((ASTReturnStatement)statement);
		}else if(statement instanceof ASTGotoStatement) {
			visit((ASTGotoStatement)statement);
		}else if(statement instanceof ASTLabeledStatement) {
			visit((ASTLabeledStatement)statement);
		}
	}

	@Override
	public void visit(ASTToken token) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
