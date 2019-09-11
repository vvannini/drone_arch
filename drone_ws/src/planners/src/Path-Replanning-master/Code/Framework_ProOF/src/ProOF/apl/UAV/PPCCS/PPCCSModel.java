package ProOF.apl.UAV.PPCCS;

import ProOF.CplexExtended.CplexExtended;
import ProOF.CplexExtended.iCplexExtract;
import ProOF.apl.UAV.PPCCS.data.TypeOfCriticalSituation;
import ProOF.apl.UAV.PPCCS.util.ConfigSimulation;
import ProOF.apl.UAV.Swing.Graphics2DReal;
import ProOF.apl.UAV.abst.UAVApproach;
import ProOF.apl.UAV.gen.linear.LinearControl;
import ProOF.apl.UAV.gen.linear.LinearModel;
import ProOF.apl.UAV.gen.linear.LinearState;
import ProOF.apl.UAV.map.Obstacle3DHalf;
import ProOF.com.Linker.LinkerResults;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.PrintStream;
import java.util.Random;

/**
 *
 * @author marcio e jesimar
 */
public class PPCCSModel extends LinearModel<PPCCSApproach> {

    public IloIntVar Ht[];
    private IloIntVar Wit[][];
    private IloIntVar ZNit[][];
    private IloIntVar ZPit[][];
    private IloIntVar YBjt[][];

    public IloNumVar PBj[];
    private IloNumVar δBit[][];
    private IloNumVar δPjt[][];
    private IloNumVar δNjt[][];

    private IloNumVar LBj[];
    private IloNumVar St[];

    private final int Tmax = 30;
    private final int Tmin = 10;
    private double vHt[];

    public PPCCSModel(PPCCSApproach approach, String name, CplexExtended cplex)
            throws IloException, Exception {
        super(approach, name, cplex);
        addDefinitionVariables();
        addTimeHorizon();
        addLimitVelocity();
        addLimitControl();
        addDetectTimeLanding();
        addRiskAllocation();
        addColisionObstacles();
        addProbLandingZonesB();
        addColisionZonesP();
        if (approach.inst.TYPE_OF_FAILURE == TypeOfCriticalSituation.FAIL_ENGINE) {
            addProblemMotor();
        } else if (approach.inst.TYPE_OF_FAILURE == TypeOfCriticalSituation.FAIL_TURN_LEFT_ONLY) {
            addProblemSurfacesS1();
        } else if (approach.inst.TYPE_OF_FAILURE == TypeOfCriticalSituation.FAIL_TURN_RIGHT_ONLY) {
            addProblemSurfacesS2();
        }
    }

    @Override
    public void extract(iCplexExtract ext, UAVApproach.Callback type) throws Exception {
        super.extract(ext, type);
        vHt = ext.getValues(Ht);
    }

    @Override
    public boolean addChanges() throws Exception {
        return false;
    }

    public final double[][] getXt() throws IloException {
        double[][] vXt = new double[approach.Waypoints() + 1][];
        for (int t = 0; t < approach.Waypoints() + 1; t++) {
            vXt[t] = cplex.getValues(states[t].x);
        }
        return vXt;
    }

