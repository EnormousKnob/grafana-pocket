package com.example.grafanapocket;
import android.content.Context;import android.graphics.Color;import android.view.ViewGroup;import android.widget.*;
import android.graphics.drawable.GradientDrawable;import android.graphics.drawable.RippleDrawable;import android.content.res.ColorStateList;
final class Ui {
 static final int BG=0xff0c1425,CARD=0xff19253b,TEXT=0xfff3f7ff,MUTED=0xffb0bed5,ACCENT=0xff80e4e8;
 static int dp(Context c,int n){return Math.round(n*c.getResources().getDisplayMetrics().density);}
 static GradientDrawable shape(Context c,int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(c,radius));return d;}
 static LinearLayout column(Context c){LinearLayout l=new LinearLayout(c);l.setOrientation(LinearLayout.VERTICAL);l.setPadding(dp(c,20),dp(c,16),dp(c,20),dp(c,24));l.setBackgroundColor(BG);applySystemBars(l);return l;}
 static void applySystemBars(android.view.View v){final int left=v.getPaddingLeft(),top=v.getPaddingTop(),right=v.getPaddingRight(),bottom=v.getPaddingBottom();v.setOnApplyWindowInsetsListener((view,insets)->{view.setPadding(left+insets.getSystemWindowInsetLeft(),top+insets.getSystemWindowInsetTop(),right+insets.getSystemWindowInsetRight(),bottom+insets.getSystemWindowInsetBottom());return insets;});v.requestApplyInsets();}
 static TextView text(Context c,String s,int size,int color){TextView v=new TextView(c);v.setText(s);v.setTextColor(color);v.setTextSize(size);v.setPadding(0,dp(c,5),0,dp(c,5));return v;}
 static TextView title(Context c,String s){return text(c,s,24,TEXT);}
 static LinearLayout card(LinearLayout parent,String title,String subtitle){Context c=parent.getContext();LinearLayout box=new LinearLayout(c);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(dp(c,18),dp(c,14),dp(c,18),dp(c,14));box.setBackground(shape(c,CARD,22));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,dp(c,8),0,dp(c,8));parent.addView(box,p);fill(box,text(c,title,20,TEXT));if(!subtitle.isEmpty())fill(box,text(c,subtitle,13,MUTED));return box;}
 static EditText input(Context c,String hint){EditText e=new EditText(c);e.setHint(hint);e.setTextColor(TEXT);e.setHintTextColor(MUTED);e.setSingleLine(true);e.setTextSize(15);e.setMinHeight(dp(c,56));return e;}
 static Button button(Context c,String s){Button b=new Button(c);b.setText(s);b.setTextSize(14);b.setAllCaps(false);b.setTextColor(ACCENT);b.setMinHeight(dp(c,48));b.setPadding(dp(c,12),dp(c,8),dp(c,12),dp(c,8));b.setBackground(new RippleDrawable(ColorStateList.valueOf(0x3380e4e8),shape(c,0xff24364e,14),null));return b;}
 static void fill(ViewGroup p,android.view.View v){LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,dp(p.getContext(),4),0,dp(p.getContext(),4));p.addView(v,lp);}
}
