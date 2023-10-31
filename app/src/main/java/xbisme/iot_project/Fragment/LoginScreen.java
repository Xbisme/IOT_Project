package xbisme.iot_project.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import xbisme.iot_project.R;


public class LoginScreen extends Fragment {
    EditText email, password;
    TextView text;
    AppCompatButton login_btn;
    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_screen, container,false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        text = view.findViewById(R.id.signup_btn);
        login_btn = view.findViewById(R.id.login_btn);
        mAuth = FirebaseAuth.getInstance();
        email = view.findViewById(R.id.login_email);
        password = view.findViewById(R.id.password_email);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_loginScreen_to_signupScreen);
            }
        });
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String indexEmail, indexPassword;
                indexEmail = String.valueOf(email.getText());
                indexPassword = String.valueOf(password.getText());
                mAuth.signInWithEmailAndPassword(indexEmail, indexPassword)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Navigation.findNavController(view).navigate(R.id.action_loginScreen_to_mainScreen);
                                }
                            }
                        });

            }
        });
    }
}