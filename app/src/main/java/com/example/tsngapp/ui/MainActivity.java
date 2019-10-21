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
import com.example.tsngapp.helpers.Constants;
import com.example.tsngapp.helpers.ErrorValidator;
import com.example.tsngapp.network.AsyncResponse;
import com.example.tsngapp.network.AsyncTaskLoginPost;
import com.example.tsngapp.view_managers.LoginManager;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private AsyncTaskLoginPost task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!ErrorValidator.getInstance().checkInternetConnection(this)) {
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
        Toast.makeText(this, "signup", Toast.LENGTH_LONG).show();
    }

    public void login(View view) {
        //Antes de fazer qualuqer coisa ve ligação à internet
        if (!ErrorValidator.getInstance().checkInternetConnection(this)) {
            ErrorValidator.getInstance().showErrorMessage(this, "Please activate your connection to Internet");
            return;
        }

        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        final JSONObject jsonObject = LoginManager.getInstance().generateJsonForPost(username, password);

        if(jsonObject==null){
            ErrorValidator.getInstance().showErrorMessage(this, "Some error happen. There must be no empty fields on form");
            return;
        }


        //se nenhum campo esta vazio faz o login
        this.task=new AsyncTaskLoginPost(jsonObject, new AsyncResponse() {
            @Override
            public void onTaskDone(String jsonString) {
                if(jsonString==null || jsonString.isEmpty()){
                    ErrorValidator.getInstance().showErrorMessage(MainActivity.this, "An error ocurred during the login");
                    return;
                }

                //Em caso de sucesso
                Toast.makeText(MainActivity.this,"login",Toast.LENGTH_LONG).show();

                //Grava token nas shared preferences
                LoginManager.getInstance().saveAuthToken();
                //Vai buscar perfil
                //Redireciona para ecra de perfil
            }
        });

        this.task.execute(Constants.LOGIN_URL);
    }
}
