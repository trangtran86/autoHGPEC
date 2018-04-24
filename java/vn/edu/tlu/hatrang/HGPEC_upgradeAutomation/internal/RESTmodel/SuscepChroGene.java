package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel;

public class SuscepChroGene extends ChromosomeGene{
	public boolean rankable;

	public SuscepChroGene() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SuscepChroGene(String entrezGeneID, String officialSymbol, long geneStart, long geneEnd, String band,
			String chromDistance, boolean rankable) {
		super(entrezGeneID, officialSymbol, geneStart, geneEnd, band, chromDistance);
		this.rankable = rankable;
	}
	
	
}
