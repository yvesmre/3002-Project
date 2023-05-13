import java.net.*;
import java.io.*;

public class Client_2 {
    public static void main(String[] args) throws IOException {
        String serverHostname = "192.168.0.14";  //needs to know servers ip in order to be able to do anything
        int serverPort = 1234;

        Socket clientSocket = new Socket(serverHostname, serverPort);
        System.out.println("Connected to server: " + clientSocket);

        InputStream inputStream = clientSocket.getInputStream();
        OutputStream outputStream = clientSocket.getOutputStream();
        
        byte[] syn = new byte[1];
        inputStream.read(syn);

        if (syn[0] == 0x01) {
            System.out.println("SYN received");

            byte[] synAck = {0x02};
            outputStream.write(synAck);
            System.out.println("SYN-ACK sent");

            byte[] ack = new byte[1];
            inputStream.read(ack);
            if (ack[0] == 0x03) {
                System.out.println("ACK received");

                BufferedWriter fileWriter = new BufferedWriter(new FileWriter("serverdata.txt"));

                StringBuilder question = new StringBuilder();
                int receivedChar;
                // Identify when a question is sent from the server and when full question sent, write it into text file 
                // Send acknowledgement for each question recieved else question will be re-sent
                while ((receivedChar = inputStream.read()) != -1) {
                    char character = (char) receivedChar;
                    if (character == '#') {
                        question.append(character);
                        System.out.println("Received data: " + question.toString());

                        fileWriter.write(question.toString());
                        fileWriter.newLine();

                        byte[] dataAck = {0x04};
                        outputStream.write(dataAck);
                        System.out.println("ACK sent for data");

                        question.setLength(0); // Clear the question
                      //If server has sent all the data it wanted to using character '@' to signify it  
                    } else if (character == '@'){
                        System.out.println("Recieved end of data ");
                        byte[] endDataAck = {0x05};
                        outputStream.write(endDataAck);
                        System.out.println("ACK sent for end of data");
                        break;
                    } 
                    else {
                        question.append(character);
                    }
                }

                //Data has all been done
                byte[] end_ack = {0x05};
                outputStream.write(end_ack);
                System.out.println("Data in text file successfully recieved and stored");


                fileWriter.close();
                clientSocket.close();
            } else {
                System.out.println("Failed to receive ACK");
            }
        } else {
            System.out.println("Failed to receive SYN");
        }
    }
}
