package com.example.final_shooting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Character {
    Context context;
    Bitmap bitmap;
    int bitsize[] = new int[2]; // 0번 = Width, 1번 = Height
    int x, y;
    int speedX, speedY;
    int life;

    public Character(Context context, int x, int y) {
        this.context = context;
        this.x = x;
        this.y = y;
        life = 1;
    }
}