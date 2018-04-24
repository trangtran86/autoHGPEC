package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.BasicData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.Common;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.MainData;



/**
 *
 * @author suvl_000
 */
public class VisualizeSubNetworkTask implements Task {

    private boolean interrupted = false;
    private CyNetworkFactory cyNetworkFactory;
    private CyNetworkNaming cyNetworkNaming;
    private CyNetworkManager cyNetworkManager;
    private CyLayoutAlgorithmManager layoutManager;
    private TaskManager taskManager;
    private CyNetworkViewFactory cyNetworkViewFactory;
    private CyNetworkViewManager cyNetworkViewManager;
    private VisualMappingManager vmm;
    private VisualStyleFactory visualStyleFactory;
    private VisualMappingFunctionFactory vmfFactoryP;
    private VisualMappingFunctionFactory vmfFactoryD;
    private VisualMappingFunctionFactory vmfFactoryC;
    private HashMap<String, CyNode> nodeIdMap;

    public VisualizeSubNetworkTask(CyNetworkFactory cyNetworkFactory, CyNetworkNaming cyNetworkNaming, CyNetworkManager cyNetworkManager, CyLayoutAlgorithmManager layoutManager,
            TaskManager taskManager, CyNetworkViewFactory cyNetworkViewFactory, CyNetworkViewManager cyNetworkViewManager, VisualMappingManager vmm,
            VisualStyleFactory visualStyleFactory, VisualMappingFunctionFactory vmfFactoryP, VisualMappingFunctionFactory vmfFactoryD, VisualMappingFunctionFactory vmfFactoryC) {
        this.cyNetworkFactory = cyNetworkFactory;
        this.cyNetworkNaming = cyNetworkNaming;
        this.cyNetworkManager = cyNetworkManager;
        this.layoutManager = layoutManager;
        this.taskManager = taskManager;
        this.cyNetworkViewFactory = cyNetworkViewFactory;
        this.cyNetworkViewManager = cyNetworkViewManager;
        this.vmm = vmm;
        this.visualStyleFactory = visualStyleFactory;
        this.vmfFactoryP = vmfFactoryP;
        this.vmfFactoryD = vmfFactoryD;
        this.vmfFactoryC = vmfFactoryC;
        this.nodeIdMap = new HashMap<>();
    }

