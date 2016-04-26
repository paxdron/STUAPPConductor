package com.paxdron.stuappconductor;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

import shem.com.materiallogin.MaterialLoginView;
import shem.com.materiallogin.MaterialLoginViewListener;

public class Login extends AppCompatActivity {

    public static final String USER="USER";
    public static final String ID_USER="IDUSER";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final List<String> mail = Arrays.asList(getResources().getStringArray(R.array.usersMail));
        final List<String> pass = Arrays.asList(getResources().getStringArray(R.array.pass));
        final List<String> idCon = Arrays.asList(getResources().getStringArray(R.array.idCon));
        final List<String> users = Arrays.asList(getResources().getStringArray(R.array.userName));

        final MaterialLoginView login = (MaterialLoginView) findViewById(R.id.login);
        login.setListener(new MaterialLoginViewListener() {
            @Override
            public void onRegister(TextInputLayout registerUser, TextInputLayout registerPass, TextInputLayout registerPassRep) {
                //Handle register
            }

            @Override
            public void onLogin(TextInputLayout loginUser, TextInputLayout loginPass) {
                //Handle login
                /*Intent i=new Intent(getApplicationContext(),MainActivity.class);
                i.putExtra(USER,loginUser.toString());
                startActivity(i);
                Snackbar.make(login, "Login success!", Snackbar.LENGTH_LONG).show();*/
                if(mail.contains(loginUser.getEditText().getText().toString())){
                    int indice=mail.indexOf(loginUser.getEditText().getText().toString());
                    if(pass.get(indice).equals(loginPass.getEditText().getText().toString())){
                        Intent i=new Intent(getApplicationContext(),MainActivity.class);
                        i.putExtra(USER, users.get(indice));
                        i.putExtra(ID_USER,idCon.get(indice));
                        startActivity(i);
                    }
                    else{
                        loginPass.setError("Wrong Password");
                    }
                }
                else
                    loginUser.setError("Usuario no Existente");

            }
        });
    }


}
