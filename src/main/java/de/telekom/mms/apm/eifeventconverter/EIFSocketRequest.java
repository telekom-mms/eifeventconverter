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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


public class EIFSocketRequest {
    private static final EIFSocketRequest socketRequest = new EIFSocketRequest();
	private static final Logger log = LogManager.getLogger("standard");
	private static final Logger logEvent = LogManager.getLogger("event");
	private String adapterURI;
	private boolean sendToAdapter;
	private String request;
	private String eventType;

	private ArrayList<String> filterList;
	
	public void setSendToAdapter(boolean sendToAdapter) {
		this.sendToAdapter = sendToAdapter;
	}
	
    public static EIFSocketRequest getInstance() {
        return socketRequest;
      }
	
	public void setRequest(String request) {
		
		this.request = request;
		logEvent.debug("New Event incomming:");
		logEvent.debug(request);

	}
	
	public void convert2Json() {

		String data = request.substring(request.indexOf(";")+1,request.length());
		String[] values = data.split(";");
		JsonObject result = new JsonObject();
		JsonObject externalEvent = new JsonObject();
		String key;
		String value;
		String[] split;
		
		
		for (String element: values) {           

			split = element.split("=");
			key = split[0];
			if (split.length > 1) {
				 value = split[1];
				 value = value.replace("\'","");
			}
			else value = "";
			
			// only add if not filteres
			if (!filterList.contains(key)) 
				result.add(key, value);
		}
		externalEvent.add(eventType, result);
		this.request = externalEvent.toString();

		
	}
	

	public void send2Adapter() {
		
		if (sendToAdapter) {
		log.debug("Trying to send to Adapter");
		HttpClient client = HttpClient.newBuilder().build();
	    HttpRequest httpRequest = HttpRequest.newBuilder()
	            .uri(URI.create(adapterURI))
	            .POST(BodyPublishers.ofString(request))
	            .build();

	    HttpResponse<?> response;
		try {
		    log.info("Send Request to EventAdapter START");
			log.debug("Sending to URL: " + adapterURI);
			log.debug("Sending request: " + request);
		    response = client.send(httpRequest, BodyHandlers.discarding());
			
			log.info("Send Request to EventAdapter END");
			log.info(response.statusCode());
		    
		} catch (IOException e) {
			log.error("IOException:" + e.getMessage());
		} catch (InterruptedException e) {
			log.error("InterruptedException:" + e.getMessage());
		}
		}

	} 

	public void setAdapterURI(String adapterURI) {
		this.adapterURI = adapterURI;
		
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
		
	}

	public void setFieldFilter(JsonObject filterFields) {


		JsonArray filterArray = filterFields.get("fields").asArray();
		Iterator<JsonValue> filterIterator = filterArray.iterator();
		filterList = new ArrayList<String>();

		while(filterIterator.hasNext()) {
			filterList.add(filterIterator.next().asString());
		}
		
		log.info("FilterList: " + filterList.toString());
	}

}