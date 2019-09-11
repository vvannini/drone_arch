/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.factorys;

import ProOF.apl.pog.problem.PPDCP.Old.PPDCPMulti;
import ProOF.apl.sample1.problem.real.multi.FON;
import ProOF.apl.sample1.problem.real.multi.KUR;
import ProOF.apl.sample1.problem.real.multi.Osyczka2;
import ProOF.apl.sample1.problem.real.multi.POL;
import ProOF.apl.sample1.problem.real.multi.Schaffer;
import ProOF.com.language.Factory;
import ProOF.gen.codification.FunctionMulti.RealMulti;

/**
 *
 * @author marcio
 */
public final class fRealMulti extends Factory<RealMulti>{
    public static final fRealMulti obj = new fRealMulti(); 
    @Override
    public String name() {
        return "fRealMulti";
    }
    @Override
    public RealMulti build(int index) {
        switch(index){
            case 0: return new Schaffer();
            case 1: return new FON();
            case 2: return new KUR();
            case 3: return new POL();
            case 4: return new PPDCPMulti();
            case 5: return new Osyczka2();
        }
        return null;
    }
}
