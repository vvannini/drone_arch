package ProOF.apl.pog.problem.RA.sequencia;

import java.util.ArrayList;

/**
 *
 * @author andre
 */
public class iRASeqIndependente extends aRASequencia{

    @Override
    public String name() {
        return "Independente";
    }

    @Override
    public String description() {
        return "Sequencia inpendente";
    }

    @Override
    public double tempoSetup(ArrayList<Integer> Yt[]) {
        
        double total = 0;    
        for(int t=0; t<inst.T; t++){
            double tempoSetup = 0;
            int a,b = -1;
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
