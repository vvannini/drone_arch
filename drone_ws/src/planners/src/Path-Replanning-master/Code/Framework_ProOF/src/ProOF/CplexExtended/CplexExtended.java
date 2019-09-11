/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ProOF.CplexExtended;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import java.util.LinkedList;
import jsc.distributions.Normal;
import org.apache.commons.math3.special.Erf;

/**
 *
 * @author marcio
 */
public class CplexExtended extends IloCplex implements iCplexExtract{
    private final double bigM;// = 1e5;
    private final double epsilon;// = 1e-4;
    
    private CplexExtended(double bigM, double epsilon) throws IloException {
        super();
        this.bigM = bigM;
        this.epsilon = epsilon;
    }
    public CplexExtended() throws IloException {
        this(1e+5, 1e-4);
    }

    public final void addMinimizeExtra(IloNumExpr exp) throws IloException {
        getObjective().setExpr(sum(exp,getObjective().getExpr()));
    }
    public final IloNumExpr RiskAllocation(IloNumVar delta, final double uncertainty, final double max, final int N, String name) throws IloException{
        return RiskAllocation(delta, uncertainty, max, 2.0, N, name);
    }
    
    /*
     * Risk allocation 
     * c(delta) = erf-inv(1-2delta) * uncertainty
     */
    public final IloNumExpr RiskAllocation(IloNumVar delta, final double uncertainty, 
            final double max, final double base, final int N, String name) throws IloException{
        CplexNumFunction erf_inv = new CplexNumFunction(this, N, true, name) {
            @Override
            public double x(int n) {
                //return 1.0 - max/Math.pow(base, n);
                return 1.0 - 2*max/Math.pow(base, n);
            }
            @Override
            public double f(int n) {
                //System.out.println("F(x("+n+")): x = "+x(0));
                return Normal.inverseStandardCdf((1+x(n))/2.0)/Math.sqrt(2);
            }
        };
        //addLe(1, sum(prod(2,delta), erf_inv.x), name+".x_2delta");
        addEq(1, sum(prod(2,delta), erf_inv.x), name+".x_2delta");
        
        return prod(erf_inv.f, uncertainty);
    }
    
    
    
    
    public final IloNumExpr jRiskAllocation(IloNumVar delta, final double uncertainty, 
            final double max, final int N, String name) throws IloException{
        return jRiskAllocation(delta, uncertainty, max, 2.0, N, name);
    }
    /*
     * Risk allocation 
     * c(delta) = erf-inv(1-2delta) * uncertainty
     */
    public final IloNumExpr jRiskAllocation(IloNumVar delta, final double uncertainty, 
            final double deltaMax, final double base, final int N, String name) 
            throws IloException{
        jCplexNumFunction erf_inv = new jCplexNumFunction(this, N, true, name) {
            @Override
            public double x(int n) {
                if (n < N){
                    return -1.0 + 2*(deltaMax-0.5)/Math.pow(base, N-n);
                } else if (n == N){
                    return 0;
                } else {
                    return 1.0 - 2*(deltaMax-0.5)/Math.pow(base, n-N);
                }
            }
            @Override
            public double f(int n) {
                //System.out.println("F(x("+n+")): x = "+x(0));
                if (n < 2*N){
                    return Erf.erfInv(x(n));
                } else{
                    return 1e6;
                }
            }
        };
        //addLe(1, sum(prod(2,delta), erf_inv.x), name+".x_2delta");
        addEq(1, sum(prod(2,delta), erf_inv.x), name+".x_2delta");
        
        return prod(erf_inv.f, uncertainty);
    }
    
    
    
    
    public IloNumExpr SumProd(IloNumExpr sum, double coef, IloNumExpr exp) throws IloException{
        if(exp==null){
            return sum;
        }else if(sum==null){
            return prod(coef, exp);
        }else{
            return sum(sum, prod(coef, exp));
        }
    }
    public IloNumExpr SumProd(double coef, IloNumExpr ...exp) throws IloException{
        return SumProd(null, coef, exp);
    }
    public IloNumExpr SumProd(IloNumExpr sum, double coef, IloNumExpr ...exp) throws IloException{
        for (IloNumExpr e : exp) {
            sum = SumProd(sum, coef, e);
        }
        return sum;
    }
    
