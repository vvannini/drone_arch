/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.Test;

import ProOF.opt.abst.problem.meta.objective.SingleObjective;

/**
 *
 * @author marcio
 */
public class TestObjective extends SingleObjective<TestFactory, TestCodification, TestObjective> {

    public TestObjective() throws Exception {
        super();
    }
    
    @Override
    public void evaluate(TestFactory mem, TestCodification codif) throws Exception {
        double fitness = 0;

        set(fitness);
    }

    @Override
    public TestObjective build(TestFactory mem) throws Exception {
        return new TestObjective();
    }
}
