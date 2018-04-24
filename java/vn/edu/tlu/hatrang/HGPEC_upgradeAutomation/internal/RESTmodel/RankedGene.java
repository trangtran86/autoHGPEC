package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel;

public class RankedGene extends GeneFilter{
	public int rank;
	public String type;
	public boolean isSeed;
	public boolean isTest;
	public String score;
	public RankedGene() {
		super();
		// TODO Auto-generated constructor stub
	}
	public RankedGene(String name, String entrezID, String officialSymbol, String alterSymbol, int rank, String type,
			boolean isSeed, boolean isTest, String score) {
		super(name, entrezID, officialSymbol, alterSymbol);
		this.rank = rank;
		this.type = type;
		this.isSeed = isSeed;
		this.isTest = isTest;
		this.score = score;
	}
	
	
	
}
