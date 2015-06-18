package com.skyrylyuk.lexicongain

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.TextView
import io.realm.RealmBaseAdapter
import io.realm.RealmResults

public class TokenPairAdapter extends RealmBaseAdapter<TokenPair> implements ListAdapter {

    public TokenPairAdapter(Context context, int resId,
                            RealmResults<TokenPair> realmResults,
                            boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.original = (TextView) convertView.findViewById(android.R.id.text1);
            viewHolder.translate = (TextView) convertView.findViewById(android.R.id.text2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        TokenPair item = getItem(position);
        viewHolder.original.text = "${item.originalText}"
        viewHolder.translate.text = "${item.translateText}"
        return convertView;
    }

    private static class ViewHolder {
        TextView original;
        TextView translate;
    }
}