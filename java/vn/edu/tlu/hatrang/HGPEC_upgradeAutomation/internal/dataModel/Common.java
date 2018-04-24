/*
 * In progress
 */
package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.dataModel;

import java.awt.Color;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;

import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Complex;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.DO;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Disease;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.GeneRIF;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Interaction;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Node;
import vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model.Pathway;


/**
 *
 * @author "MinhDA"
 */
public class Common {

    public static CyNetwork getNetworkByName(CyNetworkManager cyNetworkManager, String name) {
        for (CyNetwork net : cyNetworkManager.getNetworkSet()) {
            if (net.getRow(net).get(CyNetwork.NAME, String.class).equals(name)) {
                return net;
            }
        }
        return null;
    }

    public static CyNode getNodeById(String Id) {
        return BasicData.nodeIdMap.get(Id);
    }

    public static CyNetwork loadNetworkFromFile(CyNetworkFactory cyNetworkFactory, CyNetworkNaming namingUtil, CyNetworkManager cyNetworkManager, InputStream in, String name) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = br.readLine();
         
            CyNetwork subNetwork = cyNetworkFactory.createNetwork();
            subNetwork.getRow(subNetwork).set("name", name);
//            subNetwork.getRow(subNetwork).set(CyNetwork.NAME,namingUtil.getSuggestedNetworkTitle(name));
            subNetwork.getDefaultNetworkTable().createColumn("Type", String.class, false);
            subNetwork.getRow(subNetwork).set("Type", "Gene/Protein Network");
            subNetwork.getDefaultNodeTable().createColumn("Id", String.class, false);
            
            while (line != null) {
                CyNode aNode, bNode;
                String str[] = line.split("\\s", 3);
                if (BasicData.nodeIdMap.containsKey(str[0])) {
                    aNode = BasicData.nodeIdMap.get(str[0]);
                } else {
                    aNode = subNetwork.addNode();
                    CyRow aNodeAtt = subNetwork.getDefaultNodeTable().getRow(aNode.getSUID());
                    aNodeAtt.set("Id", str[0]);
                    aNodeAtt.set("shared name", str[0]);
                    BasicData.nodeIdMap.put(str[0], aNode);
                }

                if (BasicData.nodeIdMap.containsKey(str[2])) {
                    bNode = BasicData.nodeIdMap.get(str[2]);
                } else {
                    bNode = subNetwork.addNode();
                    CyRow bNodeAtt = subNetwork.getDefaultNodeTable().getRow(bNode.getSUID());
                    bNodeAtt.set("Id", str[2]);
                    bNodeAtt.set("shared name", str[2]);
                    BasicData.nodeIdMap.put(str[2], bNode);
                }

                CyEdge edge = subNetwork.addEdge(aNode, bNode, false);
//                CyRow edgeAtt = subNetwork.getDefaultEdgeTable().getRow((Long)edge.getSUID());
                CyRow edgeAtt = subNetwork.getRow(edge);
                edgeAtt.set("interaction", str[1]);
                line = br.readLine();
            }
            subNetwork.getDefaultNodeTable().createColumn("EntrezID", String.class, false);
            subNetwork.getDefaultNodeTable().createColumn("OfficialSymbol", String.class, false);
            subNetwork.getDefaultNodeTable().createColumn("AlternateSymbols", String.class, false);
            subNetwork.getDefaultNodeTable().createColumn("Type", String.class, false);
            
            subNetwork.getDefaultEdgeTable().createColumn("NodeSrc", Long.class, false);
            subNetwork.getDefaultEdgeTable().createColumn("NodeDst", Long.class, false);

