/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.factorys;

import ProOF.apl.pog.dynamic.DyWalker;
import ProOF.apl.pog.dynamic.aDyProblem;
import ProOF.com.language.Factory;


/**
 *
 * @author marcio
 */
public final class fDynamicProblem extends Factory<aDyProblem>{
    public static final fDynamicProblem obj = new fDynamicProblem(); 
    @Override
    public String name() {
        return "fDynamic";
    }
    @Override
    public aDyProblem build(int index) {
        switch(index){
            case 0: return new DyWalker();
        }
        return null;
    }
}
