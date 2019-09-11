/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.jsa.problem.PPCCS.core;

import ProOF.apl.jsa.method.de.oDifference;
import ProOF.apl.jsa.method.de.oDisturbance;
import ProOF.apl.jsa.method.de.oRecombine;
import ProOF.apl.jsa.problem.PPCCS.structure.PPCCSControl;
import ProOF.apl.jsa.problem.PPCCS.structure.PPCCSState;
import ProOF.apl.jsa.problem.PPCCS.util.Util;
import ProOF.apl.jsa.util.UtilGeom;
import ProOF.com.language.Factory;
import ProOF.gen.operator.oCrossover;
import ProOF.gen.operator.oInitialization;
import ProOF.gen.operator.oMutation;
import ProOF.opt.abst.problem.meta.codification.Operator;
import java.util.Random;

/**
 * Classe que modela os operados utilizados no algoritmo genético para este
 * problema.
 *
 * @author Jesimar
 */
public class PPCCSOperators extends Factory<Operator> {

    public final static PPCCSOperators obj = new PPCCSOperators();

    @Override
    public String name() {
        return "jfUAVOperators";
    }

    @Override
    public Operator build(int index) throws Exception {
        switch (index) {
            case 0:
                return new InitRandom();
            case 1:
                return new InitLowAcceleration();
            case 2:
                return new InitSmallCurves();
            case 3:
                return new InitGreedy();
            case 4:
                return new CrosOX();
            case 5:
                return new CrosAverage();
            case 6:
                return new CrosAritmetic();
            case 7:
                return new CrosGeometric();
            case 8:
                return new CrosBLXAlpha();
            case 9:
                return new MutUniform();
            case 10:
                return new MutLimite();
            case 11:
                return new MutCreep();
            case 12:
                return new Difference();
            case 13:
                return new Disturbance();
            case 14:
                return new Recombine();
        }
        return null;
    }

    /**
     * Classe que modela o operador de inicialização aleatório associado ao
     * genético pra este problema.
     */
    private class InitRandom extends oInitialization<PPCCSProblem, PPCCSCodification> {

        @Override
        public String name() {
            return "InitRandom";
        }

        @Override
        public void initialize(PPCCSProblem prob, PPCCSCodification ind) throws Exception {
            for (int t = 0; t < ind.Ut.length; t++) {
                double rndAceleration = prob.rnd.nextDouble(prob.inst.A_MIN,
                        prob.inst.A_MAX);
                double rndLeme = prob.rnd.nextDouble(prob.inst.LEME_MIN,
                        prob.inst.LEME_MAX);
                ind.Ut[t] = new PPCCSControl(rndAceleration, rndLeme);
            }
        }
    }

    /**
     * Classe que modela o operador de inicialização que prioriza baixas
     * velocidades para este problema.
     */
    private class InitLowAcceleration extends oInitialization<PPCCSProblem, PPCCSCodification> {

        @Override
        public String name() {
            return "InitLowAcceleration";
        }

        @Override
        public void initialize(PPCCSProblem prob, PPCCSCodification ind) throws Exception {
            double taxaAceleration = 0.25;
            for (int t = 0; t < ind.Ut.length; t++) {
                double rndAceleration = prob.rnd.nextDouble(
                        taxaAceleration * prob.inst.A_MIN,
                        taxaAceleration * prob.inst.A_MAX);
                double rndLeme = prob.rnd.nextDouble(prob.inst.LEME_MIN,
                        prob.inst.LEME_MAX);
                ind.Ut[t] = new PPCCSControl(rndAceleration, rndLeme);
            }
        }
    }

    /**
     * Classe que modela o operador de inicialização que prioriza pequenas
     * curvas para este problema.
     */
    private class InitSmallCurves extends oInitialization<PPCCSProblem, PPCCSCodification> {

        @Override
        public String name() {
            return "InitSmallCurves";
        }

