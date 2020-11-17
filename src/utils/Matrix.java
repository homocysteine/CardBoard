package utils;

import org.ejml.data.DMatrixRMaj;
import org.ejml.data.DenseD2Matrix64F;
import org.ejml.dense.row.CommonOps_DDRM;

import java.math.BigDecimal;
import java.util.Arrays;

public class Matrix {
    public static void setRotateEulerM(float[] rm, int rmOffset,
                                       float x, float y, float z) {
        x *= (float)(Math.PI / 180.0f);
        y *= (float)(Math.PI / 180.0f);
        z *= (float)(Math.PI / 180.0f);
        float cx = (float) Math.cos(x);
        float sx = (float) Math.sin(x);
        float cy = (float) Math.cos(y);
        float sy = (float) Math.sin(y);
        float cz = (float) Math.cos(z);
        float sz = (float) Math.sin(z);
        float cxsy = cx * sy;
        float sxsy = sx * sy;

        rm[rmOffset + 0]  =   cy * cz;
        rm[rmOffset + 1]  =  -cy * sz;
        rm[rmOffset + 2]  =   sy;
        rm[rmOffset + 3]  =  0.0f;

        rm[rmOffset + 4]  =  cxsy * cz + cx * sz;
        rm[rmOffset + 5]  = -cxsy * sz + cx * cz;
        rm[rmOffset + 6]  =  -sx * cy;
        rm[rmOffset + 7]  =  0.0f;

        rm[rmOffset + 8]  = -sxsy * cz + sx * sz;
        rm[rmOffset + 9]  =  sxsy * sz + sx * cz;
        rm[rmOffset + 10] =  cx * cy;
        rm[rmOffset + 11] =  0.0f;

        rm[rmOffset + 12] =  0.0f;
        rm[rmOffset + 13] =  0.0f;
        rm[rmOffset + 14] =  0.0f;
        rm[rmOffset + 15] =  1.0f;
    }

    static double[] double2Float(float[] list){
        double[] doubleList = new double[list.length];
        for(int i=0;i<list.length;i++){
            doubleList[i] = new BigDecimal(String.valueOf(list[i])).doubleValue();
        }
        return doubleList;
    }

    //矩阵的乘法没有错误....有错误！！！！OpenGL是列优先
    public static void multiplyMM(float[] result, int resultOffset,float[] lhs, int lhsOffset, float[] rhs, int rhsOffset){
        //double->float不会出现数据误差
        //float->double会出现数据误差
        double[] lhsList = double2Float(lhs);
        double[] rhsList = double2Float(rhs);
        double[] resultList = double2Float(result);
        DMatrixRMaj lhsM = DMatrixRMaj.wrap(4,4,lhsList);
        CommonOps_DDRM.transpose(lhsM);

        DMatrixRMaj rhsM = DMatrixRMaj.wrap(4,4,rhsList);
        CommonOps_DDRM.transpose(rhsM);

        DMatrixRMaj resM = DMatrixRMaj.wrap(4,4,resultList);

        CommonOps_DDRM.mult(lhsM,rhsM,resM);
        CommonOps_DDRM.transpose(resM);

        System.out.println(Arrays.toString(result));

        CommonOps_DDRM.transpose(lhsM);
        CommonOps_DDRM.transpose(rhsM);

        for(int i=0;i<result.length;i++){
            result[i] = (float) resM.get(i);
            lhs[i] = (float) lhsM.get(i);
            rhs[i] = (float) rhsM.get(i);
        }
    }

    public static void setIdentityM(float[] sm, int smOffset) {
        for (int i=0 ; i<16 ; i++) {
            sm[smOffset + i] = 0;
        }
        for(int i = 0; i < 16; i += 5) {
            sm[smOffset + i] = 1.0f;
        }
    }
}
