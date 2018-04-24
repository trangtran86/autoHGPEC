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
public class Interaction {
    public String Index;//Common field to store field by which Interaction list will be sorted
    public String NodeSrc;
    public String NodeDst;
    public int Type;
    public double Weight;
    public double WeightOriginal;
    public int Id;

    public Interaction(){
        this.Type=0;
        this.NodeSrc="";
        this.NodeDst="";
        this.Weight=0;
        this.WeightOriginal=0;
        this.Id = -1;
    }
    public Interaction(String nodesrc, String nodedst){
        this.NodeSrc=nodesrc;
        this.NodeDst=nodedst;
        this.Type=0;
        this.Weight=1.0;
        this.WeightOriginal=1.0;
        this.Id = -1;
    }
    public Interaction(String nodesrc, String nodedst, double Weight){
        this.NodeSrc=nodesrc;
        this.NodeDst=nodedst;
        this.Type=0;
        this.Weight=Weight;
        this.WeightOriginal=Weight;
        this.Id = -1;
    }

    public Interaction(String nodesrc, String nodedst, double Weight, double OriginalWeight){
        this.Id=-1;
        this.NodeSrc=nodesrc;
        this.NodeDst=nodedst;
        this.Type=0;
        this.Weight=Weight;
        this.WeightOriginal=OriginalWeight;
    }
    
    public Interaction Copy(){
        Interaction ina = new Interaction();
        ina.Index=this.Index;
        ina.NodeSrc=this.NodeSrc;
        ina.NodeDst=this.NodeDst;
        ina.Type=this.Type;
        ina.Weight=this.Weight;
        ina.WeightOriginal=this.WeightOriginal;
        ina.Id = this.Id;
        
        return ina;
    }
}
