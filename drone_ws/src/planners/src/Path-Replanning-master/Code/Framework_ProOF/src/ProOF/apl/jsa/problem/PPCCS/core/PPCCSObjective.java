/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.jsa.problem.PPCCS.core;

import ProOF.apl.jsa.problem.PPCCS.instance.InstanceProblem;
import ProOF.apl.jsa.problem.PPCCS.structure.PPCCSState;
import ProOF.apl.jsa.problem.PPCCS.structure.LandingRoute;
import ProOF.apl.jsa.problem.PPCCS.util.ConfigSimulation;
import ProOF.apl.jsa.problem.PPCCS.structure.TypeOfCriticalSituation;
import ProOF.apl.jsa.problem.PPCCS.util.Util;
import ProOF.apl.jsa.util.UtilGeom;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Stream.StreamPrinter;
import ProOF.opt.abst.problem.meta.objective.BoundDbl;
import ProOF.opt.abst.problem.meta.objective.SingleObjective;

/**
 *
 * @author jesimar
 */
public class PPCCSObjective extends SingleObjective<PPCCSProblem, PPCCSCodification, PPCCSObjective> {    
    
    private final static BoundDbl bound = new BoundDbl(-1e99, 1e99, 0.01);
    
    private static int cont = 0;
    private static int contLandB = 0;
    private static int contLandP = 0;
    private static int contLandR = 0;
    private static int contLandN = 0;
    private static int contViolSpeed = 0;
    private static int contViolZNN = 0;    
    private static int contFact = 0;
    
    /**
     * Penalty[0]: Penaliza quando o VANT passa por zonas do tipo não navegável
     * Penalty[1]: Penaliza a aeronave que caiu na região do tipo penalizadora 
     * Penalty[2]: Penaliza a aeronave que caiu na região do tipo bonificadora (dá bonus) 
     * Penalty[3]: Penaliza pelos giros do VANT, qto menor a quantidade de curvas melhor
     * Penalty[4]: Penaliza a distância do VANT até o centro da região do tipo B mais próxima
     * Penalty[5]: Penaliza quanto maior for o tempo gasto em voo no problema na bateria
     * Penalty[6]: Penaliza por excesso de tempo caso o VANT não caia
     * Penalty[7]: Penaliza por excesso de velocidade caso o VANT não caia
     */
    private final double penalty[];    
    private final double bestPenalty[];
    private double bestDeltaViolationZNN;

    public PPCCSObjective(int L) throws Exception {
        super(bound);
        penalty = new double[L + 4];
        bestPenalty = new double[L + 4];
    }
    
    @Override
    public void evaluate(PPCCSProblem prob, PPCCSCodification codif) throws Exception {
        InstanceProblem inst = prob.inst;
        PPCCSState state = new PPCCSState(inst.X0);
        
        //Inicializa vetor de penalidades
        for (int i = 0; i < penalty.length; i++) {
            penalty[i] = 0.0;
        }
        
        //Penaliza o giro (curvas) da aeronave e quando se passa por regiões do tipo N
        double panaltyPassRegionN = Util.probOfFail(inst, state, 0, 0);
        double panaltyForTurning = 0;
        int t = 0;
        while (state.getSpeed() > inst.V_MIN && t < inst.T){
            panaltyForTurning += Math.abs(codif.Ut[t].getLeme()) / inst.LEME_MAX;
            state = state.nextState(inst.TYPE_OF_FAILURE, codif.Ut[t], inst.DT, 
                    inst.G);
            panaltyPassRegionN += Util.probOfFail(inst, state, 0, t + 1);
            t++;
        }
        penalty[0] = Math.max(0, panaltyPassRegionN - inst.DELTA) * inst.CUST_REGIONS[0];        
        penalty[inst.L] = panaltyForTurning;               
        
        contViolZNN += Math.max(0, panaltyPassRegionN - inst.DELTA) < 0.01 ? 0 : 1;
        
        //Penaliza a distância do UAV até o centro da região do tipo B
        double x = state.getPositionX();
        double y = state.getPositionY();     
        codif.posFinalX = x;
        codif.posFinalY = y;
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
            penalty[inst.L + 2] = -inst.CUST_REGIONS[inst.L - 1] * (Math.pow(2.0, (t - inst.T)/10.0));
        }
        
        //Penaliza por excesso de tempo, pois o UAV não caiu
        if (inst.T == t) {
            double diff = (state.getSpeed() - inst.V_MIN);
            penalty[inst.L + 2] += diff > 0.0 ? - inst.CUST_REGIONS[inst.L - 1] : 0.0;
        }
        
