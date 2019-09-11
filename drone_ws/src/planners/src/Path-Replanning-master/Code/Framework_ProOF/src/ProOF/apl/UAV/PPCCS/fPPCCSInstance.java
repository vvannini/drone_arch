/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.PPCCS;

import ProOF.apl.UAV.PPCCS.data.ColorPlot;
import ProOF.apl.UAV.PPCCS.data.TypeOfCriticalSituation;
import ProOF.apl.UAV.PPCCS.util.ConfigSimulation;
import ProOF.apl.UAV.map.Hyperplane;
import ProOF.apl.UAV.map.Obstacle;
import ProOF.apl.UAV.map.Obstacle2D;
import ProOF.apl.UAV.map.Obstacle3DHalf;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.language.Factory;
import ProOF.utilities.uIO;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 *
 * @author marcio e jesimar
 */
public class fPPCCSInstance extends Factory<PPCCSInstance>{
    
    public static final fPPCCSInstance obj = new fPPCCSInstance();
    
    @Override
    public String name() {
        return "Instance Reader";
    }
    
    @Override
    public PPCCSInstance build(int index) throws Exception {
        switch(index){
            case 0 : return new Instance2D();
            case 1 : return new Instance3DhalfHard();
            case 2 : return new Instance3DhalfRmd();
            case 3 : return new Instance3DPPCCS();
            case 4 : return new Instance2_5D_PPCCS();
        }
        return null;
    }

    private static class Instance2D extends PPCCSInstance {
        private File configFile;
        private File mapFile;
        
        private String map_name;
        private final Font font = new Font(Font.SANS_SERIF, Font.BOLD, 24);

        @Override
        public String name() {
            return "Instance 2D";
        }
        
        @Override
        public void parameters(LinkerParameters link) throws Exception {
            configFile = link.File("Configuration for PPCCS 2D", null, "sgl");
            mapFile = link.File("Map for PPCCS 2D", null, "sgl");
        }
        
        @Override
        public int N() {
            return 2;
        }
        
        @Override
        public void load() throws FileNotFoundException, Exception {
            loadConfig();
            loadMap();
        }
        
        private void loadConfig() throws FileNotFoundException{
            Scanner sc = new Scanner(configFile);
            start_state = uIO.ReadVectorDouble(sc);
            sc.close();
        }
        
        private void loadMap() throws FileNotFoundException{
            Scanner sc = new Scanner(mapFile);
            int J = uIO.ReadInt(sc);    //number of obstacles
            obstacles = new Obstacle[J];
            for(int j=0; j<J; j++){
                obstacles[j] = new Obstacle2D(""+j, font, sc);
            }
            map_name = uIO.ReadTrueName(sc);
            sc.close();
        }

        @Override
        public String map_mame() {
            return map_name;
        }
        
        @Override
        public void results(LinkerResults link) throws Exception {
            super.results(link);
            link.writeString("map_name", map_name);
        }
    }
    
    private static class Instance3DhalfHard extends PPCCSInstance {
        private File configFile;
        private File mapFile;
        
        private String map_name;
        private final Font font = new Font(Font.SANS_SERIF, Font.BOLD, 24);

        @Override
        public String name() {
            return "Instance 3D half-hard";
        }
        @Override
        public void parameters(LinkerParameters link) throws Exception {
            configFile = link.File("Configuration for PPCCS 3D", null, "sgl");
            mapFile = link.File("Map for PPCCS 3D half", null, "sgl");
        }
        @Override
        public int N() {
            return 3;
        }
        @Override
        public void load() throws FileNotFoundException, Exception {
            loadConfig();
            loadMap();
        }
        private void loadConfig() throws FileNotFoundException{
            Scanner sc = new Scanner(configFile);
            start_state = uIO.ReadVectorDouble(sc);
            sc.close();
        }
        private static int h(int J){
            if(J<=6){
                return 3; 
            }else{
                return J/2-3 + h(J-2);
            }
        }
        public static void main(String args[]){
            for(int i=6; i<15; i+=2){
                System.out.printf("%d = %d\n", i, h(i));
            }
        }
        
