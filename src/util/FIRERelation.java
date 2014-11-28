package util;

/**
 * Created by diogo on 27-11-2014.
 */
public class FIRERelation {
    public String name1;
    public String name2;
    public FIRERule fireRule;

    public FIRERelation(String name1, String name2, FIRERule fireRule) {
        this.name1 = name1;
        this.name2 = name2;
        this.fireRule = fireRule;
    }
}
