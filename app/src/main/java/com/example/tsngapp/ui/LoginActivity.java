package com.example.tsngapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.tsngapp.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) {
        //Faz login com async task e se for sucesso
        //guarda token
        //vai buscar info do user com token
        setUserInfo();
    }

    private void setUserInfo(){
        //vai buscar user
        //Vai para atividade de login e devolve user
        setResult(Activity.RESULT_OK, new Intent());
        finish();
    }
}
