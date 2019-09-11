/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.utilities;

import java.util.Random;

/**
 *
 * @author marcio
 */
public class uRoulette {
    private final Random rmd;

    private final double sum;
    private final double weigth[];
    
    
    /*public uRoulette(Random rmd) {
        this(rmd, 0, null);
    }*/
    public uRoulette(Random rmd, double max, double min, int size) {
        this(rmd, sum(max, min, size), weigth(max, min, size));
    }
    public uRoulette(Random rmd, double max, int size) {
        this(rmd, sum(max, size), weigth(max, size));
    }
    public uRoulette(Random rmd, double sum, double[] weigth) {
        this.rmd = rmd;
        this.sum = sum;
        this.weigth = weigth;
    }
    public uRoulette(Random rmd, double[] weigth) {
        this.rmd = rmd;
        this.sum = uUtil.sum(weigth);
        this.weigth = weigth;
    }
    
    public static double [] weigth(double max, int size){
        return weigth(max, max/size, size);
    }
    public static double [] weigth(double max, double min, int size){
        double p[] = new double[size];
        for(int i=0; i<size; i++){
            p[i] = (size-i-1)*(max-min)/(size-1) + min;
        }
        return p;
    }
    public static double sum(double max, int size){
        return sum(max, max/size, size);
    }
    public static double sum(double max, double min, int size){
        double sum = 0;
        for(int i=0; i<size; i++){
            sum += (size-i-1)*(max-min)/(size-1) + min;
        }
        return sum;
    }
    
    public int roulette_wheel(double weigth[]){
        double tot = uUtil.sum(weigth);
        double x = rmd.nextDouble()*tot;
        for(int i=0; i<weigth.length; i++){
            if(x>weigth[i]){
                x = x - weigth[i];
            }else{
                return i;
            }
        }
        throw new ArithmeticException("Algorithm roulette incorrect.");
    }
    public int roulette_wheel(int index[], double weigth[]){
        return index[roulette_wheel(weigth)];
    }
    public int roulette_wheel(double sum, double weigth[]){
        double x = rmd.nextDouble()*sum;
        for(int i=0; i<weigth.length; i++){
            if(x>weigth[i]){
                x = x - weigth[i];
            }else{
                return i;
            }
        }
        throw new ArithmeticException("Algorithm roulette incorrect.");
    }
    public int roulette_wheel(){
        return roulette_wheel(sum, weigth);
    }
    
    public int roulette_wheel(int ...not) throws Exception{
        double sum = 0;
        for(int i=0; i<weigth.length; i++){
            if(!uUtil.find(i, not)){
                sum += weigth[i];
            }
        }
        if(Math.abs(sum)<1e-6){
            throw new Exception("Math.abs(sum)<1e-6");
        }
        double x = rmd.nextDouble()*sum;
        for(int i=0; i<weigth.length; i++){
            if(uUtil.find(i, not)){
               continue; 
            }else if(x>weigth[i]){
                x = x - weigth[i];
            }else{
                return i;
            }
        }
        throw new ArithmeticException("Algorithm roulette incorrect.");
    }
}