        private void loadMap() throws FileNotFoundException{
            Scanner sc = new Scanner(mapFile);
            int J = uIO.ReadInt(sc);    //number of obstacles
            obstacles = new Obstacle[J+1];
            for(int j=0; j<J; j++){
                obstacles[j] = new Obstacle3DHalf(h(J), 2.0, 0.0, -5.0, font, sc);//3 4 6 9 13
            }
            map_name = uIO.ReadTrueNameWithOutExt(sc, "sgl");
            sc.close();
            obstacles[J] = new Obstacle3DHalf(0, "ground", font);    //ground
        }
        @Override
        public String map_mame() {
            return map_name;
        }
        @Override
        public void results(LinkerResults link) throws Exception {
            super.results(link);
            link.writeString("map_name", map_name);
        }
    }
    
    private static class Instance3DhalfRmd extends PPCCSInstance {
        private File configFile;
        private File mapFile;
        private String map_name;
        private final Font font = new Font(Font.SANS_SERIF, Font.BOLD, 24);

        @Override
        public String name() {
            return "Instance 3D half-rmd";
        }
        
        @Override
        public void parameters(LinkerParameters link) throws Exception {
            configFile = link.File("Configuration for PPCCS 3D", null, "sgl");
            mapFile = link.File("Map for PPCCS 3D half", null, "sgl");
        }
        
        @Override
        public int N() {
            return 3;
        }
        
        @Override
        public void load() throws FileNotFoundException, Exception {
            loadConfig();
            loadMap();
        }
        
        private void loadConfig() throws FileNotFoundException{
            Scanner sc = new Scanner(configFile);
            start_state = uIO.ReadVectorDouble(sc);
            sc.close();
        }
        
        private void loadMap() throws FileNotFoundException{
            Scanner sc = new Scanner(mapFile);
            int J = uIO.ReadInt(sc);    //number of obstacles
            obstacles = new Obstacle[J+1];
            for(int j=0; j<J; j++){
                obstacles[j] = new Obstacle3DHalf((j+1)*10.0/J, 2.0, 0.0, -5.0, font, sc);
            }
            map_name = uIO.ReadTrueNameWithOutExt(sc, "sgl");
            sc.close();
            obstacles[J] = new Obstacle3DHalf(0, "ground", font);    //ground
        }
        
        @Override
        public String map_mame() {
            return map_name;
        }
        
        @Override
        public void results(LinkerResults link) throws Exception {
            super.results(link);
            link.writeString("map_name", map_name);
        }        
    }
    
    private static class Instance2_5D_PPCCS extends PPCCSInstance {
        private File configFile;
        private File mapFile;
        private String map_name;
        private final Font font = new Font(Font.SANS_SERIF, Font.BOLD, 1800);

        @Override
        public String name() {
            return "Instance 2.5D PPCCS";
        }
        
        @Override
        public void parameters(LinkerParameters link) throws Exception {
            configFile = link.File("Configuration for PPCCS 3D", null, "sgl");
            mapFile = link.File("Map for PPCCS 3D half", null, "sgl");
        }
        
        @Override
        public int N() {
            return 3;
        }
        
        @Override
        public void load() throws FileNotFoundException, Exception {
            loadConfig();
            loadMap();
        }
        
        private void loadConfig() throws FileNotFoundException{
            Scanner sc = new Scanner(configFile);
            start_state = uIO.ReadVectorDouble(sc);
            sc.nextLine();
            TYPE_OF_FAILURE = TypeOfCriticalSituation.getCriticalSituation(Integer.parseInt(sc.nextLine()));
            sc.close();
        }
        
