package com.example.irbidcitycenter;

import android.content.Context;
import android.net.http.DelegatingSSLSession;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.toolbox.StringRequest;
import com.example.irbidcitycenter.Activity.MainActivity;
import com.example.irbidcitycenter.Activity.NewShipment;
import com.example.irbidcitycenter.Activity.Replacement;
import com.example.irbidcitycenter.Models.CompanyInfo;
import com.example.irbidcitycenter.Models.ReplacementModel;
import com.example.irbidcitycenter.Models.Store;
import com.example.irbidcitycenter.Models.ZoneModel;

import com.example.irbidcitycenter.Models.Shipment;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;


import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.example.irbidcitycenter.Activity.AddZone.itemKind;
import static com.example.irbidcitycenter.Activity.AddZone.itemKintText;

import static com.example.irbidcitycenter.Activity.AddZone.validateKind;
import static com.example.irbidcitycenter.Activity.Login.getListCom;
import static com.example.irbidcitycenter.Activity.NewShipment.PoQTY;
import static com.example.irbidcitycenter.Activity.NewShipment.itemname;
import static com.example.irbidcitycenter.Activity.NewShipment.poNo;
import static com.example.irbidcitycenter.Activity.NewShipment.respon;
import static com.example.irbidcitycenter.Activity.Replacement.itemKintText1;
import static com.example.irbidcitycenter.Activity.Replacement.itemcode;

import static com.example.irbidcitycenter.Activity.Replacement.qtyrespons;
import static com.example.irbidcitycenter.Activity.Replacement.zone;
import static com.example.irbidcitycenter.GeneralMethod.convertToEnglish;


public class ImportData {
    public static ArrayList<ZoneModel> listAllZone = new ArrayList<>();
    public static int posize;
    public static String itemn;
    public static String item_name="";
    public static String poqty;
    private Context context;
    public String ipAddress = "", CONO = "", headerDll = "", link = "";
    public RoomAllData my_dataBase;
    public static String zonetype;
    public static ArrayList<Store> Storelist = new ArrayList<>();
    public static ArrayList<String> BoxNolist = new ArrayList<>();
    public static ArrayList<String> PoNolist = new ArrayList<>();
    public static List<Shipment> POdetailslist = new ArrayList<>();
    public static List<ZoneModel>  listQtyZone = new ArrayList<>();
    public static ArrayList<CompanyInfo> companyInList = new ArrayList<>();
    public static String  barcode="";
    public  JSONArray jsonArrayPo;
    public  JSONObject stringNoObject;

    public ImportData(Context context) {
        this.context = context;
        my_dataBase = RoomAllData.getInstanceDataBase(context);
        try {
            getIpAddress();
        } catch (Exception e) {
            Toast.makeText(context, "Fill Ip and Company No", Toast.LENGTH_SHORT).show();
        }

    }
    public void getPoNum(){
        if(!ipAddress.equals(""))
            new JSONTask_getAllPoNum().execute();
        else
            Toast.makeText(context, "Fill Ip", Toast.LENGTH_SHORT).show();
    }

    public void getQty() {
        listQtyZone.clear();
            new  JSONTask_getQTYOFZone().execute();

    }
    public void getStore() {
        if(!ipAddress.equals(""))
        new JSONTask_getAllStoreData().execute();
        else
        Toast.makeText(context, "Fill Ip", Toast.LENGTH_SHORT).show();
    }

    public void getboxno() {
        Log.e("ingetboxno","ingetboxno");
        BoxNolist.clear();
        if(!ipAddress.equals(""))
        new JSONTask_getAllPOboxNO().execute();
        else
            Toast.makeText(context, "Fill Ip", Toast.LENGTH_SHORT).show();
    }
    public void getPOdetails() {
        Log.e("getPOdetails","getPOdetails");
        //new JSONTaskGetPOdetails(context,cono,pono).execute();
        POdetailslist.clear();
        if(!ipAddress.equals(""))
        new JSONTask_getAllPOdetails().execute();
else
            Toast.makeText(context, "Fill Ip", Toast.LENGTH_SHORT).show();
    }

