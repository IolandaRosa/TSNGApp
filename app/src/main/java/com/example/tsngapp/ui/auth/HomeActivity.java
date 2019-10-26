package com.example.tsngapp.ui.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.tsngapp.R;
import com.example.tsngapp.helpers.Constants;
import com.example.tsngapp.model.User;
import com.example.tsngapp.network.AsyncResponse;
import com.example.tsngapp.network.AsyncTaskPostLogout;
import com.example.tsngapp.ui.LoginActivity;
import com.example.tsngapp.view_managers.LoginManager;

public class HomeActivity extends AppCompatActivity {

    private User user;
    private AsyncTaskPostLogout logoutTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = this.getIntent();

        Bundle extras = intent.getExtras();

        extras.getSerializable(Constants.INTENT_USER_KEY);

        user = (User)getIntent().getExtras().getSerializable(Constants.INTENT_USER_KEY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.auth_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()){
            case R.id.action_logout:
                makeLogout();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void makeLogout(){

        this.logoutTask = new AsyncTaskPostLogout(new AsyncResponse() {
            @Override
            public void onTaskDone(String jsonString) {
                if(jsonString.equals(Constants.HTTP_OK)){
                    Toast.makeText(HomeActivity.this, "Logout Success", Toast.LENGTH_SHORT).show();

                    //todo - retira token das shared preferences e coloca user a null e passa para a login activity
                    user = null;
                    LoginManager.getInstance().removeFromSharedPreference(HomeActivity.this);

                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    HomeActivity.this.finish();
                }
                else{
                    Toast.makeText(HomeActivity.this, "Logout Success", Toast.LENGTH_SHORT).show();
                }
            }
        }, user.getAcessToken());

        logoutTask.execute(Constants.LOGOUT_URL);
    }
}
