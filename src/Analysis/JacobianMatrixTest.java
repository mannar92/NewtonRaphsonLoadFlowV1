package Analysis;

import networkModel.Branch;
import networkModel.Bus;
import networkModel.Generation;

public class JacobianMatrixTest {

    private int numOfBuses;
    //private double[] voltages;
    //private double[] angles;
    private double[] realPowerSpecified;
    private double[] reactivePowerSpecified;
    private double[] realPower;
    private double[] reactivePower;
    private double[] realPowerMismatch;
    private double[] reactivePowerMismatch;
    private double[] deltaPower;
    private double[] deltaVoltage;
    private boolean[] busFlag;
    private double[][] jac;
    private double[][] simplifiedJac;
    private boolean[][] keepJacElement;


    public JacobianMatrixTest(AdmittanceMatrix admittanceMatrix, Bus[] bus,
                              Generation[] generations, Branch[] branch) {
        numOfBuses = bus.length;

        //  populate arrays with zeros
        zeroArrays();

        //determine power of implicit power flow equations
        for (int k=0; k<numOfBuses; k++){
            specifiedPower(bus[k], generations);
            powerFlowEquations(admittanceMatrix, bus, bus[k], branch);
            mismatchPowerEquations(bus[k]);
            determineJacobianMatrix(admittanceMatrix, bus, bus[k], branch);
        }

        simplifyJac(bus);

        for (int k=0; k<numOfBuses; k++){
            deltaPower[k] = realPowerMismatch[k];
            deltaPower[k+numOfBuses] = reactivePowerMismatch[k];
        }
    }

    private void mismatchPowerEquations(Bus busK) {
        int busIndex = (int) busK.getBusID() - 1 ;

        if (busK.isGenerationBus()) {
            realPowerMismatch[busIndex] = realPowerSpecified[busIndex] - realPower[busIndex];

        } else if (busK.isLoadBus()) {
            realPowerMismatch[busIndex] = realPowerSpecified[busIndex] - realPower[busIndex];
            reactivePowerMismatch [busIndex] = reactivePowerSpecified[busIndex] - reactivePower[busIndex];
        }
    }

    private void simplifyJac(Bus[] bus) {
        for (int k=0; k<numOfBuses; k++) {
            // Jacobian rows to be kept
            for (int i=0; i<numOfBuses; i++){
                if (bus[k].isGenerationBus()){
                    busFlag[k] = true;
                    keepJacElement[k][i] = true;
                } else if (bus[k].isLoadBus()) {
                    busFlag[k] = true;
                    busFlag[k+numOfBuses] = true;
                    keepJacElement[k][i] = true;
                    keepJacElement[k+numOfBuses][i] = true;
                    keepJacElement[i][k + numOfBuses] = true;
                    keepJacElement[k+numOfBuses][k+numOfBuses] = true;
                }
            }
        }

        for (int k=0; k<numOfBuses; k++){
            for (int i=0; i<numOfBuses; i++){
                if (bus[k].isSlackBus()) {
                    busFlag[k] = false;
                    busFlag[k+numOfBuses] = false;
                    keepJacElement[k][i] = false;
                    keepJacElement[k][i+numOfBuses] = false;
                    keepJacElement[k+numOfBuses][i] = false;
                    keepJacElement[k+numOfBuses][i+numOfBuses] = false;
                    keepJacElement[i][k] = false;
                    keepJacElement[i][k+numOfBuses] = false;
                    keepJacElement[i+numOfBuses][k] = false;
                    keepJacElement[i+numOfBuses][k+numOfBuses] = false;
                }
            }
        }
        /*
        // print flags
        for (int k=0; k<numOfBuses*2; k++){
            System.out.println();
            for (int i=0; i<numOfBuses*2; i++){
                System.out.print(keepJacElement[k][i]+"\t");
            }
            System.out.println();
        }*/


        int row = 0;
        int column = 0;
        for (int k=0; k<(numOfBuses*2); k++) {
            for (int i=0; i<(numOfBuses*2); i++) {
                if (keepJacElement[k][i]) {
                   // System.out.println("row: " + row);
                    //System.out.println("column: "+column);

                    simplifiedJac[row][column] = jac[k][i];

                    if (column < (numOfBuses-1)){
                        column++;
                    }
                }
            }
            column =0;
            if (row<(numOfBuses-1) && busFlag[k]) {
                row++;
            }
        }
    }

