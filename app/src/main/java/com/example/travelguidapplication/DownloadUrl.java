package com.example.travelguidapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadUrl {
    //retrieve the data from url using HTTP url connection & file handling

    public String ReadTheURL(String placeURL) throws IOException {
        String Data ="";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try
        {   //create the url
            URL url = new URL(placeURL);
            //open the connection
            httpURLConnection = (HttpURLConnection)url.openConnection();
            //connect
            httpURLConnection.connect();

            //read the data from url
            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();

            String line = "";

            while ((line=bufferedReader.readLine()) != null){

                stringBuffer.append(line);
            }

            Data= stringBuffer.toString();
            bufferedReader.close();



        } catch (MalformedURLException e)

        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        finally
        {
            inputStream.close();
            httpURLConnection.disconnect();
        }
        return Data;
    }
}
