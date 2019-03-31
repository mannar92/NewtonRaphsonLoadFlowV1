package Analysis;

import Jama.Matrix;
import networkModel.Branch;
import networkModel.Bus;
import networkModel.Generation;

public class NewtonRaphsonLoadFlow {

    private final int maxIterations;
    private final double adjustError;
    private final double convergeError;
    private final boolean enforceReactiveLimits;
    private int iterations;
    private double[] deltaPower;
    private double[][] jacInverse;
    private double[] deltaVoltage;
    private boolean adjustFlag;
    private boolean isConverge;


    /**
     * @param maxIterations Maximum number of iterations of the NR algorithm
     * @param adjustError Value of power mismatch that determines when generator reactive
     *                    power limits should be examin
     * @param convergeError Value of power mismatch that determines the system has converged
     * @param enfoceReactiveLimits  Set to tru if reactive power limits are enforced
     */
    public NewtonRaphsonLoadFlow(int maxIterations, double adjustError,
                                 double convergeError, boolean enfoceReactiveLimits) {
        this.maxIterations = maxIterations;
        this.adjustError = adjustError;
        this.convergeError = convergeError;
        this.enforceReactiveLimits = enfoceReactiveLimits;
    }

    public boolean solve(Bus[] bus, Generation[] generation, Branch[] branch){

       iterations = 0;

       //determine Y-Matrix
        AdmittanceMatrix yMatrix = new AdmittanceMatrix(bus,branch);
        yMatrix.printMatrix();

        //starts NR algorithm
        for(int i=0; i<maxIterations; i++){
            iterations++;
            System.out.println();
            System.out.printf("Number of Iteration: "+iterations);
            System.out.println();

            JacobianMatrixTest jacobian = new JacobianMatrixTest(yMatrix, bus, generation, branch);
            jacobian.printJac();
            jacobian.printSimplifiedJac();
            deltaPower = jacobian.getDeltaPower();
            printDeltaPower();


            if (convergeCheck(bus) || iterations == maxIterations){
                for (int k=0; k<bus.length; k++) {
                    bus[k].setRealPowerGeneration(jacobian.getDeltaPower()[k]);
                    bus[k].setReactivePowerGeneration(jacobian.getDeltaPower()[k+bus.length]);
                }
                break;
            }

            jacInverse = determineMatrixInverse(jacobian.getSimplifiedJac());
            deltaVoltage = determineMatrixMultiplication(jacInverse, deltaPower);
            printDeltaVoltage();

            updateVoltageValues(bus, deltaVoltage, jacobian.getBusFlag());


            /*
             * Power values and the Jacobian matrix are calculated using
             * current voltage values and system impedance values.

            MismatchPower mismatchPower = new MismatchPower(yMatrix, bus, generation, branch);
            mismatchPower.print();
            JacobianMatrix jacobian = new JacobianMatrix(yMatrix, bus);

            deltaPower = mismatchPower.getDeltaPower();

            if (convergeCheck(bus) || iterations == maxIterations){
                for (int k=0; k<bus.length; k++) {
                    bus[k].setRealPowerGeneration(mismatchPower.getPowerInj()[k]);
                    bus[k].setReactivePowerGeneration(mismatchPower.getPowerInj()[k+bus.length]);
                }
                break;
            }

           // zeroJacElements(jacobian, bus);

            jacobian.printJac();

            jacInverse = determineMatrixInverse(jacobian.getJac());
            deltaVoltage = determineMatrixMultiplication(jacInverse, deltaPower);

            updateVoltageValues(bus, deltaVoltage);
            */
        }
        return false;
    }

    private void printDeltaVoltage() {
        try{
            System.out.println();
            System.out.println("=====================================================================");
            System.out.println("====================    Delta Voltage Matrix    =====================");
            System.out.println("=====================================================================");

            double rows = deltaVoltage.length;
            //double columns = deltaPower[0].length;
            String str = "|\t";

            for(int i=0;i<rows;i++){
                //for(int j=0;j<columns;j++){
                str += deltaVoltage[i] + "\t";
                // }

                System.out.println(str + "|");
                str = "|\t";
            }

        }catch(Exception e){System.out.println("Matrix is empty!!");}
    }

    private void printDeltaPower() {
        try{
            System.out.println();
            System.out.println("=====================================================================");
            System.out.println("=======================    Delta Power Matrix    =======================");
            System.out.println("=====================================================================");

            double rows = deltaPower.length;
            //double columns = deltaPower[0].length;
            String str = "|\t";

            for(int i=0;i<rows;i++){
                //for(int j=0;j<columns;j++){
                    str += deltaPower[i] + "\t";
               // }

                System.out.println(str + "|");
                str = "|\t";
            }

        }catch(Exception e){System.out.println("Matrix is empty!!");}
    }

