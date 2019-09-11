/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.FunctionSingle;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.language.Factory;
import ProOF.gen.best.BestSol;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.codification.Codification;
import ProOF.opt.abst.problem.meta.objective.BoundDbl;
import ProOF.opt.abst.problem.meta.objective.SingleObjective;

/**
 *
 * @author marcio
 */
public abstract class SingleFunction extends Problem<BestSol>{
    protected RealSingle func;
    protected BoundDbl bound;
    
    private final Factory single;
    private final Factory operator;
    public SingleFunction(Factory single, Factory operator) {
        this.single = single;
        this.operator = operator;
    }
    
    @Override
    public abstract Codification build_codif()throws Exception;
    @Override
    public abstract SingleObjective build_obj()throws Exception;
    
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public final void services(LinkerApproaches link) throws Exception {
        super.services(link);
        func = link.get(single, func);
        link.add(operator);
    }

    @Override
    public void start() throws Exception {
        super.start(); //To change body of generated methods, choose Tools | Templates.
        bound = func.bound();
    }
    
    @Override
    public BestSol best(){
        return BestSol.object();
    }
}

