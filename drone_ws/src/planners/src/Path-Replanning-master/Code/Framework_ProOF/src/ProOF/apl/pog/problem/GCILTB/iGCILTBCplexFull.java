/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCILTB;

import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.CplexExtended.CplexExtended;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.UnknownObjectException;

/**
 *
 * @author marcio
 */
public final class iGCILTBCplexFull extends aGCILTBModel {

	private CplexExtended cplex;
	private GCILTBInstance inst;
	private IloNumVar Xikt[][][];
	private IloNumVar Yuikt[][][][];
	private IloNumVar Iipt[][][];
	private IloNumVar Wipvt[][][][];
	private IloNumVar I0ip[][];
	private IloNumVar Blyt[][][];
	private IloNumVar Alyt[][][];
	private IloNumVar Tluyt[][][][];
	private IloNumVar Vlyt[][][];
	private IloNumVar Zyt[][];
	private IloNumExpr ObjHold;
	private IloNumExpr ObjSwaping;
	private IloNumExpr ObjTransfer;
	private IloNumExpr ObjOverHold;
	private IloNumExpr ObjValue;
	private IloRange Yultk[][][][];
	private IloRange Blty[][][];
	private IloRange Clty[][][];
	private IloRange Xitk[][][];
	private int vTluyt[][][][];
	private int vAlyt[][][];
	private int vZyt[][];

	@Override
	public String name() {
		return "Cplex Full";
	}

