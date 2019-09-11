package ProOF.apl.pog.problem.RA.Codif1;

import ProOF.com.language.Factory;
import ProOF.gen.operator.oCrossover;
import ProOF.gen.operator.oInitialization;
import ProOF.gen.operator.oLocalMove;
import ProOF.gen.operator.oMutation;
import ProOF.opt.abst.problem.meta.codification.Operator;
import java.util.ArrayList;

/**
 *
 * @author andre
 */
public class fRAOperators1 extends Factory<Operator>{
    public static final fRAOperators1 obj = new fRAOperators1();

    @Override
    public String name() {
        return "PSSLSAN Operators-1";
    }

    @Override
    public Operator build(int index) {
        switch(index){
            case 0: return new CROSS1();
            case 1: return new CROSS2();
            case 2: return new CROSS3();
            case 3: return new CROSS4();            
            case 4: return new MUT_INSERT();
            case 5: return new MUT_SWAP();
            case 6: return new MUT_REMOVE();
            case 7: return new MUT_CHANGE();
            case 8: return new MUT_MOVE();
            case 9: return new MUT_SHUFFLE();
            case 10: return new MUT_REINIT();
            case 11: return new INIT();
            case 12: return new MUT_INSERTL();
            case 13: return new MUT_SWAPL();
            case 14: return new MUT_REMOVEL();
            case 15: return new MUT_CHANGEL();
            case 16: return new MUT_MOVEL();
            case 17: return new MUT_SHUFFLEL();
            case 18: return new MUT_REINITL();
        }
        return null;
    }
    
    private class INIT extends oInitialization<RAProblem1, RACodification1> {

		@Override
		public String name() {
			return "Random";
		}

		@Override
		public String description() {
			return "inicializa aleatoriamente um ciclo";
		}

		@Override
		public void initialize(RAProblem1 mem, RACodification1 ind) throws Exception {
			for (int t = 0; t < mem.inst.T; t++) {
                                    ind.Yt[t].clear();
				int num = mem.rnd.nextInt(mem.inst.N);
				for (int i = 0; i < num; i++) {
					ind.Yt[t].add(mem.rnd.nextInt(mem.inst.N));
				}
			}
		}
	}

	private class MUT_INSERT extends oMutation<RAProblem1, RACodification1> {

		@Override
		public void mutation(RAProblem1 mem, RACodification1 ind) throws Exception {
			int t = mem.rnd.nextInt(ind.Yt.length);
			int pos1 = ind.Yt[t].isEmpty() ? 0 : mem.rnd.nextInt(ind.Yt[t].size());
			int i = mem.rnd.nextInt(mem.inst.N);
			ind.Yt[t].add(pos1, i);
		}

		@Override
		public String name() {
			return "Insert";
		}
	}

	private class MUT_REMOVE extends oMutation<RAProblem1, RACodification1> {

		@Override
		public void mutation(RAProblem1 mem, RACodification1 ind) throws Exception {
			int i = mem.rnd.nextInt(ind.Yt.length);
			if (ind.Yt[i].isEmpty()) {
				return;
			}

			int pos1 = mem.rnd.nextInt(ind.Yt[i].size());
			ind.Yt[i].remove(pos1);
		}

		@Override
		public String name() {
			return "Remove";
		}
	}

	private class MUT_SWAP extends oMutation<RAProblem1, RACodification1> {

		@Override
		public void mutation(RAProblem1 mem, RACodification1 ind) throws Exception {
			int i = mem.rnd.nextInt(ind.Yt.length);
			if (ind.Yt[i].isEmpty()) {
				return;
			}

			int pos1 = mem.rnd.nextInt(ind.Yt[i].size());
			int pos2 = mem.rnd.nextInt(ind.Yt[i].size());
			int value = ind.Yt[i].get(pos1);
			ind.Yt[i].set(pos1, ind.Yt[i].get(pos2));
			ind.Yt[i].set(pos2, value);
		}

		@Override
		public String name() {
			return "Swap";
		}
	}
        private class MUT_CHANGE extends oMutation<RAProblem1, RACodification1> {

		@Override
		public void mutation(RAProblem1 mem, RACodification1 ind) throws Exception {
			int i = mem.rnd.nextInt(ind.Yt.length);
			if (ind.Yt[i].isEmpty()) {
				return;
			}

			int pos1 = mem.rnd.nextInt(ind.Yt[i].size());
			int value = mem.rnd.nextInt(mem.inst.N);
			ind.Yt[i].set(pos1, value);
		}

		@Override
		public String name() {
			return "Change";
		}
	}
        private class MUT_MOVE extends oMutation<RAProblem1, RACodification1> {

		@Override
		public void mutation(RAProblem1 mem, RACodification1 ind) throws Exception {
			int i = mem.rnd.nextInt(ind.Yt.length);
			if (ind.Yt[i].isEmpty()) {
				return;
			}

			int pos1 = mem.rnd.nextInt(ind.Yt[i].size());
                        int value = ind.Yt[i].get(pos1);
                        ind.Yt[i].remove(pos1);
                        
                        int t2 = mem.rnd.nextInt(ind.Yt.length);
                        int pos2 = mem.rnd.nextInt(ind.Yt[t2].size()+1);
		                     
			ind.Yt[t2].add(pos2, value);
		}

