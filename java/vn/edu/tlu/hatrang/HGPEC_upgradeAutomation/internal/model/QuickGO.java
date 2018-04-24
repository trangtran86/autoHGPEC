/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 *
 * @author "MinhDA"
 */
public class QuickGO {
    public static Set<GO> getAnnotationByGOID(String GOIDList){
        Set<GO> annset=new HashSet<GO>();
        try{
            // URL for annotations from QuickGO for one protein
            URL u=new URL("http://www.ebi.ac.uk/QuickGO/GAnnotation?db=UniProtKB&goid="+GOIDList+"&tax=9606&format=tsv");
            System.out.println(u.toString());
            //http://www.ebi.ac.uk/QuickGO/GAnnotation?db=UniProtKB&goid=GO:0006355,GO:0005634&tax=9606&format=tsv
            // Connect
            HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
            // Get data
            BufferedReader rd=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            // Read data
            List<String> columns=Arrays.asList(rd.readLine().split("\t"));
            //System.out.println(columns);
            // Collect the unique terms as a sorted set
            Set<String> IDs=new TreeSet<String>();
            Set<String> terms=new TreeSet<String>();
            Set<String> termaspects=new TreeSet<String>();
            // Find which column contains GO IDs
    //        int IDIndex=columns.indexOf("ID");
    //        int termIndex=columns.indexOf("GO ID");
    //        int termAspect=columns.indexOf("Aspect");
            // Read the annotations line by line
            String line;
            int total=0;


    //        while ((line=rd.readLine())!=null) {
    //            // Split them into fields
    //            String[] fields=line.split("\t");
    //            // Record the GO ID
    //            //System.out.println(total + ": " + line);
    //
    //            IDs.add(fields[IDIndex]);
    //            terms.add(fields[termIndex]);
    //            termaspects.add(fields[termAspect]);
    //
    //            total++;
    //        }
    //        // close input when finished
    //        rd.close();
    //        // Write out the unique terms
    //
    //        int i=0;
    //        for (String term:terms) {
    //            //System.out.println(term);
    //            //System.out.println(termaspects.toArray()[i].toString());
    //            annlist.add(new GO(IDs.toArray()[i].toString(),term, termaspects.toArray()[i].toString()));
    //            i++;
    //        }

            GO ann;
            while ((line=rd.readLine())!=null) {
                //System.out.println(line);
                // Split them into fields
                String[] fields=line.split("\t");
                // Record the GO ID
                //System.out.println(total + ": " + line);
                String EntrezID="";
                String UniProtAC=fields[1];
                String OfficialSymbol=fields[3];
                String Taxon=fields[4];
                String GOID=fields[6];
                String GOName=fields[7];
                String Category=fields[11];
                String Evidence=fields[9];

                if(GOIDList.contains(GOID)){
                    ann = new GO(EntrezID,UniProtAC,OfficialSymbol,Taxon,GOID,GOName,Category,Evidence);
                    annset.add(ann);
                    total++;
                }
            }
            // close input when finished
            rd.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return annset;
    }
    
    public static Set<GO> getAnnotationByUniProtAC(String UniProtACList){
        Set<GO> annset=new HashSet<GO>();
        try{
            // URL for annotations from QuickGO for one protein
            UniProtACList = UniProtACList.replaceAll(" ", "");
            URL u=new URL("http://www.ebi.ac.uk/QuickGO/GAnnotation?db=UniProtKB&protein="+UniProtACList+"&format=tsv");
            System.out.println(u.toString());
            //http://www.ebi.ac.uk/QuickGO/GAnnotation?db=UniProtKB&goid=GO:0006355,GO:0005634&ref=&with=&tax=9606&source=UniProtKB&format=tsv
            // Connect
            HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
            // Get data
            BufferedReader rd=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            // Read data
            List<String> columns=Arrays.asList(rd.readLine().split("\t"));
            //System.out.println(columns);
            // Collect the unique terms as a sorted set
            Set<String> IDs=new TreeSet<String>();
            Set<String> terms=new TreeSet<String>();
            Set<String> termaspects=new TreeSet<String>();

            // Read the annotations line by line
            String line;
            int total=0;

            GO ann;
            while ((line=rd.readLine())!=null) {
                //System.out.println(line);
                // Split them into fields
                String[] fields=line.split("\t");
                // Record the GO ID
                //System.out.println(total + ": " + line);
                String EntrezID="";
                String UniProtAC=fields[1];
                String OfficialSymbol=fields[3];
                String Taxon=fields[4];
                String GOID=fields[6];
                String GOName=fields[7];
                String Category=fields[11];
                String Evidence=fields[9];

                if(UniProtACList.contains(UniProtAC)){
                    ann = new GO(EntrezID,UniProtAC,OfficialSymbol,Taxon,GOID,GOName,Category,Evidence);
                    annset.add(ann);
                    total++;
                }
            }
            // close input when finished
            rd.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return annset;
    }
    
    public static Set<GO> getAnnotationByEntrezGeneID(Set<String> EntrezGeneIDSet, String Gene2GoFileName){
        Set<GO> annset=new HashSet<GO>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(Gene2GoFileName.replace("\\", File.separator)));
            // Read the annotations line by line
            String line = br.readLine();//Igore first line
            int total=0;

            GO ann;
            while ((line=br.readLine())!=null) {
                //System.out.println(line);
                // Split them into fields
                String[] fields=line.split("\t");
                // Record the GO ID
                //System.out.println(total + ": " + line);
                String EntrezID=fields[1];
                String Qualifier=fields[4];
                String PubMed=fields[6];
                String Taxon=fields[0];
                String GOID=fields[2];
                String GOName=fields[5];
                String Category=fields[7];
                String Evidence=fields[3];

                if(EntrezGeneIDSet.contains(EntrezID)){
                    ann = new GO(EntrezID,Qualifier,PubMed,Taxon,GOID,GOName,Category,Evidence);
                    annset.add(ann);
                    total++;
                }
            }
            // close input when finished
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return annset;
    }
}