    private void determineJacobianMatrix(AdmittanceMatrix admittanceMatrix, Bus[] bus,
                                         Bus busK, Branch[] branch) {
        int k = (int) busK.getBusID() -1;
        double vk = busK.getVoltageMagnitude();
        double vi;
        double angleki;
        double gki;
        double bki;
        double gkk;
        double bkk;


        for (Bus busi: bus) {
            if (busK.isGenerationBus()) {
                int i = (int) busi.getBusID() - 1;
                vi = busi.getVoltageMagnitude();
                angleki = busK.getVoltageAngle() - busi.getVoltageAngle();
                gki = admittanceMatrix.getConductance()[k][i];
                bki = admittanceMatrix.getSusceptance()[k][i];

                if (k != i) {
                    jac[k][i] = - (vk * vi * (gki * Math.sin(angleki) - bki * Math.cos(angleki)));
                    jac[k][i + numOfBuses] = -(vi * (gki * Math.cos(angleki) + bki * Math.sin(angleki)));
                    jac[k + numOfBuses][i] = 0.0;
                    jac[k + numOfBuses][i + numOfBuses] = 0.0;
                } else { // k=i
                    jac[k][i] += vk * vi * (gki * Math.sin(angleki) - bki * Math.cos(angleki));
                    jac[k][i + numOfBuses] += -(vi * (gki * Math.cos(angleki) + bki * Math.sin(angleki)));
                    jac[k + numOfBuses][i] += 0.0;
                    jac[k + numOfBuses][i + numOfBuses] += 0.0;
                }
                /*jac[k][k] += +bki * vk * vk;
                jac[k][k + numOfBuses] += -gki * vk * vk;*/
            } else if (busK.isLoadBus()) {
                int i = (int) busi.getBusID() - 1;
                vi = busi.getVoltageMagnitude();
                angleki = busK.getVoltageAngle() - busi.getVoltageAngle();
                gki = admittanceMatrix.getConductance()[k][i];
                bki = admittanceMatrix.getSusceptance()[k][i];

                if (k != i) {
                    jac[k][i] = - (vk * vi * (gki * Math.sin(angleki) - bki * Math.cos(angleki)));
                    jac[k][i + numOfBuses] =  -(vi * (gki * Math.cos(angleki) + bki * Math.sin(angleki)));
                    jac[k + numOfBuses][i] = vk * vi * (gki * Math.cos(angleki) + bki * Math.sin(angleki));
                    jac[k + numOfBuses][i + numOfBuses] = -(vk * (gki * Math.sin(angleki)                     //  partial derivative of Qk with respect to voltage at bus i.
                            - bki * Math.cos(angleki)));
                } else { // k=i
                    jac[k][k] += vk * vi * (gki * Math.sin(angleki) - bki * Math.cos(angleki));
                    jac[k][k + numOfBuses] += -(vi * (gki * Math.cos(angleki) + bki * Math.sin(angleki)));
                    jac[k + numOfBuses][k] += -(vk * vi * (gki * Math.cos(angleki) + bki * Math.sin(angleki)));
                    jac[k + numOfBuses][k + numOfBuses] += -(vi * (gki * Math.sin(angleki)
                            - bki * Math.cos(angleki)));
                }
                /*
                jac[k][k] += +bki * vk * vk;
                jac[k][k + numOfBuses] += -gki * vk * vk;
                jac[k + numOfBuses][k] += +gki * vk * vk;
                jac[k + numOfBuses][k + numOfBuses] += +bki * vk * vk; */
            }
        }

        /*
        gkk = admittanceMatrix.getConductance()[k][k];
        bkk = admittanceMatrix.getSusceptance()[k][k];
        if (busK.isGenerationBus()) {
            jac[k][k] += bkk * vk * vk;
            jac[k][k + numOfBuses] += -gkk * vk * vk;
        } else if (busK.isLoadBus()) {
            jac[k][k] += bkk * vk * vk;
            jac[k][k + numOfBuses] += -gkk * vk * vk;
            jac[k + numOfBuses][k] += +gkk * vk * vk;
            jac[k + numOfBuses][k + numOfBuses] += +bkk * vk * vk;
        }*/
    }
    private void powerFlowEquations(AdmittanceMatrix admittanceMatrix, Bus[] bus,
                                    Bus busK, Branch[] branch) {
        //int busIndex = (int) busK.getBusID() - 1 ;

        if (busK.isGenerationBus()){
            // Real power known = Pgeneration - Pload at busK
            realPowerEquations(admittanceMatrix, bus, busK, branch);

        } else if (busK.isLoadBus()){
            realPowerEquations(admittanceMatrix, bus, busK, branch);
            reactivePowerEquation(admittanceMatrix, bus, busK, branch);

        }
    }

