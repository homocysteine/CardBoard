package core;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import utils.HeadTransform;
import utils.Matrix;
import utils.Matrix3x3d;
import utils.Vector3d;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HeadTracker {

    public OrientationEKF mTracker;
    private final Vector3d mLatestGyro;
    private final Vector3d mLatestAcc;
    private float mDisplayRotation;
    private float[] mSensorToDisplay;
    private float[] mEkfToHeadTracker;
    //来自手机的外部存储空间
    private final Vector3d mGyroBias;
    private final float[] mTmpHeadView;
    private final float[] mTmpHeadView2;
    //定义一个List结构，用来存储四元数结果
    private List<float[]> quaternionList;
    //private long mLatestGyroEventClockTimeNs;

    public HeadTracker(){
        this.mTracker = new OrientationEKF();
        this.mLatestGyro = new Vector3d();
        this.mLatestAcc = new Vector3d();
        this.mDisplayRotation = Float.NaN;
        this.mSensorToDisplay = new float[16];
        this.mEkfToHeadTracker = new float[16];
        this.mGyroBias = new Vector3d();
        this.mTmpHeadView = new float[16];
        this.mTmpHeadView2 = new float[16];
        this.quaternionList = new ArrayList<>();
    }

    //需要三个参数：加速度向量、陀螺仪向量、时间戳
    public void processData(Vector3d mLatestAcc, Vector3d gyro, double[] mag,final float[] timestampList){
        //调用OrientationEKF中的processAcc函数
        //processAcc接收两个参数，一个是数据向量，一个是long类型的时间戳参数
        mTracker.processAcc(mLatestAcc,timestampList[0]);
        mTracker.processGyro(gyro,timestampList[1]);
        //mTracker.processMag(mag,timestampList[2]);
    }

    //需要一个函数来专门处理输出矩阵
    public void getLastHeadView(final float[] headView,final int offset){
        //300-340
        //300 is the best
        //float rotation = 300.0f;
        float rotation = 300.0f;

        //这里需要判断这一次的自然旋转姿态和上一次的自然旋转姿态的状态一不一样，手动模拟的时候要注意
        if(rotation != this.mDisplayRotation){
            this.mDisplayRotation = rotation;
            Matrix.setRotateEulerM(this.mSensorToDisplay,0,0.0f,0.0f,-rotation);
            Matrix.setRotateEulerM(this.mEkfToHeadTracker,0,-90.0f,0.0f,rotation);
        }

        //接收返回前，需要传入一个时间间隔参数，这里不做设置
        //mat是由so3表示的旋转矩阵转换成的长度为16的变换矩阵
        double[] mat = mTracker.getPredictedGLMatrix(0.03833333333333333);
        for(int i=0;i<headView.length;i++){
            this.mTmpHeadView[i] = (float) mat[i];
        }
        //System.out.println("变换矩阵: "+Arrays.toString(mat));

        Matrix.multiplyMM(this.mTmpHeadView2,0,this.mSensorToDisplay,0,this.mTmpHeadView,0);
        //System.out.println("mTmpHeadView2:(before) "+Arrays.toString(mTmpHeadView2));
        Matrix.multiplyMM(headView,offset,this.mTmpHeadView2,0,this.mEkfToHeadTracker,0);
        //System.out.println("mTmpHeadView2: "+Arrays.toString(mTmpHeadView2));
    }


    public void readCSV(HeadTracker headTracker, HeadTransform headTransform){
        Iterable<CSVRecord> records = null;
        try {
            Reader in = new FileReader("D:\\Logfiles\\01-Training\\01a-Regular\\T01_01\\ACCEGYROMAGN_java.csv");
            records = CSVFormat.DEFAULT.parse(in);
            boolean flag = true;
            for(CSVRecord record:records){
                if(flag==true){
                    flag = false;
                    continue;
                }

                float[] q = new float[4];
                Vector3d mLatestAcc = new Vector3d();
                Vector3d gyro = new Vector3d();

                //根据excel文件的格式，用索引读
                double xxAcc = Double.parseDouble(record.get(3));
                double yyAcc = Double.parseDouble(record.get(4));
                double zzAcc = Double.parseDouble(record.get(5));

                double xxGyro = Double.parseDouble(record.get(9));
                double yyGyro = Double.parseDouble(record.get(10));
                double zzGyro = Double.parseDouble(record.get(11));

                double mag1 = Double.parseDouble(record.get(15));
                double mag2 = Double.parseDouble(record.get(16));
                double mag3 = Double.parseDouble(record.get(17));

                float timestampAcc = Float.parseFloat(record.get(2));
                float timestampGyro = Float.parseFloat(record.get(8));
                float timestampMag = Float.parseFloat(record.get(14));

                mLatestAcc.set(xxAcc,yyAcc,zzAcc);
                gyro.set(xxGyro,yyGyro,zzGyro);
                double[] mag = {mag1,mag2,mag3};
                //1s = 1000000000 ns
                float[] timestamps = {timestampAcc,timestampGyro,timestampMag};

                headTracker.processData(mLatestAcc,gyro,mag,timestamps);
                headTracker.getLastHeadView(headTransform.getHeadView(),0);

                //需要一个将数组转变成四元组，需要设置一个HeadTransform类
                headTransform.getQuaternion(q,0);
                quaternionList.add(q);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeCSV(){
        //写入文件目录
        File csvFile = new File("D:\\CardBoard\\quaternion_6_axis_4.0.csv");
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
                float[] tempList = quaternionList.get(i);
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

    void readTxT(HeadTracker headTracker,HeadTransform headTransform){
        File file = new File("D:\\CardBoard\\test\\test8\\cardboard_data.txt");
        BufferedReader reader = null;
        String tempString = null;
        FileOutputStream fileOutputStream = null;
        File outfile = new File("D:\\CardBoard\\test\\test8\\quaternion_offline.txt");
        try {
            reader = new BufferedReader(new FileReader(file));
            fileOutputStream = new FileOutputStream(outfile);
            int line=0;
            while((tempString = reader.readLine())!=null){
                String[] datalist = tempString.split(" ");
                System.out.println(Arrays.toString(datalist));
                long time = Long.parseLong(datalist[1]);
                double xx = Double.parseDouble(datalist[2]);
                double yy = Double.parseDouble(datalist[3]);
                double zz = Double.parseDouble(datalist[4]);
                switch (tempString.charAt(0)){
                    case '1':
                        headTracker.mTracker.processAcc(new Vector3d(xx,yy,zz),0);
                        //fileOutputStream.write(("1 "+headTracker.mTracker.so3SensorFromWorld.toString()+"\n").getBytes());
                        break;
                    case '2':
                        headTracker.mTracker.processGyro(new Vector3d(xx,yy,zz),time);
                        //fileOutputStream.write(("2 "+headTracker.mTracker.so3SensorFromWorld.toString()+"\n").getBytes());
                        break;
                    case '3':
                        line++;
                        headTracker.getLastHeadView(headTransform.getHeadView(),0);
                        float[] tempq = new float[4];
                        headTransform.getQuaternion(tempq,0);
                        fileOutputStream.write((Arrays.toString(tempq)+"\n").getBytes());
                        //fileOutputStream.write((Arrays.toString()+"\n").getBytes());
                        break;
                    default:
                        break;
                }
            }
            reader.close();
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //原算法的数据来源是传感器，此处要读取csv文件中的数据
    public static void main(String[] args) {
        HeadTracker headTracker = new HeadTracker();
        HeadTransform headTransform = new HeadTransform();
        //自动化读写代码
        headTracker.readCSV(headTracker,headTransform);
        headTracker.writeCSV();

        //模拟真实数据
        //headTracker.readTxT(headTracker,headTransform);

    }
}
