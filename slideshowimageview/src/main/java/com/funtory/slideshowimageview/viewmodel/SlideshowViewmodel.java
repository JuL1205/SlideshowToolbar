package com.funtory.slideshowimageview.viewmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import com.funtory.slideshowimageview.SlideshowImageView;
import com.funtory.slideshowimageview.constants.Anim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by JuL on 16. 9. 27..
 */
public final class SlideshowViewModel {

    private List<Integer> images = new ArrayList<>();

    private Random random;

    private float[] animRangeX = null;
    private float[] animRangeY = null;

    private boolean isAnimate = false;
    private int currentChildIndex = -1;

    public SlideshowViewModel(){
        random = new Random(System.currentTimeMillis());
    }

    public void setImages(int... resIds){
        images.clear();
        addImages(resIds);
    }

    public void addImages(int... resIds) {
        for (int resId : resIds) {
            images.add(resId);
        }
    }

    public int getImageCount(){
        return images.size();
    }

    /**
     * 현재 child에 세팅되어있는 index 외에서 랜덤한 값을 얻는다.
     *
     * @return
     */
    public int getRandomImageIndex(List<Integer> curIndex) {
        List<Integer> candi = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {
            if (!curIndex.contains(i)) {
                candi.add(i);
            }
        }

        if (candi.size() == 0) {
            return -1;
        } else if (candi.size() == 1) {
            return candi.get(0);
        } else {
            return candi.get(random.nextInt(candi.size()));
        }
    }

    public Bitmap getImage(Context context, int imageIndex){
        return BitmapFactory.decodeResource(context.getResources(), images.get(imageIndex));
    }

    /**
     * 각 축으로 움직일 수 있는 최대/최소 범위 설정.
     */
    private void initRange(int viewW, int viewH) {
        float x = ((viewW * Anim.SCALE) - viewW) / 2.0f;
        float y = ((viewH * Anim.SCALE) - viewH) / 2.0f;
        animRangeX = new float[]{x, -x};
        animRangeY = new float[]{y, -y};
    }

    public void updateAnimConfig(SlideshowImageView src, int targetChildIndex){
        currentChildIndex = targetChildIndex;

        isAnimate = true;
        if (animRangeX == null || animRangeY == null) {
            initRange(src.getMeasuredWidth(), src.getMeasuredHeight());
        }
    }

    public void initAnimConfig(){
        currentChildIndex = 0;

        isAnimate = false;

        animRangeX = null;
        animRangeY = null;
    }

    /**
     * x 축 움직일 값을 랜덤하게 얻는다.
     *
     * @return
     */
    public float getRangeX() {
        return animRangeX[random.nextInt(animRangeX.length)];
    }

    /**
     * y 축 움직일 값을 랜덤하게 얻는다.
     *
     * @return
     */
    public float getRangeY() {
        return animRangeY[random.nextInt(animRangeY.length)];
    }

    public boolean isAnimate(){
        return isAnimate;
    }

    public int getCurrentChildIndex(){
        return currentChildIndex;
    }
}