    public IloNumExpr SumNumScalProd(IloNumExpr sum, String name, int N, double MAX, IloNumExpr... exp) throws IloException {
        CplexNumScalProd func = new CplexNumScalProd(this, name, N, MAX, exp);
        if(sum==null){
            return func.f;
        }else{
            return sum(sum, func.f);
        }
    }
    public IloNumExpr SumNumNorm2_2D(IloNumExpr sum, String name, int N, double MAX, IloNumExpr expX, IloNumExpr expY) throws IloException {
        CplexNumNorm2_2D func = new CplexNumNorm2_2D(this, name, N, MAX, expX, expY);
        if(sum==null){
            return func.f;
        }else{
            return sum(sum, func.f);
        }
    }
    public IloNumExpr SumNumNorm1(IloNumExpr sum, String name, double MAX, IloNumExpr... exp) throws IloException {
        CplexNumNorm1 func = new CplexNumNorm1(this, name, MAX, exp);
        if(sum==null){
            return func.f;
        }else{
            return sum(sum, func.f);
        }
    }
    public IloNumExpr SumNumFunction(IloNumExpr sum, String name, int N, final iCplexFunction1v func, IloNumExpr... exp) throws IloException {
        for(IloNumExpr e : exp){
            CplexNumFunction temp = new CplexNumFunction(this, N, false, name.replace("(.)", "("+e+")")){
                @Override
                public double x(int n) {
                    return func.x(n);
                }
                @Override
                public double f(int n) {
                    return func.f(n);
                }
            };
            addEq(temp.x, e, name+".x_e");
            if(sum==null){
                sum = temp.f;
            }else{
                sum = sum(sum, temp.f);
            }
        }
        return sum;  
    }
    
    
    
