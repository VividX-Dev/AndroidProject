package net.scgyong.and.taptu.game;

import android.content.res.AssetManager;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.util.JsonReader;
import android.util.Log;
import android.view.MotionEvent;

import net.scgyong.and.taptu.R;


import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import kr.ac.kpu.game.framework.game.Scene;
import kr.ac.kpu.game.framework.interfaces.GameObject;
import kr.ac.kpu.game.framework.objects.Sprite;
import kr.ac.kpu.game.framework.res.Metrics;
import kr.ac.kpu.game.framework.view.GameView;

public class MainScene extends Scene implements Pret.Listener {
    public static final String PARAM_SONG_JSON = "song_filename";
    private static final String TAG = MainScene.class.getSimpleName();
    private static MainScene singleton;
    private Song song;
    private Pret[] prets = new Pret[4];

    int Count=0;
    Rank.Type ty = Rank.Type.fail;



    Call call;
    Rank rank;
    private NoteGen noteGenerator;
    MediaPlayer mediaPlayer;
    private int combo=0;


    public static MainScene get() {
        if (singleton == null) {
            singleton = new MainScene();
        }
        return singleton;
    }

    public float getCurrentTime() {
        return mediaPlayer.getCurrentPosition() / 1000.0f;
    }

    public boolean loadSong(String json) {
        JsonReader reader = new JsonReader(new StringReader(json));
        AssetManager assets = GameView.view.getContext().getAssets();
        try {
            reader.beginObject();
            song = new Song(reader, assets);
            reader.endObject();
            reader.close();

            song.loadNote(assets);
            mediaPlayer = song.loadMusic(assets);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return song.isValid();
    }

    public enum Layer {
        bg, pret, note, call, rank, controller, COUNT;
    }

    public void init() {
        super.init();

        initLayers(Layer.COUNT.ordinal());

        add(Layer.bg.ordinal(), new Sprite(
                Metrics.width / 2, Metrics.height / 2,
                Metrics.width, Metrics.height,
                R.mipmap.bg
        ));
        noteGenerator = new NoteGen(song, mediaPlayer);
        add(Layer.controller.ordinal(), noteGenerator);

        for (int lane = 0; lane < 4; lane++) {
            Pret pret = new Pret(lane, this);
            prets[lane] = pret;
            add(Layer.pret.ordinal(), pret);
        }

        call = new Call();
        add(Layer.call.ordinal(), call);

        rank = new Rank();
        add(Layer.rank.ordinal(), rank);

        mediaPlayer.start();
    }

    @Override
    public void end() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();


            mediaPlayer = null;
        }
        super.end();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        for (int lane = 0; lane < 4; lane++) {
            boolean processed = prets[lane].onTouchEvent(event);
            if (processed) {
                return true;
            }
        }
        return false;
    }

    RectF tmpRect = new RectF();




    @Override
    public void onPret(int lane, boolean pressed) {
        Log.d(TAG, "onPret: lane=" + lane + " pressed=" + pressed);
        if (!pressed) return;
        float time = getCurrentTime();
        if(time>100)
            showRank();

        if(lane==0) {
            NoteSprite ns = findNearestNote(0, time);

            if (ns == null) return;
            float diff = ns.note.msec / 1000.0f - time;
            if (diff < 0) diff = -diff;
            Call.Type type = Call.Type.miss;

            if (diff < 0.1) {
                type = Call.Type.perfect;
                Count=Count+5;
            } else if (diff < 0.2) {
                type = Call.Type.great;
                Count=Count+4;
            } else if (diff < 0.3) {
                type = Call.Type.good;
                Count=Count+3;
            } else if (diff < 0.5) {
                type = Call.Type.bad;
                Count=Count+1;
            }else{combo=0;}
            call.set(type);
            remove(ns);
        }else  if(lane==1) {
            NoteSprite2 ns = findNearestNote2(1, time);

            if (ns == null) return;
            float diff = ns.note.msec / 1000.0f - time;
            if (diff < 0) diff = -diff;
            Call.Type type = Call.Type.miss;
            if (diff < 0.1) {
                type = Call.Type.perfect;
                Count=Count+5;
            } else if (diff < 0.2) {
                type = Call.Type.great;
                Count=Count+4;
            } else if (diff < 0.3) {
                type = Call.Type.good;
                Count=Count+3;
            } else if (diff < 0.5) {
                type = Call.Type.bad;
                Count=Count+1;
            }
            call.set(type);
            remove(ns);
        }else  if(lane==2) {
            NoteSprite3 ns = findNearestNote3(2, time);

            if (ns == null) return;
            float diff = ns.note.msec / 1000.0f - time;
            if (diff < 0) diff = -diff;
            Call.Type type = Call.Type.miss;
            if (diff < 0.1) {
                type = Call.Type.perfect;
                Count=Count+5;
            } else if (diff < 0.2) {
                type = Call.Type.great;
                Count=Count+4;
            } else if (diff < 0.3) {
                type = Call.Type.good;
                Count=Count+3;
            } else if (diff < 0.5) {
                type = Call.Type.bad;
                Count=Count+1;
            }
            call.set(type);
            remove(ns);
        }else  if(lane==3) {
            NoteSprite4 ns = findNearestNote4(3, time);

            if (ns == null) return;
            float diff = ns.note.msec / 1000.0f - time;
            if (diff < 0) diff = -diff;
            Call.Type type = Call.Type.miss;
            if (diff < 0.1) {
                type = Call.Type.perfect;
                Count=Count+5;
            } else if (diff < 0.2) {
                type = Call.Type.great;
                Count=Count+4;
            } else if (diff < 0.3) {
                type = Call.Type.good;
                Count=Count+3;
            } else if (diff < 0.5) {
                type = Call.Type.bad;
                Count=Count+1;
            }
            call.set(type);
            remove(ns);
        }


    }



    private NoteSprite findNearestNote(int lane, float time) {
        float dist = Float.MAX_VALUE;
        NoteSprite nearest = null;
        ArrayList<GameObject> notes = objectsAt(Layer.note.ordinal());
        for (GameObject go : notes) {
            if (!(go instanceof NoteSprite)) continue;
            NoteSprite ns = (NoteSprite) go;
            if (ns.note.lane != lane) continue;
            float diff = ns.note.msec / 1000.0f - time;
            if (diff < 0) diff = -diff;
            if (dist > diff) {
//                Log.d(TAG, "= dist=" + dist + " diff=" + diff);
                dist = diff;
                nearest = ns;
            }
        }
//        if (nearest != null) {
//            Log.d(TAG, "dist=" + dist + " nearest=" + nearest.note.msec);
//        }
        return (dist < 1.0f) ? nearest : null;
    }

    private NoteSprite2 findNearestNote2(int lane, float time) {
        float dist = Float.MAX_VALUE;
        NoteSprite2 nearest = null;
        ArrayList<GameObject> notes = objectsAt(Layer.note.ordinal());
        for (GameObject go : notes) {
            if (!(go instanceof NoteSprite2)) continue;
            NoteSprite2 ns = (NoteSprite2) go;
            if (ns.note.lane != lane) continue;
            float diff = ns.note.msec / 1000.0f - time;
            if (diff < 0) diff = -diff;
            if (dist > diff) {
//                Log.d(TAG, "= dist=" + dist + " diff=" + diff);
                dist = diff;
                nearest = ns;
            }
        }
//        if (nearest != null) {
//            Log.d(TAG, "dist=" + dist + " nearest=" + nearest.note.msec);
//        }
        return (dist < 1.0f) ? nearest : null;
    }

    private NoteSprite3 findNearestNote3(int lane, float time) {
        float dist = Float.MAX_VALUE;
        NoteSprite3 nearest = null;
        ArrayList<GameObject> notes = objectsAt(Layer.note.ordinal());
        for (GameObject go : notes) {
            if (!(go instanceof NoteSprite3)) continue;
            NoteSprite3 ns = (NoteSprite3) go;
            if (ns.note.lane != lane) continue;
            float diff = ns.note.msec / 1000.0f - time;
            if (diff < 0) diff = -diff;
            if (dist > diff) {
//                Log.d(TAG, "= dist=" + dist + " diff=" + diff);
                dist = diff;
                nearest = ns;
            }
        }
//        if (nearest != null) {
//            Log.d(TAG, "dist=" + dist + " nearest=" + nearest.note.msec);
//        }
        return (dist < 1.0f) ? nearest : null;
    }

    private NoteSprite4 findNearestNote4(int lane, float time) {
        float dist = Float.MAX_VALUE;
        NoteSprite4 nearest = null;
        ArrayList<GameObject> notes = objectsAt(Layer.note.ordinal());
        for (GameObject go : notes) {
            if (!(go instanceof NoteSprite4)) continue;
            NoteSprite4 ns = (NoteSprite4) go;
            if (ns.note.lane != lane) continue;
            float diff = ns.note.msec / 1000.0f - time;
            if (diff < 0) diff = -diff;
            if (dist > diff) {
//                Log.d(TAG, "= dist=" + dist + " diff=" + diff);
                dist = diff;
                nearest = ns;
            }
        }
//        if (nearest != null) {
//            Log.d(TAG, "dist=" + dist + " nearest=" + nearest.note.msec);
//        }
        return (dist < 1.0f) ? nearest : null;
    }

    public void showRank(){
        if(Count>=400){
            ty = Rank.Type.a;
        }else if(Count>=300){
            ty = Rank.Type.b;
        }else if(Count>=200){
            ty = Rank.Type.c;
        }else if(Count>=100){
            ty = Rank.Type.d;
        }
        rank.set(ty);


    }
}
