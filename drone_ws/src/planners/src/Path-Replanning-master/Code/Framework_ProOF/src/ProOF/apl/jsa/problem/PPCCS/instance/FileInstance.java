/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ProOF.apl.jsa.problem.PPCCS.instance;

import ProOF.apl.jsa.problem.PPCCS.util.ConfigSimulation;
import ProOF.apl.jsa.problem.PPCCS.structure.TypeOfCriticalSituation;
import ProOF.apl.jsa.problem.PPCCS.util.CreateGif;
import ProOF.com.runner.Runner;
import ProOF.utilities.uIO;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 *
 * @author jesimar
 */
public class FileInstance {
    
    private InstanceProblem inst;
    
    public FileInstance(){
        
    }  
    
    public void setInstance(InstanceProblem instance){
        this.inst = instance;
    }
    
    public void load() throws FileNotFoundException, IOException {
        loadConfig();
//        loadConfigMarcelo();
//        loadConfigPierre();
        if (inst.ONLINE){ 
            loadConfigOnline();
        }
              
        inst.plot.adjustRange1();
        
//        loadMap();
//        loadMapMultiplasInstances();
        loadMapMultiplasInstances2();
        
        inst.plot.adjustRange2();      
        
        for(int l=0; l<inst.L; l++){   
            for(int j=0; j < inst.bji[l].length; j++){
                inst.aji[l][j] = new double[inst.poly[l][j].length][4];
                inst.bji[l][j] = new double[inst.poly[l][j].length];

                for(int i=0; i<inst.poly[l][j].length; i++){
                    int k = (i+1) % inst.poly[l][j].length;
                    double x1 = inst.poly[l][j][i].getX();
                    double y1 = inst.poly[l][j][i].getY();
                    double x2 = inst.poly[l][j][k].getX();
                    double y2 = inst.poly[l][j][k].getY();
                    
                    inst.aji[l][j][i][0] = -( y2 - y1 );                    
                    inst.aji[l][j][i][2] = +( x2 - x1 );

                    inst.bji[l][j][i] = inst.aji[l][j][i][0] * x1  + inst.aji[l][j][i][2] * y1;

                    if(ConfigSimulation.PRINT){
                        System.out.printf(Locale.ENGLISH, 
                            "%8.4f * x + %8.4f * y  = %8.4f\n", inst.aji[l][j][i][0], 
                            inst.aji[l][j][i][2], inst.bji[l][j][i]);
                    }
                }
            }
        }
        if(ConfigSimulation.PRINT){
            for(int l=0; l<inst.L; l++){   
                for(int j=0; j<inst.poly[l].length; j++){
                    System.out.println("---------- poly["+j+"]------------");
                    for(int i=0; i<inst.poly[l][j].length; i++){
                        int k = (i+1) % inst.poly[l][j].length;
                        System.out.printf("(%4g,%4g) -> (%4g,%4g)\n", 
                                inst.poly[l][j][i].getX(), inst.poly[l][j][i].getY(), 
                                inst.poly[l][j][k].getX(), inst.poly[l][j][k].getY());
                    }
                }
            }
        }
        
        inst.Rjti = new double[inst.L][][][];
        for (int l = 0; l < inst.L; l++){
            inst.Rjti[l] = new double[inst.bji[l].length][inst.T+1][];
            for(int j=0; j<inst.bji[l].length; j++){
                if(ConfigSimulation.PRINT){
                    System.out.println("---------- Rj["+j+"]------------");
                }
                for(int t=0; t<inst.T+1; t++){
                    inst.Rjti[l][j][t] = new double[inst.bji[l][j].length];
                    for(int i=0; i<inst.bji[l][j].length; i++){
                        //prod = trans(a)*P0*a
//                        double R[][] = jUtil.prod(v.P0, v.aji[l][j][i]);
//                        double prod = 0;
//                        for(int k=0; k<4; k++){
//                            prod += v.aji[l][j][i][k] * R[k][0]; 
//                        }                                                 
                        double prod = 0;
                        for(int k=0; k<4; k++){
                            prod += inst.aji[l][j][i][k] * inst.aji[l][j][i][k]; 
                        } 
                        inst.Rjti[l][j][t][i] = Math.sqrt(2*prod*inst.P0);                        
                        if(t==0){
                            if(ConfigSimulation.PRINT){
                                System.out.printf("%4g ", inst.Rjti[l][j][t][i]);
                            }
                        }
                    }
                }
                if(ConfigSimulation.PRINT){
                    System.out.println();
                }
            }
        }
        inst.plot.plot("Plot 2D",null);
    } 
    
