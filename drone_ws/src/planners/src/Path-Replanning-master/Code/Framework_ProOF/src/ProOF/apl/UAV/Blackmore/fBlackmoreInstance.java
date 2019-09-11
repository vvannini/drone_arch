/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.UAV.Blackmore;

import ProOF.apl.UAV.map.Obstacle;
import ProOF.apl.UAV.map.Obstacle2D;
import ProOF.apl.UAV.map.Obstacle3DHalf;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.language.Factory;
import ProOF.utilities.uIO;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 *
 * @author marcio
 */
public class fBlackmoreInstance extends Factory<BlackmoreInstance>{
    public static final fBlackmoreInstance obj = new fBlackmoreInstance();
    
    @Override
    public String name() {
        return "Instance Reader";
    }
    @Override
    public BlackmoreInstance build(int index) throws Exception {
        switch(index){
            case 0 : return new Instance2D();
            case 1 : return new Instance3DhalfHard();
            case 2 : return new Instance3DhalfRmd();
            case 3 : return new Instance2DOno();
        }
        return null;
    }

    private static class Instance2D extends BlackmoreInstance {
        private File config;
        private File map;
        
        private String map_name;
        private final Font font = new Font(Font.SANS_SERIF, Font.BOLD, 24);

        @Override
        public String name() {
            return "Instance 2D";
        }
        @Override
        public void parameters(LinkerParameters link) throws Exception {
            config = link.File("Configuration for Blackmore 2D", null, "sgl");
            map = link.File("Map for Blackmore 2D", null, "sgl");
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
            Scanner sc = new Scanner(config);
            start_state = uIO.ReadVectorDouble(sc);
            end_point = uIO.ReadVectorDouble(sc);
            sc.close();
        }
        private void loadMap() throws FileNotFoundException{
            Scanner sc = new Scanner(map);
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
            super.results(link); //To change body of generated methods, choose Tools | Templates.
            link.writeString("map_name", map_name);
        }
    }
    
    
    private static class Instance3DhalfHard extends BlackmoreInstance {
        private File config;
        private File map;
        
        private String map_name;
        private final Font font = new Font(Font.SANS_SERIF, Font.BOLD, 24);

        @Override
        public String name() {
            return "Instance 3Dhalf-hard";
        }
        @Override
        public void parameters(LinkerParameters link) throws Exception {
            config = link.File("Configuration for Blackmore 3D", null, "sgl");
            map = link.File("Map for Blackmore 3D half", null, "sgl");
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
            Scanner sc = new Scanner(config);
            start_state = uIO.ReadVectorDouble(sc);
            end_point = uIO.ReadVectorDouble(sc);
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
            Scanner sc = new Scanner(map);
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
            super.results(link); //To change body of generated methods, choose Tools | Templates.
            link.writeString("map_name", map_name);
        }
        
    }
    private static class Instance3DhalfRmd extends BlackmoreInstance {
        private File config;
        private File map;
        
        private String map_name;
        private final Font font = new Font(Font.SANS_SERIF, Font.BOLD, 24);

        @Override
        public String name() {
            return "Instance 3Dhalf-rmd";
        }
        @Override
        public void parameters(LinkerParameters link) throws Exception {
            config = link.File("Configuration for Blackmore 3D", null, "sgl");
            map = link.File("Map for Blackmore 3D half", null, "sgl");
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
            Scanner sc = new Scanner(config);
            start_state = uIO.ReadVectorDouble(sc);
            end_point = uIO.ReadVectorDouble(sc);
            sc.close();
        }
        private void loadMap() throws FileNotFoundException{
            Scanner sc = new Scanner(map);
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
            super.results(link); //To change body of generated methods, choose Tools | Templates.
            link.writeString("map_name", map_name);
        }
        
    }
    
    
    private static class Instance2DOno extends BlackmoreInstance {
        private File config;
        private File map;
        
        private String map_name;
        private final Font font = new Font(Font.SANS_SERIF, Font.BOLD, 24);

        @Override
        public String name() {
            return "Instance 2D Ono";
        }
        @Override
        public void parameters(LinkerParameters link) throws Exception {
            config = link.File("Configuration for Blackmore 2D Ono", null, "txt");
            map = link.File("Map for Blackmore 2D Ono", null, "txt");
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
            Scanner sc = new Scanner(config);
            start_state = new double[4];
            start_state[0] = sc.nextDouble();
            start_state[1] = sc.nextDouble();
            sc.nextLine();
            
            end_point = new double[2];
            end_point[0] = sc.nextDouble();
            end_point[1] = sc.nextDouble();
            sc.nextLine();

            sc.close();
        }
        private void loadMap() throws FileNotFoundException{
            Scanner sc = new Scanner(map);
            String text = "";
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                if(line.equals("<TrueName>")){
                    map_name = sc.nextLine();
                }else{
                    text += line;
                }
            }
            sc.close();
            
            System.out.println("text = {"+text+"}");
            text = text.replaceAll("]", "@");
            System.out.println("text@ = {"+text+"}");
            text = text.replaceAll("@ ", "@");
            text = text.replaceAll("@\t", "@");
            System.out.println("text@ = {"+text+"}");
            
            text = text.replaceAll("\\[", "");
            System.out.println("text[ = {"+text+"}");
            String obs[] = text.split("@");
            obstacles = new Obstacle[obs.length];
            for(int j=0; j<obs.length; j++){
                System.out.println("obs["+j+"] = {"+obs[j]+"}");
                String corners[] = obs[j].split(";");
                Point2D points[] = new Point2D[corners.length];
                for(int i=0; i<corners.length; i++){
                    System.out.println("corners["+i+"] = {"+corners[i]+"}");
                    Scanner sc2 = new Scanner(corners[i]);
                    points[corners.length-1-i] = new Point2D.Double(sc2.nextDouble(), sc2.nextDouble());
                    sc2.close();
                }
                obstacles[j] = new Obstacle2D(""+j, font, points);
            }
            
        }
        @Override
        public String map_mame() {
            return map_name;
        }
        @Override
        public void results(LinkerResults link) throws Exception {
            super.results(link); //To change body of generated methods, choose Tools | Templates.
            link.writeString("map_name", map_name);
        }
        
    }
}