    @Override
    public void paint(Graphics2DReal gr, double size) throws Exception {
        final Font font = new Font(Font.SANS_SERIF, Font.BOLD, 1200);
        for (int t = 0; t < states.length - 1; t++) {
            if (t > 0 && vHt[t + 1] < 0.5 && vHt[t] >= 0.5) {
//                states[t - 1].plot.drawLine(gr, Color.BLACK, states[t].plot);
                states[t].plot.drawLine(gr, Color.BLACK, states[t].plot);
            } else if (t > 0 && vHt[t + 1] >= 0.5) {
//                states[t - 1].plot.drawLine(gr, Color.BLACK, states[t].plot);
                states[t].plot.drawLine(gr, Color.BLACK, states[t].plot);
            }
        }
        for (int t = 0; t < states.length - 1; t++) {
            if (t > 0 && vHt[t + 1] < 0.5 && vHt[t] >= 0.5) {
//                states[t].plot.fillPoint(gr, Color.RED, 0.005 * size);
//                states[t].plot.drawLabel(gr, Color.BLACK, font, 0.005 * size);
                states[t+1].plot.fillPoint(gr, Color.RED, 0.005 * size);
                states[t+1].plot.drawLabel(gr, Color.BLACK, font, 0.005 * size);
            } else if (vHt[t + 1] >= 0.5) {
//                states[t].plot.fillPoint(gr, Color.BLACK, 0.005 * size);
//                states[t].plot.drawLabel(gr, Color.BLACK, font, 0.005 * size);
                states[t+1].plot.fillPoint(gr, Color.BLACK, 0.005 * size);
                states[t+1].plot.drawLabel(gr, Color.BLACK, font, 0.005 * size);
            }
        }
        for (int t = 0; t < states.length - 1; t++) {
            if (t > 0 && vHt[t + 1] < 0.5 && vHt[t] >= 0.5) {
//                states[t].plot.drawRiskAllocation(gr, approach.unc,
//                        approach.Delta(), Color.BLACK);
                states[t+1].plot.drawRiskAllocation(gr, approach.unc,
                        approach.Delta(), Color.BLACK);
            } else if (vHt[t + 1] >= 0.5) {
//                states[t].plot.drawRiskAllocation(gr, approach.unc,
//                        approach.Delta(), Color.RED);
                states[t+1].plot.drawRiskAllocation(gr, approach.unc,
                        approach.Delta(), Color.RED);
            }
        }
    }

    @Override
    public void save() throws Exception {
        
    }        

    @Override
    public void results(LinkerResults link) throws Exception {
        super.results(link);
        double PPb = 0.0;
        for (int j = 0; j < approach.inst.map.sizeB; j++) {
            double v = cplex.getValue(PBj[j]);
            if (PPb < v) {
                PPb = v;
            }
        }
        double Lb = 0.0;
        for (int j = 0; j < approach.inst.map.sizeB; j++) {
            double v = cplex.getValue(LBj[j]);
            if (Lb < v) {
                Lb = v;
            }
        }
        int timeLand = 1;
        for (int t = 0; t < approach.Waypoints() + 1; t++) {
            if (vHt[t] == 1) {
                timeLand++;
            } else {
                break;
            }
        }
        double dP[][] = cplex.getValues(δPjt);
        double dN[][] = cplex.getValues(δNjt);
        double maxP = 0;
        double maxN = 0;
        int minJ = approach.inst.Φp[0];
        for (int j : approach.inst.Φp) {
            for (int t = 0; t < approach.Waypoints() + 1; t++) {
                maxP = dP[j - minJ][t] > maxP ? dP[j - minJ][t] : maxP;
            }
        }
        for (int j : approach.inst.Φn) {
            for (int t = 0; t < approach.Waypoints() + 1; t++) {
                maxN = dN[j][t] > maxN ? dN[j][t] : maxN;
            }
        }

        double dR = 1 - PPb - maxP - maxN;
        String local = (Lb >= 0.5 ? "b" : "r");
        link.writeString("Landing Local", local);
        link.writeDbl("Prob Land B", PPb);
        link.writeDbl("Prob Land R", dR);
        link.writeInt("TimeLand", timeLand);
        link.writeDbl("Violeted Delta", maxN);
//        System.out.println("Landing Local = " + local);
//        System.out.println("ProbLandB = " + PPb);
//        System.out.println("ProbLandR = " + dR);
//        System.out.println("TimeLand = " + timeLand);
//        System.out.println("sumP = " + maxP);
//        System.out.println("sumN = " + maxN);
        
        if (ConfigSimulation.IS_SAVE_ROUTE_FINAL) { 
            int rnd = (int)(new Random().nextInt(1000));
            PrintStream print = new PrintStream(new File("route3D_" + rnd + ".txt"));
            String str = "";
            for (int i = 0; i < 3; i++) {
                str += String.format("%.2f;", cplex.getValue(states[0].x[i]));
            }
            print.println(str);
            for (int t = 0; t < states.length - 1; t++) {
                if (t > 0 && vHt[t + 1] < 0.5 && vHt[t] >= 0.5) {
                    str = "";
                    for (int i = 0; i < 3; i++) {
                        str += String.format("%.2f;", cplex.getValue(states[t+1].x[i]));
                    }
                    print.println(str);
                } else if (vHt[t + 1] >= 0.5) {
                    str = "";
                    for (int i = 0; i < 3; i++) {
                        str += String.format("%.2f;", cplex.getValue(states[t+1].x[i]));
                    }
                    print.println(str);
                } else {
                    break;
                }
            }
            print.close();
        }

        if (ConfigSimulation.DEBUG_MODEL) {     
            
            System.out.println("::::State::::");
            for (LinearState state : states) {
                System.out.println(state.toString());
            }

            System.out.println("::::Control::::");
            for (LinearControl ctrl : controls) {
                System.out.println(ctrl.toString());
            }

            System.out.println("::::Ht::::");
            for (int t = 0; t < approach.Waypoints() + 1; t++) {
                System.out.print(cplex.getValue(Ht[t]) + " ");
            }
            System.out.println();
            System.out.println("::::Wit::::");
            int min = approach.inst.xMinus[0];
            for (int t = 0; t < approach.Waypoints() + 1; t++) {
                System.out.print("t = " + t + ":    ");
                for (int i = 0; i < states[t].x.length; i++) {
                    System.out.printf("%8.2f ", cplex.getValue(states[t].x[i]));
                }
                for (int i : approach.inst.xMinus) {
                    System.out.printf("%d ", (int) (cplex.getValue(Wit[i - min][t]) + 0.5));
                }
                System.out.println();
            }
//            System.out.println();
//            System.out.println("::::Zit::::");
//            for (int j = 0; j < approach.inst.map.sizeN; j++){  
//                System.out.println("j = " + j);
//                for (int t = 0; t < approach.Waypoints() + 1; t++){   
//                    int minA = approach.inst.Oj[0][0];
//                    System.out.print("t = " + t + ": ");
//                    for (int i : approach.inst.Oj[j]){      
//                        System.out.print(cplex.getValue(Zit[i-minA][t]) + " ");
//                    }
//                    System.out.println();
//                }
//            }                        

            System.out.println();
            System.out.println("::::Yjt::::");
            for (int t = 0; t < approach.Waypoints() + 1; t++) {
                for (int j = 0; j < approach.inst.map.sizeB; j++) {
                    System.out.print(cplex.getValue(YBjt[j][t]) + " ");
                }
                System.out.println();
            }
            System.out.println();

            System.out.println("::::PBj::::");
            for (int j = 0; j < approach.inst.map.sizeB; j++) {
                System.out.print(cplex.getValue(PBj[j]) + " ");
            }
            System.out.println();
        }
    }

