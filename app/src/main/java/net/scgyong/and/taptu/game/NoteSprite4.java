package net.scgyong.and.taptu.game;

import net.scgyong.and.taptu.R;

import kr.ac.kpu.game.framework.game.RecycleBin;
import kr.ac.kpu.game.framework.interfaces.Recyclable;
import kr.ac.kpu.game.framework.objects.Sprite;
import kr.ac.kpu.game.framework.res.BitmapPool;
import kr.ac.kpu.game.framework.res.Metrics;

public class NoteSprite4 extends Sprite implements Recyclable {
    public static final int CREATE_NOTE_BEFORE_MSEC = 2500;
    static  float NOTE_WIDTH = 1.0f / 24.0f;
    static  float NOTE_HEIGHT = NOTE_WIDTH / 3;
    public static final float NOTE_Y_HIT_MARGIN = 1.0f / 8.0f;
    float h=0;

    float XSize = NOTE_WIDTH;
    double YSize = NOTE_HEIGHT;

    float SizeRatio = 1.f;
    float EndTime = 3.f;

    Song.Note note;
    private float speed;

    public static NoteSprite4 get(Song.Note note, float time) {
        NoteSprite4 ns = (NoteSprite4) RecycleBin.get(NoteSprite4.class);
        if (ns == null) {
            ns = new NoteSprite4();
        }
        ns.init(note, time);
        return ns;
    }

    private void init(Song.Note note, float time) {
        SizeRatio = 1.f;



        this.note = note;
        float x = (0.5f - 2 * NOTE_WIDTH) * Metrics.width;
        x += NOTE_WIDTH * Metrics.width * note.lane;

        float seconds = note.msec / 1000.0f - time;
        float y = (1.0f - NOTE_Y_HIT_MARGIN) * Metrics.height;
        y -= seconds * Metrics.height * 1000 / CREATE_NOTE_BEFORE_MSEC;



        setCenter(x, y);

        if(SizeRatio==1.f) {
            XSize = Metrics.width * NOTE_WIDTH;
            YSize = Metrics.height * NOTE_HEIGHT;
        }

    }

    private void setCenter(float x, float y) {
        this.x = x;
        this.y = y;
        float  w = XSize*SizeRatio;
        double  h = YSize*SizeRatio;
        setDstRect(w,(float)h);
    }




    private NoteSprite4() {
        super(0, 0, Metrics.width * NOTE_WIDTH, Metrics.width * NOTE_HEIGHT, R.mipmap.n4);
        speed = Metrics.height * 1000 / CREATE_NOTE_BEFORE_MSEC;
        //Log.d("NoteSprite", "speed=" + speed);
    }

    @Override
    public void update(float frameTime) {
        super.update(frameTime);

        SizeRatio += (frameTime / EndTime)*6;

        setCenter((float) (x+frameTime*speed*0.475),y+speed*frameTime);


//        setCenter(x, y + speed * frameTime);
        MainScene scene = MainScene.get();
        if (dstRect.top > Metrics.height) {
            scene.remove(this);
            scene.call.set(Call.Type.miss);
        }
    }

    @Override
    public void finish() {

    }
}
