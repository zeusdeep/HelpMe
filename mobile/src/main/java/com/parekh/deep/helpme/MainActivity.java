package com.parekh.deep.helpme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MainActivity extends Activity {

    private static final String COMMA = ",";
    LocationActivity locationActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationActivity = new LocationActivity(getApplicationContext());
        try {
            sendEmail();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        callPolice();
    }

    private void sendEmail() throws IOException {
        Session session = createSessionObject();

        try {
            Location location = locationActivity
                    .getLocation(LocationManager.NETWORK_PROVIDER);
            String emailBody = Constants.emailMessage;
            if (location != null) {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                Address address = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1).get(0);
                emailBody +=" "+
                        address.getLocality()+COMMA+
                        address.getAdminArea()+COMMA+
                        address.getPostalCode()+COMMA+
                        address.getCountryName()+COMMA +
                        ". The coordinates are Latitude : "+
                        location.getLatitude() + COMMA +
                        " Longitude : " +
                        location.getLongitude()+".";
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Cannot find Location ",
                        Toast.LENGTH_LONG).show();
            }
            Message message = createMessage("deepjparekh@gmail.com", "Help Me",emailBody, session);
            new SendMailTask().execute(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("no-reply@helpMe.com", "Help Me !!"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
        message.setSubject(subject);
        message.setText(messageBody);
        return message;
    }

    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Constants.username, Constants.password);
            }
        });
    }

    private class SendMailTask extends AsyncTask<Message, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
               // Toast.makeText(getApplicationContext(),"Send",Toast.LENGTH_LONG).show();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void callPolice(){

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Constants.phoneNumber));
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    }
