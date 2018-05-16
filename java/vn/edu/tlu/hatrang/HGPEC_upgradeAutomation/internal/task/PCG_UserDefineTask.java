package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskMonitor.Level;
import org.cytoscape.work.TaskObserver;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.json.JSONResult;
import org.cytoscape.work.util.ListSingleSelection;

import com.google.gson.Gson;

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.RankedGene;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.SuscepChroGene;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.BasicData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.Common;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.MainData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.UserData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Node;

public class PCG_UserDefineTask implements ObservableTask {
	CyNetworkFactory networkFactory;
	CyNetworkManager networkManager;
	CyNetwork network;
	private TaskManager cyTaskManager;
	ArrayList<RankedGene> arr_UserDefined;

	// public static final String NEIGHBORS_NETWORK="Neighbors Of Training Genes
	// in Gene Network";
	// public static final String NEIGHBORS_CHROMOSOME="Neighbors Of Training
	// Genes in Chromosome";
	// public static final String ALL_REMAINING="All remaining genes in Gene
	// Network";
	// public static final String SUSCEP_CHROMO="Susceptible Chromosome
	// Regions/Bands";
	public static final String USER_DEFINE = "User-defined";

	@Tunable(description = "Entrez Gene ID", groups = { "Step 3: Provide Candidate Gene Set - " + USER_DEFINE })
	public String TG_UserInput;

	// @Tunable(description = "Training Genes ",
	// groups = { "Step 3: Provide Candidate Gene Set"},
	// dependsOn="candidateGene="+NEIGHBORS_CHROMOSOME)
	// public ListSingleSelection<String> seedGene;

