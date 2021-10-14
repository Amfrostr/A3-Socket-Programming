package no.ntnu.datakomm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * A Simple TCP client, used as a warm-up exercise for assignment A4.
 */
public class SimpleTcpClient {
    // Remote host where the server will be running
    private static final String HOST = "datakomm.work";
    // TCP port
    private static final int PORT = 1301;
    // Socket
    private Socket outputSocket;

    /**
     * Run the TCP Client.
     *
     * @param args Command line arguments. Not used.
     */
    public static void main(String[] args) {
        SimpleTcpClient client = new SimpleTcpClient();
        try {
            client.run();
        } catch (InterruptedException | IOException e) {
            log("Client interrupted");
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Run the TCP Client application. The logic is already implemented, no need to change anything in this method.
     * You can experiment, of course.
     *
     * @throws InterruptedException The method sleeps to simulate long client-server conversation.
     *                              This exception is thrown if the execution is interrupted halfway.
     */
    public void run() throws InterruptedException, IOException {
        log("Simple TCP client started");

        if (!connectToServer(HOST, PORT)) {
            log("ERROR: Failed to connect to the server");
            return;
        }
        log("Connection to the server established");

        int a = (int) (1 + Math.random() * 10);
        int b = (int) (1 + Math.random() * 10);
        String request = a + "+" + b + "\n";

        if (!sendRequestToServer(request)) {
            log("ERROR: Failed to send valid message to server!");
            return;
        }
        log("Sent " + request + " to server");

        String response = readResponseFromServer();
        if (response == null) {
            log("ERROR: Failed to receive server's response!");
            return;
        }
        log("Server responded with: " + response);

        sleepRandomTime();
        request = "bla+bla" + "\n";
        if (!sendRequestToServer(request)) {
            log("ERROR: Failed to send invalid message to server!");
            return;
        }
        log("Sent " + request + " to server");

        response = readResponseFromServer();
        if (response == null) {
            log("ERROR: Failed to receive server's response!");
            return;
        }
        log("Server responded with: " + response);


        if (!sendRequestToServer("game over\n") || !closeConnection()) {
            log("ERROR: Failed to stop conversation");
            return;
        }

        log("Game over, connection closed");

        // When the connection is closed, try to send one more message. It should fail.
        if (!sendRequestToServer("2+2")) {
            log("Sending another message after closing the connection failed as expected");
        } else {
            log("ERROR: sending a message after closing the connection did not fail!");
        }

        log("Simple TCP client finished");
    }

    /**
     * Put the main thread to sleep for a random number of seconds (between 2 and 5 seconds)
     */
    private void sleepRandomTime()  {
        long secondsToSleep = 2 + (long) (Math.random() * 5);
        log("Sleeping " + secondsToSleep + " seconds to allow simulate long client-server connection...");
        try {
            Thread.sleep(secondsToSleep * 1000);
        } catch (InterruptedException e) {
            System.out.println("Thread sleep interrupted... Oh, well...");
        }
    }

    /**
     * Try to establish TCP connection to the server (the three-way handshake).
     *
     * @param host The remote host to connect to. Can be domain (localhost, ntnu.no, etc), or IP address
     * @param port TCP port to use
     * @return True when connection established, false on error
     */
    private boolean connectToServer(String host, int port) {
        // TODO - implement this method
        try {
            this.outputSocket = new Socket(HOST, PORT);

            OutputStream output = this.outputSocket.getOutputStream();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Close the TCP connection to the remote server.
     *
     * @return True on success, false otherwise. Note: if the connection was already closed (not established),
     * return true as well.
     */
    private boolean closeConnection() throws IOException {
        if(!outputSocket.isClosed()) {
            outputSocket.close();
            return true;
        } else if (outputSocket.isClosed()) {
            log("outputSocket is already closed. Moving on.");
            return true;
        }
        else
            return false;
    }


    /**
     * Send a request message to the server (newline will be added automatically)
     *
     * @param request The request message to send. Do NOT include the newline in the message!
     * @return True when message successfully sent, false on error.
     */
    private boolean sendRequestToServer(String request) throws IOException {
        // TODO - implement this method
        // Hint: you should check if the connection is open
        if(!outputSocket.isClosed()) {
            OutputStream outputStream = outputSocket.getOutputStream();
            outputStream.write(request.getBytes(StandardCharsets.UTF_8));
            if(request.equalsIgnoreCase("game over\n")) {

            closeConnection();
            }

            return true;
        }
        return false;
    }

    /**
     * Wait for one response from the remote server.
     *
     * @return The response received from the server, null on error. The newline character is stripped away
     * (not included in the returned value).
     */
    private String readResponseFromServer() throws IOException {
        // TODO - implement this method
        if (!outputSocket.isClosed()) {
            InputStream inputStream = outputSocket.getInputStream();
            byte[] buffer = new byte[10000];
            int bytesReceived = inputStream.read(buffer);

            String responsePart = new String(buffer);
            if (bytesReceived > 0) {
                System.out.println("Received " + bytesReceived + "\n" + responsePart);
                return responsePart;
            }
        }
        return null;
    }

    /**
     * Log a message to the system console.
     *
     * @param message The message to be logged (printed).
     */
    private static void log(String message) {
        String threadId = "THREAD #" + Thread.currentThread().getId() + ": ";
        System.out.println(threadId + message);
    }
}
