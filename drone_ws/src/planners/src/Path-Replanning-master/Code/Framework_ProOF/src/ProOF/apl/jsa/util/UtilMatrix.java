/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ProOF.apl.jsa.util;

/**
 *
 * @author jesimar
 */
public class UtilMatrix {
    
    /*Calcula o maior valor do vetor e o retorna*/
    public static double maximum(double vector[]){
        double maximum = vector[0];
        for (int i = 1; i < vector.length; i++) {
            if (maximum < vector[i]){
                maximum = vector[i];
            }
        }        
        return maximum;
    }
    
    /*Calcula o menor valor do vetor e o retorna*/
    public static double minimum(double vector[]){
        double minimum = vector[0];
        for (int i = 1; i < vector.length; i++) {
            if (minimum > vector[i]){
                minimum = vector[i];
            }
        }
        return minimum;
    }
    
    /*Calcula a soma de duas matrizes*/
    public static double[][] sum(double A[][], double B[][]){
        double R[][] = new double[A.length][B[0].length];
        for(int i=0; i<R.length; i++){
            for(int j=0; j<R[i].length; j++){
                R[i][j] = A[i][j] + B[i][j];
            }
        }
        return R;
    }
    
    /*Calcula o produto de uma matriz por um vetor*/
    public static double[][] prod(double A[][], double B[]){
        double X[][] = new double[B.length][1];
        for(int i=0; i<B.length; i++){
            X[i][0] = B[i];
        }
        return prod(A, X);
    }
    
    /*Calcula o produto de uma matriz por um numero real*/
    public static double[][] prod(double A[][], double a){
        double R[][] = new double[A.length][A[0].length];
        for(int i=0; i<R.length; i++){
            for(int j=0; j<R[i].length; j++){
                R[i][j] = A[i][j]*a;
            }
        }
        return R;
    }
    
    /*Calcula o produto de duas matrizes*/
    public static double[][] prod(double A[][], double B[][]){
        double R[][] = new double[A.length][B[0].length];
        for(int i=0; i<R.length; i++){
            for(int j=0; j<R[i].length; j++){
                R[i][j] = 0;
                for(int k=0; k<B.length; k++){
                    R[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return R;
    }
    
    /*Calcula o produto de três matrizes*/
    public static double[][] prod(double A[][], double B[][], double C[][]){
        return prod(prod(A,B),C);
    }
    
    /*Calcula o produto de quatro matrizes*/
    public static double[][] prod(double A[][], double B[][], double C[][], double D[][]){
        return prod(prod(prod(A,B),C),D);
    }        
    
    /*Calcula a transposta de uma matriz A qualquer*/
    public static double [][] trans(double matrixA[][]){
        double matrixR[][] = new double[matrixA[0].length][matrixA.length];
        for(int i=0; i<matrixR.length; i++){
            for(int j=0; j<matrixR[i].length; j++){
                matrixR[i][j] = matrixA[j][i];
            }
        }
        return matrixR;
    }
    
    public static double[][] pow(double A[][], int n){
        if(n<=0){
            double R[][] =  new double[A.length][A[0].length];
            for(int i=0; i<R.length; i++){
                R[i][i] = 1;
            }
            return R;
        }else{
            double R[][] =  new double[A.length][A[0].length];
            for(int i=0; i<R.length; i++){
                System.arraycopy(A[i], 0, R[i], 0, R[i].length);
            }
            for(int i=1; i<n; i++){
                R = prod(R, A);
            }
            return R;
        }
    }
    
    /*Calcula a norma 1 entre dois vetores quaisquer*/
    public static double norm1(double[] vectorA, double[] vectorB) {
        double norm1 = 0;
        for(int i=0; i<vectorA.length; i++){
            norm1 += Math.abs(vectorA[i]-vectorB[i]);
        }
        return norm1;
    }
    
    /*Calcula a norma 2 entre dois vetores quaisquer*/
    public static double norm2(double[] vetorA, double[] vectorB) {
        double norm2 = 0;
        for(int i=0; i<vetorA.length; i++){
            norm2 += (vetorA[i]-vectorB[i])*(vetorA[i]-vectorB[i]);
        }
        return Math.sqrt(norm2);
    }
}
