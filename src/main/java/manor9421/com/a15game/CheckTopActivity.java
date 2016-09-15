package manor9421.com.a15game;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckTopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_top);

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = dbHelper.checkTop(db);

        GridLayout gridLayout = (GridLayout) findViewById(R.id.checkTopGrid);


        if(cursor.moveToFirst()){
            int i = 0;
            do{
                i++;

                //номер
                GridLayout.LayoutParams lp0 = new GridLayout.LayoutParams();
                lp0.setGravity(Gravity.CENTER_HORIZONTAL);
                TextView tv0 = new TextView(this);
                tv0.setPadding(3,1,3,1);
                tv0.setText(i+"");
                tv0.setLayoutParams(lp0);
                gridLayout.addView(tv0);

                //результат выборки
                for(int j=0;j<6;j++){
                    GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                    //lp.setMargins(2,2,2,2);
                    lp.setGravity(Gravity.CENTER_HORIZONTAL);
                    TextView tv = new TextView(this);
                    tv.setPadding(5,1,5,1);
                    tv.setLayoutParams(lp);
                    if(j==2) {
                        if(cursor.getInt(j) == 1){
                            tv.setText("Yes");
                        }else{
                            tv.setText("No");
                        }


                    }else if(j==5){//для даты
                        String dateString = new SimpleDateFormat("dd.MM.yyyy").format(new Date(cursor.getLong(j)));
                        tv.setText(dateString);
                    }else{
                        tv.setText(cursor.getInt(j) + "");
                    }
                    gridLayout.addView(tv);
                }
            }while(cursor.moveToNext());

            cursor.close();
            db.close();
        }



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
            Intent intent = new Intent(this,NewGameActivity.class);
            startActivity(intent);
            return true;
        }else if(id == R.id.action_about){
            Intent aboutIntent = new Intent(this,DeveloperInfo.class);
            startActivity(aboutIntent);
            return true;
        }
        //default
        return super.onOptionsItemSelected(item);
    }

}