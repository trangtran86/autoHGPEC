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
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.TaskObserver;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.json.JSONResult;
import org.cytoscape.work.util.ListSingleSelection;

import com.google.gson.Gson;

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.CandidateGene;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.ChromosomeGene;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.ErrorMessage;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.BasicData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.MainData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Node;

public class PCG_NeighborChromosomeNetworkTask implements ObservableTask {
	CyNetworkFactory networkFactory;
	CyNetworkManager networkManager;
	CyNetwork network;
	private TaskManager cyTaskManager;
	ArrayList<ChromosomeGene> arr_chromosome;

	public static final String NEIGHBORS_CHROMOSOME = "Neighbors Of Training Genes in Chromosome";
	// public static final String ALL_REMAINING="All remaining genes in Gene
	// Network";
	// public static final String SUSCEP_CHROMO="Susceptible Chromosome
	// Regions/Bands";
	// public static final String USER_DEFINE="User-defined";

	@Tunable(description = "Distance", longDescription = "number of neighbors of each training gene in the same chromosome", exampleStringValue = "99", groups = {
			"Step 3: Provide Candidate Gene Set - " + NEIGHBORS_CHROMOSOME })
	public int distance = 99;

	@Tunable(description = "Training Genes ", longDescription = "Choose seed gene", exampleStringValue = "All", groups = {
			"Step 3: Provide Candidate Gene Set - " + NEIGHBORS_CHROMOSOME })
	public ListSingleSelection<String> seedGene = new ListSingleSelection<>("All", "CHEK2", "AKT1", "HMMR", "KRAS",
			"ATM", "NQO2", "PHB", "PIK3CA", "BARD1", "RAD51", "BRCA1", "BRCA2", "TP53", "XRCC3", "PALB2", "BRIP1",
			"CASP8", "RAD54L", "PPM1D", "RB1CC1", "CDH1");

