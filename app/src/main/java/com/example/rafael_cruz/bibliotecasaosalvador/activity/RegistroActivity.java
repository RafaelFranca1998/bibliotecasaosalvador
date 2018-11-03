package com.example.rafael_cruz.bibliotecasaosalvador.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.config.DAO;
import com.example.rafael_cruz.bibliotecasaosalvador.config.Preferencias;
import com.example.rafael_cruz.bibliotecasaosalvador.config.actions.Insert;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Objects;

public class RegistroActivity extends AppCompatActivity {
    private RegistroActivity.UserLoginTask mAuthTask = null;
    private EditText editTextName;
    private EditText editTextLastName;
    private EditText editTextEmail;
    private EditText editTextPwd;
    private FirebaseAuth auth;
    private Button buttonRegister;

    private View mProgressView;
    private View mLoginFormView;

    private Usuario user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //-----------------------------------CAMPOS DO FORM-----------------------------------------
        editTextEmail       = findViewById(R.id.edit_text_email);
        editTextName        = findViewById(R.id.edit_text_nome);
        editTextLastName    = findViewById(R.id.edit_text_sobrenome);
        editTextPwd         = findViewById(R.id.edit_text_senha);
        buttonRegister      = findViewById(R.id.bt_concluir_regisstro);
        mProgressView       = findViewById(R.id.register_progress);
        mLoginFormView      = findViewById(R.id.register_form);
        //------------------------------------------------------------------------------------------
        buttonRegister.setOnClickListener(view -> {
            user = new Usuario();
            user.setNome(editTextName.getText().toString());
            user.setSobreNome(editTextLastName.getText().toString());
            user.setEmail(editTextEmail.getText().toString());
            attemptRegister();
        });
        //------------------------------------------------------------------------------------------
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        editTextName.setError(null);
        editTextLastName.setError(null);
        editTextEmail.setError(null);
        editTextPwd.setError(null);


        // Store values at the time of the login attempt.
        String email = editTextEmail.getText().toString();
        String password = editTextPwd.getText().toString();
        String name = editTextName.getText().toString();
        String lastName = editTextLastName.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            editTextPwd.setError(getString(R.string.error_invalid_password));
            focusView = editTextPwd;
            cancel = true;
        }
        if (!isPasswordValid(password)) {
            editTextPwd.setError(getString(R.string.error_invalid_password));
            focusView = editTextPwd;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError(getString(R.string.error_field_required));
            focusView = editTextEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            editTextEmail.setError(getString(R.string.error_invalid_email));
            focusView = editTextEmail;
            cancel = true;
        }

        // Check for a valid name.
        if (TextUtils.isEmpty(name)) {
            editTextName.setError(getString(R.string.error_field_required));
            focusView = editTextName;
            cancel = true;
        }

        // Check for a valid last name.
        if (TextUtils.isEmpty(lastName)) {
            editTextLastName.setError(getString(R.string.error_field_required));
            focusView = editTextLastName;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new RegistroActivity.UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }


    public void openLoggedUser(){
        showProgress(false);
        Intent intent =  new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Insert insert = new Insert(RegistroActivity.this);
                auth = DAO.getFirebaseAutenticacao();
                user = new Usuario();
                user.setEmail(editTextEmail.getText().toString());
                user.setNome(editTextName.getText().toString());
                user.setSobreNome(editTextLastName.getText().toString());
                final String linkImg = "gs://bibliotecasaosalvador.appspot.com/images/account/" + auth.getUid() + "/image_account.png";
                user.setLinkImgAccount(linkImg);
                auth.createUserWithEmailAndPassword(mEmail, mPassword)
                        .addOnCompleteListener(RegistroActivity.this, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegistroActivity.this, "Sucesso ao registrar usuário", Toast.LENGTH_LONG).show();
                                String identificadorUsuario = auth.getUid();
                                //FirebaseUser user =  task.getResult().getUser();

                                RegistroActivity.this.user.setIdUsuario(identificadorUsuario);
                                Preferencias preferencias = new Preferencias(RegistroActivity.this);
                                preferencias.salvarDados(RegistroActivity.this.user.getNome(), RegistroActivity.this.user.getSobreNome(), RegistroActivity.this.user.getEmail(),
                                        editTextPwd.getText().toString(), identificadorUsuario, linkImg);
                                insert.saveUserInFireStore(user);
                                insert.addOnSuccessListener(taskSnapshot -> openLoggedUser());
                            } else {
                                String errorException;
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    errorException = "Digite uma senha mais forte, contendo letras e numeros";
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    errorException = "O email digitado é inválido, digite outro email";
                                } catch (FirebaseAuthUserCollisionException e) {
                                    errorException = "já existe outra conta com este e-mail";
                                } catch (Exception e) {
                                    errorException = "Erro a o efetuar cadastro";
                                    e.printStackTrace();
                                }
                                Toast.makeText(RegistroActivity.this, errorException, Toast.LENGTH_LONG).show();
                            }
                        });
                return true;
            } catch (Exception e) {
                return false;
            }

            // TODO: register the new account here.
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            if (!success) {
                editTextPwd.setError(getString(R.string.error_incorrect_password));
                editTextPwd.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
