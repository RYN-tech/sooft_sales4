package com.sooft_sales.local_database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import com.sooft_sales.model.CreateOrderModel;
import com.sooft_sales.model.DepartmentModel;
import com.sooft_sales.model.ItemCartModel;
import com.sooft_sales.model.OrdersModel;
import com.sooft_sales.model.ProductModel;
import com.sooft_sales.tags.Tags;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AccessDatabase {
    private LocalDatabase localDatabase;
    private DAOInterface daoInterface;
    private CompositeDisposable disposable = new CompositeDisposable();

    public AccessDatabase(Context context) {
        localDatabase = LocalDatabase.newInstance(context);
        daoInterface = localDatabase.daoInterface();

    }

    public void clear(Context context) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                localDatabase.clearAllTables();
                // localDatabase.close();
                localDatabase.getOpenHelper().getReadableDatabase().query("PRAGMA wal_checkpoint(FULL)").close();

                if (!localDatabase.inTransaction()) {
                    localDatabase.getOpenHelper().getReadableDatabase().execSQL("VACUUM");
                }
                //  localDatabase.close();

                context.deleteDatabase(Tags.DATABASE_NAME);
                if (!localDatabase.isOpen()) {
                    localDatabase.getOpenHelper().getWritableDatabase();
                }
                clear();
            }
        });


    }

    public void insertCategory(List<DepartmentModel> departmentModelList, DataBaseInterfaces.CategoryInsertInterface retrieveInsertInterface) {
        Completable.fromAction (() -> daoInterface.insertCategoryData(departmentModelList)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@androidx.annotation.NonNull Disposable d) {


                        disposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        retrieveInsertInterface.onCategoryDataInsertedSuccess(true);
                        clear();

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
        //new InsertCategoryTask(retrieveInsertInterface).execute(departmentModelList);
    }

    public void insertProduct(List<ProductModel> productModelList, DataBaseInterfaces.ProductInsertInterface retrieveInsertInterface) {
        Completable.fromAction (() -> daoInterface.insertProductData(productModelList)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@androidx.annotation.NonNull Disposable d) {
                        Log.e("lll",d.toString());


                        disposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        retrieveInsertInterface.onProductDataInsertedSuccess(true);
                        clear();

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("dkdkdk",e.toString());
                    }
                });
        //   new InsertProductTask(retrieveInsertInterface).execute(productModelList);
    }

    public void getCategory(DataBaseInterfaces.CategoryInterface categoryInterface) {
        Single.fromCallable(() -> daoInterface.getCategory()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<DepartmentModel>>() {
                    @Override
                    public void onSubscribe(@androidx.annotation.NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@androidx.annotation.NonNull List<DepartmentModel> departmentModels) {
                        categoryInterface.onCategoryDataSuccess(departmentModels);
                        clear();

                    }

                    @Override
                    public void onError(@androidx.annotation.NonNull Throwable e) {

                    }
                });
        // new CategoryTask(categoryInterface).execute();
    }


    public void getProduct(DataBaseInterfaces.ProductInterface productInterface, String id) {
        Single.fromCallable(() -> daoInterface.getProductByCategory(id)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<ProductModel>>() {
                    @Override
                    public void onSubscribe(@androidx.annotation.NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@androidx.annotation.NonNull List<ProductModel> productModels) {
                        productInterface.onProductDataSuccess(productModels);
                        clear();

                    }

                    @Override
                    public void onError(@androidx.annotation.NonNull Throwable e) {

                    }
                });
        //  new ProductTask(productInterface).execute(id);
    }

    public void getLocalProduct(DataBaseInterfaces.ProductInterface productInterface, String type) {
        Single.fromCallable(() -> daoInterface.getLocalProduct(type)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<ProductModel>>() {
                    @Override
                    public void onSubscribe(@androidx.annotation.NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@androidx.annotation.NonNull List<ProductModel> productModels) {

                        productInterface.onProductDataSuccess(productModels);
                        clear();

                    }

                    @Override
                    public void onError(@androidx.annotation.NonNull Throwable e) {

                    }
                });
        // new LocalroductTask(productInterface).execute(type);
    }

    public void getlastProduct(DataBaseInterfaces.LastProductInterface productInterface) {
        Single.fromCallable(() -> daoInterface.getlastProduct()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<ProductModel>() {
                    @Override
                    public void onSubscribe(@androidx.annotation.NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@androidx.annotation.NonNull ProductModel productModel) {

                        productInterface.onLastProductDataSuccess(productModel);
                        clear();

                    }

                    @Override
                    public void onError(@androidx.annotation.NonNull Throwable e) {

                    }
                });
        //  new LastProductTask(productInterface).execute();
    }

    public void insertSingleProduct(ProductModel productModel, DataBaseInterfaces.ProductInsertInterface retrieveInsertInterface) {
        Completable.fromCallable (() -> daoInterface.insertProductData(productModel)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@androidx.annotation.NonNull Disposable d) {

                        disposable.add(d);
                    }

                    @Override
                    public void onComplete() {

                        retrieveInsertInterface.onProductDataInsertedSuccess(true);
                        clear();


                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
        //new InsertSingleProductTask(retrieveInsertInterface).execute(productModel);
    }

    public void insertAllOrder(List<CreateOrderModel> createOrderModelList, DataBaseInterfaces.OrderInsertInterface retrieveInsertInterface) {
      Log.e("llll0",createOrderModelList.size()+"");

        Completable.fromCallable (() -> daoInterface.insertOrderData(createOrderModelList)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@androidx.annotation.NonNull Disposable d) {

                      //  Log.e("flflfl",d.toString());
                        disposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(";;;;",";;kkkk;;");

                        retrieveInsertInterface.onOrderDataInsertedSuccess(true);
                        clear();


                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
Log.e(";;;;",";;;;");
                    }
                });
        //  new InsertAllOrdersTask(retrieveInsertInterface).execute(createOrderModelList);
    }

    public void insertOrder(CreateOrderModel retrieveModel, DataBaseInterfaces.OrderInsertInterface retrieveInsertInterface) {
        Completable.fromCallable (() -> daoInterface.insertOrderData(retrieveModel)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@androidx.annotation.NonNull Disposable d) {

                        disposable.add(d);
                    }

                    @Override
                    public void onComplete() {

                        retrieveInsertInterface.onOrderDataInsertedSuccess(true);
                        clear();


                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });

        //  new InsertOrderTask(retrieveInsertInterface).execute(retrieveModel);
    }

    public void insertOrderProduct(List<ItemCartModel> itemCartModelList, DataBaseInterfaces.ProductOrderInsertInterface retrieveInsertInterface) {
        Completable.fromCallable (() -> daoInterface.insertOrderProducts(itemCartModelList)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@androidx.annotation.NonNull Disposable d) {

                        disposable.add(d);
                    }

                    @Override
                    public void onComplete() {

                        retrieveInsertInterface.onProductORderDataInsertedSuccess(true);
                        clear();


                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
//        try {
//            new InsertProductOrderTask(retrieveInsertInterface).execute(itemCartModelList);
//
//        } catch (Exception e) {
//
//        }
    }

    public void search(DataBaseInterfaces.SearchInterface searchInterface, String id) {
        Single.fromCallable(() -> daoInterface.search(Double.parseDouble(id))).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<CreateOrderModel>() {
                    @Override
                    public void onSubscribe(@androidx.annotation.NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@androidx.annotation.NonNull CreateOrderModel createOrderModel) {

                        searchInterface.onSearchDataSuccess(createOrderModel);
                        clear();

                    }

                    @Override
                    public void onError(@androidx.annotation.NonNull Throwable e) {

                    }
                });
        // new SearchTask(searchInterface).execute(id);
    }

    public void udateOrder(String id, DataBaseInterfaces.OrderupdateInterface retrieveInsertInterface) {
        Completable.fromCallable (() -> daoInterface.updateOrder(Double.parseDouble(id), true, true)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@androidx.annotation.NonNull Disposable d) {

                        disposable.add(d);
                    }

                    @Override
                    public void onComplete() {

                        retrieveInsertInterface.onOrderUpdateDataSuccess();
                        clear();

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
        //     new UpdateOrderTask(retrieveInsertInterface).execute(id);
    }

    public void udateProduct(String id, String newid, DataBaseInterfaces.ProductupdateInterface retrieveInsertInterface) {
        Completable.fromCallable (() -> daoInterface.updateProduct(Double.parseDouble(id), Double.parseDouble(newid))).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@androidx.annotation.NonNull Disposable d) {

                        disposable.add(d);
                    }

                    @Override
                    public void onComplete() {

                        retrieveInsertInterface.onProductUpdateDataSuccess();
                        clear();

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                });
        //  new UpdateProductTask(retrieveInsertInterface).execute(id, newid);
    }

    public void getallOrder(DataBaseInterfaces.AllOrderInterface productInterface) {
        Single.fromCallable(() -> daoInterface.getallOrders(true, true, true)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<OrdersModel>>() {
                    @Override
                    public void onSubscribe(@androidx.annotation.NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@androidx.annotation.NonNull List<OrdersModel> ordersModels) {

                        productInterface.onAllOrderDataSuccess(ordersModels);
                        clear();

                    }

                    @Override
                    public void onError(@androidx.annotation.NonNull Throwable e) {

                    }
                });
        //new AllOrderTask(productInterface).execute();
    }

