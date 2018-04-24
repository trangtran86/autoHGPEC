/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.JOptionPane;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.BasicData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.Main;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.MainData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Interaction;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.NodeInteraction;



/**
 *
 * @author suvl_000
 */
public class HeterogeneousNetworkNormalizationAnalysisTask implements Task {

    private boolean interrupted = false;

    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        taskMonitor.setTitle("Heterogeneous Network Analysis and Normalization");
        taskMonitor.setProgress(0.1);
        try {
            int i, j, k, l;

            taskMonitor.setStatusMessage("Heterogeneous Network is being analyzed...!");
            System.out.println("Heterogeneous Network is being analyzed...!");

            System.out.println("BasicData.NetworkGeneSet.size(): " + BasicData.NetworkGeneSet.size());
            System.out.println("BasicData.GeneNetwork.size(): " + BasicData.GeneNetwork.size());

            System.out.println("BasicData.NetworkPhenotypeSet.size(): " + BasicData.NetworkPhenotypeSet.size());
            System.out.println("BasicData.PhenotypeNetwork.size(): " + BasicData.PhenotypeNetwork.size());

            //Supply missing node, Mim
            Set<String> ValidNodeSet = new TreeSet<String>();
            Set<String> ValidMimSet = new TreeSet<String>();
            for (i = 0; i < BasicData.Mim2GeneNetwork.size(); i++) {
                ValidMimSet.add(BasicData.Mim2GeneNetwork.get(i).NodeSrc);
                ValidNodeSet.add(BasicData.Mim2GeneNetwork.get(i).NodeDst);
            }

            Set<String> SuppliedMimSet = new TreeSet<String>();
            Set<String> SuppliedNodeSet = new TreeSet<String>();
            SuppliedMimSet.addAll(BasicData.NetworkPhenotypeSet);
            SuppliedMimSet.removeAll(ValidMimSet);
            SuppliedNodeSet.addAll(BasicData.NetworkGeneSet);
            SuppliedNodeSet.removeAll(ValidNodeSet);
            System.out.println("ValidNodeSet.size(): " + ValidNodeSet.size());
            System.out.println("ValidMimSet.size(): " + ValidMimSet.size());
            System.out.println("SuppliedNodeSet.size(): " + SuppliedNodeSet.size());
            System.out.println("SuppliedMimSet.size(): " + SuppliedMimSet.size());

            boolean directed = false;

            //******* Calculate M_GP (matrix B)
            ArrayList<Interaction> NormalizedNode2MimNetwork = new ArrayList<Interaction>();
            Map<String, ArrayList<NodeInteraction>> OutgoingNetworkNode2MimTable = Main.calculateOutgoingNeighbors(BasicData.Gene2MimNetwork);

            System.out.println("OutgoingNetworkNode2MimTable.size(): " + OutgoingNetworkNode2MimTable.size());
            //System.out.println("OutgoingNetworkNode2MimTable.keySet().toString(): " + OutgoingNetworkNode2MimTable.keySet().toString());

            for (String node : SuppliedNodeSet) {
                OutgoingNetworkNode2MimTable.put(node, new ArrayList<NodeInteraction>());
            }

            Map<String, Double> NodeTotalOutWeight = new TreeMap<String, Double>();

            for (Map.Entry<String, ArrayList<NodeInteraction>> e : OutgoingNetworkNode2MimTable.entrySet()) {
                double totaloutweight = 0.0;
                ArrayList<NodeInteraction> OutNodeInteractionList = e.getValue();
                for (i = 0; i < OutNodeInteractionList.size(); i++) {
                    totaloutweight += OutNodeInteractionList.get(i).Weight;
                }

                NodeTotalOutWeight.put(e.getKey(), totaloutweight);

                for (i = 0; i < OutNodeInteractionList.size(); i++) {
                    Interaction ina = new Interaction();
                    ina.NodeSrc = e.getKey();
                    ina.NodeDst = OutNodeInteractionList.get(i).Node;
                    if (totaloutweight > 0) {
                        ina.Weight = Main.lambda * OutNodeInteractionList.get(i).Weight / totaloutweight;
                    } else {
                        ina.Weight = 0;
                    }
                    //System.out.println(ina.NodeSrc + "\t" + ina.Weight + "\t" + ina.NodeDst);
                    NormalizedNode2MimNetwork.add(ina);
                }
            }

            //******* Calculate M_G (matrix AG)
            ArrayList<Interaction> DirectedNodeNetwork = new ArrayList<Interaction>();
            if (!directed) {
                for (i = 0; i < BasicData.GeneNetwork.size(); i++) {
                    Interaction ina = new Interaction();
                    ina.NodeSrc = BasicData.GeneNetwork.get(i).NodeDst;
                    ina.NodeDst = BasicData.GeneNetwork.get(i).NodeSrc;
                    ina.WeightOriginal = BasicData.GeneNetwork.get(i).WeightOriginal;
                    ina.Weight = BasicData.GeneNetwork.get(i).Weight;
                    ina.Type = BasicData.GeneNetwork.get(i).Type;
                    DirectedNodeNetwork.add(BasicData.GeneNetwork.get(i));
                    DirectedNodeNetwork.add(ina);
                }
            }
            
            System.out.println("DirectedNodeNetwork.size(): " + DirectedNodeNetwork.size());
           
            ArrayList<Interaction> NormalizedNodeNetwork = new ArrayList<Interaction>();
            Map<String, ArrayList<NodeInteraction>> OutgoingNetworkNodeTable = Main.calculateOutgoingNeighbors(DirectedNodeNetwork);

            System.out.println("OutgoingNetworkNodeTable.size(): " + OutgoingNetworkNodeTable.size());
            for (Map.Entry<String, ArrayList<NodeInteraction>> e : OutgoingNetworkNodeTable.entrySet()) {
                double totaloutweight = 0.0;
                ArrayList<NodeInteraction> OutNodeInteractionList = e.getValue();
                for (i = 0; i < OutNodeInteractionList.size(); i++) {
                    totaloutweight += OutNodeInteractionList.get(i).Weight;
                }

                for (i = 0; i < OutNodeInteractionList.size(); i++) {
                    Interaction ina = new Interaction();
                    ina.NodeSrc = e.getKey();
                    ina.NodeDst = OutNodeInteractionList.get(i).Node;
                    //System.out.println("e.getKey(): " + e.getKey());
                    if (NodeTotalOutWeight.get(e.getKey()) > 0) {
                        ina.Weight = (1 - Main.lambda) * OutNodeInteractionList.get(i).Weight / totaloutweight;
                    } else {
                        ina.Weight = OutNodeInteractionList.get(i).Weight / totaloutweight;
                    }
                    //System.out.println(ina.NodeSrc + "\t" + ina.Weight + "\t" + ina.NodeDst);
                    NormalizedNodeNetwork.add(ina);
                }
            }

            //******* Calculate M_PG (matrix BT)
            ArrayList<Interaction> NormalizedMim2NodeNetwork = new ArrayList<Interaction>();
            Map<String, ArrayList<NodeInteraction>> OutgoingNetworkMim2NodeTable = Main.calculateOutgoingNeighbors(BasicData.Mim2GeneNetwork);

            System.out.println("OutgoingNetworkMim2NodeTable.size(): " + OutgoingNetworkMim2NodeTable.size());
            //System.out.println("OutgoingNetworkMim2NodeTable.keySet().toString(): " + OutgoingNetworkMim2NodeTable.keySet().toString());

            for (String mim : SuppliedMimSet) {
                OutgoingNetworkMim2NodeTable.put(mim, new ArrayList<NodeInteraction>());
            }

            Map<String, Double> MimTotalOutWeight = new TreeMap<String, Double>();
            for (Map.Entry<String, ArrayList<NodeInteraction>> e : OutgoingNetworkMim2NodeTable.entrySet()) {
                double totaloutweight = 0.0;
                ArrayList<NodeInteraction> OutNodeInteractionList = e.getValue();
                for (i = 0; i < OutNodeInteractionList.size(); i++) {
                    totaloutweight += OutNodeInteractionList.get(i).Weight;
                }

                MimTotalOutWeight.put(e.getKey(), totaloutweight);

                for (i = 0; i < OutNodeInteractionList.size(); i++) {
                    Interaction ina = new Interaction();
                    ina.NodeSrc = e.getKey();
                    ina.NodeDst = OutNodeInteractionList.get(i).Node;
                    if (totaloutweight > 0) {
                        ina.Weight = Main.lambda * OutNodeInteractionList.get(i).Weight / totaloutweight;
                    } else {
                        ina.Weight = 0;
                    }
                    //System.out.println(ina.NodeSrc + "\t" + ina.Weight + "\t" + ina.NodeDst);
                    NormalizedMim2NodeNetwork.add(ina);
                }
            }

            //******* Calculate M_P (matrix AP)
            ArrayList<Interaction> DirectedMimNetwork = new ArrayList<Interaction>();
            if (!directed) {
                for (i = 0; i < BasicData.PhenotypeNetwork.size(); i++) {
                    Interaction ina = new Interaction();
                    ina.NodeSrc = BasicData.PhenotypeNetwork.get(i).NodeDst;
                    ina.NodeDst = BasicData.PhenotypeNetwork.get(i).NodeSrc;
                    ina.WeightOriginal = BasicData.PhenotypeNetwork.get(i).WeightOriginal;
                    ina.Weight = BasicData.PhenotypeNetwork.get(i).Weight;
                    ina.Type = BasicData.PhenotypeNetwork.get(i).Type;
                    DirectedMimNetwork.add(BasicData.PhenotypeNetwork.get(i));
                    DirectedMimNetwork.add(ina);
                }
            }
            System.out.println("DirectedMimNetwork.size(): " + DirectedMimNetwork.size());

            ArrayList<Interaction> NormalizedMimNetwork = new ArrayList<Interaction>();
            Map<String, ArrayList<NodeInteraction>> OutgoingNetworkMimTable = Main.calculateOutgoingNeighbors(DirectedMimNetwork);

            System.out.println("OutgoingNetworkMimTable.size(): " + OutgoingNetworkMimTable.size());
            for (Map.Entry<String, ArrayList<NodeInteraction>> e : OutgoingNetworkMimTable.entrySet()) {
                double totaloutweight = 0.0;
                ArrayList<NodeInteraction> OutNodeInteractionList = e.getValue();
                for (i = 0; i < OutNodeInteractionList.size(); i++) {
                    totaloutweight += OutNodeInteractionList.get(i).Weight;
                }

                for (i = 0; i < OutNodeInteractionList.size(); i++) {
                    Interaction ina = new Interaction();
                    ina.NodeSrc = e.getKey();
                    ina.NodeDst = OutNodeInteractionList.get(i).Node;
                    if (MimTotalOutWeight.get(e.getKey()) > 0) {
                        ina.Weight = (1 - Main.lambda) * OutNodeInteractionList.get(i).Weight / totaloutweight;
                    } else {
                        ina.Weight = OutNodeInteractionList.get(i).Weight / totaloutweight;
                    }
                    //System.out.println(ina.NodeSrc + "\t" + ina.Weight + "\t" + ina.NodeDst);
                    NormalizedMimNetwork.add(ina);
                }
            }

            ArrayList<Interaction> NormalizedHeterogeneousNetwork = new ArrayList<Interaction>();
            for (i = 0; i < NormalizedNodeNetwork.size(); i++) {
                NormalizedHeterogeneousNetwork.add(NormalizedNodeNetwork.get(i));
                //if(NormalizedNodeNetwork.get(i).Weight>0) System.out.println(NormalizedNodeNetwork.get(i).NodeSrc + "\t" + NormalizedNodeNetwork.get(i).Weight + "\t" + NormalizedNodeNetwork.get(i).NodeDst);
            }
            for (i = 0; i < NormalizedNode2MimNetwork.size(); i++) {
                NormalizedHeterogeneousNetwork.add(NormalizedNode2MimNetwork.get(i));
                //if(NormalizedNode2MimNetwork.get(i).Weight>0) System.out.println(NormalizedNode2MimNetwork.get(i).NodeSrc + "\t" + NormalizedNode2MimNetwork.get(i).Weight + "\t" + NormalizedNode2MimNetwork.get(i).NodeDst);
            }
            for (i = 0; i < NormalizedMim2NodeNetwork.size(); i++) {
                NormalizedHeterogeneousNetwork.add(NormalizedMim2NodeNetwork.get(i));
                //if(NormalizedMim2NodeNetwork.get(i).Weight>0) System.out.println(NormalizedMim2NodeNetwork.get(i).NodeSrc + "\t" + NormalizedMim2NodeNetwork.get(i).Weight + "\t" + NormalizedMim2NodeNetwork.get(i).NodeDst);
            }
            for (i = 0; i < NormalizedMimNetwork.size(); i++) {
                NormalizedHeterogeneousNetwork.add(NormalizedMimNetwork.get(i));
                //if(NormalizedMimNetwork.get(i).Weight>0) System.out.println(NormalizedMimNetwork.get(i).NodeSrc + "\t" + NormalizedMimNetwork.get(i).Weight + "\t" + NormalizedMimNetwork.get(i).NodeDst);
            }

            MainData.IncomingEntityTable = new TreeMap<String, ArrayList<NodeInteraction>>();
            MainData.IncomingEntityTable = Main.calculateIncomingNeighbors(NormalizedHeterogeneousNetwork);

            System.out.println("MainData.IncomingEntityTable.size(): " + MainData.IncomingEntityTable.size());

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while analyzing Heterogeneous Network: " + e.toString());

        }
        taskMonitor.setProgress(0.1);
    }

    @Override
    public void cancel() {
        this.interrupted = true;
    }

}