		@Override
		public String name() {
			return "Move";
		}
	}
        private class MUT_SHUFFLE extends oMutation<RAProblem1, RACodification1> {

		@Override
		public void mutation(RAProblem1 mem, RACodification1 ind) throws Exception {
			int t = mem.rnd.nextInt(ind.Yt.length);
			if (ind.Yt[t].isEmpty()) {
				return;
			}

			for(int i = 1; i < ind.Yt[t].size();i++){
                            int pos = mem.rnd.nextInt(i);
                            int aux = ind.Yt[t].get(pos);
                            ind.Yt[t].set(pos, ind.Yt[t].get(i));
                            ind.Yt[t].set(i, aux);
                        }
		}

		@Override
		public String name() {
			return "Shuffle";
		}
	}
        private class MUT_REINIT extends oMutation<RAProblem1, RACodification1> {

		@Override
		public void mutation(RAProblem1 mem, RACodification1 ind) throws Exception {
			int t = mem.rnd.nextInt(ind.Yt.length);
			int size = ind.Yt[t].size();
                        ind.Yt[t].clear();
                        
			for(int i = 0; i < size; i++){
                            int val = mem.rnd.nextInt(mem.inst.N);                            
                            ind.Yt[t].add(val);
                        }
		}

		@Override
		public String name() {
			return "Reinit";
		}
	}
        private class MUT_INSERTL extends oLocalMove<RAProblem1, RACodification1> {

		@Override
		public void local_search(RAProblem1 mem, RACodification1 ind) throws Exception {
			int t = mem.rnd.nextInt(ind.Yt.length);
			int pos1 = ind.Yt[t].isEmpty() ? 0 : mem.rnd.nextInt(ind.Yt[t].size());
			int i = mem.rnd.nextInt(mem.inst.N);
			ind.Yt[t].add(pos1, i);
		}

		@Override
		public String name() {
			return "Insert";
		}
	}

	private class MUT_REMOVEL extends oLocalMove<RAProblem1, RACodification1> {

		@Override
		public void local_search(RAProblem1 mem, RACodification1 ind) throws Exception {
			int i = mem.rnd.nextInt(ind.Yt.length);
			if (ind.Yt[i].isEmpty()) {
				return;
			}

			int pos1 = mem.rnd.nextInt(ind.Yt[i].size());
			ind.Yt[i].remove(pos1);
		}

		@Override
		public String name() {
			return "Remove";
		}
	}

	private class MUT_SWAPL extends oLocalMove<RAProblem1, RACodification1> {

		@Override
		public void local_search(RAProblem1 mem, RACodification1 ind) throws Exception {
			int i = mem.rnd.nextInt(ind.Yt.length);
			if (ind.Yt[i].isEmpty()) {
				return;
			}

			int pos1 = mem.rnd.nextInt(ind.Yt[i].size());
			int pos2 = mem.rnd.nextInt(ind.Yt[i].size());
			int value = ind.Yt[i].get(pos1);
			ind.Yt[i].set(pos1, ind.Yt[i].get(pos2));
			ind.Yt[i].set(pos2, value);
		}

		@Override
		public String name() {
			return "Swap";
		}
	}
        private class MUT_CHANGEL extends oLocalMove<RAProblem1, RACodification1> {

		@Override
		public void local_search(RAProblem1 mem, RACodification1 ind) throws Exception {
			int i = mem.rnd.nextInt(ind.Yt.length);
			if (ind.Yt[i].isEmpty()) {
				return;
			}

			int pos1 = mem.rnd.nextInt(ind.Yt[i].size());
			int value = mem.rnd.nextInt(mem.inst.N);
			ind.Yt[i].set(pos1, value);
		}

		@Override
		public String name() {
			return "Change";
		}
	}
        private class MUT_MOVEL extends oLocalMove<RAProblem1, RACodification1> {

		@Override
		public void local_search(RAProblem1 mem, RACodification1 ind) throws Exception {
			int i = mem.rnd.nextInt(ind.Yt.length);
			if (ind.Yt[i].isEmpty()) {
				return;
			}

			int pos1 = mem.rnd.nextInt(ind.Yt[i].size());
                        int value = ind.Yt[i].get(pos1);
                        ind.Yt[i].remove(pos1);
                        
                        int t2 = mem.rnd.nextInt(ind.Yt.length);
                        int pos2 = mem.rnd.nextInt(ind.Yt[t2].size()+1);
		                     
			ind.Yt[t2].add(pos2, value);
		}

		@Override
		public String name() {
			return "Move";
		}
	}
        private class MUT_SHUFFLEL extends oLocalMove<RAProblem1, RACodification1> {