    private void reactivePowerEquation(AdmittanceMatrix admittanceMatrix, Bus[] bus,
                                       Bus busK, Branch[] branch) {
        int busIndex = (int) busK.getBusID() - 1 ;
        double vk = busK.getVoltageMagnitude();         //  voltage magnitude at bus k
        double vi;                              //  voltage magnitude at bus i
        double angleki;                         //  angle difference between bus k and i
        double gki;                             //  conductance at line between bus k and i
        double bki;                             //  susceptance at line between bus k and i

        for (Branch line: branch){
            if (((int)line.getFromBusID() -1)== busIndex){
                int toBusIndex = (int) line.getToBusID() - 1;
                vi = bus[toBusIndex].getVoltageMagnitude();
                angleki = busK.getVoltageAngle() - bus[toBusIndex].getVoltageAngle();
                gki = admittanceMatrix.getConductance()[busIndex][toBusIndex];
                bki = admittanceMatrix.getSusceptance()[busIndex][toBusIndex];

                reactivePower[busIndex] += vk*vi*(gki*Math.sin(angleki) - bki*Math.cos(angleki));

            } else if (((int) line.getToBusID()-1) == busIndex){
                int toBusIndex = (int) line.getFromBusID() -1;
                vi = bus[toBusIndex].getVoltageMagnitude();
                angleki = busK.getVoltageAngle() - bus[toBusIndex].getVoltageAngle();
                gki = admittanceMatrix.getConductance()[busIndex][toBusIndex];
                bki = admittanceMatrix.getSusceptance()[busIndex][toBusIndex];

                reactivePower[busIndex] += vk*vi*(gki*Math.sin(angleki) - bki*Math.cos(angleki));
            }
        }

    }

    private void realPowerEquations(AdmittanceMatrix admittanceMatrix, Bus[] bus,
                                    Bus busK, Branch[] branch) {

        int busIndex = (int) busK.getBusID() - 1 ;
        double vk = busK.getVoltageMagnitude();         //  voltage magnitude at bus k
        double vi;                              //  voltage magnitude at bus i
        double angleki;                         //  angle difference between bus k and i
        double gki;                             //  conductance at line between bus k and i
        double bki;                             //  susceptance at line between bus k and i

        for (Branch line: branch){
            if (((int)line.getFromBusID() -1)== busIndex){
                int toBusIndex = (int) line.getToBusID() - 1;
                vi = bus[toBusIndex].getVoltageMagnitude();
                angleki = busK.getVoltageAngle()- bus[toBusIndex].getVoltageAngle();
                gki = admittanceMatrix.getConductance()[busIndex][toBusIndex];
                bki = admittanceMatrix.getSusceptance()[busIndex][toBusIndex];

                realPower[busIndex] += vk*vi*(gki*Math.cos(angleki) + bki*Math.sin(angleki));

            } else if (((int) line.getToBusID()-1) == busIndex){
                int toBusIndex = (int) line.getFromBusID() -1;
                vi = bus[toBusIndex].getVoltageMagnitude();
                angleki = busK.getVoltageAngle()-bus[toBusIndex].getVoltageAngle();
                gki = admittanceMatrix.getConductance()[busIndex][toBusIndex];
                bki = admittanceMatrix.getSusceptance()[busIndex][toBusIndex];

                realPower[busIndex] += vk*vi*(gki*Math.cos(angleki) + bki*Math.sin(angleki));
            }
        }
    }

