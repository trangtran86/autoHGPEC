package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel;

import java.util.ArrayList;

public class RankedResult {
	public ArrayList<RankedGene> arr_gene;
	public ArrayList<RankedDisease> arr_disease;
	public RankedResult() {
		super();
		// TODO Auto-generated constructor stub
	}
	public RankedResult(ArrayList<RankedGene> arr_gene, ArrayList<RankedDisease> arr_disease) {
		super();
		this.arr_gene = arr_gene;
		this.arr_disease = arr_disease;
	}
	
	
}
