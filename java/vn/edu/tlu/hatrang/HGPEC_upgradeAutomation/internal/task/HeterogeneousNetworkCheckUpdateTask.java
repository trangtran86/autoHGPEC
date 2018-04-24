/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.BasicData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.Common;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.MainData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.UserData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Interaction;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Node;



/**
 *
 * @author "MinhDA"
 */
public class HeterogeneousNetworkCheckUpdateTask implements Task{

    
    private CyNetworkManager cyNetworkManager;
    private volatile boolean interrupted = false;
    public static CyNetwork curNet;

    public HeterogeneousNetworkCheckUpdateTask(CyNetworkManager cyNetworkManager) {
        this.cyNetworkManager = cyNetworkManager;
    }

    
    @Override
    public void run(TaskMonitor taskMonitor) {
        taskMonitor.setTitle("Gene/Protein and Disease Network Checking and Updating");
        try {
            int i;
            
            taskMonitor.setStatusMessage("Gene Network is being read...!");
            System.out.println("Gene Network is being read...!");

            //****************Load current gene network
            BasicData.GeneNetwork=new ArrayList<Interaction>();
            
            Interaction inatemp;
            
            double weight=0.0;
            curNet = Common.getNetworkByName(cyNetworkManager, String.valueOf(MainData.curNetID));
            List<CyEdge> el = curNet.getEdgeList();
            CyTable nodeTable = curNet.getDefaultNodeTable();
            for(CyEdge edge:el){
                if(this.interrupted==true) break;
                inatemp = new Interaction();
                inatemp.NodeSrc = nodeTable.getRow(edge.getSource().getSUID()).getRaw("Id").toString();
                inatemp.NodeDst = nodeTable.getRow(edge.getTarget().getSUID()).getRaw("Id").toString();
                //????
//                if(PnlKnownGeneProvisionHGPEC.chkWeitghted.isSelected()){
//                    try{
//                        weight = Double.parseDouble(curNet.getDefaultEdgeTable().getRow(edge.getSUID()).getRaw("interaction").toString());
////                        weight = Double.parseDouble(Cytoscape.getEdgeAttributes().getStringAttribute(el.get(e).getIdentifier(), "interaction"));
//                        if(Double.isNaN(weight)){
//                            weight=0;
//                        }
//                    }catch(Exception ex){
//                        weight = 0;
//                    }
//                }else{
//                    weight = 1;
//                }
                
                try{
                  weight = Double.parseDouble(curNet.getDefaultEdgeTable().getRow(edge.getSUID()).getRaw("interaction").toString());
//                  weight = Double.parseDouble(Cytoscape.getEdgeAttributes().getStringAttribute(el.get(e).getIdentifier(), "interaction"));
                  if(Double.isNaN(weight)){
                      weight=0;
                  }
              }catch(Exception ex){
                  weight = 0;
              }
                inatemp.Weight=weight;
                inatemp.WeightOriginal=weight;
                BasicData.GeneNetwork.add(inatemp);//Has not been checked whether dupplication are occured
            }
            
            List<CyNode> nl = curNet.getNodeList();
            BasicData.NetworkGeneSet = new TreeSet<String>();

            for(CyNode node: nl){
                BasicData.NetworkGeneSet.add(nodeTable.getRow(node.getSUID()).getRaw("Id").toString());
            }
            System.out.println("BasicData.NetworkGeneSet.size(): " + BasicData.NetworkGeneSet.size());

            ////****************Update for GeneNetworkNode
            taskMonitor.setStatusMessage("Gene Network is being updated...!");
            System.out.println("Gene Network is being updated...!");

            //Find UpdatedGeneNetwork
            
            BasicData.UpdatedGeneNetworkNode = new TreeMap<String, Node>();
            
            //Store all genes in the network that are not involved in AllGene database
            UserData.MissingNetworkGenes = new ArrayList<String>();

//            CyAttributes nodeAtt = Cytoscape.getNodeAttributes();
//            int n = 1;
            
            for(String nodeid: BasicData.NetworkGeneSet){
                
//                taskMonitor.setStatusMessage("Updating information for gene " + nodeid + " (" + (n) + "/" + nl.size() + ")...!");
                if(this.interrupted==true) break;

                Node g= new Node();
                
                g.Type = "Gene/Protein";
                g.NetworkID=nodeid;
                g.EntrezID = nodeid;
                if(BasicData.AllGene_EntrezIDIndex.containsKey(g.EntrezID)){
                    g.OfficialSymbol = BasicData.AllGene_EntrezIDIndex.get(g.EntrezID).OfficialSymbol;
                    g.UniProtAC = BasicData.AllGene_EntrezIDIndex.get(g.EntrezID).UniProtAC;
                    g.AlternateSymbols = BasicData.AllGene_EntrezIDIndex.get(g.EntrezID).AlternateSymbols;
                    g.Organism = BasicData.AllGene_EntrezIDIndex.get(g.EntrezID).Organism;
                }else{
                    g.OfficialSymbol = "";
                    g.UniProtAC = "";
                    g.AlternateSymbols = new ArrayList<String>();
                    g.Organism = "";
                    
                    UserData.MissingNetworkGenes.add(g.EntrezID);
                }
                BasicData.UpdatedGeneNetworkNode.put(g.NetworkID, g);
                BasicData.UpdatedGeneNetworkNode_OfficialSymbolIndex.put(g.OfficialSymbol, g);
                
                CyNode node = Common.getNodeById(nodeid);
                CyRow row = nodeTable.getRow(node.getSUID());
                row.set("EntrezID", g.EntrezID);
                row.set("OfficialSymbol", g.OfficialSymbol);
                row.set("AlternateSymbols", g.AlternateSymbols.toString().substring(1, g.AlternateSymbols.toString().length()-1));
                row.set("Type", "Gene/Protein");
            }
            
            for(CyEdge edge : el){
                curNet.getDefaultEdgeTable().getRow(edge.getSUID()).set("NodeSrc", edge.getSource().getSUID());
                curNet.getDefaultEdgeTable().getRow(edge.getSUID()).set("NodeDst", edge.getTarget().getSUID());
            }
            System.out.println("Number of gene network nodes: " + BasicData.UpdatedGeneNetworkNode.size());
            //Store missing genes to file
//            try{
//                PrintWriter pw = new PrintWriter(new FileOutputStream("_MissingNetworkGenes.txt"),false);
//                for(i=0;i<UserData.MissingNetworkGenes.size();i++){
//                    pw.println(UserData.MissingNetworkGenes.get(i));
//                }
//                pw.close();
//                System.out.println("Number of missing genes: " + UserData.MissingNetworkGenes.size());
//            }catch(Exception ex){
//                ex.printStackTrace();
//            }

            curNet.getDefaultNetworkTable().getRow(curNet.getSUID()).set("Status", "Updated");
            //Cytoscape.getNetworkAttributes().setAttribute(MainData.curNetID, "Status", "Updated");

            //****************Update for PhenotypeNetworkNode
            taskMonitor.setStatusMessage("Heterogeneous Network is being created...!");
            System.out.println("Heterogeneous Network is being updated...!");
            

            System.out.println("BasicData.NetworkGeneSet.size(): " + BasicData.NetworkGeneSet.size());
            System.out.println("BasicData.NetworkPhenotypeSet.size(): " + BasicData.NetworkPhenotypeSet.size());
            BasicData.Mim2GeneNetwork = new ArrayList<Interaction>();
            BasicData.Gene2MimNetwork = new ArrayList<Interaction>();
            for(Map.Entry<String, Node> e1: BasicData.UpdatedPhenotypeNetworkNode.entrySet()){
                String DiseaseID = e1.getKey();
                for(i=0;i<e1.getValue().AlternateSymbols.size();i++){
                    String GeneID = e1.getValue().AlternateSymbols.get(i);
                    if(BasicData.NetworkGeneSet.contains(GeneID)){
                        BasicData.Mim2GeneNetwork.add(new Interaction(DiseaseID, GeneID, 1.0, 1.0));
                        BasicData.Gene2MimNetwork.add(new Interaction(GeneID, DiseaseID, 1.0, 1.0));
                    }
                }
                
            }
        
            System.out.println("BasicData.Mim2GeneNetwork.size(): " + BasicData.Mim2GeneNetwork.size());
            System.out.println("BasicData.Gene2MimNetwork.size(): " + BasicData.Gene2MimNetwork.size());
            

            for (i = 0; i < BasicData.AllGeneChromosome.size(); i++) {
                Node g = BasicData.AllGeneChromosome.get(i);
                if (BasicData.UpdatedGeneNetworkNode.containsKey(g.EntrezID)) {
                    BasicData.AllGeneChromosome.get(i).IsInNetwork = true;
                } else {
                    BasicData.AllGeneChromosome.get(i).IsInNetwork = false;
                }
            }

           
        }catch(Exception e){
            System.out.println("Error while analyzing network: " + e.toString());
            e.printStackTrace();
            interrupted = true;
        }
    }

    @Override
    public void cancel() {
        this.interrupted = true;
    }
    
}
