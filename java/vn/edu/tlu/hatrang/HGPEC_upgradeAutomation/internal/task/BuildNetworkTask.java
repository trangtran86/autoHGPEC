package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;


import org.cytoscape.io.read.CyNetworkReaderManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.ProvidesTitle;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.BasicData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.MainData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.Resource;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Disease;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Interaction;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Node;




public class BuildNetworkTask implements ObservableTask {
	CyNetwork network;
	private volatile boolean interrupted;
	private TaskManager cyTaskManager;
	private CyNetworkManager cyNetworkManager;
	private CyNetworkReaderManager cyNetworkReaderManager;
	private CyNetworkFactory cyNetworkFactory;
	private CyNetworkNaming namingUtil;
	private SynchronousTaskManager cySynchronousTaskManager; 
    
	
	public static String diseaseFile="";

	@Tunable(description = "Choose disease network", groups = { "Step 1: Define a Heterogeneous Network" },context=Tunable.BOTH_CONTEXT)
	public ListSingleSelection<String> diseaseNetwork=new ListSingleSelection<>("Disease_Similarity_Network_5", "Disease_Similarity_Network_10",
			"Disease_Similarity_Network_15");;

	@Tunable(description = "Choose disease-gene relation", groups = { "Step 1: Define a Heterogeneous Network" },context=Tunable.BOTH_CONTEXT)
	public ListSingleSelection<String> DiseaseGene;

	@Tunable(description = "Choose gene network", groups = { "Step 1: Define a Heterogeneous Network" },context=Tunable.BOTH_CONTEXT)
	public ListSingleSelection<String> geneNetwork;

	public BuildNetworkTask(SynchronousTaskManager cySynchronousTaskManager,TaskManager cyTaskManager,CyNetwork arg0, CyNetworkManager cyNetworkManager,
			CyNetworkReaderManager cyNetworkReaderManager, CyNetworkFactory cyNetworkFactory,
			CyNetworkNaming namingUtil) {
		super();
		this.cyTaskManager=cyTaskManager;
		this.cyNetworkManager = cyNetworkManager;
		this.cyNetworkReaderManager = cyNetworkReaderManager;
		this.cyNetworkFactory = cyNetworkFactory;
		this.namingUtil = namingUtil;
		this.cySynchronousTaskManager=cySynchronousTaskManager;
		network = arg0;
		diseaseNetwork = new ListSingleSelection<>("Disease_Similarity_Network_5", "Disease_Similarity_Network_10",
				"Disease_Similarity_Network_15");
		DiseaseGene = new ListSingleSelection<>("Disease-gene from OMIM", "Disease-gene from DisGeNet");
		
		
		LoadDataResourcesTaskFactory loadDataResourcesTaskFactory = new LoadDataResourcesTaskFactory(cyNetworkManager, cyNetworkReaderManager, cyNetworkFactory, namingUtil);
        cySynchronousTaskManager.execute(loadDataResourcesTaskFactory.createTaskIterator());
        
        List geneList=new ArrayList<>();
        for(CyNetwork net: cyNetworkManager.getNetworkSet()){
            MainData.GeneNetworks.put(net.getSUID().toString(), net.getRow(net).get("name", String.class));
            geneList.add(net.getRow(net).get("name", String.class));
        }
        
        geneNetwork = new ListSingleSelection<>(geneList);
        
//        geneNetwork=new ListSingleSelection<String>("Default_Human_PPI_Network");
        diseaseNetwork.setSelectedValue("Disease_Similarity_Network_5");
        DiseaseGene.setSelectedValue(DiseaseGene.getPossibleValues().get(0));
        geneNetwork.setSelectedValue(geneNetwork.getPossibleValues().get(0));
	}

	

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		//choose disease network
		BasicData.PhenotypeNetwork = new ArrayList<Interaction>();
		BasicData.Phenotype2Genes = new TreeMap<String, Disease>();
		BasicData.NetworkPhenotypeSet = new TreeSet<String>();

		diseaseFile=diseaseNetwork.getSelectedValue();
		BasicData.PhenotypeNetwork_MIM = new Resource().loadPhenotypeNetwork("/" + diseaseNetwork.getSelectedValue() + ".sif");

		BasicData.NetworkPhenotypeSet_MIM = BasicData.NetworkPhenotypeSet;

		BasicData.PhenotypeNetwork = BasicData.PhenotypeNetwork_MIM;
		
