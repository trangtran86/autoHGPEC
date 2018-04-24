package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.JOptionPane;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.FinishStatus;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskObserver;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.json.JSONResult;
import org.cytoscape.work.util.ListSingleSelection;

import com.google.gson.Gson;

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.CandidateGene;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.ErrorMessage;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.MainData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Node;



public class PCG_NeighborNetworkTask implements ObservableTask {
	private TaskManager cyTaskManager;
	CyNetworkFactory networkFactory;
	CyNetworkManager networkManager;
	CyNetwork network;
	SynchronousTaskManager cySynchronousTaskManager;

	public static final String NEIGHBORS_NETWORK = "Neighbors Of Training Genes in Gene Network";
	public ArrayList<CandidateGene> arr_candidate;

	@Tunable(description = "Distance", longDescription="the distance of neighbors to training genes",
			exampleStringValue="1",
			groups = { "Step 3: Provide Candidate Gene Set - " + NEIGHBORS_NETWORK })
	public int distance = 1;
	
	// @Tunable(description = "Training Genes ",
	// groups = { "Step 3: Provide Candidate Gene Set"},
	// dependsOn="candidateGene="+NEIGHBORS_CHROMOSOME)
	// public ListSingleSelection<String> seedGene;

	

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

	public PCG_NeighborNetworkTask(TaskManager cyTaskManager, CyNetworkFactory networkFactory,
			CyNetworkManager networkManager, CyNetwork network, SynchronousTaskManager cySynchronousTaskManager) {
		super();
		this.cyTaskManager = cyTaskManager;
		this.networkFactory = networkFactory;
		this.networkManager = networkManager;
		this.network = network;
		arr_candidate=new ArrayList<>();
		this.cySynchronousTaskManager=cySynchronousTaskManager;
	}

	@Override
	public void run(TaskMonitor arg0) throws Exception {
		// TODO Auto-generated method stub
		
		MainData.TestGeneType=0;
		if ((MainData.AllTrainingPhenotypes == null || MainData.AllTrainingPhenotypes.size() == 0)
				&& (MainData.AllTrainingGenes == null || MainData.AllTrainingGenes.size() == 0)) {
			JOptionPane.showMessageDialog(null,
					"Neither Training Gene nor Training Disease has been selected.\nCandidate Set Selection can not be performed");
			throw new BadRequestException("Unreacheable operation",
					Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
							.entity(new ErrorMessage("Neither Training Gene nor Training Disease has been selected.\nCandidate Set Selection can not be performed")).build());
		}
		
		TaskObserver obs = new TaskObserver() {
			@Override
			public void taskFinished(ObservableTask ot) {
				fillCandidateGeneTable(MainData.AllTestGenes);
				arg0.setStatusMessage("Total: " + MainData.AllTestGenes.size());
			}

			@Override
			public void allFinished(FinishStatus fs) {
				fillCandidateGeneTable(MainData.AllTestGenes);
				arg0.setStatusMessage("Total: " + MainData.AllTestGenes.size());
			}
		};

		if (MainData.AllTrainingGenes == null || MainData.AllTrainingGenes.size() == 0) {
			JOptionPane.showMessageDialog(null,
					"No Training Genes has been selected.\nThis Candidate Set Selection can not be performed");
			throw new BadRequestException("Unreacheable operation",
					Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
							.entity(new ErrorMessage("No Training Genes has been selected. This Candidate Set Selection can not be performed")).build());
		}

		if (distance < 1 || distance > 4) {
			JOptionPane.showMessageDialog(null, "Distance to training genes should between 1 and 4", "Notice",
					JOptionPane.WARNING_MESSAGE);
			throw new BadRequestException("Unreacheable operation",
					Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
							.entity(new ErrorMessage("Distance to training genes should between 1 and 4")).build());
		
		}

		System.out.println("Finding neighboring interactants of training genes in the network...");
		cySynchronousTaskManager.execute(new ConstructCandidateSetTaskFactory(networkManager, distance).createTaskIterator(), obs);
		Thread.sleep(4000);
		
		
	}

	protected void fillCandidateGeneTable(ArrayList<Node> allTestGenes) {
		// TODO Auto-generated method stub
		CyNetwork mynetwork = networkFactory.createNetwork();
		mynetwork.getRow(mynetwork).set(CyNetwork.NAME, NEIGHBORS_NETWORK);

		CyTable nodeTable = mynetwork.getDefaultNodeTable();

		if (nodeTable.getColumn("Entrez Gene ID") == null) {
			nodeTable.createColumn("Entrez Gene ID", String.class, false);
		}

		if (nodeTable.getColumn("Official Symbol") == null) {
			nodeTable.createColumn("Official Symbol", String.class, false);
		}

		if (nodeTable.getColumn("Alt Syms") == null) {
			nodeTable.createColumn("Alt Syms", String.class, false);
		}

		if (nodeTable.getColumn("Chrom/Distance") == null) {
			nodeTable.createColumn("Chrom/Distance", Integer.class, false);
		}
		
		
		for (int i = 0; i < allTestGenes.size(); i++) {
			CyNode node = mynetwork.addNode();
			CyRow row = nodeTable.getRow(node.getSUID());
			CandidateGene cg=new CandidateGene();
			cg.EntrezID=allTestGenes.get(i).EntrezID;
			cg.OfficialSymbols=allTestGenes.get(i).OfficialSymbol;
			cg.AlternateSymbols=allTestGenes.get(i).AlternateSymbols.toString().substring(
					allTestGenes.get(i).AlternateSymbols.toString().indexOf("[") + 1,
					allTestGenes.get(i).AlternateSymbols.toString().indexOf("]"));
			cg.DistanceToSeed=allTestGenes.get(i).DistanceToSeed;
			arr_candidate.add(cg);
			
			row.set("Entrez Gene ID", allTestGenes.get(i).EntrezID);
			row.set("Official Symbol", allTestGenes.get(i).OfficialSymbol);
			row.set("Alt Syms",allTestGenes.get(i).AlternateSymbols.toString().substring(
							allTestGenes.get(i).AlternateSymbols.toString().indexOf("[") + 1,
							allTestGenes.get(i).AlternateSymbols.toString().indexOf("]")));
			row.set("Chrom/Distance", allTestGenes.get(i).DistanceToSeed);
			row.set("shared name", allTestGenes.get(i).EntrezID);
		}
		networkManager.addNetwork(mynetwork);
		
	}

	public String getTitle() {
		return ("Provide Candidate Gene Set");
	}

	public static final String getJson(ArrayList<CandidateGene> arr_cg) {
		return new Gson().toJson(arr_cg);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> R getResults(Class<? extends R> type) {
		// TODO Auto-generated method stub
		if (type.equals(String.class)) {
			return (R) getJson(arr_candidate);
		} else if (type.equals(JSONResult.class)) {
			JSONResult res = () -> {
				return getJson(arr_candidate);
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
