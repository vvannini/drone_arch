package ProOF.apl.pog.problem.MatrixDinamicsCplex;


import ProOF.apl.pog.problem.MatrixDinamics.MDInstance;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Stream.StreamPrinter;
import ProOF.CplexExtended.CplexExtended;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import java.util.Locale;

/**
 *
 * @author andre
 */
public class iMDModel extends aMDHeuristic {
    private double valorObj;
    //private IloRange Cap_t[];
    /*
    public double BackLog;
    public double Estoque;
    public double OverTime;
    
    public double lb;
    public IloNumExpr CustoBackLog;
    public IloNumExpr CustoEstoque;
    public IloNumExpr CustoOverTime;
    public IloNumExpr CustoObj;*/
    
    private MDInstance inst;

    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
        this.inst = link.need(MDInstance.class, this.inst);
    }
    @Override
    public String name() {
        return "Model";
    }
    @Override
    public void printer(StreamPrinter com) throws Exception {
        //com.printDbl("Estoque", Estoque);
        //com.printDbl("Overtime", OverTime);
        //com.printDbl("Backlog", BackLog);
    }
    
    @Override
    public double solve(double rA[][], double rB[][], double C[][], int R) throws Exception{
        CplexExtended cplex;

        IloNumExpr CustoObj;
        IloNumExpr CustoErro;
        
        cplex = new CplexExtended();
        cplex.setOut(null); //Para nao jogar dados de saida
        cplex.setWarning(null);

        //-------------------- Criando as variaveis -------------------------
        IloNumVar A[][] = cplex.numVarArray(inst.S + R, inst.S, -100, +100);
        IloNumVar B[][] = cplex.numVarArray(inst.S + R, inst.I, -100, +100);
        IloNumVar V[][] = cplex.numVarArray(inst.S, inst.T-1, -Double.MAX_VALUE, +Double.MAX_VALUE);
        IloNumVar E[][] = cplex.numVarArray(inst.S, inst.T-1, 0, +Double.MAX_VALUE);
        IloNumVar Y[][] = cplex.numVarArray(R, inst.T, -Double.MAX_VALUE, +Double.MAX_VALUE);
        //IloNumVar Q[][] = cplex.numVarArray(R, inst.T, 0, +Double.MAX_VALUE);

        
        //-------------------Restrição E------------------------------
        for(int i=0; i<inst.S; i++){
            for(int t=0; t<inst.T-1; t++){
                cplex.addGe(E[i][t], cplex.prod(+1, V[i][t]));
                cplex.addGe(E[i][t], cplex.prod(-1, V[i][t]));
            }
        }
        
        //-------------------Restrição Q------------------------------
        /*for(int i=0; i<R; i++){
            for(int t=0; t<inst.T; t++){
                cplex.addGe(Q[i][t], cplex.prod(+1, Y[i][t]));
                cplex.addGe(Q[i][t], cplex.prod(-1, Y[i][t]));
            }
        }*/
        
        //-------------------- Função objetivo------------------------------
        CustoErro = null;
        for(int i=0; i<E.length; i++){
            for(int t=0; t<E[i].length; t++){
                CustoErro = cplex.Sum(CustoErro, E[i][t]);
            }
        }
        /*for(int i=0; i<Q.length; i++){
            for(int t=0; t<Q[i].length; t++){
                CustoErro = cplex.Sum(CustoErro, Q[i][t]);
            }
        }*/

        CustoObj = CustoErro;//cplex.sum(CustoErro);

        cplex.addMinimize(CustoObj);
        
        
        
        //-------------------Restrição 2------------------------------
        for(int i=0; i<inst.S; i++){
            for(int t=0; t<inst.T-1; t++){
                IloNumExpr sum = null;
                for(int j=0; j<inst.S; j++){
                    sum = cplex.Sum(sum, cplex.prod(A[i][j], inst.states[j][t]));
                }
                for(int j=0; j<R; j++){
                    sum = cplex.Sum(sum, cplex.prod(C[i][j], Y[j][t]));
                }
                for(int j=0; j<inst.I; j++){
                    sum = cplex.Sum(sum, cplex.prod(B[i][j], inst.inputs[j][t]));
                }
                sum = cplex.sum(sum, -1*inst.states[i][t+1]);
                cplex.addEq(V[i][t], sum);
            }
        }
        //-------------------Restrição 3------------------------------
        for(int i=0; i<R; i++){
            for(int t=0; t<inst.T-1; t++){
                IloNumExpr sum = null;
                for(int j=0; j<inst.S; j++){
                    sum = cplex.Sum(sum, cplex.prod(A[i+inst.S][j], inst.states[j][t]));
                }
                for(int j=0; j<R; j++){
                    sum = cplex.Sum(sum, cplex.prod(C[i+inst.S][j], Y[j][t]));
                }
                for(int j=0; j<inst.I; j++){
                    sum = cplex.Sum(sum, cplex.prod(B[i+inst.S][j], inst.inputs[j][t]));
                }
                cplex.addEq(Y[i][t+1], sum);
            }
        }
        //limpa os valores
        for(int i=0; i<inst.S*inst.M; i++){
            for(int j=0; j<inst.S*inst.M; j++){
                rA[i][j] = 0;
            }
            for(int j=0; j<inst.I; j++){
                rB[i][j] = 0;
            }
        }
        try {
            if (cplex.solve()) {
                valorObj = cplex.getObjValue();
                /*
                lb = cplex.getBestObjValue();
                Estoque = cplex.getValue(CustoEstoque);
                OverTime = cplex.getValue(CustoOverTime);
                BackLog = cplex.getValue(CustoBackLog);

                double vOt[] = cplex.getValues(Ot);
                for (int t = 0; t < inst.T; t++) {
                    valorObj += Math.max(vOt[t] - inst.Ut[t], 0) * 100000.0;
                }*/
                
                double vA[][] = cplex.getValues(A);
                double vB[][] = cplex.getValues(B);
                
                for(int i=0; i<inst.S + R; i++){
                    for(int j=0; j<inst.S; j++){
                        rA[i][j] = vA[i][j];
                    }
                    for(int j=0; j<inst.I; j++){
                        rB[i][j] = vB[i][j];
                    }
                }
                for(int i=0; i<inst.S + R; i++){
                    for(int j=0; j<R; j++){
                        rA[i][inst.S+j] = C[i][j];
                    }
                }
                
                /*System.out.printf("************************[R = %d],[A|B]***************************\n", R);
                System.out.printf("solver       = %g\n", valorObj);
                System.out.printf("rA.length    = %d\n", rA.length);
                System.out.printf("rA[0].length = %d\n", rA[0].length);
                System.out.printf("rB.length    = %d\n", rB.length);
                System.out.printf("rB[0].length = %d\n", rB[0].length);
                
                for(int i=0; i<rA.length; i++){
                    System.out.printf("[ ");
                    for(int j=0; j<rA[i].length; j++){
                        System.out.printf("%10g ", Math.abs(rA[i][j])<0.0001 ? 0 : rA[i][j]);
                    }
                    System.out.printf(" | ");
                    for(int j=0; j<rB[i].length; j++){
                        System.out.printf("%10g ", Math.abs(rB[i][j])<0.0001 ? 0 : rB[i][j]);
                    }
                    System.out.printf(" ]\n");
                }
                System.out.printf("---------------------------------------------------------------------------\n", R);
                for(int i=0; i<rA.length; i++){
                    for(int j=0; j<rA[i].length; j++){
                        System.out.printf(Locale.ENGLISH, "%g;", i>=R+inst.S || j>=R+inst.S ? 0 : rA[i][j]);
                    }
                    for(int j=0; j<rB[i].length; j++){
                        System.out.printf(Locale.ENGLISH, "%g;", i>=R+inst.S ? 0 : rB[i][j]);
                    }
                    System.out.printf("\n");
                }*/
                
                
            } else {
                System.out.println("=======================");
                System.out.println("Motivos pq n funfo:");
                System.out.println("Factivel: " + cplex.isPrimalFeasible());
                cplex.writeConflict("conflicts.txt");
                valorObj = 1e20;
            }
            cplex.end();
            return valorObj;
        } catch (IloException ex) {
            System.out.println("DeuPau");
        }
        cplex.end();
        return 1E30;
    }
    
}
