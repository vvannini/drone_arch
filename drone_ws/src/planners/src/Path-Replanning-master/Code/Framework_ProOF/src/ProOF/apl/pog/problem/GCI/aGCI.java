/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCI;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Approach;

/**
 *
 * @author marcio
 */
public abstract class aGCI extends Approach{
    public static final double OverPenality = 1000.0;
    
    //public abstract void execute(pMetaProblem prob, pMetaSolution best) throws Exception;
    protected GCIFactory prob;
    
    protected int ubMitk[][][];
    protected double Dit[][];
    private int NumberOfContradictoryBounds;
    private double Constant;
    
    protected abstract void setCap(GCICodification codif, int t, double C) throws Exception;
    protected abstract double solve(GCICodification codif) throws Exception;
    
    protected void setEst(GCICodification codif, int i, int t, double Sum_Dis)throws Exception{}
    protected void setMitk_UB(GCICodification codif, int i, int t, int k, int UB)throws Exception{}
    
    protected int getNumberOfContradictoryBounds() {
        return NumberOfContradictoryBounds;
    }
    protected double getConstant() {
        return Constant;
    }
    
    @Override
    public void services(LinkerApproaches link) throws Exception {
        prob = link.need(GCIFactory.class, prob);
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        
    }
    @Override
    public boolean validation(LinkerValidations com) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void load() throws Exception {
        
    }

    @Override
    public void start() throws Exception {
        ubMitk = new int[prob.inst.N][prob.inst.T][prob.inst.K];
        Dit = new double[prob.inst.N][prob.inst.T];
    }

    @Override
    public void results(LinkerResults com) throws Exception {
        
    }
    
    
    protected int LB(GCICodification codif, int i, int t, int k){
        if(i == codif.cromo[k][t] && t < codif.te){
            if(t>0){
                int j = codif.cromo[k][t-1];
                return Math.max((int)(prob.inst.MINik[i][k]+0.001), (int)(prob.inst.Sijk[j][i][k]/prob.inst.Pik[i][k] + 0.999));
            }else{
                return (int)(prob.inst.MINik[i][k]+0.001);
            }
        }else{
            return 0;
        }
    }
    
    public void init(GCICodification codif) throws Exception{
        for(int i=0; i<prob.inst.N; i++){
            for(int t=0; t<prob.inst.T; t++){
                for(int k=0; k<prob.inst.K; k++){
                    if(i == codif.cromo[k][t] && t < codif.te){ //Se Yitk == 1
                        ubMitk[i][t][k] = (int)(prob.inst.MAXik[i][k]-LB(codif,i,t,k)+0.001);
                    }else{
                        ubMitk[i][t][k] = 0;
                    }
                }
            }
        }
        
        //Calcula a demanda adicionando os tempos de troca
        for(int i=0; i<prob.inst.N; i++){
            System.arraycopy(prob.inst.Dit[i], 0, Dit[i], 0, prob.inst.T);
        }
        for(int k=0; k<prob.inst.K; k++){
            for(int t=1; t<codif.te; t++){
                int i = codif.cromo[k][t];
                int j = codif.cromo[k][t-1];
                Dit[i][t] += prob.inst.Sijk[j][i][k];
            }
        }
        
        //Calcula o valor da constante (A2)
        Constant = prob.inst.W * prob.inst.C * codif.te;
        for(int i=0; i<prob.inst.N; i++){
            for(int t=0; t<prob.inst.T; t++){
                double sum = 0;
                for(int s=0; s<=t; s++){
                    sum += Dit[i][s];
                }
                Constant -= prob.inst.Hi[i]*sum;
            }
        }
        for(int k=0; k<prob.inst.K; k++){
            for(int t=0; t<codif.te; t++){
                int i = codif.cromo[k][t];
                Constant += prob.inst.Bit[i][t] * prob.inst.Pik[i][k] * LB(codif, i, t, k);
            }
        }
        
        //Atualiza a demanda produzindo o minimo
        for(int k=0; k<prob.inst.K; k++){
            for(int t=0; t<codif.te; t++){
                int i = codif.cromo[k][t];
                Dit[i][t] -= prob.inst.Pik[i][k] * LB(codif, i, t, k);
            }
        }
        
        //Atualiza a demanda para impedir demanda negativa
        for(int i=0; i<prob.inst.N; i++){
            for(int t=0; t<prob.inst.T; t++){
                if(Dit[i][t]<0){
                    if(t+1<prob.inst.T){
                        Dit[i][t+1] += Dit[i][t];
                        Dit[i][t] = 0;
                    }else{
                        //Producao do minimo atende toda a demanda
                        Dit[i][t] = 0;
                    }                    
                }
            }
        }
        
        NumberOfContradictoryBounds = 0;
        for(int t=0; t<prob.inst.T; t++){
            if(t<codif.te){
                double sum = 0;
                for(int k=0; k<prob.inst.K; k++){
                    int i = codif.cromo[k][t];
                    sum += prob.inst.Pik[i][k] * LB(codif, i, t, k);
                }
                if(prob.inst.C < sum ){//+ 0.001
                    NumberOfContradictoryBounds++;
                }else{
                    setCap(codif, t, prob.inst.C - sum);
                }
            }else{
                setCap(codif, t, 0);
            }
        }
        
        for(int k=0; k<prob.inst.K; k++){
            for(int i=0; i<prob.inst.N; i++){
                for(int t=0; t<prob.inst.T; t++){
                    if(ubMitk[i][t][k] >= 0 ){
                        setMitk_UB(codif, i,t,k, ubMitk[i][t][k]);
                    }else{
                        NumberOfContradictoryBounds++;
                    }
                }
            }
        }
        for(int i=0; i<prob.inst.N; i++){
            for(int t=0; t<prob.inst.T; t++){
                double sum = 0;
                for(int s=0; s<=t; s++){
                    sum += Dit[i][s];
                }
                setEst(codif, i, t, sum);
            }
        }
    }
    
    
}
