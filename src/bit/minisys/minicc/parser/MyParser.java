package bit.minisys.minicc.parser;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.gui.TreeViewer;

import com.fasterxml.jackson.databind.ObjectMapper;

import bit.minisys.minicc.MiniCCCfg;
import bit.minisys.minicc.internal.util.MiniCCUtil;
import bit.minisys.minicc.parser.ast.*;

/*
PROGRAM     --> FUNC_LIST
FUNC_LIST   --> FUNC FUNC_LIST | e
FUNC        --> TYPE ID '(' ARGUMENTS ')' CODE_BLOCK
TYPE        --> INT | VOID | FLOAT | CHAR
ARGUMENTS   --> e | ARG_LIST
ARG_LIST    --> ARG ',' ARGLIST | ARG
ARG    	    --> TYPE ID
CODE_BLOCK  --> '{' STMTS '}'
STMTS       --> STMT STMTS | DECL_STMT STMTS | e
STMT        --> EXPR_STMT | RETURN_STMT | ITERA_STMT | SELECT_STMT | JUMP_STMT

DECL_STMT   --> TYPE DECL_LIST ';'
DECL_LIST   --> INIT ',' DECL_LIST | INIT
INIT		--> ID '=' CONST

RETURN STMT --> RETURN EXPR ';'
EXPR_STMT   --> EXPR ';'
ITERA_STMT  --> 'for' '(' EXPR ';' EXPR ';' EXPR ')' CODE_BLOCK 
SELECT_STMT --> 'if' '(' EXPR ')' CODE_BLOCK 
JUMP_STMT   --> goto' ID ';' 

EXPR        --> RELATE_EXPR ASSIGN_EXPR 
ASSIGN_EXPR	--> '=' RELATE_EXPR  ASSIGN_EXPR| e

RELATE_EXPR --> SUB_EXPR RELATE_EXPR' 
RELATE_EXPR'--> '>' SUB_EXPR RELATE_EXPR' | '<' SUB_EXPR RELATE_EXPR' | e

SUB_EXPR    --> MUL_EXPR SUB_EXPR'
SUB_EXPR'   --> '+' MUL_EXPR SUB_EXPR' | '-' MUL_EXPR SUB_EXPR' | e

MUL_EXPR    --> UNARY_EXPR MUL_EXPR'
MUL_EXPR'   --> '*' UNARY_EXPR MUL_EXPR' | '/' UNARY_EXPR MUL_EXPR' | e

UNARY_EXPR  --> FACTOR UNARY_EXPR'
UNARY_EXPR' --> '++' | '--' | e

FACTOR      --> ID | CONST | '(' EXPR ')'
CONST       --> CHAR_CONST | INT_CONST | FLOAT_CONST
 */

class ScannerToken{
	public String lexme;
	public String type;
	public int	  line;
	public int    column;
}

public class MyParser implements IMiniCCParser {

	private ArrayList<ScannerToken> tknList;
	private int tokenIndex;
	private ScannerToken nextToken;
	
	@Override
	public String run(String iFile) throws Exception {
		System.out.println("Parsing...");

		String oFile = MiniCCUtil.removeAllExt(iFile) + MiniCCCfg.MINICC_PARSER_OUTPUT_EXT;
		String tFile = MiniCCUtil.removeAllExt(iFile) + MiniCCCfg.MINICC_SCANNER_OUTPUT_EXT;
		
		tknList = loadTokens(tFile);
		tokenIndex = 0;

		ASTNode root = program();
		
		
		String[] dummyStrs = new String[16];
		TreeViewer viewr = new TreeViewer(Arrays.asList(dummyStrs), root);
	    viewr.open();

		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(new File(oFile), root);

		//TODO: write to file
		
		
		return oFile;
	}
	

	private ArrayList<ScannerToken> loadTokens(String tFile) {
		tknList = new ArrayList<ScannerToken>();
		
		ArrayList<String> tknStr = MiniCCUtil.readFile(tFile);
		
		for(String str: tknStr) {
			if(str.trim().length() <= 0) {
				continue;
			}
			
			ScannerToken st = new ScannerToken();
			//[@0,0:2='int',<'int'>,1:0]
			String[] segs;
			if(str.indexOf("<','>") > 0) {
				str = str.replace("','", "'DOT'");
				
				segs = str.split(",");
				segs[1] = "=','";
				segs[2] = "<','>";
				
			}else {
				segs = str.split(",");
			}
			st.lexme = segs[1].substring(segs[1].indexOf("=") + 1);
			st.lexme = st.lexme.substring(1, st.lexme.length() - 1);
			st.type  = segs[2].substring(segs[2].indexOf("<") + 1, segs[2].length() - 1);
			String[] lc = segs[3].split(":");
			st.line = Integer.parseInt(lc[0]);
			st.column = Integer.parseInt(lc[1].replace("]", ""));
			
			tknList.add(st);
		}
		
		return tknList;
	}

