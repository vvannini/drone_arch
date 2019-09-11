package ProOF.apl.pog.problem.RA.sequencia;
import ProOF.com.language.Factory;

/**
 *
 * @author andre
 */
public class fRASequencia extends Factory<aRASequencia>{
    public static final fRASequencia obj = new fRASequencia();
    @Override
    public String name() {
        return "Sequencia";
    }

    @Override
    public aRASequencia build(int index) {
        switch(index){
            case 0: return new iRASeqDependente();
            case 1: return new iRASeqIndependente();
        }
        return null;
    }

}
