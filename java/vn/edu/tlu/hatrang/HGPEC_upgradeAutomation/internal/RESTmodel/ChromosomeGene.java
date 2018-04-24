package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel;

public class ChromosomeGene {
	public String EntrezGeneID;
	public String OfficialSymbol;
	public long GeneStart;
	public long GeneEnd;
	public String Band;
	public String chromDistance;
	public ChromosomeGene() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ChromosomeGene(String entrezGeneID, String officialSymbol, long geneStart, long geneEnd, String band,
			String chromDistance) {
		super();
		EntrezGeneID = entrezGeneID;
		OfficialSymbol = officialSymbol;
		GeneStart = geneStart;
		GeneEnd = geneEnd;
		Band = band;
		this.chromDistance = chromDistance;
	}
	
	
}
