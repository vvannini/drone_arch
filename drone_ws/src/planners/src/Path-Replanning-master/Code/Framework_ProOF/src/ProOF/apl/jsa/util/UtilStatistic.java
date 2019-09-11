/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ProOF.apl.jsa.util;

import jsc.distributions.Normal;

/**
 *
 * @author jesimar
 */
public class UtilStatistic {
    
    /*Calcula a média do vetor dado*/
    public static double average(double vector[]){
        double average = 0;        
        for (int i = 0; i < vector.length; i++) {
            average += vector[i];
        }
        return average/vector.length;
    }
    
    public static double erf_inv(double z){
        return Normal.inverseStandardCdf((1+z)/2.0)/Math.sqrt(2.0);
    }
    
    public static void sort(double vetor[]){
        for (int i = 0; i < vetor.length; i++){
            for (int j = i+1; j < vetor.length; j++){
                if (vetor[i] > vetor[j]){
                    double aux = vetor[j];
                    vetor[j] = vetor[i];
                    vetor[i] = aux;
                }
            }
        }
    }
    
    public static double valueMediumVetor(double vetor[]){
        sort(vetor);
        return vetor[vetor.length/2 - 1];
    }
    
    public static int valueDivisorBimodalVetor(double vetor[]){
        sort(vetor);
        int value = 0;
        for (int i = 0; i < vetor.length; i++){
            value += vetor[i] < 0.0 ? 1 : 0;
        }
        return value < vetor.length ? value : vetor.length - 1;
    }
}
