package ProOF.apl.pog.problem.RA.solvers;

import ProOF.apl.pog.problem.RA.model.MethodResult;
import ProOF.apl.pog.problem.RA.model.RAInstance;
import ProOF.apl.pog.problem.RA.sequencia.aRASequencia;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Stream.StreamPrinter;
import java.util.Arrays;

/**
 *
 * @author andre
 */
public class iRAGreedy extends aRASolver{
    private double Ct[];
    private double STt[];
    private double Ot[];
    public int Qit[][];
    private double Dit[][];
    private Set VDit[];
    
    private double Estoque;
    private double BackLog;
    private double OverTime; 
    private double Penality;
    private static final double PENALITY_FACTOR = 500000.0;
     
    private RAInstance inst;
    private aRASequencia seq;
    private double objetivo;

    @Override
    public void services(LinkerApproaches link) throws Exception {
        super.services(link);
        this.inst = link.need(RAInstance.class, this.inst);
        this.seq = link.need(aRASequencia.class, this.seq);
    }

    @Override
    public void load() throws Exception {
        super.load();
        Ct = new double[inst.T];
        STt = new double[inst.T];
        Ot = new double[inst.T];
        Qit = new int[inst.N][inst.T];
        Dit = new double[inst.N][inst.T];
        VDit = new Set[inst.N*inst.T];
    }
    
    
    
    
    @Override
    public double solve(int Yit[][], double STt[]) {
        //System.out.println("greedy");
        int x, _t;
        resetVars(Yit, STt);
        //Lote Mínimo
        for(int t = 0; t < inst.T; t++){
            for(int i=0; i<inst.N; i++){
                for(int n=0; n<Yit[i][t]; n++){
                    add(inst.LMi[i], i, t, t);
                }
            }
            
        }
        if(Penality>0){
            return Penality + 1e8; //TODO analisar o *
        }
        //Producao sem Backlog / Estoque / Overtime
        for(int t = 0; t < inst.T; t++){
            for(int i=0; i<inst.N; i++){
                for(int n=0; n<Yit[i][t]; n++){
                    x = (int) (Ct[t]/inst.Pi[i]);
                    if(Dit[i][t] < x) {
                        x = (int) Math.ceil(Dit[i][t]);
                    }
                    add(x, i, t, t);
                }
            }
        }
        //Producao do restante da demanda
        int k = 0;
        for(int i = 0; i < inst.N; i++){
            for(int t = 0; t < inst.T; t++){
                VDit[k++] = new Set(i, t, Dit[i][t]*inst.Hi[i]);
            }
        }
        Arrays.sort(VDit);
        for(Set e : VDit){
            int i = e.i;
            int t = e.t;
            while(Dit[i][t] >=1){
                _t = bestLocal(i, t);
                if(_t < inst.T){
                    if(Ct[_t] >= inst.Pi[i]){
                        x =  Math.min((int) Dit[i][t], (int) (Ct[_t]/inst.Pi[i]));
                        
                    } else {
                        x = Math.min((int) Dit[i][t], (int) ((Ct[_t] + Ot[_t])/inst.Pi[i]));
                    }
                    add(x, i, t, _t);
                } else {
                   
                    Dit[i][t] = 0;
                }
            } 
        }
        return decode(Yit, STt);
    }

    @Override
    public String name() {
        return "Greedy";
    }

    @Override
    public String description() {
        return "";
    }

    private void resetVars(int Yit[][], double STt[]) {
        Penality = BackLog = OverTime = Estoque = 0;
        //Iniciando Ct
        for(int t = 0; t < inst.T; t++){
            this.Ct[t] = inst.Ct[t]-STt[t];          
        }
        //Iniciando Ot
        System.arraycopy(inst.Ut, 0, this.Ot, 0, inst.T);
        //Iniciando Dit
        for(int i = 0; i < inst.N; i++){
            for(int t = 0; t < inst.T;t++){
                this.Dit[i][t] = inst.Dit[i][t];
            }
        }
        
        //Arrumando penalidades
        for(int t = 0; t < inst.T;t++){
            if(Ct[t] < 0){
                Ot[t] -= Ct[t];
                Ct[t] = 0;
                if(Ot[t] < 0){
                    Penality -= Ot[t]*PENALITY_FACTOR;
                    Ot[t] = 0;
                }
            }
        }
        //Iniciando Qit
        for(int i = 0; i < inst.N;i++){
            for(int t = 0; t < inst.T; t++){
                Qit[i][t] = 0;
            }
        }
        
    }