        @Override
        public void initialize(PPCCSProblem prob, PPCCSCodification ind) throws Exception {
            double taxaCurves = 0.25;
            for (int t = 0; t < ind.Ut.length; t++) {
                double rndAceleration = prob.rnd.nextDouble(prob.inst.A_MIN,
                        prob.inst.A_MAX);
                double rndLeme = prob.rnd.nextDouble(taxaCurves * prob.inst.LEME_MIN,
                        taxaCurves * prob.inst.LEME_MAX);
                ind.Ut[t] = new PPCCSControl(rndAceleration, rndLeme);
            }
        }
    }

    /**
     * Classe que modela o operador de inicialização que prioriza pequenas
     * curvas para este problema.
     */
    private class InitGreedy extends oInitialization<PPCCSProblem, PPCCSCodification> {

        @Override
        public String name() {
            return "InitGreedy";
        }

        @Override
        public void initialize(PPCCSProblem prob, PPCCSCodification ind) throws Exception {
            int l = prob.inst.L - 1;
            PPCCSState state = new PPCCSState(prob.inst.X0);
            
            if (prob.inst.SIZE_REGIONS[l] > 0){
                int zonaB = prob.rnd.nextInt(prob.inst.SIZE_REGIONS[l]);            
                double media = 0;
                int tt = prob.rnd.nextInt(ind.Ut.length);
                boolean isFist = true;
                for (int t = 0; t < ind.Ut.length; t++) {
                    double angle = Math.atan2(UtilGeom.centerYPoly(prob.inst.poly[l][zonaB]) - state.getPositionY(),
                            UtilGeom.centerXPoly(prob.inst.poly[l][zonaB]) - state.getPositionX());
                    double dAngle = angle - state.getAngle();

                    if (dAngle > Math.PI) {
                        dAngle = -(2 * Math.PI - dAngle);
                    } else if (dAngle < -Math.PI) {
                        dAngle = (2 * Math.PI + dAngle);
                    }
                    media = (media * t + Math.abs(dAngle)) / (t + 1);

                    double rndAceleration = prob.rnd.nextDouble(prob.inst.A_MIN,
                            prob.inst.A_MAX);
                    if ((t > tt) || (t > ind.Ut.length * prob.rnd.nextDouble() && Math.abs(dAngle) > media)) {
                        rndAceleration = prob.inst.A_MIN;
                    }
                    double newLeme = Math.min(dAngle, prob.inst.LEME_MAX);
                    newLeme = Math.max(newLeme, prob.inst.LEME_MIN);
                    ind.Ut[t] = new PPCCSControl(rndAceleration, newLeme);
                    state = state.nextState(prob.inst.TYPE_OF_FAILURE, ind.Ut[t],
                            prob.inst.DT, prob.inst.G);
                    if (isFist && state.getSpeed() < prob.inst.V_MIN) {
                        ind.posFinalX = state.getPositionX();
                        ind.posFinalY = state.getPositionY();
                        isFist = false;
                    }
                }
            }else {                
                boolean isFist = true;
                for (int t = 0; t < ind.Ut.length; t++) {
                    ind.Ut[t] = new PPCCSControl(prob.inst.A_MIN, 0.0);
                    state = state.nextState(prob.inst.TYPE_OF_FAILURE, ind.Ut[t], 
                            prob.inst.DT, prob.inst.G);
                    if (isFist && state.getSpeed() < prob.inst.V_MIN) {
                        ind.posFinalX = state.getPositionX();
                        ind.posFinalY = state.getPositionY();
                        isFist = false;
                    }
                }
            }
        }
    }

    /**
     * Classe que modela o operador de crossover associado ao genético pra este
     * problema.
     */
    private class CrosOX extends oCrossover<PPCCSProblem, PPCCSCodification> {

        @Override
        public String name() {
            return "OX";
        }

        @Override
        public PPCCSCodification crossover(PPCCSProblem prob, PPCCSCodification ind1,
                PPCCSCodification ind2) throws Exception {
            PPCCSCodification codFilho = ind1.build(prob);

            for (int i = 0; i < ind1.Ut.length; i++) {
                if (prob.rnd.nextBoolean()) {
                    codFilho.Ut[i] = ind1.Ut[i];
                } else {
                    codFilho.Ut[i] = ind2.Ut[i];
                }
            }

            return codFilho;
        }
    }

