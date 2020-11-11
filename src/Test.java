import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.ejml.data.DMatrix;
import org.ejml.data.DMatrixRMaj;
import org.ejml.data.DenseD2Matrix64F;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.CommonOps_FDRM;
import org.ejml.ops.CommonOps_BDRM;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class Test {
    public static void main(String[] args) {
        //维护一个数组的矩阵
//        DMatrixRMaj operand1 = new DMatrixRMaj(3,3);
//        DMatrixRMaj operand2 = new DMatrixRMaj(3,3);
//        DMatrixRMaj res = new DMatrixRMaj(3,3);
//        int count=0;
//        for(int i=0;i<3;i++){
//            for(int j=0;j<3;j++){
//                count++;
//                operand1.set(i,j,count);
//                operand2.set(i,j,2);
//            }
//        }
//        CommonOps_DDRM.mult(operand1,operand2,res);
//        System.out.println(operand1);
//        System.out.println(operand2);
//        System.out.println(res.toString());
//        System.out.println(res.get(0,0));
//        System.out.println(res.get(8));

        Iterable<CSVRecord> records = null;
        try {
            Reader in = new FileReader("D:\\Logfiles\\01-Training\\01a-Regular\\T01_01\\ACCEGYROMAGN.csv");
            records = CSVFormat.DEFAULT.parse(in);
            boolean flag = true;
            int count = 0;
            for(CSVRecord record:records){
                //System.out.println(record.get(0));
                count++;
                if(flag==true){
                    flag = false;
                    continue;
                }
                //根据excel文件的格式，用索引读，比较方便
                System.out.println(record.get(2)+" "+record.get(3)+" "+record.get(4)+" "+record.get(5));
                System.out.println(record.get(8)+" "+record.get(9)+" "+record.get(10)+" "+record.get(11));
                System.out.println(record.get(14)+" "+record.get(15)+" "+record.get(16)+" "+record.get(17));
                if(count == 3){
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
