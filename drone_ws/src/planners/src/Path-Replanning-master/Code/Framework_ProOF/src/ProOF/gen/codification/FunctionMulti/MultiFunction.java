/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.gen.codification.FunctionMulti;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.language.Factory;
import ProOF.opt.abst.problem.meta.Best;
import ProOF.opt.abst.problem.meta.MultiProblem;
import ProOF.opt.abst.problem.meta.codification.Codification;
import ProOF.opt.abst.problem.meta.objective.BoundDbl;
import ProOF.opt.abst.problem.meta.objective.MultiObjective;

/**
 *
 * @author marcio
 */
public abstract class MultiFunction extends MultiProblem<Best> {
    protected RealMulti func;
    protected BoundDbl bounds[];
    
    private final Factory realMulti;
    private final Factory realOperator;
    public MultiFunction(Factory realMulti, Factory realOperator) {
        this.realMulti = realMulti;
        this.realOperator = realOperator;
    }
    
    @Override
    public abstract Codification build_codif()throws Exception;
    @Override
    public abstract MultiObjective build_obj()throws Exception;
    
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
        func = link.get(realMulti, func);
        link.add(realOperator);
    }

    @Override
    public void start() throws Exception {
        super.start(); //To change body of generated methods, choose Tools | Templates.
        bounds = func.bounds();
    }
    
    @Override
    public Best best(){
        return null;
    }
    @Override
    public int goals() throws Exception {
        return func.goals();
    }
}

