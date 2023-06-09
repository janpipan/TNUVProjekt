package si.uni_lj.fe.tnuv.bt_simple;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Lift {
    private String exercise;
    private Date date;
    private double peakVelocity;
    private int weight;
    private int percentToMax;
    private String percentage;
    private String tag;
    private String comment;


    public Lift(String rawData) {
        String[] dataParts = rawData.split(",");
        if (dataParts.length == 6) {
            this.exercise = dataParts[0].trim();

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy/HH:mm:ss", Locale.getDefault());
                this.date = dateFormat.parse(dataParts[1].trim());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            this.peakVelocity = Double.parseDouble(dataParts[2].trim());
            this.weight = Integer.parseInt(dataParts[3].trim());
            this.percentage = dataParts[4].trim();
            this.tag = dataParts[5].trim();
        } else {
            throw new IllegalArgumentException("Invalid raw data for lift");
        }
    }

    public String getExercise(){
        return exercise;
    }

    public Date getDate(){
        return date;
    }

    public double getPeakVelocity(){
        return peakVelocity;
    }

    public int getWeight(){
        return weight;
    }

    public String getPercentage(){
        return percentage;
    }

    public String getTag(){
        return tag;
    }

    public String getComment(){return comment;}

    public int getPercentToMax() {return percentToMax;}

    public void setExercise(String exercise){
        this.exercise = exercise;
    }

    public void setDate(Date date){
        this.date = date;
    }

    public void setPeakVelocity(double peakVelocity){
        this.peakVelocity = peakVelocity;
    }

    public void setWeight(int weight){
        this.weight = weight;
    }

    public void setPercentage(String percentage){
        this.percentage = percentage;
    }

    public void setTag(String tag){
        this.tag = tag;
    }

    public void setComment(String comment) {this.comment = comment;}
    public void setPercentToMax(int percentToMax) {this.percentToMax = percentToMax;}

    public String toString(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy/HH:mm:ss", Locale.getDefault());
        String[] liftArray = {this.exercise, dateFormat.format(this.date), Double.toString(this.peakVelocity), Integer.toString(this.weight), this.percentage, this.tag, this.comment};
        Log.d("liftString", String.join(",",liftArray));
        return String.join(",",liftArray);
    }
}
