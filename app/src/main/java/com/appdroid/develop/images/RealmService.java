package com.appdroid.develop.images;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmService {

    private Realm realm;

    public RealmService(Realm realm) {
        this.realm = realm;
    }

    public void closeRealm(){
        realm.close();
    }

    public RealmResults<Image> getAllImages(){
        return realm.where(Image.class).findAllSorted("date", Sort.DESCENDING);
    }

    public void addImageToDB(final String tags,final String pathToImage){
        realm.beginTransaction();

        Image image = realm.createObject(Image.class);
        image.setDate(System.currentTimeMillis());
        image.setPathToImage(pathToImage);
        image.setTags(tags);

        realm.commitTransaction();
    }

}