    public static String Index(int i, int size){
        String s = "";
        int len = 0;
        while(size>0){
            size = size/10;
            len++;
        }
        size = i+1;
        while(size>0){
            size = size/10;
            len--;
        }
        while(len>0){
            s += "0";
            len--;
        }
        return s+(i+1);
    }
    public IloNumVar[] numVarArray(int d1, double min, double max, String name) throws IloException{
        IloNumVar var[] = new IloNumVar[d1];
        for(int i=0; i<d1; i++){
            var[i] = numVar(min, max, name+Index(i, d1));
        }
        return var;
    }
    public IloNumVar[][] numVarArray(int d1, int d2, double min, double max, String name) throws IloException{
        IloNumVar var[][] = new IloNumVar[d1][];
        for(int i=0; i<d1; i++){
            var[i] = numVarArray(d2, min, max, name+Index(i, d1));
        }
        return var;
    }
    public IloNumVar[][][] numVarArray(int d1, int d2, int d3, double min, double max, String name) throws IloException{
        IloNumVar var[][][] = new IloNumVar[d1][][];
        for(int i=0; i<d1; i++){
            var[i] = numVarArray(d2, d3, min, max, name+Index(i, d1));
        }
        return var;
    }
    public IloNumVar[][][][] numVarArray(int d1, int d2, int d3, int d4, double min, double max, String name) throws IloException{
        IloNumVar var[][][][] = new IloNumVar[d1][][][];
        for(int i=0; i<d1; i++){
            var[i] = numVarArray(d2, d3, d4, min, max, name+Index(i, d1));
        }
        return var;
    }
    public IloNumVar[][][][][] numVarArray(int d1, int d2, int d3, int d4, int d5, double min, double max, String name) throws IloException{
        IloNumVar var[][][][][] = new IloNumVar[d1][][][][];
        for(int i=0; i<d1; i++){
            var[i] = numVarArray(d2, d3, d4, d5, min, max, name+Index(i, d1));
        }
        return var;
    }
    public IloIntVar[] intVarArray(int d1, int min, int max, String name) throws IloException{
        IloIntVar var[] = new IloIntVar[d1];
        for(int i=0; i<d1; i++){
            var[i] = intVar(min, max, name+Index(i, d1));
        }
        return var;
    }
    public IloIntVar[][] intVarArray(int d1, int d2, int min, int max, String name) throws IloException{
        IloIntVar var[][] = new IloIntVar[d1][];
        for(int i=0; i<d1; i++){
            var[i] = intVarArray(d2, min, max, name+Index(i, d1));
        }
        return var;
    }
    public IloIntVar[][][] intVarArray(int d1, int d2, int d3, int min, int max, String name) throws IloException{
        IloIntVar var[][][] = new IloIntVar[d1][][];
        for(int i=0; i<d1; i++){
            var[i] = intVarArray(d2, d3, min, max, name+Index(i, d1));
        }
        return var;
    }
    public IloIntVar[][][][] intVarArray(int d1, int d2, int d3, int d4, int min, int max, String name) throws IloException{
        IloIntVar var[][][][] = new IloIntVar[d1][][][];
        for(int i=0; i<d1; i++){
            var[i] = intVarArray(d2, d3, d4, min, max, name+Index(i, d1));
        }
        return var;
    }
    public IloIntVar[] boolVarArray(int d1, String name) throws IloException{
        IloIntVar var[] = new IloIntVar[d1];
        for(int i=0; i<d1; i++){
            var[i] = boolVar(name+Index(i, d1));
        }
        return var;
    }
    public IloIntVar[][] boolVarArray(int d1, int d2, String name) throws IloException{
        IloIntVar var[][] = new IloIntVar[d1][];
        for(int i=0; i<d1; i++){
            var[i] = boolVarArray(d2, name+Index(i, d1));
        }
        return var;
    }
    public IloIntVar[][][] boolVarArray(int d1, int d2, int d3, String name) throws IloException{
        IloIntVar var[][][] = new IloIntVar[d1][][];
        for(int i=0; i<d1; i++){
            var[i] = boolVarArray(d2, d3, name+Index(i, d1));
        }
        return var;
    }
    public IloIntVar[][][][] boolVarArray(int d1, int d2, int d3, int d4, String name) throws IloException{
        IloIntVar var[][][][] = new IloIntVar[d1][][][];
        for(int i=0; i<d1; i++){
            var[i] = boolVarArray(d2, d3, d4,name+Index(i, d1));
        }
        return var;
    }
    public IloIntVar[][][][][] boolVarArray(int d1, int d2, int d3, int d4, int d5, String name) throws IloException{
        IloIntVar var[][][][][] = new IloIntVar[d1][][][][];
        for(int i=0; i<d1; i++){
            var[i] = boolVarArray(d2, d3, d4, d5, name+Index(i, d1));
        }
        return var; 
    }

    public IloNumVar[][] numVarArray(int d1, int d2, double min, double max) throws IloException{
        IloNumVar var[][] = new IloNumVar[d1][];
        for(int i=0; i<d1; i++){
            var[i] = numVarArray(d2, min, max);
        }
        return var;
    }
    public IloNumVar[][][] numVarArray(int d1, int d2, int d3, double min, double max) throws IloException{
        IloNumVar var[][][] = new IloNumVar[d1][][];
        for(int i=0; i<d1; i++){
            var[i] = numVarArray(d2, d3, min, max);
        }
        return var;
    }
    public IloNumVar[][][][] numVarArray(int d1, int d2, int d3, int d4,double min, double max) throws IloException{
        IloNumVar var[][][][] = new IloNumVar[d1][][][];
        for(int i=0; i<d1; i++){
            var[i] = numVarArray(d2, d3, d4, min, max);
        }
        return var;
    }
    public IloNumVar[][][][][] numVarArray(int d1, int d2, int d3, int d4, int d5, double min, double max) throws IloException{
        IloNumVar var[][][][][] = new IloNumVar[d1][][][][];
        for(int i=0; i<d1; i++){
            var[i] = numVarArray(d2, d3, d4, d5, min, max);
        }
        return var;
    }
    
