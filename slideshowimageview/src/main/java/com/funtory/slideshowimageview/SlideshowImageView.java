package com.funtory.slideshowimageview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * Copyright (C) 2016 JuL <jul.funtory@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class SlideshowImageView extends RelativeLayout {

    private static final float SCALE = 1.5f;
    private static final long ANIM_DURATION = 15 * 1000;

    private List<Integer> images = new ArrayList<>();

    private float[] animRangeX = null;
    private float[] animRangeY = null;

    private Random random;

    private boolean isAnimate = false;
    private int currentChildIndex = -1;

    public SlideshowImageView(Context context) {
        super(context);
        init();
    }

    public SlideshowImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideshowImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        //2개의 이미지뷰로 번갈아 가며..
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        random = new Random(System.currentTimeMillis());
    }

    private ImageView getImageView(int index) {
        return (ImageView) getChildAt(index);
    }

    /**
     * 새로운 이미지들로 교체.
     *
     * @param resIds
     */
    public void setImages(int... resIds) {
        images.clear();
        for (int i = 0; i < getChildCount(); i++) {
            getImageView(i).setTag(null);
        }

        addImages(resIds);
    }

    /**
     * 새로운 이미지 추가.
     *
     * @param resIds
     */
    public void addImages(int... resIds) {
        for (int resId : resIds) {
            images.add(resId);
        }

        if (images.size() > 1) { //2개 이상일 경우부터 애니메이션
            if (!isAnimate) {     //애니메이션되지 않고 있을 경우
                if (currentChildIndex > -1) {
                    gone(currentChildIndex);
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            anim(Math.abs(currentChildIndex - 1));
                        }
                    }, ANIM_DURATION / 4);
                } else {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            anim(0);
                        }
                    });
                }
            } else {
                //이미 애니메이션 되고 있을 경우에는 알아서 동작 할 것이다.
            }
        } else { //1개일 경우는 그냥 세팅만 하자
            getImageView(0).setImageResource(resIds[0]);
            getImageView(0).setTag(0);
            currentChildIndex = 0;
        }
    }

    private void gone(int targetChildIndex) {
        ImageView target = getImageView(targetChildIndex);

        ObjectAnimator goneAlpha = ObjectAnimator.ofFloat(target, "alpha", 1.0f, 0.0f);
        goneAlpha.setDuration(ANIM_DURATION);

        goneAlpha.start();
    }


    /**
     * 각 축으로 움직일 수 있는 최대/최소 범위 설정.
     */
    private void initRange() {
        float x = ((getMeasuredWidth() * SCALE) - getMeasuredWidth()) / 2.0f;
        float y = ((getMeasuredHeight() * SCALE) - getMeasuredHeight()) / 2.0f;
        animRangeX = new float[]{x, -x};
        animRangeY = new float[]{y, -y};
    }

    /**
     * x 축 움직일 값을 랜덤하게 얻는다.
     *
     * @return
     */
    private float getRangeX() {
        return animRangeX[random.nextInt(animRangeX.length)];
    }

    /**
     * y 축 움직일 값을 랜덤하게 얻는다.
     *
     * @return
     */
    private float getRangeY() {
        return animRangeY[random.nextInt(animRangeY.length)];
    }

    /**
     * 현재 child에 세팅되어있는 index 외에서 랜덤한 값을 얻는다.
     *
     * @return
     */
    private int getRandomImageIndex() {
        List<Integer> candi = new ArrayList<>();

        int i1 = getImageView(0).getTag() == null ? -1 : (int) getImageView(0).getTag();
        int i2 = getImageView(1).getTag() == null ? -1 : (int) getImageView(1).getTag();

        for (int i = 0; i < images.size(); i++) {
            if (i != i1 && i != i2) {
                if (!candi.contains(i)) {
                    candi.add(i);
                }
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

    private void anim(final int targetChildIndex) {
        isAnimate = true;
        currentChildIndex = targetChildIndex;

        if (animRangeX == null || animRangeY == null) {
            initRange();
        }

        ImageView target = getImageView(targetChildIndex);
        target.setScaleX(SCALE);
        target.setScaleY(SCALE);

        int imageIndex = getRandomImageIndex();

        if (imageIndex > -1) {
            target.setTag(imageIndex);
            target.setImageResource(images.get(imageIndex));
        }

        float rangeX = getRangeX();
        ObjectAnimator transX = ObjectAnimator.ofFloat(target, "translationX", rangeX, -rangeX);
        transX.setDuration(ANIM_DURATION);

        float rangeY = getRangeY();
        ObjectAnimator transY = ObjectAnimator.ofFloat(target, "translationY", rangeY, -rangeY);
        transY.setDuration(ANIM_DURATION);

        ObjectAnimator alpha = ObjectAnimator.ofFloat(target, "alpha", 0.0f, 1.0f);
        alpha.setDuration(ANIM_DURATION / 2);

        ObjectAnimator alphaAfter = ObjectAnimator.ofFloat(target, "alpha", 1.0f, 0.0f);
        alphaAfter.setDuration(ANIM_DURATION / 2);
        alphaAfter.setStartDelay(ANIM_DURATION / 2);

        AnimatorSet animSet = new AnimatorSet();
        animSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animSet.playTogether(transX, transY, alpha, alphaAfter);
        animSet.start();

        //무한 반복
        postDelayed(new Runnable() {
            @Override
            public void run() {
                anim(targetChildIndex + 1 >= getChildCount() ? 0 : targetChildIndex + 1);
            }
        }, ANIM_DURATION / 2);

    }


}