	public PCG_NeighborChromosomeNetworkTask(CyNetworkFactory networkFactory, CyNetworkManager networkManager,
			CyNetwork network, TaskManager cyTaskManager) {
		super();
		this.networkFactory = networkFactory;
		this.networkManager = networkManager;
		this.network = network;
		this.cyTaskManager = cyTaskManager;
		arr_chromosome = new ArrayList<>();
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(TaskMonitor arg0) throws Exception {
		// TODO Auto-generated method stub
		int i, j;
		MainData.TestGeneType = 1;
		if ((MainData.AllTrainingPhenotypes == null || MainData.AllTrainingPhenotypes.size() == 0)
				&& (MainData.AllTrainingGenes == null || MainData.AllTrainingGenes.size() == 0)) {
			JOptionPane.showMessageDialog(null,
					"Neither Training Gene nor Training Disease has been selected.\nCandidate Set Selection can not be performed");
			throw new BadRequestException("Unreacheable operation",
					Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
							.entity(new ErrorMessage("Neither Training Gene nor Training Disease has been selected.Candidate Set Selection can not be performed")).build());
		
		
		}
		if (MainData.AllTrainingGenes == null || MainData.AllTrainingGenes.size() == 0) {
			JOptionPane.showMessageDialog(null,
					"No Training Genes has been selected.\nThis Candidate Set Selection can not be performed");
			return;
		}

		// if (distance < 207|| distance > 2675) ????
		if (distance < 55 || distance > 2038) {
			JOptionPane.showMessageDialog(null,
					"Number of near by genes in the same chromosome should be between 55 and 2038", "Notice",
					JOptionPane.WARNING_MESSAGE);
			throw new BadRequestException("Unreacheable operation",
					Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
							.entity(new ErrorMessage("Number of near by genes in the same chromosome should be between 55 and 2038")).build());
		
		}

		if (BasicData.AllGeneChromosome.size() == 0 || BasicData.AllGeneChromosome == null) {
			JOptionPane.showMessageDialog(null, "File Gene-Chromosome \"" + BasicData.Gene_Chromosome_FileName
					+ "\" does not exist.\nUsers could not use genes nearby known genes in the same chromosome as test set");
			throw new BadRequestException("Unreacheable operation",
					Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
							.entity(new ErrorMessage("File Gene-Chromosome \"" + BasicData.Gene_Chromosome_FileName
									+ "\" does not exist.\nUsers could not use genes nearby known genes in the same chromosome as test set")).build());
		
		}
		System.out.println("Finding nearby genes of training genes in the same chromosome...");
		MainData.NumOfNeighbors = distance;
		MainData.LinkageIntervalGenes = new ArrayList<ArrayList<Node>>();

		ArrayList<Node> AllDupTestGenes = new ArrayList<Node>();

		ArrayList<Node> IntervalGenesTemp = new ArrayList<Node>();
		int total = 0;
		Set<String> DistinctGenes = new TreeSet<String>();
		for (i = 0; i < MainData.AllTrainingGenes.size(); i++) {
			IntervalGenesTemp = findNeighboringGenesOfHeldoutGeneInChromosome(MainData.AllTrainingGenes.get(i),
					MainData.NumOfNeighbors);

			if (IntervalGenesTemp.size() == 0) {
				JOptionPane.showMessageDialog(null,
						"Can not find gene " + MainData.AllTrainingGenes.get(i).OfficialSymbol
								+ " in Gene-Chromosome Database.\nPlease check and update Gene-Chromosome database!");
				throw new BadRequestException("Unreacheable operation",
						Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
								.entity(new ErrorMessage("Can not find gene " + MainData.AllTrainingGenes.get(i).OfficialSymbol
								+ " in Gene-Chromosome Database.\nPlease check and update Gene-Chromosome database!")).build());
			
				
			} else if (IntervalGenesTemp.size() < MainData.NumOfNeighbors) {
				JOptionPane.showMessageDialog(null, MainData.AllTrainingGenes.get(i).OfficialSymbol + " has only "
						+ IntervalGenesTemp.size()
						+ " nearby genes in the same chromosome which also are in network!\nEither number of network nodes is too small or number of nearby genes is too large");
				throw new BadRequestException("Unreacheable operation",
						Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON)
								.entity(new ErrorMessage(MainData.AllTrainingGenes.get(i).OfficialSymbol + " has only "
						+ IntervalGenesTemp.size()
						+ " nearby genes in the same chromosome which also are in network!\nEither number of network nodes is too small or number of nearby genes is too large")).build());
			
			}
			// JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Hello3");
			MainData.LinkageIntervalGenes.add(IntervalGenesTemp);

			for (j = 0; j < IntervalGenesTemp.size(); j++) {
				AllDupTestGenes.add(IntervalGenesTemp.get(j));
				DistinctGenes.add(IntervalGenesTemp.get(j).EntrezID);
			}

			total += IntervalGenesTemp.size();
		}
		fillCandidateGeneTable(AllDupTestGenes);

		MainData.AllTestGenes = new ArrayList<Node>();
		for (String eid : DistinctGenes) {
			MainData.AllTestGenes.add(BasicData.UpdatedGeneNetworkNode.get(eid));
		}
		arg0.setStatusMessage("Total: " + MainData.AllTestGenes.size());

	}

