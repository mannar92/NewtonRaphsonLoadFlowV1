import Jama.Matrix;
import org.apache.commons.math3.complex.Complex;
public class Test {

    public static void main(String [] Args){

        /*
        Complex z = new Complex (0.0122, 0.02);


        Complex[][] matrix = new Complex[29][29];

        for (int i=0; i<matrix.length; i++){
            //System.out.println(i);
            for (int j=0; j<matrix.length; j++){
                //System.out.println(j);
                matrix[i][j] = new Complex (0, 0);
                System.out.println(matrix[i][j]);
            }
            */
        double[][] aa = new double[3][3];
        double[] bb = new double[3];

        aa[0][0] = 1;
        aa[0][1] = 2;
        aa[0][2] = 3;
        aa[1][0] = 11;
        aa[1][1] = 12;
        aa[1][2] = 13;
        aa[2][0] = 101;
        aa[2][1] = 1.7;
        aa[2][2] = 198;

        bb[0] = 89;
        bb[1] = 54;
        bb[2] = 2.6;
        /*
        bb[0][1] = 0;
        bb[0][2] = 0;
        bb[1][1] = 0;
        bb[1][2] = 0;
        bb[2][1] = 0;
        bb[2][2] = 0;
*/
        /*
        Matrix jac = new Matrix(aa);
            Matrix jacInv = jac.inverse();
            double[][] jacInvDouble = jacInv.getArray();
        */
        for (int i=0; i<aa.length; i++){
            System.out.println();
            for (int j=0; j<aa.length; j++){
                System.out.print("\t" + aa[i][j]);
            }
        }
        System.out.println();
        for (int i=0; i<bb.length; i++) {

                System.out.print("\t" + bb[i]);

        }
        Matrix aMatrix = new Matrix(aa);
        Matrix bMatrix = new Matrix(bb, 3);

        //double[][] multiply2D = aMatrix.times(bMatrix).getArray();
        double[] multiply1D = aMatrix.times(bMatrix).getColumnPackedCopy();

        /*
        System.out.println();
        System.out.printf("2D Array");
        System.out.println();
        for (int i=0; i<multiply2D.length; i++){
            for(int j=0; j<multiply2D.length;j++){
                System.out.println("\t" + multiply2D[i][j]);
            }
        }
*/
        System.out.println();
        System.out.printf("1D Array");
        System.out.println();
        for(int i=0; i<multiply1D.length;i++){
             System.out.println("\t" + multiply1D[i]);
        }
    }
}
