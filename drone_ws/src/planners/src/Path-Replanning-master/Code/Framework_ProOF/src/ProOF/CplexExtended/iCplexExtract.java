/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.CplexExtended;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;

/**
 *
 * @author marcio
 */
public interface iCplexExtract {
    public double getBestObjValue() throws IloException;
    public double getObjValue() throws IloException;
    public double getValue(IloNumExpr expr) throws IloException;
    public double getValue(IloNumVar var) throws IloException;
    public double[] getValues(IloNumVar[] var) throws IloException;
    public double[][] getValues(IloNumVar V[][]) throws IloException;
    public double[][][] getValues(IloNumVar V[][][]) throws IloException;
    public double[][][][] getValues(IloNumVar V[][][][]) throws IloException;

}
