package manor9421.com.a15game;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class FirstMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_menu);
    }

    //добавляем элементы в меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Заполнение меню; элементы (если они есть) добавляются на панель действий
        getMenuInflater().inflate(R.menu.menu_first_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_game) {
            startNewGame(new View(this));
            return true;
        }else if(id == R.id.action_about){
            Intent aboutIntent = new Intent(this,DeveloperInfo.class);
            startActivity(aboutIntent);
            return true;
        }
        //default
        return super.onOptionsItemSelected(item);
    }

    public void checkTop(View view) {
        Intent intent = new Intent(this,CheckTopActivity.class);
        startActivity(intent);
    }

    public void startNewGame(View view) {
        Intent intent = new Intent(this,NewGameActivity.class);
        startActivity(intent);
    }

    public void setSettings(View view) {
        try {
            SettingsDialog alertDialog = new SettingsDialog(this);
            alertDialog.show();
        }catch (Exception e){
            Toast.makeText(this,e+"",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
    }
}
