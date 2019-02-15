package Analysis;

import networkModel.Branch;
import networkModel.Bus;
import networkModel.Generation;

public class JacobianMatrix {

    private int k;          // number of but k
    private int i;          //  number of bus i
    private double vk;      // voltage magnitude at bus k
    private double vi;      // voltage magnitude at bus i
    private double angleki; // angle difference between voltages k and i
    private double gki;     // line conductance from bus k to i
    private double bki;      // line susceptance from bus k to i
    private double[][] jac;  // jacobian matrix


    public JacobianMatrix(AdmittanceMatrix admittanceMatrix, Bus[] bus,
                          Branch[] branch, Generation[] generations,
                          PowerFlowEquations[] powerFlowEquations) {

        jac = new double[bus.length*2][bus.length*2];

        for (int k=0; k<bus.length; k++){
            for (int i=0; i<bus.length; i++){
                vk = bus[k].getVoltageMagnitude();
                vi = bus[i].getVoltageMagnitude();
                angleki = bus[k].getVoltageAngle()-bus[i].getVoltageAngle();
                gki = admittanceMatrix.getConductance()[k][i];
                bki = admittanceMatrix.getSusceptance()[k][i];
                if (k!=i){
                    jac[k][i] += vk*vi*(gki*Math.sin(angleki)-bki*Math.cos(angleki));               //  partial derivative Pk with respect to angle at bus i.
                    jac[k][i+bus.length] += vk*(gki*Math.cos(angleki)+bki*Math.sin(angleki));       //  partial derivative of Pk with respect to voltage at bus i.
                    jac[k+bus.length][i] += vk*vi*(-gki*Math.cos(angleki)-bki*Math.sin(angleki));   //  partial derivative of Qk with respect to angle at bus i.
                    jac[k+bus.length][i+bus.length] += vk*(gki*Math.sin(angleki)                    //  partial derivative of Qk with respect to voltage at bus i.
                            -bki*Math.cos(angleki));
                }
                else { // k == i
                    for (int n = 0; n<bus.length; n++){
                        vi = bus[n].getVoltageMagnitude();
                        angleki = bus[k].getVoltageAngle()-bus[n].getVoltageAngle();
                        gki = admittanceMatrix.getConductance()[k][n];
                        bki = admittanceMatrix.getSusceptance()[k][n];
                        jac[k][i] += vk*vi*(-gki*Math.sin(angleki)+bki*Math.cos(angleki));          // partial derivative Pk with respect to angle k
                        jac[k][i+bus.length] += vi*(gki*Math.cos(angleki)+bki*Math.sin(angleki));   //  partial derivative Pk with respect to voltage k
                        jac[k+bus.length][i] += vk*vi*(gki*Math.cos(angleki)+bki*Math.sin(angleki));//  partial derivative Qk with respect to angle at bus k
                        jac[k+bus.length][i+bus.length] += vi*(gki*Math.sin(angleki)
                                -bki*Math.cos(angleki));                                            //  partial derivative Qk with respect to voltage at bus k
                    }
                    jac[k][i] += -bki*vk*vk;
                    jac[k][i+bus.length] += gki*vk*vk;
                    jac[k][i+bus.length] += -gki*vk*vk;
                    jac[k+bus.length][i+bus.length] += -bki*vk*vk;
                }
            }
        }
    }
}
