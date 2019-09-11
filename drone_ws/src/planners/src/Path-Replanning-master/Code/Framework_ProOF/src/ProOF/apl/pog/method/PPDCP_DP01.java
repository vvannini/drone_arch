
package ProOF.apl.pog.method;

import ProOF.apl.pog.problem.PPDCP.PPDCPFactory;
import ProOF.apl.pog.problem.PPDCP.PPDCPInstance;
import ProOF.com.Linker.LinkerApproaches;
import ProOF.com.Linker.LinkerParameters;
import ProOF.com.Linker.LinkerResults;
import ProOF.com.Linker.LinkerValidations;
import ProOF.com.language.Run;
import ProOF.utilities.uUtil;
import java.util.Locale;
import jsc.distributions.Normal;

/**
 *
 * @author Hossomi
 */
public class PPDCP_DP01 extends Run {

    
    private PPDCPInstance inst = new PPDCPInstance();
    
    private int dX = 41;
    private int dV = 11;
    private int dU = 11;
    
    
    private long initial;
    private double cost;
    private double Xt[][];
    private double Ut[][];
    @Override
    public String name() {
        return "PPDCP-PD-01";
    }

    @Override
    public String description() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void services(LinkerApproaches com) throws Exception {
        com.add(inst);
    }
    @Override
    public void parameters(LinkerParameters com) throws Exception {
        
    }
    @Override
    public boolean validation(LinkerValidations com) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void results(LinkerResults com) throws Exception {
        com.writeDbl("Obj Value", cost);
        com.writeDbl("time", (System.nanoTime()-initial)/1e9);
        //------------------------[beta]---------------------------
        double beta = inst.dist2(Xt[inst.T], inst.Xgoal);
        
        //------------------------[beta]---------------------------
        double delta = 0;
        for(int j=0; j<inst.J; j++){
            for(int t=0; t<inst.T; t++){
                delta += Delta(inst, j, t, Xt);
            }
        }
        delta = Math.max(0, delta-inst.DELTA);
        
        //------------------------[goal]---------------------------
        double goal = 0;
        for(int t=0; t<inst.T; t++){
            goal += Ut[t][0]*Ut[t][0]+Ut[t][1]*Ut[t][1];
        }
        
        double fitness = beta*inst.P_Goal + delta*inst.P_Delta  + goal;
        com.writeDbl("fitness", fitness);
        com.writeDbl("beta", beta*inst.P_Goal);
        com.writeDbl("delta", delta*inst.P_Delta);
        double M[][] = inst.trans(Xt);
        com.writeArray("PPDCP", "fMt[0]", M[0]);
        com.writeArray("PPDCP", "fMt[1]", M[1]);
        com.writeArray("PPDCP", "fMt[2]", M[2]);
        com.writeArray("PPDCP", "fMt[3]", M[3]);
        double U[][] = inst.trans(Ut);
        com.writeArray("PPDCP", "fUt[0]", U[0]);
        com.writeArray("PPDCP", "fUt[1]", U[1]);
        
        inst.plot(Xt, dX, "%1.1f m");
        inst.save();
    }
    
    public boolean factivel(int t, double X[]){
        double delta = 0;
        for(int j=0; j<inst.J; j++){
            delta += Delta(inst, j, t, Xt);
        }
        return delta>inst.DELTA;
    }
    
//    public static double Delta(PPDCPInstance inst, int j, int t, double Mt[][]){
//        double min = Integer.MAX_VALUE;
//        for(int i=0; i<inst.bji[j].length; i++){
//            double exp = inst.chi(j, t, i);
//            for(int k=0; k<4; k++){
//                exp += inst.aji[j][i][k] * Mt[t][k];
//            }
//            exp = exp / inst.psi(j, t, i);
//            min = Math.min(min, exp);
//        }
//        return Math.max(0, min);
//    }
    public static double Delta(PPDCPInstance inst, int j, int t, double X[][]){
        double max = -Integer.MAX_VALUE;
        for(int i=0; i<inst.Ob[j].length(); i++){
            double exp = inst.Ob[j].lines[i].ax*X[t][0] + inst.Ob[j].lines[i].ay*X[t][2];
            exp -= inst.Ob[j].lines[i].b;
            exp = exp/inst.Rjti[j][t][i];
            max = Math.max(max, exp);
        }
        double delta = (1-Normal.standardTailProb(max, false))/2;
        return delta;
    }


