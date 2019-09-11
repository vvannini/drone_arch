/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.jsa.method.multistart;

import ProOF.apl.factorys.fStop;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.opt.abst.run.MetaHeuristic;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.Solution;
import ProOF.gen.stopping.CountIteration;
import ProOF.gen.stopping.Stop;
import ProOF.apl.factorys.fProblem;
import ProOF.gen.operator.Initialization;
import ProOF.gen.operator.Mutation;

/**
 *
 * @author Jesimar
 */
public class MultiStart extends MetaHeuristic {

    private Problem problem;    
    private Initialization init;
    private Mutation mutation;
    private Stop stop;
    
    private CountIteration attempts = CountIteration.obj;

    @Override
    public String name() {
        return "jMultiStart";
    }

    @Override
    public void services(LinkerApproaches link) throws Exception {
        link.add(attempts);
        problem = link.get(fProblem.obj, problem);        
        stop = link.get(fStop.obj, stop);        
        init = link.add(Initialization.obj);
        mutation = link.add(Mutation.obj);
    }

    @Override
    public void parameters(LinkerParameters win) throws Exception {
        
    }

    @Override
    public void execute() throws Exception {
        Solution sol = problem.build_sol();
        Solution nextSol;

        init.initialize(sol);
        problem.evaluate(sol);

        do {
            nextSol = sol.clone(problem);
            mutation.mutation(nextSol);
            problem.evaluate(nextSol);
            if (sol.GT(nextSol)) {
                sol = nextSol;
                attempts.start();
            } else {
                attempts.iteration();
            }
        } while (!stop.end());
    }
}