    public double[][] getValues(IloNumVar V[][]) throws UnknownObjectException, IloException{
        double X[][] = new double[V.length][];
        for(int i=0; i<V.length; i++){
            X[i] = getValues(V[i]);
        }
        return X;
    }
    public double[][][] getValues(IloNumVar V[][][]) throws UnknownObjectException, IloException{
        double X[][][] = new double[V.length][][];
        for(int i=0; i<V.length; i++){
            X[i] = getValues(V[i]);
        }
        return X;
    }
    public double[][][][] getValues(IloNumVar V[][][][]) throws UnknownObjectException, IloException{
        double X[][][][] = new double[V.length][][][];
        for(int i=0; i<V.length; i++){
            X[i] = getValues(V[i]);
        }
        return X;
    }

    public IloNumExpr Sum(IloNumExpr sum, IloNumExpr exp, double b) throws IloException{
        if(sum==null){
            return sum(exp,b);
        }else{
            return sum(sum(sum, exp),b);
        }
    }
    public IloNumExpr Sum(IloNumExpr sum, IloNumExpr exp) throws IloException{
        if(sum==null){
            return exp;
        }else{
            return sum(sum, exp);
        }
    }
    public IloNumExpr Sum(IloNumExpr M[]) throws IloException{
        return sum(M);
    }
    public IloNumExpr Sum(IloNumExpr M[][]) throws IloException{
        IloNumExpr aux[] = new IloNumExpr[M.length];
        for(int i=0; i<M.length; i++){
            aux[i] = Sum(M[i]);
        }
        return sum(aux);
    }
    public IloNumExpr Sum(IloNumExpr M[][][]) throws IloException{
        IloNumExpr aux[] = new IloNumExpr[M.length];
        for(int i=0; i<M.length; i++){
            aux[i] = Sum(M[i]);
        }
        return sum(aux);
    }
    public IloNumExpr Sum(IloNumExpr M[][][][]) throws IloException{
        IloNumExpr aux[] = new IloNumExpr[M.length];
        for(int i=0; i<M.length; i++){
            aux[i] = Sum(M[i]);
        }
        return sum(aux);
    }

    public void addSubject(Object ...obj) throws IloException{
        int signal = 1;
        int opRel = -1;
        LinkedList<IloNumExpr> A = new LinkedList<IloNumExpr>();
        double sum = 0;

        for(Object o:obj){
            if(o instanceof String){
                String s = (String)o;
                if(s.equals("+")){
                    signal = 1;
                }else if(s.equals("-")){
                    signal = -1;
                }else if(s.equals("Le") && opRel==-1){
                    signal = 1;
                    opRel = 0;
                }else if(s.equals("Eq") && opRel==-1){
                    signal = 1;
                    opRel = 1;
                }else if(s.equals("Ge") && opRel==-1){
                    signal = 1;
                    opRel = 2;
                }else{
                    throw new IloException("String s = "+s+" not valid, opRel="+opRel);
                }
            }else if(o instanceof IloNumExpr[]){
                IloNumExpr[] var = (IloNumExpr[])o;
                if(opRel==-1){
                    A.addLast(prod(signal, sum(var)));
                }else{
                    A.addLast(prod( -signal , sum(var)));
                }
                signal = 1;
            }else if(o instanceof IloNumExpr){
                IloNumExpr var = (IloNumExpr)o;
                if(opRel==-1){
                    A.addLast(prod(signal, var));
                }else{
                    A.addLast(prod( -signal , var));
                }
                signal = 1;
            }else if(o instanceof Double){
                Double constante = (Double)o;
                if(opRel==-1){
                    sum -= signal*constante;
                }else{
                    sum += signal*constante;
                }
                signal = 1;
            }else if(o instanceof Integer){
                Integer constante = (Integer)o;
                if(opRel==-1){
                    sum -= signal*constante;
                }else{
                    sum += signal*constante;
                }
                signal = 1;
            }else{
                throw new IloException("Object o = "+o+" not known");
            }
        }
        if(opRel==0){
            addLe(sum(A.toArray(new IloNumExpr[A.size()])), sum);
        }else if(opRel==1){
            addEq(sum(A.toArray(new IloNumExpr[A.size()])), sum);
        }else if(opRel==2){
            addGe(sum(A.toArray(new IloNumExpr[A.size()])), sum);
        }else{
            throw new IloException("Operator relatinal not found");
        }
    }
    
    
    
