/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model;

import java.util.ArrayList;

/**
 *
 * @author "MinhDA"
 */
public class DO {
    public String DOID;
    public String Name;
    public String ICD9CM;
    public String MSH;
    public String NCI;
    public ArrayList<String> Genes;//EntrezID;
    public String AnnotatedGenes;
    
    public DO(){
        this.DOID = "";
        this.Name = "";
        this.ICD9CM = "";
        this.MSH = "";
        this.NCI = "";
        this.AnnotatedGenes = "";
        this.Genes = new ArrayList<String>();
    }
    
    public DO(String DOID, String Name, String ICD9CM, String MSH, String NCI, String AnnotatedGenes){
        this.DOID = DOID;
        this.Name = Name;
        this.ICD9CM = ICD9CM;
        this.MSH = MSH;
        this.NCI = NCI;
        this.AnnotatedGenes = AnnotatedGenes;
    }
}