    private void loadConfig() throws FileNotFoundException{
        Scanner in = new Scanner(inst.FILE_CONFIG);
        
        in.nextLine();
        inst.P0 = Double.parseDouble(in.nextLine());
        inst.P0 = inst.P0*inst.P0;
        
        in.nextLine();
        inst.G = Double.parseDouble(in.nextLine());
        
        in.nextLine();
        inst.DT = Integer.parseInt(in.nextLine());
        
        in.nextLine();
        for(int i=0; i<4; i++){
            inst.X0[i] = Double.parseDouble(in.next());
        }
        in.nextLine();
               
        in.nextLine();
        inst.V_MAX = Double.parseDouble(in.nextLine());
        
        in.nextLine();
        inst.V_MIN = Double.parseDouble(in.nextLine());
        
        in.nextLine();
        inst.LEME_MAX = Double.parseDouble(in.nextLine());
        
        in.nextLine();
        inst.LEME_MIN = Double.parseDouble(in.nextLine());
                
        in.nextLine();
        inst.A_MAX = Double.parseDouble(in.nextLine());                                
        
        in.nextLine();
        inst.T = Integer.parseInt(in.nextLine());
        inst.T = (int)(inst.T/inst.DT);
        
        in.nextLine();
        inst.WIDTH = Integer.parseInt(in.next());
        inst.HEIGTH = Integer.parseInt(in.next());
        in.nextLine();        
        
        in.nextLine();
        inst.MODE_COORDENATE = Boolean.parseBoolean(in.nextLine());
        
        in.nextLine();
        inst.DELTA = Double.parseDouble(in.nextLine());
        
        in.nextLine();
        inst.TYPE_OF_FAILURE = TypeOfCriticalSituation.getCriticalSituation(Integer.parseInt(in.nextLine()));
        
        in.nextLine();
        inst.ONLINE = Boolean.parseBoolean(in.nextLine());
                
        in.close();
    }
    
    //Configuração usada pelo pierre
    private void loadConfigPierre() throws FileNotFoundException{
        Scanner in = new Scanner(new File("./config/config-static.txt"));
        
        in.nextLine();        
        inst.P0 = Double.parseDouble(in.nextLine());
        inst.P0 = inst.P0*inst.P0;
        
        in.nextLine();        
        inst.G = Double.parseDouble(in.nextLine());
        
        in.nextLine();
        inst.DT = Integer.parseInt(in.nextLine());
        
        in.nextLine();
        for(int i=0; i<2; i++){
            inst.X0[i] = Double.parseDouble(in.next());
        }
        in.nextLine();
               
        in.nextLine();
        inst.V_MAX = Double.parseDouble(in.nextLine());
        
        in.nextLine();
        inst.V_MIN = Double.parseDouble(in.nextLine());
        
        in.nextLine();
        inst.LEME_MAX = Double.parseDouble(in.nextLine());
        
        in.nextLine();
        inst.LEME_MIN = Double.parseDouble(in.nextLine());
                
        in.nextLine();
        inst.A_MAX = Double.parseDouble(in.nextLine());       
        
        in.nextLine();
        inst.DELTA = Double.parseDouble(in.nextLine());        
                
        in.nextLine();
        inst.T = Integer.parseInt(in.nextLine());
        inst.T = (int)(inst.T/inst.DT);
        
        in.nextLine();
        inst.TYPE_OF_FAILURE = TypeOfCriticalSituation.getCriticalSituation(Integer.parseInt(in.nextLine()));
        
        in.close();
        
        Scanner in2 = new Scanner(new File("./config/vel-angle.txt"));
        in2.nextLine();
        inst.X0[2] = Double.parseDouble(in2.next());
        inst.X0[3] = Double.parseDouble(in2.next());        
        
        in2.close();
    }
    
    //Configuração usada pelo marcelo
    private void loadConfigMarcelo() throws FileNotFoundException{
        Scanner in = new Scanner(new File("./in/fgfs/config-static.txt"));
        
        in.nextLine();
        inst.P0 = Double.parseDouble(in.nextLine());
        inst.P0 = inst.P0*inst.P0;
        
        in.nextLine();        
        inst.G = Double.parseDouble(in.nextLine());
        
        in.nextLine();
        inst.DT = Integer.parseInt(in.nextLine());        
               
        in.nextLine();
        inst.V_MAX = Double.parseDouble(in.nextLine());
        
        in.nextLine();
        inst.V_MIN = Double.parseDouble(in.nextLine());
        
        in.nextLine();
        inst.LEME_MAX = Double.parseDouble(in.nextLine());
        
        in.nextLine();
        inst.LEME_MIN = Double.parseDouble(in.nextLine());
                
        in.nextLine();
        inst.A_MAX = Double.parseDouble(in.nextLine());       
        
        in.nextLine();
        inst.DELTA = Double.parseDouble(in.nextLine());        
                
        in.nextLine();
        inst.T = Integer.parseInt(in.nextLine());
        inst.T = (int)(inst.T/inst.DT);
        
        in.nextLine();
        inst.TYPE_OF_FAILURE = TypeOfCriticalSituation.getCriticalSituation(Integer.parseInt(in.nextLine()));
        
        in.close();
        
        Scanner in2 = new Scanner(new File("./in/fgfs/config-dynamic.txt"));
        in2.nextLine();
        for(int i=0; i<4; i++){
            inst.X0[i] = Double.parseDouble(in2.next());
        }
        
        in2.close();
    }
    
