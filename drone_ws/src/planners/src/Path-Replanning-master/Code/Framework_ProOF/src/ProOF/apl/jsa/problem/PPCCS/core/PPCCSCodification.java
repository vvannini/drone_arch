/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ProOF.apl.jsa.problem.PPCCS.core;

import ProOF.apl.jsa.problem.PPCCS.structure.PPCCSControl;
import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 * Classe responsável por fazer a codificação do VANT.
 * @author Jesimar
 */
public class PPCCSCodification extends Codification<PPCCSProblem, PPCCSCodification>{

    /**
     * Controles do VANT.
     */
    public final PPCCSControl Ut[];    
    public int K;
    public double posFinalX;
    public double posFinalY;
    public String landingLocal;

    /**
     * Construtor da codificação do VANT.
     * @param T - tempo total entre o instante de pane até que ocorra a queda do VANT.
     */
    public PPCCSCodification(int T) {
        Ut = new PPCCSControl[T];
    }        
    
    @Override
    public void copy(PPCCSProblem prob, PPCCSCodification source) throws Exception {
        System.arraycopy(source.Ut, 0, this.Ut, 0, Ut.length);
        this.K = source.K;
        this.posFinalX = source.posFinalX;
        this.posFinalY = source.posFinalY;
        this.landingLocal = source.landingLocal;
    }

    @Override
    public PPCCSCodification build(PPCCSProblem prob) throws Exception {
        return new PPCCSCodification(Ut.length);
    }        
}
