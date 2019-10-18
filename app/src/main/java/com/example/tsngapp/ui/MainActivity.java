package com.example.tsngapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.tsngapp.R;

public class MainActivity extends AppCompatActivity {

    private final int LOGIN_ACTIVITY_REQUEST_CODE=1;

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if(menu!=null){
            getMenuInflater().inflate(R.menu.main_menu, menu);
            this.menu = menu;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.action_home:
                break;
            case R.id.action_login:
                startLoginActivity();
                break;
            case R.id.action_logout:
                break;
            case R.id.action_profile:
                break;
                default: break;
        }

        return true;
    }

    private void startLoginActivity(){
        startActivityForResult(
                new Intent(this,LoginActivity.class),
                LOGIN_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LOGIN_ACTIVITY_REQUEST_CODE &&
        resultCode == Activity.RESULT_OK){
            //recebe user
            //atualiza menu
            updateMenuIcons();
        }
    }

    private void updateMenuIcons(){

        //Login -> true: User==null e false User!=null
        menu.getItem(1).setVisible(false);
        //Perfil -> true: User!=null e false User==null
        menu.getItem(2).setVisible(true);
        //Logout -> true: User!=null e false User!=null
        menu.getItem(3).setVisible(true);

    }


}
