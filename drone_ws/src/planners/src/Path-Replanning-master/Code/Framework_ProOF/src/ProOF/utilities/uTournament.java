/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.utilities;

/**
 *
 * @author marcio
 */
public class uTournament<T extends Comparable> {
    private final uRandom rmd;

    private final int tour_size;
    private final T array[];
        
    public uTournament(T array[], int type) {
        this(new uRandom(), array, type);
    }
    public uTournament(uRandom rmd, T array[], int type) {
        this.rmd = rmd;
        this.array = array;
        this.tour_size = type;
    }
    /**
     * @return select_in[0,n-1]
     */
    public int select_in(int n){
        return select_in(0, n-1);
    }
    /**
     * @return select_in[a,b]
     */
    public int select_in(int a, int b){
        int best = rmd.nextInt(a, b);
        for(int t=1; t<tour_size; t++){
            int i = rmd.nextInt(a, b);
            if(array[i].compareTo(array[best]) < 0){
                best = i;
            }
        }
        return best;
    }
    public T select_elem(){
        return array[select()];
    }
    public int select(){
        int best = rmd.nextInt(array.length);
        for(int t=1; t<tour_size; t++){
            int i = rmd.nextInt(array.length);
            if(array[i].compareTo(array[best]) < 0){
                best = i;
            }
        }
        return best;
    }
    public int select(int ...not) throws Exception{
        int best = -1;
        for(int t=0; t<tour_size; t++){
            int i = rmd.nextInt(array.length);
            if(uUtil.find(i, not)){
                t--;
            }else if(best<0){
                best = i;
            }else if(array[i].compareTo(array[best]) < 0){
                best = i;
            }
        }
        if(best==-1){
            throw new Exception("tour_size > array.length");
        }
        return best;
    }
    
}