    public final IloNumVar Not(String name, IloNumVar P) throws IloException {
        IloNumVar y = numVar(0.0, 1.0, name);
        if(name==null){
            addEq(y, sum(1, prod(-1, P)));
        }else{
            addEq(y, sum(1, prod(-1, P)), name+":not");
        }
        return y;
    }
    public final IloNumVar And(IloNumVar P1, IloNumVar P2) throws IloException {
        return And(null, P1, P2);
    }
    public final IloNumVar Or(IloNumVar P1, IloNumVar P2) throws IloException {
        return Or(null, P1, P2);
    }
    public final IloNumVar XOr(IloNumVar P1, IloNumVar P2) throws IloException {
        return XOr(null, P1, P2);
    }
    public final IloNumVar IF_Then(IloNumVar P1, IloNumVar P2) throws IloException {
        return IF_Then(null, P1, P2);
    }
    public final IloNumVar IF_And_Only_IF(IloNumVar P1, IloNumVar P2) throws IloException {
        return IF_Only(null, P1, P2);
    }
    public final IloNumVar And(String name, IloNumVar P1, IloNumVar P2) throws IloException {
        IloNumVar y = numVar(0.0, 1.0, name);
        And(name, y, P1, P2);
        return y;
    }
    public final IloNumVar Or(String name, IloNumVar P1, IloNumVar P2) throws IloException {
        IloNumVar y = numVar(0.0, 1.0, name);
        Or(name, y, P1, P2);
        return y;
    }
    public final IloNumVar XOr(String name, IloNumVar P1, IloNumVar P2) throws IloException {
        IloNumVar y = numVar(0.0, 1.0, name);
        XOr(name, y, P1, P2);
        return y;
    }
    public final IloNumVar IF_Then(String name, IloNumVar P1, IloNumVar P2) throws IloException {
        IloNumVar y = numVar(0.0, 1.0, name);
        IF_Then(name, y, P1, P2);
        return y;
    }
    public final IloNumVar IF_Only(String name, IloNumVar P1, IloNumVar P2) throws IloException {
        IloNumVar y = numVar(0.0, 1.0, name);
        IF_Only(name, y, P1, P2);
        return y;
    }
    public final void And(String name, IloNumVar y, IloNumVar P1, IloNumVar P2) throws IloException {
//        add_00_0(name, y, P1, P2);
//        add_01_0(name, y, P1, P2);
//        add_10_0(name, y, P1, P2);
//        add_11_1(name, y, P1, P2);
        if(name!=null){
            addGe(y, sumArg(+1,P1, +1,P2, -1), name+":00_1");
            addLe(y, P1,                    name+":10_1");
            addLe(y, P2,                    name+":01_1");
        }else{
            addGe(y, sumArg(+1,P1, +1,P2, -1));
            addLe(y, P1);
            addLe(y, P2);
        }
    }
    public final IloNumVar And(String name, IloNumVar... Pi) throws IloException {
        IloNumVar y = numVar(0.0, 1.0, name);
        And(name, y, Pi);
        return y;
    }
    public final void And(String name, IloNumExpr y, IloNumVar... Pi) throws IloException {
//        IloNumVar wi[] = numVarArray(Pi.length, 0.0, 1.0, "w");
//        addEq(wi[0], Pi[0], name+",w[0].p[0]");
//        for(int i=1; i<Pi.length; i++){
//            And(name+",w["+i+"].w["+(i-1)+"]&p["+i+"]", wi[i], wi[i-1], Pi[i]);
//        }
        //addEq(y, wi[Pi.length-1], "y.w["+(Pi.length-1)+"]");
        addGe(y, sum(1-Pi.length, sum(Pi)), name+":Tand_1");
        for(int i=0; i<Pi.length; i++){
            addLe(y, Pi[i], name+":Tand_0");
        }
    }
    public final void Or(String name, IloNumVar y, IloNumVar P1, IloNumVar P2) throws IloException {
//        add_00_0(name, y, P1, P2);
//        add_01_1(name, y, P1, P2);
//        add_10_1(name, y, P1, P2);
//        add_11_1(name, y, P1, P2);
        if(name!=null){
            addLe(y, sumArg(+1,P1, +1,P2),     name+":00_1");
            addGe(y, P1,                    name+":10_1");
            addGe(y, P2,                    name+":01_1");
        }else{
            addLe(y, sumArg(+1,P1, +1,P2));
            addGe(y, P1);
            addGe(y, P2);
        }
    }
    public final IloNumVar Or(String name, IloNumVar... Pi) throws IloException {
        IloNumVar y = numVar(0.0, 1.0, name);
        Or(name, y, Pi);
        return y;
    }
    public final void Or(String name, IloNumVar y, IloNumVar... Pi) throws IloException {
//        IloNumVar wi[] = numVarArray(Pi.length, 0.0, 1.0, "w");
//        addEq(wi[0], Pi[0], name+",w[0].p[0]");
//        for(int i=1; i<Pi.length; i++){
//            And(name+",w["+i+"].w["+(i-1)+"]&p["+i+"]", wi[i], wi[i-1], Pi[i]);
//        }
        //addEq(y, wi[Pi.length-1], "y.w["+(Pi.length-1)+"]");
        addLe(y, sum(Pi), name+":Tor_1");
        for(int i=0; i<Pi.length; i++){
            addGe(y, Pi[i], name+":Tor_0");
        }
    }
    
