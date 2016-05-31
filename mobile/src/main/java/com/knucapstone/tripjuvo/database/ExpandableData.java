package com.knucapstone.tripjuvo.database;

/**
 * Created by aassw on 2016-05-31.
 */
public class ExpandableData {
    private int poi_id;
    private int position;
    private int childPosition;
   public ExpandableData(int id, int position, int childPosition){
        setPoi_id(id); setPosition(position); setChildPosition(childPosition);
    }

    public void setChildPosition(int childPosition) {
        this.childPosition = childPosition;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getChildPosition() {
        return childPosition;
    }

    public int getPosition() {
        return position;
    }

    public void setPoi_id(int poi_id) {
        this.poi_id = poi_id;
    }

    public int getPoi_id() {
        return poi_id;
    }
}
