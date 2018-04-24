package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel;

import java.util.List;

public class DiseaseFilter {
	public String name;
	public String DiseaseID;
	public String MedGenCUI;
	public String AssociatedGenes;
	
	public DiseaseFilter() {
		super();
	}

	public DiseaseFilter(String name, String diseaseID, String medGenCUI, String associatedGenes) {
		super();
		this.name = name;
		DiseaseID = diseaseID;
		MedGenCUI = medGenCUI;
		AssociatedGenes = associatedGenes;
	}
	
	
	
}
