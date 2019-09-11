/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCI;

import ProOF.opt.abst.problem.meta.codification.Codification;

/**
 *
 * @author marcio
 */
public class GCICodification extends Codification<
    GCIFactory, GCICodification
> {
    
    protected int te;           //period end
    protected int cromo[][];    //each machine k and period t
    
    public GCICodification(GCIFactory gci) {
        this.cromo = new int[gci.inst.K][gci.inst.T];
    }
    @Override
    public void copy(GCIFactory gci, GCICodification source) throws Exception {
        this.te = source.te;
        for(int k=0; k<gci.inst.K; k++){
            System.arraycopy(source.cromo[k], 0, this.cromo[k], 0, gci.inst.T);
        }
    }
    @Override
    public GCICodification build(GCIFactory gci) throws Exception {
        return new GCICodification(gci);
    }
    
    public double Yitk(int i, int t, int k){
        return (i==cromo[k][t] && t<te ? 1 : 0);
    }
    /**Informa se houve troca do produto i para o produto j no perido t e maquina k*/
    public double Zijtk(int i, int j, int t, int k){
        if(0<t && t<te){
            return (i==cromo[k][t-1] && j==cromo[k][t] ? 1 : 0);
        }else{
            return 0;
        }
    }
    /**Informa se a fornalha esta desativada no periodo t*/
    public double Qt(int t){
        return (t<te ? 1 : 0);
    }
    
    
    public void ChangeOneProduct(GCIFactory d) {
        int k = d.rnd.nextInt(d.inst.K);
        int t = d.rnd.nextInt(te);
        int index = d.rnd.nextInt(d.inst.Productkn[k].length);
        
        cromo[k][t] = d.inst.Productkn[k][index];
    }
    public void ChangeTwoProduct(GCIFactory d) {
        ChangeOneProduct(d);
        ChangeOneProduct(d);
    }
    public void SwapTwoProduct(GCIFactory d) {
        int k1 = d.rnd.nextInt(d.inst.K);
        int t1 = d.rnd.nextInt(te);
        int k2 = d.rnd.nextInt(d.inst.K);
        int t2 = d.rnd.nextInt(te);
        
        int aux = cromo[k1][t1];
        cromo[k1][t1] = cromo[k2][t2];
        cromo[k2][t2] = aux;
    }
    public void SwapTwoProductIn_K(GCIFactory d) {
        int k = d.rnd.nextInt(d.inst.K);
        int t1 = d.rnd.nextInt(te);
        int t2 = d.rnd.nextInt(te);
        int aux = cromo[k][t1];
        cromo[k][t1] = cromo[k][t2];
        cromo[k][t2] = aux;
    }
    public void SwapTwoProductIn_T(GCIFactory d) {
        int k1 = d.rnd.nextInt(d.inst.K);
        int t = d.rnd.nextInt(te);
        int k2 = d.rnd.nextInt(d.inst.K);
        int aux = cromo[k1][t];
        cromo[k1][t] = cromo[k2][t];
        cromo[k2][t] = aux;
    }
    public void DisableEnableFurnance(GCIFactory d) {
        if(te < d.inst.T && d.rnd.nextBoolean()){
            //Enable te+1
            te++;
        }else if(te>1){
            //Disable te
            te--;
        }
    }
    
    
    public GCICodification CrossoverOX(GCICodification ind2, GCIFactory d, boolean rand) throws Exception {
        GCICodification child = build(d);
        if(rand){
            child.te = d.rnd.nextInt(this.te, ind2.te);
        }else{
            child.te = Math.min(this.te, ind2.te);
        }
        for(int k=0; k<d.inst.K; k++){
            for(int t=0; t<d.inst.T; t++){
                if(d.rnd.nextBoolean()){
                    child.cromo[k][t] = this.cromo[k][t];
                }else{
                    child.cromo[k][t] = ind2.cromo[k][t];
                }
            }
        }
        return child;
    }
    public GCICodification Crossover1P_t(GCICodification ind2, GCIFactory d, boolean rand) throws Exception {
        GCICodification child = build(d);
        if(rand){ 
            child.te = d.rnd.nextInt(this.te, ind2.te);
        }else{
            child.te = Math.min(this.te, ind2.te);
        }
        for(int k=0; k<d.inst.K; k++){
            int tc = d.rnd.nextInt(0 ,d.inst.T);
            System.arraycopy(this.cromo[k], 0, child.cromo[k], 0, tc);
            System.arraycopy(ind2.cromo[k], tc, child.cromo[k], tc, d.inst.T - tc);
        }
        return child;
    }
    public GCICodification Crossover1P_k(GCICodification ind2, GCIFactory d, boolean rand) throws Exception {
        GCICodification child = build(d);
        if(rand){
            child.te = d.rnd.nextInt(this.te, ind2.te);
        }else{
            child.te = Math.min(this.te, ind2.te);
        }
        for(int t=0; t<d.inst.T; t++){
            int kc = d.rnd.nextInt(0 ,d.inst.K);
            for(int k=0; k<kc; k++){
                child.cromo[k][t] = this.cromo[k][t];
            }
            for(int k=kc; k<d.inst.K; k++){
                child.cromo[k][t] = ind2.cromo[k][t];
            }
        }
        return child;
    }
}
