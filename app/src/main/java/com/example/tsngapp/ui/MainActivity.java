package com.example.tsngapp.ui;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tsngapp.R;
import com.example.tsngapp.helpers.ErrorValidator;

import javax.xml.validation.Validator;


public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!ErrorValidator.getInstance().checkInternetConnection(this)){
            ErrorValidator.getInstance().showErrorMessage(this, "Please activate your connection to Internet");
            return;
        }

        this.usernameEditText = findViewById(R.id.loginUsernameEditText);
        this.passwordEditText = findViewById(R.id.loginPasswordEditText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);

        menu.getItem(0).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.action_signIn) {
            setupSigupActivity();
        }

        return true;

    }

    private void setupSigupActivity() {
        Toast.makeText(this,"signup",Toast.LENGTH_LONG).show();
    }

    public void login(View view) {
        //Antes de fazer qualuqer coisa ve ligação à internet
        if(!ErrorValidator.getInstance().checkInternetConnection(this)){
            ErrorValidator.getInstance().showErrorMessage(this, "Please activate your connection to Internet");
            return;
        }

        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if(username.isEmpty() || password.isEmpty()){
            ErrorValidator.getInstance().showErrorMessage(this,"There must be no empty fields on form");
            return;
        }

        //se nenhum campo esta vazio faz o login

    }
}