	private ScannerToken getToken(int index){
		if (index < tknList.size()){
			return tknList.get(index);
		}
		return null;
	}

	public void matchToken(String type) {
		if(tokenIndex < tknList.size()) {
			ScannerToken next = tknList.get(tokenIndex);
			if(!next.type.equals(type)) {
				System.out.println("[ERROR]Parser: unmatched token, expected = " + type + ", " 
						+ "input = " + next.type);
			}
			else {
				tokenIndex++;
			}
		}
	}

	//PROGRAM --> FUNC_LIST
	public ASTNode program() {
		ASTCompilationUnit p = new ASTCompilationUnit();
		ArrayList<ASTNode> fl = funcList();
		if(fl != null) {
			//p.getSubNodes().add(fl);
			p.items.addAll(fl);
		}
		p.children.addAll(p.items);
		return p;
	}


	//FUNC_LIST --> FUNC FUNC_LIST | e
	public ArrayList<ASTNode> funcList() {
		ArrayList<ASTNode> fl = new ArrayList<ASTNode>();
		
		nextToken = tknList.get(tokenIndex);
		if(nextToken.type.equals("EOF")) {
			return null;
		}
		else {
			ASTNode f = func();
			fl.add(f);
			ArrayList<ASTNode> fl2 = funcList();
			if(fl2 != null) {
				fl.addAll(fl2);
			}
			return fl;
		}
	}

	//FUNC --> TYPE ID '(' ARGUMENTS ')' CODE_BLOCK
	public ASTNode func() {
		ASTFunctionDefine fdef = new ASTFunctionDefine();
		
		ASTToken s = type();
		
		fdef.specifiers.add(s);
		fdef.children.add(s);
		
		ASTFunctionDeclarator fdec = new ASTFunctionDeclarator();

		ScannerToken st = tknList.get(tokenIndex);
		ASTIdentifier id = new ASTIdentifier();
		id.tokenId = tokenIndex;
		id.value = st.lexme;
		matchToken("Identifier");

		ASTVariableDeclarator vd =  new ASTVariableDeclarator();
        vd.identifier = id;
        fdec.declarator = vd;
        fdec.children.add(vd);
		
		matchToken("'('");
		ArrayList<ASTParamsDeclarator> pl = arguments();
		matchToken("')'");
		
		//fdec.identifiers.add(id);
		if(pl != null) {
			fdec.params.addAll(pl);
			fdec.children.addAll(pl);
		}
		
		ASTCompoundStatement cs = codeBlock();

		fdef.declarator = fdec;
		fdef.children.add(fdec);
		fdef.body = cs;
		fdef.children.add(cs);

		
		return fdef;
	}

	//TYPE --> INT |FLOAT | CHART
	public ASTToken type() {
		ScannerToken st = tknList.get(tokenIndex);
		
		ASTToken t = new ASTToken();
		if(st.type.equals("'int'") || st.type.equals("'double'") 
			|| st.type.equals("'char'") || st.type.equals("'void'")) {
		//	System.out.println("type(): " + st.type);
			t.tokenId = tokenIndex;
			t.value = st.lexme;
			tokenIndex++;
		}
		return t;
	}

	//ARGUMENTS --> e | ARG_LIST
	public ArrayList<ASTParamsDeclarator> arguments() {
		nextToken = tknList.get(tokenIndex);
		if(nextToken.type.equals("')'")) { //ending
			return null;
		}
		else {
			ArrayList<ASTParamsDeclarator> al = argList();
			return al;
		}
	}

	//ARG_LIST --> ARGUMENT ',' ARGLIST | ARGUMENT
	public ArrayList<ASTParamsDeclarator> argList() {
		ArrayList<ASTParamsDeclarator> pdl = new ArrayList<ASTParamsDeclarator>();
		ASTParamsDeclarator pd = argument();
		pdl.add(pd);
		
		nextToken = tknList.get(tokenIndex);
		if(nextToken.type.equals("','")) {
			matchToken("','");
			ArrayList<ASTParamsDeclarator> pdl2 = argList();
			pdl.addAll(pdl2);
		}
		
		return pdl;
	}
		
