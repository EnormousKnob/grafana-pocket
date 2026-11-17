package com.example.grafanapocket;

import android.content.Context;import android.graphics.*;import java.io.*;

final class WidgetCache {
    static File file(Context c,int id){return new File(c.getFilesDir(),"widget_"+id+".png");}
    static void save(Context c,int id,Bitmap bitmap)throws IOException{try(FileOutputStream out=new FileOutputStream(file(c,id))){bitmap.compress(Bitmap.CompressFormat.PNG,100,out);}}
    static Bitmap load(Context c,int id){return BitmapFactory.decodeFile(file(c,id).getAbsolutePath());}
    static void delete(Context c,int id){file(c,id).delete();}
}
