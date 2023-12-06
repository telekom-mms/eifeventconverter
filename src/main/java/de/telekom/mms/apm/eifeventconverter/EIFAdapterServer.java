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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
/**
 * 
 * This class 
 * * starting the jetty 
 * * reading the config file
 * * reading the mappingfile
 * 
 * @author Kay Koedel
 * 
 */
public class EIFAdapterServer {

	private static final Logger log = LogManager.getLogger("standard");
	private final Properties properties = new Properties();
	
	public void start() {

		readConfig();
		EIFSocketRequest.getInstance().setFieldFilter(readFilterField());
		log.info("Starting EIFEventConverter complete ");
		log.info("*************************************************************************");
		String eventAdapterProtokoll = properties.getProperty("eventAdapter_protokoll");
		String eventAdapterHost = properties.getProperty("eventAdapter_host");
		String eventAdapterPort = properties.getProperty("eventAdapter_port");
		String eventAdapterEndpoint = properties.getProperty("eventAdapter_endpoint");
		String eventType = properties.getProperty("eventType");

		String httpPort = properties.getProperty("http_port");
		String connectionTimeout = properties.getProperty("connectionTimeout");
		String eifeof = properties.getProperty("eifeof");
		String charset = properties.getProperty("charset");

		boolean sendToAdapter = Boolean.valueOf(properties.getProperty("sendToAdapter")).booleanValue();
		EIFSocketRequest.getInstance().setAdapterURI(eventAdapterProtokoll + "://"+ eventAdapterHost + ":" + eventAdapterPort + eventAdapterEndpoint);
		EIFSocketRequest.getInstance().setSendToAdapter(sendToAdapter);
		EIFSocketRequest.getInstance().setEventType(eventType);
		
		new EIFSocketServer(httpPort, connectionTimeout, eifeof, charset);

	}

	private JsonObject readFilterField() {
		JsonObject fieldFilter;
		FileReader reader;
		try {
			reader = new FileReader("config/fieldfilter.json");
			fieldFilter = Json.parse(reader).asObject();
			return fieldFilter;

		} catch (FileNotFoundException e) {

			log.error(e);
		} catch (IOException e) {

			log.error(e);
		}

		return null;

	}
	
	/**
	 * 
	 */
	public void readConfig() {

		InputStream input = null;

		try {

			input = this.getClass()
					.getResourceAsStream("/META-INF/maven/com.tsystems.mms.apm.EIFEventConverter/EIFEventConverter/pom.properties");
			properties.load(input);
			if (properties.containsKey("version")) {
				log.info("Version: " + properties.getProperty("version"));
			}
		} catch (Exception e) {
			log.error(e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}

		try {

			input = new FileInputStream("config/config.properties");

			// load a properties file
			properties.load(input);

			// get the property value and print it out
			log.info("http_port: " + properties.getProperty("http_port"));
			log.info("eventAdapter_protokoll: " + properties.getProperty("eventAdapter_protokoll"));
			log.info("eventAdapter_port: " + properties.getProperty("eventAdapter_port"));
			log.info("eventAdapter_host: " + properties.getProperty("eventAdapter_host"));			
			log.info("eventAdapter_endpoint: " + properties.getProperty("eventAdapter_endpoint"));			
			log.info("eventType: " + properties.getProperty("eventType"));			

			log.info("sendToAdapter: " + properties.getProperty("sendToAdapter"));	
			log.info("eifeof: " + properties.getProperty("eifeof"));	
			log.info("charset: " + properties.getProperty("charset"));	
			log.info("connectionTimeout: " + properties.getProperty("connectionTimeout"));	

						

			
		} catch (IOException e) {
			log.error(e.getClass().toString(), e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception e) {
					log.error(e);
				}
			}
		}

	}


}