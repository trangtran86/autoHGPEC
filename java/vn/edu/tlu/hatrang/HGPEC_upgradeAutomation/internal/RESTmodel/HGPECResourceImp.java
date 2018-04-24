package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.BasicData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.UserData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Disease;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Node;

public class HGPECResourceImp implements HGPECresource {

	@Override
	public List<DiseaseFilter> getDisease(String diseaseName) {
		// TODO Auto-generated method stub
		UserData.term = diseaseName.toLowerCase();
		String[] DiseaseNameToken = diseaseName.split(" ");
		int i;
		ArrayList<Disease> DiseaseList = new ArrayList<Disease>();
		List<DiseaseFilter> df_list = new ArrayList<>();
		for (Map.Entry<String, Node> e : BasicData.UpdatedPhenotypeNetworkNode.entrySet()) {
			boolean satisfied = true;
			for (i = 0; i < DiseaseNameToken.length; i++) {
				if (e.getValue().Name.toUpperCase().contains(DiseaseNameToken[i].trim().toUpperCase()) == false) {
					satisfied = false;
					break;
				}
			}
			if (satisfied == true) {
				Disease d = new Disease();
				DiseaseFilter df = new DiseaseFilter();
				d.DiseaseID = e.getKey();
				df.DiseaseID = e.getKey();
				if (BasicData.Phenotype2Genes_Full.containsKey(d.DiseaseID)) {
					d.Prefix = BasicData.Phenotype2Genes_Full.get(d.DiseaseID).Prefix;
					df.MedGenCUI = d.Prefix;
				}
				d.Name = e.getValue().Name;
				df.name = d.Name;

				if (BasicData.Phenotype2Genes.containsKey(d.DiseaseID)) {
					d.KnownGenes = BasicData.Phenotype2Genes.get(d.DiseaseID).KnownGenes;
					d.KnownGeneList = d.KnownGenes.toString().substring(1, d.KnownGenes.toString().length() - 1);
					df.AssociatedGenes = d.KnownGeneList;
					// System.out.println(d.KnownGenes.toString());
				}
				DiseaseList.add(d);
				df_list.add(df);
			}
		}
		return df_list;
	}

}
