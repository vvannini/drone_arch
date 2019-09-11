/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.utilities;

import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * @author marcio
 */
public class uSort {
    
    public static void sort(Comparator cmp, Comparable ...R) {
        Arrays.sort(R, cmp);
    }
    public static void sort(Comparable ...R) {
        Comparator<Comparable> cmp = new Comparator<Comparable>() {
            @Override
            public int compare(Comparable o1, Comparable o2) {
                return o1.compareTo(o2);
            }
        };
        sort(cmp, R);
    }
    /**
     * Sort the index I by compare Ri.compareTo(Rj), the R is not modified
     * @param R
     * @param I 
     */
    public static void sort(final Comparable R[], Integer I[]) {
        Comparator<Integer> cmp = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return R[o1].compareTo(R[o2]);
            }
        };
        sort(cmp, I);
    }
}
