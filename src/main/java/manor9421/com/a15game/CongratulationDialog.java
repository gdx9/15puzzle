package manor9421.com.a15game;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by manor on 8/20/16.
 */
public class CongratulationDialog extends DialogFragment {

    View newV;
    NewGameActivity act;

    //задать цвет фона
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#60000000")));
        getDialog().getWindow().setBackgroundDrawableResource(R.color.black_60);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.congratsText));
            ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getActivity(), R.color.congratsText));
            ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(getActivity(), R.color.congratsText));
        }catch (Exception e){
            Toast.makeText(getActivity(),""+e, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        newV = getActivity().getLayoutInflater().inflate(R.layout.congratulation_message_dialog,null);
        ((TextView) newV.findViewById(R.id.userMoves)).setText(act.getMoves()+"");
        ((TextView) newV.findViewById(R.id.userTime)).setText(act.getSeconds()+" seconds");
        if(act.isUseNums()){
            ((TextView) newV.findViewById(R.id.userUseNums)).setText("Yes");
        }else{
            ((TextView) newV.findViewById(R.id.userUseNums)).setText("No");
        }

        return new AlertDialog.Builder(getActivity()) //Builder - pattern
            .setView(newV)
            .setTitle("Congratulation!!!")
            .setPositiveButton("Start New", new DialogInterface.OnClickListener() {//анонимный внутренний класс
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //начать новую игру
                    act.startNewGame();
                }
            })
            .setNeutralButton("Info", new DialogInterface.OnClickListener() {//анонимный внутренний класс
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(getActivity(),DeveloperInfo.class);
                    startActivity(intent);

                }
            })
            .setNegativeButton("Exit", new DialogInterface.OnClickListener() {//анонимный внутренний класс
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //выйти
                    act.finish();//закрываем активность
                }
            })
        .create();
    }

}
