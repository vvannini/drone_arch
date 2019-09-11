/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCILTB;

import ProOF.gen.operator.oCrossover;
import ProOF.gen.operator.oInitialization;
import ProOF.gen.operator.oLocalMove;
import ProOF.gen.operator.oMutation;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.language.Factory;
import ProOF.opt.abst.problem.meta.codification.Operator;
import ProOF.gen.best.BestSol;
import java.io.File;

/**
 *
 * @author Hossomi
 */
public class GCILTBOperators extends Factory<Operator> {

	public static GCILTBOperators obj = new GCILTBOperators();

	@Override
	public String name() {
		return "GCILTB Operators";
	}

	@Override
	public Operator build(int index) {
		switch (index) {
			case 0:
				return new opInitializerMarcio();
			case 1:
				return new opCrossoverOX();
			case 2:
				return new opCrossover1PT();
			case 3:
				return new opCrossover1PY();
			case 4:
				return new opMutationSwapStatus();
			case 5:
				return new opMutationSwapT();
			case 6:
				return new opLocalMoveSwapValue();
			case 7:
				return new opLocalMoveSwapStatus();
			case 8:
				return new opLocalMoveSwapT();
			case 9:
				return new opLocalMoveSwapY();
			//case 10:
			//	return new opInitializerMarcelo();
			//case 11:
			//	return new opMutationSwapY();
		}
		return null;
	}

	private static class opInitializerMarcelo extends oInitialization<GCILTBProblem, GCILTBCodification> {

		private int fitFreeVar;

		@Override
		public String name() {
			return "Init. Marcelo";
		}

		@Override
		public void parameters(LinkerParameters win) throws Exception {
			fitFreeVar = win.Int("Fitness Free Var,", 0, 0, 100);
		}

		@Override
		public void initialize(GCILTBProblem problem, GCILTBCodification ind) throws Exception {
			GCILTBInstance inst = problem.inst;
			double Wl[];
			int start, u, l;
			boolean finish;

			for (int y = 0; y < inst.Y; y++) {
				// Para cada forno escolhemos um periodo para
				// parar a producao (z)
				int z = problem.rnd.nextInt(0, inst.T);

				// l = cor atual
				// u = proxima cor
				start = inst.ovenStartColor(y);
				l = start;

				for (int t = 0; t < inst.T; t++) {
					// Definir dados do periodo (alfa, z)
					ind.var[y][t].setStartColor(l);
					ind.var[y][t].Zyt = (t < z) ? 1 : 0;

					// Se o periodo estiver marcado para produzir,
					// determinar trocas
					if (t < z) {
						// Serao sorteadas cores que o forno possa produzir, sem repetir,
						// ponderadas pela sua demanda.
						// Se a mesma cor for sorteada, serao cessadas
						// as trocas
						Wl = inst.Dl(y);

						do {
							u = problem.rnd.roulette_wheel(Wl);

							// Se sorteou uma cor valida, registrar
							// troca e zerar seu peso
							finish = false;
							if (u != l) {
								Wl[l] = 0;
								ind.var[y][t].setNextColor(l, u);
								//System.out.printf("var[%d][%d] swap: %d -> %d\n", y, t, l, u );
								l = u;

								// Sorteou a mesma cor,
								// terminar trocas
							} else {
								finish = true;
							}
						} while (u != start && !finish);

						// Se fechar um ciclo (u == start) deve-se
						// tambem terminar as trocas

						// A ultima cor adicionada sera a inicial do
						// proximo periodo
						start = l;
					}
				}
			}

			int y, t;
			int max = Math.min(fitFreeVar, inst.Y * inst.T / 2);
			for (int i = 0; i < max; i++) {
				do {
					y = problem.rnd.nextInt(inst.Y);
					t = problem.rnd.nextInt(inst.T);
				} while (ind.var[y][t].free);
				ind.var[y][t].free = true;
			}
		}
	}

	private static class opInitializerMarcio extends oInitialization<GCILTBProblem, GCILTBCodification> {

		private int fitFreeVar;

		@Override
		public String name() {
			return "Init. Marcio";
		}

		@Override
		public void parameters(LinkerParameters win) throws Exception {
			fitFreeVar = win.Int("Fitness Free Var,", 0, 0, 100);
		}

