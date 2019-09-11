/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.BlackMore;

import ProOF.com.Linker.LinkerResults;
import ProOF.com.Stream.StreamPrinter;
import ProOF.opt.abst.problem.meta.objective.SingleObjective;

/**
 *
 * @author marcio
 */
public class BMObjective extends SingleObjective<BMProblem, BMCodification, BMObjective> {
    private double delta;
    private double dist_obj;
    private double cost;
    
    public BMObjective() throws Exception {
    }

    @Override
    public void evaluate(BMProblem prob, BMCodification codif) throws Exception {
        //------------------------[beta]---------------------------
        delta = 0;
        for(int t=0; t<prob.inst.T+1 && delta<prob.inst.DELTA; t++){
            for(int j=0; j<prob.inst.J; j++){
                delta += codif.Xt[t].NFZ_violation(prob, j, t);///Math.pow(1.1, t);
            }
        }
        delta = Math.max(0, delta-prob.inst.DELTA);
        
        //------------------------[beta]---------------------------
        
        dist_obj = codif.Xt[prob.inst.T].dist_obj(prob);
        
        
        //------------------------[goal]---------------------------
        cost = prob.obj.cost(prob, codif.Xt, codif.Ut);
        
        double fitness = prob.inst.GOAL(dist_obj) + prob.inst.DELTA(delta) + prob.inst.OBJ(cost);
        set(fitness);
    }
    @Override
    public void copy(BMProblem prob, BMObjective source) throws Exception {
        super.copy(prob, source);
        this.cost = source.cost;
        this.delta = source.delta;
        this.dist_obj = source.dist_obj;
    }

    @Override
    public BMObjective build(BMProblem prob) throws Exception {
        return new BMObjective();
    }
    
    @Override
    public void printer(BMProblem prob, StreamPrinter com, BMCodification codif) throws Exception {
        super.printer(prob, com, codif);
        com.printDbl("beta", prob.inst.GOAL(dist_obj));
        com.printDbl("delta", prob.inst.DELTA(delta));
        com.printDbl("obj_val", prob.inst.OBJ(cost));
        prob.serv.plot(prob, codif);
    }
    @Override
    public void results(BMProblem prob, LinkerResults com, BMCodification codif) throws Exception {
        super.results(prob, com, codif);
        com.writeDbl("beta", prob.inst.GOAL(dist_obj));
        com.writeDbl("delta", prob.inst.DELTA(delta));
        com.writeDbl("obj_val", prob.inst.OBJ(cost));
        prob.serv.results(com, prob.inst, codif);
    }
}
