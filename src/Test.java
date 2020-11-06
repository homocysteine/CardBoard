import org.ejml.data.DMatrix;
import org.ejml.data.DMatrixRMaj;
import org.ejml.data.DenseD2Matrix64F;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.CommonOps_FDRM;
import org.ejml.ops.CommonOps_BDRM;

public class Test {
    public static void main(String[] args) {
        //维护一个数组的矩阵
        DMatrixRMaj operand1 = new DMatrixRMaj(3,3);
        DMatrixRMaj operand2 = new DMatrixRMaj(3,3);
        DMatrixRMaj res = new DMatrixRMaj(3,3);
        int count=0;
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                count++;
                operand1.set(i,j,count);
                operand2.set(i,j,2);
            }
        }
        CommonOps_DDRM.mult(operand1,operand2,res);
        System.out.println(operand1);
        System.out.println(operand2);
        System.out.println(res.toString());
        System.out.println(res.get(0,0));
        System.out.println(res.get(8));
    }
}
