package bit.minisys.minicc.semantic;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.antlr.v4.gui.TreeViewer;
import org.python.antlr.PythonParser.return_stmt_return;

import com.fasterxml.jackson.databind.ObjectMapper;

import bit.minisys.minicc.MiniCCCfg;
import bit.minisys.minicc.internal.util.MiniCCUtil;
import bit.minisys.minicc.parser.ast.*;

public class MySemantic implements IMiniCCSemantic {

    @Override
	public String run(String iFile) throws Exception {
    //    System.out.println("filename: " + iFile);
        ObjectMapper mapper = new ObjectMapper();
        ASTCompilationUnit program = 
            (ASTCompilationUnit)mapper.readValue(new File(iFile), ASTCompilationUnit.class);
        
  //      MyVisitor v = new MyVisitor();
        ASTVisitor v = new MyVisitor();
        program.accept(v);
        
        System.out.println("4. Semantic finished!");
        return iFile;
    }
}