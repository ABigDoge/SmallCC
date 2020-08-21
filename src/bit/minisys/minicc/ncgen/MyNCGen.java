package bit.minisys.minicc.ncgen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import bit.minisys.minicc.MiniCCCfg;
import bit.minisys.minicc.icgen.internal.IRBuilder;
import bit.minisys.minicc.icgen.internal.MiniCCICGen;
import bit.minisys.minicc.internal.util.MiniCCUtil;
import bit.minisys.minicc.ncgen.IMiniCCCodeGen;

import bit.minisys.minicc.icgen.*;
import bit.minisys.minicc.semantic.*;
import bit.minisys.minicc.parser.ast.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;


public class MyNCGen implements IMiniCCCodeGen{

	StringBuilder sb;
	Map<Integer, String> lblist;
	List<Quat> iccode;
	List<String> strlist;
	Map<String, VarDescriptor> vdesc;
	Map<Integer, RegDescriptor> rdesc;
	Stack<Map<String, SymbolProperty>> ststack;
	Map<String, SymbolProperty> nowst;
	Map<String, Integer> funcdetail;
	int rownum = 1;
	int strnum = 1;
	int stacksize = 0;
	int argnum = 0;
	String paralist = null;

	public MyNCGen() {
		sb = new StringBuilder();
		lblist = new HashMap<Integer, String>();
		vdesc = new HashMap<String, VarDescriptor>();
		rdesc = new HashMap<Integer, RegDescriptor>();
		for(String name : MyICBuilder.getlblist().keySet()) {
			int tmprow = MyICBuilder.getlblist().get(name).idx;
			lblist.put(tmprow, name);
		}
		iccode = MyICBuilder.getQuats();
		strlist = MyVisitor.getstr();
		funcdetail = MyICBuilder.getfuncinfo();
		ststack = MyVisitor.getstack();

	}
	