    private void getIpAddress() {
        headerDll="";
        ipAddress=my_dataBase.settingDao().getIpAddress().trim();
        CONO=my_dataBase.settingDao().getCono().trim();
        Log.e("getIpAddress",""+ipAddress);


    }
    public  void getAllZones(){
        if(!ipAddress.equals(""))
        {
            new JSONTask_getAllZoneCode().execute();
        }
        else {
            Toast.makeText(context, "Fill Ip", Toast.LENGTH_SHORT).show();
        }

    }

    public void getKindItem(String itemNo) {
        if(!ipAddress.equals(""))
        {
            new JSONTask_getItemKind(itemNo).execute();
        }
        else {
            Toast.makeText(context, "Fill Ip", Toast.LENGTH_SHORT).show();
        }
    }

    public void getCompanyInfo() {
        if(!ipAddress.equals(""))
        {
            try {
                new JSONTask_getCompanyInfo().execute();
            }
            catch (Exception e)
            {}

        }
        else {

            Toast.makeText(context, "Fill Ip", Toast.LENGTH_SHORT).show();
        }
    }

    public void updatePoClosed(String poNumber) {
        getJsnStr(poNumber);
       new JSONTask_UpdatePoClose(poNumber).execute();
    }

    private void getJsnStr(String poNumber) {

            jsonArrayPo = new JSONArray();
            JSONObject object=new JSONObject();
        try {
            object.put("PONO",poNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonArrayPo.put(object);


            try {
                stringNoObject=new JSONObject();
                stringNoObject.put("JSN",jsonArrayPo);
                Log.e("vouchersObject",""+jsonArrayPo.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }




    }

    public class JSONTask_UpdatePoClose extends AsyncTask<String, String, String> {

        private String poNo = "", JsonResponse;

        public JSONTask_UpdatePoClose(String po) {
            this.poNo = po;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String do_ = "my";

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                if (!ipAddress.equals("")) {

                    //   http://localhost:8082/IrGetItemData?CONO=290&ITEMCODE=28200152701

                    link = "http://" + ipAddress.trim() + headerDll.trim() + "/UPDATEPO" ;
                    Log.e("linkUpdatePo", "" + link);
                }
            } catch (Exception e) {
                //progressDialog.dismiss();
                Toast.makeText(context, R.string.fill_setting, Toast.LENGTH_SHORT).show();

            }



                //*************************************
                String ipAddress = "",JsonResponse="";
                Log.e("UPDATEPO", "JsonResponseEXPORTDROPPRICE");


                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpPost request = new HttpPost();
                    try {
                        request.setURI(new URI(link));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("CONO", CONO.trim()));
                    nameValuePairs.add(new BasicNameValuePair("JSONSTR", stringNoObject.toString().trim()));
                     Log.e("nameValuePairs","JSONSTR"+stringNoObject.toString().trim());


                    request.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));


                    HttpResponse response = client.execute(request);


                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(response.getEntity().getContent()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                    }

                    in.close();


                    JsonResponse = sb.toString();
                    Log.e("JsonResponse", "ExporVoucher" + JsonResponse);



                return JsonResponse;


            }//org.apache.http.conn.HttpHostConnectException: Connection to http://10.0.0.115 refused
            catch (HttpHostConnectException ex) {
                ex.printStackTrace();
//                progressDialog.dismiss();

                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {

                        Toast.makeText(context, "Ip Connection Failed ", Toast.LENGTH_LONG).show();
                    }
                });



            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception", "" + e.getMessage());
                return null;
            }
            return JsonResponse;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("onPostExecute",""+result.toString());
            if (result != null) {





            }

        }
    }
    public class JSONTask_getItemKind extends AsyncTask<String, String, String> {

        private String itemNo = "", JsonResponse;

        public JSONTask_getItemKind(String itemNo) {
            this.itemNo = itemNo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String do_ = "my";

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                if (!ipAddress.equals("")) {

                 //   http://localhost:8082/IrGetItemData?CONO=290&ITEMCODE=28200152701

                    link = "http://" + ipAddress.trim() + headerDll.trim() + "/IrGetItemData?CONO=" + CONO.trim()+"&&ITEMCODE="+convertToEnglish(itemNo.trim());
                    Log.e("link", "" + link);
                }
            } catch (Exception e) {

            }

            try {

                //*************************************

                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new
                        InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();


                // JsonResponse = sb.toString();

                String finalJson = sb.toString();
                Log.e("finalJson***Import", "itemNo"+finalJson);



                return finalJson;


            }//org.apache.http.conn.HttpHostConnectException: Connection to http://10.0.0.115 refused
            catch (HttpHostConnectException ex) {
                ex.printStackTrace();
//                progressDialog.dismiss();

                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {

                        Toast.makeText(context, "Ip Connection Failed ", Toast.LENGTH_LONG).show();
                    }
                });


                return null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception", "" + e.getMessage());
                return null;
            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                validateKind = false;
                if (result.contains("ITEMTYPE")) {
                    try {
                        ZoneModel requestDetail=new ZoneModel();
                        JSONArray requestArray = null;
                        requestArray =  new JSONArray(result);


                        for (int i = 0; i < requestArray.length(); i++) {
                            JSONObject infoDetail = requestArray.getJSONObject(i);
                            requestDetail = new ZoneModel();
                            requestDetail.setZONETYPE(infoDetail.get("ITEMTYPE").toString());
                            requestDetail.setZoneCode(infoDetail.get("ITEMCODE").toString());
                            requestDetail.setZONENAME(infoDetail.get("ITEMNAME").toString());
                           barcode= infoDetail.get("ITEMCODE").toString();
                        }
                        itemKind=requestDetail.getZONENAME();
                        zonetype=requestDetail.getZONETYPE();
                        Log.e("itemKind",""+itemKind);
                        if(MainActivity.setflage==0)
                        itemKintText.setText(requestDetail.getZONETYPE());
                        else
                        if(MainActivity.setflage == 1)
                        itemKintText1.setText(requestDetail.getZONETYPE());

                    } catch (JSONException e) {
//                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                }
                else
                {
                     if(MainActivity.setflage==0)
                    itemKintText.setText("NOTEXIST");
                    else
                     if(MainActivity.setflage == 1)
                        itemKintText1.setText("NOTEXIST");
                }





            }
            else {
                itemKintText.setText("ErrorNet");
            }
        }
    }

    public class JSONTask_getCompanyInfo extends AsyncTask<String, String, String> {

        private String itemNo = "", JsonResponse;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String do_ = "my";

        }

        @Override
        protected String doInBackground(String... params) {

            try {

                if (!ipAddress.equals("")) {
                    //http://localhost:8082/IrGetCoYear

                    link = "http://" + ipAddress.trim() + headerDll.trim() + "/IrGetCoYear";
                    Log.e("link", "" + link);
                }
            } catch (Exception e) {

            }

            try {

                //*************************************

                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new
                        InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();


                // JsonResponse = sb.toString();

                String finalJson = sb.toString();
                Log.e("finalJson***Import", "Company" + finalJson);



                return finalJson;


            }//org.apache.http.conn.HttpHostConnectException: Connection to http://10.0.0.115 refused
            catch (HttpHostConnectException ex) {
                ex.printStackTrace();
//                progressDialog.dismiss();

                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {

                        Toast.makeText(context, "Ip Connection Failed ", Toast.LENGTH_LONG).show();
                    }
                });


                return null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception", "" + e.getMessage());
                return null;
            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null ) {
                // {
                //    "CoNo": "200",
                //    "CoYear": "2021",
                //    "CoNameA": "Al Rayyan Plastic Factory 2017"
                //  },

                if (result.contains("CoNo")) {
                    try {
                        CompanyInfo requestDetail = new CompanyInfo();
                        JSONArray requestArray = null;
                        requestArray = new JSONArray(result);
                        companyInList = new ArrayList<>();


                        for (int i = 0; i < requestArray.length(); i++) {
                            JSONObject infoDetail = requestArray.getJSONObject(i);
                            requestDetail = new CompanyInfo();
                            requestDetail.setCoNo(infoDetail.get("CoNo").toString());
                            requestDetail.setCoYear(infoDetail.get("CoYear").toString());
                            requestDetail.setCoNameA(infoDetail.get("CoNameA").toString());

                            companyInList.add(requestDetail);
                        }
                        if (companyInList.size() != 0) {
                            getListCom.setText("fill");
                        }


//                            itemKintText.setText(requestDetail.getZONETYPE());


                    } catch (JSONException e) {
//                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                }


            }
            else {
                if (MainActivity.setflage == 0)
                    itemKintText.setText("NOTEXIST");
                else
                    if(MainActivity.setflage == 1)
                    itemKintText1.setText("NOTEXIST");
            }
        }
    }

    private class JSONTask_getAllZoneCode extends AsyncTask<String, String, JSONArray> {

        private String custId = "", JsonResponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String do_ = "my";

        }

        @Override
        protected JSONArray doInBackground(String... params) {

            try {
                if (!ipAddress.equals("")) {
                    //http://localhost:8082/IrGetAllZone?CONO=290

                    link = "http://" + ipAddress.trim() + headerDll.trim() + "/IrGetAllZone?CONO=" + CONO.trim();
                    Log.e("link", "" + link);
                }
            } catch (Exception e) {

            }

            try {

                //*************************************

                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));

