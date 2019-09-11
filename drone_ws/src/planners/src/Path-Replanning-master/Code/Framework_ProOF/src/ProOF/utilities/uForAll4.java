/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.utilities;

/**
 *
 * @author marcio
 */
public abstract class uForAll4<T> extends uForAll {    
    public final T v[][][][];

    public uForAll4(T[][][][] v) {
        this.v = v;
        execute();
    }
    public abstract T all(int i, int j, int k, int m);
    @Override
    public final void execute(){
        for(int i=0; i<v.length; i++){
            for(int j=0; j<v[i].length; j++){
                for(int k=0; k<v[i][j].length; k++){
                    for(int m=0; m<v[i][j][k].length; m++){
                        v[i][j][k][m] = all(i,j,k,m);
                    }
                }
            }
        }
    }
}