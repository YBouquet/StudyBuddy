package ch.epfl.sweng.studdybuddy.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import ch.epfl.sweng.studdybuddy.R;
import ch.epfl.sweng.studdybuddy.firebase.FirebaseReference;
import ch.epfl.sweng.studdybuddy.services.chat.ChatMessage;
import ch.epfl.sweng.studdybuddy.util.Messages;

public class ChatActivity extends AppCompatActivity
{
    String groupID;
    public FirebaseReference ref;
    private StorageReference storageRef;
    private Button addImage, cameraButon;
    private Uri filePath;
    private String downloadUri;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int OPEN_CAMERA_REQUEST = 42;
    private FloatingActionButton fab;

    private ProgressDialog mProgress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        this.ref = initRef();
        storageRef = FirebaseStorage.getInstance().getReference();

        Bundle extras = getIntent().getExtras();

        if(extras != null)
        {
            groupID = extras.getString(Messages.groupID);
        }
        else
        {
            Toast.makeText(this,
                    "Group not found in database",
                    Toast.LENGTH_LONG).show();
            finish();
        }
        downloadUri = "";
        displayChatMessages();
        mProgress = new ProgressDialog(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        addImage = (Button) findViewById(R.id.gallery);
        addImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), PICK_IMAGE_REQUEST);
            }
        });
        cameraButon = (Button) findViewById(R.id.camera);
        cameraButon.setOnClickListener(getCameraListener());

        //check if camera permission is granted and when it isn't ask for it]
        if(ContextCompat.checkSelfPermission(ChatActivity.this.getApplicationContext(),
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
        {
        }
        else
        {
            ActivityCompat.requestPermissions(ChatActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    1);

        }



        fab.setOnClickListener(getFabListener());
    }

    @NonNull
    private View.OnClickListener getCameraListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, OPEN_CAMERA_REQUEST);

            }

        };
    }

    //Get the filepath of the selected image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null)
        {
            if(requestCode == PICK_IMAGE_REQUEST && data.getData() != null)
            {
                filePath = data.getData();
                uploadImage();
            }
            else if(requestCode == OPEN_CAMERA_REQUEST)
            {
                mProgress.setMessage("Uploading");
                mProgress.show();

                //get the camera image
                Bundle extras = data.getExtras();
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] dataBAOS = baos.toByteArray();
                ImageView image = (ImageView) ChatActivity.this.findViewById(R.id.imgViewGall);

                uploadImage(dataBAOS);
            }
        }
    }

    private void uploadImage(byte[] dataBAOS)
    {
        //Firebase storage folder where you want to put the images
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://studybuddy-a5bfe.appspot.com/images");

        //name of the image file (add time to have different files to avoid rewrite on the same file)
        StorageReference imagesRef = storageRef.child("image" + String.valueOf(System.currentTimeMillis()) + ".jpg");

        //upload image to firebase
        UploadTask uploadTask = imagesRef.putBytes(dataBAOS);
        uploadTask.addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception exception)
            {
                Toast.makeText(getApplicationContext(), "Sending failed", Toast.LENGTH_SHORT).show();
                mProgress.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!urlTask.isSuccessful()) ;
                downloadUri = urlTask.getResult().toString();
                mProgress.dismiss();
                Toast.makeText(ChatActivity.this, downloadUri, Toast.LENGTH_SHORT).show();
                fab.performClick();
            }
        });
    }

    private void uploadImage()
    {
        if(filePath != null)
        {
            mProgress.setTitle("Uploading...");
            mProgress.show();
            StorageReference ref = storageRef.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
            {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>()
            {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if(task.isSuccessful())
                    {
                        downloadUri = task.getResult().toString();
                        mProgress.dismiss();
                        fab.performClick();
                        Toast.makeText(ChatActivity.this, downloadUri, Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        mProgress.dismiss();
                        Toast.makeText(ChatActivity.this, "Failed Uploading", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }
    }

    @NonNull
    protected View.OnClickListener getFabListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                EditText input = (EditText) findViewById(R.id.input);
                if(input.getText().toString().trim().length() > 0 || !downloadUri.isEmpty())
                {
                    pushToFirebase(input.getText().toString(), downloadUri);
                    input.setText("");
                    downloadUri = "";
                    displayChatMessages();
                }
            }
        };
    }

    private void pushToFirebase(String input, String downloadUri)
    {
        ref.select(Messages.FirebaseNode.CHAT).select(groupID).push(new ChatMessage(input,
                FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), downloadUri));
    }


    public FirebaseReference initRef()
    {
        return new FirebaseReference();
    }

    public void displayChatMessages()
    {
        ListView listOfMessages = (ListView) findViewById(R.id.list_of_messages);

        FirebaseListAdapter<ChatMessage> adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message,
                FirebaseDatabase.getInstance().getReference().child(Messages.FirebaseNode.CHAT).child(groupID))
        {
            @Override
            protected void populateView(View v, ChatMessage model, int position)
            {
                TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                messageUser.setText(model.getMessageUser());
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);
                messageTime.setText(DateFormat.format("dd-MM (HH:mm)",
                        model.getMessageTime()));

                //  if (!model.getMessageText().isEmpty()) { TODO add the check again probably
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                messageText.setText(model.getMessageText());
                //}

                ImageView image = (ImageView) v.findViewById(R.id.imgViewGall);
                String modelUri = model.getImageUri();
                if(modelUri != null && !modelUri.isEmpty())
                {
                    //Put the image in the chat
                    Glide.with(ChatActivity.this).load(modelUri).apply(new RequestOptions().override(500, 700)).into(image);
                }
                else
                {
                    image.setImageResource(android.R.color.transparent);
                }


            }

        };
        listOfMessages.setAdapter(adapter);
    }
}