    /**
     * Classe que modela o operador de crossover de média associado ao genético
     * pra este problema.
     */
    private class CrosAverage extends oCrossover<PPCCSProblem, PPCCSCodification> {

        @Override
        public String name() {
            return "Average";
        }

        @Override
        public PPCCSCodification crossover(PPCCSProblem prob, PPCCSCodification ind1,
                PPCCSCodification ind2) throws Exception {
            PPCCSCodification codFilho = ind1.build(prob);
            for (int i = 0; i < ind1.Ut.length; i++) {
                double acelAvg = (ind1.Ut[i].getAceleration()
                        + ind2.Ut[i].getAceleration()) / 2.0;
                double lemeAvg = (ind1.Ut[i].getLeme() + ind2.Ut[i].getLeme()) / 2.0;
                PPCCSControl control = new PPCCSControl(acelAvg, lemeAvg);
                codFilho.Ut[i] = control;
            }
            return codFilho;
        }
    }

    /**
     * Classe que modela o operador de crossover aritmético associado ao
     * genético pra este problema.
     */
    private class CrosAritmetic extends oCrossover<PPCCSProblem, PPCCSCodification> {

        @Override
        public String name() {
            return "Aritmetic";
        }

        @Override
        public PPCCSCodification crossover(PPCCSProblem prob, PPCCSCodification ind1,
                PPCCSCodification ind2) throws Exception {
            PPCCSCodification codFilho = ind1.build(prob);
            for (int i = 0; i < ind1.Ut.length; i++) {
                double beta = prob.rnd.nextDouble();
                double newAcel = beta * ind1.Ut[i].getAceleration()
                        + (1 - beta) * ind2.Ut[i].getAceleration();
                double newLeme = beta * ind1.Ut[i].getLeme()
                        + (1 - beta) * ind2.Ut[i].getLeme();
                PPCCSControl control = new PPCCSControl(newAcel, newLeme);
                codFilho.Ut[i] = control;
            }
            return codFilho;
        }
    }

    /**
     * Classe que modela o operador de crossover de média geométrica associado
     * ao genético pra este problema.
     */
    private class CrosGeometric extends oCrossover<PPCCSProblem, PPCCSCodification> {

        @Override
        public String name() {
            return "Geometric";
        }

        @Override
        public PPCCSCodification crossover(PPCCSProblem prob, PPCCSCodification ind1,
                PPCCSCodification ind2) throws Exception {
            PPCCSCodification codFilho = ind1.build(prob);
            for (int i = 0; i < ind1.Ut.length; i++) {
                double prodAcel = ind1.Ut[i].getAceleration() * ind2.Ut[i].getAceleration();
                double acelGeom = prodAcel < 0 ? -Math.sqrt(-prodAcel) : Math.sqrt(prodAcel);
                double prodLeme = ind1.Ut[i].getLeme() * ind2.Ut[i].getLeme();
                double lemeGeom = prodLeme < 0 ? -Math.sqrt(-prodLeme) : Math.sqrt(prodLeme);
                PPCCSControl control = new PPCCSControl(acelGeom, lemeGeom);
                codFilho.Ut[i] = control;
            }
            return codFilho;
        }
    }

    /**
     * Classe que modela o operador de crossover BLX-Alpha associado ao genético
     * pra este problema.
     */
    private class CrosBLXAlpha extends oCrossover<PPCCSProblem, PPCCSCodification> {

        @Override
        public String name() {
            return "BLX-Alpha";
        }

