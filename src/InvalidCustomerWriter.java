import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class InvalidCustomerWriter extends Thread{
    List<String[]> invalidCustomers = new ArrayList<>();

    public void copyInvalidCustomersData(List<String[]> mainList){
        invalidCustomers = List.copyOf(mainList);
    }

    public void run(){
        try {
            PrintWriter outputFile = null;
            int x = 0;
            int fileSerial = 0;
            int entryPerPage = 0;
            for (String[] dataVal: invalidCustomers) {
                x++;
                if(x==1 || entryPerPage==100000){
                    fileSerial++;
                    File csvFile = new File("invalidCustomers/invalidCustomerList"+fileSerial+".csv");
                    outputFile = new PrintWriter(csvFile);
                    StringBuilder heading = new StringBuilder();
                    heading.append("First Name").append(",").append("Last Name").append(",").append("City")
                            .append(",").append("State").append(",").append("Code").append(",").append("Phone No")
                            .append(",").append("Email").append(",").append("Ip").append("\n");
                    outputFile.write(heading.toString());
                    entryPerPage = 0;
                }
                StringBuilder customerData = new StringBuilder();
                for(int i=0;i<dataVal.length;i++){
                    if(i!=dataVal.length-1){
                        customerData.append(dataVal[i]);
                        customerData.append(",");
                    } else {
                        customerData.append(dataVal[i]);
                        customerData.append("\n");
                    }
                }
                outputFile.write(customerData.toString());
                entryPerPage++;
            }
            outputFile.close();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
