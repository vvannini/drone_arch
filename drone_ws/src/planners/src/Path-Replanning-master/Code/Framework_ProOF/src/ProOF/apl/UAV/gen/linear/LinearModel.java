/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.gen.linear;

import ProOF.CplexExtended.CplexExtended;
import ProOF.CplexExtended.iCplexExtract;
import ProOF.apl.UAV.Swing.Graphics2DReal;
import ProOF.apl.UAV.abst.UAVApproach.Callback;
import ProOF.apl.UAV.abst.UAVModel;
import ilog.concert.IloException;
import java.awt.Color;
import java.awt.Font;
 
/**
 *
 * @author marcio
 * @param <App>
 */
public abstract class LinearModel<App extends LinearApproach> extends UAVModel<App>{
    public final LinearState states[];
    public final LinearControl controls[]; 
    
    public LinearModel(App approach, String name, CplexExtended cplex) throws IloException, Exception {
        super(name, approach, cplex);
        states = approach.vehicle.build_states(approach, this);
        controls = approach.vehicle.build_controls(approach, this);
        approach.vehicle.addConstraints(approach, this);
        approach.mission.addConstraints(approach, this);
        approach.mission.addObjective(approach, this);
    }
    @Override
    public void extract(iCplexExtract ext, Callback type) throws Exception {
        for(LinearState s : states){
            s.extract(ext);
        }
        for(LinearControl c : controls){
            c.extract(ext);
        }
    }
    
    @Override
    public void paint(Graphics2DReal gr, double size) throws Exception {
        for(int t=0; t<states.length-1; t++){//plot lines
            states[t].plot.drawLine(gr, Color.BLACK, states[t+1].plot);
        }
        for(int t=0; t<states.length; t++){
            states[t].plot.fillPoint(gr, Color.BLACK, 0.005*size);
            states[t].plot.drawLabel(gr, Color.BLACK, 0.005*size);
        }
    }
    
    @Override
    public void paint(Graphics2DReal gr, Font font, double size) throws Exception {
        for(int t=0; t<states.length-1; t++){//plot lines
            states[t].plot.drawLine(gr, Color.BLACK, states[t+1].plot);
        }
        for(int t=0; t<states.length; t++){
            states[t].plot.fillPoint(gr, Color.BLACK, 0.005*size);
            states[t].plot.drawLabel(gr, Color.BLACK, font, 0.005*size);
        }
    }
}