		@Override
		public void initialize(GCILTBProblem problem, GCILTBCodification ind) throws Exception {
			GCILTBInstance inst = problem.inst;
			//System.out.println("Initializing...!");
			int LASTy[] = new int[inst.Y];
			for (int y = 0; y < inst.Y; y++) {
				LASTy[y] = inst.ovenStartColor(y);
			}

			double Dl[] = new double[inst.L];
			double Rl[] = new double[inst.L];
			System.arraycopy(inst.Dl, 0, Dl, 0, inst.L);

			boolean STOPy[] = new boolean[inst.Y];
			for (int t = 0; t < inst.T; t++) {
				for (int y: problem.rnd.shuffle(0, inst.Y)) {
					ind.var[y][t].clearSwaps();
					ind.var[y][t].Zyt = 0;

					int l = LASTy[y];
					boolean flag[] = new boolean[inst.L];

					ind.var[y][t].Alyt[l] = 1;
					flag[l] = true;

					//System.out.printf("(%d;%d) l = %d | ", t, y, l);

					int remDays = inst.Qt[t];
					while (remDays > 0 && !STOPy[y]) {
						l = LASTy[y];
						
						double sum = 0;
						for (int w = 0; w < inst.L; w++) {
							if (inst.Ly[y].contains(w)) {
								Rl[w] = Math.min(Dl[w], inst.STluy[l][w][y] * inst.Cly[w][y]);
							}
							else {
								Rl[w] = Dl[w];
							}
							
							Dl[w] -= Rl[w];
							sum += Dl[w];
						}
						
						if (sum < inst.Cy[y]) {
							STOPy[y] = true;
							remDays = 0;
							//System.out.printf(" stop");
						}
						
						else {
							if (t > 0) {
								ind.var[y][t-1].Zyt = 1;
							}
							
							/*System.out.printf("Dl[%2d,%2d] = [ ", y, t);
							for (int w = 0; w < inst.L; w++) {
								System.out.printf("%4.0f ", Dl[w]);
							}
							System.out.printf("] ");*/
							
							int u = problem.rnd.roulette_wheel(Dl);
							//System.out.printf("Chose %d ", u);
							
							if (inst.fullSetup(l, u, y) < remDays) {
								int time = problem.rnd.nextInt( flag[u] ? 1 : (inst.fullSetup(l, u, y) + 1), remDays);
                                time = Math.min(time, (flag[u] ? 0 : inst.fullSetup(l, u, y)) + (int)(0.9999 + (Dl[u]+Rl[u])/problem.inst.Cly[u][y]));

                                double q = (time - (flag[u] ? 0 : inst.STluy[l][u][y]))*inst.Cly[u][y];
								//System.out.printf("Produced %4.0f\n", q);
								
								if (Dl[u] + Rl[u] > q) {
									Dl[u] -= q;
								}
								else {
									Rl[u] = 0;
									Dl[u] = 0;
								}
								
								if (flag[u]) {
									remDays -= time; //(int) (1 + q / inst.Cly[u][y]);
									//System.out.printf(" *{%d;%d;%1.0f} ", u, time, q);
								}
								else {
									ind.var[y][t].Tluyt[l][u] = 1;
									flag[u] = true;
									LASTy[y] = u;
									remDays -= time; //inst.fullSetup(l, u, y) + (int) (1 + q / inst.Cly[u][y]);

									//System.out.printf(" +{%d;%d;%1.0f} ", u, time, q);
								}
							}
							else {
								double q = remDays * inst.Cly[l][y];
								if (Dl[l] + Rl[l] > q) {
									Dl[l] -= q;
								} else {
									Rl[l] = 0;
									Dl[l] = 0;
								}
								//System.out.printf(" ${%d;%d;%1.0f} ", l, remDays, q);
								remDays = 0;

							}
						}
						for (int w = 0; w < inst.L; w++) {
							Dl[w] += Rl[w];
						}
					}
					/*System.out.printf("[ ");
					for (int w = 0; w < inst.L; w++) {
						System.out.printf("%1.0f ", Dl[w]);
					}
					System.out.printf("]\n");*/
				}
			}
			
			int y, t;
			int max = Math.min(fitFreeVar, inst.Y * inst.T / 2);
			for (int i = 0; i < max; i++) {
				do {
					y = problem.rnd.nextInt(inst.Y);
					t = problem.rnd.nextInt(inst.T);
				} while (ind.var[y][t].free);
				ind.var[y][t].free = true;
			}
		}
	}