        @Override
        public PPCCSCodification crossover(PPCCSProblem prob, PPCCSCodification ind1,
                PPCCSCodification ind2) throws Exception {
            PPCCSCodification codFilho = ind1.build(prob);
            double alpha = 1;
            for (int i = 0; i < ind1.Ut.length; i++) {
                double beta = -alpha + (1 + 2 * alpha) * prob.rnd.nextDouble();
                double acelBLX = ind1.Ut[i].getAceleration()
                        + beta * (ind2.Ut[i].getAceleration()
                        - ind1.Ut[i].getAceleration());
                double lemeBLX = ind1.Ut[i].getLeme()
                        + beta * (ind2.Ut[i].getLeme() - ind1.Ut[i].getLeme());
                double acel = bound(acelBLX, prob.inst.A_MIN, prob.inst.A_MAX);
                double lem = bound(lemeBLX, prob.inst.LEME_MIN, prob.inst.LEME_MAX);
                PPCCSControl control = new PPCCSControl(acel, lem);
                codFilho.Ut[i] = control;
            }
            return codFilho;
        }

        private double bound(double val, double min, double max) {
            return Math.min(max, Math.max(val, min));
        }
    }

    /**
     * Classe que modela o operador de mutação uniforme associado ao genético
     * pra este problema.
     */
    private class MutUniform extends oMutation<PPCCSProblem, PPCCSCodification> {

        @Override
        public String name() {
            return "Uniform";
        }

        @Override
        public void mutation(PPCCSProblem prob, PPCCSCodification ind) throws Exception {
            int t = prob.rnd.nextInt(prob.inst.T);
            double rndAceleration = prob.rnd.nextDouble(prob.inst.A_MIN, prob.inst.A_MAX);
            double rndLeme = prob.rnd.nextDouble(prob.inst.LEME_MIN,
                    prob.inst.LEME_MAX);
            ind.Ut[t] = new PPCCSControl(rndAceleration, rndLeme);
        }
    }

    /**
     * Classe que modela o operador de mutação limite associado ao genético pra
     * este problema.
     */
    private class MutLimite extends oMutation<PPCCSProblem, PPCCSCodification> {

        @Override
        public String name() {
            return "Limite";
        }

        @Override
        public void mutation(PPCCSProblem prob, PPCCSCodification ind) throws Exception {
            int t = prob.rnd.nextInt(prob.inst.T);
            double rnd = prob.rnd.nextDouble();
            double newAceleration;
            double newLeme;
            if (rnd < 0.5) {
                newAceleration = prob.inst.A_MIN;
            } else {
                newAceleration = prob.inst.A_MAX;
            }
            rnd = prob.rnd.nextDouble();
            if (rnd < 0.5) {
                newLeme = prob.inst.LEME_MIN;
            } else {
                newLeme = prob.inst.LEME_MAX;
            }
            ind.Ut[t] = new PPCCSControl(newAceleration, newLeme);
        }
    }

    /**
     * Classe que modela o operador de mutação creep associado ao genético pra
     * este problema.
     */
    private class MutCreep extends oMutation<PPCCSProblem, PPCCSCodification> {

        @Override
        public String name() {
            return "Creep";
        }

        @Override
        public void mutation(PPCCSProblem prob, PPCCSCodification ind) throws Exception {
            int t = prob.rnd.nextInt(prob.inst.T);
            double rndAcel = (prob.inst.A_MAX / 20.0) * (0.5 - prob.rnd.nextDouble());
            double newAceleration = ind.Ut[t].getAceleration() + rndAcel;
            if (newAceleration < 0.0 || newAceleration > prob.inst.A_MAX) {
                newAceleration = ind.Ut[t].getAceleration() - rndAcel;
            }
            double rndLeme = ((prob.inst.LEME_MAX - prob.inst.LEME_MIN) / 20.0)
                    * (0.5 - prob.rnd.nextDouble());
            double newLeme = ind.Ut[t].getLeme() + rndLeme;
            if (newAceleration < prob.inst.LEME_MIN
                    || newAceleration > prob.inst.LEME_MAX) {
                newLeme = ind.Ut[t].getLeme() - rndLeme;
            }
            ind.Ut[t] = new PPCCSControl(newAceleration, newLeme);
        }
    }
    
    /**
     * Classe que modela o operador de mutação capaz de desviar de obstaculos.
     * Não terminado ainda.
     * @deprecated 
     */
    private class MutDesvObstacle extends oMutation<PPCCSProblem, PPCCSCodification> {

