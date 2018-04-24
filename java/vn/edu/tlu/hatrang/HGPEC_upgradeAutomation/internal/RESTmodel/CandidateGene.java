package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel;

public class CandidateGene {
	public String EntrezID;
	public String OfficialSymbols;
	public String AlternateSymbols;
	public int DistanceToSeed;
	public CandidateGene() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CandidateGene(String entrezID, String officialSymbols, String alternateSymbols, int distanceToSeed) {
		super();
		EntrezID = entrezID;
		OfficialSymbols = officialSymbols;
		AlternateSymbols = alternateSymbols;
		DistanceToSeed = distanceToSeed;
	}
	
	
}
