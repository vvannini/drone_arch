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
public class CplexNumNorm2_2D {
    public final IloNumVar f;
    public final String name;
    public CplexNumNorm2_2D(CplexExtended cplex, String name, int N, IloNumExpr exp_x, IloNumExpr exp_f) throws IloException {
        this(cplex, name, N, Double.POSITIVE_INFINITY, exp_x, exp_f);
    }
    public CplexNumNorm2_2D(CplexExtended cplex, String name, int N, double MAX, IloNumExpr exp_x, IloNumExpr exp_f) throws IloException {
        if(N<3){
            throw new IloException("N<3 is invalid");
        }
        if(MAX<0){
            throw new IloException("MAX<0 is invalid");
        }
        this.name = name;
        f = cplex.numVar(0, MAX, name+".f");
        for (int n = 0; n < N; n++) {
            IloNumExpr exp = cplex.sum(
                cplex.prod(Math.cos((2 * Math.PI * n) / N), exp_x), 
                cplex.prod(Math.sin((2 * Math.PI * n) / N), exp_f)
            );
            cplex.addGe(f, exp, name+".f_ge_norm2");
        }
    }
    
    public final void print(CplexExtended cplex) throws IloException{
        System.out.printf("%s.f    : %8g\n", name, cplex.getValue(f));
    }
}