	private static class opCrossoverOX extends oCrossover<GCILTBProblem, GCILTBCodification> {

		@Override
		public GCILTBCodification crossover(GCILTBProblem problem, GCILTBCodification ind1, GCILTBCodification ind2) throws Exception {
			GCILTBCodification child = (GCILTBCodification) problem.build_codif();

			for (int y = 0; y < problem.inst.Y; y++) {
				for (int t = 0; t < problem.inst.T; t++) {
					if (problem.rnd.nextBoolean()) {
						child.var[y][t] = new GCILTBVar(ind1.var[y][t]);
					} else {
						child.var[y][t] = new GCILTBVar(ind2.var[y][t]);
					}
				}
			}

			repair(problem, child);
			return child;
		}

		@Override
		public String name() {
			return "Cr. OX";
		}
	}

	private static class opCrossover1PT extends oCrossover<GCILTBProblem, GCILTBCodification> {

		@Override
		public GCILTBCodification crossover(GCILTBProblem problem, GCILTBCodification ind1, GCILTBCodification ind2) throws Exception {
			GCILTBCodification child = (GCILTBCodification) problem.build_codif();
			int tc;

			for (int y = 0; y < problem.inst.Y; y++) {
				tc = problem.rnd.nextInt(1, problem.inst.T - 1);
				for (int t = 0; t < problem.inst.T; t++) {
					if (t < tc) {
						child.var[y][t] = new GCILTBVar(ind1.var[y][t]);
					} else {
						child.var[y][t] = new GCILTBVar(ind2.var[y][t]);
					}
				}
			}

			repair(problem, child);
			return child;
		}

		@Override
		public String name() {
			return "Cr. Macro 1PT";
		}
	}

	private static class opCrossover1PY extends oCrossover<GCILTBProblem, GCILTBCodification> {

		@Override
		public GCILTBCodification crossover(GCILTBProblem problem, GCILTBCodification ind1, GCILTBCodification ind2) throws Exception {
			GCILTBCodification child = (GCILTBCodification) problem.build_codif();
			int yc;

			for (int t = 0; t < problem.inst.T; t++) {
				yc = problem.rnd.nextInt(1, problem.inst.Y - 1);
				for (int y = 0; y < problem.inst.Y; y++) {
					if (y < yc) {
						child.var[y][t] = new GCILTBVar(ind1.var[y][t]);
					} else {
						child.var[y][t] = new GCILTBVar(ind2.var[y][t]);
					}
				}
			}

			repair(problem, child);
			return child;
		}

		@Override
		public String name() {
			return "Cr. Macro 1PY";
		}
	}

	private class opLocalMoveSwapValue extends oLocalMove<GCILTBProblem, GCILTBCodification> {

		@Override
		public void local_search(GCILTBProblem mem, GCILTBCodification ind) throws Exception {
			// Nao e usado (nao esta na lista de operadores la em cima)
			mutationSwapValue(mem, ind);
		}

		@Override
		public String name() {
			return "LM. Swap Value";
		}
	}

	private class opMutationSwapValue extends oMutation<GCILTBProblem, GCILTBCodification> {

		@Override
		public void mutation(GCILTBProblem mem, GCILTBCodification ind) throws Exception {
			// Nao e usado (nao esta na lista de operadores la em cima)
			mutationSwapValue(mem, ind);
		}

		@Override
		public String name() {
			return "Mut. Swap Value";
		}
	}

	private class opLocalMoveSwapStatus extends oLocalMove<GCILTBProblem, GCILTBCodification> {

		@Override
		public void local_search(GCILTBProblem mem, GCILTBCodification ind) throws Exception {
			mutationSwapStatus(mem, ind);
		}

		@Override
		public String name() {
			return "LM. Swap Status";
		}
	}

	private class opMutationSwapStatus extends oMutation<GCILTBProblem, GCILTBCodification> {

		@Override
		public void mutation(GCILTBProblem mem, GCILTBCodification ind) throws Exception {
			mutationSwapStatus(mem, ind);
		}

