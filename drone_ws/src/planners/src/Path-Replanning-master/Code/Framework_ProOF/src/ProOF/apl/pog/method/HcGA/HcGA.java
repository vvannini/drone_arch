/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method.HcGA;

import ProOF.apl.factorys.fStop;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.opt.abst.run.MetaHeuristic;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.Solution;
import ProOF.gen.stopping.Stop;
import ProOF.gen.operator.oFixAndOpt;
import ProOF.gen.operator.oHcGA;
import ProOF.apl.factorys.fProblem;


/**
 *
 * @author marcio
 */
public class HcGA extends MetaHeuristic{
    private int popSize;
    private int nInds;
    private Problem problem;
    private Stop stop;
    private oHcGA operator;
    private oFixAndOpt fixAndOpt;
    
    public HcGA() {
        //generations = cIteration.object();
    }
    @Override
    public String name() {
        return "HcGA";
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        popSize     = link.Int("population size",      10,  3, 1000);
        nInds       = link.Int("individuals created",   5,  3, 1000);
    }
    @Override
    public void services(LinkerApproaches com) throws Exception {
        problem     = com.get(fProblem.obj, problem);
        stop        = com.get(fStop.obj, stop);
        operator    = com.need(oHcGA.class, operator);
        fixAndOpt   = com.need(oFixAndOpt.class, fixAndOpt);
    }
    
    public Solution getBetter(Solution ...pop){
        int p = 0;
        for(int i=1; i<pop.length; i++){
            if(pop[i].LT(pop[p])){
                p = i;
            }
        }
        return pop[p];
    }
    @Override
    public void execute() throws Exception {
        Solution best = null;
        Solution pop[] = new Solution[popSize];
        for(int i=0; i<pop.length; i++){
            pop[i] = problem.build_sol();
        }
        
        operator.initialize(problem);
        do{
            for(int n=0; n<nInds; n++){
                operator.create(problem, pop[n]);
                problem.evaluate(pop[n]);
            }
            Solution ind = getBetter(pop);
            ind = fixAndOpt.execute(problem, ind);
            if(ind.LT(best)){
                operator.update(problem, ind, popSize);
                best = ind;
            }else{
                fixAndOpt.increment(problem);
            }
        }while(!stop.end());
        
    }
    @Override
    public void results(LinkerResults win) throws Exception {
        //win.writeLong("generations", generations.value());
    }
}
