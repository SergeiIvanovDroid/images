package com.appdroid.develop.images;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity implements OnImageAddListener {

    @BindView(R.id.recyclerView)RecyclerView recyclerView;
    RealmResults<Image> imagesAll;
    Iterator<Image> imagesAllIterator;
    private Realm realm;
    private ImagesAdapter imagesAdapter;
    private LinearLayoutManager layoutManager;
    SearchView searchView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        realm = Realm.getInstance(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Dexter.withActivity((Activity) context)
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                            }
                        }).check();

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if(resultCode == RESULT_OK){
            Uri selectedImage = imageReturnedIntent.getData();
//            Picasso.get()
//                    .load(selectedImage)
//                    .fit().centerCrop()
//                    .into(imageView);
            DialogAddImage dialogAddImage = new DialogAddImage();
            dialogAddImage.setUriImage(selectedImage);
            dialogAddImage.setRealm(realm);
            dialogAddImage.show(getSupportFragmentManager(),"dlgAdd");
//            String pathToImage = getRealPathFromURI(selectedImage);
//            String tags = "tag3,tag4,tag3,tag6,tag8,tag9,tag88,tag46,tag35,tag433,tag36,tag4733,tag73,tag74,tag93,tag14,tag13,tag24,tag223,tag544,tag354,tag454,tag453";
//            long date = System.currentTimeMillis();
//
//            realm.beginTransaction();
//
//            Image image = realm.createObject(Image.class);
//            image.setDate(date);
//            image.setPathToImage(pathToImage);
//            image.setTags(tags);
//
//            realm.commitTransaction();

        }
    }


//    @OnClick(R.id.button2)
//    public void onClick2(){
////        RealmResults<Image> images = getImagesByTag("tag2");
//        RealmResults<Image> images = getImagesByTags(new String[]{"tag1","tag1","tag1"});
//        imagesAdapter = new ImagesAdapter(images,context,realm);
//
//        recyclerView.setLayoutManager(layoutManager);
//        imagesAdapter.notifyDataSetChanged();
//        recyclerView.setAdapter(imagesAdapter);
////        Iterator<Image> imageIterator = images.iterator();
////        imageIterator.next();
//    }

    private RealmResults<Image> getInfo(){
        return realm.where(Image.class).findAllSorted("date", Sort.DESCENDING);
    }

    private RealmResults<Image> getImagesByTag(String tag){
        RealmResults<Image> images = realm.where(Image.class).contains("tags",tag).findAll();
        return images;
    }


    private RealmResults<Image> getImagesByTags(String[] tags){
        RealmQuery<Image> realmQuery = realm.where(Image.class).contains("tags",tags[0]);
        if(realmQuery.findAll().size() > 0){
            for(int i = 1; i < tags.length;i++){
                realmQuery = realmQuery.contains("tags",tags[i]);
            }
            return realmQuery.findAll();
        }else{
            return null;
        }
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setQueryHint(getString(R.string.filter));
        searchView.setOnQueryTextListener(textChangeListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        imagesAll = getInfo();
        imagesAdapter = new ImagesAdapter(imagesAll,context,realm);
        recyclerView.setLayoutManager(layoutManager);
        imagesAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(imagesAdapter);
    }


    SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
//        imagesAdapter.getFilter().filter(query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            imagesAdapter.getFilter().filter(newText);
            return false;
        }
    };


    @Override
    public void onCompleted(long date, String tags, String pathToImage) {
            realm.beginTransaction();

            Image image = realm.createObject(Image.class);
            image.setDate(date);
            image.setPathToImage(pathToImage);
            image.setTags(tags);

            realm.commitTransaction();
            if(imagesAdapter != null){
                imagesAdapter.notifyDataSetChanged();
            }
            
    }
}