    @Override
    public void run(TaskMonitor taskMonitor){
        taskMonitor.setTitle("Visualizing Sub-network");
        taskMonitor.setProgress(0.1);
        try {

            System.out.println("Showing network of selected genes and phenotypes from whole network...");
            taskMonitor.setStatusMessage("Showing network of selected genes and phenotypes from whole network...");

            //CyNetwork curNetwork = Cytoscape.getCurrentNetwork();
            CyNetwork separateNetwork = cyNetworkFactory.createNetwork();
            String networkName = "Sub-Network";
            separateNetwork.getRow(separateNetwork).set(CyNetwork.NAME, cyNetworkNaming.getSuggestedNetworkTitle(networkName));
            //node's attributes
            separateNetwork.getDefaultNodeTable().createColumn("EntrezID", String.class, false);
            separateNetwork.getDefaultNodeTable().createColumn("OfficialSymbol", String.class, false);
            separateNetwork.getDefaultNodeTable().createColumn("AlternateSymbols", String.class, false);
            separateNetwork.getDefaultNodeTable().createColumn("Type", String.class, false);
            separateNetwork.getDefaultNodeTable().createColumn("Rank", Integer.class, false);
            separateNetwork.getDefaultNodeTable().createColumn("Score", Double.class, false);
            separateNetwork.getDefaultNodeTable().createColumn("Role", String.class, false);
//            separateNetwork.getDefaultNodeTable().createColumn("Id", String.class, false);

            // edge's attributes
            separateNetwork.getDefaultEdgeTable().createColumn("NodeSrc", Long.class, false);
            separateNetwork.getDefaultEdgeTable().createColumn("NodeDst", Long.class, false);
            
            //network_gene and network_disease
            CyNetwork network_gene=Common.getNetworkByName(cyNetworkManager, "All Ranked Genes");
            CyNetwork network_disease=Common.getNetworkByName(cyNetworkManager, "All Ranked Diseases");
            
            List<CyNode> selected_genes = CyTableUtil.getNodesInState(network_gene, "selected", true);
            List<CyNode> selected_diseases = CyTableUtil.getNodesInState(network_disease, "selected", true);
            int i, j;
            System.out.println(selected_diseases.size());
            System.out.println(selected_genes.size());
            //JOptionPane.showMessageDialog(Cytoscape.getDesktop(), ri.length + ":" + this.tblResult.getSelectedRows().length);
            ArrayList<String> SelectedRankedGenes = new ArrayList<String>();
            Set<String> SelectedRankedGeneSet = new TreeSet<String>();
            for (CyNode node:selected_genes) {
            	String EID = network_gene.getRow(node).get("Entrez Gene ID", String.class);
                SelectedRankedGenes.add(EID);
                
            }

            //Create nodes
            taskMonitor.setStatusMessage("Creating nodes of Gen-Graph...!");
            //curNetwork.setSelectedNodeState(curNetwork.nodesList(), false);

            List<CyNode> nodesList = HeterogeneousNetworkCheckUpdateTask.curNet.getNodeList();

            for (i = 0; i < nodesList.size(); i++) {
                for (j = 0; j < SelectedRankedGenes.size(); j++) {
                    CyRow curNetCyRow = HeterogeneousNetworkCheckUpdateTask.curNet.getDefaultNodeTable().getRow(nodesList.get(i).getSUID());
                    String identifierString = curNetCyRow.getRaw("Id").toString();

                    if (identifierString.compareTo(SelectedRankedGenes.get(j)) == 0) {
                        CyNode aNode = separateNetwork.addNode();
                        CyRow cyRow = separateNetwork.getDefaultNodeTable().getRow(aNode.getSUID());

                        cyRow.set("name", identifierString);
                        cyRow.set("EntrezID", identifierString);
                        cyRow.set("OfficialSymbol", curNetCyRow.getRaw("OfficialSymbol").toString());
                        cyRow.set("AlternateSymbols", curNetCyRow.getRaw("AlternateSymbols").toString());
                        cyRow.set("Type", curNetCyRow.getRaw("Type").toString());
                        cyRow.set("Rank", Integer.parseInt(curNetCyRow.getRaw("Rank").toString()));
                        cyRow.set("Score", Double.parseDouble(curNetCyRow.getRaw("Score").toString()));
                        cyRow.set("Role", curNetCyRow.getRaw("Role").toString());
                        SelectedRankedGeneSet.add(identifierString);
                        this.nodeIdMap.put(identifierString, aNode);
                        break;
                    }
                }
            }

            taskMonitor.setStatusMessage("Creating edges of Gen-Graph...!");
            //Create edges
            List<CyEdge> edgesList = HeterogeneousNetworkCheckUpdateTask.curNet.getEdgeList();
            for (i = 0; i < edgesList.size(); i++) {

                boolean exist1 = false;
                String identifierStringSource = HeterogeneousNetworkCheckUpdateTask.curNet.getDefaultNodeTable().getRow(edgesList.get(i).getSource().getSUID()).getRaw("Id").toString();

                for (j = 0; j < SelectedRankedGenes.size(); j++) {
                    if (identifierStringSource.compareTo(SelectedRankedGenes.get(j)) == 0) {
                        exist1 = true;
                        break;
                    }
                }
                String identifierStringTarget = HeterogeneousNetworkCheckUpdateTask.curNet.getDefaultNodeTable().getRow(edgesList.get(i).getTarget().getSUID()).getRaw("Id").toString();

                boolean exist2 = false;
                for (j = 0; j < SelectedRankedGenes.size(); j++) {
                    if (identifierStringTarget.compareTo(SelectedRankedGenes.get(j)) == 0) {
                        exist2 = true;
                        break;
                    }
                }
                if (exist1 && exist2) {
                    CyNode nodeSource = getNodeByName(identifierStringSource);
                    CyNode nodeTarget = getNodeByName(identifierStringTarget);
                    CyEdge aEdge = separateNetwork.addEdge(nodeSource, nodeTarget, true);
                    CyRow cyRow = separateNetwork.getDefaultEdgeTable().getRow(aEdge.getSUID());
                    cyRow.set("NodeSrc", Long.parseLong(identifierStringSource));
                    cyRow.set("NodeDst", Long.parseLong(identifierStringTarget));
                    cyRow.set("interaction", HeterogeneousNetworkCheckUpdateTask.curNet.getDefaultEdgeTable().getRow(edgesList.get(i).getSUID()).getRaw("interaction").toString());
                }
            }

            //if (VisualizationOptionDialog.optHeterogeneous.isSelected()) {

               // int riP[] = PnlGenePrioritizationHGPEC.tblRankedPhenotypes.getSelectedRows();

                //JOptionPane.showMessageDialog(Cytoscape.getDesktop(), ri.length + ":" + this.tblResult.getSelectedRows().length);
                ArrayList<String> SelectedRankedPhenotypes = new ArrayList<String>();
                Set<String> SelectedRankedPhenotypeSet = new TreeSet<String>();
                for (CyNode node:selected_diseases) {                   
                        SelectedRankedPhenotypes.add(network_disease.getRow(node).get("Disease ID", String.class));
          
                }

                //Create nodes
                //curNetwork.setSelectedNodeState(curNetwork.nodesList(), false);
                CyNetwork cn = Common.getNetworkByName(cyNetworkManager, MainData.curPheNetID);

                if (null != cn) {
                    taskMonitor.setStatusMessage("Creating nodes of Hete Sub-Graph...!");
                    List<CyNode> PhenodesList = cn.getNodeList();
                    for (i = 0; i < PhenodesList.size(); i++) {
                        for (j = 0; j < SelectedRankedPhenotypes.size(); j++) {
                            CyRow curPheNetCyRow = cn.getDefaultNodeTable().getRow(PhenodesList.get(i).getSUID());
                            String identifierString = curPheNetCyRow.getRaw("Id").toString();
                            if (identifierString.compareTo(SelectedRankedPhenotypes.get(j)) == 0) {
                                CyNode aNode = separateNetwork.addNode();
                                CyRow cyRow = separateNetwork.getDefaultNodeTable().getRow(aNode.getSUID());

                                cyRow.set("name", identifierString);
                                cyRow.set("EntrezID", identifierString);
                                cyRow.set("OfficialSymbol", curPheNetCyRow.getRaw("name").toString());
                                cyRow.set("Type", curPheNetCyRow.getRaw("Type").toString());
                                cyRow.set("Rank", Integer.parseInt(curPheNetCyRow.getRaw("Rank").toString()));
                                cyRow.set("Score", Double.parseDouble(curPheNetCyRow.getRaw("Score").toString()));
                                cyRow.set("Role", curPheNetCyRow.getRaw("Role").toString());
                                SelectedRankedPhenotypeSet.add(identifierString);
                                this.nodeIdMap.put(identifierString, aNode);
                                break;
                            }
                        }
                    }
                    //Create edges
                    taskMonitor.setStatusMessage("Creating edges of Hete Sub-Graph...!");
                    List<CyEdge> PheedgesList = cn.getEdgeList();
                    for (i = 0; i < PheedgesList.size(); i++) {
                        boolean exist1 = false;
                        String identifierStringSource = cn.getDefaultNodeTable().getRow(PheedgesList.get(i).getSource().getSUID()).getRaw("Id").toString();

                        for (j = 0; j < SelectedRankedPhenotypes.size(); j++) {
                            if (identifierStringSource.compareTo(SelectedRankedPhenotypes.get(j)) == 0) {
                                exist1 = true;
                                break;
                            }
                        }
                        boolean exist2 = false;
                        String identifierStringTarget = cn.getDefaultNodeTable().getRow(PheedgesList.get(i).getTarget().getSUID()).getRaw("Id").toString();

                        for (j = 0; j < SelectedRankedPhenotypes.size(); j++) {
                            if (identifierStringTarget.compareTo(SelectedRankedPhenotypes.get(j)) == 0) {
                                exist2 = true;
                                break;
                            }
                        }
                        if (exist1 && exist2) {
                            //cyEdgeAtt.setAttribute(PheedgesList.get(i).getIdentifier(), "Link Type", "Disease");
                            CyNode nodeSource = getNodeByName(identifierStringSource);
                            CyNode nodeTarget = getNodeByName(identifierStringTarget);
                            CyEdge aEdge = separateNetwork.addEdge(nodeSource, nodeTarget, true);
                            CyRow cyRow = separateNetwork.getDefaultEdgeTable().getRow(aEdge.getSUID());
//                            cyRow.set("NodeSrc", Long.parseLong(identifierStringSource));
//                            cyRow.set("NodeDst", Long.parseLong(identifierStringTarget));
                            cyRow.set("interaction", cn.getDefaultEdgeTable().getRow(PheedgesList.get(i).getSUID()).getRaw("interaction").toString());
                        }
                    }
                    //Create disease-gene edges
                    taskMonitor.setStatusMessage("Creating disease-gene edges of Hete Sub-Graph...!");
                    int peEdgeId = 0;
                    for (i = 0; i < BasicData.Mim2GeneNetwork.size(); i++) {
                        if (SelectedRankedPhenotypeSet.contains(BasicData.Mim2GeneNetwork.get(i).NodeSrc) && SelectedRankedGeneSet.contains(BasicData.Mim2GeneNetwork.get(i).NodeDst)) {
                            peEdgeId++;
                            CyNode nodeSource = getNodeByName(BasicData.Mim2GeneNetwork.get(i).NodeSrc);
                            CyNode nodeDestination = getNodeByName(BasicData.Mim2GeneNetwork.get(i).NodeDst);

                            //System.out.println("e.getIdentifier(): " + e.getIdentifier());
                            //cyEdgeAtt.setAttribute(e.getIdentifier(), "Link Type", "Disease-Gene");
                            separateNetwork.addEdge(nodeSource, nodeDestination, true);

                        }
                    }
                }
        
            //Add network properties
            separateNetwork.getDefaultNetworkTable().createColumn("Type", String.class, false);
            separateNetwork.getRow(separateNetwork).set("Type", "Sub network");

            cyNetworkManager.addNetwork(separateNetwork);

            CyLayoutAlgorithm layout = layoutManager.getLayout("attributes-layout");//force-directed
            final Collection<CyNetworkView> views = cyNetworkViewManager.getNetworkViews(separateNetwork);
            CyNetworkView PheGenNetworkView = null;
            if (views.size() != 0) {
                PheGenNetworkView = views.iterator().next();
                this.taskManager.execute(layout.createTaskIterator(PheGenNetworkView, layout.createLayoutContext(), CyLayoutAlgorithm.ALL_NODE_VIEWS, "Type"));
            }
            if (PheGenNetworkView == null) {
                // create a new view for my network
                PheGenNetworkView = cyNetworkViewFactory.createNetworkView(separateNetwork);
                // Apply the visual style to a NetwokView
//                this.vs.apply(PheGenNetworkView);
                PheGenNetworkView.updateView();
                this.taskManager.execute(layout.createTaskIterator(PheGenNetworkView, layout.createLayoutContext(), CyLayoutAlgorithm.ALL_NODE_VIEWS, "Type"));
                cyNetworkViewManager.addNetworkView(PheGenNetworkView);

            } else {
                System.out.println("networkView already existed.");
            }

            Common.applyNetworkVisualStyle(separateNetwork, PheGenNetworkView, MainData.vsNetworkName, vmm, visualStyleFactory, vmfFactoryP, vmfFactoryD, vmfFactoryC);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        taskMonitor.setProgress(0.1);
    }

    @Override
    public void cancel() {
        this.interrupted = true;
    }

    private CyNode getNodeByName(String identifierStringSource) {
        return this.nodeIdMap.get(identifierStringSource);
    }

}