	@Override
	public String description() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	@Override
	public void start() throws Exception {
		inst = problem.inst;
		cplex = new CplexExtended();

		vTluyt = new int[inst.L][inst.L][inst.Y][inst.T];
		vAlyt = new int[inst.L][inst.Y][inst.T];
		vZyt = new int[inst.Y][inst.T];

		Xikt = cplex.intVarArray(inst.N, inst.K, inst.T, 0, 31, "X");
		Yuikt = cplex.boolVarArray(inst.L, inst.N, inst.K, inst.T, "Y");
		Iipt = cplex.numVarArray(inst.N, inst.P, inst.T, 0, Double.MAX_VALUE, "I");
		Wipvt = cplex.numVarArray(inst.N, inst.P, inst.P, inst.T, 0, Double.MAX_VALUE, "W");
		I0ip = cplex.numVarArray(inst.N, inst.P, 0, Double.MAX_VALUE, "Io");

		Blyt = cplex.numVarArray(inst.L, inst.Y, inst.T, 0, 31, "B");

		Alyt = cplex.boolVarArray(inst.L, inst.Y, inst.T, "A");
		Tluyt = cplex.boolVarArray(inst.L, inst.L, inst.Y, inst.T, "T");
		Vlyt = cplex.numVarArray(inst.L, inst.Y, inst.T, 0, Integer.MAX_VALUE, "V");
		Zyt = cplex.boolVarArray(inst.Y, inst.T, "Z");

		//--------------------------Definindo função objetivo ------------------
		IloNumExpr Obj_luyt[][][][] = new IloNumExpr[inst.L][inst.L][inst.Y][inst.T];
		for (int l = 0; l < inst.L; l++) {
			for (int u = 0; u < inst.L; u++) {
				for (int y = 0; y < inst.Y; y++) {
					for (int t = 0; t < inst.T; t++) {
						Obj_luyt[l][u][y][t] = cplex.prod(Tluyt[l][u][y][t], inst.SCluy[l][u][y]);
					}
				}
			}
		}

		IloNumExpr Obj_ipvt[][][][] = new IloNumExpr[inst.N][inst.P][inst.P][inst.T];
		for (int i = 0; i < inst.N; i++) {
			for (int p = 0; p < inst.P; p++) {
				for (int v = 0; v < inst.P; v++) {
					for (int t = 0; t < inst.T; t++) {
						Obj_ipvt[i][p][v][t] = cplex.prod(Wipvt[i][p][v][t], inst.Ripv[i][p][v]);
					}
				}
			}
		}

		IloNumExpr Obj_ipt[][][] = new IloNumExpr[inst.N][inst.P][inst.T];
		for (int i = 0; i < inst.N; i++) {
			for (int p = 0; p < inst.P; p++) {
				for (int t = 0; t < inst.T; t++) {
					Obj_ipt[i][p][t] = cplex.prod(Iipt[i][p][t], inst.Hip[i][p]);
				}
			}
		}

		IloNumExpr Obj_ip[][] = new IloNumExpr[inst.N][inst.P];
		for (int i = 0; i < inst.N; i++) {
			for (int p = 0; p < inst.P; p++) {
				Obj_ip[i][p] = cplex.prod(I0ip[i][p], inst.Pip[i][p]);
			}
		}


		ObjHold = Sum(cplex, Obj_ipt);
		ObjSwaping = Sum(cplex, Obj_luyt);
		ObjTransfer = Sum(cplex, Obj_ipvt);
		ObjOverHold = Sum(cplex, Obj_ip);

		ObjValue = cplex.sum(ObjHold, ObjSwaping, ObjTransfer, ObjOverHold);

		cplex.addMinimize(ObjValue);

		//----------------------------------------------------------------------
		//Wipvt = 0 se p=v
		for (int i = 0; i < inst.N; i++) {
			for (int p = 0; p < inst.P; p++) {
				for (int t = 0; t < inst.T; t++) {
					Wipvt[i][p][p][t].setLB(0);
					Wipvt[i][p][p][t].setUB(0);
				}
			}
		}
		//----------------------------------------------------------------------
		//Tluyt = 0 se l=u
		for (int l = 0; l < inst.L; l++) {
			for (int y = 0; y < inst.Y; y++) {
				for (int t = 0; t < inst.T; t++) {
					Tluyt[l][l][y][t].setLB(0);
					Tluyt[l][l][y][t].setUB(0);
					for (int i : inst.Nl[l]) {
						for (int k : inst.Ky[y]) {
							Yuikt[l][i][k][t].setLB(0);
							Yuikt[l][i][k][t].setUB(0);
						}
					}
				}
			}
		}

		//--------------------------------- sub(2) -----------------------------
		for (int l = 0; l < inst.L; l++) {
			for (int i : inst.Nl[l]) {
				for (int p = 0; p < inst.P; p++) {
					for (int t = 0; t < inst.T; t++) {
						IloNumExpr hold_out = cplex.prod(Iipt[i][p][t], +1);

						IloNumExpr transfer_out = null;
						for (int v = 0; v < inst.P; v++) {
							IloNumExpr temp = cplex.prod(Wipvt[i][p][v][t], +1);
							if (transfer_out == null) {
								transfer_out = temp;
							} else {
								transfer_out = cplex.sum(transfer_out, temp);
							}
						}
						IloNumExpr transfer_in = null;
						for (int v = 0; v < inst.P; v++) {
							IloNumExpr temp = cplex.prod(Wipvt[i][v][p][t], -1);
							if (transfer_in == null) {
								transfer_in = temp;
							} else {
								transfer_in = cplex.sum(transfer_in, temp);
							}
						}
						IloNumExpr prod = null;
						for (int y : inst.Fp[p]) {
							for (int k : inst.Ky[y]) {
								IloNumExpr temp = cplex.prod(Xikt[i][k][t], -inst.Ek[k] * inst.Pik[i][k]);
								if (prod == null) {
									prod = temp;
								} else {
									prod = cplex.sum(prod, temp);
								}
								for (int u = 0; u < inst.L; u++) {
									prod = cplex.sum(prod, cplex.prod(Yuikt[u][i][k][t], -inst.Lluy(u, l, y) * inst.Ek[k] * inst.Pik[i][k]));
								}
							}
						}
						if (prod == null) {
							cplex.output().println("prod==null");
						}

						if (t == 0) {
							IloNumExpr overhold_in = cplex.prod(I0ip[i][p], -1);

							if (transfer_in == null && transfer_out == null) {
								cplex.addEq(
										cplex.sum(hold_out, overhold_in, prod),
										/*inst.Iip0[i][p]*/ -inst.Dipt[i][p][t],
										"Estoque(" + i + "," + p + "," + t + ")");
							} else {
								cplex.addEq(
										cplex.sum(hold_out, transfer_out, overhold_in, transfer_in, prod),
										/*inst.Iip0[i][p]*/ -inst.Dipt[i][p][t],
										"Estoque(" + i + "," + p + "," + t + ")");
							}
						} else {
							IloNumExpr hold_in = cplex.prod(Iipt[i][p][t - 1], -1);

							if (transfer_in == null && transfer_out == null) {
								cplex.addEq(
										cplex.sum(hold_out, hold_in, prod),
										-inst.Dipt[i][p][t],
										"Estoque(" + i + "," + p + "," + t + ")");
							} else {
								cplex.addEq(
										cplex.sum(hold_out, transfer_out, hold_in, transfer_in, prod),
										-inst.Dipt[i][p][t],
										"Estoque(" + i + "," + p + "," + t + ")");
							}

						}
					}
				}
			}
		}

		for (int u = 0; u < inst.L; u++) {
			for (int l = 0; l < inst.L; l++) {
				for (int y = 0; y < inst.Y; y++) {
					for (int k : inst.Ky[y]) {
						for (int t = 0; t < inst.T; t++) {
							IloNumExpr sum = null;
							for (int i : inst.Nl[l]) {
								IloNumExpr temp = cplex.prod(Yuikt[u][i][k][t], 1);
								if (sum == null) {
									sum = temp;
								} else {
									sum = cplex.sum(sum, temp);
								}
							}
							if (inst.Lluy(u, l, y) > 0.001) {
								cplex.addEq(sum, Tluyt[u][l][y][t],
										"Subject03(" + u + "," + l + "," + y + "," + k + "," + t + ")");
							} else {
								cplex.addEq(sum, 0,
										"Subject03(" + u + "," + l + "," + y + "," + k + "," + t + ")");
							}
						}
					}
				}
			}
		}


		for (int l = 0; l < inst.L; l++) {
			for (int y = 0; y < inst.Y; y++) {
				for (int k : inst.Ky[y]) {
					for (int t = 0; t < inst.T; t++) {
						IloNumExpr sum = null;
						for (int i : inst.Nl[l]) {
							IloNumExpr temp = cplex.prod(Xikt[i][k][t], 1);
							if (sum == null) {
								sum = temp;
							} else {
								sum = cplex.sum(sum, temp);
							}
						}
						for (int u = 0; u < inst.L; u++) {
							IloNumExpr temp = cplex.prod(Tluyt[u][l][y][t], inst.STluy[u][l][y] + inst.Lluy(u, l, y));
							sum = cplex.sum(sum, temp);
						}
						cplex.addEq(sum, Blyt[l][y][t],
								"Subject04(" + l + "," + y + "," + t + ")");
					}
				}
			}
		}

		for (int y = 0; y < inst.Y; y++) {
			for (int t = 1; t < inst.T; t++) {
				cplex.addLe(Zyt[y][t], Zyt[y][t - 1],
						"Subject05(" + y + "," + t + ")");
			}
		}


		for (int y = 0; y < inst.Y; y++) {
			for (int t = 0; t < inst.T; t++) {
				IloNumExpr sum = null;
				for (int l = 0; l < inst.L; l++) {
					IloNumExpr temp = cplex.prod(Blyt[l][y][t], +1);
					if (sum == null) {
						sum = temp;
					} else {
						sum = cplex.sum(sum, temp);
					}
				}
				if (t == 0) {
					cplex.addLe(sum, inst.Qt[t],
							"Subject06a(" + y + "," + t + ")");
				} else {
					cplex.addLe(sum, cplex.prod(inst.Qt[t], Zyt[y][t - 1]),
							"Subject06b(" + y + "," + t + ")");
				}

			}
		}
		for (int y = 0; y < inst.Y; y++) {
			for (int t = 0; t < inst.T; t++) {
				IloNumExpr sum = null;
				for (int l = 0; l < inst.L; l++) {
					IloNumExpr temp = cplex.prod(Blyt[l][y][t], +1);
					if (sum == null) {
						sum = temp;
					} else {
						sum = cplex.sum(sum, temp);
					}
				}
				cplex.addGe(sum, cplex.prod(inst.Qt[t], Zyt[y][t]),
						"Subject07(" + y + "," + t + ")");
			}
		}

		for (int l = 0; l < inst.L; l++) {
			for (int y = 0; y < inst.Y; y++) {
				for (int t = 0; t < inst.T; t++) {
					IloNumExpr sum = null;
					for (int k : inst.Ky[y]) {
						for (int i : inst.Nl[l]) {
							IloNumExpr temp = cplex.prod(Xikt[i][k][t], inst.Pik[i][k]);
							if (sum == null) {
								sum = temp;
							} else {
								sum = cplex.sum(sum, temp);
							}
							for (int u = 0; u < inst.L; u++) {
								temp = cplex.prod(Yuikt[u][i][k][t], inst.Lluy(u, l, y) * inst.Pik[i][k]);
								sum = cplex.sum(sum, temp);
							}
						}
					}
					for (int u = 0; u < inst.L; u++) {
						IloNumExpr temp = cplex.prod(Tluyt[u][l][y][t], inst.STluy[u][l][y] * inst.Cy[y]);
						sum = cplex.sum(sum, temp);
					}
					cplex.addLe(sum, cplex.prod(Blyt[l][y][t], inst.Cy[y]),
							"Subject08(" + l + "," + y + "," + t + ")");
				}
			}
		}

		for (int l = 0; l < inst.L; l++) {
			for (int y = 0; y < inst.Y; y++) {
				for (int t = 0; t < inst.T; t++) {
					for (int k : inst.Ky[y]) {
						for (int i : inst.Nl[l]) {
							IloNumExpr max = cplex.prod(Alyt[l][y][t], inst.Mikt(i, k, t));
							for (int u = 0; u < inst.L; u++) {
								//IloNumExpr temp = cplex.prod(Tluyt[l][u][y][t], inst.Mikt[i][k][t]);
								IloNumExpr temp = cplex.prod(Tluyt[u][l][y][t], inst.Mikt(i, k, t));
								max = cplex.sum(max, temp);
							}

							IloNumExpr prod = Xikt[i][k][t];
							for (int u = 0; u < inst.L; u++) {
								//prod = cplex.sum(prod, Yuikt[u][i][k][t]);
								prod = cplex.sum(prod, cplex.prod(inst.Lluy(u, l, y), Yuikt[u][i][k][t]));
							}

							cplex.addLe(prod, max,
									"Subject09(" + y + "," + t + ")");
						}
					}
				}
			}
		}

		for (int y = 0; y < inst.Y; y++) {
			for (int t = 0; t < inst.T; t++) {
				IloNumExpr one = null;
				for (int l = 0; l < inst.L; l++) {
					if (one == null) {
						one = Alyt[l][y][t];
					} else {
						one = cplex.sum(one, Alyt[l][y][t]);
					}
				}

				cplex.addEq(one, 1,
						"Subject10(" + y + "," + t + ")");
			}
		}

		//Subject 07(B)
		for (int l = 0; l < inst.L; l++) {
			for (int y = 0; y < inst.Y; y++) {
				Alyt[l][y][0].setLB(inst.Aly0[l][y]);
				Alyt[l][y][0].setUB(inst.Aly0[l][y]);
			}
		}

		for (int l = 0; l < inst.L; l++) {
			for (int y = 0; y < inst.Y; y++) {
				for (int t = 0; t < inst.T; t++) {
					IloNumExpr in = cplex.prod(Alyt[l][y][t], +1);
					for (int u = 0; u < inst.L; u++) {
						IloNumExpr temp = cplex.prod(Tluyt[u][l][y][t], +1);
						if (in == null) {
							in = temp;
						} else {
							in = cplex.sum(in, temp);
						}
					}

					IloNumExpr out = t + 1 == inst.T ? null : cplex.prod(Alyt[l][y][t + 1], -1);
					for (int u = 0; u < inst.L; u++) {
						IloNumExpr temp = cplex.prod(Tluyt[l][u][y][t], -1);
						if (out == null) {
							out = temp;
						} else {
							out = cplex.sum(out, temp);
						}
					}

					if (t + 1 == inst.T) {
						if (out == null) {
							cplex.addLe(in, 1,
									"Subject11b(" + y + "," + t + ")");
						} else {
							cplex.addLe(cplex.sum(in, out), 1,
									"Subject11c(" + y + "," + t + ")");
						}
					} else {
						cplex.addEq(cplex.sum(in, out), 0,
								"Subject11a(" + y + "," + t + ")");
					}
				}
			}
		}


		for (int l = 0; l < inst.L; l++) {
			for (int u = 0; u < inst.L; u++) {
				if (l != u) {
					for (int y = 0; y < inst.Y; y++) {
						for (int t = 0; t < inst.T; t++) {
							cplex.addGe(cplex.sum(
									cplex.prod(Vlyt[u][y][t], +1),
									cplex.prod(Vlyt[l][y][t], -1),
									cplex.prod(Tluyt[l][u][y][t], -(inst.L)),
									cplex.prod(Alyt[l][y][t], +(inst.L))), 1 - (inst.L),
									"Subject12(" + y + "," + t + ")");
						}
					}
				}
			}
		}
	}
	private boolean print = false;

