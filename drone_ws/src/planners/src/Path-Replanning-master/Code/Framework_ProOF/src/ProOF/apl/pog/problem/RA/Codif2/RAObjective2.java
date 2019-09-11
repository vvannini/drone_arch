package ProOF.apl.pog.problem.RA.Codif2;

import ProOF.opt.abst.problem.meta.objective.BoundDbl;
import ProOF.opt.abst.problem.meta.objective.SingleObjective;
import java.util.ArrayList;

/**
 *
 * @author andre
 */
public class RAObjective2 extends SingleObjective<RAProblem2, RACodification2, RAObjective2>{
    private static final double alpha = 0.1;
    private static final BoundDbl bound = new BoundDbl(0, 1e15, 0.01);
    public RAObjective2() throws Exception {
        super(bound);
    }
    
    @Override
    public void evaluate(RAProblem2 prob, RACodification2 codif) throws Exception {
        ArrayList<Integer> Yt[] = prob.tsp.solve(codif.Wit);
        
        double tempoSetup = prob.seq.tempoSetup(Yt);
        
        double fitness = tempoSetup*alpha;
        
        int Wit[][] = new int[prob.inst.N][prob.inst.T];
        for(int t = 0; t < prob.inst.T; t++){
            for(int  i : Yt[t]){
                Wit[i][t]=1; 
            }
        }
        
        fitness += prob.solver.solve(Wit, prob.seq.STt);
        
        set(fitness);
    }
    @Override
    public RAObjective2 build(RAProblem2 prob) throws Exception {
        return new RAObjective2();
    }
}
