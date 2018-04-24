/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.JOptionPane;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.session.CyNetworkNaming;

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Complex;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.DO;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Disease;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Interaction;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Node;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Pathway;


/**
 *
 * @author "MinhDA"
 */
public class Resource {
    
    private CyNetworkFactory cyNetworkFactory;
    private CyNetworkNaming cnn;
    private CyNetworkManager cyNetworkManager;

    public Resource() {
        
    }

    public void loadAllGenes_Chromosomes(String filename){
        try{
            int i;
            InputStream is = getClass().getResourceAsStream(filename);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String str=null;

            BasicData.AllGeneChromosome = new ArrayList<Node>();

            Node gene;
            String ensemblid="";
            String officialsymbol="";
            String chromosome="";
            String band="";
            long genestart=0;
            long geneend=0;
            String entrezid="";

            System.out.println("Human Gene-Chromosome database is being loaded...!");
            while((str=br.readLine())!=null){
                //System.out.println(numofina + ": " + str);
                StringTokenizer st = new StringTokenizer(str,"\t");
                //System.out.println(st.nextToken());
                if(st.countTokens()==7){
                    ensemblid=st.nextToken();
                    genestart=Long.parseLong(st.nextToken());
                    geneend=Long.parseLong(st.nextToken());

                    chromosome=st.nextToken();
                    band=st.nextToken();
                    officialsymbol=st.nextToken();
                    entrezid=st.nextToken();

                    gene= new Node();

                    gene.EnsemblID=ensemblid;
                    gene.GeneStart=genestart;
                    gene.GeneEnd=geneend;
                    gene.Chromosome=chromosome;
                    gene.Band=band;
                    gene.OfficialSymbol=officialsymbol;
                    gene.EntrezID = entrezid;
                    
                    BasicData.AllGeneChromosome.add(gene);
                }
            }
            br.close();


        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error while loading Gene-Chromosome Database: " + e.toString());
            e.printStackTrace();
        }
    }
    
