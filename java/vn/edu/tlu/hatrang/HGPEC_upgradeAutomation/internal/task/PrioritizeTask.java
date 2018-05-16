package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTable;

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

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.CreateTrainingListResult;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.RankedDisease;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.RankedGene;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.RankedResult;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.BasicData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.Common;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.Main;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.MainData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.UserData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Node;



public class PrioritizeTask implements ObservableTask {
	private TaskManager cyTaskManager;
	CyNetworkFactory networkFactory;
	CyNetworkManager networkManager;
	SynchronousTaskManager cySynchronousTaskManager;
	CyNetwork network;
	public RankedResult result;
	@Tunable(description = "Back prob",longDescription="back-probability (alpha) of RWRH algorithm ",
			exampleStringValue="0.5",
			groups = { "Step 4: Prioritize candidate genes and diseases in the heterogeneous network "  })
	public ListSingleSelection<Float> backProb = new ListSingleSelection<Float>(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f,
			0.7f, 0.8f, 0.9f);

	@Tunable(description = "Jumping prob",longDescription="jumping probability (lamda) of RWRH algorithm ",
			exampleStringValue="0.6",
			groups = { "Step 4: Prioritize candidate genes and diseases in the heterogeneous network "  })
	public ListSingleSelection<Float> jumpProb = new ListSingleSelection<Float>(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f,
			0.8f, 0.9f);

	@Tunable(description = "Sub-network importance weight",longDescription="subnetwork importance (eta) of RWRH algorithm ",
			exampleStringValue="0.7",
			groups = { "Step 4: Prioritize candidate genes and diseases in the heterogeneous network "  })
	public ListSingleSelection<Float> subnetWeight = new ListSingleSelection<Float>(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f,
			0.7f, 0.8f, 0.9f);