//    public void getOrderProduct(DataBaseInterfaces.AllOrderProductInterface productInterface, String id) {
//        new ProductOrders(productInterface).execute(id);
//    }

    public class InsertCategoryTask extends AsyncTask<List<DepartmentModel>, Void, Boolean> {
        private DataBaseInterfaces.CategoryInsertInterface retrieveInsertInterface;

        public InsertCategoryTask(DataBaseInterfaces.CategoryInsertInterface retrieveInsertInterface) {
            this.retrieveInsertInterface = retrieveInsertInterface;
        }

        @Override
        protected Boolean doInBackground(List<DepartmentModel>... lists) {
            boolean isInserted = false;
            long[] data = daoInterface.insertCategoryData(lists[0]);
            if (data != null && data.length > 0) {
                isInserted = true;
            }
            return isInserted;
        }

        @Override
        protected void onPostExecute(Boolean bol) {

            retrieveInsertInterface.onCategoryDataInsertedSuccess(bol);

        }
    }

    public class InsertAllOrdersTask extends AsyncTask<List<CreateOrderModel>, Void, Boolean> {
        private DataBaseInterfaces.OrderInsertInterface retrieveInsertInterface;

        public InsertAllOrdersTask(DataBaseInterfaces.OrderInsertInterface retrieveInsertInterface) {
            this.retrieveInsertInterface = retrieveInsertInterface;
        }

        @Override
        protected Boolean doInBackground(List<CreateOrderModel>... lists) {
            boolean isInserted = false;
            long[] data = daoInterface.insertOrderData(lists[0]);
            if (data != null && data.length > 0) {
                isInserted = true;
            }
            return isInserted;
        }

        @Override
        protected void onPostExecute(Boolean bol) {

            retrieveInsertInterface.onOrderDataInsertedSuccess(bol);

        }
    }

    public class InsertProductTask extends AsyncTask<List<ProductModel>, Void, Boolean> {
        private DataBaseInterfaces.ProductInsertInterface retrieveInsertInterface;

        public InsertProductTask(DataBaseInterfaces.ProductInsertInterface retrieveInsertInterface) {
            this.retrieveInsertInterface = retrieveInsertInterface;
        }

        @Override
        protected Boolean doInBackground(List<ProductModel>... lists) {
            boolean isInserted = false;
            if (lists.length > 0) {
                try {
                    long[] data = daoInterface.insertProductData(lists[0]);
                    if (data != null && data.length > 0) {
                        isInserted = true;
                    }
                } catch (Exception e) {

                }
                return isInserted;
            }
            return isInserted;
        }

        @Override
        protected void onPostExecute(Boolean bol) {

            retrieveInsertInterface.onProductDataInsertedSuccess(bol);

        }
    }

    public class CategoryTask extends AsyncTask<Void, Void, List<DepartmentModel>> {
        private DataBaseInterfaces.CategoryInterface categoryInterface;

        public CategoryTask(DataBaseInterfaces.CategoryInterface categoryInterface) {
            this.categoryInterface = categoryInterface;
        }

        @Override
        protected List<DepartmentModel> doInBackground(Void... voids) {
            return daoInterface.getCategory();
        }

        @Override
        protected void onPostExecute(List<DepartmentModel> DepartmentModelList) {
            categoryInterface.onCategoryDataSuccess(DepartmentModelList);
        }
    }

    public class ProductTask extends AsyncTask<String, Void, List<ProductModel>> {
        private DataBaseInterfaces.ProductInterface productInterface;

        public ProductTask(DataBaseInterfaces.ProductInterface productInterface) {
            this.productInterface = productInterface;
        }

        @Override
        protected List<ProductModel> doInBackground(String... strings) {


            return daoInterface.getProductByCategory(strings[0]);

        }

        @Override
        protected void onPostExecute(List<ProductModel> productModelList) {

            productInterface.onProductDataSuccess(productModelList);
        }

    }

    public class LocalroductTask extends AsyncTask<String, Void, List<ProductModel>> {
        private DataBaseInterfaces.ProductInterface productInterface;

        public LocalroductTask(DataBaseInterfaces.ProductInterface productInterface) {
            this.productInterface = productInterface;
        }

        @Override
        protected List<ProductModel> doInBackground(String... strings) {


            return daoInterface.getLocalProduct(strings[0]);

        }

        @Override
        protected void onPostExecute(List<ProductModel> productModelList) {

            productInterface.onProductDataSuccess(productModelList);
        }

    }


    //
    public class LastProductTask extends AsyncTask<String, Void, ProductModel> {
        private DataBaseInterfaces.LastProductInterface lastProductInterface;

        public LastProductTask(DataBaseInterfaces.LastProductInterface lastProductInterface) {
            this.lastProductInterface = lastProductInterface;
        }

        @Override
        protected ProductModel doInBackground(String... strings) {


            return daoInterface.getlastProduct();

        }

        @Override
        protected void onPostExecute(ProductModel productModel) {

            lastProductInterface.onLastProductDataSuccess(productModel);
        }

    }

    public class InsertSingleProductTask extends AsyncTask<ProductModel, Void, Boolean> {
        private DataBaseInterfaces.ProductInsertInterface retrieveInsertInterface;

        public InsertSingleProductTask(DataBaseInterfaces.ProductInsertInterface retrieveInsertInterface) {
            this.retrieveInsertInterface = retrieveInsertInterface;
        }

        @Override
        protected Boolean doInBackground(ProductModel... lists) {
            boolean isInserted = false;
            long data = daoInterface.insertProductData(lists[0]);
            if (data > 0) {
                isInserted = true;
            }
            return isInserted;
        }

        @Override
        protected void onPostExecute(Boolean bol) {

            retrieveInsertInterface.onProductDataInsertedSuccess(bol);

        }
    }

    public class InsertOrderTask extends AsyncTask<CreateOrderModel, Void, Boolean> {
        private DataBaseInterfaces.OrderInsertInterface orderInsertInterface;

        public InsertOrderTask(DataBaseInterfaces.OrderInsertInterface orderInsertInterface) {
            this.orderInsertInterface = orderInsertInterface;
        }

        @Override
        protected Boolean doInBackground(CreateOrderModel... lists) {
            boolean isInserted = false;
            long data = daoInterface.insertOrderData(lists[0]);
            if (data > 0) {
                isInserted = true;
            }
            return isInserted;
        }

        @Override
        protected void onPostExecute(Boolean bol) {
            orderInsertInterface.onOrderDataInsertedSuccess(bol);

        }
    }

    public class InsertProductOrderTask extends AsyncTask<List<ItemCartModel>, Void, Boolean> {
        private DataBaseInterfaces.ProductOrderInsertInterface productOrderInsertInterface;

        public InsertProductOrderTask(DataBaseInterfaces.ProductOrderInsertInterface productOrderInsertInterface) {
            this.productOrderInsertInterface = productOrderInsertInterface;
        }

        @Override
        protected Boolean doInBackground(List<ItemCartModel>... lists) {
            boolean isInserted = false;
            long[] data = null;
            try {
                data = daoInterface.insertOrderProducts(lists[0]);

            } catch (Exception e) {

            }
            if (data != null && data.length > 0) {
                isInserted = true;
            }
            return isInserted;
        }

        @Override
        protected void onPostExecute(Boolean bol) {

            productOrderInsertInterface.onProductORderDataInsertedSuccess(bol);

        }
    }

    public class SearchTask extends AsyncTask<String, Void, CreateOrderModel> {
        private DataBaseInterfaces.SearchInterface searchInterface;

        public SearchTask(DataBaseInterfaces.SearchInterface searchInterface) {
            this.searchInterface = searchInterface;
        }

        @Override
        protected CreateOrderModel doInBackground(String... strings) {


            return daoInterface.search(Double.parseDouble(strings[0]));

        }

        @Override
        protected void onPostExecute(CreateOrderModel createOrderModel) {
            if (createOrderModel != null) {
                searchInterface.onSearchDataSuccess(createOrderModel);
            }
        }

    }

    public class UpdateOrderTask extends AsyncTask<String, Void, Long> {
        private DataBaseInterfaces.OrderupdateInterface retrieveInsertInterface;

        public UpdateOrderTask(DataBaseInterfaces.OrderupdateInterface retrieveInsertInterface) {
            this.retrieveInsertInterface = retrieveInsertInterface;
        }

        @Override
        protected Long doInBackground(String... retrieveModels) {
            long data = daoInterface.updateOrder(Double.parseDouble(retrieveModels[0]), true, true);

            return data;
        }

        @Override
        protected void onPostExecute(Long id) {
            if (id > 0) {
                retrieveInsertInterface.onOrderUpdateDataSuccess();
            }
        }
    }

    public class UpdateProductTask extends AsyncTask<String, Void, Long> {
        private DataBaseInterfaces.ProductupdateInterface retrieveInsertInterface;

        public UpdateProductTask(DataBaseInterfaces.ProductupdateInterface retrieveInsertInterface) {
            this.retrieveInsertInterface = retrieveInsertInterface;
        }

        @Override
        protected Long doInBackground(String... retrieveModels) {
            long data = daoInterface.updateProduct(Double.parseDouble(retrieveModels[0]), Double.parseDouble(retrieveModels[1]));

            return data;
        }

        @Override
        protected void onPostExecute(Long id) {
            //    Log.e("lllll", id + "");
            if (id > 0) {
                retrieveInsertInterface.onProductUpdateDataSuccess();
            }
        }
    }

    public class AllOrderTask extends AsyncTask<String, Void, List<OrdersModel>> {
        private DataBaseInterfaces.AllOrderInterface allOrderInterface;

        public AllOrderTask(DataBaseInterfaces.AllOrderInterface allOrderInterface) {
            this.allOrderInterface = allOrderInterface;
        }

        @Override
        protected List<OrdersModel> doInBackground(String... strings) {


            return daoInterface.getallOrders(true, true, true);

        }

        @Override
        protected void onPostExecute(List<OrdersModel> productModelList) {

            allOrderInterface.onAllOrderDataSuccess(productModelList);
        }

    }

    //    public class ProductOrders extends AsyncTask<String, Void, List<ItemCartModel>> {
//        private DataBaseInterfaces.AllOrderProductInterface productInterface;
//
//        public ProductOrders(DataBaseInterfaces.AllOrderProductInterface productInterface) {
//            this.productInterface = productInterface;
//        }
//
//        @Override
//        protected List<ItemCartModel> doInBackground(String... strings) {
//
//
//            return daoInterface.getOrderProducts(Double.parseDouble(strings[0]));
//
//        }
//
//        @Override
//        protected void onPostExecute(List<ItemCartModel> productModelList) {
//
//            productInterface.onAllOrderProductDataSuccess(productModelList);
//        }
//
//    }
    public void clear() {
        disposable.clear();
    }
}
