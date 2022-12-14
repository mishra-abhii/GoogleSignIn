package com.example.googlesignin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class GoogleSignActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 100;
    GoogleSignInClient gsc;
    FirebaseAuth mAuth;

    Button btnSignIn;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign);

        mAuth = FirebaseAuth.getInstance();
        btnSignIn = findViewById(R.id.btnsignIn);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(GoogleSignActivity.this);
                progressDialog.setMessage("Google Sign In..");
                progressDialog.show();

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                gsc = GoogleSignIn.getClient(GoogleSignActivity.this, gso);
                gsc.revokeAccess();

                Intent signInIntent = gsc.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if(account!= null){
                    String email = account.getEmail();
                    if(Objects.equals(email, "200108010@hbtu.ac.in")){
                        firebaseAuthWithGoogle(account.getIdToken());
                    }
                    else{
                        progressDialog.dismiss();
                        gsc.signOut();
                        finish();
                        Toast.makeText(GoogleSignActivity.this, "Sign in using college mail id", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            catch (ApiException e){
                progressDialog.dismiss();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            progressDialog.dismiss();
//                            String currentUser = mAuth.getCurrentUser().getUid();
//                            updateUI(currentUser);
                            Intent intent = new Intent(GoogleSignActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(GoogleSignActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }
}