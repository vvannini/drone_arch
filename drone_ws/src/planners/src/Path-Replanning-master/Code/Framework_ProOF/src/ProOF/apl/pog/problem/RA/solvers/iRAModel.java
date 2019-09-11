package ProOF.apl.pog.problem.RA.solvers;

import ProOF.apl.pog.problem.RA.Codif1.RACodification1;
import ProOF.apl.pog.problem.RA.Codif1.RAProblem1;
import ProOF.apl.pog.problem.RA.model.MethodResult;
import ProOF.apl.pog.problem.RA.model.RAInstance;
import ProOF.apl.pog.problem.RA.sequencia.aRASequencia;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Stream.StreamPrinter;
import ProOF.CplexExtended.CplexExtended;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

/**
 *
 * @author andre
 */
public class iRAModel extends aRASolver {

    private int Wit[][];
    private double Ct[];
    public IloNumVar Ijt[][];
    public IloNumVar Bjt[][];
    public IloIntVar Qjt[][];
    public IloNumVar Ot[];
    private IloRange Cap_t[];
    public CplexExtended cplex;
    public double BackLog;
    public double Estoque;
    public double OverTime;
    public double valorObj;
    public double lb;
    public IloNumExpr CustoBackLog;
    public IloNumExpr CustoEstoque;
    public IloNumExpr CustoOverTime;
    public IloNumExpr CustoObj;
    private RAInstance inst;
    private aRASequencia seq;

    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
        this.inst = link.need(RAInstance.class, this.inst);
        this.seq = link.need(aRASequencia.class, this.seq);
    }

    @Override
    public void start() throws Exception {
        super.start();
        Ct = new double[inst.T];
        Wit = new int[inst.N][inst.T];
        CreateModel();
    }

    @Override
    public double solve(int Yit[][], double STt[]) {
        try {
            resetVars(Yit, STt);
            if (cplex.solve()) {
                valorObj = cplex.getObjValue();
                lb = cplex.getBestObjValue();
                Estoque = cplex.getValue(CustoEstoque);
                OverTime = cplex.getValue(CustoOverTime);
                BackLog = cplex.getValue(CustoBackLog);

                double vOt[] = cplex.getValues(Ot);
                for (int t = 0; t < inst.T; t++) {
                    valorObj += Math.max(vOt[t] - inst.Ut[t], 0) * 100000.0;
                }
            } else {
                System.out.println("=======================");
                System.out.println("Motivos pq n funfo:");
                System.out.println("Factivel: " + cplex.isPrimalFeasible());
                cplex.writeConflict("conflicts.txt");
                valorObj = 1E20;
                Estoque = 1E20;
                OverTime = 1E20;
                BackLog = 1E20;
            }

            return valorObj;
        } catch (IloException ex) {
            System.out.println("DeuPau");
        }
        return 1E10;
    }

    @Override
    public String name() {
        return "Model";
    }

    @Override
    public String description() {
        return "";
    }

    private void resetVars(int Yit[][], double STt[]) throws IloException {
        //Define Wit
        for (int t = 0; t < inst.T; t++) {
            for (int i = 0; i < inst.N; i++) {
                Wit[i][t] = Yit[i][t];
            }
        }
        //Define Ct
        for (int t = 0; t < inst.T; t++) {
            this.Ct[t] = inst.Ct[t] - STt[t];
        }
        //Define Bound Qjt
        for (int t = 0; t < inst.T; t++) {
            for (int j = 0; j < inst.N; j++) {
                if (Wit[j][t] == 0) {
                    Qjt[j][t].setMin(0);
                    Qjt[j][t].setMax(0);
                } else {
                    Qjt[j][t].setMin(inst.LMi[j]);
                    Qjt[j][t].setMax((int) ((Ct[t] + inst.Ut[t]) / inst.Pi[j]));
                }
            }
        }
        //Define bound Ct
        for (int t = 0; t < inst.T; t++) {
            Cap_t[t].setBounds(0, Ct[t]);
        }
    }

    protected void CreateModel() throws Exception {
        cplex = new CplexExtended();
        cplex.setOut(null); //Para nao jogar dados de saida
        cplex.setWarning(null);

        /*Root algorithm value Optimizer
         0 Automatic (default)
         1 Primal Simplex
         2 Dual Simplex
         3 Network Simplex
         4 Barrier
         5 Sifting
         6 Concurrent*/
        cplex.setParam(IloCplex.IntParam.RootAlg, 2);


        //CPX_PREREDUCE_NO_PRIMALORDUAL (0) it uses neither (which is equivalent to turning presolve off
        //CPX_PREREDUCE_PRIMALONLY (1)      it only uses primal information
        //CPX_PREREDUCE_DUALONLY (2)        presolve only uses dual information
        //CPX_PREREDUCE_PRIMALANDDUAL (3)   (default)indicates that presolve can rely on primal and dual information
        //cplex.setParam(IntParam.Reduce, 0);

        //BooleanParam.MemoryEmphasis
        //BooleanParam.NumericalEmphasis

        //-1    opportunistic 
        //0     only deterministic algorithms are used
        //1     deterministic algorithms in all cases
        //cplex.setParam(IntParam.ParallelMode, -1);

        //cplex.setParam(DoubleParam.TiLim, 2);



        //-------------------- Criando as variaveis -------------------------
        Ijt = cplex.numVarArray(inst.N, inst.T, 0, Double.MAX_VALUE);
        Bjt = cplex.numVarArray(inst.N, inst.T, 0, Double.MAX_VALUE);
        Qjt = cplex.intVarArray(inst.N, inst.T, 0, 100000, "Qit");
        Ot = cplex.numVarArray(inst.T, 0, Double.MAX_VALUE);


        //-------------------- Função objetivo------------------------------
        int n;
        IloNumExpr Hj_Xjt[] = new IloNumExpr[inst.N * inst.T];
        n = 0;
        for (int j = 0; j < inst.N; j++) {
            for (int t = 0; t < inst.T; t++) {
                Hj_Xjt[n++] = cplex.prod(inst.Hi[j], Ijt[j][t]);
            }
        }
        CustoEstoque = cplex.sum(Hj_Xjt);

        IloNumExpr Bj_Bjt[] = new IloNumExpr[inst.N * inst.T];
        n = 0;
        for (int j = 0; j < inst.N; j++) {
            for (int t = 0; t < inst.T; t++) {
                Bj_Bjt[n++] = cplex.prod(inst.Bi[j], Bjt[j][t]);
            }
        }
        CustoBackLog = cplex.sum(Bj_Bjt);

        IloNumExpr COt_Ot[] = new IloNumExpr[inst.T];
        for (int t = 0; t < inst.T; t++) {
            COt_Ot[t] = cplex.prod(inst.COt[t], Ot[t]);
        }
        CustoOverTime = cplex.sum(COt_Ot);

        CustoObj = cplex.sum(CustoEstoque, CustoBackLog, CustoOverTime);

        cplex.addMinimize(CustoObj);

        //-------------------Restrição de estoque------------------------------
        for (int j = 0; j < inst.N; j++) {
            for (int t = 0; t < inst.T; t++) {
                if (t == 0) {
                    cplex.addEq(cplex.sum(Qjt[j][t], Bjt[j][t],
                            cplex.prod(-1, Ijt[j][t])), inst.Dit[j][t]);
                } else {
                    cplex.addEq(cplex.sum(Ijt[j][t - 1], Qjt[j][t], Bjt[j][t],
                            cplex.prod(-1, Ijt[j][t]), cplex.prod(-1, Bjt[j][t - 1])), inst.Dit[j][t]);
                }
            }
        }


        //-------------------Restrição de capacidade------------------------------

        Cap_t = new IloRange[inst.T];
        for (int t = 0; t < inst.T; t++) {
            IloNumExpr AmjXjt[] = new IloNumExpr[inst.N];
            for (int j = 0; j < inst.N; j++) {
                AmjXjt[j] = cplex.prod(inst.Pi[j], Qjt[j][t]);
            }
            Cap_t[t] = cplex.addRange(0, cplex.sum(cplex.sum(AmjXjt), cplex.prod(-1, Ot[t])), inst.Ct[t]);
        }

        /*R4_jt = new IloRange[inst.N][inst.T];
         for(int j=0; j<inst.N; j++){
         for(int t=0; t<inst.T; t++){
         R4_jt[j][t] = cplex.addRange(0, cplex.sum(cplex.prod(inst.Pi[j], Qjt[j][t]), cplex.prod(-1, Ot[t])), inst.Ct[t]); 
         }
         }*/

        for (int t = 0; t < inst.T; t++) {
            for (int j = 0; j < inst.N; j++) {
                cplex.addLe(Qjt[j][t], cplex.prod(cplex.sum(inst.Ct[t], Ot[t]), 1 / inst.Pi[j] + 0.01));
                //Qjt[j][t].setMax((int)(inst.Ct[t]/inst.Pi[j] + 0.01));
            }
        }

        for (int t = 0; t < inst.T; t++) {
            Ot[t].setUB(inst.Ut[t]);
        }
    }

    @Override
    public void printer(StreamPrinter com) throws Exception {
        com.printDbl("Estoque", Estoque);
        com.printDbl("Overtime", OverTime);
        com.printDbl("Backlog", BackLog);
    }
    /**
     * Responsavel por decodificar a ultima solucao criada em MethodResult.
     * Serah necessariamente chamado depois de solve().
     * @param codif
     * @param result Modelo destino do resultado
     * @throws Exception 
     */
    public void export(MethodResult result) throws Exception {
        if (cplex.isPrimalFeasible()) {
            double Ijt[][] = cplex.getValues(this.Ijt);
            double Bjt[][] = cplex.getValues(this.Bjt);
            double Qjt[][] = cplex.getValues(this.Qjt);
            double Ot[] = cplex.getValues(this.Ot);
            for (int i = 0; i < inst.N; i++) {
                for (int t = 0; t < inst.T; t++) {
                    result.Iit[i][t] = (int) Ijt[i][t];
                }
            }
            for (int i = 0; i < inst.N; i++) {
                for (int t = 0; t < inst.T; t++) {
                    result.Bit[i][t] = (int) Bjt[i][t];
                }
            }
            for (int i = 0; i < inst.N; i++) {
                for (int t = 0; t < inst.T; t++) {
                    result.Qit[i][t] = (int) Qjt[i][t];
                }
            }
            for (int t = 0; t < inst.T; t++) {
                result.Ot[t] = (int) Ot[t];
            }
        }
        result.custoSetup = 0;
        //seq.
        for (int t = 0; t < inst.T; t++) {
            result.STt[t] = seq.STt[t];
            result.custoSetup += result.STt[t];
        }
        result.objective = this.valorObj;
        result.overtime = this.OverTime;
        result.hold = this.Estoque;
        result.backlog = this.BackLog;
        result.LB = this.lb;
    }

    public double validate(MethodResult res) {
        try {
            //Define Wit
            for (int t = 0; t < inst.T; t++) {
                for (int i = 0; i < inst.N; i++) {
                    Wit[i][t] = 0;
                }
                for (int p : res.Yt[t]) {
                    Wit[p][t] = 1;
                }
            }
            //Define Ct
            for (int t = 0; t < inst.T; t++) {
                this.Ct[t] = inst.Ct[t] - seq.STt[t];
            }
            //Define Bound Qjt
            for (int t = 0; t < inst.T; t++) {
                for (int j = 0; j < inst.N; j++) {
                    if (Wit[j][t] == 0) {
                        Qjt[j][t].setMin(0);
                        Qjt[j][t].setMax(0);
                    } else {
                        Qjt[j][t].setMin(res.Qit[j][t]);
                        Qjt[j][t].setMax(res.Qit[j][t]);
                    }
                }
            }
            //Define bound Ct
            for (int t = 0; t < inst.T; t++) {                
                Cap_t[t].setBounds(0, Ct[t]);
            }
            
            if (cplex.solve()) {
                valorObj = cplex.getObjValue();
                lb = cplex.getBestObjValue();
                Estoque = cplex.getValue(CustoEstoque);
                OverTime = cplex.getValue(CustoOverTime);
                BackLog = cplex.getValue(CustoBackLog);

                double vOt[] = cplex.getValues(Ot);
                for (int t = 0; t < inst.T; t++) {
                    valorObj += Math.max(vOt[t] - inst.Ut[t], 0) * 100000.0;
                }
            } else {
                System.out.println("=======================");
                System.out.println("Motivos pq n funfo:");
                System.out.println("Factivel: " + cplex.isPrimalFeasible());
                cplex.writeConflict("conflicts.txt");
                valorObj = 1E20;
                Estoque = 1E20;
                OverTime = 1E20;
                BackLog = 1E20;
            }

            return valorObj;
        } catch (IloException ex) {
            System.out.println("DeuPau");
        }
        return 1E10;
    }
}
