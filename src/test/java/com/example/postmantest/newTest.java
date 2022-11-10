package com.example.postmantest;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
class newTest {

    List<String> fileList = Arrays.asList("textFile1", "textFile2", "textFile3", "textFile4");
    File filePath = new File("read\\filepath");
    String protocol = "https:/";
    String date = "202210270000";

    @Test
    String beforeDate() throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        Date chkDate = simpleDateFormat.parse(date);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(chkDate);
        calendar.add(Calendar.DATE, -1);
        System.out.println(date.substring(0, 9));
        return simpleDateFormat.format(calendar.getTime());
    }

    @Test
    void contextLoads() throws IOException, ParseException {

        for(String fileName : fileList) {
            JSONArray jsonArray = new JSONArray();
            JSONObject postmanObject = new JSONObject();

            FileReader aReader = new FileReader(filePath + "\\" + fileName + ".txt");
            BufferedReader aBufReader = new BufferedReader(aReader);

            List<String> urlList = new ArrayList<String>();
            String aLine = "";
            String beforeDate = beforeDate();

            while ((aLine = aBufReader.readLine()) != null) {
                String newLine = aLine.replace(protocol, "{{protocol}}").replace("portValue", "{{port}}").replace("urlValue", "{{url}}");
                urlList.add(newLine);
            }
            aBufReader.close();

            for (int i = 0; i < urlList.size(); i++) {
                jsonArray.add(createItem(urlList.get(i)));
            }

            postmanObject.put("info", createInfo(fileName));
            postmanObject.put("item", jsonArray);
            postmanObject.put("variable", getVariables());

            try {
                FileWriter file = new FileWriter("write\\filepath\\"+fileName + ".json");
                file.write(postmanObject.toJSONString());
                file.flush();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    JSONObject createItem(String url) {
        JSONObject obj = new JSONObject();
        obj.put("name", JSONObject.escape(getName(url)));

        JSONObject request = new JSONObject();
        request.put("method", "GET");
        request.put("header", "[]");
        request.put("url", url);
        obj.put("request", request);
        obj.put("response", "[]");

        System.out.println("obj = " + obj);
        return obj;
    }

    JSONObject createInfo(String fileName) {
        JSONObject obj = new JSONObject();
        obj.put("_postman_id", "postmanId");
        obj.put("name",fileName);
        obj.put("schema","https://schema.getpostman.com/json/collection/v2.0.0/collection.json");

        return obj;
    }

    JSONArray getVariables() {
        JSONArray array = new JSONArray();
        JSONObject protocolObj = new JSONObject();
        protocolObj.put("key", "protocol");
        protocolObj.put("value", protocol);
        array.add(protocolObj);

        JSONObject urlObj1 = new JSONObject();
        urlObj1.put("key", "port");
        urlObj1.put("value", "portValue");
        array.add(urlObj1);

        JSONObject urlObj2 = new JSONObject();
        urlObj2.put("key", "url");
        urlObj2.put("value", "urlValue");
        array.add(urlObj2);

        return array;
    }

    String getName(String url) {
        String[] name = url.split("/");
        String returnName = "";
        for(int i=3; i < name.length; i++) {
            if(i == name.length-1) {
                returnName += name[i];
            }else {
                returnName += name[i] + "_";
            }
        }
        return returnName;
    }

}