package com.tika.app2.Home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tika.app2.R;

public class aadharupload extends AppCompatActivity {
    EditText editPDFName;
    Button btn_upload;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aadharupload);

        editPDFName= findViewById(R.id.txt_pdfName);
        btn_upload= findViewById(R.id.btn_upload);
        storageReference= FirebaseStorage.getInstance().getReference();
        databaseReference= FirebaseDatabase.getInstance().getReference("uploads");

        btn_upload.setOnClickListener(view -> selectPDFFile());
    }


    private void selectPDFFile(){
        Intent intent=new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select PDF file"),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK && data !=null && data.getData()!=null) {
            uploadPDFFile(data.getData());
        }
    }
    private void uploadPDFFile(Uri data){
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        StorageReference reference=storageReference.child("uploads/"+System.currentTimeMillis()+".pdf");
        reference.putFile(data).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uri=taskSnapshot.getStorage().getDownloadUrl();
            while(!uri.isComplete());
            Uri url=uri.getResult();

            uploadPDF uploadPDF=new uploadPDF(editPDFName.getText().toString(),url.toString());
            databaseReference.child(databaseReference.push().getKey()).setValue(uploadPDF);
            Toast.makeText(aadharupload.this, "File Uploaded", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }).addOnProgressListener(taskSnapshot -> {
            double progress=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();


            progressDialog.setMessage("Uploaded: "+(int)progress+"%");

        });

    }
}
