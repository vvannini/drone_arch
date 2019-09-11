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
public class uDataUp<T> implements Comparable<uDataUp>{
    public final Comparable ord;
    public final T data;

    public uDataUp(T data, Comparable ord) {
        this.ord = ord;
        this.data = data;
    }
    @Override
    public int compareTo(uDataUp o) {
        return this.ord.compareTo(o.ord);
    }
}
