/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.chart.ROC;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Interaction;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Node;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.NodeInteraction;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.OMIM;



/**
 *
 * @author "MinhDA"
 */
public class MainData {
    public static ArrayList<Node> AllKnownGenes;
    public static ArrayList<Node> AllKnownGenes_OMIM;
    public static ArrayList<Node> AllKnownGenes_Chromosome;
    public static ArrayList<Node> AllKnownGenes_KEGG;
    public static ArrayList<Node> AllKnownGenes_GO;
    public static ArrayList<Node> AllKnownGenes_Others;
    
    public static ArrayList<OMIM> AllOMIMRecords;
    public static ArrayList<Node> MatchedGenes;

    public static ArrayList<Node> AllTrainingGenes;//All Known Disease Genes in Network
    public static Set<String> TrainingGeneSet = new TreeSet<String>();//All Known Disease Genes in Network
    public static ArrayList<ArrayList<Node>> LinkageIntervalGenes;

    //Find Seed Genes for each trial (In each trial, one training gene is held out and the remaining set is training genes
    public static ArrayList<ArrayList<Node>> AllSeedGenes; //Contains Seed Genes only
    public static ArrayList<Node> SeedGenes; //Contains Seed Genes for each trial
    public static ArrayList<Node> AllTrainingNodes; //Contains all Traning Genes and Seed Diseases
    public static ArrayList<Node> AllTrainingPhenotypes; //Contains all Traning Genes and Seed Diseases
    
    //To store for all trial (number of runs equal to number of training genes)
    public static ArrayList<ArrayList<Node>> AllRuns;

    public static boolean isDirected;
    public static ArrayList<Interaction> ConvertedGeneNetwork;

    public static boolean isWeighted=true;
    
    public static ArrayList<Interaction> NormalizedGeneNetwork;

    public static Map<String, ArrayList<NodeInteraction>> IncomingEntityTable = new TreeMap<String, ArrayList<NodeInteraction>>();
    //Store all genes in normalized network before and after each trial of validation
    public static ArrayList<Node> ValidationScore;
    //Store all genes in normalized network before and after network prioritization (only 1 time)
    public static ArrayList<Node> PrioritizationScore;

    public static int TestGeneType;

    public static ArrayList<Node> AllTestGenes;//Store All Candidate Genes for each trial (each held out)
    public static ArrayList<Node> AllNonTrainingGenes;//Store All non-training genes

   

    public static ROC myROC;

    public static ArrayList<ArrayList<Double>> InEdgeWeights;     //Weights of link from current node to target nodes
    public static ArrayList<ArrayList<Integer>> InNodeIndices;   //Target node indices of current node
    public static ArrayList<ArrayList<String>> InNodes;           //Target nodes of current node

    public static int NumOfNeighbors;

    public static String vsNetworkName="Scored Network Visual Style";

    public static String GeneFormat;

    public static String NetworkGeneIdentifier;

    public static boolean AnalysisOK=false;
    public static String AnalysisErrorMsg="";

    public static Map<String,String> GeneNetworks = new TreeMap<String,String>();

    public static String curNetID;
    public static String curPheNetID;
    
    public static void setInitialValidationScore(ArrayList<Node> NormalizedNetworkNode){
        int i;
        MainData.ValidationScore = new ArrayList<Node>();
        
        for(i=0;i<NormalizedNetworkNode.size();i++){
            Node n = NormalizedNetworkNode.get(i).Copy();
            if(n.Type.compareTo("Disease")==0){
                //n.OfficialSymbol = n.Name;
                n.IsInNetwork = false;
                n.IsTest = false;
                n.IsHeldout = false;
            }else{
                n.IsInNetwork = true;//true mean g is actual GENE not a Disease
            }
            MainData.ValidationScore.add(n);
        }
        //System.out.println("OK Vad");
    }
    
    public static Node assignNormalizedNetworkNode(Node NormalizedNetworkNode){
                
        Node g = new Node();
        g.AlternateSymbols = NormalizedNetworkNode.AlternateSymbols;
        g.Band = NormalizedNetworkNode.Band;
        g.Chromosome = NormalizedNetworkNode.Chromosome;
        g.DistanceToSeed=NormalizedNetworkNode.DistanceToSeed;
        g.EntrezID=NormalizedNetworkNode.EntrezID;
        g.GeneEnd=NormalizedNetworkNode.GeneEnd;
        g.GeneStart = NormalizedNetworkNode.GeneStart;
        g.IsInNetwork = NormalizedNetworkNode.IsInNetwork;
        g.Index = NormalizedNetworkNode.Index;
        g.NetworkID = NormalizedNetworkNode.NetworkID;
        g.IsHeldout=NormalizedNetworkNode.IsHeldout;
        g.IsSeed=NormalizedNetworkNode.IsSeed;
        g.IsTest=NormalizedNetworkNode.IsTest;
        //g.Name=UpdatedGeneNetworkNode.Name;
        g.OfficialSymbol=NormalizedNetworkNode.OfficialSymbol;
        g.Organism=NormalizedNetworkNode.Organism;
        g.Rank=NormalizedNetworkNode.Rank;
        g.Score=NormalizedNetworkNode.Score;
        g.UniProtAC=NormalizedNetworkNode.UniProtAC;
        g.Tag=NormalizedNetworkNode.Tag;

        return g;

    }
    
    public static void setInitialPrioritizationScore(ArrayList<Node> NormalizedNetworkNode){
        int i;
        MainData.PrioritizationScore = new ArrayList<Node>();
        
        for(i=0;i<NormalizedNetworkNode.size();i++){
            Node n = NormalizedNetworkNode.get(i).Copy();
            if(n.Type.compareTo("Disease")==0){
                //n.OfficialSymbol = n.Name;
                n.IsInNetwork = false;
                n.IsTest = false;
                n.IsHeldout = false;
            }else{
                n.IsInNetwork = true;//true mean g is actual GENE not a Disease
            }
            MainData.PrioritizationScore.add(n);
        }
        
    }
    
}