	@Override
	public void set(boolean print) {
		this.print = print;
	}

	@Override
	public void results(LinkerResults com) throws Exception {
		super.results(com);

		com.writeString("Status", cplex.getStatus().toString());
		com.writeDbl("Obj Value", cplex.getObjValue());
		com.writeDbl("Lower Bound", cplex.getBestObjValue());
		com.writeDbl("Swap Cost", cplex.getValue(ObjSwaping));
		com.writeDbl("Holding Cost", cplex.getValue(ObjHold));
		com.writeDbl("Transfer Cost", cplex.getValue(ObjTransfer));
		com.writeDbl("OverHold Cost", cplex.getValue(ObjOverHold));
	}

	@Override
	public double initialize(GCILTBCodification codif) throws Exception {
		
		// Obter valores das variaveis da codificacao
		// Fixar todas as variaveis para inicializar o CPLEX
		for (int t = 0; t < inst.T; t++) {
			for (int y = 0; y < inst.Y; y++) {
				for (int l = 0; l < inst.L; l++) {
					for (int u = 0; u < inst.L; u++) {
						if (l != u) {
							vTluyt[l][u][y][t] = codif.var[y][t].Tluyt[l][u];
							Tluyt[l][u][y][t].setLB(vTluyt[l][u][y][t]);
							Tluyt[l][u][y][t].setUB(vTluyt[l][u][y][t]);
						}
					}
					if (t > 0) {
						vAlyt[l][y][t] = codif.var[y][t].Alyt[l];
						Alyt[l][y][t].setLB(vAlyt[l][y][t]);
						Alyt[l][y][t].setUB(vAlyt[l][y][t]);
					}
				}
				vZyt[y][t] = codif.var[y][t].Zyt;
				Zyt[y][t].setLB(vZyt[y][t]);
				Zyt[y][t].setUB(vZyt[y][t]);
			}
		}
		
		// Inicializar CPLEX
		cplex.setParam(IloCplex.DoubleParam.TiLim, 1.0);
		cplex.setOut(null);
		cplex.setWarning(null);
		cplex.solve();

		// Liberar variaveis livres
		for (int t = 0; t < problem.inst.T; t++) {
			for (int y = 0; y < problem.inst.Y; y++) {
				
				// Variavel livre
				if (codif.var[y][t].free) {
					for (int l = 0; l < problem.inst.L; l++) {
						for (int u = 0; u < problem.inst.L; u++) {
							if (l != u) {
								Tluyt[l][u][y][t].setLB(0);
								Tluyt[l][u][y][t].setUB(1);
							}
						}
						
						if (t > 0) {
							Alyt[l][y][t].setLB(0);
							Alyt[l][y][t].setUB(1);
						}
					}

					Zyt[y][t].setLB(0);
					Zyt[y][t].setUB(1);
				}
			}
		}

		return 0;
	}

