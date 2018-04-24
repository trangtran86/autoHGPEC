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
public class Disease {
    public String DiseaseID;
    public String Prefix;
    public String Name;
    public ArrayList<String> KnownGenes;
    public ArrayList<Node> TestGenes;
    public String KnownGeneList;
    public boolean IsNeighbor ;
    public String Locus;

    public Disease(){
        this.DiseaseID="";
        this.Prefix="";
        this.Name="";
        this.KnownGenes=new ArrayList<String>();
        this.TestGenes= new ArrayList<Node>();
        this.IsNeighbor = false;
        this.KnownGeneList = "";
        this.Locus ="";
    }
}
