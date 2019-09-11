/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.utilities;

/**
 *
 * @author marcio
 */
public abstract class uForAll1<T> extends uForAll {
    public final T v[];

    public uForAll1(T[] v) {
        this.v = v;
        execute();
    }
    public abstract T all(int i);
    
    public final void execute(){
        for(int i=0; i<v.length; i++){
            v[i] = all(i);
        }
    }
}