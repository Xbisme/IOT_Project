package xbisme.iot_project.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

import xbisme.iot_project.R;

public class LoginScreen extends Fragment {
    private EditText email, password;
    private ImageView hide_show_icon;
    private FirebaseAuth mAuth;

    private CheckBox checkBox;

    private static final String SHARE_PREFS = "sharedPrefs";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_screen, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView signup = view.findViewById(R.id.signup_btn);
        AppCompatButton login_btn = view.findViewById(R.id.login_btn);
        mAuth = FirebaseAuth.getInstance();
        email = view.findViewById(R.id.login_email);
        password = view.findViewById(R.id.password_email);
        hide_show_icon = view.findViewById(R.id.hide_show_pass_log);
        checkBox = view.findViewById(R.id.checkbox_remember);

        SharedPreferences share = requireContext().getSharedPreferences(SHARE_PREFS, Context.MODE_PRIVATE);
        String check = share.getString("name","");
        if (check.equals("true")) {
            Navigation.findNavController(view)
                    .navigate(R.id.action_loginScreen_to_mainScreen);
        }

        signup.setOnClickListener(view1 ->
                Navigation.findNavController(view1)
                        .navigate(R.id.action_loginScreen_to_signupScreen));

        login_btn.setOnClickListener(view12 -> {
            String indexEmail, indexPassword;
            indexEmail = String.valueOf(email.getText());
            indexPassword = String.valueOf(password.getText());

            if(indexEmail.isEmpty()) {
                email.setError("Please enter your email");
                email.requestFocus();
                return;
            }
            else if(indexPassword.isEmpty()) {
                password.setError("Please enter your password");
                password.requestFocus();
                return;
            }

            mAuth.signInWithEmailAndPassword(indexEmail, indexPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARE_PREFS,Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            if (checkBox.isChecked()) {

                                editor.putString("name","true");
                                editor.apply();
                            }
                            else {
                                editor.putString("name","");
                                editor.apply();
                            }
                            Navigation.findNavController(view12)
                                    .navigate(R.id.action_loginScreen_to_mainScreen);

                        }

                            else{
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (FirebaseAuthInvalidCredentialsException |
                                         FirebaseAuthInvalidUserException e) {
                                    handleFirebaseAuthException(e);
                                } catch (Exception e) {
                                    Log.e("Failed to log", "Failed");
                                }
                            }
                        });
        });

        hide_show_icon.setOnClickListener(view13 -> {
            if(password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                hide_show_icon.setImageResource(R.drawable.hide);
            }
            else {
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                hide_show_icon.setImageResource(R.drawable.show);
            }
        });
    }
    private void handleFirebaseAuthException(FirebaseAuthException e) {
        String errorCode = e.getErrorCode();

        switch (errorCode) {
            case "ERROR_WRONG_PASSWORD":
                password.setError("Your password is wrong. Please check and try again");
                password.requestFocus();
                break;
            case "ERROR_INVALID_EMAIL":
                email.setError("Your email is invalid. Please use another email or register");
                email.requestFocus();
                break;
            case "ERROR_USER_NOT_FOUND":
                email.setError("Your email is not assigned. Please register");
                email.requestFocus();
                break;
            default:
                Log.e("Failed to log", errorCode);
                break;
        }
    }
}