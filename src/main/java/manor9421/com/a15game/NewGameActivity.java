package manor9421.com.a15game;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

public class NewGameActivity extends AppCompatActivity {

    private CellButton[][] buttons;//массив кнопок
    private int ROW_COUNT = 4;
    private int COL_COUNT = 4;

    private Cell emptyCell = new Cell();//чтобы сохранять номера пустых ячеек

    private int seconds = 0;// cт.155-156
    private boolean running;
    private Handler handler = new Handler();

    boolean win = false;

    public int getSeconds() {
        return seconds;
    }

    public int getMoves() {
        return moves;
    }

    public boolean isUseNums() {
        return useNums;
    }

    private int moves = 0;
    private boolean useNums = false;

    CongratulationDialog sd;

    private TextView timeView;
    private TextView movesView;

    GridLayout newGameGridLayout;

    MediaPlayer btnSound;

    File buttonsFile;
    File movesSecondsFile;

    @Override
    protected void onResume() {
        super.onResume();

        ROW_COUNT = UserPreferences.getPrefRowCount(this);
        COL_COUNT = UserPreferences.getPrefColCount(this);

        newGameGridLayout = (GridLayout) findViewById(R.id.newGameGridLayout);
        newGameGridLayout.setRowCount(ROW_COUNT);
        newGameGridLayout.setColumnCount(COL_COUNT);

        buttons = new CellButton[ROW_COUNT][COL_COUNT];//двумерный массив для кнопок

        useNums = UserPreferences.getPrefUseNum(this);

        timeView = (TextView) findViewById(R.id.settingsTV);
        movesView = (TextView) findViewById(R.id.movesTV);

        btnSound = MediaPlayer.create(this, R.raw.btn_sound);
        //btnSound.prepareAsync();
        //btnSound.setAudioStreamType(AudioManager.STREAM_MUSIC);

        buttonsFile = new File(this.getCacheDir().getAbsoluteFile() + File.separator + "saved_buttons.txt");
        movesSecondsFile = new File(this.getCacheDir().getAbsoluteFile() + File.separator + "movesSeconds.txt");
        try {
            // создаем файл, если нет
            if (!buttonsFile.exists() || !movesSecondsFile.exists()) {
                buttonsFile.createNewFile();//
                movesSecondsFile.createNewFile();//
            }
            //Toast.makeText(this, " File length: " + buttonsFile.length(), Toast.LENGTH_SHORT).show();//выводим длину

            if (buttonsFile.length() == 0 /*|| movesSecondsFile.length() == 0*/) {
                //начинаем новую игру
                startNewGame();
                //Toast.makeText(this, " File clear: " + buttonsFile.length(), Toast.LENGTH_SHORT).show();
            } else {
                //если файл не пуст
                resumeGame(buttonsFile);
            }
        }catch (Exception e){
            Toast.makeText(this, "New Game Failed, Try again", Toast.LENGTH_SHORT).show();
            //очистить файл
            try {
                clearFile();
                //выйти
                finish();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void clearFile() throws FileNotFoundException {
        // очищаем файл
        PrintWriter writer1 = null;
        writer1 = new PrintWriter(buttonsFile);
        writer1.print("");
        writer1.close();

        PrintWriter writer2 = null;
        writer2 = new PrintWriter(movesSecondsFile);
        writer2.print("");
        writer2.close();
    }

    private void resumeGame(File buttonsFile) throws IOException, ClassNotFoundException {
        newGameGridLayout.removeAllViews();
        ObjectInputStream input = null;
        input = new ObjectInputStream(new FileInputStream(buttonsFile));
        // получаем массив с данными
        int[][][] data = (int[][][]) input.readObject();
        input.close();

        ObjectInputStream input2 = null;
        input2 = new ObjectInputStream(new FileInputStream(movesSecondsFile));
        // получаем массив с данными
        int[] movesSeconds = (int[]) input2.readObject();
        input2.close();

        ArrayList<Drawable> dd = new ArrayList<>();
        //получаем баттонсы
        //создаем кнопки с бекграундом, но не добавляем
        Bitmap croppedBitmap = takeBitmap();
        int margin = 1;
        if(croppedBitmap != null) { //если картинка подходит
            int buttonNum = 0;
            for (int i = 0; i < ROW_COUNT; i++) {
                for (int j = 0; j < COL_COUNT; j++) {
                    buttonNum++;
                    int widthHeight = getButtonSize() - margin*2;
                    Drawable d = cropForButton(croppedBitmap,i,j,widthHeight);
                    dd.add(d);
                }
            }// создали массив с картинками

            //Toast.makeText(this, " Length : " +buttons.length+"x"+buttons[0].length +"||"+data.length+"x"+data[0].length, Toast.LENGTH_SHORT).show();

            if( ROW_COUNT == data.length && COL_COUNT == data[0].length ){//проверяем соответствие длины массива настройкам
                // восстанавливаем кнопки вместе со слушателями
                //создаем баттонсы по данным из массива

                int widthHeight = getButtonSize();
                for (int i = 0; i < ROW_COUNT; i++) {
                    for (int j = 0; j < COL_COUNT; j++) {

                        GridLayout.LayoutParams a = new GridLayout.LayoutParams();
                        a.setMargins(margin,margin,margin,margin);

                        buttons[i][j] = new CellButton(this,i,j,data[i][j][0]);
                        buttons[i][j].setMinimumWidth(0);
                        buttons[i][j].setMinimumHeight(0);
                        buttons[i][j].setLayoutParams(a);
                        buttons[i][j].setWidth(widthHeight);
                        buttons[i][j].setWidth(widthHeight);

                        if(buttons[i][j].num == ROW_COUNT*COL_COUNT){
                            // empty cell
                            emptyCell.x=buttons[i][j].x;
                            emptyCell.y=buttons[i][j].y;
                            buttons[i][j].setBackgroundColor(ContextCompat.getColor(this,R.color.transparent));
                            buttons[i][j].setVisibility(View.INVISIBLE);

                        }else{
                            buttons[i][j].setBackground(dd.get(buttons[i][j].num-1));
                        }

                        if(useNums) {
                            buttons[i][j].setText(buttons[i][j].num+"");
                            buttons[i][j].setTextColor(Color.WHITE);
                            buttons[i][j].setShadowLayer(8, -1, -1, Color.BLACK);//radius,x,y,color
                        }

                        newGameGridLayout.addView(buttons[i][j]);//добавляем кнопку
                    }
                }// # end of for1

                //обнуляем buttons
                //buttons = null;
                // очищаем файл
                clearFile();

                setButtonsListeners();//////////////////

                handler.removeCallbacksAndMessages(null);//обнуляем хэндлер, чтобы таймеры после сброса не накладывались друг на друга

                moves = movesSeconds[0];
                seconds = movesSeconds[1];

                movesView.setText(moves + "");
                //startTime = System.currentTimeMillis();
                running = true;

                if(checkWin()){//если вдруг вышли во время поздравления о победе
                    running = false;
                    congratulationMessage();
                    clearFile();
                }

                runTimer();
            }else{
                //Toast.makeText(this, " Length fail " +ROW_COUNT+"x"+COL_COUNT+"||"+data.length+"x"+data[0].length, Toast.LENGTH_SHORT).show();
                startNewGame();
            }
        }else{
            // ошибка битмапа
            Toast.makeText(this, " Bitmap fail: Check you picture or add new ", Toast.LENGTH_LONG).show();
            //очистить файл
            clearFile();
            finish();
        }
    }

    private void runTimer(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds/3600;
                int minutes = (seconds%3600)/60;
                int secs = seconds%60;
                String time = String.format("%d:%02d:%02d", hours, minutes, secs);
                timeView.setText(time);
                if(running) {
                    seconds++;
                }
                handler.postDelayed(this, 1000);//выполнять каждую секунду
            }
        });
    }