    private void loadConfigOnline() throws FileNotFoundException{
        File fileOnline = new File("./fail.sgl");
        Scanner in = new Scanner(fileOnline);
        in.nextLine();
        inst.NUMBER_OF_EXECUTION = Integer.parseInt(in.nextLine());//Antigo ID  
        inst.TYPE_OF_FAILURE = TypeOfCriticalSituation.getCriticalSituation(Integer.parseInt(in.nextLine()));
        inst.X0[0] = Double.parseDouble(in.nextLine());//positionX
        inst.X0[1] = Double.parseDouble(in.nextLine());//positionY
        inst.T = Integer.parseInt(in.nextLine());
        inst.DT = Double.parseDouble(in.nextLine());            
        inst.X0[2] = Double.parseDouble(in.nextLine());//Velocidade
        inst.X0[3] = Double.parseDouble(in.nextLine());//Angulo
        inst.DELTA = Double.parseDouble(in.nextLine());
        in.close();        
    }
    
    public static String[] toVectorString(String line) {
        String[] sa = line.split(",");
        String[] result = new String[sa.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = sa[i];
        }
        return result;
    }
    
    private void loadMap() throws FileNotFoundException{
        Scanner sc = new Scanner(inst.FILE_MAP);
        sc.nextLine();
        inst.L = Integer.parseInt(sc.nextLine());
        sc.nextLine();
        inst.CUST_REGIONS = uIO.toVectorDouble(sc.nextLine());
        sc.nextLine();
        inst.NAME_REGIONS = toVectorString(sc.nextLine());
        
        inst.poly = new Point2D[inst.L][][];
        inst.aji = new double[inst.L][][][];
        inst.bji = new double[inst.L][][];
        for(int l=0; l<inst.L; l++){
            sc.nextLine();
            int J = Integer.parseInt(sc.nextLine());            
            inst.poly[l] = new Point2D[J][];
            inst.aji[l] = new double[J][][];
            inst.bji[l] = new double[J][];
            for(int j=0; j<J; j++){
                sc.nextLine();
                double x[] = uIO.toVectorDouble(sc.nextLine());
                double y[] = uIO.toVectorDouble(sc.nextLine());
                inst.poly[l][j] = new Point2D[x.length];
                for(int i=0; i<x.length; i++){
                    inst.poly[l][j][i] = new Point2D.Double(x[i], y[i]);
                    inst.plot.adjustRange3(x[i], y[i]);
                }
            }
        }        
        sc.close();
    }
    
    //Leitura dos mapas gerados automaticamente
    private void loadMapMultiplasInstances() throws FileNotFoundException{
        Scanner sc = new Scanner(inst.FILE_MAP);        
        inst.L = ConfigSimulation.AMOUNT_REGION;
        inst.CUST_REGIONS = new double[inst.L]; 
        inst.CUST_REGIONS[0] = ConfigSimulation.CUST_REGION_N;
        inst.CUST_REGIONS[1] = ConfigSimulation.CUST_REGION_P;
        inst.CUST_REGIONS[2] = ConfigSimulation.CUST_REGION_B;
        
        int sizeRegion[] = new int[inst.L];
        sizeRegion[0] = ConfigSimulation.AMOUNT_REGION_N;
        sizeRegion[1] = ConfigSimulation.AMOUNT_REGION_P;
        sizeRegion[2] = ConfigSimulation.AMOUNT_REGION_B;
        
        inst.NAME_REGIONS = new String[inst.L];
        inst.NAME_REGIONS[0] = "n";
        inst.NAME_REGIONS[1] = "p";
        inst.NAME_REGIONS[2] = "b";
        
        inst.poly = new Point2D[inst.L][][];
        inst.aji = new double[inst.L][][][];
        inst.bji = new double[inst.L][][];
        
        sc.nextLine();
        int N = Integer.parseInt(sc.nextLine());
        
        for(int l=0; l<inst.L; l++){         
            int size = sizeRegion[l];
            inst.poly[l] = new Point2D[size][];
            inst.aji[l] = new double[size][][];
            inst.bji[l] = new double[size][];
            for(int j=0; j<size; j++){
                sc.nextLine();
                double x[] = uIO.toVectorDouble(sc.nextLine());
                double y[] = uIO.toVectorDouble(sc.nextLine());
                inst.poly[l][j] = new Point2D[x.length];
                for(int i=0; i<x.length; i++){
                    inst.poly[l][j][i] = new Point2D.Double(x[i], y[i]);
                    inst.plot.adjustRange3(x[i], y[i]);
                }
            }
        }
        sc.close();
    }
    
