/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.gen.linear;

import ProOF.CplexExtended.CplexExtended;
import ProOF.CplexExtended.iCplexExtract;
import ProOF.apl.UAV.abst.Control;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import java.awt.Graphics2D;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marcio
 */
public abstract class LinearControl extends Control{
    
    public IloNumVar u[];   //[ax   ay  ...]
    private final CplexExtended cplex; 
    
    public LinearControl(CplexExtended cplex, int N) throws IloException {
        u = cplex.numVarArray(N, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, "U");
        this.cplex = cplex;
    }
    public abstract IloNumExpr delta() throws Exception;
    public abstract IloNumExpr risk() throws Exception;
    
//    public final double[] u(iCplexExtract ext) throws Exception{
//        return ext.getValues(u);
//    }
//    public final double delta(iCplexExtract ext) throws Exception{
//        IloNumExpr risk = delta();
//        if(risk!=null){
//            return ext.getValue(risk);
//        }
//        return 0;
//    }
//    public final double risk(iCplexExtract ext) throws Exception{
//        IloNumExpr risk = risk();
//        if(risk!=null){
//            return ext.getValue(risk);
//        }
//        return 0;
//    }
    
    public double sol_u[];
    public final void extract(iCplexExtract ext) throws IloException, Exception{
        sol_u = ext.getValues(u);
    }
    public final double[] u() {
        return sol_u;
    }
    public void plot(Graphics2D g2) {
        //g2.setColor(Color.BLACK);
        //g2.drawOval(unit(sol_x[0]-0.05), unit(sol_x[0]-0.05), unit(0.1), unit(0.1));
    }
    
    @Override
    public String toString() {        
        try {
            String str = "u: ";
            for (IloNumVar valueU : u) {
                str += String.format("%10.2f,", cplex.getValue(valueU));
            }
            return str;
        } catch (IloException ex) {
            Logger.getLogger(LinearState.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
}
