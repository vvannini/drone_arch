/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.RA.Codif2.TSP;

import java.util.ArrayList;

/**
 *
 * @author marcio
 */
public class iRATSPGreedy extends aRATSP{
    
    
    @Override
    public String name() {
        return "RA-TSP-Greedy";
    }
    
    @Override
    public ArrayList<Integer>[] solve(int[][] Wit) throws Exception {
        ArrayList<Integer> Yt[] = new ArrayList[inst.T];
        for(int t=0; t<inst.T; t++){
            Yt[t] = new ArrayList<Integer>();
        }
        double custo_final = 0;
        for(int t=0; t<inst.T; t++){
            /*System.out.printf("Wit [%d] -- > [ ", t);
            for(int i=0; i<inst.N; i++){
                System.out.printf("%s ", Wit[i][t]==0?"-":"*");
            }
            System.out.println();
            */
            int cout = 0;
            for(int i=0; i<inst.N; i++){
                if(Wit[i][t]>0){
                    cout++;
                }
            }
            if(cout==0){
                //System.out.println("List ["+t+"] -- > "+ Yt[t]);
                break;
            }else{
                boolean  flag[] = new boolean[inst.N];
                for(int i=0; i<inst.N; i++){
                    if(Wit[i][t]>0 && !flag[i]){
                        Yt[t].add(i);
                        flag[i] = true;
                        break;
                    }
                }
                double custo_total = 0;
                while(Yt[t].size()<cout){
                    int i = Yt[t].get(Yt[t].size()-1);
                    int next = -1;
                    double custo_next = Integer.MAX_VALUE;
                    for(int j=0; j<inst.N; j++){
                        if(!flag[j] && Wit[j][t]>0 && inst.STij[i][j]<custo_next){
                            custo_next = inst.STij[i][j];
                            next = j;
                        }
                    }

                    i = Yt[t].get(0);
                    int back = -1;
                    double custo_back = Integer.MAX_VALUE;
                    for(int j=0; j<inst.N; j++){
                        if(!flag[j] && Wit[j][t]>0 && inst.STij[j][i]<custo_back){
                            custo_back = inst.STij[j][i];
                            back = j;
                        }
                    }

                    if(custo_next < custo_back){
                        custo_total += custo_next;
                        Yt[t].add(Yt[t].size()-1, next);
                        flag[next] = true;
                    }else{
                        custo_total += custo_back;
                        Yt[t].add(0, back);
                        flag[back] = true;
                    }
                }
                    
                if(custo_total>0.1){
                    custo_final += custo_total;
                    System.out.printf("List [%d] = %8g -- > %s\n",t, custo_total, Yt[t]);
                }
            }
                
        }
        if(custo_final>0.1){
            System.out.printf("custo final -- > %8g\n",custo_final);
        }
        return Yt;
    }
}
