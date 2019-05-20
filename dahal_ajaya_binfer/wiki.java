/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dahal_ajaya_binfer;

/**
 *
 * @author ajaya
 */
import java.net.URI;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder ;  
 
import org.jsoup.* ;
import org.json.* ;
public class wiki {
    public String url;
    public static String query(String ask) throws JSONException {
        
        UriBuilder builder = UriBuilder.fromPath("https://en.wikipedia.org/w/api.php") ;
        builder.queryParam("action", "query") ; 
        builder.queryParam("list", "search");
        builder.queryParam("srsearch", ask);
        builder.queryParam("format" ,"json");
        URI uri = builder.build();
        Client client = ClientBuilder.newClient() ; 
        WebTarget target = client.target(uri);
        JSONObject obj = new JSONObject(target.request(MediaType.APPLICATION_JSON).get(String.class)) ; 
        JSONObject query = obj.getJSONObject("query") ;
        JSONArray arr = query.getJSONArray("search") ;
        String concat_string="" ;
        for(int i= 0 ; i <arr.length(); i++){
            String title = html2text(arr.getJSONObject(i).getString("title"));
            String snippet = html2text(arr.getJSONObject(i).getString("snippet"));
            concat_string = concat_string + title +"<br>" +snippet + "<br>";
           
        }
        return concat_string ; 
    }
    public static String html2text(String html) {
    return Jsoup.parse(html).text();
}
    public void returnSource(){
            System.out.println("s");
    }
}
