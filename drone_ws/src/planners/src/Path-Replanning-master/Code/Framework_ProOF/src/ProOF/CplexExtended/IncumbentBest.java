/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.CplexExtended;

import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex.IncumbentCallback;

/**
 *
 * @author marcio
 */
public abstract class IncumbentBest extends IncumbentCallback implements iCplexExtract{
    
    @Override
    public abstract void main() throws IloException;
    
    @Override
    public double getBestObjValue() throws IloException {
        return super.getBestObjValue(); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public double getObjValue() throws IloException {
        return super.getObjValue(); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public double getValue(IloNumExpr expr) throws IloException {
        return super.getValue(expr); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public double getValue(IloNumVar var) throws IloException {
        return super.getValue(var); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public double[] getValues(IloNumVar[] var) throws IloException {
        return super.getValues(var); //To change body of generated methods, choose Tools | Templates.
    } 
    public double[][] getValues(IloNumVar V[][]) throws IloException{
        double X[][] = new double[V.length][];
        for(int i=0; i<V.length; i++){
            X[i] = getValues(V[i]);
        }
        return X;
    }
    public double[][][] getValues(IloNumVar V[][][]) throws IloException{
        double X[][][] = new double[V.length][][];
        for(int i=0; i<V.length; i++){
            X[i] = getValues(V[i]);
        }
        return X;
    }
    public double[][][][] getValues(IloNumVar V[][][][]) throws IloException{
        double X[][][][] = new double[V.length][][][];
        for(int i=0; i<V.length; i++){
            X[i] = getValues(V[i]);
        }
        return X;
    }
}
