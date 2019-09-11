/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.TSP;

import ProOF.opt.abst.problem.meta.objective.SingleObjective;

/**
 *
 * @author marcio
 */
public class TSPObjective extends SingleObjective<TSPFactory, TSPCodification, TSPObjective> {

    public TSPObjective() throws Exception {
        super();
    }
    
    @Override
    public void evaluate(TSPFactory mem, TSPCodification codif) throws Exception {
        double fitness = 0;
        int i = codif.path[codif.path.length-1];
        for(int j : codif.path){
            fitness += mem.inst.Cij[i][j];
            i = j;
        }
        set(fitness);
    }

    @Override
    public TSPObjective build(TSPFactory mem) throws Exception {
        return new TSPObjective();
    }
}