    public final void XOr(String name, IloNumVar y, IloNumVar P1, IloNumVar P2) throws IloException {
        add_00_0(name, y, P1, P2);
        add_01_1(name, y, P1, P2);
        add_10_1(name, y, P1, P2);
        add_11_0(name, y, P1, P2);
    }
    public final void IF_Then(String name, IloNumVar y, IloNumVar P1, IloNumVar P2) throws IloException {
        add_00_1(name, y, P1, P2);
        add_01_1(name, y, P1, P2);
        add_10_0(name, y, P1, P2);
        add_11_1(name, y, P1, P2);
    }
    public final void IF_Only(String name, IloNumVar y, IloNumVar P1, IloNumVar P2) throws IloException {
        add_00_1(name, y, P1, P2);
        add_01_0(name, y, P1, P2);
        add_10_0(name, y, P1, P2);
        add_11_1(name, y, P1, P2);
    }
    
    private final void add_00_0(String name, IloNumVar y, IloNumVar p1, IloNumVar p2) throws IloException {
        if(name!=null){
            addLe(y, sum(p1, p2), name+":00_0");
        }else{
            addLe(y, sum(p1, p2));
        }
    }
    private final void add_01_0(String name, IloNumVar y, IloNumVar p1, IloNumVar p2) throws IloException {
        if(name!=null){
            addLe(y, sumArg(p1, -1,p2, +1), name+":01_0");
        }else{
            addLe(y, sumArg(p1, -1,p2, +1));
        }
    }
    private final void add_10_0(String name, IloNumVar y, IloNumVar p1, IloNumVar p2) throws IloException {
        if(name!=null){
            addLe(y, sumArg(p2, -1,p1, +1), name+":10_0");
        }else{
            addLe(y, sumArg(p2, -1,p1, +1));
        }
    }
    private final void add_11_0(String name, IloNumVar y, IloNumVar p1, IloNumVar p2) throws IloException {
        if(name!=null){
            addLe(y, sumArg(-1,p1, -1,p2, +2), name+":11_0");
        }else{
            addLe(y, sumArg(-1,p1, -1,p2, +2));
        }
    }
    private final void add_00_1(String name, IloNumVar y, IloNumVar p1, IloNumVar p2) throws IloException {
        if(name!=null){
            addGe(y, sumArg(-1,p1, -1,p2, +1), name+":00_1");
        }else{
            addGe(y, sumArg(-1,p1, -1,p2, +1));
        }
    }
    private final void add_01_1(String name, IloNumVar y, IloNumVar p1, IloNumVar p2) throws IloException {
        if(name!=null){
            addGe(y, sumArg(-1,p1, +1,p2), name+":01_1");
        }else{
            addGe(y, sumArg(-1,p1, +1,p2));
        }
    }
    private final void add_10_1(String name, IloNumVar y, IloNumVar p1, IloNumVar p2) throws IloException {
        if(name!=null){
            addGe(y, sumArg(+1,p1, -1,p2), name+":10_1");
        }else{
            addGe(y, sumArg(+1,p1, -1,p2));
        }
    }
    private final void add_11_1(String name, IloNumVar y, IloNumVar p1, IloNumVar p2) throws IloException {
        if(name!=null){
            addGe(y, sumArg(+1,p1, +1,p2, -1), name+":00_1");
        }else{
            addGe(y, sumArg(+1,p1, +1,p2, -1));
        }
    } 
    