    public void startNewGame(){
        newGameGridLayout.removeAllViews();
        handler.removeCallbacksAndMessages(null);

        Bitmap croppedBitmap = takeBitmap();

        if(croppedBitmap != null) {//если картинка подходит
            int buttonNum = 0;

            //для каждой кнопки получаем данные и добавляем
            for (int i = 0; i < ROW_COUNT; i++) {
                for (int j = 0; j < COL_COUNT; j++) {
                    buttonNum++;

                    GridLayout.LayoutParams a = new GridLayout.LayoutParams();
                    int margin = 1;
                    a.setMargins(margin,margin,margin,margin);
                    buttons[i][j] = new CellButton(this, i, j, buttonNum);
                    //get size
                    int widthHeight = getButtonSize() - margin*2;
                    buttons[i][j].setMinimumWidth(0);
                    buttons[i][j].setMinimumHeight(0);
                    buttons[i][j].setWidth(widthHeight);
                    buttons[i][j].setHeight(widthHeight);
                    buttons[i][j].setLayoutParams(a);

                    Drawable d = cropForButton(croppedBitmap,i,j,widthHeight);

                    if(buttonNum >= ROW_COUNT*COL_COUNT) {//для последней ячейки
                        emptyCell.x = i;
                        emptyCell.y = j;
                        buttons[i][j].setBackgroundColor(ContextCompat.getColor(this,R.color.transparent));
                        buttons[i][j].setVisibility(View.INVISIBLE);

                    }else {
                        buttons[i][j].setBackground(d);
                        //задать номер
                    }
                    if(useNums) {
                        buttons[i][j].setText(buttonNum + "");
                        buttons[i][j].setTextColor(Color.WHITE);
                        buttons[i][j].setShadowLayer(8, -1, -1, Color.BLACK);//radius,x,y,color
                    }
                    newGameGridLayout.addView(buttons[i][j]);//добавляем кнопку
                }
            }//# end of for1
            setButtonsListeners();

            shakeButtons();

            win = false;

            moves = 0;
            seconds = 0;
            movesView.setText("0");

            running = true;
            runTimer();
        }else{
            Toast.makeText(this, "Oops, image fail", Toast.LENGTH_SHORT).show();
        }
    }

