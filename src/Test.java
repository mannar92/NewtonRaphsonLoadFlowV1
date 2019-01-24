import org.apache.commons.math3.complex.Complex;
public class Test {

    public static void main(String [] Args){

        Complex z = new Complex (0.0122, 0.02);

        Complex[][] matrix = new Complex[29][29];

        for (int i=0; i<matrix.length; i++){
            //System.out.println(i);
            for (int j=0; j<matrix.length; j++){
                //System.out.println(j);
                matrix[i][j] = new Complex (0, 0);
                System.out.println(matrix[i][j]);
            }
        }


    }
}
