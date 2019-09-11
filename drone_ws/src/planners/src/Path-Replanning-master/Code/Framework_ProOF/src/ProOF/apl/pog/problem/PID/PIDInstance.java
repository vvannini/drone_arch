/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.PID;

import ProOF.com.Linker.LinkerParameters;
import ProOF.opt.abst.problem.Instance;
import ProOF.utilities.uIO;
import java.io.File;
import java.util.Scanner;

/**
 *
 * @author marcio
 */
public class PIDInstance extends Instance{
    private File file;
    private double min[];
    private double max[];
    public double weights[];
    public double RNA[][][];
    @Override
    public String name() {
        return "PID-Instance";
    }

    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void parameters(LinkerParameters link) throws Exception {
        file = link.File("Instances for PID",null,"sgl");
    }

    /*
    Valor mínimo de Kc, Ti, Td
        0.18306 0.20943 0.05238
    Valor máximo de Kc, Ti, Td
        1.03598 1.45024 0.36256
    Ponderação entre as redes neurais
        0.5 0.5
    Rede Neural - ITAE
        //matriz com 18 linhas e 19 colunas
    Rede Neural - ITAY
        //matriz com 18 linhas e 19 colunas
     */
    
    @Override
    public void load() throws Exception {
        //file = new File("D:/Dropbox/fInstances/PID/S0R1.sgl");
        Scanner sc = new Scanner(file);
        /*System.out.println("["+file+"]");
        System.out.println("["+file.exists()+"]");
        System.out.println("["+file.isFile()+"]");
        System.out.println("sc ["+sc+"]");
        System.out.println("[begin]");
        
        //while(sc.hasNext()){
            System.out.println("["+sc.next()+"]");
        //}
        System.out.println("[end]");*/
        
        
        min = uIO.ReadVectorDouble(sc);
        max = uIO.ReadVectorDouble(sc);
        weights = uIO.ReadVectorDouble(sc);
        RNA = new double[weights.length][][];
        for(int i=0; i<weights.length; i++){
            RNA[i] = uIO.ReadMatrixDouble(sc);
        }
        sc.close();
    }
    public double decode(int i, double x) {
        return min[i] + x*(max[i]-min[i]);
    }
}
