/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.jsa.method.apstd;

import ProOF.apl.jsa.problem.PPCCS.core.PPCCSCodification;
import ProOF.apl.jsa.problem.PPCCS.instance.InstanceProblem;
import ProOF.apl.jsa.problem.PPCCS.structure.PPCCSControl;
import ProOF.apl.jsa.problem.PPCCS.structure.PPCCSState;
import ProOF.apl.jsa.problem.PPCCS.util.ConfigSimulation;
import ProOF.apl.jsa.problem.PPCCS.structure.TypeOfCriticalSituation;
import ProOF.apl.jsa.problem.PPCCS.util.Util;
import ProOF.apl.jsa.util.UtilGeom;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.opt.abst.run.MetaHeuristic;

/**
 * Classe que modela um algoritmo de pouso sem tomada de decisão associada. 
 * Independente de problema associado.
 * @author Jesimar
 */
public class APSTD extends MetaHeuristic {

    private final InstanceProblem inst = InstanceProblem.instance;

    private double bestPenalty[];
    private double bestDeltaViolationZNN;
    private double timeTotalExecution;    

    @Override
    public String name() {
        return "jAPSTD_PPCCS";
    }

    @Override
    public void services(LinkerApproaches link) throws Exception {
        link.add(inst);
    }

    @Override
    public void parameters(LinkerParameters link) throws Exception {
        super.parameters(link);
    }

    @Override
    public void execute() throws Exception {
        double timeInit = System.currentTimeMillis();                               
        PPCCSCodification codif = new PPCCSCodification(inst.T);        
        initialize(codif);        
        evaluate(codif);        
        inst.plot.plot(codif);
        double timeEnd = System.currentTimeMillis();
        timeTotalExecution = (timeEnd - timeInit) / 1000.0;
    }

    public void initialize(PPCCSCodification codif) throws Exception {
        PPCCSState state = new PPCCSState(inst.X0);
        boolean isFist = true;
        for (int t = 0; t < codif.Ut.length; t++) {
            codif.Ut[t] = new PPCCSControl(inst.A_MIN, 0.0);
            state = state.nextState(inst.TYPE_OF_FAILURE, codif.Ut[t], inst.DT, 
                    inst.G);
            if (isFist && state.getSpeed() < inst.V_MIN) {
                codif.posFinalX = state.getPositionX();
                codif.posFinalY = state.getPositionY();
                isFist = false;
            }
        }
    }

