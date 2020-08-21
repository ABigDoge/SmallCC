package bit.minisys.minicc.semantic;

public class SymbolProperty {
	public String vartype;
	public String datatype;
	public String scope;
	
	public SymbolProperty() {

	}
	
	public SymbolProperty(String vartype, String datatype, String scope) {
		this.vartype = vartype;
		this.datatype = datatype;
		this.scope = scope;
	}

}
