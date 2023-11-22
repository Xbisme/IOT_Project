package xbisme.iot_project.Fragment;

// Android lib
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

// Androidx lib
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

// Firebase lib
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// Java Objects lib
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Package
import xbisme.iot_project.Data.ReadWriteUserDetail;
import xbisme.iot_project.R;

public class SignupScreen extends Fragment {
    private EditText email;
    private EditText password, confirm_password;
    private EditText displayName;
    private EditText userAddress;
    private EditText userPhone;
    private ImageView hide_show_icon,hide_show_cf_icon;
    private FirebaseAuth mAuth;

    // Create View
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_signup_screen, container,false);
    }

    // View Created
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Mapping
        mAuth = FirebaseAuth.getInstance();
        // Init var
        TextView login = view.findViewById(R.id.signup_click);
        AppCompatButton signup_btn = view.findViewById(R.id.btn_signup);
        email = view.findViewById(R.id.signup_email);
        password = view.findViewById(R.id.password_signup);
        confirm_password = view.findViewById(R.id.cf_password_signup);
        hide_show_icon = view.findViewById(R.id.hide_show_pass_sig);
        hide_show_cf_icon = view.findViewById(R.id.hide_show_cfpass_sig);
        displayName = view.findViewById(R.id.name_signup);
        userPhone = view.findViewById(R.id.signup_phone);
        userAddress = view.findViewById(R.id.signup_address);

        login.setOnClickListener(
                view1 -> Navigation.findNavController(view1)
                        .navigate(R.id.action_signupScreen_to_loginScreen));

        signup_btn.setOnClickListener(view12 -> {

            //Obtain the entered data
            String indexEmail = String.valueOf(email.getText());
            String indexPassword = String.valueOf(password.getText());
            String indexConfirmPassword = String.valueOf(confirm_password.getText());
            String indexName = String.valueOf(displayName.getText());
            String indexPhone = String.valueOf(userPhone.getText());
            String mobileRegex = "(0|84)\\d{9}";
            Matcher mobileMatcher;
            Pattern mobilePattern = Pattern.compile(mobileRegex);
            mobileMatcher = mobilePattern.matcher(indexPhone);
            String indexAddress = String.valueOf(userAddress.getText());

            if(TextUtils.isEmpty(indexName)) {
                displayName.setError("Please enter your full name");
                displayName.requestFocus();
                return;
            }
            else if(TextUtils.isEmpty(indexPhone)|| indexPhone.length() < 10) {
                userPhone.setError("Please enter your phone number");
                userPhone.requestFocus();
                return;
            }
            else if (!mobileMatcher.find()) {
                userPhone.setError("Phone number is invalid");
                userPhone.requestFocus();
                return;
            }
            else if(TextUtils.isEmpty(indexAddress)) {
                userAddress.setError("Please enter your address");
                userAddress.requestFocus();
                return;
            }
            else if(TextUtils.isEmpty(indexEmail)) {
                email.setError("Please enter your email");
                email.requestFocus();
                return;
            }
            else if(TextUtils.isEmpty(indexPassword)) {
                password.setError("Please enter your password");
                password.requestFocus();
                return;
            }
            else if(TextUtils.isEmpty(indexConfirmPassword)) {
                confirm_password.setError("Please confirm your password");
                confirm_password.requestFocus();
                return;
            }
            else if (!indexConfirmPassword.equals(indexPassword)) {
                confirm_password.setError("The password and confirm password is not equal");
                confirm_password.requestFocus();
                return;
            }

            mAuth.createUserWithEmailAndPassword(indexEmail, indexPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            ReadWriteUserDetail writeUserDetail = new ReadWriteUserDetail(indexName, indexAddress, indexPhone);
                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("user");

                            if (user != null) {
                                referenceProfile.child(user.getUid()).setValue(writeUserDetail)
                                        .addOnCompleteListener(task1 ->
                                                Navigation.findNavController(view12).
                                                        navigate(R.id.action_signupScreen_to_mainScreen));
                            }
                        } else {
                            try {
                                throw Objects.requireNonNull(task.getException());

                            } catch (FirebaseAuthWeakPasswordException e) {
                                handleFirebaseAuthWeakPasswordException(e);

                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                handleFirebaseAuthInvalidCredentialsException(e);

                            } catch (FirebaseAuthUserCollisionException e) {
                                handleFirebaseAuthUserCollisionException(e);

                            } catch (Exception e) {
                                Log.e("Failed to reg", "failed");
                            }
                        }
                    });
        });
        hide_show_icon.setOnClickListener(view14 -> {
            if(password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                hide_show_icon.setImageResource(R.drawable.hide);
            }
            else {
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                hide_show_icon.setImageResource(R.drawable.show);
            }
        });
        hide_show_cf_icon.setOnClickListener(view13 -> {
            if(confirm_password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                confirm_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                hide_show_cf_icon.setImageResource(R.drawable.hide);
            }
            else {
                confirm_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                hide_show_cf_icon.setImageResource(R.drawable.show);
            }
        });
    }
    private void handleFirebaseAuthWeakPasswordException(FirebaseAuthWeakPasswordException e) {
        password.setError("Your password is too weak. Please use a mix of alphabets, numbers, and special characters");
        password.requestFocus();
    }

    private void handleFirebaseAuthInvalidCredentialsException(FirebaseAuthInvalidCredentialsException e) {
        String errorCode = e.getErrorCode();

        if (errorCode.equals("ERROR_WRONG_PASSWORD")) {
            password.setError("Your password is wrong. Please check and try again");
            password.requestFocus();
        } else if (errorCode.equals("ERROR_INVALID_EMAIL")) {
            email.setError("Your email is invalid. Please use another email");
            email.requestFocus();
        } else {
            Log.e("Failed to reg", "Invalid");
        }
    }

    private void handleFirebaseAuthUserCollisionException(FirebaseAuthUserCollisionException e) {
        email.setError("Your email is already in use. Please use another email");
        email.requestFocus();
    }
}