	//ARGUMENT --> TYPE ID
	public ASTParamsDeclarator argument() {
		ASTParamsDeclarator pd = new ASTParamsDeclarator();
		ASTToken t = type();
		pd.specfiers.add(t);
		
		ScannerToken st = tknList.get(tokenIndex);
		ASTIdentifier id = new ASTIdentifier();
		id.tokenId = tokenIndex;
		id.value = st.lexme;
		matchToken("Identifier");
		
		ASTVariableDeclarator vd =  new ASTVariableDeclarator();
		vd.identifier = id;
		pd.declarator = vd;
		
		return pd;
	}

	

	//CODE_BLOCK --> '{' STMTS '}'
	public ASTCompoundStatement codeBlock() {
		matchToken("'{'");
		ASTCompoundStatement cs = stmts();
		matchToken("'}'");

		return cs;
	}

	//STMTS --> STMT STMTS | DECL_STMT STMTS | e
	public ASTCompoundStatement stmts() {
		nextToken = tknList.get(tokenIndex);
		if (nextToken.type.equals("'}'"))
			return null;
		else {
			ASTCompoundStatement cs = new ASTCompoundStatement();
			if (nextToken.type.equals("'int'") || nextToken.type.equals("'double'") 
			|| nextToken.type.equals("'char'") || nextToken.type.equals("'void'")) {
		//		System.out.println("stmts(): " + nextToken.type);
				ASTDeclaration dec = assignstmt();
				cs.blockItems.add(dec);
			}
			
			else {
				ASTStatement s = stmt();
				cs.blockItems.add(s);
			}
		
			ASTCompoundStatement cs2 = stmts();
			
			if(cs2 != null)
				cs.blockItems.addAll(cs2.blockItems);
			return cs;
		}
	}

	//STMT  --> RETURN_STMT | EXPR_STMT | ITERA_STMT | SELECT_STMT | JUMP_STMT 
	public ASTStatement stmt() {
		nextToken = tknList.get(tokenIndex);

		if(nextToken.type.equals("'return'")) {
			return returnStmt();
		}
		else if(nextToken.type.equals("'for'")) {
			return iterastmt();
		}
		else if(nextToken.type.equals("'if'")) {
			return selectstmt();
		}
		else if(nextToken.type.equals("'goto'")) {
			return jumpstmt();
		}
		else if(nextToken.type.equals("Identifier")) {
			return exprstmt();
		}
        else{
			System.out.println("[ERROR]Parser: unreachable stmt!");
			return null;
		}
	}

	//RETURN_STMT --> RETURN EXPR ';'
	public ASTReturnStatement returnStmt() {
		matchToken("'return'");
		ASTReturnStatement rs = new ASTReturnStatement();
		ASTExpression e = expr();
		matchToken("';'");
		rs.expr.add(e);
		return rs;
	}

	//ITERA_STMT  --> 'for' '(' EXPR ';' EXPR ';' EXPR ')' CODE_BLOCK 
	public ASTIterationStatement iterastmt() {
		matchToken("'for'");
		matchToken("'('");
		ASTIterationStatement is = new ASTIterationStatement();
		is.init = new LinkedList<ASTExpression>();
		is.cond = new LinkedList<ASTExpression>();
		is.step = new LinkedList<ASTExpression>();  
		ASTExpression e1 = expr();
		matchToken("';'");
		ASTExpression e2 = expr();
		matchToken("';'");
		ASTExpression e3 = expr();
		matchToken("')'");
		ASTCompoundStatement cs = codeBlock();

		is.init.add(e1);
		is.cond.add(e2);
		is.step.add(e3);
		is.stat = cs;
		return is;
	}

	//SELECT_STMT --> 'if' '(' EXPR ')' CODE_BLOCK
	public ASTSelectionStatement selectstmt() {
		matchToken("'if'");
		matchToken("'('");
		ASTSelectionStatement ss = new ASTSelectionStatement();
		ss.cond = new LinkedList<ASTExpression>();
		ASTExpression e1 = expr();
		matchToken("')'");
		ASTCompoundStatement cs = codeBlock();

		ss.cond.add(e1);
		ss.then = cs;
		return ss;
	}