    //добавляем листенеры всем buttons
    private void setButtonsListeners(){
        View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                CellButton thisButton = (CellButton) view;
                //thisButton.setVisibility(View.INVISIBLE);

                //является ли пустой
                if (canMove(thisButton)) {

                    if(btnSound.isPlaying()) {
                        btnSound.stop();

                        btnSound.release();
                        btnSound = MediaPlayer.create(getApplicationContext(), R.raw.btn_sound);
                    }
                    btnSound.start();// звук

                    moveButton(thisButton);

                    //увеличиваем количество ходов
                    moves++;
                    movesView.setText(moves+"");

                    // проверяем победу
                    if (checkWin()) {
                        running = false;//останавливаем таймер
                        //либо наверное можно обнулить хэндлер
                        DBHelper dbHelper = new DBHelper(getApplicationContext());
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        dbHelper.saveGameResults(db,moves,seconds,useNums,ROW_COUNT,COL_COUNT);
                        congratulationMessage();
                        try {
                            clearFile();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        /*cheat*/
        View.OnLongClickListener longListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                CellButton thisButton = (CellButton) view;
                //является ли пустой

                btnSound.start();// звук

                moveButton(thisButton);

                //увеличиваем количество ходов
                moves++;
                movesView.setText(moves+"");

                // проверяем победу
                if (checkWin()) {
                    running = false;//останавливаем таймер
                    //либо наверное можно обнулить хэндлер
                    DBHelper dbHelper = new DBHelper(getApplicationContext());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    dbHelper.saveGameResults(db,moves,seconds,useNums,ROW_COUNT,COL_COUNT);
                    congratulationMessage();
                    try {
                        clearFile();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(getApplicationContext(),"Cheat :)",Toast.LENGTH_SHORT).show();
                return false;
            }
        };
        /*end of cheat*/

        //задаем листенер каждой кнопке
        for(int i = 0;i < ROW_COUNT; i++) {
            for (int j = 0; j < COL_COUNT; j++) {
                buttons[i][j].setOnClickListener(listener);
                buttons[i][j].setOnLongClickListener(longListener);
            }
        }
    }

    private void congratulationMessage() {
        sd = new CongratulationDialog();
        sd.setCancelable(false);
        sd.act = this;
        try {
            FragmentManager manager = getSupportFragmentManager();
            sd.show(manager, "Congrats");
        } catch (Exception e) {
            Toast.makeText(this, "Congratulation message error" + e, Toast.LENGTH_SHORT).show();
        }
    }

    private void shakeButtons(){
        Random rn = new Random();
        int howMany = ROW_COUNT*COL_COUNT+20;
        for(int i=0; i<howMany; i++){
            // берем рандомную кнопку
            int x = rn.nextInt(ROW_COUNT);// x новой кнопки
            int y = rn.nextInt(COL_COUNT);// y новой кнопки
            // двигаем ее, если не пустая
            if(emptyCell.x == x && emptyCell.y == y){
            }else {
                moveButton(buttons[x][y]);
            }
        }
    }

    private void moveButton(CellButton thisButton){
        //получаем данные этой ячейки
        int bufNum = thisButton.num;// значение в этой ячейке
        Drawable d = thisButton.getBackground();//задник
        CharSequence bufText = thisButton.getText();
        //меняем эту ячейку
        thisButton.num = ROW_COUNT*COL_COUNT;
        //thisButton.setVisibility(View.INVISIBLE);
        thisButton.setText("");
        thisButton.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent));

        //заполняем пустую ячейку
        buttons[emptyCell.x][emptyCell.y].num = bufNum;
        buttons[emptyCell.x][emptyCell.y].setText(bufText);
        buttons[emptyCell.x][emptyCell.y].setBackground(d);

        //делаем ее видимой
        buttons[emptyCell.x][emptyCell.y].setVisibility(View.VISIBLE);
        //меняем данные пустой ячейки
        emptyCell.x = thisButton.x;
        emptyCell.y = thisButton.y;
    }

    private boolean checkWin(){
        //проверяем, на последнем ли месте пустая клетка
        if(buttons[ROW_COUNT-1][COL_COUNT-1].num == ROW_COUNT*COL_COUNT){
            int count = 0;
            int matches = 0;
            for(int i=0;i<ROW_COUNT;i++) {
                for (int j = 0; j < COL_COUNT; j++) {
                    count++;
                    if(buttons[i][j].num == count){
                        matches++;
                    }else if(i==ROW_COUNT-1 && j == COL_COUNT-1){//если последняя ячейка
                        matches++;
                    }else{
                        return false;
                    }
                }
            }
            if(matches == ROW_COUNT*COL_COUNT){
                //WIN
                win = true;
                return true;
            }
        }
        return false;
    }

    private boolean canMove(CellButton clickedButton){
        if(clickedButton.x == emptyCell.x && clickedButton.y == emptyCell.y){
            return false;
        }
        if(clickedButton.x == emptyCell.x){
            int diff = Math.abs(emptyCell.y - clickedButton.y);
            if(diff == 1)
                return true;
        }else if(clickedButton.y == emptyCell.y){
            int diff = Math.abs(emptyCell.x - clickedButton.x);
            if(diff == 1)
                return true;
        }
        return false;
    }

    //возвращает бекграунд для конкретной кнопки
    private Drawable cropForButton(Bitmap bmOriginal,int row,int col,int widthHeight){
        //обрезаем изображение
        Bitmap bmUpRightPartial = Bitmap.createBitmap(bmOriginal,col*widthHeight,row*widthHeight,widthHeight, widthHeight);
        Drawable croppedImage = new BitmapDrawable(getResources(),bmUpRightPartial);

        return croppedImage;
    }

    //возвращает ширину/высоту кнопки
    private int getButtonSize(){
        Display display = this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;//ширина экрана
        int height = size.y-150;//высота экрана

        //узнаем ширину куба
        int buttonWidth = (int)Math.floor( width/COL_COUNT );//округляем в неньшую сторону ширину квадрата
        int buttonHeight = (int)Math.floor( height/ROW_COUNT );//округляем в неньшую сторону высоту квадрата
        if(buttonWidth > buttonHeight){
            return buttonHeight;
        }else{
            return buttonWidth;
        }
    }


    private Bitmap takeBitmap(){//возвращаем картинку, обрезанную по экрану
        //получаем исходный битмап
        Bitmap userImage;
        if(UserPreferences.getPrefSelImage(this).equals("")){//если в настройках нет картинки
            userImage = BitmapFactory.decodeResource(getResources(),
                    R.drawable.def_img);
        }else{
            String imagePath = getApplicationInfo().dataDir + "/images/images-big" + "/" + UserPreferences.getPrefSelImage(this);

            userImage = BitmapFactory.decodeFile(imagePath);
        }

        int bitWidth = userImage.getWidth();
        int bitHeight = userImage.getHeight();

        // получаем размер экрана
        Display display = this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int displayWidth = size.x;//ширина экрана
        int displayHeight = size.y;//высота экрана

        if(displayWidth == bitWidth || displayHeight == bitHeight){
            // все ОК, берем картинку
            return userImage;
        }else{
            //ресайзим картинку
            Bitmap newImage;
            try {
                if (displayWidth / displayHeight > bitWidth / bitHeight) {
                    // по ширине
                    newImage = Bitmap.createScaledBitmap(userImage,
                            displayWidth,
                            (int) Math.ceil(bitHeight / (bitWidth / displayWidth)),
                            true);
                } else {
                    // по высоте
                    newImage = Bitmap.createScaledBitmap(userImage,
                            (int) Math.ceil(bitWidth / (bitHeight / displayHeight)),
                            displayHeight,
                            true);
                }
            }catch (Exception e){
                newImage = Bitmap.createScaledBitmap(userImage,
                        displayWidth,
                        displayHeight,
                        true);
            }
            //обрезаем центр
            Bitmap bigBitmap = Bitmap.createBitmap(
                    newImage,
                    newImage.getWidth()/2 - displayWidth/2,
                    newImage.getHeight()/2 - displayHeight/2,
                    displayWidth, //displayHeight
                    displayHeight
            );
            return bigBitmap;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(sd != null && sd.getDialog() != null && sd.getDialog().isShowing()) {
            sd.dismiss();
        }
        if(!win) { // сохраняем результат, если нет выиграша
            win = false;
            running = false;
            try {

                //создаем массив
                int[][][] btnData = new int[ROW_COUNT][COL_COUNT][1];//создаем массив для записи данных [строка][колонка][номер]

                for (int i = 0; i < ROW_COUNT; i++) {
                    for (int j = 0; j < COL_COUNT; j++) {
                        btnData[i][j][0] = buttons[i][j].num;
                    }
                }

                FileOutputStream fos1 = null;
                ObjectOutputStream oos1 = null;
                //открываем поток
                fos1 = new FileOutputStream(buttonsFile);
                oos1 = new ObjectOutputStream(fos1);

                oos1.writeObject(btnData);// пишем файл
                oos1.close();
                fos1.close();

                //сохраняем время и количество ходов
                int[] movesSeconds = new int[2];
                movesSeconds[0] = moves;
                movesSeconds[1] = seconds;

                FileOutputStream fos2 = null;
                ObjectOutputStream oos2 = null;
                //открываем поток
                fos2 = new FileOutputStream(movesSecondsFile);
                oos2 = new ObjectOutputStream(fos2);

                oos2.writeObject(movesSeconds);// пишем файл
                oos2.close();
                fos2.close();

            } catch (Exception e) {
                Toast.makeText(this, "Saving game fail!! : ", Toast.LENGTH_SHORT).show();
            }
        }else{
            win = false;
            running = false;
            try{
                clearFile();
            }catch (Exception e){

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
    }

    public void refresh(View view) {
        startNewGame();
    }
}
