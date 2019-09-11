/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method;

import ProOF.apl.factorys.fStop;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.opt.abst.run.MetaHeuristic;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.Solution;
import ProOF.gen.stopping.CountIteration;
import ProOF.gen.stopping.Stop;
import ProOF.gen.operator.oInitialization;
import ProOF.gen.operator.oLocalMove;
import ProOF.apl.factorys.fProblem;

/**
 *
 * @author Hossomi
 */
public class Descend extends MetaHeuristic {

    private Problem problem;
    private oInitialization init;
    private oLocalMove move;
    private Stop stop;

    private int maxAttempts;
    private CountIteration attempts = CountIteration.obj;

    @Override
    public String name() {
        return "Descend";
    }

    @Override
    public void services(LinkerApproaches com) throws Exception {
        com.add(attempts);
        problem = com.get(fProblem.obj, problem);
        stop = com.get(fStop.obj, stop);
        init = com.need(oInitialization.class, init);
        move = com.need(oLocalMove.class, move);
    }

    @Override
    public void parameters(LinkerParameters win) throws Exception {
        maxAttempts = win.Int("Max Attempts", 100);
    }

    @Override
    public void execute() throws Exception {
        Solution sol = problem.build_sol();
        Solution nextSol;

        init.initialize(problem, sol.codif());
        problem.evaluate(sol);

        do {
            nextSol = sol.clone(problem);
            move.local_search(problem, nextSol.codif());
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
