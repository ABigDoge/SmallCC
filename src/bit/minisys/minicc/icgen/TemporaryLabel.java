package bit.minisys.minicc.icgen;

import bit.minisys.minicc.parser.ast.ASTNode;
import bit.minisys.minicc.parser.ast.ASTVisitor;

public class TemporaryLabel extends ASTNode{

	public Integer id;
	public Integer idx;

	public String name() {
		return "L"+id+":"+idx;
	}
	public String simplename() {
		return "L"+id;
	}
	@Override
	public void accept(ASTVisitor visitor) throws Exception {

	}
	
	public TemporaryLabel(Integer id) {
		super("TemporaryValue");
		this.id = id;
	}
	public TemporaryLabel(String type) {
		super(type);
	}
}