		@Override
		public String name() {
			return "Mut. Swap Status";
		}
	}

	private class opLocalMoveSwapY extends oLocalMove<GCILTBProblem, GCILTBCodification> {

		@Override
		public void local_search(GCILTBProblem mem, GCILTBCodification ind) throws Exception {
			mutationSwapY(mem, ind);
		}

		@Override
		public String name() {
			return "LM. Swap Y";
		}
	}

	private class opMutationSwapY extends oMutation<GCILTBProblem, GCILTBCodification> {

		@Override
		public void mutation(GCILTBProblem mem, GCILTBCodification ind) throws Exception {
			mutationSwapY(mem, ind);
		}

		@Override
		public String name() {
			return "Mut. Swap Y";
		}
	}

	private class opLocalMoveSwapT extends oLocalMove<GCILTBProblem, GCILTBCodification> {

		@Override
		public void local_search(GCILTBProblem mem, GCILTBCodification ind) throws Exception {
			mutationSwapT(mem, ind);
		}

		@Override
		public String name() {
			return "LM. Swap T";
		}
	}

	private class opMutationSwapT extends oMutation<GCILTBProblem, GCILTBCodification> {

		@Override
		public void mutation(GCILTBProblem mem, GCILTBCodification ind) throws Exception {
			mutationSwapT(mem, ind);
		}

		@Override
		public String name() {
			return "Mut. Swap T";
		}
	}

	public static void mutationSwapValue(GCILTBProblem problem, GCILTBCodification ind) {
		// Nao e usado (nao esta na lista de operadores la em cima)
	}

	public static void mutationSwapStatus(GCILTBProblem problem, GCILTBCodification ind) {
		int y = problem.rnd.nextInt(problem.inst.Y);
		int t = problem.rnd.nextInt(problem.inst.T);
		ind.var[y][t].free = !ind.var[y][t].free;
	}

	public static void mutationSwapT(GCILTBProblem problem, GCILTBCodification ind) throws Exception {
		int y = problem.rnd.nextInt(problem.inst.Y);
		int t0 = problem.rnd.nextInt(1, problem.inst.T-1);

		int t1;
		do {
			t1 = problem.rnd.nextInt(problem.inst.T);
		} while (t0 == t1 || !(t1 == 0 || ind.var[y][t1-1].Zyt == 1));

		GCILTBVar tmp = ind.var[y][t1];
		ind.var[y][t1] = ind.var[y][t0];
		ind.var[y][t0] = tmp;
		
		ind.var[y][t0-1].Zyt = 1;
		if (t1 > 0) {
			ind.var[y][t1-1].Zyt = 1;
		}
		
		repair( problem, ind );
	}

	public static void mutationSwapY(GCILTBProblem problem, GCILTBCodification ind) throws Exception {
		int y0 = problem.rnd.nextInt(problem.inst.Y);
		int t = problem.rnd.nextInt(problem.inst.T);

		int y1;
		do {
			y1 = problem.rnd.nextInt(problem.inst.Y);
		} while (y0 == y1);

		GCILTBVar tmp = ind.var[y1][t];
		ind.var[y1][t] = ind.var[y0][t];
		ind.var[y0][t] = tmp;
		repair( problem, ind );
	}

