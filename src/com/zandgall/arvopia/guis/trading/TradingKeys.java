package com.zandgall.arvopia.guis.trading;

import com.zandgall.arvopia.utils.Mapper;

public class TradingKeys {
	public Mapper<String, Mapper<String, String>> responseKeys;
	
	public TradingKeys() {
		responseKeys = new Mapper<String, Mapper<String, String>>();
	}
	
	public void removeResponse(String speech, String response) {
		for(Mapper<String, String> m: responseKeys.getValues(speech)) {
			if(m.hasKey(response))
				m.removeKey(response);
		}
	}
	
	public void removeSpeech(String speech) {
		
	}
	
	public String getSpeech(int index) {
		return (String) responseKeys.getKeys().get(index);
	}
	
	public String getResponse(int sIndex, int index) {
		return getResponse(getSpeech(sIndex), index);
	}
	
	public String getResponse(String speech, int index) {
		return (String) responseKeys.getValues(speech).get(index).getKeys().get(0);
	}
	
	public int indexOfSpeech(String speech) {
		return responseKeys.getKeys().indexOf(speech);
	}
	
	public int indexOfResponse(String speech, String response) {
		for(int i = 0; i<responseKeys.getValues(speech).size(); i++)
			if(responseKeys.getValues(speech).get(i).getKeys().get(0).contains(response))
				return i;
		
		return -1;
	}
	
	public boolean containsSpeech(String speech) {
		return responseKeys.getKeys().contains(speech);
	}
	
	public boolean containsResponse(String speech, String response) {
		return indexOfResponse(speech, response)!=-1;
	}
	
	public String getNext(String speech, String response) {
		return responseKeys.getValues(speech).get(indexOfResponse(speech, response)).getValues(response).get(0);
	}
	
	public String getNext(int speech, String response) {
		return responseKeys.getValues(responseKeys.getKeys().get(speech)).get(indexOfResponse(getSpeech(speech), response)).getValues(response).get(0);
	}
	
	public int getNextIndex(String speech, String response) {
		return indexOfSpeech(getNext(speech, response));
	}
	
	public int responseAmmount(String speech) {
		return responseKeys.getValues(speech).size();
	}
	
	public int responseAmmount(int index) {
		return responseAmmount(getSpeech(index));
	}
	
	public void put(String speech, String response, String next) {
//		if(containsSpeech(speech)) {
//			if(containsResponse(response)) {
//				
//			} else {
//				responseKeys.get(speech).
//			}
//		} else {
			Mapper<String, String> responses = new Mapper<String, String>();
			responses.put(response, next);
			responseKeys.put(speech, responses);
//		}
	}
}
