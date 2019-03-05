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
            deltaPower = jacobian.getDeltaPower();



            if (convergeCheck(bus) || iterations == maxIterations){
                for (int k=0; k<bus.length; k++) {
                    bus[k].setRealPowerGeneration(jacobian.getDeltaPower()[k]);
                    bus[k].setReactivePowerGeneration(jacobian.getDeltaPower()[k+bus.length]);
                }
                break;
            }
            //jacInverse = determineMatrixInverse(jacobian.getJac());
            //deltaVoltage = determineMatrixMultiplication(jacInverse, deltaPower);

            //updateVoltageValues(bus, deltaVoltage);


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

    private void updateVoltageValues(Bus[] bus, double[] deltaVoltage) {
        for (int i=0; i<bus.length; i++){
            double angle = bus[i].getVoltageAngle();
            double voltage = bus[i].getVoltageMagnitude();
            bus[i].setVoltageAngle(angle+deltaVoltage[i]);
            bus[i].setVoltageMagnitude(voltage+deltaVoltage[i+bus.length]);
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
