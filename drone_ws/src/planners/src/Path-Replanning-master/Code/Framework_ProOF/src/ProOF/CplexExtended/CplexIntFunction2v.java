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
public abstract class CplexIntFunction2v implements iCplexFunction2v{
    private final IloNumVar vx[][];
    private final IloNumVar vy[][];
    private final IloIntVar w[][];
    public final IloNumVar x;
    public final IloNumVar y;
    
    public final IloNumVar f;
    public final int N;
    public final int M;
    public final String name;
    public CplexIntFunction2v(CplexExtended cplex, int N, int M, String name) throws IloException {
        this.N = N;
        this.M = M;
        this.name = name;
        vx = cplex.numVarArray(N-1, M-1,  0, 1, name+".vx");
        vy = cplex.numVarArray(N-1, M-1,  0, 1, name+".vy");
        w = cplex.boolVarArray(N-1, M-1, name+".w");
        x = cplex.numVar(x(0), x(N), name+".x");
        y = cplex.numVar(y(0), y(M), name+".y");
        f = cplex.numVar(minF(), maxF(), name+".f");
        
        
        for (int n=0; n<N-1; n++) {
            for (int m=0; m<M-1; m++) {
                cplex.addLe(vx[n][m], w[n][m]);
            }
        }
        for (int n=0; n<N-1; n++) {
            for (int m=0; m<M-1; m++) {
                cplex.addLe(vy[n][m], w[n][m]);
            }
        }
        
        IloNumExpr sum = null;
        
        
        sum = null;
        for (int n=0; n<N-1; n++) {
            for (int m=0; m<M-1; m++) {
                sum = cplex.SumProd(sum, 1, w[n][m]);
            }
        }
        cplex.addEq(sum, 1, name+".sum(w)_1");
        
        sum = null;
        for (int n=0; n<N-1; n++) {
            for (int m=0; m<M-1; m++) {
                sum = cplex.SumProd(sum, x(n), w[n][m]);
                sum = cplex.SumProd(sum, dx(n), vx[n][m]);
            }
        }
        cplex.addEq(x, sum, name+".x_sum()");
        
        sum = null;
        for (int n=0; n<N-1; n++) {
            for (int m=0; m<M-1; m++) {
                sum = cplex.SumProd(sum, y(m), w[n][m]);
                sum = cplex.SumProd(sum, dy(m), vy[n][m]);
            }
        }
        cplex.addEq(y, sum, name+".y_sum()");
        
        sum = null;
        for (int n=0; n<N-1; n++) {
            for (int m=0; m<M-1; m++) {
                sum = cplex.SumProd(sum, f(n, m), w[n][m]);
                
                double ax = df_x(n, m);
                double ay = df_y(n, m);
                sum = cplex.SumProd(sum, ax, vx[n][m]);
                sum = cplex.SumProd(sum, ay, vy[n][m]);
            }
        }
        cplex.addEq(f, sum, name+".f_sum()");
    }
    private double dx(int n) throws IloException{
        if(x(n+1) < x(n)){
            throw new IloException("x(n+1)<x(n) | it is need a crescent sequence");
        }
        return x(n+1) - x(n);
    }
    private double dy(int m) throws IloException{
        if(y(m+1) < y(m)){
            throw new IloException("y(m+1)<y(m) | it is need a crescent sequence");
        }
        return y(m+1) - y(m);
    }
    private double df_x(int n, int m) throws IloException{
        return f(n+1, m) - f(n, m);
    }
    private double df_y(int n, int m) throws IloException{
        return f(n, m+1) - f(n, m);
    }
    private double minF(){
        double min = Double.POSITIVE_INFINITY;
        for(int n=0; n<=N; n++){
            for(int m=0; m<=M; m++){
                min = Math.min(min, f(n,m));
            }
        }
        return min;
    }
    private double maxF(){
        double max = Double.NEGATIVE_INFINITY;
        for(int n=0; n<=N; n++){
            for(int m=0; m<=M; m++){
                max = Math.max(max, f(n,m));
            }
        }
        return max;
    }
    
    public final void print(CplexExtended cplex) throws IloException{
        System.out.printf("%s.x    : %8g\n", name, cplex.getValue(x));
        System.out.printf("%s.y    : %8g\n", name, cplex.getValue(y));
        System.out.printf("%s.f    : %8g\n", name, cplex.getValue(f));
        System.out.printf("%s.w    : [\n", name);
        for(int n=0; n<N-1; n++){
            System.out.printf("\t[ ");
            for(int m=0; m<M-1; m++){
                System.out.printf("%8g ", cplex.getValue(w[n][m]));
            }
            System.out.printf("]\n");
        }
        System.out.printf("]\n");
        
        System.out.printf("%s.vx   : [\n", name);
        for(int n=0; n<N-1; n++){
            System.out.printf("\t[ ");
            for(int m=0; m<M-1; m++){
                System.out.printf("%8g ", cplex.getValue(vx[n][m]));
            }
            System.out.printf("]\n");
        }
        System.out.printf("]\n");
        
        System.out.printf("%s.vy   : [\n", name);
        for(int n=0; n<N-1; n++){
            System.out.printf("\t[ ");
            for(int m=0; m<M-1; m++){
                System.out.printf("%8g ", cplex.getValue(vy[n][m]));
            }
            System.out.printf("]\n");
        }
        System.out.printf("]\n");
        
//        System.out.printf("%s.w    : [\n", name);
//        for(int n=0; n<N-1; n++){
//            System.out.printf("\t[ ");
//            for(int m=0; m<M-1; m++){
//                System.out.printf("%8g ", cplex.getValue(w[n][m]));
//            }
//            System.out.printf("]\n");
//        }
//        System.out.printf("]\n");
    }
    
    @Override
    public abstract double x(int n);

    @Override
    public abstract double y(int m);
    
    @Override
    public abstract double f(int n, int m);
}
