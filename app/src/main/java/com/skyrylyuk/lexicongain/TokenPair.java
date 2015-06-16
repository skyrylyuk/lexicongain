package com.skyrylyuk.lexicongain;

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

    private String tramslateDate;

    public String getOriginalText() {

        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getTramslateDate() {
        return tramslateDate;
    }

    public void setTramslateDate(String tramslateDate) {
        this.tramslateDate = tramslateDate;
    }
}