	@Override
	public double execute(GCILTBCodification codif) throws Exception {
		//Escrita do modelo em arquivo
		//cplex.exportModel("LTGCIP.lp");

		//Parada por tempo maximo 
		cplex.setParam(IloCplex.DoubleParam.TiLim, fitTime);

		//Parada por gap relativo
		cplex.setParam(IloCplex.DoubleParam.EpGap, fitGap);

		//Modo de paralelismo, numero de threads
		//cplex.setParam(IloCplex.IntParam.Threads, threads);
		//Parada por gap absoluto de 100
		//cplex.setParam(IloCplex.DoubleParam.EpAGap, 100);

		//Parada por gap relativo
		//cplex.setParam(IloCplex.DoubleParam.EpGap, 0.000001);

		//Desligar saida
		cplex.setOut(null);
		cplex.setWarning(null);
		//codif.print(problem);

		if (cplex.solve()) {
			System.out.printf("Fitness: %.0f\n", cplex.getObjValue() );
			return cplex.getObjValue();
		}
		return 1e11;
	}

	public static double[] getValues(IloCplex cplex, IloNumVar V[]) throws UnknownObjectException, IloException {
		return cplex.getValues(V);
	}

	public static double[][] getValues(IloCplex cplex, IloNumVar V[][]) throws UnknownObjectException, IloException {
		double X[][] = new double[V.length][];
		for (int i = 0; i < V.length; i++) {
			X[i] = getValues(cplex, V[i]);
		}
		return X;
	}

