package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal;

import static org.cytoscape.work.ServiceProperties.COMMAND;
import static org.cytoscape.work.ServiceProperties.COMMAND_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_LONG_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_NAMESPACE;
import static org.cytoscape.work.ServiceProperties.ENABLE_FOR;
import static org.cytoscape.work.ServiceProperties.ID;
import static org.cytoscape.work.ServiceProperties.IN_MENU_BAR;
import static org.cytoscape.work.ServiceProperties.INSERT_SEPARATOR_BEFORE;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;
import static org.cytoscape.work.ServiceProperties.IN_CONTEXT_MENU;
import static org.cytoscape.work.ServiceProperties.TOOLTIP;
import static org.cytoscape.work.ServiceProperties.COMMAND_EXAMPLE_JSON;
import static org.cytoscape.work.ServiceProperties.COMMAND_SUPPORTS_JSON;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.io.read.CyNetworkReaderManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskManager;
import org.osgi.framework.BundleContext;


import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.CandidateGene;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.ChromosomeGene;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.CreateTrainingListResult;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.DiseaseFilter;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.GeneFilter;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.HGPECResourceImp;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.HGPECresource;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.RankedDisease;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.RankedGene;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.RankedResult;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.SuscepChroGene;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task.AboutActionHGPEC;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task.BuildNetworkTaskFactory;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task.CreateTrainingListTask;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task.CreateTrainingListTaskFactory;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task.ExamineRankedGenesandDiseasesTask;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task.ExamineRankedGenesandDiseasesTaskFactory;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task.HelpAction;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task.PCG_AllRemainingTask;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task.PCG_AllremainingTaskFactory;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task.PCG_NeighborChromosomeNetworkTask;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task.PCG_NeighborChromosomeNetworkTaskFactory;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task.PCG_NeighborNetworkTask;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task.PCG_NeighborNetworkTaskFactory;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task.PCG_SubceptibleChromosomeNetworkTask;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task.PCG_SubceptibleChromosomeNetworkTaskFactory;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task.PCG_UserDefineTask;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task.PCG_UserDefineTaskFactory;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task.PrioritizeTask;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task.PrioritizeTaskFactory;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task.SelectDiseaseTask;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task.SelectDiseaseTaskFactory;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task.VisualizeSubNetworkTaskFactory;



