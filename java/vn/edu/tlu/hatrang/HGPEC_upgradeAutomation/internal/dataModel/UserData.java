/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.swing.JOptionPane;

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Interaction;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Node;



/**
 *
 * @author "MinhDA"
 */
public class UserData {
    public static ArrayList<String> KDGUserInput;
    public static ArrayList<Node> KDGUserInputNormalized;

    public static Set<String> TGUserInput = new TreeSet<String>();
    public static ArrayList<Node> TGUserInputNormalized;

    public static String Network_FileNameFullPath;
    public static String Network_FileName;

    public static String term="";

    public static ArrayList<String> MissingNetworkGenes;
    
    public static ArrayList<String> MissingKnownDiseaseGenes;
    public static ArrayList<String> MissingTestGenes;

    public static ArrayList<String> MissingGenes;
    public static String MissingGeneIdentifier;
    
    
    public static ArrayList<Interaction> UpdatedGeneNetworkBackup;
    public static ArrayList<Node> UpdatedGeneNetworkNodeBackup;
    
    public static void loadNormalizedGraph(){
        try{
            BufferedReader br= new BufferedReader(new FileReader("Directed_Normalized_Network1.txt"));
            String str=null;

            MainData.NormalizedGeneNetwork=new ArrayList<Interaction>();
            Interaction inatemp;
            String srcnode="";
            String dstnode="";
            double weight=0.0;
            System.out.println("Normalized Graph data file is being loaded...!");

            while((str=br.readLine())!=null){
                //System.out.println(numofina + ": " + str);
                StringTokenizer st = new StringTokenizer(str,"\t");
                //System.out.println(st.nextToken());
                if(st.countTokens()==3){
                    srcnode=st.nextToken();
                    weight=Double.parseDouble(st.nextToken());
                    dstnode=st.nextToken();

                    inatemp= new Interaction();

                    inatemp.NodeSrc=srcnode;
                    inatemp.NodeDst=dstnode;
                    inatemp.Weight=weight;
                    MainData.NormalizedGeneNetwork.add(inatemp);
                }
            }
            br.close();
            JOptionPane.showMessageDialog(null,"Total interaction of " + MainData.NormalizedGeneNetwork.size());
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error in loading Normalized Graph: " + e.toString());
        }
    }

    public static void loadAllSeedGenes(){
        try{
            BufferedReader br= new BufferedReader(new FileReader("AllSeedGenes.txt"));
            String str=null;
            int i;
            MainData.AllTrainingGenes= new ArrayList<Node>();
            MainData.AllSeedGenes= new ArrayList<ArrayList<Node>>();

            Node gene;
            
            System.out.println("Normalized Graph data file is being loaded...!");

            while((str=br.readLine())!=null){
                //System.out.println(numofina + ": " + str);
                StringTokenizer st = new StringTokenizer(str,"\t");
                //System.out.println(st.nextToken());
                if(st.countTokens()==76){
                    gene=new Node();
                    gene.OfficialSymbol=st.nextToken();
                    MainData.AllTrainingGenes.add(gene);
                    ArrayList<Node> SeedGenes = new ArrayList<Node>();
                    for(i=0;i<75;i++){
                        gene=new Node();
                        gene.OfficialSymbol=st.nextToken();
                        SeedGenes.add(gene);
                    }
                    MainData.AllSeedGenes.add(SeedGenes);
                }
            }
            br.close();
            JOptionPane.showMessageDialog(null,"Total training set of " + MainData.AllSeedGenes.size());
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error in loading Training Genes: " + e.toString());
        }
    }
}