    public final IloNumExpr sumArg(Object ...args) throws IloException{
//        System.out.println("-------------------------------------------------"); 
//        for(int i=0; i<args.length; i++){
//            System.out.printf("%s,", args[i]);
//        }
//        System.out.println(); 
        IloNumExpr exp = null;
        for(int i=0; i<args.length; i++){
            if(args[i] instanceof Double){
                double coef = (Double)args[i];
                if(i+1<args.length){
                    IloNumExpr var = (IloNumExpr)args[i+1];
                    i++;
                    if(exp==null){
                        exp = prod(coef, var);
                    }else{
                        exp = sum(prod(coef, var), exp);
                    }
                }else{
                    exp = sum(exp, coef);
                }
            }else if(args[i] instanceof Integer){
                double coef = (Integer)args[i];
                if(i+1<args.length){
                    IloNumExpr var = (IloNumExpr)args[i+1];
                    i++;
                    if(exp==null){
                        exp = prod(coef, var);
                    }else{
                        exp = sum(prod(coef, var), exp);
                    }
                }else{
                    exp = sum(exp, coef);
                }
            }else if(args[i] instanceof IloNumExpr){
                IloNumExpr var = (IloNumExpr)args[i];
                if(exp==null){
                    exp = var;
                }else{
                    exp = sum(var, exp);
                }
            }else{
                throw new IloException("arg["+i+"] = '"+args[i]+"' is not valid");
            }
            //System.out.printf("exp ~ %s\n", exp);
            
        }
        
        //System.out.println("exp = "+exp);
        return exp;
    }
    
