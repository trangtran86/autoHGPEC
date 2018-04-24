package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel;

public class RankedDisease extends DiseaseFilter {
	public int rank;
	public String type;
	public boolean isSeed;
	public boolean isTest;
	public String score;
	public RankedDisease() {
		super();
		// TODO Auto-generated constructor stub
	}
	public RankedDisease(String name, String diseaseID, String medGenCUI, String associatedGenes, int rank, String type,
			boolean isSeed, boolean isTest, String score) {
		super(name, diseaseID, medGenCUI, associatedGenes);
		this.rank = rank;
		this.type = type;
		this.isSeed = isSeed;
		this.isTest = isTest;
		this.score = score;
	}
	
}
