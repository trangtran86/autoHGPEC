package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.JOptionPane;

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Node;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.NodeInteraction;




/**
 *
 * @author suvl_000
 */
public class Scoring {

    public static int storeScores(ArrayList<Node> mn, String filename) {
        int HeldoutGeneRank = 0;
        try {

//            PrintWriter pw = new PrintWriter(new FileOutputStream(filename),false);
            int TestGeneRank = 0;
            for (int i = 0; i < mn.size(); i++) {
                mn.get(i).Rank = i + 1;
                if (mn.get(i).IsTest) {
                    TestGeneRank++;
                    if (mn.get(i).IsHeldout) {
                        HeldoutGeneRank = TestGeneRank;
                    }
                }
//                pw.println(mn.get(i).Rank + "\t" + mn.get(i).EntrezID + "\t" + mn.get(i).UniProtAC + "\t" + mn.get(i).OfficialSymbol + "\t" + mn.get(i).Score + "\t" + mn.get(i).IsSeed + "\t" + mn.get(i).IsHeldout + "\t" + mn.get(i).IsTest);
            }

//            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return HeldoutGeneRank;
        }
    }

    public static void storeScores1(ArrayList<Node> mn, String filename) {
        int orderInTestSet = 0;
        int HoldoutRank = 0;
        int RandomRank = 0;
        try {
            ArrayList<Node> RunResult = new ArrayList<Node>();

            FileOutputStream fs = new FileOutputStream(filename);
            PrintWriter pw = new PrintWriter(fs, false);

            Common.sortQuickNodeListInDescScore(mn);

            //System.out.println("Random Gene: " + RandomUnknownDiseaseGene.id);
            for (int i = 0; i < mn.size(); i++) {

                pw.println((i + 1) + "\t" + mn.get(i).OfficialSymbol + "\t" + mn.get(i).Score + "\t" + mn.get(i).IsSeed + "\t" + mn.get(i).IsHeldout + "\t" + mn.get(i).IsTest);

                RunResult.add(mn.get(i));

            }
            MainData.myROC.HoldoutRanks.add(HoldoutRank);
            MainData.AllRuns.add(RunResult);

            //myROC.RandomRanks.add(RandomRank);
            //pw.println("Total node: " + mn.size());
            pw.close();
            fs.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Map<String, Double> rankByRWRH(Map<String, ArrayList<NodeInteraction>> IncomingNodeTable, double alpha, Map<String, Double> Priors) {

        int i, j, k;
//            PrintWriter pw = new PrintWriter(new FileOutputStream(fileName.replace(".txt", "_LoopScore.txt")),false);
        Set<String> NodeSet = IncomingNodeTable.keySet();
        Map<String, Double> priorsl = new TreeMap<String, Double>();

        //System.out.println("Before");
        for (String n : NodeSet) {
            if (Priors.containsKey(n)) {
                priorsl.put(n, Priors.get(n));
            } else {
                priorsl.put(n, 0.0);
            }
        }
//            System.out.println("priorsl");
//            for(Entry<String, Double> e: priorsl.entrySet()){
//                System.out.println(e.getKey() + "\t" + e.getValue());
//            }

        Map<String, Double> probnext0 = new TreeMap<String, Double>();
        Map<String, Double> probnext1 = new TreeMap<String, Double>();

        probnext0 = priorsl;
//            System.out.println("probnext0");
//            for(Entry<String, Double> e: probnext0.entrySet()){
//                System.out.println(e.getKey() + "\t" + e.getValue());
//            }

        int it = 0;

        while (true) {
            probnext1 = new TreeMap<String, Double>();
            double normL2 = 0.0;
            //System.out.println("Iteration #" + it);

            for (String n : NodeSet) {
                double temp = 0.0;
                ArrayList<NodeInteraction> InNodeList = IncomingNodeTable.get(n);
                //System.out.println(n + "<--" + InNodeList.size());
                for (k = 0; k < InNodeList.size(); k++) {
                    //System.out.println("\t" + n + " <-- " + InNodeList.get(k).Weight + " <-- " + InNodeList.get(k).Node);
                    temp += InNodeList.get(k).Weight * probnext0.get(InNodeList.get(k).Node);
                }
                //System.out.println("\ttemp: " + temp);
                probnext1.put(n, (1 - alpha) * temp + alpha * priorsl.get(n));

                //System.out.println("\t" + n + "\t" + probnext1.get(n));
            }
            //alpha-=0.1;                
            normL2 = Common.normL2(probnext0, probnext1);
            //System.out.println("Iteration #" + it + "\t--> Norm L2: " + normL2);

            //if(normL2<Math.pow(10.0, -6.0)||alpha<0) break;
            if (normL2 < Math.pow(10.0, -6.0) || it >= 10) {
                break;
            }
            probnext0 = probnext1;

            it++;

        }

        return probnext1;

    }

    public static int rankbyRWRH_Old(ArrayList<Node> NodeScore, ArrayList<ArrayList<Double>> InEdgeWeights, ArrayList<ArrayList<Integer>> InNodeIndices, double alpha, String fileName) {
        int HeldoutGeneRank = 0;
        try {
            int i, j, k;
//            PrintWriter pw = new PrintWriter(new FileOutputStream(fileName.replace(".txt", "_LoopScore.txt")),false);

            ArrayList<Double> priorsl = new ArrayList<Double>();

            //prior probabilities
            for (i = 0; i < NodeScore.size(); i++) {
                priorsl.add(NodeScore.get(i).Score);
            }

            System.out.println("Ranking by using Random Walk With Restart...");

            ArrayList<Double> probnext0 = new ArrayList<Double>();
            ArrayList<Double> probnext1 = new ArrayList<Double>();
            probnext0 = priorsl;

            int it = 0;

            while (true) {

//                for(i=0;i<probnext0.size();i++){
//                    System.out.println(NodeScore.get(i).OfficialSymbol + "\t" + probnext0.get(i));
//                }
//                System.out.println();
                probnext1 = new ArrayList<Double>();
                double normL2 = 0.0;
                //System.out.println("Iteration #" + it + ", Norm L2: " + normL2);
                //Traverse each row
                for (i = 0; i < BasicData.UpdatedGeneNetworkNode.size(); i++) {
                    double temp = 0.0;
                    for (k = 0; k < InNodeIndices.get(i).size(); k++) {
                        //System.out.println(OutNodeIndices.get(i).get(k) + ", " + OutEdgeWeights.get(i).get(k) + ", " + probnext0.get(OutNodeIndices.get(i).get(k)));
                        temp += InEdgeWeights.get(i).get(k) * probnext0.get(InNodeIndices.get(i).get(k));
                    }
                    probnext1.add(i, (1 - alpha) * temp + alpha * priorsl.get(i));

                    //System.out.println("V " + i + ": " + temp + ", " + probnext1.get(i));
                    //System.out.println(probnext1.get(i));
                }

                normL2 = Common.normL2(probnext0, probnext1);
                System.out.println("L2 norm at step " + it + ": " + normL2);
                if (normL2 < Math.pow(10.0, -6.0)) {
                    break;
                }
                probnext0 = probnext1;

                it++;
                //System.out.println("Loop: " + it + " normL2: " + normL2);
            }
//            pw.close();
            //JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Hello3");
            for (i = 0; i < NodeScore.size(); i++) {
                NodeScore.get(i).Score = probnext1.get(i);
                //System.out.println(MainData.ValidationScore.get(i).Score);
            }

            Common.sortQuickNodeListInDescScore(NodeScore);
            HeldoutGeneRank = Scoring.storeScores(NodeScore, fileName);
            //System.out.println("Prioritization is finished...");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error when ranking by Random Walk With Restart Method: " + e.toString()
                    + "\n-MainData.PrioritizationScore.size() " + MainData.PrioritizationScore.size()
                    + "\n-OutEdgeWeights.size() " + InEdgeWeights.size()
                    + "\n-OutNodeIndices.size() " + InNodeIndices.size()
            );
        } finally {
            return HeldoutGeneRank;
        }
    }
}
