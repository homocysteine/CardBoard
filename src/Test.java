import core.HeadTracker;
import utils.Vector3d;

import java.io.*;
import java.util.Arrays;
import java.util.Vector;


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
        HeadTracker headTracker = new HeadTracker();
        double[][] datalist = {
                { 1,-2.1164743900299072, 0.28730419278144836, 9.47146224975586 },
                { 1,-2.1164743900299072, 0.28730419278144836, 9.452308654785156 },
                { 1,-2.1164743900299072, 0.28730419278144836, 9.461885452270508 },
                { 1,-2.0877439975738525, 0.3064578175544739, 9.423578262329102 },
                {1, -2.097320795059204, 0.2777273952960968, 9.442731857299805 },
                {1, -2.1164743900299072, 0.2777273952960968, 9.452308654785156 },
                { 1,-2.1068975925445557, 0.2777273952960968, 9.442731857299805 },
                { 1,-2.1164743900299072, 0.26815059781074524, 9.433155059814453 },
                { 1,-2.1164743900299072, 0.26815059781074524, 9.423578262329102 },
                { 1,-2.126051187515259, 0.26815059781074524, 9.404424667358398 },
                { 1,-2.126051187515259, 0.26815059781074524, 9.404424667358398 },
                { 1,-2.1164743900299072, 0.28730419278144836, 9.41400146484375 },
                { 1,-2.097320795059204, 0.26815059781074524, 9.423578262329102 },
                { 1,-2.078166961669922, 0.26815059781074524, 9.423578262329102 },
                { 1,-2.1068975925445557, 0.26815059781074524, 9.423578262329102 },
                { 1,-2.1164743900299072, 0.26815059781074524, 9.41400146484375 },
                { 1,-2.1164743900299072, 0.2777273952960968, 9.41400146484375 },
                { 1,-2.1164743900299072, 0.2777273952960968, 9.404424667358398 },
                { 1,-2.1164743900299072, 0.2777273952960968, 9.404424667358398 },
                { 1,-2.126051187515259, 0.28730419278144836, 9.394847869873047 },
                { 1,-2.1068975925445557, 0.28730419278144836, 9.385271072387695 },
                { 1,-2.1068975925445557, 0.2777273952960968, 9.394847869873047 },
                { 1,-2.1068975925445557, 0.2777273952960968, 9.394847869873047 },
                { 1,-2.1068975925445557, 0.2777273952960968, 9.433155059814453 },
                { 1,-2.1068975925445557, 0.26815059781074524, 9.423578262329102 },
                { 1,-2.097320795059204, 0.26815059781074524, 9.41400146484375 },
                { 2,0.001658062799833715, -6.108652451075613E-4, -4.1887903353199363E-4 },//26
                { 1,-2.1164743900299072, 0.2777273952960968, 9.41400146484375 },
                { 1,-2.1164743900299072, 0.2777273952960968, 9.394847869873047 },
                { 2,-4.5378561480902135E-4, 4.3633230961859226E-4, -4.1887903353199363E-4 },//29
                { 1,-2.1068975925445557, 0.2777273952960968, 9.394847869873047 },
                { 2,-0.0025830871891230345, 0.0015009831404313445, 6.2831852119416E-4 }
        };
        long[] timelist = {
                195194060649185L,
                195194064647414L,
                195194068645643L,
                195194072643872L,
                195194076642101L,
                195194080640330L,
                195194084638560L,
                195194088636789L,
                195194092635018L,
                195194096633247L,
                195194100631476L,
                195194104660226L,
                195194108658455L,
                195194112656685L,
                195194116654914L,
                195194120653143L,
                195194124651372L,
                195194128649601L,
                195194132647830L,
                195194136646060L,
                195194140644289L,
                195194144642518L,
                195194148640747L,
                195194152638976L,
                195194156637205L,
                195194160635435L,
                195194163656997L,//26
                195194164633664L,
                195194168631893L,
                195194168631893L,
                195194172660643L,
                195194173637310L
        };
        //System.out.println(Arrays.toString(datalist[0]));
        for(int i=0;i<datalist.length;i++){
            if(datalist[i][0]==1.0){
                headTracker.mTracker.processAcc(new Vector3d(datalist[i][1],datalist[i][2],datalist[i][3]),timelist[i]);
            }
            else{
                headTracker.mTracker.processGyro(new Vector3d(datalist[i][1],datalist[i][2],datalist[i][3]),timelist[i]);
            }
        }
        System.out.println(headTracker.mTracker);
    }
}