            subNetwork.getDefaultNetworkTable().createColumn("Status", String.class, false);
            cyNetworkManager.addNetwork(subNetwork);
            return subNetwork;
        } catch (FileNotFoundException ex) {
            System.out.println("File not found");
            return null;
        } catch (IOException ex) {
            System.out.println("IO error");
            return null;
        } catch (Exception ex) {
            System.out.println("Exception: " + ex);
            return null;
        }
    }

    public static void preprocessGeneList(ArrayList<Node> Genes, String By) {
        int i;
        for (i = 0; i < Genes.size(); i++) {
            if (By.compareTo("OfficialSymbol") == 0) {
                Genes.get(i).Index = Genes.get(i).OfficialSymbol;
            } else if (By.compareTo("EntrezID") == 0) {
                Genes.get(i).Index = Genes.get(i).EntrezID;
            } else if (By.compareTo("UniProtAC") == 0) {
                Genes.get(i).Index = Genes.get(i).UniProtAC;
            } else if (By.compareTo("Organism") == 0) {
                Genes.get(i).Index = Genes.get(i).Organism;
            } else if (By.compareTo("Tag") == 0) {
                Genes.get(i).Index = Genes.get(i).Tag;
            } else {
                Genes.get(i).Index = Genes.get(i).NetworkID;
            }
        }
    }

    public static void sortQuickNodeListInAsc(ArrayList<Node> Genes) {

        Common.quickSortGene(Genes, 0, Genes.size() - 1);
    }

    public static void quickSortGene(ArrayList<Node> A, int lower, int upper) {
        int i, j;
        String x;
        x = A.get((lower + upper) / 2).Index;
        i = lower;
        j = upper;
        while (i <= j) {
            while (A.get(i).Index.compareTo(x) < 0) {
                i++;
            }
            while (A.get(j).Index.compareTo(x) > 0) {
                j--;
            }
            if (i <= j) {
                Node temp = new Node();
                temp = A.get(i);
                A.set(i, A.get(j));
                A.set(j, temp);

                i++;
                j--;
            }
            //System.out.println("i = " + i + ", j = " + j);
        }
        if (j > lower) {
            quickSortGene(A, lower, j);
        }
        if (i < upper) {
            quickSortGene(A, i, upper);
        }
    }

    public static ArrayList<Integer> searchUsingBinaryNodeArray(String searchterm, ArrayList<Node> List) {
        ArrayList<Integer> posarr = new ArrayList<Integer>();
        try {
            int lo, high;
            lo = 0;
            high = List.size();
            int pos = Common.searchUsingBinaryNodeDetail(searchterm, List, lo, high);
            if (pos == -1) {
                return posarr;
            }

            posarr.add(pos);
            int postemp1 = pos;
            int postemp2 = pos;
            boolean exist1, exist2;
            while (true) {
                exist1 = false;
                postemp1++;
                if (postemp1 < List.size() && List.get(postemp1).Index.compareTo(searchterm) == 0) {
                    posarr.add(postemp1);
                    exist1 = true;
                }
                if (exist1 == false) {
                    break;
                }
            }
            while (true) {
                exist2 = false;
                postemp2--;
                if (postemp2 >= 0 && List.get(postemp2).Index.compareTo(searchterm) == 0) {
                    posarr.add(postemp2);
                    exist2 = true;
                }
                if (exist2 == false) {
                    break;
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error while searching in Gene/Protein Information database: " + e.toString());
        }
        return posarr;
    }

    public static int searchUsingBinaryNodeDetail(String key, ArrayList<Node> a, int lo, int hi) {
        // possible key indices in [lo, hi)
        if (hi <= lo) {
            return -1;
        }
        int mid = lo + (hi - lo) / 2;
        int cmp = a.get(mid).Index.compareTo(key);
        if (cmp > 0) {
            return searchUsingBinaryNodeDetail(key, a, lo, mid);
        } else if (cmp < 0) {
            return searchUsingBinaryNodeDetail(key, a, mid + 1, hi);
        } else {
            return mid;
        }
    }

    public static void highlightNodesInNetwork(CyNetworkManager cyNetworkManager, ArrayList<Node> Genes) {
        int i, j;
        Set<String> NodeSet = new TreeSet<String>();
        for (i = 0; i < Genes.size(); i++) {
            NodeSet.add(Genes.get(i).NetworkID);
        }

//        List<CyNode> listNodes = Cytoscape.getNetwork(MainData.curNetID).nodesList();
        CyNetwork curNetwork = getNetworkByName(cyNetworkManager, MainData.curNetID);
        List<CyNode> listNodes = curNetwork.getNodeList();
        for (CyNode node : listNodes) {
            curNetwork.getRow(node).set("selected", false);
        }
//        Cytoscape.getNetwork(MainData.curNetID).setSelectedNodeState(listNodes, false);

        List<CyNode> listSelectedNodes = new ArrayList<CyNode>();

        for (i = 0; i < listNodes.size(); i++) {
            if (NodeSet.contains(curNetwork.getRow(listNodes.get(i)).getRaw("Id"))) {
                listSelectedNodes.add(listNodes.get(i));
            }

        }
        for (CyNode node : listSelectedNodes) {
            curNetwork.getRow(node).set("selected", true);
        }
//        Cytoscape.getNetwork(MainData.curNetID).setSelectedNodeState(listSelectedNodes, true);
        //TODO after selected node
//        Cytoscape.getNetworkView(MainData.curNetID).redrawGraph(true, true);
    }

    public static void showOMIMDetail(String OMIMID) {
        if (OMIMID == null || OMIMID.compareTo("") == 0) {
            return;
        }
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI("http://www.ncbi.nlm.nih.gov/omim/" + OMIMID));
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
    }

    public static void showGeneDetailInEntrez(String EntrezID) {
        if (EntrezID == null || EntrezID.compareTo("") == 0) {
            return;
        }
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI("http://www.ncbi.nlm.nih.gov/gene/" + EntrezID));
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
    }

    public static void showGeneDetailInUniProt(String UniProtAC) {
        if (UniProtAC == null || UniProtAC.compareTo("") == 0) {
            return;
        }
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI("http://www.uniprot.org/uniprot/" + UniProtAC));
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
    }

    public static void preprocessInteractionList(ArrayList<Interaction> Interactions, String By) {
        int i;
        for (i = 0; i < Interactions.size(); i++) {
            if (By.compareTo("NodeSrc") == 0) {
                Interactions.get(i).Index = Interactions.get(i).NodeSrc;
            } else if (By.compareTo("NodeDst") == 0) {
                Interactions.get(i).Index = Interactions.get(i).NodeDst;
            }
        }
    }

    public static void sortQuickInteractionListInAsc(ArrayList<Interaction> Interactions) {

        Common.quickSortInteraction(Interactions, 0, Interactions.size() - 1);
    }

    public static void quickSortInteraction(ArrayList<Interaction> A, int lower, int upper) {
        int i, j;
        String x;
        x = A.get((lower + upper) / 2).Index;
        i = lower;
        j = upper;
        while (i <= j) {
            while (A.get(i).Index.compareTo(x) < 0) {
                i++;
            }
            while (A.get(j).Index.compareTo(x) > 0) {
                j--;
            }
            if (i <= j) {
                Interaction temp = new Interaction();
                temp = A.get(i);
                A.set(i, A.get(j));
                A.set(j, temp);

                i++;
                j--;
            }
            //System.out.println("i = " + i + ", j = " + j);
        }
        if (j > lower) {
            quickSortInteraction(A, lower, j);
        }
        if (i < upper) {
            quickSortInteraction(A, i, upper);
        }
    }

    public static ArrayList<Integer> searchUsingBinaryInteraction(String searchterm, ArrayList<Interaction> List) {
        int lo, high;
        lo = 0;
        high = List.size();
        int pos = Common.searchUsingBinaryInteractionDetail(searchterm, List, lo, high);

        ArrayList<Integer> posarr = new ArrayList<Integer>();
        if (pos >= 0) {
            posarr.add(pos);
            int postemp1 = pos;
            int postemp2 = pos;
            boolean exist1, exist2;
            while (true) {
                exist1 = false;
                postemp1++;
                if (postemp1 < List.size() && List.get(postemp1).Index.compareTo(searchterm) == 0) {
                    posarr.add(postemp1);
                    exist1 = true;
                }
                if (exist1 == false) {
                    break;
                }
            }
            while (true) {
                exist2 = false;
                postemp2--;
                if (postemp2 >= 0 && List.get(postemp2).Index.compareTo(searchterm) == 0) {
                    posarr.add(postemp2);
                    exist2 = true;
                }
                if (exist2 == false) {
                    break;
                }
            }
        }
        return posarr;
    }

    private static int searchUsingBinaryInteractionDetail(String key, ArrayList<Interaction> a, int lo, int hi) {
        // possible key indices in [lo, hi)
        if (hi <= lo) {
            return -1;
        }
        int mid = lo + (hi - lo) / 2;
        int cmp = a.get(mid).Index.compareTo(key);
        if (cmp > 0) {
            return searchUsingBinaryInteractionDetail(key, a, lo, mid);
        } else if (cmp < 0) {
            return searchUsingBinaryInteractionDetail(key, a, mid + 1, hi);
        } else {
            return mid;
        }
    }
    

    public static void assignNodeScoresInNetwork(ArrayList<Node> RankedNodes, CyNetworkManager cyNetworkManager) {
        try {
            int i, j;
            Map<String, Node> RankedNodeMap = new TreeMap<String, Node>();
            for (i = 0; i < RankedNodes.size(); i++) {
                RankedNodeMap.put(RankedNodes.get(i).NetworkID, RankedNodes.get(i));
            }
            CyNetwork cyNetwork = getNetworkByName(cyNetworkManager, MainData.curNetID);
            List<CyNode> listNodes = cyNetwork.getNodeList();

//        CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();
            CyTable cyTable = cyNetwork.getDefaultNodeTable();
            String attributeName0 = "Rank";
            String attributeName1 = "Score";
//        String attributeName2="Training";
//        String attributeName3="Candidate";
//        String attributeName4="Heldout";
            String attributeName5 = "Role";
            cyTable.createColumn(attributeName0, Integer.class, false);
            cyTable.createColumn(attributeName1, Double.class, false);
            cyTable.createColumn(attributeName5, String.class, false);
            for (i = 0; i < listNodes.size(); i++) {
                String identifierString = cyNetwork.getDefaultNodeTable().getRow(listNodes.get(i).getSUID()).getRaw("Id").toString();
                if (RankedNodeMap.containsKey(identifierString)) {
                    Node RankedNode = RankedNodeMap.get(identifierString);
                    cyTable.getRow(listNodes.get(i).getSUID()).set(attributeName0, RankedNode.Rank);
                    cyTable.getRow(listNodes.get(i).getSUID()).set(attributeName1, RankedNode.Score);
                    if (RankedNode.IsSeed == true) {
                        if (RankedNode.Type.compareTo("Gene/Protein") == 0) {
                            cyTable.getRow(listNodes.get(i).getSUID()).set(attributeName5, "Training-Gene/Protein");
                        } else {
                            cyTable.getRow(listNodes.get(i).getSUID()).set(attributeName5, "Training-Disease");
                        }
                    } else if (RankedNode.IsHeldout == true) {
                        cyTable.getRow(listNodes.get(i).getSUID()).set(attributeName5, "Heldout-Gene/Protein");
                    } else if (RankedNode.IsTest == true) {
                        if (RankedNode.Type.compareTo("Gene/Protein") == 0) {
                            cyTable.getRow(listNodes.get(i).getSUID()).set(attributeName5, "Candidate-Gene/Protein");
                        } else {
                            cyTable.getRow(listNodes.get(i).getSUID()).set(attributeName5, "Candidate-Disease");
                        }
                    } else if (RankedNode.Type.compareTo("Gene/Protein") == 0) {
                        cyTable.getRow(listNodes.get(i).getSUID()).set(attributeName5, "Unknown-Gene/Protein");
                    } else {
                        cyTable.getRow(listNodes.get(i).getSUID()).set(attributeName5, "Unknown-Disease");
                    }
                }
            }
            //Disease Network
            CyNetwork diseaseNetwork = getNetworkByName(cyNetworkManager, MainData.curPheNetID);
            listNodes = diseaseNetwork.getNodeList();
            CyTable diseaseTable = diseaseNetwork.getDefaultNodeTable();

            attributeName0 = "Rank";
            attributeName1 = "Score";
            String attributeName2 = "Name";
//        String attributeName3="Candidate";
//        String attributeName4="Heldout";
            attributeName5 = "Role";
            diseaseTable.createColumn(attributeName0, Integer.class, false);
            diseaseTable.createColumn(attributeName1, Double.class, false);
            diseaseTable.createColumn(attributeName5, String.class, false);

            for (i = 0; i < listNodes.size(); i++) {
                String identifierString = diseaseNetwork.getDefaultNodeTable().getRow(listNodes.get(i).getSUID()).getRaw("Id").toString();
                
                if (RankedNodeMap.containsKey(identifierString)) {
                    Node RankedNode = RankedNodeMap.get(identifierString);
                    diseaseTable.getRow(listNodes.get(i).getSUID()).set(attributeName0, RankedNode.Rank);
                    diseaseTable.getRow(listNodes.get(i).getSUID()).set(attributeName1, RankedNode.Score);
                    diseaseTable.getRow(listNodes.get(i).getSUID()).set(attributeName2, RankedNode.Name);
                    if (RankedNode.IsSeed == true) {
                        if (RankedNode.Type.compareTo("Gene/Protein") == 0) {
                            diseaseTable.getRow(listNodes.get(i).getSUID()).set(attributeName5, "Training-Gene/Protein");
                        } else {
                            diseaseTable.getRow(listNodes.get(i).getSUID()).set(attributeName5, "Training-Disease");
                        }
                    } else if (RankedNode.IsHeldout == true) {
                        diseaseTable.getRow(listNodes.get(i).getSUID()).set(attributeName5, "Heldout-Gene/Protein");
                    } else if (RankedNode.IsTest == true) {
                        if (RankedNode.Type.compareTo("Gene/Protein") == 0) {
                            diseaseTable.getRow(listNodes.get(i).getSUID()).set(attributeName5, "Candidate-Gene/Protein");
                        } else {
                            diseaseTable.getRow(listNodes.get(i).getSUID()).set(attributeName5, "Candidate-Disease");
                        }
                    } else if (RankedNode.Type.compareTo("Gene/Protein") == 0) {
                        diseaseTable.getRow(listNodes.get(i).getSUID()).set(attributeName5, "Unknown-Gene/Protein");
                    } else {
                        diseaseTable.getRow(listNodes.get(i).getSUID()).set(attributeName5, "Unknown-Disease");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Exception in assignNodeScoresInNetwork : " + e);
        }
    }

    public static void sortQuickNodeListInDescScore(ArrayList<Node> Genes) {

        Common.quickSortNodeInDescScore(Genes, 0, Genes.size() - 1);
    }

    public static void quickSortNodeInDescScore(ArrayList<Node> A, int lower, int upper) {
        int i, j;
        double x;
        x = A.get((lower + upper) / 2).Score;
        i = lower;
        j = upper;
        while (i <= j) {
            while (A.get(i).Score > x) {
                i++;
            }
            while (A.get(j).Score < x) {
                j--;
            }
            if (i <= j) {
                Node temp = new Node("");
                temp = A.get(i);
                A.set(i, A.get(j));
                A.set(j, temp);

                i++;
                j--;
            }
            //System.out.println("i = " + i + ", j = " + j);
        }
        if (j > lower) {
            quickSortNodeInDescScore(A, lower, j);
        }
        if (i < upper) {
            quickSortNodeInDescScore(A, i, upper);
        }
    }

    public static double normL2(Map<String, Double> vector0, Map<String, Double> vector1) {
        double temp = 0.0;
        for (Map.Entry<String, Double> e : vector0.entrySet()) {
            String n = e.getKey();
            temp += (vector0.get(n) - vector1.get(n)) * (vector0.get(n) - vector1.get(n));
        }
        return Math.sqrt(temp);
    }

    public static double normL2(ArrayList<Double> vector0, ArrayList<Double> vector1) {
        double temp = 0.0;
        for (int i = 0; i < vector0.size(); i++) {
            temp += (vector0.get(i) - vector1.get(i)) * (vector0.get(i) - vector1.get(i));
        }
        return Math.sqrt(temp);
    }

	public static void analyzeGeneSetEnrichmentWithDAVID(String EntrezIDList){
        if(EntrezIDList==null || EntrezIDList.compareTo("")==0) return;
        Desktop desktop = Desktop.getDesktop();
        try{
            //Functional Annotation Clustering
            desktop.browse(new URI("http://david.abcc.ncifcrf.gov/api.jsp?type=ENTREZ_GENE_ID&ids="  + EntrezIDList + "&tool=term2term&annot=GOTERM_BP_FAT,GOTERM_CC_FAT,GOTERM_MF_FAT,KEGG_PATHWAY"));
            //Gene Functional Classfication
            desktop.browse(new URI("http://david.abcc.ncifcrf.gov/api.jsp?type=ENTREZ_GENE_ID&ids="  + EntrezIDList + "&tool=gene2gene&annot=GOTERM_BP_FAT,GOTERM_CC_FAT,GOTERM_MF_FAT,KEGG_PATHWAY"));
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
    }

    public static VisualStyle getVisualStyleByName(String styleName, VisualMappingManager vmm) {
        Set<VisualStyle> styles = vmm.getAllVisualStyles();
        for (VisualStyle style : styles) {
            if (style.getTitle().equals(styleName)) {
//                System.out.println("style found in VisualStyles: " + styleName + " == " + style.getTitle());
                return style;
            }
        }
        System.out.println("style [" + styleName + "] not in VisualStyles, default style used.");
        return null;
    }

    public static void applyNetworkVisualStyle(CyNetwork network, CyNetworkView view, String vsNetworkName, VisualMappingManager vmm,
            VisualStyleFactory visualStyleFactory, VisualMappingFunctionFactory vmfFactoryP, VisualMappingFunctionFactory vmfFactoryD, VisualMappingFunctionFactory vmfFactoryC) {
        //Check to see if a visual style with this name already exists

        VisualStyle vs = getVisualStyleByName(vsNetworkName, vmm);
        if (null == vs) {
            //if not, create it and add it to the catalog.
            vs = Common.createNetworkVisualStyle(network, vsNetworkName, visualStyleFactory, vmfFactoryP, vmfFactoryD, vmfFactoryC);
            vmm.addVisualStyle(vs);
        }
        //Actually apply the visual style
        vs.apply(view);
        vmm.setVisualStyle(vs, view);
    }

    public static VisualStyle createNetworkVisualStyle(CyNetwork network, String vsNetworkName, VisualStyleFactory visualStyleFactory,
            VisualMappingFunctionFactory vmfFactoryP, VisualMappingFunctionFactory vmfFactoryD, VisualMappingFunctionFactory vmfFactoryC) {

        VisualStyle visualStyle = visualStyleFactory.createVisualStyle(vsNetworkName);
        //Node settings
        //Node Label
        PassthroughMapping pm = (PassthroughMapping) vmfFactoryP.createVisualMappingFunction("OfficialSymbol", String.class, BasicVisualLexicon.NODE_LABEL);
        visualStyle.addVisualMappingFunction(pm);
        //Node color
//        DiscreteMapping disMapping = (DiscreteMapping) vmfFactoryD.createVisualMappingFunction("Role", String.class, BasicVisualLexicon.NODE_FILL_COLOR);
//
//        disMapping.putMapValue("Training-Gene/Protein", Color.RED);
//        disMapping.putMapValue("Training-Disease", Color.RED);
//        disMapping.putMapValue("Candidate-Gene/Protein", Color.ORANGE);
//        disMapping.putMapValue("Candidate-Disease", Color.ORANGE);
//        disMapping.putMapValue("Unknown-Gene/Protein", Color.GREEN);
//        disMapping.putMapValue("Unknown-Disease", Color.GREEN);
//        visualStyle.addVisualMappingFunction(disMapping);

        int min = 0;
        int max =0;

        List<CyNode> it = network.getNodeList();//nodesIterator();
        for (CyNode cyNode : it) {
            Integer value = network.getDefaultNodeTable().getRow(cyNode.getSUID()).get("Rank", Integer.class);
            if (value.intValue() < min) {
                min = value.intValue();
            }
            else if (value.intValue() > max) {
                max = value.intValue();
            }
        }

        // pick 3 points within (min~max)
        double p1 = min + (max-min)/3.0;
        double p2 = p1 + (max-min)/3.0;
        double p3 = p2 + (max-min)/3.0;

        // Create a calculator for "Degree" attribute
        
//        final Object defaultObj = type.getDefault(Cytoscape.getVisualMappingManager().getVisualStyle());

        ContinuousMapping cm = (ContinuousMapping) vmfFactoryC.createVisualMappingFunction("Rank", Integer.class, BasicVisualLexicon.NODE_FILL_COLOR);
        

//        Interpolator numToColor = new LinearNumberToColorInterpolator();
//        cm.setInterpolator(numToColor);

        Color underColor = Color.GRAY;
        Color minColor = Color.RED;
        Color midColor = Color.WHITE;
        Color maxColor = Color.GREEN;
        Color overColor = Color.BLUE;

        BoundaryRangeValues bv0 = new BoundaryRangeValues(minColor, minColor, minColor);
        BoundaryRangeValues bv1 = new BoundaryRangeValues(midColor, midColor, midColor);
        BoundaryRangeValues bv2 = new BoundaryRangeValues(maxColor, maxColor, maxColor);
//
//        Color underColor = Color.GRAY;
//        Color minColor = Color.RED;
//        Color midColor = Color.WHITE;
//        Color maxColor = Color.GREEN;
//        Color overColor = Color.BLUE;
//
//        BoundaryRangeValues bv0 = new BoundaryRangeValues(Color.RED, Color.WHITE, Color.WHITE);
//        BoundaryRangeValues bv1 = new BoundaryRangeValues(Color.WHITE, Color.WHITE, Color.GREEN);
//        BoundaryRangeValues bv2 = new BoundaryRangeValues(maxColor, maxColor, overColor);

        // Set the attribute point values associated with the boundary values
        cm.addPoint(p1, bv0);
        cm.addPoint(p2, bv1);
        cm.addPoint(p3, bv2);
        visualStyle.addVisualMappingFunction(cm);
        //Node shape
        DiscreteMapping disMapping = (DiscreteMapping) vmfFactoryD.createVisualMappingFunction("Role", String.class, BasicVisualLexicon.NODE_SHAPE);

        disMapping.putMapValue("Training-Gene/Protein", NodeShapeVisualProperty.TRIANGLE);
        disMapping.putMapValue("Training-Disease", NodeShapeVisualProperty.DIAMOND);
        disMapping.putMapValue("Candidate-Gene/Protein", NodeShapeVisualProperty.OCTAGON);
        disMapping.putMapValue("Candidate-Disease", NodeShapeVisualProperty.RECTANGLE);
        disMapping.putMapValue("Unknown-Gene/Protein", NodeShapeVisualProperty.ELLIPSE);
        disMapping.putMapValue("Unknown-Disease", NodeShapeVisualProperty.ROUND_RECTANGLE);
        visualStyle.addVisualMappingFunction(disMapping);

        //Node size
//        Calculator nodeSizeCalculator = createNodeColorCalculatorBasedRank(network);
//        nodeAppCalc.setCalculator(nodeSizeCalculator);
        //Edge settings
        //Edge weight
//        disMapping = new DiscreteMapping(ArrowShape.NONE,ObjectMapping.EDGE_MAPPING);
//        disMapping.setControllingAttributeName("interaction", network, false);
//        disMapping.putMapValue(new String("1"), ArrowShape.ARROW);
//        disMapping.putMapValue(new String("-1"), ArrowShape.T);
//        disMapping.putMapValue(new String("0"), ArrowShape.NONE);
//        Calculator edgeTargetArrowCalculator = new BasicCalculator("Example Edge Target Arrow Calc", disMapping,VisualPropertyType.EDGE_TGTARROW_SHAPE);
//        edgeAppCalc.setCalculator(edgeTargetArrowCalculator);
        return visualStyle;
    }

	//For GeneRIF
    public static void sortQuickGeneRIFsListInAsc(ArrayList<GeneRIF> Genes){

        Common.quickSortGeneRIFs(Genes, 0, Genes.size()-1);
    }
    
    public static void quickSortGeneRIFs(ArrayList<GeneRIF> A, int lower, int upper){
        int i, j;
        String x;
        x = A.get((lower + upper) / 2).EntrezID;
        i = lower;
        j = upper;
        while(i <= j){
            while(A.get(i).EntrezID.compareTo(x)<0) i++;
            while(A.get(j).EntrezID.compareTo(x)>0) j--;
            if (i <= j){
                GeneRIF temp=new GeneRIF();
                temp=A.get(i);
                A.set(i,A.get(j));
                A.set(j,temp);

                i++;
                j--;
            }
            //System.out.println("i = " + i + ", j = " + j);
        }
        if (j > lower) quickSortGeneRIFs(A, lower, j);
        if (i < upper) quickSortGeneRIFs(A, i, upper);
    }
    
    public static ArrayList<Integer> searchUsingBinaryGeneRIFs(String searchterm, ArrayList<GeneRIF> List){
        int lo, high;
        lo=0;
        high=List.size();
        int pos= Common.searchUsingBinaryGeneRIFsDetail(searchterm, List, lo, high);

        ArrayList<Integer> posarr= new ArrayList<Integer>();
        if(pos>=0){

            posarr.add(pos);
            int postemp1=pos;
            int postemp2=pos;
            boolean exist1, exist2;
            while(true){
                exist1=false;
                postemp1++;
                if(postemp1<List.size() && List.get(postemp1).EntrezID.compareTo(searchterm)==0){
                   posarr.add(postemp1);
                   exist1=true;
                }
                if(exist1==false) break;
            }
            while(true){
                exist2=false;
                postemp2--;
                if(postemp2>=0 && List.get(postemp2).EntrezID.compareTo(searchterm)==0){
                   posarr.add(postemp2);
                   exist2=true;
                }
                if(exist2==false) break;
            }
        }
        return posarr;
    }
    
    public static int searchUsingBinaryGeneRIFsDetail(String key, ArrayList<GeneRIF> a, int lo, int hi) {
        // possible key indices in [lo, hi)
        if (hi <= lo) return -1;
        int mid = lo + (hi - lo) / 2;
        int cmp = a.get(mid).EntrezID.compareTo(key);
        if      (cmp > 0) return searchUsingBinaryGeneRIFsDetail(key, a, lo, mid);
        else if (cmp < 0) return searchUsingBinaryGeneRIFsDetail(key, a, mid+1, hi);
        else              return mid;
    }

    public static void fillComplex2GeneTable(Map<String,Complex> ComplexData, JTable table, JLabel label){
        try{
            Vector<String> ColHeader;
            Vector<Vector> Data;
            int i,j;
            ColHeader = new Vector<String>();
            
            
            ColHeader.add("Complex ID");
            ColHeader.add("Complex Name");
            ColHeader.add("Synonyms");
            ColHeader.add("Organism");
            ColHeader.add("Subunits (UniProt AC)");
            ColHeader.add("Subunits (Entrez ID)");
            ColHeader.add("Purification Method");
            ColHeader.add("PubMed IDs");
            ColHeader.add("FunCat Categories");
            ColHeader.add("Functional Comment");
            ColHeader.add("Disease Comment");
            ColHeader.add("Subunit Comment");
            
            Data= new Vector<Vector>();
            for(Iterator<Map.Entry<String, Complex>> it=ComplexData.entrySet().iterator();it.hasNext();){
                Map.Entry<String, Complex>  e = it.next();
                Vector<String> vt = new Vector<String>();
                vt.add(0,e.getKey());
                vt.add(1,e.getValue().ComplexName);
                vt.add(2,e.getValue().Synonyms);
                vt.add(3,e.getValue().Organism);
                vt.add(4,e.getValue().Subunits_UniProtACs);
                vt.add(5,e.getValue().Subunits_EntrezIDs);
                vt.add(6,e.getValue().PurificationMethod);
                vt.add(7,e.getValue().PubMedIDs);
                vt.add(8,e.getValue().FunCategories);
                vt.add(9,e.getValue().FunctionalComment);
                vt.add(10,e.getValue().DiseaseComment);
                vt.add(11,e.getValue().SubunitComment);
                                
                Data.add(vt);
            }
            table.setModel(new javax.swing.table.DefaultTableModel(Data,ColHeader));
            
            label.setText("Total: " + table.getRowCount());
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error while filling Complex-Gene database into table: " + e.toString());
        }
        
        
    }

    public static void fillPathway2GeneTable(Map<String,Pathway> PathwayData, JTable table, JLabel label){
        try{
            Vector<String> ColHeader;
            Vector<Vector> Data;
            int i,j;
            ColHeader = new Vector<String>();
            ColHeader.add("KEGG PathwayID");
            ColHeader.add("Pathway Name");
            ColHeader.add("Associated Genes (Entrez ID)");
            

            Data= new Vector<Vector>();
            for(Iterator<Map.Entry<String, Pathway>> it=PathwayData.entrySet().iterator();it.hasNext();){
                Map.Entry<String, Pathway>  e = it.next();
                Vector<String> vt = new Vector<String>();
                vt.add(0,e.getKey());
                vt.add(1,e.getValue().name);
                vt.add(2,e.getValue().AssociatedGenes);
                                
                Data.add(vt);
            }
            table.setModel(new javax.swing.table.DefaultTableModel(Data,ColHeader));
            
            label.setText("Total: " + table.getRowCount());
        }catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while filling KEGG pathways into table: " + e.toString());
        }
    }

    public static void fillDO2GeneTable(Map<String,DO> DOData, JTable table, JLabel label){
        try{
            Vector<String> ColHeader;
            Vector<Vector> Data;
            int i,j;
            ColHeader = new Vector<String>();
            
            
            ColHeader.add("DOID");
            ColHeader.add("Name");
            ColHeader.add("ICD9CM");
            ColHeader.add("MeSH");
            ColHeader.add("NCI");
            ColHeader.add("Annotated Genes (Entrez ID)");
            
            
            Data= new Vector<Vector>();
            for(Iterator<Map.Entry<String, DO>> it=DOData.entrySet().iterator();it.hasNext();){
                Map.Entry<String, DO>  e = it.next();
                Vector<String> vt = new Vector<String>();
                vt.add(0,e.getKey());
                vt.add(1,e.getValue().Name);
                vt.add(2,e.getValue().ICD9CM);
                vt.add(3,e.getValue().MSH);
                vt.add(4,e.getValue().NCI);
                vt.add(5,e.getValue().AnnotatedGenes);
                                                
                Data.add(vt);
            }
            table.setModel(new javax.swing.table.DefaultTableModel(Data,ColHeader));
            
            label.setText("Total: " + table.getRowCount());
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error while filling Disease Ontology-Gene database into table: " + e.toString());
        }
    }

    public static void fillPhenotype2GeneTable(Map<String,Disease> DiseaseData, JTable table, JLabel label){
        try{
            Vector<String> ColHeader;
            Vector<Vector> Data;
            int i,j;
            ColHeader = new Vector<String>();
            
            
            ColHeader.add("MIMID");
            
            ColHeader.add("MedGenCUI");
            
            ColHeader.add("Name");
            
            ColHeader.add("Locus");
            
            ColHeader.add("Associated Genes (Entrez ID)");
            

            Data= new Vector<Vector>();
            for(Iterator<Map.Entry<String, Disease>> it=DiseaseData.entrySet().iterator();it.hasNext();){
                Map.Entry<String, Disease>  e = it.next();
                Vector<String> vt = new Vector<String>();
                vt.add(0,e.getKey());
                
                vt.add(1,e.getValue().Prefix);
                
                vt.add(2,e.getValue().Name);
                
                vt.add(3,e.getValue().Locus);
                
                vt.add(4,e.getValue().KnownGeneList);
                                
                Data.add(vt);
            }
            table.setModel(new javax.swing.table.DefaultTableModel(Data,ColHeader));
            
            
            label.setText("Total: " + table.getRowCount());
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error while filling Disease-Gene database into table: " + e.toString());
        }
    }

    public static void fillGeneInfoTable(ArrayList<Node> GeneData, JTable table, String ID, JLabel label){
        try{
            Vector<String> ColHeader;
            Vector<Vector> Data;
            int i,j;
            ColHeader = new Vector<String>();
            ColHeader.add("#");
            if(ID.compareTo("EntrezID")==0){
                ColHeader.add("EntrezID");
            }else{
                ColHeader.add("UniProtAC");
            }
            ColHeader.add("Official Symbol");
            ColHeader.add("Alternate Symbols");
            ColHeader.add("Organism");
            

            Data= new Vector<Vector>();
            for(i=0;i<GeneData.size();i++){
                Vector<String> vt = new Vector<String>();
                vt.add(0,GeneData.get(i).Tag);
                if(ID.compareTo("EntrezID")==0){
                    vt.add(1,GeneData.get(i).EntrezID);
                }else{
                    vt.add(1,GeneData.get(i).UniProtAC);
                }
                
                vt.add(2,GeneData.get(i).OfficialSymbol);
                String genesyms="";
                for(j=0;j<GeneData.get(i).AlternateSymbols.size();j++){
                    genesyms = genesyms.concat(GeneData.get(i).AlternateSymbols.get(j));
                    genesyms = genesyms.concat(", ");
                }
                genesyms = (genesyms.compareTo("")!=0)?genesyms.substring(0, genesyms.length()-2):"";
                vt.add(3,genesyms);
                vt.add(4,GeneData.get(i).Organism);
                
                Data.add(vt);
            }
            table.setModel(new javax.swing.table.DefaultTableModel(Data,ColHeader));
            
            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setMaxWidth(0);
            
            table.getColumnModel().getColumn(1).setMinWidth(70);
            table.getColumnModel().getColumn(1).setMaxWidth(100);

            table.getColumnModel().getColumn(2).setMinWidth(100);
            table.getColumnModel().getColumn(3).setMinWidth(200);
            table.getColumnModel().getColumn(4).setMinWidth(200);

            
            label.setText("Total found: " + table.getRowCount());
            

            //tblIDMappingDatabase.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error while filling ID Mappings into table: " + e.toString());
        }
    }

    public static void fillChromosomeInfoTable(ArrayList<Node> GeneData, JTable table, JLabel label){
        try{
            //System.out.println("Showing chromosome regions...");
                
            int i,j;
            
            Vector<String> ColHeader = new Vector<String>();
            Vector<Vector> Data = new Vector<Vector>();

            ColHeader = new Vector<String>();
            
            ColHeader.add(0,"Chromosome");
            ColHeader.add(1,"Band");
            ColHeader.add(2,"Official Symbol");
            ColHeader.add(3,"Gene Start");
            ColHeader.add(4,"Gene End");
            ColHeader.add(5,"Entrez ID");
            //ColHeader.add(5,"Chrom/Distance");
            Data = new Vector<Vector>();
            Vector<Object> GeneRecord= new Vector<Object>();
            for(i=0;i<BasicData.AllGeneChromosome.size();i++){
                GeneRecord= new Vector<Object>();
                
                GeneRecord.add(0,GeneData.get(i).Chromosome);
                GeneRecord.add(1,GeneData.get(i).Band);
                GeneRecord.add(2,GeneData.get(i).OfficialSymbol);
                GeneRecord.add(3,GeneData.get(i).GeneStart);
                GeneRecord.add(4,GeneData.get(i).GeneEnd);
                GeneRecord.add(5,GeneData.get(i).EntrezID);

                Data.add(GeneRecord);
            }
            table.setModel(new javax.swing.table.DefaultTableModel(Data,ColHeader){
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int colIndex) {
                if(colIndex==1){
                    return true;   //Allow the editing of any cell
                }else{
                    return false;
                }
              }

            });
            
            label.setText("Total found: " + table.getRowCount());
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Error while filling Chromosome Information table: " + e.toString());
        }
    }
    
    public static ArrayList<Node> findAllNeighboringGenesOfKnownGenesInNetwork(CyNetworkManager networkManager, int dist2) {
		ArrayList<Node> AllTestGenes = new ArrayList<Node>();
        Common.highlightNodesInNetwork(networkManager, MainData.AllTrainingGenes);//Select traning genes in the network
        CyNetwork currentNetwork = Common.getNetworkByName(networkManager, MainData.curNetID);
        ArrayList<CyNode> eliminatedNodes = new ArrayList<CyNode>();
        List<CyNode> selectedNodesList = CyTableUtil.getNodesInState(currentNetwork, "selected", true);
        for (Iterator it = selectedNodesList.iterator(); it.hasNext();) {
            eliminatedNodes.add((CyNode) it.next());
        }

        int d;
        for (d = 1; d <= dist2; d++) {
            //MinhDA WARNING Node vs CyNode?
            List<CyNode> selectedNodes = CyTableUtil.getNodesInState(currentNetwork, "selected", true);
            //MinhDA WARNING Node vs CyNode?
            for (CyNode i : selectedNodes) {
                for (CyNode j : currentNetwork.getNeighborList(i, CyEdge.Type.ANY)) {
                    currentNetwork.getRow(j).set("selected", true);
                }
//                currentNetwork.setSelectedNodeState(currentNetwork.getNeighborList((CyNode) it.next(), CyEdge.Type.ANY), true);
            }

            //Eliminate all training genes
            for (CyNode node : eliminatedNodes) {
                currentNetwork.getRow(node).set("selected", false);
            }
//            currentNetwork.setSelectedNodeState((Collection)eliminatedNodes, false);
//            ((CyNetworkView) cyNetworkViewManager.getNetworkViewSet().toArray()[0]).updateView();
//            Cytoscape.getCurrentNetworkView().updateView();

//            CyAttributes nodeAtt = Cytoscape.getNodeAttributes();
            //Select all remaining selected nodes as Candidate Genes
            selectedNodes = CyTableUtil.getNodesInState(currentNetwork, "selected", true);
            for (CyNode node : selectedNodes) {
                CyNode n = node;
                eliminatedNodes.add(n);

                Node g = new Node();
                g.DistanceToSeed = d;
                g.EntrezID = currentNetwork.getRow(n).get("EntrezID", String.class);
                g.OfficialSymbol = currentNetwork.getRow(n).get("OfficialSymbol", String.class);
//                StringTokenizer stk = new StringTokenizer(currentNetwork.getRow(n).get("AlternateSymbols", String.class), ", ");
//                while (stk.hasMoreTokens()) {
//                    g.AlternateSymbols.add(stk.nextToken());
//                }
                g.NetworkID = currentNetwork.getRow(n).get("Id", String.class);
                AllTestGenes.add(g);
            }
            
//            for(Iterator it = currentNetwork.getSelectedNodes().iterator(); it.hasNext();){
//                giny.model.Node n=(giny.model.Node)it.next();
//                eliminatedNodes.add(n);
//                
//                Node g= new Node();
//                g.DistanceToSeed=d;
//                g.EntrezID=nodeAtt.getAttribute(n.getIdentifier(), "EntrezID").toString();
//                g.OfficialSymbol=nodeAtt.getAttribute(n.getIdentifier(), "OfficialSymbol").toString();
//                StringTokenizer stk = new StringTokenizer(nodeAtt.getAttribute(n.getIdentifier(), "AlternateSymbols").toString(),", ");
//                while(stk.hasMoreTokens()){
//                    g.AlternateSymbols.add(stk.nextToken());
//                }
//                //g.UniProtAC=nodeAtt.getAttribute(n.getIdentifier(), "UniProtAC").toString();
//                //g.Organism=nodeAtt.getAttribute(n.getIdentifier(), "Organism").toString();
//                g.NetworkID=n.getIdentifier();
//                AllTestGenes.add(g);
//            }
        }

        return AllTestGenes;
	}

}
