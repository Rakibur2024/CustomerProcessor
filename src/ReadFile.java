import java.io.*;
import java.util.*;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadFile {
    public void readCustomerFile() throws Exception{

        try {
            Set<String> uniquePhoneNumbers = new HashSet<>();
            Set<String> uniqueEmails = new HashSet<>();
            List<String[]> validCustomers = new ArrayList<>();
            List<String[]> inValidCustomers = new ArrayList<>();

            DbConnection con = new DbConnection();
            Connection connection = con.createConnection();
            PreparedStatement psInvalidCustomers = connection.prepareStatement("insert into invalid_customers(first_name,last_name,city,state,code,phone_no,email,ip) values(?,?,?,?,?,?,?,?)");
            PreparedStatement psValidCustomers = connection.prepareStatement("insert into valid_customers(first_name,last_name,city,state,code,phone_no,email,ip) values(?,?,?,?,?,?,?,?)");

            File fileObj = new File("1M-customers.txt");
            Scanner fileReader = new Scanner(fileObj);
            int rowNumber = 0;

            connection.setAutoCommit(false);
            while (fileReader.hasNextLine()) {
                rowNumber++;
                String userInfo = fileReader.nextLine();
                String[] userInfoArray = userInfo.split(",");
                boolean valid = checkValidity(userInfoArray[5],userInfoArray[6]);
                if(uniquePhoneNumbers.add(userInfoArray[5]) && uniqueEmails.add(userInfoArray[6])){
                    if(valid){
                        psValidCustomers.setString(1,userInfoArray[0]);
                        psValidCustomers.setString(2,userInfoArray[1]);
                        psValidCustomers.setString(3,userInfoArray[2]);
                        psValidCustomers.setString(4,userInfoArray[3]);
                        psValidCustomers.setString(5,userInfoArray[4]);
                        psValidCustomers.setString(6,userInfoArray[5]);
                        psValidCustomers.setString(7,userInfoArray[6]);
                        if(userInfoArray.length > 7){
                            psValidCustomers.setString(8,userInfoArray[7]);
                        }
                        psValidCustomers.addBatch();

                        if(userInfoArray.length > 7){
                            validCustomers.add(new String[] {
                                userInfoArray[0],
                                userInfoArray[1],
                                userInfoArray[2],
                                userInfoArray[3],
                                userInfoArray[4],
                                userInfoArray[5],
                                userInfoArray[6],
                                userInfoArray[7]
                            });
                        } else {
                            validCustomers.add(new String[] {
                                userInfoArray[0],
                                userInfoArray[1],
                                userInfoArray[2],
                                userInfoArray[3],
                                userInfoArray[4],
                                userInfoArray[5],
                                userInfoArray[6]
                            });
                        }


                    } else {
                        psInvalidCustomers.setString(1,userInfoArray[0]);
                        psInvalidCustomers.setString(2,userInfoArray[1]);
                        psInvalidCustomers.setString(3,userInfoArray[2]);
                        psInvalidCustomers.setString(4,userInfoArray[3]);
                        psInvalidCustomers.setString(5,userInfoArray[4]);
                        psInvalidCustomers.setString(6,userInfoArray[5]);
                        psInvalidCustomers.setString(7,userInfoArray[6]);
                        psInvalidCustomers.setString(8,userInfoArray[7]);
                        psInvalidCustomers.addBatch();

                        if(userInfoArray.length > 7){
                            inValidCustomers.add(new String[] {
                                    userInfoArray[0],
                                    userInfoArray[1],
                                    userInfoArray[2],
                                    userInfoArray[3],
                                    userInfoArray[4],
                                    userInfoArray[5],
                                    userInfoArray[6],
                                    userInfoArray[7]
                            });
                        } else {
                            inValidCustomers.add(new String[] {
                                    userInfoArray[0],
                                    userInfoArray[1],
                                    userInfoArray[2],
                                    userInfoArray[3],
                                    userInfoArray[4],
                                    userInfoArray[5],
                                    userInfoArray[6]
                            });
                        }

                    }

                }

                if(rowNumber%100==0){
                    psValidCustomers.executeBatch();
                    psInvalidCustomers.executeBatch();

                    psValidCustomers.clearBatch();
                    psInvalidCustomers.clearBatch();

                    connection.commit();
                }

            }
            fileReader.close();

            ValidCustomerWriter validCustomerWriter = new ValidCustomerWriter();
            validCustomerWriter.copyValidCustomersData(validCustomers);
            validCustomerWriter.start();

            InvalidCustomerWriter invalidCustomerWriter = new InvalidCustomerWriter();
            invalidCustomerWriter.copyInvalidCustomersData(inValidCustomers);
            invalidCustomerWriter.start();


        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }


    }
    public boolean checkValidity(String phoneNo, String emailAddress){

        String regexPhone = "^(\\d{1,1}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$";
        Pattern phonePattern = Pattern.compile(regexPhone);
        Matcher phoneMatcher=phonePattern.matcher(phoneNo);

        String emailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";
        Pattern emailPattern = Pattern.compile(emailRegex);
        Matcher emailMatcher=emailPattern.matcher(emailAddress);

        if(phoneMatcher.matches() && emailMatcher.matches()){
            return true;
        } else {
            return false;
        }
    }

}
