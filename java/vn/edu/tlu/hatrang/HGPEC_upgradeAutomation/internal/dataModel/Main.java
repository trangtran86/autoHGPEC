/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel;


import java.util.*;

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Interaction;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Node;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.NodeInteraction;




public class Main {
    public static ArrayList<Interaction> NormalizedHeterogeneousNetwork=null;
    public static ArrayList<Interaction> UpdatedHeterogeneousNetwork=null;

    public static ArrayList<Node> UpdatedHeterogeneousNetworkNode=null;
    //public static Map<String, ArrayList<String>> Mim2Genes = new TreeMap<String, ArrayList<String>>();
    
    public static double lambda;
    public static double eta;
    public static double alpha=0.3;
    
    public static Map<String,ArrayList<NodeInteraction>> calculateOutgoingNeighbors(ArrayList<Interaction> Network){
        Map<String,ArrayList<NodeInteraction>> OutgoingNeighbors = new TreeMap<String,ArrayList<NodeInteraction>>();

        int i;
        Set<String> NetworkNodeSet = new TreeSet<String>();
        for(i=0;i<Network.size();i++){
            NetworkNodeSet.add(Network.get(i).NodeSrc);
            
        }
        
        Common.preprocessInteractionList(Network, "NodeSrc");
        Common.sortQuickInteractionListInAsc(Network);
        
        for(Iterator<String> it=NetworkNodeSet.iterator();it.hasNext();){
            String Gene = it.next();
            ArrayList<Integer> posarr = Common.searchUsingBinaryInteraction(Gene, Network);
            ArrayList<NodeInteraction> neighbors=new ArrayList<NodeInteraction>();
            if(posarr.size()>0){
                
                for(i=0;i<posarr.size();i++){
                    Interaction ina = Network.get(posarr.get(i));
                    neighbors.add(new NodeInteraction(ina.NodeDst, ina.Type, ina.Weight));
                }
                OutgoingNeighbors.put(Gene, neighbors);
            }else{
                OutgoingNeighbors.put(Gene, neighbors);
            }
        }

        
        return OutgoingNeighbors;
    }
    
    public static Map<String,ArrayList<NodeInteraction>> calculateIncomingNeighbors(ArrayList<Interaction> Network){
        Map<String,ArrayList<NodeInteraction>> IncomingNeighbors = new TreeMap<String,ArrayList<NodeInteraction>>();

        int i;
        Set<String> NetworkNodeSet = new TreeSet<String>();
        for(i=0;i<Network.size();i++){
            NetworkNodeSet.add(Network.get(i).NodeSrc);
            NetworkNodeSet.add(Network.get(i).NodeDst);
        }
        
        Common.preprocessInteractionList(Network, "NodeDst");
        Common.sortQuickInteractionListInAsc(Network);
        
        for(Iterator<String> it=NetworkNodeSet.iterator();it.hasNext();){
            String Gene = it.next();
            ArrayList<Integer> posarr = Common.searchUsingBinaryInteraction(Gene, Network);
            ArrayList<NodeInteraction> neighbors=new ArrayList<NodeInteraction>();
            if(posarr.size()>0){
                
                for(i=0;i<posarr.size();i++){
                    Interaction ina = Network.get(posarr.get(i));
                    neighbors.add(new NodeInteraction(ina.NodeSrc, ina.Type, ina.Weight));
                }
                IncomingNeighbors.put(Gene, neighbors);
            }else{
                IncomingNeighbors.put(Gene, neighbors);
            }
        }

        
        return IncomingNeighbors;
    }
    
}