    private void addDefinitionVariables() throws IloException {
        int wp = approach.Waypoints() + 1;
        Wit = cplex.boolVarArray(approach.inst.xMinus.length, wp, "Wit");
        ZNit = cplex.boolVarArray(approach.inst.qtdEdgeZoneN(), wp, "ZNit");
        ZPit = cplex.boolVarArray(approach.inst.qtdEdgeZoneP(), wp, "ZPit");
        YBjt = cplex.boolVarArray(approach.inst.map.sizeB, wp, "Yjt");
        δBit = cplex.numVarArray(approach.inst.qtdEdgeZoneB(), wp, 0, 1, "δBit");
        δPjt = cplex.numVarArray(approach.inst.map.sizeP, wp, 0, 1, "δPit");
        δNjt = cplex.numVarArray(approach.inst.map.sizeN + 1, wp, 0, 1, "δOjt");
        LBj = cplex.numVarArray(approach.inst.map.sizeB, 0, 1, "LBj");
        St = cplex.numVarArray(wp, 0, 1, "Lt");
    }

    private void addTimeHorizon() throws IloException, Exception {
        //----------------Definindo o Horizonte de Planejamento----------------
        Ht[0].setMin(1);
        Ht[approach.Waypoints()].setMax(0);

        //H_{t} ≤ H_{t-1}
        for (int t = 1; t < approach.Waypoints() + 1; t++) {
            cplex.addLe(Ht[t], Ht[t - 1]);
        }

        //H_t = 1
        for (int t = 0; t < Tmin; t++) {
            Ht[t].setMin(1);
        }

        //H_t = 0
        for (int t = Tmax + 1; t < approach.Waypoints() + 1; t++) {
            Ht[t].setMax(0);
        }
    }

