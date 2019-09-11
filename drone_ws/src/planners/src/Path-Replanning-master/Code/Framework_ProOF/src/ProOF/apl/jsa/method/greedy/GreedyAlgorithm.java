/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.jsa.method.greedy;

import ProOF.apl.jsa.problem.PPCCS.core.PPCCSCodification;
import ProOF.apl.jsa.problem.PPCCS.core.PPCCSProblem;
import ProOF.apl.jsa.problem.PPCCS.instance.InstanceProblem;
import ProOF.apl.jsa.problem.PPCCS.structure.LandingRoute;
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
 * Classe que modela um algoritmo guloso. Independente de problema associado.
 *
 * @author Jesimar
 */
public class GreedyAlgorithm extends MetaHeuristic {

    private final InstanceProblem inst = InstanceProblem.instance;

    private double penalty[];
    private double bestPenalty[];
    private double bestDeltaViolationZNN;
    private double timeTotalExecution;
    private double timeToBest;
    private PPCCSCodification codifBestInd;

    @Override
    public String name() {
        return "jHG_PPCCS";
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
        double timeEndBest = System.currentTimeMillis();
        bestPenalty = new double[inst.L + 4];
        codifBestInd = new PPCCSCodification(inst.T);
        double fitnessBestInd = Double.MAX_VALUE;
        int kBestInd = Integer.MAX_VALUE;
        //Avalia todas as soluções do problema que caem em um zona atratora
        for (int i = 0; i < inst.SIZE_REGIONS[inst.L - 1]; i++) {
            PPCCSCodification codif = new PPCCSCodification(inst.T);
            //Inicializa a solução do problema
            initialize(codif, i);
            //Avalia a solução do problema
            evaluate(codif);
            if (inst.actualFitness < fitnessBestInd) {
                fitnessBestInd = inst.actualFitness;
                kBestInd = inst.actualK;
                codifBestInd = codif;
                timeEndBest = System.currentTimeMillis();
            }
        }
        if (inst.SIZE_REGIONS[inst.L - 1] == 0) {
            PPCCSCodification codif = new PPCCSCodification(inst.T);
            //Inicializa a solução do problema
            initialize(codif);
            //Avalia a solução do problema
            evaluate(codif);
            if (inst.actualFitness < fitnessBestInd) {
                fitnessBestInd = inst.actualFitness;
                kBestInd = inst.actualK;
                codifBestInd = codif;
                timeEndBest = System.currentTimeMillis();
            }
        }

        inst.actualFitness = fitnessBestInd;
        inst.actualK = kBestInd;
        inst.plot.plot(codifBestInd);
        if (ConfigSimulation.IS_SAVE_GIF){
            inst.file.saveBufferGif();
        }
        double timeEnd = System.currentTimeMillis();
        timeTotalExecution = (timeEnd - timeInit) / 1000.0;
        timeToBest = (timeEndBest - timeInit) / 1000.0;
    }

