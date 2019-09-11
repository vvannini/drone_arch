/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF.apl.pog.problem.CCQSP;

import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.opt.abst.problem.Instance;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;


/**
 *
 * @author marcio
 */
public class CCQSPInstance extends Instance{
    

    protected final double LBk[] = new double[]{
        2, 2, 2, 2, 2
    };
    protected final double UBk[] = new double[]{
        6, 6, 6, 6, 6 
    };
    
    protected File file_map;
    protected File file_dinamic;
    
    public final CCQSPDynamic dy = new CCQSPDynamic();
    public final CCQSPMap  map = new CCQSPMap();
    public final CCQSPPlot plot = new CCQSPPlot(map);
    public QSP qsp = null;
    
    @Override
    public String name() {
        return "Instance-CCQSP";
    }
    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void parameters(LinkerParameters win) throws Exception {
        file_map = win.File("Map for CCQSP",null,"sgl");
        file_dinamic = win.File("Dinamic for CCQSP",null,"sgl");
    }
    @Override
    public void results(LinkerResults win) throws Exception {
        map.results(win);
    }
    
    public static void main(String args[]) throws IOException{
        CCQSPInstance inst = new CCQSPInstance();
        inst.file_dinamic = new File("./CCQSP/zdynamic.sgl");
        inst.file_map = new File("./CCQSP/zmap.sgl");
        inst.load();
    }
    
    @Override
    public void load() throws FileNotFoundException, IOException {
        
        Scanner in = new Scanner(file_dinamic);
        
        System.out.println(in.nextLine());
        map.TEMPO = Double.parseDouble(in.nextLine());
        
        System.out.println(in.nextLine());
        map.SD = Double.parseDouble(in.nextLine());
       
        System.out.println(in.nextLine());
        for(int i=0; i<4; i++){
            map.X0[i] = Double.parseDouble(in.next());
        }
        in.nextLine();
        
//        System.out.println(in.nextLine());
//        for(int i=0; i<4; i++){
//            map.Xgoal[i] = Double.parseDouble(in.next());
//        }
//        in.nextLine();
        
        System.out.println(in.nextLine());
        map.VMAX = Double.parseDouble(in.nextLine());
        
        System.out.println(in.nextLine());
        map.UMAX = Double.parseDouble(in.nextLine());
        
        //System.out.println(in.nextLine());
        //VMIN = Double.parseDouble(in.nextLine());
        
        System.out.println(in.nextLine());
        map.DELTA = Double.parseDouble(in.nextLine());
        
        System.out.println(in.nextLine());
        map.T = Integer.parseInt(in.nextLine());
        
        System.out.println(in.nextLine());
        plot.width = Integer.parseInt(in.next());
        plot.heigth = Integer.parseInt(in.next());
        
        in.close();
        map.DT = map.TEMPO/map.T;
        
        dy.start(map.DT, map.SD);
        
        map.start(file_map, dy);
        
        qsp = new QSP(map, map.I, 0, (map.TEMPO*2)/map.I);
        
        plot.plot("Start",null);
    }

    public int G(int j) {
        return map.Ob[j].length();
    }
    public int E(int j, int t) {
        return map.Dy[j][t].length();
    }
    public int I(int j) {
        return map.In[j].length();
    }

    public int T() {
        return map.T;
    }
    public int E() {
        return map.E;
    }
    public int I() {
        return map.I;
    }
}
