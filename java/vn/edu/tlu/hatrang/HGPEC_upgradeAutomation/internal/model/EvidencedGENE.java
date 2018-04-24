/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.edu.tlu.hatrang.HGPEC_upgradeAutomation.internal.model;

/**
 *
 * @author "MinhDA"
 */
public class EvidencedGENE extends Node {
    public String GeneRIF;
    public String PubMed;
    public String OMIM;

    public EvidencedGENE(){
        new Node();
        this.GeneRIF="";
        this.PubMed="";
        this.OMIM="";
    }
}