		//choose disease_gene
		if (DiseaseGene.getSelectedValue().equals("Disease-gene from OMIM")) {
			BasicData.Phenotype2Genes = BasicData.Phenotype2Genes_MIM;
		} else {
			BasicData.Phenotype2Genes = BasicData.Phenotype2Genes_DisGenNET;
		}
		
		//choose gene
		BasicData.NetworkPhenotypeSet = BasicData.NetworkPhenotypeSet_MIM;
		
		updatePhenotypeNetworkNDiseaseList();
		
		//?????
//		if (this.chkDirected.isSelected()) {
//            MainData.isDirected = true;
//        } else {
//            MainData.isDirected = false;
//        }
		MainData.isDirected=false;
		MainData.curNetID = geneNetwork.getSelectedValue();
        
		HeterogeneousNetworkCheckUpdateTaskFactory heterogeneousNetworkCheckUpdateTaskFactory = new HeterogeneousNetworkCheckUpdateTaskFactory(cyNetworkManager);
        cyTaskManager.execute(heterogeneousNetworkCheckUpdateTaskFactory.createTaskIterator(network));

        System.out.println("Heterogeneous network is successfully created!");
       
        loadPhenotypeNetwork();

	}
	
	public static void updatePhenotypeNetworkNDiseaseList(){
        BasicData.UpdatedPhenotypeNetworkNode = new TreeMap<String, Node>();
        ArrayList<Disease> DiseaseList = new ArrayList<Disease>();
        for(String p: BasicData.NetworkPhenotypeSet){
            Disease d = new Disease();
            Node n = new Node();
            d.DiseaseID = p;
            n.NetworkID = p;

                if(BasicData.Phenotype2Genes_Full.containsKey(p)){
                    d.Prefix = BasicData.Phenotype2Genes_Full.get(p).Prefix;
                    d.Name = BasicData.Phenotype2Genes_Full.get(p).Name;
                    
                    if(BasicData.Phenotype2Genes.containsKey(p)){
                        d.KnownGenes = BasicData.Phenotype2Genes.get(p).KnownGenes;
                        d.KnownGeneList = BasicData.Phenotype2Genes.get(p).KnownGeneList;
                    }
                    n.Name = d.Name;
                    n.AlternateSymbols = d.KnownGenes;
                }

            
            n.Type = "Disease";
            //n.OfficialSymbol = n.Name;

            BasicData.UpdatedPhenotypeNetworkNode.put(p, n);
            DiseaseList.add(d);
        }
        System.out.println("BasicData.Phenotype2Genes.size(): " + BasicData.Phenotype2Genes.size());
        System.out.println("BasicData.UpdatedPhenotypeNetworkNode.size(): " + BasicData.UpdatedPhenotypeNetworkNode.size());

//        fillPhenotypeTable(DiseaseList);
//        System.out.println("Number of Diseases: " + DiseaseList.size());
//
        //loadPhenotypeNetwork();
    }
	
	 	 private void loadPhenotypeNetwork() {
	        int i;
	        
	        Iterator<CyNetwork> it = cyNetworkManager.getNetworkSet().iterator();
	        boolean PhenotypeNetworkLoaded=false;
	        String curPheNet = "";
	        while(it.hasNext()){
	            CyNetwork net = it.next();
	            if(net.getRow(net).get(CyNetwork.NAME, String.class).equals(DiseaseGene.getSelectedValue())){
	                curPheNet = net.getRow(net).get(CyNetwork.NAME, String.class);
	                PhenotypeNetworkLoaded=true;
	                break;
	            }
	        }
	        if(PhenotypeNetworkLoaded){
	            MainData.curPheNetID = curPheNet;
	            return;
	        }
	        LoadPhenotypeNetworkTaskFactory loadPhenotypeNetworkTaskFactory = new LoadPhenotypeNetworkTaskFactory(cyNetworkFactory, namingUtil, cyNetworkManager);
	        cySynchronousTaskManager.execute(loadPhenotypeNetworkTaskFactory.createTaskIterator());
	    }

	@ProvidesTitle
	public String getTitle() {
		return "Define a Heterogeneous Network";
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}



	@SuppressWarnings("unchecked")
	@Override
	public <R> R getResults(Class<? extends R> type) {
		// TODO Auto-generated method stub
			return (R) "Build Heterogeneous Network successfully";
		
	}
}