    public void evaluate(PPCCSCodification codif) throws Exception {
        PPCCSState state = new PPCCSState(inst.X0);
        bestPenalty = new double[inst.L + 4];        
        //Inicializa vetor de penalidades
        for (int i = 0; i < bestPenalty.length; i++) {
            bestPenalty[i] = 0.0;
        }

        //Penaliza o giro (curvas) da aeronave e quando se passa por regiões do tipo N
        double panaltyPassRegionN = Util.probOfFail(inst, state, 0, 0);
        double panaltyForTurning = 0;
        int t = 0;
        while (state.getSpeed() > inst.V_MIN && t < inst.T) {
            panaltyForTurning += Math.abs(codif.Ut[t].getLeme()) / inst.LEME_MAX;
            state = state.nextState(inst.TYPE_OF_FAILURE, codif.Ut[t], inst.DT, 
                    inst.G);
            panaltyPassRegionN += Util.probOfFail(inst, state, 0, t + 1);
            t++;
        }
        bestPenalty[0] = Math.max(0, panaltyPassRegionN - inst.DELTA) * inst.CUST_REGIONS[0];
        bestPenalty[inst.L] = panaltyForTurning;

        //Penaliza a distância do UAV até o centro da região do tipo B
        double x = state.getPositionX();
        double y = state.getPositionY();
        double penaltyDistRegionB = Double.MAX_VALUE;
        for (int l = 0; l < inst.L; l++) {
            if (inst.CUST_REGIONS[l] < 0.0) {
                for (int j = 0; j < inst.SIZE_REGIONS[l]; j++) {
                    double dist = UtilGeom.distPointToCenter(inst.poly[l][j], x, y);
                    penaltyDistRegionB = Math.min(penaltyDistRegionB, dist);
                }
            }
        }
        bestPenalty[inst.L + 1] = Double.MAX_VALUE == penaltyDistRegionB ? 0 : penaltyDistRegionB;

        //Penaliza a aeronave que caiu na região do tipo penalizadora e bonificadora
        for (int l = 1; l < inst.L; l++) {
            bestPenalty[l] += Util.probOfFail(inst, state, l, t) * inst.CUST_REGIONS[l];
        }

        //Penaliza quanto maior for o tempo gasto em voo no problema na bateria
        if (inst.TYPE_OF_FAILURE == TypeOfCriticalSituation.FAIL_BATTERY) {
//            penalty[inst.L + 2] = -t * inst.CUST_REGIONS[inst.L - 1] / inst.T;
            bestPenalty[inst.L + 2] = -inst.CUST_REGIONS[inst.L - 1] * (Math.pow(2.0, (t - inst.T)/10.0));
        }

        //Penaliza por excesso de tempo, pois o UAV não caiu
        if (inst.T == t) {
            double diff = (state.getSpeed() - inst.V_MIN);
            bestPenalty[inst.L + 2] += diff > 0.0 ? -inst.CUST_REGIONS[inst.L - 1] : 0.0;
        }

        //Penaliza por excesso de velocidade, pois o UAV não caiu
        if (inst.T == t) {
            double diff = (state.getSpeed() - inst.V_MIN);
            bestPenalty[inst.L + 3] = diff > 0.0 ? diff * 10000 : 0.0;
        }

        //Calcula o fitness a partir de cada uma das penalizações
        double fitness = 0;

        for (int i = 0; i < bestPenalty.length; i++) {
            fitness += bestPenalty[i];
        }

        //Ajusta o fitness para que o mesmo seja sempre positivo
        fitness -= inst.CUST_REGIONS[inst.L - 1];

        //Calculo para zerar o fitness a cada nova avaliação
        if (fitness < inst.bestFitness) {
            inst.bestFitness = fitness;
            inst.bestK = t;
            inst.bestLandingLocal = Util.landingLocal(inst, codif.posFinalX, codif.posFinalY);            
            bestDeltaViolationZNN = Math.max(0, panaltyPassRegionN - inst.DELTA);            
        }
        inst.actualFitness = fitness;
        inst.actualK = t;
        inst.actualLandingLocal = Util.landingLocal(inst, codif.posFinalX, codif.posFinalY);
        codif.K = t;
        codif.landingLocal = inst.actualLandingLocal;

        //Plota o grafico da rota a cada avaliaçao
        if (ConfigSimulation.IS_SEE_PLOT_FOR_EVALUATE) {
            inst.plot.plot(codif);
        }
    }

    @Override
    public void results(LinkerResults link) throws Exception {
        super.results(link);       
        link.writeDbl("Time Total", timeTotalExecution);
        link.writeDbl("Best Fitness", inst.bestFitness);
        link.writeInt("Time Used (K)", inst.bestK);
        link.writeInt("Time Total (T)", inst.T);
        link.writeString("Landing Local", inst.bestLandingLocal);
        link.writeDbl("Z Not Fly", bestPenalty[0]);
        link.writeDbl("Z Penalize", bestPenalty[1]);
        link.writeDbl("Z Bonus", bestPenalty[2]);
        link.writeDbl("Curves UAV", bestPenalty[inst.L]);
        link.writeDbl("Distance B", bestPenalty[inst.L + 1]);
        link.writeDbl("Time Fly", bestPenalty[inst.L + 2]);
        link.writeDbl("Speed", bestPenalty[inst.L + 3]);
        link.writeDbl("DeltaViolacaoZNN", bestDeltaViolationZNN);
        boolean factivel = inst.bestLandingLocal.equals("b") && 
                bestDeltaViolationZNN < 0.01 && bestPenalty[inst.L + 3] <= 0;
        String strFactivel = factivel ? "true" : "not";
        link.writeString("Factivel", strFactivel);
        
        if (ConfigSimulation.IS_SAVE_IMAGE_SOLUTION_FINAL) {            
            if (bestDeltaViolationZNN > 0.01) {
                inst.file.save("hard_" + inst.FILE_MAP.getName() + "_", false, "");
            } else {
                inst.file.save("easy_" + inst.FILE_MAP.getName() + "_", false, "");
            }
        }
    }
}
