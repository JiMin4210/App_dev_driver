package com.example.final_shooting;

import android.content.Context;
import android.graphics.BitmapFactory;

public class Gun extends Character{

    int subSpeed; // 마우스 클릭 방향으로 총알을 보내려면 증가 값이 일정해야하기 때문에 이 값으로 값을 조절

    public Gun(Context context, int x, int y) {
        super(context,x,y);
        String bitname = "shot" + String.valueOf(DrawFrame.shotname); // 몬스터 이미지는 랜덤으로 생성 + 몬스터마다 점수, 체력 다르게 가능
        int resID = context.getResources().getIdentifier(bitname, "drawable", context.getPackageName());
        this.bitmap = BitmapFactory.decodeResource(context.getResources(), resID);
        bitsize[0] = bitmap.getWidth();
        bitsize[1] = bitmap.getHeight();
        this.x = x - bitsize[0]/2; // 원하는 좌표가 중앙이 되도록 조절해줌 (사이즈 계산으로)
        this.y = y - bitsize[1]/2; // 원하는 좌표가 중앙이 되도록 조절해줌 (사이즈 계산으로)
        if(DrawFrame.mode == 1) // 이지모드일 때 총알속도
            subSpeed = 25;
        else // 하드모드일 때 총알속도
            subSpeed = 30;
    }

    void atan(int clickX, int clickY) // 각도를 찾는 함수
    {
        double angle = Math.atan((double)(y-clickY)/(clickX-x)); // 값이 90 ~ -90까지 표현된다.(라디안이 아님에 유의하자)
        if(clickX-x < 0) // 2사분면, 3사분면
        {
            this.speedX = (int)(-subSpeed*Math.cos(angle));
            this.speedY = (int)(subSpeed*Math.sin(angle));
        }
        else // 1사분면, 4사분면
        {
            this.speedX = (int)(subSpeed*Math.cos(angle));
            this.speedY = (int)(-subSpeed*Math.sin(angle));
        }
    }
    @Override
    public void moveShape(DrawFrame df) {
        outCheck(x + speedX, y+speedY);
        for(int i = 0; i<df.monsters.size(); i++)
            collisionCheck(df.monsters.get(i), df);
        for(int i = 0; i<df.bosses.size(); i++)
            collisionCheck(df.bosses.get(i), df);
        x += speedX;
        y += speedY;
    }

    @Override
    public void outCheck(int movingX, int movingY)
    {
        if(movingX > DrawFrame.screenWidth - bitsize[0] || movingX < 0 || movingY > DrawFrame.screenHeight  - bitsize[1] || movingY < 0)
        {
            life --;
        }
    }

    @Override
    public void collisionCheck(Character ch, DrawFrame df) {
        if(x>ch.x-bitsize[0] && x<ch.x+ch.bitsize[0] &&y>ch.y-bitsize[1] && y<ch.y+ch.bitsize[1])
        {
            ch.life -= df.hero.power;
            life = 0;
            Enemy enemy = (Enemy) ch; // 총알 충돌은 enemy하고만 일어나기에 이렇게 형변환 해준다. (enemyScore에 접근하기 위함)
            if(enemy.life <=0) {
                df.score += enemy.enemyScore;
                df.killm++;
                String dir = enemy.direction == 1 ? "r" : "l";
                df.effects.add(new Effect(context,ch.x,ch.y,"goast"+dir,5));
            }
            df.ReceiveFndValue(String.valueOf(df.score));
            df.effects.add(new Effect(context,ch.x+ch.bitsize[0]/2,ch.y,"blood",3));
        }
    }

}