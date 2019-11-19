package com.example.tsngapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.tsngapp.R;
import com.example.tsngapp.helpers.Constants;
import com.example.tsngapp.helpers.ErrorCode;
import com.example.tsngapp.helpers.ErrorValidator;
import com.example.tsngapp.helpers.JsonConverterSingleton;
import com.example.tsngapp.model.DataToSend;
import com.example.tsngapp.model.User;
import com.example.tsngapp.model.UserGender;
import com.example.tsngapp.model.UserType;
import com.example.tsngapp.network.AsyncResponse;
import com.example.tsngapp.network.AsyncTaskAuthenticationPost;
import com.example.tsngapp.view_managers.RegisterManager;

public class RegisterActivity extends AppCompatActivity {

    private final String LOG_TAG = "RegisterActivity";

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText elderNameEditText;
    private EditText elderBirthDateEditText;
    private UserGender elderGender;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText passwordConfEditText;

    private AsyncTaskAuthenticationPost registerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.nameEditText = findViewById(R.id.registerNameEditText);
        this.emailEditText = findViewById(R.id.registerEmailEditText);
        this.elderNameEditText = findViewById(R.id.registerElderNameEditText);
        this.elderBirthDateEditText = findViewById(R.id.registerElderBirthDateEditText);
        this.elderGender = UserGender.UNDEFINED;
        this.usernameEditText = findViewById(R.id.registerUsernameEditText);
        this.passwordEditText = findViewById(R.id.registerPasswordEditText);
        this.passwordConfEditText = findViewById(R.id.registerPasswordConfEditText);

    }

    public void register(View view) {

        this.hideSoftKeyboard();

        String name = this.nameEditText.getText().toString().trim();
        String email = this.emailEditText.getText().toString().trim();
        String elderName = this.elderNameEditText.getText().toString().trim();
        String elderBirthDate = this.elderBirthDateEditText.getText().toString().trim();
        String username = this.usernameEditText.getText().toString().trim();
        final String password = this.passwordEditText.getText().toString().trim();
        String passConf = this.passwordConfEditText.getText().toString().trim();

        //validar inputs e gerar json
        DataToSend dataToSendForUser = RegisterManager.getInstance().generateJsonForPost(name, username,email,password,passConf);

        DataToSend dataToSendForElder = RegisterManager.getInstance().generateJsonElderForPost(elderName, elderBirthDate, elderGender);

        //Erros do user
        if(dataToSendForUser.getErrorCodes().size()>0
                || dataToSendForElder.getErrorCodes().size()>0){

            for (ErrorCode e: dataToSendForUser.getErrorCodes()){
                if(e == ErrorCode.NAME_EMPTY){
                    nameEditText.setError(ErrorValidator.getInstance().getErrorMessage(e));
                }
                if(e == ErrorCode.USERNAME_EMPTY){
                    usernameEditText.setError(ErrorValidator.getInstance().getErrorMessage(e));
                }
                if(e == ErrorCode.EMAIL_EMPTY){
                    emailEditText.setError(ErrorValidator.getInstance().getErrorMessage(e));
                }
                if(e == ErrorCode.PASSWORD_EMPTY){
                    passwordEditText.setError(ErrorValidator.getInstance().getErrorMessage(e));
                }
                if(e == ErrorCode.PASSWORD_CONF_EMPTY){
                    passwordConfEditText.setError(ErrorValidator.getInstance().getErrorMessage(e));
                }
                if(e == ErrorCode.PASSWORD_DONT_MATCH){
                    passwordConfEditText.setError(ErrorValidator.getInstance().getErrorMessage(e));
                }
            }

            for(ErrorCode e: dataToSendForElder.getErrorCodes()){
                if(e == ErrorCode.ELDER_NAME_EMPTY){
                    elderNameEditText.setError(ErrorValidator.getInstance().getErrorMessage(e));
                }

                if(e == ErrorCode.ELDER_BIRTH_DATE_EMPTY ||
                        e == ErrorCode.ELDER_BIRTH_DATE_INAVLID_DATE ||
                        e == ErrorCode.ELDER_BIRTH_DATE_INVALID_FORMAT){
                    elderBirthDateEditText.setError(ErrorValidator.getInstance().getErrorMessage(e));
                }

                if(e == ErrorCode.GENDER_UNDEFINED){
                    ErrorValidator.getInstance().showErrorMessage(this,ErrorValidator.getInstance().getErrorMessage(e));
                }
            }

            return;
        }

        this.registerTask = new AsyncTaskAuthenticationPost(dataToSendForUser.getJsonObject(), new AsyncResponse() {
            @Override
            public void onTaskDone(String jsonString) {

                //todo registar elder

                getUserAndRedirectToLogin(password, jsonString);


            }
        });

        this.registerTask.execute(Constants.REGISTER_URL);

    }

    private void getUserAndRedirectToLogin(String password, String jsonString){

        User user = JsonConverterSingleton.getInstance().jsonToUser(jsonString, true);

        if(user != null){
            //Passar para atividade de login
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.INTENT_USER_KEY, user);
            bundle.putString(Constants.INTENT_PASSWORD_KEY, password);

            Intent intent = new Intent();

            intent.putExtras(bundle);

            setResult(Activity.RESULT_OK,intent);
            finish();
        }
        else{
            ErrorValidator.getInstance().showErrorMessage(this, "An error occurred during register. Please try again.");
        }
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
        setResult(Activity.RESULT_CANCELED,new Intent());
        finish();
    }

    public void onRadioButtonClicked(View view) {
        //Esconde o teclado
        this.hideSoftKeyboard();
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.registerRadioBtnFemale:
                if (checked)
                    this.elderGender = UserGender.FEMALE;
                    break;
            case R.id.registerRadioBtnMale:
                if (checked)
                    this.elderGender = UserGender.MALE;
                    break;
        }
    }

    private void hideSoftKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            Log.d(LOG_TAG, "Failed keyboard hidding" + e.getMessage());
        }
    }

    public void pickElderPhoto(View view) {

        this.hideSoftKeyboard();
        //todo escolher photo de galeria
    }
}