	//JUMP_STMT   --> goto' ID ';' 
	public ASTGotoStatement jumpstmt() {
		matchToken("'goto'");
		ASTGotoStatement js = new ASTGotoStatement();
		ScannerToken st = tknList.get(tokenIndex);
		ASTIdentifier id = new ASTIdentifier();
        id.tokenId = tokenIndex;
		id.value = st.lexme;
		js.label = id;
		matchToken("Identifier");
		matchToken("';'");
		return js;
	}

	//EXPR_STMT   --> EXPR ';'
	public ASTExpressionStatement exprstmt() {
		ASTExpressionStatement es = new ASTExpressionStatement();
		es.exprs = new ArrayList<ASTExpression>();
		ASTExpression e1 = expr();
		matchToken("';'");
		es.exprs.add(e1);
		return es;
	}

    //DECL_STMT   --> TYPE DECL_LIST ';'
    public ASTDeclaration assignstmt() {
		ASTDeclaration dec = new ASTDeclaration();
		dec.specifiers = new ArrayList<ASTToken>();
		dec.initLists = new ArrayList<ASTInitList>();
		ASTToken s = new ASTToken();
		s = type();
	//	System.out.println("assignstmt(): " + s.value);
	//	System.out.println("assignstmt(): " + dec.specifiers.size());

		dec.specifiers.add(s);
		dec.children.add(s);

        ArrayList<ASTInitList> il = decllist();
        if(il != null) {
            dec.initLists = il;
            dec.children.addAll(il);
        }

		matchToken("';'");
		return dec;
    }
	

    //DECL_LIST   --> INIT ',' DECL_LIST | INIT
    public ArrayList<ASTInitList> decllist() {
        ArrayList<ASTInitList> il1 = new ArrayList<ASTInitList>();
        ASTInitList ini = init();
        il1.add(ini);

        nextToken = tknList.get(tokenIndex);
		if(nextToken.type.equals("','")) {
			matchToken("','");
			ArrayList<ASTInitList> il2 = decllist();
			il1.addAll(il2);
		}

        return il1;
    }

    //INIT		--> ID '=' CONST
    public ASTInitList init() {
        ASTInitList ini = new ASTInitList();
        ini.exprs = new ArrayList<ASTExpression>();

		ScannerToken st = tknList.get(tokenIndex);
        ASTIdentifier id = new ASTIdentifier();
        id.tokenId = tokenIndex;
		id.value = st.lexme;
		matchToken("Identifier");

        ASTVariableDeclarator vd =  new ASTVariableDeclarator();
        vd.identifier = id;
		ini.declarator = vd;

        matchToken("'='");

		st = tknList.get(tokenIndex);
		if(st.type.equals("IntegerConstant")) {
			ASTIntegerConstant ic =  new ASTIntegerConstant();
			ic.tokenId = tokenIndex;
			ic.value = Integer.parseInt(st.lexme);
			matchToken("IntegerConstant");
			ini.exprs.add(ic);
		}
        else if(st.type.equals("FloatConstant")) {
			ASTFloatConstant ic =  new ASTFloatConstant();
			ic.tokenId = tokenIndex;
			ic.value = Double.parseDouble(st.lexme);
			matchToken("FloatConstant");
			ini.exprs.add(ic);
		}
		else if(st.type.equals("CharConstant")) {
			ASTCharConstant ic =  new ASTCharConstant();
			ic.tokenId = tokenIndex;
			ic.value = st.lexme;
			matchToken("CharConstant");
			ini.exprs.add(ic);
		}

        return ini;
    }

	/*
	EXPR        --> RELATE_EXPR ASSIGN_EXPR 
	ASSIGN_EXPR	--> '=' RELATE_EXPR  ASSIGN_EXPR| e

	RELATE_EXPR   --> SUB_EXPR RELATE_EXPR' 
	RELATE_EXPR'	--> '>' SUB_EXPR RELATE_EXPR' | '<' SUB_EXPR RELATE_EXPR' | e

	SUB_EXPR        --> MUL_EXPR SUB_EXPR'
	SUB_EXPR'       --> '+' MUL_EXPR SUB_EXPR' | '-' MUL_EXPR SUB_EXPR' | e

	MUL_EXPR        --> UNARY_EXPR MUL_EXPR'
	MUL_EXPR'       --> '*' UNARY_EXPR MUL_EXPR' | '/' UNARY_EXPR MUL_EXPR' | e

	UNARY_EXPR    --> FACTOR UNARY_EXPR'
	UNARY_EXPR'   --> '++' | '--' | e
	*/

