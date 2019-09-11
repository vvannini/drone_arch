/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.RA.model;

import ProOF.com.Linker.LinkerParameters;
import ProOF.opt.abst.problem.Instance;
import ProOF.utilities.uIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author marcio
 */
public class RAInstance extends Instance {

    public static final RAInstance obj = new RAInstance();
    private File file;
    public int N, T, LMi[], Dit[][], Iio[], S, Bf;
    public double Pi[], Hi[], Bi[], COt[], STij[][], Ut[], Ct[];
    public int Ord[];

    @Override
    public String name() {
        return "Instance PSSLSAN";
    }

    @Override
    public String description() {
        return "";
    }

    @Override
    public void parameters(LinkerParameters win) throws Exception {
        file = win.File("PSSLSAN instance", null, "inst");
    }

    @Override
    public void load() throws FileNotFoundException {
        
        Scanner input = new Scanner(file);
        N = uIO.ReadInt(input);
        T = uIO.ReadInt(input);
        Ct = uIO.ReadVectorDouble(input);
        Pi = uIO.ReadVectorDouble(input);
        LMi = uIO.ReadVectorInt(input);
        Hi = uIO.ReadVectorDouble(input);
        Bf = uIO.ReadInt(input);
        COt = uIO.ReadVectorDouble(input);
        STij = uIO.ReadMatrixDouble(input);
        Dit = uIO.ReadMatrixInt(input);
        Iio = uIO.ReadVectorInt(input);
        Ut = uIO.ReadVectorDouble(input);
        
        //N = 6;
        
        Bi = new double[N];
        for (int i = 0; i < N; i++) {
            Bi[i] = Hi[i] * Bf;
        }
        input.close();

        S = N;//*8/10;

        Ord = new int[N];
        for (int i = 0; i < N; i++) {
            Ord[i] = i;
        }
        for (int i = N - 1; i >= 0; i--) {
            for (int j = 1; j <= i; j++) {
                if (Pi[Ord[j]] < Pi[Ord[j - 1]]) {
                    int aux = Ord[j];
                    Ord[j] = Ord[j - 1];
                    Ord[j - 1] = aux;
                }
            }
        }
        
        
        /*for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if(i!=j){
                    STij[i][j] = 10 + Math.random() * 1000;
                }
            }
        }*/
    }
}