		@Override
		public void local_search(RAProblem1 mem, RACodification1 ind) throws Exception {
			int t = mem.rnd.nextInt(ind.Yt.length);
			if (ind.Yt[t].isEmpty()) {
				return;
			}

			for(int i = 1; i < ind.Yt[t].size();i++){
                            int pos = mem.rnd.nextInt(i);
                            int aux = ind.Yt[t].get(pos);
                            ind.Yt[t].set(pos, ind.Yt[t].get(i));
                            ind.Yt[t].set(i, aux);
                        }
		}

		@Override
		public String name() {
			return "Shuffle";
		}
	}
        private class MUT_REINITL extends oLocalMove<RAProblem1, RACodification1> {
            
		@Override
		public void local_search(RAProblem1 mem, RACodification1 ind) throws Exception {
			int t = mem.rnd.nextInt(ind.Yt.length);
			int size = ind.Yt[t].size();
                        ind.Yt[t].clear();
                        
			for(int i = 0; i < size; i++){
                            int val = mem.rnd.nextInt(mem.inst.N);                            
                            ind.Yt[t].add(val);
                        }
		}

		@Override
		public String name() {
			return "Reinit";
		}
	}

	private class CROSS1 extends oCrossover<RAProblem1, RACodification1> {

		@Override
		public String name() {
			return "1P";
		}

		@Override
		public String description() {
			return "Corta em 2 conjuntos de períodos";
		}

		@Override
		public RACodification1 crossover(RAProblem1 mem, RACodification1 ind1, RACodification1 ind2) throws Exception {
			RACodification1 child = ind1.build(mem);
			int corte = mem.rnd.nextInt(child.Yt.length);
			for (int i = 0; i < corte; i++) {
				for (int j = 0; j < ind1.Yt[i].size(); j++) {
					child.Yt[i].add(ind1.Yt[i].get(j));
				}
			}
			for (int i = corte; i < child.Yt.length; i++) {
				for (int j = 0; j < ind2.Yt[i].size(); j++) {
					child.Yt[i].add(ind2.Yt[i].get(j));
				}
			}
			return child;
		}
	}

	private class CROSS2 extends oCrossover<RAProblem1, RACodification1> {

		@Override
		public String name() {
			return "1P-T";
		}

		@Override
		public String description() {
			return "Corta cada periodo";
		}

		@Override
		public RACodification1 crossover(RAProblem1 mem, RACodification1 ind1, RACodification1 ind2) throws Exception {
			RACodification1 child = ind1.build(mem);
			for (int i = 0; i < child.Yt.length; i++) {
				int corte = ind1.Yt[i].isEmpty() ? 0 : mem.rnd.nextInt(ind1.Yt[i].size());
				for (int j = 0; j < corte; j++) {
					child.Yt[i].add(ind1.Yt[i].get(j));
				}
				corte = ind2.Yt[i].isEmpty() ? 0 : mem.rnd.nextInt(ind2.Yt[i].size());
				for (int j = corte + 1; j < ind2.Yt[i].size(); j++) {
					child.Yt[i].add(ind2.Yt[i].get(j));
				}

			}
			return child;
		}
	}

	private class CROSS3 extends oCrossover<RAProblem1, RACodification1> {

		@Override
		public String name() {
			return "OX";
		}

		@Override
		public String description() {
			return "Uniforme, com periodos inteiros";
		}

		@Override
		public RACodification1 crossover(RAProblem1 mem, RACodification1 ind1, RACodification1 ind2) throws Exception {
			RACodification1 child = ind1.build(mem);
			for (int i = 0; i < child.Yt.length; i++) {
				if (mem.rnd.nextBoolean()) {
					for (int j = 0; j < ind1.Yt[i].size(); j++) {
						child.Yt[i].add(ind1.Yt[i].get(j));
					}
				} else {
					for (int j = 0; j < ind2.Yt[i].size(); j++) {
						child.Yt[i].add(ind2.Yt[i].get(j));
					}
				}
			}
			return child;
		}
	}

	private class CROSS4 extends oCrossover<RAProblem1, RACodification1> {

		@Override
		public String name() {
			return "OX-T";
		}

		@Override
		public String description() {
			return "Uniforme, por produto por periodo";
		}

		@Override
		public RACodification1 crossover(RAProblem1 mem, RACodification1 ind1, RACodification1 ind2) throws Exception {
			RACodification1 child = ind1.build(mem);
			for (int t = 0; t < child.Yt.length; t++) {
				ArrayList<Integer> min = ind1.Yt[t];
				ArrayList<Integer> max = ind2.Yt[t];

				if (min.size() > max.size()) {
					min = ind2.Yt[t];
					max = ind1.Yt[t];
				}
				for (int j = 0; j < min.size(); j++) {
					if (mem.rnd.nextBoolean()) {
						child.Yt[t].add(min.get(j));
					} else {
						child.Yt[t].add(max.get(j));
					}
				}
				for (int j = 0; j < max.size(); j++) {
					if (mem.rnd.nextBoolean()) {
						child.Yt[t].add(max.get(j));
					}
				}
			}
			return child;
		}
        }

}
