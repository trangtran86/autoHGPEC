/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.task;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import javax.swing.JOptionPane;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.RESTmodel.ErrorMessage;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.BasicData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.Common;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel.MainData;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Node;



/**
 *
 * @author "MinhDA"
 */
public class LoadPhenotypeNetworkTask implements Task {

    private volatile boolean interrupted = false;
    private CyNetworkFactory cyNetworkFactory;
    private CyNetworkNaming cyNetworkNaming;
    CyNetworkManager cyNetworkManager;
    public static CyNetwork curNet;

    public LoadPhenotypeNetworkTask(CyNetworkFactory cyNetworkFactory, CyNetworkNaming cyNetworkNaming, CyNetworkManager cyNetworkManager) {
        this.cyNetworkFactory = cyNetworkFactory;
        this.cyNetworkNaming = cyNetworkNaming;
        this.cyNetworkManager = cyNetworkManager;
    }

    
    
    @Override
    public void run(TaskMonitor taskMonitor) {
        try {

            taskMonitor.setStatusMessage("Loading Disease Similarity Network...!");
            int i;

            String fileName = BuildNetworkTask.diseaseFile;
            InputStream in = getClass().getResourceAsStream("/" + fileName + ".sif");
            CyNetwork PheNet;
            if (!fileName.equals("Default_Human_PPI_Network")){
            	 PheNet= Common.loadNetworkFromFile(cyNetworkFactory, cyNetworkNaming, cyNetworkManager, in, fileName);
            }else{
            	PheNet=Common.getNetworkByName(cyNetworkManager, "Default_Human_PPI_Network");
            }
//            CyNetwork PheNet = Cytoscape.createNetworkFromURL(getClass().getResource("Resources/" + pnlKnownGeneProvisionHGPEC.cboPhenotypeDisease.getSelectedItem().toString() + ".sif"), false);

//            Cytoscape.getNetworkAttributes().setAttribute(PheNet.getIdentifier(), "Type", "Disease Network");
            PheNet.getDefaultNetworkTable().getRow(PheNet.getSUID()).set("Type", "Disease Network");

            List<CyNode> nl = PheNet.getNodeList();
//            List<giny.model.Node> nl = Cytoscape.getNetwork(PheNet.getIdentifier()).nodesList();

            for (CyNode node : nl) {
                CyRow nodeAtt = PheNet.getDefaultNodeTable().getRow(node.getSUID());
                nodeAtt.set("Type", "Disease");
                String nodeName = (nodeAtt.getRaw("Id").toString());
                if (BasicData.UpdatedPhenotypeNetworkNode.containsKey(nodeName)) {
                    Node dn = BasicData.UpdatedPhenotypeNetworkNode.get(nodeName);
//                    nodeAtt.set("OfficialSymbol", dn.Name);
                    nodeAtt.set("name", dn.Name);
                    //n
//                    nodeAtt.set("OfficialSymbol", dn.Name);
                }
            }

//            for(i=0;i<nl.size();i++){
//                Cytoscape.getNodeAttributes().setAttribute(nl.get(i).getIdentifier(), "Type", "Disease");
//                if(BasicData.UpdatedPhenotypeNetworkNode.containsKey(nl.get(i).getIdentifier())){
//                    Node dn = BasicData.UpdatedPhenotypeNetworkNode.get(nl.get(i).getIdentifier());
//                    Cytoscape.getNodeAttributes().setAttribute(nl.get(i).getIdentifier(), "Name", dn.Name);
//                    Cytoscape.getNodeAttributes().setAttribute(nl.get(i).getIdentifier(), "OfficialSymbol", dn.Name);
//                }
//            }
            MainData.curPheNetID = fileName;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while loading Disease Network: " + e.toString());
            throw new NotFoundException("Data resources not found",Response.status(Response.Status.NOT_FOUND)
            		.type(MediaType.APPLICATION_JSON).entity(new ErrorMessage("Error while loading data resources")).build()); 
       
        }
    }

    @Override
    public void cancel() {
        this.interrupted = true;
    }

}
