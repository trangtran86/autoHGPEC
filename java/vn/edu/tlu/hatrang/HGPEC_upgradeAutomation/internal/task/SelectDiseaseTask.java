package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.json.JSONResult;

import com.google.gson.Gson;

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.DiseaseFilter;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.ErrorMessage;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.BasicData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.UserData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Disease;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Node;

public class SelectDiseaseTask extends AbstractTask implements ObservableTask {
	CyNetwork network;
	CyNetworkFactory networkFactory;
	CyNetworkManager networkManager;
	public ArrayList<DiseaseFilter> df_list;

	@Tunable(description = "Input disease name", longDescription = "The disease to filter from Heterogeneous Network", 
			exampleStringValue = "breast cancer", groups = {"Step 2: Select Diseases"})
	public String diseaseName= "breast cancer";

	public SelectDiseaseTask(CyNetworkFactory networkFactory, CyNetworkManager networkManager, CyNetwork network) {
		super();
		this.network = network;
		this.networkFactory = networkFactory;
		this.networkManager = networkManager;
	}

	@Override
	public void run(TaskMonitor arg0) {
		// TODO Auto-generated method stub
		try {
			String DiseaseName = diseaseName.trim();
			UserData.term = DiseaseName.toLowerCase();
			String[] DiseaseNameToken = DiseaseName.split(" ");
			int i;
			ArrayList<Disease> DiseaseList = new ArrayList<Disease>();
			df_list = new ArrayList<>();
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
			fillPhenotypeTable(DiseaseList, true);
		} catch (Exception e) {
			throw new NotFoundException("Disease not found", Response.status(Response.Status.NOT_FOUND)
					.type(MediaType.APPLICATION_JSON).entity(new ErrorMessage("Cannot find disease")).build());

		}
	}

	public static String getJson(ArrayList<DiseaseFilter> df) {
		return new Gson().toJson(df);
	}

	private void fillPhenotypeTable(ArrayList<Disease> PhenotypeData, boolean isManual) {
		// TODO Auto-generated method stub
		CyNetwork mynetwork = networkFactory.createNetwork();
		mynetwork.getRow(mynetwork).set(CyNetwork.NAME, diseaseName);

		CyTable nodeTable = mynetwork.getDefaultNodeTable();

		if (nodeTable.getColumn("Disease ID") == null) {
			nodeTable.createColumn("Disease ID", String.class, false);
		}

		if (nodeTable.getColumn("MedGenCUI") == null) {
			nodeTable.createColumn("MedGenCUI", String.class, false);
		}

		if (nodeTable.getColumn("Name") == null) {
			nodeTable.createColumn("Name", String.class, false);
		}

		if (nodeTable.getColumn("Associated Genes") == null) {
			nodeTable.createColumn("Associated Genes", String.class, false);
		}

		// Vector<Object> RankedGene= new Vector<Object>();
		// System.out.println("PhenotypeData.size(): " + PhenotypeData.size());
		for (int i = 0; i < PhenotypeData.size(); i++) {
			CyNode node = mynetwork.addNode();
			CyRow row = nodeTable.getRow(node.getSUID());

			row.set("Disease ID", PhenotypeData.get(i).DiseaseID);
			row.set("MedGenCUI", PhenotypeData.get(i).Prefix);
			row.set("Name", PhenotypeData.get(i).Name);
			row.set("Associated Genes", PhenotypeData.get(i).KnownGeneList);
			row.set("shared name", PhenotypeData.get(i).DiseaseID);
		}
		networkManager.addNetwork(mynetwork);
	}

	public String getTitle() {
		return ("Select Disease");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> R getResults(Class<? extends R> type) {
		// TODO Auto-generated method stub
		if (type.equals(String.class)) {
			return (R) getJson(df_list);
		} else if (type.equals(JSONResult.class)) {
			JSONResult res = () -> {
				return getJson(df_list);
			};
			return (R) (res);
		} else {
			return null;
		}
	}

	@Override
	public List<Class<?>> getResultClasses() {
		// TODO Auto-generated method stub
		return Arrays.asList(String.class, JSONResult.class);
	}

}
