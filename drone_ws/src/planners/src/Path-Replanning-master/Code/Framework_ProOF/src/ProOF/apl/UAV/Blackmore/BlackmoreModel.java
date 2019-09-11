/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.Blackmore;

import ProOF.CplexExtended.CplexExtended;
import ProOF.apl.UAV.Swing.Graphics2DReal;
import ProOF.apl.UAV.gen.linear.LinearModel;
import ProOF.apl.UAV.gen.linear.LinearState;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import java.awt.Color;

/**
 *
 * @author marcio
 */
public class BlackmoreModel extends LinearModel<BlackmoreApproach>{
    
    private final IloIntVar[][][] Zjti;
    
    public BlackmoreModel(BlackmoreApproach approach, String name, CplexExtended cplex) throws IloException, Exception {
        super(approach, name, cplex);
        Zjti = new IloIntVar[approach.inst.J()][approach.Waypoints()+1][];
        for(int j=0; j<approach.inst.J(); j++){
            for(int t=0; t<approach.Waypoints()+1; t++){
                Zjti[j][t] = cplex.boolVarArray(approach.inst.Gj(j), "Z"+(j+1)+""+(t));
            }
        }
    }   

    @Override
    public boolean addChanges() throws Exception {
        return false;
    }
    
    public final IloIntVar Z(int j, int t, int i){
        return Zjti[j][t][i];
    }

    public final int[][][] getZjti() throws IloException{
        int vZjti[][][] = new int[approach.inst.J()][approach.Waypoints()+1][];
        for(int j=0; j<approach.inst.J(); j++){
            for(int t=0; t<approach.Waypoints()+1; t++){
                vZjti[j][t] = new int[approach.inst.Gj(j)];                
                double vals[] = cplex.getValues(Zjti[j][t]);
                for(int i=0; i<approach.inst.Gj(j); i++){
                    vZjti[j][t][i] = vals[i] > 0.5 ? 1 : 0; 
                }                
            }
        }
        return vZjti;
    }
    public final double[][] getXt() throws IloException {
        double [][] vXt = new double[approach.Waypoints()+1][];
        for(int t=0; t<approach.Waypoints()+1; t++){
            vXt[t] = cplex.getValues(states[t].x);
        }
        return vXt;
    }   
    
    @Override
    public void paint(Graphics2DReal gr, double size) throws Exception {       
        super.paint(gr, size);
        for (LinearState state : states) {
            state.plot.drawRiskAllocation(gr, approach.unc, approach.Delta(), Color.RED, approach.inst.obstacles);
        }
    }

    @Override
    public void save() throws Exception {
        
    }
}
