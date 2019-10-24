package com.example.tsngapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tsngapp.R;
import com.example.tsngapp.helpers.Constants;
import com.example.tsngapp.helpers.ErrorCode;
import com.example.tsngapp.helpers.ErrorValidator;
import com.example.tsngapp.helpers.JsonConverterSingleton;
import com.example.tsngapp.model.DataToSend;
import com.example.tsngapp.model.User;
import com.example.tsngapp.network.AsyncGetAuthTask;
import com.example.tsngapp.network.AsyncResponse;
import com.example.tsngapp.network.AsyncTaskAuthenticationPost;
import com.example.tsngapp.view_managers.LoginManager;


public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private AsyncTaskAuthenticationPost loginTask;
    private AsyncGetAuthTask getUserTask;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!ErrorValidator.getInstance().checkInternetConnection(this)) {
            ErrorValidator.getInstance().showErrorMessage(this, "Please activate your connection to Internet");
            return;
        }

        //todo ir buscar token as shared preference
        //todo ir buscar user com token
        //todo se user não for null redireciona para pagina inicial

        this.usernameEditText = findViewById(R.id.registerNameEditText);
        this.passwordEditText = findViewById(R.id.registerUsernameEditText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.initial_menu, menu);

        menu.getItem(0).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.action_signIn) {
            setupRegisterActivity();
        }

        return true;
    }

    private void setupRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);

        startActivityForResult(intent, Constants.REGISTER_ACTIVITY_CODE);
        //startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
        //this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.REGISTER_ACTIVITY_CODE && resultCode == Activity.RESULT_OK){
            if(data!=null){
                user = (User)data.getSerializableExtra(Constants.INTENT_USER_KEY);
                String password = data.getStringExtra(Constants.INTENT_PASSWORD_KEY);
                makeLogin(user.getEmail(),password);
            }
        }
    }

    public void login(View view) {
        //Antes de fazer qualuqer coisa ve ligação à internet
        if (!ErrorValidator.getInstance().checkInternetConnection(this)) {
            ErrorValidator.getInstance().showErrorMessage(this, "Please activate your connection to Internet");
            return;
        }

        //Obtém password e username, valida e cria json para enviar no pedido
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        makeLogin(username, password);

    }

    private void makeLogin(String username, String password){

        final DataToSend dataToSend = LoginManager.getInstance().generateJsonForPost(username, password);

        if(dataToSend.getErrorCodes().size()>0){
            for (ErrorCode e: dataToSend.getErrorCodes()){
                if(e == ErrorCode.EMAIL_EMPTY){
                    usernameEditText.setError(ErrorValidator.getInstance().getErrorMessage(e));
                }
                if(e == ErrorCode.PASSWORD_EMPTY){
                    passwordEditText.setError(ErrorValidator.getInstance().getErrorMessage(e));
                }
            }

            return;
        }

        this.loginTask =new AsyncTaskAuthenticationPost(dataToSend.getJsonObject(), new AsyncResponse() {
            @Override
            public void onTaskDone(String jsonString) {
                if(jsonString==null || jsonString.isEmpty()){
                    ErrorValidator.getInstance().showErrorMessage(LoginActivity.this, "An error ocurred during the login");
                    return;
                }

                String token = LoginManager.getInstance().getTokenFromJson(jsonString);

                //Grava token nas shared preferences
                LoginManager.getInstance().saveAuthToken(token, LoginActivity.this);

                //Vai buscar a informação do utilizador
                getUserInfo(token);

                //Redireciona para ecra de perfil
            }
        });

        this.loginTask.execute(Constants.LOGIN_URL);

    }

    private void getUserInfo(final String token){
        this.getUserTask = new AsyncGetAuthTask(token, new AsyncResponse() {
            @Override
            public void onTaskDone(String jsonString) {

                //Converte json em User
                user = JsonConverterSingleton.getInstance().jsonToUser(jsonString, false);

                if(user!=null){
                    user.setAcessToken(token);

                    Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();

                    //vai para pagina inicial da aplicação
                    //todo - atualizar
                }
            }
        });

        this.getUserTask.execute(Constants.USERS_ME_URL);
    }
}
