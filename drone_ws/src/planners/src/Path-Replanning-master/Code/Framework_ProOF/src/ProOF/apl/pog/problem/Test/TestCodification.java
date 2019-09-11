/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.Test;

import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 *
 * @author marcio
 */
public class TestCodification extends Codification<
    TestFactory, TestCodification
> {
    
    public TestCodification(TestFactory prob) {
    }
    @Override
    public void copy(TestFactory prob, TestCodification source) throws Exception {
        
    }
    @Override
    public TestCodification build(TestFactory prob) throws Exception {
        return new TestCodification(prob);
    }
}
