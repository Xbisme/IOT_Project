package xbisme.iot_project.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xbisme.iot_project.R;


public class TutorialScreen extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tutorial_screen, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppCompatButton started_btn = view.findViewById(R.id.get_started_btn);
        AppCompatButton skip_btn = view.findViewById(R.id.skip_button_tur);
        started_btn.setOnClickListener(this);
        skip_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Navigation.findNavController(view).navigate(R.id.action_tutorialScreen_to_loginScreen);
    }
}