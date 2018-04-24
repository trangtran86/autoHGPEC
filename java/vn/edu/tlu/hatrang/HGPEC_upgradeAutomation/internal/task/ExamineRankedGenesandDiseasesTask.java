package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.BasicData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.Common;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.MainData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.GO;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.QuickGO;

public class ExamineRankedGenesandDiseasesTask implements Task {
	CyNetworkFactory networkFactory;
	CyNetworkManager networkManager;
	CyNetwork network;
	TaskManager cyTaskManager;
	SynchronousTaskManager cySynchronousTaskManager;
	public static Set<GO> knownannset;
	private volatile boolean interrupted = false;

	public ExamineRankedGenesandDiseasesTask(CyNetworkFactory networkFactory, CyNetworkManager networkManager,
			CyNetwork network, TaskManager cyTaskManager, SynchronousTaskManager cySynchronousTaskManager) {
		super();
		this.networkFactory = networkFactory;
		this.networkManager = networkManager;
		this.network = network;
		this.cyTaskManager = cyTaskManager;
		this.cySynchronousTaskManager = cySynchronousTaskManager;
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		interrupted = true;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		// TODO Auto-generated method stu
		// network_gene and network_disease
		CyNetwork network_gene = Common.getNetworkByName(networkManager, "All Ranked Genes");
		CyNetwork network_disease = Common.getNetworkByName(networkManager, "All Ranked Diseases");

		List<CyNode> selected_genes = CyTableUtil.getNodesInState(network_gene, "selected", true);
		List<CyNode> selected_diseases = CyTableUtil.getNodesInState(network_disease, "selected", true);
		System.out.println(selected_diseases.size());
		System.out.println(selected_genes.size());

		if (selected_diseases.size() == 0 && selected_genes.size() == 0) {
			taskMonitor.setStatusMessage(
					"Choose 'All Ranked Genes' network or 'All Ranked Diseases' network and highlight some rows to find evidences");
			return;
		}

		int count_gene = 0;
		for (CyNode node : selected_genes) {
			if (network_gene.getRow(node).get("Candidate", Boolean.class)) {
				count_gene++;
			}
		}

		int count_disease = 0;
		for (CyNode node : selected_diseases) {
			if (network_disease.getRow(node).get("Candidate", Boolean.class)) {
				count_disease++;
			}
		}

		System.out.println("count_disease: " + count_disease);
		System.out.println("count_gene: " + count_gene);
		if (count_gene == 0 && count_disease == 0) {
			System.out.println("You must choose at least one Candidate genes to examine");
			taskMonitor.setStatusMessage("You must choose at least one Candidate genes to examine");
			return;
		}

		taskMonitor.setStatusMessage("Annotating Ranked Genes/Proteins with Protein Complexes...");
		System.out.println("Annotating Ranked Genes/Proteins with Protein Complexes...");
		if (count_gene != 0) {
			CyNetwork newnetwork = networkFactory.createNetwork();
			newnetwork.getRow(newnetwork).set(CyNetwork.NAME, "Genes Evidence Collection");

			CyTable nodeTable = newnetwork.getDefaultNodeTable();
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

			if (nodeTable.getColumn("Protein Complex") == null) {
				nodeTable.createColumn("Protein Complex", String.class, false);
			}

			if (nodeTable.getColumn("KEGG - Pathway") == null) {
				nodeTable.createColumn("KEGG - Pathway", String.class, false);
			}

			if (nodeTable.getColumn("Disease Ontology") == null) {
				nodeTable.createColumn("Disease Ontology", String.class, false);
			}

			if (nodeTable.getColumn("Biological Process") == null) {
				nodeTable.createColumn("Biological Process", String.class, false);
			}

			if (nodeTable.getColumn("Cellular Component") == null) {
				nodeTable.createColumn("Cellular Component", String.class, false);
			}

			if (nodeTable.getColumn("Molecular Function") == null) {
				nodeTable.createColumn("Molecular Function", String.class, false);
			}
			networkManager.addNetwork(newnetwork);

			Set<String> KnownEntrezIDSet = new TreeSet<String>();
			for (int i = 0; i < MainData.AllKnownGenes.size(); i++) {
				KnownEntrezIDSet.add(MainData.AllKnownGenes.get(i).EntrezID);
			}

			knownannset = new HashSet<GO>();
			knownannset = QuickGO.getAnnotationByEntrezGeneID(KnownEntrezIDSet, BasicData.Gene2GO_FileName);

			taskMonitor.setStatusMessage("Annotating Ranked Genes/Proteins by GO terms...");

			Set<String> CandidateEntrezIDSet = new TreeSet<String>(); // List of
																		// Original
																		// KD
																		// genes
																		// inputted
																		// by
																		// users

			for (CyNode node : selected_genes) {
				String EID = network_gene.getRow(node).get("Entrez Gene ID", String.class);

				CyNode n = newnetwork.addNode();
				CyRow row = nodeTable.getRow(n.getSUID());

				row.set("Rank", network_gene.getRow(node).get("Rank", Integer.class));
				row.set("Entrez Gene ID", EID);
				row.set("Type", network_gene.getRow(node).get("Type", String.class));
				row.set("Official Symbol", network_gene.getRow(node).get("Official Symbol", String.class));
				row.set("Alternate Symbols", network_gene.getRow(node).get("Alternate Symbols", String.class));
				System.out.println(EID);
				CandidateEntrezIDSet.add(EID);
				if (BasicData.Gene2Complexes.get(EID) != null) {
					String ComplexIDList = BasicData.Gene2Complexes.get(EID).toString();
					row.set("Protein Complex", ComplexIDList.substring(1, ComplexIDList.length() - 1));
				}
				if (BasicData.Gene2Pathways.get(EID) != null) {
					String PathwayIDList = BasicData.Gene2Pathways.get(EID).toString();
					row.set("KEGG - Pathway", PathwayIDList.substring(1, PathwayIDList.length() - 1));
				}
				if (BasicData.Gene2DOs.get(EID) != null) {
					String DOIDList = BasicData.Gene2DOs.get(EID).toString();
					row.set("Disease Ontology", DOIDList.substring(1, DOIDList.length() - 1));
				}
			}

			Set<GO> annset = new HashSet<GO>();
			annset = QuickGO.getAnnotationByEntrezGeneID(CandidateEntrezIDSet, BasicData.Gene2GO_FileName);
			List<CyNode> listNode = newnetwork.getNodeList();

			for (CyNode node : listNode) {
				String EID = newnetwork.getRow(node).get("Entrez Gene ID", String.class);

				Set<String> GOID_BPs = new TreeSet<String>();
				Set<String> GOID_CCs = new TreeSet<String>();
				Set<String> GOID_MFs = new TreeSet<String>();

				for (String EntrezID : CandidateEntrezIDSet) {

					if (EntrezID.compareToIgnoreCase(EID) == 0) {
						for (Iterator<GO> itgo = annset.iterator(); itgo.hasNext();) {

							GO go = itgo.next();
							// System.out.println(go.GOID + "\t" + go.GOName +
							// "\t"
							// + go.Category + "\t" + go.Evidence + "\t" +
							// go.UniProtAC);
							if (EntrezID.compareToIgnoreCase(go.EntrezID) == 0) {
								if (go.Category.compareToIgnoreCase("Process") == 0) {
									GOID_BPs.add(go.GOID);
								} else if (go.Category.compareToIgnoreCase("Component") == 0) {
									GOID_CCs.add(go.GOID);
								} else {
									GOID_MFs.add(go.GOID);
								}
								BasicData.EvidenceGOs.put(go.GOID, go);
							}
						}
						break;
					}

				}
				System.out.println("BasicData.EvidenceGOs: " + BasicData.EvidenceGOs.size());
				CyRow myrow = nodeTable.getRow(node.getSUID());

				myrow.set("Biological Process", GOID_BPs.toString().substring(1, GOID_BPs.toString().length() - 1));
				myrow.set("Cellular Component", GOID_CCs.toString().substring(1, GOID_CCs.toString().length() - 1));
				myrow.set("Molecular Function", GOID_MFs.toString().substring(1, GOID_MFs.toString().length() - 1));
			}
		}

		if (count_disease != 0) {
			taskMonitor.setTitle("Annotate Ranked Diseases by KEGG pathways");
			taskMonitor.setProgress(0.1);
			try {
				CyNetwork newnetwork = networkFactory.createNetwork();
				newnetwork.getRow(newnetwork).set(CyNetwork.NAME, "Disease Evidence Collection");

				CyTable nodeTable = newnetwork.getDefaultNodeTable();
				if (nodeTable.getColumn("Rank") == null) {
					nodeTable.createColumn("Rank", Integer.class, false);
				}

				if (nodeTable.getColumn("Disease ID") == null) {
					nodeTable.createColumn("Disease ID", String.class, false);
				}

				if (nodeTable.getColumn("Name") == null) {
					nodeTable.createColumn("Name", String.class, false);
				}

				if (nodeTable.getColumn("Training") == null) {
					nodeTable.createColumn("Training", Boolean.class, false);
				}

				if (nodeTable.getColumn("Ass Genes (Entrez ID)") == null) {
					nodeTable.createColumn("Ass Genes (Entrez ID)", String.class, false);
				}

				if (nodeTable.getColumn("Ass Genes (Symbol)") == null) {
					nodeTable.createColumn("Ass Genes (Symbol)", String.class, false);
				}

				if (nodeTable.getColumn("Ass Protein Complex") == null) {
					nodeTable.createColumn("Ass Protein Complex", String.class, false);
				}

				if (nodeTable.getColumn("Ass Pathway (KEGG ID)") == null) {
					nodeTable.createColumn("Ass Pathway (KEGG ID)", String.class, false);
				}

				if (nodeTable.getColumn("Disease Ontology") == null) {
					nodeTable.createColumn("Disease Ontology", String.class, false);
				}

				networkManager.addNetwork(newnetwork);

				taskMonitor.setStatusMessage("Annotating Ranked Diseases with KEGG pathways...");
				System.out.println("Annotating Ranked Diseases with KEGG pathways...");

				for (CyNode node : selected_diseases) {
					String EIDList = network_disease.getRow(node).get("Associated Genes", String.class);
					String DiseaseID = network_disease.getRow(node).get("Disease ID", String.class);

					CyNode n = newnetwork.addNode();
					CyRow row = nodeTable.getRow(n.getSUID());

					row.set("Rank", network_disease.getRow(node).get("Rank", Integer.class));
					row.set("Disease ID", DiseaseID);
					row.set("Name", network_disease.getRow(node).get("Name", String.class));
					row.set("Training", network_disease.getRow(node).get("Training", Boolean.class));
					row.set("Ass Genes (Entrez ID)", EIDList);

					taskMonitor.setStatusMessage("Annotating " + DiseaseID + " with KEGG pathways");
					// System.out.println("Annotating " + DiseaseID + " with
					// KEGG pathways");

					if (EIDList.trim().equals(""))
						continue;
					String[] EIDs = EIDList.split(",");
					ArrayList<String> SymbolArr = new ArrayList<String>();

					for (int i = 0; i < EIDs.length; i++) {
						for (int j = 0; j < BasicData.AllEntrezID_OfficialSymbol.size(); j++) {
							String sym = "";
							String EID = EIDs[i].trim();
							if (BasicData.AllEntrezID_OfficialSymbol.containsKey(EID)) {
								sym = BasicData.AllEntrezID_OfficialSymbol.get(EID);
								SymbolArr.add(sym);
							}
							break;
						}
					}

					if (interrupted == true)
						return;
					String AssoGeneSymbol = SymbolArr.toString().substring(1, SymbolArr.toString().length() - 1);
					row.set("Ass Genes (Symbol)", AssoGeneSymbol);

					Set<String> PathwayIDList = new TreeSet<String>();

					for (int i = 0; i < EIDs.length; i++) {
						String EID = EIDs[i].trim();
						if (BasicData.Gene2Pathways.get(EID) != null) {
							PathwayIDList.addAll(BasicData.Gene2Pathways.get(EID));
						}
					}
					String PathwayIDListStr = PathwayIDList.toString();
					PathwayIDListStr = PathwayIDListStr.substring(1, PathwayIDListStr.length() - 1);

					row.set("Ass Pathway (KEGG ID)", PathwayIDListStr);

					// -------------------------- column Protein Comlexes
					taskMonitor.setStatusMessage("Annotating Ranked Diseases with Protein Complexes...");
					System.out.println("Annotating Ranked Diseases with Protein Complexes...");
					taskMonitor.setStatusMessage("Annotating " + DiseaseID + " with Complexs");
					// System.out.println("Annotating " + DiseaseID + " with
					// Complexs");

					Set<String> ComplexIDList = new TreeSet<String>();
					if (interrupted == true)
						return;

					for (int j = 0; j < EIDs.length; j++) {
						String EID = EIDs[j].trim();
						if (BasicData.Gene2Complexes.get(EID) != null) {
							ComplexIDList.addAll(BasicData.Gene2Complexes.get(EID));
						}
					}
					String ComplexIDListStr = ComplexIDList.toString();
					ComplexIDListStr = ComplexIDListStr.substring(1, ComplexIDListStr.length() - 1);

					row.set("Ass Protein Complex", ComplexIDListStr);

					// ------------------------- column Disease Ontology------
					taskMonitor.setStatusMessage("Annotating Ranked Diseases with Disease Ontologies...");
					System.out.println("Annotating Ranked Diseases with Disease Ontologies...");

					if (interrupted == true)
						return;
					Set<String> DOIDList = new TreeSet<String>();

					for (int j = 0; j < EIDs.length; j++) {
						String EID = EIDs[j].trim();
						if (BasicData.Gene2DOs.get(EID) != null) {
							DOIDList.addAll(BasicData.Gene2DOs.get(EID));
						}
					}
					String DOIDListStr = DOIDList.toString();
					DOIDListStr = DOIDListStr.substring(1, DOIDListStr.length() - 1);

					row.set("Disease Ontology", DOIDListStr);

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			taskMonitor.setProgress(100);
		}
	}

}
