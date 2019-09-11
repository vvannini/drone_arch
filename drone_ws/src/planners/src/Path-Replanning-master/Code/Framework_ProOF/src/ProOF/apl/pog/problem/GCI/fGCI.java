/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCI;

import ProOF.com.language.Factory;

/**
 *
 * @author marcio
 */
public final class fGCI extends Factory<aGCI>{
    public static final fGCI obj = new fGCI();
    
    @Override
    public String name() {
        return "Solver";
    }
    
    @Override
    public aGCI build(int index) {
        switch(index){
            case 0: return new iGCI_CH();
        }
        return null;
    }
}