    @Override
    public void load() throws Exception {
        
    }
    
    @Override
    public void start() throws Exception {
        Ut = new double[inst.T][2];
        Xt = new double[inst.T+1][4];
        Vt = new double[inst.T+1][dX][dX][dV][dV];
        Ht = new double[inst.T][dX][dX][dV][dV][2];
        for(int t=0; t<inst.T+1; t++){
            for(int px=0; px<dX; px++){
                for(int py=0; py<dX; py++){
                    for(int vx=0; vx<dV; vx++){
                        for(int vy=0; vy<dV; vy++){
                            Vt[t][px][py][vx][vy] = Double.MAX_VALUE;
                        }
                    }
                }
            }
        }
        
        inst.plot(null, dX, "%1.1f m");
    }
    
    
    /*private double dist(double x1, double y1, double x2, double y2){
        return uUtil.dist(x1, y1, x2, y2);
    }*/
    
    private double Cn(double X[]) throws Exception{
        //return uUtil.dist(X[0], X[2], inst.Xgoal[0], inst.Xgoal[2]);
        //return inst.BETA(uUtil.dist(X[0], X[2], inst.Xgoal[0], inst.Xgoal[2]));
        //return uUtil.dist(X[0], X[2], inst.Xgoal[0], inst.Xgoal[2]) < (inst.Rx(inst.T)+inst.Ry(inst.T))/2  ? 0 : Double.MAX_VALUE;
        double delta = Delta(inst.T, X);
        double cost = 0;
        if(delta > inst.DELTA/inst.T){
            cost += inst.DELTA(delta);
        }
        /*if(uUtil.dist(X[0], X[2], inst.Xgoal[0], inst.Xgoal[2]) < 5*(inst.Rx(inst.T)+inst.Ry(inst.T))/2){
            return cost + uUtil.dist(X[0], X[2], inst.Xgoal[0], inst.Xgoal[2]);
        }else{
            return cost + inst.BETA(uUtil.dist(X[0], X[2], inst.Xgoal[0], inst.Xgoal[2]));
        }*/
        return cost + inst.GOAL(uUtil.dist(X[0], X[2], inst.Xgoal[0], inst.Xgoal[2]));
    }
    private double Ct(int t, double X[], double U[]) throws Exception{
        double delta = Delta(t, X);
        double cost = 0;
        if(delta > inst.DELTA/inst.T){
            cost += inst.DELTA(delta);
        }
        return cost + uUtil.SQN2(U);
    }
    
    
    
