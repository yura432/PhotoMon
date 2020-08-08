package ru.perm.mrc.photomon;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import lombok.AllArgsConstructor;
import ru.perm.mrc.photomon.data.DBHelper;
import ru.perm.mrc.photomon.model.Category;
import ru.perm.mrc.photomon.model.Product;
import ru.perm.mrc.photomon.model.Task;

public class CategoriesActivity extends AppCompatActivity {

    private Task task;
    private DBHelper dbHelper;
    private ArrayList<CatWithId> closedCategories;
    private ArrayList<CatWithId> openedCategories;
    private ArrayList<ProdWithId> products;
    private ListView openedCategoriesLV;
    private ListView closedCategoriesLV;
    private ListView productsLV;

    ArrayAdapter<CatWithId> openedCatAdapter;
    ArrayAdapter<CatWithId> closedCatAdapter;
    ArrayAdapter<ProdWithId> prodAdapter;

    @AllArgsConstructor
    private class CatWithId{
        int id;
        String catName;

        @Override
        public String toString(){
            return id+1 + ". " + catName;
        }
    }

    @AllArgsConstructor
    private class ProdWithId{
        int id;
        int catID;
        String productName;
        @Override
        public String toString(){
            return productName;
        }

    }




    Comparator<CatWithId> compareCategoryWithId = new Comparator<CatWithId>() {
        public int compare(CatWithId cat1, CatWithId cat2) {
            return cat1.id - cat2.id;
        }
    };

    AdapterView.OnItemClickListener categoryOnClick = new AdapterView.OnItemClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            CatWithId catWithId = (CatWithId) adapterView.getItemAtPosition(i);
            int id = catWithId.id;

            if (task.getCategories().get(id).isOpen())
            {
                task.getCategories().get(id).setOpen(false);
                closedCategories.add(catWithId);
                openedCategories.remove(catWithId);
                closedCategories.sort(compareCategoryWithId);
                closeProducts(id);
            }
            else
            {
                task.getCategories().get(id).setOpen(true);
                openedCategories.add(catWithId);
                closedCategories.remove(catWithId);
                openedCategories.sort(compareCategoryWithId);
                openProducts(id);
            }

            closedCatAdapter.notifyDataSetChanged();
            openedCatAdapter.notifyDataSetChanged();
            prodAdapter.notifyDataSetChanged();
        }
    };

    private void openProducts (int catID){
        Category category = task.getCategories().get(catID);
        Iterator<Product> iterator = category.getProducts().listIterator();
        while (iterator.hasNext()){
            Product product = iterator.next();
            int id = category.getProducts().indexOf(product);
            products.add(new ProdWithId(id,catID,product.getName()));
        }
    }

    private void closeProducts (int catID){
        Iterator<ProdWithId> iterator = products.listIterator();
        while (iterator.hasNext()){
            ProdWithId prod = iterator.next();
            if (prod.catID == catID)
                products.remove(prod);
        }

    }

    View.OnClickListener productOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_categories);

        dbHelper = new DBHelper(this);
        closedCategories = new ArrayList<>();
        openedCategories = new ArrayList<>();
        products = new ArrayList<>();

        openedCategoriesLV = findViewById(R.id.opened_categories);
        closedCategoriesLV = findViewById(R.id.closed_categories);
        productsLV = findViewById(R.id.products);

        Intent intent = getIntent();

        task = new Task(dbHelper,intent.getStringExtra("taskName"),intent.getIntExtra("taskID",0));
        createCatArrays();

        openedCatAdapter = new ArrayAdapter<>(this,R.layout.fragment_category,openedCategories);
        openedCategoriesLV.setAdapter(openedCatAdapter);

        closedCatAdapter = new ArrayAdapter<>(this,R.layout.fragment_category,closedCategories);
        closedCategoriesLV.setAdapter(closedCatAdapter);

        prodAdapter = new ArrayAdapter<>(this,R.layout.fragment_product,products);
        productsLV.setAdapter(prodAdapter);

        openedCategoriesLV.setOnItemClickListener(categoryOnClick);
        closedCategoriesLV.setOnItemClickListener(categoryOnClick);





    }

    private void createCatArrays(){
        Iterator<Category> iteratorCat = task.getCategories().listIterator();
        while (iteratorCat.hasNext()){
            Category cat = iteratorCat.next();

            String catName = cat.getName();
            int catID = task.getCategories().indexOf(cat);


            if (cat.isOpen())
                openedCategories.add(new CatWithId(catID,catName));
            else
                closedCategories.add(new CatWithId(catID,catName));

        }

    }

    private void redraw(){

    }




}
