/*Copyright 2022 T-Systems MMS GmbH (https://www.t-systems-mms.com/) 

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Author: Kay Koedel
*/

package de.telekom.mms.apm.eifeventconverter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

 
public class EIFServerThread extends Thread {
    private Socket socket;
	private static final Logger log = LogManager.getLogger("standard");
	private String eifeof;
	private String charset;
	private String connectionTimeout;
 
    public EIFServerThread(Socket socket, String eifeof, String charset, String connectionTimeout) {
        this.socket = socket;
        this.eifeof = eifeof;
        this.charset = charset;
        this.connectionTimeout = connectionTimeout;
    }
 
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
            boolean eventNotFinished = true;

            long end = System.currentTimeMillis() + Long.valueOf(connectionTimeout);
            
            String str = "";
        	String request = "";
            do {
 
            	try {
                byte[] array = new byte[] { (byte) reader.read()};
                str = new String(array,charset);
                request = request.concat(str);
                
                if (end < System.currentTimeMillis()) {
                	eventNotFinished = false;

            		writer.write("Response: Abort");
            		writer.flush();
            		input.close();
            		output.close();
            		socket.close();
                    log.warn("Abort occured: " + connectionTimeout);
                    log.debug(request);
                	
            	}
                
            	} catch (SocketTimeoutException ste) {
            	
            		writer.write("Response: Timeout");
            		writer.flush();
            		input.close();
            		output.close();
            		socket.close();
                    log.warn("Timeout occured: " + connectionTimeout);
                    log.debug(request);
            		EIFSocketRequest.getInstance().setRequest(request);
                    EIFSocketRequest.getInstance().convert2Json();
            		EIFSocketRequest.getInstance().send2Adapter();
                    eventNotFinished = false;
            		
            	}
            	if (request.contains(eifeof)) {
            		writer.write("Response: EOF");
            		writer.flush();           		
            		input.close();
            		output.close();
            		socket.close();
                    log.debug("EOF received: " + eifeof );
                    log.debug(request);
                    EIFSocketRequest.getInstance().setRequest(request);
                    EIFSocketRequest.getInstance().convert2Json();
                    EIFSocketRequest.getInstance().send2Adapter();
                    eventNotFinished = false;
            	}
            	
            } while (eventNotFinished);
          
 
        } catch (IOException ex) {
        	log.error("IOException occured");
        	log.error(ex.getMessage());

        } 
}
}