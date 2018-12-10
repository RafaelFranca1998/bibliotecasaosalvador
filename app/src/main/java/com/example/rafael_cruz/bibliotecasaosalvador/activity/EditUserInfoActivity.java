package com.example.rafael_cruz.bibliotecasaosalvador.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.config.actions.Insert;
import com.example.rafael_cruz.bibliotecasaosalvador.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditUserInfoActivity extends AppCompatActivity {
    private String idUsuario;
    private EditText editNome;
    private EditText editSobrenome;
    private EditText editEmail;
    private EditText editCurso;
    private EditText editSemestre;
    private EditText editIdade;
    private Button btConcluir;
    private Usuario mUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);
        //------------------------------------------------------------------------------------------
        editNome = findViewById(R.id.edit_text_Info_nome);
        editSobrenome = findViewById(R.id.edit_text_Info_sobrenome);
        editEmail = findViewById(R.id.edit_text_Info_email);
        editCurso = findViewById(R.id.edit_text_Info_curso);
        editSemestre = findViewById(R.id.edit_text_Info_semestre);
        editIdade = findViewById(R.id.edit_text_Info_idade);
        btConcluir = findViewById(R.id.bt_edit_info_concluir);
        //------------------------------------------------------------------------------------------
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        idUsuario = user.getUid();


        btConcluir.setOnClickListener(v -> insert());

        getDatabase();

    }

    private void getDatabase(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore
                .collection("usuarios")
                .document(idUsuario).get().addOnCompleteListener(task -> {
                    try {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            mUsuario = doc.toObject(Usuario.class);
                            editNome.setText(mUsuario.getNome());
                            editSobrenome.setText(mUsuario.getSobreNome());
                            editEmail.setText(mUsuario.getEmail());
                            editEmail.setEnabled(false);
                            editCurso.setText(mUsuario.getCurso());
                            editIdade.setText(mUsuario.getIdade());
                            editSemestre.setText(mUsuario.getSemestre());
                        }
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                });
    }

    private void insert (){
       mUsuario.setIdUsuario(idUsuario);
       mUsuario.setLinkImgAccount("gs://bibliotecasaosalvador.appspot.com/images/account/"+ idUsuario +"/image_account.png");
       mUsuario.setNome(editNome.getText().toString());
       mUsuario.setSobreNome(editSobrenome.getText().toString());
       mUsuario.setEmail(editEmail.getText().toString());
       mUsuario.setSemestre(editSemestre.getText().toString());
       mUsuario.setIdade(editIdade.getText().toString());
       mUsuario.setCurso(editCurso.getText().toString());
       Insert insert =  new Insert(this);
       insert.saveUserInFireStore(mUsuario);
       insert.addOnSuccessListener(taskSnapshot -> {
           Toast.makeText(EditUserInfoActivity.this,"Dados atualizados",Toast.LENGTH_LONG).show();
           finish();
       });
    }

}