    private double Ht[][][][][][];  //t,px,py,vx,vy --> return [ax,ay]*
    private double Vt[][][][][];    //t,px,py,vx,vy --> return [cost]
    
    
    private double[] getXt(double X[]){
        int px = i(X[0]);
        int py = i(X[2]);
        int vx = j(X[1]);
        int vy = j(X[3]);
        return new double[]{p(px),v(vx),p(py),v(vy)};
    }
    private double getVt(int t, double X[]){
        int px = i(X[0]);
        int py = i(X[2]);
        int vx = j(X[1]);
        int vy = j(X[3]);
        /*if(px<0||py<0||vx<0||vy<0||px>=dX||py>=dX||vx>=dV||vy>=dV){
            return Double.MAX_VALUE;
        }*/
        px = Math.max(px, 0);
        py = Math.max(py, 0);
        vx = Math.max(vx, 0);
        vy = Math.max(vy, 0);
        
        px = Math.min(px, dX-1);
        py = Math.min(py, dX-1);
        vx = Math.min(vx, dV-1);
        vy = Math.min(vy, dV-1);
        
        return Vt[t][px][py][vx][vy];
    }
    /*private void setVt(int t, double X[][], double value){
        int px = i(X[t][0]);
        int py = i(X[t][2]);
        int vx = j(X[t][1]);
        int vy = j(X[t][3]);
        Vt[t][px][py][vx][vy] = value;
    }*/
    private void set_minVt(int t, double X[][], double U[][], double value){
        int px = i(X[t][0]);
        int py = i(X[t][2]);
        int vx = j(X[t][1]);
        int vy = j(X[t][3]);
        
        if(value < Vt[t][px][py][vx][vy]){
            Vt[t][px][py][vx][vy] = value;
            if(t<U.length){
                Ht[t][px][py][vx][vy][0] = U[t][0];
                Ht[t][px][py][vx][vy][1] = U[t][1];
                
                //double W[] = G(t, X, U[t]);
                //Ht[t][px][py][vx][vy][0] = W[0];
                //Ht[t][px][py][vx][vy][1] = W[1];
            }
        }
    }
   
    private double bestCost(int t, double[] X) {
        int px = i(X[0]);
        int py = i(X[2]);
        int vx = j(X[1]);
        int vy = j(X[3]);
        
        return Vt[t][px][py][vx][vy];
    }
    private double bestU(int t, double X[], int index){
        int px = i(X[0]);
        int py = i(X[2]);
        int vx = j(X[1]);
        int vy = j(X[3]);
        
        /*if(px<0||py<0||vx<0||vy<0){
            System.out.printf("px = %8.3f %5d\n", X[0],px);
            System.out.printf("py = %8.3f %5d\n", X[2],py);
            System.out.printf("vx = %8.3f %5d\n", X[1],vx);
            System.out.printf("vy = %8.3f %5d\n", X[3],vy);
            
            System.exit(-1);
        }*/
        return Ht[t][px][py][vx][vy][index];
    }
    
    private double p(int i){
        return (i*2.0/(dX-1)-1)*inst.XMAX();
    }
    private double v(int j){
        return (j*2.0/(dV-1)-1)*inst.VMAX;
    }
    private double a(int k){
        return (k*2.0/(dU-1)-1)*inst.UMAX;
    }
    private int i(double p){//(p/X + 1)*(dx-1)/2 = i
        return (int)((p/inst.XMAX()+1.0)*(dX-1)/2 + 0.5);
    }
    private int j(double v){
        return (int)((v/inst.VMAX + 1.0)*(dV-1)/2 + 0.5);
    }
    private int k(double a){
        return (int)((a/inst.UMAX + 1.0)*(dU-1)/2 + 0.5);
    }
    private void calcVt(int t, double X[][], double U[][]) throws Exception{
        if(t==inst.T){    //Cn
            set_minVt(t, X, U, Cn(X[t]));
        }else{
            set_minVt(t, X, U, Ct(t,X[t],U[t]) + getVt(t+1, F(X[t], U[t])));
        }
    }
    
    private double Delta(int t, double X[]){
        double sum = 0;
        for(int j=0; j<inst.J; j++){
            sum += Delta(j, t, X);
        }
        return sum;
    }
//    private double Delta(int j, int t, double X[]){
//        double min = Integer.MAX_VALUE;
//        for(int i=0; i<inst.bji[j].length; i++){
//            double exp = inst.chi(j, t, i);
//            for(int k=0; k<4; k++){
//                exp += inst.aji[j][i][k] * X[k];
//            }
//            exp = exp / inst.psi(j, t, i);
//            min = Math.min(min, exp);
//        }
//        return Math.max(0, min);
//    }
    public double Delta(int j, int t, double X[]){
        double max = -Integer.MAX_VALUE;
        for(int i=0; i<inst.Ob[j].length(); i++){
            double exp = inst.Ob[j].lines[i].ax*X[0] + inst.Ob[j].lines[i].ay*X[2];
            exp -= inst.Ob[j].lines[i].b;
            exp = exp/inst.Rjti[j][t][i];
            max = Math.max(max, exp);
        }
        double delta = (1-Normal.standardTailProb(max, false))/2;
        return delta;
    }
    
