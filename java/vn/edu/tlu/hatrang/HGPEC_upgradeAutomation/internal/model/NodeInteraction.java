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
public class NodeInteraction {
    public String Node;
    public int InaType;
    public int State;
    public double Weight;

    public NodeInteraction(){
        this.Node="";
        this.InaType=0;
        this.Weight=0.0;
    }

    public NodeInteraction(String Node, int InaType, double Weight){
        this.Node=Node;
        this.InaType=InaType;
        this.Weight=Weight;
    }

    public NodeInteraction(String Node, int State, int InaType){
        this.Node=Node;
        this.State=State;
        this.InaType=InaType;
        this.Weight=0.0;
    }
}
