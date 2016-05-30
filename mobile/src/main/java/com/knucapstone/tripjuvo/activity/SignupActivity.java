package com.knucapstone.tripjuvo.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.knucapstone.tripjuvo.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import butterknife.ButterKnife;
import butterknife.Bind;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    String p_vehicle;

    @Bind(R.id.input_name) EditText _nameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_age) EditText _ageText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("선호하는 탈것은?")
                .setItems(R.array.vehicle,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String[] vehicle =
                                        getResources().getStringArray(R.array.vehicle);
                                p_vehicle = vehicle[which];
                                final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                                        R.style.AppTheme_Dark_Dialog);
                                progressDialog.setIndeterminate(true);
                                progressDialog.setMessage("Creating Account...");
                                progressDialog.show();

                                RadioButton R_M = (RadioButton)findViewById(R.id.radio_M);
                                RadioButton R_W = (RadioButton)findViewById(R.id.radio_W);

                                String gender = "M";
                                if(R_W.isSelected())
                                    gender = "F";
                                String name = _nameText.getText().toString();
                                String email = _emailText.getText().toString();
                                String age = _ageText.getText().toString();
                                String password = _passwordText.getText().toString();
                                insert(name, password,age, p_vehicle, gender, email);
                                String sql = "INSERT INTO logins (checks, account, age, vehicle) values (1,'"+name+
                                        "',"+age+",'"+p_vehicle+"');";
                                SQLiteDatabase db = openOrCreateDatabase(
                                        "tripjuvo.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
                                db.execSQL(sql);
                                db.close();

                                // TODO: Implement your own signup logic here.

                                new android.os.Handler().postDelayed(
                                        new Runnable() {
                                            public void run() {
                                                // On complete call either onSignupSuccess or onSignupFailed
                                                // depending on success
                                                onSignupSuccess();
                                                // onSignupFailed();
                                                progressDialog.dismiss();
                                            }
                                        }, 2000);
                            }
                        }
                )
                .setCancelable(false)
                .show();

        _signupButton.setEnabled(false);


    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public void insert(String id, String ps, String age, String vehicle, String gender, String email) {
        insertToDatabase(id, ps, age, vehicle, gender, email);
    }

    private void insertToDatabase(String id, String ps, String age, String vehicle, String gender, String email) {

        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //    loading = ProgressDialog.show(SignupActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {

                try {

                    String id = (String) params[0];
                    String ps = (String) params[1];
                    String age = (String) params[2];
                    String vehicle = (String) params[3];
                    String gender = (String) params[4];
                    String email = (String) params[5];

                    String link = "http://tripjuvo.ivyro.net/signup.php";
                    String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
                    data += "&" + URLEncoder.encode("ps", "UTF-8") + "=" + URLEncoder.encode(ps, "UTF-8");
                    data += "&" + URLEncoder.encode("age", "UTF-8") + "=" + URLEncoder.encode(age, "UTF-8");
                    data += "&" + URLEncoder.encode("vehicle", "UTF-8") + "=" + URLEncoder.encode(vehicle, "UTF-8");
                    data += "&" + URLEncoder.encode("gender", "UTF-8") + "=" + URLEncoder.encode(gender, "UTF-8");
                    data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }

            }

        }

        InsertData task = new InsertData();
        task.execute(id, ps, age, vehicle, gender, email);
    }
}