public class CyActivator extends AbstractCyActivator {
	public static final String MYAPP_COMMAND_NAMESPACE="autoHGPEC";
	@Override
	public void start(BundleContext context) throws Exception {
		CyApplicationManager cyApplicationManager = getService(context, CyApplicationManager.class);
        TaskManager cyTaskManager = getService(context, TaskManager.class);
        SynchronousTaskManager cySynchronousTaskManager = getService(context, SynchronousTaskManager.class);
        CyNetworkManager cyNetworkManager = getService(context, CyNetworkManager.class);
        CyNetworkReaderManager cyNetworkReaderManager = getService(context, CyNetworkReaderManager.class);
        CyNetworkFactory cyNetworkFactory = getService(context, CyNetworkFactory.class);
        CyNetworkNaming cyNetworkNaming = getService(context, CyNetworkNaming.class);
        CyNetworkViewManager cyNetworkViewManager = getService(context, CyNetworkViewManager.class);
        CyNetworkViewFactory cyNetworkViewFactory = getService(context, CyNetworkViewFactory.class);
        CyLayoutAlgorithmManager layoutManager = getService(context, CyLayoutAlgorithmManager.class);
        VisualMappingManager visualMappingManager = getService(context, VisualMappingManager.class);
        VisualStyleFactory visualStyleFactory = getService(context,VisualStyleFactory.class);
        
        VisualMappingFunctionFactory vmfFactoryC = getService(context, VisualMappingFunctionFactory.class, "(mapping.type=continuous)");
        VisualMappingFunctionFactory vmfFactoryP = getService(context, VisualMappingFunctionFactory.class, "(mapping.type=passthrough)");
        VisualMappingFunctionFactory vmfFactoryD = getService(context, VisualMappingFunctionFactory.class, "(mapping.type=discrete)");
        {
			Properties buildProps = new Properties();
			BuildNetworkTaskFactory buildTaskFactory = new BuildNetworkTaskFactory(cyTaskManager, cyNetworkManager, cyNetworkReaderManager, cyNetworkFactory, cyNetworkNaming, cySynchronousTaskManager);
			
			String buildNetworkDescription = "Step 1: Construct a Heterogeneous Network";
			String buildNetworkLongDescription="Step 1: Construct a heterogeneous network of diseases and genes by selecting:"
					+ "\n\n\t+ Phenotypic disease network: pre-installed 3 networks corresponding to 5, 10 or 15 nearest neighbors, which were extracted from a phenotypic disease similarity matrix data collected from (van Driel, et al., 2006)"
					+ "'Disease_Similarity_Network_5' (5,080 diseases and 19,729 interactions), 'Disease_Similarity_Network_10' (5,080 diseases and 38,467 interactions), 'Disease_Similarity_Network_15' (5,080 diseases and 56,870 interactions)"
					+ "\n\n\t+ Human protein interaction network: use Default_Human_PPI_Network (10,486 genes and 50,791 interactions) or other protein/gene interaction networks by importing to Cytoscape"
					+ "\n\n\t+ Disease-gene associations: select from OMIM or disGeNET";
			buildProps.setProperty(COMMAND_NAMESPACE, MYAPP_COMMAND_NAMESPACE);
			buildProps.setProperty(COMMAND, "step1_construct_network");
			buildProps.setProperty(COMMAND_DESCRIPTION,  buildNetworkDescription);
			buildProps.setProperty(COMMAND_LONG_DESCRIPTION, buildNetworkLongDescription);
			buildProps.setProperty(COMMAND_SUPPORTS_JSON, "true");
			buildProps.setProperty(PREFERRED_MENU, "Apps."+MYAPP_COMMAND_NAMESPACE);
			buildProps.setProperty(TITLE, "Step 1: Construct a Heterogeneous Network");
			buildProps.setProperty(IN_MENU_BAR, "true");
			buildProps.setProperty(MENU_GRAVITY, "1.0");
			buildProps.setProperty(TOOLTIP,  buildNetworkDescription);
			
			registerAllServices(context, buildTaskFactory, buildProps);
			//registerService(context, buildTaskFactory, NetworkTaskFactory.class, buildProps);	
		}
        
        {
			Properties selectProps = new Properties();
			SelectDiseaseTaskFactory selectTaskFactory = new SelectDiseaseTaskFactory(cyNetworkFactory, cyNetworkManager);

			String selectDiseaseDescription = "Step 2.1: Select disease";
			String selectDiseaseLongDescription="Step 2.1: Select disease of interest "
					+ "\n\nThis step is followed "
					+ "\n\n\t+ Step 1: Construct Heterogeneous Network at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/construct_network)"
		
					+ "\n\nInput keyword of a disease of interest to filter from Heterogeneous network";
	
			selectProps = new Properties();
			selectProps.setProperty(COMMAND_NAMESPACE, MYAPP_COMMAND_NAMESPACE);
			selectProps.setProperty(COMMAND, "step2_1_select_disease");
			selectProps.setProperty(COMMAND_DESCRIPTION,  selectDiseaseDescription);
			selectProps.setProperty(COMMAND_LONG_DESCRIPTION, selectDiseaseLongDescription);
			selectProps.setProperty(COMMAND_SUPPORTS_JSON, "true");
			selectProps.setProperty(COMMAND_EXAMPLE_JSON,  getDiseaseFilter());
			selectProps.setProperty(PREFERRED_MENU, "Apps."+MYAPP_COMMAND_NAMESPACE+".Step 2: Select a disease of interest");		
			selectProps.setProperty(TITLE, "1. Select disease");
			selectProps.setProperty(MENU_GRAVITY, "3.0");
			selectProps.setProperty(TOOLTIP,  selectDiseaseDescription);
			
			registerAllServices(context, selectTaskFactory, selectProps);
		}
        
        {
			Properties selectProps = new Properties();
			CreateTrainingListTaskFactory createTrainingListTaskFactory = new CreateTrainingListTaskFactory(cyNetworkFactory, cyNetworkManager);
			
			String createTraingingDescription = "Step 2.2: Select associated genes of disease";
			String createTrainingLongDescription="Step 2.2: Select associated genes with the disease"
					+ "\n\nThis step is followed "
					+ "\n\n\t+ Step 1: Construct Heterogeneous Network at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/construct_network)"
					+ "\n\n\t+ Step 2.1: Select disease at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/select_disease"
					+ "\n\nSelect disease and associated genes by either highlighting the row in node table or inputing the DiseaseID in step 2.1 to create training list";
			
			selectProps = new Properties();
			selectProps.setProperty(COMMAND_NAMESPACE, MYAPP_COMMAND_NAMESPACE);
			selectProps.setProperty(COMMAND, "step2_2_create_training_list");
			selectProps.setProperty(COMMAND_DESCRIPTION,  createTraingingDescription);
			selectProps.setProperty(COMMAND_LONG_DESCRIPTION, createTrainingLongDescription);
			selectProps.setProperty(COMMAND_SUPPORTS_JSON, "true");
			selectProps.setProperty(PREFERRED_MENU, "Apps."+MYAPP_COMMAND_NAMESPACE+".Step 2: Select a disease of interest");
			selectProps.setProperty(TITLE, "2. Create Training List");
			selectProps.setProperty(MENU_GRAVITY, "5.0");
			selectProps.setProperty(COMMAND_EXAMPLE_JSON,  getCreatedTrainingList());
			selectProps.setProperty(TOOLTIP,  createTraingingDescription);
			registerAllServices(context, createTrainingListTaskFactory, selectProps);
		}
        
        {
			Properties selectProps = new Properties();
			PCG_NeighborNetworkTaskFactory pcg_NeighborNetworkTaskFactory = new PCG_NeighborNetworkTaskFactory(cyNetworkFactory, cyNetworkManager, cyTaskManager, cySynchronousTaskManager);
			
			String PCG_NeighborNetworkDescription = "Step 3: Provide candidate sets - Neighbors of Training Genes in Gene Network";
			String PCG_NeighborNetworkLongDescription="Step 3: Provide candidate gene sets by selecting Neighbors of Training Genes in Gene Network"
					+ "\n\nThis step is followed "
					+ "\n\n\t+ Step 1: Construct Heterogeneous Network at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step1_construct_network)"
					+ "\n\n\t+ Step 2.1: Select disease at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step21_select_disease"
					+ "\n\n\t+ Step 2.2: Select disease of interest and associated genes 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step22_create_training_list"
					+ "\n\nThere are five ways to construct a candidate gene set: "
					+ "\n\n\t+ Neighbors of Training Genes in Gene Network"
					+ "\n\n\t+ Neighbors Of Training Genes in Chromosome (also known as Artificial Linkage Interval)"
					+ "\n\n\t+ All remaining genes in Gene Network"
					+ "\n\n\t+ Susceptible Chromosome Regions/Bands"
					+ "\n\n\t+ User-defined"
					+ "\n\nThis command uses 'Neighbor of Training Gene in Gene Network'. Refer to other ways by looking at the other commands";
			
			selectProps = new Properties();
			selectProps.setProperty(COMMAND_NAMESPACE, MYAPP_COMMAND_NAMESPACE);
			selectProps.setProperty(COMMAND, "step3_PCG_NBNetwork");
			selectProps.setProperty(COMMAND_DESCRIPTION,  PCG_NeighborNetworkDescription);
			selectProps.setProperty(COMMAND_LONG_DESCRIPTION, PCG_NeighborNetworkLongDescription);
			selectProps.setProperty(COMMAND_SUPPORTS_JSON, "true");
			selectProps.setProperty(COMMAND_EXAMPLE_JSON,  getCG_Network());
			selectProps.setProperty(PREFERRED_MENU, "Apps."+MYAPP_COMMAND_NAMESPACE+".Step 3: Provide Candidate Gene Set");
			selectProps.setProperty(TITLE, "Neighbors Of Training Genes in Gene Network");
			selectProps.setProperty(MENU_GRAVITY, "9.0");			
			selectProps.setProperty(TOOLTIP,  PCG_NeighborNetworkDescription);
			registerAllServices(context, pcg_NeighborNetworkTaskFactory, selectProps);
		}
        
		{
			Properties selectProps = new Properties();
			PCG_NeighborChromosomeNetworkTaskFactory provideCandidateGeneSetTaskFactory = new PCG_NeighborChromosomeNetworkTaskFactory(cyNetworkFactory, cyNetworkManager, cyTaskManager);
			String PCG_NeighborNetworkDescription = "Step 3: Select candidate sets - Neighbors of Training Genes in Chromosome";
			String PCG_NeighborNetworkLongDescription="Step 3: Provide candidate gene sets by selecting Neighbors of Training Genes in Chromosome"
					+ "\n\nThis step is followed "
					+ "\n\n\t+ Step 1: Build Heterogeneous Network at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step1_construct_network)"
					+ "\n\n\t+ Step 2.1: Select disease at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step21_select_disease"
					+ "\n\n\t+ Step 2.2: Select disease of interest and associated genes 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step22_create_training_list"
					+ "\n\nThere are five ways to construct a candidate gene set: "
					+ "\n\n\t+ Neighbors of Training Genes in Gene Network"
					+ "\n\n\t+ Neighbors Of Training Genes in Chromosome (also known as Artificial Linkage Interval)"
					+ "\n\n\t+ All remaining genes in Gene Network"
					+ "\n\n\t+ Susceptible Chromosome Regions/Bands"
					+ "\n\n\t+ User-defined"
					+ "\n\nThis command uses 'Neighbor of Training Gene in Chromosome'. Refer to other ways by looking at the other commands";
			
			selectProps = new Properties();
			selectProps.setProperty(COMMAND_NAMESPACE, MYAPP_COMMAND_NAMESPACE);
			selectProps.setProperty(COMMAND, "step3_PCG_NBChromosome");
			selectProps.setProperty(COMMAND_DESCRIPTION,  PCG_NeighborNetworkDescription);
			selectProps.setProperty(COMMAND_LONG_DESCRIPTION, PCG_NeighborNetworkLongDescription);
			selectProps.setProperty(COMMAND_SUPPORTS_JSON, "true");
			selectProps.setProperty(COMMAND_EXAMPLE_JSON,  getCG_Chromosome());
			selectProps.setProperty(PREFERRED_MENU, "Apps."+MYAPP_COMMAND_NAMESPACE+".Step 3: Provide Candidate Gene Set");
			selectProps.setProperty(TITLE, "Neighbors Of Training Genes in Chromosome ");
			selectProps.setProperty(MENU_GRAVITY, "8.0");
			selectProps.setProperty(TOOLTIP,  PCG_NeighborNetworkDescription);
			registerAllServices(context, provideCandidateGeneSetTaskFactory, selectProps);
		}
		{
			Properties selectProps = new Properties();
			PCG_AllremainingTaskFactory provideCandidateGeneSetTaskFactory = new PCG_AllremainingTaskFactory(cyNetworkFactory, cyNetworkManager, cyTaskManager);
			
			String PCG_NeighborNetworkDescription = "Step 3: Select candidate sets - All remaining genes in Gene Network";
			String PCG_NeighborNetworkLongDescription="Step 3: Provide candidate gene sets by selecting All remaining genes in Gene Network"
					+ "\n\nThis step is followed "
					+ "\n\n\t+ Step 1: Construct Heterogeneous Network at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step1_construct_network)"
					+ "\n\n\t+ Step 2.1: Select disease at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step21_select_disease"
					+ "\n\n\t+ Step 2.2: Select disease of interest and associated genes 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step22_create_training_list"
					+ "\n\nThere are five ways to construct a candidate gene set: "
					+ "\n\n\t+ Neighbors of Training Genes in Gene Network"
					+ "\n\n\t+ Neighbors Of Training Genes in Chromosome (also known as Artificial Linkage Interval)"
					+ "\n\n\t+ All remaining genes in Gene Network"
					+ "\n\n\t+ Susceptible Chromosome Regions/Bands"
					+ "\n\n\t+ User-defined"
					+ "\n\nThis command uses 'All remaining genes in Gene Network'. Refer to other ways by looking at the other commands";
			
			
			selectProps = new Properties();
			selectProps.setProperty(COMMAND_NAMESPACE, MYAPP_COMMAND_NAMESPACE);
			selectProps.setProperty(COMMAND, "step3_PCG_allRemaining");
			selectProps.setProperty(COMMAND_DESCRIPTION,  PCG_NeighborNetworkDescription);
			selectProps.setProperty(COMMAND_LONG_DESCRIPTION, PCG_NeighborNetworkLongDescription);
			selectProps.setProperty(COMMAND_SUPPORTS_JSON, "true");
			selectProps.setProperty(COMMAND_EXAMPLE_JSON,  getCG_AllRemaining());
			selectProps.setProperty(PREFERRED_MENU, "Apps."+MYAPP_COMMAND_NAMESPACE+".Step 3: Provide Candidate Gene Set");
			selectProps.setProperty(TITLE, "All remaining genes in Gene Network");
			selectProps.setProperty(MENU_GRAVITY, "7.0");
			selectProps.setProperty(TOOLTIP,  PCG_NeighborNetworkDescription);
			registerAllServices(context, provideCandidateGeneSetTaskFactory, selectProps);
		}
		{
			Properties selectProps = new Properties();
			PCG_SubceptibleChromosomeNetworkTaskFactory provideCandidateGeneSetTaskFactory = new PCG_SubceptibleChromosomeNetworkTaskFactory(cyNetworkFactory, cyNetworkManager, cyTaskManager);
			String PCG_NeighborNetworkDescription = "Step 3: Select candidate sets - Susceptible Chromosome Regions/Bands";
			String PCG_NeighborNetworkLongDescription="Step 3: Provide candidate gene sets by selecting Susceptible Chromosome Regions/Bands"
					+ "\n\nThis step is followed "
					+ "\n\n\t+ Step 1: Construct Heterogeneous Network at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step1_construct_network)"
					+ "\n\n\t+ Step 2.1: Select disease at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step21_select_disease"
					+ "\n\n\t+ Step 2.2: Select disease of interest and associated genes 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step22_create_training_list"
					+ "\n\nThere are five ways to construct a candidate gene set: "
					+ "\n\n\t+ Neighbors of Training Genes in Gene Network"
					+ "\n\n\t+ Neighbors Of Training Genes in Chromosome (also known as Artificial Linkage Interval)"
					+ "\n\n\t+ All remaining genes in Gene Network"
					+ "\n\n\t+ Susceptible Chromosome Regions/Bands"
					+ "\n\n\t+ User-defined"
					+ "\n\nThis command uses 'Susceptible Chromosome Regions/Bands'. Refer to other ways by looking at the other commands";
			
			
			selectProps = new Properties();
			selectProps.setProperty(COMMAND_NAMESPACE, MYAPP_COMMAND_NAMESPACE);
			selectProps.setProperty(COMMAND, "step3_PCG_suscepChromo");
			selectProps.setProperty(COMMAND_DESCRIPTION,  PCG_NeighborNetworkDescription);
			selectProps.setProperty(COMMAND_LONG_DESCRIPTION, PCG_NeighborNetworkLongDescription);
			selectProps.setProperty(COMMAND_SUPPORTS_JSON, "true");
			selectProps.setProperty(COMMAND_EXAMPLE_JSON,  getCG_SuscepChromosome());
			selectProps.setProperty(PREFERRED_MENU, "Apps."+MYAPP_COMMAND_NAMESPACE+".Step 3: Provide Candidate Gene Set");
			selectProps.setProperty(TITLE, "Susceptible Chromosome Regions/Bands");
			selectProps.setProperty(MENU_GRAVITY, "10.0");
			selectProps.setProperty(TOOLTIP,  PCG_NeighborNetworkDescription);
			registerAllServices(context, provideCandidateGeneSetTaskFactory, selectProps);
		}
		{
			Properties selectProps = new Properties();
			PCG_UserDefineTaskFactory provideCandidateGeneSetTaskFactory = new PCG_UserDefineTaskFactory(cyNetworkFactory, cyNetworkManager, cyTaskManager);
			String PCG_NeighborNetworkDescription = "Step 3: Select candidate sets - User-defined";
			String PCG_NeighborNetworkLongDescription="Step 3: Provide candidate gene sets by selecting User-defined"
					+ "\n\nThis step is followed "
					+ "\n\n\t+ Step 1: Build Heterogeneous Network at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step1_build_network)"
					+ "\n\n\t+ Step 2.1: Select disease at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step21_select_disease"
					+ "\n\n\t+ Step 2.2: Select disease of interest and associated genes 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step22_create_training_list"
					+ "\n\nThere are five ways to construct a candidate gene set: "
					+ "\n\n\t+ Neighbors of Training Genes in Gene Network"
					+ "\n\n\t+ Neighbors Of Training Genes in Chromosome (also known as Artificial Linkage Interval)"
					+ "\n\n\t+ All remaining genes in Gene Network"
					+ "\n\n\t+ Susceptible Chromosome Regions/Bands"
					+ "\n\n\t+ User-defined"
					+ "\n\nThis command uses 'User-defined'. Refer to other ways by looking at the other commands";
			
			
			selectProps = new Properties();
			selectProps.setProperty(COMMAND_NAMESPACE, MYAPP_COMMAND_NAMESPACE);
			selectProps.setProperty(COMMAND, "step3_PCG_userDefined");
			selectProps.setProperty(COMMAND_DESCRIPTION,  PCG_NeighborNetworkDescription);
			selectProps.setProperty(COMMAND_LONG_DESCRIPTION, PCG_NeighborNetworkLongDescription);
			selectProps.setProperty(COMMAND_SUPPORTS_JSON, "true");
			selectProps.setProperty(COMMAND_EXAMPLE_JSON,  getCG_UserDefinedExample());
			selectProps.setProperty(PREFERRED_MENU, "Apps."+MYAPP_COMMAND_NAMESPACE+".Step 3: Provide Candidate Gene Set");
			selectProps.setProperty(TITLE, "User-defined");
			selectProps.setProperty(MENU_GRAVITY, "11.0");
			selectProps.setProperty(TOOLTIP,  PCG_NeighborNetworkDescription);
			registerService(context, provideCandidateGeneSetTaskFactory, NetworkTaskFactory.class, selectProps);
		}
		
		{
			Properties selectProps = new Properties();
			PrioritizeTaskFactory prioritizeTaskFactory = new PrioritizeTaskFactory(cyNetworkFactory, cyNetworkManager, cyTaskManager, cySynchronousTaskManager);
			String PCG_NeighborNetworkDescription = "Step 4: Prioritize candidate genes and diseases";
			String PCG_NeighborNetworkLongDescription="Step 4: Prioritize candidate genes and diseases in the heterogeneous network"
					+ "\n\nThis step is followed "
					+ "\n\n\t+ Step 1: Construct Heterogeneous Network at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step1_construct_network)"
					+ "\n\n\t+ Step 2.1: Select disease at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step21_select_disease"
					+ "\n\n\t+ Step 2.2: Select disease of interest and associated genes at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step22_create_training_list"
					+ "\n\n\t+ Step 3: Select candidate sets at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step3_PCG"
					+ "\n\nSet three parameters (i.e., back-probability (alpha), jumping probability (lamda) and subnetwork (Disease/Gene) importance (eta)) of RWRH algorithm."
					+ " Please refer to (Li and Patra, 2010) for best parameter setting "
					+ "\n\nThen rank all candidate genes and diseases in the heterogeneous network";
			
			selectProps = new Properties();
			selectProps.setProperty(COMMAND_NAMESPACE, MYAPP_COMMAND_NAMESPACE);
			selectProps.setProperty(COMMAND, "step4_prioritize");
			selectProps.setProperty(COMMAND_DESCRIPTION,  PCG_NeighborNetworkDescription);
			selectProps.setProperty(COMMAND_LONG_DESCRIPTION, PCG_NeighborNetworkLongDescription);
			selectProps.setProperty(COMMAND_SUPPORTS_JSON, "true");
			selectProps.setProperty(COMMAND_EXAMPLE_JSON,  getRankExample());
			selectProps.setProperty(PREFERRED_MENU, "Apps."+MYAPP_COMMAND_NAMESPACE);
			selectProps.setProperty(TITLE, "Step 4: Prioritize");
			selectProps.setProperty(MENU_GRAVITY, "15.0");
			selectProps.setProperty(TOOLTIP,  PCG_NeighborNetworkDescription);
			registerAllServices(context, prioritizeTaskFactory, selectProps);
		}
		
		{
			Properties infoProps = new Properties();
			ExamineRankedGenesandDiseasesTaskFactory selectTaskFactory= new ExamineRankedGenesandDiseasesTaskFactory(cyNetworkFactory, cyNetworkManager);
			
			String PCG_NeighborNetworkDescription = "Step 5.1: Evidence Search";
			String PCG_NeighborNetworkLongDescription="Step 5.1: This function is to collect evidences and anotations for associations between highly ranked candidate genes/diseases and the disease of interest."
					+ "\n\nThis step is followed "
					+ "\n\n\t+ Step 1: Construct Heterogeneous Network at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step1_construct_network)"
					+ "\n\n\t+ Step 2.1: Select disease at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step21_select_disease"
					+ "\n\n\t+ Step 2.2: Select disease of interest and associated genes at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step22_create_training_list"
					+ "\n\n\t+ Step 3: Select candidate sets at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step3_PCG"
					+ "\n\n\t+ Step 4: Prioritize candidate genes and diseases at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step4_prioritize"
					
					+ "\n\nSelecting by highlighting candidate genes in the ranked genes table for evidence collection. "
					+ "This set of genes will be annotated with pathways, protein complexes, dissease ontology and gene ontology terms"
					+ "Note that IEA evidence is included.";
			
			
			
			infoProps = new Properties();
			infoProps.setProperty(COMMAND_NAMESPACE, MYAPP_COMMAND_NAMESPACE);
			infoProps.setProperty(COMMAND, "step5_1_search_evidences");
			infoProps.setProperty(COMMAND_DESCRIPTION,  PCG_NeighborNetworkDescription);
			infoProps.setProperty(COMMAND_LONG_DESCRIPTION, PCG_NeighborNetworkLongDescription);
			infoProps.setProperty(COMMAND_SUPPORTS_JSON, "true");
			infoProps.setProperty(COMMAND_EXAMPLE_JSON,  getRankExample());
			infoProps.setProperty(INSERT_SEPARATOR_BEFORE, "true");
			infoProps.setProperty(PREFERRED_MENU, "Apps."+MYAPP_COMMAND_NAMESPACE+".Step 5: Examine ranked Genes and Diseases");
			infoProps.setProperty(TITLE, "1. Search Evidences");
			infoProps.setProperty(MENU_GRAVITY, "21.0");
			infoProps.setProperty(TOOLTIP,  PCG_NeighborNetworkDescription);
			registerService(context, selectTaskFactory, NetworkTaskFactory.class, infoProps);
		}
		
		{
			Properties infoProps = new Properties();
			VisualizeSubNetworkTaskFactory selectTaskFactory = new VisualizeSubNetworkTaskFactory(cyNetworkFactory, cyNetworkNaming, cyNetworkManager, layoutManager, cyTaskManager, cyNetworkViewFactory, cyNetworkViewManager, visualMappingManager, visualStyleFactory, vmfFactoryP, vmfFactoryD, vmfFactoryC);
			
			String PCG_NeighborNetworkDescription = "Step 5.2: Visualization";
			String PCG_NeighborNetworkLongDescription="Step 5.2: Prioritize candidate genes and diseases in the heterogeneous network"
					+ "\n\nThis step is followed "
					+ "\n\n\t+ Step 1: Construct Heterogeneous Network at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step1_construct_network)"
					+ "\n\n\t+ Step 2.1: Select disease at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step21_select_disease"
					+ "\n\n\t+ Step 2.2: Select disease of interest and associated genes at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step22_create_training_list"
					+ "\n\n\t+ Step 3: Select candidate sets at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step3_PCG"
					+ "\n\n\t+ Step 4: Prioritize candidate genes and diseases at 'POST /v1/commands/"+MYAPP_COMMAND_NAMESPACE+"/step4_prioritize"
					
					+ "\n\nRelationships between selected genes and diseases in the heterogeneous network are visualized."
					+ "Highlighting the selected candidate genes and candidate diseases in the table to visualizing.";
			
			infoProps = new Properties();
			infoProps.setProperty(COMMAND_NAMESPACE, MYAPP_COMMAND_NAMESPACE);
			infoProps.setProperty(COMMAND, "step5_2_visualize");
			infoProps.setProperty(COMMAND_DESCRIPTION,  PCG_NeighborNetworkDescription);
			infoProps.setProperty(COMMAND_LONG_DESCRIPTION, PCG_NeighborNetworkLongDescription);
			infoProps.setProperty(COMMAND_SUPPORTS_JSON, "true");
			infoProps.setProperty(COMMAND_EXAMPLE_JSON,  getRankExample());
			
			infoProps.setProperty(PREFERRED_MENU, "Apps."+MYAPP_COMMAND_NAMESPACE+".Step 5: Examine ranked Genes and Diseases");
			infoProps.setProperty(TITLE, "2. Visualize");
			infoProps.setProperty(MENU_GRAVITY, "23.0");
			infoProps.setProperty(TOOLTIP,  PCG_NeighborNetworkDescription);
			registerService(context, selectTaskFactory, NetworkTaskFactory.class, infoProps);
		}
		
		HelpAction helpAction = new HelpAction();
        AboutActionHGPEC aboutActionHGPEC = new AboutActionHGPEC();
		try{
			registerService(context, new HGPECResourceImp(), HGPECresource.class,new Properties());
			registerService(context, helpAction, CyAction.class, new Properties());
	        registerService(context, aboutActionHGPEC, CyAction.class, new Properties());

		}
		catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("CyRest sample registerService completed");
	}
	