//

                HttpResponse response = client.execute(request);


                BufferedReader in = new BufferedReader(new
                        InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";
                Log.e("finalJson***Import", sb.toString());

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();


                // JsonResponse = sb.toString();

                String finalJson = sb.toString();
                Log.e("finalJson***Import", finalJson);


                JSONArray parentObject = new JSONArray(finalJson);

                return parentObject;


            }//org.apache.http.conn.HttpHostConnectException: Connection to http://10.0.0.115 refused
            catch (HttpHostConnectException ex) {
                ex.printStackTrace();
//                progressDialog.dismiss();

                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {

                        Toast.makeText(context, "Ip Connection Failed", Toast.LENGTH_LONG).show();
                    }
                });


                return null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception", "" + e.getMessage());
//                progressDialog.dismiss();
                return null;
            }


            //***************************

        }

        @Override
        protected void onPostExecute(JSONArray array) {
            super.onPostExecute(array);

            JSONObject result = null;


            if (array != null ) {
                if (array.length() != 0) {


                    for (int i = 0; i < array.length(); i++) {
                        try {
                            result = array.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ZoneModel itemZone = new ZoneModel();
                        try {
                            itemZone.setZoneCode(result.getString("ZONENO"));
                            itemZone.setZONENAME(result.getString("ZONENAME"));
                            itemZone.setZONETYPE(result.getString("ZONETYPE"));

                            listAllZone.add(itemZone);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }
            }
        }
    }






    /////////
    private class JSONTask_getAllPOdetails extends AsyncTask<String, String, String> {

        private String custId = "", JsonResponse;
        Shipment shipment;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String do_ = "my";

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                if (!ipAddress.equals("")) {
                    //http://localhost:8082/IrGetAllZone?CONO=290

                    link = "http://" + ipAddress.trim() + headerDll.trim() + "/IrGetItemInfo?CONO=" + CONO.trim() + "&PONO=" + poNo.trim() + "&ITEMCODE=" + convertToEnglish(NewShipment.barCode.trim());

                    Log.e("link", "" + link);
                }
            } catch (Exception e) {
                Log.e("getAllPOdetails", e.getMessage());
            }

//                } catch (Exception e) {
//            Log.e("getAllPOdetails",e.getMessage());
//                }
//
            try {
//
//                //*************************************
//
                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));

//

                HttpResponse response = client.execute(request);


                BufferedReader in = new BufferedReader(new
                        InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";
                Log.e("finalJson***Import", sb.toString());

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();


                // JsonResponse = sb.toString();

                String finalJson = sb.toString();
                Log.e("finalJson***Import", finalJson);


                //JSONArray parentObject = new JSONArray(finalJson);

                return finalJson;


            }//org.apache.http.conn.HttpHostConnectException: Connection to http://10.0.0.115 refused
            catch (HttpHostConnectException ex) {
                ex.printStackTrace();
//                progressDialog.dismiss();

                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {

                        Toast.makeText(context, "Ip Connection Failed", Toast.LENGTH_LONG).show();
                    }
                });


                return null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception", "" + e.getMessage());
