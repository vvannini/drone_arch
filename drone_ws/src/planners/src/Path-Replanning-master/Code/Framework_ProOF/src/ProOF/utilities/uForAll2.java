/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.utilities;

/**
 *
 * @author marcio
 */
public abstract class uForAll2<T> extends uForAll {
    public final T v[][];

    public uForAll2(T[][] v) {
        this.v = v;
        execute();
    }
    public abstract T all(int i, int j);
    @Override
    public final void execute(){
        for(int i=0; i<v.length; i++){
            for(int j=0; j<v[i].length; i++){
                v[i][j] = all(i,j);
            }
        }
    }
}