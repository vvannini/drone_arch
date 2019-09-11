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
public class CplexNumNorm1 {
    private final IloNumVar v[];
    public final IloNumExpr f;
    public final String name;
    public CplexNumNorm1(CplexExtended cplex, String name, IloNumExpr ...exp) throws IloException {
        this(cplex, name, Double.POSITIVE_INFINITY, exp);
    }
    public CplexNumNorm1(CplexExtended cplex, String name, double MAX, IloNumExpr ...exp) throws IloException {
        if(MAX<0){
            throw new IloException("MAX<0 is invalid");
        }
        this.name = name;
        v = cplex.numVarArray(exp.length, 0, MAX, name+".v");
        
        for (int j = 0; j < exp.length; j++) {
            cplex.addGe(v[j], cplex.prod(+1, exp[j]), name+".mod");
            cplex.addGe(v[j], cplex.prod(-1, exp[j]), name+".mod");
        }
        
        f = cplex.SumProd(1.0, v); 
    }
    
    public final void print(CplexExtended cplex) throws IloException{
        System.out.printf("%s.f    : %8g\n", name, cplex.getValue(f));
        System.out.printf("%s.v    : [ ", name);
        for (IloNumVar vi : v) {
            System.out.printf("%8g ", cplex.getValue(vi));
        }
        System.out.printf("]\n");
    }
}
