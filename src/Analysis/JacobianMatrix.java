package Analysis;

import networkModel.Branch;
import networkModel.Bus;
import networkModel.Generation;

public class JacobianMatrix {

    private double[][] jac;  // jacobian matrix


    public JacobianMatrix(AdmittanceMatrix admittanceMatrix, Bus[] bus) {
        jac = new double[bus.length*2][bus.length*2];

        // number of but k
        int k;
        for (k =0; k <bus.length; k++){
            //  number of bus i
            int i;
            for (i =0; i <bus.length; i++){
                // voltage magnitude at bus k
                double vk = bus[k].getVoltageMagnitude();
                // voltage magnitude at bus i
                double vi = bus[i].getVoltageMagnitude();
                // angle difference between voltages k and i
                double angleki = bus[k].getVoltageAngle() - bus[i].getVoltageAngle();
                // line conductance from bus k to i
                double gki = admittanceMatrix.getConductance()[k][i];
                // line susceptance from bus k to i
                double bki = admittanceMatrix.getSusceptance()[k][i];
                if (!bus[k].isSlackBus()) {
                        if (bus[k].isGenerationBus()) {
                            if (k != i) {
                                jac[k][i] += vk * vi * (gki * Math.sin(angleki) - bki * Math.cos(angleki));              //  partial derivative Pk with respect to angle at bus i.
                                jac[k][i + bus.length] += vk * (gki * Math.cos(angleki) + bki * Math.sin(angleki));       //  partial derivative of Pk with respect to voltage at bus i.
                                jac[k + bus.length][i] = 0.0; //  partial derivative of Qk with respect to angle at bus i.
                                jac[k + bus.length][i + bus.length] = 0.0;
                            }   else { // k == i
                                for (int n = 0; n < bus.length; n++) {
                                    vi = bus[n].getVoltageMagnitude();
                                    angleki = bus[k].getVoltageAngle() - bus[n].getVoltageAngle();
                                    gki = admittanceMatrix.getConductance()[k][n];
                                    bki = admittanceMatrix.getSusceptance()[k][n];
                                    jac[k][i] += vk * vi * (-gki * Math.sin(angleki) + bki * Math.cos(angleki));          // partial derivative Pk with respect to angle k
                                    jac[k][i + bus.length] += vi * (gki * Math.cos(angleki) + bki * Math.sin(angleki));   //  partial derivative Pk with respect to voltage k
                                    jac[k + bus.length][i] = 0.0;//  partial derivative Qk with respect to angle at bus k
                                    jac[k + bus.length][i + bus.length] = 0.0;                                            //  partial derivative Qk with respect to voltage at bus k
                                }
                                jac[k][i] += -bki * vk * vk;
                                jac[k][i + bus.length] += gki * vk * vk;
                            //jac[k][i + bus.length] += -gki * vk * vk;
                            }
                        }  //jac[k + bus.length][i + bus.length] += -bki * vk * vk;
                        else if (bus[k].isLoadBus()){
                            if (k != i) {
                                jac[k][i] += vk * vi * (gki * Math.sin(angleki) - bki * Math.cos(angleki));              //  partial derivative Pk with respect to angle at bus i.
                                jac[k][i + bus.length] += vk * (gki * Math.cos(angleki) + bki * Math.sin(angleki));       //  partial derivative of Pk with respect to voltage at bus i.
                                jac[k + bus.length][i] += vk * vi * (-gki * Math.cos(angleki) - bki * Math.sin(angleki)); //  partial derivative of Qk with respect to angle at bus i.
                                jac[k + bus.length][i + bus.length] += vk * (gki * Math.sin(angleki)                     //  partial derivative of Qk with respect to voltage at bus i.
                                         - bki * Math.cos(angleki));
                            }   else { // k == i
                                for (int n = 0; n < bus.length; n++) {
                                    vi = bus[n].getVoltageMagnitude();
                                    angleki = bus[k].getVoltageAngle() - bus[n].getVoltageAngle();
                                    gki = admittanceMatrix.getConductance()[k][n];
                                    bki = admittanceMatrix.getSusceptance()[k][n];
                                    jac[k][i] += vk * vi * (-gki * Math.sin(angleki) + bki * Math.cos(angleki));          // partial derivative Pk with respect to angle k
                                    jac[k][i + bus.length] += vi * (gki * Math.cos(angleki) + bki * Math.sin(angleki));   //  partial derivative Pk with respect to voltage k
                                    jac[k + bus.length][i] += vk * vi * (gki * Math.cos(angleki) + bki * Math.sin(angleki));//  partial derivative Qk with respect to angle at bus k
                                    jac[k + bus.length][i + bus.length] += vi * (gki * Math.sin(angleki)
                                         - bki * Math.cos(angleki));                                            //  partial derivative Qk with respect to voltage at bus k
                                }
                                jac[k][i] += -bki * vk * vk;
                                jac[k][i + bus.length] += gki * vk * vk;
                                jac[k][i + bus.length] += -gki * vk * vk;
                                jac[k + bus.length][i + bus.length] += -bki * vk * vk;
                            }
                        }
                    }
                }
            }
        }


    public double[][] getJac() {
        return jac;
    }

    public double getJacValue(int k, int i){
        return jac[k][i];
    }

    public void setJac(int row, int column, double value) {
        jac[row][column] = value;
    }

    public void printJac(){
        try{
            System.out.println();
            System.out.println("=====================================================================");
            System.out.println("=======================    Jacobian Matrix    =======================");
            System.out.println("=====================================================================");

            double rows = jac.length;
            double columns = jac[0].length;
            String str = "|\t";

            for(int i=0;i<rows;i++){
                for(int j=0;j<columns;j++){
                    str += jac[i][j] + "\t";
                }

                System.out.println(str + "|");
                str = "|\t";
            }

        }catch(Exception e){System.out.println("Matrix is empty!!");}
    }
}
                                       