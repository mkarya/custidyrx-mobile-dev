package com.custodyrx.app.src.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.custodyrx.app.src.ui.screens.Activities.Comments.RequestModel.Items;
import com.custodyrx.app.src.ui.screens.Activities.PerformInventory.models.ResponseModel.AllProduct;
import com.custodyrx.app.src.ui.screens.Fragments.GetAllProductItemResponseModel.BizLocation;
import com.custodyrx.app.src.ui.screens.Fragments.GetAllProductItemResponseModel.ProductItemData;
import com.custodyrx.app.src.ui.screens.Fragments.GetAllProductsResponseModel.ProductCategory;
import com.custodyrx.app.src.ui.screens.Fragments.GetAllProductsResponseModel.ProductData;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String db = "custody_rx.db";
    private static String DB_PATH = "";
    private final Context mContext;
    private SQLiteDatabase mDb = null;

    public static final String TABLE_PRODUCTS = "Products";
    public static final String TABLE_PRODUCT_CATEGORIES = "ProductCategories";
    public static final String TABLE_PRODUCT_ITEMS = "ProductItems";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CREATED_AT = "createdAt";
    public static final String COLUMN_UPDATED_AT = "updatedAt";
    public static final String COLUMN_GUID = "GUID";
    public static final String COLUMN_IS_ACTIVE = "isActive";
    public static final String COLUMN_COMPANY_GUID = "companyGuid";
    public static final String COLUMN_PRODUCT_GUID = "productGuid";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PRODUCT_TYPE = "productType";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IMAGE_URL = "image";
    public static final String COLUMN_MANUFACTURER = "manufacturer";
    public static final String COLUMN_PRODUCT_CODE = "productCode";
    public static final String COLUMN_UNSPSC_CODE = "UNSPSCCode";
    public static final String COLUMN_MODEL_NUMBER = "modelNumber";
    public static final String COLUMN_BRAND = "brand";
    public static final String COLUMN_NET_CONTENT = "netContent";
    public static final String COLUMN_NDC = "ndc";
    public static final String COLUMN_DOSAGE = "dosage";
    public static final String COLUMN_STRENGTH = "strength";
    public static final String COLUMN_EXPIRY_DATE = "expiryDate";
    public static final String COLUMN_INVENTORY_QUANTITY = "inventoryQuantity";
    public static final String COLUMN_UNIT_COUNT = "unitCount";
    public static final String COLUMN_ITEM_EPC = "itemEPC";
    public static final String COLUMN_ITEM_TYPE = "itemType";
    public static final String COLUMN_SERIAL_NUMBER = "serialNumber";
    public static final String COLUMN_LOT_NUMBER = "lotNumber";
    public static final String COLUMN_EXPIRATION_DATE = "expirationDate";
    public static final String COLUMN_ASSET_ID = "assetId";
    public static final String COLUMN_MANUFACTURE_DATE = "manufactureDate";
    public static final String COLUMN_VENDOR = "vendor";
    public static final String COLUMN_VENDOR_NUMBER = "vendorNumber";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_DEPARTMENT = "department";
    public static final String COLUMN_PURCHASE_DATE = "purchaseDate";
    public static final String COLUMN_LAST_CHECK_IN = "lastCheckIn";
    public static final String COLUMN_BIZ_LOCATION_GUID = "bizLocationGuid";
    public static final String COLUMN_PACKING_EPC = "packingEPC";
    public static final String COLUMN_BIZ_LOCATION = "bizLocation";

    public static final String COLUMN_TOTAL_UNIT_COUNT = "total_unit_count";


    public DatabaseHandler(Context context) {
        super(context, db, null, 1);

        if (android.os.Build.VERSION.SDK_INT >= 17)
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        else
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        this.mContext = context;

        copyDataBase();

        this.getReadableDatabase();
    }

    @Override
    public synchronized void close() {
        if (mDb != null)
            mDb.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private void copyDataBase() {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + db);
        return dbFile.exists();
    }

    private void copyDBFile() throws IOException {
        InputStream mInput = mContext.getAssets().open(db);
        OutputStream mOutput = new FileOutputStream(DB_PATH + db);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public long insertProduct(ProductData productData) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CREATED_AT, productData.getCreatedAt());
        values.put(COLUMN_UPDATED_AT, productData.getUpdatedAt());
        values.put(COLUMN_GUID, productData.getGuid());
        values.put(COLUMN_IS_ACTIVE, productData.isActive() ? 1 : 0); // BOOLEAN stored as 0/1
        values.put(COLUMN_COMPANY_GUID, productData.getCompanyGuid());
        values.put(COLUMN_NAME, productData.getName());
        values.put(COLUMN_PRODUCT_TYPE, productData.getProductType());
        values.put(COLUMN_DESCRIPTION, productData.getDescription());
        values.put(COLUMN_IMAGE_URL, productData.getImage());
        values.put(COLUMN_MANUFACTURER, productData.getManufacturer());
        values.put(COLUMN_PRODUCT_CODE, productData.getProductCode());
        values.put(COLUMN_UNSPSC_CODE, productData.getUnspscCode());
        values.put(COLUMN_MODEL_NUMBER, productData.getModelNumber());
        values.put(COLUMN_BRAND, productData.getBrand());
        values.put(COLUMN_NET_CONTENT, productData.getNetContent());
        values.put(COLUMN_NDC, productData.getNdc());
        values.put(COLUMN_DOSAGE, productData.getDossage());
        values.put(COLUMN_STRENGTH, productData.getStrength());
        values.put(COLUMN_EXPIRY_DATE, productData.getExpiryDate());
        values.put(COLUMN_INVENTORY_QUANTITY, productData.getInventoryQuantity());

        // Attempt to insert row
        long rowId = db.insert(TABLE_PRODUCTS, null, values);

        db.close();
        return rowId;
    }


    public long insertProductCategory(ProductCategory productCategory, String productGUID) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_GUID, productGUID);
        values.put(COLUMN_CREATED_AT, productCategory.getCreatedAt());
        values.put(COLUMN_UPDATED_AT, productCategory.getUpdatedAt());
        values.put(COLUMN_GUID, productCategory.getGuid());
        values.put(COLUMN_IS_ACTIVE, productCategory.isActive() ? 1 : 0); // BOOLEAN stored as INTEGER (0 or 1)
        values.put(COLUMN_NAME, productCategory.getName());
        values.put(COLUMN_DESCRIPTION, productCategory.getDescription());
        values.put(COLUMN_COMPANY_GUID, productCategory.getCompanyGuid());

        // Attempt to insert the row
        long result = db.insert(TABLE_PRODUCT_CATEGORIES, null, values);

        db.close(); // Closing database connection
        return result;
    }

    public long insertProductItems(ProductItemData productItemData) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String bizLocation = new Gson().toJson(productItemData.getBizLocation());
        values.put(COLUMN_CREATED_AT, productItemData.getCreatedAt());
        values.put(COLUMN_UPDATED_AT, productItemData.getUpdatedAt());
        values.put(COLUMN_GUID, productItemData.getGuid());
        values.put(COLUMN_IS_ACTIVE, productItemData.isActive() ? 1 : 0);
        values.put(COLUMN_COMPANY_GUID, productItemData.getCompanyGuid());
        values.put(COLUMN_UNIT_COUNT, productItemData.getUnitCount());
        values.put(COLUMN_ITEM_EPC, productItemData.getItemEPC());
        values.put(COLUMN_ITEM_TYPE, productItemData.getItemType());
        values.put(COLUMN_SERIAL_NUMBER, productItemData.getSerialNumber());
        values.put(COLUMN_LOT_NUMBER, productItemData.getLotNumber());
        values.put(COLUMN_EXPIRATION_DATE, productItemData.getExpirationDate());
        values.put(COLUMN_ASSET_ID, productItemData.getAssetId());
        values.put(COLUMN_MANUFACTURE_DATE, productItemData.getManufactureDate());
        values.put(COLUMN_VENDOR, productItemData.getVendor());
        values.put(COLUMN_VENDOR_NUMBER, productItemData.getVendorNumber());
        values.put(COLUMN_STATUS, productItemData.getStatus());
        values.put(COLUMN_DEPARTMENT, productItemData.getDepartment());
        values.put(COLUMN_PURCHASE_DATE, productItemData.getPurchaseDate());
        values.put(COLUMN_LAST_CHECK_IN, productItemData.getLastCheckIn());
        values.put(COLUMN_PRODUCT_GUID, productItemData.getProductGuid());
        values.put(COLUMN_BIZ_LOCATION_GUID, productItemData.getBizLocationGuid());
        values.put(COLUMN_PACKING_EPC, productItemData.getPackingEPC());
        values.put(COLUMN_BIZ_LOCATION, bizLocation);

        // Attempt to insert the row
        long result = db.insert(TABLE_PRODUCT_ITEMS, null, values);

        db.close(); // Closing database connection
        return result;

    }

    public int getTotalMatchedInventoryQuantity() {
        int totalQuantity = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT SUM(" + COLUMN_INVENTORY_QUANTITY + ") FROM " + TABLE_PRODUCTS + "";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                totalQuantity = cursor.getInt(0);
            }
            cursor.close();
        }

        db.close(); // Closing database connection
        return totalQuantity;
    }

    public int getTotalInventoryQuantity() {
        int totalQuantity = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT SUM(" + COLUMN_INVENTORY_QUANTITY + ") FROM " + TABLE_PRODUCTS + "";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                totalQuantity = cursor.getInt(0);
            }
            cursor.close();
        }

        db.close(); // Closing database connection
        return totalQuantity;
    }

    public int getTotalUnitCount() {
        int totalUnitCount = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT SUM(" + COLUMN_UNIT_COUNT + ") FROM " + TABLE_PRODUCT_ITEMS + "";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                totalUnitCount = cursor.getInt(0);
            }
            cursor.close();
        }

        db.close(); // Closing database connection
        return totalUnitCount;
    }

    public List<Items> getSelectedProductItems(String productGuid) {
        List<Items> itemsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to select items based on productGuid
        String query = "SELECT GUID, itemEPC, unitCount FROM ProductItems WHERE productGuid = ?";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{productGuid});

            if (cursor.moveToFirst()) {
                do {
                    Items item = new Items();
                    item.setItemGuid(cursor.getString(cursor.getColumnIndexOrThrow("GUID")));
                    item.setItemEpc(cursor.getString(cursor.getColumnIndexOrThrow("itemEPC")));
                    item.setUnitCount(cursor.getInt(cursor.getColumnIndexOrThrow("unitCount")));

                    itemsList.add(item);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return itemsList;
    }


    public List<AllProduct> getAllProductsWithTotalUnitCount() {
        List<AllProduct> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();


        String selectQuery = "SELECT " +
                "p." + COLUMN_NAME + ", " +
                "p." + COLUMN_INVENTORY_QUANTITY + ", " +
                "p." + COLUMN_GUID + ", " +
                "COALESCE(SUM(pi." + COLUMN_UNIT_COUNT + "), 0) AS total_unit_count " +
                "FROM " + TABLE_PRODUCTS + " p " +
                "LEFT JOIN " + TABLE_PRODUCT_ITEMS + " pi " +
                "ON p." + COLUMN_GUID + " = pi." + COLUMN_PRODUCT_GUID + " " +
                "GROUP BY p." + COLUMN_NAME + ", p." + COLUMN_INVENTORY_QUANTITY + " " +
                "ORDER BY p." + COLUMN_ID + ";";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                AllProduct product = new AllProduct();
                product.setName(getStringValue(cursor, COLUMN_NAME));
                product.setInventoryQuantity(getIntValue(cursor, COLUMN_INVENTORY_QUANTITY));
                product.setGuid(getStringValue(cursor, COLUMN_GUID));
                product.setTotalUnitCount(getIntValue(cursor, COLUMN_TOTAL_UNIT_COUNT));

                productList.add(product);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return productList;
    }

    public String getProductGuid(String itemEpc) {
        String productGuid = null;
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COLUMN_PRODUCT_GUID +
                " FROM " + TABLE_PRODUCT_ITEMS +
                " WHERE " + COLUMN_ITEM_EPC + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{itemEpc});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                productGuid = cursor.getString(0);
            }
            cursor.close();
        }

        db.close(); // Closing database connection
        return productGuid;
    }

    public String getProductName(String guid) {
        String productName = null;
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + COLUMN_NAME +
                " FROM " + TABLE_PRODUCTS +
                " WHERE " + COLUMN_GUID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{guid});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                productName = cursor.getString(0);
            }
            cursor.close();
        }

        db.close(); // Closing database connection
        return productName;
    }


    public AllProduct getProductByGuid(String guid) {
        AllProduct product = null;
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT " +
                "p." + COLUMN_NAME + ", " +
                "p." + COLUMN_INVENTORY_QUANTITY + ", " +
                "p." + COLUMN_GUID + ", " +
                "COALESCE(SUM(pi." + COLUMN_UNIT_COUNT + "), 0) AS total_unit_count " +
                "FROM " + TABLE_PRODUCTS + " p " +
                "LEFT JOIN " + TABLE_PRODUCT_ITEMS + " pi " +
                "ON p." + COLUMN_GUID + " = pi." + COLUMN_PRODUCT_GUID + " " +
                "WHERE p." + COLUMN_GUID + " = ? " +  // Filtering by GUID
                "GROUP BY p." + COLUMN_NAME + ", p." + COLUMN_INVENTORY_QUANTITY + ", p." + COLUMN_GUID + " " +
                "ORDER BY p." + COLUMN_ID + ";";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{guid});

        if (cursor.moveToFirst()) {
            product = new AllProduct();
            product.setName(getStringValue(cursor, COLUMN_NAME));
            product.setInventoryQuantity(getIntValue(cursor, COLUMN_INVENTORY_QUANTITY));
            product.setGuid(getStringValue(cursor, COLUMN_GUID));
            product.setTotalUnitCount(getIntValue(cursor, COLUMN_TOTAL_UNIT_COUNT));
        }

        cursor.close();
        db.close();

        return product; // Returns null if no product is found
    }

    public boolean incrementInventoryQuantity(String GUID) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "UPDATE " + TABLE_PRODUCTS +
                " SET " + COLUMN_INVENTORY_QUANTITY + " = " + COLUMN_INVENTORY_QUANTITY + " + 1" +
                " WHERE " + COLUMN_GUID + " = ?";

        try {
            db.execSQL(query, new String[]{GUID});
            db.close();
            return true; // Update successful
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
            return false; // Update failed
        }
    }

    public boolean incrementUnitCount(String itemEpc) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "UPDATE " + TABLE_PRODUCT_ITEMS +
                " SET " + COLUMN_UNIT_COUNT + " = " + COLUMN_UNIT_COUNT + " + 1" +
                " WHERE " + COLUMN_ITEM_EPC + " = ?";

        try {
            db.execSQL(query, new String[]{itemEpc});
            db.close();
            return true; // Update successful
        } catch (Exception e) {
            e.printStackTrace();
            db.close();
            return false; // Update failed
        }
    }


    public void clearProductsTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            db.delete(TABLE_PRODUCTS, null, null);
            db.execSQL("DELETE FROM sqlite_sequence WHERE name = ?", new String[]{TABLE_PRODUCTS});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }


    public void clearProductCategoriesTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            db.delete(TABLE_PRODUCT_CATEGORIES, null, null);
            db.execSQL("DELETE FROM sqlite_sequence WHERE name = '" + TABLE_PRODUCT_CATEGORIES + "'");
            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
            db.close();
        }

    }

    public void clearProductItemsTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            db.delete(TABLE_PRODUCT_ITEMS, null, null);
            db.execSQL("DELETE FROM sqlite_sequence WHERE name = '" + TABLE_PRODUCT_ITEMS + "'");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    private String getStringValue(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return (columnIndex != -1 && !cursor.isNull(columnIndex)) ? cursor.getString(columnIndex) : "null";
    }


    private int getIntValue(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return (columnIndex != -1 && !cursor.isNull(columnIndex)) ? cursor.getInt(columnIndex) : 0;
    }


    private boolean getBooleanValue(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        return (columnIndex != -1 && !cursor.isNull(columnIndex)) && cursor.getInt(columnIndex) == 1;
    }


}
