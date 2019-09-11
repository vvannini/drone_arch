/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.factorys;

import ProOF.apl.pog.problem.ACK.ACK2;
import ProOF.apl.sample1.problem.real.single.ACK;
import ProOF.apl.sample1.problem.real.single.B2;
import ProOF.com.language.Factory;
import ProOF.gen.codification.FunctionSingle.RealSingle;

/**
 *
 * @author marcio
 */
public final class fRealSingle extends Factory<RealSingle>{
    public static final fRealSingle obj = new fRealSingle(); 
    @Override
    public String name() {
        return "fRealSingle";
    }
    @Override
    public RealSingle build(int index) {
        switch(index){
            case 0: return new B2();
            case 1: return new ACK();
            case 2: return new ACK2();
        }
        return null;
    }
}
