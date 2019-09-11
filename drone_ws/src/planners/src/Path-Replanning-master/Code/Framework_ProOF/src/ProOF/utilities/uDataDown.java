/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.utilities;

/**
 *
 * @author marcio
 * @param <T>
 */
public class uDataDown<T> implements Comparable<uDataDown>{
    public final Comparable ord;
    public final T data;

    public uDataDown(T data, Comparable ord) {
        this.ord = ord;
        this.data = data;
    }
    @Override
    public int compareTo(uDataDown o) {
        return o.ord.compareTo(this.ord);
    }

    @Override
    public String toString() {
        return String.format("[%s %s]", data, ord);
    }
    
}
