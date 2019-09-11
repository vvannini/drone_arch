/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ProOF.apl.jsa.problem.PPCCS.core;

import ProOF.apl.jsa.problem.PPCCS.instance.InstanceProblem;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.gen.best.BestSol;
import ProOF.opt.abst.problem.meta.Objective;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 * PPCCS - Path Planning in Case Critical Situation
 * @author jesimar
 */
public class PPCCSProblem extends Problem<BestSol>{

    protected final InstanceProblem inst = InstanceProblem.instance;
    
    @Override
    public String name() {
        return "jPPCCS";
    }

    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link); 
        link.add(inst);
        link.add(PPCCSOperators.obj);
    }        
    
    @Override
    public BestSol best() {
        return BestSol.object();
    }

    @Override
    public Codification build_codif() throws Exception {
        return new PPCCSCodification(inst.T);
    }

    @Override
    public Objective build_obj() throws Exception {
        return new PPCCSObjective(inst.L);
    }       
}