	public static void repair(GCILTBProblem problem, GCILTBCodification ind) throws Exception {
		GCILTBInstance inst = problem.inst;
		int T = inst.T;
		int Y = inst.Y;

		int curColor, startColor, lastColor, nextColor;
		for (int y = 0; y < Y; y++) {
			// Corrigir z
			// O forno produzira ate o ultimo periodo
			// que estava marcado para produzir
			// (eliminar 0's no meio dos 1's)
			for (int t = T - 2; t >= 0; t--) {
				if (ind.var[y][t + 1].Zyt == 1) {
					ind.var[y][t].Zyt = 1;
				}
			}

			// Verificar coerencia entre
			// trocas de cores e cor inicial do proximo periodo
			for (int t = 0; t < T; t++) {

				// Seesse periodo ainda estiver marcado para produzir...
				if (t == 0 || (ind.var[y][t-1].Zyt == 1 && t < T-1)) {

					// lastColor = cor final deste periodo = cor inicial do proximo periodo
					lastColor = ind.var[y][t + 1].getStartColor();

					// So realizar essa verificacao se a ultima cor deste periodo
					// nao bater com a cor iniciao do proximo periodo
					if (ind.var[y][t].getLastColor() != lastColor) {
						startColor = ind.var[y][t].getStartColor();
						curColor = startColor;
						nextColor = ind.var[y][t].getNextColor(curColor);

						// Enquanto nao chegar ao final das trocas
						while (nextColor != -1) {

							// Encontrou a cor final no meio das trocas
							if (nextColor == lastColor) {
								nextColor = ind.var[y][t].getNextColor(nextColor);

								// Se nextColor != -1, a cor final esta no meio
								// das trocas; entao, pular troca
								if (nextColor != -1) {
									if (nextColor != curColor) {
										//System.out.println("[a " + y + "/" + t + "] Setting " + curColor + " -> " + nextColor);
										ind.var[y][t].setNextColor(curColor, nextColor);
									} // Caso seja um ciclo de 2 cores, eliminar ciclo
									else {
										//nextColor = ind.var[y][t].getNextColor(curColor);
										ind.var[y][t].setNextColor(lastColor, -1);
										//System.out.println("[b " + y + "/" + t + "] Setting " + lastColor + " -> -1");
									}
								} // Senao, a cor final na verdade ja esta
								// na ultima troca, entao nao faz nada
								else {
									curColor = lastColor;
								}
							} // Encontrou cor inicial (fechou ciclo completo)
							// Trocar ultima troca para a cor final (eliminar ciclo)
							else if (nextColor == startColor) {
								nextColor = lastColor;
								ind.var[y][t].setNextColor(curColor, nextColor);
								//System.out.println("[c " + y + "/" + t + "] Setting " + curColor + " -> " + nextColor);
							} // Nada anormal detectado, seguir sequencia
							else {
								curColor = nextColor;
								nextColor = ind.var[y][t].getNextColor(curColor);
							}
						}

						// No final das trocas, a cor nao era a cor final
						// Adicionar uma troca para a cor final
						if (curColor != lastColor) {
							ind.var[y][t].setNextColor(curColor, lastColor);
							//ind.var[y][t].setNextColor(lastColor, -1);
							//System.out.println("[d " + y + "/" + t + "] Setting " + curColor + " -> " + lastColor);
							//System.out.println("[e] Setting " + lastColor + " -> " + lastColor);
						}
					}
				}
				
				// Se o periodo ja estiver com forno desligado
				else {
					if (t > 0) {
						ind.var[y][t].setStartColor(ind.var[y][t-1].getLastColor());
					} else {
						ind.var[y][t].setStartColor(inst.ovenStartColor(y));
					}

					ind.var[y][t].clearSwaps();
				}
			}
		}
		//System.out.println("Repaired:");
	}

	public static void main(String args[]) throws Exception {
		GCILTBProblem problem = new GCILTBProblem();
		//problem.inst.file = new File("T06P1Y01K01L2N006_U9005.dat");
		//problem.inst.file = new File("T12P1Y01K03L6N018_U9002.dat");
		//problem.inst.file = new File("T12P1Y01K05L4N030_U9009.dat");
		//problem.inst.file = new File("T06P4Y09K27L8N165_U9000.dat");
		problem.inst.file = new File("T06P2Y03K09L2N055_U9000.dat");
		problem.inst.load();

		GCILTBCodification ind1 = (GCILTBCodification) problem.build_codif();
		GCILTBCodification ind2 = (GCILTBCodification) problem.build_codif();
		GCILTBCodification ind3 = null;

		opInitializerMarcio init = new opInitializerMarcio();
		opCrossover1PY cross = new opCrossover1PY();

		init.initialize(problem, ind1);
		//ind1.print( problem );
		init.initialize(problem, ind2);
		//ind2.print( problem );
		ind3 = cross.crossover(problem, ind1, ind2);
		ind3.print(problem);

		GCILTBObjective objective = (GCILTBObjective) problem.build_obj();
		problem.model = new iGCILTBCplexFull();
		problem.model.problem = problem;
		problem.model.best = new BestSol();
		problem.model.start();
		objective.evaluate(problem, ind3);
	}
}
