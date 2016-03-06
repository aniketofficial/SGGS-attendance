package io.github.aniket.SGGSattendance;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {
    public static String RESPONSE;
    Button getOTPbutton,otp_pswd;
    EditText usrnme,pswd;
    HttpURLConnection connection;
    URL url;
    ArrayList <SubjectHolder> subjectHolders=new ArrayList<>();
    EditText otp;
    Button result;
    String line;
    String OTP=null, headerName=null;
    TextView pswdEnable,dhp;
    public static String cookie;
    static String LoginPage = "https://onlinesggs.org/attendance/index.php";
    static String Otppage = "https://onlinesggs.org/attendance/userAuthOTP.php";
    static String MainPage = "https://onlinesggs.org/attendance/student/";
    public String username=null,password=null;
    public  ConnectivityManager conMgr;
    public String emailcheck,otpcheck;
    public ProgressDialog ringProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usrnme=(EditText)findViewById(R.id.email);
        pswd=(EditText)findViewById(R.id.password);
        pswdEnable=(TextView)findViewById(R.id.password_enabler);
        getOTPbutton = (Button) findViewById(R.id.get_otp_button);
        otp_pswd=(Button)findViewById(R.id.otp_generator2);
        otp = (EditText) findViewById(R.id.OTP);
        result = (Button) findViewById(R.id.result);
        dhp=(TextView)findViewById(R.id.dhp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);


        dhp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pswd.setVisibility(View.INVISIBLE);
               getOTPbutton.setVisibility(View.VISIBLE);
                otp_pswd.setVisibility(View.INVISIBLE);
                dhp.setVisibility(View.INVISIBLE);
                pswdEnable.setVisibility(View.VISIBLE);
            }
        });
    pswdEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pswd.setVisibility(View.VISIBLE);
                otp_pswd.setVisibility(View.VISIBLE);
                pswdEnable.setVisibility(View.INVISIBLE);
                dhp.setVisibility(View.VISIBLE);
                getOTPbutton.setVisibility(View.INVISIBLE);

            }
        });
        getOTPbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usrnme.getText().toString().trim();
                if (username.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter your E-mail ID", Toast.LENGTH_LONG).show();

                } else if (!isNetworkAvailable()) {
                    Toast.makeText(getApplicationContext(), "You are not connected to internet", Toast.LENGTH_LONG).show();
                } else {
                    ringProgressDialog = ProgressDialog.show(MainActivity.this, "Please wait ...", "Logging in", true);
                    ringProgressDialog.setCancelable(false);
                    // Trigger Async Task (onPreExecute method)
                    new generateOtp().execute();

                }
            }
        });
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OTP = otp.getText().toString().trim();
                if(OTP.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Enter the OTP received via Email",Toast.LENGTH_LONG).show();

                }
                else if(!isNetworkAvailable()) {
                    Toast.makeText(getApplicationContext(),"You are not connected to internet",Toast.LENGTH_LONG).show();

                } else {
                    ringProgressDialog = ProgressDialog.show(MainActivity.this, "Please wait ...", "Logging in", true);
                    ringProgressDialog.setCancelable(false);

                    new sendOtp().execute();
                }
            }
        });
        otp_pswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usrnme.getText().toString().trim();
                password=pswd.getText().toString().trim();

                if(username.isEmpty()||password.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Email or Password is not Entered",Toast.LENGTH_LONG).show();

                }

                else if (!isNetworkAvailable()) {
                    Toast.makeText(getApplicationContext(),"You are not connected to internet",Toast.LENGTH_LONG).show();
                }
                else
                {
                    ringProgressDialog = ProgressDialog.show(MainActivity.this, "Please wait ...", "Sending email to server", true);
                    ringProgressDialog.setCancelable(false);
                    // Trigger Async Task (onPreExecute method)
                    new LoginWithPassword().execute();
                }
            }
        });

    }



    // Async Task Class
    class generateOtp extends AsyncTask<String, String, String> {

        // Show Progress bar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Shows Progress Bar Dialog and then call doInBackground method

        }

        // Log in
        @Override
        protected String doInBackground(String... f_url) {
            try {


                url = new URL(LoginPage);
                CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                for (int i = 1; (headerName = connection.getHeaderFieldKey(i)) != null; i++) {
                    if (headerName.contains("Set-Cookie")) {
                        //System.out.println("cookie is : "+connection.getHeaderField(headerName).split(";")[0]);
                        Log.e("cookie", connection.getHeaderField(headerName).split(";")[0]);
                        cookie = connection.getHeaderField(headerName).split(";")[0];
                    }

                }
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Cookie", cookie);
                connection.setDoOutput(true);
                DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
                writer.writeBytes("email=" + username + "&pass=&faculty_forgot=Get+OTP");
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = reader.readLine()) != null) {
                    emailcheck+= line;
                }