	public static double[][][] getValues(IloCplex cplex, IloNumVar V[][][]) throws UnknownObjectException, IloException {
		double X[][][] = new double[V.length][][];
		for (int i = 0; i < V.length; i++) {
			X[i] = getValues(cplex, V[i]);
		}
		return X;
	}

	public static double[][][][] getValues(IloCplex cplex, IloNumVar V[][][][]) throws UnknownObjectException, IloException {
		double X[][][][] = new double[V.length][][][];
		for (int i = 0; i < V.length; i++) {
			X[i] = getValues(cplex, V[i]);
		}
		return X;
	}

	public static IloNumExpr Sum(IloCplex cplex, IloNumExpr M[]) throws IloException {
		return cplex.sum(M);
	}

	public static IloNumExpr Sum(IloCplex cplex, IloNumExpr M[][]) throws IloException {
		IloNumExpr aux[] = new IloNumExpr[M.length];
		for (int i = 0; i < M.length; i++) {
			aux[i] = Sum(cplex, M[i]);
		}
		return cplex.sum(aux);
	}

	public static IloNumExpr Sum(IloCplex cplex, IloNumExpr M[][][]) throws IloException {
		IloNumExpr aux[] = new IloNumExpr[M.length];
		for (int i = 0; i < M.length; i++) {
			aux[i] = Sum(cplex, M[i]);
		}
		return cplex.sum(aux);
	}

	public static IloNumExpr Sum(IloCplex cplex, IloNumExpr M[][][][]) throws IloException {
		IloNumExpr aux[] = new IloNumExpr[M.length];
		for (int i = 0; i < M.length; i++) {
			aux[i] = Sum(cplex, M[i]);
		}
		return cplex.sum(aux);
	}
}
