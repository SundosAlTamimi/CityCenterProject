package com.example.irbidcitycenter;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.irbidcitycenter.Activity.NewShipment;
import com.example.irbidcitycenter.Activity.Replacement;
import com.example.irbidcitycenter.Models.ZoneModel;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.irbidcitycenter.Activity.AddZone.exportStateText;

public class ExportData {
    private Context context;
    public  String ipAddress="",CONO="",headerDll="",link="",PONO="";
    public RoomAllData my_dataBase;
    SweetAlertDialog pdVoucher;
    JSONObject vouchersObject;
    private JSONArray jsonArrayVouchers;
    public  List<NewShipment> listAllShipment   =new ArrayList<>();
    public  List<Replacement> listAllReplacment =new ArrayList<>();
    int typeExportZone=0;
    public ExportData(Context context) {
        this.context = context;
        my_dataBase= RoomAllData.getInstanceDataBase(context);
        try {
            getIpAddress();
        }catch (Exception e){
            Toast.makeText(context, "Fill Ip and Company No", Toast.LENGTH_SHORT).show();
        }

    }
    private void getIpAddress() {
        headerDll="";
        ipAddress=my_dataBase.settingDao().getIpAddress().trim();
        CONO=my_dataBase.settingDao().getCono().trim();
        Log.e("getIpAddress",""+ipAddress);


    }
    public void exportAllUnposted(List<ZoneModel> listZone, List<NewShipment> listShipment, List<Replacement> listReplacment){
        exportZoneList(listZone,2);
        listAllShipment=listShipment;
        listAllReplacment=listReplacment;
    }

    public void exportZoneList(List<ZoneModel> listZone,int type) {
        typeExportZone=type;
        getZoneObject(listZone);
        pdVoucher = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pdVoucher.getProgressHelper().setBarColor(Color.parseColor("#FDD835"));
        pdVoucher.setTitleText(" Start export Vouchers");
        pdVoucher.setCancelable(false);
        pdVoucher.show();

         new JSONTaskDelphi(listZone).execute();
    }

    private void getZoneObject(List<ZoneModel> listZone) {
        jsonArrayVouchers = new JSONArray();
        for (int i = 0; i < listZone.size(); i++)
        {

                jsonArrayVouchers.put(listZone.get(i).getJSONObjectDelphi());

        }
        try {
            vouchersObject=new JSONObject();
            vouchersObject.put("JSN",jsonArrayVouchers);
            Log.e("vouchersObject",""+vouchersObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class JSONTaskDelphi extends AsyncTask<String, String, String> {
        private String JsonResponse = null;
        private HttpURLConnection urlConnection = null;
        private BufferedReader reader = null;
       List<ZoneModel> listZones=new ArrayList<>();

        public JSONTaskDelphi(List<ZoneModel> listZones) {
            this.listZones = listZones;
        }

        public  String salesNo="",finalJson;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;

            try {
                if (!ipAddress.equals("")) {

                    //http://10.0.0.22:8082/ExportIrZone?CONO=290&JSONSTR={JSN:[{ZONENO:AB1,ITEMCODE:123456,QTY:10,TRDATE:13/04/2021,TRTIME:10:15}]}

                    link = "http://"+ipAddress.trim()+headerDll.trim()+"/ExportIrZone";



                    Log.e("URL_TO_HIT",""+link);
                }
            } catch (Exception e) {
                //progressDialog.dismiss();

            }

//********************************************************


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
                nameValuePairs.add(new BasicNameValuePair("JSONSTR", vouchersObject.toString().trim()));
                // Log.e("nameValuePairs","JSONSTR"+vouchersObject.toString().trim());


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



                //*******************************************


            } catch (Exception e) {
            }
            return JsonResponse;

        }


        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            Log.e("onPostExecute",""+result);
            pdVoucher.dismissWithAnimation();
            if (result != null && !result.equals("")) {
                if(result.contains("Saved Successfully"))
                {
                    if(typeExportZone==1)
                    {
                        exportStateText.setText("exported");   
                    }
                    else {
                        if(typeExportZone==2)
                        {
                            updateDataBasePosted();
//                            exportNewShepment();

                        }
                    }
                   


                }else {
                    if(typeExportZone==1) {
                        exportStateText.setText("not");
                    }

                }
//                Toast.makeText(context, "onPostExecute"+result, Toast.LENGTH_SHORT).show();


            } else {  if(typeExportZone==1) {
                exportStateText.setText("not");
            }
                Toast.makeText(context, "onPostExecute", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateDataBasePosted() {
        my_dataBase.zoneDao().updateZonePosted();
        my_dataBase.shipmentDao().updateShipmentPosted();
        my_dataBase.replacementDao().updateReplacmentPosted();
    }
}
