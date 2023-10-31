package xbisme.iot_project.Fragment;

import static android.app.ProgressDialog.show;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import xbisme.iot_project.Activity.MainActivity;
import xbisme.iot_project.R;

public class SignupScreen extends Fragment {
    TextView text;
    EditText email, password, displayName,userAddress,userPhone;
    AppCompatButton signup_btn;
    private FirebaseAuth mAuth;
// ...
// Initialize Firebase Auth

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_signup_screen, container,false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        text = view.findViewById(R.id.signup_click);
        signup_btn = view.findViewById(R.id.btn_signup);
        email = view.findViewById(R.id.signup_email);
        password = view.findViewById(R.id.password_signup);
        displayName = view.findViewById(R.id.name_signup);
        userPhone = view.findViewById(R.id.signup_phone);
        userAddress = view.findViewById(R.id.signup_address);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_signupScreen_to_loginScreen);
            }
        });

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String indexEmail, indexPassword, indexName,indexAddress, indexPhone;
                indexEmail = String.valueOf(email.getText());
                indexPassword = String.valueOf(password.getText());
                indexName = String.valueOf(displayName.getText());
                indexPhone = String.valueOf(userPhone.getText());
                indexAddress = String.valueOf(userAddress.getText());
                mAuth.createUserWithEmailAndPassword(indexEmail, indexPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user;
                                    user = FirebaseAuth.getInstance().getCurrentUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(indexName)
                                            .build();
                                    Log.i("result", indexName);
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
                                    databaseReference.child(user.getUid()).child("address").setValue(indexAddress);
                                    databaseReference.child(user.getUid()).child("phone").setValue(indexPhone);
                                    // Sign in success, update UI with the signed-in user's information
                                    Navigation.findNavController(view).navigate(R.id.action_signupScreen_to_mainScreen);
                                }
                            }
                        });
            }
        });
    }

}