/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.Blackmore;

import ProOF.CplexExtended.iCplexExtract;
import ProOF.apl.UAV.abst.uncertainty.Delta;
import ProOF.apl.UAV.gen.linear.LinearApproach;
import ProOF.apl.UAV.gen.linear.uncertainty.pLinearStateUncertainty;
import ProOF.apl.UAV.gen.linear.vehicle.parts.oLinearDynamic;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;

/**
 *
 * @author marcio
 */
public class BlackmoreApproach extends LinearApproach<BlackmoreModel>{
    private final BlackmoreStateUncertainty to_add = new BlackmoreStateUncertainty(this);
    public final BlackmorePlot plot = new BlackmorePlot(this);
    
    public BlackmoreInstance inst;
    public pLinearStateUncertainty unc;
    public oLinearDynamic dynamic;
    
    private final Delta delta = Delta.obj;
    
    private int Naprox;
    @Override
    public String name() {
        return "Blackmore";
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link); //To change body of generated methods, choose Tools | Templates.
        link.add(delta);
        link.add(to_add);
        link.add(plot);
        link.add(fBlackmoreOperator.obj);
        inst = link.get(fBlackmoreInstance.obj, inst);
        unc = link.need(pLinearStateUncertainty.class, unc);
        dynamic = link.need(oLinearDynamic.class, dynamic);
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        super.parameters(link); //To change body of generated methods, choose Tools | Templates.
        Naprox  = link.Int("Avoid-N-risk", 12, 1, 16, 
                "Number of picewise restriction to aproximate the "+
                "inverse of erf(x) to calculate the risk allocation "+
                "for obstacle avoidance");
        //Naprox = 8;
    }
    public int Naprox() {
        return Naprox;
    }
    public double Delta() throws Exception {
        return delta.Delta();
    }

    @Override
    public int N() throws Exception {
        return inst.N();
    }
    
    @Override
    public void solutionCallback(iCplexExtract ext, BlackmoreModel model, Callback type) throws Exception{ 
        //System.err.println("solution callback| model = "+model.name+" | type = "+type);
        model.extract(ext, type);
        plot.addModel(model);
    }    
}
