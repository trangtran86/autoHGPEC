package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.BasicData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.Common;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.MainData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.UserData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Disease;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Node;

public class HGPECResourceImp implements HGPECresource {
	 
	@Override
	public List<DiseaseFilter> getDisease(String diseaseName) {
		// TODO Auto-generated method stub
		if (BasicData.UpdatedPhenotypeNetworkNode.size()==0){
			throw new BadRequestException("Unreacheable operation",
					Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
							.entity(new ErrorMessage("You must complete step 1 by Command first")).build());

		};
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

	@Override
	public List<RankedDisease> getRankedDiseases(int limit) {
		// TODO Auto-generated method stub
		if (MainData.PrioritizationScore.size()==0){
			throw new BadRequestException("Unreacheable operation",
					Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
							.entity(new ErrorMessage("You must complete all the steps by Command first")).build());

		};
		List<RankedDisease> list=new ArrayList<>();
		ArrayList<Node> runresult = new ArrayList<Node>();
		int r=0;
		for (int i = 0; r<limit; i++) {
			if (MainData.PrioritizationScore.get(i).Type.equals("Disease")) {
				r++;
				runresult.add(MainData.PrioritizationScore.get(i).Copy());
				runresult.get(r - 1).Rank = r;
				
			}
		}
		DecimalFormat df = new DecimalFormat("0.00000000");
		for (Node n:runresult){
			RankedDisease rg=new RankedDisease();
			rg.rank=n.Rank;
			rg.DiseaseID=n.NetworkID;
			rg.type=n.Type;
			rg.AssociatedGenes=n.AlternateSymbols.toString().substring(1,n.AlternateSymbols.toString().length() - 1);
			rg.isSeed=n.IsSeed;
			rg.isTest=n.IsTest;
			rg.score=df.format(n.Score);
			rg.name=n.Name;
			
			list.add(rg);
		}
		
		return list;
	}

	@Override
	public List<RankedGene> getRankedGenes(int limit) {
		// TODO Auto-generated method stub
		if (MainData.PrioritizationScore.size()==0){
			throw new BadRequestException("Unreacheable operation",
					Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
							.entity(new ErrorMessage("You must complete all the steps by Command first")).build());

		};
		ArrayList<Node> runresult = new ArrayList<Node>();
		int r = 0;
		for (int i = 0; r<limit; i++) {
			if (MainData.PrioritizationScore.get(i).Type.compareTo("Gene/Protein") == 0) {
				r++;
				runresult.add(MainData.PrioritizationScore.get(i).Copy());
				runresult.get(r - 1).Rank = r;
			}
		}
		DecimalFormat df = new DecimalFormat("0.00000000");
		ArrayList<RankedGene> arr_gene=new ArrayList<>();
		for (Node n:runresult){
			RankedGene rg=new RankedGene();
			rg.rank=n.Rank;
			rg.EntrezID=n.EntrezID;
			rg.type=n.Type;
			rg.OfficialSymbol=n.OfficialSymbol;
			rg.AlterSymbol=n.AlternateSymbols.toString().substring(1,n.AlternateSymbols.toString().length() - 1);
			rg.isSeed=n.IsSeed;
			rg.isTest=n.IsTest;
			rg.score=df.format(n.Score);
			
			arr_gene.add(rg);
		}
		return arr_gene;
	}

}
