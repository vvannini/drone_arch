package ProOF.apl.pog.gen.operator;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import ProOF.com.language.Factory;

/**
 *
 * @author marcio
 */
public final class fCrossover extends Factory<aCrossover>{
    public static final fCrossover obj = new fCrossover();
    
    @Override
    public String name() {
        return "Crossover";
    }
    
    @Override
    public aCrossover build(int index) {
        switch(index){
            case 0: return new iCrossRandom();
        }
        return null;
    }
}
