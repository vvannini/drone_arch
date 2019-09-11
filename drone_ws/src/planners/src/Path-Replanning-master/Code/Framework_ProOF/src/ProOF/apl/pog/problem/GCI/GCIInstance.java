/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.GCI;

import ProOF.com.Linker.LinkerParameters;
import ProOF.opt.abst.problem.Instance;
import ProOF.utilities.uIO;
import java.io.File;
import java.util.Scanner;

/**
 *
 * @author marcio
 */
public class GCIInstance extends Instance{
    public final double W =1;
    
    private File file;
    
    /**number of products*/
    public int N;
    /**number of periods*/
    public int T;
    /**number of machines*/
    public int K;
    /**melting capacity of the furnace in a period (tons).*/
    public double C;
    /**demand for product i at the end of period t (expressed in tons)*/
    public double Dit[][];
    /**the maximum number of mold cavities of machine k in which product i can be produced*/
    public double MAXik[][];
    /**the minimum number of mold cavities of machine k in which product i can be produced*/
    public double MINik[][];
    /**quantity of product i produced per mold cavity of machine k in a period (tons)*/
    public double Pik[][];
    /**setup time of a changeover from product i to product j, j != i on machine k(tons)*/
    public double Sijk[][][];
    /**cost incurred to set up machine k from product i to product j, j != i*/
    public double Cijk[][][];
    /**holding cost of carrying one ton of product i from one period to the next*/
    public double Hi[];

    public double Y0ik[][];
    
    //--------------------dados preprocessados utilizados pelos modelos-------------
    /**cumulative demand for product i to the period prior to t*/
    public double AcDit[][];
    
    /**indices of products that can be produced on the machine k*/
    public int Productkn[][];
    
    /**penalidade extra da troca do produto i para o produto j no perido t e maquina k*/
    public double PEijtk[][][][];
    
    /**Custo de ativacao de cavidades utilizado pelo modelo SoverInt2*/
    public double Bit[][];
    
    public double optimal;

    @Override
    public String name() {
        return "Instance-GCI";
    }

    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void parameters(LinkerParameters com) throws Exception {
        file = com.File("Instances for GCI",null,"dat");
    }

    @Override
    public void load() throws Exception {
        Scanner input = new Scanner(file);
        
        N = uIO.ReadIntOpl(input);
        T = uIO.ReadIntOpl(input);
        K = uIO.ReadIntOpl(input);
        C = uIO.ReadDoubleOpl(input);


        MINik   = uIO.ReadMatrixDoubleOpl(input);
        MAXik   = uIO.ReadMatrixDoubleOpl(input);

        Pik     = uIO.ReadMatrixDoubleOpl(input);
        Dit     = uIO.ReadMatrixDoubleOpl(input);

        Sijk = uIO.ReadCubeDoubleOpl(input);
        Cijk = uIO.ReadCubeDoubleOpl(input);

        Hi = uIO.ReadVectorDoubleOpl(input);

        
        try{
            optimal = uIO.ReadDoubleOpl(input);
        }catch(Exception ex){
            optimal = Double.NaN;
        }
        
        Y0ik = new double[N][K];
        for(int k=0; k<K; k++){
            Y0ik[0][k] = 1;
        }

        AcDit = new double[N][T+1];
        for(int i=0; i<N; i++){
            AcDit[i][0] = 0;
            for(int t=1; t<T+1; t++){
                AcDit[i][t] = AcDit[i][t-1] + Dit[i][t-1];
            }
        }

        Productkn = new int[K][];
        for(int k=0; k<K; k++){
            int cont = 0;
            for(int i=0; i<N; i++){
                if(MAXik[i][k] >= MINik[i][k]){
                    cont++;
                }
            }
            Productkn[k] = new int[cont];
            cont = 0;
            for(int i=0; i<N; i++){
                if(MAXik[i][k] >= MINik[i][k]){
                    Productkn[k][cont] = i;
                    cont++;
                }
            }
        }

        PEijtk = new double[N][N][T][K];
        for(int i=0; i<N; i++){
            for(int j=0; j<N; j++){
                for(int t=0; t<T; t++){
                    for(int k=0; k<K; k++){
                        PEijtk[i][j][t][k] = Hi[j] * Sijk[i][j][k] * (T - t); 
                    }
                }
            }
        }
        Bit = new double[N][T];
        for(int i=0; i<N; i++){
            for(int t=0; t<T; t++){
                Bit[i][t] = Hi[i]*(T-t) - W;
            }
        }
    }
}