        private void loadMap() throws FileNotFoundException{
            Scanner sc = new Scanner(mapFile);
            int J = 1+uIO.ReadInt(sc);        //number of obstacles
            map.sizeN = 1+uIO.ReadInt(sc);    //number of no-fly regions
            map.sizeP = uIO.ReadInt(sc);    //number of penalty regions 
            map.sizeB = uIO.ReadInt(sc);    //number of bonus regions
            obstacles = new Obstacle[J];           
            int h = 5;
            Oj = new Integer[J][];            
            Φn = new Integer[map.sizeN];
            Φp = new Integer[map.sizeP];
            Φb = new Integer[map.sizeB];
            for(int j=0; j<J; j++){                
                if (j == 0){
                    obstacles[j] = new Obstacle3DHalf(0.0, "ground", font);
                    Φn[j] = j;
                }else if (j < map.sizeN){
                    obstacles[j] = new Obstacle3DHalf(ColorPlot.ZONE_N, "n" + (j) + " [∞]", font, sc, 0);
                    Φn[j] = j;
                }else if (j >= map.sizeN && j < map.sizeN + map.sizeP){
                    obstacles[j] = new Obstacle3DHalf(ColorPlot.ZONE_P, 
                            "p" + (j-map.sizeN) + " [" + h + "]", font, sc, h);
                    h = h % 25 + 5;
                    Φp[j-map.sizeN] = j;
                }else {
                    obstacles[j] = new Obstacle3DHalf(0, ColorPlot.ZONE_B, 
                            "b" + (j- map.sizeN - map.sizeP) + " [" + 0 + "]", font, sc);
                    Φb[j-map.sizeN-map.sizeP] = j;
                }
                
                LinkedList<Integer> setIndexO = new  LinkedList<Integer>();
                for (int i = 0; i < obstacles[j].hyperplans.length; i++){
                    addHyper(obstacles[j].hyperplans[i], setIndexO);
                }
                Oj[j] = setIndexO.toArray(new Integer[setIndexO.size()]);
            }
            map_name = uIO.ReadTrueNameWithOutExt(sc, "sgl");
            sc.close();
            hi = listHyper.toArray(new Hyperplane[listHyper.size()]);
        }
        
        @Override
        public String map_mame() {
            return map_name;
        }
        
        @Override
        public void results(LinkerResults link) throws Exception {
            super.results(link);
            link.writeString("map_name", map_name);
            if (ConfigSimulation.DEBUG_INSTANCE){
                System.out.println("|Φn|: " + map.sizeN);
                System.out.println("|Φp|: " + map.sizeP);
                System.out.println("|Φb|: " + map.sizeB);                
                System.out.print("Φn: ");
                for (int i = 0; i < Φn.length; i++){
                    System.out.print(Φn[i] + " ");
                }
                System.out.println();
                
                System.out.print("Φp: ");
                for (int i = 0; i < Φp.length; i++){
                    System.out.print(Φp[i] + " ");
                }
                System.out.println();
                
                System.out.print("Φb: ");
                for (int i = 0; i < Φb.length; i++){
                    System.out.print(Φb[i] + " ");
                }
                System.out.println();
                
                System.out.println("Oj: ");
                for (int j = 0; j < Oj.length; j++){
                    for (int i = 0; i < Oj[j].length; i++){
                        System.out.print(Oj[j][i] + " ");
                    }
                    System.out.println();
                }
            }
        }        
    }
    
    private static class Instance3DPPCCS extends PPCCSInstance {
        private File configFile;
        private File mapFile;
        private String map_name;
        private final Font font = new Font(Font.SANS_SERIF, Font.BOLD, 1800);

        @Override
        public String name() {
            return "Instance 3D PPCCS";
        }
        
        @Override
        public void parameters(LinkerParameters link) throws Exception {
            configFile = link.File("Configuration for PPCCS 3D", null, "sgl");
            mapFile = link.File("Map for PPCCS 3D half", null, "sgl");
        }
        
        @Override
        public int N() {
            return 3;
        }
        
        @Override
        public void load() throws FileNotFoundException, Exception {
            loadConfig();
            loadMap();
        }
        
        private void loadConfig() throws FileNotFoundException{
            Scanner sc = new Scanner(configFile);
            start_state = uIO.ReadVectorDouble(sc);            
            deltaP = uIO.ReadDouble(sc);
            TYPE_OF_FAILURE = TypeOfCriticalSituation.getCriticalSituation(uIO.ReadInt(sc));
            sc.close();            
            createHyperplans();
        }
        
        private void add(
                LinkedList<Integer[]> indexHyper, 
                LinkedList<Integer> indexObs, 
                LinkedList<Integer> indexZone, 
                LinkedList<Obstacle> listObs, 
                Obstacle toAdd
        ){
            indexObs.addLast(listObs.size());
            indexZone.addLast(listObs.size());
            listObs.addLast(toAdd);
            LinkedList<Integer> setIndexO = new  LinkedList<Integer>();
            for (int i = 0; i < toAdd.hyperplans.length; i++){
                addHyper(toAdd.hyperplans[i], setIndexO);
            }
            indexHyper.addLast(setIndexO.toArray(new Integer[setIndexO.size()]));
        }
        
