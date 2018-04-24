/*
 * In progress
 */
package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import org.cytoscape.model.CyNode;

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Complex;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.DO;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Disease;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.GO;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.GeneRIF;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Interaction;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Node;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.OMIM;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Pathway;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.PubMed;


/**
 *
 * @author "MinhDA"
 */
public class BasicData {

    public static ArrayList<Node> AllGene = new ArrayList<Node>();
    public static Map<String, Node> AllGene_EntrezIDIndex = new TreeMap<String, Node>();
    public static Map<String, Node> AllGene_UniProtACIndex = new TreeMap<String, Node>();
    public static Map<String, Node> AllGene_OfficialSymbolIndex = new TreeMap<String, Node>();
    public static ArrayList<Node> AllGene_EntrezID = new ArrayList<Node>();
    public static ArrayList<Node> AllGene_UniProtAC = new ArrayList<Node>();
    
    //MinhDA add InputNetworkFileName 
    public static String InputNetworkFileName = "Default_Human_PPI_Network.sif";
    public static HashMap<String, CyNode> nodeIdMap = new HashMap<>();
    //MinhDA add InputNetworkFileName

    public static String AllGene_FileName = "Data" + File.separator + "IDMapping_EntrezID_UniProt.txt";
    public static String AllGene_EntrezID_FileName = "Data" + File.separator + "EntrezGeneInfo.txt";
    public static String AllGene_UniProtAC_FileName = "Data" + File.separator + "UniProtInfo.txt";
    //public static String AllGene_FileName="Data" + File.separator + "Genes" + File.separator + "AllGene4.txt";

    public static String AllGene_Mammalia_FileName = "Data" + File.separator + "All_Mammalia.gene_info";

    public static ArrayList<Node> AllGeneChromosome = new ArrayList<Node>();
    public static String Gene_Chromosome_FileName = "Data" + File.separator + "GeneChromosome.txt";
    //public static String ncbiWSClient_FileName="plugins" + File.separator + "ncbiWSClient.jar";

    public static ArrayList<GeneRIF> AllGeneRIFs = new ArrayList<GeneRIF>();
    public static String GeneRIFs_FileName = "Data" + File.separator + "generifs_basic";
    public static String Gene2GO_FileName = "Data" + File.separator + "gene2go";

    //FTP. Download GeneRIF via FTP
    public static String hostname = "ftp.ncbi.nih.gov";//reader.readLine();
    public static String username = "anonymous";//reader.readLine();
    public static String password = "hauldhut@yahoo.com";//reader.readLine();
    public static String remotedirectory = "/gene/GeneRIF/";
    public static String localdirectory = "Data" + File.separator + "";
    public static String filename = "generifs_basic.gz";//interaction_sources
    //public static String filename="hiv_interactions.gz";//For testing

    public static ArrayList<GO> AllGO = new ArrayList<GO>();
    public static ArrayList<GO> AllGO_EntrezID = new ArrayList<GO>();
    public static ArrayList<GO> AllGO_UniProtAC = new ArrayList<GO>();

    public static String AllGO_FileName = "";
    public static String AllGO_EntrezID_FileName = "Data" + File.separator + "gene2go";//GO_EntrezID
    public static String AllGO_UniProtAC_FileName = "Data" + File.separator + "AllGO_UniProtAC.txt";

    public static Map<String, Pathway> Pathway2Genes = new TreeMap<String, Pathway>();
    public static Map<String, Set<String>> Gene2Pathways = new TreeMap<String, Set<String>>();

    public static Hashtable<String, GO> EvidenceGOs = new Hashtable<String, GO>();
    public static Map<String, OMIM> EvidenceOMIMs = new TreeMap<String, OMIM>();
    public static Map<String, PubMed> EvidencePubMeds = new TreeMap<String, PubMed>();
    public static Hashtable<String, PubMed> EvidencePubMedsInGeneRIF = new Hashtable<String, PubMed>();
    public static ArrayList<GeneRIF> EvidenceGeneRIFs = new ArrayList<GeneRIF>();
    
    public static Set<String> SearchedOMIMIDs = new TreeSet<String>();
    public static Set<String> SearchedPubMedIDs = new TreeSet<String>();

    public static ArrayList<Node> CaseStudies = new ArrayList<Node>();

    public static Map<String, String> AllEntrezID_OfficialSymbol = new TreeMap<String, String>();
    public static Map<String, String> AllUniProtAC_OfficialSymbol = new TreeMap<String, String>();