	public PrioritizeTask(CyNetwork arg0, CyNetworkFactory networkFactory, CyNetworkManager networkManager,
			TaskManager cyTaskManager,SynchronousTaskManager cySynchronousTaskManager) {
		super();
		network = arg0;
		this.networkFactory = networkFactory;
		this.networkManager = networkManager;
		this.cyTaskManager = cyTaskManager;
		this.cySynchronousTaskManager=cySynchronousTaskManager;

		backProb.setSelectedValue(0.5f);
		jumpProb.setSelectedValue(0.6f);
		subnetWeight.setSelectedValue(0.7f);
		result=new RankedResult();

	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(TaskMonitor arg0) throws Exception {
		// TODO Auto-generated method stub
		if ((MainData.AllTrainingPhenotypes == null || MainData.AllTrainingPhenotypes.size() == 0)
				&& (MainData.AllTrainingGenes == null || MainData.AllTrainingGenes.size() == 0)) {
			arg0.setStatusMessage(
					"Neither Training Genes nor Training Diseases are selected yet. Prioritization can not be performed");
			return;
		}

		String DiseaseName = UserData.term;

		if (BasicData.UpdatedGeneNetworkNode == null || BasicData.UpdatedGeneNetworkNode.size() == 0) {
			arg0.setStatusMessage("Gene Network need to be checked and updated first");
			return;
		}

		if (MainData.AllTestGenes == null || MainData.AllTestGenes.size() == 0) {
			arg0.setStatusMessage("You have to choose candidate genes first...!");
			return;
		}

		Main.alpha = backProb.getSelectedValue();
		Main.lambda = jumpProb.getSelectedValue();
		Main.eta = subnetWeight.getSelectedValue();

		arg0.setStatusMessage("Summary before ranking: " + "\n-Disease: " + UserData.term
				+ "\n-Back probability (Alpha): " + Main.alpha + "\n-Jumping probability (Lambda): " + Main.lambda
				+ "\n-Subnetwork importance (Eta): " + Main.eta + "\n-Disease Network size: |V| = "
				+ BasicData.NetworkPhenotypeSet.size() + ", |A| = " + BasicData.PhenotypeNetwork.size()
				+ "\n-Gene/Protein Network size: |V| = " + BasicData.NetworkGeneSet.size() + ", |A| = "
				+ BasicData.GeneNetwork.size() + "\n-Number of Training Genes: " + MainData.AllTrainingGenes.size()
				+ "\n-Number of Training Diseases: " + MainData.AllTrainingPhenotypes.size()
				+ "\n-Number of Candidate Genes: " + MainData.AllTestGenes.size());

		HeterogeneousNetworkNormalizationAnalysisTaskFactory analysisTaskFactory = new HeterogeneousNetworkNormalizationAnalysisTaskFactory();
		TaskObserver observer = new TaskObserver() {
			@Override
			public void taskFinished(ObservableTask ot) {
				throw new UnsupportedOperationException("Not supported yet.");
			}

			@Override
			public void allFinished(FinishStatus fs) {
				// ========================================================
				// Call Disease Gene Prioritization Task
				TaskObserver obs = new TaskObserver() {
					@Override
					public void taskFinished(ObservableTask ot) {
						System.out.println("su1");

					}

					@Override
					public void allFinished(FinishStatus fs) {
						//JOptionPane.showMessageDialog(null, "Prioritization process is finished!");
						showRankedPhenotypes();
						showRankedGenes();
						Common.assignNodeScoresInNetwork(MainData.PrioritizationScore, networkManager);
					}
				};

				PrioritizationTaskFactory prioritizationTaskFactory = new PrioritizationTaskFactory();
				cySynchronousTaskManager.execute(prioritizationTaskFactory.createTaskIterator(), obs);
			}
		};
		cySynchronousTaskManager.execute(analysisTaskFactory.createTaskIterator(), observer);

	}

	private void showRankedGenes() {
		int i;
		ArrayList<Node> runresult = new ArrayList<Node>();

		int r = 0;
		// if (cboPrioritizationScoreGene.getSelectedIndex() == 0) {
		// System.out.println("All ranked candidate genes are shown");
		// r = 0;
		//
		// for (i = 0; i < MainData.PrioritizationScore.size(); i++) {
		// if (MainData.PrioritizationScore.get(i).Type.equals("Gene/Protein")
		// && MainData.PrioritizationScore.get(i).IsTest) {
		// r++;
		// runresult.add(MainData.PrioritizationScore.get(i).Copy());
		// runresult.get(r - 1).Rank = r;
		// }
		// }
		// } else {
		System.out.println("All ranked network genes are shown");
		for (i = 0; i < MainData.PrioritizationScore.size(); i++) {
			if (MainData.PrioritizationScore.get(i).Type.compareTo("Gene/Protein") == 0) {
				r++;
				runresult.add(MainData.PrioritizationScore.get(i).Copy());
				runresult.get(r - 1).Rank = r;
			}
		}

		// Display in table
		CyNetwork mynetwork = networkFactory.createNetwork();
		mynetwork.getRow(mynetwork).set(CyNetwork.NAME, "All Ranked Genes");

		CyTable nodeTable = mynetwork.getDefaultNodeTable();
		if (nodeTable.getColumn("Rank") == null) {
			nodeTable.createColumn("Rank", Integer.class, false);
		}

		if (nodeTable.getColumn("Entrez Gene ID") == null) {
			nodeTable.createColumn("Entrez Gene ID", String.class, false);
		}

		if (nodeTable.getColumn("Type") == null) {
			nodeTable.createColumn("Type", String.class, false);
		}

		if (nodeTable.getColumn("Official Symbol") == null) {
			nodeTable.createColumn("Official Symbol", String.class, false);
		}

		if (nodeTable.getColumn("Alternate Symbols") == null) {
			nodeTable.createColumn("Alternate Symbols", String.class, false);
		}

		if (nodeTable.getColumn("Training") == null) {
			nodeTable.createColumn("Training", Boolean.class, false);
		}

		if (nodeTable.getColumn("Candidate") == null) {
			nodeTable.createColumn("Candidate", Boolean.class, false);
		}

		if (nodeTable.getColumn("Score") == null) {
			nodeTable.createColumn("Score", String.class, false);
		}
		DecimalFormat df = new DecimalFormat("0.00000000");
		ArrayList<RankedGene> arr_gene=new ArrayList<>();
		for (i = 0; i < runresult.size(); i++) {
			CyNode node = mynetwork.addNode();
			CyRow row = nodeTable.getRow(node.getSUID());

			row.set("Rank", runresult.get(i).Rank);
			row.set("Entrez Gene ID", runresult.get(i).EntrezID);
			row.set("Type", runresult.get(i).Type);
			row.set("Official Symbol", runresult.get(i).OfficialSymbol);
			row.set("Alternate Symbols", runresult.get(i).AlternateSymbols.toString().substring(1,
					runresult.get(i).AlternateSymbols.toString().length() - 1));
			row.set("Training", runresult.get(i).IsSeed);

			row.set("Candidate", runresult.get(i).IsTest);
			// RankedGene.add(7,runresult.get(i).IsHeldout);
			row.set("Score", df.format(runresult.get(i).Score));
			// RankedGene.add(8,"");
			// RankedGene.add(9,"");
			// RankedGene.add(10,"");
			if (i<100){
				RankedGene rg=new RankedGene();
				rg.rank=runresult.get(i).Rank;
				rg.EntrezID=runresult.get(i).EntrezID;
				rg.type=runresult.get(i).Type;
				rg.OfficialSymbol=runresult.get(i).OfficialSymbol;
				rg.AlterSymbol=runresult.get(i).AlternateSymbols.toString().substring(1,runresult.get(i).AlternateSymbols.toString().length() - 1);
				rg.isSeed=runresult.get(i).IsSeed;
				rg.isTest=runresult.get(i).IsTest;
				rg.score=df.format(runresult.get(i).Score);
				
				arr_gene.add(rg);
			}
		}
		result.arr_gene=arr_gene;
		networkManager.addNetwork(mynetwork);
		// lblTotalGene.setText("Total: " + runresult.size());
		// Always assign Score for all nodes in the network
		// Common.assignNodeScoresInNetwork(MainData.PrioritizationScore);
		Common.highlightNodesInNetwork(networkManager, runresult);

	}

	private void showRankedPhenotypes() {
		int i;
		ArrayList<Node> runresult = new ArrayList<Node>();
		int r = 0;
		// if (cboPrioritizationScorePhenotype.getSelectedIndex() == 0) {
//		System.out.println("All ranked Non-Training Phenotypes/Diseases are shown");
//		r = 0;
//		for (i = 0; i < MainData.PrioritizationScore.size(); i++) {
//			if (MainData.PrioritizationScore.get(i).Type.equals("Disease")
//					&& !MainData.PrioritizationScore.get(i).IsSeed) {
//				r++;
//				runresult.add(MainData.PrioritizationScore.get(i).Copy());
//				runresult.get(r - 1).Rank = r;
//			}
//		}

		// } else {
		System.out.println("All ranked Diseases are shown");
		r = 0;
		for (i = 0; i < MainData.PrioritizationScore.size(); i++) {
			if (MainData.PrioritizationScore.get(i).Type.compareTo("Disease") == 0) {
				r++;
				runresult.add(MainData.PrioritizationScore.get(i).Copy());
				runresult.get(r - 1).Rank = r;
			}
		}
		//
		// }

		// Display in table
		CyNetwork mynetwork = networkFactory.createNetwork();
		mynetwork.getRow(mynetwork).set(CyNetwork.NAME, "All Ranked Diseases");

		CyTable nodeTable = mynetwork.getDefaultNodeTable();
		if (nodeTable.getColumn("Rank") == null) {
			nodeTable.createColumn("Rank", Integer.class, false);
		}

		if (nodeTable.getColumn("Disease ID") == null) {
			nodeTable.createColumn("Disease ID", String.class, false);
		}

		if (nodeTable.getColumn("Type") == null) {
			nodeTable.createColumn("Type", String.class, false);
		}

		if (nodeTable.getColumn("Associated Genes") == null) {
			nodeTable.createColumn("Associated Genes", String.class, false);
		}


		if (nodeTable.getColumn("Training") == null) {
			nodeTable.createColumn("Training", Boolean.class, false);
		}

		if (nodeTable.getColumn("Candidate") == null) {
			nodeTable.createColumn("Candidate", Boolean.class, false);
		}

		if (nodeTable.getColumn("Score") == null) {
			nodeTable.createColumn("Score", String.class, false);
		}
		DecimalFormat df = new DecimalFormat("0.00000000");
		ArrayList<RankedDisease> arr_disease=new ArrayList<>();
		for (i = 0; i < runresult.size(); i++) {
			CyNode node = mynetwork.addNode();
			CyRow row = nodeTable.getRow(node.getSUID());
			
			row.set("Rank", runresult.get(i).Rank);
			row.set("Disease ID", runresult.get(i).NetworkID);
			row.set("shared name", runresult.get(i).Name);
			row.set("Type", runresult.get(i).Type);
			row.set("Associated Genes", runresult.get(i).AlternateSymbols.toString().substring(1,runresult.get(i).AlternateSymbols.toString().length() - 1));
			row.set("Training", runresult.get(i).IsSeed);
			row.set("Candidate", runresult.get(i).IsTest);
			// RankedGene.add(7,runresult.get(i).IsHeldout);
			row.set("Score", df.format(runresult.get(i).Score));
			// RankedGene.add(8,"");
			// RankedGene.add(9,"");
			// RankedGene.add(10,"");
			
			if (i<100){
				RankedDisease rg=new RankedDisease();
				rg.rank=runresult.get(i).Rank;
				rg.DiseaseID=runresult.get(i).NetworkID;
				rg.type=runresult.get(i).Type;
				rg.AssociatedGenes=runresult.get(i).AlternateSymbols.toString().substring(1,runresult.get(i).AlternateSymbols.toString().length() - 1);
				rg.isSeed=runresult.get(i).IsSeed;
				rg.isTest=runresult.get(i).IsTest;
				rg.score=df.format(runresult.get(i).Score);
				rg.name=runresult.get(i).Name;
				
				arr_disease.add(rg);
			}
		}
		result.arr_disease=arr_disease;
		networkManager.addNetwork(mynetwork);

		// lblTotalGene.setText("Total: " + runresult.size());
		// Always assign Score for all nodes in the network
		// Common.assignNodeScoresInNetwork(MainData.PrioritizationScore);
		// Common.highlightNodeScoresInNetwork(runresult)
	}
	public static String getJson(RankedResult listresult) {
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