    private void simplifyJac() {
    }

    private void zeroJacElements(JacobianMatrix jacobian, Bus[] bus) {

        for (int k=0; k<bus.length; k++){
            // zero P and Q elements for the slack bus
            if (bus[k].isSlackBus()){
                for (int i=0; i<bus.length; i++){
                    jacobian.setJac(k, i, 0.0);                                 // partial P/theta
                    jacobian.setJac(k, (i+bus.length), 0.0);                    // partial P/V
                    jacobian.setJac((k+bus.length), i, 0.0);                    // partial Q/theta
                    jacobian.setJac((k+bus.length), (i+bus.length), 0.0);       // partial Q/V
                    jacobian.setJac((i+bus.length), k, 0.0);
                    jacobian.setJac(i, (k+bus.length), 0.0);
                    jacobian.setJac((i+bus.length), (k+bus.length), 0.0);
                    jacobian.setJac(i, k, 0.0);
                    //jacobian.setJac(k, k, 1e+10);
                    //  jacobian.setJac((k+bus.length), (k+bus.length), 1e+10);
                }
            }


            if (bus[k].isGenerationBus()){
                for (int i=0; i<bus.length; i++){
                    //jacobian.setJac(k, i, 0.0);                                      // partial P/theta
                    //jacobian.setJac(k, (i+bus.length), 0.0);                    // partial P/V
                    //jacobian.setJac((k+bus.length), i, 0.0);                        // partial Q/theta
                    jacobian.setJac((k+bus.length), (i+bus.length), 0.0);      // partial Q/V
                    jacobian.setJac((i+bus.length), k, 0.0);
                    jacobian.setJac((k+bus.length), i, 0.0);

                    //jacobian.setJac(i, (k+bus.length), 0.0);
                    jacobian.setJac((i+bus.length), (k+bus.length), 0.0);
                    //jacobian.setJac(i, k, 0.0);
                    //jacobian.setJac((k+bus.length), (k+bus.length), 0.0);
                }
            }
        }
    }

    //check if algorithm has successfully converged
    //NOTE: adjust error is not considered in this algorithm
    private boolean convergeCheck(Bus[] bus) {
        isConverge = true;

        for (int i=0; i<bus.length; i++) {
            if (bus[i].isGenerationBus() && !bus[i].isSlackBus()) {
                //check real power mismatch only
                if (Math.abs(deltaPower[i]) > convergeError) {
                    isConverge = false;
                    System.out.println("Generation bus " + bus[i].getBusID() + "ΔP > ε");
                }
            } else { // load bus (PQ bus)
                //check both real and reactive power mismatches
                /*if ((Math.abs(deltaPower[i]) > adjustError)
                        || (Math.abs(deltaPower[i+bus.length]) > adjustError)) {
                    adjustFlag = false;*/

                if (Math.abs(deltaPower[i]) > convergeError) {
                    isConverge = false;
                    System.out.println("Load bus " + bus[i].getBusID() + "ΔP > ε");
                } else {
                    isConverge = false;
                    System.out.println("Load bus " + bus[i].getBusID() + "ΔQ > ε");
                }
            }
        }
        return isConverge;
    }

    private void updateVoltageValues(Bus[] bus, double[] deltaVoltage, boolean[] busFlag) {
        boolean[] realPowerFlag = new boolean[bus.length];
        boolean[] reactivePowerFlag = new boolean[bus.length];

        System.out.println();
        for (int k=0; k<bus.length; k++){
            realPowerFlag[k] = busFlag[k];
            reactivePowerFlag[k] = busFlag[k+bus.length];
        }
        for (int k=0; k<bus.length; k++){
            if (realPowerFlag[k]){
                double angle = bus[k].getVoltageAngle();
                bus[k].setVoltageAngle(angle+deltaVoltage[k]);
                System.out.println("New Voltage angle at bus "+bus[k].getBusID()+" ="+bus[k].getVoltageAngle());
            }
            if (reactivePowerFlag[k]){
                double voltage = bus[k].getVoltageMagnitude();
                bus[k].setVoltageMagnitude(voltage+deltaVoltage[k]);
                System.out.println("New Voltage magnitude at bus "+bus[k].getBusID()+" ="+bus[k].getVoltageMagnitude());
            }
        }
    }

    // A*B matrix multiplication
    private double[] determineMatrixMultiplication(double[][] a, double[] b) {
        Matrix aMatrix = new Matrix(a);
        Matrix bMatrix = new Matrix(b, b.length);
        return aMatrix.times(bMatrix).getColumnPackedCopy();
    }

    private double[][] determineMatrixInverse(double[][] matrix) {
        Matrix matrixInv = new Matrix(matrix);
        return matrixInv.inverse().getArray();
    }
}
