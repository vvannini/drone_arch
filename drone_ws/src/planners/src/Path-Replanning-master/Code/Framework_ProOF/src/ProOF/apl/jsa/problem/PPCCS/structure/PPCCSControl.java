/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ProOF.apl.jsa.problem.PPCCS.structure;

/**
 * Classe responsável por armazenar os controles do VANT.
 * @author Jesimar
 */
public class PPCCSControl {
    
    /**
     * Acelaração do VANT (m/s²).
     */
    private final double aceleration;
    
    /**
     * Angulo de mudança de direção do VANT (radianos).
     */
    private final double leme;       

    /**
     * Construtor dos controles do VANT.
     * @param aceleration - aceleração do VANT (m/s²).
     * @param leme - angulo de mudança de direção do VANT (radianos).
     */
    public PPCCSControl(double aceleration, double leme) {
        this.aceleration = aceleration;
        this.leme = leme;
    }

    public double getAceleration() {
        return aceleration;
    }

    public double getLeme() {
        return leme;
    }        
}
