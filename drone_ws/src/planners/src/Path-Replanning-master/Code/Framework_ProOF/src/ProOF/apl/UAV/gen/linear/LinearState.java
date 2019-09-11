/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.gen.linear;

import ProOF.CplexExtended.CplexExtended;
import ProOF.CplexExtended.iCplexExtract;
import ProOF.apl.UAV.abst.State;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marcio
 */
public abstract class LinearState extends State{
    public final IloNumVar x[];   //[px   py  ...| vx  vy  ...]d
    public final int t;
    private final CplexExtended cplex;
    
    public LinearState(CplexExtended cplex, int N, int t) throws IloException {
        this.t = t;
        x = cplex.numVarArray(2*N, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, "X");
        this.cplex = cplex;
    }
    
    public abstract IloNumExpr delta() throws IloException;
    public abstract IloNumExpr risk() throws IloException;
    
//    public final double[] x(iCplexExtract ext) throws IloException{
//        return ext.getValues(x);
//    }
//    public final double delta(iCplexExtract ext) throws IloException{
//        IloNumExpr risk = delta();
//        if(risk!=null){
//            return ext.getValue(risk);
//        }
//        return 0;
//    }
//    public final double risk(iCplexExtract ext) throws IloException{
//        IloNumExpr risk = risk();
//        if(risk!=null){
//            return ext.getValue(risk);
//        }
//        return 0;
//    }
    
    public LinearPlotState plot;
    
    @Override
    public final void extract(iCplexExtract ext) throws IloException{
        plot= new LinearPlotState(ext.getValues(x), t);
    }

    public final double[] x() {
        return plot.copy_x();
    }

    @Override
    public String toString() {        
        try {
            String str = "x: ";
            for (IloNumVar valueX : x) {
                str += String.format("%10.2f,", cplex.getValue(valueX));
            }
            return str;
        } catch (IloException ex) {
            Logger.getLogger(LinearState.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
}
