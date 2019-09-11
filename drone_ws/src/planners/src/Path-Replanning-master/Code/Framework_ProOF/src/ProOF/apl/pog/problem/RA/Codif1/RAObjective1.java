package ProOF.apl.pog.problem.RA.Codif1;

import ProOF.com.Stream.StreamPrinter;
import ProOF.opt.abst.problem.meta.objective.BoundDbl;
import ProOF.opt.abst.problem.meta.objective.SingleObjective;

/**
 *
 * @author andre
 */
public class RAObjective1 extends SingleObjective<RAProblem1, RACodification1, RAObjective1>{
    private static final double alpha = 0.1;
    private static final BoundDbl bound = new BoundDbl(0, 1e15, 0.01);
    public RAObjective1() throws Exception {
        super(bound);
    }
    
    @Override
    public void evaluate(RAProblem1 prob, RACodification1 codif) throws Exception {
        for(int t = 0; t < prob.inst.T; t++){
            codif.repair(prob, t);
        }
        double tempoSetup = prob.seq.tempoSetup(codif.Yt);
        
        double fitness = tempoSetup*alpha;
        
        int Wit[][] = new int[prob.inst.N][prob.inst.T];
        for(int t = 0; t < prob.inst.T; t++){
            for(int  i : codif.Yt[t]){
                Wit[i][t]=1; 
            }
        }
        
        fitness += prob.solver.solve(Wit, prob.seq.STt);
        
        set(fitness);
    }
    @Override
    public RAObjective1 build(RAProblem1 prob) throws Exception {
        return new RAObjective1();
    }

    @Override
    public void printer(RAProblem1 prob, StreamPrinter com, RACodification1 codif) throws Exception {
        super.printer(prob, com, codif);
    }
    
}
