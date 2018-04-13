package com.example.home.gomoku_demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {
    final static int maxN=15;
    private Context context;
    private ImageView[][] ivCell=new ImageView[maxN][maxN];

    private Drawable[] drawCell= new Drawable[4];

    private Button btnPlay;
    private TextView tvTurn;

    private int[][]valueCell=new int[maxN][maxN];
    private int winner_play;
    private boolean firstMove;
    private int xMove,yMove;
    private int turnPlay;

    public MainActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        context=this;
        
        setListen();
        loadResources();
        designBoardGame();
    }

    private void setListen() {
        btnPlay=(Button) findViewById(R.id.btnPlay);
        tvTurn=(TextView)findViewById(R.id.tvTurn);

        btnPlay.setText("Play Game");
        tvTurn.setText("Press button Play Game");

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init_game();
                play_game();
            }
        });
    }

    private void play_game() {
        Random r=new Random();
        turnPlay=r.nextInt(2)+1;

        if(turnPlay==1){
            Toast.makeText(context,"Player play first!",Toast.LENGTH_LONG).show();
            playerTurn();
        }else {
            Toast.makeText(context,"Bot play first!",Toast.LENGTH_LONG).show();
            botTurn();
        }

    }

    private void botTurn() {
        Log.d("tuongvi","bot turn");
        tvTurn.setText("Bot");

        if(firstMove){
            firstMove=false;
            xMove=7;yMove=7;
            make_a_move();
        }else {
            findBotMove();
            make_a_move();
        }
    }

    private final int[] iRow={-1,-1,-1,0,1,1,1,0};
    private final int[] iCol={-1,0,1,1,1,0,-1,-1};

    private void findBotMove() {
        List<Integer> listX=new ArrayList<Integer>();
        List<Integer> listY=new ArrayList<Integer>();

        final int range=2;
        for(int i=0;i<maxN;i++){
            for(int j=0;j<maxN;j++){
                if(valueCell[i][j]!=0){
                    for(int t=1;t<=range;t++){
                        for(int k=0;k<8;k++){
                            int x=i+iRow[k]*t;
                            int y=j+iCol[k]*t;
                            if(inBoard(x,y) && valueCell[x][y]==0){
                                listX.add(x);
                                listY.add(y);
                            }
                        }
                    }
                }
            }
        }
        int lx=listX.get(0);
        int ly=listY.get(0);

        int res= Integer.MAX_VALUE - 10;
        for(int i=0;i<listX.size();i++){
            int x=listX.get(i);
            int y=listY.get(i);
            valueCell[x][y]=2;
            int rr=getValue_Position();
            if(rr<res){
                res=rr;
                lx=x;
                ly=y;
            }
            valueCell[x][y]=0;
        }
        xMove=lx;yMove=ly;
    }

    private int getValue_Position() {
        int rr=0;
        int p1=turnPlay;
        //row
        for(int i=0;i<maxN;i++){
            rr+=CheckValue(maxN-1,i,-1,0,p1);
        }
        for(int i=0;i<maxN;i++){
            rr+=CheckValue(i,maxN-1,0,-1,p1);
        }

        for(int i=maxN-1;i>=0;i--){
            rr+= CheckValue(i,maxN-1,-1,-1,p1);
        }
        for(int i=maxN-2;i>=0;i--){
            rr+=CheckValue(maxN-1,i,-1,-1,p1);
        }




        for(int i=maxN-1;i>=0;i--){
            rr+= CheckValue(i,0,-1,1,p1);
        }
        for(int i=maxN-1;i>=1;i--){
            rr+=CheckValue(maxN-1,i,-1,1,p1);
        }

        return rr;
    }

    private int CheckValue(int xd, int yd, int vx, int vy, int p1) {

        int i,j;
        int rr=0;
        i=xd;j=yd;
        String st=String.valueOf(valueCell[i][j]);
        while (true){
            i+=vx;j+=vy;
            if(inBoard(i,j)){
                st=st+String.valueOf(valueCell[i][j]);
                if(st.length()==6){
                    rr+=Eval(st,p1);
                    st=st.substring(1,6);
                }
            } else break;
        }

        return rr;
    }

    private void make_a_move() {
        Log.d("tuongvi","make a move with" +xMove+":"+yMove+":"+turnPlay);

        ivCell[xMove][yMove].setImageDrawable(drawCell[turnPlay]);

        valueCell[xMove][yMove]=turnPlay;

        if(noEmtycell()){
            Toast.makeText(context,"Draw!!",Toast.LENGTH_SHORT).show();
            return;
        }else if(CheckWinner()) {
            if(winner_play==1){
                Toast.makeText(context,"Winner is Player",Toast.LENGTH_SHORT).show();
                tvTurn.setText("Winner is Player");
            }
            else
            {
                Toast.makeText(context,"Winner is Bot",Toast.LENGTH_SHORT).show();
                tvTurn.setText("Winner is Player");
            }
            return;
        }


        if(turnPlay==1){
            turnPlay=(1+2)-turnPlay;
            botTurn();
        }else {
            turnPlay=3-turnPlay;
            playerTurn();
        }
    }

    private boolean CheckWinner() {

        if(winner_play!=0) return true;

        VectorEnd(xMove,0,0,1,xMove,yMove);

        VectorEnd(0,yMove,1,0,xMove,yMove);

        if(xMove+yMove>=maxN-1){
            VectorEnd(maxN-1,xMove+yMove-maxN+1,-1,1,xMove,yMove);
        }else {
            VectorEnd(xMove+yMove,0,-1,1,xMove,yMove);
        }
        if(xMove<=yMove){
            VectorEnd(xMove=yMove+maxN-1,maxN-1,-1,-1,xMove,yMove);
        }else {
            VectorEnd(maxN-1,maxN-1-(xMove-yMove),-1,-1,xMove,yMove);
        }

        if(winner_play!=0) return true;
        else return false;
    }

    private void VectorEnd(int xx, int yy, int vx, int vy, int rx, int ry) {
        if(winner_play!=0) return;
        final int range=4;
        int i,j;
        int xbelow=rx-range*vx;
        int ybelow=ry-range*vy;
        int xabove=rx-range*vx;
        int yabove=ry-range*vy;

        String st="";
        i=xx;j=yy;
        while (!inside(i,xbelow,xabove)||!inside(j,ybelow,yabove)){
            i+=vx;j+=vy;
        }

        while (true){
            st=st+String.valueOf(valueCell[i][j]);
            if(st.length()==5){
                EvalEnd(st);
                st=st.substring(1,5);
            }
            i+=vx;j+=vy;
            if((!inBoard(i,j)) || !inside(i,xbelow,xabove) || !inside(j,ybelow,yabove) || winner_play!=0){
                break;
            }

        }

    }

    private boolean inBoard(int i, int j) {

        if(i<0 || i>maxN-1 || j>maxN-1) return false;
        return true;
    }

    private void EvalEnd(String st) {
        switch (st){
            case "11111":winner_play=1;break;
            case "22222":winner_play=2;break;
            default:break;
        }

    }

    private boolean inside(int i, int xbelow, int xabove) {


        return (i-xbelow)*(i-xabove)<=0;
    }

    private boolean noEmtycell() {
        for(int i =0;i<maxN;i++){
            for(int j=0;j<maxN;j++){
                if(valueCell[i][j]==0){
                    return false;
                }
            }
        }
        return true;
    }

    private void playerTurn() {
        Log.d("tuongvi","player turn");
        tvTurn.setText("Player");
        firstMove=false;
        isChecked=false;

    }

    private void init_game(){
        firstMove=true;
        winner_play=0;

        for(int i=0;i<maxN;i++){
            for (int j=0;j<maxN;j++){
                ivCell[i][j].setImageDrawable(drawCell[0]);
                valueCell[i][j]=0;
            }
        }
    }

    private void loadResources() {
        drawCell[3]=context.getResources().getDrawable(R.drawable.cell_bg);//background
        drawCell[0]=null;
        drawCell[1]=context.getResources().getDrawable(R.drawable.check_icon); //for player
        drawCell[2]=context.getResources().getDrawable(R.drawable.x_icon);//for bot
    }

    private boolean isChecked;

    @SuppressLint("NewApi")
    private void designBoardGame() {

        int sizeofCell=Math.round(ScreenWidth()/maxN);

        LinearLayout.LayoutParams lpRow     =   new LinearLayout.LayoutParams(sizeofCell*maxN,sizeofCell);
        LinearLayout.LayoutParams lpCell    =   new LinearLayout.LayoutParams(sizeofCell,sizeofCell);

        LinearLayout linBoardGame=(LinearLayout)findViewById(R.id.linBoardGame);


        for(int i=0;i<maxN;i++){
            LinearLayout linRow=new LinearLayout(context);
            for(int j=0;j<maxN;j++){
                ivCell[i][j]    =   new ImageView(context);
                ivCell[i][j].setBackground(drawCell[3]);
                final int x =  i;
                final int y =  j;
                ivCell[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(valueCell[x][y]==0){
                            if(turnPlay==1 || !isChecked){
                                Log.d("tuongvi","click to cell");
                                isChecked=true;
                                xMove=x;
                                yMove=y;
                                make_a_move();
                            }
                        }

                    }
                });
                linRow.addView(ivCell[i][j],lpCell);
            }
            linBoardGame.addView(linRow,lpRow);
        }

    }

    private float ScreenWidth() {
        Resources resources =   context.getResources();
        DisplayMetrics dm   =   resources.getDisplayMetrics();

        return dm.widthPixels;
    }


    private int Eval(String st, int p1){
        int b1=1, b2=1;
        if(p1==1){
            b1=2;
            b2=1;
        } else {
            b1=1;
            b2=2;
        }

        switch (st){
            case "111110": return b1*100000000;
            case "011111": return b1*100000000;
            case "211111": return b1*100000000;
            case "111112": return b1*100000000;
            case "011110": return b1*10000000;
            case "101110": return b1*1002;
            case "011101": return b1*1002;
            case "011112": return b1*1000;
            case "011100": return b1*102;
            case "001110": return b1*102;
            case "210111": return b1*100;
            case "211110": return b1*100;
            case "211011": return b1*100;
            case "211101": return b1*100;
            case "010100": return b1*10;
            case "011000": return b1*10;
            case "001100": return b1*10;
            case "000110": return b1*10;
            case "211000": return b1*1;
            case "201100": return b1*1;
            case "200110": return b1*1;
            case "200011": return b1*1;
            case "222220": return b2*-100000000;
            case "022222": return b2*-100000000;
            case "122222": return b2*100000000;
            case "222221": return b2*-100000000;
            case "022220": return b2*-10000000;
            case "202220": return b2*-1002;
            case "022202": return b2*-1002;
            case "022221": return b2*-1000;
            case "022200": return b2*-102;
            case "002220": return b2*-102;
            case "120222": return b2*-100;
            case "122220": return b2*-100;
            case "122022": return b2*-100;
            case "122202": return b2*-100;
            case "020200": return b2*-10;
            case "022000": return b2*-10;
            case "002200": return b2*-10;
            case "000220": return b2*-10;
            case "122000": return b2*-1;
            case "102200": return b2*-1;
            case "100220": return b2*-1;
            case "100022": return b2*-1;
            default:
                break;
        }
    return 0;
    }
}
