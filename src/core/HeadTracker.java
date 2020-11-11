package core;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import utils.HeadTransform;
import utils.Matrix;
import utils.Vector3d;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    //定义一个List结构，用来存储四元数结果
    private List<double[]> quaternionList;

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
        this.quaternionList = new ArrayList<>();
    }

    //需要三个参数：加速度向量、陀螺仪向量、时间戳
    public void processData(Vector3d mLatestAcc, Vector3d gyro, double[] mag,final double[] timestampList){
        //调用OrientationEKF中的processAcc函数
        //processAcc接收两个参数，一个是数据向量，一个是long类型的时间戳参数
        //process和getPrediction的调用顺序？？
        mTracker.processAcc(mLatestAcc,timestampList[0]);
        mTracker.processGyro(gyro,timestampList[1]);
        mTracker.processMag(mag,timestampList[2]);
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


    public void readCSV(){
        Iterable<CSVRecord> records = null;
        try {
            Reader in = new FileReader("D:\\Logfiles\\01-Training\\01a-Regular\\T01_01\\ACCEGYROMAGN.csv");
            records = CSVFormat.DEFAULT.parse(in);
            boolean flag = true;
            for(CSVRecord record:records){
                if(flag==true){
                    flag = false;
                    continue;
                }

                double[] q = new double[4];
                Vector3d mLatestAcc = new Vector3d();
                Vector3d gyro = new Vector3d();

                //根据excel文件的格式，用索引读，比较方便
                double xxAcc = Double.parseDouble(record.get(3));
                double yyAcc = Double.parseDouble(record.get(4));
                double zzAcc = Double.parseDouble(record.get(5));

                double xxGyro = Double.parseDouble(record.get(9));
                double yyGyro = Double.parseDouble(record.get(10));
                double zzGyro = Double.parseDouble(record.get(11));

                double mag1 = Double.parseDouble(record.get(15));
                double mag2 = Double.parseDouble(record.get(16));
                double mag3 = Double.parseDouble(record.get(17));

                double timestampAcc = Double.parseDouble(record.get(2));
                double timestampGyro = Double.parseDouble(record.get(8));
                double timestampMag = Double.parseDouble(record.get(14));

                mLatestAcc.set(xxAcc,yyAcc,zzAcc);
                gyro.set(xxGyro,yyGyro,zzGyro);
                double[] mag = {mag1,mag2,mag3};
                //1s = 1000000000 ns
                double[] timestamps = {timestampAcc,timestampGyro,timestampMag};

                HeadTracker cardBoard = new HeadTracker();
                HeadTransform head = new HeadTransform();

                cardBoard.processData(mLatestAcc,gyro,mag,timestamps);
                cardBoard.getLastHeadView(head.getHeadView(),0);

                //需要一个将数组转变成四元组，需要设置一个HeadTransform类
                head.getQuaternion(q,0);
                quaternionList.add(q);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeCSV(){
        //写入文件目录
        File csvFile = new File("D:\\CardBoard\\quaternion.csv");
        //输入输出流
        BufferedWriter csvFileOutputStream = null;
        //定义CSVHeader
        String[] fileheaders = {"index","scalar","vector1","vector2","vector3"};
        //定义CSV format
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(fileheaders);
        //定义CSVPrinter
        CSVPrinter csvPrinter = null;
        try {
            csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile)));
            csvPrinter = new CSVPrinter(csvFileOutputStream,csvFileFormat);
            for(int i=0;i<quaternionList.size();i++){
                double[] tempList = quaternionList.get(i);
                csvPrinter.print(i);
                for(int j=0;j<tempList.length;j++){
                    csvPrinter.print(tempList[j]);
                }
                csvPrinter.println();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                csvFileOutputStream.flush();
                csvFileOutputStream.close();
                csvPrinter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //原算法的数据来源是传感器，此处要读取csv文件中的数据
    public static void main(String[] args) {
        HeadTracker headTracker = new HeadTracker();
        headTracker.readCSV();
        for(double[] i:headTracker.quaternionList){
            System.out.println(Arrays.toString(i));
        }
        headTracker.writeCSV();
    }
}
