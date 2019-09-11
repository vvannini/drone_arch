/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF;

import ProOF.apl.factorys.fRun;
import ProOF.com.model.CallBack;
import ProOF.com.model.DrawPanel;
import ProOF.com.model.Model;
import ProOF.com.model.dVertex;
import ProOF.com.runner.Runner;
import ProOF.opt.abst.problem.meta.Best;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author marcio
 */
public class ProOF { 
//    public static final double X0[] = new double[4];
//    public static final double Xgoal[] = new double[4];
//    
    
    
    public static double inv_erf(double x, double lookup_arg[], double lookup_inv_erf[]){
        for(int i=0; i<=52; i++){
            if(x<lookup_arg[i+1]){
                double M = (lookup_inv_erf[i+1] - lookup_inv_erf[i])/(lookup_arg[i+1]-lookup_arg[i]);
                double w = x-lookup_arg[i];
                
                //System.out.printf("x = %g | M = %g | w = %g | f = %g\n", x, M, w, lookup_inv_erf[i] + w*M);
                
                return lookup_inv_erf[i] + w*M;
            }
        }
        return lookup_inv_erf[52];
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, Exception {
        
        //System.out.printf("%50s = [%s]\n", "File.pathSeparator", );
        
        //System.exit(-1);
        // TODO code application logic here
        //System.out.printf("%10g = %10g\n", 0, Normal.inverseStandardCdf(0.0));
        //1-2d = [0 1[
//        for(int i=0; i<100; i++){
//            double x = i*1.0/100;
//            double inv_erf = Normal.inverseStandardCdf((1+x)/2.0)/Math.sqrt(2);
//                //double inv_cdf = x>0?Normal.inverseStandardCdf(x):0;
//                System.out.printf(Locale.ENGLISH,"%1.20f = %1.20f\n", x, inv_erf);
//        }
//        
//        System.exit(1);
//        
//        double lookup_arg[] = new double[53];
//        double lookup_inv_erf[] = new double[53];
//        
//        System.out.printf(Locale.ENGLISH,"%s;%s\n", "x", "inv_erf");
//        for(int i=0; i<=52; i++){
//            double x = 1.0 - 1.0/Math.pow(2.0, i);
//            //if(x!=0.0){
//                double inv_erf = Normal.inverseStandardCdf((1+x)/2.0)/Math.sqrt(2);
//                //double inv_cdf = x>0?Normal.inverseStandardCdf(x):0;
//                System.out.printf(Locale.ENGLISH,"%1.20f\n", inv_erf);
//            //}
//                
//            lookup_arg[i] = x;
//            lookup_inv_erf[i] = inv_erf;
//        }
//        
//        double max_error = 0;
//        for(int i=0; i<1000000; i++){
//            double x = Math.random();
//            double e1 = Normal.inverseStandardCdf((1+x)/2.0)/Math.sqrt(2);
//            double e2 = inv_erf(x, lookup_arg, lookup_inv_erf);
//            double error = Math.abs(e1-e2);
//            if(error>max_error){
//                max_error =error;
//                System.out.printf("%4d, erf[%g] = %g\n", i, x, max_error);
//            }
//        }
//        
//        System.exit(1);
//        
//        
        boolean local = false;
        if (args == null || args.length == 0 || args[0].equals("-parameters")) {
            local = true;
            //args = new String[]{"model"};
//            if(args!=null && args.length>0){
//                X0[0] = Double.parseDouble(args[1]);
//                X0[1] = Double.parseDouble(args[1]);
//                X0[2] = Double.parseDouble(args[1]);
//                X0[3] = Double.parseDouble(args[1]);
//                Xgoal[0] = Double.parseDouble(args[1]);
//                Xgoal[1] = Double.parseDouble(args[1]);
//                Xgoal[2] = Double.parseDouble(args[1]);
//                Xgoal[3] = Double.parseDouble(args[1]);
//            }
            
//            args = new String[]{"run", "./UAV/eddq", "./UAV/input/"};   //CAIv2
//            args = new String[]{"run", "./UAV/tadq", "./UAV/input/"};   //CA
//            args = new String[]{"run", "./UAV/nflz", "./UAV/input/"};   //CAIv1
//            args = new String[]{"run", "./UAV/pwdv", "./UAV/input/"};   //CAIv3
            
              //  args = new String[]{"run", "./teste/RFFO-GCI", "./teste/GCIST/Artificiais/Unknown_0/"};
            //args = new String[]{"run", "./UAV/zmpf", "./UAV/"};
           // args = new String[]{"run", "./GCIST/smzg", "./GCIST/"};
            //args = new String[]{"run", "F:/zProOF/client_space_GCISTAll/job_local/waiting/hhyv", "F:/zProOF/client_space_GCISTAll/input/"};
//args = new String[]{"run", "F:/zProOF/client_space/job_local/waiting/aaavq", "F:/zProOF/client_space/input/"};
            
            //args = new String[]{"run", "E:/zProOF/client_space/job_local/finished/holl", "E:/zProOF/client_space/input/"};
            //args = new String[]{"run", "E:/zProOF/client_space/job_local/finished/mypa", "E:/zProOF/client_space/input/"};
            //args = new String[]{"run", "./config_ca.sgl", "./"};
            
//            args = new String[]{"run", "./config/yoyk", "./config/"};
            args = new String[]{"run", "./in/inputExact/auv", "./in/inputExact/"};
        }
        try{
            starting(args, local);
        }catch(Throwable ex){
            ex.printStackTrace(System.err);
            PrintStream log = new PrintStream(new File("log_error.txt"));
            ex.printStackTrace(log);
            log.close();
        }
        //System.exit(0);
    }

    private static void starting(String[] args, boolean local) throws Exception {
        Locale.setDefault(Locale.ENGLISH);
        if (args == null || args.length < 1) {
            throw new Exception("don't have arguments");
        } else if (args[0].equals("model")) {
            Model.PRINT = true;
            Model model = new Model();
            model.create(fRun.obj);
            model.savePof("model.pof");
            model.saveSgl("model.sgl");
        } else if (args[0].equals("run")) {
            Runner.PRINT = false;
            Runner.LOCAL = local;
            Best.force_finish(true);
            Runner runner = new Runner(new File(args[1]), new File(args[2]), fRun.obj);
            runner.run();
        } else {
            throw new Exception(String.format("arg[0]='%s' is not recognized.", args[0]));
        }
    }

    private static void main4() throws FileNotFoundException, Exception {
        Runner runner = new Runner(new File("D:/fFramework/ProOF/ProOFClient_vA/work_space/job/aahu.job"), new File("D:/fFramework/ProOF/ProOFClient_vA/work_space/input/"), fRun.obj);
        runner.run();
    }

    public static void main3(String[] args) throws FileNotFoundException {
        // TODO code application logic here

        Scanner sc = new Scanner(new File("GraphModel.txt"));
        LinkedList<Integer> list = new LinkedList<Integer>();
        String code = "";
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            //line = line.replaceAll("\t", "");
            //line = line.replaceAll(" ", "");
            code += line;
            list.addLast(line.length());
        }
        sc.close();

        System.out.println(code);

        //while(code.matches("(node\\[)(.*)")){

        //    System.out.println();
        //}
        String RgInt = "(([1-9][0-9]+)|([0-9]))";
        String RgName = "[a-zA-Z_][(\\w)]+";

        String head[] = new String[]{
            "node", "\\[", "id", "=", RgInt, ",", "name", "=", RgName, "\\]", ".*"
        };
        String make_head = make(head);



        //String s = String.format("_node_[_id_]", "node", "[", "id", "=", RgInt, ",", "name", "=", RgName, "]");

        //System.out.println(code.matches("((\\s)*node(\\s)*\\[(\\s)*id(\\s)*=(\\s)*"+RgInt+"(\\s)*,(\\s)*name(\\s)*=(\\s)*"+RgName+"(\\s)*\\])(.*)"));

        System.out.println(code.matches(make_head));


        //System.out.println("."+sc.next());
        //while(sc.){
        //System.out.println(sc.next());
        //}
        /*sc.close();
         while(sc.hasNext("[n][o][d][e]")){//"[a-zA-Z0-9_]+"
         System.out.println("."+sc.next("[node]"));
         }*/


        // main2(args);
    }

