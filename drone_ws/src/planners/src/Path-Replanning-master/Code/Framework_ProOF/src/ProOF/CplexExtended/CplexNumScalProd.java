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
public class CplexNumScalProd {
    private final IloNumVar v[];
    private final CplexNumFunction prods[];
    public final IloNumExpr f;
    public final String name;
    public CplexNumScalProd(CplexExtended cplex, String name, int N, IloNumExpr ...exp) throws IloException {
        this(cplex, name, N, Double.POSITIVE_INFINITY, exp);
    }
    public CplexNumScalProd(CplexExtended cplex, String name, int N, final double MAX, IloNumExpr ...exp) throws IloException {
        if(MAX<0){
            throw new IloException("MAX<0 is invalid");
        }
        this.name = name;
        //v = cplex.numVarArray(exp.length, 0, MAX*MAX, name+".v");
        v = cplex.numVarArray(exp.length, 0, Double.POSITIVE_INFINITY, name+".v");
        prods = new CplexNumFunction[exp.length];
        
        for (int j = 0; j < exp.length; j++) {
            prods[j] = new CplexNumFunction(cplex, N, false, name) {
                @Override
                public double x(int n) {
                    return n*MAX/N;
                }
                @Override
                public double f(int n) {
                    return x(n)*x(n);
                }
            };
            cplex.addEq(v[j], prods[j].f, name+".prod"+j);
            
            cplex.addGe(prods[j].x, cplex.prod(+1, exp[j]), name+".mod"+(j));
            cplex.addGe(prods[j].x, cplex.prod(-1, exp[j]), name+".mod"+(j));
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
        for (CplexNumFunction prod : prods) {
            prod.print(cplex);
        }
    }
}
