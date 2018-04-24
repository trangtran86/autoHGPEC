package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.json.JSONResult;

import com.google.gson.Gson;

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.CreateTrainingListResult;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.DiseaseFilter;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.ErrorMessage;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.GeneFilter;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.BasicData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.Common;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.MainData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Node;

public class CreateTrainingListTask implements ObservableTask {

	CyNetwork network;
	CyNetworkFactory networkFactory;
	CyNetworkManager networkManager;
	@Tunable(description = "Disease IDs", longDescription = "The disease ID to create training list. Choose from the list created in step 2: ", exampleStringValue = "MIM114480", groups = {
			"Step 2: Select Diseases" })
	public String diseaseTraining="MIM114480";

	public CreateTrainingListResult result;

	public CreateTrainingListTask(CyNetwork network, CyNetworkFactory networkFactory, CyNetworkManager networkManager) {
		super();
		this.network = network;
		this.networkFactory = networkFactory;
		this.networkManager = networkManager;
		result = new CreateTrainingListResult();
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(TaskMonitor arg0) throws Exception {
		// TODO Auto-generated method stub
		int i, j;
		if (BasicData.UpdatedGeneNetworkNode == null || BasicData.UpdatedGeneNetworkNode.size() == 0) {
			arg0.showMessage(TaskMonitor.Level.WARN, "You have to create the Heterogeneous network first!");
			arg0.setTitle("Warning");
			throw new BadRequestException("Unreacheable operation",
					Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
							.entity(new ErrorMessage("You must complete step 1 and 2.1 first")).build());

		}

		// if(network==null){
		// arg0.setStatusMessage( "You must choose a network");
		// throw new NotFoundException("Network not
		// found",Response.status(Response.Status.NOT_FOUND)
		// .type(MediaType.APPLICATION_JSON).entity(new ErrorMessage("Network is
		// not selected")).build());
		//
		// }

		CyTable curTable = network.getDefaultNodeTable();

		System.out.println(curTable.getRowCount());
		List<CyNode> selectedNode = CyTableUtil.getNodesInState(network, "selected", true);
		System.out.println("Number of selected nodes are " + selectedNode.size());

		// if (selectedNode.size() == 0) {
		// arg0.setStatusMessage("You must choose a Disease");
		// throw new NotFoundException("Not found selected row",
		// Response.status(Response.Status.NOT_FOUND)
		// .type(MediaType.APPLICATION_JSON).entity(new ErrorMessage("Not found
		// selected row")).build());
		//
		// }

		Set<String> AssociatedEntrezIDSet = new TreeSet<String>();// Distinct
																	// Associated
																	// Genes
		Set<String> SelectedDiseaseIDSet = new TreeSet<String>();
		
		if (selectedNode.size() != 0) {
			for (CyNode node : selectedNode) {
				CyRow row = network.getRow(node);
				String DiseaseID = row.get("Disease ID", String.class);
				// System.out.println(DiseaseID);
				if (BasicData.NetworkPhenotypeSet.contains(DiseaseID)) {
					SelectedDiseaseIDSet.add(DiseaseID);
					ArrayList<String> KnownGenes = BasicData.UpdatedPhenotypeNetworkNode
							.get(DiseaseID).AlternateSymbols;
					KnownGenes.retainAll(BasicData.NetworkGeneSet);
					AssociatedEntrezIDSet.addAll(KnownGenes);
				}
			}
		}

		if (!diseaseTraining.trim().equals("")) {
			String[] str = diseaseTraining.trim().split(",");
			for (String s : str) {
				if (BasicData.NetworkPhenotypeSet.contains(s)) {
					SelectedDiseaseIDSet.add(s);
					ArrayList<String> KnownGenes = BasicData.UpdatedPhenotypeNetworkNode.get(s).AlternateSymbols;
					KnownGenes.retainAll(BasicData.NetworkGeneSet);
					AssociatedEntrezIDSet.addAll(KnownGenes);
				}
			}
		}

		// for (i = 0; i < this.tblSelectedPhenotypes.getRowCount(); i++) {
		// if (Boolean.parseBoolean(this.tblSelectedPhenotypes.getValueAt(i,
		// 0).toString()) == true) {
		// String DiseaseID = this.tblSelectedPhenotypes.getValueAt(i,
		// 1).toString();
		// if (BasicData.NetworkPhenotypeSet.contains(DiseaseID)) {
		// SelectedDiseaseIDSet.add(DiseaseID);
		// ArrayList<String> KnownGenes =
		// BasicData.UpdatedPhenotypeNetworkNode.get(DiseaseID).AlternateSymbols;//AlternateSymbols
		// contains Associated genes of a disease
		// KnownGenes.retainAll(BasicData.NetworkGeneSet);
		// AssociatedEntrezIDSet.addAll(KnownGenes);
		// }

		System.out.println("AssociatedEntrezIDSet.size(): " + AssociatedEntrezIDSet.size());
		System.out.println("SelectedDiseaseIDSet.size(): " + SelectedDiseaseIDSet.size());

		MainData.AllKnownGenes = new ArrayList<Node>();

		for (String g : AssociatedEntrezIDSet) {
			Node n = new Node();
			if (BasicData.UpdatedGeneNetworkNode.containsKey(g)) {
				n = BasicData.UpdatedGeneNetworkNode.get(g);
			} else {
				n.EntrezID = g;
				n.NetworkID = g;
			}
			MainData.AllKnownGenes.add(n);
		}

		// Find training genes
		MainData.AllTrainingGenes = new ArrayList<Node>();
		MainData.TrainingGeneSet = new TreeSet<String>();

		for (i = 0; i < MainData.AllKnownGenes.size(); i++) {
			Node g = MainData.AllKnownGenes.get(i);
			if (BasicData.UpdatedGeneNetworkNode.containsKey(g.EntrezID)) {
				MainData.AllTrainingGenes.add(g);
				MainData.TrainingGeneSet.add(g.EntrezID);
			}
		}

		// Find training genes
		MainData.AllTrainingPhenotypes = new ArrayList<Node>();
		for (String p : SelectedDiseaseIDSet) {
			Node n = BasicData.UpdatedPhenotypeNetworkNode.get(p);
			MainData.AllTrainingPhenotypes.add(n);
		}

		// System.out.println("MainData.AllTrainingPhenotypes.size(): " +
		// MainData.AllTrainingPhenotypes.size());
		// System.out.println("MainData.AllTrainingGene.size(): " +
		// MainData.AllTrainingGenes.size());

		fillTrainingNodeTable(MainData.AllTrainingGenes, "Gene/Protein");
		fillTrainingNodeTable(MainData.AllTrainingPhenotypes, "Disease");

		// this.lblKnownGenesFound.setText("Total: " +
		// this.tblKnownGenes.getRowCount() + " Gene(s), " +
		// MainData.AllTrainingPhenotypes.size() + " Disease(s)");
		//
		// ======================================================================
		// Find Seed Genes for each trial (In each trial, one training gene is
		// held out and the remaining set is training genes
		MainData.AllSeedGenes = new ArrayList<ArrayList<Node>>();
		ArrayList<Node> TempSeeds;
		for (i = 0; i < MainData.AllTrainingGenes.size(); i++) {
			TempSeeds = new ArrayList<Node>();
			for (j = 0; j < MainData.AllTrainingGenes.size(); j++) {
				if (j == i) {
					continue;
				}
				TempSeeds.add(MainData.AllTrainingGenes.get(j).Copy());
			}
			MainData.AllSeedGenes.add(TempSeeds);
		}

		for (i = 0; i < MainData.AllTrainingGenes.size(); i++) {
			System.out.println("Training gene: " + MainData.AllTrainingGenes.get(i).NetworkID + "\t"
					+ MainData.AllTrainingGenes.get(i).UniProtAC + "\t" + MainData.AllTrainingGenes.get(i).EntrezID
					+ "\t" + MainData.AllTrainingGenes.get(i).OfficialSymbol);
		}

		// JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Hello1");
		// if (PnlGenePrioritizationHGPEC.cboSeedGene.getItemCount() > 0) {
		// PnlGenePrioritizationHGPEC.cboSeedGene.removeAllItems();
		// }
		// PnlGenePrioritizationHGPEC.cboSeedGene.addItem("All");
		//
		// //To easily read, show training gene in OfficialSymbol
		// for (i = 0; i < MainData.AllTrainingGenes.size(); i++) {
		// PnlGenePrioritizationHGPEC.cboSeedGene.addItem(MainData.AllTrainingGenes.get(i).OfficialSymbol);
		//
		// }

		Common.highlightNodesInNetwork(networkManager, MainData.AllTrainingGenes);

		for (i = 0; i < BasicData.AllGeneChromosome.size(); i++) {
			Node g = BasicData.AllGeneChromosome.get(i);
			if (MainData.TrainingGeneSet.contains(g.EntrezID)) {
				BasicData.AllGeneChromosome.get(i).IsSeed = true;
			} else {
				BasicData.AllGeneChromosome.get(i).IsSeed = false;
			}
		}
	}

	private void fillTrainingNodeTable(ArrayList<Node> GeneData, String nodeType) {
		// TODO Auto-generated method stub

		if (nodeType.equals("Gene/Protein")) {
			CyNetwork mynetwork = networkFactory.createNetwork();
			mynetwork.getRow(mynetwork).set(CyNetwork.NAME, "Training Genes");

			CyTable nodeTable = mynetwork.getDefaultNodeTable();

			if (nodeTable.getColumn("EntrezID") == null) {
				nodeTable.createColumn("EntrezID", String.class, false);
			}

			if (nodeTable.getColumn("Official Symbol") == null) {
				nodeTable.createColumn("Official Symbol", String.class, false);
			}

			if (nodeTable.getColumn("Alternate Symbols") == null) {
				nodeTable.createListColumn("Alternate Symbols", String.class, false);
			}

			// Vector<Object> RankedGene= new Vector<Object>();
			// System.out.println("PhenotypeData.size(): " +
			// PhenotypeData.size());
			ArrayList<GeneFilter> arr_gene = new ArrayList<>();
			for (int i = 0; i < GeneData.size(); i++) {
				CyNode node = mynetwork.addNode();
				CyRow row = nodeTable.getRow(node.getSUID());
				GeneFilter ge = new GeneFilter();
				ge.EntrezID = GeneData.get(i).EntrezID;
				ge.OfficialSymbol = GeneData.get(i).OfficialSymbol;

				row.set("EntrezID", GeneData.get(i).EntrezID);
				row.set("Official Symbol", GeneData.get(i).OfficialSymbol);
				StringBuilder genesyms = new StringBuilder();
				for (int j = 0; j < GeneData.get(i).AlternateSymbols.size(); j++) {
					genesyms.append(GeneData.get(i).AlternateSymbols.get(j));
					genesyms.append(", ");
				}
				if (!genesyms.toString().equals(""))
					genesyms.substring(0, genesyms.length() - 2);

				ge.AlterSymbol = genesyms.toString();
				arr_gene.add(ge);

				row.set("Alternate Symbols", GeneData.get(i).AlternateSymbols);
				row.set("shared name", GeneData.get(i).EntrezID);
			}
			networkManager.addNetwork(mynetwork);
			result.geneTrainingList = arr_gene;
		} else {
			CyNetwork mynetwork = networkFactory.createNetwork();
			mynetwork.getRow(mynetwork).set(CyNetwork.NAME, "Training Diseases");

			CyTable nodeTable = mynetwork.getDefaultNodeTable();

			if (nodeTable.getColumn("Disease ID") == null) {
				nodeTable.createColumn("Disease ID", String.class, false);
			}

			if (nodeTable.getColumn("Name") == null) {
				nodeTable.createColumn("Name", String.class, false);
			}

			if (nodeTable.getColumn("Associated Genes") == null) {
				nodeTable.createListColumn("Associated Genes", String.class, false);
			}

			// Vector<Object> RankedGene= new Vector<Object>();
			// System.out.println("PhenotypeData.size(): " +
			// PhenotypeData.size());
			ArrayList<DiseaseFilter> arr_disease = new ArrayList<>();
			for (int i = 0; i < GeneData.size(); i++) {
				CyNode node = mynetwork.addNode();
				CyRow row = nodeTable.getRow(node.getSUID());

				DiseaseFilter di = new DiseaseFilter();
				row.set("Disease ID", GeneData.get(i).NetworkID);
				row.set("Name", GeneData.get(i).Name);
				StringBuilder genesyms = new StringBuilder();
				for (int j = 0; j < GeneData.get(i).AlternateSymbols.size(); j++) {
					genesyms.append(GeneData.get(i).AlternateSymbols.get(j));
					genesyms.append(", ");
				}
				if (!genesyms.equals(""))
					genesyms.substring(0, genesyms.length() - 2);

				di.DiseaseID = GeneData.get(i).NetworkID;
				di.name = GeneData.get(i).Name;
				di.AssociatedGenes = genesyms.toString();
				arr_disease.add(di);
				row.set("Associated Genes", GeneData.get(i).AlternateSymbols);
				row.set("shared name", GeneData.get(i).NetworkID);
			}
			result.diseaseTrainingList = arr_disease;
			networkManager.addNetwork(mynetwork);
		}

	}

	public String getTitle() {
		return ("Create Training List");
	}

	public static String getJson(CreateTrainingListResult listresult) {
		return new Gson().toJson(listresult);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> R getResults(Class<? extends R> type) {
		// TODO Auto-generated method stub
		if (type.equals(String.class)) {
			return (R) getJson(result);
		} else if (type.equals(JSONResult.class)) {
			JSONResult res = () -> {
				return getJson(result);
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