        @Override
        public String name() {
            return "DesvObstacle";
        }

        @Override
        public void mutation(PPCCSProblem prob, PPCCSCodification ind) throws Exception {
            PPCCSState state = new PPCCSState(prob.inst.X0);         
            for (int i = 0; i < ind.Ut.length; i++){
                state = state.nextState(prob.inst.TYPE_OF_FAILURE, ind.Ut[i], 
                        prob.inst.DT, prob.inst.G);
                int idObstacle = Util.isNearObstacle(prob.inst, state, i);
                if (idObstacle != -1){
                    double rndAceleration = prob.rnd.nextDouble(prob.inst.A_MIN, 
                            prob.inst.A_MAX);
                    double rndLeme = prob.rnd.nextDouble(prob.inst.LEME_MIN,
                            prob.inst.LEME_MAX);
                    ind.Ut[i] = new PPCCSControl(rndAceleration, rndLeme);
                }
            }
        }    
    }
    
    private class Difference extends oDifference<PPCCSProblem, PPCCSCodification> {

        @Override
        public String name() {
            return "Difference";
        }

        @Override
        public PPCCSCodification difference(PPCCSProblem prob, PPCCSCodification ind1, 
                PPCCSCodification ind2) throws Exception {
            PPCCSCodification codFilho = ind1.build(prob);
            for (int i = 0; i < ind1.Ut.length; i++) {
                double acelDiff = ind1.Ut[i].getAceleration() - ind2.Ut[i].getAceleration();
                double lemeDiff = ind1.Ut[i].getLeme() - ind2.Ut[i].getLeme();
                codFilho.Ut[i] = new PPCCSControl(acelDiff, lemeDiff);
            }
            return codFilho;
        }
    }
    
    private class Disturbance extends oDisturbance<PPCCSProblem, PPCCSCodification> {

        @Override
        public String name() {
            return "Disturbance";
        }

        @Override
        public PPCCSCodification disturbance(PPCCSProblem prob, PPCCSCodification ind1, 
                PPCCSCodification ind2, int weight) throws Exception {
            PPCCSCodification codFilho = ind1.build(prob);
            for (int i = 0; i < ind1.Ut.length; i++) {
                double acelDist = ind1.Ut[i].getAceleration() + (weight/100.0) * 
                        ind2.Ut[i].getAceleration();
                double lemeDist = ind1.Ut[i].getLeme() + (weight/100.0) * 
                        ind2.Ut[i].getLeme();                
                //normaliza o resultado
                double acel = acelDist > prob.inst.A_MAX ? prob.inst.A_MAX : acelDist;
                acel =        acelDist < prob.inst.A_MIN ? prob.inst.A_MIN : acelDist;
                double leme = lemeDist > prob.inst.LEME_MAX ? prob.inst.LEME_MAX : lemeDist;
                leme =        lemeDist < prob.inst.LEME_MIN ? prob.inst.LEME_MIN : lemeDist;
                
                codFilho.Ut[i] = new PPCCSControl(acel, leme);
            }
            return codFilho;
        }
    }
    
    private class Recombine extends oRecombine<PPCCSProblem, PPCCSCodification> {

        @Override
        public String name() {
            return "Recombine";
        }

        @Override
        public PPCCSCodification recombine(PPCCSProblem prob, PPCCSCodification ind1, 
                PPCCSCodification ind2, int rateCrossover) throws Exception {
            PPCCSCodification codFilho = ind1.build(prob);
            int sigma = new Random().nextInt(ind1.Ut.length);
            for (int i = 0; i < ind1.Ut.length; i++) {
                if (new Random().nextDouble() < (rateCrossover/100.0) || i == sigma){
                    codFilho.Ut[i] = new PPCCSControl(ind2.Ut[i].getAceleration(), 
                            ind2.Ut[i].getLeme());
                }else {
                    codFilho.Ut[i] = new PPCCSControl(ind1.Ut[i].getAceleration(), 
                            ind1.Ut[i].getLeme());                    
                }
            }
            return codFilho;
        }
    }
}