    private void addLimitVelocity() throws IloException {
        //----------------Limites Superior de velocidade----------------
        //H_t ⇒ a^T_i ∗ x_t ≤ b_i
        for (int t = 0; t < approach.Waypoints() + 1; t++) {
            for (int i : approach.inst.xPlus) {
                IloNumExpr exp = approach.inst.hi[i].scalarProd(cplex, states[t].x);
                exp = cplex.sum(exp, -approach.inst.hi[i].b);
                cplex.addIF_Y_Them_Le("Ht", Ht[t], exp);
            }
        }

        //----------------Limites Inferior de velocidade----------------
        //W_{it} ⇒ a^T_i ∗ x_t ≥ b_i
        int min = approach.inst.xMinus[0];
        for (int t = 0; t < approach.Waypoints() + 1; t++) {
            for (int i : approach.inst.xMinus) {
                IloNumExpr exp = approach.inst.hi[i]
                        .scalarProd(cplex, states[t].x);
                exp = cplex.sum(exp, -approach.inst.hi[i].b);
                cplex.addIF_Y_Them_Ge("Wit", Wit[i - min][t], exp);
            }
        }

        //sum(W_{it}) ≥ H_t
        for (int t = 0; t < approach.Waypoints() + 1; t++) {
            IloNumExpr exp = null;
            for (int i : approach.inst.xMinus) {
                exp = cplex.Sum(exp, Wit[i - min][t]);
            }
            if (approach.inst.TYPE_OF_FAILURE == TypeOfCriticalSituation.FAIL_TURN_LEFT_ONLY
                    || approach.inst.TYPE_OF_FAILURE == TypeOfCriticalSituation.FAIL_TURN_RIGHT_ONLY) {
                cplex.addEq(exp, Ht[t]);
            } else {
                cplex.addGe(exp, Ht[t]);
            }
        }
    }

    private void addLimitControl() throws IloException {
        //----------------Limites dos controles----------------
        //H_t ⇒ a^T_i ∗ u_t ≤ b_i
        for (int t = 0; t < approach.Waypoints(); t++) {
            for (int i : approach.inst.uPlus) {
                IloNumExpr exp = approach.inst.hi[i]
                        .scalarProd(cplex, controls[t].u);
                exp = cplex.sum(exp, -approach.inst.hi[i].b);
                cplex.addIF_Y_Them_Le("Ht", Ht[t], exp);
            }
        }
    }

    private void addColisionObstacles() throws IloException, Exception {
        //----------------Colisão com obstáculos----------------
        //sum(Z_{it}) ≥ H_t
        int min = approach.inst.Oj[0][0];
        for (int t = 0; t < approach.Waypoints() + 1; t++) {
            for (int j : approach.inst.Φn) {
                IloNumExpr exp = null;
                for (int i : approach.inst.Oj[j]) {
                    exp = cplex.Sum(exp, ZNit[i - min][t]);
                }
                cplex.addGe(exp, Ht[t]);
            }
        }

        //Z_{it} ⇒ a^T_i ∗ x_t ≥ b_i + c_{i,t}(δ^O_{jt})
        for (int t = 0; t < approach.Waypoints() + 1; t++) {
            for (int j : approach.inst.Φn) {
                for (int i : approach.inst.Oj[j]) {
                    IloNumExpr exp = approach.inst.hi[i].scalarProd(cplex, states[t].x);
                    exp = cplex.sum(exp, -approach.inst.hi[i].b);

                    double a[] = approach.inst.hi[i].a;
                    double sigma = approach.unc.sigma(t, a);
                    double unc = Math.sqrt(2 * Math.pow(sigma, 2.0));
                    IloNumExpr c = cplex.RiskAllocation(δNjt[j][t], unc,
                            approach.Delta(), approach.Naprox(), "RiskAllocO");

                    exp = cplex.sum(exp, cplex.prod(-1, c));
                    cplex.addIF_Y_Them_Ge("Zit", ZNit[i - min][t], exp);
                }
            }
        }
    }

    private IloNumVar Yjt(int j, int t) {
        int minB = approach.inst.Φb[0];
        return YBjt[j - minB][t];
    }

    private IloNumVar LBj(int j) {
        int minB = approach.inst.Φb[0];
        return LBj[j - minB];
    }