	//EXPR        --> RELATE_EXPR ASSIGN_EXPR 
	public ASTExpression expr() {
		ASTExpression re = relateexpr();
		ASTBinaryExpression be = assignexpr();

		if( be != null) {
			be.expr1 = re;
			return be;
		}
		else{
			return re;
		}
	}

	//ASSIGN_EXPR	--> '=' RELATE_EXPR ASSIGN_EXPR | e
	public ASTBinaryExpression assignexpr() {
		nextToken = tknList.get(tokenIndex);
		if (nextToken.type.equals("';'"))
			return null;
		if(nextToken.type.equals("'='")){
			ASTBinaryExpression be = new ASTBinaryExpression();
			
			ASTToken tkn = new ASTToken();
			tkn.tokenId = tokenIndex;
			tkn.value = nextToken.lexme;
			matchToken("'='");
			
			be.op = tkn;
			be.expr2 = relateexpr();
			
			ASTBinaryExpression expr = assignexpr();
			if(expr != null) {
				expr.expr1 = be;
				return expr;
			}
			
			return be;
		}else {
			return null;
		}
	}

	//RELATE_EXPR   --> SUB_EXPR RELATE_EXPR'
	public ASTExpression relateexpr() {
		ASTExpression se = subexpr();
		ASTBinaryExpression be = relateexpr2();

		if( be != null) {
			be.expr1 = se;
			return be;
		}
		else{
			return se;
		}
	}

	//RELATE_EXPR'	--> '>' SUB_EXPR RELATE_EXPR' | '<' SUB_EXPR RELATE_EXPR' | e
	public ASTBinaryExpression relateexpr2() {
		nextToken = tknList.get(tokenIndex);
		if(nextToken.type.equals("'>'")){
			ASTBinaryExpression be = new ASTBinaryExpression();
			
			ASTToken tkn = new ASTToken();
			tkn.tokenId = tokenIndex;
			tkn.value = nextToken.lexme;
			matchToken("'>'");
			
			be.op = tkn;
			be.expr2 = subexpr();
			
			ASTBinaryExpression expr = relateexpr2();
			if(expr != null) {
				expr.expr1 = be;
				return expr;
			}
			
			return be;
		}
		else if(nextToken.type.equals("'<'")) {
			ASTBinaryExpression be = new ASTBinaryExpression();
			
			ASTToken tkn = new ASTToken();
			tkn.tokenId = tokenIndex;
			tkn.value = nextToken.lexme;
			matchToken("'<'");
			
			be.op = tkn;
			be.expr2 = subexpr();
			
			ASTBinaryExpression expr = relateexpr2();
			if(expr != null) {
				expr.expr1 = be;
				return expr;
			}
			
			return be;
		}
		else {
			return null;
		}
	}

	//SUB_EXPR        --> MUL_EXPR SUB_EXPR'
	public ASTExpression subexpr() {
		ASTExpression se = mulexpr();
		ASTBinaryExpression be = subexpr2();

		if( be != null) {
			be.expr1 = se;
			return be;
		}
		else{
			return se;
		}
	}

	//SUB_EXPR'       --> '+' MUL_EXPR SUB_EXPR' | '-' MUL_EXPR SUB_EXPR' | e
	public ASTBinaryExpression subexpr2() {
		nextToken = tknList.get(tokenIndex);
		if(nextToken.type.equals("'+'")){
			ASTBinaryExpression be = new ASTBinaryExpression();
			
			ASTToken tkn = new ASTToken();
			tkn.tokenId = tokenIndex;
			tkn.value = nextToken.lexme;
			matchToken("'+'");
			
			be.op = tkn;
			be.expr2 = mulexpr();
			
			ASTBinaryExpression expr = subexpr2();
			if(expr != null) {
				expr.expr1 = be;
				return expr;
			}
			
			return be;
		}
		else if(nextToken.type.equals("'-'")) {
			ASTBinaryExpression be = new ASTBinaryExpression();
			
			ASTToken tkn = new ASTToken();
			tkn.tokenId = tokenIndex;
			tkn.value = nextToken.lexme;
			matchToken("'-'");
			
			be.op = tkn;
			be.expr2 = mulexpr();
			
			ASTBinaryExpression expr = subexpr2();
			if(expr != null) {
				expr.expr1 = be;
				return expr;
			}
			
			return be;
		}
		else {
			return null;
		}
	}

