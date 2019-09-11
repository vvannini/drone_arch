/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.factorys;

import ProOF.apl.advanced1.FMS.local_search.LocalSearch;
import ProOF.apl.advanced1.FMS.local_search.Nothing;
import ProOF.apl.advanced1.FMS.local_search.SA;
import ProOF.com.language.Factory;

/**
 *
 * @author marcio
 */
public final class fLocalSearch extends Factory<LocalSearch>{
    public static final fLocalSearch obj = new fLocalSearch();
    
    @Override
    public String name() {
        return "Local Search";
    }
    
    @Override
    public LocalSearch build(int index) {
        switch(index){
            case 0: return new Nothing();
            case 1: return new SA();
        }
        return null;
    }
}
