package ProOF.apl.pog.problem.RA.solvers;

import ProOF.com.language.Factory;

/**
 *
 * @author andre
 */
public class fRASolver extends Factory<aRASolver>{
    public static final fRASolver obj = new fRASolver();
    @Override
    public String name() {
        return "Solver";
    }

    @Override
    public aRASolver build(int index) {
        switch(index){
            case 0: return new iRAGreedy();
            case 1: return new iRAModel();
        }
        return null;
    }

}
