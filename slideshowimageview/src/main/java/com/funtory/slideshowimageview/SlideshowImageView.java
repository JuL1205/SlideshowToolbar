package com.funtory.slideshowimageview;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.funtory.slideshowimageview.constants.Anim;
import com.funtory.slideshowimageview.viewmodel.SlideshowViewModel;

import java.util.ArrayList;
import java.util.List;

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

    private SlideshowViewModel slideshowViewModel = new SlideshowViewModel();

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
        for (int i = 0; i < getChildCount(); i++) {
            getImageView(i).setTag(null);
        }

        slideshowViewModel.setImages(resIds);
        tryAnim(resIds);
    }

    /**
     * 새로운 이미지 추가.
     *
     * @param resIds
     */
    public void addImages(int... resIds) {
        slideshowViewModel.addImages(resIds);
        tryAnim(resIds);
    }

    private void tryAnim(int... resIds){
        if (slideshowViewModel.getImageCount() > 1) { //2개 이상일 경우부터 애니메이션
            if (!slideshowViewModel.isAnimate()) {     //애니메이션되지 않고 있을 경우
                final int currentChildIndex = slideshowViewModel.getCurrentChildIndex();
                if (currentChildIndex > -1) {
                    gone(currentChildIndex);
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            anim(Math.abs(currentChildIndex - 1));
                        }
                    }, Anim.DURATION / 4);
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

            slideshowViewModel.initAnimConfig();
        }
    }

    private void gone(int targetChildIndex) {
        ImageView target = getImageView(targetChildIndex);

        ObjectAnimator goneAlpha = ObjectAnimator.ofFloat(target, "alpha", 1.0f, 0.0f);
        goneAlpha.setDuration(Anim.DURATION);

        goneAlpha.start();
    }



    private List<Integer> getCurrentIndex(){
        List<Integer> result = new ArrayList<>();
        for(int i = 0 ; i < getChildCount() ; i++){
            result.add(getChildAt(i).getTag() == null ? -1 : (Integer) getChildAt(i).getTag());
        }

        return result;
    }

    private void anim(final int targetChildIndex) {
        slideshowViewModel.updateAnimConfig(this, targetChildIndex);

        ImageView target = getImageView(targetChildIndex);
        target.setScaleX(Anim.SCALE);
        target.setScaleY(Anim.SCALE);

        int imageIndex = slideshowViewModel.getRandomImageIndex(getCurrentIndex());

        if (imageIndex > -1) {
            target.setTag(imageIndex);
            target.setImageBitmap(slideshowViewModel.getImage(getContext(), imageIndex));
        }

        float rangeX = slideshowViewModel.getRangeX();
        ObjectAnimator transX = ObjectAnimator.ofFloat(target, "translationX", rangeX, -rangeX);
        transX.setDuration(Anim.DURATION);

        float rangeY = slideshowViewModel.getRangeY();
        ObjectAnimator transY = ObjectAnimator.ofFloat(target, "translationY", rangeY, -rangeY);
        transY.setDuration(Anim.DURATION);

        ObjectAnimator alpha = ObjectAnimator.ofFloat(target, "alpha", 0.0f, 1.0f);
        alpha.setDuration(Anim.DURATION / 2);

        ObjectAnimator alphaAfter = ObjectAnimator.ofFloat(target, "alpha", 1.0f, 0.0f);
        alphaAfter.setDuration(Anim.DURATION / 2);
        alphaAfter.setStartDelay(Anim.DURATION / 2);

        AnimatorSet animSet = new AnimatorSet();
        animSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animSet.playTogether(transX, transY, alpha, alphaAfter);
        animSet.start();

        //무한 반복
        postDelayed(new Runnable() {
            @Override
            public void run() {
                anim(Math.abs(targetChildIndex - 1));
            }
        }, Anim.DURATION / 2);

    }


}
