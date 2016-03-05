package model;

/**
 * Created by Aleks on 05-Mar-16.
 */
public class FavoriteItem {
    private boolean showNotify;
    private String name;
    private String descr;

    public FavoriteItem() {

    }

    public FavoriteItem(boolean showNotify, String name, String descr)
    {
        this.showNotify = showNotify;
        this.name = name;
        this.descr = descr;
    }

    public boolean isShowNotify()
    {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify)
    {
        this.showNotify = showNotify;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDescr(){
        return descr;
    }

    public void setDescr(String descr){
        this.descr = descr;
    }
}
