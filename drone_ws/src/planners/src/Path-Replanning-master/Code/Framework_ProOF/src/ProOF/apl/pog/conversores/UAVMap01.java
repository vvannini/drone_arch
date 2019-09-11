/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.conversores;

import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Run;
import ProOF.utilities.uIO;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.Formatter;
import java.util.Locale;
import java.util.Scanner;

/**
 *
 * @author marcio
 */
public class UAVMap01 extends Run{
    private File file_in;
    private File file_out;
    
    @Override
    public String name() {
        return "Sgl to Txt";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void services(LinkerApproaches link) throws Exception {
        
    }
    @Override
    public void parameters(LinkerParameters link) throws Exception {
        file_in = link.File("Map for PPDCP",null,"sgl");
    }

    @Override
    public void load() throws Exception {
        
    }
    @Override
    public void start() throws Exception {
        file_out = new File("./"+file_in.getName().replace(".sgl", ".txt"));
    }
    @Override
    public void execute() throws Exception {
        Scanner sc = new Scanner(file_in);
        sc.nextLine();
        int J = Integer.parseInt(sc.nextLine());
        Point2D points[][] = new Point2D[J][];
        for(int j=0; j<J; j++){
            sc.nextLine();
            double x[] = uIO.toVectorDouble(sc.nextLine());
            double y[] = uIO.toVectorDouble(sc.nextLine());
            points[j] = new Point2D[x.length];
            for(int i=0; i<x.length; i++){
                points[j][i] = new Point2D.Double(x[i], y[i]);
            }
        }
        sc.close();
        
        Formatter out = new Formatter(file_out);
        out.format("%d\n", J);
        for(int j=0; j<J; j++){
            out.format("%d\n", points[j].length);
            for(int i=0; i<points[j].length; i++){
                out.format(Locale.ENGLISH, "%g %g ", points[j][i].getX(), points[j][i].getY());
            }
            out.format("\n");
        }
        out.close();
    }
    @Override
    public boolean validation(LinkerValidations link) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void results(LinkerResults link) throws Exception {
        link.writeFile("outTXT", file_out);
    }

    
    
}
