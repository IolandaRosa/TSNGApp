package com.example.tsngapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.tsngapp.R;

public class RegisterActivity extends AppCompatActivity {

    /*
    'name' => 'required|String',
                'username' => 'required|String|unique:users,username|regex:/^[A-Za-záàâãéèêíóôõúçÁÀÂÃÉÈÍÓÔÕÚÇ ]+$/',
                'email' => 'required|email|unique:users,email',
                'password' => 'required',
                'type' => 'required|in:admin,normal',
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void register(View view) {
    }

    public void redirectToLogin(View view) {
        setupLoginActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.initial_menu,menu);
        menu.getItem(1).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.action_login) {
            setupLoginActivity();
        }

        return true;
    }

    private void setupLoginActivity(){
        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
        this.finish();
    }
}