        private void loadMap() throws FileNotFoundException{
            Scanner sc = new Scanner(mapFile);
            uIO.ReadInt(sc);        //number of obstacles
            map.sizeN = uIO.ReadInt(sc);    //number of no-fly regions
            map.sizeP = uIO.ReadInt(sc);    //number of penalty regions 
            map.sizeB = uIO.ReadInt(sc);    //number of bonus regions
            LinkedList<Obstacle> listObs = new LinkedList<Obstacle>();
            LinkedList<Integer> indexObs = new LinkedList<Integer>();
            LinkedList<Integer[]> indexHyper = new LinkedList<Integer[]>();
            LinkedList<Integer> indexN = new LinkedList<Integer>();
            LinkedList<Integer> indexP = new LinkedList<Integer>();
            LinkedList<Integer> indexB = new LinkedList<Integer>();
            
            add(indexHyper, indexObs, indexN, listObs, 
                    new Obstacle3DHalf(0.0, "ground", font));
            
            for(int j=0; j<map.sizeN; j++){  
                add(indexHyper, indexObs, indexN, listObs, 
                    new Obstacle3DHalf(ColorPlot.ZONE_N, "n" + (j) + " [∞]", font, sc, 0.0));
            }
            int h = 5;
            LinkedList<Obstacle3DHalf> tempP = new LinkedList<Obstacle3DHalf>();
            for(int j=0; j<map.sizeP; j++){  
                h = h % 25 + 5;
                Obstacle3DHalf zone = new Obstacle3DHalf(ColorPlot.ZONE_P, 
                        "p" + (j) + " [" + h + "]", font, sc, h);
                add(indexHyper, indexObs, indexN, listObs, new Obstacle3DHalf(h, zone));
                map.sizeN++;
                tempP.addLast(zone);
            }
            
            h = 1;
            LinkedList<Obstacle3DHalf> tempB = new LinkedList<Obstacle3DHalf>();
            for(int j=0; j<map.sizeB; j++){  
                h = h % 5 + 1;
                Obstacle3DHalf zone = new Obstacle3DHalf(ColorPlot.ZONE_B, 
                            "b" + (j) + " [" + h + "]", font, sc, h);
                add(indexHyper, indexObs, indexN, listObs, new Obstacle3DHalf(h, zone));
                map.sizeN++;
                tempB.addLast(zone);
            }
            for(Obstacle3DHalf zone : tempP){  
                add(indexHyper, indexObs, indexP, listObs, zone);
            }
            for(Obstacle3DHalf zone : tempB){  
                add(indexHyper, indexObs, indexB, listObs, zone);
            }
            map.sizeN++;
            
            map_name = uIO.ReadTrueNameWithOutExt(sc, "sgl");
            sc.close();
            
            Oj = indexHyper.toArray(new Integer[indexHyper.size()][]);            
            Φn = indexN.toArray(new Integer[indexN.size()]);
            Φp = indexP.toArray(new Integer[indexP.size()]);
            Φb = indexB.toArray(new Integer[indexB.size()]);
            obstacles = listObs.toArray(new Obstacle[listObs.size()]);
            hi = listHyper.toArray(new Hyperplane[listHyper.size()]);
        }
        
        @Override
        public String map_mame() {
            return map_name;
        }
        
        @Override
        public void results(LinkerResults link) throws Exception {
            super.results(link);
            link.writeString("map_name", map_name);
            if (ConfigSimulation.DEBUG_INSTANCE){
                System.out.println("|Φn|: " + map.sizeN);
                System.out.println("|Φp|: " + map.sizeP);
                System.out.println("|Φb|: " + map.sizeB);                
                System.out.print("Φn: ");
                for (int i = 0; i < Φn.length; i++){
                    System.out.print(Φn[i] + " ");
                }
                System.out.println();
                
                System.out.print("Φp: ");
                for (int i = 0; i < Φp.length; i++){
                    System.out.print(Φp[i] + " ");
                }
                System.out.println();
                
                System.out.print("Φb: ");
                for (int i = 0; i < Φb.length; i++){
                    System.out.print(Φb[i] + " ");
                }
                System.out.println();
                
                System.out.println("Oj: ");
                for (int j = 0; j < Oj.length; j++){
                    for (int i = 0; i < Oj[j].length; i++){
                        System.out.print(Oj[j][i] + " ");
                    }
                    System.out.println();
                }
            }
        }        
    }
}
