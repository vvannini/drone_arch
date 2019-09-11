package ProOF.apl.pog.problem.RA.sequencia;

import java.util.ArrayList;

/**
 *
 * @author andre
 */
public class iRASeqDependente extends aRASequencia{

    @Override
    public String name() {
        return "Dependente";
    }

    @Override
    public String description() {
        return "Sequencia dependente";
    }

    @Override
    public double tempoSetup(ArrayList<Integer> Yt[]){
        double total = 0;
        for(int t=0; t<inst.T; t++){
            int a,b = -1;
            double tempoSetup = 0;
            for(int ti = t-1; ti >= 0; ti--){
                if(!Yt[ti].isEmpty()){
                    b = Yt[ti].get(Yt[ti].size() -1);
                    break;
                }
            }
            for( int i=0; i<Yt[t].size(); i++ ){
                a = Yt[t].get(i);
                if(b!=-1){
                    tempoSetup += inst.STij[b][a];
                }
                b = a;
            }
            STt[t] = tempoSetup;
            total += tempoSetup;
        }
            
        return total;
    }
}
