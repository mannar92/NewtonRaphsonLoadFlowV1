package Analysis;

import org.apache.commons.math3.complex.Complex;
import networkModel.Branch;
import networkModel.Bus;

public class AdmittanceMatrix {

    private double[][] conductance;
    private double[][] susceptance;

    //private double[][] yMatrix;
    private Complex[] diagonalElements;

    private Complex[][] yMatrix;



    public AdmittanceMatrix(Bus[] bus, Branch[] branch){
       // yMatrix = new double[bus.length][bus.length];
        diagonalElements = new Complex[bus.length];

        yMatrix = new Complex[diagonalElements.length][diagonalElements.length];

        conductance = new double[bus.length][bus.length];
        susceptance = new double[bus.length][bus.length];

        setArraysToZero();

        for (Branch line: branch){
            double resistance = line.getResistance();
            double reactance = line.getReactance();
            double real = resistance / (resistance * resistance + reactance * reactance);
            double imaginary = -reactance / (resistance * resistance + reactance * reactance);

            Complex admittance = new Complex(real, imaginary);

            setDiagonalElements(line, admittance);
            setOffDiagonalElement(line, admittance);
        }

        System.out.println();
        System.out.println(diagonalElements[0]);
        System.out.println(diagonalElements[9]);
        System.out.println(diagonalElements[28]);
        System.out.println();

        for (int i=0; i<diagonalElements.length; i++){
            yMatrix[i][i]=diagonalElements[i];
            System.out.println("Admittance Y" + (i+1) + (i+1) + " = " + diagonalElements[i]);
        }

        for (int i=0; i<yMatrix.length; i++){
            System.out.println("Bus 1 admittance line: ");
            System.out.println("Y" + 1 + (i+1) + " = " + yMatrix[0][i]);
            System.out.println();
        }
        setConductance();
        setSusceptance();
    }

    private void setArraysToZero() {
        for (int i=0; i<diagonalElements.length; i++){
            diagonalElements[i] = new Complex(0,0);
        }

        for (int i=0; i<diagonalElements.length; i++){
            for (int j=0; j<diagonalElements.length; j++){
                yMatrix[i][j] = new Complex (0, 0);
            }
        }

        for (int i=0; i<conductance.length; i++){
            for (int j=0; j<conductance.length; j++){
                conductance[i][j] = 0.0;
            }
        }

        for (int i=0; i<susceptance.length; i++){
            for (int j=0; j<susceptance.length; j++){
                susceptance[i][j] = 0.0;
            }
        }
    }

    private void setSusceptance() {
        for (int i=0; i<yMatrix.length; i++){
            for (int j=0; j<yMatrix.length; j++){
                susceptance[i][j] = yMatrix[i][j].getImaginary();
            }
        }
    }

    private void setConductance() {
        for (int i=0; i<yMatrix.length; i++){
            for (int j=0; j<yMatrix.length; j++){
                conductance[i][j] = yMatrix[i][j].getReal();
            }
        }
    }

    // sets an array of Complex objects representing the diagonal elements of the admittance matrix.
    private void setDiagonalElements(Branch line, Complex admittance) {

       switch ((int) line.getFromBusID()){
            case 1:
                diagonalElements[0] = diagonalElements[0].add(admittance);
                break;
            case 2:
                diagonalElements[1] = diagonalElements[1].add(admittance);
                break;
            case 3:
                diagonalElements[2] = diagonalElements[2].add(admittance);
                break;
            case 4:
                diagonalElements[3] = diagonalElements[3].add(admittance);
                break;
            case 5:
                diagonalElements[4] = diagonalElements[4].add(admittance);
                break;
            case 6:
                diagonalElements[5] = diagonalElements[5].add(admittance);
                break;
            case 7:
                diagonalElements[6] = diagonalElements[6].add(admittance);
                break;
            case 8:
                diagonalElements[7] = diagonalElements[7].add(admittance);
                break;
            case 9:
                diagonalElements[8] = diagonalElements[8].add(admittance);
                break;
            case 10:
                diagonalElements[9] = diagonalElements[9].add(admittance);
                break;
            case 11:
                diagonalElements[10] = diagonalElements[10].add(admittance);
                break;
            case 12:
                diagonalElements[11] = diagonalElements[1].add(admittance);
                break;
            case 13:
                diagonalElements[12] = diagonalElements[12].add(admittance);
                break;
            case 14:
                diagonalElements[13] = diagonalElements[13].add(admittance);
                break;
            case 15:
                diagonalElements[14] = diagonalElements[14].add(admittance);
                break;
            case 16:
                diagonalElements[15] = diagonalElements[15].add(admittance);
                break;
            case 17:
                diagonalElements[16] = diagonalElements[16].add(admittance);
                break;
            case 18:
                diagonalElements[17] = diagonalElements[17].add(admittance);
                break;
            case 19:
                diagonalElements[18] = diagonalElements[18].add(admittance);
                break;
            case 20:
                diagonalElements[19] = diagonalElements[19].add(admittance);
                break;
            case 21:
                diagonalElements[20] = diagonalElements[20].add(admittance);
                break;
            case 22:
                diagonalElements[21] = diagonalElements[21].add(admittance);
                break;
            case 23:
                diagonalElements[22] = diagonalElements[22].add(admittance);
                break;
            case 24:
                diagonalElements[23] = diagonalElements[23].add(admittance);
                break;
            case 25:
                diagonalElements[24] = diagonalElements[24].add(admittance);
                break;
            case 26:
                diagonalElements[25] = diagonalElements[25].add(admittance);
                break;
            case 27:
                diagonalElements[26] = diagonalElements[26].add(admittance);
                break;
            case 28:
                diagonalElements[27] = diagonalElements[27].add(admittance);
                break;
            case 29:
                diagonalElements[28] = diagonalElements[28].add(admittance);
                break;
            default:
                break;
        }
    }

    private void setOffDiagonalElement(Branch line, Complex admittance) {
        int fromBus = (int) line.getFromBusID();
        int toBus = (int) line.getToBusID();

        Complex zeroComplex = new Complex (0, 0);

        if (yMatrix[fromBus - 1][toBus - 1].equals(zeroComplex)){
            yMatrix[fromBus - 1][toBus - 1] = admittance.negate(); // -admittance
        } else {
           yMatrix[fromBus - 1][toBus - 1] =
                   yMatrix[fromBus - 1][toBus - 1].add(admittance.negate());
        }
    }

    public Complex[][] getyMatrix() {
        return yMatrix;
    }

    public double[][] getConductance() {
        return conductance;
    }

    public double[][] getSusceptance() {
        return susceptance;
    }

    public Complex getyMatrixElement(int fromBus, int toBus) {
        return yMatrix[fromBus - 1][toBus - 1];
    }
}
