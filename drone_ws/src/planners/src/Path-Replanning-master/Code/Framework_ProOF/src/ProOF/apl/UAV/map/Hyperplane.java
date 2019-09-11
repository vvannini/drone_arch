/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.map;

import ProOF.CplexExtended.CplexExtended;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;

/**
 *
 * @author marcio
 */
public class Hyperplane {
    
    public final double a[];
    public final double b;
    
    public Hyperplane(double b, double ...a) {
        this.a = a;
        this.b = b;
    }
    
    public double scalarProd(double ...p) {
        double sum = 0;
        for(int n=0; n<a.length; n++){
            sum += a[n] * p[n];
        }
        return sum;
    }
    
    public IloNumExpr scalarProd(CplexExtended cplex, IloNumExpr... p) throws IloException {
        IloNumExpr exp = null;
        for(int n=0; n<a.length; n++){
            exp = cplex.SumProd(exp, a[n], p[n]);
        }
        return exp;
    }
}