//                progressDialog.dismiss();
                return null;
            }


            //***************************

        }

        @Override
        protected void onPostExecute(String array) {
            super.onPostExecute(array);
Log.e("shiparray",array);
            JSONObject jsonObject1 = null;

            if (array != null) {
                if (array.length() != 0) {
                    if (array.contains("ItemOCode")) {


                        try {
                            JSONArray requestArray = null;
                            requestArray = new JSONArray(array);

                            for (int i = 0; i < requestArray.length(); i++) {

                                shipment = new Shipment();
                                jsonObject1 = requestArray.getJSONObject(i);
                                shipment.setBarcode(jsonObject1.getString("ItemOCode"));
                                shipment.setPoqty(jsonObject1.getString("Qty"));
                                shipment.setItemname(jsonObject1.getString("ItemNameA"));
                                shipment.setBoxNo(jsonObject1.getString("Hints"));
                                POdetailslist.add(shipment);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        //NewShipment.respon.setText(POdetailslist.get(0).getBarcode().toString());
                        itemname.setText(POdetailslist.get(0).getItemname());
                        Log.e("itemname",itemname.getText().toString());
                        PoQTY.setText(POdetailslist.get(0).getPoqty());
                        poqty = POdetailslist.get(0).getPoqty();
                        //



                        posize = POdetailslist.size();
                        NewShipment.respon.setText("ItemOCode");

                    }
                    else
                    {
                        NewShipment.respon.setText("invlalid");
                    }
                }
            }
            else{
                NewShipment.respon.setText("Nointernet");
            }

        }

    }
    private class JSONTask_getAllPOboxNO extends AsyncTask<String, String, String> {

        private String custId = "", JsonResponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String do_ = "my";
            Log.e("onPreExecute", "onPreExecute");
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                if (!ipAddress.equals("")) {


                   link = "http://" + ipAddress.trim() + headerDll.trim() + "/IrGetBOXNO?CONO=" + CONO.trim() + "&PONO=" + convertToEnglish(poNo.trim());

                    Log.e("boxlink", "" + link);
                }
            } catch (Exception e) {
                Log.e("Exception",""+e.getMessage());
            }


            try {

                //*************************************

                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));

