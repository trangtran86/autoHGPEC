package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
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
import org.cytoscape.work.TaskObserver;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.json.JSONResult;
import org.cytoscape.work.util.ListSingleSelection;

import com.google.gson.Gson;

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.ChromosomeGene;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.SuscepChroGene;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.BasicData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.MainData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Node;



public class PCG_SubceptibleChromosomeNetworkTask implements ObservableTask {
	CyNetworkFactory networkFactory;
	CyNetworkManager networkManager;
	CyNetwork network;
	private TaskManager cyTaskManager;
	ArrayList<SuscepChroGene> arr_chromosome;
	
	public static final String SUSCEP_CHROMO = "Susceptible Chromosome Regions/Bands";
	// public static final String USER_DEFINE="User-defined";

	public PCG_SubceptibleChromosomeNetworkTask(CyNetworkFactory networkFactory, CyNetworkManager networkManager,
			CyNetwork network, TaskManager cyTaskManager) {
		super();
		this.networkFactory = networkFactory;
		this.networkManager = networkManager;
		this.network = network;
		this.cyTaskManager = cyTaskManager;
		arr_chromosome=new ArrayList<>();
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(TaskMonitor arg0) throws Exception {
		// TODO Auto-generated method stub
		MainData.TestGeneType=3;
		if ((MainData.AllTrainingPhenotypes == null || MainData.AllTrainingPhenotypes.size() == 0)
				&& (MainData.AllTrainingGenes == null || MainData.AllTrainingGenes.size() == 0)) {
			JOptionPane.showMessageDialog(null,
					"Neither Training Gene nor Training Disease has been selected.\nCandidate Set Selection can not be performed");
			return;
		}

		System.out.println("Showing chromosome regions...");

		MainData.AllTestGenes = new ArrayList<Node>();
		CyNetwork mynetwork = networkFactory.createNetwork();
		mynetwork.getRow(mynetwork).set(CyNetwork.NAME, SUSCEP_CHROMO);

		CyTable nodeTable = mynetwork.getDefaultNodeTable();

		if (nodeTable.getColumn("Rankable") == null) {
			nodeTable.createColumn("Rankable", Boolean.class, false);
		}

		if (nodeTable.getColumn("Chromosome") == null) {
			nodeTable.createColumn("Chromosome", String.class, false);
		}
		if (nodeTable.getColumn("Band") == null) {
			nodeTable.createColumn("Band", String.class, false);
		}

		if (nodeTable.getColumn("Official Symbol") == null) {
			nodeTable.createColumn("Official Symbol", String.class, false);
		}

		if (nodeTable.getColumn("Gene Start") == null) {
			nodeTable.createColumn("Gene Start", Long.class, false);
		}

		if (nodeTable.getColumn("Gene End") == null) {
			nodeTable.createColumn("Gene End", Long.class, false);
		}

		if (nodeTable.getColumn("Entrez ID") == null) {
			nodeTable.createColumn("Entrez ID", String.class, false);
		}
		
		
		for (int i = 0; i < BasicData.AllGeneChromosome.size(); i++) {
			CyNode node = mynetwork.addNode();
			CyRow row = nodeTable.getRow(node.getSUID());

			row.set("Rankable", BasicData.AllGeneChromosome.get(i).IsInNetwork && !BasicData.AllGeneChromosome.get(i).IsSeed);
			row.set("Chromosome", BasicData.AllGeneChromosome.get(i).Chromosome);
			row.set("Official Symbol",BasicData.AllGeneChromosome.get(i).OfficialSymbol);
			row.set("Gene Start", BasicData.AllGeneChromosome.get(i).GeneStart);
			row.set("Gene End", BasicData.AllGeneChromosome.get(i).GeneEnd);
			row.set("Band", BasicData.AllGeneChromosome.get(i).Band);
			row.set("Entrez ID", BasicData.AllGeneChromosome.get(i).EntrezID);
			
			SuscepChroGene cg = new SuscepChroGene();
			cg.rankable=BasicData.AllGeneChromosome.get(i).IsInNetwork && !BasicData.AllGeneChromosome.get(i).IsSeed;
			cg.EntrezGeneID = BasicData.AllGeneChromosome.get(i).EntrezID;
			cg.OfficialSymbol = BasicData.AllGeneChromosome.get(i).OfficialSymbol;
			cg.GeneStart = BasicData.AllGeneChromosome.get(i).GeneStart;
			cg.GeneEnd = BasicData.AllGeneChromosome.get(i).GeneEnd;
			cg.Band = BasicData.AllGeneChromosome.get(i).Band;
			cg.chromDistance = BasicData.AllGeneChromosome.get(i).Chromosome;
			arr_chromosome.add(cg);
		}
		networkManager.addNetwork(mynetwork);
	}

	public String getTitle() {
		return ("Provide Candidate Gene Set");
	}
	
	public static final String getJson(ArrayList<SuscepChroGene> arr_cg) {
		return new Gson().toJson(arr_cg);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> R getResults(Class<? extends R> type) {
		// TODO Auto-generated method stub
		if (type.equals(String.class)) {
			return (R) getJson(arr_chromosome);
		} else if (type.equals(JSONResult.class)) {
			JSONResult res = () -> {
				return getJson(arr_chromosome);
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
