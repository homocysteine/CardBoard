package core;

import utils.HeadTransform;
import utils.Matrix;
import utils.Vector3d;

import java.util.Arrays;

public class HeadTracker {

    private OrientationEKF mTracker;
    private final Vector3d mLatestGyro;
    private final Vector3d mLatestAcc;
    private float mDisplayRotation;
    private double[] mSensorToDisplay;
    private double[] mEkfToHeadTracker;
    //来自手机的外部存储空间
    private final Vector3d mGyroBias;
    private final double[] mTmpHeadView;
    private final double[] mTmpHeadView2;

    public HeadTracker(){
        this.mTracker = new OrientationEKF();
        this.mLatestGyro = new Vector3d();
        this.mLatestAcc = new Vector3d();
        this.mDisplayRotation = Float.NaN;
        this.mSensorToDisplay = new double[16];
        this.mEkfToHeadTracker = new double[16];
        this.mGyroBias = new Vector3d();
        this.mTmpHeadView = new double[16];
        this.mTmpHeadView2 = new double[16];
    }

    //需要三个参数：加速度向量、陀螺仪向量、时间戳
    public void processData(Vector3d mLatestAcc, Vector3d gyro, final long timestamps){
        //调用OrientationEKF中的processAcc函数
        //processAcc接收两个参数，一个是数据向量，一个是long类型的时间戳参数
        mTracker.processAcc(mLatestAcc,timestamps);
        mTracker.processGyro(gyro,timestamps);
    }

    //需要一个函数来专门处理输出矩阵
    public void getLastHeadView(final double[] headView,final int offset){
        //用一个double数组接收来自OrientationEKF对象的getPredictGLMatrix函数
        //getPredictedGLMatrix接收一个double类型的函数
        float rotation = 0.0f;

        if(rotation != this.mDisplayRotation){
            this.mDisplayRotation = rotation;
            Matrix.setRotateEulerM(this.mSensorToDisplay,0,0.0f,0.0f,-rotation);
            Matrix.setRotateEulerM(this.mEkfToHeadTracker,0,-90.0f,0.0f,rotation);
        }

        //接收返回前，需要传入一个时间间隔参数，这里不做设置
        //mat是由so3表示的旋转矩阵转换成的长度为16的变换矩阵
        double[] mat = mTracker.getPredictedGLMatrix(1);
        for(int i=0;i<headView.length;i++){
            this.mTmpHeadView[i] = mat[i];
        }
        //如何实现multiplyMM？接受的是两个数组参数，返回一个数组参数
        Matrix.multiplyMM(this.mTmpHeadView2,0,this.mSensorToDisplay,0,this.mTmpHeadView,0);
        Matrix.multiplyMM(headView,offset,this.mTmpHeadView2,0,this.mEkfToHeadTracker,0);

    }

    //需要一个将数组转变成四元组，需要设置一个HeadTransform类

    //原算法的数据来源是传感器，此处要读取csv文件中的数据
    public static void main(String[] args) {
        double[] q = new double[4];

        Vector3d mLatestAcc = new Vector3d();
        Vector3d gyro = new Vector3d();

        mLatestAcc.set(1,2,3);
        gyro.set(1,2,3);

        HeadTracker cardBoard = new HeadTracker();
        HeadTransform head = new HeadTransform();

        cardBoard.processData(mLatestAcc,gyro,0);
        cardBoard.getLastHeadView(head.getHeadView(),0);

        head.getQuaternion(q,0);

        System.out.println(Arrays.toString(q));
    }
}