    private void addDetectTimeLanding() throws IloException, Exception {
        //~H_t ⇒ x^z_t − c_t(δ^O_t) = sum(h^b_j * Y_{jt})
        for (int t = 0; t < approach.Waypoints() + 1; t++) {
            IloNumVar notHt = cplex.Not("nHt", Ht[t]);
            double distTopoB = approach.unc.risk_allocation(t, 
                    approach.Delta() * 0.1, new double[]{0, 0, +1});
            IloNumExpr exp = cplex.sum(states[t].x[2], -distTopoB);

            if (approach.inst.Φb.length > 0) {
                int minB = approach.inst.Φb[0];
                for (int j : approach.inst.Φb) {
                    if (approach.inst.obstacles[j] instanceof Obstacle3DHalf) {
                        Obstacle3DHalf obs = (Obstacle3DHalf) approach.inst.obstacles[j];
                        exp = cplex.sum(exp, cplex.prod(-obs.getHeight(), YBjt[j - minB][t]));
                    }
                }
            }
            cplex.addIF_Y_Them_Eq("nHt", notHt, exp);
        }

        //Essa restrição aqui não coloquei no modelo matemático (artigo) tem que colocar?
        for (int j = 0; j < approach.inst.map.sizeB; j++) {
            YBjt[j][0].setUB(0);
            St[0].setUB(0);
        }

        //L_t = H_{t-1} - H_t
        for (int t = 1; t < approach.Waypoints() + 1; t++) {
            IloNumExpr expDiff = cplex.Sum(Ht[t - 1], cplex.prod(-1, Ht[t]));
            cplex.addEq(St[t], expDiff);
        }

        //L_t >= sum(Y_{jt})
        for (int t = 1; t < approach.Waypoints() + 1; t++) {
            IloNumExpr exp = null;
            for (int j = 0; j < approach.inst.map.sizeB; j++) {
                exp = cplex.Sum(exp, YBjt[j][t]);
            }
            cplex.addLe(exp, St[t]);
        }
    }

    private void addProbLandingZonesB() throws IloException, Exception {
        //----------Probabilidade de pousar em zona bonificadora----------------
        //Yjt[1][24].setLB(1);  

        if (approach.inst.Φb.length == 0) {
            return;
        }

        int minB = approach.inst.Φb[0];

        //sum(L^b_j) ≤ 1
        IloNumExpr sumB = null;
        for (int j : approach.inst.Φb) {
            sumB = cplex.Sum(sumB, LBj[j - minB]);
        }
        cplex.addLe(sumB, 1.0);

        //sum(Y_{jt}) = LB_j
        for (int j : approach.inst.Φb) {
            IloNumExpr sumY = null;
            for (int t = 0; t < approach.Waypoints() + 1; t++) {
                sumY = cplex.Sum(sumY, Yjt(j, t));
            }
            cplex.addEq(sumY, LBj(j));
        }

        //H_t ≤ 1 - sum(Y_{jt})
        for (int t = 0; t < approach.Waypoints() + 1; t++) {
            IloNumExpr sumY = null;
            for (int j : approach.inst.Φb) {
                sumY = cplex.Sum(sumY, YBjt[j - minB][t]);
            }
            cplex.addLe(Ht[t], cplex.sum(cplex.prod(-1, sumY), 1));
        }

        //L^b_j ⇒ 1 − sum(δ_b{it}) = P^b_j    and    L^b_j ≥ P^b_j
        for (int j : approach.inst.Φb) {
            IloNumExpr exp = null;
            for (int t = 0; t < approach.Waypoints() + 1; t++) {
                for (int i : approach.inst.Oj[j]) {
                    //int w = indexWji(j, i, approach.inst.Φb);
                    int w = i - approach.inst.Oj[minB][0];
                    exp = cplex.Sum(exp, δBit[w][t]);
                }
            }
            exp = cplex.sum(cplex.prod(-1, exp), 1.0);
            exp = cplex.sum(exp, cplex.prod(-1, PBj[j - minB]));
            cplex.addIF_Y_Them_Eq("Imp", LBj[j - minB], exp);
            cplex.addGe(LBj[j - minB], PBj[j - minB]);
        }

        //Y_{jt} ⇒ a^T_i ∗ x_t ≤ b_i − c_{i,t}(δ^b_{it})        
        for (int t = 0; t < approach.Waypoints() + 1; t++) {
            int w = 0;
            for (int j : approach.inst.Φb) {
                for (int i : approach.inst.Oj[j]) {
                    IloNumExpr exp = approach.inst.hi[i].scalarProd(cplex, states[t].x);
                    exp = cplex.sum(exp, -approach.inst.hi[i].b);
                    double a[] = approach.inst.hi[i].a;
                    double sigma = approach.unc.sigma(t, a);
                    double unc = Math.sqrt(2 * Math.pow(sigma, 2.0));
                    IloNumExpr c = cplex.RiskAllocation(δBit[w][t], unc,
                            approach.Delta(), approach.Naprox(), "RiskAllocB");
                    exp = cplex.sum(exp, c);
                    cplex.addIF_Y_Them_Le("Yjt", YBjt[j - minB][t], exp);
                    w++;
                }
            }
        }

    }

