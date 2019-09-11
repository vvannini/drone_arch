/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ProOF.apl.pog.problem.CCQSP;

/**
 *
 * @author marcio
 */
public class QSP {
    private final CCQSPMap  map;
    //protected final int K;
    protected final double LBk[];
    protected final double UBk[];

    public QSP(CCQSPMap map, double LBk[], double UBk[]) {
        this.map = map;
        //this.K = K;
        this.LBk = LBk;
        this.UBk = UBk;
    }

    public QSP(CCQSPMap map, int K, double min, double max) {
        this.map = map;
        //this.K = K;
        this.LBk = new double[K];
        this.UBk = new double[K];
        for(int k=0; k<K; k++){
            LBk[k] = min;
            UBk[k] = max;
        }
    }
    
    
    public int O(int k) {
        if(k<0){
            return 1;
        }else{
            int lb = (int)(LBk[k]/map.DT + 0.9999);
            int ub = (int)(UBk[k]/map.DT + 0.0001);
            return ub - lb + 1;
        }
    }

    public double Ckij(int l, int i, int j) {
        if(l<0){
            return 0;
        }else{
            //return LBk[l]+j*map.DT;
            return (j+1)*map.DT;
        }
    }
}