	public PCG_UserDefineTask(CyNetwork arg0, CyNetworkFactory networkFactory, CyNetworkManager networkManager) {
		super();
		network = arg0;
		this.networkFactory = networkFactory;
		this.networkManager = networkManager;

		// candidateGene=new
		// ListSingleSelection<String>(NEIGHBORS_NETWORK,NEIGHBORS_CHROMOSOME,ALL_REMAINING,SUSCEP_CHROMO,USER_DEFINE);
		// seedGene=new
		// ListSingleSelection<>("All","CHEK2","AKT1","HMMR","KRAS","ATM","NQO2","PHB","PIK3CA","BARD1","RAD51","BRCA1","BRCA2","TP53","XRCC3","PALB2","BRIP1","CASP8","RAD54L","PPM1D","RB1CC1","CDH1");
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(TaskMonitor arg0) throws Exception {
		// TODO Auto-generated method stub
		arg0.setProgress(0.1);
		MainData.TestGeneType = 4;
		if (BasicData.UpdatedGeneNetworkNode == null || BasicData.UpdatedGeneNetworkNode.size() == 0) {
			JOptionPane.showMessageDialog(null, "The heterogeneous network should be built first!");
			return;
		}

		UserData.TGUserInput = new TreeSet<String>();
		String str = TG_UserInput.trim();
		StringTokenizer stk = new StringTokenizer(str, ",");
		while (stk.hasMoreTokens()) {
			String genetemp = stk.nextToken().trim();
			if (genetemp.compareTo("") != 0) {
				// if(this.cboTG_Format_UserInput.getSelectedIndex()==1){
				// if(BasicData.AllUniProtAC_OfficialSymbol.containsKey(genetemp)){
				// genetemp=BasicData.AllUniProtAC_OfficialSymbol.get(genetemp);//Convert
				// to UniProtAC to Official Symbol
				// UserData.TGUserInput.add(genetemp);
				// }
				// }else{
				UserData.TGUserInput.add(genetemp);
				// }
			}
		}

		UserData.TGUserInputNormalized = new ArrayList<Node>();
		MainData.AllTestGenes = new ArrayList<Node>();

		for (String g : UserData.TGUserInput) {
			Node n = new Node();
			n.IsInNetwork = false;
			n.NetworkID = g;
			n.Type = "Gene/Protein";
			n.EntrezID = g;
			if (BasicData.UpdatedGeneNetworkNode.containsKey(g)) {
				n = BasicData.UpdatedGeneNetworkNode.get(g);
				n.IsInNetwork = true;
				MainData.AllTestGenes.add(n);
			}
			UserData.TGUserInputNormalized.add(n);
		}

		fillTestGeneTable(UserData.TGUserInputNormalized);
		arg0.showMessage(Level.INFO,"Valid (in network & not in training set): " + MainData.AllTestGenes.size());
		
		if (MainData.AllTestGenes.size() > 0) {
			Common.highlightNodesInNetwork(networkManager, MainData.AllTestGenes);
		} else {
			JOptionPane.showMessageDialog(null,
					"Neither candidate genes/proteins are inputted nor found in the gene/protein interaction network!");
		}
		arg0.setProgress(1);
	}

	private void fillTestGeneTable(ArrayList<Node> GeneData) {
		// TODO Auto-generated method stub
		try {
			CyNetwork mynetwork = networkFactory.createNetwork();
			mynetwork.getRow(mynetwork).set(CyNetwork.NAME, USER_DEFINE);

			CyTable nodeTable = mynetwork.getDefaultNodeTable();

			if (nodeTable.getColumn("EntrezID") == null) {
				nodeTable.createColumn("EntrezID", String.class, false);
			}

			if (nodeTable.getColumn("Official Symbol") == null) {
				nodeTable.createColumn("Official Symbol", String.class, false);
			}

			if (nodeTable.getColumn("Alt Syms") == null) {
				nodeTable.createColumn("Alt Syms", String.class, false);
			}

			if (nodeTable.getColumn("In network") == null) {
				nodeTable.createColumn("In network", Boolean.class, false);
			}

			if (nodeTable.getColumn("Is Training") == null) {
				nodeTable.createColumn("Is Training", Boolean.class, false);
			}

			if (nodeTable.getColumn("Type") == null) {
				nodeTable.createColumn("Type", String.class, false);
			}

			arr_UserDefined = new ArrayList<>();
			for (int i = 0; i < GeneData.size(); i++) {
				CyNode node = mynetwork.addNode();
				CyRow row = nodeTable.getRow(node.getSUID());

				row.set("EntrezID", GeneData.get(i).EntrezID);
				row.set("Official Symbol", GeneData.get(i).OfficialSymbol);
				String genesyms = "";
				for (int j = 0; j < GeneData.get(i).AlternateSymbols.size(); j++) {
					genesyms = genesyms.concat(GeneData.get(i).AlternateSymbols.get(j));
					genesyms = genesyms.concat(", ");
				}
				genesyms = (genesyms.compareTo("") != 0) ? genesyms.substring(0, genesyms.length() - 2) : "";

				row.set("Alt Syms", genesyms);

				row.set("In network", GeneData.get(i).IsInNetwork);
				row.set("Is Training", GeneData.get(i).IsSeed);
				row.set("Type", GeneData.get(i).Type);

				RankedGene cg = new RankedGene();
				cg.EntrezID = GeneData.get(i).EntrezID;
				cg.OfficialSymbol = GeneData.get(i).OfficialSymbol;
				cg.AlterSymbol = genesyms;
				cg.isSeed = GeneData.get(i).IsSeed;
				cg.type = GeneData.get(i).Type;
				arr_UserDefined.add(cg);
			}
			networkManager.addNetwork(mynetwork);

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error while filling genes into table: " + e.toString());
		}
	}
	
	public static final String getJson(ArrayList<RankedGene> arr_cg) {
		return new Gson().toJson(arr_cg);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> R getResults(Class<? extends R> type) {
		// TODO Auto-generated method stub
		if (type.equals(String.class)) {
			return (R) getJson(arr_UserDefined);
		} else if (type.equals(JSONResult.class)) {
			JSONResult res = () -> {
				return getJson(arr_UserDefined);
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
	public String getTitle() {
		return ("Provide Candidate Gene Set");
	}
}