//                Log.i("emailcheck",emailcheck);

                for (int i = 1; (headerName = connection.getHeaderFieldKey(i)) != null; i++) ;

            } catch (Exception e) {

                Toast.makeText(MainActivity.this,"Network problem",Toast.LENGTH_LONG).show();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String file_url) {



            ringProgressDialog.hide();
//            Document doc = Jsoup.parse(emailcheck);
//            Elements script = doc.getElementsByTag("script");
            if(emailcheck.contains("alert('This Email or Mobile not yet Register.')"))
            {
                Toast.makeText(getApplicationContext(),"Wrong email id or Number. Enter Valid one",Toast.LENGTH_LONG).show();
                emailcheck=null;

            }
             else {
                Toast.makeText(getApplicationContext(), "OTP SENT to your mail", Toast.LENGTH_LONG).show();
                getOTPbutton.setVisibility(View.INVISIBLE);
                usrnme.setVisibility(View.INVISIBLE);
                pswdEnable.setVisibility(View.INVISIBLE);
                otp.setVisibility(View.VISIBLE);
                result.setVisibility(View.VISIBLE);
            }
        }

    }

    // Async Task Class
    class sendOtp extends AsyncTask<String, String, String> {

        // Show Progress bar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Shows Progress Bar Dialog and then call doInBackground method

        }

        // otp send and receive response
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                url = new URL(Otppage);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
                writer.writeBytes("otp=" + OTP + "&user_sub_confirm=");
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = reader.readLine()) != null) {
//                     Log.i("response", line);
                    otpcheck+= line;
                }

                for (int i = 1; (headerName = connection.getHeaderFieldKey(i)) != null; i++) ;

                url = new URL(MainPage);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                InputStream inputStream1 = connection.getInputStream();
                BufferedReader reader1 = new BufferedReader(new InputStreamReader(inputStream1));
                while ((line = reader1.readLine()) != null) {
//                    Log.i("response", line);
                    RESPONSE+= line;
                }


            } catch (Exception e) {
                Toast.makeText(MainActivity.this,"Network problem",Toast.LENGTH_LONG).show();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String file_url) {
            // Dismiss the dialog
            ringProgressDialog.hide();
            if(otpcheck.contains("×Invalid One Time Password (OTP) entered. Please try again.")) {
                Toast.makeText(getApplicationContext(),"Invalid One Time Password (OTP) entered", Toast.LENGTH_LONG).show();
            }
           else {
                Toast.makeText(getApplicationContext(), "Successfully logged in", Toast.LENGTH_SHORT).show();

//                Intent i = new Intent(MainActivity.this, ShowAttendance.class);
//                startActivity(i);
                Document doc= Jsoup.parse(RESPONSE);
                Element table=doc.select("table").get(0);
                Elements rows = table.select("tr");
//                Log.i ("sizeis", String.valueOf(rows.size()));
                float percentage=0;int j=0;
                subjectHolders.clear();
                for (int i = 1; i < rows.size(); i++) { //first row is the col names so skip it.
                    Element row = rows.get(i);
                    Elements cols = row.select("td");
                    SubjectHolder holder=new SubjectHolder();
                    holder.setFullSubjectName(cols.get(0).text());
                    holder.setSubjectName(getInitials(cols.get(0).text()));
                    holder.setAttendedLectures(cols.get(2).text());
                    holder.setConductedLectures(cols.get(3).text());
                    if (Float.valueOf(cols.get(3).text())!=0){
                        percentage+=Float.valueOf(cols.get(4).text());
                        j++;
                    }
                    holder.setPercentAttendance(cols.get(4).text());
                    subjectHolders.add(holder);
                }

                Intent intent = new Intent(MainActivity.this, AttendanceActivity.class);
                intent.putParcelableArrayListExtra("holder",subjectHolders);
                intent.putExtra("name", doc.getElementsByClass("profile-data-name").get(0).text());
                intent.putExtra("attendance", String.valueOf(percentage/j));
                startActivity(intent);
            }
        }
    }

    // Async Task Class
    class LoginWithPassword extends AsyncTask<String, String, String> {

        // Show Progress bar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Shows Progress Bar Dialog and then call doInBackground method

        }

        // Log in
        @Override
        protected String doInBackground(String... f_url) {
            try {
                url = new URL(LoginPage);
                CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                for (int i=1; (headerName = connection.getHeaderFieldKey(i))!=null; i++)
                {
                    if (headerName.contains("Set-Cookie"))
                    {
                       // System.out.println("cookie is : "+connection.getHeaderField(headerName).split(";")[0]);
                        cookie=connection.getHeaderField(headerName).split(";")[0];
                    }

                }
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
               // connection.setRequestProperty("Cookie", cookie);
                connection.setDoOutput(true);
                DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
                writer.writeBytes("email="+username+"&faculty_login=Login&pass="+password);
                connection.connect();
                for (int i=1; (headerName = connection.getHeaderFieldKey(i))!=null; i++);
                url = new URL(MainPage);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                while ((line=reader.readLine())!=null)
                {
                    //Build here the logic to parse the data
//                    Log.i("response",line);
                    RESPONSE+=line;
                 //   System.out.println(line);
                }
            } catch (Exception e) {
                Toast.makeText(MainActivity.this,"Network problem",Toast.LENGTH_LONG).show();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String file_url) {
            // Dismiss the dialog  SGGS Attendance - Login
            ringProgressDialog.hide();
//            org.jsoup.nodes.Document doc = Jsoup.parse(RESPONSE);

//            String title =doc.title();
            if(RESPONSE.contains("SGGS Attendance - Login")) {
                Toast.makeText(getApplicationContext(), "Wrong Email id or Password", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_LONG).show();

//                Intent i = new Intent(MainActivity.this, ShowAttendance.class);
//                startActivity(i);
                Document doc= Jsoup.parse(RESPONSE);
                Element table=doc.select("table").get(0);
                Elements rows = table.select("tr");
//                Log.i ("sizeis", String.valueOf(rows.size()));
                float percentage=0;int j=0;
                subjectHolders.clear();
                for (int i = 1; i < rows.size(); i++) { //first row is the col names so skip it.
                    Element row = rows.get(i);
                    Elements cols = row.select("td");
                    SubjectHolder holder=new SubjectHolder();
                    holder.setFullSubjectName(cols.get(0).text());
                    holder.setSubjectName(getInitials(cols.get(0).text()));
                    holder.setAttendedLectures(cols.get(2).text());
                    holder.setConductedLectures(cols.get(3).text());
                    if (Float.valueOf(cols.get(3).text())!=0){
                        percentage+=Float.valueOf(cols.get(4).text());
                        j++;
                    }
                    holder.setPercentAttendance(cols.get(4).text());
                    subjectHolders.add(holder);
                }

                Intent intent = new Intent(MainActivity.this, AttendanceActivity.class);
                intent.putParcelableArrayListExtra("holder",subjectHolders);
                intent.putExtra("name", doc.getElementsByClass("profile-data-name").get(0).text());
                intent.putExtra("attendance", String.valueOf(percentage/j));
                startActivity(intent);
            }
        }
    }

    public String getInitials(String name) {
        StringBuilder initials = new StringBuilder();
        boolean addNext = true;
        if (name != null) {
            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                if (c == ' ' || c == '-' || c == '.') {
                    addNext = true;
                } else if (addNext) {
                    initials.append(c);
                    addNext = false;
                }
            }
        }
        return initials.toString();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