    public static Set<String> NetworkGeneSet = new TreeSet<String>();
    public static Set<String> NetworkPhenotypeSet = new TreeSet<String>();
    public static Set<String> NetworkPhenotypeSet_MIM = new TreeSet<String>();
    public static Set<String> NetworkPhenotypeSet_RR = new TreeSet<String>();
    public static Set<String> NetworkPhenotypeSet_PHI = new TreeSet<String>();

    public static ArrayList<Interaction> PhenotypeNetwork = new ArrayList<Interaction>();
    public static ArrayList<Interaction> PhenotypeNetwork_MIM = new ArrayList<Interaction>();
    public static ArrayList<Interaction> PhenotypeNetwork_RR = new ArrayList<Interaction>();
    public static ArrayList<Interaction> PhenotypeNetwork_PHI = new ArrayList<Interaction>();

    public static Map<String, Node> UpdatedPhenotypeNetworkNode = new TreeMap<String, Node>();

    public static ArrayList<Interaction> Mim2GeneNetwork;//EntrezID
    public static ArrayList<Interaction> Gene2MimNetwork;//EntrezID

    public static ArrayList<Interaction> OriginalNetwork;
    public static ArrayList<Interaction> GeneNetwork;

    public static ArrayList<String> OriginalNetworkNode;
    public static Map<String, Node> UpdatedGeneNetworkNode = new TreeMap<String, Node>();
    public static ArrayList<Node> UpdatedGeneNetworkNode_EntrezIDIndex;
    public static ArrayList<Node> UpdatedGeneNetworkNode_UniProtACIndex;
    public static Map<String, Node> UpdatedGeneNetworkNode_OfficialSymbolIndex = new TreeMap<String, Node>();

    public static String PhenotypeNetwork_FileNameFullPath;
    public static String Mim2Gene_FileNameFullPath;

    //public static Map<String, ArrayList<String>> Mim2Genes = new TreeMap<String, ArrayList<String>>();
    //public static Map<String,Disease> Phenotype2Genes = new TreeMap<String,Disease>();
    public static Map<String, Disease> Phenotype2Genes_MIM = new TreeMap<String, Disease>();
    public static Map<String, Disease> Phenotype2Genes_DisGenNET = new TreeMap<String, Disease>();
    
    public static Map<String, Disease> Phenotype2Genes_COM = new TreeMap<String, Disease>();
    public static Map<String, Disease> Phenotype2Genes = new TreeMap<String, Disease>();
    public static Map<String, Disease> Phenotype2Genes_Full = new TreeMap<String, Disease>();

    public static String Complex2Gene_FileNameFullPath;
    public static Map<String, Complex> Complex2Genes = new TreeMap<String, Complex>();
    public static Map<String, Set<String>> Gene2Complexes = new TreeMap<String, Set<String>>();

    public static String DO2Gene_FileNameFullPath;
    public static Map<String, DO> DO2Genes = new TreeMap<String, DO>();
    public static Map<String, Set<String>> Gene2DOs = new TreeMap<String, Set<String>>();