    private double[] F(double X[], double U[]){
        double Y[] = new double[4];
        for(int i=0; i<4; i++){
            double sum = 0;
            for(int k=0; k<4; k++){
                sum += inst.A[i][k]*X[k];
            }
            for(int j=0; j<2; j++){
                sum += inst.B[i][j]*U[j];
            }
            Y[i] = sum;
        }
        return Y;
    }
    private double[] G(int t, double X[][]){
        //X(t+1) = A*Xt + B*Ut
        //Ut = ~B*X(t+1) - ~B*A*Xt
        
        
        double W[] = new double[2];
        for(int j=0; j<2; j++){
            double sum = 0;
            for(int k=0; k<4; k++){
                sum += inst.invB[j][k]*X[t+1][k];
            }
            for(int k=0; k<4; k++){
                double aux = 0;
                for(int i=0; i<4; i++){
                    aux += inst.A[k][i]*X[t][i];
                }
                sum -= inst.invB[j][k]*aux;
            }
            W[j] = sum;
        }
        return W;
        
    }
    
    
    @Override
    public void execute() throws Exception {
        initial = System.nanoTime();
        
        int t=inst.T;
        for(int px=0; px<dX; px++){
            Xt[t][0] = p(px);
            for(int py=0; py<dX; py++){
                Xt[t][2] = p(py);
                for(int vx=0; vx<dV; vx++){
                    Xt[t][1] = v(vx);
                    for(int vy=0; vy<dV; vy++){
                        Xt[t][3] = v(vy);
                        calcVt(t, Xt, Ut);
                        
                        double val = Vt[t][px][py][vx][vy];
                        System.out.printf(Locale.ENGLISH,"t = %2d [px,py,vx,vy|ax,ay] = [%8.3f,%8.3f,%8.3f,%8.3f|   .    ,   .    ] -> %8g\n", t, Xt[t][0],Xt[t][2],Xt[t][1],Xt[t][3], val);
                    }
                }
            }
        }
        t--;
        while(t>=0){
            for(int px=0; px<dX; px++){
                Xt[t][0] = p(px);
                for(int py=0; py<dX; py++){
                    Xt[t][2] = p(py);
                    for(int vx=0; vx<dV; vx++){
                        Xt[t][1] = v(vx);
                        for(int vy=0; vy<dV; vy++){
                            Xt[t][3] = v(vy);
                            for(int ax=0; ax<dU; ax++){
                                Ut[t][0] = a(ax);
                                for(int ay=0; ay<dU; ay++){
                                    Ut[t][1] = a(ay);
                                    calcVt(t, Xt, Ut);
                                }
                            }
                            double val = Vt[t][px][py][vx][vy];    
                            System.out.printf(Locale.ENGLISH,"t = %2d [px,py,vx,vy|ax,ay] = [%8.3f,%8.3f,%8.3f,%8.3f|%8.3f,%8.3f] -> %8g\n", t, Xt[t][0],Xt[t][2],Xt[t][1],Xt[t][3],bestU(t,Xt[t],0), bestU(t,Xt[t],1), val);
                            
                        }
                    }
                }
            }
            t--;
        }
        
        System.out.println("-------------------- [V0] ----------------------");
        t=0;
        for(int px=0; px<dX; px++){
            Xt[t][0] = p(px);
            for(int py=0; py<dX; py++){
                Xt[t][2] = p(py);
                for(int vx=0; vx<dV; vx++){
                    Xt[t][1] = v(vx);
                    for(int vy=0; vy<dV; vy++){
                        Xt[t][3] = v(vy);
                        for(int j=0; j<2; j++){
                            Ut[t][j] = bestU(t, Xt[t], j);
                        }
                        double val = Vt[t][px][py][vx][vy];
                        if(t==inst.T){
                            System.out.printf(Locale.ENGLISH,"t = %2d [px,py,vx,vy|ax,ay] = [%8.3f,%8.3f,%8.3f,%8.3f|   .    ,   .    ] -> %8g\n", t, Xt[t][0],Xt[t][2],Xt[t][1],Xt[t][3], val);
                        }else{
                            System.out.printf(Locale.ENGLISH,"t = %2d [px,py,vx,vy|ax,ay] = [%8.3f,%8.3f,%8.3f,%8.3f|%8.3f,%8.3f] -> %8g\n", t, Xt[t][0],Xt[t][2],Xt[t][1],Xt[t][3],Ut[t][0],Ut[t][1], val);
                        }
                    }
                }
            }
        }
        
        //----------------------- decode the solution --------------------------
        Xt[0][0] = inst.X0[0];
        Xt[0][1] = inst.X0[1];
        Xt[0][2] = inst.X0[2];
        Xt[0][3] = inst.X0[3];
        cost = bestCost(0, Xt[0]);
        for(t=0; t<inst.T; t++){
            for(int j=0; j<2; j++){
                Ut[t][j] = bestU(t, Xt[t], j);
            }
            for(int i=0; i<4; i++){
                double sum = 0;
                for(int k=0; k<4; k++){
                    sum += inst.A[i][k]*Xt[t][k];
                }
                for(int j=0; j<2; j++){
                    sum += inst.B[i][j]*Ut[t][j];
                }
                Xt[t+1][i] = sum;
            }
            
            Xt[t+1] = getXt(Xt[t+1]);
            /*Ut[t] = G(t, Xt);
            for(int j=0; j<2; j++){
                Ut[t][j]= Ut[t][j] > +inst.VMAX ? +inst.VMAX : 
                          Ut[t][j] < -inst.VMAX ? -inst.VMAX : 
                          Ut[t][j]; 
            }
            for(int i=0; i<4; i++){
                double sum = 0;
                for(int k=0; k<4; k++){
                    sum += inst.A[i][k]*Xt[t][k];
                }
                for(int j=0; j<2; j++){
                    sum += inst.B[i][j]*Ut[t][j];
                }
                Xt[t+1][i] = sum;
            }*/
        }
        
        double lower = 0;
        for(t=0; t<inst.T; t++){
            lower += uUtil.SQN2(Ut[t]);
        }
        System.out.println("Solution lower      = " + lower);
        System.out.println("Solution value      = " + cost);   
        System.out.println("Time                = " + (System.nanoTime()-initial)/1e9);    
        System.out.println("-------------------- [Ut] -> [Xt] ----------------------");
        for(t=0; t<inst.T; t++){
            System.out.printf(Locale.ENGLISH, "[%8.3f %8.3f %8.3f %8.3f] + [%8.3f %8.3f] | [%8.3f %8.3f %8.3f %8.3f] + [%8.3f %8.3f]--> \n",
                    Xt[t][0],Xt[t][1], Xt[t][2], Xt[t][3], Ut[t][0],Ut[t][1],
                    p(i(Xt[t][0])),p(i(Xt[t][2])),v(j(Xt[t][1])),v(j(Xt[t][3])),a(k(Ut[t][0])),a(k(Ut[t][1])));
        }
        System.out.printf(Locale.ENGLISH, "[%8.3f %8.3f %8.3f %8.3f] --> end \n", Xt[inst.T][0],Xt[inst.T][1], Xt[inst.T][2], Xt[inst.T][3]);
        inst.plot(Xt);
    }

    

}
