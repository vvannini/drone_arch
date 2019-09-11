/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ProOF;

import ProOF.apl.factorys.fRun;
import ProOF.com.model.Model;
import ProOF.com.runner.Runner;
import ProOF.opt.abst.problem.meta.Best;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Locale;

/**
 *
 * @author marcio
 */
public class ProOF_JSA { 
    
    public static void main(String[] args) throws FileNotFoundException, Exception {
        boolean local = false;
        if (args == null || args.length == 0 || args[0].equals("-parameters")) {
            local = true;            
            
            args = new String[]{"run", "./instance", "./"};
            
//            args = new String[]{"run", "./in/ICTAI2017/agmp/agmp", "./in/ICTAI2017/agmp/"};
//            args = new String[]{"run", "./in/ICTAI2017/ag/ag", "./in/ICTAI2017/ag/"};
//            args = new String[]{"run", "./in/ICTAI2017/hg/hg", "./in/ICTAI2017/hg/"};
//            args = new String[]{"run", "./in/ICTAI2017/de/de", "./in/ICTAI2017/de/"};
//            args = new String[]{"run", "./in/ICTAI2017/apstd/apstd", "./in/ICTAI2017/apstd/"};
//              args = new String[]{"run", "./in/ICTAI2017/ms/ms", "./in/ICTAI2017/ms/"};
            
//            args = new String[]{"run", "./in/APSTD/method_apstd", "./in/APSTD/"};//Ok
//            args = new String[]{"run", "./in/GH/method_gh", "./in/GH/"};//Ok

//            args = new String[]{"run", "./in/SGA/method_sga", "./in/SGA/"};//Ok
//            args = new String[]{"run", "./in/ED/method_ed", "./in/ED/"}; //ERRO
            
//            args = new String[]{"run", "./in/MPGA/method_mpga", "./in/MPGA/"};
//            args = new String[]{"run", "./in/MPGA_Simoes/method_mpga", "./in/MPGA_Simoes/"};

//            args = new String[]{"run", "./in/MILP/method_milp1", "./in/MILP/"};//OK
//            args = new String[]{"run", "./in/MILP/method_milp2", "./in/MILP/"};//OK
//            args = new String[]{"run", "./in/MILP_Seq/ayph", "./in/MILP_Seq/"};//OK
//            args = new String[]{"run", "./in/MILP_Seq/ahcf", "./in/MILP_Seq/"};//OK
            //IFA do pierre
//            args = new String[]{"run", "./in/IFA/config-jga", "./in/IFA/"};//ERRO
            //AutoFG do marcelo
//            args = new String[]{"run", "./in/FGFS/run-mpga", "./in/FGFS/"};//ERRO
        }
        try{
            starting(args, local);
        }catch(Throwable ex){
            ex.printStackTrace(System.err);
            PrintStream log = new PrintStream(new File("log_error.txt"));
            ex.printStackTrace(log);
            log.close();
        }
        //Habilitar toda vez que for executado pelo ProofClient
//        System.exit(0);
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
}