        //Penaliza por excesso de velocidade, pois o UAV não caiu
        if (inst.T == t) {         
            double diff = (state.getSpeed() - inst.V_MIN);
            penalty[inst.L + 3] = diff > 0.0 ? diff*10000 : 0.0;
            contViolSpeed += diff > 0.0 ? 1 : 0;
        }
        
        //Calcula o fitness a partir de cada uma das penalizações
        double fitness = 0;
        for (int i = 0; i < penalty.length; i++) {
            fitness += penalty[i];
        }
        
        //Ajusta o fitness para que o mesmo seja sempre positivo
        fitness = fitness - inst.CUST_REGIONS[inst.L - 1];
        
        //Calculo para zerar o fitness a cada nova avaliação
        if (fitness < inst.bestFitness){
            inst.bestFitness = fitness;
            inst.bestK = t;
            inst.bestLandingLocal = Util.landingLocal(inst, codif.posFinalX, codif.posFinalY);
            for (int k = 0; k < bestPenalty.length; k++){
                bestPenalty[k] = penalty[k];
            }
            bestDeltaViolationZNN = Math.max(0, panaltyPassRegionN - inst.DELTA);
        }
        inst.actualFitness = fitness;
        inst.actualK = t;
        inst.actualLandingLocal = Util.landingLocal(inst, codif.posFinalX, codif.posFinalY);
        codif.K = t;
        codif.landingLocal = inst.actualLandingLocal;                
        
        set(fitness);
        
        //Plota o grafico da rota a cada avaliaçao
        if (ConfigSimulation.IS_SEE_PLOT_FOR_EVALUATE){
            inst.plot.plot(codif);
            if (ConfigSimulation.IS_SAVE_GIF)
                inst.file.saveBufferGif();
        }
        
//        boolean factivel = codif.landingLocal.equals("b") && 
//                bestDeltaViolationZNN < 0.01 && bestPenalty[prob.inst.L + 3] <= 0;
//        String strFactivel = factivel ? "true" : "not";
        