    public static String make(String... block) {
        String r = "(\\s)*";
        for (String b : block) {
            r += "(" + b + ")(\\s)*";
        }
        System.out.println(r);
        return r;
    }

    public static void main2(String[] args) throws FileNotFoundException {
        args = new String[]{"model"};
        if (args[0].equals("model")) {
            final Model model = new Model();
            model.create(fRun.obj);

            model.savePof("model.pof");
            model.saveSgl("model.sgl");

            final DrawPanel draw = new DrawPanel(new CallBack() {
                @Override
                public void main() {
                }
            });
            draw.setProject(model);

            draw.setPreferredSize(new Dimension(970, 650));
            draw.setBackground(Color.WHITE);
            final JFrame frame = new JFrame("Frame");
            frame.setLayout(new FlowLayout(FlowLayout.CENTER));
            frame.add(draw);

            //frame.setSize(1000, 700);
            frame.setSize(new Dimension(1000, 700));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            frame.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(final ComponentEvent e) {
                    System.out.printf("componentResized[%d, %d]\n", frame.getWidth(), frame.getHeight());
                    draw.setPreferredSize(new Dimension(frame.getWidth() - 30, frame.getHeight() - 50));
                    SwingUtilities.updateComponentTreeUI(frame);
                }
            });
            frame.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        dVertex.paint_lines++;
                        draw.repaint();
                    }
                }
            });
        }
    }

    public static void setLookAndFeel(String lookAndFeelds, JFrame frame) {
        try {
            UIManager.setLookAndFeel(lookAndFeelds);
            SwingUtilities.updateComponentTreeUI(frame);
            //fileChooserInstances.updateUI();
            //fileChooserBatchs.updateUI();
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Erro: ClassNotFoundException", "Atualizando LookAndFeel", JOptionPane.INFORMATION_MESSAGE);
        } catch (InstantiationException ex) {
            JOptionPane.showMessageDialog(null, "Erro: InstantiationException", "Atualizando LookAndFeel", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalAccessException ex) {
            JOptionPane.showMessageDialog(null, "Erro: IllegalAccessException", "Atualizando LookAndFeel", JOptionPane.INFORMATION_MESSAGE);
        } catch (UnsupportedLookAndFeelException ex) {
            JOptionPane.showMessageDialog(null, "Erro: UnsupportedLookAndFeelException", "Atualizando LookAndFeel", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
