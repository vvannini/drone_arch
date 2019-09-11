/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ProOF.apl.pog.problem.CCQSP;


/**
 *
 * @author marcio
 */
public class CCQSPDynamic {
    //ATENÇÃO AS MATRIZES ABAIXO SÃO MODIFICADAS COM BASE EM LEITURAS DE ARQUIVOS
    public final double A[][] = new double[][]{
        {1, 0.7869, 0,      0},
        {0, 0.6065, 0,      0},
        {0,      0, 1, 0.7869},
        {0,      0, 0, 0.6065}
    };
    public final double B[][] = new double[][]{
        {0.2131, 0     },
        {0.3935, 0     },
        {     0, 0.2131},
        {     0, 0.3935}
    };
    public final double invB[][] = new double[][]{
        {0.5/B[0][0],   0.5/B[1][0],    0,              0               },
        {0,             0,              0.5/B[2][1],    0.5/B[3][1]     },
    };
    public final double Q[][] = new double[][]{
        {0.003555, 0,      0,      0},
        {0, 0.006320,      0,      0},
        {0,      0, 0.003555,      0},
        {0,      0,      0, 0.006320}
    };
    public final double P0[][] = new double[][]{
        {0.05*0.05,      0,          0,          0},
        {0,     0.005*0.005,          0,          0},
        {0,          0,      0.05*0.05,          0},
        {0,          0,         0,      0.005*0.005}
    };

    
    
    public void start(double DT, double SD) {
        
        for(int i=0; i<4; i++){
            for(int j=0; j<4; j++){
                A[i][j] = 0;
                Q[i][j] = 0;
                P0[i][j] = 0;
            }
            for(int j=0; j<2; j++){
                B[i][j] = 0;
            }
        }
        for(int i=0; i<4; i++){
            Q[i][i] = DT*Math.pow(SD/20, 2);
            P0[i][i] = i%2==0 ? Math.pow(SD, 2) : 0;
        }
        for(int i=0; i<4; i++){
            A[i][i] = 1;
        }
        A[0][1] = DT;
        A[2][3] = DT;
        B[0][0] = DT*DT/2;
        B[1][0] = DT;
        B[2][1] = DT*DT/2;
        B[3][1] = DT;
        //VMAX = VMAX * DT;
        //UMAX = UMAX * DT * DT;
    }
}
