/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.method;

import ProOF.gen.stopping.CountIteration;
import ProOF.gen.stopping.Stop;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.Solution;
import ProOF.opt.abst.run.MetaHeuristic;

import ProOF.gen.operator.gInitialization;
import ProOF.gen.operator.gLocalMove;
import ProOF.gen.operator.oInitialization;
import ProOF.gen.operator.oLocalMove;

import ProOF.apl.factorys.fStop;
import ProOF.apl.factorys.fTemperature;
import ProOF.apl.advanced1.FMS.temperature.Temperature;
import ProOF.apl.factorys.fProblem;

import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerApproaches;


/**
 *
 * @author marcio
 */
public final class mSAImplementation extends MetaHeuristic{
    private Problem problem;
    private Stop stop_criterion;
    private Temperature function;
    
    private oInitialization init;
    private oLocalMove moves[];
    
    private CountIteration loop = CountIteration.obj;
    
    @Override
    public String name() {
        return "Simulated Annealing";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void services(LinkerApproaches com) throws Exception {
        com.add(loop);
        
        problem = com.get(fProblem.obj, problem);
        stop_criterion = com.get(fStop.obj, stop_criterion);
        function = com.get(fTemperature.obj, function);
        init = com.need(oInitialization.class, init);
        moves = com.needs(oLocalMove.class, new oLocalMove[1]);
    }
    @Override
    public void execute() throws Exception {
        gInitialization op_init = new gInitialization(problem, init);
        gLocalMove op_move = new gLocalMove(problem, moves);
        
        Solution sol = problem.build_sol();
        
        op_init.initialize(sol);
        problem.evaluate(sol);
        
        do{
            function.start();
            do{
                double x = problem.rnd.nextDouble();
                
                Solution neibor = op_move.local_search(sol);
                
                problem.evaluate(neibor);

                double delta = neibor.compareToAbs(sol);
                
                if(delta < 0){
                    sol = neibor;
                }else if(x < Math.exp(-delta/function.temperature())){
                    sol = neibor;
                }
                function.decress();
            }while(!function.end());
            
            loop.iteration();
        }while(!stop_criterion.end());
    }
    @Override
    public void results(LinkerResults win) throws Exception {
        win.writeLong("iterations", loop.value());
    }
}
