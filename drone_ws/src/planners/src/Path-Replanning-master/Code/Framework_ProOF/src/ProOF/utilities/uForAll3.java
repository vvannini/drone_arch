/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.utilities;

/**
 *
 * @author marcio
 */
public abstract class uForAll3<T> extends uForAll {    
    public final T v[][][];

    public uForAll3(T[][][] v) {
        this.v = v;
        execute();
    }
    public abstract T all(int i, int j, int k);
    
    @Override
    public final void execute(){
        for(int i=0; i<v.length; i++){
            for(int j=0; j<v[i].length; i++){
                for(int k=0; k<v[i][j].length; i++){
                    v[i][j][k] = all(i,j,k);
                }
            }
        }
    }
}