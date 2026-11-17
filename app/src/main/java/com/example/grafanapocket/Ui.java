package com.example.grafanapocket;
import android.content.Context;import android.graphics.Color;import android.view.ViewGroup;import android.widget.*;
final class Ui {
 static LinearLayout column(Context c){LinearLayout l=new LinearLayout(c);l.setOrientation(LinearLayout.VERTICAL);l.setPadding(32,24,32,24);l.setBackgroundColor(Color.rgb(17,18,23));applySystemBars(l);return l;}
 static void applySystemBars(android.view.View v){final int left=v.getPaddingLeft(),top=v.getPaddingTop(),right=v.getPaddingRight(),bottom=v.getPaddingBottom();v.setOnApplyWindowInsetsListener((view,insets)->{view.setPadding(left+insets.getSystemWindowInsetLeft(),top+insets.getSystemWindowInsetTop(),right+insets.getSystemWindowInsetRight(),bottom+insets.getSystemWindowInsetBottom());return insets;});v.requestApplyInsets();}
 static TextView title(Context c,String s){TextView v=new TextView(c);v.setText(s);v.setTextColor(Color.WHITE);v.setTextSize(24);v.setPadding(8,16,8,16);return v;}
 static EditText input(Context c,String hint){EditText e=new EditText(c);e.setHint(hint);e.setTextColor(Color.WHITE);e.setHintTextColor(Color.GRAY);e.setSingleLine(true);return e;}
 static Button button(Context c,String s){Button b=new Button(c);b.setText(s);return b;}
 static void fill(ViewGroup p,android.view.View v){p.addView(v,new ViewGroup.LayoutParams(-1,-2));}
}
