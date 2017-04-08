package android.lovefantasy.mlscproxy.Wigets;

/**
 * Created by lovefantasy on 17-2-27.
 */

public class ItemData {
    String   mTitle;
    boolean  isSelected;
    public   ItemData(String title){
        mTitle=title;
        isSelected=false;
    }
    public void setSelected(boolean iss){
        isSelected=iss;
    }
    public String getData(){
        return mTitle;
    }
}
