import java.io.*;


public class Test {

    void readTxT(){
        File file = new File("D:\\CardBoard\\cardboard_data_2.txt");
        BufferedReader reader = null;
        String tempString = null;
        FileOutputStream fileOutputStream = null;
        File outfile = new File("D:\\CardBoard\\cardboard_quaternion_2.txt");
        try {
            fileOutputStream = new FileOutputStream(outfile);
            reader = new BufferedReader(new FileReader(file));
            int line = 0;
            while((tempString = reader.readLine())!=null){
                System.out.println(tempString);
                if(tempString.charAt(0)=='3'){
                    line++;
                    tempString+="\n";
                    fileOutputStream.write((line+" "+tempString.substring(2)).getBytes());
                }
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            reader.close();
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


    public static void main(String[] args) {
        //维护一个数组的矩阵
        Test test = new Test();
        test.readTxT();
    }
}
