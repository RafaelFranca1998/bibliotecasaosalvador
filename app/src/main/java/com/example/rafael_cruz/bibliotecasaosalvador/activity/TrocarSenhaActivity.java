package com.example.rafael_cruz.bibliotecasaosalvador.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.config.DAO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TrocarSenhaActivity extends AppCompatActivity {

    private FirebaseAuth auntenticacao;
    private Button buttonConcluirEdicao;
    private EditText editTextAntigaSenha;
    private EditText editTextNovaSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trocar_senha);

        //--------------------------------------FIND VIEWS------------------------------------------
        editTextAntigaSenha =  findViewById(R.id.edit_text_senha_antiga);
        editTextNovaSenha =  findViewById(R.id.edit_text_nova_senha);
        buttonConcluirEdicao = findViewById(R.id.bt_concluir_edit_senha);
        //------------------------------------------------------------------------------------------
        buttonConcluirEdicao.setOnClickListener(v -> atualizarSenha());
    }


    private void atualizarSenha(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        auntenticacao = DAO.getFirebaseAutenticacao();
        String newPassword = editTextNovaSenha.getText().toString();
        String oldPassword = editTextAntigaSenha.getText().toString();

        auntenticacao
                .signInWithEmailAndPassword(user.getEmail(),oldPassword)
                .addOnSuccessListener(authResult -> user.updatePassword(newPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this,
                                        "Senha Atualizada",Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }))
                .addOnFailureListener(e -> Toast.makeText
                        (this,"Erro ao atualizar: "+e.getMessage(),
                                Toast.LENGTH_LONG).show());

    }
}