    private void addColisionZonesP() throws IloException, Exception {
        //----------------Colisão com Zonas P----------------
        //sum(Z_{it}) ≥ H_t
        int min = approach.inst.minP();
        for (int t = 0; t < approach.Waypoints() + 1; t++) {
            for (int j : approach.inst.Φp) {
                IloNumExpr exp = null;
                for (int i : approach.inst.Oj[j]) {
                    exp = cplex.Sum(exp, ZPit[i - min][t]);
                }
                cplex.addGe(exp, St[t]);
            }
        }

        //Z_{it} ⇒ a^T_i ∗ x_t ≥ b_i + c_{i,t}(δ^O_{jt})
        for (int t = 0; t < approach.Waypoints() + 1; t++) {
            int minJ = approach.inst.Φp[0];
            for (int j : approach.inst.Φp) {
                for (int i : approach.inst.Oj[j]) {
                    IloNumExpr exp = approach.inst.hi[i].scalarProd(cplex, states[t].x);
                    exp = cplex.sum(exp, -approach.inst.hi[i].b);
                    double a[] = approach.inst.hi[i].a;
                    double sigma = approach.unc.sigma(t, a);
                    double unc = Math.sqrt(2 * Math.pow(sigma, 2.0));
                    IloNumExpr c = cplex.RiskAllocation(δPjt[j - minJ][t], unc,
                            approach.inst.deltaP, approach.Naprox(), "RiskAllocO");
                    exp = cplex.sum(exp, cplex.prod(-1, c));
                    cplex.addIF_Y_Them_Ge("ZPit", ZPit[i - min][t], exp);
                }
            }
        }
    }

    private void addRiskAllocation() throws IloException, Exception {
        //----------------Alocação dos Riscos----------------
        //sum(δ^O_{jt}) ≤ Δ
        for (int t = 0; t < approach.Waypoints() + 1; t++) {
            IloNumExpr exp = null;
            for (int j = 0; j < approach.inst.map.sizeN + 1; j++) {
                exp = cplex.Sum(exp, δNjt[j][t]);
            }
            cplex.addLe(exp, approach.Delta());
        }
    }

    private void addProblemMotor() throws IloException {                
        //-----Tentando manter a velocidade de queda na vertical constante de ~ -5 ------
        for (int t = 0; t < approach.Waypoints() + 1; t++) {  
            //H_t ⇒ v_z + 4 ≤ 0
            IloNumExpr expMin = states[t].x[5];
            expMin = cplex.sum(expMin, -approach.inst.limits.K_V_Vmin);
            cplex.addIF_Y_Them_Le("KvVmin", Ht[t], expMin);
            
            //H_t ⇒ v_z + 6 ≥ 0
            IloNumExpr expMax = states[t].x[5];
            expMax = cplex.sum(expMax, -approach.inst.limits.K_V_Vmax);
            cplex.addIF_Y_Them_Ge("KvVmax", Ht[t], expMax);
        }
    }   

