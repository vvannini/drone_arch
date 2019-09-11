/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.opt.abst.problem.meta;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Approach;
import ProOF.opt.abst.problem.Dev;
import ProOF.opt.abst.problem.Gap;
import ProOF.opt.abst.problem.Val;
import ProOF.opt.abst.problem.meta.codification.Codification;
import ProOF.utilities.uRandom;
import ProOF.utilities.uRoulette;
import java.util.LinkedList;

/**
 *
 * @author marcio
 */
public abstract class Problem<B extends Best> extends Approach {

    public final uRandom rnd = new uRandom();
    public abstract B best();
    public abstract Codification build_codif() throws Exception;
    public abstract Objective build_obj() throws Exception;
    private final LinkedList<Val> vals = new LinkedList<Val>();
    
    public void add_gap(String title, double value){
        vals.addLast(new Gap(title, value));
        
    }
    public void add_desv(String title, double value){
        vals.addLast(new Dev(title, value));
    }
    public Val[] vals(){
        return vals.toArray(new Val[vals.size()]);
    }
    
    public uRoulette make(Solution pop[]){
        return new uRoulette(rnd, 2, pop.length);
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        link.add(best());
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        
    }
    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        return true;
    }
    @Override
    public void load() throws Exception {
        
    }
    @Override
    public void start() throws Exception {
        
    }

    @Override
    public void finish() throws Exception {
        if(best()!=null){
            best().flush(this);
        }
    }
    
    @Override
    public void results(LinkerResults link) throws Exception {
        if(best()!=null){
            best().results(this, link);
        }
    }

    public Solution build_sol() throws Exception{
        return new Sol();
    }
    public Solution build_sol(Codification codif) throws Exception{
        return new Sol(build_obj(), codif);
    }

    public void evaluate(Solution sol) throws Exception {
        sol.evaluate(this);
        if(best()!=null){
            best().better(this, sol);
        }
    }
    //public abstract void evaluate(pMetaSolution sol, pMetaSolution base, double cut_diff) throws Exception;
    /*public void evaluate(pMetaSolution sol, pMetaSolution base, double cut_diff) throws Exception {
        sol.Evaluate(this, base.objective., cut_diff);
    }*/
    
    private class Sol extends Solution
    {
        public Sol() throws Exception {
            super(build_obj(), build_codif());
        }
        public Sol(Objective obj, Codification codif) throws Exception {
            super(obj, codif);
        }
        @Override
        public Sol build(Problem mem) throws Exception {
            return new Sol();
        }
        @Override
        public Sol build(Problem mem, Codification codification) throws Exception {
            return new Sol(objective, codification);
        }
    }
}