	//MUL_EXPR        --> UNARY_EXPR MUL_EXPR'
	public ASTExpression mulexpr() {
		ASTExpression se = unaryexpr();
		ASTBinaryExpression be = mulexpr2();

		if( be != null) {
			be.expr1 = se;
			return be;
		}
		else{
			return se;
		}
	}

	//MUL_EXPR'       --> '*' UNARY_EXPR MUL_EXPR' | '/' UNARY_EXPR MUL_EXPR' | e
	public ASTBinaryExpression mulexpr2() {
		nextToken = tknList.get(tokenIndex);
		if(nextToken.type.equals("'*'")){
			ASTBinaryExpression be = new ASTBinaryExpression();
			
			ASTToken tkn = new ASTToken();
			tkn.tokenId = tokenIndex;
			tkn.value = nextToken.lexme;
			matchToken("'*'");
			
			be.op = tkn;
			be.expr2 = unaryexpr();
			
			ASTBinaryExpression expr = mulexpr2();
			if(expr != null) {
				expr.expr1 = be;
				return expr;
			}
			
			return be;
		}
		else if(nextToken.type.equals("'/'")) {
			ASTBinaryExpression be = new ASTBinaryExpression();
			
			ASTToken tkn = new ASTToken();
			tkn.tokenId = tokenIndex;
			tkn.value = nextToken.lexme;
			matchToken("'/'");
			
			be.op = tkn;
			be.expr2 = unaryexpr();
			
			ASTBinaryExpression expr = mulexpr2();
			if(expr != null) {
				expr.expr1 = be;
				return expr;
			}
			
			return be;
		}
		else {
			return null;
		}
	}

	//UNARY_EXPR    --> FACTOR UNARY_EXPR'
	public ASTExpression unaryexpr() {
		ASTExpression f = factor();
		ASTPostfixExpression pe = unaryexpr2();

		if(pe != null) {
			pe.expr = f;
			return pe;
		}else {
			return f;
		}
	}

	//UNARY_EXPR'   --> '++' | '--' | e
	public ASTPostfixExpression unaryexpr2() {
		nextToken = tknList.get(tokenIndex);
		ASTPostfixExpression pe = new ASTPostfixExpression();
		if (nextToken.type.equals("';'"))
			return null;
		else if(nextToken.type.equals("'++'")) {
			ASTToken tkn = new ASTToken();
			tkn.tokenId = tokenIndex;
			tkn.value = nextToken.lexme;
			pe.op = tkn;
			matchToken("'++'");
			return pe;
		}
		else if(nextToken.type.equals("'--'")) {
			ASTToken tkn = new ASTToken();
			tkn.tokenId = tokenIndex;
			tkn.value = nextToken.lexme;
			pe.op = tkn;
			matchToken("'--'");
			return pe;
		}
		else{
			return null;
		}
	}

	//FACTOR --> '(' EXPR ')' | ID | CONST 
	public ASTExpression factor() {
		nextToken = tknList.get(tokenIndex);
		if(nextToken.type.equals("Identifier")) {
			ASTIdentifier id = new ASTIdentifier();
			id.tokenId = tokenIndex;
			id.value = nextToken.lexme;
			matchToken("Identifier");
			return id;
		}
		else if(nextToken.type.equals("IntegerConstant")) {
			ASTIntegerConstant ic = new ASTIntegerConstant();
			ic.tokenId = tokenIndex;
			ic.value = Integer.parseInt(nextToken.lexme);
			matchToken("IntegerConstant");
			return ic;
		}
		else if(nextToken.type.equals("FloatConstant")) {
			ASTFloatConstant fc = new ASTFloatConstant();
			fc.tokenId = tokenIndex;
			fc.value = Double.parseDouble(nextToken.lexme);
			matchToken("FloatConstant");
			return fc;
		}
		else if(nextToken.type.equals("CharConstant")) {
			ASTCharConstant fc = new ASTCharConstant();
			fc.tokenId = tokenIndex;
			fc.value = nextToken.lexme;
			matchToken("CharConstant");
			return fc;
		}
		else if(nextToken.type.equals("'('")) {
			matchToken("'('");
			ASTExpression e1 = expr();
			matchToken("')'");
			return e1;
		}
		else
		{
			return null;
		}
	}

}
