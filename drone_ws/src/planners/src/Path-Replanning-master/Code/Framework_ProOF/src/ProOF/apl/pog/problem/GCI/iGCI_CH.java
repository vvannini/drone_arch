/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCI;

import ProOF.utilities.uUtil;





/**
 *
 * @author marcio
 */
public class iGCI_CH extends aGCI{
    private double Oi[]; //OverEstoque
    
    private double DHit[][];    //Demanda obrigatoria do produto i no periodo t
   
    private int Mkt[][];    //Numero de cavidades ativas acima do minimmo na maquina k no periodo t para produzir o produto que está neste local
    
    private int Wit[][];    //Numero de lotes do produto i no periodo t

    private double CAPt[];  //Capacidade disponivel no periodo t
    private double CUt[];   //Capacidade utilizada no periodo t
    
    private double Cost_Production;
    private double Cost_OverHold;
    
    @Override
    public String name() {
        return "Cavit Heuristic";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void start() throws Exception {
        super.start();
        Oi  = new double[prob.inst.N];
        CUt = new double[prob.inst.T];
        CAPt = new double[prob.inst.T];
        Mkt = new int[prob.inst.K][prob.inst.T];
        DHit = new double[prob.inst.N][prob.inst.T];
        Wit = new int[prob.inst.N][prob.inst.T];
    }
    @Override
    public void setCap(GCICodification codif, int t, double C) throws Exception {
        CAPt[t] = C;
    }
    
    
    @Override
    public void init(GCICodification codif) throws Exception {
        super.init(codif);
        for(int i=0; i<prob.inst.N; i++){
            Oi[i] = 0;
        }
        for(int t=0; t<prob.inst.T; t++){
            CUt[t] = 0;
        }
        for(int k=0; k<prob.inst.K; k++){
            for(int t=0; t<prob.inst.T; t++){
                Mkt[k][t] = 0;
            }
        }

        //-----Calcula o numero de lotes do produto i no periodo t (Wit)--------
        for(int i=0; i<prob.inst.N; i++){
            for(int t=0; t<prob.inst.T; t++){
                Wit[i][t] = 0;
            }
        }
        for(int k=0; k<prob.inst.K; k++){
            for(int t=0; t<codif.te; t++){
                int i = codif.cromo[k][t];
                Wit[i][t]++;
            }
        }
        
        //-------Calculando o estoque obrigatorio ----
        for(int i=0; i<prob.inst.N; i++){
            System.arraycopy(Dit[i], 0, DHit[i], 0, prob.inst.T);
        }
        for(int i=0; i<prob.inst.N; i++){
            //jogando demanda para ser produzida em periodos anterires
            //caso nao tenhamos lotes para o produto neste perido
            for(int t=prob.inst.T-1; t>0; t--){
                if(Wit[i][t]==0){
                    DHit[i][t-1] += DHit[i][t];
                    DHit[i][t] = 0;
                }
            }
        }
    }

    @Override
    protected double solve(GCICodification codif){
        Cost_Production = Cost_OverHold = 0;
        if(getNumberOfContradictoryBounds()>0){
            return 1E12 + getNumberOfContradictoryBounds() * 1e4;
            //return /*1E6*/ + getNumberOfContradictoryBounds() * 1e2;
        }else{
            
            Calc_DH_and_Oi(codif);
                
            Divide_Demanda(codif);
            
            if(isOptmal(codif)){
                //NormalizeToInt(C);
                
                OptimizerObj(codif);
                
                if(hasHoldingViolation(codif)){
                    return getConstant() + Cost_Production + Cost_OverHold - 1e6;
                }else if(hasCapacityViolation(codif)){
                    return getConstant() + Cost_Production + Cost_OverHold - 1e6;
                }else{
                    return getConstant() + Cost_Production + Cost_OverHold;
                }
            }else{
                //NormalizeToInt(C);
                Estoques(codif);

                //NormalizeToInt(C);

                OptimizerObj(codif);
                
                if(hasHoldingViolation(codif)){
                    return getConstant() + Cost_Production + Cost_OverHold - 1e6;
                }else if(hasCapacityViolation(codif)){
                    return getConstant() + Cost_Production + Cost_OverHold + 1e6;//- 1e6;
                }else{
                    return getConstant() + Cost_Production + Cost_OverHold;
                }
            }
        }
    }
    
    private void Calc_DH_and_Oi(GCICodification codif){
        //penalidade de demanda nao atendida obrigatoria
        for(int i=0; i<prob.inst.N; i++){
            if(Wit[i][0]==0){
                Oi[i] = DHit[i][0];
                DHit[i][0] = 0;

                Cost_OverHold += Oi[i]*OverPenality;
            }else{
                Oi[i] = 0;
            }
        }
        
        //Verifica ocasioes onde mesmo ativando o maximo de cavidades
        //não se consegue atender a demanda
        for(int i=0; i<prob.inst.N; i++){
            double Sum_Xi = 0;  //Acumulado da produção se ativar todas as cavidades
            double Sum_Di = 0;  //Acumulado da demanda
            double p = 0;       //parcela que deve ser penalizada
            for(int t=0; t<prob.inst.T; t++){
                for(int k=0; k<prob.inst.K; k++){
                    if(i==codif.cromo[k][t] && t<codif.te){
                        Sum_Xi += ubMitk[i][t][k] * prob.inst.Pik[i][k];
                    }
                }
                Sum_Di += DHit[i][t];
                if(Sum_Xi < Sum_Di){
                    p = Math.max(p, Sum_Di - Sum_Xi);
                }
            }
            //penaliza a parcela p caso esta seja maior que zero
            Oi[i] += p;
            Cost_OverHold += p*OverPenality;
            
            //retira da demanda a parcela p
            for(int t=0; t<prob.inst.T && p>0; t++){
                double q = Math.min(p, DHit[i][t]);
                DHit[i][t] = DHit[i][t] - q;
                p = p - q;
            }
        }
    }
    private void Divide_Demanda(GCICodification codif){
        //Atribuindo producao inicial, sem limite de capacidade
        for(int t=0; t<codif.te; t++){

            //Divide igualmente entre as linhas que produzem i
            int DHk[] = new int[prob.inst.K];
            for(int k=0; k<prob.inst.K; k++){
                int i = codif.cromo[k][t];
                DHk[k] = (int)(DHit[i][t]/(prob.inst.Pik[i][k] * Wit[i][t]));
            }
            
            for(int k=0; k<prob.inst.K; k++){
                int i = codif.cromo[k][t];

                double sum;
                if(Wit[i][t]==1){
                    sum = DHk[k]*prob.inst.Pik[i][k];
                }else{
                    sum = 0;
                    for(int m=0; m<prob.inst.K; m++){
                        if(codif.cromo[m][t]==i){
                            sum += DHk[m]*prob.inst.Pik[i][m];
                        }
                    }
                }

                if(DHit[i][t] > sum){// + 0.001
                    DHk[k]++;
                }
            }

            //============================================================================
            //=========================( Melhorar esta parte )============================
            //============================================================================
            //============================================================================
            //Quantia para estoque (Aqui pode ser melhorado comsiderando este valor real)
            int PHk[] = new int[prob.inst.K];

            for(int k=0; k<prob.inst.K; k++){
                int i = codif.cromo[k][t];

                int p = DHk[k];

                int max = ubMitk[i][t][k] - Mkt[k][t];

                //Se producao da parcela viola numero de cavidades
                if(p > max){
                    //Produz o maximo
                    AddProduction(i,t,k, max);
                    //Define parcela para estoque
                    PHk[k] = p - max;
                }else{
                    AddProduction(i,t,k, p);
                }
            }


            for(int k=0; k<prob.inst.K; k++){
                if(PHk[k] > 0){
                    //Obtem parcela para estoque
                    int p = PHk[k];
                    int i = codif.cromo[k][t];

                    //Procura produzir em outras maquinas que estao configuradas com o mesmo produto e
                    //em seguida Estoca o restante em qualquer maquina e periodos anteriores
                    for(int s=t; s>=0 && p > 0; s--){
                        for(int m=0; m<prob.inst.K && p > 0; m++){
                            if(codif.cromo[m][s]==i){
                                int aux = ubMitk[i][s][m] - Mkt[m][s];
                                if(p > aux){
                                    //Produz o maximo
                                    AddProduction(i,s,m, aux);
                                    p = p - aux;
                                }else{
                                    AddProduction(i,s,m, p);
                                    p = 0;
                                    break;
                                }
                            }
                        }
                    }

                    //Se nao foi possivel produzir toda a parcela, penaliza
                    if(p > 0){
                        Oi[i] += p * prob.inst.Pik[i][k];
                        Cost_OverHold +=  p * prob.inst.Pik[i][k] * OverPenality;
                    }
                }
            }
        }
    }
    
    private boolean isOptmal(GCICodification codif){
        for(int k=0; k<prob.inst.K; k++){
            for(int t=0; t<codif.te; t++){
                int i = codif.cromo[k][t];
                if(CUt[t] > CAPt[t] || Mkt[k][t] > ubMitk[i][t][k]){
                    return false;
                }
            }
        }
        return true;
    }
    
    private void OptimizerObj(GCICodification codif){
        //Produzido mais onde o custo de producao eh negativo
        //Seguindo ordem de Menor valor para Bit enquanto Bit<0
        boolean flag = true;
        for(int t=codif.te-1; t>=0 && flag; t--){
            for(int k=0; k<prob.inst.K && flag; k++){
                int i = codif.cromo[k][t];
                if(prob.inst.Bit[i][t]<0){
                    if(CUt[t] + prob.inst.Pik[i][k] <= CAPt[t] && Mkt[k][t] < ubMitk[i][t][k]){
                        int p = Math.min((int)((CAPt[t] - CUt[t])/prob.inst.Pik[i][k]) , ubMitk[i][t][k] - Mkt[k][t]);
                        AddProduction(i, t, k, p);
                    }
                }else{
                    flag = false;
                }
            }
        }
    }

    
    private void Estoques(GCICodification codif){
        //Previsao do quanto pode-se estocar do produto i em periodos anteriroes ao atual
        //Inicialmente QHi e QHit eh zero
        
        double PWi[] = new double[prob.inst.N];         //Potencial para escolher o produto i para estocar
        
        for(int t=0; t<codif.te; t++){
            //--------------------------------------------------------------------
            //Realiza estoque corrigindo capacidades violadas            
            
            boolean lotes[] = new boolean[prob.inst.K];
            double PWk[] = new double[prob.inst.K];
            for(int k=0; k<prob.inst.K; k++){
                int i = codif.cromo[k][t];
                PWk[k] = PWi[i];
            }
            
            
            for(int x=0; x<prob.inst.K; x++){
                //Obtem a maquina com o lote com maior potencial e que nao foi escolhido ainda
                int k = uUtil.indexMax(PWk, lotes);
                lotes[k] = true;
                
                int i = codif.cromo[k][t];    //obtem o produto que ocupa a maquina
                
                //Se temos capacidade faltando na fornalha nete periodo e
                //capacidades ativas na maquina para desativar e
                //podemos estocar o produto i em periodos anteriores
                if(Mkt[k][t] > 0 && CUt[t] > CAPt[t]){
                    //Quantia que precisa ser estocada para corrigir a viloacao
                    
                    //int qh = Util.minInt(1, (int)((CUt[t]-CAPt[t])/prob.inst.Pik[i][k] + 0.999), Mkt[k][t]);
                    //int qh = Util.minInt((int)((CUt[t]-CAPt[t])/prob.inst.Pik[i][k] + 0.999), Mkt[k][t]);

                    int mh = uUtil.minInt((int)((CUt[t]-CAPt[t])/prob.inst.Pik[i][k] + 0.999), Mkt[k][t]);
                    //int mh = Util.minInt(1, (int)((CUt[t]-CAPt[t])/prob.inst.Pik[i][k] + 0.999), Mkt[k][t]);
                    
                    double qh = mh * prob.inst.Pik[i][k]; 
                    //Remove qh de producao do produto i no periodo t que esta na maquina k
                    AddProduction(i, t, k, -mh);
                        

                    for(int s=t-1; s>=0 && qh > 0.001; s--){
                        for(int m=0; m<prob.inst.K && qh > 0.001; m++){
                            if(codif.cromo[m][s]==i){
                                if(CUt[s] < CAPt[s] && Mkt[m][s] < ubMitk[i][s][m]){
                                    //Quantia que sera estocada aqui
                                    int p = uUtil.minInt(
                                        (int)(qh/prob.inst.Pik[i][m] + 0.999),
                                        ubMitk[i][s][m] - Mkt[m][s],
                                        (int)((CAPt[s] - CUt[s])/prob.inst.Pik[i][m] + 0.001)
                                    );

                                    //Produz parcela
                                    AddProduction(i, s, m, p);
                                    
                                    //Atualiza qh
                                    qh -= p * prob.inst.Pik[i][m];
                                }
                            }
                        }
                    }
                    //Atualiza potencial
                    PWi[i] = PWi[i]/2.0;
                    
                    //Se nao foi possivel produzir toda a parcela qh, penaliza
                    if(mh>0){
                        PWi[i] = 0;
                        Oi[i] += mh * prob.inst.Pik[i][k];
                        Cost_OverHold +=  mh * prob.inst.Pik[i][k] * OverPenality;
                    }
                }
            }
            
            //--------------------------------------------------------------------
            //Realiza previsao do quanto pode-se estocar neste periodo dos produtos
            for(int k=0; k<prob.inst.K; k++){
                int i = codif.cromo[k][t];    //obtem o produto que ocupa a maquina k neste periodo t   
                //Se temos capacidade sobrando na fornalha nete periodo e capacidades sobrando na maquina para ativar
                if(CUt[t] < CAPt[t] && Mkt[k][t] < ubMitk[i][t][k]){
                    int mh = Math.min( ubMitk[i][t][k] - Mkt[k][t] , (int)((CAPt[t] - CUt[t])/prob.inst.Pik[i][k] + 0.001) );
                    double qh = mh * prob.inst.Pik[i][k]; //Atualiza previsao
                    //Podemos estocar produtos de periodos posteriores aqui.
                    //QHit[i][t] += qh;
                    //QHi[i] += qh;
                    
                    PWi[i] = PWi[i]/2.0 + qh / (1.0 + Math.max(0 , prob.inst.Bit[i][t]));
                }
            }
        }
    }
    
    private void AddProduction(int i, int t, int k, int p){
        //Atualiza producao
        Mkt[k][t] += p;
        //Atualiza capacidade utilizada
        CUt[t] += prob.inst.Pik[i][k] * p;
        //Calcula objetivo
        Cost_Production += prob.inst.Bit[i][t] * prob.inst.Pik[i][k] * p;
    }
    
    private boolean hasHoldingViolation(GCICodification codif){
        //Violacao de estoque
        for(int i=0; i<prob.inst.N; i++){
            double SumDis = 0;
            double Xi = Oi[i];
            for(int t=0; t<prob.inst.T; t++){
                for(int k=0; k<prob.inst.K && t<codif.te; k++){
                    if(codif.cromo[k][t]==i){
                        Xi += Mkt[k][t] * prob.inst.Pik[i][k];
                    }
                }
                SumDis += Dit[i][t];
                
                if(Xi + 0.001 < SumDis){
                    return true;
                }
            }
        }
        return false;
    }
    private boolean hasCapacityViolation(GCICodification codif){
        //Violacao de capacidade
        for(int t=0; t<codif.te; t++){
            double Xi = 0;
            for(int k=0; k<prob.inst.K; k++){
                int i = codif.cromo[k][t];
                Xi += Mkt[k][t] * prob.inst.Pik[i][k];
            }
            if(Xi > CAPt[t] + 0.001){
                return true;
            }
        }
        return false;
    }
    private void NormalizeToInt(GCICodification codif){
        
        //Violacao de estoque
        for(int i=0; i<prob.inst.N; i++){
            double SumDis = 0;
            double Xi = Oi[i];
            for(int t=0; t<prob.inst.T; t++){
                for(int k=0; k<prob.inst.K && t<codif.te; k++){
                    if(codif.cromo[k][t]==i){
                        Xi += Mkt[k][t] * prob.inst.Pik[i][k];
                    }
                }
                SumDis += Dit[i][t];
                
                if(Xi < SumDis){
                    //Recupera parcela de correcao
                    double p = SumDis - Xi;

                    //Estoque para correcao
                    for(int s=Math.min(t,codif.te-1); s>=0 && p>0.001; s--){
                        for(int k=0; k<prob.inst.K && p>0.001; k++){
                            if(codif.cromo[k][s]==i && ubMitk[i][s][k]>Mkt[k][s] && CUt[s]+ prob.inst.Pik[i][k] <= CAPt[s] ){
                                int aux = Math.min(ubMitk[i][s][k] - Mkt[k][s], (int)((CAPt[s]-CUt[s])/prob.inst.Pik[i][k]) );
                                if(p > aux * prob.inst.Pik[i][k]){
                                    //Produz o maximo
                                    AddProduction(i,s,k, aux);
                                    p = p - aux * prob.inst.Pik[i][k];
                                    Xi += aux * prob.inst.Pik[i][k];
                                }else{
                                    AddProduction(i,s,k, (int)(p/prob.inst.Pik[i][k]+0.999));
                                    Xi += (int)(p/prob.inst.Pik[i][k]+0.999) * prob.inst.Pik[i][k];
                                    p = 0;
                                    break;
                                }
                            }
                        }
                    }

                    //se nao conseguiu, penaliza
                    if(p>0.001){
                        Xi += p;
                        Oi[i] += p;
                        Cost_OverHold += p*OverPenality;
                    }
                    
                }
            }
        }
        
        //Violacao de capacidade
        for(int t=0; t<codif.te; t++){
            double Xi = 0;
            for(int k=0; k<prob.inst.K; k++){
                int i = codif.cromo[k][t];
                Xi += Mkt[k][t] * prob.inst.Pik[i][k];
            }
            if(Xi > CAPt[t]){
                double p = Xi-CAPt[t];
                //Oi[i] += p;
                //Cost_OverHold += p*100;
                Cost_OverHold -= 1e4;
            }
        }
    } 
    
}
