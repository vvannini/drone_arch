/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.abst;

import ProOF.CplexExtended.iCplexExtract;
import ilog.concert.IloException;


/**
 *
 * @author marcio
 */
public abstract class State{
    public abstract void extract(iCplexExtract ext) throws IloException;
}
