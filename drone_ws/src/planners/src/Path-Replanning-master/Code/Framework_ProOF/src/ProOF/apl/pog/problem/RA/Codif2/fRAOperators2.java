package ProOF.apl.pog.problem.RA.Codif2;

import ProOF.com.language.Factory;
import ProOF.gen.operator.oCrossover;
import ProOF.gen.operator.oInitialization;
import ProOF.gen.operator.oLocalMove;
import ProOF.gen.operator.oMutation;
import ProOF.opt.abst.problem.meta.codification.Operator;

/**
 *
 * @author andre
 */
public class fRAOperators2 extends Factory<Operator> {

    public static final fRAOperators2 obj = new fRAOperators2();

    @Override
    public String name() {
        return "PSSLSAN Operators-2";
    }

    @Override
    public Operator build(int index) {
        switch (index) {
            case 0:
                return new INIT();
            case 1:
                return new MUT_INVERT_BIT();
            case 2:
                return new MOV_INVERT_BIT();
            case 3:
                return new CROSS_OX();
        }
        return null;
    }

    private class INIT extends oInitialization<RAProblem2, RACodification2> {

        @Override
        public String name() {
            return "Random";
        }

        @Override
        public String description() {
            return "inicializa aleatoriamente um ciclo";
        }

        @Override
        public void initialize(RAProblem2 mem, RACodification2 ind) throws Exception {
            for (int t = 0; t < mem.inst.T; t++) {
                for(int i = 0; i < mem.inst.N; i++){
                    ind.Wit[i][t] = mem.rnd.nextInt(2);
                }
            }
        }
    }

    private class MUT_INVERT_BIT extends oMutation<RAProblem2, RACodification2> {
        @Override
        public void mutation(RAProblem2 mem, RACodification2 ind) throws Exception {
            int t = mem.rnd.nextInt(mem.inst.T);
            int i = mem.rnd.nextInt(mem.inst.N);
            ind.Wit[i][t] = 1 - ind.Wit[i][t];
        }

        @Override
        public String name() {
            return "Invert";
        }
    }
    private class MOV_INVERT_BIT extends oLocalMove<RAProblem2, RACodification2> {
        @Override
        public void local_search(RAProblem2 mem, RACodification2 ind) throws Exception {
            int t = mem.rnd.nextInt(mem.inst.T);
            int i = mem.rnd.nextInt(mem.inst.N);
            ind.Wit[i][t] = 1 - ind.Wit[i][t];
        }

        @Override
        public String name() {
            return "Invert";
        }
    }

    private class CROSS_OX extends oCrossover<RAProblem2, RACodification2> {

        @Override
        public String name() {
            return "OX";
        }

        @Override
        public String description() {
            return "Uniforme";
        }

        @Override
        public RACodification2 crossover(RAProblem2 mem, RACodification2 ind1, RACodification2 ind2) throws Exception {
            RACodification2 child = ind1.build(mem);
            
            for(int i=0; i<mem.inst.N; i++){
                for(int t=0; t<mem.inst.T; t++){
                    child.Wit[i][t] = mem.rnd.nextBoolean() ? ind1.Wit[i][t] : ind2.Wit[i][t];
                }
            }
            return child;
        }
    }

    
}
