/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.gen.linear.uncertainty;

import ProOF.apl.UAV.abst.uncertainty.pStateUncertainty;

/**
 *
 * @author marcio
 */
public abstract class pLinearControlUncertainty extends pStateUncertainty{
    public abstract double Sigma(int t, int i, int j) throws Exception;
}