    private void loadMapMultiplasInstances2() throws FileNotFoundException{
        Scanner sc = new Scanner(inst.FILE_MAP);        
        inst.L = ConfigSimulation.AMOUNT_REGION;
        inst.CUST_REGIONS = new double[inst.L]; 
        inst.CUST_REGIONS[0] = ConfigSimulation.CUST_REGION_N;
        inst.CUST_REGIONS[1] = ConfigSimulation.CUST_REGION_P;
        inst.CUST_REGIONS[2] = ConfigSimulation.CUST_REGION_B;                
        
        inst.NAME_REGIONS = new String[inst.L];
        inst.NAME_REGIONS[0] = "n";
        inst.NAME_REGIONS[1] = "p";
        inst.NAME_REGIONS[2] = "b";
        
        inst.poly = new Point2D[inst.L][][];
        inst.aji = new double[inst.L][][][];
        inst.bji = new double[inst.L][][];
        inst.polygon = new Polygon[inst.L][];
        
        sc.nextLine();
        int N = Integer.parseInt(sc.nextLine());
        
        inst.SIZE_REGIONS = new int[inst.L];
        
        sc.nextLine();        
        inst.SIZE_REGIONS[0] = Integer.parseInt(sc.nextLine());
        sc.nextLine();        
        inst.SIZE_REGIONS[1] = Integer.parseInt(sc.nextLine());
        sc.nextLine();        
        inst.SIZE_REGIONS[2] = Integer.parseInt(sc.nextLine());
        
        for(int l=0; l<inst.L; l++){         
            int sizeR = inst.SIZE_REGIONS[l];
            inst.poly[l] = new Point2D[sizeR][];
            inst.aji[l] = new double[sizeR][][];
            inst.bji[l] = new double[sizeR][];
            inst.polygon[l] = new Polygon[sizeR];
            for(int j=0; j<sizeR; j++){
                sc.nextLine();
                double x[] = uIO.toVectorDouble(sc.nextLine());
                double y[] = uIO.toVectorDouble(sc.nextLine());
                inst.poly[l][j] = new Point2D[x.length];
                
                int pointX[] = new int[x.length];
                int pointY[] = new int[y.length];
                for (int w = 0; w < x.length; w++){
                    pointX[w] = (int)(1*x[w]);//houve perda de informação (2 casas decimais)
                    pointY[w] = (int)(1*y[w]);//houve perda de informação (2 casas decimais)
                }
                
                inst.polygon[l][j] = new Polygon(pointX, pointY, pointX.length);
                for(int i=0; i<x.length; i++){
                    inst.poly[l][j][i] = new Point2D.Double(x[i], y[i]);
                    inst.plot.adjustRange3(x[i], y[i]);
                }
            }
        }
        sc.close();
    }        
    
    public void save(){
        save("", false, "");
    }
    
    public void save(String nameRand){
        save("", true, nameRand);
    }
    
    public void save(String name, boolean nameRandom, String rand){
        BufferedImage img = new BufferedImage(inst.WIDTH, inst.HEIGTH, 
                BufferedImage.TYPE_3BYTE_BGR);
        Graphics g = img.getGraphics();                
        try {
            if(inst.MODE_COORDENATE){
                inst.plot.paintGeographic(g);
            }else{
                inst.plot.paintCartesian(g);
            }
            if (Runner.LOCAL){
                String nameFile;
                if (nameRandom){
                    nameFile = "./sol_"+rand+".png";
                }else{
                    nameFile = "./sol_"+inst.NUMBER_OF_EXECUTION+".png";
                }
                ImageIO.write(img, "png", new File(nameFile));
            } else {
                String nameFile = ConfigSimulation.PATH_IMG + "sol_" + name + 
                        inst.FILE_CONFIG.getName() + "_"+ inst.job.getName()+".png";
                ImageIO.write(img, "png", new File(nameFile));
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(inst.plot.getFrame(), ex.getCause(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
    }
    
    private final LinkedList<BufferedImage> bufferGif = new LinkedList<BufferedImage>();

    public void saveBufferGif() {        
        bufferGif.add(new BufferedImage(inst.WIDTH, inst.HEIGTH, BufferedImage.TYPE_3BYTE_BGR));
        Graphics g = bufferGif.getLast().getGraphics();
        inst.plot.paintCartesian(g);
    }
    
    public void saveGif(String name) {
        try {
            if (Runner.LOCAL) {
                CreateGif gif = new CreateGif();
                BufferedImage imgBuffer[] = bufferGif.toArray(new BufferedImage[bufferGif.size()]);
                gif.createGif(imgBuffer, new File(name + ".gif"), 50, 1);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