    // gets real/ reactive power specified for Generation and Load buses
    private void specifiedPower(Bus busK,
                                Generation[] generations) {
        int busIndex = (int) busK.getBusID() - 1 ;

        if (busK.isGenerationBus()){
            System.out.println("Bus " + busK.getBusID() + "is a PV (Generation) bus");
            // Real power known = Pgeneration - Pload at busK
            for (Generation generation: generations){
                if (generation.getBusID() == busK.getBusID()) {
                    realPowerSpecified[busIndex] += generation.getRealPowerOutput();
                }
            }
            realPowerSpecified[busIndex] -= busK.getRealPowerDemand();

            //voltages[busIndex] = busK.getVoltageMagnitude();

        } else if (busK.isLoadBus()){
            System.out.println("Bus " + busK.getBusID() + "is a PQ (Load) bus");
            realPowerSpecified[busIndex] = busK.getRealPowerDemand();
            reactivePowerSpecified[busIndex] = busK.getReactivePowerDemand();

        } else if (busK.isSlackBus()){
            System.out.println("Bus " + busK.getBusID() + "is the Slack bus");
            //voltages[busIndex] = busK.getVoltageMagnitude();
           // angles[busIndex] = busK.getVoltageAngle();
        }
    }

    private void zeroArrays() {
        //voltages = new double[numOfBuses];
        //angles = new double[numOfBuses];
        realPowerSpecified = new double[numOfBuses];
        reactivePowerSpecified = new double[numOfBuses];
        realPower = new double[numOfBuses];
        reactivePower = new double[numOfBuses];
        realPowerMismatch = new double[numOfBuses];
        reactivePowerMismatch = new double[numOfBuses];
        busFlag = new boolean[numOfBuses*2];
        jac = new double[numOfBuses*2][numOfBuses*2];
        deltaPower = new double[numOfBuses*2];
        keepJacElement = new boolean[numOfBuses*2][numOfBuses*2];
        simplifiedJac = new double[numOfBuses][numOfBuses];

        for (int k=0; k<numOfBuses; k++){
            //voltages[k] = 1.0;
           // angles[k] = 0.0;
            realPowerSpecified[k] = 0.0;
            reactivePowerSpecified[k] = 0.0;
            realPower[k] = 0.0;
            reactivePower[k] = 0.0;
            reactivePowerMismatch[k] = 0.0;
            realPowerMismatch[k] = 0.0;
            busFlag[k] = false;
            busFlag[k+numOfBuses] = false;
            for (int i=0; i<numOfBuses; i++){
                jac[k][i] = 0.0;
                jac[k][i+numOfBuses] = 0.0;
                jac[k+numOfBuses][i] = 0.0;
                jac[k+numOfBuses][i+numOfBuses] = 0.0;
                keepJacElement[k][i] = false;
                simplifiedJac[k][i] = 0.0;
            }
        }
    }

    public void printJac() {
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

    public void printSimplifiedJac() {
        try{
            System.out.println();
            System.out.println("=====================================================================");
            System.out.println("=================    Simplified Jacobian Matrix     =================");
            System.out.println("=====================================================================");

            double rows = simplifiedJac.length;
            double columns = simplifiedJac[0].length;
            String str = "|\t";

            for(int i=0;i<rows;i++){
                for(int j=0;j<columns;j++){
                    str += simplifiedJac[i][j] + "\t";
                }

                System.out.println(str + "|");
                str = "|\t";
            }

        }catch(Exception e){System.out.println("Matrix is empty!!");}
    }

    public double[] getDeltaPower() {
        int index =0;
        double[] implicitDeltaPower = new double[numOfBuses];
        for (int k=0; k<numOfBuses*2; k++){
            if (busFlag[k]){
                implicitDeltaPower[index] = deltaPower[k];
                if (index < (numOfBuses-1)) {
                    index++;
                }
            }
        }
        return implicitDeltaPower;
    }

    public double[] getDeltaVoltage() {
        return deltaVoltage;
    }

    public double[][] getJac() {
        return jac;
    }
    public double[][] getSimplifiedJac() {
        return simplifiedJac;
    }

    public boolean[] getBusFlag() {
        return busFlag;
    }
}
