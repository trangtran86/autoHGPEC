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

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.BasicData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.Common;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.Main;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.MainData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.Scoring;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.UserData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Node;



/**
 *
 * @author suvl_000
 */
public class PrioritizationTask implements Task{
    private boolean interrupted = false;
    
    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        taskMonitor.setTitle("Gene/Protein and Disease Prioritization");
        taskMonitor.setProgress(0.1);
        try {
            int numofpriors=0;
          
            int i,j;
            
            taskMonitor.setStatusMessage("Prioritizing all nodes in the Heterogeneous Network");
            if(this.interrupted==true) return;
            
            String DiseaseOfInterest = UserData.term;
            
            Map<String, Double> Priors = new TreeMap<String, Double>();
            Map<String, Double> NodePriors = new TreeMap<String, Double>();
            Map<String, Double> MimPriors = new TreeMap<String, Double>();
            

            System.out.println("Number of seed nodes: " + MainData.AllTrainingGenes.size());

            Set<String> TrainingGeneSet = new TreeSet<String>();
            for(i=0;i<MainData.AllTrainingGenes.size();i++){
                NodePriors.put(MainData.AllTrainingGenes.get(i).NetworkID, (1-Main.eta)*1/MainData.AllTrainingGenes.size());
                TrainingGeneSet.add(MainData.AllTrainingGenes.get(i).NetworkID);
            }
            Set<String> TrainingPhenotypeSet = new TreeSet<String>();
            for(i=0;i<MainData.AllTrainingPhenotypes.size();i++){
                MimPriors.put(MainData.AllTrainingPhenotypes.get(i).NetworkID,Main.eta*1/MainData.AllTrainingPhenotypes.size());
                TrainingPhenotypeSet.add(MainData.AllTrainingPhenotypes.get(i).NetworkID);
            }
            Priors.putAll(NodePriors);
            Priors.putAll(MimPriors);

          
            Map<String, Double> NodeScore = Scoring.rankByRWRH(MainData.IncomingEntityTable, Main.alpha, Priors);
            ArrayList<Node> NodeScoreArray = new ArrayList<Node>();
            for(Map.Entry<String, Double> e: NodeScore.entrySet()){
                Node g = new Node();
                g.NetworkID = e.getKey();
                g.Score = e.getValue();
                NodeScoreArray.add(g);
            }
            Common.sortQuickNodeListInDescScore(NodeScoreArray);
            
            Map<String, Integer> NodeRank = new TreeMap<String, Integer>();
            for(i=0;i<NodeScoreArray.size();i++){
                //System.out.println(GeneScore.get(i).NodeID + "\t" + GeneScore.get(i).Score);
                NodeRank.put(NodeScoreArray.get(i).NetworkID, i+1);
            }

//            PrintWriter pw = new PrintWriter(new FileOutputStream("Results\\Ranking_" + DiseaseOfInterest + ".txt"), true);
            System.out.println("NodeRank.size(): " + NodeRank.size());
            Map<Integer, String> RankNode = new TreeMap<Integer, String>();
            for(Map.Entry<String, Integer> e: NodeRank.entrySet()){
                RankNode.put(e.getValue(),e.getKey());
//                if(Priors.containsKey(e.getKey())){
//                    //System.out.println(e.getKey() + "\t" + e.getValue() + "\tTraining");
//                    pw.println(e.getValue() + "\t" + e.getKey() + "\t" + "Training");
//                }else{
//                    //System.out.println(e.getKey() + "\t" + e.getValue() + "\tUnlabeled");
//                    pw.println(e.getValue() + "\t" + e.getKey() + "\t" +  "Unlabeled");
//                }
            }
//            pw.close();


            MainData.PrioritizationScore = new ArrayList<Node>();
            
            
            //Set Candidate Genes in Graph
            Set<String> TestGeneSet = new TreeSet<String>();
            for(i=0;i<MainData.AllTestGenes.size();i++){
                String GeneID=MainData.AllTestGenes.get(i).NetworkID;
                TestGeneSet.add(GeneID);
            }
            
            for(Map.Entry<Integer, String> e: RankNode.entrySet()){
                Node n = new Node();
                String NodeID = e.getValue();
                
                if(BasicData.UpdatedGeneNetworkNode.containsKey(NodeID)){
                    n = BasicData.UpdatedGeneNetworkNode.get(NodeID);
                    n.Type = "Gene/Protein";
                }
                if(BasicData.UpdatedPhenotypeNetworkNode.containsKey(NodeID)){
                    n = BasicData.UpdatedPhenotypeNetworkNode.get(NodeID);
                    n.Type = "Disease";
                    //n.OfficialSymbol = n.Name;
                }
                if(TrainingGeneSet.contains(NodeID) || TrainingPhenotypeSet.contains(NodeID)){
                    n.IsSeed = true;
                }else{
                    n.IsSeed = false;
                }
                if(TestGeneSet.contains(NodeID) || (n.Type.compareToIgnoreCase("Disease")==0 && !TrainingPhenotypeSet.contains(NodeID))){
                    //n.OfficialSymbol = n.Name;
                    n.IsTest = true;
                }else{
                    n.IsTest = false;
                }
                n.Rank = e.getKey();
                n.Score = NodeScore.get(NodeID);
                MainData.PrioritizationScore.add(n.Copy());
            }
            
        }catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error when running Prioritization Task: " + e.toString());
            
        }
        taskMonitor.setProgress(0.1);
    }

    @Override
    public void cancel() {
        this.interrupted = true;
    }
    
}