    //Load Gene ID Mapping database
    public static void loadAllGenes_TextVersion(String GeneIdentifier) {
        try {

            int i;
            //FileOutputStream fs = new FileOutputStream(filename);

            if (GeneIdentifier.compareTo("EntrezID") == 0) {
                BasicData.AllGene_FileName = BasicData.AllGene_EntrezID_FileName;
                BasicData.AllGene_EntrezID = new ArrayList<Node>();
                System.out.println("Entrez Gene Information database is being loaded...!");
            } else {
                BasicData.AllGene_FileName = BasicData.AllGene_UniProtAC_FileName;
                BasicData.AllGene_UniProtAC = new ArrayList<Node>();
                System.out.println("UniProt Information database is being loaded...!");
            }

            File f = new File(BasicData.AllGene_FileName.replace("\\", File.separator));
            if (f.exists() == false) {
                f.createNewFile();
            }
            BufferedReader br = new BufferedReader(new FileReader(BasicData.AllGene_FileName.replace("\\", File.separator)));
            //PrintWriter pw = new PrintWriter(new FileOutputStream(filename.replace(".txt", "_Output.txt")),false);
            String str = null;
            int geneindex = 0;

            Node gene;

            String ensemblid = "";

            String geneid = "";
            String officialsymbol = "";
            String organism = "";
            String uniprotid = "";
            //String alternatesymbols="";

            while ((str = br.readLine()) != null) {
                //System.out.println(numofina + ": " + str);
                StringTokenizer st = new StringTokenizer(str, "\t");
                //System.out.println(st.nextToken());
                if (st.countTokens() == 4) {
                    gene = new Node();
                    //pharmgkbid=st.nextToken();
                    //ensemblid=st.nextToken();
                    //uniprotid=st.nextToken();
                    geneid = st.nextToken();
                    officialsymbol = st.nextToken();
                    organism = st.nextToken();

                    StringTokenizer alternatesymbols = new StringTokenizer(st.nextToken(), ", ");

                    gene.Organism = organism;
                    gene.OfficialSymbol = officialsymbol;

                    while (alternatesymbols.hasMoreTokens()) {
                        gene.AlternateSymbols.add(alternatesymbols.nextToken());
                    }
                    gene.Tag = Integer.toString(geneindex);

                    if (GeneIdentifier.compareTo("EntrezID") == 0) {
                        gene.EntrezID = geneid;
                        BasicData.AllGene_EntrezID.add(gene);
                        BasicData.AllGene_EntrezIDIndex.put(geneid, gene);
                        BasicData.AllEntrezID_OfficialSymbol.put(gene.EntrezID, gene.OfficialSymbol);
                    } else {
                        gene.UniProtAC = geneid;
                        BasicData.AllGene_UniProtAC.add(gene);
                        BasicData.AllGene_UniProtACIndex.put(geneid, gene);
                        BasicData.AllUniProtAC_OfficialSymbol.put(gene.UniProtAC, gene.OfficialSymbol);
                        BasicData.AllGene_OfficialSymbolIndex.put(officialsymbol, gene);
                    }
                    geneindex++;
                }
            }
            br.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error while loading AllGene Database: " + e.toString());
            e.printStackTrace();
        }
    }
    
