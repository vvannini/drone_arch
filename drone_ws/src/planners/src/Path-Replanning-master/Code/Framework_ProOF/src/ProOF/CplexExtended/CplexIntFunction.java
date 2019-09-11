/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ProOF.CplexExtended;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;

/**
 *
 * @author marcio
 */
public abstract class CplexIntFunction implements iCplexFunction1v{
    private final IloNumVar v[];
    private final IloIntVar w[];
    public final IloNumVar x;
    public final IloNumVar f;
    public final int N;
    public final String name;
    public CplexIntFunction(CplexExtended cplex, int N, String name) throws IloException {
        this.N = N;
        this.name = name;
        v = cplex.numVarArray(N, 0, Double.POSITIVE_INFINITY, name+".v");
        w = cplex.boolVarArray(N-1, name+".w");
        x = cplex.numVar(x(0), x(N), name+".x");
        f = cplex.numVar(minF(), maxF(), name+".f");
        for(int n=0; n<N; n++){
            v[n].setUB(dx(n));
        }
        
        for (int n=0; n<N; n++) {
            if(n<N-1){
                cplex.addGe(v[n], cplex.prod(dx(n),w[n]), name);
            }
            if(n>0){
                cplex.addLe(v[n], cplex.prod(dx(n),w[n-1]), name);
            }
        }
        cplex.addEq(x, cplex.sum(x(0), cplex.sum(v)), name+".x_sum(v)");
        
        IloNumExpr sum = null;
        for (int n=0; n<N; n++) {
            double a = df(n) / dx(n);
            if (sum == null) {
                sum = cplex.prod(a, v[n]);
            } else {
                sum = cplex.sum(sum, cplex.prod(a, v[n]));
            }
        }
        cplex.addEq(f, cplex.sum(f(0), sum), name+".f_sum(Mw)");
    }
    private double dx(int n) throws IloException{
        if(x(n+1) < x(n)){
            throw new IloException("x(n+1)<x(n) | it is need a crescent sequence");
        }
        return x(n+1) - x(n);
    }
    private double df(int n) throws IloException{
        return f(n+1) - f(n);
    }
    private double minF(){
        double min = Double.POSITIVE_INFINITY;
        for(int n=0; n<=N; n++){
            min = Math.min(min, f(n));
        }
        return min;
    }
    private double maxF(){
        double max = Double.NEGATIVE_INFINITY;
        for(int n=0; n<=N; n++){
            max = Math.max(max, f(n));
        }
        return max;
    }
    
    public final void print(CplexExtended cplex) throws IloException{
        System.out.printf("%s.x    : %8g\n", name, cplex.getValue(x));
        System.out.printf("%s.f    : %8g\n", name, cplex.getValue(f));
        System.out.printf("%s.v    : [ ", name);
        for(int n=0; n<N; n++){
            System.out.printf("%8g ", cplex.getValue(v[n]));
        }
        System.out.printf("]\n");
        System.out.printf("%s.w    : [ ", name);
        for(int n=0; n<N-1; n++){
            System.out.printf("%8g ", cplex.getValue(w[n]));
        }
        System.out.printf("]\n");
    }
    
    @Override
    public abstract double x(int n);
    @Override
    public abstract double f(int n);
}
