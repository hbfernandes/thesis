package pt.uevora.hfernandes;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.mashape.client.authentication.Authentication;
import com.mashape.client.authentication.MashapeAuthentication;
import com.mashape.client.http.ContentType;
import com.mashape.client.http.HttpClient;
import com.mashape.client.http.HttpMethod;
import com.mashape.client.http.MashapeCallback;
import com.mashape.client.http.MashapeResponse;
import com.mashape.client.http.ResponseType;

public class SentimentAnalysisFree {

	private final static String PUBLIC_DNS = "chatterbox-analytics-sentiment-analysis-free.p.mashape.com";
    private List<Authentication> authenticationHandlers;

    public SentimentAnalysisFree (String publicKey, String privateKey) {
        authenticationHandlers = new LinkedList<Authentication>();
        authenticationHandlers.add(new MashapeAuthentication(publicKey, privateKey));
        
    }
    
    /**
     * Synchronous call with optional parameters.
     */
    public MashapeResponse<JSONObject> classifytext(String lang, String text, String exclude) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (lang != null && !lang.equals("")) {
	parameters.put("lang", lang);
        }
        
        
        if (text != null && !text.equals("")) {
	parameters.put("text", text);
        }
        
        
        if (exclude != null && !exclude.equals("")) {
	parameters.put("exclude", exclude);
        }
        
        
        return (MashapeResponse<JSONObject>) HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/sentiment/current/classify_text/",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers);
    }

    /**
     * Synchronous call without optional parameters.
     */
    public MashapeResponse<JSONObject> classifytext(String lang, String text) {
        return classifytext(lang, text, "");
    }


    /**
     * Asynchronous call with optional parameters.
     */
    public Thread classifytext(String lang, String text, String exclude, MashapeCallback<JSONObject> callback) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        
        if (lang != null && !lang.equals("")) {
        
            parameters.put("lang", lang);
        }
        
        
        if (text != null && !text.equals("")) {
        
            parameters.put("text", text);
        }
        
        
        if (exclude != null && !exclude.equals("")) {
        
            parameters.put("exclude", exclude);
        }
        
        return HttpClient.doRequest(JSONObject.class,
                    HttpMethod.POST,
                    "https://" + PUBLIC_DNS + "/sentiment/current/classify_text/",
                    parameters,
                    ContentType.FORM,
                    ResponseType.JSON,
                    authenticationHandlers,
                    callback);
    }

    /**
     * Asynchronous call without optional parameters.
     */
    public Thread classifytext(String lang, String text, MashapeCallback<JSONObject> callback) {
        return classifytext(lang, text, "", callback);
    }
}