package ru.perm.mrc.photomon.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.commonsware.cwac.merge.MergeAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import lombok.AllArgsConstructor;
import ru.perm.mrc.photomon.R;
import ru.perm.mrc.photomon.data.DBHelper;
import ru.perm.mrc.photomon.model.Category;
import ru.perm.mrc.photomon.model.Product;
import ru.perm.mrc.photomon.model.Task;

public class CategoriesActivity extends AppCompatActivity {

    private Task task;
    private ArrayList<CatWithId> closedCategories;
    private ArrayList<CatWithId> openedCategories;
    private ArrayList<ProdWithId> products;

    private ArrayAdapter<CatWithId> openedCatAdapter;
    private ArrayAdapter<CatWithId> closedCatAdapter;
    private ArrayAdapter<ProdWithId> prodAdapter;

    @AllArgsConstructor
    private static class CatWithId{
        int id;
        String catName;

        @Override
        @NonNull
        public String toString(){
            return id+1 + ". " + catName;
        }
    }

    @AllArgsConstructor
    private static class ProdWithId{
        int id;
        int catID;
        String productName;

        @Override
        @NonNull
        public String toString(){
            return productName;
        }

    }

    private Comparator<CatWithId> compareCategoryWithId = new Comparator<CatWithId>() {
        public int compare(CatWithId cat1, CatWithId cat2) {
            return cat1.id - cat2.id;
        }
    };

    private Comparator<ProdWithId> compareProdWithId = new Comparator<ProdWithId>() {
        @Override
        public int compare(ProdWithId prod1, ProdWithId prod2) {
            int i = prod1.catID - prod2.catID;
            if (i == 0)
                return prod1.id - prod2.id;
            return i;
        }
    };

    private void categoryOnClick(CatWithId catWithId){
        int id = catWithId.id;

        if (task.getCategories().get(id).isOpen())
        {
            task.getCategories().get(id).setOpen(false);
            closedCategories.add(catWithId);
            openedCategories.remove(catWithId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                closedCategories.sort(compareCategoryWithId);
            }
            closeProducts(id);
        }
        else
        {
            task.getCategories().get(id).setOpen(true);
            openedCategories.add(catWithId);
            closedCategories.remove(catWithId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                openedCategories.sort(compareCategoryWithId);
            }
            openProducts(id);
        }

        closedCatAdapter.notifyDataSetChanged();
        openedCatAdapter.notifyDataSetChanged();
        prodAdapter.notifyDataSetChanged();
    }

    private void productOnClick (ProdWithId prodWithId){

    }

    private AdapterView.OnItemClickListener productCategoryOnClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Object item = adapterView.getItemAtPosition(i);
            if (item instanceof CatWithId)
                categoryOnClick((CatWithId) item);
            else if (item instanceof ProdWithId)
                productOnClick((ProdWithId) item);
        }
    };


    private void openProducts (int catID){
        Category category = task.getCategories().get(catID);
        for (Product product : category.getProducts()) {
            int id = category.getProducts().indexOf(product);
            products.add(new ProdWithId(id, catID, product.getName()));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            products.sort(compareProdWithId);
        }
    }

    private void closeProducts (int catID){
        Iterator<ProdWithId> iterator = products.listIterator();
        while (iterator.hasNext()){
            ProdWithId prod = iterator.next();
            if (prod.catID == catID)
                iterator.remove();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_categories);

        DBHelper dbHelper = new DBHelper(this);
        closedCategories = new ArrayList<>();
        openedCategories = new ArrayList<>();
        products = new ArrayList<>();

        ListView openedCategoriesLV = findViewById(R.id.opened_categories);
        ListView productsClosedCategoriesLV = findViewById(R.id.closed_categories);

        Intent intent = getIntent();

        task = new Task(dbHelper,intent.getStringExtra("taskName"),intent.getIntExtra("taskID",0));
        createCatArrays();

        openedCatAdapter = new ArrayAdapter<>(this,R.layout.fragment_category,openedCategories);
        openedCategoriesLV.setAdapter(openedCatAdapter);

        closedCatAdapter = new ArrayAdapter<>(this,R.layout.fragment_category,closedCategories);
        prodAdapter = new ArrayAdapter<>(this,R.layout.fragment_product,products);

        MergeAdapter mergeAdapter = new MergeAdapter();
        mergeAdapter.addAdapter(prodAdapter);
        mergeAdapter.addAdapter(closedCatAdapter);

        productsClosedCategoriesLV.setAdapter(mergeAdapter);

        openedCategoriesLV.setOnItemClickListener(productCategoryOnClick);
        productsClosedCategoriesLV.setOnItemClickListener(productCategoryOnClick);

    }

    private void createCatArrays(){
        for (Category cat : task.getCategories()) {
            String catName = cat.getName();
            int catID = task.getCategories().indexOf(cat);

            if (cat.isOpen())
                openedCategories.add(new CatWithId(catID, catName));
            else
                closedCategories.add(new CatWithId(catID, catName));
        }
    }
}
