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
public class Pathway {
    public String name;
    public String org;
    public String number;
    public String title;
    public String image;
    public String link;
    public ArrayList<String> Genes;
    public String AssociatedGenes;
    public Pathway(){
        this.name="";
        this.org="";
        this.number="";
        this.title="";
        this.image="";
        this.link="";
        this.Genes=new ArrayList<String>();
        this.AssociatedGenes ="";
    }
}
