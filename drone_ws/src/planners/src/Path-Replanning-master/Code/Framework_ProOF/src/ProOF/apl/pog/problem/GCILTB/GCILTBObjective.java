/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCILTB;

import ProOF.opt.abst.problem.meta.objective.BoundDbl;
import ProOF.opt.abst.problem.meta.objective.SingleObjective;

/**
 *
 * @author Hossomi
 */
public class GCILTBObjective extends SingleObjective<GCILTBProblem, GCILTBCodification, GCILTBObjective> {

	private static BoundDbl uBound = new BoundDbl(0.0, 1e12, 0.001);

	public GCILTBObjective() throws Exception {
		super(uBound);
	}

	@Override
	public void evaluate(GCILTBProblem problem, GCILTBCodification codif) throws Exception {
		problem.model.initialize(codif);
		set(problem.model.execute(codif));
		//codif.print(problem);
		//System.out.printf("Fitness: %.0f\n\n", abs_value());
	}

	@Override
	public GCILTBObjective build(GCILTBProblem mem) throws Exception {
		return new GCILTBObjective();
	}
}
