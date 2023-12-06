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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class EIFSocketServer {
	
	private static final Logger log = LogManager.getLogger("standard");
	 
    public EIFSocketServer(String httpPort, String connectionTimeout, String eifeof, String charset) {
 
        int port = Integer.valueOf(httpPort).intValue();
 
        try (ServerSocket serverSocket = new ServerSocket(port)) {
 
            log.info("Server is listening on port " + port);
 
            while (true) {
                Socket socket = serverSocket.accept();
                log.info("New Request incomming");
                socket.setSoTimeout(Integer.valueOf(connectionTimeout).intValue());                
                new EIFServerThread(socket, eifeof, charset, connectionTimeout).run();

            }
 
        } catch (IOException ex) {
            log.error("IO Exeption " + ex.getMessage());
        }
    }
}