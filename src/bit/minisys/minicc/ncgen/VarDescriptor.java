package bit.minisys.minicc.ncgen;

public class VarDescriptor {
	public String varname;	
	public int regnum;
	public int stored;
	public int modyfied;
	public VarDescriptor(String varname, int regnum, int stored, int modyfied) {
		this.varname = varname;
		this.regnum = regnum;
		this.stored = stored;
		this.modyfied = modyfied;
		
	}
	
}