    /**
     * <b>If</b> y = 1 <b>then</b> constraints &le 0;
     * @param name
     * @param y
     * @param constraints
     * @throws IloException 
     */
    public final void addIF_Y_Them_Le(String name, IloNumVar y, IloNumExpr ...constraints) throws IloException {
        for(IloNumExpr exp : constraints){
            addLe(exp, sum(bigM-epsilon, prod(-bigM, y)), name);    //Ax - b <= M(1-y) - e  || (M-e) - My
        }
    }
    /**
     * <b>If</b> y = 1 <b>then</b> constraints &ge 0;
     * @param name
     * @param y
     * @param constraints
     * @throws IloException 
     */
    public final void addIF_Y_Them_Ge(String name, IloNumVar y, IloNumExpr ...constraints) throws IloException {
        for(IloNumExpr exp : constraints){
            addGe(exp, sum(epsilon-bigM, prod(+bigM, y)), name);    //Ax - b >= M(y-1) + e  || (e-M) + My
        }
    }
    /**
     * <b>If</b> y = 1 <b>then</b> constraints = 0;
     * @param name
     * @param y
     * @param constraints
     * @throws IloException 
     */
    public final void addIF_Y_Them_Eq(String name, IloNumVar y, IloNumExpr ...constraints) throws IloException {
        //Ax - b <= M(1-y)+e
        //b - Ax <= M(1-y)+e
        for(IloNumExpr exp : constraints){
            addLe(exp, sum(+bigM+epsilon, prod(-bigM, y)), name);    //Ax - b <= M(1-y) + e  || +(M+e) - My
            addGe(exp, sum(-bigM-epsilon, prod(+bigM, y)), name);    //Ax - b >= M(y-1) - e  || -(M+e) + My
        }
    }
    /**
     * <b>Attention, requires the creation of new binary variables for each constraint</b><br>
     * <b>If</b> constraints &le 0 <b>then</b> y = 1;
     * @param name
     * @param y
     * @param constraints
     * @throws IloException 
     */
    public final void addIF_Le_Them_Y(String name, IloNumExpr y, IloNumExpr ...constraints) throws IloException {
        IloIntVar zi[] = boolVarArray(constraints.length, "z");
        for(int i=0; i<constraints.length; i++){
            addIF_Le_Them_Y(name+"["+i+"]", zi[i], constraints[i]);
        }
        And(name+".and", y, zi);
    }
    /**
     * <b>If</b> constraint &le 0 <b>then</b> y = 1;
     * @param name
     * @param y
     * @param constraint
     * @throws IloException 
     */
    public final void addIF_Le_Them_Y(String name, IloNumExpr y, IloNumExpr constraint) throws IloException {
        addGe(constraint, sum(+epsilon, prod(-bigM, y)), name);    //Ax - b >= e - My
    }
    public final void addIF_Ge_Them_Y(String name, IloNumExpr y, IloNumExpr constraint) throws IloException {
        addLe(constraint, sum(-epsilon, prod(+bigM, y)), name);    //Ax - b >= e - My
    }

    /**
     * <b>Attention, requires the creation of new binary variables for each constraint</b><br>
     * <b>If</b> constraints &le 0 <b>then</b> y = 1;
     * @param name
     * @param y
     * @param constraints
     * @throws IloException 
     */
    public final void addIF_Eq_Them_Y(String name, IloNumVar y, IloNumExpr ...constraints) throws IloException {
        IloNumExpr exp[] = new IloNumExpr[2*constraints.length];
        for(int i=0; i<constraints.length; i++){
            exp[2*i] = constraints[i];
            exp[2*i+1] = prod(-1,constraints[i]);
        }
        addIF_Le_Them_Y(name, y, exp);
    }
    public final void addIF_Eq_Them_Y(String name, IloNumVar y, IloNumExpr exp) throws IloException {
        IloIntVar z = boolVar("z");
        IloIntVar w = boolVar("w");
        addIF_Le_Them_Y(name, z, exp);
        addIF_Ge_Them_Y(name, w, exp);
        And(name+".and", y, z, w);
    }

    

    
    /**
     * <b>If</b> constraints &ge 0 <b>then</b> y = 1;
     * @param name
     * @param y
     * @param constraints
     * @throws IloException 
     */
//    public final void addIF_Ge_Them_Y(String name, IloNumVar y, IloNumExpr ...constraints) throws IloException {
//        for(IloNumExpr exp : constraints){
//            addLe(exp, sum(-epsilon, prod(+bigM, y)), name);    //Ax - b <= My - e
//        }
//    }
    /**
     * y = 1 &lt==> constraints &ge 0
     * @param name
     * @param y
     * @param constraints
     * @throws IloException 
     */
//    public final void addEq_Y_Le(String name, IloNumVar y, IloNumExpr ...constraints) throws IloException {
//        addIF_Y_Them_Le(name, y, constraints);
//        addIF_Le_Them_Y(name, y, constraints);
//    }
    /**
     * y = 1 &lt==> constraints &ge 0
     * @param name
     * @param y
     * @param constraints
     * @throws IloException 
     */
//    public final void addEq_Y_Ge(String name, IloNumVar y, IloNumExpr ...constraints) throws IloException {
//        addIF_Y_Them_Ge(name, y, constraints);
//        addIF_Ge_Them_Y(name, y, constraints);
//    }
    
}
