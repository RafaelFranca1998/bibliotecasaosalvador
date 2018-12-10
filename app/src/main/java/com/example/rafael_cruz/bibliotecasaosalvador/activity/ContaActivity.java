package com.example.rafael_cruz.bibliotecasaosalvador.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import com.example.rafael_cruz.bibliotecasaosalvador.R;
import com.example.rafael_cruz.bibliotecasaosalvador.config.DAO;
import com.example.rafael_cruz.bibliotecasaosalvador.config.Preferencias;
import com.example.rafael_cruz.bibliotecasaosalvador.config.helper.PagerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContaActivity extends AppCompatActivity {
    private CircleImageView imgAccount;
    private Button btChangeImg;


    private String url;
    private String idUser;
    private String linkDownload;
    private Uri pathLocalImg;
    private double progress;
    private ProgressDialog pd;
    private FirebaseAuth user;
    private File userImg;


    public StorageReference storageReference;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conta);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.divider));
        tabLayout.addTab(tabLayout.newTab().setText("Informações"));
        tabLayout.addTab(tabLayout.newTab().setText("Preferências"));
        tabLayout.addTab(tabLayout.newTab().setText("Configu\nrações"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //------------------------------------------------------------------------------------------
        imgAccount = findViewById(R.id.img_account);
        btChangeImg = findViewById(R.id.bt_trocar_ft);
        //------------------------------------------------------------------------------------------
        user = FirebaseAuth.getInstance();
        if (user != null){
            idUser = user.getUid();
        }
        userImg = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/Biblioteca/Profile/user_image.png");
        Preferencias preferencias = new Preferencias(ContaActivity.this);
        url = "gs://bibliotecasaosalvador.appspot.com/images/account/"+ idUser +"/image_account.png";
        //------------------------------------------------------------------------------------------
        Toolbar toolbar = findViewById(R.id.toolbar_tab_layout);
        toolbar.setTitle(R.string.conta);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(v -> finish());

        storageReference = DAO.getFirebaseStorage()
                .child("images")
                .child("account")
                .child(idUser)
                .child("image_account.png");
        linkDownload = storageReference.getPath();


        if (!isConected(this) && userImg.exists()){
            getLocalImage();
        }else {
            updateImgAccount();
        }


        btChangeImg.setOnClickListener(v -> shareImg());

        Button btSair =  findViewById(R.id.bt_logout);
        btSair.setOnClickListener(v -> {
            user.signOut();
            finish();
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null){
            pathLocalImg = data.getData();
            deleteImage();
        }
    }

    /**
     * Method to obtain the path for images.
     */
    public void shareImg(){
        new Thread(() -> {
            Intent intent =  new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,1);
        }).start();
    }

    /**
     * Method to update the user image.
     */
    private void updateImgAccount(){
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        final long FIVE_MEGABYTE = 1024 * 1024 * 5;
        pd = new ProgressDialog(ContaActivity.this);
        pd.setCancelable(false);
        pd.setMessage("Carregando");
        pd.show();
        // mProgressBar.setProgress((int)progress);
        System.out.println("Upload is " + progress + "% done");
        reference.getBytes(FIVE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            writeImg(bitmap);
            imgAccount.setImageBitmap(bitmap);
            pd.dismiss();
        }).addOnFailureListener(exception -> {
            pd.dismiss();
            Log.e("Erro: ",exception.getMessage());
        });
    }


    private void sendImg(){
        try {
            Bitmap imagem = MediaStore.Images.Media.getBitmap((ContaActivity.this).getContentResolver(), pathLocalImg);
            // comprimir no formato jpeg
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imagem.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            byte[] byteData = stream.toByteArray();
            UploadTask uploadTask = storageReference.putBytes(byteData);
            final ProgressDialog pd2 = new ProgressDialog(ContaActivity.this);
            // Listen for state changes, errors, and completion of the upload.
            uploadTask.addOnProgressListener(taskSnapshot -> {
                progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                pd2.setCancelable(false);
                pd2.setProgress((int) progress);
                pd2.setMessage("Carregando (" + (int) progress + "%)");
                pd2.show();
                System.out.println("Upload is " + progress + "% done");
            }).addOnPausedListener(taskSnapshot -> {
                System.out.println("Upload is paused");
                pd2.dismiss();
            }).addOnFailureListener(exception -> {
                Toast.makeText(ContaActivity.this, "Falha ao carregar a imagem", Toast.LENGTH_SHORT).show();
                pd2.dismiss();
                updateImgAccount();
                exception.printStackTrace();
            }).addOnSuccessListener(taskSnapshot -> {
                pd2.dismiss();
                Toast.makeText(ContaActivity.this, "Imagem carregada!", Toast.LENGTH_SHORT).show();
                updateImgAccount();
            });
            updateImgAccount();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getLocalImage(){
        Bitmap myBitmap = BitmapFactory.decodeFile(userImg.getAbsolutePath());
        imgAccount.setImageBitmap(myBitmap);
    }

    private void deleteImage(){
        storageReference = DAO.getFirebaseStorage()
                .child("images")
                .child("account")
                .child(idUser)
                .child("image_account.png");
        storageReference.delete().addOnSuccessListener(aVoid -> {
            try {
                sendImg();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(e -> {
            try {
                sendImg();
            }catch (Exception e2){
                e2.printStackTrace();
            }
        });
    }

    public  void writeImg(Bitmap bmp){
        try {
            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/Biblioteca/Profile";
            File dir = new File(file_path);
            if(!dir.exists())
                dir.mkdirs();
            File file = new File(dir, "user_image.png");
            FileOutputStream fOut = new FileOutputStream(file);

            bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void baixarImagem(Uri imagemUri){
        StorageReference islandRef = DAO.getFirebaseStorage().child(imagemUri.toString());
        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                writeImg(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("Erro: ",exception.getMessage());
            }
        });
    }
    public void deleteImg(){
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/Biblioteca/Profile";
        File f0 = new File(dir, "user_image.png");
        boolean d0 = f0.delete();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imgAccount.setImageBitmap(null);
    }

    public static boolean isConected(Context cont){
        ConnectivityManager conmag = (ConnectivityManager)cont.getSystemService(Context.CONNECTIVITY_SERVICE);

        if ( conmag != null ) {
            conmag.getActiveNetworkInfo();

            if (conmag.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) return true;

            if (conmag.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()) return true;
        }
        return false;
    }
}