	@Override
	public String run(String iFile, MiniCCCfg cfg) throws Exception {
		String oFile = MiniCCUtil.remove2Ext(iFile) + MiniCCCfg.MINICC_CODEGEN_OUTPUT_EXT;
		
		int lblistsize = lblist.size();
		for (Integer rown: lblist.keySet()) {
			if (lblist.get(rown).equals("L"+(lblistsize+1))) {
				String updates = "L" + lblistsize;
				lblist.put(rown, updates);
			}

		}
		
		//rdesc and vdesc initial
		for (int i = 0; i < 32; i++) {
			RegDescriptor rd = new RegDescriptor(i, 1);
			rdesc.put(i, rd);
		}
		
		for (Map<String, SymbolProperty> mapnow : ststack) {
			nowst = mapnow;
		}
		for (String vname : nowst.keySet()) {
			VarDescriptor vd = new VarDescriptor(vname, 0, 0, 0);
			vdesc.put(vname, vd);
		//	System.out.println(vname);
		}
	
		//.data
		nowst = null;
		sb.append(".data\nblank : .asciiz \" \"\n");
		for (String strnow : strlist) {
			sb.append("_" + strnum + "str : .asciiz " + strnow + "\n");
		//	strnum++;
		//	System.out.println(strnow);
		}

		//initial, mars相关内置函数
		sb.append(".text\n__init:\n	lui $sp, 0x8000\n	addi $sp, $sp, 0x0000\n	move $fp, $sp" +
					"\n	add $gp, $gp, 0x8000\n	jal main\n	li $v0, 10\n	syscall" +
					"\nMars_PrintInt:\n	li $v0, 1\n	syscall\n	li $v0, 4\n	move $v1, $a0" +
					"\n	la $a0, blank\n	syscall\n	move $a0, $v1\n	jr $ra" +
					"\nMars_GetInt:\n	li $v0, 5\n	syscall\n	jr $ra" +
					"\nMars_PrintStr:\n	li $v0, 4\n	syscall\n	jr $ra\n");
		
		//quats
		for (Quat quat : iccode){

			//is it a label
			if (lblist.containsKey(rownum)) {
				String segname = lblist.get(rownum);
				sb.append(segname + ":\n");
			}
			rownum++;

			//quat op
			if (quat.getOp().equals("enter")) {
				int stacksize =  32;
				String funcname = astStr(quat.getRes());
				sb.append(funcname + ":\n");
				sb.append("	subu $sp, $sp, " + stacksize + "\n");
				argnum = funcdetail.get(funcname);
				if (argnum != 0) {
					int regnum = getreg();
					String argname = "arg";
					VarDescriptor vd = new VarDescriptor(argname, regnum, 0, 1);
					vdesc.put(argname, vd);
					sb.append("	move $" + regnum + ", $4\n");
				}
			}
			else if (quat.getOp().equals("ret")) {
				ASTNode ret = quat.getRes();
				if( ret instanceof ASTIntegerConstant) {
					int retreg = getreg();
					sb.append("	li $" + retreg + ", " + astStr(ret) + "\n");
					sb.append("	move $2, $" + retreg + "\n");
					
				}
				else if(ret instanceof ASTIdentifier) {
					String vname = ((ASTIdentifier)ret).value;
					int ireg = vdesc.get(vname).regnum;
					sb.append("	move $2, $" + ireg + "\n");
				}
			//	int regnum = vdesc.get(key)
			}
			else if (quat.getOp().equals("leave") ) {
				sb.append("	move $sp, $fp\n	jr $31\n");
			}

			else if (quat.getOp().equals("=")) {
				ASTNode res = quat.getRes();
				ASTNode op1 = quat.getOpnd1();
				String vname = ((ASTIdentifier)res).value;
				int treg;
				if (vdesc.get(vname).regnum != 0) {
					treg = vdesc.get(vname).regnum;
				}
				else {
					treg = getreg();
					vdesc.get(vname).regnum = treg;
					vdesc.get(vname).modyfied = 1;
					rdesc.get(treg).empty = 0;
				}
				if ( op1 instanceof ASTIntegerConstant ) {
					sb.append("	li $25" + ", " + astStr(op1) + "\n");
					sb.append("	move $" + treg + ", $25" + "\n");
				}
				else if (op1 instanceof TemporaryValue) {
					String op1name = astStr(op1);
					int srcreg = vdesc.get(op1name).regnum;
				
					sb.append("	move $" + treg + ", $" + srcreg + "\n");
					rdesc.get(srcreg).empty = 1;
				}
			}

			else if(quat.getOp().equals("~")) {
				ASTNode res = quat.getRes();
				ASTNode op1 = quat.getOpnd1();
				String vname = astStr(op1);
				String tvname = astStr(res);
				int srcreg = vdesc.get(vname).regnum;
				if (res instanceof TemporaryValue) {
					int treg = getreg();
					VarDescriptor vd = new VarDescriptor(tvname, treg, 0, 1);
					vdesc.put(tvname, vd);
					rdesc.get(treg).empty = 0;
					sb.append("	xori $" + treg + ", $" + srcreg + ", 0xffffffff\n");
				}
			}

			else if (quat.getOp().equals("+") || quat.getOp().equals("-") 
				|| quat.getOp().equals("*") || quat.getOp().equals("/")) {
				ASTNode res = quat.getRes();
				ASTNode op1 = quat.getOpnd1();
				ASTNode op2 = quat.getOpnd2();
				String resname = astStr(res);
				String op1name = astStr(op1);
				String op2name = astStr(op2);
				int op1reg = vdesc.get(op1name).regnum;
				int op2reg;
				int resreg;
				if (op2 instanceof ASTIntegerConstant) {
					op2reg = 25;
					sb.append("	li $25" + ", " + op2name + "\n");
				}
				else {
					op2reg = vdesc.get(op2name).regnum;
				}
				String opt = null;
				if (quat.getOp().equals("+")) {
					opt = "add";
				}
				else if (quat.getOp().equals("-")) {
					opt = "sub";
				}
				else if (quat.getOp().equals("*")) {
					opt = "mul";
				}
				if (vdesc.containsKey(resname)) {
					resreg = vdesc.get(resname).regnum;
				}
				else {
					resreg = getreg();
					VarDescriptor vd = new VarDescriptor(resname, resreg, 0, 1);
					vdesc.put(resname, vd);
					rdesc.get(resreg).empty = 0;
				}
				sb.append("	" + opt + " $25, $" + op1reg + ", $" + op2reg + "\n");
				sb.append("	move $" + resreg +", $25\n");

				if (op1 instanceof TemporaryValue) {
					rdesc.get(op1reg).empty = 1;
				}
				if (op2 instanceof TemporaryValue) {
					rdesc.get(op2reg).empty = 1;
				}
			}

			else if (quat.getOp().equals("param")) {
				ASTNode para = quat.getRes();
				paralist = astStr(para);
				int parareg=0;
				if (para instanceof ASTIdentifier)	{
					parareg = vdesc.get(paralist).regnum;
				}
				else if (para instanceof ASTStringConstant) {
					parareg = getreg();
					sb.append("	la $" + parareg + ", _" + strnum + "str\n");
				}
				sb.append("	subu $sp, $sp, 4\n	sw $fp, ($sp)\n	move $fp, $sp\n" +
					"	sw $31, 20($sp)\n	move $4, $" + parareg + "\n");			
			}

			else if (quat.getOp().equals("call")) {
				ASTNode res = quat.getRes();
				ASTNode func = quat.getOpnd1();
				String resname = astStr(res);
				String funcname = astStr(func);
				int resreg = getreg();
				rdesc.get(resreg).empty = 0;
				VarDescriptor vd = new VarDescriptor(resname, resreg, 0, 1);
				vdesc.put(resname, vd);
				sb.append("	jal " + funcname + "\n	lw $31, 20($sp)\n" +
					"	lw $fp, ($sp)\n" + "	addu $sp, $sp, 4\n" + 
					"	move $" + resreg + ", $2\n");				
			}

			else if (quat.getOp().equals(">") || quat.getOp().equals("<")) {
				ASTNode res = quat.getRes();
				ASTNode op1 = quat.getOpnd1();
				ASTNode op2 = quat.getOpnd2();
				String resname = astStr(res);
				String op1name = astStr(op1);
				String op2name = astStr(op2);
				String opt = null;
				int resreg;
				int op1reg = vdesc.get(op1name).regnum;
				int op2reg = 0;				

				if (quat.getOp().equals(">")) {
					opt = "sgt";
				}
				else if (quat.getOp().equals("<")) {
					opt = "slt";
				}

				resreg = getreg();
				VarDescriptor vd = new VarDescriptor(resname, resreg, 0, 1);
				vdesc.put(resname, vd);
				rdesc.get(resreg).empty = 0;

				if (op2 instanceof ASTIdentifier ) {
					op2reg = vdesc.get(op2name).regnum;
				}
				else if (op2 instanceof ASTIntegerConstant) {
					op2reg = 25;
					sb.append("	li $25" + ", " + op2name + "\n");
				}

				sb.append( "	" + opt + " $" + resreg + ", $" + op1reg +
						", $" + op2reg + "\n");
			}

			else if(quat.getOp().equals("JF")) {
				ASTNode res = quat.getRes();
				ASTNode op1 = quat.getOpnd1();
				String resname = ((TemporaryLabel)res).simplename();
				String op1name = astStr(op1);
				int op1reg = vdesc.get(op1name).regnum;
				sb.append("	beq $" + op1reg + ", $0, " + resname + "\n");				
			}

			else if (quat.getOp().equals("J")) {
				ASTNode res = quat.getRes();
				String resname = ((TemporaryLabel)res).simplename();
				sb.append("	j " + resname + "\n");
			}

		}
		
	
	/*	if(cfg.target.equals("mips")) {
		
			//TODO:
		}else if (cfg.target.equals("riscv")) {
			//TODO:
		}else if (cfg.target.equals("x86")){
			//TODO:
		}   */

		System.out.println("7. Target code generation finished!");
		print(oFile);
		
		return oFile;
	}
	
