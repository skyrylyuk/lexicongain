package com.skyrylyuk.lexicongain;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by skyrylyuk on 17.06.15.
 */

//@Canonical
//@TupleConstructor
public class TokenPair extends RealmObject {


    @PrimaryKey
    private String originalText;
    private String translateText;

    private Date translateDate;

    public String getOriginalText() {

        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getTranslateText() {
        return translateText;
    }

    public void setTranslateText(String translateText) {
        this.translateText = translateText;
    }

    public Date getTranslateDate() {
        return translateDate;
    }

    public void setTranslateDate(Date translateDate) {
        this.translateDate = translateDate;
    }
}