    private void addProblemSurfacesS1() throws IloException {
        //----------------Falha na superfícies (s1)----------------        
        //W_{it} ⇒ a^T_i ∗ x_t ≥ 0        
        int Sp = approach.inst.Sp;
        
        //Captura para o primeiro instante de tempo o estado do Wit.
        int index = (int)(Math.atan2(approach.inst.start_state[4], 
                approach.inst.start_state[3])*Sp/(2*Math.PI) + 0.5);
        index = (index + Sp) % Sp;
        Wit[index][0].setMin(1);
        
        double R = approach.inst.limits.V_H_MIN * approach.inst.limits.V_H_MIN/ approach.inst.limits.A_H_MAX;
        double deltaT = 1;
        int n = (int)(Math.atan2(R, approach.inst.limits.V_H_MIN * deltaT)*Sp/(2*Math.PI) + 0.5);
        for (int t = 0; t < approach.Waypoints(); t++) {
            for (int i = 0; i < approach.inst.xMinus.length; i++) {
                IloNumExpr exp = null;

                for (int j = 0; j < n; j++){
                    exp = cplex.SumProd(exp, 1, Wit[(i + j + Sp) % Sp][t+1]);
                }
                IloNumVar and = cplex.And(Wit[i][t], Ht[t+1]);                
                cplex.addLe(and, exp);
            }
        }
        
        for (int t = 0; t < approach.Waypoints(); t++) {
            for (int i = 0; i < approach.inst.xMinus.length; i++) {
                double a[] = new double[]{-Math.sin((2.0 * Math.PI * i) / Sp),
                    Math.cos((2.0 * Math.PI * i) / Sp), 0};
                IloNumExpr exp = null;
                for (int j = 0; j < a.length; j++) {
                    IloNumExpr dv = cplex.sum(states[t + 1].x[a.length + j],
                            cplex.prod(-1, states[t].x[a.length + j]));
                    exp = cplex.SumProd(exp, a[j], dv);
                }
                cplex.addIF_Y_Them_Ge("Wit", Wit[i][t], exp);
            }
        }
    }

    private void addProblemSurfacesS2() throws IloException {
        //----------------Falha na superfícies (s2)----------------        
        //W_{it} ⇒ a^T_i ∗ x_t ≤ 0        
        int Sp = approach.inst.Sp;
        
        //Captura para o primeiro instante de tempo o estado do Wit.
        int index = (int)(Math.atan2(approach.inst.start_state[4], 
                approach.inst.start_state[3])*Sp/(2*Math.PI) + 0.5);
        index = (index + Sp) % Sp;
        Wit[index][0].setMin(1);        
        
        double R = approach.inst.limits.V_H_MIN * approach.inst.limits.V_H_MIN/ approach.inst.limits.A_H_MAX;
        double deltaT = 1;
        int n = (int)(Math.atan2(R, approach.inst.limits.V_H_MIN * deltaT)*Sp/(2*Math.PI) + 0.5);
        for (int t = 0; t < approach.Waypoints(); t++) {
            for (int i = 0; i < approach.inst.xMinus.length; i++) {
                IloNumExpr exp = null;               
                for (int j = 0; j < n; j++){
                    exp = cplex.SumProd(exp, 1, Wit[(i - j + Sp) % Sp][t+1]);
                }
                IloNumVar and = cplex.And(Wit[i][t], Ht[t+1]);                
                cplex.addLe(and, exp);
            }
        }
        
        for (int t = 0; t < approach.Waypoints(); t++) {
            for (int i = 0; i < approach.inst.xMinus.length; i++) {
                double a[] = new double[]{-Math.sin((2.0 * Math.PI * i) / Sp),
                    Math.cos((2.0 * Math.PI * i) / Sp), 0};
                IloNumExpr exp = null;
                for (int j = 0; j < a.length; j++) {
                    IloNumExpr dv = cplex.sum(states[t + 1].x[a.length + j],
                            cplex.prod(-1, states[t].x[a.length + j]));
                    exp = cplex.SumProd(exp, a[j], dv);
                }
                cplex.addIF_Y_Them_Le("Wit", Wit[i ][t], exp);
            }
        }
    }
    
    private int indexWji(int j, int i, Integer[] Φ) {
        int w = 0;
        for (Integer indexj : Φ) {
            for (int indexi : approach.inst.Oj[j]) {
                if (indexj == j && indexi == i) {
                    return w;
                }
                w++;
            }
        }
        return -1;
    }        
}