	protected void fillCandidateGeneTable(ArrayList<Node> allTestGenes) {
		// TODO Auto-generated method stub
		CyNetwork mynetwork = networkFactory.createNetwork();
		mynetwork.getRow(mynetwork).set(CyNetwork.NAME, NEIGHBORS_CHROMOSOME);

		CyTable nodeTable = mynetwork.getDefaultNodeTable();

		if (nodeTable.getColumn("Entrez Gene ID") == null) {
			nodeTable.createColumn("Entrez Gene ID", String.class, false);
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

		if (nodeTable.getColumn("Band") == null) {
			nodeTable.createColumn("Band", String.class, false);
		}

		if (nodeTable.getColumn("Chrom/Distance") == null) {
			nodeTable.createColumn("Chrom/Distance", String.class, false);
		}

		for (int i = 0; i < allTestGenes.size(); i++) {
			CyNode node = mynetwork.addNode();
			CyRow row = nodeTable.getRow(node.getSUID());

			ChromosomeGene cg = new ChromosomeGene();
			cg.EntrezGeneID = allTestGenes.get(i).EntrezID;
			cg.OfficialSymbol = allTestGenes.get(i).OfficialSymbol;
			cg.GeneStart = allTestGenes.get(i).GeneStart;
			cg.GeneEnd = allTestGenes.get(i).GeneEnd;
			cg.Band = allTestGenes.get(i).Band;
			cg.chromDistance = allTestGenes.get(i).Chromosome;
			arr_chromosome.add(cg);

			row.set("Entrez Gene ID", allTestGenes.get(i).EntrezID);
			row.set("Official Symbol", allTestGenes.get(i).OfficialSymbol);
			row.set("Gene Start", allTestGenes.get(i).GeneStart);
			row.set("Gene End", allTestGenes.get(i).GeneEnd);
			row.set("Band", allTestGenes.get(i).Band);
			row.set("Chrom/Distance", allTestGenes.get(i).Chromosome);

		}
		networkManager.addNetwork(mynetwork);

	}

	public static ArrayList<Node> findNeighboringGenesOfHeldoutGeneInChromosome(Node HeldoutGene, int NumOfNeighbors) {

		ArrayList<Node> AllTestGenes = new ArrayList<Node>();

		try {

			int i, j;

			String chrom = "";
			for (i = 0; i < BasicData.AllGeneChromosome.size(); i++) {
				if (HeldoutGene.EntrezID.compareTo(BasicData.AllGeneChromosome.get(i).EntrezID) == 0) {
					chrom = BasicData.AllGeneChromosome.get(i).Chromosome;
					break;
				}
			}
			if (chrom.isEmpty()) {
				return AllTestGenes;// If not found then AllTestGenes.size()
									// will be 0
			}
			ArrayList<Node> GeneOnAChromosomeAndNetwork = new ArrayList<Node>();
			Set<String> OtherTrainingGeneSet = new TreeSet<String>();
			for (String eid : MainData.TrainingGeneSet) {
				if (eid.compareToIgnoreCase(HeldoutGene.EntrezID) != 0) {
					OtherTrainingGeneSet.add(eid);
				}
			}
			for (i = 0; i < BasicData.AllGeneChromosome.size(); i++) {
				if (BasicData.AllGeneChromosome.get(i).Chromosome.compareToIgnoreCase(chrom) == 0
						&& BasicData.NetworkGeneSet.contains(BasicData.AllGeneChromosome.get(i).EntrezID)
						&& !OtherTrainingGeneSet.contains(BasicData.AllGeneChromosome.get(i).EntrezID)) {
					GeneOnAChromosomeAndNetwork.add(BasicData.AllGeneChromosome.get(i));
				}
			}

			int pos = -1;// Position of known gene in Gene-Chromosome database
			for (i = 0; i < GeneOnAChromosomeAndNetwork.size(); i++) {
				if (HeldoutGene.EntrezID.compareTo(GeneOnAChromosomeAndNetwork.get(i).EntrezID) == 0) {
					pos = i;
					break;
				}
			}

			int start; // u: upstream
			int end; // d: downstream

			if (pos < NumOfNeighbors / 2) {
				start = 0;
				end = start + NumOfNeighbors;
			} else if (pos > GeneOnAChromosomeAndNetwork.size() - NumOfNeighbors / 2) {
				end = GeneOnAChromosomeAndNetwork.size() - 1;
				start = end - NumOfNeighbors;
			} else {
				start = pos - NumOfNeighbors / 2;
				end = start + NumOfNeighbors;
			}

			for (i = start; i <= end; i++) {
				if (HeldoutGene.EntrezID.compareTo(GeneOnAChromosomeAndNetwork.get(i).EntrezID) != 0) {
					AllTestGenes.add(GeneOnAChromosomeAndNetwork.get(i));
				}
			}

			System.out.println("AllTestGenes.size(): " + AllTestGenes.size());

		} catch (Exception e) {
			System.out.println("Error while finding neighboring genes in the same chromosome: " + e.toString());
		}
		return AllTestGenes;
	}

	public String getTitle() {
		return ("Provide Candidate Gene Set");
	}

	public static final String getJson(ArrayList<ChromosomeGene> arr_cg) {
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
