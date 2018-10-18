package android.lovefantasy.mlscproxy.Wigets;

/**
 * Created by lovefantasy on 17-2-27.
 */

public class ItemData {
    String   mTitle;
    boolean  mSelected;
    public   ItemData(String title){
        mTitle=title;
        mSelected=false;
    }
    public boolean isSelected(){
        return mSelected;
    }
    public void setSelected(boolean iss){
        mSelected=iss;
    }
    public String getData(){
        return mTitle;
    }
}
