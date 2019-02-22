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
        //this.adjustError = adjustError;
        this.convergeError = convergeError;
        this.enforceReactiveLimits = enfoceReactiveLimits;
    }

    public boolean solve(Bus[] bus, Generation[] generation, Branch[] branch){

       iterations = 0;

       //determine Y-Matrix
        AdmittanceMatrix yMatrix = new AdmittanceMatrix(bus,branch);

        //starts NR algorithm
        for(int i=0; i<maxIterations; i++){
            iterations++;
            System.out.println();
            System.out.printf("Number of Iteration: "+iterations);
            System.out.println();

            /*
             * Power values and the Jacobian matrix are calculated using
             * current voltage values and system impedance values.
             */
            MismatchPower mismatchPower = new MismatchPower(yMatrix, bus, generation, branch);
            JacobianMatrix jacobian = new JacobianMatrix(yMatrix, bus);


            deltaPower = mismatchPower.getDeltaPower();
            jacInverse = determineMatrixInverse(jacobian.getJac());

            if (convergeCheck(bus) || iterations == maxIterations){
                for (int i=0; i<bus.length; i++) {
                    bus[i].setRealPowerGeneration(mismatchPower.getPowerInj()[i]);
                    bus[i].setReactivePowerGeneration(mismatchPower.getPowerInj()[i+bus.length]);
                }
            }

            deltaVoltage = determineMatrixMultiplication(jacInverse, deltaPower);

            updateVoltageValues(bus, deltaVoltage);
        }
        return false;
    }

    //check if algorithm has successfully converged
    //NOTE: adjust error is not consided in this algorithm
    private boolean convergeCheck(Bus[] bus) {
        isConverge = true;

        for (int i=0; i<bus.length; i++) {
            if (bus[i].isGenerationBus() && !bus[i].isSlackBus()) {
                //check real power mismatch only
                if (Math.abs(deltaPower[i]) > convergeError) {
                    isConverge = false;
                    System.out.println("\nGeneration bus " + bus[i].getBusID() + "ΔP > ε\n");
                }
            } else { // load bus (PQ bus)
                //check both real and reactive power mismatches
                /*if ((Math.abs(deltaPower[i]) > adjustError)
                        || (Math.abs(deltaPower[i+bus.length]) > adjustError)) {
                    adjustFlag = false;*/

                if (Math.abs(deltaPower[i]) > convergeError) {
                    isConverge = false;
                    System.out.println("\nLoad bus " + bus[i].getBusID() + "ΔP > ε\n");
                } else {
                    isConverge = false;
                    System.out.println("\nLoad bus " + bus[i].getBusID() + "ΔQ > ε\n");
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