	public static final String getDiseaseFilter(){
		ArrayList<DiseaseFilter> df=new ArrayList<>();
		df.add(new DiseaseFilter("BREAST CANCER","MIM114480","C0346153","5888, 3845, 83990, 8493, 580, 841"));
		return SelectDiseaseTask.getJson(df);
	}
	
	public static final String getCreatedTrainingList(){
		ArrayList<DiseaseFilter> df=new ArrayList<>();
		ArrayList<GeneFilter> gf=new ArrayList<>();
		df.add(new DiseaseFilter("BREAST CANCER","MIM114480","C0346153","5888, 3845, 83990, 8493, 580, 841"));
		gf.add(new GeneFilter("", "112000", "CHEK2", "RP11-436C9.1, CDS1"));
		
		return CreateTrainingListTask.getJson(new CreateTrainingListResult(gf,df));
	}
	
	public static final String getCG_Network(){
		ArrayList<CandidateGene> df=new ArrayList<>();
		df.add(new CandidateGene("7334", "UBE2N", "", 1));
		return PCG_NeighborNetworkTask.getJson(df);
	}
	
	public static final String getCG_Chromosome(){
		ArrayList<ChromosomeGene> df=new ArrayList<>();
		df.add(new ChromosomeGene("10577", "NPC2", 74486192, 74494177, "a24.3", "14"));
		return PCG_NeighborChromosomeNetworkTask.getJson(df);
	}
	