    public static void mergeMainAllGenes(){
        int i,j;
        BasicData.AllGene = new ArrayList<Node>();
        ArrayList<Node> Difference = new ArrayList<Node>();

        if(BasicData.AllGene_EntrezID.size()>0 && BasicData.AllGene_UniProtAC.size()==0){
            for(i=0;i<BasicData.AllGene_EntrezID.size();i++){
                BasicData.AllGene.add(BasicData.AllGene_EntrezID.get(i).Copy());
            }
            return;
        }else if(BasicData.AllGene_EntrezID.size()==0 && BasicData.AllGene_UniProtAC.size()>0){
            for(i=0;i<BasicData.AllGene_UniProtAC.size();i++){
                BasicData.AllGene.add(BasicData.AllGene_UniProtAC.get(i).Copy());
            }
            return;
        }else if(BasicData.AllGene_EntrezID.size()==0 && BasicData.AllGene_UniProtAC.size()==0){
            return;
        }

        //Merging two main ID Mapping database is based on OfficialSymbol
        Common.preprocessGeneList(BasicData.AllGene_EntrezID, "OfficialSymbol");
        Common.sortQuickNodeListInAsc(BasicData.AllGene_EntrezID);
//        for(i=0;i<BasicData.AllGene_EntrezID.size();i++){
//            System.out.println("AllGeneE " + i + "\t" + BasicData.AllGene_EntrezID.get(i).EntrezID + "\t" + BasicData.AllGene_EntrezID.get(i).UniProtAC + "\t" + BasicData.AllGene_EntrezID.get(i).OfficialSymbol + "\t" +BasicData.AllGene_EntrezID.get(i).Organism + "\t" +BasicData.AllGene_EntrezID.get(i).AlternateSymbols.toString());
//        }

        Common.preprocessGeneList(BasicData.AllGene_UniProtAC, "OfficialSymbol");
        Common.sortQuickNodeListInAsc(BasicData.AllGene_UniProtAC);
//        for(i=0;i<BasicData.AllGene_UniProtAC.size();i++){
//            System.out.println("AllGeneU " + i + "\t" + BasicData.AllGene_UniProtAC.get(i).EntrezID + "\t" + BasicData.AllGene_UniProtAC.get(i).UniProtAC + "\t" + BasicData.AllGene_UniProtAC.get(i).OfficialSymbol + "\t" +BasicData.AllGene_UniProtAC.get(i).Organism + "\t" +BasicData.AllGene_UniProtAC.get(i).AlternateSymbols.toString());
//        }
        

        //Put all genes from UniProtAC ID Mapping database into AllGene
        for(i=0;i<BasicData.AllGene_UniProtAC.size();i++){
            Node g = BasicData.AllGene_UniProtAC.get(i).Copy();
            BasicData.AllGene.add(g);
        }

        //Traverse each gene in EntrezID ID Mapping database
        for(i=0;i<BasicData.AllGene_EntrezID.size();i++){
            Node g=new Node();
            ArrayList<Integer> posarr = Common.searchUsingBinaryNodeArray(BasicData.AllGene_EntrezID.get(i).OfficialSymbol, BasicData.AllGene);

            //If found in AllGene
            if(posarr.size()>0){
                boolean exist=false;
                for(j=0;j<posarr.size();j++){//Check if identical (the same Organism)
                    if(BasicData.AllGene_EntrezID.get(i).Organism.compareToIgnoreCase(BasicData.AllGene.get(posarr.get(j)).Organism)==0){
                        //But EntrezID in AllGene is empty, so update this field, and also replace alternate symbol field by new one in Entrez Gene ID Mapping database (more sufficient)
                        if(BasicData.AllGene.get(posarr.get(j)).EntrezID.trim().compareTo("")==0){
                            BasicData.AllGene.get(posarr.get(j)).EntrezID=BasicData.AllGene_EntrezID.get(i).EntrezID;
                            BasicData.AllGene.get(posarr.get(j)).AlternateSymbols=BasicData.AllGene_EntrezID.get(i).AlternateSymbols;
                        }else{//There are more than one Entrez Gene ID for the same Official Symbol and Organism (One is new, one is old)
                            if(BasicData.AllGene.get(posarr.get(j)).EntrezID.compareTo(BasicData.AllGene_EntrezID.get(i).EntrezID)!=0){
                                g=BasicData.AllGene.get(posarr.get(j)).Copy();
                                g.EntrezID=BasicData.AllGene_EntrezID.get(i).EntrezID;
                                g.AlternateSymbols=BasicData.AllGene_EntrezID.get(i).AlternateSymbols;
                                BasicData.AllGene.add(g);
                            }
                        }
                        exist=true;
                    }
                }
                if(exist==false){//not identical --> difference 
                    g=BasicData.AllGene_EntrezID.get(i).Copy();
                    Difference.add(g);
                }
            }else{//not found --> difference
                g=BasicData.AllGene_EntrezID.get(i).Copy();
                Difference.add(g);
            }
        }
        //Add differences to 
        for(i=0;i<Difference.size();i++){
            BasicData.AllGene.add(Difference.get(i).Copy());
        }
//        for(i=0;i<BasicData.AllGene.size();i++){
//            System.out.println("AllGene " + i + "\t" + BasicData.AllGene.get(i).EntrezID + "\t" + BasicData.AllGene.get(i).UniProtAC + "\t" + BasicData.AllGene.get(i).OfficialSymbol + "\t" +BasicData.AllGene.get(i).Organism + "\t" +BasicData.AllGene.get(i).AlternateSymbols.toString());
//        }
        //JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Finished");
    }

    public static void loadAllGeneRIFs(Set<String> EntrezIDSet){
        try{
            int i;
            
            
            
            BasicData.AllGeneRIFs=new ArrayList<GeneRIF>();

            BufferedReader br= new BufferedReader(new FileReader(BasicData.GeneRIFs_FileName.replace("\\", File.separator)));
            
            String str=null;
            
            GeneRIF gene;
            String taxid="";
            String entrezid="";
            String pubmedid="";
            String lastupdate="";
            String generifstext="";


            System.out.println("GeneRIF data file is being loaded...!");
            br.readLine();//Igore first line (column title)
            while((str=br.readLine())!=null){
                //System.out.println(numofina + ": " + str);
                StringTokenizer st = new StringTokenizer(str,"\t");
                //System.out.println(st.nextToken());
                if(st.countTokens()==5){
                    gene= new GeneRIF();
                    taxid=st.nextToken();
                    entrezid=st.nextToken();
                    pubmedid=st.nextToken();
                    lastupdate=st.nextToken();
                    generifstext=st.nextToken();

                    gene.TaxID=Integer.parseInt(taxid);
                    gene.EntrezID=entrezid;
                    gene.PubMedID=pubmedid;
                    gene.LastUpdateTimeStamp=lastupdate;
                    gene.GeneRIFsText=generifstext;
                    if(EntrezIDSet.contains(entrezid)){
                        BasicData.AllGeneRIFs.add(gene);
                    }
                }
            }
            br.close();
            
        }catch(Exception e){
            //JOptionPane.showMessageDialog(Cytoscape.getDesktop(), e.toString());
            e.printStackTrace();
        }
    }
}