    private double decode(int Yit[][], double STt[]) {
        this.objetivo = 0;
        Estoque = 0;
        BackLog = 0;
        
        double temp;
        for(int i = 0; i < inst.N; i++){
            temp = 0;
            for(int t = 0; t < inst.T; t++){
                temp += Qit[i][t] - inst.Dit[i][t];
                if(temp > 0){
                    Estoque += inst.Hi[i]*temp;
                } else {
                    BackLog -= inst.Bi[i]*temp;
                }
            } 
        }
        OverTime = 0;
        Penality = 0;
        for(int t = 0; t < inst.T; t++){
            temp = STt[t];
            for(int i = 0; i < inst.N; i++){
                temp += inst.Pi[i]*Qit[i][t];
            }
            temp -= inst.Ct[t];
            if(temp >= 0){
                OverTime += temp*inst.COt[t];
                if(temp>inst.Ut[t]){
                    Penality += (temp-inst.Ut[t]) * PENALITY_FACTOR;
                }
            }
            //OverTime += temp >= 0 ? temp*G.COt[t]: 0;            
        }
        /*if(OverTime + BackLog + Estoque + Penality<4000){
            decode();
        }*/
        
        this.objetivo = OverTime + BackLog + Estoque + Penality;
        return objetivo;
    }
    /**
     * Adiciona lotes para producao em um dado periodo
     * @param x quantidade de lotes
     * @param i produto
     * @param t periodo em que ha demanda por este produto
     * @param _t periodo para produzir os lotes
     */
    private void add(int x, int i, int t, int _t) {
        Qit[i][_t] += x;
        Ct[_t] -= inst.Pi[i]*x;
        
        Dit[i][t] -= x;
        
        for(int s = t; s < inst.T -1 && Dit[i][s] < 0; s++){
            Dit[i][s + 1] += Dit[i][s];
            Dit[i][t] = 0;
        }
        if(Dit[i][inst.T-1]<0){
           Dit[i][inst.T-1] = 0; 
        }
        
        if(Ct[_t] < 0){
            Ot[_t] += Ct[_t];
            Ct[_t] = 0;
            if(Ot[_t] < 0) {
                Penality += -Ot[_t]*PENALITY_FACTOR;
                Ot[_t] = 0;
            }
        }
    }

    private int bestLocal(int i, int t) {
        int bestT = inst.T + 1;
        double bestCost = inst.Bi[i]*(inst.T+1-t);
        double temp;
        for(int _t = 0; _t < inst.T; _t++){
            //Como esse metodo so vai ser chamado depois de produzir o lote minimo, Qit > 0 <=> Wit == 1
            if(Ct[_t] + Ot[_t] >= inst.Pi[i] && Qit[i][_t] > 0){
                temp = inst.Pi[i] - Ct[_t] > 0 ? inst.COt[_t]*(inst.Pi[i] - Ct[_t]) : 0;
                if(_t > t) {
                    temp += inst.Bi[i]*(_t-t);
                } else {
                    temp += inst.Hi[i]*(t-_t);
                }
                if(temp < bestCost){
                    bestT = _t;
                    bestCost = temp;
                }
            }
        }
        
        return bestT;
    }
    private class Set implements Comparable<Set> {
        final int i, t;
        final int D;

        public Set(int i, int t, double D) {
            this.i = i;
            this.t = t;
            this.D = (int) Math.ceil(D);
        }
        
        @Override
        public int compareTo(Set t) {
            if(t.D < this.D) {
                return 1;
            }
            else if(t.D == this.D){
                return 0;
            } else {
                return -1;
            }
        }
        
    }
    public void printer(StreamPrinter com) throws Exception {
        com.printDbl("Estoque", Estoque);        
        com.printDbl("Overtime", OverTime);
        com.printDbl("Backlog", BackLog);
        com.printDbl("Penality", Penality);
    }  
    public void export(MethodResult res){
        res.Qit = this.Qit;
        
    }
}
