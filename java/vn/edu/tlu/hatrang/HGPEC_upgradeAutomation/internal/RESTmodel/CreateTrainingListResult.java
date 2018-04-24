package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel;

import java.util.ArrayList;

public class CreateTrainingListResult {
	public ArrayList<GeneFilter> geneTrainingList;
	public ArrayList<DiseaseFilter> diseaseTrainingList;
	public CreateTrainingListResult() {
		super();
	}
	public CreateTrainingListResult(ArrayList<GeneFilter> arr_gene, ArrayList<DiseaseFilter> arr_disease) {
		super();
		this.geneTrainingList = arr_gene;
		this.diseaseTrainingList = arr_disease;
	}
	
	
}
