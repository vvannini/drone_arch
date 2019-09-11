/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.PPCCS;

import ProOF.CplexExtended.iCplexExtract;
import ProOF.apl.UAV.abst.uncertainty.Delta;
import ProOF.apl.UAV.gen.linear.LinearApproach;
import ProOF.apl.UAV.gen.linear.uncertainty.pLinearStateUncertainty;
import ProOF.apl.UAV.gen.linear.vehicle.parts.oLinearDynamic;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;

/**
 *
 * @author marcio e jesimar
 */
public class PPCCSApproach extends LinearApproach<PPCCSModel>{
    
    private final PPCCSStateUncertainty ppccsUnc = new PPCCSStateUncertainty(this);
    public final PPCCSPlot plot = new PPCCSPlot(this);
    
    
    public PPCCSInstance inst;
    public pLinearStateUncertainty unc;
    public oLinearDynamic dynamic;
    
    private final Delta delta = Delta.obj;    
    private int Naprox;
    
    @Override
    public String name() {
        return "PPCCS";
    }
    
    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
        link.add(delta);
        link.add(ppccsUnc);
        link.add(plot);
        link.add(fPPCCSOperator.obj);
        inst = link.get(fPPCCSInstance.obj, inst);
        unc = link.need(pLinearStateUncertainty.class, unc);
        dynamic = link.need(oLinearDynamic.class, dynamic);
    }
    
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        super.parameters(link);
        Naprox  = link.Int("Avoid-N-risk", 12, 1, 16, 
                "Number of picewise restriction to aproximate the "+
                "inverse of erf(x) to calculate the risk allocation "+
                "for obstacle avoidance");
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
    public void solutionCallback(iCplexExtract ext, PPCCSModel model, Callback type) throws Exception{       
        model.extract(ext, type);
        plot.addModel(model);
    }    

    @Override
    public void results(LinkerResults link) throws Exception {
        super.results(link);                
    }        
}