	private String astStr(ASTNode node) {
		if (node == null) {
			return "";
		}else if (node instanceof ASTIdentifier) {
			return ((ASTIdentifier)node).value;
		}else if (node instanceof ASTIntegerConstant) {
			return ((ASTIntegerConstant)node).value+"";
		}else if (node instanceof ASTStringConstant) {
			return ((ASTStringConstant)node).value+"";
		}else if (node instanceof TemporaryValue) {
			return ((TemporaryValue)node).name();
		}else if (node instanceof TemporaryLabel) {
			return ((TemporaryLabel)node).name();
		}else {
			return "";
		}
	}

	private int getreg() {
		int hasempty = 0;
		int ret = 0;
		for (int i = 8; i <= 15 ; i++) {
			RegDescriptor rd = rdesc.get(i);
			if(rd.empty == 1) {
				hasempty = i;
				break;
			}
		}
		if (hasempty == 0) {
			for (int i = 24; i <= 25 ; i++) {
				RegDescriptor rd = rdesc.get(i);
				if(rd.empty == 1) {
					hasempty = i;
					break;
				}
			}
		}

		if(hasempty > 0) {
			ret = hasempty;
			rdesc.get(ret).empty = 0;
		}
		else {
			for (String varname : vdesc.keySet()) {
				VarDescriptor vd = vdesc.get(varname);
				if (vd.stored == 1 || vd.modyfied == 0) {
					ret = vd.regnum;
					break;
				}
			}
		}
		
		return ret;
	}

	private void print(String filename) {
		try {
			FileWriter fileWriter = new FileWriter(new File(filename));
			fileWriter.write(sb.toString());
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}