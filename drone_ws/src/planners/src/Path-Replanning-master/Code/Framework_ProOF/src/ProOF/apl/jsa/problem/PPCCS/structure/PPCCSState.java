/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ProOF.apl.jsa.problem.PPCCS.structure;

/**
 * Classe que modela os estados do VANT.
 * @author Jesimar
 */
public class PPCCSState {
    
    /**
     * Posição do VANT (m).
     */
    private final Position position;
    
    /**
     * Velocidade do VANT (m/s).
     */
    private final double speed;
    
    /**
     * Angulo de direção do VANT (radianos).
     */
    private final double angle;        
   
    /**
     * Construtor dos estados do VANT.
     * @param positionX - posição X do VANT.
     * @param positionY - posição Y do VANT.
     * @param speed - velocidade do VANT.
     * @param angle - angulo direcional do VANT.
     */
    public PPCCSState(double positionX, double positionY, double speed, double angle) {
        this.position = new Position2D(positionX, positionY);        
        this.speed = speed;
        this.angle = angle;
    } 
    
    /**
     * Construtor dos estados do VANT.
     * @param position - posição do VANT.
     * @param speed - velocidade do VANT.
     * @param angle - angulo direcional do VANT.
     */
    public PPCCSState(Position position, double speed, double angle) {
        this.position = position;
        this.speed = speed;
        this.angle = angle;
    }
    
    /**
     * Construtor dos estados do VANT.
     * @param state - vetor de estados {positionX, positionY, velocidade, angle}.
     */
    public PPCCSState(double state[]) {
        this.position = new Position2D(state[0], state[1]);
        this.speed = state[2];
        this.angle = state[3];
    } 

    /**
     * Transição de um estado para o próximo baseados em equações de dinâmica
     * para um ambiente 2D.
     * @param typeFail - tipo de falha associada na mudança de estados.
     * @param u - controles do VANT.
     * @param dt - variação de tempo entre dois estados consecutivos.
     * @param G - constante (força) de resistencia do ar. 
     * @return novo estado do VANT dadas o estado anterior e os controles do VANT.
     */
    public PPCCSState nextState(TypeOfCriticalSituation typeFail, PPCCSControl u, 
            double dt, double G) {        
        
        
        double pXf = 0.0;
        double pYf = 0.0;
        double vf = 0.0;
        double newAngle = 0.0;
        
        double pX = getPositionX();
        double pY = getPositionY();        
        double v = getSpeed();
        double l = u.getLeme();
        double a = u.getAceleration();
        
        if (typeFail == TypeOfCriticalSituation.NOTHING){
            pXf = pX + v * Math.cos(angle) * dt + a * Math.cos(angle) * dt * dt / 2.0;
            pYf = pY + v * Math.sin(angle) * dt + a * Math.sin(angle) * dt * dt / 2.0;            
            vf = v + a * dt - G * v * v * dt;
            newAngle = angle + l;
       } else if (typeFail == TypeOfCriticalSituation.FAIL_ENGINE){
            pXf = pX + v * Math.cos(angle) * dt;
            pYf = pY + v * Math.sin(angle) * dt;
            vf = v - G * v * v * dt;
            newAngle = angle + l;
        } else if (typeFail == TypeOfCriticalSituation.FAIL_BATTERY){
            pXf = pX + v * Math.cos(angle) * dt + a * Math.cos(angle) * dt * dt / 2.0;
            pYf = pY + v * Math.sin(angle) * dt + a * Math.sin(angle) * dt * dt / 2.0;            
            vf = v + a * dt - G * v * v * dt;
            newAngle = angle + l;
        } else if (typeFail == TypeOfCriticalSituation.FAIL_TURN_LEFT_ONLY){
            pXf = pX + v * Math.cos(angle) * dt + a * Math.cos(angle) * dt * dt / 2.0;
            pYf = pY + v * Math.sin(angle) * dt + a * Math.sin(angle) * dt * dt / 2.0;
            vf = v + a * dt - G * v * v * dt;
            double alpha = l < 0 ? 0 : l;
            newAngle = angle + alpha;
        } else if (typeFail == TypeOfCriticalSituation.FAIL_TURN_RIGHT_ONLY){
            pXf = pX + v * Math.cos(angle) * dt + a * Math.cos(angle) * dt * dt / 2.0;
            pYf = pY + v * Math.sin(angle) * dt + a * Math.sin(angle) * dt * dt / 2.0;
            vf = v + a * dt - G * v * v * dt;
            double alpha = l > 0 ? 0 : l;
            newAngle = angle + alpha;
        }
        vf = Math.max(vf, 0.0);
        
        return new PPCCSState(pXf, pYf, vf, newAngle);        
    }   

    public double getPositionX() {
        if (position instanceof Position2D){
            return ((Position2D)position).getX();
        }
        else if (position instanceof Position3D){
            return ((Position3D)position).getX();
        }
        return 0.0;
    }

    public double getPositionY() {
        if (position instanceof Position2D){
            return ((Position2D)position).getY();
        }
        else if (position instanceof Position3D){
            return ((Position3D)position).getY();
        }
        return 0.0;
    }
    
    public Position getPosition() {
        return position;
    }

    public double getSpeed() {
        return speed;
    }

    public double getAngle() {
        return angle;
    }
}
