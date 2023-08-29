import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Hw5{
    private List<String[]> data;
    private int[][][] dataCounter;
    private int republicans;
    public List<String[]> loadUpData(String filePath){
        data = new ArrayList<>();
        dataCounter = new int[2][16][3];
        republicans = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] attributes = line.split(",");
                data.add(attributes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void run(List<String[]> data){
        dataCounter = new int[2][16][3];
        republicans = 0;
        for (int record = 0; record < data.size(); record++){
            int inWhichClass;
            if (data.get(record)[0].equals("republican")){
                republicans++;
                inWhichClass = 0;
            }else{
                inWhichClass = 1;
            }
            for (int col = 1; col < 17; col++){
                int attributeToInt = attributeToInt(data.get(record)[col]);
                dataCounter[inWhichClass][col-1][attributeToInt]++;
            }
        }
    }

    public double testClassifier(List<String[]> data){
        int all = data.size();
        int correct = 0;
        for (int record = 0; record < all; record++){
            String result = getClassForAttributes(data.get(record));
            if(result.equals(data.get(record)[0])){
                correct++;
            }
        }
        return (double)correct/all;
    }

    private String getClassForAttributes(String[] record){
        double pNBRep;
        double pRep = (double)republicans/data.size();
        double pRepAttr = 1;
        for (int i = 0; i < 16; i++){
            int attributeToInt = attributeToInt(record[i+1]);
            pRepAttr*= (double)dataCounter[0][i][attributeToInt]/republicans;
        }
        pNBRep = pRep * pRepAttr;

        double pNBDem;
        double pDem = (double)(data.size()-republicans)/data.size();
        double pDemAttr = 1;
        for (int i = 0; i < 16; i++){
            int attributeToInt = attributeToInt(record[i+1]);
            pDemAttr*= (double)dataCounter[1][i][attributeToInt]/(data.size()-republicans);
        }
        pNBDem = pDem * pDemAttr;
        double test = (pNBRep+pNBDem);
        pNBRep = pNBRep / test;
        pNBDem = pNBDem / test;

        if(pNBDem>pNBRep){
            return "democrat";
        }else{
            return "republican";
        }
    }

    private int attributeToInt(String attribute){
        switch (attribute){
            case "y":
                return 0;
            case "n":
                return 1;
            default:
                return 2;
        }
    }

}

public class NaiveBayesClassifier {
    public static void main(String[] args) {
        Hw5 hw5 = new Hw5();
        List<String[]> data = hw5.loadUpData("resources/house-votes-84.data");
        int recordsPerExec = data.size()/10;
        for(int i = 0; i < data.size()-recordsPerExec; i+=recordsPerExec){
            List<String[]> execOnData = data.subList(i, i + recordsPerExec);
            List<String[]> remainingData = new ArrayList<>();
            List<String[]> firstPart = data.subList(0, i);
            List<String[]> secondPart = data.subList(i + recordsPerExec, data.size());
            remainingData.addAll(firstPart);
            remainingData.addAll(secondPart);
            hw5.run(execOnData);
            System.out.println(hw5.testClassifier(remainingData));
        }
    }
}
