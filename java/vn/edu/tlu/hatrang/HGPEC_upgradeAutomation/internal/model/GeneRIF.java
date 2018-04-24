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
public class GeneRIF {
    public int TaxID;
    public String EntrezID;
    public String PubMedID;
    public String OfficialSymbol;
    public String GeneRIFsText;
    public String LastUpdateTimeStamp;

    public GeneRIF(){
        this.TaxID=-1;
        this.EntrezID="";
        this.PubMedID="";
        this.OfficialSymbol="";
        this.GeneRIFsText="";
        this.LastUpdateTimeStamp="";
    }
}