//

                HttpResponse response = client.execute(request);


                BufferedReader in = new BufferedReader(new
                        InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";
                Log.e("finalJson***Import", sb.toString());

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();


                // JsonResponse = sb.toString();

                String finalJson = sb.toString();
                Log.e("finalJson***Import", finalJson);


//                JSONArray parentObject = new JSONArray(finalJson);

                return finalJson;


            }//org.apache.http.conn.HttpHostConnectException: Connection to http://10.0.0.115 refused
            catch (HttpHostConnectException ex) {
                ex.printStackTrace();
//                progressDialog.dismiss();

                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {

                        Toast.makeText(context, "Ip Connection Failed", Toast.LENGTH_LONG).show();
                    }
                });


                return null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception", "" + e.getMessage());
//                progressDialog.dismiss();
                return null;
            }


            //***************************

        }

        @Override
        protected void onPostExecute(String respon) {
            super.onPostExecute(respon);

            JSONObject jsonObject1 = null;


            if (respon != null) {
                if (respon.length() != 0) {
                    if (respon.contains("BOXNO")) {
                        JSONArray array = null;


                        try {
                            array = new JSONArray(respon);



                        if (array.length()>0)for (int i = 0; i < array.length(); i++) {
                            try {
                                jsonObject1 = array.getJSONObject(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {

                                BoxNolist.add(jsonObject1.getString("BOXNO"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        NewShipment.boxnorespon.setText("BOXNO");
                        if (NewShipment.boxnorespon.getText().length() > 0) {
                            NewShipment.boxno.setEnabled(true);
                            NewShipment.boxno.requestFocus();
                        }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    else if (respon.contains("No Parameter Found")) {
                        NewShipment.boxnorespon.setText("Not");
                    }
                    }

                }//nuul

        }

    }
        private class JSONTask_getAllStoreData extends AsyncTask<String, String, String> {


            Store store;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                String do_ = "my";

            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    if (!ipAddress.equals("")) {
                        http:
//http://10.0.0.22:8082/Getsore?CONO=304

                        link = "http://" + ipAddress.trim() + headerDll.trim() + "/Getsore?CONO="+CONO.trim();

                        Log.e("link", "" + link);
                    }
                } catch (Exception e) {
                    Log.e("getAllSto", e.getMessage());
                }

                try {

                    //*************************************

                    String JsonResponse = null;
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet();
                    request.setURI(new URI(link));

//

                    HttpResponse response = client.execute(request);


                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(response.getEntity().getContent()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";
                    Log.e("finalJson***Import", sb.toString());

                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                    }

                    in.close();


                    // JsonResponse = sb.toString();

                    String finalJson = sb.toString();


                    //JSONArray parentObject = new JSONArray(finalJson);

                    return finalJson;


                }//org.apache.http.conn.HttpHostConnectException: Connection to http://10.0.0.115 refused
                catch (HttpHostConnectException ex) {
                    ex.printStackTrace();
//                progressDialog.dismiss();

                    Handler h = new Handler(Looper.getMainLooper());
                    h.post(new Runnable() {
                        public void run() {

                            Toast.makeText(context, "Ip Connection Failed ", Toast.LENGTH_LONG).show();
                        }
                    });


                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Exception", "" + e.getMessage());
//                progressDialog.dismiss();
                    return null;
                }


                //***************************

            }

            @Override
            protected void onPostExecute(String array) {
                super.onPostExecute(array);

                JSONObject jsonObject1 = null;
                if (array != null) {
                    if (array.contains("STORENO")) {

                            if (array.length() != 0) {
                                try {
                                    JSONArray requestArray = null;
                                    requestArray = new JSONArray(array);
                                    Storelist.clear();

                                    for (int i = 0; i < requestArray.length(); i++) {
                                        store = new Store();
                                        jsonObject1 = requestArray.getJSONObject(i);
                                        store.setSTORENO(jsonObject1.getString("STORENO"));
                                        store.setSTORENAME(jsonObject1.getString("STORENAME"));

                                        Storelist.add(store);
                                    }
                                    Replacement.respon.setText("fill");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }



                    }
                } else {

                    Replacement.respon.setText("nodata");


                }
            }



    }



    private class JSONTask_getQTYOFZone extends AsyncTask<String, String, String> {

        private String custId = "", JsonResponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String do_ = "my";

        }

        @Override
        protected String doInBackground(String... params) {

            try {
                if (!ipAddress.equals("")) {
                    // http://10.0.0.22:8082/GetZoneDatInfo?CONO=290&ZONENO=6&ITEMCODE=6253349404082

                    link = "http://" + ipAddress.trim() + headerDll.trim() + "/GetZoneDatInfo?CONO=" + CONO.trim()+"&ZONENO="+zone.getText().toString().trim()+"&ITEMCODE="+itemcode.getText().toString().trim();
                    //    link ="http://10.0.0.22:8082/GetZoneDatInfo?CONO=304&ZONENO=C03D&ITEMCODE=8058578435856";
                    Log.e("link", "" + link);
                }
            } catch (Exception e) {

            }

            try {

                //*************************************

                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));

//

                HttpResponse response = client.execute(request);


                BufferedReader in = new BufferedReader(new
                        InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";
                Log.e("finalJson***Import", sb.toString());

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();


                // JsonResponse = sb.toString();

                String finalJson = sb.toString();
                Log.e("finalJson***Import", finalJson);


               // JSONArray parentObject = new JSONArray(finalJson);

                return finalJson;


            }//org.apache.http.conn.HttpHostConnectException: Connection to http://10.0.0.115 refused
            catch (HttpHostConnectException ex) {
                ex.printStackTrace();
//                progressDialog.dismiss();

                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {

                        Toast.makeText(context, "Ip Connection Failed", Toast.LENGTH_LONG).show();
                    }
                });


                return null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception", "" + e.getMessage());
//                progressDialog.dismiss();
                return null;
            }


            //***************************

        }

        @Override
        protected void onPostExecute(String array) {
            super.onPostExecute(array);
           String d="";
            JSONObject jsonObject1 = null;

            if (array != null) {
                if (array.contains("QTY")) {

                    if (array.length() != 0) {
                        try {

                            JSONArray requestArray = null;
                            requestArray = new JSONArray(array);

                            for (int i = 0; i < requestArray.length(); i++) {

                                ZoneModel zoneModel = new ZoneModel();
                                jsonObject1 = requestArray.getJSONObject(i);
                                zoneModel.setZoneCode(jsonObject1.getString("ZONENO"));
                                zoneModel.setItemCode(jsonObject1.getString("ITEMCODE"));
                                zoneModel.setQty(jsonObject1.getString("QTY"));
                                d=jsonObject1.getString("QTY");
                                listQtyZone.add(zoneModel);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                    Replacement.qty.setText(d);
                    qtyrespons.setText("QTY");

                    Log.e("qtyrespons",qtyrespons.getText().toString()+d);

                    Log.e("qtyrespons",qtyrespons.getText().toString()+d);
                    }
                else {

                    qtyrespons.setText("nodata");


                }

            }
            else {
                qtyrespons.setText("nodata");
            }
        }

    }


    private class  JSONTask_getAllPoNum extends AsyncTask<String, String, String> {

        private String custId = "", JsonResponse;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String do_ = "my";
            Log.e("onPreExecute", "onPreExecute");
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                if (!ipAddress.equals("")) {


                    link = "http://" + ipAddress.trim() + headerDll.trim() + "/GetAllOPO?CONO=" + CONO.trim();

                    Log.e("link", "" + link);
                }
            } catch (Exception e) {
                Log.e("Exception",""+e.getMessage());
            }


            try {

                //*************************************

                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));

//

                HttpResponse response = client.execute(request);


                BufferedReader in = new BufferedReader(new
                        InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";
                Log.e("finalJson***Import", sb.toString());

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();


                // JsonResponse = sb.toString();

                String finalJson = sb.toString();
                Log.e("finalJson***Import", finalJson);


//                JSONArray parentObject = new JSONArray(finalJson);

                return finalJson;


            }//org.apache.http.conn.HttpHostConnectException: Connection to http://10.0.0.115 refused
            catch (HttpHostConnectException ex) {
                ex.printStackTrace();
//                progressDialog.dismiss();

                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {

                        Toast.makeText(context, "Ip Connection Failed", Toast.LENGTH_LONG).show();
                    }
                });


                return null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception", "" + e.getMessage());
//                progressDialog.dismiss();
                return null;
            }


            //***************************

        }

        @Override
        protected void onPostExecute(String respon) {
            super.onPostExecute(respon);

            JSONObject jsonObject1 = null;


            if (respon != null) {
                if (respon.length() != 0) {
                    if (respon.contains("PONO")) {
                        JSONArray array = null;


                        try {
                            array = new JSONArray(respon);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        if (array.length()>0)for (int i = 0; i < array.length(); i++) {
                            try {
                                jsonObject1 = array.getJSONObject(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {

                                PoNolist.add(jsonObject1.getString("PONO"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                    }

                }

            }

        }

    }
}









