package manor9421.com.a15game;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DeveloperInfo extends AppCompatActivity {

    TextView playStoreLink;
    TextView devEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_info);

        playStoreLink = (TextView) findViewById(R.id.playStoreLink);
        devEmail = (TextView) findViewById(R.id.devEmail);

        //ссылка на ПлейСтор
        playStoreLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=manor9421.com.a15game"));
                startActivity(intent);
            }
        });

        //ссылка на имейл
        devEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        //Uri.parse("mailto:roman3920@yandex.ua")
                emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{"manor9421@yahoo.co.jp"});
                emailIntent.setType("text/plain");
                startActivity(emailIntent);
            }
        });

    }
}
