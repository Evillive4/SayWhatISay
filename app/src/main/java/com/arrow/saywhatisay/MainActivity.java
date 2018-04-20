package com.arrow.saywhatisay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.arrow.saywhatisay.control.InitConfig;
import com.arrow.saywhatisay.control.MyRecognizer;
import com.arrow.saywhatisay.control.MySyntherizer;
import com.arrow.saywhatisay.control.MyWakeup;
import com.arrow.saywhatisay.recognization.MessageStatusRecogListener;
import com.arrow.saywhatisay.recognization.NonBlockSyntherizer;
import com.arrow.saywhatisay.recognization.StatusRecogListener;
import com.arrow.saywhatisay.recognization.UiMessageListener;
import com.arrow.saywhatisay.util.AutoCheck;
import com.arrow.saywhatisay.util.Global;
import com.arrow.saywhatisay.util.GlobalMethods;
import com.arrow.saywhatisay.util.Logger;
import com.arrow.saywhatisay.util.OfflineResource;
import com.arrow.saywhatisay.wakeup.IWakeupListener;
import com.arrow.saywhatisay.wakeup.RecogWakeupListener;
import com.arrow.saywhatisay.wakeup.SimpleWakeupListener;
import com.baidu.speech.asr.SpeechConstant;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    protected MyWakeup myWakeup;
    protected MyRecognizer myRecognizer;
    protected Handler handler;
    private TextView tv_text;

    private Button btn_on;

    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    protected TtsMode ttsMode = TtsMode.MIX;
    // 离线发音选择，VOICE_FEMALE即为离线女声发音。
    // assets目录下bd_etts_speech_female.data为离线男声模型；bd_etts_speech_female.data为离线女声模型
    protected String offlineVoice = OfflineResource.VOICE_FEMALE;
    // 主控制类，所有合成控制方法从这个类开始
    protected MySyntherizer synthesizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_text = (TextView) findViewById(R.id.tv_text);


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.this.getClass().getSimpleName());
        registerReceiver(receiver, intentFilter);

        btn_on = (Button) findViewById(R.id.btn_on);
        btn_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                int result = synthesizer.speak("你好");
//                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.alibaba.android.rimet");
//                startActivity(LaunchIntent);

                GlobalMethods.lock(MainActivity.this);
            }
        });

        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                handleMsg(msg);
                switch (msg.what) {
                    case 101:
                        //唤醒开始
                        Logger.info("handleMessage","handleMessage " + msg.what + " " + msg.toString());
                        GlobalMethods.Unlock(MainActivity.this,true);
                        break;
                    case 102:
                        //唤醒结束

                        break;
                    case 103:
                        //唤醒失败

                        break;

                }
            }

        };

        IWakeupListener listener = new SimpleWakeupListener();
        myWakeup = new MyWakeup(this, listener);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        myWakeup.start(params);

//        StatusRecogListener recogListener = new MessageStatusRecogListener(handler);
//        myRecognizer = new MyRecognizer(this, recogListener);
//
//        IWakeupListener iWakeupListener = new RecogWakeupListener(handler);
//        myWakeup = new MyWakeup(this, iWakeupListener);

        initialTts();

        Logger.setHandler(handler);

    }

    protected void handleMsg(Message msg) {
        if (tv_text != null && msg.obj != null) {
            tv_text.append(msg.obj.toString() + "\n");
        }

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            String word = intent.getStringExtra("word");
            switch (type) {
                case "101":
                    Logger.info("receiver","receiver " + type + " " + word);
                    if(word.equals("你好未来")) {
                        int result = synthesizer.speak("请说");
                        GlobalMethods.Unlock(MainActivity.this,true);
                    } else if(word.equals("你好小未")){
                        GlobalMethods.Unlock(MainActivity.this,false);
                    }
                    break;
                case "102":
                    //唤醒结束

                    break;
                case "103":
                    //唤醒失败

                    break;

            }
        }
    };

    /**
     * 初始化引擎，需要的参数均在InitConfig类里
     * <p>
     * DEMO中提供了3个SpeechSynthesizerListener的实现
     * MessageListener 仅仅用log.i记录日志，在logcat中可以看见
     * UiMessageListener 在MessageListener的基础上，对handler发送消息，实现UI的文字更新
     * FileSaveListener 在UiMessageListener的基础上，使用 onSynthesizeDataArrived回调，获取音频流
     */
    protected void initialTts() {
        // 设置初始化参数
        // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
        SpeechSynthesizerListener listener = new UiMessageListener(handler);

        Map<String, String> params = getParams();

        // appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
        InitConfig initConfig = new InitConfig(Global.appId, Global.appKey, Global.secretKey, ttsMode, params, listener);

        // 如果您集成中出错，请将下面一段代码放在和demo中相同的位置，并复制InitConfig 和 AutoCheck到您的项目中
        // 上线时请删除AutoCheck的调用
        AutoCheck.getInstance(getApplicationContext()).check(initConfig, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 100) {
                    AutoCheck autoCheck = (AutoCheck) msg.obj;
                    synchronized (autoCheck) {
                        String message = autoCheck.obtainDebugMessage();
//                        toPrint(message); // 可以用下面一行替代，在logcat中查看代码
                        // Log.w("AutoCheckMessage", message);
                    }
                }
            }
        });
        synthesizer = new NonBlockSyntherizer(this, initConfig, handler); // 此处可以改为MySyntherizer 了解调用过程
    }

    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        // 以下参数均为选填
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        params.put(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置合成的音量，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_VOLUME, "5");
        // 设置合成的语速，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_PITCH, "5");

        params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

        // 离线资源文件
        OfflineResource offlineResource = createOfflineResource(offlineVoice);
        // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
        params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
        params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE,
                offlineResource.getModelFilename());
        return params;
    }

    protected OfflineResource createOfflineResource(String voiceType) {
        OfflineResource offlineResource = null;
        try {
            offlineResource = new OfflineResource(this, voiceType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return offlineResource;
    }
}
