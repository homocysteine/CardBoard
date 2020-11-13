package utils;

import org.ejml.data.DMatrixRMaj;
import org.ejml.data.DenseD2Matrix64F;
import org.ejml.dense.row.CommonOps_DDRM;

import java.util.Arrays;

public class Matrix {
    public static void setRotateEulerM(double[] rm, int rmOffset,
                                       double x, double y, double z) {
        x *= (Math.PI / 180.0f);
        y *= (Math.PI / 180.0f);
        z *= (Math.PI / 180.0f);
        double cx = Math.cos(x);
        double sx = Math.sin(x);
        double cy = Math.cos(y);
        double sy = Math.sin(y);
        double cz = Math.cos(z);
        double sz = Math.sin(z);
        double cxsy = cx * sy;
        double sxsy = sx * sy;

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

    //矩阵的乘法没有错误....有错误！！！！OpenGL是列优先
    public static void multiplyMM(double[] result, int resultOffset,double[] lhs, int lhsOffset, double[] rhs, int rhsOffset){
        DMatrixRMaj lhsM = DMatrixRMaj.wrap(4,4,lhs);
        CommonOps_DDRM.transpose(lhsM);

        DMatrixRMaj rhsM = DMatrixRMaj.wrap(4,4,rhs);
        CommonOps_DDRM.transpose(rhsM);

        DMatrixRMaj resM = DMatrixRMaj.wrap(4,4,result);

        CommonOps_DDRM.mult(lhsM,rhsM,resM);
        CommonOps_DDRM.transpose(resM);

        System.out.println(Arrays.toString(result));

        CommonOps_DDRM.transpose(lhsM);
        CommonOps_DDRM.transpose(rhsM);

        for(int i=0;i<result.length;i++){
            result[i] = resM.get(i);
            lhs[i] = lhsM.get(i);
            rhs[i] = rhsM.get(i);
        }
    }

    public static void setIdentityM(double[] sm, int smOffset) {
        for (int i=0 ; i<16 ; i++) {
            sm[smOffset + i] = 0;
        }
        for(int i = 0; i < 16; i += 5) {
            sm[smOffset + i] = 1.0f;
        }
    }
}
