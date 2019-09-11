/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.utilities;

import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author Marcio
 */
public class uRandom extends Random {

    public int nextInt(int a, int b) {
        return b > a ? a + super.nextInt(b - a + 1)
                : b < a ? b + super.nextInt(a - b + 1)
                : a;
    }

    /**
     * [a,b[
     */
    public double nextDouble(double a, double b) {
        return b > a ? a + nextDouble() * (b - a)
                : b < a ? b + nextDouble() * (a - b)
                : a;
    }

    public Object nextObj(LinkedList list) {
        return list.get(nextInt(list.size()));
    }

    public Object nextObj(Object array[]) {
        return array[nextInt(array.length)];
    }

    public Integer nextInt(LinkedList<Integer> list) {
        return list.get(nextInt(list.size()));
    }

    public Integer nextInt(Integer array[]) {
        return array[nextInt(array.length)];
    }

    public int nextInt(int array[]) {
        return array[nextInt(array.length)];
    }

    public int[] shuffle(int a, int b) {
        int r[] = new int[b - a];
        int tmp, j, k;

        r[0] = a;
        for (int i = 1; i < b - a; i++) {
            r[i] = r[i - 1] + 1;
        }

        for (int i = 0; i < r.length; i++) {
            j = nextInt(r.length);
            k = nextInt(r.length);
            tmp = r[j];
            r[j] = r[k];
            r[k] = tmp;
        }

        return r;
    }

    public int[] cuts_points(int n, int length) {
        if (n < length) {
            throw new IllegalArgumentException("n<length");
        }
        boolean find[] = new boolean[n];
        for (int i = 0; i < length; i++) {
            int r = nextInt(n);
            while (find[r]) {
                r = nextInt(n);
            }
            find[r] = true;
        }
        int p[] = new int[length];
        int j = 0;
        for (int i = 0; i < n; i++) {
            if (find[i]) {
                p[j] = i;
                j++;
            }
        }
        return p;
    }

    public int nextInt(int a, int b, int... not) {
        if (Math.abs(b - a + 1) < not.length) {
            throw new IllegalArgumentException("Math.abs(b-a+1)<not.length");
        }
        int r = nextInt(a, b);
        while (find(not, r)) {
            r = nextInt(a, b);
        }
        return r;
    }

    public int nextInt(int length, int not_ord[], int size) {
        return nextInt(0, length - 1, not_ord, size);
    }

    public int nextInt(int a, int b, int not_ord[], int size) {
        int aux = nextInt(a, b);
        while (bin_search(not_ord, aux, 0, size - 1)) {
            aux = nextInt(a, b);
        }
        return aux;
    }

    private static boolean find(int vet[], int key) {
        for (int v : vet) {
            if (v == key) {
                return true;
            }
        }
        return false;
    }

    public int[] nextCutPoints(int[] array, int length) throws Exception {
        if (array.length > length || array.length < 1) {
            throw new Exception("p.length (" + array.length + ")>length(" + length + ") || p.length<1 ");
        }
        array[0] = nextInt(length);
        for (int i = 1; i < array.length; i++) {
            int key = nextInt(length, array, i);
            insert(array, key, i);
        }
        return array;
    }

    private void insert(int array[], int key, int i) {
        int j = i - 1;
        while (j >= 0 && key < array[j]) {
            array[j + 1] = array[j];
            j--;
        }
        array[j + 1] = key;
    }

    private boolean bin_search(int array[], int key) {
        return bin_search(array, key, 0, array.length - 1);
    }

    private boolean bin_search(int array[], int key, int a, int b) {
        if (a <= b) {
            int m = (a + b) / 2;
            if (array[m] < key) {
                return bin_search(array, key, m + 1, b);
            } else if (array[m] > key) {
                return bin_search(array, key, a, m - 1);
            } else {
                return true;
            }
        }
        return false;
    }

    public long[] nextMask(long[] mask, int length) {
        mask = (mask != null)
                ? mask
                : new long[(length + 63) / 64];
        for (int j = 0; j < mask.length; j++) {
            mask[j] = nextLong();
        }
        return mask;
    }

    public int[] nextMask(int[] mask, int length) {
        mask = (mask != null)
                ? new int[(length + 63) / 64]
                : mask;
        for (int j = 0; j < mask.length; j++) {
            mask[j] = nextInt();
        }
        return mask;
    }

    public boolean decode(long[] mask, int bit) {
        return uUtil.decode(mask, bit);
    }

    public int roulette_wheel(double sum, double weigth[]) {
        double x = nextDouble() * sum;
        for (int i = 0; i < weigth.length; i++) {
            if (x > weigth[i]) {
                x = x - weigth[i];
            } else {
                return i;
            }
        }
        throw new ArithmeticException("Algorithm roulette incorrect.");
    }

    public int roulette_wheel(double weigth[]) {
        return roulette_wheel(uUtil.sum(weigth), weigth);
    }
}
