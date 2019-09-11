/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.advanced1.FMS.local_search;


import ProOF.com.Linker.LinkerApproaches;
import ProOF.opt.abst.problem.meta.Problem;
import ProOF.opt.abst.problem.meta.Solution;
import ProOF.gen.operator.gLocalMove;
import ProOF.gen.operator.oLocalMove;
import ProOF.apl.advanced1.FMS.temperature.Temperature;
import ProOF.apl.factorys.fTemperature;

/**
 *
 * @author marcio
 */
public class SA extends LocalSearch{
    private Temperature function;
    private oLocalMove moves[];

    @Override
    public String name() {
        return "SA-Cooling";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        function = link.get(fTemperature.obj, function);
        moves = link.needs(oLocalMove.class, new oLocalMove[1]);
    }
    
    @Override
    public void execute(Problem problem, Solution best) throws Exception {
        gLocalMove op_move = new gLocalMove(problem, moves);
        Solution current = best;
        function.start();
        do{
            double x = problem.rnd.nextDouble();

            Solution neibor = op_move.local_search(current);

            problem.evaluate(neibor);

            double delta = neibor.compareToAbs(current);

            if(delta < 0){
                current = neibor;
                best.copyIfBetter(problem, current);
            }else if(x < Math.exp(-delta/function.temperature())){
                current = neibor;
            }
            function.decress();
        }while(!function.end());
    }
}