	public static final String getCG_AllRemaining(){
		ArrayList<CandidateGene> df=new ArrayList<>();
		df.add(new CandidateGene("7334", "UBE2N", "", 1));
		return PCG_AllRemainingTask.getJson(df);
	}
	
	public static final String getCG_SuscepChromosome(){
		ArrayList<SuscepChroGene> df=new ArrayList<>();
		df.add(new SuscepChroGene("10577", "NPC2", 74486192, 74494177, "a24.3", "14",true));
		return PCG_SubceptibleChromosomeNetworkTask.getJson(df);
	}
	
	public static final String getRankExample(){
		ArrayList<RankedDisease> df=new ArrayList<>();
		ArrayList<RankedGene> gf=new ArrayList<>();
		df.add(new RankedDisease("BREAST CANCER","MIM114480","C0346153","5888, 3845, 83990, 8493, 580, 841",1,"Disease",true,false,"0.40831814"));
		gf.add(new RankedGene("", "112000", "CHEK2", "RP11-436C9.1, CDS1",1,"Gene/Protein",true,false,"0.01817476"));
		
		return PrioritizeTask.getJson(new RankedResult(gf,df));
	}
	
	public static final String getCG_UserDefinedExample(){
		ArrayList<RankedGene> gf=new ArrayList<>();
		gf.add(new RankedGene("", "112000", "CHEK2", "RP11-436C9.1, CDS1","Gene/Protein",true,false));
		
		return PCG_UserDefineTask.getJson(gf);
	}
}
