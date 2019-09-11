/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCILT.withZyt;

import ProOF.opt.abst.problem.meta.objective.BoundDbl;
import ProOF.opt.abst.problem.meta.objective.SingleObjective;

/**
 *
 * @author Hossomi
 */
public class GCILTObjective extends SingleObjective<GCILTWProblem, GCILTCodification, GCILTObjective> {

    private static BoundDbl uBound = new BoundDbl(0.0, 1e12, 0.001);

    public GCILTObjective() throws Exception {
        super(uBound);
    }

    @Override
    public void evaluate(GCILTWProblem problem, GCILTCodification codif) throws Exception {
        double penalty = problem.model.initialize(codif);

        if (penalty <= 0.99) {
            double fitness = problem.model.execute(codif);
            set(fitness);
        } else {
            //System.out.println("penalty = " + penalty * 1e9);
            set(penalty * 1e9);
        }
        //System.exit(-1);
    }

    @Override
    public GCILTObjective build(GCILTWProblem mem) throws Exception {
        return new GCILTObjective();
    }
}