    public void initialize(PPCCSCodification codif, int zonaB) throws Exception {
        int l = inst.L - 1;
        double centerXpoly = UtilGeom.centerXPoly(inst.poly[l][zonaB]);
        double centerYpoly = UtilGeom.centerYPoly(inst.poly[l][zonaB]);
        int i = 100;
        int size = 100;
        while (i >= 0) {
            PPCCSState state = new PPCCSState(inst.X0);
            double media = 0;
            boolean isFirst = true;
            double rndAceleration = inst.A_MAX * (size - i) / (1.0 * size);
            for (int t = 0; t < codif.Ut.length; t++) {
                double angle = Math.atan2(centerYpoly - state.getPositionY(),
                        centerXpoly - state.getPositionX());
                double dAngle = angle - state.getAngle();

                if (dAngle > Math.PI) {
                    dAngle = -(2 * Math.PI - dAngle);
                } else if (dAngle < -Math.PI) {
                    dAngle = (2 * Math.PI + dAngle);
                }
                media = (media * t + Math.abs(dAngle)) / (t + 1);

                double newLeme = Math.min(dAngle, inst.LEME_MAX);
                newLeme = Math.max(newLeme, inst.LEME_MIN);
                codif.Ut[t] = new PPCCSControl(rndAceleration, newLeme);
                state = state.nextState(inst.TYPE_OF_FAILURE, codif.Ut[t], 
                        inst.DT, inst.G);
                if (isFirst && state.getSpeed() < inst.V_MIN) {
                    codif.posFinalX = state.getPositionX();
                    codif.posFinalY = state.getPositionY();
                    isFirst = false;
                }
            }
            String local = Util.landingLocal(inst, codif.posFinalX, codif.posFinalY);
            if (local.equals("b") && UtilGeom.distanceEuclidian(centerXpoly,
                    centerYpoly, codif.posFinalX, codif.posFinalY) < 20) {
                break;
            }
            i--;
        }
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
        penalty = new double[inst.L + 4];
        //Inicializa vetor de penalidades
        for (int i = 0; i < penalty.length; i++) {
            penalty[i] = 0.0;
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
        penalty[0] = Math.max(0, panaltyPassRegionN - inst.DELTA) * inst.CUST_REGIONS[0];
        penalty[inst.L] = panaltyForTurning;

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
        penalty[inst.L + 1] = Double.MAX_VALUE == penaltyDistRegionB ? 0 : penaltyDistRegionB;

        //Penaliza a aeronave que caiu na região do tipo penalizadora e bonificadora
        for (int l = 1; l < inst.L; l++) {
            penalty[l] += Util.probOfFail(inst, state, l, t) * inst.CUST_REGIONS[l];
        }

        //Penaliza quanto maior for o tempo gasto em voo no problema na bateria
        if (inst.TYPE_OF_FAILURE == TypeOfCriticalSituation.FAIL_BATTERY) {
//            penalty[inst.L + 2] = -t * inst.CUST_REGIONS[inst.L - 1] / inst.T;
            penalty[inst.L + 2] = -inst.CUST_REGIONS[inst.L - 1] * (Math.pow(2.0, (t - inst.T)/10.0));
        }

        //Penaliza por excesso de tempo, pois o UAV não caiu
        if (inst.T == t) {
            double diff = (state.getSpeed() - inst.V_MIN);
            penalty[inst.L + 2] += diff > 0.0 ? -inst.CUST_REGIONS[inst.L - 1] : 0.0;
        }

        //Penaliza por excesso de velocidade, pois o UAV não caiu
        if (inst.T == t) {
            double diff = (state.getSpeed() - inst.V_MIN);
            penalty[inst.L + 3] = diff > 0.0 ? diff * 10000 : 0.0;
        }

        //Calcula o fitness a partir de cada uma das penalizações
        double fitness = 0;

        for (int i = 0; i < penalty.length; i++) {
            fitness += penalty[i];
        }

        //Ajusta o fitness para que o mesmo seja sempre positivo
        fitness -= inst.CUST_REGIONS[inst.L - 1];

        //Calculo para zerar o fitness a cada nova avaliação
        if (fitness < inst.bestFitness) {
            inst.bestFitness = fitness;
            inst.bestK = t;
            inst.bestLandingLocal = Util.landingLocal(inst, codif.posFinalX, codif.posFinalY);
            for (int k = 0; k < bestPenalty.length; k++) {
                bestPenalty[k] = penalty[k];
            }
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
            if (ConfigSimulation.IS_SAVE_GIF){
                inst.file.saveBufferGif();
            }
        }
    }
    
    @Override
    public void results(LinkerResults link) throws Exception {
        super.results(link);       
        link.writeDbl("Time Total", timeTotalExecution);
        link.writeDbl("Time to Best", timeToBest);
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
                
        saveRouteInformation(codifBestInd);
    }
    
     private void saveRouteInformation(PPCCSCodification codif){
        LandingRoute landingRoute = new LandingRoute(inst.T+1);
        PPCCSState state = new PPCCSState(inst.X0);
        inst.actualK = inst.bestK;
        double x = state.getPositionX();
        double y = state.getPositionY();
        landingRoute.setPositionX(x, 0);
        landingRoute.setPositionY(y, 0);
        landingRoute.setTime(0, 0);
        int t;
        for (t = 0; t < codif.K; t++){
            state = state.nextState(inst.TYPE_OF_FAILURE, codif.Ut[t], inst.DT, inst.G);
            x = state.getPositionX();
            y = state.getPositionY();
            landingRoute.setPositionX(x, t + 1);
            landingRoute.setPositionY(y, t + 1);
            landingRoute.setTime((t + 1) * inst.DT, t + 1);
        }
        landingRoute.setTimeForLanding(t < inst.T ? t : t - 1);        
        int nameRand = ((int)(Math.random() * 100000));
        if (ConfigSimulation.IS_SAVE_OBJECT_NEW_ROUTE){
            landingRoute.saveRouteObject("./routeObject");
        }
        if (ConfigSimulation.IS_SAVE_ROUTE_FINAL){
            landingRoute.saveRouteText("./route.txt");
        } 
        if (ConfigSimulation.IS_SAVE_IMAGE_SOLUTION_FINAL){     
            inst.file.save("" + nameRand);
        }
        if (ConfigSimulation.IS_SAVE_IMAGE_SOLUTION_FINAL) {
            inst.plot.plot(codif);
            if (bestDeltaViolationZNN > 0.01) {
                inst.file.save("hard_" + inst.FILE_MAP.getName() + "_", false, "");
                if (ConfigSimulation.IS_SAVE_GIF)
                    inst.file.saveGif("hard_" + inst.FILE_MAP.getName());
            } else {
                inst.file.save("easy_" + inst.FILE_MAP.getName() + "_", false, "");
                if (ConfigSimulation.IS_SAVE_GIF)
                    inst.file.saveGif("easy_" + inst.FILE_MAP.getName());
            }
        }
    }

}