        if (codif.landingLocal.equals("r")){
            contLandR++;
        } else if (codif.landingLocal.equals("n")){
            contLandN++;
        } else if (codif.landingLocal.equals("p")){
            contLandP++;
        } else if (codif.landingLocal.equals("b")){
            contLandB++;
        }
        
//        if (cont > 0 && cont < 100){
//            inst.plot.plot(codif);
//        }
//        cont++;
        
//        if (cont > 500 && cont < 600){
//            inst.plot.plot(codif);
//        }
//        cont++;
        
//        if (cont > 1100 && cont < 1200){
//            inst.plot.plot(codif);
//        }
//        cont++;
        
//        if (cont > 2400 && cont < 2500){
//            inst.plot.plot(codif);
//        }
//        cont++;
        
//        if (cont > 4900 && cont < 5000){
//            inst.plot.plot(codif);
//        }
//        cont++;
            
//        if (cont > 9900){
//            inst.plot.plot(codif);
//        }
//        cont++;
        
//        if (cont > 9610){
//            inst.plot.plot(codif);
//        }
//        cont++;
        
//        if (cont > 90000){
//            System.out.println(fitness);
//        }
//        cont++;
    }

    @Override
    public PPCCSObjective build(PPCCSProblem prob) throws Exception {
        return new PPCCSObjective(prob.inst.L);
    }

    @Override
    public void copy(PPCCSProblem prob, PPCCSObjective source) throws Exception {
        super.copy(prob, source);
        System.arraycopy(source.penalty, 0, this.penalty, 0, penalty.length);
        System.arraycopy(source.bestPenalty, 0, this.bestPenalty, 0, bestPenalty.length);
        this.bestDeltaViolationZNN = source.bestDeltaViolationZNN;
    }

    @Override
    public void printer(PPCCSProblem prob, StreamPrinter com, PPCCSCodification codif) throws Exception {
        super.printer(prob, com, codif);
        com.printDbl("Z Not Fly",  bestPenalty[0]);
        com.printDbl("Z Penalize",  bestPenalty[1]);
        com.printDbl("Z Bonus",  bestPenalty[2]);
        com.printDbl("Curves UAV",  bestPenalty[prob.inst.L]);
        com.printDbl("Distance B",  bestPenalty[prob.inst.L + 1]);
        com.printDbl("Time Fly",   bestPenalty[prob.inst.L + 2]);
        com.printDbl("Speed", bestPenalty[prob.inst.L + 3]);
        
        //Plota o grafico da rota a cada progresso no fitness
        if (ConfigSimulation.IS_SEE_PLOT_FOR_PROGRESS){
            prob.inst.plot.plot(codif);
            if (ConfigSimulation.IS_SAVE_GIF)
                prob.inst.file.saveBufferGif();
        }
    }

    @Override
    public void results(PPCCSProblem prob, LinkerResults link, PPCCSCodification codif) throws Exception {
        super.results(prob, link, codif);        
        link.writeInt("Time Used (K)", prob.inst.bestK);
        link.writeInt("Time Total (T)", prob.inst.T);
        link.writeString("Landing Local", prob.inst.bestLandingLocal);  
        link.writeDbl("Z Not Fly",  bestPenalty[0]);
        link.writeDbl("Z Penalize",  bestPenalty[1]);
        link.writeDbl("Z Bonus",  bestPenalty[2]);
        link.writeDbl("Curves UAV",  bestPenalty[prob.inst.L]);
        link.writeDbl("Distance B",  bestPenalty[prob.inst.L + 1]);
        link.writeDbl("Time Fly",   bestPenalty[prob.inst.L + 2]);
        link.writeDbl("Speed", bestPenalty[prob.inst.L + 3]);
        link.writeDbl("DeltaViolacaoZNN", bestDeltaViolationZNN);
        String strFactivel = isFeasible(prob) ? "true" : "not";
        link.writeString("Factivel", strFactivel);
        System.out.println("Total Landing B: " + contLandB);
        System.out.println("Total Landing R: " + contLandR);
        System.out.println("Total Landing P: " + contLandP);
        System.out.println("Total Landing N: " + contLandN);
        System.out.println("Total Violeted ZNN: " + contViolZNN);
        System.out.println("Total Violeted Speed: " + contViolSpeed);
                
        saveRouteInformation(prob, codif);        
    }
    
    public boolean isFeasible(PPCCSProblem prob){
        return prob.inst.bestLandingLocal.equals("b") && 
                bestDeltaViolationZNN < 0.01 && bestPenalty[prob.inst.L + 3] <= 0;
    }
    
    //Tem algum bug em algum lugar: não salva todos os mapas
    private void saveRouteInformation(PPCCSProblem prob, PPCCSCodification codif){
        LandingRoute landingRoute = new LandingRoute(prob.inst.T+1);
        PPCCSState state = new PPCCSState(prob.inst.X0);
        prob.inst.actualK = prob.inst.bestK;
        double x = state.getPositionX();
        double y = state.getPositionY();
        landingRoute.setPositionX(x, 0);
        landingRoute.setPositionY(y, 0);
        landingRoute.setTime(0, 0);
        int t;
        for (t = 0; t < codif.K; t++){
            state = state.nextState(prob.inst.TYPE_OF_FAILURE, codif.Ut[t], 
                    prob.inst.DT, prob.inst.G);
            x = state.getPositionX();
            y = state.getPositionY();
            landingRoute.setPositionX(x, t + 1);
            landingRoute.setPositionY(y, t + 1);
            landingRoute.setTime((t + 1) * prob.inst.DT, t + 1);
        }
        landingRoute.setTimeForLanding(t < prob.inst.T ? t : t - 1);        
        int nameRand = ((int)(Math.random() * 100000));
        if (ConfigSimulation.IS_SAVE_OBJECT_NEW_ROUTE){
            landingRoute.saveRouteObject("./routeObject");
        }
        if (ConfigSimulation.IS_SAVE_ROUTE_FINAL){
            landingRoute.saveRouteText("./route.txt");
        } 
        if (ConfigSimulation.IS_SAVE_IMAGE_SOLUTION_FINAL){     
            prob.inst.file.save("" + nameRand);
        }
        if (ConfigSimulation.IS_SAVE_IMAGE_SOLUTION_FINAL) {
            prob.inst.plot.plot(codif);
            if (bestDeltaViolationZNN > 0.01) {
                prob.inst.file.save("hard_" + prob.inst.FILE_MAP.getName() + "_", false, "");
                if (ConfigSimulation.IS_SAVE_GIF)
                    prob.inst.file.saveGif("hard_" + prob.inst.FILE_MAP.getName());
            } else {
                prob.inst.file.save("easy_" + prob.inst.FILE_MAP.getName() + "_", false, "");
                if (ConfigSimulation.IS_SAVE_GIF)
                    prob.inst.file.saveGif("easy_" + prob.inst.FILE_MAP.getName());
            }
        }        
    }
    
}