    //Load Gene ID Mapping database
    public void loadAllGenes(String GeneIdentifier, String filename){
        try{
            if(GeneIdentifier.compareTo("EntrezID")==0){
                BasicData.AllGene_EntrezID = new ArrayList<Node>();
                System.out.println("Entrez Gene Information database is being loaded...!");
            }else{
                BasicData.AllGene_UniProtAC = new ArrayList<Node>();
                System.out.println("UniProt Information database is being loaded...!");
            }

            int i;

            //Load from Resource (this is fixed)
            InputStream is = getClass().getResourceAsStream(filename);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String str=null;
            Node gene;

            String ensemblid="";

            String geneid="";
            String officialsymbol="";
            String organism="";
            String uniprotid="";
            //String alternatesymbols="";
            int geneindex=0;
            while((str=br.readLine())!=null){
                StringTokenizer st = new StringTokenizer(str,"\t");
                
                if(st.countTokens()>=3){
                    gene= new Node();
                    geneid=st.nextToken().trim();
                    officialsymbol=st.nextToken().trim();
                    organism=st.nextToken().trim();

                    gene.Organism=organism;
                    gene.OfficialSymbol=officialsymbol;

                    if(st.hasMoreTokens()){
                        
                        StringTokenizer alternatesymbols=new StringTokenizer(st.nextToken().trim(),", ");

                        while(alternatesymbols.hasMoreTokens()){
                            gene.AlternateSymbols.add(alternatesymbols.nextToken().trim());
                        }
                    }

                    gene.Tag=Integer.toString(geneindex);

                    if(GeneIdentifier.compareTo("EntrezID")==0){
                        gene.EntrezID=geneid;
                        BasicData.AllGene_EntrezID.add(gene);
                        BasicData.AllGene_EntrezIDIndex.put(geneid, gene);
                        BasicData.AllEntrezID_OfficialSymbol.put(gene.EntrezID, gene.OfficialSymbol);
                    }else{
                        gene.UniProtAC=geneid;
                        BasicData.AllGene_UniProtAC.add(gene);
                        BasicData.AllGene_UniProtACIndex.put(geneid, gene);
                        BasicData.AllUniProtAC_OfficialSymbol.put(gene.UniProtAC, gene.OfficialSymbol);
                        BasicData.AllGene_OfficialSymbolIndex.put(officialsymbol,gene);
                    }
                    geneindex++;
                }
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while loading Gene Information Database!\n" + e.toString());
            
        }
    }
    
    public Map<String, Pathway> loadPathway2Genes(String Filename){
                        
        Map<String, Pathway> PathwayList = new TreeMap<String, Pathway>();
        BasicData.Gene2Pathways = new TreeMap<String, Set<String>>();
        
        int i;
        try {
            InputStream is = getClass().getResourceAsStream(Filename);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            
            String str = "";
            //str = br.readLine(); //Ignore first line (title)
            while ((str = br.readLine()) != null) {

                String[] stt = str.split("\t");
//                for(i=0;i<stt.length;i++){
//                    System.out.println(stt[i].trim());
//                }
//                System.out.println("-----");
//                StringTokenizer st = new StringTokenizer(str, ";");
//                for(i=0;i<st.countTokens();i++){
//                    System.out.println(st.nextToken());
//                }
//                System.out.println("-----");
//                st = new StringTokenizer(str, ";");
                
                Pathway p = new Pathway();//In this case, Disease mean Pathway

                p.number = stt[0].trim();

                p.name = stt[1].trim();
                
                p.AssociatedGenes = stt[2].trim();
                String[] EntrezIDs = p.AssociatedGenes.split(",");
                p.Genes = new ArrayList<String>();
                for(i=0;i<EntrezIDs.length;i++){
                    String EntrezID = EntrezIDs[i].trim();
                    p.Genes.add(EntrezID);
                    if(BasicData.Gene2Pathways.containsKey(EntrezID)){
                        BasicData.Gene2Pathways.get(EntrezID).add(p.number);
                    }else{
                        Set<String> PathwayIDs = new TreeSet<String>();
                        BasicData.Gene2Pathways.put(EntrezID, PathwayIDs);
                    }
                }

                PathwayList.put(p.number,p);
            }
            br.close();
            System.out.println("Total Pathways: " + PathwayList.size());
            System.out.println("Total Genes annotated with Pathways: " + BasicData.Gene2Pathways.size());
            
            //Preview Gene-...... List
//            int count=0;
//            for(Iterator<Entry<String, Set<String>>> it = BasicData.Gene2Pathways.entrySet().iterator();it.hasNext();){
//                Entry<String, Set<String>> e = it.next();
//                System.out.println(count + "\t" + e.getKey() + "\t" + e.getValue().toString());
//                count++;
//                if(count>=10) break;
//            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while loading Pathway-Gene list: " + e.toString());
            
        }
        return PathwayList;
        
    }

    public void loadCaseStudies(String filename){
        try{
            BasicData.CaseStudies = new ArrayList<Node>();
            int i;
            InputStream is = getClass().getResourceAsStream(filename);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String line=null;

            while((line=br.readLine())!=null){
                String[] str = line.split("\t");
                Node gene = new Node();
                gene.EntrezID=str[0].trim();
                gene.OfficialSymbol=str[1].trim();
                if(str.length>2){
                    gene.UniProtAC=str[2].trim();
                }else{
                    gene.UniProtAC="";
                }

                BasicData.CaseStudies.add(gene);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    
//    public void loadDefaultNetwork(String filename) {
//        try{
//            int i,j;
//
//            InputStream is = getClass().getResourceAsStream(filename);
//            InputStreamReader isr = new InputStreamReader(is);
//            BufferedReader br = new BufferedReader(isr);
//
//            String str=null;
//
//            ArrayList<Interaction> DefaultNet=new ArrayList<Interaction>();
//            Set<String> NetNodes = new TreeSet<String>();
//            Interaction inatemp;
//            String srcnode="";
//            String dstnode="";
//            double weight=0.0;
//            
//            System.out.println("Gene/Protein Network data file is being loaded...!");
//
//            while((str=br.readLine())!=null){
//                StringTokenizer st = new StringTokenizer(str,"\t");
//                if(st.countTokens()==3){
//                    srcnode=st.nextToken();
//                    try{
//                        weight=Double.parseDouble(st.nextToken());
//                    }catch(Exception e){//Middle column is not a double value
//                        weight=1.0;
//                    }
//                    dstnode=st.nextToken();
//
//                    inatemp= new Interaction();
//
//                    inatemp.NodeSrc=srcnode;
//                    inatemp.NodeDst=dstnode;
//                    inatemp.Weight=weight;
//                    inatemp.WeightOriginal=weight;
//                    NetNodes.add(inatemp.NodeSrc);
//                    NetNodes.add(inatemp.NodeDst);
//                    DefaultNet.add(inatemp);//Has not been checked whether dupplication are occured
//                }
//            }
//            br.close();
//            
//            CyNetwork cyNetwork = cyNetworkFactory.createNetwork();
//            cyNetwork.getRow(cyNetwork).set(CyNetwork.NAME,cnn.getSuggestedNetworkTitle(filename.substring(filename.indexOf("/")+1, filename.length())));
//            
////            CyNetwork cyNetwork = Cytoscape.createNetwork(filename.substring(filename.indexOf("/")+1, filename.length()),false);//Do not create view
//                        
//            for(Iterator<String> it = NetNodes.iterator();it.hasNext();){
//                CyNode node = Cytoscape.getCyNode(it.next(),true);
//                cyNetwork.addNode(node);
//            }
//
//            for(i=0;i<DefaultNet.size();i++){
//                CyEdge edge=Cytoscape.getCyEdge(DefaultNet.get(i).NodeSrc,Integer.toString(i),DefaultNet.get(i).NodeDst,Double.toString(DefaultNet.get(i).Weight));
//                cyNetwork.addEdge(edge);
//            }
//            
//
//            
//            //Cytoscape.getCurrentNetworkView().applyLayout(CyLayouts.getDefaultLayout());
//
//        }catch(Exception e){
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Error in Create Network Task: " + e.toString());
//        }
//    }
    
    public Resource(CyNetworkFactory cyNetworkFactory) {
        this.cyNetworkFactory = cyNetworkFactory;
    }

    public Map<String, Disease> loadPhenotypeInfo(String FileName) {
        
        Map<String, Disease> PhenotypeList = new TreeMap<String, Disease>();
        //GENE Phenotype = new GENE();//Phenotype is considered as a known node
        Set<String> DiseaseGeneSet = new TreeSet<String>();
        try {
            InputStream is = getClass().getResourceAsStream(FileName);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String str = "";
            while ((str = br.readLine()) != null) {
                //System.out.println(str);
                String[] st = str.split("\t");
                Disease d = new Disease();//In this case, Disease mean Pathway

                d.DiseaseID = st[0].trim();
                d.Prefix = st[1].trim();
                d.Locus = st[2].trim();
                d.Name = st[3].trim();
                String AssociatedGenes = st[4].trim();
                
                String[] ste = AssociatedGenes.split(", ");
                for(int i=0;i<ste.length;i++){
                    d.KnownGenes.add(ste[i]);
                    DiseaseGeneSet.add(ste[i]);
                }
                d.KnownGeneList = d.KnownGenes.toString().substring(1, d.KnownGenes.toString().length()-1);
                PhenotypeList.put(d.DiseaseID,d);
            }
            br.close();
            System.out.println("Total Diseases: " + PhenotypeList.size());
            System.out.println("Total Disease Genes: " + DiseaseGeneSet.size());
        } catch (Exception e) {
            System.out.println("Error while loading Disease Information Database: " + e.toString());
            e.printStackTrace();
        }
        return PhenotypeList;
    }

    public Map<String, Complex> loadComplex2Gene(String Organism, String FileName) {
        
        BasicData.Complex2Gene_FileNameFullPath = FileName;
                
        Map<String, Complex> ComplexList = new TreeMap<String, Complex>();
        BasicData.Gene2Complexes = new TreeMap<String, Set<String>>();
        
        int i;
        try {
            InputStream is = getClass().getResourceAsStream(BasicData.Complex2Gene_FileNameFullPath);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            
            String str = "";
            str = br.readLine(); //Ignore first line (title)
            while ((str = br.readLine()) != null) {
//                System.out.println(str);                    
                String[] stt = str.split(";");
//                for(i=0;i<stt.length;i++){
//                    System.out.println(stt[i].trim());
//                }
//                System.out.println("-----");
//                StringTokenizer st = new StringTokenizer(str, ";");
//                for(i=0;i<st.countTokens();i++){
//                    System.out.println(st.nextToken());
//                }
//                System.out.println("-----");
//                st = new StringTokenizer(str, ";");
                
                Complex c = new Complex();//In this case, Disease mean Pathway

//                if (st.hasMoreTokens()) {
//                    c.ComplexID = st.nextToken().toString().trim();
//                }
                c.ComplexID = stt[0].trim();
//                if (st.hasMoreTokens()) {
//                    c.ComplexName = st.nextToken().toString().trim();
//                }
                c.ComplexName = stt[1].trim();
//                if (st.hasMoreTokens()) {
//                    c.Synonyms = st.nextToken().toString().trim();
//                }
                c.Synonyms = stt[2].trim();
//                if (st.hasMoreTokens()) {
//                    c.Organism = st.nextToken().toString().trim();
//                }
                c.Organism = stt[3].trim();
                if(c.Organism.compareToIgnoreCase(Organism)!=0) continue;
                
//                if (st.hasMoreTokens()) {
//                    c.Subunits_UniProtACs = st.nextToken().toString().trim();
//                }
                c.Subunits_UniProtACs = stt[4].trim();
                //if (st.hasMoreTokens()) {
                    //c.Subunits_EntrezIDs = st.nextToken().toString().trim();
                    c.Subunits_EntrezIDs = stt[5].trim();
                    String[] EntrezIDs = c.Subunits_EntrezIDs.split(",");
                    c.Genes = new ArrayList<String>();
                    for(i=0;i<EntrezIDs.length;i++){
                        String EntrezID = EntrezIDs[i].trim();
                        if(EntrezID.contains("(")) EntrezID = EntrezID.substring(1, EntrezID.length());
                        if(EntrezID.contains(")")) EntrezID = EntrezID.substring(0, EntrezID.length()-1);
                        //System.out.println(EntrezID);
                        c.Genes.add(EntrezID);
                        if(BasicData.Gene2Complexes.containsKey(EntrezID)){
                            BasicData.Gene2Complexes.get(EntrezID).add(c.ComplexID);
                        }else{
                            Set<String> ComplexIDs = new TreeSet<String>();
                            BasicData.Gene2Complexes.put(EntrezID, ComplexIDs);
                        }
                    }
                //}
//                if (st.hasMoreTokens()) {
//                    c.PurificationMethod = st.nextToken().toString().trim();
//                }
                c.PurificationMethod = stt[6].trim();
//                if (st.hasMoreTokens()) {
//                    c.PubMedIDs = st.nextToken().toString().trim();
//                }
                c.PubMedIDs = stt[7].trim();
//                if (st.hasMoreTokens()) {
//                    c.FunCategories = st.nextToken().toString().trim();
//                }
                c.FunCategories = stt[8].trim();
//                if (st.hasMoreTokens()) {
//                    c.FunctionalComment = st.nextToken().toString().trim();
//                }
                c.FunctionalComment = stt[9].trim();
//                if (st.hasMoreTokens()) {
//                    c.DiseaseComment= st.nextToken().toString().trim();
//                }
                c.DiseaseComment = stt[10].trim();
//                if (st.hasMoreTokens()) {
//                    c.SubunitComment= st.nextToken().toString().trim();
//                }
                c.SubunitComment = stt[11].trim();
                
                ComplexList.put(c.ComplexID,c);
            }
            br.close();
            System.out.println("Total Protein Complexes: " + ComplexList.size());
            System.out.println("Total Genes annotated with Protein Complexes: " + BasicData.Gene2Complexes.size());
            
            //Preview Gene-..... List
//            int count=0;
//            for(Iterator<Entry<String, Set<String>>> it = BasicData.Gene2Complexes.entrySet().iterator();it.hasNext();){
//                Entry<String, Set<String>> e = it.next();
//                System.out.println(count + "\t" + e.getKey() + "\t" + e.getValue().toString());
//                count++;
//                if(count>=10) break;
//            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while loading Complex-gene list: " + e.toString());
            
        }
        return ComplexList;
    }

    public Map<String, DO> loadDO2Gene(String Organism, String FileName) {
        
        BasicData.DO2Gene_FileNameFullPath = FileName;
                
        Map<String, DO> DOList = new TreeMap<String, DO>();
        BasicData.Gene2DOs = new TreeMap<String, Set<String>>();
        
        int i;
        try {
            InputStream is = getClass().getResourceAsStream(BasicData.DO2Gene_FileNameFullPath);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            
            String str = "";
            //str = br.readLine(); //Ignore first line (title)
            while ((str = br.readLine()) != null) {
//                System.out.println(str);                    
                String[] stt = str.split("\t");
//                for(i=0;i<stt.length;i++){
//                    System.out.println(stt[i].trim());
//                }
//                System.out.println("-----");
//                StringTokenizer st = new StringTokenizer(str, ";");
//                for(i=0;i<st.countTokens();i++){
//                    System.out.println(st.nextToken());
//                }
//                System.out.println("-----");
//                st = new StringTokenizer(str, ";");
                
                DO DO = new DO();//In this case, Disease mean Pathway

                DO.DOID = stt[0].trim();

                DO.Name = stt[1].trim();

                DO.ICD9CM = stt[2].trim();

                DO.MSH = stt[3].trim();
                
                DO.NCI = stt[4].trim();
                
                DO.AnnotatedGenes = stt[5].trim();
                String[] EntrezIDs = DO.AnnotatedGenes.split(",");
                DO.Genes = new ArrayList<String>();
                for(i=0;i<EntrezIDs.length;i++){
                    String EntrezID = EntrezIDs[i].trim();
                    DO.Genes.add(EntrezID);
                    if(BasicData.Gene2DOs.containsKey(EntrezID)){
                        BasicData.Gene2DOs.get(EntrezID).add(DO.DOID);
                    }else{
                        Set<String> DOIDs = new TreeSet<String>();
                        BasicData.Gene2DOs.put(EntrezID, DOIDs);
                    }
                }

                DOList.put(DO.DOID,DO);
            }
            br.close();
            System.out.println("Total Disease Ontologies: " + DOList.size());
            System.out.println("Total Genes annotated with Disease Ontologies: " + BasicData.Gene2DOs.size());
            
            //Preview Gene-..... List
//            int count=0;
//            for(Iterator<Entry<String, Set<String>>> it = BasicData.Gene2DOs.entrySet().iterator();it.hasNext();){
//                Entry<String, Set<String>> e = it.next();
//                System.out.println(count + "\t" + e.getKey() + "\t" + e.getValue().toString());
//                count++;
//                if(count>=10) break;
//            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while loading DiseaseOntology-Gene list: " + e.toString());
            
        }
        return DOList;
    }

    public Map<String, Disease> loadPhenotype2GeneNetwork(String FileName) {
        
        Map<String, Disease> DiseaseList = new TreeMap<String, Disease>();
        //GENE Phenotype = new GENE();//Phenotype is considered as a known node
        Set<String> DiseaseGeneSet = new TreeSet<String>();
        try {
            InputStream is = getClass().getResourceAsStream(FileName);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String str = "";
            while ((str = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(str, "\t");
                Disease d = new Disease();//In this case, Disease mean Pathway

                if (st.hasMoreTokens()) {
                    d.DiseaseID = st.nextToken().toString().trim();
                }
                if (st.hasMoreTokens()) {
                    d.Name = st.nextToken().toString().trim();
                }
                if(st.hasMoreTokens()){
                    String EntrezIDList=st.nextToken().toString().trim();
                    StringTokenizer ste = new StringTokenizer(EntrezIDList, ", ");
                    while (ste.hasMoreTokens()) {
                        String s=ste.nextToken().toString().trim();
                        if(s.compareTo("")!=0){
                            d.KnownGenes.add(s);
                            DiseaseGeneSet.add(s);
                        }
                    }
                    d.KnownGeneList = d.KnownGenes.toString().substring(1, d.KnownGenes.toString().length()-1);
                }
                
                if(d.KnownGenes.size()>0){
                    DiseaseList.put(d.DiseaseID,d);
                }
                
            }
            br.close();
            System.out.println("Total Diseases: " + DiseaseList.size());
            System.out.println("Total Disease Genes: " + DiseaseGeneSet.size());
        } catch (Exception e) {
            System.out.println("Error while loading Disease 2 genes List: " + e.toString());
            e.printStackTrace();
        }
        return DiseaseList;
    }

    public ArrayList<Interaction> loadPhenotypeNetwork(String FileName) {
        ArrayList<Interaction> PhenotypeNetwork = new ArrayList<Interaction>();
        try {
            int i, j;
            
            BasicData.PhenotypeNetwork_FileNameFullPath = FileName;
           
            
            InputStream is = getClass().getResourceAsStream(BasicData.PhenotypeNetwork_FileNameFullPath);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            
            
            String str = null;

            
                        
            Interaction inatemp;
            String srcnode = "";
            String dstnode = "";
            double weight = 0.0;
            System.out.println("Disease Network data file is being loaded...!");

            int id=0;
            BasicData.NetworkPhenotypeSet = new TreeSet<String>();
            while ((str = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(str, "\t");
                //System.out.println(st.nextToken());
                if (st.countTokens() == 3) {
                    srcnode = st.nextToken().trim();
                    try {
                        weight = Double.parseDouble(st.nextToken().trim());
                    } catch (Exception e) {//Middle column is not a double value
                        weight = 1.0;
                    }
                    dstnode = st.nextToken().trim();

                    inatemp = new Interaction();

                    inatemp.NodeSrc = srcnode;
                    inatemp.NodeDst = dstnode;
                    inatemp.Weight = weight;
                    inatemp.WeightOriginal = weight;

                    inatemp.Id=id;
                    
                    PhenotypeNetwork.add(inatemp);
                    BasicData.NetworkPhenotypeSet.add(srcnode);
                    BasicData.NetworkPhenotypeSet.add(dstnode);
                    
                    id++;
                }
            }
            br.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return PhenotypeNetwork;
    }
    
}
