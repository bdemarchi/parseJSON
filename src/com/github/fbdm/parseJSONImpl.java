package com.github.fbdm;

import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.lib.uno.helper.WeakBase;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import com.jayway.jsonpath.JsonPath;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.net.ssl.HttpsURLConnection;


public final class parseJSONImpl extends WeakBase
   implements com.sun.star.lang.XServiceInfo,
              com.github.fbdm.XparseJSON,
              com.sun.star.lang.XLocalizable
{
    private final XComponentContext m_xContext;
    private static final String m_implementationName = parseJSONImpl.class.getName();
    private static final String[] m_serviceNames = {
        "com.github.fbdm.parseJSON" };

    private com.sun.star.lang.Locale m_locale = new com.sun.star.lang.Locale();

    public parseJSONImpl( XComponentContext context )
    {
        m_xContext = context;
    }

    public static XSingleComponentFactory __getComponentFactory( String sImplementationName ) {
        XSingleComponentFactory xFactory = null;

        if ( sImplementationName.equals( m_implementationName ) )
            xFactory = Factory.createComponentFactory(parseJSONImpl.class, m_serviceNames);
        return xFactory;
    }

    public static boolean __writeRegistryServiceInfo( XRegistryKey xRegistryKey ) {
        return Factory.writeRegistryServiceInfo(m_implementationName,
                                                m_serviceNames,
                                                xRegistryKey);
    }

    // com.sun.star.lang.XServiceInfo:
    public String getImplementationName() {
         return m_implementationName;
    }

    public boolean supportsService( String sService ) {
        int len = m_serviceNames.length;

        for( int i=0; i < len; i++) {
            if (sService.equals(m_serviceNames[i]))
                return true;
        }
        return false;
    }

    public String[] getSupportedServiceNames() {
        return m_serviceNames;
    }
    
    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
          sb.append((char) cp);
        }
        return sb.toString();
      }

    // com.github.fbdm.XparseJSON:
    public String parseJSON(String url, String query)
    {
        String returnText = ""; // text to resturn
        try {
            URL jsonURL = null; 
            InputStream is = null;
            if (url.startsWith("https://")) { // if url uses SSL
                jsonURL = new URL(url); 
                HttpsURLConnection con = (HttpsURLConnection)jsonURL.openConnection(); // get HTTPS connection
                is = con.getInputStream();
            } else {
                if (!url.startsWith("http://")) { // check if url starts with http://, if not add it
                    url = "http://"+url;
                }
                jsonURL = new URL(url);
                is = jsonURL.openStream();
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8"))); // get data as UTF-8
            String jsonText = this.readAll(rd);
            returnText = ""+JsonPath.read(jsonText, query); // query json using https://github.com/json-path/JsonPath 2.3.0
            // ensures it is always string
            
            is.close(); // close stream
            
        } catch (MalformedURLException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            returnText = "Invalid URL. Exception message: "+ex.getMessage()+" -- "+sw.toString();
        } catch (IOException ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            returnText = "Error reading JSON from URL. Exception message: "+ex.getMessage()+" -- "+sw.toString();
        } 
        
        return returnText;
    }

    // com.sun.star.lang.XLocalizable:
    public void setLocale(com.sun.star.lang.Locale eLocale)
    {
        m_locale = eLocale;
    }

    public com.sun.star.lang.Locale getLocale()
    {
        return m_locale;
    }

}
