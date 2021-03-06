package com.example.final_shooting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Enemy extends Character{
    int enemyScore; // 적들을 죽일시 얻는 점수
    long lifeTime; // 적들이 생존한 시간
    int angryTime = 10; // 10초가 지나면 화나서? 주인공 쫒아오게함
    int angrySpeed = 5; // 1초마다 추적시간을 점점 증가시키기 위함.
    int xymargin = 300; // 추적 좌표에서 얼마만큼 오차가 나도 되는지
    int traceX, traceY;
    int bufX, bufY;
    int nownumber;
    int ragemode;
    int interval = 10; // 주인공과 몹이 얼마만큼 닿아야 하는지를 나타내는 변수 (충돌 정밀도 향상 용도) -> 가상디바이스에선 사이즈들이 다 커져서 40해도 되는데 보드에선 실제사이즈이기에 낮게해야함
    Bitmap ragebitmap;
    String nickname;

    public Enemy(Context context, int x, int y) {
        super(context,x,y);
        this.lifeTime = 0;
        this.traceX = (int)(Math.random()*(DrawFrame.screenWidth-100)); // 처음에 추적할 위치
        this.traceY = (int)(Math.random()*(DrawFrame.screenHeight-DrawFrame.buttonbar));
        this.nownumber = 1; // 현재 무슨 행동인지 식별하기위함
        this.ragemode = 0;
        this.direction = (int)(Math.random()*2)+1; // 적의 방향은 왼쪽, 오른쪽 두개밖에 없음
        this.ragebitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ragemode);
    }

    @Override
    public void moveShape(DrawFrame df) {
        lifeTime ++;
        collisionCheck(df.hero, df);
        if(lifeTime <= df.FPS*angryTime) {
            traceXY(traceX, traceY, xymargin, xymargin, 0);
            if(x>traceX-bitsize[0] && x<traceX+xymargin &&y>traceY-bitsize[1] && y<traceY+xymargin)
            {
                this.traceX = (int)(Math.random()*(DrawFrame.screenWidth-100)); // 새로운 위치 추적
                this.traceY = (int)(Math.random()*(DrawFrame.screenHeight-DrawFrame.buttonbar));
            }
        }
        else// n초 이상 시 주인공을 추적하는 if문
        {
            traceXY(df.hero.x, df.hero.y, df.hero.bitsize[0], df.hero.bitsize[1], 1);
            ragemode = 1;
        }
        outCheck(x + speedX, y+speedY);
        x += speedX; // ->이걸 8방향으로 랜덤하게 움직이고 방향있게 해주기
        y += speedY;

        if(speedX > 0)
            direction = 1; // 스피드가 0보다 큰 경우 오른쪽 방향
        else
            direction = 2; // 스피드가 0보다 작은 경우 왼쪽 방향

        speedX = bufX;
        speedY = bufY;

        checkbitmap();
    }

    @Override
    public void outCheck(int movingX, int movingY)
    {
        if(movingX > DrawFrame.screenWidth - bitsize[0])
        {
            speedX = -Math.abs(speedX);
        }
        else if(movingX < 0)
        {
            speedX = Math.abs(speedX);
        }
        if(movingY > DrawFrame.screenHeight  - bitsize[1])
        {
            speedY = -Math.abs(speedY);
        }
        else if(movingY < 0)
        {
            speedY = Math.abs(speedY);
        }
    }

    @Override
    public void collisionCheck(Character ch, DrawFrame df) {
        if(x>ch.x-bitsize[0] + interval && x<ch.x+ch.bitsize[0] - interval &&y>ch.y-bitsize[1] + interval && y<ch.y+ch.bitsize[1] - interval)
        {
            ch.life --;
            ReciveLedValue(ch.life);
            life = 0; // 주인공과 부딪히는 적군들은 모두 없어진다 - 점수는 안오르게 함
            if(df.hero.face != 4) {
                df.hero.face = 4;
                df.hero.facetime = 0;
                ReceiveDotValue(df.hero.face);
            }
            df.effects.add(new Effect(context,ch.x-15,ch.y-ch.bitsize[1]-10,"explosion",9));
        }
    }

    public void traceXY(int tx, int ty,int marginX,int marginY, int flag) // 대상을 추적하는 함수
    {
        if(x > tx + marginX - interval)
            speedX = -Math.abs(speedX);

        else if(x + bitsize[0] < tx + interval)
            speedX = Math.abs(speedX);

        if(y > ty + marginY - interval)
            speedY = -Math.abs(speedY);

        else if(y + bitsize[1] < ty + interval)
            speedY = Math.abs(speedY);

        bufX = speedX;
        bufY = speedY;

        if(x < tx + marginX - interval && x + bitsize[0] > tx + interval) // 지그제그 움직임 오류 해결 (조금이라도 걸치면 직진)
            speedX = 0;
        else if(y < ty + marginY - interval && y + bitsize[1] > ty + interval)
            speedY = 0;

        if(flag == 1 && lifeTime % (DrawFrame.FPS * angrySpeed) == 0) // 추적 시작후 n초마다 속도가 1씩 빨라진다.
        {
            if(bufX < 0) // 지그제그 오류 해결용도 = buf
                bufX--;
            else
                bufX++;

            if(bufY < 0)
                bufY--;
            else
                bufY++;

        }
    }
    public void checkbitmap(){ // 왼쪽 걸음 2개 오른쪽 걸음 2개일경우이다.
        if(lifeTime % (DrawFrame.FPS/3) == 0) // n 초마다 발 전환하겠다. (FPS/3 = 0.333초)
            nownumber = (nownumber == 2) ? 1:2;
        String dir = (direction == 2) ? "l" : "r";
        String bitname = this.nickname + dir + String.valueOf(nownumber); // 몬스터 이미지는 랜덤으로 생성 + 몬스터마다 점수, 체력 다르게 가능
        int resID = context.getResources().getIdentifier(bitname, "drawable", context.getPackageName());
        bitmap = BitmapFactory.decodeResource(context.getResources(), resID);
    }



}