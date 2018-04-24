package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel;

public class GeneFilter {
	public String name;
	public String EntrezID;
	public String OfficialSymbol;
	public String AlterSymbol;
	public GeneFilter(String name, String entrezID, String officialSymbol, String alterSymbol) {
		super();
		this.name = name;
		EntrezID = entrezID;
		OfficialSymbol = officialSymbol;
		AlterSymbol = alterSymbol;
	}
	public GeneFilter() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
