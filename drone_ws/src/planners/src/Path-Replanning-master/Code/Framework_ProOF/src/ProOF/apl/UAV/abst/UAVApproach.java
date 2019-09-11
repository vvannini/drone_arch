/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.abst;

import ProOF.CplexExtended.iCplexExtract;
import ProOF.apl.UAV.Swing.Graphics2DReal;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Approach;

/**
 *
 * @author marcio
 * @param <Model>
 */
public abstract class UAVApproach<Model extends UAVModel> extends Approach{
    public enum Callback {
        PARTIAL_INCUMBENT, PARTIAL_OPTIMAL, PARTIAL_FEASIBLE, OPTIMAL, FEASIBLE
    }
    
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        
    }
    
    @Override
    public void load() throws Exception {
        
    }
    @Override
    public void start() throws Exception {
        
    }
    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public abstract void solutionCallback(iCplexExtract ext, Model model, Callback type) throws Exception;
    public abstract void paint(Graphics2DReal gr, double size) throws Exception;
    
    @Override
    public void results(LinkerResults link) throws Exception {
        
    }
//    public abstract M build_model(String name) throws Exception;
//    public abstract M build_model(String name, CplexExtended cplex) throws Exception;
//    
    //public abstract double Delta();
    //public abstract int Waypoints();
    //public abstract int N();
//    public abstract double maxControl();
//    public abstract double maxVelocity();
    
    public void solve(Model model) throws Exception {
        model.solve();
    }
}
